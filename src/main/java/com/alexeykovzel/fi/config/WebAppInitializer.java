package com.alexeykovzel.fi.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {
    private static final String BASE_PACKAGES_PATH = "com.alexeykovzel.fi";

    @Override
    public void onStartup(ServletContext context) {

        // init application config
        var root = new AnnotationConfigWebApplicationContext();
        root.scan(BASE_PACKAGES_PATH);
        context.addListener(new ContextLoaderListener(root));

        // init dispatcher servlet
        var dispatcher = new DispatcherServlet(new GenericWebApplicationContext());
        var servlet = context.addServlet("mvc", dispatcher);
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
    }
}
