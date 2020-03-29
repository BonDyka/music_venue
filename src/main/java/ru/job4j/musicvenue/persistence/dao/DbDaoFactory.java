package ru.job4j.musicvenue.persistence.dao;

import ru.job4j.musicvenue.persistence.ConnectionsPool;

public class DbDaoFactory extends DaoFactory {

    private static final ConnectionsPool POOL = ConnectionsPool.getInstance();

    @Override
    public AddressDao getAddressDao() {
        return new DbAddressDao(POOL);
    }

    @Override
    public MusicTypeDao getMusicTypeDao() {
        return new DbMusicTypeDao(POOL);
    }

    @Override
    public RoleDao getRoleDao() {
        return new DbRoleDao(POOL);
    }

    @Override
    public UserDao getUserDao() {
        return new DbUserDao(POOL);
    }
}
