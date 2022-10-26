package com.alexeykovzel.fi.utils;

import lombok.NonNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class YamlPropertyFactory implements PropertySourceFactory {

    @Override
    @SuppressWarnings("ConstantConditions")
    public @NonNull PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        // retrieve property resource data
        Properties properties = factory.getObject();
        String filename = encodedResource.getResource().getFilename();
        return new PropertiesPropertySource(filename, properties);
    }
}
