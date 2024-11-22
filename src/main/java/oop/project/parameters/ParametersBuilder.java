package oop.project.parameters;

import static oop.project.shared.Config.getTmdbToken;

public class ParametersBuilder {
    private String token = getTmdbToken();
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

    public ParametersBuilder withToken(String token) {
        this.token = token;
        return this;
    }

    public ParametersBuilder withCertificationLte(String certificationLte) {
        this.certificationLte = certificationLte;
        return this;
    }

    public ParametersBuilder withCertificationCountry(String certificationCountry) {
        this.certificationCountry = certificationCountry;
        return this;
    }

    public ParametersBuilder withIncludeAdult(boolean includeAdult) {
        this.includeAdult = includeAdult;
        return this;
    }

    public ParametersBuilder withLanguage(String language) {
        this.language = language;
        return this;
    }

    public ParametersBuilder withPage(int page) {
        this.page = page;
        return this;
    }

    public ParametersBuilder withReleaseDateGte(String releaseDateGte) {
        this.releaseDateGte = releaseDateGte;
        return this;
    }

    public ParametersBuilder withReleaseDateLte(String releaseDateLte) {
        this.releaseDateLte = releaseDateLte;
        return this;
    }

    public ParametersBuilder withSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public ParametersBuilder withVoteAverageGte(int voteAverageGte) {
        this.voteAverageGte = voteAverageGte;
        return this;
    }

    public ParametersBuilder withVoteAverageLte(int voteAverageLte) {
        this.voteAverageLte = voteAverageLte;
        return this;
    }

    public ParametersBuilder withGenres(String withGenres) {
        this.withGenres = withGenres;
        return this;
    }

    public ParametersBuilder withOriginCountry(String withOriginCountry) {
        this.withOriginCountry = withOriginCountry;
        return this;
    }

    public ParametersBuilder withRuntimeGte(float withRuntimeGte) {
        this.withRuntimeGte = withRuntimeGte;
        return this;
    }

    public ParametersBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    public MovieParameters build() {
        return new MovieParameters(token, certificationLte, certificationCountry, includeAdult, language, page,
                releaseDateGte, releaseDateLte, sortBy, voteAverageGte, voteAverageLte, withGenres,
                withOriginCountry, withRuntimeGte, year);
    }
}