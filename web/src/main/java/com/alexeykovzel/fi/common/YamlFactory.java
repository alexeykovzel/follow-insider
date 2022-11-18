package com.alexeykovzel.fi.common;

import lombok.NonNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class YamlFactory implements PropertySourceFactory {

    @Override
    @SuppressWarnings("ConstantConditions")
    public @NonNull PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        var factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());
        String filename = encodedResource.getResource().getFilename();
        return new PropertiesPropertySource(filename, factory.getObject());
    }
}
