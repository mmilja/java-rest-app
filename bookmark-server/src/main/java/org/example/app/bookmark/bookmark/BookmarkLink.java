package org.example.app.bookmark.bookmark;

import org.example.app.bookmark.exceptions.BadParametersException;

import java.net.URI;

/**
 * Class defining the bookmarkLink object.
 */
public class BookmarkLink {

    /**
     * Location of the bookmark link.
     */
    private URI uri;

    /**
     * Name of the bookmark link.
     */
    private String uriName;

    /**
     * Create BookmarkLink object from api model object.
     *
     * @param apiBookmarkLink object received from the api model.
     */
    public BookmarkLink(org.example.app.bookmark_api.model.BookmarkLink apiBookmarkLink) {
        if (apiBookmarkLink.getName() == null || apiBookmarkLink.getUri() == null) {
            throw new BadParametersException(Bookmark.BAD_BOOKMARK_LINK);
        }

        this.setUriName(apiBookmarkLink.getName());
        this.setUri(apiBookmarkLink.getUri());
    }

    /**
     * Getter for uri value.
     *
     * @return URI of the bookmark link.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Setter for uri value.
     *
     * @param uri to set as a bookmark link location.
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Getter for uri name.
     *
     * @return name of the uri in the bookmark link.
     */
    public String getUriName() {
        return uriName;
    }

    /**
     * Setter for uri name.
     *
     * @param uriName to set as a name of the bookmarked link.
     */
    public void setUriName(String uriName) {
        this.uriName = uriName;
    }
}