package org.example.app.bookmark.config;

import org.example.app.bookmark.bookmarkmanager.BookmarkManager;
import org.example.app.bookmark.bookmarkmanager.IBookmarkManager;
import org.glassfish.jersey.internal.inject.AbstractBinder;

/**
 * Defines which objects will be used for Jersey dependency injection.
 */
public class Binder extends AbstractBinder {
    @Override
    protected final void configure() {
        // Injects singletons into REST resources
        bind(BookmarkManager.getInstance()).to(IBookmarkManager.class);
    }
}