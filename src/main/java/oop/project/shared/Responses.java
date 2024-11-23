package oop.project.shared;

import oop.project.deserializers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static oop.project.shared.Config.*;

public class Responses {
    private static final int CONST_NUM = 12;

    public static String responseWithPerson(int id) {
        String responseMessage;
        CreditsDeserializer actorsFilms = tmdbService().getActorsMovies(apiToken(), id).sortByPopularity();
        PersonDeserializer actor = tmdbService().getActorById(apiToken(), id);

        StringBuilder actorsData = new StringBuilder("**" + actor.getName() + "**").append(" (").append(actor.getBirthday(), 0, 4);
        if (actor.getDeathday() != null) {
            actorsData.append(" - ").append(actor.getDeathday(), 0, 4);
        }

        actorsData.append(")").append("\n").append("Place of birth: ").append(actor.getPlace_of_birth().trim())
                .append("\n").append("Popularity: ").append(actor.getPopularity())
                .append("\n").append("ID: ").append(actor.getId()).append("\n\n");

        if (!Objects.equals(actor.getBiography().replace("\n", " "), "")) {
            actorsData.append(actor.getBiography()).append("\n\n");
        }

        if ((actorsFilms.getCast() != null || actorsFilms.getCrew() != null) && actor.getKnown_for_department() != null) {
            StringBuilder actorsFilmsBuilder = new StringBuilder("Фильмы с участием " + actor.getName() + ":\n");

            List<FilmDeserializer> movies;
            if (Objects.equals(actor.getKnown_for_department(), "Acting")) {
                movies = actorsFilms.getCast();
            } else {
                movies = actorsFilms.getCrew();
            }

            if (movies != null && !movies.isEmpty()) {
                int CONST_NUM = 12;
                int filmsToDisplay = Math.min(CONST_NUM, movies.size());
                for (int i = 0; i < filmsToDisplay; i++) {
                    FilmDeserializer currentMovie = movies.get(i);
                    actorsFilmsBuilder.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
                }
            } else {
                actorsFilmsBuilder.append("Фильмов не найдено.\n");
            }

            responseMessage = actorsData.append(actorsFilmsBuilder).toString();
        } else {
            responseMessage = "Данные не найдены";
        }
        return responseMessage;
    }

    public static String responseWithMovie(int id) {
        FilmDeserializer film = tmdbService().getMovieById(apiToken(), id);
        ListDeserializer<VideoDeserializer> videos = tmdbService().getVideosForMovie(apiToken(), id);

        StringBuilder filmBuilder = new StringBuilder(film.getTitle());
        if (!Objects.equals(film.getOriginal_language(), "en")) {
            filmBuilder.append(" / ").append(film.getOriginal_title());
        }
        filmBuilder.append(" (").append(film.getRelease_date(), 0, 4).append(", ")
                .append(film.getOrigin_country()[0]).append(")").append("\n\n")
                .append(film.getOverview().replace("\n", " ")).append("\n\n")
                .append("Vote average: ").append(film.getVote_average()).append("/10\n")
                .append("Runtime: ").append(film.getRuntime()).append(" min \n");
        if (film.getHomepage() != null && !film.getHomepage().isEmpty()) {
            filmBuilder.append("[Homepage](").append(film.getHomepage()).append(") ");
        }

        for (int i = 0; i < videos.getResults().size(); i++) {
            if (videos.getResults().get(i).getId() != null &&
                    Objects.equals(videos.getResults().get(i).getName(), "Official Trailer") &&
                    Objects.equals(videos.getResults().get(i).getSite(), "YouTube") &&
                    Objects.equals(videos.getResults().get(i).getType(), "Trailer") &&
                    videos.getResults().get(i).isOfficial()) {
                filmBuilder.append("/ [Trailer](").append(youtubeUrl()).append(videos.getResults().get(i).getKey()).append(")").append("\n");
            }
        }

        return filmBuilder.toString();
    }
    
    public static String responseWithListOfMovies(ListDeserializer<FilmDeserializer> result, String line) {
        return responseWithListOfMovies(result, line, 0, null, null);
    }

    public static <T> String responseWithListOfMovies(ListDeserializer<FilmDeserializer> result, String line, int index, HashMap<T, Integer> defaultMap, T data) {
        List<FilmDeserializer> movies = result.getResults();
        StringBuilder responseMessage = new StringBuilder(line);
        for (int i = 0; i < CONST_NUM; i++) {
            FilmDeserializer currentMovie = movies.get((index + i) % movies.size());
            responseMessage.append(i + 1).append(". ").append(currentMovie.getTitle()).append("\n");
        }
        index = (index + CONST_NUM) % movies.size();
        if (defaultMap != null && data != null) {
            defaultMap.put(data, index);            
        }
        return responseMessage.toString();
    }
}