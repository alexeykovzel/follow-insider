#!/bin/sh

# e.g. "sh deploy.sh 0.0.1"

APP_NAME=follow-insider
VERSION=fi-source-$1
ENV_NAME=fi-eb-env

S3_BUCKET=elasticbeanstalk-eu-central-1-969247426993
S3_ASSET=follow-insider-0.0.1.jar

gradle build

aws s3 cp build/libs/$S3_ASSET s3://$S3_BUCKET/

aws elasticbeanstalk create-application-version \
  --application-name $APP_NAME \
  --version-label "$VERSION" \
  --source-bundle S3Bucket=$S3_BUCKET,S3Key=$S3_ASSET

aws elasticbeanstalk update-environment \
  --application-name $APP_NAME \
  --environment-name $ENV_NAME \
  --version-label "$VERSION"
