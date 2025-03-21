.. _modipa_pushSOAP:

Pattern di Interazione PUSH per API SOAP
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il pattern di interazione, denominato PUSH, è utilizzabile nel caso in cui il fruitore abbia a sua volta la possibilità di esporre una interfaccia di servizio per la ricezione delle risposte (:numref:`modipa_flusso_PUSH_SOAP`).

   .. figure:: ../../_figure_console/modipa_flusso_PUSH.png
    :scale: 50%
    :align: center
    :name: modipa_flusso_PUSH_SOAP

    Flusso previsto in un Pattern di Interazione PUSH

Come riportato dalle Linee Guida di Interoperabilità ModI:

- Al passo (1), il fruitore DEVE indicare l’endpoint della callback utilizzando l’header SOAP custom 'X-ReplyTo';
- Al passo (2), l’erogatore DEVE fornire insieme all’acknowledgement della richiesta nel body, il correlation ID utilizzando l’header SOAP custom X-Correlation-ID;
- Al passo (3), l’erogatore DEVE riutilizzare lo stesso correlation ID fornito al passo (2) sempre utilizzando l’header SOAP custom X-Correlation-ID;
- Al passo (4), il fruitore DEVE riconoscere tramite un messaggio di acknowledgement il ricevimento della risposta.

**Configurazione delle API**

Per attuare la configurazione su GovWay si deve procedere con la registrazione delle due API che definiscono il servizio di ricezione della richiesta e il servizio di Callback dove l'erogatore deve inoltrare la risposta.

- Richiesta

Effettuata la registrazione delle API, accedere al dettaglio dell'azione relativa al servizio di richiesta ed impostare nella sezione 'ModI' un pattern di interazione non bloccante 'PUSH' con ruolo 'Richiesta' come mostrato nella figura :numref:`modipa_flusso_PUSH_richiesta_SOAP`:

   .. figure:: ../../_figure_console/modipa_flusso_PUSH_richiesta_SOAP.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PUSH_richiesta_SOAP

    Configurazione della richiesta dell'API SOAP (PUSH)

- Risposta

Successivamente, accedere al dettaglio dell'azione relativa al servizio di callback ed impostare nella sezione 'ModI' un pattern di interazione non bloccante 'PUSH' con ruolo 'Risposta'. Definire anche la correlazione verso il servizio e l'azione relativa alla richiesta come mostrato nella figura :numref:`modipa_flusso_PUSH_risposta_SOAP`:

   .. figure:: ../../_figure_console/modipa_flusso_PUSH_risposta_SOAP.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PUSH_risposta_SOAP

    Configurazione della risposta dell'API SOAP (PUSH)

**Configurazione dell'Erogazione**

Sul dominio dell'erogatore deve essere definita sia un'erogazione dell'API relativa al servizio di richiesta che una fruizione del servizio di callback.

- Erogazione del Servizio di Richiesta

Le richieste ricevute sull'erogazione vengono validate da GovWay verificando la presenza dell'header SOAP custom 'X-ReplyTo' come previsto dal profilo 'ModI'. Effettuata la validazione del messaggio di richiesta, eventualmente gestendo anche gli aspetti di sicurezza descritti nella sezione :ref:`modipa_sicurezzaMessaggio`, GovWay inoltra il messaggio al backend e rimane in attesa dell'acknowledgement.

Ricevuto il messaggio di acknowledgement GovWay verifica la presenza dell’header SOAP custom 'X-Correlation-ID'. Se tale header non risulta presente viene generato da GovWay impostando come valore l'identificativo della transazione, che è stato inoltrato con la richiesta al backend tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Transaction-ID').

.. note::

	**Header 'X-Correlation-ID' generato da GovWay**

	La generazione dell'header soap 'X-Correlation-ID', se non presente, è disabilitabile intervenendo sulla proprietà "org.openspcoop2.protocol.modipa.soap.push.request.correlationId.header.useTransactionIdIfNotExists" presente nel file “/etc/govway/modipa_local.properties” (si assume che '/etc/govway' sia la directory di configurazione indicata in fase di installazione). Se si disabilita la proprietà, GovWay termina con errore la transazione se rileva l'assenza dell'header soap 'X-Correlation-ID' nel messaggio di acknowledgement ricevuto dal backend.

- Fruizione del Servizio di Callback per la Risposta

