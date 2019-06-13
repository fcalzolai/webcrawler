package com.webcrawler.domain;

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

    private static final int N_THREADS = 10;
    private static final int CAPACITY = 2_000;

    private final String baseUrl;
    private final BlockingQueue<Link> toBeScanned;
    private final Map<Link, Set<Link>> links;
    private final ScheduledExecutorService executor;

    public ScanManager(String baseUrl) {
        this.baseUrl = baseUrl;
        links = new ConcurrentHashMap<>();
        toBeScanned = new ArrayBlockingQueue<>(CAPACITY);
        executor = Executors.newScheduledThreadPool(N_THREADS);

        initThreads();
        toBeScanned.add(new Link(baseUrl));
    }

    public Map<Link, Set<Link>> getLinks() {
        return new HashMap<>(links);
    }

    public void newLinksFound(Link src, Set<Link> newLinks) {
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

    private boolean shouldBeScanned(Link link){
        String url = link.getLink();
        return !url.endsWith(".css")
                && !url.endsWith(".ico")
                && !url.endsWith(".gif")
                && !url.endsWith(".pdf")
                && !links.keySet().contains(url);
    }

    private void initThreads() {
        for (int i = 0; i < N_THREADS; i++) {
            executor.scheduleWithFixedDelay(getTask(i), 0, 500, TimeUnit.MILLISECONDS);
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

    private boolean isInternalLink(Link link) {
        String dest = link.getLink();
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

        Link path = toBeScanned.take();  //Blocking invocation
        finder.setConsumer(newLinks -> newLinksFound(path, newLinks));
        String link = path.getLink();
        String url = (link.contains(baseUrl)) ? link : baseUrl + link;
        scanner.scan(url);
        System.err.println("["+Thread.currentThread().getName()+"] - toBeScanned["+toBeScanned.size()+"] - links["+links.size()+"]");
    }

    public void shoutDown(){
        executor.shutdown();
    }

    public int getLinksSize() {
        return links.values().stream().mapToInt(Set::size).sum();
    }
}
