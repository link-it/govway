.. _scenari_monitoraggio_transazione_corretta:

Transazione con esito corretto
================================

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
