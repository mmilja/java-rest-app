package org.example.app.bookmark.utils;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.example.app.bookmark.config.JerseyConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

/**
 * Class with utilities for Bookmark application.
 * Used to isolate references to external packages.
 */
public class Utils implements IUtils {

    /**
     * Timeout for idle connections (in ms).
     */
    private static final int IDLE_CONNECTION_TIMEOUT = 30000;

    /**
     * Number of acceptor threads to be used by the server.
     */
    private static final int ACCEPTOR_THREADS = 1;

    /**
     * Number of selector threads to be used by the server.
     */
    private static final int SELECTOR_THREADS = 2;

    /**
     * Constructor for the utility class.
     * Methods from external packages are wrapped in methods of this class
     * to enable unit testing. Class cannot be static for the same reason.
     */
    public Utils() {
        // Methods from external packages are wrapped in methods of this class
        // to enable unit testing. Class cannot be static for the same reason.
    }

    @Override
    public final Server setupJettyServer(final QueuedThreadPool threadPool, final int port, final String pathMapping) {
        Server server = new Server(threadPool);
        ServerConnector httpConnector = new ServerConnector(server, ACCEPTOR_THREADS,
                SELECTOR_THREADS, new HttpConnectionFactory());
        httpConnector.setPort(port);
        httpConnector.setIdleTimeout(Utils.IDLE_CONNECTION_TIMEOUT);
        server.addConnector(httpConnector);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        server.setHandler(contextHandler);

        ServletHolder servletHolder = contextHandler.addServlet(ServletContainer.class, pathMapping + "/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS,
                JerseyConfig.class.getCanonicalName());

        return server;
    }

    @Override
    public final void startJettyServer(final Server server) throws Exception {
        server.start();
    }

    @Override
    public final void stopJettyServer(final Server server) throws Exception {
        server.stop();
    }
}
