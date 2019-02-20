.. _configurazioneRateLimiting_statistiche:

Visualizzazione Statistiche Policy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Quando una policy è attivata si ha la possibilità di accedere ad una
finestra che fornisce una sintesi dei dati statistici legati
all'applicazione della policy in fase di controllo del traffico.

Per visualizzare questa finestra è sufficiente accedere all'elenco delle
policy attivate ed utilizzare il collegamento “Visualizza” nella colonna
“Runtime” (:numref:`statistichePolicy`).

   .. figure:: ../../_figure_console/ControlloTraffico-StatistichePolicy.png
    :scale: 100%
    :align: center
    :name: statistichePolicy

    Dati statistici relativi ad una policy di rate limiting

Si noti che saranno visualizzati dei dati solo dopo la
data di attivazione della policy e cioè dopo che è transitata la prima
richiesta cui viene applicata la policy.

I dati statistici riportati sono i seguenti:

-  *Criterio di Collezionamento dei dati*: I criteri di raggruppamento
   utilizzati dalla policy.

-  *Dati Generali*:

   -  *Il numero istantaneo delle richieste attive*

   -  *la data di attivazione della policy (che corrisponde alla data di
      primo utilizzo della medesima)*

-  *Dati collezionati per la risorsa <nomeRisorsa>*: dati di sintesi
   sulle transazioni cui è stata applicata la policy.

Sono inoltre disponibili i seguenti collegamenti:

-  *Refresh*: per aggiornare i dati visualizzati.

-  *Reset Contatori*: per azzerare i valori visualizzati (solo nella
   modalità di controllo realtime).
