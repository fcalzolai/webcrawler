package com.webcrawler.domain;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

public class UrlFinder {

    private static final String WORD = "href=";

    private Consumer<Set<String>> consumer;
    private Set<String> newLinks;

    public void setConsumer(Consumer<Set<String>> consumer) {
        this.consumer = consumer;
    }

    public void scan(InputStream is) throws IOException {
        newLinks = new HashSet<>();

        BufferedInputStream bis = new BufferedInputStream(is);
        Scanner sc = new Scanner(bis, "UTF-8");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            scanLine(line);
        }
        if (sc.ioException() != null) {
            throw sc.ioException();
        }

        consumer.accept(newLinks);
    }

    private void scanLine(String line) {
        int index = 0;
        while(index != -1){
            index = line.indexOf(WORD, index);
            if (index != -1) {
                String link = Utils.extractLinkFromHref(line, WORD, index);
                if(link.length() > 0) {
                    newLinks.add(link);
                    index += link.length();
                } else {
                    index++;
                }
            }
        }
    }

}
