package org.oopproject.deserializers;

import java.util.List;

public class FilmDeserializer {
    public boolean adult;
    public String backdrop_path;
    public int budget;
    public List<GenreDeserializer> genres;
    public int[] genre_ids;
    public String homepage;
    public String id;
    public String imdb_id;
    public String[] origin_country;
    public String original_language;
    public String original_title;
    public String overview;
    public double popularity;
    public String poster_path;
    public String release_date;
    public String revenue;
    public String runtime;
    public List<LanguageDeserializer> spoken_languages;
    public String status;
    public String tagline;
    public String title;
    public boolean video;
    public double vote_average;
    public double vote_count;
//    public boolean belongs_to_collection;
//    public List<FilmDeserializer> production_companies;
//    public List<FilmDeserializer> production_countries;
}