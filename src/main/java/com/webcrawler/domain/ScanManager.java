package com.webcrawler.domain;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanManager {

    private static final int N_THREADS = 2;
    private static final int CAPACITY = 200;

    private final String baseUrl;
    private final BlockingQueue<String> toBeScanned;
    private final Map<String, Set<String>> links;
    private final ExecutorService executor;

    public ScanManager(String baseUrl) {
        setLogLevel();
        this.baseUrl = baseUrl;
        links = new ConcurrentHashMap<>();
        toBeScanned = new ArrayBlockingQueue<>(CAPACITY);
        executor = Executors.newFixedThreadPool(N_THREADS);

        initThreads();
        toBeScanned.add(baseUrl);
    }

    private void setLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("io.netty").setLevel(Level.INFO);
        loggerContext.getLogger("reactor.netty").setLevel(Level.INFO);
    }

    public Map<String, Set<String>> getLinks() {
        return new HashMap<>(links);
    }

    public BlockingQueue<String> getToBeScanned() {
        return toBeScanned;
    }

    public void newLinkFound(String src, String dest) {
        if(isInternalLink(dest)) {
            if (shouldBeScanned(dest)) {
                new Thread(() -> {
                    try {
                        toBeScanned.put(dest);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            links.computeIfAbsent(src, s -> new HashSet<>())
                    .add(dest);
            System.err.println(Thread.currentThread().getName()+"-["+toBeScanned.size()+"]"+src + " --> " + dest);
        }
    }

    private boolean shouldBeScanned(String url){
        return !url.endsWith(".css")
                && !url.endsWith(".ico")
                && !url.endsWith(".gif")
                && !links.keySet().contains(url);
    }

    private void initThreads() {
        for (int i = 0; i < N_THREADS; i++) {
            executor.execute(new Thread("T"+i) {
                @Override
                public void run() {
                    try {
                        scanFirstElement();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private boolean isInternalLink(String dest) {
        if(dest.startsWith("http")
                || dest.startsWith("//")) {
            return dest.contains(baseUrl);
        }

        if(dest.startsWith("/"))
            return true;

        return false;
    }

    private void scanFirstElement() throws InterruptedException {
        Finder finder = new Finder();
        Scanner scanner = new Scanner(finder);

        while (true) {
            String path = toBeScanned.take();  //Blocking invocation
            finder.setConsumer(s -> newLinkFound(path, s));
            String url = (path.contains(baseUrl))? path : baseUrl + path;
            scanner.scan(url);
        }
    }

    public void shoutDown(){
        executor.shutdown();
    }
}
