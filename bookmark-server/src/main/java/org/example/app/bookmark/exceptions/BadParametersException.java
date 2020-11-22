package org.example.app.bookmark.exceptions;

/**
 * An exception that subclasses RuntimeException. To be used when something fails in execution.
 */
public class BadParametersException extends RuntimeException {

    /**
     * Class constructor.
     *
     * @param message Error description.
     */
    public BadParametersException(final String message) {
        super(message);
    }

    /**
     * Class constructor.
     *
     * @param message Error description.
     * @param cause   Throwable to be rethrown
     */
    public BadParametersException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
