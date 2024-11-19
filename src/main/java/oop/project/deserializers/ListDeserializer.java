package oop.project.deserializers;

import java.util.List;

public class ListDeserializer<T> {
    public DateDeserializer dates;
    public int page;
    public int id;
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