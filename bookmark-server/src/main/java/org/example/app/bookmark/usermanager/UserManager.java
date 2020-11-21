package org.example.app.bookmark.usermanager;

import com.ericsson.adp.bookmark_api.model.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.exceptions.ExecutionFailedException;
import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark.user.User;
import org.example.app.bookmark.utils.IUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages users.
 */
public class UserManager implements IUserManager {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserManager.class.getSimpleName());


    /**
     * Singleton instance.
     */
    private static volatile UserManager instance;

    /**
     * Object containing utility methods.
     */
    private final IUtils utils;

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
     * @param utils object containing utility methods.
     * @param javaJws object used for jws token management.
     */
    private UserManager(final IUtils utils, final IJavaJws javaJws) {
        this.utils = utils;
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
     * @param utils object containing utility methods.
     * @param javaJws object used for jws token management.
     * @return IUserManager instance.
     */
    public static IUserManager getInstance(final IUtils utils, final IJavaJws javaJws) {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager(utils, javaJws);
                }
            }
        }
        return instance;
    }

    @Override
    public UserStatus registerUser(final UserData userData) {
        if (userData.getName().isEmpty() || userData.getPassword().isEmpty()) {
            return UserStatus.INVALID_DATA;
        }

        if (userList.stream().anyMatch(user -> user.getUsername().equals(userData.getName()))) {
            return UserStatus.USERNAME_EXISTS;
        }

        User user = new User(userData);
        this.userList.add(user);

        return UserStatus.REGISTERED;
    }

    @Override
    public UserStatus loginUser(final UserData userData) {
        if (userData.getName().isEmpty() || userData.getPassword().isEmpty()) {
            return UserStatus.INVALID_DATA;
        }

        if (userList.stream().noneMatch(user -> user.getUsername().equals(userData.getName()))) {
            return UserStatus.NOT_FOUND;
        }

        User user = userList.stream().filter(tmpUser -> tmpUser.getUsername().equals(userData.getName())).findFirst().
                orElseThrow(() -> new ExecutionFailedException("Failed to get user with name: " + userData.getName()));
        if (checkPassword(userData.getPassword(), user.getPassword())) {
            this.javaJws.createJws(userData.getName());
        } else {
            return UserStatus.INVALID_PASSWORD;
        }

        return UserStatus.OK;
    }

    @Override
    public UserStatus logoutUser(final String userName, final String suthString) {
        if (userName.isEmpty() || suthString.isEmpty()) {
            return UserStatus.INVALID_DATA;
        }

        if (userList.stream().noneMatch(user -> user.getUsername().equals(userName))) {
            return UserStatus.NOT_FOUND;
        }

        this.javaJws.abolishJws(userName, suthString);

        return UserStatus.OK;
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
        int length = Math.max(providedPassword.length(), internalPassword.length());
        if (providedPassword.length() > internalPassword.length()) {
            providedPassword = padPassword(providedPassword, length);
        } else if (providedPassword.length() < internalPassword.length()) {
            internalPassword = padPassword(internalPassword, length);
        }

        for (int i = 0; i < length; i++) {
            if (providedPassword.charAt(i) != internalPassword.charAt(i)) {
                equals = false;
            }
            integer++;
        }
        return  equals;
    }

    /**
     * Pad password to desired length.
     *
     * @param passwordToPad password to pad.
     * @param length to pad the password to.
     * @return padded password.
     */
    private String padPassword(String passwordToPad, final int length) {
        while (passwordToPad.length() < length) {
            passwordToPad = passwordToPad.concat("A");
        }

        return passwordToPad;
    }
}
