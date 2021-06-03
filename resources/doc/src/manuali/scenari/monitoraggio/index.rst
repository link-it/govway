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


Transazione in errore
---------------------

Se apriamo il dettaglio della transazione con esito errore, relativa all'invocazione della "POST /pet" senza token, vediamo le informazioni di :numref:`monitor_token_non_presente_fig`.

   .. figure:: ../_figure_scenari/Monitor_token_non_presente.png
    :scale: 80%
    :align: center
    :name: monitor_token_non_presente_fig

    Dettaglio della transazione in errore

Il dettaglio della transazione:

- Il riquadro "Informazioni Generali" riepiloga i principali dati identificativi della transazione. In questo riquadro è mostrato l'esito, in questo caso negativo. Tramite il link apposito si possono visualizzare i messaggi diagnosti, utili all'identificazione del problema occorso (:numref:`monitor_token_non_presente_diagnostici_fig`).

   .. figure:: ../_figure_scenari/Monitor_token_non_presente_diagnostici.png
    :scale: 80%
    :align: center
    :name: monitor_token_non_presente_diagnostici_fig

    Messaggi diagnostici della transazione in errore


- I riquadri "Dettagli Richiesta" e "Dettagli Risposta" forniscono informazioni specifiche relative al messaggio di richiesta e a quello di risposta. In questo caso, ad esempio, è possibile visualizzare il messaggio di fault inviato al client in risposta (:numref:`monitor_fault_uscita_fig`).

   .. figure:: ../_figure_scenari/Monitor_fault_uscita.png
    :scale: 80%
    :align: center
    :name: monitor_fault_uscita_fig

    Fault in uscita

- Il riquadro "Informazioni Mittente" fornisce dettagli sulla provenienza della richiesta.
- Il riquadro "Informazioni Avanzate" fornisce dati aggiuntivi riguardo la transazione.


Transazione con esito corretto
------------------------------

Se apriamo il dettaglio della transazione con esito positivo, relativa all'invocazione della "POST /pet", possiamo ad esempio:

- Visualizzare le informazioni generali con l'esito dell'operazione (:numref:`monitor_token_generali_fig`).

   .. figure:: ../_figure_scenari/Monitor_token_generali.png
    :scale: 80%
    :align: center
    :name: monitor_token_generali_fig

    Messaggi diagnostici della transazione con esito regolare

- Nel contesto delle informazioni generali si possono visualizzare i messaggi diagnostici con il dettaglio dell'elaborazione regolarmente eseguita (:numref:`monitor_token_diagnostici_fig`).

   .. figure:: ../_figure_scenari/Monitor_token_diagnostici.png
    :scale: 80%
    :align: center
    :name: monitor_token_diagnostici_fig

    Messaggi diagnostici della transazione con esito regolare

- Nel contesto delle informazioni mittente in questo caso sarà presente la sezione "Token Info" che consente di visualizzare dati inerenti il token che è stato fornito con la richiesta del mittente. Risultano immediatamente visibili le informazioni principali (issuer, subject, ...), come mostrato in :numref:`monitor_token_mittente_fig`.

   .. figure:: ../_figure_scenari/Monitor_token_mittente.png
    :scale: 80%
    :align: center
    :name: monitor_token_mittente_fig

    Informazioni mittente con presenza del token

- Dalla sezione mittente è possibile aprire una finestra per visualizzare la versione in chiaro del token ricevuto con la richiesta (:numref:`monitor_token_info_fig`).

   .. figure:: ../_figure_scenari/Monitor_token_info.png
    :scale: 80%
    :align: center
    :name: monitor_token_info_fig

    Visualizzazione del token




