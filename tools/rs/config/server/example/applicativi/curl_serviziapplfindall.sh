function usage()
{
    echo "FindAll"
    echo ""
    echo $0
    echo -e "\t-o=offset"
    echo -e "\t-q=query"
    echo -e "\t-l=limit"
	echo -e "\t--soggetto=nomesoggetto"
    echo -e "\t--profilo=ApiGateway | SpCoop | FatturazioneElettronica"
	echo -e "\t--ruolo=ruolo"
    echo ""
}

query_params=""

#Parso i parametri
while [ "$1" != "" ]; do
    PARAM=`echo $1 | awk -F= '{print $1}'`
    VALUE=`echo $1 | awk -F= '{print $2}'`
    case $PARAM in
        -h | --help)
            usage
            exit
            ;;
        -o)
			query_params="$query_params""offset=$VALUE&"
            ;;
        -l)
			query_params="$query_params""limit=$VALUE&"
            ;;
		-q)
			query_params="$query_params""q=$VALUE&"
			;;
		--soggetto)
			query_params="$query_params""soggetto=$VALUE&"
			;;
		--profilo)
			query_params="$query_params""profilo=$VALUE&"
			;;
		--ruolo)
			query_params="$query_params""ruolo=$VALUE&"
			;;
        *)
            echo "ERROR: unknown parameter \"$PARAM\""
            usage
            exit 1
            ;;
    esac
    shift
done

if [ $query_params ]; then
	query_params="?"$query_params
fi

echo $query_params

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X GET "http://127.0.0.1:8080/govwayAPIConfig/applicativi${query_params}"
