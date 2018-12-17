#!/bin/bash

entity="applicativi"
name="Applicativo"
body="body_update.json"

if [ $1 ]; then
	name=$1
fi

if [ $2 ]; then
	body=$2
fi

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X PUT --data @body_update.json "http://127.0.0.1:8080/govwayAPIConfig/$entity/$name/"
