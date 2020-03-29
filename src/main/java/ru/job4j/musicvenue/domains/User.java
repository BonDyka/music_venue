package ru.job4j.musicvenue.domains;

import java.util.List;

public class User extends BaseModel {

    private String login;
    private String password;

    private Address address;
    private Role role;

    private List<MusicType> types;

    public User() {
    }

    public User(int id) {
        super(id);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<MusicType> getTypes() {
        return types;
    }

    public void setTypes(List<MusicType> types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!login.equals(user.login)) {
            return false;
        }
        if (!password.equals(user.password)) {
            return false;
        }
        if (!address.equals(user.address)) {
            return false;
        }
        if (!role.equals(user.role)) {
            return false;
        }
        return types != null ? types.equals(user.types) : user.types == null;
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }
}
