package com.webcrawler.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanManager {

    private static final int N_THREADS = 1;

    private final String baseUrl;
    private final BlockingQueue<String> toBeScanned;
    private final Map<String, List<String>> links;
    private final ExecutorService executor;

    public ScanManager(String baseUrl) {
        this.baseUrl = baseUrl;
        links = new ConcurrentHashMap<>();
        toBeScanned = new ArrayBlockingQueue<>(200);
        executor = Executors.newFixedThreadPool(N_THREADS);

        initThreads();
        newLinkFound(baseUrl, "");
    }

    public Map<String, List<String>> getLinks() {
        return new HashMap<>(links);
    }

    public BlockingQueue<String> getToBeScanned() {
        return toBeScanned;
    }

    public void newLinkFound(String src, String dest) {
        if(isInternalLink(dest)) {
            if (!alreadyScanned(dest)) { //TODO execute this in a new Thread to avoid to be blocked
                toBeScanned.add(dest);
            }

            links.computeIfAbsent(src, s -> new LinkedList<>())
                    .add(dest);
        }
    }

    private boolean alreadyScanned(String url){
        return links.keySet().stream().anyMatch(s -> s.equals(url));
    }

    private void initThreads() {
        for (int i = 0; i < N_THREADS; i++) {
            executor.execute(() -> {
                try {
                    scanFirstElement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private boolean isInternalLink(String dest) {
        if(dest.startsWith("/"))
            return true;

        if(dest.startsWith("http")) {
            return dest.contains(baseUrl);
        }

        return true;
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
