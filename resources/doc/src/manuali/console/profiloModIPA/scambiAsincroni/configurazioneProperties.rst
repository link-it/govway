.. _modipa_scambiAsincroni_properties:

Configurazione Avanzata
-----------------------

I valori di default utilizzati negli scambi di dati asincroni sono modificabili tramite le proprietà descritte di seguito, da indicare nei file di configurazione locali presenti nella directory '/etc/govway' (si assume che '/etc/govway' sia la directory di configurazione indicata in fase di installazione).

**Profilo ModI (file 'modipa_local.properties')**

- *org.openspcoop2.protocol.modipa.pdnd.async.purposeId.getResourceConfirmation* (default: true): indica se il 'purposeId' viene inserito nella richiesta del voucher per l'ottenimento della risposta e per la conferma di ricezione; corrisponde al valore *Default* del campo *Invio PurposeId* della fruizione.

- *org.openspcoop2.protocol.modipa.pdnd.async.httpStatus.successo* (default: 200-299): codici HTTP con cui una fase viene considerata completata; corrisponde al valore *Default* del campo *Esito Positivo* di fruizioni ed erogazioni.

- *org.openspcoop2.protocol.modipa.pdnd.async.urlCallback.header.name* (default: GovWay-PDND-Url-Callback): nome dell'header HTTP con cui la URL di callback viene inoltrata all'applicativo erogatore.

- *org.openspcoop2.protocol.modipa.pdnd.async.urlCallback.header.encoding* (default: none): codifica del valore dell'header precedente ('none', 'base64' o 'hex'); corrisponde al valore *Default* del campo *Codifica Header* dell'erogazione.

- *org.openspcoop2.protocol.modipa.pdnd.async.entityNumber.header.name* (default: GovWay-PDND-Entity-Number): nome dell'header HTTP con cui il numero di entità viene inoltrato all'applicativo che implementa l'API di callback.

**Svecchiamento delle interazioni (file 'govway_local.properties')**

Lo svecchiamento delle interazioni non più utilizzabili viene effettuato da un timer, avviato solamente se è presente il profilo ModI, che le elimina dalla base dati:

- *org.openspcoop2.pdd.gestoreInterazioniAsincronePDND.enabled* (default: true): abilita il timer;

- *org.openspcoop2.pdd.gestoreInterazioniAsincronePDND.timer.intervalloSecondi* (default: 3600, un'ora): intervallo, in secondi, tra due verifiche successive;

- *org.openspcoop2.pdd.gestoreInterazioniAsincronePDND.conservazioneSecondi* (default: 604800, 7 giorni): secondi per cui un'interazione viene conservata dopo la sua scadenza, calcolata rispetto ai tempi indicati nell'API; per le interazioni senza scadenza il tempo è calcolato rispetto all'ultimo aggiornamento. Durante il periodo di conservazione le richieste ricevono un errore che indica la scadenza della fase, invece di un'interazione non trovata;

- *org.openspcoop2.pdd.gestoreInterazioniAsincronePDND.debug* (default: false): abilita il livello di debug.

**Selezione delle API (file 'console_local.properties' e 'rs-api-config_local.properties')**

- *modipa.selezioneApi.escludiScambiAsincroniIncompleti* (default: true): nella creazione di erogazioni e fruizioni, e nel cambio di versione dell'API implementata, non vengono ammesse le API configurate per gli scambi di dati asincroni con risorse o azioni non associate a tutte le fasi previste (per le API SOAP i servizi incompleti). La proprietà è presente sia nella configurazione della console che in quella della API di configurazione.
