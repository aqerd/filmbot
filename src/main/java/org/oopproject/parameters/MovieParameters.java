package org.oopproject.parameters;

public record MovieParameters(
        String token,
        String certificationLte,
        String certificationCountry,
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
) {}


//public record MovieParameters(
//        String token,
//        String certificationLte,
//        String certificationCountry,
//        boolean includeAdult,
//        String language,
//        int page,
//        String releaseDateGte,
//        String releaseDateLte,
//        String sortBy,
//        int voteAverageGte,
//        int voteAverageLte,
//        String withGenres,
//        String withOriginCountry,
//        float withRuntimeGte,
//        int year
//) {
//    public MovieParameters() {
//        this(TMDB_TOKEN, "G","US",false, "en", 1,
//                "1900-01-01", "2100-01-01", "popularity.desc", 0,
//                10, "", "US", 0, 0);
//    }
//
//    public MovieParameters withToken(String token) {
//        return new MovieParameters(token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withCertificationLte(String certificationLte) {
//        return new MovieParameters(this.token, certificationLte, this.certificationCountry, includeAdult, this.language,
//                this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withCertificationCountry(String certificationCountry) {
//        return new MovieParameters(this.token, this.certificationLte, certificationCountry, includeAdult, this.language,
//                this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withIncludeAdult(boolean includeAdult) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withLanguage(String language) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withPage(int page) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withReleaseDateGte(String releaseDateGte) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withReleaseDateLte(String releaseDateLte) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withSortBy(String sortBy) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withVoteAverageGte(int voteAverageGte) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withVoteAverageLte(int voteAverageLte) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withGenres(String withGenres) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, withGenres, this.withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withOriginCountry(String withOriginCountry) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, withOriginCountry, this.withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withRuntimeGte(float withRuntimeGte) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, withRuntimeGte, this.year);
//    }
//
//    public MovieParameters withYear(int year) {
//        return new MovieParameters(this.token, this.certificationLte, this.certificationCountry, this.includeAdult,
//                this.language, this.page, this.releaseDateGte, this.releaseDateLte, this.sortBy, this.voteAverageGte,
//                this.voteAverageLte, this.withGenres, this.withOriginCountry, this.withRuntimeGte, year);
//    }
//}
