package com.webcrawler.service;

import org.junit.Test;

public class CrawlerTest {

    @Test
    public void crawler() throws InterruptedException {
        Crawler crawler = new Crawler();
        crawler.crawler("https://www.lloydsbank.com/");
    }
}