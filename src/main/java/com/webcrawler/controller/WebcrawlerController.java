package com.webcrawler.controller;

import com.webcrawler.domain.GetStatsResponse;
import com.webcrawler.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebcrawlerController {

    @Autowired
    private WebCrawlerService webCrawlerService;

    @GetMapping("/getStats/{baseurl}/nthreads/{nthreads}/delay/{delay}}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<GetStatsResponse> getStats (
                    @PathVariable("baseurl") String baseUrl,
                    @PathVariable("nthreads") int nthreads,
                    @PathVariable("nthreads") int delay
            ) {
        GetStatsResponse response = webCrawlerService.getScanManager(baseUrl, nthreads, delay);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stopStats/{baseurl}/")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Void> stopScanManager (@PathVariable("baseurl") String baseUrl) {
        webCrawlerService.stopScanManager(baseUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}