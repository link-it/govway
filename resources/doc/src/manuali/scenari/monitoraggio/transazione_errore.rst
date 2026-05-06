.. _scenari_monitoraggio_transazione_errore:

Transazione in errore
============================

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
