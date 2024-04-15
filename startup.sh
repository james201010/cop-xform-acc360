#!/usr/bin/env bash



JAVA_OPTS="$JAVA_OPTS -Xms64m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true"
JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=/log4j.properties"

java $JAVA_OPTS -jar /otlp-receiver-0.0.1.war