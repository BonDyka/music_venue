package ru.job4j.musicvenue.persistence;

import ru.job4j.musicvenue.domains.Role;

public class GetUserByRoleSpec implements Specification {

    private final String specOfQuery;

    public GetUserByRoleSpec(Role role) {
        this.specOfQuery = String.format("WHERE r.id = %s AND r.title = %s ORDER BY u.id;",
                role.getId(), role.getTitle());
    }

    @Override
    public String toSql() {
        return this.specOfQuery;
    }
}
