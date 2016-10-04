#!/bin/sh

# The default case runs the installer in an X session if it can
# falling back to text if the Graphics environment is not available
# This can be changed by uncommenting the if statement and the default
# can be changed by replacing "text" with "swing" below
# or passing the required option on the command line

GUI=$1
if [ "$GUI" = "" ] ; then
	GUI=default;
fi

# controllo JAVA_HOME
if [ "$JAVA_HOME" = "" ] ; then
	echo "L'installazione richiede l'installazione di java http://java.sun.com"
	echo "Se la jvm e' gia installata provare a settare la variabile JAVA_HOME"
	exit 1;
fi


# try javac first, or we might get the location of the jre instead - djw
#java=`which javac 2>/dev/null || :`


#if [ -z "$java" ] ; then
#    java=`which java 2>/dev/null || :`
#fi

#if [ -n "$java" ] ; then
#  while [ -h "$java" ] ; do
#      java=`readlink $java 2>/dev/null`
#  done
#    JAVA_HOME="`dirname $java`/.."
#    export JAVA_HOME
#fi

ROOT_OPENSPCOOP=../..
export OUTPUTDIR=../..
LIBRARIES=${ROOT_OPENSPCOOP}/lib
ANTINSTALLER_LIBRARIES=${LIBRARIES}/antinstaller

# Installer from command line classpath
CLASSPATH=${LIBRARIES}/shared/xercesImpl-2.11.0.jar
CLASSPATH=${LIBRARIES}/shared/xml-apis-2.11.0.jar
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/xml-apis_antinstaller0.8b.jar
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/ant-installer-0.8b.jar
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/ai-icons-eclipse_antinstaller0.8b.jar

# JGoodies Look And Feel
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/jgoodies-edited-1_2_2.jar

# minimal ANT classpath requirements
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/ant-1.9.7.jar
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/ant-launcher-1.9.7.jar
CLASSPATH=${CLASSPATH}:${ROOT_OPENSPCOOP}/ant/setup/deploy/resources

# minimal regular expression env
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/ant-apache-regexp-1.9.7.jar
CLASSPATH=${CLASSPATH}:${ANTINSTALLER_LIBRARIES}/jakarta-regexp-1.5.jar

COMMAND=$JAVA_HOME/bin/java

$COMMAND -classpath $CLASSPATH  org.tp23.antinstaller.runtime.ExecInstall $GUI ${ROOT_OPENSPCOOP}/ant/setup
