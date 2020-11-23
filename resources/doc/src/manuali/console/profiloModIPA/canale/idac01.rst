.. _modipa_idac01:

[ID_AUTH_CHANNEL_01] Direct Trust Transport-Level Security
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Questo pattern di sicurezza prevede l'utilizzo del canale HTTPS, per le comunicazioni sul confine tra i due domini, con validazione del certificato dell'ente destinatario della comunicazione.

Descriviamo di seguito i passi di configurazione da effettuare:

- La creazione della relativa API prevede che nella sezione "ModI", elemento "Sicurezza Canale", venga selezionato il pattern "ID_AUTH_CHANNEL_01" come indicato in :numref:`api_canale1_fig`.

   .. figure:: ../../_figure_console/modipa_api_canale1.png
    :scale: 50%
    :name: api_canale1_fig

    Selezione del pattern "ID_AUTH_CHANNEL_01" per l'API

- Nel caso si voglia configurare una fruizione, le maschere di configurazione terranno conto degli aspetti di sicurezza sul canale garantendo che l'endpoint specificato nel connettore di uscita sia di tipo HTTPS, indipendentemente dal pattern adottato nella API (TLS sempre obbligatorio). L'autenticazione HTTPS può essere gestita opzionalmente da GovWay o, in alternativa, delegata alla configurazione della JVM sull'application server. Per la gestione in GovWay sono disponibili i campi per la configurazione HTTPS, lasciando opzionalmente la possibilità di impostare l'autenticazione client (vedi sez. :ref:`avanzate_connettori_https`).

- Nel caso si voglia configurare una erogazione, il pattern di sicurezza "ID_AUTH_CHANNEL_01" impatta sulla configurazione del Controllo Accessi, previsto nella configurazione specifica dell'erogazione:

    + La sezione "Autenticazione Canale" è impostata a "HTTPS" ammettendo il flag "Opzionale" (:numref:`erogazione_authTrasportoOpzionale_fig`).

   .. figure:: ../../_figure_console/modipa_erogazione_authTrasportoOpzionale.png
    :scale: 50%
    :name: erogazione_authTrasportoOpzionale_fig

    Autenticazione Canale HTTPS con flag opzionale

    + La sezione "Autorizzazione Canale" è per default disabilitata (:numref:`erogazione_authCanaleDisabilitato_fig`). Abilitando tale sezione sarà possibile inserire i criteri di autorizzazione, come descritto nella sez. :ref:`apiGwAutorizzazione`, con la differenza che in questo caso le politiche saranno riferite esclusivamente ai soggetti censiti in configurazione (e non gli applicativi, per i quali si rimanda alla sez. :ref:`modipa_sicurezzaMessaggio`).

   .. figure:: ../../_figure_console/modipa_erogazione_authCanaleDisabilitato.png
    :scale: 50%
    :name: erogazione_authCanaleDisabilitato_fig

    Autorizzazione Canale Disabilitata
