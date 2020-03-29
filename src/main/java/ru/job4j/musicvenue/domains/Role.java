package ru.job4j.musicvenue.domains;

public class Role extends BaseModel {

    private String title;

    public Role() {
    }

    public Role(int id) {
        super(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Role role = (Role) o;

        return title.equals(role.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
