package org.example.app.bookmark.bookmark;

import com.ericsson.adp.bookmark_api.model.BookmarkAccess;

/**
 * Class defining the bookmark entity.
 */
public class Bookmark {

    /**
     * Object containing the name and the location of the bookmark link.
     */
    private BookmarkLink bookmarkLink;

    /**
     * Defines if the current bookmark is private or public.
     * Private bookmarks are only visible to users that own them.
     */
    private boolean isPrivate;

    /**
     * Create Bookmark object from api model object.
     *
     * @param apiBookmark object received from the api model.
     */
    public Bookmark(com.ericsson.adp.bookmark_api.model.Bookmark apiBookmark) {
        this.setPrivate(apiBookmark.getAccess() != null && apiBookmark.getAccess().equals(BookmarkAccess.PRIVATE));
        this.setBookmarkLink(new BookmarkLink(apiBookmark.getBookmarkLink()));
    }

    /**
     * Getter for bookmarkLink object.
     *
     * @return bookmark link that belongs to the bookmark.
     */
    public BookmarkLink getBookmarkLink() {
        return bookmarkLink;
    }

    /**
     * Setter for bookmartkLink object.
     *
     * @param bookmarkLink to set as a bookmark link.
     */
    public void setBookmarkLink(BookmarkLink bookmarkLink) {
        this.bookmarkLink = bookmarkLink;
    }

    /**
     * Getter for is private.
     *
     * @return true if bookmark link is private, false otherwise.
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Setter for is private.
     *
     * @param anPrivate to set as a bookmark private value.
     */
    public void setPrivate(boolean anPrivate) {
        isPrivate = anPrivate;
    }
}