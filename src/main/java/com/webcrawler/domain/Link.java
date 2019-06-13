package com.webcrawler.domain;

import java.util.Objects;

public class Link {

    private String link;
    private int hashCode;

    public Link(String link) {
        this.link = link;
        this.hashCode = Objects.hash(link);
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link1 = (Link) o;
        return Objects.equals(link, link1.link);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return link;
    }
}
