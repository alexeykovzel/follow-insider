package com.alexeykovzel.fi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Collections;
import java.util.List;

@Configuration
public class FaviconConfig {
    private static final String FAVICON_PATH = "static/images/favicon.ico";

    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        // set the highest priority for loading a favicon
        mapping.setOrder(Integer.MIN_VALUE);
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        var requestHandler = new ResourceHttpRequestHandler();
        var classPathResource = new ClassPathResource(FAVICON_PATH);
        requestHandler.setLocations(List.of(classPathResource));
        return requestHandler;
    }
}
