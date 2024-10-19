package org.oopproject.enums;

public enum Genres {
    ACTION(28),
    DRAMA(28),
    COMEDY(28),
    THRILLER(28);

    public final String genreId;

    private Genres(int genreId) {
        this.genreId = String.valueOf(genreId);
    }
}
