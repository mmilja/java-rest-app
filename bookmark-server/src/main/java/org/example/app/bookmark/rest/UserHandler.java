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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response registerUser(@PathParam("UserData") UserData userData) {

        Response response;
        UserStatus registrationStatus = this.userManager.registerUser(userData);
        LOGGER.info("Registering user: " + userData.getName());

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
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response loginUser(@PathParam("UserData") UserData userData) {

        Response response;
        UserStatus loginStatus = this.userManager.loginUser(userData);
        LOGGER.info("User login: " + userData.getName());

        switch (loginStatus) {
            case OK:
                response = Response.status(Response.Status.CREATED)
                        .entity("Successfully logged-in user: " + userData.getName()).build();
                break;
            case NOT_FOUND:
                response = Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("User not found: " + userData.getName()).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to log-in the user.").build();
                break;
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response logoutUser(@PathParam("UserName") String userName,
                               @HeaderParam("authorization") String authString) {


        Response response;
        UserStatus logoutStatus = this.userManager.logoutUser(userName, authString);
        LOGGER.info("User logout: " + userName);

        switch (logoutStatus) {
            case OK:
                response = Response.status(Response.Status.CREATED)
                        .entity("Successfully logged-out user: " + userName).build();
                break;
            case NOT_FOUND:
                response = Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("User not found: " + userName).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to create the user.").build();
                break;
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }
}
