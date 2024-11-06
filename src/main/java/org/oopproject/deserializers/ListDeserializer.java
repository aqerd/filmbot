package org.oopproject.deserializers;

import java.util.List;

public class ListDeserializer {
    public int page;
    public List<FilmDeserializer> results;
    public int total_pages;
    public int total_results;

    public List<FilmDeserializer> getResults() {
        return results;
    }

    public void setResults(List<FilmDeserializer> results) {
        this.results = results;
    }
}