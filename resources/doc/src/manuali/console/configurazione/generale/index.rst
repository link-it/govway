.. _configGenerale:

Generale
--------

La sezione *Configurazione > Generale* consente di impostare i parametri
generali per le funzionalità di base del gateway (:numref:`configurazioneFig`). In particolare è
possibile:

-  Attivare e configurare la modalità Multi-Tenant. Abilitando questa
   modalità sarà ammessa la creazione di ulteriori soggetti interni al
   dominio GovWay.

-  Configurare le Base URL utilizzate per visulizzare le URL di invocazione delle API

-  Configurare la gestione del CORS (*cross-origin HTTP request (CORS)*)
   a livello globale valido per tutte le APIs

-  Configurare il Caching Risposta a livello globale valido per tutte le
   APIs

-  Configurare i profili fornendo i riferimenti ai servizi di base per
   l'elaborazione dei messaggi ed al soggetto interno

-  Attivare e configurare la modalità Canali in una installazione composta da più nodi in Load Balancing.
   Abilitando questa modalità sarà possibile assegnare uno o più canali ad ogni nodo che compone il cluster e suddividere le API in canali di appartenenza.
   Su ogni nodo saranno autorizzate ad essere invocate solamente le API che possiedono un canale corrispondente alla configurazione del nodo.

-  Configurare proprietà di sistema

   .. figure:: ../../_figure_console/ConfigurazioneGenerale.png
    :scale: 100%
    :align: center
    :name: configurazioneFig

    Maschera per l’impostazione dei parametri di configurazione generale (1/2)

   .. figure:: ../../_figure_console/ConfigurazioneGeneraleExt.png
    :scale: 80%
    :align: center
    :name: configurazioneExtFig

    Maschera per l’impostazione dei parametri di configurazione generale (2/2)

.. toctree::
        :maxdepth: 2

	multiTenant
	urlInvocazione/index
	cors
	cachingRisposta
	profili
	canali/index
	proprieta
