package ru.job4j.musicvenue.persistence.dao;

import ru.job4j.musicvenue.persistence.PersistException;

public class OperationNotCompleteException extends PersistException {
    public OperationNotCompleteException(String msg, Exception cause) {
        super(msg, cause);
    }

    public OperationNotCompleteException(String msg) {
        super(msg);
    }
}
