package org.oopproject.enums;

public enum Genres {
    ACTION(28),
    DRAMA(18),
    COMEDY(35),
    THRILLER(53);

    public final String genreId;

    private Genres(int genreId) {
        this.genreId = String.valueOf(genreId);
    }
}
