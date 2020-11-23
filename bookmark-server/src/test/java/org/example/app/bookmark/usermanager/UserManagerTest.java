package org.example.app.bookmark.usermanager;

import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark.javajws.JwsStatus;
import org.example.app.bookmark.user.User;
import org.example.app.bookmark_api.model.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Map;

import static org.example.app.bookmark.testutils.TestUtils.printTestFooter;
import static org.example.app.bookmark.testutils.TestUtils.printTestHeader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserManagerTest {

    @Rule
    public TestName testName = new TestName();

    private IUserManager userManager;
    private IJavaJws javaJws;

    private void resetFields() throws IllegalAccessException {
        for (Field field : UserManager.class.getDeclaredFields()) {
            field.setAccessible(true);
            //Required for testing singleton
            //Has to be up to date with constructor
            switch (field.getName()) {
                case "userList":
                    field.set(userManager, null);
                    break;
                case "javaJws":
                    field.set(userManager, null);
                    break;
                case "instance":
                    field.set(null, null);
                    break;
            }
        }
    }

    @Before
    public void setUp() {
        printTestHeader(testName.getMethodName());

        this.javaJws = mock(IJavaJws.class);
    }

    @After
    public void tearDown() throws IllegalAccessException {
        printTestFooter();
        resetFields();
    }


    private IUserManager getUserManagerInstance() {
        return UserManager.getInstance(this.javaJws);
    }

    @Test
    public void testGetInstance() {
        assertNull("Instance should not have been created!", UserManager.getInstance());

        this.userManager = this.getUserManagerInstance();
        assertNotNull("Instance should not be null", this.userManager);
        int objectHash = this.userManager.hashCode();

        IUserManager userManager1 = UserManager.getInstance();
        assertNotNull("Instance should not be null", userManager1);
        assertEquals("Hash  codes do not match!", objectHash, userManager1.hashCode());

        IUserManager userManager2 = this.getUserManagerInstance();
        assertNotNull("Instance should not be null", userManager2);
        assertEquals("Hash  codes do not match!", objectHash, userManager2.hashCode());
    }

    @Test
    public void testRegisterUser() {
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);


        this.userManager = this.getUserManagerInstance();
        UserStatus registerStatus = this.userManager.registerUser(user);
        assertEquals("User registration should succeed!", registerStatus.getUserStatus(), UserStatus.REGISTERED.getUserStatus());
    }

    @Test
    public void testRegisterUserBadUserData() {
        UserData user = new UserData();
        String name = "";
        String password = "";
        user.setName(name);
        user.setPassword(password);


        this.userManager = this.getUserManagerInstance();
        UserStatus registerStatus = this.userManager.registerUser(user);
        assertEquals("User registration should fail!", registerStatus, UserStatus.INVALID_DATA);
    }

    @Test
    public void testRegisterUserUsernameExists() {
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);


        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        UserStatus registerStatus = this.userManager.registerUser(user);
        assertEquals("User registration should fail!", registerStatus, UserStatus.USERNAME_EXISTS);
    }

    @Test
    public void testRegisterUserPasswordTooLong() {
        UserData user = new UserData();
        String name = "name";
        int passwordLength = User.MAXIMUM_PASSWORD_LENGTH + 1;
        user.setName(name);
        user.setPassword("a".repeat(passwordLength));


        this.userManager = this.getUserManagerInstance();
        UserStatus registerStatus = this.userManager.registerUser(user);
        assertEquals("User registration should fail!", registerStatus, UserStatus.PASSWORD_TOO_LONG);
    }

    @Test
    public void testLoginUser() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.checkUserLoggedIn(name)).thenReturn(false);
        when(this.javaJws.createJws(name)).thenReturn(new AbstractMap.SimpleEntry<>(JwsStatus.CREATED, authString));

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        Map.Entry<UserStatus, String> loginUser = this.userManager.loginUser(user);
        assertEquals("User login should succeed!", loginUser.getKey(), UserStatus.OK);
        assertEquals("Auth string returned!", loginUser.getValue(), authString);
    }

    @Test
    public void testLoginUserLoginIssue() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.checkUserLoggedIn(name)).thenReturn(false);
        when(this.javaJws.createJws(name)).thenReturn(new AbstractMap.SimpleEntry<>(JwsStatus.ALREADY_EXISTS, authString));

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        Map.Entry<UserStatus, String> loginUser = this.userManager.loginUser(user);
        assertEquals("User login should not succeed!", loginUser.getKey(), UserStatus.LOGIN_ISSUE);
        assertEquals("No auth given!", loginUser.getValue(), "");
    }

    @Test
    public void testLoginUserLoggedIn() {
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.checkUserLoggedIn(name)).thenReturn(true);

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        Map.Entry<UserStatus, String> loginUser = this.userManager.loginUser(user);
        assertEquals("User login should not succeed!", loginUser.getKey(), UserStatus.LOGGED_IN);
        assertEquals("No auth given!", loginUser.getValue(), "");
    }

    @Test
    public void testLoginUserInvalidPassword() {
        UserData user2 = new UserData();
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);
        user2.setName(name);
        user2.setPassword(password + "2");


        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        Map.Entry<UserStatus, String> loginUser = this.userManager.loginUser(user2);
        assertEquals("User login should not succeed!", loginUser.getKey(), UserStatus.INVALID_PASSWORD);
        assertEquals("No auth given!", loginUser.getValue(), "");
    }

    @Test
    public void testLoginUserNotFound() {
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);


        this.userManager = this.getUserManagerInstance();
        Map.Entry<UserStatus, String> loginUser = this.userManager.loginUser(user);
        assertEquals("User login should not succeed!", loginUser.getKey(), UserStatus.NOT_FOUND);
        assertEquals("No auth given!", loginUser.getValue(), "");
    }

    @Test
    public void testLoginUserInvalidData() {
        UserData user = new UserData();


        this.userManager = this.getUserManagerInstance();
        Map.Entry<UserStatus, String> loginUser = this.userManager.loginUser(user);
        assertEquals("User login should not succeed!", loginUser.getKey(), UserStatus.INVALID_DATA);
        assertEquals("No auth given!", loginUser.getValue(), "");
    }

    @Test
    public void testLogoutUser() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.abolishJws(name, authString)).thenReturn(JwsStatus.REMOVED);

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        UserStatus logoutStatus = this.userManager.logoutUser(name, authString);
        assertEquals("User logout should succeed!", logoutStatus, UserStatus.OK);
    }

    @Test
    public void testLogoutUserUnauthorized() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.abolishJws(name, authString)).thenReturn(JwsStatus.UNAUTHORIZED);

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        UserStatus logoutStatus = this.userManager.logoutUser(name, authString);
        assertEquals("User logout should not succeed!", logoutStatus, UserStatus.UNAUTHORIZED);
    }

    @Test
    public void testLogoutUserNoSession() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.abolishJws(name, authString)).thenReturn(JwsStatus.NO_SESSION);

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        UserStatus logoutStatus = this.userManager.logoutUser(name, authString);
        assertEquals("User logout should not succeed!", logoutStatus, UserStatus.NOT_FOUND);
    }

    @Test
    public void testLogoutUserInvalidJwsStatus() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        when(this.javaJws.abolishJws(name, authString)).thenReturn(JwsStatus.CREATED);

        this.userManager = this.getUserManagerInstance();
        this.userManager.registerUser(user);
        UserStatus logoutStatus = this.userManager.logoutUser(name, authString);
        assertEquals("User logout should not succeed!", logoutStatus, UserStatus.INVALID_DATA);
    }

    @Test
    public void testLogoutUserNotFound() {
        String authString = "authString";
        UserData user = new UserData();
        String name = "name";
        String password = "password";
        user.setName(name);
        user.setPassword(password);

        this.userManager = this.getUserManagerInstance();
        UserStatus logoutStatus = this.userManager.logoutUser(name, authString);
        assertEquals("User logout should not succeed!", logoutStatus, UserStatus.NOT_FOUND);
    }

    @Test
    public void testLogoutUserInvalidData() {
        String authString = "";
        String name = "";

        this.userManager = this.getUserManagerInstance();
        UserStatus logoutStatus = this.userManager.logoutUser(name, authString);
        assertEquals("User logout should not succeed!", logoutStatus, UserStatus.INVALID_DATA);
    }

}
