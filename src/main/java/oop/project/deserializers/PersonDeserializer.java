package oop.project.deserializers;

import java.util.List;

public class PersonDeserializer {
    public boolean adult;
    public String[] also_known_as;
    public String biography;
    public String birthday;
    public String deathday;
    public int gender;
    public String homepage;
    public int id;
    public String imdb_id;
    public String known_for_department;
    public String name;
    public String original_name;
    public String place_of_birth;
    public String popularity;
    public String profile_path;
    public List<FilmDeserializer> known_for;
}