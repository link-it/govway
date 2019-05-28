Nuova funzionalità di Trasformazione dei Messaggi
-------------------------------------------------

Aggiunta la funzionalità di trasformazione dei messaggi in
transito. È possibile intervenire sugli header http, sui parametri
della url, sui contenuti scambiati e sul codice di risposta, tramite
varie modalità di trasformazione:

-  *Header HTTP*: è possibile aggiungere nuovi header oppure modificare o eliminare quelli esistenti sia sulla richiesta che sulla risposta. I valori forniti possono essere statici o possono contenere parti dinamiche risolte a runtime dal Gateway.

-  *Parametri della URL*: è possibile aggiungere nuovi parametri oppure modificare o eliminare quelli esistenti. I valori forniti possono essere statici o possono contenere parti dinamiche risolte a runtime.

-  *Payload HTTP*: la funzionalità consente di modificare il payload della richiesta e/o della risposta. È possibile indicare la generazione di un payload vuoto o fornire un nuovo payload definito tramite una delle seguenti modalità:

   - *GovWay Template*: file contenente parti dinamiche risolte a runtime in maniera analoga agli header http e ai parametri della url.
   - *Freemarker Template*: template dinamico che può utilizzare i costrutti supportati da 'Freemarker' ( https://freemarker.apache.org/ ).
   - *Velocity Template*: template dinamico che può utilizzare i costrutti supportati da 'Velocity' ( http://velocity.apache.org/ ).
   - *XSLT*: fogli di stile XSLT utilizzabili su messaggi di tipo XML o SOAP.

-  *Trasformazione di Protocollo*: è possibile effettuare
   trasformazioni di protocollo da SOAP a REST o viceversa,
   permettendo anche di fruire o erogare lo stesso servizio in
   entrambe le modalità.

Le regole di trasformazione sono soggette ai seguenti criteri di applicabilità:

- *Elenco Risorse*: indicazione puntuale di una o più risorse a cui la trasformazione deve essere applicata.
- *Elenco Soggetti e/o Applicativi*: indicazione puntuale di uno o più soggetti e/o applicativi mittenti.
- *Content-Type*: indicazione del Content-Type della richiesta.
- *Espressione XPath o JsonPath*: espressione applicata sul messaggio di richiesta. La trasformazione viene applicata in caso di match.

All'interno di una regola di trasformazione, è possibile poi applicare trasformazioni diverse della risposta ottenuta in funzione di:

- *Codice Risposta*: codice di risposta http.
- *Content-Type*: indicazione sul Content-Type della risposta.
- *Espressione XPath o JsonPath*: espressione applicata sul messaggio di risposta. La trasformazione viene applicata in caso di match.
