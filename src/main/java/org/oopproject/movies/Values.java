package org.oopproject.movies;

import com.google.gson.annotations.SerializedName;

public class Values {
    public String title;
    public String overview;
    public String id;
    public double popularity;

    @SerializedName("vote_average")
    public double rating;
}
