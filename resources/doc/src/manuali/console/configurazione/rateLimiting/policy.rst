.. _trafficoPolicy:

Policy Globali
^^^^^^^^^^^^^^

Questa sezione consente di definire le policy di rate limiting che hanno un raggio d'azione che supera la singola erogazione/fruizione ed effettua quindi valutazioni su un campo più ampio.

L'attivazione di una Policy Globale segue in prevalenza il medesimo criterio già descritto nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy` riguardo il caso della configurazione di una singola erogazione/fruizione. Vi sono però alcune differenze che riguadano i criteri di raggruppamento, per il calcolo dei valori di soglia, e i criteri di filtro per l'applicabilità della policy.


Raggruppamento
++++++++++++++

Come descritto nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy` è possibile definire dei criteri di raggruppamento che consentono di verificare i valori di soglia. La logica è del tutto analoga a quella dell'operatore GROUP BY del linguaggio SQL.

I criteri di raggruppamento, per una policy a livello globale, sono maggiori rispetto a quelli descritti in precedenza (:numref:`policyRateLimitingCollezionamento`):

   -  *Fruizione/Erogazione*

   -  *Soggetto Erogatore*

   -  *API*

   -  *Azione/Risorsa*

   -  *Soggetto Fruitore*

   -  *Applicativo Fruitore*

   -  *Token*

   -  *Raggruppamento per Chiave*

   .. figure:: ../../_figure_console/PolicyGlobaliRaggruppamento.png
    :scale: 100%
    :align: center
    :name: policyRateLimitingCollezionamento

    Definizione criteri di raggruppamento per la policy di rate limiting


Filtro
++++++

Abilitando questa sezione è possibile indicare i criteri affinché la policy sia applicabile in base alle caratteristiche di ciascuna richiesta in ingresso. In assenza di filtro, la policy sarà valutata su tutte le richieste in ingresso che riguardano l’erogazione/fruizione che si sta configurando. I criteri di filtro, per una policy a livello globale, sono maggiori rispetto a quelli descritti in precedenza nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy` (:numref:`policyRateLimitingFiltroFig`):

   -  *Stato*: Opzione per abilitare/disabilitare il filtro.

   -  *Ruolo Gateway*: Opzione per filtrare le richieste di servizio in
      base al ruolo ricoperto dal gateway nella specifica richiesta:
      Fruitore o Erogatore.

   -  *Profilo*: Opzione per filtrare le richieste di servizio in base
      al profilo di utilizzo del Gateway. Nel caso si sia selezionata un singolo
      profilo (o se il gateway ne supporta uno solo) viene
      visualizzato il valore attuale in modo non modificabile.

   -  *Ruolo Erogatore*: Opzione per filtrare le richieste di servizio
      in base al ruolo posseduto dal soggetto erogatore. Tramite la
      lista è possibile selezionare uno tra i ruoli censiti nel
      registro. La selezione di un ruolo esclude la possibilità di
      selezionare un soggetto erogatore.

   -  *Soggetto Erogatore*: Opzione per filtrare le richieste di
      servizio in base al soggetto erogatore. Tramite la lista è
      possibile selezionare uno tra i soggetti censiti nel registro. La
      selezione di un soggetto esclude la possibilità di selezionare un
      ruolo erogatore.

   -  *API*: Opzione per filtrare le richieste in base
      alla API invocata. Tramite la lista è possibile selezionare una
      tra le API censite nel registro. Se è stato selezionato un
      soggetto erogatore, saranno elencati solo le API da esso
      erogate. Analogamente, se è stato selezionato un profilo,
      saranno elencate solo API relative a tale profilo.

   -  *Azione/Risorsa*: Opzione per filtrare le richieste di servizio in base
      all'azione/risorsa invocata. Tramite la lista è possibile selezionare una
      tra le azioni/risorse censite nel registro. Se è stato selezionato una API, saranno elencati solo le azioni ad essa appartenenti.

   -  *Ruolo Fruitore*: Opzione per filtrare le richieste di servizio in
      base al ruolo posseduto dal soggetto fruitore. Tramite la lista è
      possibile selezionare uno tra i ruoli censiti nel registro. La
      selezione di un ruolo esclude la possibilità di selezionare un
      soggetto fruitore.

   -  *Soggetto Fruitore*: Opzione per filtrare le richieste di servizio
      in base al soggetto fruitore. Tramite la lista è possibile
      selezionare uno tra i soggetti censiti nel registro. Se è stato
      selezionato un servizio, saranno elencati solo i soggetti fruitori
      del medesimo. La selezione di un soggetto esclude la possibilità
      di selezionare un ruolo fruitore.

   -  *Applicativo Fruitore*: Opzione per filtrare le richieste di
      servizio in base all'applicativo fruitore (opzione non disponibile
      nel caso di una erogazione). Tramite la lista è possibile
      selezionare uno tra i servizi applicativi censiti nel registro. Se
      sono stati selezionati servizi e/o soggetti, la lista presentata
      sarà filtrata di conseguenza.

   -  *Filtro per Chiave*: Si tratta di un'opzione avanzata che consente
      di filtrare le richieste in ingresso sul gateway in base ad una
      chiave che può essere specificata in maniera personalizzata. Questa parte è già stata descritta in maniera approfondita nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy`.
      

.. note::
   È possibile specificare più di un criterio di filtro; la
   logica applicata sarà quella dell'operatore AND.

   .. figure:: ../../_figure_console/PolicyGlobaliFiltro.png
    :scale: 100%
    :align: center
    :name: policyRateLimitingFiltroFig

    Definizione del filtro per l’istanza della policy di rate limiting


