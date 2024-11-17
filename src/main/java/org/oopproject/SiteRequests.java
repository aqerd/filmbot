package org.oopproject;

import feign.Param;
import feign.RequestLine;
import org.oopproject.deserializers.FilmDeserializer;
import org.oopproject.deserializers.PersonDeserializer;
import org.oopproject.parameters.MovieParameters;
import org.oopproject.deserializers.AuthDeserializer;
import org.oopproject.deserializers.ListDeserializer;

public interface SiteRequests {
    @RequestLine("GET /authentication?api_key={api_key}")
    AuthDeserializer checkAuthStatus(@Param("api_key") String token);

    @RequestLine("GET /movie/popular?api_key={api_key}")
    ListDeserializer<FilmDeserializer> getPopularMovies(@Param("api_key") String token);

    @RequestLine("GET /movie/{id}?api_key={api_key}")
    FilmDeserializer getMovieById(@Param("api_key") String token, @Param("id") int id);

    @RequestLine("GET /movie/{id}/similar?api_key={api_key}")
    ListDeserializer<FilmDeserializer> getSimilarMovies(@Param("api_key") String token, @Param("id") int id);

    @RequestLine("GET /movie/{id}/recommendations?api_key={api_key}")
    ListDeserializer<FilmDeserializer> getRecommendationsForMovie(@Param("api_key") String token, @Param("id") int id);

    @RequestLine("GET /movie/upcoming?api_key={api_key}")
    ListDeserializer<FilmDeserializer> getUpcoming(@Param("api_key") String token);

    @RequestLine("GET /search/movie" +
            "?api_key={api_key}" +
            "&query={query}" +
            "&language={language}" +
            "&page={page}" +
            "&year={year}"
            // +
            // "&include_adult={adult}"
    )
    ListDeserializer<FilmDeserializer> searchMovie(@Param("api_key") String token,
            @Param("query") String query,
            @Param("language") String language,
            @Param("page") int page,
            @Param("year") String year
            // ,
            // @Param("adult") boolean adult
    );

    @RequestLine("GET /search/person?" +
            "api_key={api_key}" +
            "&query={query}" +
            "&language={language}" +
            "&page={page}"
            // +
            // "&include_adult={adult}"
    )
    ListDeserializer<PersonDeserializer> searchPerson(@Param("api_key") String token,
            @Param("query") String query,
            @Param("language") String language,
            @Param("page") int page
            // ,
            // @Param("adult") boolean adult
    );

    @RequestLine("GET /discover/movie" +
            "?api_key={api_key}" +
            "&certification.lte={certification_lte}" +
            "&certification_country={certification_country}" +
            "&include_adult={include_adult}" +
            "&language={language}" +
            "&page={page}" +
            "&release_date.gte={release_date_gte}" +
            "&release_date.lte={release_date_lte}" +
            "&sort_by={sort_by}" +
            "&vote_average.gte={vote_average_gte}" +
            "&vote_average.gte={vote_average_lte}" +
            "&with_genres={with_genres}" +
            "&with_origin_country={with_origin_country}" +
            "&with_runtime.gte={with_runtime_gte}" +
            "&year={year}"
            // +
            // "&include_video={include_video}" +
            // "&primary_release_year={primary_release_year}" +
            // "&region={region}" +
            // "&watch_region={watch_region}" +
            // "&with_cast={with_cast}" +
            // "&with_companies={with_companies}" +
            // "&with_crew={with_crew}" +
            // "&with_original_language={with_original_language}" +
            // "&with_runtime.lte={with_runtime_lte}" +
            // "&without_genres={without_genres}"
            )
    ListDeserializer<FilmDeserializer> findMovie(@Param("api_key") String token,
            @Param("certification_lte") String certificationLte,
            @Param("certification_country") String certificationCountry,
            @Param("include_adult") boolean includeAdult,
            @Param("language") String language,
            @Param("page") int page,
            @Param("release_date_gte") String releaseDateGte,
            @Param("release_date_lte") String releaseDateLte,
            @Param("sort_by") String sortBy,
            @Param("vote_average_gte") float voteAverageGte,
            @Param("vote_average_lte") float voteAverageLte,
            @Param("with_genres") String withGenres,
            @Param("with_origin_country") String withOriginCountry,
            @Param("with_runtime_gte") float withRuntimeGte,
            @Param("year") int year
            // ,
            // @Param("include_video") boolean includeVideo,
            // @Param("primary_release_year") int primaryReleaseYear,
            // @Param("region") String region,
            // @Param("watch_region") String watchRegion,
            // @Param("with_cast") String withCast,
            // @Param("with_companies") String withCompanies,
            // @Param("with_crew") String withCrew,
            // @Param("with_original_language") String withOriginalLanguage,
            // @Param("with_runtime_lte") float withRuntimeLte,
            // @Param("without_genres") String withoutGenres,
    );

    default ListDeserializer<FilmDeserializer> findMovie(MovieParameters params) {
        return findMovie(
                params.token(),
                params.certificationLte(),
                params.certificationCountry(),
                params.includeAdult(),
                params.language(),
                params.page(),
                params.releaseDateGte(),
                params.releaseDateLte(),
                params.sortBy(),
                params.voteAverageGte(),
                params.voteAverageLte(),
                params.withGenres(),
                params.withOriginCountry(),
                params.withRuntimeGte(),
                params.year()
        );
    }
}
