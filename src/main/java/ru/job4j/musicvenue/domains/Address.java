package ru.job4j.musicvenue.domains;

public class Address extends BaseModel {

    private String country;

    private String city;

    public Address() {
    }

    public Address(int id) {
        super(id);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;

        if (!country.equals(address.country)) {
            return false;
        }
        return city.equals(address.city);
    }

    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}
