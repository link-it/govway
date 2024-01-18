.. _avanzate_fileTrace_info:

Informazioni Tracciabili
-------------------------

Le informazioni inerenti le comunicazioni gestite dal gateway, che possono essere riversate nei file di log associati ai topic, sono indicabili all'interno del formato di un topic tramite una delle seguenti sintassi:

- *${log:<id>}* : viene registrata la risorsa con l'identificativo indicato.
- *${log:<id>(defaultValue)}* : viene registrata la risorsa con l'identificativo indicato; se la risorsa non è valorizzata, viene registrato il valore di default fornito come parametro
- *${log:<id>(parameters ...)}* : viene registrata la risorsa con l'identificativo indicato, il cui valore può essere personalizzato rispetto ad alcuni parametri.

Le informazioni possono essere registrate codificate in base64 utilizzando il prefisso 'logBase64' invece di 'log':

- *${logBase64:<id>}*
- *${logBase64:<id>(defaultValue)}*
- *${logBase64:<id>(parameters ...)}*
        
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

- resultClass: classe a cui appartiene l'esito della transazione tra OK, KO e FAULT;

- resultClassOk: indicazione se l'esito della transazione appartiene alla classe OK (true/false);

- resultClassKo: indicazione se l'esito della transazione appartiene alla classe KO (true/false);

- resultClassFault: indicazione se l'esito della transazione appartiene alla classe FAULT (true/false);

- result: esito della transazione (codifica GovWay);

- resultCode: esito della transazione (codifica numerica di GovWay);

- errorDetail: dettaglio dell'errore avvenuto durante la gestione della transazione (per maggiori informazioni si rimanda al manuale sulla console di monitoraggio nella sezione :ref:`mon_dettaglioErrore`);

- transactionType: tipo della transazione (Standard, Sistema ...).

 


**Diagnostici**

Di seguito vengono indicati gli identificativi che consentono di accedere ai diagnostici emessi da GovWay durante la gestione della richiesta:

- diagnostics: elenco completo dei messaggi diagnostici emessi;

- errorDiagnostics: elenco dei messaggi diagnostici di sola severità errore.

Ogni diagnostico viene fornito nella forma seguente e separato dagli altri tramite un ritorno a capo (configurazione di default):

  <livelloSeverità> <dataEmissione> <codiceDiagnostico> <messaggio>

ad esempio:

   ::

      infoIntegration 2020-09-10T14:15:51.605Z 004074 Autenticazione [basic] in corso ( BasicUsername 'prova' ) ...
      infoIntegration 2020-09-10T14:15:51.606Z 004075 Autenticazione [basic] effettuata con successo (in cache)

L'elenco dei diagnostici sono accessibili anche con i seguenti parametri:

- (separator): consente di indicare un separatore dei diagnostici differente da quello di default (ritorno a capo)

- (separator, format): oltre al separatore, consente di indicare il formato della data (es. yyyy-MM-dd HH:mm:ss:SSS.Z);

- (separator, format, timeZone): oltre al separatore e al formato della data (es. yyyy-MM-dd HH:mm:ss:SSS) consente di indicare il time zone (es. UTC).


**Date**

- acceptedRequestDate: data in cui la richiesta è pervenuta sul gateway;

- inRequestDate e inRequestStartTime: data in cui la richiesta è iniziata ad essere gestita sul gateway;

- inRequestEndTime: data in cui la richiesta è stata completamente ricevuta sul gateway;

- outRequestDate e outRequestStartTime: data in cui la richiesta viene inoltrata dal gateway al backend;

- outRequestEndTime: data in cui la richiesta è stata consegnata al backend;

- acceptedResponseDate: data in cui la risposta è pervenuta sul gateway;

- inResponseDate e inResponseStartTime: data in cui la risposta è iniziata ad essere gestita sul gateway;

- inResponseEndTime: data in cui la risposta è stata completamente ricevuta sul gateway;

- outResponseStartTime: data in cui la risposta viene ritornata al client;

- outResponseDate e outResponseEndTime: data in cui la risposta è stata completamente consegnata al client.

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

- organizationId: identificativo del soggetto, di dominio interno, che ha gestito l'erogazione o la fruizione (identificativo nel formato previsto dal profilo di interoperabilità);

- organization: nome del soggetto, di dominio interno, che ha gestito l'erogazione o la fruizione;

- organizationType: tipo del soggetto, di dominio interno, che ha gestito l'erogazione o la fruizione;

