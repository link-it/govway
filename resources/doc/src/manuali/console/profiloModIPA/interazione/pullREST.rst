.. _modipa_pullREST:

Profilo di Interazione PULL per API REST
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il profilo di interazione, denominato PULL, prevede che il fruitore non fornisca un indirizzo di callback, mentre l’erogatore fornisce un indirizzo interrogabile per verificare lo stato di processamento di una richiesta e, al fine dell’elaborazione della stessa, il risultato (:numref:`modipa_flusso_PULL_REST`).

   .. figure:: ../../_figure_console/modipa_flusso_PULL_REST.png
    :scale: 50%
    :align: center
    :name: modipa_flusso_PULL_REST

    Flusso previsto in un Profilo di Interazione PULL per API REST

Come riportato dalle Linee Guida di Interoperabilità ModI PA:

- L’interfaccia di servizio dell’erogatore fornisce tre metodi differenti al fine di inoltrare una richiesta, controllarne lo stato ed ottenerne il risultato
- Al passo (1), il fruitore DEVE utilizzare il verbo HTTP POST;
- Al passo (2), l’erogatore DEVE fornire insieme all’acknowledgement della richiesta, un percorso di risorsa per interrogare lo stato di processamento utilizzando HTTP header Location ; Il codice HTTP di stato DEVE essere HTTP status 202 Accepted a meno che non si verifichino errori;
- Al passo (3), il fruitore DEVE utilizzare il percorso di cui al passo (2) per richiedere lo stato della risorsa; Il verbo HTTP utilizzato deve essere GET;
- Al passo (4) l’erogatore indica che la risorsa non è ancora pronta, fornendo informazioni circa lo stato della lavorazione della richiesta; il codice HTTP restituito è HTTP status 200 OK;
- Al passo (6) l’erogatore indica che la risorsa è pronta, utilizzando HTTP header Location ; per indicare il percorso dove recuperare la risorsa, il codice HTTP restituito è HTTP status 303 See Other;
- Al passo (8) l’erogatore risponde con la rappresentazione della risorsa,Il codice HTTP restituito è HTTP status 200 OK;


**Configurazione delle API**

Per attuare la configurazione su GovWay si deve procedere con la registrazione dell'API che deve contenere le tre risorse differenti descritti precedentemente.

- Richiesta

Effettuata la registrazione delle API, accedere al dettaglio della risorsa corrispondente alla richiesta ed impostare nella sezione 'ModI PA' un profilo di interazione non bloccante 'PULL' con ruolo 'Richiesta' come mostrato nella figura :numref:`modipa_flusso_PULL_richiesta_REST`:

   .. figure:: ../../_figure_console/modipa_flusso_PULL_richiesta_REST.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PULL_richiesta_REST

    Configurazione della richiesta dell'API REST (PULL)

- Richiesta Stato

Successivamente, accedere al dettaglio dell'azione che consente di richiedere lo stato di processamento ed impostare nella sezione 'ModI PA' un profilo di interazione non bloccante 'PULL' con ruolo 'Richiesta Stato'. Definire anche la correlazione verso la risorsa relativa alla richiesta come mostrato nella figura :numref:`modipa_flusso_PULL_richiestaStato_REST`:

   .. figure:: ../../_figure_console/modipa_flusso_PULL_richiestaStato_REST.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PULL_richiestaStato_REST

    Configurazione della richiesta stato di processamento dell'API REST (PULL)

- Risposta

Accedere al dettaglio dell'azione corrispondente alla risposta ed impostare nella sezione 'ModI PA' un profilo di interazione non bloccante 'PULL' con ruolo 'Risposta'. Definire anche la correlazione verso la risorsa relativa alla richiesta come mostrato nella figura :numref:`modipa_flusso_PULL_risposta_REST`:

   .. figure:: ../../_figure_console/modipa_flusso_PULL_risposta_REST.png
    :scale: 40%
    :align: center
    :name: modipa_flusso_PULL_risposta_REST

    Configurazione della risposta dell'API REST (PUSH)

**Configurazione dell'Erogazione**

Sul dominio dell'erogatore deve essere definita l'erogazione dell'API.

- Richiesta

Le richieste ricevute sull'erogazione vengono inoltrate al backend da GovWay rimanendo poi in attesa dell'acknowledgement.

Ricevuto il messaggio di acknowledgement GovWay verifica che il codice HTTP di stato sia 202 e verifica la presenza dell’header HTTP 'Location'. 


- Richiesta Stato di Processamento

Le richieste che richiedono uno stato del processamento vengono validate da GovWay verificando che il codice HTTP di stato sia 200 (risposta non ancora pronta) o 303 (risposta pronta ad essere recuperata). Nel caso il codice HTTP sia 303 viene anche verificata la presenza dell’header HTTP 'Location'.


- Risposta

GovWay valida le risposte verificando che il codice HTTP di stato sia 200.


.. note::

	**Id Correlazione**

	GovWay estrae dal valore presente nell'header 'Location' (per la richiesta e la richiesta stato) e dall'endpoint (per la risposta) l'identificativo di correlazione al fine di correlare la richiesta con le successive operazioni.


**Configurazione della Fruizione**

Sul dominio del fruitore deve essere definita una fruizione dell'API.

- Richiesta

Le richieste devono essere inoltrate dall'applicativo mittente utilizzando la fruizione dell'API configurata su GovWay. 

Il messaggio di acknowledgement ricevuto viene validato al fine di verificare la presenza dell'header http 'Location' come previsto dalla specifica 'ModI PA'. L'informazione sull'id di correlazione è ottenibile dall'applicativo mittente sulla risposta tramite gli header di integrazione descritti nella sezione :ref:`headerIntegrazione_richiestaInoltrata` e :ref:`headerIntegrazione_other` (per default tramite l'header http 'GovWay-Conversation-ID').

- Richiesta Stato di Processamento e Risposta

Le successive operazioni devono essere inoltrate dall'applicativo mittente utilizzando la fruizione dell'API configurata su GovWay. 


.. note::

	**Id Correlazione**

	GovWay estrae dal valore presente nell'header 'Location' (per la richiesta) e dall'endpoint (per la richiesta stato e per la risposta) l'identificativo di correlazione al fine di correlare la richiesta con le successive operazioni.
