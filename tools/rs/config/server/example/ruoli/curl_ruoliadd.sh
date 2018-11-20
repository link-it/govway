#!/bin/bash

curl --user amministratore:123456 -i -H "Content-Type: application/json" -X POST --data @ruolo1.json "http://127.0.0.1:8080/govwayAPIConfig/ruoli/"
