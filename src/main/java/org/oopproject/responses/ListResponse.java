package org.oopproject.responses;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class ListResponse {
    public int page;
    public List<FilmResponse> results;
    public int total_pages;
    public int total_results;
    public String title; // Поле для хранения названия фильма
    public String overview; // Краткое описание

    // Геттер для получения списка фильмов
    public List<FilmResponse> getResults() {
        return results;
    }

    // Сеттер (если нужно)
    public void setResults(List<FilmResponse> results) {
        this.results = results;
    }
}