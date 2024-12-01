package org.oopproject.services;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.oopproject.SiteRequests;
import org.oopproject.deserializers.FilmDeserializer;
import org.oopproject.deserializers.ListDeserializer;
import org.oopproject.deserializers.MovieVideosResponse;
import org.oopproject.deserializers.VideoDeserializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {
    private SiteRequests siteRequests;
    private int currentPage;

    public MovieService() {
        siteRequests = Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logLevel(Logger.Level.FULL)
                .target(SiteRequests.class, "https://api.themoviedb.org/3");
        this.currentPage = 1;
    }

    private boolean isUpcoming(String releaseDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date release = sdf.parse(releaseDate);
            Date now = new Date();
            return release.after(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<FilmDeserializer> getUpcomingMovies() {
        try {
            ListDeserializer upcomingMovies = siteRequests.findUpcomingMovies(
                    "240e7fef369901fb314c80d53d1532d1",
                    "ru",
                    currentPage
            );

            if (upcomingMovies != null && upcomingMovies.results != null) {
                List<FilmDeserializer> resultList = upcomingMovies.results.stream()
                        .filter(movie -> isUpcoming(movie.release_date))
                        .limit(10) // Ограничиваем до 10 фильмов
                        .collect(Collectors.toList());
                for (FilmDeserializer movie : resultList) {
                    MovieVideosResponse videoResponse = siteRequests.getMovieVideos(
                            "240e7fef369901fb314c80d53d1532d1",
                            String.valueOf(movie.id),
                            "ru"
                    );

                    if (videoResponse != null && videoResponse.results != null && !videoResponse.results.isEmpty()) {
                        VideoDeserializer trailer = videoResponse.results.stream()
                                .filter(v -> v.site.equals("YouTube"))
                                .findFirst()
                                .orElse(null);

                        if (trailer != null) {
                            movie.trailerUrl = "https://www.youtube.com/watch?v=" + trailer.key;
                        }
                    }
                }
                currentPage++;
                return resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
