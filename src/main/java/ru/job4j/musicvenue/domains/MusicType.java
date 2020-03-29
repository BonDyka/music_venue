package ru.job4j.musicvenue.domains;

public class MusicType extends BaseModel {

    private String genre;

    public MusicType() {
    }

    public MusicType(int id) {
        super(id);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MusicType musicType = (MusicType) o;

        return genre.equals(musicType.genre);
    }

    @Override
    public int hashCode() {
        return genre.hashCode();
    }
}
