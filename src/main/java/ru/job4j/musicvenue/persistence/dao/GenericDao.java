package ru.job4j.musicvenue.persistence.dao;

import ru.job4j.musicvenue.domains.BaseModel;
import ru.job4j.musicvenue.persistence.PersistException;

import java.util.List;

public interface GenericDao<T extends BaseModel> {

    List<T> getAll() throws PersistException;

    T getById(int id) throws PersistException;

    void save(T entity) throws PersistException;

    void update(T entity) throws PersistException;

    void delete(int id) throws PersistException;
}
