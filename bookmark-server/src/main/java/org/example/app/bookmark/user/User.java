package org.example.app.bookmark.user;

import com.ericsson.adp.bookmark_api.model.UserData;
import org.example.app.bookmark.exceptions.BadParametersException;

/**
 * Class defining User entity.
 */
public class User {

    public static final int MAXIMUM_PASSWORD_LENGTH = 1024;

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
            throw new BadParametersException("Password too long");
        }
        this.password = password;
    }
}
