package com.webcrawler.domain;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class ScanManagerTest {

    private static final String LLOYDS = "https://www.lloydsbank.com/";
    private static final String HALIFAX = "https://www.halifax.co.uk/";
    private static final String REP = "https://www.repubblica.it/";

    @BeforeClass
    public static void beforeClass(){
        Awaitility.setDefaultPollInterval(10, TimeUnit.SECONDS);
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.TEN_MINUTES);
    }

    @Test
    public void test() {
        ScanManager scanManager = new ScanManager(REP);

        await().atLeast(3, TimeUnit.SECONDS)
                .atMost(30000, TimeUnit.SECONDS)
               .until(() -> scanManager.getToBeScanned().isEmpty());
        scanManager.shoutDown();
        Map<String, Set<String>> links = scanManager.getLinks();
        System.out.println(links.size());
    }
}
