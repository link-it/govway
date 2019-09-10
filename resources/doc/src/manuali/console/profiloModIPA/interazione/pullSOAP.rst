.. _modipa_pullSOAP:

Profilo di Interazione PULL per API SOAP
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il profilo di interazione, denominato PULL, prevede che il fruitore non fornisca un indirizzo di callback, mentre l’erogatore fornisce un indirizzo interrogabile per verificare lo stato di processamento di una richiesta e, al fine dell’elaborazione della stessa, il risultato (:numref:`modipa_flusso_PULL_SOAP`).

   .. figure:: ../../_figure_console/modipa_flusso_PULL_SOAP.png
    :scale: 50%
    :align: center
    :name: modipa_flusso_PULL_SOAP

    Flusso previsto in un Profilo di Interazione PULL per API SOAP

Come riportato dalle Linee Guida di Interoperabilità ModI PA:

- L’interfaccia di servizio dell’erogatore fornisce tre metodi differenti al fine di inoltrare una richiesta, controllarne lo stato ed ottenerne il risultato
- Al passo (1), il fruitore effettua una richiesta;
- Al passo (2), l’erogatore DEVE fornire insieme all’acknowledgement della richiesta nel body, un correlation ID riportato nel header custom SOAP X-Correlation-ID;
- Al passo (3), il fruitore DEVE utilizzare i l correlation ID ottenuto al passo (2) per richiedere lo stato di processamento di una specifica richiesta;
- Al passo (4) l’erogatore, quando il processamento non si è ancora concluso fornisce informazioni circa lo stato della lavorazione della richiesta, quando invece il processamento si è concluso risponde indicando in maniera esplicita il completamento;
- Al passo (5), il fruitore utilizza il correlation ID di cui al passo (2) al fine di richiedere il risultato della richiesta;
- Al passo (6), l’erogatore fornisce il risultato del processamento.


**Configurazione delle API**

Per attuare la configurazione su GovWay si deve procedere con la registrazione dell'API che deve contenere i tre metodi differenti descritti precedentemente.

- Richiesta

Effettuata la registrazione delle API, accedere al dettaglio dell'azione corrispondente alla richiesta ed impostare nella sezione 'ModI PA' un profilo di interazione non bloccante 'PULL' con ruolo 'Richiesta' come mostrato nella figura :numref:`modipa_flusso_PULL_richiesta_SOAP`:

   .. figure:: ../../_figure_console/modipa_flusso_PULL_richiesta_SOAP.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PULL_richiesta_SOAP

    Configurazione della richiesta dell'API SOAP (PULL)

- Richiesta Stato

Successivamente, accedere al dettaglio dell'azione che consente di richiedere lo stato di processamento ed impostare nella sezione 'ModI PA' un profilo di interazione non bloccante 'PULL' con ruolo 'Richiesta Stato'. Definire anche la correlazione verso l'azione relativa alla richiesta come mostrato nella figura :numref:`modipa_flusso_PULL_richiestaStato_SOAP`:

   .. figure:: ../../_figure_console/modipa_flusso_PULL_richiestaStato_SOAP.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PULL_richiestaStato_SOAP

    Configurazione della richiesta stato di processamento dell'API SOAP (PULL)

- Risposta

Accedere al dettaglio dell'azione corrispondente alla risposta ed impostare nella sezione 'ModI PA' un profilo di interazione non bloccante 'PULL' con ruolo 'Risposta'. Definire anche la correlazione verso l'azione relativa alla richiesta come mostrato nella figura :numref:`modipa_flusso_PULL_risposta_SOAP`:

   .. figure:: ../../_figure_console/modipa_flusso_PULL_risposta_SOAP.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PULL_risposta_SOAP

    Configurazione della risposta dell'API SOAP (PUSH)

**Configurazione dell'Erogazione**

Sul dominio dell'erogatore deve essere definita l'erogazione dell'API.

- Richiesta

Le richieste ricevute sull'erogazione vengono inoltrate al backend da GovWay rimanendo poi in attesa dell'acknowledgement.

Ricevuto il messaggio di acknowledgement GovWay verifica la presenza dell’header SOAP custom 'X-Correlation-ID'. Se tale header non risulta presente viene generato da GovWay impostando come valore l'identificativo della transazione, che è stato inoltrato con la richiesta al backend tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Transaction-ID').

