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
                       example: Xzzh3pQqLp+VK7gG9NwR6w==
                       type: string
                     cryptoHashFunction:
                       example: SHA-256
                       type: string
                     signalId:
                       example: 13129
                       type: integer
                       format: int64
                   required:
                     - seed
                     - cryptoHashFunction
                   description: Success

Per il dettaglio sul formato dell'identificativo dell'algoritmo restituito nel campo ``cryptoHashFunction`` si rimanda alla sezione :ref:`Formato dell'identificativo dell'algoritmo esposto sul servizio di pseudoanonimizzazione <modipa_signalhub_exposedAlgorithmName>`.

Il campo opzionale ``signalId`` esposto nella risposta indica il signalId del segnale di tipo ``SEEDUPDATE`` che ha introdotto il seme corrente; consente al consumatore di sincronizzarsi correttamente sapendo da quale segnale in poi il seme è applicabile. Per il primo seme storico (mai sostituito da un ``SEEDUPDATE``) il valore esposto è ``0``. L'esposizione del campo è controllata dalla proprietà ``org.openspcoop2.protocol.modipa.signalHub.pseudonymization.exposeSignalId`` descritta nella sezione :ref:`Esposizione del signalId sul servizio di pseudoanonimizzazione <modipa_signalhub_exposeSignalId>`.

Il valore del campo ``seed`` deve essere utilizzato dal consumatore così come fornito, come stringa opaca e senza decodifica preventiva, secondo quanto descritto nella sezione :ref:`Pseudoanonimizzazione: generazione dell'identificativo e verifica lato consumatore <modipa_signalhub_pseudoanonimizzazione>`.
