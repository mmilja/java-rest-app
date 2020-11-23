package org.example.app.bookmark.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * Defines utility methods to be used in other classes. Used to isolate references
 * to external packages and enable unit testing.
 */
public interface IUtils {


    /**
     * Setup properties for HTTP server.
     *
     * @param threadPool  for the server
     * @param port        for the server
     * @param pathMapping path mapping for the servlet
     * @return Server object
     * @see org.eclipse.jetty.server.Server
     * Creates and sets-up Jetty server that will use the provided thread pool
     */
    Server setupJettyServer(final QueuedThreadPool threadPool, final int port, final String pathMapping);

    /**
     * Starts the provided Jetty server.
     *
     * @param server to be started
     * @throws Exception if unsuccessful
     * @see org.eclipse.jetty.server.Server#start()
     */
    void startJettyServer(final Server server) throws Exception;

    /**
     * Stops the provided Jetty server.
     *
     * @param server to be stopped
     * @throws Exception if unsuccessful
     * @see org.eclipse.jetty.server.Server#stop()
     */
    void stopJettyServer(final Server server) throws Exception;

}