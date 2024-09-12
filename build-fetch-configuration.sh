#!/bin/bash
set -e
ENV=$1
CONFIG=`echo "$ENV" | tr '[:upper:]' '[:lower:]'`
aws s3 cp s3://appirio-platform-$CONFIG/application/tc-api-core/$CONFIG/TC.prod.ldap.newv2.keystore TC.prod.ldap.keystore