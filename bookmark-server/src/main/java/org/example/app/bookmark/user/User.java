package org.example.app.bookmark.user;

import com.ericsson.adp.bookmark_api.model.UserData;

/**
 * Class defining User entity.
 */
public class User {

    /**
     * Name of the user.
     */
    private String username;

    /**
     * Password of the user.
     */
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor that creates the user from the provided user data object.
     *
     * @param userData object containing user information.
     */
    public User(UserData userData) {
        this.username = userData.getName();
        this.password = userData.getPassword();
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
        this.password = password;
    }
}
