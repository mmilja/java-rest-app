package org.example.app.bookmark.bookmarkmanager;

import com.ericsson.adp.bookmark_api.model.BookmarkAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.bookmark.Bookmark;
import org.example.app.bookmark.bookmark.BookmarkLink;
import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark.utils.IUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Class that manages bookmarks.
 */
public final class BookmarkManager implements IBookmarkManager {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(BookmarkManager.class.getSimpleName());

    /**
     * Standard charset encoding assumed for files.
     */
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Singleton instance.
     */
    private static volatile BookmarkManager instance;

    /**
     * Object containing utility methods.
     */
    private final IUtils utils;

    /**
     * Object used for jws token creation.
     */
    private final IJavaJws javaJws;

    /**
     * Map used to store bookmarks for each user.
     */
    private Map<UUID, List<Bookmark>> userBookmarkMap;

    /**
     * Set of all known public bookmarks.
     */
    private Set<BookmarkLink> publicBookmarks;

    /**
     * Private constructor.
     *
     * @param utils object containing utility methods.
     * @param javaJws object used for jws token management.
     */
    private BookmarkManager(final IUtils utils, final IJavaJws javaJws) {
        this.utils = utils;
        this.javaJws = javaJws;

        this.userBookmarkMap = new HashMap<>();
        this.publicBookmarks = new HashSet<>();
    }

    /**
     * Convenience getter.
     *
     * @return IBookmarkManager instance.
     */
    public static IBookmarkManager getInstance() {
        return instance;
    }

    /**
     * Getter for the singleton.
     *
     * @param utils object containing utility methods.
     * @param javaJws object used for jws token management.
     * @return IBookmarkManager instance.
     */
    public static IBookmarkManager getInstance(final IUtils utils, final IJavaJws javaJws) {
        if (instance == null) {
            synchronized (BookmarkManager.class) {
                if (instance == null) {
                    instance = new BookmarkManager(utils, javaJws);
                }
            }
        }
        return instance;
    }

    public BookmarkStatus addBookmark(final com.ericsson.adp.bookmark_api.model.Bookmark bookmark,
                                      final String authString,
                                      final UUID userUuid) {

        if (this.javaJws.authorizeUser(authString, userUuid)) {
            this.addBookmark(bookmark, userUuid);
            return BookmarkStatus.CREATED;
        } else {
            return BookmarkStatus.UNAUTHORIZED;
        }
    }

    private void addBookmark(final com.ericsson.adp.bookmark_api.model.Bookmark bookmark, final UUID userUuid) {
        Bookmark newBookmark = new Bookmark();
        BookmarkLink newBookmarkLink = new BookmarkLink();
        newBookmarkLink.setUri(bookmark.getBookmarkLink().getUri());
        newBookmarkLink.setUriName(bookmark.getBookmarkLink().getName());
        newBookmark.setBookmarkLink(newBookmarkLink);
        newBookmark.setPrivate(bookmark.getAccess() != null && bookmark.getAccess().equals(BookmarkAccess.PRIVATE));

        List<Bookmark> bookmarkList = this.userBookmarkMap.get(userUuid);
        bookmarkList.add(newBookmark);
        this.userBookmarkMap.put(userUuid, bookmarkList);

        if (!newBookmark.isPrivate()) {
            publicBookmarks.add(newBookmarkLink);
        }
    }

    /**
     * Create a new set of public bookmarks from all public bookmarks.
     */
    private void createPublicBookmarkSet() {
        publicBookmarks = new HashSet<>();
        this.userBookmarkMap.forEach((uuid, bookmarks) -> bookmarks.forEach( bookmark -> {
            if (!bookmark.isPrivate()) {
                publicBookmarks.add(bookmark.getBookmarkLink());
            }
        }));
    }

}
