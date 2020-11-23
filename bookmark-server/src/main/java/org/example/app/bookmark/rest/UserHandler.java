package org.example.app.bookmark.rest;

import org.example.app.bookmark_api.model.Message;
import org.example.app.bookmark_api.model.UserData;
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

    /**
     * Register a new user.
     *
     * @param userData with which a user will be registered.
     * @return  CREATED if operation is successful.
     *          BAD REQUEST if something went wrong during registration attempt.
     *          INTERNAL SERVER ERROR in case of an error.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response registerUser(UserData userData) {

        Response response;
        UserStatus registrationStatus = this.userManager.registerUser(userData);
        LOGGER.info("Registering user attempt: " + userData.getName());

        Message message = new Message();
        switch (registrationStatus) {
            case REGISTERED:
                message.setMessage("Successfully registered user: " + userData.getName());
                response = Response.status(Response.Status.CREATED).entity(message).build();
                break;
            case USERNAME_EXISTS:
                message.setMessage("Chosen username already exist: " + userData.getName());
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case INVALID_DATA:
                message.setMessage("Not enough information provided to create the user.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case PASSWORD_TOO_LONG:
                message.setMessage("Provided password is too long.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            default:
                message.setMessage(DEFAULT_MESSAGE);
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
                break;
        }

        return response;
    }

    /**
     * Login a registrated user.
     *
     * @param userData of the user to login.
     * @return  OK and jws token information is operation is successful.
     *          BAD_REQUEST if something went wrong during login attempt.
     *          INTERNAL_SERVER_ERROR in case of an error.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response loginUser(UserData userData) {

        Response response;
        Map.Entry<UserStatus, String> loginStatus = this.userManager.loginUser(userData);
        LOGGER.info("User login attempt: " + userData.getName());

        Message message = new Message();
        switch (loginStatus.getKey()) {
            case OK:
                message.setMessage("Successfully logged-in user: " + userData.getName()
                        + "\n Use the following string for authorization: " + loginStatus.getValue());
                response = Response.status(Response.Status.OK).entity(message).build();
                break;
            case NOT_FOUND:
                message.setMessage("User does not exist: " + userData.getName());
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case INVALID_DATA:
                message.setMessage("Not enough information provided to log-in the user.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case LOGIN_ISSUE:
                message.setMessage("Could not log in the user.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case LOGGED_IN:
                message.setMessage("User with that name is already logged in.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case INVALID_PASSWORD:
                message.setMessage("Provided password is invalid.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            default:
                message.setMessage(DEFAULT_MESSAGE);
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
                break;
        }
        return response;
    }

    /**
     * Log-out a logged in user.
     *
     * @param userName of the user to log-out.
     * @param authString to authorize the user.
     * @return  OK if operation is successful.
     *          BAD_REQUEST if something went wrong during log-out attempt.
     *          UNAUTHORIZED if authorization failed.
     *          INTERNAL_SERVER_ERROR in case of an error.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logoutUser(String userName, @HeaderParam("authorization") String authString) {


        Response response;
        UserStatus logoutStatus = this.userManager.logoutUser(userName, authString);
        LOGGER.info("User logout attempt: " + userName);

        Message message = new Message();
        switch (logoutStatus) {
            case OK:
                message.setMessage("Successfully logged-out user: " + userName);
                response = Response.status(Response.Status.OK).entity(message).build();
                break;
            case NOT_FOUND:
                message.setMessage("User does not exist: " + userName);
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case INVALID_DATA:
                message.setMessage("Information to log out the user is not correct.");
                response = Response.status(Response.Status.BAD_REQUEST).entity(message).build();
                break;
            case UNAUTHORIZED:
                message.setMessage("Authorization information is not correct.");
                response = Response.status(Response.Status.UNAUTHORIZED).entity(message).build();
                break;
            default:
                message.setMessage(DEFAULT_MESSAGE);
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
                break;
        }

        return response;
    }
}
