FileAppender_LOGFILE=""
FileAppender_THRESHOLD=""
FileAppender_FORMAT=""
FileAppender_RESETFILE=""

IS_FileAppender_INIT="false"
##############################################################
##################### Formattatori ###########################
##############################################################

function FileFormatting()
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


function FileAppender()
{
local OLDIFS="$IFS"
if [ "$IS_FileAppender_INIT" != "true" ]
then
	IFS=" "
        for prop in $ALL_PROPERTIES
        do
                [ "${prop%%_*}" == "FileAppender" ] && eval "$prop"
        done
	IFS="$OLDIFS"
        IS_FileAppender_INIT="true"

	if [ -n "$FileAppender_RESETFILE" -a "$FileAppender_RESETFILE" == "true" ]
	then
		/bin/cp /dev/null "$FileAppender_LOGFILE" 2>/dev/null
	fi
fi


#local FORMAT_LINE=${FileAppender_FORMAT:-$3}
local MESSAGE_INFOS=""

if ! isLoggable $FileAppender_THRESHOLD
then
        if ! isLoggable $THRESHOLD
        then
                return
        fi
fi

FileFormatting "$FileAppender_FORMAT"

[ -z "$FileAppender_LOGFILE" ] && return

if [ $2 -eq  0 ]
then
        echo -en "$MESSAGE_INFOS: $1"  2>/dev/null >> $FileAppender_LOGFILE
else
        echo -e "$MESSAGE_INFOS: $1"  2>/dev/null >> $FileAppender_LOGFILE
fi

unset MESSAGE_INFOS

}

