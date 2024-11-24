package org.oopproject.deserializers;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieVideosResponse {
    @SerializedName("results")
    public List<VideoDeserializer> results;  // Список видео

    // Конструкторы и методы, если нужно
}
