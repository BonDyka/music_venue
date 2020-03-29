package ru.job4j.musicvenue.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.domains.Role;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.dao.OperationNotCompleteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements Repository<User> {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    private static final String QUERY_TEMPLETE = String.format("%s %s %s %s %s",
        "SELECT u.id, login, password, address_id, role_id, country, city, title, mt.id AS mt_id, mt.genre FROM users AS u",
        "INNER JOIN addresses AS a ON u.address_id = a.id",
        "INNER JOIN roles AS r ON u.role_id = r.id",
        "INNER JOIN user_musics AS um ON u.id = um.user_id",
        "INNER JOIN music_types AS mt ON um.music_type_id = mt.id"
    );

    @Override
    public void save(User entity) throws PersistException {
        try (Connection conn = ConnectionsPool.getInstance().getConnection()) {
            try {
                conn.setAutoCommit(false);
                this.saveAddress(conn, entity.getAddress());
                this.saveUser(conn, entity);
                this.saveUserMusic(conn, entity);
            } catch (Exception e) {
                conn.rollback();
                LOG.error(e.getMessage(), e);
                throw new OperationNotCompleteException("Operation not completed", e);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't save an User.", e);
        }
    }

    @Override
    public List<User> getByCriteria(Specification spec) throws PersistException {
        List<User> result = new ArrayList<>();
        if (spec == null) {
            throw new PersistException("Specification can't be equals null.");
        }
        try (Connection conn = ConnectionsPool.getInstance().getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(String.format("%s %s", QUERY_TEMPLETE, spec.toSql()))
        ) {
            User lastCreated = null;
            while (rs.next()) {
                int userId = rs.getInt("id");
                if (lastCreated != null && userId == lastCreated.getId()) {
                    MusicType type = new MusicType(rs.getInt("mt_id"));
                    type.setGenre(rs.getString("genre"));
                    lastCreated.getTypes().add(type);
                } else {
                    lastCreated = this.createFromResultSet(rs);
                    if (lastCreated != null) {
                        result.add(lastCreated);
                    }
                }
            }
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Empty result of query.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't obtain result of query from DB.");
        }
        return result;
    }

    private User createFromResultSet(ResultSet rs) throws SQLException {

        Address address = new Address(rs.getInt("address_id"));
        address.setCountry(rs.getString("country"));
        address.setCity(rs.getString("city"));

        Role role = new Role(rs.getInt("role_id"));
        role.setTitle(rs.getString("title"));

        MusicType type = new MusicType(rs.getInt("mt_id"));
        type.setGenre(rs.getString("genre"));

        User result = new User(rs.getInt("id"));
        result.setLogin(rs.getString("login"));
        result.setPassword(rs.getString("password"));
        result.setAddress(address);
        result.setRole(role);
        List<MusicType> usersMusic = new ArrayList<>();
        usersMusic.add(type);
        result.setTypes(usersMusic);

        return result;
    }

    private void saveAddress(Connection conn, Address address) throws OperationNotCompleteException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO addresses (country, city) VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, address.getCountry());
            ps.setString(2, address.getCity());
            ps.executeUpdate();
            ResultSet resultSet = ps.getGeneratedKeys();
            resultSet.next();
            address.setId(resultSet.getInt("id"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new OperationNotCompleteException("Can't save address for the user", e);
        }
    }

    private void saveUser(Connection conn, User user) throws OperationNotCompleteException {
        try (
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (login, password, address_id, role_id) VALUES (?, ?, ?, ?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getAddress().getId());
            ps.setInt(4, user.getRole().getId());
            ps.executeUpdate();
            ResultSet resultSet = ps.getGeneratedKeys();
            resultSet.next();
            user.setId(resultSet.getInt("id"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new OperationNotCompleteException("Can't save users data.", e);
        }
    }

    private void saveUserMusic(Connection conn, User user) throws OperationNotCompleteException {
        List<MusicType> types = user.getTypes();
        if (types != null && !types.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement(String.format("%s %s",
                    "INSERT INTO user_musics (user_id, music_type_id)",
                    " VALUES(?, ?);"))
            ) {
                for (MusicType item : types) {
                    ps.setInt(1, user.getId());
                    ps.setInt(2, item.getId());
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new OperationNotCompleteException("Can't save list of users music", e);
            }
        }
    }
}
