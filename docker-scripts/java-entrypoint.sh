#!/usr/bin/env sh

# called as entrypoint of docker alpine-openjdk container, used to run microservice
set -eux pipefail

HOSTNAME="127.0.0.1"

PLUS="-XX:+UseStringDeduplication"

NON_HEAP_MEMORY="-XX:NativeMemoryTracking=detail \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/data/heapdump/iste-kubernetes.hprof \
-XX:ThreadStackSize=${JAVA_THREADSTACK_SIZE_MB:-512}"

HEAP_MEMORY="-XX:MaxRAMPercentage=${JAVA_RAM_PERCENTAGE:-60} -XX:InitialRAMPercentage=${JAVA_RAM_PERCENTAGE:-60}"

JMX="-Dcom.sun.management.jmxremote=true \
 -Dcom.sun.management.jmxremote.rmi.port=8082 \
 -Dcom.sun.management.jmxremote.port=8082 \
 -Dcom.sun.management.jmxremote.local.only=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.ssl=false \
 -Djava.rmi.server.hostname=$HOSTNAME \
 -agentlib:jdwp=transport=dt_socket,address=*:8081,server=y,suspend=n"

SPRING_OPT="-Dspring.cloud.bootstrap.enabled=false"
if [ -n "${SPRING_PROFILE:-}" ]
then
  SPRING_OPT="$SPRING_OPT -Dspring.cloud.config.profile=${SPRING_PROFILE} \
   -Dspring.profiles.active=${SPRING_PROFILE}"
fi

JAR_FOLDER="/usr/local/java-app"

JAR_OPT="-Dserver.port=${PORT:-8080} \
 -Djava.security.egd=file:/dev/./urandom"

#NEWRELIC_OPT="-javaagent:newrelic-agent.jar"

if [ "${JSON_LOGGING:-}" == "true" ]
then
  JAR_OPT="-Dlogging.config=${JAR_FOLDER}/logback.xml ${JAR_OPT}"
fi

cd ${JAR_FOLDER}
JAR_PATH="app.jar"

if [ "${DEBUGGING:-}" == "true" ]
then
  # order matters!
  java ${PLUS} ${HEAP_MEMORY} ${NON_HEAP_MEMORY} ${JMX} ${SPRING_OPT} ${JAR_OPT} -jar ${JAR_PATH}
else
  # order matters!
  java ${PLUS} ${HEAP_MEMORY} ${SPRING_OPT} ${JAR_OPT} -jar ${JAR_PATH}
fi
