package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScanManager {

    private final Logger LOGGER = LoggerFactory.getLogger(ScanManager.class);

    private static final int N_THREADS = 10;
    private static final int CAPACITY = 2_000;
    private static final int INITIAL_DELAY = 0;
    private static final int DELAY = 500;

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

    public void shoutDown(){
        executor.shutdown();
    }

    public int getLinksSize() {
        return links.values().stream().mapToInt(Set::size).sum();
    }

    public Map<Link, Set<Link>> getLinks() {
        return new HashMap<>(links);
    }

    public void newLinksFound(Link src, Set<String> newLinks) {
        new Thread(() -> {
//            LOGGER.debug(format("NewLinksFound Src [%s] - new links found [%s]", src, newLinks.size()));
            newLinks.forEach(url -> {
                Link dest = getOrCreate(url);
                try {
                    if (isInternalLink(dest)) {
                        if (shouldBeScanned(dest)) {
                            toBeScanned.put(dest);  //Blocking method
                        }

                        links.get(src).add(dest);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private synchronized Link getOrCreate(String url) {
        Optional<Link> first = links.keySet().parallelStream()
                .filter(link -> link.getLink().equals(url))
                .findFirst();
        return first.orElseGet(() ->
                toBeScanned.parallelStream()
                        .filter(link -> link.getLink().equals(url))
                        .findFirst()
                        .orElse(new Link(url))
        );
    }

    private boolean shouldBeScanned(Link link){
        return !link.getLink().endsWith(".css")
                && !link.getLink().endsWith(".ico")
                && !link.getLink().endsWith(".gif")
                && !link.getLink().endsWith(".pdf")
                && !links.keySet().contains(link);
    }

    private void initThreads() {
        for (int i = 0; i < N_THREADS; i++) {
            Scanner scanner = new Scanner("T"+i, this);
            executor.scheduleWithFixedDelay(scanner, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
        }
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

    protected Link getLinkToScan() throws InterruptedException {
        Link path = toBeScanned.take();  //Blocking invocation
        links.computeIfAbsent(path, s -> new HashSet<>());
        return path;
    }

    protected String getFullUrlToScan(Link path) {
        String link = path.getLink();
        return (link.contains(baseUrl)) ? link : baseUrl + link;
    }

    public int getLinksToBeScannedSize() {
        return toBeScanned.size();
    }
}
