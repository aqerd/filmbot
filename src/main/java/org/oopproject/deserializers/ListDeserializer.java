package org.oopproject.deserializers;

import java.util.List;

public class ListDeserializer<T> {
    public DatesDeserializer dates;
    public int page;
    public List<T> results;
    public int total_pages;
    public int total_results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}