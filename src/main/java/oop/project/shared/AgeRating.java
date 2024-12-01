package oop.project.shared;

public enum AgeRating {
    GENERAL("G", 0, 5),
    PARENTAL_GUIDANCE("PG", 6, 11),
    WITH_PARENTS_STRONGLY("PG-13", 12, 15),
    RESTRICTED("R", 16, 17),
    ADULTS("NC-17", 18, 120);

    private final String rating;
    private final int minAge;
    private final int maxAge;

    AgeRating(String rating, int minAge, int maxAge) {
        this.rating = rating;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public static String getRatingForAge(int age) {
        for (AgeRating rating : values()) {
            if (age >= rating.minAge && age <= rating.maxAge) {
                return rating.rating;
            }
        }
        return null;
    }

    public String getRating() {
        return rating;
    }
}