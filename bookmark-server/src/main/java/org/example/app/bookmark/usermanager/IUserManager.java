package org.example.app.bookmark.usermanager;

import com.ericsson.adp.bookmark_api.model.UserData;

import java.util.Map;

/**
 * Interface that describes the user manager.
 */
public interface IUserManager {

    /**
     *  Register the user into the application.
     *
     * @param userData object conaining the user information.
     * @return status code signaling the success or failure of the operation.
     */
    UserStatus registerUser(final UserData userData);

    /**
     * Log-in the user into application. If successful String is a jws token, else it's an empty string.
     *
     * @param userData which identifies the user.
     * @return status code signaling the success or failure of the operation and jws token if successful.
     */
    Map.Entry<UserStatus, String> loginUser(final UserData userData);

    /**
     * Log-out user from the application.
     *
     * @param userName of the user to log out.
     * @param authString containing the authentication token.
     * @return status code signaling the success or failure of the operation.
     */
    UserStatus logoutUser(final String userName, String authString);
}
