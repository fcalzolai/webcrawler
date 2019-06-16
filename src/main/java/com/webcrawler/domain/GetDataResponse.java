package com.webcrawler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GetDataResponse {

    @JsonProperty
    private Map<Link, Set<Link>> links;

    public GetDataResponse(Map<Link, Set<Link>> links) {
        this.links = links;
    }

    public Map<Link, Set<Link>> getLinks() {
        return links;
    }

    public void setLinks(Map<Link, Set<Link>> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetDataResponse that = (GetDataResponse) o;
        return Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links);
    }
}
