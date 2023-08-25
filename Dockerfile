FROM eclipse-temurin:17.0.8_7-jre-alpine

LABEL vendor="Intershop Communications AG"

################
# INSTALL BASH #
################
# in alpine-slime is no bash
RUN apk add --no-cache --upgrade bash

##########################
# INSTALL HELM / KUBECTL #
##########################
# Note: Latest version of kubectl may be found at:
# https://github.com/kubernetes/kubernetes/releases
ENV KUBE_LATEST_VERSION="v1.27.4"
# Note: Latest version of helm may be found at:
# https://github.com/kubernetes/helm/releases
ENV HELM_VERSION="v3.12.3"

LABEL license="Apache License 2.0" \
      vendor="Intershop Communications AG" \
      helm=$HELM_VERSION \
      kubectl=$KUBE_LATEST_VERSION

RUN apk add --no-cache ca-certificates bash git openssh curl \
    && wget -q https://storage.googleapis.com/kubernetes-release/release/${KUBE_LATEST_VERSION}/bin/linux/amd64/kubectl -O /usr/local/bin/kubectl \
    && chmod +x /usr/local/bin/kubectl \
    && wget -q https://get.helm.sh/helm-${HELM_VERSION}-linux-amd64.tar.gz -O - | tar -xzO linux-amd64/helm > /usr/local/bin/helm \
    && chmod +x /usr/local/bin/helm

####################
# INSTALL envsubst #
####################
ENV BUILD_DEPS="gettext"  \
    RUNTIME_DEPS="libintl"

RUN set -x && \
    apk add --update $RUNTIME_DEPS && \
    apk add --virtual build_deps $BUILD_DEPS &&  \
    cp /usr/bin/envsubst /usr/local/bin/envsubst && \
    apk del build_deps

##############
# COPY FILES #
##############
COPY ./build/libs/*.jar /usr/local/java-app/app.jar
#COPY ./build/newrelic_agent/newrelic-agent-*.jar /usr/local/java-app/newrelic-agent.jar
COPY ./docker-scripts/java-entrypoint.sh /usr/local/java-app/java-entrypoint.sh
COPY ./docker-scripts/krew-install.sh /usr/local/krew/krew-install.sh

################
# INSTALL KREW #
################

RUN chmod +x /usr/local/krew/krew-install.sh  \
    && /usr/local/krew/krew-install.sh
ENV PATH="/root/.krew/bin:${PATH}"

############
# RUN JAVA #
############
RUN chmod +x /usr/local/java-app/java-entrypoint.sh
ENTRYPOINT /usr/local/java-app/java-entrypoint.sh
