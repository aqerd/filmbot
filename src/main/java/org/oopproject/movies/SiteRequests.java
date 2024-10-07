package org.oopproject.movies;

import feign.Param;
import feign.RequestLine;

public interface SiteRequests {
    @RequestLine("GET /movie/popular?api_key={api_key}")
    ListOfResults getPopularMovies(@Param("api_key") String tmdbToken);
}
