.. _quickProfiloAPIGW_EsempioREST:

Servzio REST di Riferimento per gli Esempi
------------------------------------------

Per quanto concerne la tipologia di servizi **REST**, il servizio di
esempio utilizzato per mostrare le funzionalità dell'API Gateway è lo
*Swagger Petstore* (web site: https://petstore.swagger.io/ ) disponibile
all'indirizzo http://petstore.swagger.io/v2/. L'interfaccia API è
scaricabile in https://petstore.swagger.io/v2/swagger.json. Per simulare
un aggiornamento di un animale all'interno del negozio è utilizzabile il
seguente comando:

::

    curl -v -X PUT "http://petstore.swagger.io/v2/pet" \
    -H "accept: application/json" \
    -H "Content-Type: application/json" \
    -d '{
            "id": 3,
            "category": { "id": 22, "name": "dog" },
            "name": "doggie",
            "photoUrls": [ "http://image/dog.jpg" ],
            "tags": [ { "id": 23, "name": "white" } ],
            "status": "available"
    }'

L'esito dell'aggiornamento viene confermato con un codice http 200 e una
risposta json equivalente alla richiesta:

::

    HTTP/1.1 200 OK
    Access-Control-Allow-Origin: *
    Access-Control-Allow-Methods: GET, POST, DELETE, PUT
    Access-Control-Allow-Headers: Content-Type, api_key, Authorization
    Content-Type: application/json
    Connection: close
    Server: Jetty(9.2.9.v20150224)

    {
        "id":3,
        "category":{"id":22,"name":"dog"},
        "name":"doggie",
        "photoUrls":["http://image/dog.jpg"],
        "tags":[{"id":23,"name":"white"}],
        "status":"available"
    }
