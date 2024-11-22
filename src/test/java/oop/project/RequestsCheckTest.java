package oop.project;

import oop.project.deserializers.*;
import org.junit.jupiter.api.Test;

import static oop.project.shared.Config.tmdbService;
import static oop.project.shared.Config.apiToken;
import static org.junit.jupiter.api.Assertions.*;

public class RequestsCheckTest {
    private final int FILM_ID = 725201; // The Gray Man
    private final int ACTOR_ID = 30614; // Ryan Gosling

    @Test
    void checkAuthStatusTest() {
        AuthDeserializer response = tmdbService().authStatus(apiToken());
        assertNotNull(response, "Response should not be null");
        assertTrue(Boolean.parseBoolean(response.getSuccess()), "Expected status should be true");
        assertEquals(1, response.getStatus_code(), "Expected status code should be 1");
        assertEquals("Success.", response.getStatus_message(), "Expected status should be 'Success.'");
    }

    @Test
    void getPopularMoviesTest() {
        ListDeserializer<FilmDeserializer> response = tmdbService().getPopular(apiToken());
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getMovieByIdTest() {
        FilmDeserializer response = tmdbService().getMovieById(apiToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertEquals("The Gray Man", response.getTitle(), "Expected movie should be 'The Gray Man'");
    }

    @Test
    void getSimilarMovies() {
        ListDeserializer<FilmDeserializer> response = tmdbService().getSimilar(apiToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getRecommendationsForMovieTest() {
        ListDeserializer<FilmDeserializer> response = tmdbService().getRecommended(apiToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getUpcomingTest() {
        ListDeserializer<FilmDeserializer> response = tmdbService().getUpcoming(apiToken());
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getTopRatedTest() {
        ListDeserializer<FilmDeserializer> response = tmdbService().getTopRated(apiToken());
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getActorTest() {
        PersonDeserializer response = tmdbService().getActorById(apiToken(), ACTOR_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBirthday(), "Result should not be null");
        assertEquals(response.getName(), "Ryan Gosling", "Result should be Ryan Gosling");
    }

    @Test
    void getActorsFilmsTest() {
        CreditsDeserializer response = tmdbService().getActorsMovies(apiToken(), ACTOR_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCrew(), "Results should not be null");
        assertNotNull(response.getCast(), "Results should not be null");
    }
    
    @Test
    void getVideosForFilmTest() {
        ListDeserializer<VideoDeserializer> response = tmdbService().getVideosForMovie(apiToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertNotEquals(0, response.getId(), "Id should not be null");
    }

    @Test
    void searchMovieTest() {
        ListDeserializer<FilmDeserializer> response = tmdbService()
                .searchMovie(apiToken(), "Oppenheimer", "en-US", 1);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void searchPersonTest() {
        ListDeserializer<PersonDeserializer> response = tmdbService()
                .searchPerson(apiToken(), "Ryan Gosling", "en-US", 1);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void findMovieTest() {
        MovieParameters params = MovieParameters.builder().withGenres("28").build();
        ListDeserializer<FilmDeserializer> response = tmdbService().findMovie(params);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }
}