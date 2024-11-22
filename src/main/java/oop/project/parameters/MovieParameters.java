package oop.project.parameters;

import static oop.project.shared.Config.apiToken;

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
) {
    public static class Builder {
        private String token = apiToken();
        private String certificationLte = "G";
        private String certificationCountry = "US";
        private boolean includeAdult = false;
        private String language = "en";
        private int page = 1;
        private String releaseDateGte = "1900-01-01";
        private String releaseDateLte = "2100-01-01";
        private String sortBy = "popularity.desc";
        private int voteAverageGte = 0;
        private int voteAverageLte = 10;
        private String withGenres = "";
        private String withOriginCountry = "US";
        private float withRuntimeGte = 0;
        private int year = 0;

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withCertificationLte(String certificationLte) {
            this.certificationLte = certificationLte;
            return this;
        }

        public Builder withCertificationCountry(String certificationCountry) {
            this.certificationCountry = certificationCountry;
            return this;
        }

        public Builder withIncludeAdult(boolean includeAdult) {
            this.includeAdult = includeAdult;
            return this;
        }

        public Builder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder withPage(int page) {
            this.page = page;
            return this;
        }

        public Builder withReleaseDateGte(String releaseDateGte) {
            this.releaseDateGte = releaseDateGte;
            return this;
        }

        public Builder withReleaseDateLte(String releaseDateLte) {
            this.releaseDateLte = releaseDateLte;
            return this;
        }

        public Builder withSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder withVoteAverageGte(int voteAverageGte) {
            this.voteAverageGte = voteAverageGte;
            return this;
        }

        public Builder withVoteAverageLte(int voteAverageLte) {
            this.voteAverageLte = voteAverageLte;
            return this;
        }

        public Builder withGenres(String withGenres) {
            this.withGenres = withGenres;
            return this;
        }

        public Builder withOriginCountry(String withOriginCountry) {
            this.withOriginCountry = withOriginCountry;
            return this;
        }

        public Builder withRuntimeGte(float withRuntimeGte) {
            this.withRuntimeGte = withRuntimeGte;
            return this;
        }

        public Builder withYear(int year) {
            this.year = year;
            return this;
        }

        public MovieParameters build() {
            return new MovieParameters(token, certificationLte, certificationCountry, includeAdult, language, page,
                    releaseDateGte, releaseDateLte, sortBy, voteAverageGte, voteAverageLte, withGenres,
                    withOriginCountry, withRuntimeGte, year);
        }
    }
}