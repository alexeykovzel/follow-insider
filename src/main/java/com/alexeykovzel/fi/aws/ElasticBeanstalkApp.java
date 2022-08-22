package com.alexeykovzel.fi.aws;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class ElasticBeanstalkApp {

    public static void main(final String[] args) {
        App app = new App();

        // Create CDK stack with default account and region
        new ElasticBeanstalkStack(app, "fi-eb-stack", StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .build());

        // Create CloudFormation changeset
        app.synth();
    }
}
