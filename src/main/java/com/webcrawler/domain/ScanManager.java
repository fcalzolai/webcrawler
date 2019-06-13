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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScanManager {

    private static final int N_THREADS = 2;
    private static final int CAPACITY = 200;

    private final String baseUrl;
    private final BlockingQueue<String> toBeScanned;
    private final Map<String, Set<String>> links;
    private final ScheduledExecutorService executor;

    public ScanManager(String baseUrl) {
        setLogLevel();
        this.baseUrl = baseUrl;
        links = new ConcurrentHashMap<>();
        toBeScanned = new ArrayBlockingQueue<>(CAPACITY);
        executor = Executors.newScheduledThreadPool(N_THREADS);

        initThreads();
        toBeScanned.add(baseUrl);
    }

    private void setLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("io.netty").setLevel(Level.INFO);
        loggerContext.getLogger("reactor.netty").setLevel(Level.INFO);
        loggerContext.getLogger("org.springframework.core.codec").setLevel(Level.INFO);
        loggerContext.getLogger("org.springframework.web").setLevel(Level.INFO);
    }

    public Map<String, Set<String>> getLinks() {
        return new HashMap<>(links);
    }

    public BlockingQueue<String> getToBeScanned() {
        return toBeScanned;
    }

    public void newLinksFound(String src, Set<String> newLinks) {
        new Thread(() -> {
            newLinks.forEach(dest -> {
                try {
                    if (isInternalLink(dest)) {
                        if (shouldBeScanned(dest)) {
                            toBeScanned.put(dest);  //Blocking method
                        }

                        links.computeIfAbsent(src, s -> new HashSet<>())
                                .add(dest);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private boolean shouldBeScanned(String url){
        return !url.endsWith(".css")
                && !url.endsWith(".ico")
                && !url.endsWith(".gif")
                && !url.endsWith(".pdf")
                && !links.keySet().contains(url);
    }

    private void initThreads() {
        for (int i = 0; i < N_THREADS; i++) {
            executor.scheduleWithFixedDelay(getTask(i), 0, 1, TimeUnit.SECONDS);
        }
    }

    private Thread getTask(int i) {
        return new Thread("T"+i) {
            @Override
            public void run() {
                try {
                    scanFirstElement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
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

        String path = toBeScanned.take();  //Blocking invocation
        finder.setConsumer(links -> newLinksFound(path, links));
        String url = (path.contains(baseUrl)) ? path : baseUrl + path;
        scanner.scan(url);
        System.err.println("["+Thread.currentThread().getName()+"] - toBeScanned["+toBeScanned.size()+"] - links["+links.size()+"]");
    }

    public void shoutDown(){
        executor.shutdown();
    }
}
