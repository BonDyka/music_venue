package ru.job4j.musicvenue.persistence;

import org.junit.Test;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.domains.Role;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.dao.DaoFactory;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class UserRepositoryTest {

    @Test
    public void whenSaveUserThanItShouldBeSaved() throws PersistException {
        List<MusicType> types = new ArrayList<>();
        MusicType type = new MusicType(1);
        type.setGenre("Blues");
        types.add(type);

        Address address = new Address();
        address.setCountry("Ukraine");
        address.setCity("Lviv");

        Role role = new Role(3);
        role.setTitle("USER");

        User user = new User();
        user.setLogin("login");
        user.setPassword("password");
        user.setAddress(address);
        user.setRole(role);
        user.setTypes(types);

        Repository<User> repository = new UserRepository();
        repository.save(user);

        assertThat(user.getId(), not(0));

        DaoFactory.getFactory().getAddressDao().delete(user.getAddress().getId());
    }

    @Test
    public void whenGetUserByLoginSpecThenShouldGotListWithSingleValue() throws PersistException {
        Repository<User> repository = new UserRepository();
        String expected = "admin";

        List<User> result = repository.getByCriteria(new GetUserByLoginSpec(expected));

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getLogin(), is(expected));
    }

    @Test(expected = PersistException.class)
    public void whenTryObtainNotExistingUserThenShouldGotException() throws PersistException {
        Repository<User> repository = new UserRepository();
        repository.getByCriteria(new GetUserByLoginSpec("asdf"));
    }

    @Test(expected = PersistException.class)
    public void whenSaveUserThatHaveExistingDataThenShouldGetException() throws PersistException {
        Address address = new Address();
        address.setCountry("country");
        address.setCity("city");
        Role role = new Role(3);
        MusicType type = new MusicType(2);
        List<MusicType> list = new ArrayList<>(1);
        list.add(type);
        User user = new User();
        user.setLogin("test");
        user.setPassword("password");
        user.setAddress(address);
        user.setRole(role);
        user.setTypes(list);
        Repository<User> repository = new UserRepository();

        repository.save(user);
        repository.save(user);

        DaoFactory.getFactory().getAddressDao().delete(address.getId());
    }
}