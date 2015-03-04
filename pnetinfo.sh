#!/bin/bash

environment='dev'

JAR=build/libs/cloudinfo-all.jar

if [ "${1}" != "" ] ; then
    environment="${1}"
fi

if [ ! -f $JAR ]; then
    gradle build
fi

java -jar build/libs/cloudinfo-all.jar "${1}"
