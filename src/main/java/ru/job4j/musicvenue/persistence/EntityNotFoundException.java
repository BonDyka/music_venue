package ru.job4j.musicvenue.persistence;

/**
 * @author Alexandr Bondarev(mailto:bondarew2507@gmail.com).
 * @since 24-Jul-18.
 */
public class EntityNotFoundException extends PersistException {
    public EntityNotFoundException(String msg, Exception cause) {
        super(msg, cause);
    }

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
