.. _modipa_idar03:

[IDAS03 / IDAR03] Integrità payload del messaggio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::
    La sigla che identifica il profilo di sicurezza messaggio varia a seconda se l'API sia di tipo REST, per cui la sigla corrisponde a *IDAR03*, o SOAP dove viene utilizzata la sigla *IDAS03*.

Questo profilo di sicurezza consente di estendere IDAR01 e IDAR02 aggiungendo un meccanismo che garantisce l'integrità del messaggio scambiato grazie all'invio, nel token di sicurezza, della firma digitale del payload.

L'attivazione di questo profilo avviene a livello della relativa API, nella sezione "ModIPA", elemento "Profilo Sicurezza Messaggio", selezionando il profilo "IDAR03 (IDAR01)" nel caso si voglia estendere IDAR01, oppure il profilo "IDAR03 (IDAR02)" nel caso si voglia estendere IDAR02 con il meccanismo di garanzia dell'integrità del payload (:numref:`api_messaggio3_fig`).

  .. figure:: ../../_figure_console/modipa_api_messaggio3.png
    :scale: 50%
    :align: center
    :name: api_messaggio3_fig

    Profilo di sicurezza messaggio IDAR03 per l'API

Per le configurazioni successive procedere come già descritto in precedenza per il profilo :ref:`modipa_idar01`.

Occorre solo tenere presente che per questo profilo di sicurezza sono presenti le seguenti differenze sulle maschere di configurazione delle API di tipo REST:

- Nel contesto della configurazione di una fruizione, relativamente alla sezione "ModI PA - Richiesta", oltre ai dati da fornire per la produzione della firma digitale deve essere aggiunta anche l'indicazione degli eventuali Header HTTP da firmare. Tale indicazione viene fornita con il campo "HTTP Headers da firmare" (:numref:`fruizione_richiesta_headers_fig`).

   .. figure:: ../../_figure_console/modipa_fruizione_richiesta_headers.png
    :scale: 50%
    :align: center
    :name: fruizione_richiesta_headers_fig

    Fruizione IDAR03 - Configurazione richiesta con indicazione HTTP Headers da firmare

- Nel contesto della configurazione di una erogazione, relativamente alla sezione "ModI PA - Risposta", oltre ai dati da fornire per la produzione della firma digitale deve essere aggiunta anche l'indicazione degli eventuali Header HTTP da firmare. Tale indicazione viene fornita con il campo "HTTP Headers da firmare" (:numref:`erogazione_risposta_headers_fig`).

   .. figure:: ../../_figure_console/modipa_erogazione_risposta_headers.png
    :scale: 50%
    :align: center
    :name: erogazione_risposta_headers_fig

    Erogazione IDAR03 - Configurazione risposta con indicazione HTTP Headers da firmare
