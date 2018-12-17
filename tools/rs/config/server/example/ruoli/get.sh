
name="Applicativo";

if [ $1 ]; then
    name=$1
fi

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X GET "http://127.0.0.1:8080/govwayAPIConfig/ruoli/$name"

