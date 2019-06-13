package com.webcrawler.domain;

public class Utils {

    public static String extractLinkFromHref(String line, String word, int index) {
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

    private static int minGreaterThanZero(int val1, int val2) {
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
