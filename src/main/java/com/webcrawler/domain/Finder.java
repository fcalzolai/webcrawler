package com.webcrawler.domain;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

public class Finder {

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
                String link = extractLinkFromHref(line, WORD, index);
                if(link.length() > 0) {
                    newLinks.add(link);
                    index += link.length();
                } else {
                    index++;
                }
            }
        }
    }

    private String extractLinkFromHref(String line, String word, int index) {
        try {
            int startDoubleQuote = line.indexOf('"', index + word.length());
            int startSingleQuote = line.indexOf('\'', index + word.length());
            int start = minGreaterThanZero(startDoubleQuote, startSingleQuote);
            int end = line.indexOf('"', start + 1);
            return line.substring(start + 1, end);
        } catch (Exception e) {
            return "";
        }
    }

    private int minGreaterThanZero(int val1, int val2) {
        if(val1 > 0
           && val2 > 0) {
            return Math.min(val1, val2);
        } else if (val1 < 0
            && val2 > 0) {
            return val2;
        } else if (val1 > 0
                && val2 < 0) {
            return val1;
        } else {
            return val1;
        }
    }
}
