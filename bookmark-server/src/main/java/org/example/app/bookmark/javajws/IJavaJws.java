package org.example.app.bookmark.javajws;

import java.util.Map;

/**
 * Interface describing JWT functions.
 */
public interface IJavaJws {

    /**
     * Create jws for a user with given username.
     *
     * @param username for which to create jws.
     * @return object containing the status of creation and the jws.
     */
    Map.Entry<JwsStatus, String> createJws(String username);

    /**
     * Abolish existing user session.
     *
     * @param userName of the current session owner.
     * @param authString of the current session owner.
     * @return status code signaling the success or failure of the operation.
     */
    JwsStatus abolishJws(final String userName, final String authString);

    /**
     * Authorize the user from the provided token string.
     *
     * @param authString that holds the token information.
     * @return username in jws subject, or empty string is jws is not recognized.
     */
    String authorizeUser(final String authString);

    /**
     * Check if the user session already exists.
     *
     * @param username to check.
     * @return true if the user session exists, false otherwise.
     */
    boolean checkUserLoggedIn(final String username);
}
