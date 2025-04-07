.. _byokInstallKmsRemoto:

KMS Remoto
-------------------------------------------------------

In questa sezione viene descritta la sintassi da utilizzare per definire i KMS funzionali ad operazioni di cifratura (wrap) o di decifratura (unwrap) dove la master key  è depositata su un servizio remoto (es. in cloud) e le operazioni di wrap e unwrap sono rese disponibili dal servizio remoto tramite chiamate di API.

Di seguito un esempio di KMS basato sulla chiamata dell'operazione wrap in cui l'informazione da cifrare viene fornita nel payload http come json payload e l'informazione cifrata viene ritornata all'interno di una risposta json nel claim 'value'.

::

    # Esempio di KMS Wrap
    kms.govway-remote-wrap.label=GovWay Remote Wrap Example
    kms.govway-remote-wrap.type=govway-remote-wrap
    kms.govway-remote-wrap.mode=wrap
    kms.govway-remote-wrap.encryptionMode=remote
    kms.govway-remote-wrap.http.endpoint=https://vault.example/keys/wrapkey?api-version=1.0
    kms.govway-remote-wrap.http.method=POST
    kms.govway-remote-wrap.http.payload.inline={"alg": "RSA1_5","value": "${kms-key}"}
    kms.govway-remote-wrap.http.response.jsonPath=$.value
    
Un esempio di KMS basato sulla chiamata dell'operazione unwrap in cui l'informazione da decifrare viene fornita come parametro 'key' della query url, e l'informazione decifrata viene tornata nel payload http codificata in base64. Viene inoltre attivata una gestione personalizzata del protocollo https dove viene indicato un truststore custom per effettuare l'autenticazione server.

::
    
    # Esempio di KMS Unwrap
    kms.govway-remote-unwrap.label=GovWay Remote Unwrap Example
    kms.govway-remote-unwrap.type=govway-remote-unwrap
    kms.govway-remote-unwrap.mode=unwrap
    kms.govway-remote-unwrap.encryptionMode=remote
    kms.govway-remote-unwrap.http.endpoint=https://vault.example/keys/unwrapkey?api-version=1.0&key=${kms-urlencoded-key}
    kms.govway-remote-unwrap.http.method=GET
    kms.govway-remote-unwrap.http.username=test
    kms.govway-remote-unwrap.http.password=changeme
    kms.govway-remote-unwrap.http.response.base64Encoded=true
    kms.govway-remote-unwrap.https=true
    kms.govway-remote-unwrap.https.hostnameVerifier=true
    kms.govway-remote-unwrap.https.serverAuth=true
    kms.govway-remote-unwrap.https.serverAuth.trustStore.path=/tmp/test.jks
    kms.govway-remote-unwrap.https.serverAuth.trustStore.type=jks
    kms.govway-remote-unwrap.https.serverAuth.trustStore.password=123456

L'operazione viene descritta da un insieme di direttive definite tramite la sintassi:

- '*kms.<idKms>.http.<direttiva>*'

Di seguito vengono fornite tutte le direttive supportate per la gestione della richiesta http:

- *endpoint* [required]: definisce l'endpoint del kms;

- *method* [required]: definisce il metodo HTTP utilzzato per connettersi al kms;

- *header.<nome>* [optional]: consente di definire un header http con nome '<name>' valorizzato con il valore indicato nella proprietà;

- *payload.inline*: [optional] payload inviato nella richiesta http;

- *payload.path*: [optional; ignorata se presente 'http.payload.inline'] path su filesystem relativo al contenuto da inviare nella richiesta http;

- *payload.username* e *http.payload.password*: [optional] definiscono la credenziale http-basic;

- *connectionTimeout*: [optional; int] tempo massimo in millisecondi di attesa per stabilire una connessione con il server;

- *readTimeout*: [optional; int] tempo massimo in millisecondi di attesa per la ricezione di una risposta dal server.

L'informazione cifrata o decifrata viene attesa per default nel payload della risposta http. È possibile configurare comportamenti differenti tramite le seguenti proprietà:

- *response.base64Encoded*: [optional; boolean] indicazione se sarà attesa una risposta codificata in base64;

- *response.hexEncoded*: [optional; boolean] indicazione se sarà attesa una risposta codificata tramite una rappresentazione esadecimale;

- *response.jsonPath*: [optional] se la risposta è un json (eventualmente dopo la decodificata base64/hex) consente di indicare un jsonPath per estrarre l'informazione da un singolo elemento.
	
- *response.jsonPath.base64Encoded*: [optional; boolean] indicazione se sarà atteso un valore, estratto tramite jsonPath, codificato in base64;

- *response.jsonPath.hexEncoded*: [optional; boolean] indicazione se sarà atteso un valore, estratto tramite jsonPath, codificato tramite una rappresentazione esadecimale;	

Inoltre se l'endpoint contattato è su protocollo https, può essere attivata una gestione personalizzata dell'autenticazione server e/o client definendo la seguente proprietà:

- kms.<idKMS>.https=true

Tutte le configurazioni relative al protocollo https possono essere fornite utilizzando le seguenti ulteriori direttive definibili tramite la sintassi:

- '*kms.<idKms>.https.<direttivaHttps>*'

Di seguito vengono fornite tutte le direttive https supportate:

- *hostnameVerifier*: [optional; boolean] indica se deve essere verificato l'hostname rispetto al certificato server;

- *serverAuth*: [optional; boolean] indica se deve essere effettuata l'autenticazione del certificato server; nel caso venga abilitata la verifica possono essere forniti i seguenti parametri:

     - *serverAuth.trustStore.path*: path su filesystem al truststore;
     
     - *serverAuth.trustStore.type*: tipo del truststore;
     
     - *serverAuth.trustStore.password*: password del truststore;
     
     - *serverAuth.trustStore.crls*: path su filesystem delle CRL;
     
     - *serverAuth.trustStore.ocspPolicy*: identificativo di una OCSP Policy (:ref:`ocspInstall`);

- *clientAuth*: [optional; boolean] indica se deve essere inviato un certificato client; nel caso venga abilitata l'autenticazione client possono essere forniti i seguenti parametri:

     - *clientAuth.keyStore.path*: path su filesystem al keystore;
     
     - *clientAuth.keyStore.type*: tipo di keystore;
     
     - *clientAuth.keyStore.password*: password per l'accesso al keystore;
     
     - *clientAuth.key.alias*: alias che identifica il certificato client nel keystore;
     
     - *clientAuth.key.password*: password della chiave privata.
	 
	  
    

