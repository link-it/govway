ConsoleAppender_THRESHOLD=""
ConsoleAppender_FORMAT=""


IS_ConsoleAppender_INIT="false"


##############################################################
##################### Formattatori ###########################
##############################################################

function ConsoleFormatting()
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
                        MESSAGE_INFOS="$MESSAGE_INFOS$DATE"
                        ;;
                W)
                        WHO=${0##./}
                        MESSAGE_INFOS="$MESSAGE_INFOS$WHO"
                        ;;
                L)
                                                MESSAGE_INFOS="$MESSAGE_INFOS$LEVEL"
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

function ConsoleAppender()
{
local OLDIFS="$IFS"
if [ "$IS_ConsoleAppender_INIT" != "true" ]
then
        IFS=" "
        for prop in $ALL_PROPERTIES
        do
                [ "${prop%%_*}" == "ConsoleAppender" ] && eval "$prop"
        done
        IFS="$OLDIFS"
        IS_ConsoleAppender_INIT="true"
fi

#local FORMAT_LINE=${ConsoleAppender_FORMAT:-$3}
local MESSAGE_INFOS=""



#isLoggable $THRESHOLD && isLoggable $Console_THRESHOLD || return

if ! isLoggable $ConsoleAppender_THRESHOLD
then
        if ! isLoggable $THRESHOLD
        then
                return
        fi
fi
ConsoleFormatting "$ConsoleAppender_FORMAT"


if [ $2 -eq  0 ]
then
        echo -en "$MESSAGE_INFOS: $1"
else
        echo -e "$MESSAGE_INFOS: $1"
fi

#tput sgr0
unset MESSAGE_INFOS
}


