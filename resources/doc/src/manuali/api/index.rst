===
API
===

Le principali funzionalità di Govway, normalmente accedute tramite le console grafiche, sono disponibili anche per l'utilizzo per via applicativa tramite apposite API.
Le API di Govway, basate sul protocollo REST, permettono ad esempio di realizzare strumenti personalizzati, nel dominio applicativo dell'utente, per eseguire le principali operazioni di gestione e monitoraggio.

Le interfacce delle API sono descritte in formato OpenAPI e sono consultabili ai seguenti indirizzi:

    + `API di Configurazione <https://generator.swagger.io/?url=https://raw.githubusercontent.com/link-it/govway/master/tools/rs/config/server/src/schemi/merge/govway_rs-api_config.yaml>`_: consentono di effettuare le operazioni di configurazione del registro di Govway disponibili anche tramite la console 'govwayConsole'.
    + `API di Monitoraggio <https://generator.swagger.io/?url=https://raw.githubusercontent.com/link-it/govway/master/tools/rs/monitor/server/src/schemi/merge/govway_rs-api_monitor.yaml>`_: consente di accedere a tutte le funzionalità di monitoraggio e statistica accedibili anche tramite console 'govwayMonitor'.

L'utilizzo di tali API è abilitato se in fase di installazione di Govway è stata effettuata una scelta in tal senso (vedi :ref:`inst_installer_nuova`). Nel caso siano state installate le API, i relativi servizi saranno accessibili tramite protocollo REST, le cui interfacce OpenAPI 3 sono consultabili seguendo i link riportati in seguito.

Per poter invocare i servizi è necessario completare una fase iniziale di configurazione per abilitare il relativo controllo degli accessi (seguire le indicazioni riportate nella Guida di Installazione, nella sezione 'Finalizzazione dell’Installazione' al paragrafo :ref:`apiRest`). Infatti saranno presenti in configurazione le seguenti erogazioni:

- api-config v1

   .. figure:: _figure_api/api-config-detail.png
    :scale: 80%
    :align: center
    :name: api_config_v1_fig

    Dettaglio dell'erogazione api-config v1

- api-monitor v1

   .. figure:: _figure_api/api-monitor-detail.png
    :scale: 80%
    :align: center
    :name: api_monitor_v1_fig

    Dettaglio dell'erogazione api-monitor v1

Nel dettaglio delle relative erogazioni è comunque visibile la "URL Invocazione" (Base URL) necessaria per accedere ai servizi.
