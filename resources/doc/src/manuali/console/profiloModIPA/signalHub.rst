.. _modipa_signalHub:

Signal Hub
----------------------

`Signal Hub <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ è una funzionalità offerta dalla PDND che permette di rimanere aggiornati sulle modifiche dei dati di servizi registrati sulla PDND. Maggiorni informazioni sul servizio vengono fornite in :doc:`signalHub/panoramica` e nel `Manuale Operativo Signal Hub <https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub>`__ sul sito della PDND.

Con il prodotto vengono fornite built-in 2 fruizioni verso il servizio Signal-Hub della PDND:

- *api-pdnd-push-signals*: GovWay gestisce l’integrazione con la funzionalità Signal-Hub, rendendola trasparente sia per il soggetto erogatore che per l’e-service, come descritto nella sezione :doc:`signalHub/configurazioneConsole`;

- *api-pdnd-pull-signals*: viene fornita per consentire l’integrazione verso la pull dei segnali, demandando alle applicazioni client la gestione della pseudonimizzazione dei dati di interesse.

Alcuni aspetti di configurazione avanzata, relativamente alla funzionalità di integrazione di Signal-Hub con GovWay, vengono forniti nella sezione :doc:`signalHub/configurazioneProperties`.


.. toctree::
    :maxdepth: 2

    signalHub/panoramica
    signalHub/configurazioneConsole
    signalHub/configurazioneProperties
