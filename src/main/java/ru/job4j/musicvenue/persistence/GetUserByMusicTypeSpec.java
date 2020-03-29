package ru.job4j.musicvenue.persistence;

import ru.job4j.musicvenue.domains.MusicType;

public class GetUserByMusicTypeSpec implements Specification {

    private final String specOfQuery;

    public GetUserByMusicTypeSpec(MusicType entity) {
        this.specOfQuery = String.format("%s %s %s %s %s %s;", "WHERE u.id IN (SELECT user_id FROM user_musics AS music",
                "INNER JOIN music_types AS type ON music.music_type_id = type.id WHERE music.music_type_id =",
                entity.getId(), "AND type.name =", entity.getGenre(), ") ORDER BY u.id");
    }

    @Override
    public String toSql() {
        return this.specOfQuery;
    }
}
