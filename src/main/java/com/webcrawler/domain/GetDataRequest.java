package com.webcrawler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GetDataRequest {

    @JsonProperty
    private String baseUrl;

    @JsonProperty
    private int maxLinks;

    @JsonProperty
    private int maxDest;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getMaxLinks() {
        return maxLinks;
    }

    public void setMaxLinks(int maxLinks) {
        this.maxLinks = maxLinks;
    }

    public int getMaxDest() {
        return maxDest;
    }

    public void setMaxDest(int maxDest) {
        this.maxDest = maxDest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetDataRequest that = (GetDataRequest) o;
        return maxLinks == that.maxLinks &&
                maxDest == that.maxDest &&
                Objects.equals(baseUrl, that.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, maxLinks, maxDest);
    }

    @Override
    public String toString() {
        return "GetDataRequest{" +
                "baseUrl='" + baseUrl + '\'' +
                ", maxLinks=" + maxLinks +
                ", maxDest=" + maxDest +
                '}';
    }
}
