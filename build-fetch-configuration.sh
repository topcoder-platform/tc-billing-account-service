#!/bin/bash
set -e
ENV=$1
CONFIG=`echo "$ENV" | tr '[:upper:]' '[:lower:]'`

if [ "$CONFIG" = "prod" ]; then
    aws s3 cp s3://appirio-platform-$CONFIG/application/tc-api-core/$CONFIG/TC.prod.ldap.sept2024.keystore TC.prod.ldap.keystore
fi
if [ "$CONFIG" = "dev" ]; then
    aws s3 cp s3://appirio-platform-$CONFIG/application/tc-api-core/$CONFIG/TC.prod.ldap.newv2.keystore TC.prod.ldap.keystore
fi