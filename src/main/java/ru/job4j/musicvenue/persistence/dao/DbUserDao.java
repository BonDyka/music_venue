package ru.job4j.musicvenue.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.domains.Role;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.ConnectionsPool;
import ru.job4j.musicvenue.persistence.EntityNotFoundException;
import ru.job4j.musicvenue.persistence.PersistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbUserDao implements UserDao {
    private static final Logger LOG = LoggerFactory.getLogger(DbUserDao.class);

    private ConnectionsPool pool;

    public DbUserDao(ConnectionsPool pool) {
        this.pool = pool;
    }

    @Override
    public List<User> getAll() throws PersistException {
        List<User> result = new ArrayList<>();
        try (Connection conn = this.pool.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(String.format("%s %s %s",
                "SELECT u.id, login, password, address_id, role_id, country, city, title FROM users AS u",
                "INNER JOIN addresses AS a ON u.address_id = a.id",
                "INNER JOIN role AS r ON u.role_id = r.id;"
             ))
        ) {
            while (rs.next()) {
                User entity = this.createUserFromResultSet(rs);
                if (entity == null) {
                    throw new EntityNotFoundException("Can't find entity.");
                }
                entity.setTypes(this.getUsersMusic(entity.getId()));
                result.add(entity);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new PersistException("Can't obtain query result from DB.", e);
        }
        return result;
    }

    @Override
    public User getById(int id) throws PersistException {
        User result = null;
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(String.format("%s %s %s",
                     "SELECT u.id, login, password, address_id, role_id, country, city, title FROM users AS u",
                     "INNER JOIN addresses AS a ON u.address_id = a.id",
                     "INNER JOIN roles AS r ON u.role_id = r.id WHERE u.id = ?;"
             ))
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = this.createUserFromResultSet(rs);
                result.setTypes(this.getUsersMusic(result.getId()));
            }
            if (result == null) {
                throw new EntityNotFoundException("Can't find entity.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't obtain query result from DB.", e);
        }
        return result;
    }

    @Override
    public void save(User entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (login, password, address_id, role_id) VALUES (?, ?, ?, ?);"
             )
        ) {
            ps.setString(1, entity.getLogin());
            ps.setString(2, entity.getPassword());
            ps.setInt(3, entity.getAddress().getId());
            ps.setInt(4, entity.getRole().getId());
            ps.executeUpdate();
            try {
                this.saveUsersMusic(entity);
            } catch (Exception e1) {
                conn.rollback();
                LOG.error(e1.getMessage(), e1);
                throw new OperationNotCompleteException("Can't complete operation.", e1);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("The entity not saved.", e);
        }
    }

    @Override
    public void update(User entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET login = ?, password = ?, address_id = ?, role_id = ? WHERE id = ?;"
             )
        ) {
            ps.setString(1, entity.getLogin());
            ps.setString(2, entity.getPassword());
            ps.setInt(3, entity.getAddress().getId());
            ps.setInt(4, entity.getRole().getId());
            ps.setInt(5, entity.getId());
            ps.executeUpdate();
            try {
                this.saveUsersMusic(entity);
            } catch (Exception e1) {
                conn.rollback();
                LOG.error(e1.getMessage(), e1);
                throw new OperationNotCompleteException("Can't complete operation.", e1);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("The entity not updated.", e);
        }
    }

    @Override
    public void delete(int id) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?;")
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException(String.format("Can't delete entity by id = %s", id), e);
        }
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User result = new User(rs.getInt("id"));
        result.setLogin(rs.getString("login"));
        result.setPassword(rs.getString("password"));

        Address address = new Address(rs.getInt("address_id"));
        address.setCountry(rs.getString("country"));
        address.setCity(rs.getString("city"));
        result.setAddress(address);

        Role role = new Role(rs.getInt("role_id"));
        role.setTitle(rs.getString("title"));
        result.setRole(role);

        return result;
    }

    private List<MusicType> getUsersMusic(int id) throws PersistException {
        List<MusicType> result = new ArrayList<>();
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(String.format("%s %s",
                     "SELECT mt.id, genre FROM user_musics AS um INNER JOIN music_types AS mt",
                             "ON um.music_type_id = mt.id WHERE um.user_id = ?;"
             ))
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int mtId = rs.getInt("id");
                MusicType entity = new MusicType(mtId);
                entity.setGenre(rs.getString("genre"));
                result.add(entity);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't obtain list of users music type.", e);
        }
        return result;
    }

    private void saveUsersMusic(User entity) throws OperationNotCompleteException {
        Connection conn = null;
        try {
            conn = this.pool.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM user_musics WHERE user_id = ?;");
            deleteStatement.setInt(1, entity.getId());
            deleteStatement.executeUpdate();
            PreparedStatement saveStatement = conn.prepareStatement(
                    "INSERT INTO user_musics (user_id, music_type_id) VALUES (?, ?)"
            );
            for (MusicType musicType : entity.getTypes()) {
                saveStatement.setInt(1, entity.getId());
                saveStatement.setInt(2, musicType.getId());
                saveStatement.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            }
            LOG.error(e.getMessage(), e);
            throw new OperationNotCompleteException("Can't save entities of users music.", e);
        }
    }
}
