package org.example.app.bookmark.exceptions;

/**
 * An exception that subclasses RuntimeException. To be used when methods which initialize/setup
 * something fail.
 */
public class InitFailedException extends RuntimeException {

    /**
     * Class constructor.
     *
     * @param message Error description.
     */
    public InitFailedException(final String message) {
        super(message);
    }

    /**
     * Class constructor.
     *
     * @param message Error description.
     * @param cause   Throwable to be rethrown
     */
    public InitFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
