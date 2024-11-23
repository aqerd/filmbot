package oop.project.shared;

import com.google.gson.GsonBuilder;

public class Utils {
    public static <Deserializer> void printPrettyJson(Deserializer des) {
        String prettyJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(des);
        System.out.println(prettyJson);
    }
}