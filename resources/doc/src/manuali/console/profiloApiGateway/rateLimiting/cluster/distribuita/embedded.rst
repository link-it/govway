.. _headerGWRateLimitingCluster_distribuita_embedded:

Embedded
~~~~~~~~~~

Il conteggio delle metriche viene effettuato tramite un archivio dati distribuito implementato sul database di GovWay ed avviene tramite una tecnica di sincronizzazione con misurazione esatta.

La soluzione viene fornita per ambienti di test in cui si desidera provare le funzionalità di rate limiting distribuito.

   .. note::
       La soluzione 'embedded' non è utilizzabile su ambienti di produzione.

Per attivarlo impostare una sincronizzazione '*Distribuita*' e scegliere l'implementazione '*Embedded*'.

  .. figure:: ../../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingEmbedded.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingEmbedded

    Sincronizzazione Distribuita 'Embedded' con misurazione delle metriche esatta
