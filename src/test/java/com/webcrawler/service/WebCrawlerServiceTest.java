package com.webcrawler.service;

import com.webcrawler.domain.GetStatsResponse;
import com.webcrawler.domain.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WebCrawlerServiceTest {

    private static final String BASE_URL = TestUtils.CORRIERE;

    private WebCrawlerService webCrawlerService;

    @Before
    public void before(){
        TestUtils.setLogLevel();
        webCrawlerService = new WebCrawlerService();
    }

    @After
    public void after(){
        webCrawlerService.stopScanManager(BASE_URL);
    }

    @Test
    public void crawler() throws InterruptedException {
        GetStatsResponse stats = webCrawlerService.getScanManager(
                BASE_URL,
                20,
                500);

        Assert.assertNotNull(stats);
        System.out.println(stats);

        Thread.sleep(5_000);
        stats = webCrawlerService.getScanManager(TestUtils.CORRIERE);
        System.out.println(stats);
    }
}