#!/bin/bash

entity="ruoli"
name="Ruolo1"
body="ruolo1.json"

if [ $1 ]; then
	name=$1
fi

if [ $2 ]; then
	body=$2
fi

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X PUT --data @$body "http://127.0.0.1:8080/govwayAPIConfig/$entity/$name"
