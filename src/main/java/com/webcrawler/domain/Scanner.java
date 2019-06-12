package com.webcrawler.domain;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Scanner {

    private final Finder finder;
    private final WebClient.Builder webClient;

    public Scanner(Finder finder) {
        this.finder = finder;
        webClient = WebClient.builder();
    }

    public void scan(String url) {
        webClient.baseUrl(url)
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(this::handleResponse, t -> {
                    if (t instanceof WebClientResponseException) {
                        WebClientResponseException o = (WebClientResponseException) t;
                        System.err.println(o.getRequest().getURI());
                    } else {
                        t.printStackTrace();
                    }
                });
    }

    private void handleResponse(String s) {
        try {
            finder.scan(new ByteArrayInputStream(s.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
