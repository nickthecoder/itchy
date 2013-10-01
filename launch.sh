#!/bin/bash

NATIVE_DIR=lib/native/`uname -m`
CLASSPATH=lib/itchy.jar:lib/jame.jar
MAIN=uk.co.nickthecoder.itchy.Launcher

java  -Djava.library.path=${NATIVE_DIR} -classpath "${CLASSPATH}" "${MAIN}" "$@"

