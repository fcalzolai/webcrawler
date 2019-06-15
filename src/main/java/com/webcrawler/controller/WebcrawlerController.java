package com.webcrawler.controller;

import com.webcrawler.domain.GetStatsResponse;
import com.webcrawler.domain.PostStatsRequest;
import com.webcrawler.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebcrawlerController {

    @Autowired
    private WebCrawlerService webCrawlerService;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<GetStatsResponse> start(@RequestBody PostStatsRequest postStatsRequest) {
        GetStatsResponse response = webCrawlerService.getScanManager(
                postStatsRequest.getBaseUrl(),
                postStatsRequest.getnThreads(),
                postStatsRequest.getDelay());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/stop")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Void> stopScanManager(@RequestBody PostStatsRequest postStatsRequest) {
        webCrawlerService.stopScanManager(postStatsRequest.getBaseUrl());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}