package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Scanner extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

    private UrlFinder urlFinder;
    private UrlScanner urlScanner;
    private final String baseUrl;
    private ConcurrentSkipListSet<Link> toBeScanned;
    private Map<Link, Set<Link>> links;

    public Scanner(String name,
                   String baseUrl,
                   ConcurrentSkipListSet<Link> toBeScanned,
                   Map<Link, Set<Link>> links) {
        super(name);

        this.baseUrl = baseUrl;
        this.toBeScanned = toBeScanned;
        this.links = links;
        this.urlFinder = new UrlFinder();
        this.urlScanner = new UrlScanner(urlFinder);
    }

    @Override
    public void run() {
        try {
            Link link = getNextLinkToScan();
            urlFinder.setConsumer(newLinks -> newLinksFound(link, newLinks));
            urlScanner.scan(getFullUrlToScan(link));
        } catch (Exception e) {
            LOGGER.trace(e.getMessage());
        }
    }

    private Link getNextLinkToScan() {
        Link path = toBeScanned.pollFirst();
        links.computeIfAbsent(path, s -> new HashSet<>());
        return path;
    }

    private String getFullUrlToScan(Link path) {
        String link = path.getLink();
        return (link.contains(baseUrl)) ? link : baseUrl + link;
    }

    private int getLinksSize() {
        return links.values().stream().mapToInt(Set::size).sum();
    }

    private void newLinksFound(Link src, Set<String> destLinks) {
        destLinks.forEach(url -> {
            Link dest = getOrCreate(url);
            if (isInternalLink(dest)) {
                if (shouldBeScanned(dest)) {
                    toBeScanned.add(dest);
                }

                links.get(src).add(dest);
            }
        });
    }

    private synchronized Link getOrCreate(String url) {
        Optional<Link> first = toBeScanned.parallelStream()
                .filter(link -> link.getLink().equals(url))
                .findFirst();
        return first.orElseGet(() ->
                links.keySet().parallelStream()
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
}
