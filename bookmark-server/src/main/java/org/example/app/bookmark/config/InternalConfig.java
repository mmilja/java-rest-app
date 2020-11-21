package org.example.app.bookmark.config;

/**
 * Holds configuration read from the configuration file.
 */
public class InternalConfig {

    /**
     * Root path for the REST API.
     */
    private String rootApiPath;

    /**
     * Port that will be used by the embedded HTTP server in this process.
     */
    private int httpPort;

    /**
     * @return root REST API path
     */
    public final String getRootApiPath() {
        return this.rootApiPath;
    }

    /**
     * @param rootApiPath root REST API path
     */
    public final void setRootApiPath(final String rootApiPath) {
        this.rootApiPath = rootApiPath;
    }

    /**
     * @return port used by the embedded HTTP server.
     */
    public final int getHttpPort() {
        return this.httpPort;
    }

    /**
     * @param httpPort set value.
     */
    public final void setHttpPort(final int httpPort) {
        this.httpPort = httpPort;
    }
}
