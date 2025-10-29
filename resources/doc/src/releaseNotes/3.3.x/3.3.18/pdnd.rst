Miglioramenti all'integrazione con la PDND
------------------------------------------------------

**Tracing**

La funzionalità di `Tracing PDND <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-tracing>`__  è stato rivista nei seguenti aspetti:

- È stata introdotta la possibilità di aggregare, prima della pubblicazione, le informazioni statistiche provenienti da più soggetti operativi in un unico record. Questa estensione consente di supportare scenari in cui più soggetti operativi non corrispondano necessariamente a enti distinti registrati sulla PDND.

- In presenza di un pattern di tipo INTEGRITY, la configurazione predefinita di GovWay prevedeva l’utilizzo dell’identificativo del token di integrità come identificativo del messaggio. Tuttavia, poiché il report di tracciamento PDND si basa su tale identificativo, il valore riportato nel tracciamento non coincideva con il JTI del voucher, ovvero con l’identificativo generato dalla PDND stessa. L’anomalia è stata risolta introducendo il tracciamento esplicito dell’identificativo del token, che viene ora correttamente utilizzato nella generazione del file CSV destinato al tracciamento PDND.

- È stato normalizzato l’header Content-Type nelle richieste multipart/form-data, sostituendo eventuali caratteri HTAB con SP negli OWS attorno al punto e virgola (;) e tra il tipo e il boundary. In alcuni ambienti, dove l’application server non effettuava la normalizzazione automatica degli header HTTP, tale anomalia causava il fallimento della pubblicazione dei record verso la PDND, con errori del tipo:

  [{"code":"BAD_REQUEST_ERROR","detail":"Validation error: Required at \"file\""},
  {"code":"BAD_REQUEST_ERROR","detail":"Validation error: Required at \"date\""}]

  L'errore avveniva a causa della presenza di tab nel `Content-Type`, non accettati dal server PDND di Tracing.


**API Interoperabilità**

Aggiunto supporto all'integrazione delle api di interoperabilità v2 per:

- recupero delle chiavi: utilizzo delle risorse 'GET /keys/{kid}' per le chiavi client e 'GET /producerKeys/{kid}/' per le chiavi server;
- recupero delle informazioni del client e dell'organizzazione: utilizzo delle risorse 'GET /clients/{clientId}' e 'GET /tenants/{tenantId}'
