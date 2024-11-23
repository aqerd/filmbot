package oop.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import oop.project.shared.Utils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    static class SampleObject {
        private String name;
        private int age;

        public SampleObject(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    void testMakePrettyJson() {
        SampleObject sampleObject = new SampleObject("John Doe", 30);
        String prettyJson = Utils.makePrettyJson(sampleObject);
        String expectedJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(sampleObject);
        assertEquals(expectedJson, prettyJson, "Pretty JSON output should match expected formatting");
    }

    @Test
    void testMakePrettyJsonWithNull() {
        String prettyJson = Utils.makePrettyJson(null);
        assertEquals("null", prettyJson, "Pretty JSON for null input should return 'null'");
    }
}