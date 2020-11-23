package org.example.app.bookmark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.bookmarkmanager.BookmarkManager;
import org.example.app.bookmark.config.InternalConfig;
import org.example.app.bookmark.httpserver.HttpServer;
import org.example.app.bookmark.javajws.IJavaJws;
import org.example.app.bookmark.javajws.JavaJws;
import org.example.app.bookmark.usermanager.UserManager;
import org.example.app.bookmark.utils.Utils;

/**
 * Class implementing entrypoint and user console.
 */
public class BookmarkApp {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LogManager.getLogger(BookmarkApp.class.getSimpleName());

    /**
     * Error exit code.
     */
    private static final int ERROR_EXIT_CODE = 1;

    /**
     * Private constructor that prevents the default constructor from being created.
     */
    private BookmarkApp() {
    }

    /**
     * Starting class for the application.
     *
     * @param args for additional options.
     */
    public static void main(String[] args) {
        LOGGER.info("Bookmark App starting up");

        Utils utils = new Utils();
        InternalConfig internalConfig;

        internalConfig = new InternalConfig();
        internalConfig.setHttpPort(8080);
        internalConfig.setRootApiPath("/management");

        IJavaJws javaJws = JavaJws.getInstance();
        UserManager.getInstance(javaJws);
        BookmarkManager.getInstance(javaJws);

        try (HttpServer httpServer = new HttpServer(internalConfig, utils)) {
            httpServer.start();
            LOGGER.info("HTTP server started..");
            httpServer.join();
        } catch (InterruptedException exception) {
            LOGGER.error("Interrupted!", exception);
            Thread.currentThread().interrupt();
            System.exit(BookmarkApp.ERROR_EXIT_CODE);
        }
    }
}
