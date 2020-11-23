package org.example.app.bookmark.javajws;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.Field;
import java.util.Map;

import static org.example.app.bookmark.testutils.TestUtils.printTestFooter;
import static org.example.app.bookmark.testutils.TestUtils.printTestHeader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JavaJwsTest {

    @Rule
    public TestName testName = new TestName();

    private IJavaJws javaJws;

    private void resetFields() throws IllegalAccessException {
        for (Field field : JavaJws.class.getDeclaredFields()) {
            field.setAccessible(true);
            //Required for testing singleton
            //Has to be up to date with constructor
            switch (field.getName()) {
                case "authorizedUsers":
                    field.set(javaJws, null);
                    break;
                case "secretKey":
                    field.set(javaJws, null);
                    break;
                case "utils":
                    field.set(javaJws, null);
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

    }

    @After
    public void tearDown() throws IllegalAccessException {
        printTestFooter();
        resetFields();
    }


    private IJavaJws getJavaJws() {
        return JavaJws.getInstance();
    }

    @Test
    public void testGetInstance() {
        this.javaJws = this.getJavaJws();
        assertNotNull("Instance should not be null", this.javaJws);
        int objectHash = this.javaJws.hashCode();

        IJavaJws javaJws1 = JavaJws.getInstance();
        assertNotNull("Instance should not be null", javaJws1);
        assertEquals("Hash  codes do not match!", objectHash, javaJws1.hashCode());

        IJavaJws javaJws2 = this.getJavaJws();
        assertNotNull("Instance should not be null", javaJws2);
        assertEquals("Hash  codes do not match!", objectHash, javaJws2.hashCode());
    }

    @Test
    public void testCreateJws() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        Map.Entry<JwsStatus, String> entry = this.javaJws.createJws(username);
        assertEquals("Creation of jws should succeed!", entry.getKey().getJwsStatus(), JwsStatus.CREATED.getJwsStatus());
    }

    @Test
    public void testCreateJwsAlreadyExists() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        this.javaJws.createJws(username);
        Map.Entry<JwsStatus, String> entry = this.javaJws.createJws(username);
        assertEquals("Creation of jws should succeed!", entry.getKey(), JwsStatus.ALREADY_EXISTS);
    }

    @Test
    public void testCheckUserLoggedIn() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        this.javaJws.createJws(username);
        boolean loggedIn = this.javaJws.checkUserLoggedIn(username);
        assertTrue("Creation of jws should succeed!", loggedIn);
    }

    @Test
    public void testCheckUserLoggedInFalse() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        boolean loggedIn = this.javaJws.checkUserLoggedIn(username);
        assertFalse("Creation of jws should succeed!", loggedIn);
    }

    @Test
    public void testAbolishJws() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        Map.Entry<JwsStatus, String> entry = this.javaJws.createJws(username);
        JwsStatus abolishStatus = this.javaJws.abolishJws(username, entry.getValue());
        assertEquals("Abolish of the jws should succeed!", abolishStatus, JwsStatus.REMOVED);
    }

    @Test
    public void testAbolishJwsBadUsername() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        Map.Entry<JwsStatus, String> entry = this.javaJws.createJws(username);
        this.javaJws.createJws(username + "2");
        JwsStatus abolishStatus = this.javaJws.abolishJws(username + "2", entry.getValue());
        assertEquals("Abolish of the jws should not succeed!", abolishStatus, JwsStatus.UNAUTHORIZED);
    }

    @Test
    public void testAbolishJwsNoSession() {
        String authString = "authString";
        String username = "user";

        this.javaJws = this.getJavaJws();
        JwsStatus abolishStatus = this.javaJws.abolishJws(username, authString);
        assertEquals("Abolish of the jws should not succeed!", abolishStatus, JwsStatus.NO_SESSION);
    }

    @Test
    public void testAuthorizeUser() {
        String username = "user";

        this.javaJws = this.getJavaJws();
        Map.Entry<JwsStatus, String> entry = this.javaJws.createJws(username);
        String authUser = this.javaJws.authorizeUser(entry.getValue());
        assertEquals("User should be authorized", username, authUser);
    }

    @Test
    public void testAuthorizeUserBadUsername() {
        String username = "";

        this.javaJws = this.getJavaJws();
        Map.Entry<JwsStatus, String> entry = this.javaJws.createJws(username);
        String authUser = this.javaJws.authorizeUser(entry.getValue());
        assertEquals("User should not be authorized", username, authUser);
    }

    @Test
    public void testAuthorizeUserBadCredentials() {
        String authString = "authString";

        this.javaJws = this.getJavaJws();
        String authUser = this.javaJws.authorizeUser(authString);
        assertEquals("User should not be authorized", "", authUser);
    }

}
