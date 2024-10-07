package org.oopproject.movies;

import com.google.gson.annotations.SerializedName;

public class Values {
    public String title;
    public String overview;
    public String id;

    @SerializedName("vote_average")
    public double rating;
}
