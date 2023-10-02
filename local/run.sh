export IP="127.0.0.1"
export DOCKER_IP="$IP"

export TIME_OLTP_PW="1nf0rm1x"
export TIME_OLTP_USER="informix"
export TIME_OLTP_URL="jdbc:informix-sqli://$DOCKER_IP:2021/time_oltp:INFORMIXSERVER=informixoltp_tcp;IFX_LOCK_MODE_WAIT=5;OPTCOMPIND=0;STMT_CACHE=1;"
export TC_JWT_KEY="secret"

#export VALID_ISSUERS='["https://testsachin.topcoder-dev.com/","https://test-sachin-rs256.auth0.com/","https://api.topcoder.com","https://api.topcoder-dev.com","https://topcoder-dev.auth0.com/", "https://auth.topcoder-dev.com/"]'

export PUBLISHER_LAMBDA_FUNCTION="arn:aws:lambda:us-east-1:811668436784:function:harmony-api-dev-processMessage"

java -jar ../service/target/billing-account-*.jar server ../service/src/main/resources/billing-account.yaml
