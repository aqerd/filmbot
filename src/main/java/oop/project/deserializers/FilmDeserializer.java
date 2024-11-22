package oop.project.deserializers;

import java.util.List;

public class FilmDeserializer {
    private boolean adult;
    private String backdrop_path;
    private int budget;
    private List<Integer> genre_ids;
    private String homepage;
    private int id;
    private String imdb_id;
    private String media_type;
    private String[] origin_country;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private String release_date;
    private String revenue;
    private String runtime;
    private List<LanguageDeserializer> spoken_languages;
    private String status;
    private String tagline;
    private String title;
    private boolean video;
    private double vote_average;
    private double vote_count;
//    private boolean belongs_to_collection;
//    private List<Integer> genres;
//    private List<FilmDeserializer> production_companies;
//    private List<FilmDeserializer> production_countries;

public boolean isAdult() {
    return adult;
}

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String[] getOrigin_country() {
        return origin_country;
    }

    public void setOrigin_country(String[] origin_country) {
        this.origin_country = origin_country;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public List<LanguageDeserializer> getSpoken_languages() {
        return spoken_languages;
    }

    public void setSpoken_languages(List<LanguageDeserializer> spoken_languages) {
        this.spoken_languages = spoken_languages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public double getVote_count() {
        return vote_count;
    }

    public void setVote_count(double vote_count) {
        this.vote_count = vote_count;
    }

//    public boolean isBelongs_to_collection() {
//        return belongs_to_collection;
//    }
//
//    public void setBelongs_to_collection(boolean belongs_to_collection) {
//        this.belongs_to_collection = belongs_to_collection;
//    }
//
//    public List<Integer> getGenres() {
//        return genres;
//    }
//
//    public void setGenres(List<Integer> genres) {
//        this.genres = genres;
//    }
//
//    public List<FilmDeserializer> getProduction_companies() {
//        return production_companies;
//    }
//
//    public void setProduction_companies(List<FilmDeserializer> production_companies) {
//        this.production_companies = production_companies;
//    }
//
//    public List<FilmDeserializer> getProduction_countries() {
//        return production_countries;
//    }
//
//    public void setProduction_countries(List<FilmDeserializer> production_countries) {
//        this.production_countries = production_countries;
//    }
}