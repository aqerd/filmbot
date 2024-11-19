package oop.project;

import oop.project.deserializers.*;
import org.junit.jupiter.api.Test;
import oop.project.parameters.MovieParameters;
import oop.project.parameters.ParametersBuilder;
import static org.junit.jupiter.api.Assertions.*;
import static oop.project.shared.Config.TMDB_TOKEN;
import static oop.project.shared.Config.tmdbService;

public class RequestsCheckTest {
    @Test
    void checkAuthStatusTest() {
        AuthDeserializer status = tmdbService.checkAuthStatus(TMDB_TOKEN);
        assertEquals("true", status.success, "Expected auth status should be true");
    }

    @Test
    void getPopularMoviesTest() {
        ListDeserializer<FilmDeserializer> films = tmdbService.getPopularMovies(TMDB_TOKEN);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getMovieByIdTest() {
        FilmDeserializer film = tmdbService.getMovieById(TMDB_TOKEN, 725201);
        assertEquals("The Gray Man", film.title, "Expected movie should be 'The Gray Man'");
    }

    @Test
    void getSimilarMovies() {
        ListDeserializer<FilmDeserializer> films = tmdbService.getSimilarMovies(TMDB_TOKEN, 725201);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getRecommendationsForMovieTest() {
        ListDeserializer<FilmDeserializer> films = tmdbService.getRecommendationsForMovie(TMDB_TOKEN, 725201);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getUpcomingTest() {
        ListDeserializer<FilmDeserializer> films = tmdbService.getUpcoming(TMDB_TOKEN);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getTopRatedTest() {
        ListDeserializer<FilmDeserializer> films = tmdbService.getTopRated(TMDB_TOKEN);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void getActorTest() {
        PersonDeserializer actor = tmdbService.getActor(TMDB_TOKEN,30614);
        assertNotNull(actor, "Results should not be null");
        assertNotNull(actor.birthday, "Result should not be null");
        assertEquals(actor.name, "Ryan Gosling", "Result should be Ryan Gosling");
    }

    @Test
    void getActorsFilmsTest() {
        CreditsDeserializer films = tmdbService.getActorsFilms(TMDB_TOKEN, 30614);
        assertNotNull(films.crew, "Results should not be null");
        assertNotNull(films.cast, "Results should not be null");
    }

    @Test
    void searchMovieTest() {
        ListDeserializer<FilmDeserializer> films = tmdbService
                .searchMovie(TMDB_TOKEN, "Oppenheimer", "en-US", 1);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void searchPersonTest() {
        ListDeserializer<PersonDeserializer> people = tmdbService
                .searchPerson(TMDB_TOKEN, "Ryan Gosling", "en-US", 1);
        assertNotNull(people.results, "Results should not be null");
        assertFalse(people.results.isEmpty(), "Results should not be empty");
    }

    @Test
    void findMovieTest() {
        MovieParameters params = new ParametersBuilder()
                .withGenres("28")
                .withCertificationLte("PG-13")
                .withCertificationCountry("US")
                .build();
        ListDeserializer<FilmDeserializer> films = tmdbService.findMovie(params);
        assertNotNull(films.results, "Results should not be null");
        assertFalse(films.results.isEmpty(), "Results should not be empty");
    }
}