#!/bin/bash

# Find the base directory
pushd `dirname $0` > /dev/null
BASE=`pwd -P`
popd > /dev/null

CLASSPATH=${BASE}/../../lib/itchy.jar:${BASE}/../../lib/jame.jar
MAIN=uk.co.nickthecoder.itchy.tools.ClassDiagram

for NAME in drunkInvaders extras core overview 
do
    HTML=${BASE}/${NAME}.html
    java -classpath "${CLASSPATH}" "${MAIN}" "${HTML}"
    cutycapt "--url=file://${HTML}" "--out=${BASE}/${NAME}.png"
    convert -thumbnail 200x200 "${BASE}/${NAME}.png" "${BASE}/${NAME}-thumbnail.png"
done

