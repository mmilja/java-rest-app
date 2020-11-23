package org.example.app.bookmark.bookmarkmanager;

import org.example.app.bookmark_api.model.BookmarkAccess;
import org.example.app.bookmark_api.model.BookmarkLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.bookmark.Bookmark;
import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark.utils.IUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Class that manages bookmarks.
 */
public final class BookmarkManager implements IBookmarkManager {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(BookmarkManager.class.getSimpleName());

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
    private final Map<String, List<Bookmark>> userBookmarkMap;

    /**
     * Set of all known public bookmarks.
     */
    private Set<org.example.app.bookmark_api.model.BookmarkLink> publicBookmarks;

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

    @Override
    public Map.Entry<BookmarkStatus, List<org.example.app.bookmark_api.model.Bookmark>>
    getBookmarks(final String authString) {
        String user = this.javaJws.authorizeUser(authString);
        if (!user.isEmpty()){
            if (!this.userBookmarkMap.containsKey(user)) {
                return new AbstractMap.SimpleEntry<>(BookmarkStatus.NOT_FOUND, null);
            }
            List<Bookmark> bookmarkList = this.userBookmarkMap.get(user);
            List<org.example.app.bookmark_api.model.Bookmark> bookmarks = new ArrayList<>();
            bookmarkList.forEach(bookmark -> {
                BookmarkLink bookmarkLink = new BookmarkLink();
                bookmarkLink.setUri(bookmark.getBookmarkLink().getUri());
                bookmarkLink.setName(bookmark.getBookmarkLink().getUriName());

                org.example.app.bookmark_api.model.Bookmark tmpBookmark =
                        new org.example.app.bookmark_api.model.Bookmark();
                tmpBookmark.setBookmarkLink(bookmarkLink);
                tmpBookmark.setAccess(bookmark.isPrivate() ? BookmarkAccess.PRIVATE: BookmarkAccess.PUBLIC);
                bookmarks.add(tmpBookmark);
            });
            return new AbstractMap.SimpleEntry<>(BookmarkStatus.OK, bookmarks);
        } else {
            return new AbstractMap.SimpleEntry<>(BookmarkStatus.UNAUTHORIZED, null);
        }
    }

    @Override
    public BookmarkStatus addBookmark(final org.example.app.bookmark_api.model.Bookmark bookmark,
                                      final String authString) {

        String user = this.javaJws.authorizeUser(authString);
        if (!user.isEmpty()){
            if (!this.userBookmarkMap.containsKey(user)) {
                this.userBookmarkMap.put(user, new ArrayList<>());
            }
            List<Bookmark> bookmarkList = this.userBookmarkMap.get(user);
            if (bookmarkList.stream().anyMatch(tmpBookmark ->
                    tmpBookmark.getBookmarkLink().getUriName().equals(bookmark.getBookmarkLink().getName()))) {
                return BookmarkStatus.BOOKMARK_EXISTS;
            } else {
                bookmarkList.add(new Bookmark(bookmark));
                this.userBookmarkMap.put(user, bookmarkList);
                return BookmarkStatus.CREATED;
            }
        } else {
            return BookmarkStatus.UNAUTHORIZED;
        }
    }

    @Override
    public BookmarkStatus deleteBookmark(final String bookmarkName, final String authString) {
        String user = this.javaJws.authorizeUser(authString);
        if (!user.isEmpty()){
            if (!this.userBookmarkMap.containsKey(user)) {
                return BookmarkStatus.NOT_FOUND;
            }
            List<Bookmark> bookmarkList = this.userBookmarkMap.get(user);

            Optional<Bookmark> bookmarkOptional = bookmarkList.stream().filter(
                    bookmark -> bookmark.getBookmarkLink().getUriName().equals(bookmarkName)).findFirst();
            if (bookmarkOptional.isPresent()) {
                bookmarkList.remove(bookmarkOptional.get());
                this.userBookmarkMap.put(user, bookmarkList);
                return BookmarkStatus.DELETED;
            } else {
                return BookmarkStatus.INVALID_DATA;
            }
        } else {
            return BookmarkStatus.UNAUTHORIZED;
        }
    }

    @Override
    public BookmarkStatus updateBookmark( final String bookmarkName,
            final org.example.app.bookmark_api.model.Bookmark bookmark, final String authString) {

        String user = this.javaJws.authorizeUser(authString);
        if (!user.isEmpty()){
            if (!this.userBookmarkMap.containsKey(user)) {
                return BookmarkStatus.NOT_FOUND;
            }
            List<Bookmark> bookmarkList = this.userBookmarkMap.get(user);

            Optional<Bookmark> bookmarkOptional = bookmarkList.stream().filter(
                    tmpBookamrk -> tmpBookamrk.getBookmarkLink().getUriName().equals(bookmarkName)).findFirst();
            if (bookmarkOptional.isPresent()) {
                bookmarkList.remove(bookmarkOptional.get());
                bookmarkList.add(new Bookmark(bookmark));
                this.userBookmarkMap.put(user, bookmarkList);
                return BookmarkStatus.UPDATED;
            } else {
                return BookmarkStatus.INVALID_DATA;
            }
        } else {
            return BookmarkStatus.UNAUTHORIZED;
        }
    }

    @Override
    public Map.Entry<BookmarkStatus, Set<BookmarkLink>>
    getPublicBookmarks(final String authString) {
        if (!this.javaJws.authorizeUser(authString).isEmpty()) {
            this.createPublicBookmarkSet();
            return new AbstractMap.SimpleEntry<>(BookmarkStatus.OK, this.publicBookmarks);
        }
        return new AbstractMap.SimpleEntry<>(BookmarkStatus.UNAUTHORIZED, null);
    }

    /**
     * Create a new set of public bookmarks from all public bookmarks.
     */
    private void createPublicBookmarkSet() {
        publicBookmarks = new HashSet<>();
        this.userBookmarkMap.forEach((uuid, bookmarks) -> bookmarks.forEach( bookmark -> {
            if (!bookmark.isPrivate()) {
                BookmarkLink bookmarkLink = new BookmarkLink();
                bookmarkLink.setName(bookmark.getBookmarkLink().getUriName());
                bookmarkLink.setUri(bookmark.getBookmarkLink().getUri());
                publicBookmarks.add(bookmarkLink);
            }
        }));
    }

}
