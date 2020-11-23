package org.example.app.bookmark.bookmarkmanager;

import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark_api.model.Bookmark;
import org.example.app.bookmark_api.model.BookmarkAccess;
import org.example.app.bookmark_api.model.BookmarkLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.example.app.bookmark.testutils.TestUtils.printTestFooter;
import static org.example.app.bookmark.testutils.TestUtils.printTestHeader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookmarkManagerTest {

    @Rule
    public TestName testName = new TestName();

    private IBookmarkManager bookmarkManager;
    private IJavaJws javaJws;

    private void resetFields() throws IllegalAccessException {
        for (Field field : BookmarkManager.class.getDeclaredFields()) {
            field.setAccessible(true);
            //Required for testing singleton
            //Has to be up to date with constructor
            switch (field.getName()) {
                case "publicBookmarks":
                    field.set(bookmarkManager, null);
                    break;
                case "userBookmarkMap":
                    field.set(bookmarkManager, null);
                    break;
                case "javaJws":
                    field.set(bookmarkManager, null);
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


    private IBookmarkManager getBookmarkManagerInstance() {
        return BookmarkManager.getInstance(this.javaJws);
    }

    @Test
    public void testGetInstance() {
        assertNull("Instance should not have been created!", BookmarkManager.getInstance());

        this.bookmarkManager = this.getBookmarkManagerInstance();
        assertNotNull("Instance should not be null", this.bookmarkManager);
        int objectHash = this.bookmarkManager.hashCode();

        IBookmarkManager bookmarkManager1 = BookmarkManager.getInstance();
        assertNotNull("Instance should not be null", bookmarkManager1);
        assertEquals("Hash  codes do not match!", objectHash, bookmarkManager1.hashCode());

        IBookmarkManager bookmarkManager2 = this.getBookmarkManagerInstance();
        assertNotNull("Instance should not be null", bookmarkManager2);
        assertEquals("Hash  codes do not match!", objectHash, bookmarkManager2.hashCode());
    }

    @Test
    public void testAddBookmark() throws URISyntaxException {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("name");
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus bookmarkStatus = this.bookmarkManager.addBookmark(bookmark, authString);
        assertEquals("Add bookmark should succeed!", bookmarkStatus.getBookmarkStatus(), BookmarkStatus.CREATED.getBookmarkStatus());
    }

    @Test
    public void testAddBookmarkAlreadyExists() throws URISyntaxException {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("name");
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        BookmarkStatus bookmarkStatus = this.bookmarkManager.addBookmark(bookmark, authString);
        assertEquals("Add bookmark should not succeed!", bookmarkStatus.getBookmarkStatus(), BookmarkStatus.BOOKMARK_EXISTS.getBookmarkStatus());
    }


    @Test
    public void testAddBookmarkBadBookmarkObject() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus bookmarkStatus = this.bookmarkManager.addBookmark(bookmark, authString);
        assertEquals("Add bookmark should not succeed!", bookmarkStatus, BookmarkStatus.INVALID_DATA);
    }

    @Test
    public void testAddBookmarkUnauthorized() {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();

        when(this.javaJws.authorizeUser(authString)).thenReturn("");

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus bookmarkStatus = this.bookmarkManager.addBookmark(bookmark, authString);
        assertEquals("Add bookmark should not succeed!", bookmarkStatus, BookmarkStatus.UNAUTHORIZED);
    }



    @Test
    public void testGetBookmarksEmpty() {
        String authString = "authString";

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        Map.Entry<BookmarkStatus, List<org.example.app.bookmark_api.model.Bookmark>> entry =
                this.bookmarkManager.getBookmarks(authString);
        assertEquals("Get bookmark should succeed!", entry.getKey(), BookmarkStatus.OK);
        assertEquals("List of bookmarks should be empty!", entry.getValue().size(), 0);
    }

    @Test
    public void testGetBookmarks() throws URISyntaxException {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("name");
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        Map.Entry<BookmarkStatus, List<Bookmark>> entry = this.bookmarkManager.getBookmarks(authString);
        assertEquals("Get bookmark should succeed!", entry.getKey(), BookmarkStatus.OK);
        assertEquals("List of bookmarks should be empty!", entry.getValue().size(), 1);
    }

    @Test
    public void testGetBookmarksUnauthorized() {
        String authString = "authString";

        when(this.javaJws.authorizeUser(authString)).thenReturn("");

        this.bookmarkManager = this.getBookmarkManagerInstance();
        Map.Entry<BookmarkStatus, List<Bookmark>> entry = this.bookmarkManager.getBookmarks(authString);
        assertEquals("Get bookmark should not succeed!", entry.getKey(), BookmarkStatus.UNAUTHORIZED);
    }

    @Test
    public void testDeleteBookmark() throws URISyntaxException {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName(bookmarkName);
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        BookmarkStatus deleteStatus = this.bookmarkManager.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete bookmark should succeed!", deleteStatus, BookmarkStatus.DELETED);
    }

    @Test
    public void testDeleteBookmarkInvalidData() throws URISyntaxException {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName(bookmarkName);
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        BookmarkStatus deleteStatus = this.bookmarkManager.deleteBookmark(bookmarkName + "2", authString);
        assertEquals("Delete bookmark should not succeed!", deleteStatus, BookmarkStatus.INVALID_DATA);
    }

    @Test
    public void testDeleteBookmarkNotFound() {
        String authString = "authString";
        String bookmarkName = "name";

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus deleteStatus = this.bookmarkManager.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete bookmark should not succeed!", deleteStatus, BookmarkStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteBookmarkUnauthorized() {
        String authString = "authString";
        String bookmarkName = "name";

        when(this.javaJws.authorizeUser(authString)).thenReturn("");

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus deleteStatus = this.bookmarkManager.deleteBookmark(bookmarkName, authString);
        assertEquals("Delete bookmark should not succeed!", deleteStatus, BookmarkStatus.UNAUTHORIZED);
    }

    @Test
    public void testUpdateBookmark() throws URISyntaxException {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName(bookmarkName);
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);
        Bookmark bookmark1 = new Bookmark();
        bookmark1.setBookmarkLink(bookmarkLink);
        bookmark1.setAccess(BookmarkAccess.PRIVATE);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        BookmarkStatus deleteStatus = this.bookmarkManager.updateBookmark(bookmarkName, bookmark1, authString);
        assertEquals("Update bookmark should succeed!", deleteStatus, BookmarkStatus.UPDATED);
    }

    @Test
    public void testUpdateBookmarkBadBookmark() throws URISyntaxException {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName(bookmarkName);
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);
        Bookmark bookmark1 = new Bookmark();

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        BookmarkStatus updateStatus = this.bookmarkManager.updateBookmark(bookmarkName, bookmark1, authString);
        assertEquals("Update bookmark should not succeed!", updateStatus, BookmarkStatus.INVALID_DATA);
    }

    @Test
    public void testUpdateBookmarkBadName() throws URISyntaxException {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName(bookmarkName);
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);
        Bookmark bookmark1 = new Bookmark();

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        BookmarkStatus updateStatus = this.bookmarkManager.updateBookmark(bookmarkName + "2", bookmark1, authString);
        assertEquals("Update bookmark should not succeed!", updateStatus, BookmarkStatus.INVALID_DATA);
    }

    @Test
    public void testUpdateBookmarkNotPresent() {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();


        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus updateStatus = this.bookmarkManager.updateBookmark(bookmarkName, bookmark, authString);
        assertEquals("Update bookmark should not succeed!", updateStatus, BookmarkStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateBookmarkUnauthorized() {
        String authString = "authString";
        String bookmarkName = "name";
        Bookmark bookmark = new Bookmark();

        when(this.javaJws.authorizeUser(authString)).thenReturn("");

        this.bookmarkManager = this.getBookmarkManagerInstance();
        BookmarkStatus updateStatus = this.bookmarkManager.updateBookmark(bookmarkName, bookmark, authString);
        assertEquals("Update bookmark should not succeed!", updateStatus, BookmarkStatus.UNAUTHORIZED);
    }

    @Test
    public void testGetPublicBookmarks() {
        String authString = "authString";

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> entry = this.bookmarkManager.getPublicBookmarks(authString);
        assertEquals("Get public bookmark should succeed!", entry.getKey(), BookmarkStatus.OK);
        assertEquals("No bookmarks should exist!", entry.getValue().size(), 0);
    }

    @Test
    public void testGetPublicBookmarksWithBookmarks() throws URISyntaxException {
        String authString = "authString";
        Bookmark bookmark = new Bookmark();
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setName("name");
        bookmarkLink.setUri(new URI("uri"));
        bookmark.setBookmarkLink(bookmarkLink);

        when(this.javaJws.authorizeUser(authString)).thenReturn(authString);

        this.bookmarkManager = this.getBookmarkManagerInstance();
        this.bookmarkManager.addBookmark(bookmark, authString);
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> entry = this.bookmarkManager.getPublicBookmarks(authString);
        assertEquals("Get public bookmark should succeed!", entry.getKey(), BookmarkStatus.OK);
        assertEquals("1 bookmarks should exist!", entry.getValue().size(), 1);
    }

    @Test
    public void testGetPublicBookmarksUnauthorized() {
        String authString = "authString";

        when(this.javaJws.authorizeUser(authString)).thenReturn("");

        this.bookmarkManager = this.getBookmarkManagerInstance();
        Map.Entry<BookmarkStatus, Set<BookmarkLink>> entry = this.bookmarkManager.getPublicBookmarks(authString);
        assertEquals("Get public bookmark should not succeed!", entry.getKey(), BookmarkStatus.UNAUTHORIZED);
    }

}
