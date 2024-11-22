package oop.project;

import oop.project.deserializers.*;
import org.junit.jupiter.api.Test;
import oop.project.parameters.MovieParameters;
import oop.project.parameters.ParametersBuilder;

import static oop.project.shared.Config.*;
import static org.junit.jupiter.api.Assertions.*;

public class RequestsCheckTest {
    private final int FILM_ID = 725201; // The Gray Man
    private final int ACTOR_ID = 30614; // Ryan Gosling

    @Test
    void checkAuthStatusTest() {
        AuthDeserializer response = getTmdbService().checkAuthStatus(getTmdbToken());
        assertNotNull(response, "Response should not be null");
        assertTrue(Boolean.parseBoolean(response.getSuccess()), "Expected status should be true");
        assertEquals(1, response.getStatus_code(), "Expected status code should be 1");
        assertEquals("Success.", response.getStatus_message(), "Expected status should be 'Success.'");
    }

    @Test
    void getPopularMoviesTest() {
        ListDeserializer<FilmDeserializer> response = getTmdbService().getPopularMovies(getTmdbToken());
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getMovieByIdTest() {
        FilmDeserializer response = getTmdbService().getMovieById(getTmdbToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertEquals("The Gray Man", response.getTitle(), "Expected movie should be 'The Gray Man'");
    }

    @Test
    void getSimilarMovies() {
        ListDeserializer<FilmDeserializer> response = getTmdbService().getSimilarMovies(getTmdbToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getRecommendationsForMovieTest() {
        ListDeserializer<FilmDeserializer> response = getTmdbService().getRecommendationsForMovie(getTmdbToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getUpcomingTest() {
        ListDeserializer<FilmDeserializer> response = getTmdbService().getUpcoming(getTmdbToken());
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getTopRatedTest() {
        ListDeserializer<FilmDeserializer> response = getTmdbService().getTopRated(getTmdbToken());
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void getActorTest() {
        PersonDeserializer response = getTmdbService().getActor(getTmdbToken(),ACTOR_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBirthday(), "Result should not be null");
        assertEquals(response.getName(), "Ryan Gosling", "Result should be Ryan Gosling");
    }

    @Test
    void getActorsFilmsTest() {
        CreditsDeserializer response = getTmdbService().getActorsFilms(getTmdbToken(), ACTOR_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCrew(), "Results should not be null");
        assertNotNull(response.getCast(), "Results should not be null");
    }
    
    @Test
    void getVideosForFilmTest() {
        ListDeserializer<VideoDeserializer> response = getTmdbService().getVideosForFilm(getTmdbToken(), FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertNotEquals(0, response.getId(), "Id should not be null");
    }

    @Test
    void searchMovieTest() {
        ListDeserializer<FilmDeserializer> response = getTmdbService()
                .searchMovie(getTmdbToken(), "Oppenheimer", "en-US", 1);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void searchPersonTest() {
        ListDeserializer<PersonDeserializer> response = getTmdbService()
                .searchPerson(getTmdbToken(), "Ryan Gosling", "en-US", 1);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }

    @Test
    void findMovieTest() {
        MovieParameters params = new ParametersBuilder().withGenres("28").build();
        ListDeserializer<FilmDeserializer> response = getTmdbService().findMovie(params);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResults(), "Results should not be null");
        assertFalse(response.getResults().isEmpty(), "Results should not be empty");
    }
}