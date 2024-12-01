package oop.project.shared;

import com.google.gson.GsonBuilder;

public class Utils {
    public static <Deserializer> String makePrettyJson(Deserializer des) {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(des);
    }
}