Le risposte devono essere inoltrate dall'applicativo mittente utilizzando la fruizione del servizio di Callback configurata su GovWay. Le risposte vengono validate da GovWay verificando la presenza dell'header SOAP custom 'X-Correlation-ID'. GovWay permette di fornire l'informazione sull'identificativo di correlazione anche tramite modalità alternative all'header soap (header http, parametri della url...) per poi generare un header soap 'X-Correlation-ID' come previsto dalla specifica 'ModI' valorizzato con il valore fornito. Le modalità alternative sono le seguenti:

    - Header HTTP 'X-Correlation-ID'
    - Header HTTP 'GovWay-Conversation-ID' o parametro della url 'govway_conversation_id' previsto per la correlazione tramite identificativo di collaborazione descritta nella sezione :ref:`correlazioneTransazioniDifferenti`. Questa modalità richiede che sia abilitata l'indicazione dell'identificativo di collaborazione nell'API o sulla singola azione come mostrato nella seguente :numref:`abilitazioneIdCollaborazioneSOAP`:

        .. figure:: ../../_figure_console/abilitazioneIdCollaborazione_SOAP.png
         :scale: 30%
         :align: center
         :name: abilitazioneIdCollaborazioneSOAP

         Abilitazione funzionalità di correlazione govway tramite identificativo di colllaborazione
    - Header HTTP 'GovWay-Relates-To' o parametro della url 'govway_relates_to' previsto per la correlazione tramite riferimento della richiesta descritta nella sezione :ref:`correlazioneTransazioniDifferenti`. Questa modalità richiede che sia abilitata l'indicazione dell'identificativo di riferimento alla richiesta nell'API o sulla singola azione come mostrato nella figura :numref:`abilitazioneIdRiferimentoRichiestaSOAP`:

        .. figure:: ../../_figure_console/abilitazioneIdRiferimentoRichiesta_SOAP.png
         :scale: 30%
         :align: center
         :name: abilitazioneIdRiferimentoRichiestaSOAP

         Abilitazione funzionalità di correlazione govway tramite identificativo della richiesta


**Configurazione della Fruizione**

Sul dominio del fruitore deve essere definita sia una fruizione dell'API relativa al servizio di richiesta che un'erogazione del servizio di callback.

- Fruizione del Servizio di Richiesta

Le richieste devono essere inoltrate dall'applicativo mittente utilizzando la fruizione del servizio di richiesta configurata su GovWay. Su ogni richiesta GovWay crea, o ne modifica il valore se già presente, dell'header soap 'X-ReplyTo' previsto dal profilo 'ModI'. L'header viene valorizzato con l'url di invocazione utilizzabile dalla controparte per invocare il servizio di callback configurato su GovWay.

.. note::

	**Header 'X-ReplyTo' generato da GovWay**

	La valorizzazione dell'header soap 'X-ReplyTo' da parte di GovWay è disabilitabile intervenendo sulla proprietà "org.openspcoop2.protocol.modipa.soap.push.replyTo.header.updateOrCreate" presente nel file “/etc/govway/modipa_local.properties” (si assume che '/etc/govway' sia la directory di configurazione indicata in fase di installazione). Se si disabilita la proprietà, GovWay termina con errore la transazione se rileva l'assenza dell'header soap 'X-ReplyTo' nel messaggio di richiesta ricevuto dal backend.

Il messaggio di acknowledgement ricevuto viene anch'esso validato al fine di verificare la presenza dell'header soap 'X-Correlation-ID' come previsto dalla specifica 'ModI'. L'informazione sull'id di correlazione è ottenibile dall'applicativo mittente sulla risposta, oltre che tramite l'header soap 'X-Correlation-ID', anche tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Conversation-ID').

- Erogazione del Servizio di Callback per la Risposta

Le risposte ricevute sull'erogazione del servizio di Callback vengono validate da GovWay verificando la presenza dell'header SOAP custom 'X-Correlation-ID' come previsto dal profilo 'ModI'. Effettuata la validazione del messaggio di risposta, eventualmente gestendo anche gli aspetti di sicurezza descritti nella sezione :ref:`modipa_sicurezzaMessaggio`, GovWay inoltra il messaggio al backend e rimane in attesa dell'acknowledgement. L'informazione sull'id di correlazione è inoltrato al backend, oltre che tramite l'header soap 'X-Correlation-ID', anche tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Conversation-ID').
