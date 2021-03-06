package org.example.app.bookmark.bookmarkmanager;

public enum BookmarkStatus {

    /**
     * Signals correct execution of the task.
     */
    OK(1),

    /**
     * Signals the CREATED status.
     */
    CREATED(2),

    /**
     * Signals the successful registration of the user.
     */
    DELETED(3),

    /**
     * Signals that the bookmark has been updated.
     */
    UPDATED(4),

    /**
     * Signals that the specified bookmark cannot be found.
     */
    NOT_FOUND(5),

    /**
     * Signals that the bookmark already exists.
     */
    BOOKMARK_EXISTS(6),

    /**
     * Signals that the data provided to register the bookmark is invalid.
     */
    INVALID_DATA(7),

    /**
     * Signals that the user executing the operation was unauthorized.
     */
    UNAUTHORIZED(8);

    /**
     * Field containing the status for the current instance.
     */
    private final int bookmarkStatus;

    /**
     * Constructor that binds the signal to the field.
     *
     * @param bookmarkStatus to bind to the field.
     */
    BookmarkStatus(int bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
    }

    /**
     * Get the number that is bound tho the current signal.
     *
     * @return number representing the current signal.
     */
    public int getBookmarkStatus() {
        return this.bookmarkStatus;
    }
}
