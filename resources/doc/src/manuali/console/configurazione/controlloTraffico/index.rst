.. _traffico:

Controllo del Traffico
----------------------

Accedendo la sezione *Configurazione > Controllo del Traffico* si
possono impostare i parametri di configurazione relativamente alla
fuzionalità che consente di stabilire le politiche di accesso alle
risorse del gateway, nell'ottica di amministrare le risorse applicative
a disposizione, ottimizzando le prestazioni e gestendo le situazioni di
picco.

   .. figure:: ../../_figure_console/ControlloTraffico.png
    :scale: 50%
    :align: center
    :name: trafficoSetup

    Maschera per l’impostazione dei parametri di controllo del traffico

La configurazione della funzionalità di controllo del traffico
(:numref:`trafficoSetup`) si compone dei seguenti gruppi di configurazioni:

-  *Limitazione Numero di Richieste Complessive*: consente di fissare un
   numero limite, riguardo le richieste gestibili simultaneamente dal
   gateway, bloccando le richieste in eccesso.

-  *Controllo della Congestione*: consente di attivare il rilevamento
   dello stato di congestionamento del gateway, in seguito al
   superamento di una determinata soglia relativamente alle richieste
   simultanee.

-  *Rate Limiting*: sezione per l'impostazione di policy al fine di
   attivare strategie di controllo del traffico con criteri di selezione
   specifici della singola richiesta.

-  *Tempi Risposta*: sezione per l'impostazione dei valori limite
   relativi ai tempi di risposta dei servizi, sia nei casi di erogazione
   che di fruizione.

Le sezioni seguenti dettagliano questi elementi di configurazione.

.. toctree::
    :maxdepth: 2

    limitazioneNumeroRichieste
    controlloCongestione
    rateLimiting
    tempiRisposta
