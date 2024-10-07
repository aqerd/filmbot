package org.oopproject.movies;

import com.google.gson.annotations.SerializedName;

public class Values {
    public String title;
    public String overview;
    public int id;
    public String original_language;
    public double popularity;

    @SerializedName("vote_average")
    public double rating;
}
