package com.webcrawler.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;

public class Scanner {

    private final static Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

    private final Finder finder;
    private final WebClient.Builder webClient;

    public Scanner(Finder finder) {
        this.finder = finder;
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
            System.err.println(o.getRequest().getURI());
        } else {
            LOGGER.warn(t.getMessage());
//            t.printStackTrace();
        }
    }

    private void handleResponse(String s) {
        try {
            finder.scan(new ByteArrayInputStream(s.getBytes()));
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.warn(e.getMessage());
        }
    }
}
