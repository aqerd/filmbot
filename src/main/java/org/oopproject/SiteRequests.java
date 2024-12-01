package org.oopproject;

import feign.Param;
import feign.RequestLine;
import org.oopproject.deserializers.*;
import org.oopproject.parameters.MovieParameters;

import java.util.List;

public interface SiteRequests {

    @RequestLine("GET /authentication?api_key={api_key}")
    AuthDeserializer checkAuthStatus(@Param("api_key") String token);

    @RequestLine("GET /movie/popular?api_key={api_key}")
    ListDeserializer getPopularMovies(@Param("api_key") String token);

    @RequestLine("GET /movie/{id}?api_key={api_key}")
    FilmDeserializer getMovieById(@Param("api_key") String token, @Param("id") String id);

    @RequestLine("GET /movie/upcoming?api_key={api_key}&language={language}&page={page}")
    ListDeserializer findUpcomingMovies(@Param("api_key") String token,
                                               @Param("language") String language,
                                               @Param("page") int page);

    @RequestLine("GET /movie/{id}/videos?api_key={api_key}&language={language}")
    MovieVideosResponse getMovieVideos(@Param("api_key") String token,
                                       @Param("id") String movieId,
                                       @Param("language") String language);



    @RequestLine("GET /discover/movie" +
            "?api_key={api_key}" +
            "&certification.lte={certification_lte}" +
            "&certification_country={certification_country}" +
            "&include_adult={include_adult}" +
//            "&include_video={include_video}" +
            "&language={language}" +
            "&page={page}" +
//            "&primary_release_year={primary_release_year}" +
//            "&region={region}" +
            "&release_date.gte={release_date_gte}" +
            "&release_date.lte={release_date_lte}" +
            "&sort_by={sort_by}" +
            "&vote_average.gte={vote_average_gte}" +
            "&vote_average.gte={vote_average_lte}" +
//            "&watch_region={watch_region}" +
//            "&with_cast={with_cast}" +
//            "&with_companies={with_companies}" +
//            "&with_crew={with_crew}" +
            "&with_genres={with_genres}" +
            "&with_origin_country={with_origin_country}" +
//            "&with_original_language={with_original_language}" +
            "&with_runtime.gte={with_runtime_gte}" +
//            "&with_runtime.lte={with_runtime_lte}" +
//            "&without_genres={without_genres}" +
            "&year={year}")
    ListDeserializer findMovie(@Param("api_key") String token,
                               @Param("certification_lte") String certificationLte,
                               @Param("certification_country") String certificationCountry,
                               @Param("include_adult") boolean includeAdult,
//                           @Param("include_video") boolean includeVideo,
                               @Param("language") String language,
                               @Param("page") int page,
//                           @Param("primary_release_year") int primaryReleaseYear,
//                           @Param("region") String region,
                               @Param("release_date_gte") String releaseDateGte,
                               @Param("release_date_lte") String releaseDateLte,
                               @Param("sort_by") String sortBy,
                               @Param("vote_average_gte") float voteAverageGte,
                               @Param("vote_average_lte") float voteAverageLte,
//                           @Param("watch_region") String watchRegion,
//                           @Param("with_cast") String withCast,
//                           @Param("with_companies") String withCompanies,
//                           @Param("with_crew") String withCrew,
                               @Param("with_genres") String withGenres,
                               @Param("with_origin_country") String withOriginCountry,
//                           @Param("with_original_language") String withOriginalLanguage,
                               @Param("with_runtime_gte") float withRuntimeGte,
//                           @Param("with_runtime_lte") float withRuntimeLte,
//                           @Param("without_genres") String withoutGenres,
                               @Param("year") int year
    );

    default ListDeserializer findMovie(MovieParameters params) {
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

    /*
    @RequestLine("GET /movie/{id}/similar?api_key={api_key}")
    ListDeserializer getSimilarMovies(@Param("api_key") String token, @Param("id") String id);

    @RequestLine("GET /movie/{id}/recommendations?api_key={api_key}")
    ListDeserializer getRecommendationsForMovie(@Param("api_key") String token, @Param("id") String id);

    @RequestLine("GET /search/movie?query={query}&include_adult={adult}&language={language}&page={page}&year={year}")
    ListDeserializer searchMovie(@Param("api_key") String token,
                             @Param("query") String query,
                             @Param("adult") boolean adult,
                             @Param("language") String language,
                             @Param("page") int page,
                             @Param("year") String year);
     */
}
