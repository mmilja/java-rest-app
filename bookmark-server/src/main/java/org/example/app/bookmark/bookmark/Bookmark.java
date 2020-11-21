package org.example.app.bookmark.bookmark;

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
     * Bookmarks are considered public unless otherwise specified.
     *
     * @param bookmarkLink that will belong to the bookmark.
     */
    public Bookmark(BookmarkLink bookmarkLink) {
        this(bookmarkLink, false);
    }

    /**
     * Creates bookmark object.
     *
     * @param bookmarkLink that will belong to the bookmark.
     * @param isPrivate    if bookmark is private or public.
     */
    public Bookmark(BookmarkLink bookmarkLink, boolean isPrivate) {
        this.bookmarkLink = bookmarkLink;
        this.isPrivate = isPrivate;
    }

    /**
     * Creates bookmark object.
     */
    public Bookmark() {
        this.bookmarkLink = null;
        this.isPrivate = false;
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