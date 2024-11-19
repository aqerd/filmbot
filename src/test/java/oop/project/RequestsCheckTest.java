package oop.project;

import oop.project.deserializers.*;
import org.junit.jupiter.api.Test;
import oop.project.parameters.MovieParameters;
import oop.project.parameters.ParametersBuilder;
import static org.junit.jupiter.api.Assertions.*;
import static oop.project.shared.Config.TMDB_TOKEN;
import static oop.project.shared.Config.TMDB_SERVICE;

public class RequestsCheckTest {
    private final int FILM_ID = 725201; // The Gray Man
    private final int ACTOR_ID = 30614; // Ryan Gosling

    @Test
    void checkAuthStatusTest() {
        AuthDeserializer response = TMDB_SERVICE.checkAuthStatus(TMDB_TOKEN);
        assertNotNull(response, "Response should not be null");
        assertTrue(Boolean.parseBoolean(response.success), "Expected status should be true");
        assertEquals(1, response.status_code, "Expected status code should be 1");
        assertEquals("Success.", response.status_message, "Expected status should be 'Success.'");
    }

    @Test
    void getPopularMoviesTest() {
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE.getPopularMovies(TMDB_TOKEN);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getMovieByIdTest() {
        FilmDeserializer response = TMDB_SERVICE.getMovieById(TMDB_TOKEN, FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertEquals("The Gray Man", response.title, "Expected movie should be 'The Gray Man'");
    }

    @Test
    void getSimilarMovies() {
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE.getSimilarMovies(TMDB_TOKEN, FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getRecommendationsForMovieTest() {
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE.getRecommendationsForMovie(TMDB_TOKEN, FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getUpcomingTest() {
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE.getUpcoming(TMDB_TOKEN);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getTopRatedTest() {
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE.getTopRated(TMDB_TOKEN);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getActorTest() {
        PersonDeserializer response = TMDB_SERVICE.getActor(TMDB_TOKEN,ACTOR_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.birthday, "Result should not be null");
        assertEquals(response.name, "Ryan Gosling", "Result should be Ryan Gosling");
    }

    @Test
    void getActorsFilmsTest() {
        CreditsDeserializer response = TMDB_SERVICE.getActorsFilms(TMDB_TOKEN, ACTOR_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.crew, "Results should not be null");
        assertNotNull(response.cast, "Results should not be null");
    }
    
    @Test
    void getVideosForFilmTest() {
        ListDeserializer<VideoDeserializer> response = TMDB_SERVICE.getVideosForFilm(TMDB_TOKEN, FILM_ID);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertNotEquals(0, response.id, "Id should not be null");
    }

    @Test
    void searchMovieTest() {
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE
                .searchMovie(TMDB_TOKEN, "Oppenheimer", "en-US", 1);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void searchPersonTest() {
        ListDeserializer<PersonDeserializer> response = TMDB_SERVICE
                .searchPerson(TMDB_TOKEN, "Ryan Gosling", "en-US", 1);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void findMovieTest() {
        MovieParameters params = new ParametersBuilder().withGenres("28").build();
        ListDeserializer<FilmDeserializer> response = TMDB_SERVICE.findMovie(params);
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.results, "Results should not be null");
        assertFalse(response.results.isEmpty(), "Results should not be empty");
    }
}