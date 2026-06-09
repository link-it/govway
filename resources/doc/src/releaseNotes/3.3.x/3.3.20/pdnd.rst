Miglioramenti all'integrazione con la PDND
------------------------------------------------------

**Signal Hub**

La funzionalità di `Signal Hub <https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__  è stato rivista nei seguenti aspetti:

- aggiornata la documentazione di SignalHub relativa alla pubblicazione dei segnali e aggiunta la specifica OpenAPI che ne descrive le diverse modalità;

- aggiunta la documentazione su formato delle informazioni crittografiche (seme e identificativo) e ordine di concatenazione input/seed nel calcolo del digest, per generazione e verifica come richiesto dal Manuale Operativo Signal Hub AGID/PDND;

- la risorsa di pseudoanonimizzazione può ora esporre il 'signalId' del segnale SEEDUPDATE che ha introdotto il seme corrente (valorizzato a '0' per il seme iniziale), consentendo al consumer di individuare il primo segnale utile da cui iniziare la lettura.

- risolta anomalia presente nella risorsa di pseudonimizzazione (REST/SOAP) la quale esponeva, per la funzione hash utilizzata, il nome dell'enum interno DigestType anziché l'identificativo standard JCA (es. SHA512_256 → SHA-512/256).

**API Interoperabilità**

Aggiunto il supporto all'integrazione con le API di Interoperabilità v3, con l'invio del DPoP nelle richieste e la validazione del token di integrity restituito nelle risposte.
