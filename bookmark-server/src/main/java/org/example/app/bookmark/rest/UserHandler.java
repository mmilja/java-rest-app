package org.example.app.bookmark.rest;

import com.ericsson.adp.bookmark_api.model.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.usermanager.IUserManager;
import org.example.app.bookmark.usermanager.UserStatus;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Implements methods used to manage Bookmark objects.
 */
@Path("/user")
public class UserHandler {

    /**
     * Message that indicates that something has gone wrong.
     */
    public static final String DEFAULT_MESSAGE = "An issue occurred";

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserHandler.class.getSimpleName());

    /**
     * Used for managing user objects.
     */
    private final IUserManager userManager;

    /**
     * Constructor.
     *
     * @param userManager used for managing user objects.
     */
    @Inject
    public UserHandler(final IUserManager userManager) {
        this.userManager = userManager;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response registerUser(UserData userData) {

        Response response;
        UserStatus registrationStatus = this.userManager.registerUser(userData);
        LOGGER.info("Registering user attempt: " + userData.getName());

        switch (registrationStatus) {
            case REGISTERED:
                response = Response.status(Response.Status.CREATED)
                        .entity("Successfully registered user: " + userData.getName()).build();
                break;
            case USERNAME_EXISTS:
                response = Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Chosen username already exist: " + userData.getName()).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to create the user.").build();
                break;
            case PASSWORD_TOO_LONG:
                response = Response.status(Response.Status.BAD_REQUEST)
                        .entity("Provided password is too long.").build();
                break;
            default:
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response loginUser(UserData userData) {

        Response response;
        Map.Entry<UserStatus, String> loginStatus = this.userManager.loginUser(userData);
        LOGGER.info("User login attempt: " + userData.getName());

        switch (loginStatus.getKey()) {
            case OK:
                response = Response.status(Response.Status.CREATED)
                        .entity("Successfully logged-in user: " + userData.getName()
                                + "\n Use the following string for authorization: " + loginStatus.getValue()).build();
                break;
            case NOT_FOUND:
                response = Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("User does not exist: " + userData.getName()).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to log-in the user.").build();
                break;
            case LOGIN_ISSUE:
                response = Response.status(Response.Status.BAD_REQUEST)
                        .entity("Could not log in the user.").build();
                break;
            case LOGGED_IN:
                response = Response.status(Response.Status.BAD_REQUEST)
                        .entity("User with that name is already logged in.").build();
                break;
            case INVALID_PASSWORD:
                response = Response.status(Response.Status.BAD_REQUEST)
                        .entity("Provided password is invalid.").build();
                break;
            default:
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(DEFAULT_MESSAGE).build();
                break;
        }
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logoutUser(String userName, @HeaderParam("authorization") String authString) {


        Response response;
        UserStatus logoutStatus = this.userManager.logoutUser(userName, authString);
        LOGGER.info("User logout attempt: " + userName);

        switch (logoutStatus) {
            case OK:
                response = Response.status(Response.Status.OK)
                        .entity("Successfully logged-out user: " + userName).build();
                break;
            case NOT_FOUND:
                response = Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("User does not exist: " + userName).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Information to log out the user is not correct.").build();
                break;
            case UNAUTHORIZERD:
                response = Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authorization information is not correct.").build();
                break;
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }
}
