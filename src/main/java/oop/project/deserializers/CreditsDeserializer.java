package oop.project.deserializers;

import java.util.List;

public class CreditsDeserializer {
    private List<FilmDeserializer> cast;
    private List<FilmDeserializer> crew;
    private int id;

    public List<FilmDeserializer> getCast() {
        return cast;
    }

    public void setCast(List<FilmDeserializer> cast) {
        this.cast = cast;
    }

    public List<FilmDeserializer> getCrew() {
        return crew;
    }

    public void setCrew(List<FilmDeserializer> crew) {
        this.crew = crew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
