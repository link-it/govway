.. _avanzate_fileTrace_info:

Informazioni Tracciabili
-------------------------

Le informazioni inerenti le comunicazioni gestite dal gateway, che possono essere riversate nei file di log associati ai topic, sono indicabili all'interno del formato di un topic tramite una delle seguenti sintassi:

- *${log:<id>}* : viene registrata la risorsa con l'identificativo indicato.
- *${log:<id>(defaultValue)}* : viene registrata la risorsa con l'identificativo indicato; se la risorsa non è valorizzata, viene registrato il valore di default fornito come parametro
- *${log:<id>(paramters ...)}* : viene registrata la risorsa con l'identificativo indicato, il cui valore può essere personalizzato rispetto ad alcuni parametri.

Le informazioni possono essere registrate codificate in base64 utilizzando il prefisso 'logBase64' invece di 'log':

- *${logBase64:<id>}*
- *${logBase64:<id>(defaultValue)}*
- *${logBase64:<id>(paramters ...)}*
        
L'esempio seguente definisce un topic che utilizza i formati precedentemente indicati. Viene registrato l'identificativo di transazione (informazione acceduta puntualmente), la data di accesso all'API (informazione formattata rispetto ai parametri 'yyyy-MM-dd HH:mm:ss:SSS' e 'UTC'), il contenuto della richiesta codificato in base64 e l'identificativo di correlazione applicativa se presente o la costante 'ExampleDefaultValue' altrimenti.

   ::

      format.topic.erogazioni.example=${log:transactionId}|${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC):ss:SSS,UTC)}"|${logBase64:inRequestContent}|${log:applicationId(ExampleDefaultValue)}|
      
Le informazioni prodotte saranno le seguenti:

   ::

      "b6cdd758-342c-4599-ae95-33a781730b3f"|"2020-06-26 12:46:50:629"|eyJtaXR0ZW50ZSI6IkF2dm9jYXR1cmEgR2VuZXJhbGUgZGVsbG8gU3RhdG8iLCJkZXN0a...|ExampleDefaultValue
      "2a9dc253-9dd5-458b-8689-edee7c9ba139"|"2020-06-26 12:47:50:561"|eyJtaXR0ZW50ZSI6IkF2dm9jYXR1cmEgR2VuZXJhbGUgZGVsbG8gU3RhdG8iLCJkZXN0a...|ExampleDefaultValue
      "eeedb92b-66b5-451e-8266-ade2cf1f34ce"|"2020-06-26 12:47:53:291"|eyJtaXR0ZW50ZSI6IkF2dm9jYXR1cmEgR2VuZXJhbGUgZGVsbG8gU3RhdG8iLCJkZXN0a...|ApplicationXXX3
      "b4355a45-71cc-4293-b3b7-a4622af8ea84"|"2020-06-26 12:48:00:102"|eyJtaXR0ZW50ZSI6IkF2dm9jYXR1cmEgR2VuZXJhbGUgZGVsbG8gU3RhdG8iLCJkZXN0a...|ExampleDefaultValue

Di seguito vengono indicati tutti gli identificativi delle informazioni disponibili con i possibili parametri. 

.. note::
      Gli identificativi per cui non vengono specificati parametri sono sempre disponibili nella modalità con o senza la definizione del valore di default.

**Identificativi**

- transactionId: identificativo della transazione;

- requestId: identificativo del messaggio di richiesta;

- responseId: identificativo del messaggio di risposta;

- correlationId: identificativo che correla molteplici transazioni;

- asyncId: identificativo utilizzato in profili asincroni;

- requestApplicationId: identificativo di correlazione applicativa della richiesta;

- responseApplicationId: identificativo di correlazione applicativa della risposta;

- applicationId: requestApplicationId + responseApplicationId;
	
- clusterId: identificativo del nodo in una installazione in cluster del gateway.


**Esito**

- inHttpStatus: codice http di risposta ritornato dal backend contattato dal gateway;

- inHttpReason: http reason associato al codice di risposta ritornato dal backend;

- outHttpStatus: codice http di risposta ritornato al client dal gateway;

- outHttpReason: http reason associato al codice di risposta ritornato al client;

- result: esito della transazione (codifica GovWay);

- transactionType: tipo della transazione (Standard, Sistema ...).


**Date**

- acceptedRequestDate: data in cui la richiesta è pervenuta sul gateway;

- inRequestDate: data in cui la richiesta è stata completamente ricevuta sul gateway;

- outRequestDate: data in cui la richiesta viene inoltrata dal gateway al backend;

- acceptedResponseDate: data in cui la risposta è pervenuta sul gateway;

- inResponseDate: data in cui la risposta è stata completamente ricevuta sul gateway;

- outResponseDate: data in cui la risposta viene ritornata al client.

Tutte le date indicate sono accessibili anche con i seguenti parametri:

- (format): formato della data (es. yyyy-MM-dd HH:mm:ss:SSS.Z);

- (format, timeZone): formato della data (es. yyyy-MM-dd HH:mm:ss:SSS) + time zone (es. UTC).


**Elapsed Time**

- elapsedTime: tempo di risposta complessivo trascorso tra l'ingresso della richiesta nel gateway e la risposta ritornata al client;

- apiElapsedTime: tempo di risposta del backend;

