##############################################################
################### Variabili globali ########################
##############################################################
APPENDERS="${APPENDERS}"
THRESHOLD="${THRESHOLD}"
FORMAT="${FORMAT}"
LEVEL="${LEVEL}"
LOG4BASH_CONFIGURED="${LOG4BASH_CONFIGURED}"

ALL_PROPERTIES="${ALL_PROPERTIES}"
##############################################################
########################## Core ##############################
##############################################################

function PropertyConfigurator
{

[ "$LOG4BASH_CONFIGURED" = "TRUE" ] && return

local LINE=""
local LINETEMP=""
local PROPERTY=""
local VALUE=""
local OBJECT=""
local APPENDERS_CORRECT=""
local APPENDER_TYPE=""
local APPENDER_PROPERTY=""
local PROPERTIES_FILE=""

if [ -n "$2" ]
then
	PREFIX="$2"
	if [ "$1" != "-" ] 
	then
		SEARCH_PROP_LIST="$1  $PREFIX/$1 log4bash-lib/$1 ./log4bash.properties $PREFIX/log4bash.properties log4bash-lib/log4bash.properties"
	else
		SEARCH_PROP_LIST="./log4bash.properties $PREFIX/log4bash.properties log4bash-lib/log4bash.properties"
	fi
	SEARCH_LIB_LIST="./ $PREFIX/ ./log4bash-lib/ $PREFIX/log4bash-lib/" 
else
	if [ "$1" != "-" ]
        then
		SEARCH_PROP_LIST="$1 log4bash-lib/$1 ./log4bash.properties log4bash-lib/log4bash.properties"
	else
		SEARCH_PROP_LIST="./log4bash.properties log4bash-lib/log4bash.properties"	
	fi
	SEARCH_LIB_LIST="./ log4bash-lib/" 
fi
for p in $SEARCH_PROP_LIST
do
	if [ -s "$p" -a -f "$p" -a -r "$p" ]
	then
		PROPERTIES_FILE="$p"
		break
	fi
done

if [ -z "$PROPERTIES_FILE" ]
then
        THRESHOLD="INFO"
        APPENDERS="ConsoleAppender"
        FORMAT="%L <%D>"
else
        exec 6<> $PROPERTIES_FILE
        while read LINE <&6
        do
                [ -z "$LINE" ]&& continue

                LINETEMP=${LINE// /}
                [ "${LINETEMP:0:1}" == "#" ]&& continue

                PROPERTY=$(expr match "$LINETEMP" 'log4bash\.\(.*\)=.*')
                if [ -z "$PROPERTY" ] 
                then
                        THRESHOLD="INFO"
                        APPENDERS="ConsoleAppender"
                        FORMAT="%L <%D>"
                        echo "Syntax Error in $1 line $LINE. Resetting to defaults"
                        return
                fi

                VALUE=${LINE##*=}
                case $PROPERTY in
                THRESHOLD) #Soglia di default
                        THRESHOLD="$VALUE"
                        ;;
                FORMAT) #Formato di output di default
                        FORMAT="$VALUE"
                        ;;
                *) OBJECT=${PROPERTY%.*}
                        case $OBJECT in
                        appender) #Nuovo appender 
                                APPENDERS="$VALUE $APPENDERS"
                                ;;
                        *) #Proprieta di un appender esistente
                                APPENDER_TYPE=${OBJECT%%.*}
                                APPENDER_PROPERTY=${PROPERTY##*.}
				ALL_PROPERTIES="${APPENDER_TYPE}_${APPENDER_PROPERTY}=$VALUE $ALL_PROPERTIES"
                                ;;
                        esac
                        ;;
                esac
        done
        exec 6>& -
fi
FOUND="false"

[ -z "$APPENDERS" ] &&  APPENDERS="ColorConsoleAppender"

for appender in $APPENDERS
do
	for prefix in $SEARCH_LIB_LIST
	do
		[ -f $prefix/log4bash-${appender//Appender/}.sh ] && source $prefix/log4bash-${appender//Appender/}.sh && FOUND=true
	done

	if [ "$FOUND" == "true" ] 
	then
		APPENDERS_CORRECT="$APPENDERS_CORRECT $appender"
		FOUND="false"
	else
		echo "$appender isn't a valid appender. Review you config file."
	fi
done
APPENDER="$APPENDERS_CORRECT"
[ -z "$APPENDERS" ] &&	echo "No valid appenders found. Please specify your log4bash-lib/ directory"
[ -z "$THRESHOLD" ] &&  THRESHOLD="INFO"
[ -z "$FORMAT" ] &&  FORMAT="%L <%D>"

LOG4BASH_CONFIGURED=TRUE
#FORMAT=

#log4bash.THRESHOLD=ERROR
#log4bash.FORMAT="%L <%D>"
#log4bash.appender=FileAppender
#log4bash.FileAppender.LOGFILE=/var/log/pippo.log
#log4bash.FileAppender.THRESHOLD=DEBUG


#log4bash.appender=ConsoleAppender
#log4bash.ConsoleAppneder.THRESHOLD=INFO

}

function isLoggable()
{
	local ORDERED_LEVELS="ALL DEBUG INFO WARN ERROR"
	local Threshold_to_verify="$1"

	[ -z "$Threshold_to_verify" -o "$Threshold_to_verify" == "OFF" ] && return 1
        EXCLUDED_LEVELS=${ORDERED_LEVELS%%$Threshold_to_verify*}
        for l in $EXCLUDED_LEVELS
        do
                [ "${l// /}" = "$LEVEL" ] && return 1
        done
	return 0
}

function Log()
{
        #appender <Messaggio> <A Capo> 
        for appender in $APPENDERS
        do
                eval "$appender \"$1\" $2"
        done
}

##############################################################
###################### Interfaccia ###########################
##############################################################


function warningPrint()
{
	if [[ $- =~ x ]] 
	then
		MENOX=true
		set +x
	fi
        LEVEL="WARN"
        local MESSAGE="$1"
        Log "$MESSAGE" 0
	[ -n "$MENOX" ] && set -x
}

function warningPrintln()
{
        if [[ $- =~ x ]]
        then
                MENOX=true
                set +x
        fi
        LEVEL="WARN"
        local MESSAGE="$1" 
        Log "$MESSAGE" 1 

        [ -n "$MENOX" ] && set -x
}

function errorPrint()
{
        if [[ $- =~ x ]]
        then
                MENOX=true
                set +x
        fi
        LEVEL="ERROR"
        local MESSAGE="$1"
        Log "$MESSAGE" 0 

        [ -n "$MENOX" ] && set -x
}

function errorPrintln()
{
        if [[ $- =~ x ]]
        then
                MENOX=true
                set +x
        fi
        LEVEL="ERROR"
        local MESSAGE="$1"
        Log "$MESSAGE" 1 

        [ -n "$MENOX" ] && set -x
}

function infoPrint()
{
        if [[ $- =~ x ]]
        then
                MENOX=true
                set +x
        fi
	LEVEL="INFO"
        local MESSAGE="$1" 
        Log "$MESSAGE" 0

        [ -n "$MENOX" ] && set -x
}

function infoPrintln()
{
        if [[ $- =~ x ]] 
        then
                MENOX=true
                set +x
        fi

        LEVEL="INFO"
        local MESSAGE="$1" 
        Log "$MESSAGE" 1 

        [ -n "$MENOX" ] && set -x
}


function debugPrint()
{
        if [[ $- =~ x ]]
        then
                MENOX=true
                set +x
        fi
        LEVEL="DEBUG"
        local MESSAGE="$1" 
        Log "$MESSAGE" 0 

        [ -n "$MENOX" ] && set -x
}

function debugPrintln() 
{
        if [[ $- =~ x ]]
        then
                MENOX=true
                set +x
        fi
        LEVEL="DEBUG"
        local MESSAGE="$1" 
        Log "$MESSAGE" 1 

        [ -n "$MENOX" ] && set -x
}

