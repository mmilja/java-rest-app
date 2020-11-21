package org.example.app.bookmark.bookmark;

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
     * Create bookmark link object.
     *
     * @param uri     of the bookmark link.
     * @param uriName name of the uri.
     */
    public BookmarkLink(URI uri, String uriName) {
        this.uri = uri;
        this.uriName = uriName;
    }

    /**
     * Create bookmark link object.
     */
    public BookmarkLink() {
        this.uri = null;
        this.uriName = "";
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
