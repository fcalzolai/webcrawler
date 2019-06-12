package com.webcrawler.domain;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class FinderTest {

    private static final String FILE_TO_SCAN = "lbg.html";

    @Test
    public void findWord() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_TO_SCAN);

        AtomicInteger count = new AtomicInteger(0);
        Finder finder= new Finder(s -> {
            System.out.println(s);
            count.incrementAndGet();
        });
        finder.scan(is);
        Assert.assertTrue(count.get() > 0);
    }
}