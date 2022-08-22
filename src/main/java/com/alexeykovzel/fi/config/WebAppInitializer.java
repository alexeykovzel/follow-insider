package com.alexeykovzel.fi.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext context) {

        // init application config
        var root = new AnnotationConfigWebApplicationContext();
        root.scan("com.alexeykovzel.insidr");
        context.addListener(new ContextLoaderListener(root));

        // init dispatcher servlet
        ServletRegistration.Dynamic servlet = context.addServlet("mvc",
                new DispatcherServlet(new GenericWebApplicationContext()));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
