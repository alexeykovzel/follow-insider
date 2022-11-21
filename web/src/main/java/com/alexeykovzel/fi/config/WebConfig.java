package com.alexeykovzel.fi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/signup").setViewName("register");
        registry.addViewController("/profile").setViewName("profile");
        registry.addViewController("/contact").setViewName("contact");
        registry.addViewController("/faq").setViewName("faq");
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        var templates = new ClassLoaderTemplateResolver();
        templates.setPrefix("pages/");
        templates.setSuffix(".html");
        templates.setTemplateMode(TemplateMode.HTML);
        templates.setCharacterEncoding("UTF-8");
        templates.setOrder(1);
        templates.setCheckExistence(true);
        return templates;
    }
}
