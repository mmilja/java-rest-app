package org.example.app.bookmark.usermanager;

/**
 * Enum describing possible user statuses.
 */
public enum UserStatus {

    /**
     * Signals the OK status.
     */
    OK(1),

    /**
     * Signals that the specified user cannot be found.
     */
    NOT_FOUND(2),

    /**
     * Signals the successful registration of the user.
     */
    REGISTERED(3),

    /**
     * Signals that the username already exists.
     */
    USERNAME_EXISTS(4),

    /**
     * Signals that the data provided to register the user is invalid.
     */
    INVALID_DATA(5),

    /**
     * Signals that the password for the user is invalid.
     */
    INVALID_PASSWORD(6);

    /**
     * Field containing the status for the current instance.
     */
    private final int userStatus;

    /**
     * Constructor that binds the signal to the field.
     *
     * @param userStatus to bind to the field.
     */
    UserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * Get the number that is bound tho the current signal.
     *
     * @return number representing the current signal.
     */
    public int getUserStatus() {
        return this.userStatus;
    }
}
