package org.example.app.bookmark.rest;


import com.ericsson.adp.bookmark_api.model.Bookmark;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.bookmarkmanager.BookmarkStatus;
import org.example.app.bookmark.bookmarkmanager.IBookmarkManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Implements methods used to manage Bookmark objects.
 */
@Path("/bookmark")
public class BookmarkHandler {

    /**
     * Message that indicates that something has gone wrong.
     */
    public static final String DEFAULT_MESSAGE = "An issue occurred";

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(BookmarkHandler.class.getSimpleName());

    /**
     * Used for managing bookmark objects.
     */
    private final IBookmarkManager bookmarkManager;

    /**
     * Constructor.
     *
     * @param bookmarkManager used for managing bookmark objects.
     */
    @Inject
    public BookmarkHandler(final IBookmarkManager bookmarkManager) {
        this.bookmarkManager = bookmarkManager;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response addBookmark(@PathParam("Bookmark") Bookmark bookmark,
                               @HeaderParam("authorization") String authString,
                                @HeaderParam("kid") UUID userUuid) {

        Response response;
        BookmarkStatus logoutStatus = this.bookmarkManager.addBookmark(bookmark, authString, userUuid);
        LOGGER.info("Adding bookmark: " + bookmark.getBookmarkLink().getName());

        switch (logoutStatus) {
            case CREATED:
                response = Response.status(Response.Status.CREATED)
                        .entity("Successfully added bookmark: " + bookmark.getBookmarkLink().getName()).build();
                break;
            case BOOKMARK_EXISTS:
                response = Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Bookmark already exists: " + bookmark.getBookmarkLink().getUri()).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to add the bookmark.").build();
                break;
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }
}
