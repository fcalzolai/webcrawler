package com.webcrawler.service;

import com.webcrawler.Utils;
import com.webcrawler.domain.GetStatsResponse;
import com.webcrawler.domain.ScanManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class WebCrawlerService {

    private Set<ScanManager> scanManagers;

    public WebCrawlerService(){
        scanManagers = new HashSet<>();
    }

    public GetStatsResponse getScanManager(String baseUrl) {
        return getScanManager(baseUrl, Utils.N_THREADS, Utils.DELAY);
    }

    public GetStatsResponse getScanManager(String baseUrl, int nThreads, int delay) {
        ScanManager scanManager = getOrCreate(baseUrl, nThreads, delay);
        GetStatsResponse response = new GetStatsResponse();
        response.setToBeScanned(scanManager.getToBeScannedSize());
        response.setEdges(scanManager.getEdgesSize());
        return response;
    }

    private ScanManager getOrCreate(String baseUrl, int nThreads, int delay) {
        Optional<ScanManager> first = scanManagers.stream()
                .filter(scanManager -> scanManager.getBasUrl().equals(baseUrl))
                .findFirst();

        return first.orElseGet(() -> {
            ScanManager scanManager = new ScanManager(baseUrl, nThreads, delay);
            scanManagers.add(scanManager);
            return scanManager;
        });
    }

    public void stopScanManager(String baseUrl) {
        scanManagers.stream()
                .filter(scanManager -> scanManager.getBasUrl().equals(baseUrl))
                .findFirst()
                .ifPresent(ScanManager::shoutDown);
    }
}