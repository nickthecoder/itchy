#!/bin/bash

# Find the base directory
pushd `dirname $0` > /dev/null
BASE=`pwd -P`
popd > /dev/null

CLASSPATH=${BASE}/../lib/itchy.jar:${BASE}/../lib/jame.jar
MAIN=uk.co.nickthecoder.itchy.tools.ClassDiagram

echo java -classpath "${CLASSPATH}" "${MAIN}" "$@"
java -classpath "${CLASSPATH}" "${MAIN}" "$@"

