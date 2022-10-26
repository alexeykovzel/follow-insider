package com.alexeykovzel.fi;

import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironment;

import java.util.ArrayList;
import java.util.List;

public class EnvSettingBuilder {
    private final List<CfnEnvironment.OptionSettingProperty> settings = new ArrayList<>();

    public EnvSettingBuilder setting(String namespace, String optionName, String value) {
        settings.add(CfnEnvironment.OptionSettingProperty.builder()
                .namespace(namespace)
                .optionName(optionName)
                .value(value)
                .build());
        return this;
    }

    public List<CfnEnvironment.OptionSettingProperty> build() {
        return settings;
    }
}
