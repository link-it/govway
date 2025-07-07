.. _modipa_signalhub_configurazione_erogazione_rest:

Interfaccia REST per il recupero delle informazioni crittografiche
------------------------------------------------------------------------

GovWay gestisce l’endpoint dedicato all’esposizione delle informazioni crittografiche correnti, messe a disposizione dal servizio, in conformità al formato previsto dalla specifica OpenAPI riportata di seguito.

L’endpoint consente inoltre di richiedere, tramite un parametro opzionale della query string, le informazioni crittografiche associate a uno specifico signalId, abilitando l’accesso anche a eventuali dati storici correlati.

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