.. note::

	**Header 'X-Correlation-ID' generato da GovWay**

	La generazione dell'header soap 'X-Correlation-ID', se non presente, è disabilitabile intervenendo sulla proprietà "org.openspcoop2.protocol.modipa.soap.pull.request.correlationId.header.useTransactionIdIfNotExists" presente nel file “/etc/govway/modipa_local.properties” (si assume che '/etc/govway' sia la directory di configurazione indicata in fase di installazione). Se si disabilita la proprietà, GovWay termina con errore la transazione se rileva l'assenza dell'header soap 'X-Correlation-ID' nel messaggio di acknowledgement ricevuto dal backend.

- Richiesta Stato di Processamento

Le richieste che richiedono uno stato del processamento vengono validate da GovWay verificando la presenza dell'header soap 'X-Correlation-ID' come previsto dal profilo 'ModI PA'. Effettuata la validazione del messaggio di richiesta, eventualmente gestendo anche gli aspetti di sicurezza descritti nella sezione :ref:`modipa_sicurezzaMessaggio`, GovWay inoltra il messaggio al backend e rimane in attesa dell'acknowledgement. L'informazione sull'id di correlazione è inoltrato al backend, oltre che tramite l'header soap 'X-Correlation-ID', anche tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Conversation-ID').

- Risposta

Le risposte vengono gestite da GovWay in maniera simile a quanto indicato per le richieste di stato del processamento.


**Configurazione della Fruizione**

Sul dominio del fruitore deve essere definita una fruizione dell'API.

- Richiesta

Le richieste devono essere inoltrate dall'applicativo mittente utilizzando la fruizione dell'API configurata su GovWay. 

Il messaggio di acknowledgement ricevuto viene validato al fine di verificare la presenza dell'header soap 'X-Correlation-ID' come previsto dalla specifica 'ModI PA'. L'informazione sull'id di correlazione è ottenibile dall'applicativo mittente sulla risposta, oltre che tramite l'header soap 'X-Correlation-ID', anche tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Conversation-ID').

- Richiesta Stato di Processamento

Le richieste che richiedono uno stato del processamento devono essere inoltrate dall'applicativo mittente utilizzando la fruizione dell'API configurata su GovWay. Le richieste vengono validate da GovWay verificando la presenza dell'header soap 'X-Correlation-ID'. GovWay permette di fornire l'informazione sull'identificativo di correlazione anche tramite modalità alternative all'header soap per poi generarlo come previsto dalla specifica 'ModI PA' valorizzato con il valore fornito. Le modalità alternative sono le seguenti:

    - Header HTTP 'X-Correlation-ID'
    - Header HTTP 'GovWay-Conversation-ID' o parametro della url 'govway_conversation_id' previsto per la correlazione tramite identificativo di collaborazione descritta nella sezione :ref:`correlazioneTransazioniDifferenti`. Questa modalità richiede che sia abilitata l'indicazione dell'identificativo di collaborazione nell'API o sulla singola azione come mostrato nella figura :numref:`abilitazioneIdCollaborazioneSOAP2`:

        .. figure:: ../../_figure_console/abilitazioneIdCollaborazione_SOAP.png
         :scale: 30%
         :align: center
         :name: abilitazioneIdCollaborazioneSOAP2

         Abilitazione funzionalità di correlazione govway tramite identificativo di colllaborazione
    - Header HTTP 'GovWay-Relates-To' o parametro della url 'govway_relates_to' previsto per la correlazione tramite riferimento della richiesta descritta nella sezione :ref:`correlazioneTransazioniDifferenti`. Questa modalità richiede che sia abilitata l'indicazione dell'identificativo di riferimento alla richiesta nell'API o sulla singola azione come mostrato nella figura :numref:`abilitazioneIdRiferimentoRichiestaSOAP2`:

        .. figure:: ../../_figure_console/abilitazioneIdRiferimentoRichiesta_SOAP.png
         :scale: 30%
         :align: center
         :name: abilitazioneIdRiferimentoRichiestaSOAP2

         Abilitazione funzionalità di correlazione govway tramite identificativo della richiesta

- Risposta

Le risposte vengono gestite da GovWay in maniera simile a quanto indicato per le richieste di stato del processamento.
