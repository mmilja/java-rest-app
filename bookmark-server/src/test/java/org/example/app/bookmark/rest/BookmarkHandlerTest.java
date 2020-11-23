package org.example.app.bookmark.rest;

import org.example.app.bookmark.bookmarkmanager.BookmarkStatus;
import org.example.app.bookmark.bookmarkmanager.IBookmarkManager;
import org.example.app.bookmark_api.model.Bookmark;
import org.example.app.bookmark_api.model.BookmarkLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import javax.ws.rs.core.Response;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.example.app.bookmark.testutils.TestUtils.printTestFooter;
import static org.example.app.bookmark.testutils.TestUtils.printTestHeader;
import static org.example.app.bookmark.testutils.TestUtils.printTestInfo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookmarkHandlerTest {

    @Rule
    public TestName testName = new TestName();

    private IBookmarkManager bookmarkManager;

    @Before
    public void setUp() {
        printTestHeader(testName.getMethodName());
        bookmarkManager = mock(IBookmarkManager.class);
    }

    @After
    public void tearDown() {
        printTestFooter();
    }

    @Test
    public void testGetBookmark() {
        String authString = "authString";
        List<Bookmark> bookmarkList = new ArrayList<>();
        Map.Entry<BookmarkStatus, List<Bookmark>> entry = new AbstractMap.SimpleEntry<>(BookmarkStatus.OK, bookmarkList);

        when(bookmarkManager.getBookmarks(authString)).thenReturn(entry);

        printTestInfo("Getting user bookmarks");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response getResponse = bookmarkHandler.getBookmark(authString);
        assertEquals("Get operation should be valid",
                getResponse.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testGetBookmarkUnauthorized() {
        String authString = "authString";
        List<Bookmark> bookmarkList = new ArrayList<>();
        Map.Entry<BookmarkStatus, List<Bookmark>> entry = new AbstractMap.SimpleEntry<>(BookmarkStatus.UNAUTHORIZED, bookmarkList);

        when(bookmarkManager.getBookmarks(authString)).thenReturn(entry);

        printTestInfo("Getting user bookmarks");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response getResponse = bookmarkHandler.getBookmark(authString);
        assertEquals("Get operation should be invalid, user not authorized",
                getResponse.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testGetBookmarkInternalServerError() {
        String authString = "authString";
        List<Bookmark> bookmarkList = new ArrayList<>();
        Map.Entry<BookmarkStatus, List<Bookmark>> entry = new AbstractMap.SimpleEntry<>(BookmarkStatus.DELETED, bookmarkList);

        when(bookmarkManager.getBookmarks(authString)).thenReturn(entry);

        printTestInfo("Getting user bookmarks");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response getResponse = bookmarkHandler.getBookmark(authString);
        assertEquals("Get operation should be invalid, unrecognized signal",
                getResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testAddBookmark() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("Name");
        bookmark.setBookmarkLink(bookmarkLink);

        when(bookmarkManager.addBookmark(bookmark, authString)).thenReturn(BookmarkStatus.CREATED);

        printTestInfo("Adding user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response addResponse = bookmarkHandler.addBookmark(bookmark, authString);
        assertEquals("Add operation should be valid",
                addResponse.getStatus(), Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void testAddBookmarkExists() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("Name");
        bookmark.setBookmarkLink(bookmarkLink);

        when(bookmarkManager.addBookmark(bookmark, authString)).thenReturn(BookmarkStatus.BOOKMARK_EXISTS);

        printTestInfo("Adding user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response addResponse = bookmarkHandler.addBookmark(bookmark, authString);
        assertEquals("Add operation should be invalid",
                addResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testAddBookmarkInvalidData() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("Name");
        bookmark.setBookmarkLink(bookmarkLink);

        when(bookmarkManager.addBookmark(bookmark, authString)).thenReturn(BookmarkStatus.INVALID_DATA);

        printTestInfo("Adding user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response addResponse = bookmarkHandler.addBookmark(bookmark, authString);
        assertEquals("Add operation should be invalid",
                addResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testAddBookmarkUnauthorized() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("Name");
        bookmark.setBookmarkLink(bookmarkLink);

        when(bookmarkManager.addBookmark(bookmark, authString)).thenReturn(BookmarkStatus.UNAUTHORIZED);

        printTestInfo("Adding user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response addResponse = bookmarkHandler.addBookmark(bookmark, authString);
        assertEquals("Add operation should be invalid",
                addResponse.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testAddBookmarkInternalServerError() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("Name");
        bookmark.setBookmarkLink(bookmarkLink);

        when(bookmarkManager.addBookmark(bookmark, authString)).thenReturn(BookmarkStatus.DELETED);

        printTestInfo("Adding user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response addResponse = bookmarkHandler.addBookmark(bookmark, authString);
        assertEquals("Add operation should be invalid",
                addResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testUpdateBookmark() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";
        Bookmark bookmark = new Bookmark();

        when(bookmarkManager.updateBookmark(bookmarkName, bookmark, authString)).thenReturn(BookmarkStatus.UPDATED);

        printTestInfo("Update user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response updateResponse = bookmarkHandler.updateBookmark(bookmark, bookmarkName, authString);
        assertEquals("Update operation should be valid",
                updateResponse.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testUpdateBookmarkInvalidData() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";
        Bookmark bookmark = new Bookmark();

        when(bookmarkManager.updateBookmark(bookmarkName, bookmark, authString)).thenReturn(BookmarkStatus.INVALID_DATA);

        printTestInfo("Update user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response updateResponse = bookmarkHandler.updateBookmark(bookmark, bookmarkName, authString);
        assertEquals("Update operation should be invalid",
                updateResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testUpdateBookmarkUnauthorized() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";
        Bookmark bookmark = new Bookmark();

        when(bookmarkManager.updateBookmark(bookmarkName, bookmark, authString)).thenReturn(BookmarkStatus.UNAUTHORIZED);

        printTestInfo("Update user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response updateResponse = bookmarkHandler.updateBookmark(bookmark, bookmarkName, authString);
        assertEquals("Update operation should be invalid, user is not authorized",
                updateResponse.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testUpdateBookmarkInternalServerError() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";
        Bookmark bookmark = new Bookmark();

        when(bookmarkManager.updateBookmark(bookmarkName, bookmark, authString)).thenReturn(BookmarkStatus.CREATED);

        printTestInfo("Update user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response updateResponse = bookmarkHandler.updateBookmark(bookmark, bookmarkName, authString);
        assertEquals("Update operation should be invalid",
                updateResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testDeleteBookmark() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";

        when(bookmarkManager.deleteBookmark(bookmarkName, authString)).thenReturn(BookmarkStatus.DELETED);

        printTestInfo("Delete user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response deleteResponse = bookmarkHandler.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete operation should be valid",
                deleteResponse.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testDeleteBookmarkInvalidData() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";

        when(bookmarkManager.deleteBookmark(bookmarkName, authString)).thenReturn(BookmarkStatus.INVALID_DATA);

        printTestInfo("Delete user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response deleteResponse = bookmarkHandler.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete operation should be invalid",
                deleteResponse.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteBookmarkUnauthorized() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";

        when(bookmarkManager.deleteBookmark(bookmarkName, authString)).thenReturn(BookmarkStatus.UNAUTHORIZED);

        printTestInfo("Delete user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response deleteResponse = bookmarkHandler.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete operation should be invalid, user is not authorized",
                deleteResponse.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testDeleteBookmarkInternalServerError() {
        String authString = "authString";
        String bookmarkName = "bookmarkName";

        when(bookmarkManager.deleteBookmark(bookmarkName, authString)).thenReturn(BookmarkStatus.CREATED);

        printTestInfo("Delete user bookmark");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response deleteResponse = bookmarkHandler.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete operation should be invalid",
                deleteResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    public void testgetPublicBookmarks() {
        String authString = "authString";
        Set<BookmarkLink> bookmarkList = new HashSet<>();
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> entry = new AbstractMap.SimpleEntry<>(BookmarkStatus.OK, bookmarkList);

        when(bookmarkManager.getPublicBookmarks(authString)).thenReturn(entry);

        printTestInfo("Get public bookmarks");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response getPublicResponse = bookmarkHandler.getPublicBookmarks(authString);
        assertEquals("Get public bookmarks operation should be valid",
                getPublicResponse.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void testgetPublicBookmarksUnauthorized() {
        String authString = "authString";
        Set<BookmarkLink> bookmarkList = new HashSet<>();
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> entry = new AbstractMap.SimpleEntry<>(BookmarkStatus.UNAUTHORIZED, bookmarkList);

        when(bookmarkManager.getPublicBookmarks(authString)).thenReturn(entry);

        printTestInfo("Get public bookmarks");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response getPublicResponse = bookmarkHandler.getPublicBookmarks(authString);
        assertEquals("Get public bookmarks operation should be invalid, user not authorized",
                getPublicResponse.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void testgetPublicBookmarksInternalServerError() {
        String authString = "authString";
        Set<BookmarkLink> bookmarkList = new HashSet<>();
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> entry = new AbstractMap.SimpleEntry<>(BookmarkStatus.CREATED, bookmarkList);

        when(bookmarkManager.getPublicBookmarks(authString)).thenReturn(entry);

        printTestInfo("Get public bookmarks");
        BookmarkHandler bookmarkHandler = new BookmarkHandler(bookmarkManager);
        Response getPublicResponse = bookmarkHandler.getPublicBookmarks(authString);
        assertEquals("Get public bookmarks operation should be invalid",
                getPublicResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

}
