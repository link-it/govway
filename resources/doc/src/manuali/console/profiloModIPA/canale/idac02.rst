.. _modipa_idac02:

ID_AUTH_CHANNEL_02 - Direct Trust mutual Transport-Level Security
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Questo pattern di sicurezza prevede l'utilizzo del canale HTTPS con autenticazione client, per le comunicazioni sul confine tra i due domini, con reciproca validazione dei certificati degli enti in gioco.

Descriviamo di seguito i passi di configurazione da effettuare:

- La creazione della relativa API prevede che nella sezione "ModI", elemento "Sicurezza Canale", venga selezionato il pattern "ID_AUTH_CHANNEL_02" come indicato in :numref:`api_canale2_fig`.

   .. figure:: ../../_figure_console/modipa_api_canale2.png
    :scale: 50%
    :name: api_canale2_fig

    Selezione del pattern "ID_AUTH_CHANNEL_02" per l'API

- Nel caso si voglia configurare una fruizione, le maschere di configurazione terranno conto degli aspetti di sicurezza sul canale garantendo che l'endpoint specificato nel connettore di uscita sia di tipo HTTPS, indipendentemente dal pattern adottato nella API (TLS sempre obbligatorio).  L'autenticazione HTTPS può essere gestita opzionalmente da GovWay o, in alternativa, delegata alla configurazione della JVM sull'application server. Per la gestione in GovWay sono disponibili i campi per la configurazione HTTPS, con l'obbligo di impostare l'autenticazione client (vedi sez. :ref:`avanzate_connettori_https`).

- Nel caso si voglia configurare una erogazione, il pattern di sicurezza "ID_AUTH_CHANNEL_02" impatta sulla configurazione del Controllo Accessi, previsto nella configurazione specifica dell'erogazione:

    + La sezione "Autenticazione Canale" è impostata forzatamente a "HTTPS" (:numref:`erogazione_authTrasporto_fig`).

   .. figure:: ../../_figure_console/modipa_erogazione_authTrasporto.png
    :scale: 50%
    :name: erogazione_authTrasporto_fig

    Autenticazione Canale HTTPS

    + Nella sezione "Autorizzazione Canale" è possibile attivare l'autorizzazione per richiedente inserendo gli identificativi dei soggetti autorizzati tra quelli identificati tramite il certificato SSL (:numref:`erogazione_authCanale_fig`). Abilitando tale sezione sarà possibile inserire i criteri di autorizzazione, come descritto nella sez. :ref:`apiGwAutorizzazione`, con la differenza che in questo caso le politiche saranno riferite esclusivamente ai soggetti censiti in configurazione (e non gli applicativi, per i quali si rimanda alla sez. :ref:`modipa_sicurezzaMessaggio`).

   .. figure:: ../../_figure_console/modipa_erogazione_authCanale.png
    :scale: 50%
    :name: erogazione_authCanale_fig

    Autorizzazione Canale su soggetti
