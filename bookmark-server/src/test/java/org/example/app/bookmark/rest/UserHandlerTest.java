package org.example.app.bookmark.rest;

import org.example.app.bookmark_api.model.UserData;
import org.example.app.bookmark.usermanager.IUserManager;
import org.example.app.bookmark.usermanager.UserStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import javax.ws.rs.core.Response;

import java.util.AbstractMap;

import static org.example.app.bookmark.testutils.TestUtils.printTestFooter;
import static org.example.app.bookmark.testutils.TestUtils.printTestHeader;
import static org.example.app.bookmark.testutils.TestUtils.printTestInfo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserHandlerTest {

    @Rule
    public TestName testName = new TestName();

    private IUserManager userManager;

    @Before
    public void setUp() {
        printTestHeader(testName.getMethodName());
        userManager = mock(IUserManager.class);
    }

    @After
    public void tearDown() {
        printTestFooter();
    }

    @Test
    public void testRegisterUser() {
        UserData registeredUser = new UserData();

        when(userManager.registerUser(registeredUser)).thenReturn(UserStatus.REGISTERED);

        printTestInfo("Registering valid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response registerResponse = userHandler.registerUser(registeredUser);
        assertEquals("User registration should be valid",
                registerResponse.getStatus(), Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testRegisterUserUsernameExists() {
        UserData registeredUser = new UserData();

        when(userManager.registerUser(registeredUser)).thenReturn(UserStatus.USERNAME_EXISTS);

        printTestInfo("Registering invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response registerResponse = userHandler.registerUser(registeredUser);
        assertEquals("User registration should fail since username is taken",
                registerResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRegisterUserInvalidData() {
        UserData registeredUser = new UserData();

        when(userManager.registerUser(registeredUser)).thenReturn(UserStatus.INVALID_DATA);

        printTestInfo("Registering invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response registerResponse = userHandler.registerUser(registeredUser);
        assertEquals("User registration should fail since user data is invalid",
                registerResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRegisterUserPasswordTooLong() {
        UserData registeredUser = new UserData();

        when(userManager.registerUser(registeredUser)).thenReturn(UserStatus.PASSWORD_TOO_LONG);

        printTestInfo("Registering invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response registerResponse = userHandler.registerUser(registeredUser);
        assertEquals("User registration should fail since user password is too long",
                registerResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRegisterUserInternalServerError() {
        UserData registeredUser = new UserData();

        when(userManager.registerUser(registeredUser)).thenReturn(UserStatus.LOGGED_IN);

        printTestInfo("Registering invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response registerResponse = userHandler.registerUser(registeredUser);
        assertEquals("User registration should fail since operation status is unknown",
                registerResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testLoginUser() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.OK, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in valid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should be valid",
                loginResponse.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testLoginUserNotFound() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.NOT_FOUND, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should fail",
                loginResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLoginUserInvalidData() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.INVALID_DATA, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should fail",
                loginResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLoginUserLoginIssue() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.LOGIN_ISSUE, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should fail",
                loginResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLoginUserLoggedin() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.LOGGED_IN, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should fail",
                loginResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLoginUserInvalidPassword() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.INVALID_PASSWORD, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should fail",
                loginResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLoginUserInternalServerError() {
        UserData loginUser = new UserData();
        String authToken = "authToken";
        AbstractMap.SimpleEntry<UserStatus, String> entry = new AbstractMap.SimpleEntry<>(UserStatus.REGISTERED, authToken);

        when(userManager.loginUser(loginUser)).thenReturn(entry);

        printTestInfo("Logging-in invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response loginResponse = userHandler.loginUser(loginUser);
        assertEquals("User login should fail",
                loginResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testLogoutUser() {
        String userName = "username";
        String authToken = "authToken";

        when(userManager.logoutUser(userName, authToken)).thenReturn(UserStatus.OK);

        printTestInfo("Logging-out valid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response logoutResponse = userHandler.logoutUser(userName, authToken);
        assertEquals("User logout should be valid",
                logoutResponse.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testLogoutUserNotFound() {
        String userName = "username";
        String authToken = "authToken";

        when(userManager.logoutUser(userName, authToken)).thenReturn(UserStatus.NOT_FOUND);

        printTestInfo("Logging-out invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response logoutResponse = userHandler.logoutUser(userName, authToken);
        assertEquals("User logout should be invalid",
                logoutResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLogoutUserInvalidData() {
        String userName = "username";
        String authToken = "authToken";

        when(userManager.logoutUser(userName, authToken)).thenReturn(UserStatus.INVALID_DATA);

        printTestInfo("Logging-out invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response logoutResponse = userHandler.logoutUser(userName, authToken);
        assertEquals("User logout should be invalid",
                logoutResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testLogoutUserUnauthorized() {
        String userName = "username";
        String authToken = "authToken";

        when(userManager.logoutUser(userName, authToken)).thenReturn(UserStatus.UNAUTHORIZED);

        printTestInfo("Logging-out invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response logoutResponse = userHandler.logoutUser(userName, authToken);
        assertEquals("User logout should be invalid",
                logoutResponse.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testLogoutUserInternalServerError() {
        String userName = "username";
        String authToken = "authToken";

        when(userManager.logoutUser(userName, authToken)).thenReturn(UserStatus.REGISTERED);

        printTestInfo("Logging-out invalid user");
        UserHandler userHandler = new UserHandler(userManager);
        Response logoutResponse = userHandler.logoutUser(userName, authToken);
        assertEquals("User logout should be invalid",
                logoutResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