- gatewayLatency: latenza introdotta dal gateway rispetto al tempo di risposta del backend.

Tutte le informazioni sono ritornate in millisecondi. È possibile ottenere le medesime informazioni in un altro formato di tempo utilizzando i seguenti suffissi:

- <elapsedTime>S: tempo in secondi;

- <elapsedTime>Ms: tempo in millisecondi (è il default);

- <elapsedTime>uS: tempo in microsecondi;

- <elapsedTime>nS: tempo in nanosecondi.

**Dominio**

- domain: identificativo del dominio interno che ha gestito l'erogazione o la fruizione;

- organization: identificativo del soggetto, di dominio interno, che ha gestito l'erogazione o la fruizione;

- organizationType: tipo del soggetto, di dominio interno, che ha gestito l'erogazione o la fruizione;

- role: indica se la transazione rappresenta una 'erogazione' o 'fruizione'.

**API**

- apiProtocol: indica se l'API è di tipo 'rest' o 'soap';

- api: identificativo dell'API;

- apiVersion: versione dell'API;

- apiType: tipo dell'API;

- apiInterface: identificativo dell'interfaccia implementata dall'erogazione o dalla fruizione (contiene nome, versione e soggetto referente);

- action: identificativo della risorsa (API Rest) o dell'azione (API Soap);

- httpMethod: metodo http invocato;

- outURL: url utilizzata dal gateway per invocare il backend (se presenti, contiene anche i parametri della url);

- inURL: url utilizzata dal client per invocare il gateway (se presenti, contiene anche i parametri della url);

- inFunction: indica il tipo di canale (in, out, out/xml2soap) utilizzato dal client per invocare il gateway;

- collaborationProfileCode: indica il profilo di collaborazione associato all'azione di una API Soap (Oneway/Sincrono/AsincronoSimmetrico/AsincronoAsimmetrico);

- collaborationProfile: indica il profilo di collaborazione associato all'azione di una API Soap con la terminologia del profilo di interoperabilità dell'API;

- profile: profilo di interoperabilità in cui è stata registrata l'API;

- interface: identificativo dell'erogazione o della fruizione.

**Soggetti**

- provider: identificativo del soggetto erogatore;

- providerType: tipo del soggetto erogatore;

- providerDomain: identificativo del dominio erogatore;

- providerURI: uri associata al soggetto erogatore;

- sender: identificativo del soggetto fruitore;

- senderType: tipo del soggetto fruitore;

- senderDomain: identificativo del dominio fruitore;

- senderURI: uri associata al soggetto fruitore.

**Mittente**
	
- application: identificativo dell'applicativo richiedente;

- credentials: credenziali presenti nella richiesta;

- principal: identificato con cui l'applicativo è stato autenticato;

- token: token OAuth2 presente nella richiesta;

- tokenIssuer: issuer presente nel token;

- tokenSubject: subject presente nel token;

- tokenClientId: clientId presente nel token;
	
- tokenUsername: username presente nel token;

- tokenMail: eMail presente nel token;

- clientIP: indirizzo IP del client;

- forwardedIP: indirizzo IP presente nella richiesta in uno degli header http appartenente alla classe "Forwarded-For" o "Client-IP";
	
**Messaggi**

- duplicateRequest: numero di volte in cui una richiesta con stesso 'requestId' è stata ricevuta dal gateway;

- duplicateResponse: numero di volte in cui una risposta con stesso 'responseId' è stata ricevuta dal gateway;
	
- getInFault: eventuale SOAP Fault o Problem Detail RFC 7807 ricevuto dal backend;

- getOutFault: eventuale SOAP Fault o Problem Detail RFC 7807 ritornato al client.

È inoltre possibile accedere alle seguenti informazioni riguardanti i singoli messaggi in ingresso o uscita dal gateway:

- <messageType>ContentType: valore dell'header http 'Content-Type';

- <messageType>Content: payload http;

- <messageType>Size: dimensione del payload http;
	
- <messageType>Header(name): valore dell'header http indicato come parametro;

- <messageType>Headers: elenco degli headers http nel formato <nome>=<valore> separati dal carattere ',' ;

- <messageType>Headers(headersSeparator, nameValueSeparator, prefix, suffix): i parametri permettono di personalizzare il formato degli headers http.

I tipi di messaggi disponibili sono:

- inRequest: richiesta ricevuta sul gateway;

- outRequest: richiesta inoltrata al backend;

- inResponse: risposta ricevuta dal backend;

- outResponse: risposta ritornata a client.

.. note::
      Le informazioni sui 4 tipi di messaggio saranno disponibili solamente se è stata abilitata la funzionalità di dump per ciascun tipo nel file di configurazione locale '/etc/govway/govway_local.properties' (assumendo sia /etc/govway la directory di configurazione indicata in fase di installazione). Di seguito un estratto della configurazione che riporta l'abilitazione dei 4 tipi:

         ::

            # ================================================
            # FileTrace
            ...
            #
            # Indicazione se nella funzionalità è consentito l'accesso ai contenuti
            # -- Fruizioni --
            # inRequest/outResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.enabled=true
            # outRequest/inResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.connettore.enabled=true
            # -- Erogazioni --
            # inRequest/outResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.enabled=true
            # outRequest/inResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.connettore.enabled=true
            ...
	

