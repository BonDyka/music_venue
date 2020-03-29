package ru.job4j.musicvenue.persistence.dao;

import org.junit.Test;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.persistence.PersistException;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class AddressDaoTest {

    private static DaoFactory factory = DaoFactory.getFactory();

    @Test
    public void whenGetAllThenShouldGetIt() throws PersistException {
        AddressDao dao = factory.getAddressDao();

        List<Address> result = dao.getAll();

        assertThat(result.size(), not(0));
    }

    @Test
    public void whenGetExistingEntityByIdThenShouldGetIt() throws PersistException {
        AddressDao dao = factory.getAddressDao();

        Address result = dao.getById(1);

        assertThat(result.getId(), is(1));
        assertThat(result.getCity(), is("Moscow"));
    }

    @Test
    public void whenTrySaveNewEntityThenItMustBeSaved() throws PersistException {
        Address address = new Address();
        address.setCountry("Ukraine");
        address.setCity("Kiev");
        AddressDao dao = factory.getAddressDao();

        dao.save(address);

        List<Address> queryResult = dao.getAll();
        Address result = queryResult.get(queryResult.size() - 1);

        assertThat(result, is(address));

        dao.delete(result.getId());
    }

    @Test(expected = PersistException.class)
    public void whenTrySaveExistedEntityThenShouldGetException() throws PersistException {
        Address address = new Address();
        address.setCountry("Ukraine");
        address.setCity("Kharkov");
        AddressDao dao = factory.getAddressDao();

        dao.save(address);
        dao.save(address);
    }

    @Test
    public void whenUpdateEntityThenItShouldBeUpdated() throws PersistException {
        Address expected = new Address();
        expected.setCountry("USA");
        expected.setCity("New York");
        AddressDao dao = factory.getAddressDao();

        dao.save(expected);
        List<Address> list = dao.getAll();
        expected = list.get(list.indexOf(expected));
        expected.setCity("Los Angeles");

        dao.update(expected);

        Address result = dao.getById(expected.getId());

        assertThat(result, is(expected));

        dao.delete(expected.getId());
    }
}