package org.oopproject;

import feign.Param;
import feign.RequestLine;
import org.oopproject.responses.AuthResponse;
import org.oopproject.responses.FilmResponse;
import org.oopproject.responses.ListResponse;

public interface SiteRequests {
    @RequestLine("GET /authentication?api_key={api_key}")
    AuthResponse checkAuthStatus(@Param("api_key") String token);

    @RequestLine("GET /movie/popular?api_key={api_key}")
    ListResponse getPopularMovies(@Param("api_key") String token);

    @RequestLine("GET /movie/{id}?api_key={api_key}")
    FilmResponse getMovieById(@Param("api_key") String token, @Param("id") String id);

//    @RequestLine("GET /movie/{id}/similar?api_key={api_key}")
//    ListResponse getSimilarMovies(@Param("api_key") String token, @Param("id") String id);
//
//    @RequestLine("GET /movie/{id}/recommendations?api_key={api_key}")
//    ListResponse getRecommendationsForMovie(@Param("api_key") String token, @Param("id") String id);
//
//    @RequestLine("GET /search/movie?query={query}&include_adult={adult}&language={language}&page={page}&year={year}")
//    ListResponse searchMovie(@Param("api_key") String token,
//                             @Param("query") String query,
//                             @Param("adult") boolean adult,
//                             @Param("language") String language,
//                             @Param("page") int page,
//                             @Param("year") String year);
}
