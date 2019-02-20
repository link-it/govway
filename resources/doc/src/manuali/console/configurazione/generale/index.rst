.. _configGenerale:

Generale
--------

La sezione *Configurazione > Generale* consente di impostare i parametri
generali per le funzionalità di base del gateway (:numref:`configurazioneFig`). In particolare è
possibile:

-  Attivare e configurare la modalità Multi-Tenant. Abilitando questa
   modalità sarà ammessa la creazione di ulteriori soggetti interni al
   dominio GovWay.

-  Configurare i profili fornendo i riferimenti ai servizi di base per
   l'elaborazione dei messaggi ed al soggetto interno

-  Configurare la gestione del CORS (*cross-origin HTTP request (CORS)*)
   a livello globale valido per tutte le APIs

-  Configurare il Caching Risposta a livello globale valido per tutte le
   APIs

   .. figure:: ../../_figure_console/ConfigurazioneGenerale.png
    :scale: 100%
    :align: center
    :name: configurazioneFig

    Maschera per l’impostazione dei parametri di configurazione generale

.. toctree::
        :maxdepth: 2

	multiTenant
	profili
	cors
	cachingRisposta
