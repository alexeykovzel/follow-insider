package com.alexeykovzel.fi;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.elasticbeanstalk.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.s3.assets.Asset;
import software.amazon.awscdk.services.s3.assets.AssetProps;
import software.constructs.Construct;

import java.util.List;

// TODO: Configure load balancer to redirect to HTTPs.
// TODO: Change default version ID.

public class ElasticBeanstalkStack extends Stack {
    private static final String CERTIFICATE_ARN = "arn:aws:acm:eu-central-1:969247426993:certificate/ebe25ab6-8a72-4ad5-96f6-5920cc3ecbd2";
    private static final String ASSET_PATH = System.getProperty("user.dir") + "/build/libs/follow-insider-0.0.1.jar";
    private static final String APPLICATION_NAME = "follow-insider";
    private static final String IAM_PROFILE_ID = "fi-profile";
    private static final String IAM_ROLE_ID = "fi-eb-role";
    private static final String ASSET_ID = "fi-jar-asset";
    private static final String VERSION_ID = "version";
    private static final String APP_ID = "fi-eb-app";
    private static final String ENV_ID = "fi-eb-env";
    private static final int SERVER_PORT = 5000;

    public ElasticBeanstalkStack(Construct scope, String id, StackProps props) {
        super(scope, id);

        // Create a ElasticBeanStalk app.
        var app = new CfnApplication(this, APP_ID, CfnApplicationProps.builder()
                .applicationName(APPLICATION_NAME)
                .build());

        // Construct an S3 asset from the JAR file.
        var asset = new Asset(this, ASSET_ID, AssetProps.builder()
                .path(ASSET_PATH)
                .build());

        // Create an app version from the S3 asset defined earlier
        var version = new CfnApplicationVersion(this, VERSION_ID, CfnApplicationVersionProps.builder()
                .applicationName(APPLICATION_NAME)
                .sourceBundle(CfnApplicationVersion.SourceBundleProperty.builder()
                        .s3Bucket(asset.getS3BucketName())
                        .s3Key(asset.getS3ObjectKey())
                        .build())
                .build());

        // Make sure that Elastic Beanstalk app exists before creating an app version
        version.addDependsOn(app);

        // Create role and instance profile
        var role = new Role(this, IAM_ROLE_ID, RoleProps.builder()
                .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .build());

        IManagedPolicy policy = ManagedPolicy.fromAwsManagedPolicyName("AWSElasticBeanstalkWebTier");
        role.addManagedPolicy(policy);

        var profile = new CfnInstanceProfile(this, IAM_PROFILE_ID, CfnInstanceProfileProps.builder()
                .instanceProfileName(IAM_PROFILE_ID)
                .roles(List.of(role.getRoleName()))
                .build());

        // Option settings of an application environment
        List<CfnEnvironment.OptionSettingProperty> settings = new EnvSettingBuilder()
                .setting("aws:elasticbeanstalk:application:environment", "SERVER_PORT", String.valueOf(SERVER_PORT))
                .setting("aws:autoscaling:launchconfiguration", "IamInstanceProfile", profile.getRef())
                .setting("aws:ec2:instances", "InstanceTypes", "t2.micro")

                /* Load balancer */
                .setting("aws:elasticbeanstalk:environment", "EnvironmentType", "LoadBalanced")
                .setting("aws:elasticbeanstalk:environment", "LoadBalancerType", "application")
                .setting("aws:elbv2:listener:443", "SSLCertificateArns", CERTIFICATE_ARN)
                .setting("aws:elbv2:listener:443", "ListenerEnabled", "true")
                .setting("aws:elbv2:listener:443", "Protocol", "HTTPS")

                /* Autoscaling */
                .setting("aws:autoscaling:asg", "MinSize", "1")
                .setting("aws:autoscaling:asg", "MaxSize", "1")
                .build();

        // Create an Elastic Beanstalk environment to run the application
        var env = new CfnEnvironment(this, ENV_ID, CfnEnvironmentProps.builder()
                .applicationName(app.getApplicationName())
                .environmentName(ENV_ID)
                .solutionStackName("64bit Amazon Linux 2 v3.3.1 running Corretto 11")
                .optionSettings(settings)
                .versionLabel(version.getRef())
                .build());
    }
}
