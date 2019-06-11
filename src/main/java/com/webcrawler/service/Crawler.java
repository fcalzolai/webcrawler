package com.webcrawler.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class Crawler {

    public void crawler(String url) throws InterruptedException {
        WebClient webClient = WebClient.create(url);
        Mono<String> result = webClient.get()
                .retrieve()
                .bodyToMono(String.class);
        result.subscribe(Crawler::handleResponse);
        System.out.println("After subscribe");
        //wait for a while for the response
        Thread.sleep(1000);
    }

    private static void handleResponse(String s) {
        System.out.println("handle response");
        System.out.println(s);
    }
}
