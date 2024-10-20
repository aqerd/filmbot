package org.oopproject.responses;

import java.util.List;

public class ListResponse {
    public int page;
    public List<FilmResponse> results;
    public int total_pages;
    public int total_results;

    public List<FilmResponse> getResults() {
        return results;
    }

    public void setResults(List<FilmResponse> results) {
        this.results = results;
    }
}