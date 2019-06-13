package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScanManager {

    private final Logger LOGGER = LoggerFactory.getLogger(ScanManager.class);

    private static final int N_THREADS = 20;
    private static final int INITIAL_DELAY = 0;
    private static final int DELAY = 500;

    private final String baseUrl;
    private final ConcurrentSkipListSet<Link> toBeScanned;
    private final Map<Link, Set<Link>> links;
    private final ScheduledExecutorService executor;

    public ScanManager(String baseUrl) {
        this.baseUrl = baseUrl;
        links = new ConcurrentHashMap<>();
        toBeScanned = new ConcurrentSkipListSet<>(Comparator.comparing(Link::getLink));
        executor = Executors.newScheduledThreadPool(N_THREADS);

        toBeScanned.add(new Link(baseUrl));
        initThreads();
    }

    public void shoutDown(){
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.trace(e.getMessage());
        }
    }

    public int getLinksSize() {
        return links.values().stream().mapToInt(Set::size).sum();
    }

    public Map<Link, Set<Link>> getLinks() {
        return new HashMap<>(links);
    }

    private void initThreads() {
        for (int i = 0; i < N_THREADS; i++) {
            Scanner scanner = new Scanner("T"+i, baseUrl, toBeScanned, links);
            executor.scheduleWithFixedDelay(scanner, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
        }
    }
}
