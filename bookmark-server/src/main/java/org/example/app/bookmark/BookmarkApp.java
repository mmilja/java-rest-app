package org.example.app.bookmark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.app.bookmark.bookmarkmanager.BookmarkManager;
import org.example.app.bookmark.config.InternalConfig;
import org.example.app.bookmark.exceptions.InitFailedException;
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

        LOGGER.info("Reading data from the environment and the configuration files");
        try {

            internalConfig = new InternalConfig();
            internalConfig.setHttpPort(8080);
            internalConfig.setRootApiPath("/management");

            IJavaJws javaJws = JavaJws.getInstance(utils);
            UserManager.getInstance(utils, javaJws);
            BookmarkManager.getInstance(utils, javaJws);
        } catch (InitFailedException exception) {
            LOGGER.error(exception, exception.getCause());
            System.exit(BookmarkApp.ERROR_EXIT_CODE);
            return;
        }
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
