.. _scenari_monitoraggio:

Monitoraggio
============

In questa sezione descriviamo alcuni tipici scenari di impiego delle funzionalità di monitoraggio offerte da Govway. Il monitoraggio consente di tenere sotto controllo il traffico gestito dal gateway al fine di verificare il regolare funzionamento dei servizi, individuare situazioni anomale ed avviare l'indagine diagnostica.

Per meglio descrivere le attività tipiche della fase di monitoraggio, supponiamo di intervenire nella fase successiva all'esecuzione dei passi dello scenario "Erogazione SPID" (:ref:`scenari_erogazione_oauth`).

La console govwayMonitor, nella sezione Monitoraggio, prevede la consultazione del traffico gestito nelle modalità "Storico" e "Live". Ciascuna di queste sezioni mostra l'elenco delle transazioni, in ordine cronologico decrescente, che soddisfano i criteri di filtro impostati (:numref:`monitor_elenco_transazioni_fig`).

   .. figure:: ../_figure_scenari/Monitor_elenco_transazioni.png
    :scale: 80%
    :align: center
    :name: monitor_elenco_transazioni_fig

    Elenco delle transazioni

Le transazioni riportate nell'elenco riportano i dati per l'identificazione delle stesse, con evidenza dell'esito riportato.

A titolo esemplificativo vengono riportati di seguito due tipici scenari di indagine sul dettaglio di una singola transazione:

- :ref:`scenari_monitoraggio_transazione_errore`: descrive le informazioni mostrate nel dettaglio di una transazione con esito negativo, utili a identificare la causa dell'errore.

- :ref:`scenari_monitoraggio_transazione_corretta`: illustra le informazioni mostrate nel dettaglio di una transazione con esito positivo, comprese quelle relative al token presentato dal mittente.

.. toctree::
   :maxdepth: 2

   transazione_errore
   transazione_corretta
