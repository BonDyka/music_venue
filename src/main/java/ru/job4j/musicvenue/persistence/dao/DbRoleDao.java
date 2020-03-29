package ru.job4j.musicvenue.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Role;
import ru.job4j.musicvenue.persistence.ConnectionsPool;
import ru.job4j.musicvenue.persistence.PersistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbRoleDao implements RoleDao {
    private static final Logger LOG = LoggerFactory.getLogger(DbRoleDao.class);

    private ConnectionsPool pool;

    public DbRoleDao(ConnectionsPool pool) {
        this.pool = pool;
    }

    @Override
    public List<Role> getAll() throws PersistException {
        List<Role> result = new ArrayList<>();
        try (Connection conn = this.pool.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, title FROM roles;")
        ) {
            while (rs.next()) {
                Role entity = new Role(rs.getInt("id"));
                entity.setTitle(rs.getString("title"));
                result.add(entity);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
                    throw new PersistException("Can't obtain query result from DB.", e);
        }
        return result;
    }

    @Override
    public Role getById(int id) throws PersistException {
        Role result = new Role();
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, title FROM roles WHERE id = ?;");
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setTitle(rs.getString("title"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't obtain query result from DB.", e);
        }
        return result;
    }

    @Override
    public void save(Role entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO roles (title) VALUES (?);")
        ) {
            ps.setString(1, entity.getTitle());
            if (ps.executeUpdate() == 0) {
                throw new OperationNotCompleteException("Can't complete the operation");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new PersistException("Can't save entity to DB", e);
        }
    }

    @Override
    public void update(Role entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE roles SET title = ? WHERE id = ?;")
        ) {
            ps.setString(1, entity.getTitle());
            if (ps.executeUpdate() == 0) {
                throw new OperationNotCompleteException("Can't execute updating of the entity");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't update the entity.", e);
        }
    }

    @Override
    public void delete(int id) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM roles WHERE id = ?;")
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException(String.format("Can't delete entity by id = %s", id), e);
        }
    }
}
