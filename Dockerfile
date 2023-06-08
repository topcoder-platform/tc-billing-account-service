FROM openjdk:8u102

LABEL description="tc-billing-account"

# copy jar
COPY ./service/target/billing-account-*.jar billing-account.jar
# copy config - do we need to masssage yaml based on ENV?
COPY ./service/target/classes/billing-account.yaml billing-account.yaml

VOLUME ["/var/log/"]

EXPOSE 8080

# Command line config should ideally be replaced with environment based options
CMD java -Ddw.authDomain=$AUTH_DOMAIN -jar billing-account.jar server billing-account.yaml
