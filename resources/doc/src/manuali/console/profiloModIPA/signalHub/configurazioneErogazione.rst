.. _modipa_signalhub_configurazione_erogazione:

Configurazione erogazione e-service
-----------------------------------

**Configurazione erogazione**

È possibile abilitare il supporto a Signal-Hub su tutte le erogazioni di API erogate su PDND (:ref:`modipa_pdnd`) accedendo alla schermata dei dettagli del profilo di interoperabilità come mostrato nella figura ':numref:`SignalHubErogazioneBase`'.

.. figure:: ../../_figure_console/SignalHubErogazioneBase.png
    :scale: 90%
    :align: center
    :name: SignalHubErogazioneBase

    Schermata di modifica profilo interoperabilità

Abilitando il supporto alla funzionalità Signal-Hub, diventa obbligatorio indicare l’ID dell’e-service corrispondente alla registrazione del servizio presso la PDND. (':numref:`SignalHubErogazione`').

.. figure:: ../../_figure_console/SignalHubErogazione.png
    :scale: 90%
    :align: center
    :name: SignalHubErogazione

    Schermata di modifica profilo interoperabilità con supporto a Signal-Hub abilitato

Come si può vedere dalla figura ':numref:`SignalHubErogazione`', in questa schermata sarà possibile selezionare quale risorsa esporrà le informazioni di pseudoanonimizzazione, l’algoritmo utilizzato per generare l’hash dell’objectId, la dimensione del seme e il periodo di rotazione delle informazioni crittografiche.

**Ottenimento informazioni crittografiche**

GovWay gestisce l’endpoint designato per l’esposizione delle informazioni crittografiche correnti fornite dal servizio, in conformità al formato definito nella specifica OpenAPI riportata di seguito. La risorsa consente, mediante un parametro opzionale nella query string, di richiedere le informazioni crittografiche associate a uno specifico signalId, abilitando così l’accesso a eventuali dati storici.

.. code-block:: yaml
   :caption: Specifica OpenAPI - Risorsa di pseudoanonimizzazione
   :linenos:

   openapi: 3.0.1
   info:
     title: Risorsa di pseudoanonimizzazione implementata da GovWay
     version: 1.0.0
   paths:
     /pseudonymization:
       get:
         summary: Gets a pseudonymization info
         description: Info about crypto hash function and seed
         parameters:
           - in: query
             name: signalId
             schema:
               type: integer
             required: false
             description: SignalID
         responses:
           "200":
             description: Success
             content:
               application/json:
                 schema:
                   type: object
                   properties:
                     seed:
                       example: 3b9942ce-1f07-4512-8f34-f31b1a7b0061
                       type: string
                     cryptoHashFunction:
                       example: sha256
                       type: string
                   required:
                     - seed
                     - cryptoHashFunction
                   description: Success

La seconda parte della configurazione consente di indicare quali servizi applicativi o ruoli sono abilitati a pubblicare segnali per il servizio selezionato. Solo tali applicativi/ruoli potranno accedere alla fruizione built-in (descritta nel paragrafo successivo) per pubblicare nuovi segnali. I servizi applicativi disponibili sono solo quelli inseriti nell’autorizzazione per richiedente della fruizione built-in.