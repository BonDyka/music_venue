package ru.job4j.musicvenue.persistence;

public class GetAllUsersSpec implements Specification {
    private final String specOfQuery = "ORDER BY id;";

    public GetAllUsersSpec() {

    }

    @Override
    public String toSql() {
        return this.specOfQuery;
    }
}
