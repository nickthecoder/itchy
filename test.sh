#!/bin/bash

NATIVE_DIR=native/`uname -m`
CLASSPATH=itchy.jar:lib/jame.jar:lib/bsh.jar:lib/jdom.jar:lib/junit.jar
MAIN=org.junit.runner.JUnitCore

CLASS=uk.co.nickthecoder.itchy.test.NeighbourhoodTest

java  -Djava.library.path=${NATIVE_DIR} -classpath "${CLASSPATH}" "${MAIN}" "$CLASS"
#java  -Djava.library.path=${NATIVE_DIR} -classpath "${CLASSPATH}" uk.co.nickthecoder.drunkinvaders.DrunkInvaders
#java  -Djava.library.path=${NATIVE_DIR} -classpath "${CLASSPATH}" ${CLASS}
#java  -Djava.library.path=${NATIVE_DIR} -classpath "${CLASSPATH}" org.junit.runner.JUnitCore uk.co.nickthecoder.itchy.test.NeighbourhoodTest

