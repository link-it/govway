ColorConsoleAppender_THRESHOLD=""
ColorConsoleAppender_FORMAT=""


IS_ColorConsoleAppender_INIT="false"

readonly black='\E[30;48m'
readonly red='\E[31;48m'
readonly green='\E[32;48m'
readonly yellow='\E[33;48m'
readonly blue='\E[34;48m'
readonly magenta='\E[35;48m'
readonly cyan='\E[36;48m'
readonly white='\E[37;48m'
readonly normaltext='\e[0m'

##############################################################
##################### Formattatori ###########################
##############################################################

function ColorConsoleFormatting()
{
local TO_PARSE=""
[ -z "$TO_PARSE" ] && TO_PARSE="$FORMAT"
local i=0
local end=${#TO_PARSE}

local CH=""
local DATE=""
local WHO=""

while [ $i -lt $end ]
do
        CH="${TO_PARSE:$i:1}"
        if [ "$CH" = "%" ]
        then
                i=$(( $i + 1 ))
                CH="${TO_PARSE:$i:1}"
                case $CH in
                D)
                        DATE=$(date +'%Y/%m/%d %H:%M:%S')
                        MESSAGE_INFOS="$MESSAGE_INFOS$normaltext$DATE$normaltext"
                        ;;
                W)
                        WHO=${0##./}
                        MESSAGE_INFOS="$MESSAGE_INFOS$normaltext$WHO$normaltext"
                        ;;
                L)
                        case $LEVEL in
                        INFO) COLOR=$blue
                                ;;
                        ERROR) COLOR=$red
                                ;;
                        WARN) COLOR=$yellow
                                ;;
                        DEBUG) COLOR=$cyan
                                ;;
                        esac
                        MESSAGE_INFOS="$MESSAGE_INFOS$COLOR$LEVEL$normaltext"
                esac
        else
                MESSAGE_INFOS="$MESSAGE_INFOS$CH"

        fi

        i=$(( $i + 1 ))


done
}


##############################################################
####################### Appenders ############################
##############################################################


ORDERED_LEVELS="ALL DEBUG INFO WARN ERROR"

function ColorConsoleAppender()
{
local OLDIFS="$IFS"
if [ "$IS_ColorConsoleAppender_INIT" != "true" ]
then
	IFS=" "
	for prop in $ALL_PROPERTIES
	do
        	[ "${prop%%_*}" == "ColorConsoleAppender" ] && eval "$prop"
	done
	IFS="$OLDIFS"
	IS_ColorConsoleAppender_INIT="true"
fi

#local FORMAT_LINE=${ColorConsoleAppender_FORMAT:-$3}
local MESSAGE_INFOS=""



#isLoggable $THRESHOLD && isLoggable $ColorConsole_THRESHOLD || return

if ! isLoggable $ColorConsoleAppender_THRESHOLD
then
	if ! isLoggable $THRESHOLD
	then
		return
	fi
fi
ColorConsoleFormatting "$ColorConsoleAppender_FORMAT"


if [ $2 -eq  0 ]
then
        echo -en "$MESSAGE_INFOS: $1"
else
        echo -e "$MESSAGE_INFOS: $1"
fi

tput sgr0
unset MESSAGE_INFOS
}



