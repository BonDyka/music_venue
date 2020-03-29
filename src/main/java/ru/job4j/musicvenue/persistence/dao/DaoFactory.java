package ru.job4j.musicvenue.persistence.dao;

public abstract class DaoFactory {

    public abstract AddressDao getAddressDao();

    public abstract MusicTypeDao getMusicTypeDao();

    public abstract RoleDao getRoleDao();

    public abstract UserDao getUserDao();

    public static DaoFactory getFactory() {
        return new DbDaoFactory();
    }
}
