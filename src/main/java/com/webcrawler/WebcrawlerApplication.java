package com.webcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class WebcrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcrawlerApplication.class, args);
	}

}
