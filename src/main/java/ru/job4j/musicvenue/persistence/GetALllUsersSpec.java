package ru.job4j.musicvenue.persistence;

public class GetALllUsersSpec implements Specification {
    private final String specOfQuery = "ORDER BY id;";

    public GetALllUsersSpec() {

    }

    @Override
    public String toSql() {
        return this.specOfQuery;
    }
}
