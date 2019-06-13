package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class Scanner extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

    private final ScanManager scanManager;
    private UrlFinder urlFinder;
    private UrlScanner urlScanner;

    public Scanner(String name, ScanManager scanManager) {
        super(name);

        this.scanManager = scanManager;
        urlFinder = new UrlFinder();
        urlScanner = new UrlScanner(urlFinder);
    }

    @Override
    public void run() {
        try {
            Link path = scanManager.getLinkToScan();
            urlFinder.setConsumer(newLinks -> scanManager.newLinksFound(path, newLinks));
            urlScanner.scan(scanManager.getFullUrlToScan(path));
            LOGGER.debug(format("Scanned: [Thread%s] - toBeScanned[%s] - links[%s]",
                    getName(),
                    scanManager.getLinksToBeScannedSize(),
                    scanManager.getLinksSize()));
        } catch (Throwable e) {
            LOGGER.trace(e.getMessage());
        }
    }
}
