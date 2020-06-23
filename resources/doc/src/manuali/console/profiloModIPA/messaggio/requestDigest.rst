.. _modipa_requestDigest:

Digest della Richiesta
~~~~~~~~~~~~~~~~~~~~~~~

Questa funzionalità consente di estendere IDAR03 aggiungendo all'interno del token di sicurezza della risposta il digest della richiesta. 

La funzionalità consente di implementare la soluzione per la non ripudiabilità della trasmissione come suggerito nelle linee guida di interoperabilità (:numref:`api_modipaRequestDigest_fig`): https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/doc/doc_04/soluzioni-di-sicurezza.html#soluzioni-per-la-non-ripudiabilita-della-trasmissione.

  .. figure:: ../../_figure_console/modipa_api_requestDigest.png
    :scale: 50%
    :align: center
    :name: api_modipaRequestDigest_fig

    Punto 'D' della soluzione di sicurezza per la non ripudiabilità della trasmissione

.. note::
    La sigla che identifica il profilo di sicurezza messaggio varia a seconda se l'API sia di tipo REST, per cui la sigla corrisponde a *IDAR03*, o SOAP dove viene utilizzata la sigla *IDAS03*.

L'attivazione di questo profilo avviene a livello della relativa API, nella sezione "ModIPA", elemento "Profilo Sicurezza Messaggio", selezionando la voce "Digest Richiesta" (:numref:`api_modipaRequestDigest_opzione_fig`).

  .. figure:: ../../_figure_console/modipa_api_requestDigest_opzione.png
    :scale: 50%
    :align: center
    :name: api_modipaRequestDigest_opzione_fig

    Profilo di sicurezza messaggio IDAR03 + Digest Richiesta

.. note::
    Poichè la funzionalità è una estensione di IDAR03, la voce 'Digest Richiesta' compare solamente se è stato selezionato il profilo "IDAR03 (IDAR01)" o "IDAR03 (IDAR02)"

Nella figura :numref:`api_modipaRequestDigest_exampleRest_fig` viene riportato un esempio del payload, relativo al token di sicurezza ModI PA della risposta per una API REST, contenente il digest della richiesta.

  .. figure:: ../../_figure_console/modipa_api_requestDigest_example_rest.png
    :scale: 50%
    :align: center
    :name: api_modipaRequestDigest_exampleRest_fig

    Payload del Token di Sicurezza REST con profilo IDAR03 + Digest Richiesta

Nella figura :numref:`api_modipaRequestDigest_exampleSoap_fig` viene riportato un esempio relativo al token di sicurezza ModI PA della risposta per una API SOAP. Tutti i digest degli elementi firmati nella richiesta vengono riportati all'interno di un header soap 'X-Digest-Richiesta' della risposta. Il nuovo header 'X-Digest-Richiesta' sarà aggiunto agli elementi firmati nella risposta.

  .. figure:: ../../_figure_console/modipa_api_requestDigest_example_soap.png
    :scale: 50%
    :align: center
    :name: api_modipaRequestDigest_exampleSoap_fig

    Payload del Token di Sicurezza SOAP con profilo IDAS03 + Informazioni Utente

