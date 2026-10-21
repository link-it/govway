Miglioramenti all'integrazione con la PDND
------------------------------------------------------

**Scambi asincroni**

Aggiunto il supporto agli `scambi di dati asincroni PDND <https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/e-service/scambi-di-dati-asincroni>`__ (risposta differita tramite callback): un'API può essere configurata come 'Erogazione dati' o come 'Callback', associando le risorse alle fasi previste (start_interaction, callback_invocation, get_resource, confirmation). Il gateway negozia i voucher PDND con i claim di ciascuna fase, verifica la URL di callback e lo stato dell'interazione; le interazioni sono consultabili in console e la configurazione è gestibile anche tramite le API di configurazione.

**Signal Hub**

La funzionalità di `Signal Hub <https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ è stata rivista nei seguenti aspetti:

- l'applicativo pubblicatore dei segnali viene adesso riconosciuto anche tramite l'autenticazione token, oltre che tramite l'autenticazione di trasporto;

- nella console, alla voce 'Configurazione > Cache PDND > Semi Signal Hub', è possibile consultare i semi di pseudoanonimizzazione delle erogazioni e forzarne la rotazione.
