package org.oopproject.deserializers;

import java.util.List;

public class PersonDeserializer {
    public boolean adult;
    public int gender;
    public int id;
    public String known_for_department;
    public String name;
    public String original_name;
    public String popularity;
    public String profile_path;
    public List<FilmDeserializer> known_for;
}
