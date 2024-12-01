package oop.project.deserializers;

import java.util.List;

public class ListDeserializer<T> {
    private DateDeserializer dates;
    private int page;
    private int id;
    private List<T> results;
    private int total_pages;
    private int total_results;

    public DateDeserializer getDates() {
        return dates;
    }

    public void setDates(DateDeserializer dates) {
        this.dates = dates;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public ListDeserializer<T> sortByPopularity() {
        if (results == null || results.isEmpty()) {
            return this;
        }

        results.sort((o1, o2) -> {
            try {
                double popularity1 = (double) o1.getClass().getMethod("getPopularity").invoke(o1);
                double popularity2 = (double) o2.getClass().getMethod("getPopularity").invoke(o2);
                return Double.compare(popularity2, popularity1);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при попытке получить поле popularity", e);
            }
        });

        return this;
    }
}