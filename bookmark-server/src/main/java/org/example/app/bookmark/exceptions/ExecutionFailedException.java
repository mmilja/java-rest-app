package org.example.app.bookmark.exceptions;

/**
 * An exception that subclasses RuntimeException. To be used when something fails in execution.
 */
public class ExecutionFailedException extends RuntimeException {

    /**
     * Class constructor.
     *
     * @param message Error description.
     */
    public ExecutionFailedException(final String message) {
        super(message);
    }

    /**
     * Class constructor.
     *
     * @param message Error description.
     * @param cause   Throwable to be rethrown
     */
    public ExecutionFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
