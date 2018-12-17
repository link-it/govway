entityname="ruoli"
nome="Ruolo1"

if [ $1 ]; then
	nome=$1
fi

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X DELETE "http://127.0.0.1:8080/govwayAPIConfig/$entityname/$nome"

