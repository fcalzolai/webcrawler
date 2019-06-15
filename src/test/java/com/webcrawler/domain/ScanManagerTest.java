package com.webcrawler.domain;

import com.webcrawler.Utils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

public class ScanManagerTest {

    @BeforeClass
    public static void beforeClass(){
        TestUtils.setAwaitility();
        TestUtils.setLogLevel();
    }

    @Test
    public void test() throws InterruptedException {
        ScanManager scanManager = new ScanManager(TestUtils.CORRIERE, Utils.N_THREADS, Utils.DELAY);

        await().atLeast(TestUtils.AT_LEAST_TIMEOUT, TimeUnit.SECONDS)
                .atMost(TestUtils.AT_MOST_TIMEOUT, TimeUnit.SECONDS)
                .until(() -> scanManager.getEdgesSize() > 0);
        scanManager.shoutDown();

        Thread.sleep(5_000);

        Map<Link, Set<Link>> links = scanManager.getLinks();
        Assert.assertFalse(links.isEmpty());

        List<Link> collect = links.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        long distinct = collect.stream()
                .distinct()
                .count();

        System.out.println("ToBeScanned:"+scanManager.getToBeScannedSize()+" - Distinct: " + distinct);
    }

}
