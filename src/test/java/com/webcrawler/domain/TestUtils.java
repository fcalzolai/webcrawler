package com.webcrawler.domain;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static final String BBC = "https://www.bbc.co.uk/";
    public static final String CORRIERE = "https://www.corriere.it";
    public static final String CHIMA = "https://www.chima.it/";
    public static final String MONZO = "https://monzo.com";
    public static final int AT_LEAST_TIMEOUT = 5;
    public static final int AT_MOST_TIMEOUT = AT_LEAST_TIMEOUT+3;

    public static void setAwaitility() {
        Awaitility.setDefaultPollInterval(AT_LEAST_TIMEOUT, TimeUnit.SECONDS);
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.TEN_MINUTES);
    }

    public static void setLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("io.netty").setLevel(Level.INFO);
        loggerContext.getLogger("reactor.netty").setLevel(Level.INFO);
        loggerContext.getLogger("org.springframework.core.codec").setLevel(Level.INFO);
        loggerContext.getLogger("org.springframework.web").setLevel(Level.INFO);
    }
}