- role: indica se la transazione rappresenta una 'erogazione' o 'fruizione';

- contextPropertiesKeys: nomi delle proprietà definite nel contesto;

- contextProperties: proprietà (nome=valore) definite nel contesto separate da uno spazio;

- contextProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- contextProperty(nomeProprietà): valore della proprietà indicata come parametro.

**API**

- apiProtocol: indica se l'API è di tipo 'rest' o 'soap';

- apiId: identificativo dell'API, secondo il formato previsto dal profilo di interoperabilità;

- api: nome dell'API;

- apiVersion: versione dell'API;

- apiType: tipo dell'API;

- apiInterface: identificativo dell'interfaccia implementata dall'erogazione o dalla fruizione (contiene nome, versione e soggetto referente);

- apiInterfaceId: identificativo dell'interfaccia implementata dall'erogazione o dalla fruizione secondo il formato previsto dal profilo di interoperabilità;

- apiPropertiesKeys: nomi delle proprietà definite sull'erogazione o sulla fruizione;

- apiProperties: proprietà (nome=valore) definite sull'erogazione o sulla fruizione separate da uno spazio;

- apiProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- apiProperty(nomeProprietà): valore della proprietà indicata come parametro;

- action: identificativo della risorsa (API Rest) o dell'azione (API Soap);

- httpMethod: metodo http invocato;

- outURL: url utilizzata dal gateway per invocare il backend (se presenti, contiene anche i parametri della url);

- inURL: url utilizzata dal client per invocare il gateway (se presenti, contiene anche i parametri della url);

- inFunction: indica il tipo di canale (in, out, out/xml2soap) utilizzato dal client per invocare il gateway;

- collaborationProfileCode: indica il profilo di collaborazione associato all'azione di una API Soap (Oneway/Sincrono/AsincronoSimmetrico/AsincronoAsimmetrico);

- collaborationProfile: indica il profilo di collaborazione associato all'azione di una API Soap con la terminologia del profilo di interoperabilità dell'API;

- profile: profilo di interoperabilità in cui è stata registrata l'API;

- profileLabel: nome descrittivo del profilo di interoperabilità in cui è stata registrata l'API;

- interface: identificativo dell'erogazione o della fruizione;

- outConnectorName: nome del connettore multiplo selezionato per la consegna.

**Soggetti**

- providerId: identificativo del soggetto erogatore, secondo il formato previsto dal profilo di interoperabilità;

- provider: nome del soggetto erogatore;

- providerType: tipo del soggetto erogatore;

- providerDomain: identificativo del dominio erogatore;

- providerURI: uri associata al soggetto erogatore;

- providerPropertiesKeys: nomi delle proprietà definite sul soggetto fruitore;

- providerProperties: proprietà (nome=valore) definite sul soggetto fruitore separate da uno spazio;

- providerProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- providerProperty(nomeProprietà): valore della proprietà indicata come parametro;

- senderId: identificativo del soggetto fruitore, secondo il formato previsto dal profilo di interoperabilità;

- sender: nome del soggetto fruitore;

- senderType: tipo del soggetto fruitore;

- senderDomain: identificativo del dominio fruitore;

- senderURI: uri associata al soggetto fruitore;

- senderPropertiesKeys: nomi delle proprietà definite sul soggetto fruitore;

- senderProperties: proprietà (nome=valore) definite sul soggetto fruitore separate da uno spazio;

- senderProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- senderProperty(nomeProprietà): valore della proprietà indicata come parametro.

**Mittente**
	
- application: identificativo dell'applicativo richiedente;

- applicationPropertiesKeys: nomi delle proprietà definite sull'applicativo richiedente;

- applicationProperties: proprietà (nome=valore) definite sull'applicativo separate da uno spazio;

- applicationProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- applicationProperty(nomeProprietà): valore della proprietà indicata come parametro;

- credentials: credenziali presenti nella richiesta;

- principal: identificato con cui l'applicativo è stato autenticato;

- principalAuthType: tipo di autenticazione (basic/ssl/principal) con cui l'applicativo è stato autenticato;

- clientCertificateSubjectDN: distinguished name del subject relativo al certificato tls client; 

- clientCertificateSubjectCN: common name del subject relativo al certificato tls client;

- clientCertificateSubjectDNInfo(String oid): ritorna l'informazione indicata come parametro relativa al subject del certificato tls client;
	
