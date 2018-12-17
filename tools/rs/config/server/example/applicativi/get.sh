entita="applicativi"
nome="Applicativo"

if [ $1 ]; then
	nome=$1
fi

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X GET "http://127.0.0.1:8080/govwayAPIConfig/$entita/$nome"

