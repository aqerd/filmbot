package org.oopproject;

import static org.oopproject.Config.TMDB_TOKEN;

public record MovieSearchParameters(
        String token,
        boolean includeAdult,
        String language,
        int page,
        String releaseDateGte,
        String releaseDateLte,
        String sortBy,
        int voteAverageGte,
        int voteAverageLte,
        String withGenres,
        String withOriginCountry,
        float withRuntimeGte,
        int year
) {
    public MovieSearchParameters() {
        this(TMDB_TOKEN, false, "en", 1, "1900-01-01",
                "2100-01-01", "popularity.desc", 0, 10, "",
                "US", 0, 0);
    }

    public MovieSearchParameters withToken(String token) {
        return new MovieSearchParameters(token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withIncludeAdult(boolean includeAdult) {
        return new MovieSearchParameters(this.token, includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withLanguage(String language) {
        return new MovieSearchParameters(this.token, this.includeAdult, language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withPage(int page) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withReleaseDateGte(String releaseDateGte) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withReleaseDateLte(String releaseDateLte) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withSortBy(String sortBy) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withVoteAverageGte(int voteAverageGte) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withVoteAverageLte(int voteAverageLte) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withGenres(String withGenres) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withOriginCountry(String withOriginCountry) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withRuntimeGte(float withRuntimeGte) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, withRuntimeGte, this.year);
    }

    public MovieSearchParameters withYear(int year) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, year);
    }
}
