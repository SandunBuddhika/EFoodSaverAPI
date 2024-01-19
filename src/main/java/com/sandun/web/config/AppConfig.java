package com.sandun.web.config;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("com.sandun.web.controllers");
        packages("com.sandun.web.middleware");
        register(MultiPartFeature.class);
        register(DependencyBinder.class);
    }
}
