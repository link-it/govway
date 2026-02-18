Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Aggiunto il supporto completo per l'integrazione con le API di Interoperabilità PDND versione 2, promossa come versione predefinita per le nuove installazioni.

Aggiunta opzione per le API REST che implementano il pattern [BULK_RESOURCE_REST] (Linee Guida di Interoperabilità, Allegato 1 - Sezione 7.1) che consente di preservare l'header Content-Length nelle risposte inoltrate al client, necessario per il corretto funzionamento delle Range Requests (RFC 9110, Section 14).

Introdotto miglioramento della funzionalità SignalHub con aggiunta del supporto per la configurazione senza pseudoanonimizzazione nelle erogazioni e relativi aggiornamenti a console, API REST e documentazione.

Sono infine stati apportati dei miglioramenti prestazionali alla funzionalità di Tracing PDND. Inoltre nella sezione 'Statistiche PDND Tracing' della console di monitoraggio è stata aggiunta la possibilità di effettuare il download del dettaglio degli errori segnalati dalla PDND.
Nell'intervento è stata risolta un'anomalia che si presentava durante la generazione del tracciato e riportava il seguente errore nei file di log: "Soggetto '-' non esistente nella configurazione PdndTracciamento (ricerca per aggregato)".
