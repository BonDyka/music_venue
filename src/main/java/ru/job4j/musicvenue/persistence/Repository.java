package ru.job4j.musicvenue.persistence;

import ru.job4j.musicvenue.domains.BaseModel;

import java.util.List;

public interface Repository<T extends BaseModel> {

    void save(T entity) throws PersistException;

    List<T> getByCriteria(Specification spec) throws PersistException;
}
