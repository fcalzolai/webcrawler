package com.webcrawler.domain;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class ScanManagerTest {

    private static final String LLOYDS = "https://www.lloydsbank.com/";
    private static final String HALIFAX = "https://www.halifax.co.uk/";
    private static final String REP = "https://www.repubblica.it/";
    private static final String CORRIERE = "https://www.corriere.it";

    @BeforeClass
    public static void beforeClass(){
        setAwaitility();
        setLogLevel();
    }

    @Test
    public void test() {
        ScanManager scanManager = new ScanManager(LLOYDS);

        await().atLeast(10, TimeUnit.SECONDS)
                .atMost(30, TimeUnit.SECONDS)
                .until(() -> scanManager.getLinksSize() > 0);
        scanManager.shoutDown();
        Map<String, Set<String>> links = scanManager.getLinks();
        Assert.assertTrue(!links.isEmpty());

        System.out.println(links.values().stream().mapToInt(Set::size).sum());
    }

    private static void setAwaitility() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.SECONDS);
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
