package org.example.app.bookmark.javajws;

public enum JwsStatus {

    /**
     * Signals the creation of the jws.
     */
    CREATED(1),

    /**
     * Signals an unspecified error.
     */
    FAILED(2),

    /**
     * Signals that the session already exists.
     */
    ALREADY_EXISTS(3);

    /**
     * Field containing the status for the current instance.
     */
    private final int jwsStatus;

    /**
     * Constructor that binds the signal to the field.
     *
     * @param jwsStatus to bind to the field.
     */
    JwsStatus(int jwsStatus) {
        this.jwsStatus = jwsStatus;
    }

    /**
     * Get the number that is bound tho the current signal.
     *
     * @return number representing the current signal.
     */
    public int getJwsStatus() {
        return this.jwsStatus;
    }
}
