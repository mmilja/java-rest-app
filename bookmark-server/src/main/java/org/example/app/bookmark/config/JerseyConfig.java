package org.example.app.bookmark.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configuration class for Jersey.
 */
public class JerseyConfig extends ResourceConfig {
    /**
     * Specifies where to find resources and registers binder.
     */
    public JerseyConfig() {
        packages("org.example.app.bookmark.rest");
        register(new Binder());
        register(JacksonFeature.class);
    }
}
