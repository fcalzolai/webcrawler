package com.webcrawler.domain;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

public class ScanManagerTest {

    private static final String LLOYDS = "https://www.lloydsbank.com/";
    private static final String HALIFAX = "https://www.halifax.co.uk/";
    private static final String REP = "https://www.repubblica.it/";
    private static final String CORRIERE = "https://www.corriere.it";
    private static final String CHIMA = "https://www.chima.it/";
    public static final int AT_LEAST_TIMEOUT = 60;
    public static final int AT_MOST_TIMEOUT = 100;

    @BeforeClass
    public static void beforeClass(){
        setAwaitility();
        setLogLevel();
    }

    @Test
    public void test() {
        ScanManager scanManager = new ScanManager(CHIMA);

        await().atLeast(AT_LEAST_TIMEOUT, TimeUnit.SECONDS)
                .atMost(AT_MOST_TIMEOUT, TimeUnit.SECONDS)
                .until(() -> scanManager.getLinksSize() > 0);
        scanManager.shoutDown();
        Map<Link, Set<Link>> links = scanManager.getLinks();
        Assert.assertFalse(links.isEmpty());

        List<Link> collect = links.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Link> distinct = collect.stream()
                .distinct()
                .collect(Collectors.toList());

        List<Link> duplicated = collect.stream()
                .filter(s -> Collections.frequency(collect, s) > 1)
                .sorted(Comparator.comparing(Link::getLink))
                .collect(Collectors.toList());

        System.out.println("Duplicated: " + duplicated.size());
        System.out.println("Distinct: " + distinct.size());
    }

    private static void setAwaitility() {
        Awaitility.setDefaultPollInterval(AT_LEAST_TIMEOUT, TimeUnit.SECONDS);
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.TEN_MINUTES);
    }

    private static void setLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("io.netty").setLevel(Level.INFO);
        loggerContext.getLogger("reactor.netty").setLevel(Level.INFO);
        loggerContext.getLogger("org.springframework.core.codec").setLevel(Level.INFO);
        loggerContext.getLogger("org.springframework.web").setLevel(Level.INFO);
    }
}
