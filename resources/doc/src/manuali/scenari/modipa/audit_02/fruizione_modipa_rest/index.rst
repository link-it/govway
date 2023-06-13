.. _scenari_fruizione_rest_modipa_audit_02:

Fruizione API REST
======================

**Obiettivo** 

Fruire di un servizio REST, definito tramite una API REST (OpenAPI 3.0), che richiede per l'accesso oltre ai token di sicurezza descritti nei precedenti scenari anche un token aggiuntivo adibito a contenere informazioni utili all'erogatore a identificare la specifica provenienza di ogni singola richiesta di accesso ai dati effettuta dal fruitore. Il token di audit deve rispettare il pattern di sicurezza descritto nella sezione :ref:`modipa_infoUtente_audit02`.

**Sintesi**

Mostriamo in questa sezione come procedere per l'integrazione di un applicativo con un servizio REST erogato nel rispetto della normativa italiana alla base dell'interoperabilità tra i sistemi della pubblica amministrazione. In particolare andiamo ad illustrare lo scenario in cui il servizio è stato registrato sulla PDND, e il fruitore per poterlo fruire deve ottenere un voucher dalla PDND che successivamente deve inviare all'erogatore insieme alla normale richiesta di servizio. Oltre al voucher il fruitore devo anche presentare il token di audit "Agid-JWT-TrackingEvidence" previsto dal pattern "AUDIT_REST_02". Da notare come il pattern :ref:`modipa_infoUtente_audit02` prevede che nella richiesta del voucher verso la PDND e nel voucher restituito debba essere presente il digest del token di audit che verrà poi utilizzato dall'erogatore per verificare la correlazione tra i due token.

La figura seguente descrive graficamente questo scenario.

.. figure:: ../../../_figure_scenari/FruizioneModIPA_audit_02_rest.jpg
 :scale: 70%
 :align: center
 :name: fruizione_modipa_audit_02_fig

 Fruizione di una API REST con profilo 'ModI', pattern AUDIT_REST_02 e pattern ID_AUTH_REST_01 via PDND

Le caratteristiche principali di questo scenario sono:

1. un applicativo fruitore che dialoga con il servizio erogato in modalità ModI in accordo ad una API condivisa e pubblicata su PDND;
2. la comunicazione diretta verso il dominio erogatore veicolata su un canale gestito con il pattern di sicurezza canale "ID_AUTH_CHANNEL_01";
3. l'autenticità della comunicazione tra il servizio erogato e ciascun fruitore è garantita tramite sicurezza a livello messaggio con pattern "ID_AUTH_REST_01 via PDND";
4. le informazioni di audit, richieste dall'erogatore per identificare la specifica provenienza di ogni singola richiesta di accesso ai dati effettuta dal fruitore, vengono inserite in un token di audit conforme al pattern "AUDIT_REST_02". Le informazioni vengono fornite dall'applicativo fruitore tramite header HTTP;
5. la negoziazione del voucher con la PDND prevede l'inserimento nella richiesta del digest del token di audit che verrà a sua volta incluso dalla PDND nel voucher restituito e sarà utilizzabile dall'erogatore per verificare la correlazione tra il token di audit e il token di autenticazione.

.. toctree::
    :maxdepth: 2

    esecuzione
    configurazione
