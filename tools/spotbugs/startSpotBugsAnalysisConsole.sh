#!/bin/bash

SPOTBUGS_HOME="$1"
if [ -z "${SPOTBUGS_HOME}" ]
then
	echo "ERROR: spotbugs home non fornita"
	echo "usage: ./startSpotBugsAnalysisConsole <SPOTBUGS_HOME>"
	exit 2
fi
if [ ! -e "${SPOTBUGS_HOME}" ]
then
	echo "ERROR: spotbugs home fornita '${SPOTBUGS_HOME}' non esistente"
	echo "usage: ./startSpotBugsAnalysisConsole <SPOTBUGS_HOME>"
	exit 3
fi
if [ ! -d "${SPOTBUGS_HOME}" ]
then
	echo "ERROR: spotbugs home fornita '${SPOTBUGS_HOME}' non è una directory"
	echo "usage: ./startSpotBugsAnalysisConsole <SPOTBUGS_HOME>"
	exit 4
fi
if [ ! -d "${SPOTBUGS_HOME}/lib" ]
then
	echo "ERROR: spotbugs home fornita '${SPOTBUGS_HOME}' corrotta?? lib non trovata"
	echo "usage: ./startSpotBugsAnalysisConsole <SPOTBUGS_HOME>"
	exit 5
fi
java -jar ${SPOTBUGS_HOME}/lib/spotbugs.jar
