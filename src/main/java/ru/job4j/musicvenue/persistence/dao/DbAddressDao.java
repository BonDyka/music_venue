package ru.job4j.musicvenue.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.persistence.ConnectionsPool;
import ru.job4j.musicvenue.persistence.PersistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbAddressDao implements AddressDao {
    private static final Logger LOG = LoggerFactory.getLogger(DbAddressDao.class);

    private ConnectionsPool pool;

    public DbAddressDao(ConnectionsPool pool) {
        this.pool = pool;
    }

    @Override
    public List<Address> getAll() throws PersistException {
        List<Address> result = new ArrayList<>();
        try (Connection conn = this.pool.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, country, city FROM addresses;")
        ) {
            while (rs.next()) {
                Address found = new Address(rs.getInt("id"));
                found.setCountry(rs.getString("country"));
                found.setCity(rs.getString("city"));
                result.add(found);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't create the query result", e);
        }
        return result;
    }

    @Override
    public Address getById(int id) throws PersistException {
        Address result = new Address();
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, country, city FROM addresses WHERE id = ?;")
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setCountry(rs.getString("country"));
                result.setCity(rs.getString("city"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't get entity from DB", e);
        }
        return result;
    }

    @Override
    public void save(Address entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO addresses (country, city) VALUES (?, ?);")
        ) {
            ps.setString(1, entity.getCountry());
            ps.setString(2, entity.getCity());
            int rowUpdated = ps.executeUpdate();
            if (rowUpdated == 0) {
                throw new OperationNotCompleteException("Can't execute updating of the entity");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't save entity to DB.", e);
        }
    }

    @Override
    public void update(Address entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE addresses SET country = ?, city = ? WHERE id = ?;")
        ) {
            ps.setString(1, entity.getCountry());
            ps.setString(2, entity.getCity());
            ps.setInt(3, entity.getId());
            if (ps.executeUpdate() == 0) {
                throw new OperationNotCompleteException("Can't complete operation.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't update the entity.", e);
        }
    }

    @Override
    public void delete(int id) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM addresses WHERE id = ?;")
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException(String.format("Can't delete entity by id = %s", id), e);
        }
    }
}
