# GovWay Docker

## Introduzione

L’ambiente di esecuzione è composto da un ambiente Docker Compose preinizializzato con gli scenari descritti in questa guida.

Gli [scenari](https://github.com/link-it/govway/raw/master/resources/scenari/scenari_oauth_client_credentials/scenari_oauth_client_credentials.zip) configurati sull’ambiente docker devono poter accedere al seguente servizio su internet:
- Petstore: https://petstore.swagger.io/


## Avvio Ambiente

Di seguito vengono forniti tutti i passaggi da effettuare per ottenere un ambiente funzionante:

- *Archivio*: scompattare l'archivio [scenari_oauth_client_credentials.zip](https://github.com/link-it/govway/raw/master/resources/scenari/scenari_oauth_client_credentials/scenari_oauth_client_credentials.zip) nella cartella di destinazione scelta per ospitare l’ambiente di esecuzione degli scenari.

- *Hostname*: l’ambiente è configurato per utilizzare l’hostname "govway.localdomain". Configurare una risoluzione dell’hostname ad esempio registrando nel file /etc/hosts l’entry:
  `  127.0.0.1       govway.localdomain`

- *Ambiente Docker*: avviare l’ambiente docker compose utilizzando lo script "starttest.sh" presente all’interno della cartella di destinazione dell’ambiente.

I componenti avviati sono i seguenti:

- *gateway*: l’istanza di Govway

- *PGSQL95*: il database Postgres

- *keycloak*: l’authorization server

- *traefik*: il load balancer

Dopo aver avviato l’ambiente è possibile verificare l’accesso alle seguenti console:

- *GovWay - Console di Gestione*: permette di visualizzare le configurazioni realizzate su Govway
  endpoint: https://govway.localdomain/govwayConsole/
  username: amministratore
  password: 123456

- *GovWay - Console di Monitoraggio*: permette di consultare le transazioni gestite da Govway:
  endpoint: https://govway.localdomain/govwayMonitor/
  username: operatore
  password: 123456

- *Keycloak - Authorization Server*: permette di consultare le configurazioni realizzate sull’Authorization Server Keycloak
  endpoint: https://govway.localdomain/auth/
  username: admin
  password: admin

## Scenario OAuth2

Lo scenario di esempio realizza una installazione Multi-Tenant dove:

- un soggetto generico *Ministero* eroga l'API Petstore. Il servizio è stato configurato per richiedere un token OAuth per accedere alle risorse PUT, POST e DELETE. Mentre consente un accesso pubblico alle risorse GET.
  Sono inoltre stati censiti differenti applicativi (corrispondenti ai client registrati su Keycloak) e autorizzati puntualmente sui gruppi di risorse.
  Infine per ogni gruppo di risorse vengono anche controllati gli scope richiesti: *example-create* per risorse PUT e POST, *example-delete* per risorse DELETE.

- un soggetto generico *EnteFruitore* che fruisce del servizio erogato dal Ministero. Vengono definite 3 fruizioni diverse che differiscono nella modalità di negoziazione del token, associato al connettore.
  Le modalità di negoziazione, definite nelle sezione 'Configurazione -> Token Policy', sono:

  - *KeyCloak.ClientIdSecret*: autenticazione su keycloak tramite client id e client secret
  - *KeyCloak.ClientJWT*: autenticazione tramite un jwt firmato mediante un pkcs12 (coppia chiavi x509); su keycloak è stato registrato il certificato
  - *KeyCloak.ClientJWTSecret*: autenticazione tramite un jwt firmato con una chiave simmetrica condivisa con keycloak

**NOTA**: per esaminare le configurazioni assicurarsi di avere selezionato il profilo *'API Gateway'* in alto a destra sia nella [console di gestione](https://govway.localdomain/govwayConsole/) che nella [console di monitoraggio](https://govway.localdomain/govwayMonitor/) .

Di seguito vengono riportati degli esempi, è possibile consultare la [govwayMonitor](https://govway.localdomain/govwayMonitor/) console per esaminare le richieste transitate sul gateway.

Esempi.

**Invocazione diretta dell'API erogata dal Ministero**
La richiesta termina in errore poichè la richiesta non presenta un token oauth e la risorsa richiesta è una tra quelle protette.

```shell
curl -v -k -X PUT "https://govway.localdomain/govway/Ministero/PetStore/v1/pet" \
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

HTTP/2 401 
content-type: application/problem+json
date: Wed, 23 Jun 2021 15:33:05 GMT
govway-transaction-errortype: TokenAuthenticationRequired
govway-transaction-id: 4da3a7e6-d438-11eb-afe7-0242ac140002
server: GovWay
www-authenticate: Bearer realm="KeyCloack", error="invalid_request", error_description="The request is missing a required token parameter"
content-length: 215
{"type":"https://govway.org/handling-errors/401/TokenAuthenticationRequired.html","title":"TokenAuthenticationRequired","status":401,"detail":"A token is required","govway_id":"4da3a7e6-d438-11eb-afe7-0242ac140002"}
```
**Invocazione diretta dell'API erogata dal Ministero**
La richiesta termina con successo poichè la risorsa richiesta tra quelle pubbliche.

```shell
curl -v -k -X GET "https://govway.localdomain/govway/Ministero/PetStore/v1/pet/findByStatus"

HTTP/2 200 
content-type: application/json
date: Wed, 23 Jun 2021 15:35:57 GMT
govway-message-id: b3fb74ac-d438-11eb-afe7-0242ac140002
govway-transaction-id: b3f9ee06-d438-11eb-afe7-0242ac140002
server: GovWay
content-length: 2
```

**Negoziazione Token (ClientId/ClientSecret)**
Viene simulato un applicativo client dell'EnteFruitore che utilizza GovWay per andare a fruire del servizio erogato dal Ministero.
L'applicativo client dell'EnteFruitore invoca in questo caso la fruizione configurata per attuare una negoziazione del token tramite client id e client secret:

```shell
curl -v -k -X PUT "https://govway.localdomain/govway/out/EnteFruitore/Ministero/FruizioneNegoziazioneClientSecret/v1/pet" \
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

HTTP/2 200 
access-control-allow-headers: Content-Type, api_key, Authorization
access-control-allow-methods: GET, POST, DELETE, PUT
access-control-allow-origin: *
content-type: application/json
date: Wed, 23 Jun 2021 15:38:06 GMT
govway-message-id: 0092ff23-d439-11eb-afe7-0242ac140002
govway-transaction-id: 007760be-d439-11eb-afe7-0242ac140002
server: GovWay
content-length: 150

{"id":3,"category":{"id":22,"name":"dog"},"name":"doggie","photoUrls":["http://image/dog.jpg"],"tags":[{"id":23,"name":"white"}],"status":"available"}
```

**Negoziazione Token (ClientId/ClientSecret), scope non autorizzato **
Lo scenario è simile al precedente, dove l'applicativo client utilizza la fruizione di GovWay configurata per negoziare un token tramite autenticazione clientId/clientSecret.
Poichè la policy 'KeyCloak.ClientIdSecret' è configurata per richiedere solamente lo scope 'example-create', l'invocazione di una risorsa DELETE da parte del client avrà come risultato un accesso negato.

```shell
curl -v -k -X DELETE "https://govway.localdomain/govway/out/EnteFruitore/Ministero/FruizioneNegoziazioneClientSecret/v1/pet/1"

HTTP/2 403 
content-type: application/problem+json
date: Wed, 23 Jun 2021 15:40:31 GMT
govway-message-id: 57a9eb5e-d439-11eb-afe7-0242ac140002
govway-transaction-id: 57a7c878-d439-11eb-afe7-0242ac140002
server: GovWay
www-authenticate: Bearer realm="KeyCloack", error="insufficient_scope", error_description="The request requires higher privileges than provided by the access token", scope="example-delete"
content-length: 223

{"type":"https://govway.org/handling-errors/403/AuthorizationMissingScope.html","title":"AuthorizationMissingScope","status":403,"detail":"Required token scopes not found","govway_id":"57ae7f40-d439-11eb-afe7-0242ac140002"}
```
**Negoziazione Token (Asserzione JWT firmata con X.509)**
In questo scenario di esempio, l'applicativo client dell'EnteFruitore invoca la fruizione configurata per attuare una negoziazione del token attraverso lo scambio di un jwt firmato tramite pkcs12 (coppia chiavi x509).
 La configurazione della policy 'KeyCloak.ClientJWT' negozia entrambi gli scope 'example-create' e 'example-delete' e quindi anche un'invocazione della risorsa DELETE avviene con successo.

```shell
curl -v -k -X DELETE "https://govway.localdomain/govway/out/EnteFruitore/Ministero/FruizioneNegoziazioneJWT/v1/pet/1"

HTTP/2 200 
access-control-allow-headers: Content-Type, api_key, Authorization
access-control-allow-methods: GET, POST, DELETE, PUT
access-control-allow-origin: *
content-type: application/json
date: Wed, 23 Jun 2021 15:43:09 GMT
govway-message-id: b593f94c-d439-11eb-afe7-0242ac140002
govway-transaction-id: b5759bca-d439-11eb-afe7-0242ac140002
server: GovWay
content-length: 43

{"code":200,"type":"unknown","message":"1"}
```

**Negoziazione Token (Asserzione JWT firmata con secret key)**
In questo scenario di esempio, l'applicativo client dell'EnteFruitore invoca la fruizione configurata per attuare una negoziazione del token attraverso lo scambio di un jwt firmato una chiave simmetrica condivisa con keycloak.

```shell
curl -v -k -X PUT "https://govway.localdomain/govway/out/EnteFruitore/Ministero/FruizioneNegoziazioneJWTWithSecret/v1/pet" \
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

HTTP/2 200 
access-control-allow-headers: Content-Type, api_key, Authorization
access-control-allow-methods: GET, POST, DELETE, PUT
access-control-allow-origin: *
content-type: application/json
date: Wed, 23 Jun 2021 15:44:14 GMT
govway-message-id: dc1ca110-d439-11eb-afe7-0242ac140002
govway-transaction-id: dbfe6a9d-d439-11eb-afe7-0242ac140002
server: GovWay
content-length: 150

{"id":3,"category":{"id":22,"name":"dog"},"name":"doggie","photoUrls":["http://image/dog.jpg"],"tags":[{"id":23,"name":"white"}],"status":"available"}
```

**Negoziazione Token (Asserzione JWT firmata con secret key), client-id non autorizzato**
Lo scenario è simile al precedente, dove l'applicativo client utilizza la fruizione di GovWay configurata per negoziare un token tramite jwt firmato con secret key.
L'invocazione di una risorsa DELETE da parte del client avrà come risultato un accesso negato, poichè nel controllo degli accessi dell'erogazione PetStore del Ministero, l'applicativo con client id 'example-client-jwt-secret' non è stato aggiunto nella lista degli applicativi autorizzati all'interno del gruppo delle risorse DELETE.

```shell
curl -v -k -X DELETE "https://govway.localdomain/govway/out/EnteFruitore/Ministero/FruizioneNegoziazioneJWTWithSecret/v1/pet/1"

HTTP/2 403 
content-type: application/problem+json
date: Wed, 23 Jun 2021 15:46:02 GMT
govway-message-id: 1cd2f0db-d43a-11eb-afe7-0242ac140002
govway-transaction-id: 1cd1df65-d43a-11eb-afe7-0242ac140002
server: GovWay
content-length: 194

{"type":"https://govway.org/handling-errors/403/AuthorizationDeny.html","title":"AuthorizationDeny","status":403,"detail":"Authorization deny","govway_id":"1cd75dae-d43a-11eb-afe7-0242ac140002"}
```



# Scenario OAuth2 + ModI

Lo scenario di esempio utilizza una installazione Multi-Tenant dove:

- un soggetto generico 'Ministero' eroga l'API Petstore. Il servizio è stato configurato per richiedere un token OAuth simile a quanto descritto nella sezione precedente.
 La configurazione dell'erogazione richiede inoltre la presenza degli header http 'Agid-JWT-Signature' e 'Digest' previsti dal pattern INTEGRITY ModI.

- un soggetto generico 'EnteFruitore' che fruisce del servizio erogato dal Ministero. Viene definita una fruizione che:
  - negozia un token oauth tramite un jwt firmato tramite un pkcs12 (coppia chiavi x509); su keycloak è stato registrato il certificato
  - firma il payload della richiesta (producendo gli header 'Agid-JWT-Signature' e 'Digest') tramite il medesimo certificato utilizzato per firmare l'asserzione jwt durante la negoziazione. (Certificato associato all'applicativo 'ApplicativoClient').

**NOTA**: per esaminare le configurazioni assicurarsi di avere selezionato il profilo *'ModI'* in alto a destra sia nella [console di gestione](https://govway.localdomain/govwayConsole/) che nella [console di monitoraggio](https://govway.localdomain/govwayMonitor/) .

Viene simulato un applicativo client dell'EnteFruitore che utilizza GovWay per andare a fruire del servizio erogato dal Ministero.
L'applicativo client dell'EnteFruitore invoca in questo caso la fruizione configurata per attuare una negoziazione del token tramite jwt firmato tramite pkcs12 (coppia chiavi x509 e per produrre gli header previsti dal pattern INTEGRITY di ModI.

È possibile consultare la [govwayMonitor](https://govway.localdomain/govwayMonitor/) console per esaminare le richieste transitate sul gateway.

```shell
curl -v -k -X PUT -u "ApplicativoClient:123456" "https://govway.localdomain/govway/rest/out/EnteFruitore/Ministero/PetStore/v1/pet" \
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

HTTP/2 200 
access-control-allow-headers: Content-Type, api_key, Authorization
access-control-allow-methods: GET, POST, DELETE, PUT
access-control-allow-origin: *
content-type: application/json
date: Wed, 23 Jun 2021 15:53:01 GMT
govway-message-id: 168ed3ee-d43b-11eb-afe7-0242ac140002
govway-transaction-id: 1687f60d-d43b-11eb-afe7-0242ac140002
server: GovWay
content-length: 150

{"id":3,"category":{"id":22,"name":"dog"},"name":"doggie","photoUrls":["http://image/dog.jpg"],"tags":[{"id":23,"name":"white"}],"status":"available"}
```

Accendo alla [console di monitoraggio](https://govway.localdomain/govwayMonitor/) è possibile esaminare i contenuti dei messaggi salvati ai fini di non ripudio e si può vedere come tra gli header http siano presenti sia l'header 'Authorization' (token OAuth2) che l'header 'Agid-JWT-Signature' insieme all'header 'Digest' (token ModI). 
Di seguito vengono riportati i 3 header raccolti tramite la console:

```shell
Digest: SHA-256=eec506791ca37d52867261c206eb32d8a6f1104a3ba01d106eb4ca15f5c9bdcd
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5......XYwCtA
Agid-JWT-Signature: eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIm.......zqC4-TZtIYxCiQbQ
```
