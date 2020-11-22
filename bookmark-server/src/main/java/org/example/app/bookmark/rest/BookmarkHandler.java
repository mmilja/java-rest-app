package org.example.app.bookmark.rest;


import com.ericsson.adp.bookmark_api.model.Bookmark;
import com.ericsson.adp.bookmark_api.model.BookmarkLink;
import com.ericsson.adp.bookmark_api.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.bookmarkmanager.BookmarkStatus;
import org.example.app.bookmark.bookmarkmanager.IBookmarkManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements methods used to manage Bookmark objects.
 */
@Path("/")
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bookmark")
    public Response getBookmark(@HeaderParam("authorization") String authString) {

        Response response;
        Map.Entry<BookmarkStatus, List<Bookmark>> getStatus = this.bookmarkManager.getBookmarks(authString);
        LOGGER.info("Getting bookmarks");

        switch (getStatus.getKey()) {
            case OK:
                response = Response.status(Response.Status.CREATED).entity(getStatus.getValue()).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to get the bookmarks.").build();
                break;
            case UNAUTHORIZED:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Authorization information is not correct").build();
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
    @Path("/bookmark")
    public Response addBookmark(Bookmark bookmark, @HeaderParam("authorization") String authString) {

        Response response;
        BookmarkStatus addStatus = this.bookmarkManager.addBookmark(bookmark, authString);
        LOGGER.info("Adding bookmark: " + bookmark.getBookmarkLink().getName());

        switch (addStatus) {
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
            case UNAUTHORIZED:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Authorization information is not correct").build();
                break;
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bookmark/{bookmarkName}")
    public Response deleteBookmark(
            @PathParam("bookmarkName") String bookmarkName, @HeaderParam("authorization") String authString) {

        Response response;
        BookmarkStatus deleteStatus = this.bookmarkManager.deleteBookmark(bookmarkName, authString);
        LOGGER.info("Deleting bookmark: " + bookmarkName);

        switch (deleteStatus) {
            case DELETED:
                response = Response.status(Response.Status.OK)
                        .entity("Successfully deleted bookmark: " + bookmarkName).build();
                break;
            case INVALID_DATA:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Not enough information provided to delete the bookmark.").build();
                break;
            case UNAUTHORIZED:
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity("Authorization information is not correct").build();
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
    @Path("/bookmark/{bookmarkName}")
    public Response updateBookmark(
            Bookmark bookmark,
            @PathParam("bookmarkName") String bookmarkName,
            @HeaderParam("authorization") String authString) {

        Response response;
        BookmarkStatus updateStatus = this.bookmarkManager.updateBookmark(bookmarkName, bookmark, authString);
        LOGGER.info("Updating bookmark: " + bookmarkName);

        Message message = new Message();
        switch (updateStatus) {
            case UPDATED:
                message.setMessage("Successfully updated bookmark: " + bookmarkName);
                response = Response.status(Response.Status.OK).entity(message).build();
                break;
            case INVALID_DATA:
                message.setMessage("Not enough information provided to add the bookmark.");
                response = Response.status(Response.Status.NO_CONTENT).entity(message).build();
                break;
            case UNAUTHORIZED:
                message.setMessage("Authorization information is not correct");
                response = Response.status(Response.Status.NO_CONTENT)
                        .entity(message).build();
                break;
            default:
                message.setMessage(DEFAULT_MESSAGE);
                response = Response.status(Response.Status.NOT_FOUND).entity(message).build();
                break;
        }

        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bookmark/public")
    public Response getPublicBookmarks(@HeaderParam("authorization") String authString) {

        Response response;
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> publicBookmarks =
                this.bookmarkManager.getPublicBookmarks(authString);
        LOGGER.info("Getting public bookmarks.");

        Message message = new Message();
        switch (publicBookmarks.getKey()) {
            case OK:
                response = Response.status(Response.Status.OK)
                        .entity(publicBookmarks.getValue()).build();
                break;
            case UNAUTHORIZED:
                message.setMessage("Authorization information is not correct");
                response = Response.status(Response.Status.NO_CONTENT).entity(message).build();
                break;
            default:
                response = Response.status(Response.Status.NOT_FOUND).entity(DEFAULT_MESSAGE).build();
                break;
        }

        return response;
    }

}
