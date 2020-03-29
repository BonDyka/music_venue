package ru.job4j.musicvenue.persistence;

public class PersistException extends Exception {
    public PersistException(String msg, Exception cause) {
        super(msg, cause);
    }

    public PersistException(String msg) {
        super(msg);
    }
}
