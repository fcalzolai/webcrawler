package com.webcrawler.domain;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ScanManagerTest {

    private static final String BASE_URL = "https://www.lloydsbank.com";

    @BeforeClass
    public static void beforeClass(){
        Awaitility.setDefaultPollInterval(10, TimeUnit.SECONDS);
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.TEN_MINUTES);
    }

    @Test
    public void test() throws InterruptedException {
        ScanManager scanManager = new ScanManager(BASE_URL);

        Thread.sleep(30_000);
//        await().atLeast(10, TimeUnit.MINUTES)
//               .until(() -> scanManager.getToBeScanned().isEmpty());
        scanManager.shoutDown();
        Map<String, Set<String>> links = scanManager.getLinks();
        System.out.println(links);
    }
}
