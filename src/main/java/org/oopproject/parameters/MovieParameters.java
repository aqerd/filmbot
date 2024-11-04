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