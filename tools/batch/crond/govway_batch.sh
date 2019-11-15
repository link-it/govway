#!/bin/bash

# JAVA_HOME
#export JAVA_HOME=/opt/local/data/jdk1.8.0_222/
[ -d "$JAVA_HOME" ] || { echo "La directory JAVA_HOME [${JAVA_HOME}] non esiste"; exit 5; }
export PATH=${JAVA_HOME}/bin:${PATH}

# Batch Directory
[ -z "$1" ] && { echo "Indicare la directory di esecuzione del batch"; exit 1; }
BATCH_DIRECTORY="$1"
[ -d "$BATCH_DIRECTORY" ] || { echo "La directory di esecuzione del batch non esiste"; exit 2; }

# Batch Command
[ -z "$2" ] && { echo "Indicare lo script sh da eseguire"; exit 1; }
BATCH_COMMAND="$2"
[ -x "$BATCH_DIRECTORY/$BATCH_COMMAND" ] || { echo "Il comando [$BATCH_DIRECTORY/${BATCH_COMMAND}] non possiede i diritti di esecuzione"; exit 6; }

# Batch Tipo
BATCH_TIPO=$(basename ${BATCH_DIRECTORY})

# PID File
PID_FILE="/var/govway/log/govway_batch_$BATCH_TIPO.pid"
mkdir -p /var/govway/log

# LOG DIR
LOG_DIR="/var/govway/log/cron"
mkdir -p ${LOG_DIR}
LOG_FILE="${LOG_DIR}/debug-$BATCH_TIPO-$(date +'%s').log"

# DEBUG
DEBUG=false
if [ ! -z "$3" ]
then
	DEBUG="$3"
fi
if [ "$DEBUG" == "true" ] 
then
	[ -d ${LOG_DIR} ] || mkdir -p ${LOG_DIR}
	>$LOG_FILE
	exec 2> $LOG_FILE
	set -x
fi


# ESISTE PID FILE
if [ -f $PID_FILE ]
then
	# Make a copy of pidfile to assure the file existence (it will be used in a "cat" command !!)
	COPY_PIDFILE="/tmp/$(basename $PID_FILE)"
	/bin/cp -f $PID_FILE $COPY_PIDFILE
	EXISTING_PID=$(cat $COPY_PIDFILE)
	REAL_PROCESS=$(ps ww -p $EXISTING_PID --no-heading 2> /dev/null)
	if [ -z "$REAL_PROCESS" ]
	then
		# The process has crashed or ended in the meantime
		/bin/rm -f $PID_FILE $COPY_PIDFILE
	else
		if echo  "$REAL_PROCESS"|grep -q $BATCH_COMMAND
		then
			# Process still running
			echo "Processo ancora in esecuzione"
			exit 0			
		else
			# Process has crashed and another process is using the same pid
			/bin/rm -f $PID_FILE $COPY_PIDFILE
		fi
	fi
else
	# Assure pidfile path existence
	mkdir -p $(dirname $PID_FILE)
fi


### Main ###
echo $$ > $PID_FILE
cd $BATCH_DIRECTORY
if [ -n "$DEBUG" ]
then
	./$BATCH_COMMAND 1>&2
else
	./$BATCH_COMMAND > /dev/null
fi	
/bin/rm -f $PID_FILE


