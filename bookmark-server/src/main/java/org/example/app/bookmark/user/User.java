package org.example.app.bookmark.user;

import org.example.app.bookmark_api.model.UserData;
import org.example.app.bookmark.exceptions.BadParametersException;

/**
 * Class defining User entity.
 */
public class User {

    /**
     * Maximum length of the password.
     */
    public static final int MAXIMUM_PASSWORD_LENGTH = 1024;

    /**
     * Message indicating that user password is too long.
     */
    public static final String PASSWORD_TOO_LONG = "Password too long";

    /**
     * Message indicating bad user data was provided.
     */
    public static final String BAD_DATA = "Bad user data provided";

    /**
     * Name of the user.
     */
    private String username;

    /**
     * Password of the user.
     */
    private String password;

    /**
     * Constructor that creates the user from the provided user data object.
     *
     * @param userData object containing user information.
     */
    public User(UserData userData) {
        if (userData.getName() == null || userData.getPassword() == null) {
            throw new BadParametersException(BAD_DATA);
        }
        this.setUsername(userData.getName());
        this.setPassword(userData.getPassword());
    }

    /**
     * Get username.
     *
     * @return name of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username.
     *
     * @param username to give to the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password.
     *
     * @return password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password.
     *
     * @param password to set for the user.
     */
    public void setPassword(String password) {
        if (password.length() > MAXIMUM_PASSWORD_LENGTH) {
            throw new BadParametersException(PASSWORD_TOO_LONG);
        }
        this.password = password;
    }
}