- clientCertificateIssuerDN: distinguished name dell'issuer relativo al certificato tls client; 

- clientCertificateIssuerCN: common name dell'issuer relativo al certificato tls client; 

- clientCertificateIssuerDNInfo(String oid): ritorna l'informazione indicata come parametro relativa all'issuer del certificato tls client;

- attribute(nomeAttributo): valore dell'attributo indicato come parametro (informazione disponibile solamente se nell'erogazione/fruizione è stata configurata una sola A.A.);

- attributeByAA(nomeAttributeAuthority,nomeAttributo): valore dell'attributo recuperato tramite l'AttributeAuthority indicata come parametro (informazione disponibile solamente se nell'erogazione/fruizione è stata configurata più di una A.A.);

- clientIP: indirizzo IP del client;

- forwardedIP: indirizzo IP presente nella richiesta in uno degli header http appartenente alla classe "Forwarded-For" o "Client-IP";

- requesterIP: (o ipRequester) rappresenta l'indirizzo IP del richiedente e assumerà la prima informazione valorizzata, trovata nella richiesta, nel seguente ordine:

	- forwardedIP
	- clientIP

- requester: rappresenta il richiedente della richiesta e assumerà la prima informazione valorizzata, trovata nella richiesta, nel seguente ordine (per maggiori informazioni si rimanda al manuale sulla console di monitoraggio nella sezione :ref:`mon_richiedente`):

	- tokenUsername: username presente nel token;
	- tokenClient: identificativo dell'applicativo identificato tramite il clientId presente nel token;
        - pdndOrganizationName: nome dell’organizzazione del client ottenuto risolvendo il clientId presente nel token tramite la consultazione delle API PDND;
	- application: identificativo dell'applicativo richiedente identificato tramite l'autenticazione di trasporto;
	- tokenClientId: clientId presente nel token nel caso di client credentials grant type (claims clientId e sub presentano lo stesso valore);
	- tokenSubject[@tokenIssuer]: subject presente nel token; viene aggiunto anche un suffisso @tokenIssuer se è presente anche un issuer nel token;
	- principal: identificativo (credenziali) con cui l'applicativo è stato autenticato; se il tipo di autenticazione di trasporto risulta essere 'ssl' viene ritornato il valore dell'attributo CN.

**Validazione Token**

- token: token OAuth2 presente nella richiesta;

- tokenIssuer: issuer presente nel token;

- tokenSubject: subject presente nel token;

- tokenClientId: clientId presente nel token;
	
- tokenClientApplication: identificativo dell'applicativo identificato tramite il clientId presente nel token;

- tokenClientApplicationPropertiesKeys: nomi delle proprietà definite sull'applicativo identificato tramite il clientId;

- tokenClientApplicationProperties: proprietà (nome=valore) definite sull'applicativo separate da uno spazio;

- tokenClientApplicationProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- tokenClientApplicationProperty(nomeProprietà): valore della proprietà indicata come parametro;

- tokenClientOrganizationId: identificativo del soggetto proprietario dell'applicativo identificato tramite il clientId, secondo il formato previsto dal profilo di interoperabilità;

- tokenClientOrganization: nome del soggetto proprietario dell'applicativo identificato tramite il clientId;

- tokenClientOrganizationType: tipo del soggetto proprietario dell'applicativo identificato tramite il clientId;

- tokenClientOrganizationPropertiesKeys: nomi delle proprietà definite sul soggetto proprietario dell'applicativo identificato tramite il clientId;

- tokenClientOrganizationProperties: proprietà (nome=valore) definite sul soggetto separate da uno spazio;

- tokenClientOrganizationProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- tokenClientOrganizationProperty(nomeProprietà): valore della proprietà indicata come parametro.

- tokenUsername: username presente nel token;

- tokenMail: eMail presente nel token;

- tokenClaim(nomeClaim): valore del claim indicato come parametro e presente nel token;

.. note::
      Le informazioni seguenti sono presenti solamente se è stata abilitata la validazione JWT del token

- tokenRaw: JWT token presente nella richiesta; 

- tokenHeaderRaw: porzione dell'header relativa al token JWT presente nella richiesta, in formato base64; 

- tokenPayloadRaw: porzione del payload relativa al token JWT presente nella richiesta, in formato base64; 

- tokenDecodedHeader: contenuto decodificato dell'header presente nel token JWT; 

- tokenDecodedPayload: contenuto decodificato del payload presente nel token JWT; 

- tokenHeaderClaim(nomeClaim): valore del claim indicato come parametro e presente nell'header del token JWT;

- tokenPayloadClaim(nomeClaim): valore del claim indicato come parametro e presente nel payload del token JWT;
	
- tokenHeaderClaims(): claims (nome=valore) presenti nell'header del token JWT;
	
- tokenHeaderClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- tokenPayloadClaims(): claims (nome=valore) presenti nel payload del token JWT;
	
- tokenPayloadClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- tokenCertificateSubjectDN: distinguished name del subject relativo al certificato con cui è stato firmato il token JWT; 

- tokenCertificateSubjectCN: common name del subject relativo al certificato con cui è stato firmato il token JWT; 

- tokenCertificateSubjectDNInfo(String oid): ritorna l'informazione indicata come parametro relativa al subject del certificato con cui è stato firmato il token JWT;
	
- tokenCertificateIssuerDN: distinguished name dell'issuer relativo al certificato con cui è stato firmato il token JWT; 

- tokenCertificateIssuerCN: common name dell'issuer relativo al certificato con cui è stato firmato il token JWT; 

- tokenCertificateIssuerDNInfo(String oid): ritorna l'informazione indicata come parametro relativa all'issuer del certificato con cui è stato firmato il token JWT.

**Negoziazione Token**

- retrievedAccessToken: access token ottenuto dall'authorization server configurato nella Token Policy associata al connettore;

- retrievedTokenClaim(nomeClaim): valore del claim indicato come parametro e presente nella risposta ritornata dall'authorization server;

- retrievedTokenRequestTransactionId: identificativo della transazione che ha originato la richiesta verso l'authorization server;

- retrievedTokenRequestGrantType: tipo di grant type utilizzato nella negoziazione del token (clientCredentials, usernamePassword, rfc7523_x509, rfc7523_clientSecret);

- retrievedTokenRequestJwtClientAssertion: asserzione jwt generata durante una negoziazione con grant type 'rfc7523_x509';

- retrievedTokenRequestClientId: clientId utilizzato durante la negoziazione del token;

- retrievedTokenRequestClientToken: bearer token utilizzato durante la negoziazione del token;

- retrievedTokenRequestUsername: username utilizzato durante una negoziazione del token con grant type 'usernamePassword';

- retrievedTokenRequestUrl: endpoint dell'authorization server.
	
**Informazioni specifiche dei Profili di Interoperabilità**

- requestPropertiesKeys: nomi delle proprietà associate alla traccia della richiesta;

- requestProperties: proprietà (nome=valore), associate alla traccia della richiesta, separate da uno spazio;

- requestProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- requestProperty(nomeProprietà): valore della proprietà indicata come parametro;

- responsePropertiesKeys: nomi delle proprietà associate alla traccia della risposta;

- responseProperties: proprietà (nome=valore), associate alla traccia della risposta, separate da uno spazio;

- responseProperties(propertySeparator, valueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- responseProperty(nomeProprietà): valore della proprietà indicata come parametro.

**ModI**

- tokenModI<tokenType>Raw: security token presente nella richiesta; 

- tokenModI<tokenType>CertificateSubjectDN: distinguished name del subject relativo al certificato con cui è stato firmato il security token; 

- tokenModI<tokenType>CertificateSubjectCN: common name del subject relativo al certificato con cui è stato firmato il security token; 

- tokenModI<tokenType>CertificateSubjectDNInfo(String oid): ritorna l'informazione indicata come parametro relativa al subject del certificato con cui è stato firmato il security token;
	
- tokenModI<tokenType>CertificateIssuerDN: distinguished name dell'issuer relativo al certificato con cui è stato firmato il security token; 

- tokenModI<tokenType>CertificateIssuerCN: common name dell'issuer relativo al certificato con cui è stato firmato il security token; 

- tokenModI<tokenType>CertificateIssuerDNInfo(String oid): ritorna l'informazione indicata come parametro relativa all'issuer del certificato con cui è stato firmato il security token.

I tipi di token disponibili sono:

- Authorization: security token ricevuto nell'header HTTP 'Authorization';

- Integrity: security token ricevuto nell'header HTTP 'Agid-JWT-Signature';

- Audit: security token ricevuto nell'header HTTP 'Agid-JWT-TrackingEvidence';

- Soap: security token ricevuto nell'header SOAP;

Per i tipi di token 'Authorization' e 'Integrity', relativi ad API di tipo REST, sono disponibili anche le seguenti informazioni:

- tokenModI<tokenType>HeaderRaw: porzione dell'header relativa al security token presente nella richiesta, in formato base64; 

- tokenModI<tokenType>PayloadRaw: porzione del payload relativa al security token presente nella richiesta, in formato base64; 

- tokenModI<tokenType>DecodedHeader: contenuto decodificato dell'header presente nel security token; 

- tokenModI<tokenType>DecodedPayload: contenuto decodificato del payload presente nel security token; 

- tokenModI<tokenType>HeaderClaim(nomeClaim): valore del claim indicato come parametro e presente nell'header del security token;

- tokenModI<tokenType>PayloadClaim(nomeClaim): valore del claim indicato come parametro e presente nel payload del security token;
	
- tokenModI<tokenType>HeaderClaims(): claims (nome=valore) presenti nell'header del security token;
	
- tokenModI<tokenType>HeaderClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

- tokenModI<tokenType>PayloadClaims(): claims (nome=valore) presenti nel payload del security token;
	
- tokenModI<tokenType>PayloadClaims(claimSeparator, nameValueSeparator): simile alla precedente opzione, consente di indicare i separatori utilizzati;

**PDND**

Di seguito le indicazioni su come accedere alle informazioni riguardanti il client e l'organizzazione recuperate tramite le API PDND.

- pdndOrganizationName: nome dell'organizzazione a cui appartiene il client; 

- pdndOrganizationId: identificativo PDND (uuid) dell'organizzazione a cui appartiene il client; 

- pdndOrganizationCategory: categoria in cui è stata classificata dalla PDND l'organizzazione a cui appartiene il client; 

- pdndOrganizationExternalId e pdndOrganizationExternalOrigin: rispettivamente identificativo dell'organizzazione e tipo di repository esterno a cui l'identificativo appartiene; 

- pdndOrganizationJson: consente di ottenere la risposta json ottenuta dalla PDND invocando l'operazione 'GET /organizations/{organizationId}'.

- pdndClientId: identificativo PDND (uuid) del client; 

- pdndClientConsumerId: identificativo PDND (uuid) dell'organizzazione a cui appartiene il client; 

- pdndClientJson: consente di ottenere la risposta json ottenuta dalla PDND invocando l'operazione 'GET /clients/{clientId}'; 


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

- <messageType>Header(name, multiValueSeparator): elenco di valori, separati con il carattere indicato nel parametro 'multiValueSeparator', relativi agli header http che possiedono il nome indicato dal parametro 'name';

- <messageType>Headers: elenco degli headers http nel formato <nome>=<valore> separati dal carattere ',' ;

- <messageType>Headers(headersSeparator, nameValueSeparator, prefix, suffix): i parametri permettono di personalizzare il formato degli headers http.

I tipi di messaggi disponibili sono:

- inRequest: richiesta ricevuta sul gateway;

- outRequest: richiesta inoltrata al backend;

- inResponse: risposta ricevuta dal backend;

- outResponse: risposta ritornata a client.

.. note::
      Le informazioni sui 4 tipi di messaggio saranno disponibili solamente se è stata abilitata la funzionalità di dump per ciascun tipo nel file di configurazione locale '/etc/govway/govway_local.properties' (assumendo sia /etc/govway la directory di configurazione indicata in fase di installazione) o tramite le :ref:`configProprieta` come indicato in :ref:`avanzate_fileTrace`. 
      Di seguito un estratto della configurazione globale che riporta l'abilitazione dei 4 tipi:

         ::

            # ================================================
            # FileTrace
            ...
            #
            # Indicazione se nella funzionalità è consentito l'accesso ai contenuti
            # -- Fruizioni --
            # inRequest/outResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.payload.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.headers.enabled=true
            # outRequest/inResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.connettore.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.connettore.payload.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.connettore.headers.enabled=true
            # -- Erogazioni --
            # inRequest/outResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.payload.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.headers.enabled=true
            # outRequest/inResponse
            org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.connettore.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.connettore.payload.enabled=true
            #org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.connettore.headers.enabled=true
            ...
	

**Ambiente**

- hostAddress(): InetAddress.getLocalHost().getHostAddress();

- hostName(): InetAddress.getLocalHost().getHostName();

- systemProperty(nomeProprietà): valore della proprietà di sistema indicata come parametro;

- javaProperty(nomeProprietà): valore della proprietà java indicata come parametro.
