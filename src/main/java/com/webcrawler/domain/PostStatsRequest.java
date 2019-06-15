package com.webcrawler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PostStatsRequest {

    @JsonProperty
    private String baseUrl;

    @JsonProperty
    private int nThreads;

    @JsonProperty
    private int delay;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getnThreads() {
        return nThreads;
    }

    public void setnThreads(int nThreads) {
        this.nThreads = nThreads;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostStatsRequest that = (PostStatsRequest) o;
        return nThreads == that.nThreads &&
                delay == that.delay &&
                Objects.equals(baseUrl, that.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, nThreads, delay);
    }

    @Override
    public String toString() {
        return "PostStatsRequest{" +
                "baseUrl='" + baseUrl + '\'' +
                ", nThreads=" + nThreads +
                ", delay=" + delay +
                '}';
    }
}
