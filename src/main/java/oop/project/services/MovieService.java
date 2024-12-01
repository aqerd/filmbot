package oop.project.services;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import oop.project.SiteRequests;
import oop.project.deserializers.FilmDeserializer;
import oop.project.deserializers.ListDeserializer;
import oop.project.deserializers.VideoDeserializer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static oop.project.shared.Config.apiToken;
import static oop.project.shared.Config.youtubeUrl;

public class MovieService {
    private final SiteRequests siteRequests;
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
            ListDeserializer<FilmDeserializer> upcomingMovies = siteRequests.getUpcoming(apiToken());

            if (upcomingMovies != null && upcomingMovies.getResults() != null) {
                List<FilmDeserializer> resultList = upcomingMovies.getResults().stream()
                        .filter(movie -> isUpcoming(movie.getRelease_date()))
                        .limit(10)
                        .collect(Collectors.toList());
                for (FilmDeserializer movie : resultList) {
                    ListDeserializer<VideoDeserializer> videoResponse = siteRequests.getVideosForMovie(apiToken(), movie.getId());

                    if (videoResponse != null && videoResponse.getResults() != null && !videoResponse.getResults().isEmpty()) {
                        videoResponse.getResults().stream()
                                .filter(v -> v.getSite().equals("YouTube"))
                                .findFirst().ifPresent(trailer -> movie.setTrailerUrl(youtubeUrl() + trailer.getKey()));
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
