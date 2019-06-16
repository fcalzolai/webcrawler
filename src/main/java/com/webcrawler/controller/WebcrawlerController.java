package com.webcrawler.controller;

import com.webcrawler.domain.GetDataRequest;
import com.webcrawler.domain.GetDataResponse;
import com.webcrawler.domain.GetStatsResponse;
import com.webcrawler.domain.PostStatsRequest;
import com.webcrawler.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebcrawlerController {

    @Autowired
    private WebCrawlerService webCrawlerService;

    @PostMapping("/getStats")
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
    ResponseEntity<GetStatsResponse> stopScanManager(@RequestBody PostStatsRequest postStatsRequest) {
        webCrawlerService.stopScanManager(postStatsRequest.getBaseUrl());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/getLinks")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<GetDataResponse> getScanManager(@RequestBody GetDataRequest getDataRequest) {
        GetDataResponse getDataResponse = webCrawlerService.getScanManagerData(
                getDataRequest.getBaseUrl(),
                getDataRequest.getMaxLinks(),
                getDataRequest.getMaxDest());
        return new ResponseEntity<>(getDataResponse, HttpStatus.OK);
    }
}