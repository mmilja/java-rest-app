package org.example.app.bookmark.bookmarkmanager;

import com.ericsson.adp.bookmark_api.model.Bookmark;

import java.util.UUID;

/**
 * Interface that describes the bookmark manager.
 */
public interface IBookmarkManager {

    /**
     * Add bookmark to the list of bookmarks for the user.
     *
     * @param bookmark to add.
     * @param authString to authorize the user.
     * @param userUuid to identifz the user.
     * @return status code signaling the success or failure of the operation.
     */
    BookmarkStatus addBookmark(final Bookmark bookmark, final String authString, final UUID userUuid);

}
