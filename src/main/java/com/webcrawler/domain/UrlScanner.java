package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;

import static java.lang.String.format;

public class UrlScanner {

    private final static Logger LOGGER = LoggerFactory.getLogger(UrlScanner.class);

    private final UrlFinder urlFinder;
    private final WebClient.Builder webClient;

    public UrlScanner(UrlFinder urlFinder) {
        this.urlFinder = urlFinder;
        webClient = WebClient.builder();
    }

    public void scan(String url) {
        try {
            String block = webClient.baseUrl(url)
                    .build()
                    .get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            handleResponse(block);
        } catch (Throwable t) {
            handleError(t);
        }
    }

    private void handleError(Throwable t) {
        if (t instanceof WebClientResponseException) {
            WebClientResponseException o = (WebClientResponseException) t;
            LOGGER.warn(format("Exception with URL[%s]", o.getRequest().getURI()));
        } else {
            LOGGER.warn(t.getMessage());
        }
    }

    private void handleResponse(String s) {
        try {
            urlFinder.scan(new ByteArrayInputStream(s.getBytes()));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
    }
}
