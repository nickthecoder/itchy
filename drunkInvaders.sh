#!/bin/bash

NATIVE_DIR=native/`uname -m`
CLASSPATH=itchy.jar:lib/jame.jar:lib/bsh.jar:lib/jdom.jar
MAIN=uk.co.nickthecoder.drunkinvaders.DrunkInvaders


java  -Djava.library.path=${NATIVE_DIR} -classpath "${CLASSPATH}" "${MAIN}" "$@"

