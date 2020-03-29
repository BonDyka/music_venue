package ru.job4j.musicvenue.persistence;


/**
 * @author Alexandr Bondarev(mailto:bondarew2507@gmail.com).
 * @since 07-Aug-18.
 */
public class GetUserByLoginSpec implements Specification {


    private final String specOfQuery;

    public GetUserByLoginSpec(String login) {
        this.specOfQuery = String.format("WHERE u.login = '%s';", login);
    }

    @Override
    public String toSql() {
        return this.specOfQuery;
    }
}
