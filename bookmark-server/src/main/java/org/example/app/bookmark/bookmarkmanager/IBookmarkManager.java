package org.example.app.bookmark.bookmarkmanager;

import org.example.app.bookmark_api.model.Bookmark;
import org.example.app.bookmark_api.model.BookmarkLink;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface that describes the bookmark manager.
 */
public interface IBookmarkManager {

    /**
     * Get all bookmarks for authorized user.
     *
     * @param authString to authorize the user.
     * @return status code signaling the success or failure of the operation
     *         and a list of user bookmarks if successful.
     */
    Map.Entry<BookmarkStatus, List<Bookmark>>
    getBookmarks(final String authString);

    /**
     * Add bookmark to the list of bookmarks for the user.
     *
     * @param bookmark   to add.
     * @param authString to authorize the user.
     * @return status code signaling the success or failure of the operation.
     */
    BookmarkStatus addBookmark(final Bookmark bookmark, final String authString);

    /**
     * Add bookmark to the list of bookmarks for the user.
     *
     * @param bookmarkName to delete.
     * @param authString   to authorize the user.
     * @return status code signaling the success or failure of the operation.
     */
    BookmarkStatus deleteBookmark(final String bookmarkName, final String authString);

    /**
     * Add bookmark to the list of bookmarks for the user.
     *
     * @param bookmarkName to delete.
     * @param bookmark     object containing the updated fields.
     * @param authString   to authorize the user.
     * @return status code signaling the success or failure of the operation.
     */
    BookmarkStatus updateBookmark(final String bookmarkName, final Bookmark bookmark, final String authString);

    /**
     * Get all public bookmarks.
     *
     * @param authString to authorize the user.
     * @return status code signaling the success or failure of the operation
     *         and a list of all public bookmarks if successful.
     */
    Map.Entry<BookmarkStatus, Set<BookmarkLink>>
    getPublicBookmarks(final String authString);

}
