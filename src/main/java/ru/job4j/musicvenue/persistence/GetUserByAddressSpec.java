package ru.job4j.musicvenue.persistence;

import ru.job4j.musicvenue.domains.Address;

public class GetUserByAddressSpec implements Specification {

    private final String specOfQuery;

    public GetUserByAddressSpec(Address address) {
        this.specOfQuery = String.format("WHERE a.country = %s AND a.city = %s;",
                address.getCountry(), address.getCity());
    }

    @Override
    public String toSql() {
        return this.specOfQuery;
    }
}