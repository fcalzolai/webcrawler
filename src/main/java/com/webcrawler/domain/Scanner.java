package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.lang.String.format;

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
            Link path = getLinkToScan();
            urlFinder.setConsumer(newLinks -> newLinksFound(path, newLinks));
            urlScanner.scan(getFullUrlToScan(path));
//            LOGGER.debug(format("Scanned: [Thread%s] - toBeScanned[%s] - links[%s]",
//                    getName(),
//                    toBeScanned.size(),
//                    getLinksSize()));
        } catch (Throwable e) {
            LOGGER.trace(e.getMessage());
        }
    }

    private Link getLinkToScan() {
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


    private void newLinksFound(Link src, Set<String> newLinks) {
        new Thread(() -> {
//            LOGGER.debug(format("NewLinksFound Src [%s] - new links found [%s]", src, newLinks.size()));
            newLinks.forEach(url -> {
                Link dest = getOrCreate(url);
                if (isInternalLink(dest)) {
                    if (shouldBeScanned(dest)) {
                        toBeScanned.add(dest);
                    }

                    links.get(src).add(dest);
                }
            });
        }).start();
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
