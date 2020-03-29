package ru.job4j.musicvenue.persistence;

import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.*;

public class ConnectionsPoolTest {

    @Test
    public void whenAskConnectionThenShouldGetIt() throws PersistException {
        ConnectionsPool pool = ConnectionsPool.getInstance();

        Connection connection = pool.getConnection();

        assertNotNull(connection);
    }
}