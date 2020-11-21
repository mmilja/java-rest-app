package org.example.app.bookmark.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configuration class for Jersey.
 */
public class JerseyConfig extends ResourceConfig {
    /**
     * Specifies where to find resources and registers binder.
     */
    public JerseyConfig() {
        packages("com.ericsson.adp.benchmarkManager.rest");
        register(new Binder());
        register(JacksonFeature.class);
        register(MultiPartFeature.class);
    }
}
