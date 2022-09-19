package com.alexeykovzel.fi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

//@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
        registry.addViewController("/faq").setViewName("/faq");
        registry.addViewController("/contacts").setViewName("/contact");
        registry.addViewController("/stocks/**").setViewName("/stock");
    }
}
