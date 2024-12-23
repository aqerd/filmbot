package oop.project.shared;

public enum Genres {
    ACTION(28),
    ADVENTURE(12),
    ANIMATION(16),
    COMEDY(35),
    CRIME(80),
    DOCUMENTARY(99),
    DRAMA(18),
    FAMILY(10751),
    FANTASY(14),
    HISTORY(36),
    HORROR(27),
    MUSIC(10402),
    MYSTERY(9648),
    ROMANCE(10749),
    SCIENCE_FICTION(878),
    TV_MOVIE(10770),
    THRILLER(53),
    WAR(10752),
    WESTERN(37);

    private final String GENRE_ID;

    Genres(int genre_id) {
        this.GENRE_ID = String.valueOf(genre_id);
    }

    public String getGenreId() {
        return GENRE_ID;
    }
}

