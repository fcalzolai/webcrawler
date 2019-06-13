package com.webcrawler.service;

import com.webcrawler.domain.UrlFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class Crawler {

    @Autowired
    private UrlFinder urlFinder;

    public void crawler(String url) {
        WebClient webClient = WebClient.create(url);
        Mono<String> result = webClient.get()
                .retrieve()
                .bodyToMono(String.class);
        result.subscribe(this::handleResponse);
    }

    private void handleResponse(String s) {
        try {
            urlFinder.scan(new ByteArrayInputStream(s.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
