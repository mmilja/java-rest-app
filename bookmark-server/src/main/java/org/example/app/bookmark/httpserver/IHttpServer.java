package org.example.app.bookmark.httpserver;

import java.io.Closeable;

/**
 * Http server interface, used to start http server.
 */
public interface IHttpServer extends Closeable {

    /**
     * Starts the Http server.
     *
     * @return true if successful, false otherwise.
     */
    boolean start();

    /**
     * @return if server is running, false otherwise.
     */
    boolean isServerRunning();

    /**
     * Pause current thread until HTTP thread stops.
     *
     * @throws InterruptedException in case of an error.
     */
    void join() throws InterruptedException;
}
