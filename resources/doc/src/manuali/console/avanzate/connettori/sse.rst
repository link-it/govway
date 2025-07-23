.. _avanzate_connettori_sse:

SSE (Server-Sent Events)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

SSE (Server-Sent Events) sono uno standard HTTP/1.1 che permette al server di inviare eventi in streaming al client su una connessione long-lived, identificata tramite content-type 'text/event-stream'.

È un canale unidirezionale: dal server -> al client.

Il client (browser o consumer) apre una richiesta GET che rimane aperta, e il server manda messaggi quando vuole.


**Supporto su GovWay**

Per utilizzare SSE (Server-Sent Events) su GovWay è necessario seguire i seguenti accorgimenti di configurazione:

- È necessario che l’erogazione o la fruizione non richieda funzionalità che impongono il buffering della risposta, come ad esempio:

   - :ref:`configSpecificaValidazione`
   - :ref:`configSpecificaRegistrazioneMessaggi`
   - :ref:`sicurezzaLivelloMessaggio`.

- il client deve invocare l'erogazione o la fruizione di GovWay attraverso un’URL che identifichi il canale NIO, utilizzando il path **async**, come descritto nella sezione :ref:`avanzate_canaleIO`. Se viene utilizzato il contesto **sync** o quello di default la modalità SSE non si attiva.

- per supportare una connessione long-lived è necessario modificare i criteri di read timeout predefiniti, come descritto nella sezione :ref:`avanzate_connettori_tempiRisposta`.

- Per poter individuare correttamente la richiesta nello storico delle transazioni, è necessario abilitare il tracciamento della fase "Risposta in consegna", come descritto nella sezione :ref:`tracciamentoTransazioniFasi`. In questo modo la traccia verrà generata immediatamente alla ricezione dei primi eventi, senza attendere il completamento della risposta, che in uno scenario SSE risulta indefinito.

La funzionalità SSE è attiva di default, ma può essere disabilitata per una singola erogazione o fruizione di API tramite la seguente :ref:`configProprieta`:

- *connettori.serverSentEvents.enabled* (true/false default:true): consente di abilitare o disabilitare la gestione SSE;

È inoltre possibile modificare il comportamento di default a livello globale agendo sul file di configurazione 'govway_local.properties', definendo le proprietà specifiche rispettivamente per fruizioni ed erogazioni.

   ::

      # Fruizione di API
      org.openspcoop2.pdd.connettori.inoltroBuste.serverSentEvents.enabled=true
      # Erogazioni di API
      org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.serverSentEvents.enabled=true

