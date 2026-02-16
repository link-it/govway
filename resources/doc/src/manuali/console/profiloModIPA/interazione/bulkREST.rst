.. _modipa_bulkREST:

Pattern BULK_RESOURCE_REST per API REST
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le Linee Guida di Interoperabilità ModI (`Allegato 1 - Pattern di Interazione, Sezione 7.1 <https://www.agid.gov.it/it/infrastrutture/sistema-pubblico-connettivita/il-nuovo-modello-interoperabilita>`_) definiscono il pattern `BULK_RESOURCE_REST <https://www.agid.gov.it/sites/agid/files/2024-07/Linee_guida_interoperabilit%C3%A0PA_All1_Pattern_interazione.pdf>`_ per la gestione di risorse massive tramite API REST.

Il pattern è orientato allo scenario in cui un fruitore debba accedere a risorse di grandi dimensioni e prevede l'utilizzo delle *Range Requests* definite nella `RFC 9110, Section 14 <https://www.rfc-editor.org/rfc/rfc9110#section-14>`_. In particolare:

- Il fruitore utilizza il metodo HTTP *HEAD* per ottenere i metadati della risorsa, tra cui la dimensione complessiva indicata dall'header *Content-Length* e l'header *Accept-Ranges* che indica il supporto al recupero parziale.
- Il fruitore può quindi richiedere porzioni della risorsa tramite l'header *Range*, ottenendo risposte con status *206 Partial Content* e gli header *Content-Range* e *Content-Length* relativi al frammento restituito.

Per il corretto funzionamento di questo pattern è essenziale che il gateway preservi l'header *Content-Length* presente nelle risposte del backend, evitando di sostituirlo con il *Transfer-Encoding: chunked*.

**Configurazione dell'API**

Per abilitare il supporto al pattern BULK_RESOURCE_REST su GovWay, accedere alla configurazione dell'API REST nel contesto del profilo ModI. Nella sezione *Interazione* è disponibile la checkbox *Risorse Massive (BULK_RESOURCE_REST)* (:numref:`modipa_api_bulk_resource_rest`).

   .. figure:: ../../_figure_console/modipa_api_bulk_resource_rest.png
    :scale: 70%
    :align: center
    :name: modipa_api_bulk_resource_rest

    Abilitazione del pattern BULK_RESOURCE_REST nella configurazione dell'API

Quando la checkbox è abilitata, GovWay preserva automaticamente l'header *Content-Length* presente nella risposta del backend, inoltrandolo al client al posto del *Transfer-Encoding: chunked* utilizzato per default. Il comportamento attuato è equivalente a quello descritto dalla proprietà *connettori.http.contentLength.preserve* documentata nella sezione :ref:`contentLengthRisposta`.

.. note::
   La checkbox è disponibile solamente per API di tipo REST. L'abilitazione ha effetto su tutte le risorse dell'API.
