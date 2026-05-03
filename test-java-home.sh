#!/bin/bash
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
echo "JAVA_HOME is $JAVA_HOME"
mvn -version
