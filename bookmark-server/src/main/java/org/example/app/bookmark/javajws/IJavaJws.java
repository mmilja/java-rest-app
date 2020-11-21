package org.example.app.bookmark.javajws;

import java.util.UUID;

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
    JavaJwsToken createJws(String username);

    /**
     * Abolish existing user session.
     *
     * @param userName of the current session owner.
     * @param authString of the current session owner.
     * @return if session was abolished successfully.
     */
    boolean abolishJws(final String userName, final String authString);

    /**
     * Authorize the user from the provided token string.
     *
     * @param authString that holds the token information.
     * @param userUuid that identifies the user.
     * @return true if user is authenticated, false otehrwise.
     */
    boolean authorizeUser(final String authString, final UUID userUuid);
}
