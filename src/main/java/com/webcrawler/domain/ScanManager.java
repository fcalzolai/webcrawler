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

import static java.lang.String.format;

public class ScanManager {

    private final Logger LOGGER = LoggerFactory.getLogger(ScanManager.class);

    private final String baseUrl;
    private int nThreads;
    private int deelay;
    private final ConcurrentSkipListSet<Link> toBeScanned;
    private final Map<Link, Set<Link>> links;
    private final ScheduledExecutorService executor;

    public ScanManager(String baseUrl, int nThreads, int delay) {
        this.baseUrl = baseUrl;
        this.nThreads = nThreads;
        this.deelay = delay;
        links = new ConcurrentHashMap<>();
        toBeScanned = new ConcurrentSkipListSet<>(Comparator.comparing(Link::getLink));
        executor = Executors.newScheduledThreadPool(nThreads);

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

    public int getEdgesSize() {
        return links.values().stream().mapToInt(Set::size).sum();
    }

    public Map<Link, Set<Link>> getLinks() {
        return new HashMap<>(links);
    }

    private void initThreads() {
        for (int i = 0; i < nThreads; i++) {
            Scanner scanner = new Scanner("T"+i, baseUrl, toBeScanned, links);
            executor.scheduleWithFixedDelay(scanner, 0, deelay, TimeUnit.MILLISECONDS);
        }
        executor.scheduleWithFixedDelay(()-> LOGGER.debug(format("toBeScanned[%s] - state[%s] edges[%s]",
                toBeScanned.size(),
                links.keySet().size(),
                getEdgesSize())), 0, 30, TimeUnit.SECONDS);
    }

    public int getToBeScannedSize() {
        return toBeScanned.size();
    }

    public String getBasUrl() {
        return baseUrl;
    }

    public int getLinksSize(){
        return links.keySet().size();
    }
}
