#!/bin/sh

DEFAULT_JVM_OPTS=""

exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
