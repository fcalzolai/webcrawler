package com.webcrawler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GetStatsResponse {

    @JsonProperty
    private int toBeScanned;

    @JsonProperty
    private int links;

    @JsonProperty
    private int edges;

    public int getToBeScanned() {
        return toBeScanned;
    }

    public void setToBeScanned(int toBeScanned) {
        this.toBeScanned = toBeScanned;
    }

    public int getLinks() {
        return links;
    }

    public void setLinks(int links) {
        this.links = links;
    }

    public int getEdges() {
        return edges;
    }

    public void setEdges(int edges) {
        this.edges = edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetStatsResponse that = (GetStatsResponse) o;
        return toBeScanned == that.toBeScanned &&
                links == that.links &&
                edges == that.edges;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toBeScanned, links, edges);
    }

    @Override
    public String toString() {
        return "GetStatsResponse{" +
                "toBeScanned=" + toBeScanned +
                ", links=" + links +
                ", edges=" + edges +
                '}';
    }
}
