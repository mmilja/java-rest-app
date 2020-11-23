package org.example.app.bookmark.usermanager;

import org.example.app.bookmark.exceptions.BadParametersException;
import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark.javajws.JwsStatus;
import org.example.app.bookmark.user.User;
import org.example.app.bookmark_api.model.UserData;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class that manages users.
 */
public class UserManager implements IUserManager {

    /**
     * Singleton instance.
     */
    private static volatile UserManager instance;

    /**
     * Object used for jws token creation.
     */
    private final IJavaJws javaJws;

    /**
     * List of existing users.
     */
    private final List<User> userList;

    /**
     * Private constructor.
     *
     * @param javaJws object used for jws token management.
     */
    private UserManager(final IJavaJws javaJws) {
        this.javaJws = javaJws;

        this.userList = new ArrayList<>();
    }

    /**
     * Convenience getter.
     *
     * @return IUserManager instance.
     */
    public static IUserManager getInstance() {
        return instance;
    }

    /**
     * Getter for the singleton.
     *
     * @param javaJws object used for jws token management.
     * @return IUserManager instance.
     */
    public static IUserManager getInstance(final IJavaJws javaJws) {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager(javaJws);
                }
            }
        }
        return instance;
    }

    @Override
    public UserStatus registerUser(final UserData userData) {
        if (userData.getName() == null || userData.getName().isEmpty()
                || userData.getPassword() == null || userData.getPassword().isEmpty()) {
            return UserStatus.INVALID_DATA;
        }

        if (userList.stream().anyMatch(user -> user.getUsername().equals(userData.getName()))) {
            return UserStatus.USERNAME_EXISTS;
        }

        User user;
        try {
            user = new User(userData);
        } catch (BadParametersException e) {
            if (e.getMessage().equals(User.PASSWORD_TOO_LONG)) {
                return UserStatus.PASSWORD_TOO_LONG;
            } else {
                return UserStatus.INVALID_DATA;
            }
        }

        this.userList.add(user);

        return UserStatus.REGISTERED;
    }

    @Override
    public Map.Entry<UserStatus, String> loginUser(final UserData userData) {
        String jws = "";
        if (userData.getName() == null || userData.getName().isEmpty()
                || userData.getPassword() == null || userData.getPassword().isEmpty()) {
            return new AbstractMap.SimpleEntry<>(UserStatus.INVALID_DATA, jws);
        }

        User user;
        Optional<User> optionalUser =
                this.userList.stream().filter(tmpUser -> tmpUser.getUsername().equals(userData.getName())).findFirst();
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return new AbstractMap.SimpleEntry<>(UserStatus.NOT_FOUND, jws);
        }

        if (checkPassword(userData.getPassword(), user.getPassword())) {
            if (!this.javaJws.checkUserLoggedIn(user.getUsername())) {
                Map.Entry<JwsStatus, String> javaJwsToken = this.javaJws.createJws(userData.getName());
                if (javaJwsToken.getKey().equals(JwsStatus.CREATED)) {
                    jws = javaJwsToken.getValue();
                    return new AbstractMap.SimpleEntry<>(UserStatus.OK, jws);
                } else {
                    return new AbstractMap.SimpleEntry<>(UserStatus.LOGIN_ISSUE, jws);
                }
            } else {
                return new AbstractMap.SimpleEntry<>(UserStatus.LOGGED_IN, jws);
            }
        } else {
            return new AbstractMap.SimpleEntry<>(UserStatus.INVALID_PASSWORD, jws);
        }
    }

    @Override
    public UserStatus logoutUser(final String userName, final String authString) {
        if (userName == null || userName.isEmpty() || authString == null || authString.isEmpty()) {
            return UserStatus.INVALID_DATA;
        }

        if (this.userList.stream().noneMatch(user -> user.getUsername().equals(userName))) {
            return UserStatus.NOT_FOUND;
        }

        JwsStatus status = this.javaJws.abolishJws(userName, authString);
        switch (status) {
            case REMOVED:
                return UserStatus.OK;
            case UNAUTHORIZED:
                return UserStatus.UNAUTHORIZED;
            case NO_SESSION:
                return UserStatus.NOT_FOUND;
            default:
                return UserStatus.INVALID_DATA;
        }
    }


    /**
     * Check if the password is correct.
     *
     * @param providedPassword password provided by the user.
     * @param internalPassword internal password of the user.
     * @return true if the password matches, false otherwise.
     */
    private boolean checkPassword(String providedPassword, String internalPassword) {
        int integer = 1;
        boolean equals = true;
        int length = User.MAXIMUM_PASSWORD_LENGTH;
        providedPassword = padPassword(providedPassword, length);
        internalPassword = padPassword(internalPassword, length);

        for (int i = 0; i < length; i++) {
            if (providedPassword.charAt(i) != internalPassword.charAt(i)) {
                equals = false;
            }
            integer++;
        }
        return equals;
    }

    /**
     * Pad password to desired length.
     *
     * @param passwordToPad password to pad.
     * @param length        to pad the password to.
     * @return padded password.
     */
    private String padPassword(String passwordToPad, final int length) {
        while (passwordToPad.length() < length) {
            passwordToPad = passwordToPad.concat("A");
        }

        return passwordToPad;
    }
}
