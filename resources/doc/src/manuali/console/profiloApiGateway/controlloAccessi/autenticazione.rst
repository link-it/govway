.. _apiGwAutenticazione:

Autenticazione Trasporto
^^^^^^^^^^^^^^^^^^^^^^^^

In questa sezione è possibile configurare il meccanismo di
autenticazione richiesto per l'accesso al servizio. 

.. figure:: ../../_figure_console/Autenticazione.png
    :scale: 100%
    :align: center
    :name: autenticazione

    Configurazione dell’autenticazione del servizio

Come mostrato in :numref:`autenticazione` la configurazione dell'autenticazione deve essere effettuata attraverso la selezione di un tipo di autenticazione tra quelli disponibili:

**- disabilitato**

Nessuna autenticazione.

**- https** 

(:numref:`controlloAccessiAutenticazioneHttps`)
La richiesta deve possedere un certificato client X509. La presenza del certificato client nella richiesta è obbligatoria a meno che non sia abilitato il flag *Opzionale*. Per maggiori informazioni sulla configurazione necessaria affinchè il certificato client sia ricevuto dal gateway si faccia riferimento alla sezione :ref:`install_ssl_server` della 'Guida di Installazione'.

Se è presente un certificato client, il gateway cercherà di identificare un applicativo o un soggetto a cui è stato associato il certificato come credenziale di accesso (per ulteriori dettagli si rimanda alle sezioni :ref:`soggetto` e :ref:`applicativo`); l'identificazione non è obbligatoria ma nel caso avvenga con successo l'applicativo o il soggetto verrà registrato nei log e potrà essere utilizzato anche ai fini di autorizzazione puntuale e per ruoli (:ref:`apiGwAutorizzazione`).
	
.. _controlloAccessiAutenticazioneHttps:

.. figure:: ../../_figure_console/controlloAccessiAutenticazioneHttps.png
    :scale: 80%
    :align: center

    Configurazione Autenticazione 'https'

