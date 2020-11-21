package org.example.app.bookmark.httpserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.example.app.bookmark.config.InternalConfig;
import org.example.app.bookmark.utils.IUtils;

/**
 * Implements methods used to control an HTTP server.
 */
public class HttpServer implements IHttpServer {

    /**
     * Logger for this instance.
     */
    private static final Logger LOGGER = LogManager.getLogger(HttpServer.class.getSimpleName());

    /**
     * Minimum number of threads to be used by the server.
     */
    private static final int MIN_THREADS = 4;

    /**
     * Maximum number of threads to be used by the server.
     */
    private static final int MAX_THREADS = 10;

    /**
     * Object with utility methods.
     */
    private final IUtils utils;

    /**
     * Jetty server object.
     */
    private final Server server;

    /**
     * Flag indicating whether the server is running.
     */
    private boolean serverRunning;

    /**
     * Constructor.
     *
     * @param internalConfig hold internal configuration
     * @param utils          object with utility methods
     */
    public HttpServer(final InternalConfig internalConfig, final IUtils utils) {
        LOGGER.debug("HTTP server will be started on port {}", internalConfig.getHttpPort());
        QueuedThreadPool threadPool = new QueuedThreadPool(HttpServer.MAX_THREADS, HttpServer.MIN_THREADS);
        this.utils = utils;
        this.server = this.utils.setupJettyServer(
                threadPool, internalConfig.getHttpPort(), internalConfig.getRootApiPath());
        this.serverRunning = false;
    }

    @Override
    public final boolean isServerRunning() {
        return this.serverRunning;
    }

    @Override
    public final boolean start() {
        boolean result;

        try {
            LOGGER.info("Starting the HTTP server..");
            this.utils.startJettyServer(this.server);
            this.serverRunning = true;
            result = true;
        } catch (Exception exception) {
            LOGGER.error("An error occurred while starting the HTTP server: ", exception);
            result = false;
        }
        return result;
    }

    @Override
    public final void join() throws InterruptedException {
        this.server.join();
    }

    @Override
    public final void close() {
        try {
            this.utils.stopJettyServer(this.server);
            this.serverRunning = false;
        } catch (Exception exception) {
            LOGGER.warn("An error occurred while stopping the HTTP server: ", exception);
        }
    }
}
