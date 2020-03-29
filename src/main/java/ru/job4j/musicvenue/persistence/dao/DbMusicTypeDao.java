package ru.job4j.musicvenue.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.persistence.ConnectionsPool;
import ru.job4j.musicvenue.persistence.PersistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbMusicTypeDao implements MusicTypeDao {
    private static final Logger LOG = LoggerFactory.getLogger(DbMusicTypeDao.class);

    private ConnectionsPool pool;

    public DbMusicTypeDao(ConnectionsPool pool) {
        this.pool = pool;
    }

    @Override
    public List<MusicType> getAll() throws PersistException {
        List<MusicType> result = new ArrayList<>();
        try (Connection conn = this.pool.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, genre FROM music_types;")
        ) {
            while (rs.next()) {
                MusicType entity = new MusicType(rs.getInt("id"));
                entity.setGenre(rs.getString("genre"));
                result.add(entity);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException("Can't obtain query result from DB.", e);
        }
        return result;
    }

    @Override
    public MusicType getById(int id) throws PersistException {
        MusicType result = new MusicType();
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, genre FROM music_types WHERE id = ?;")
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setGenre(rs.getString("genre"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException(String.format("Can't get entity by id = %s.", id), e);
        }
        return result;
    }

    @Override
    public void save(MusicType entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO music_types (genre) VALUES (?);")
        ) {
            ps.setString(1, entity.getGenre());
            if (ps.executeUpdate() == 0) {
                throw new OperationNotCompleteException("Can't complete the operation");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new PersistException("Can't save entity to DB", e);
        }
    }

    @Override
    public void update(MusicType entity) throws PersistException {
        try (Connection conn = this.pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE music_types SET genre = ? WHERE id = ?;")
        ) {
            ps.setString(1, entity.getGenre());
            ps.setInt(2, entity.getId());
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
             PreparedStatement ps = conn.prepareStatement("DELETE FROM music_types WHERE id = ?;")
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PersistException(String.format("Can't delete entity by id = %s", id), e);
        }
    }
}
