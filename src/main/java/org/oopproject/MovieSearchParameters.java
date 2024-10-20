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

    public MovieSearchParameters withLanguage(String language) {
        return new MovieSearchParameters(this.token, this.includeAdult, language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, this.year);
    }

    public MovieSearchParameters withYear(int year) {
        return new MovieSearchParameters(this.token, this.includeAdult, this.language, this.page, this.releaseDateGte,
                this.releaseDateLte, this.sortBy, this.voteAverageGte, this.voteAverageLte, this.withGenres,
                this.withOriginCountry, this.withRuntimeGte, year);
    }
}
