.. _headerRisposta:

Informazioni restituite dal gateway nella risposta all'applicativo client
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Header Standard**

Le informazioni restituite dal gateway all'applicativo client, sia per
per le fruizioni che per le erogazioni, sono riassunte nella :numref:`headerGw2ClientTab`.

.. table:: Header restituiti dal gateway nella risposta all'applicativo client
   :widths: 35 65
   :name: headerGw2ClientTab

   =========================================  ==============================================
   Nome Header Trasporto                      Descrizione                                                                       
   =========================================  ==============================================
   GovWay-Transaction-ID                      Identificativo della transazione assegnato da GovWay   
   GovWay-Message-ID                          Identificativo del messaggio assegnato da GovWay                          
   GovWay-Relates-To                          Identificativo del messaggio riferito                                                 
   GovWay-Conversation-ID                     Identificativo della conversazione                                                    
   GovWay-Application-Message-ID              Identificativo del messaggio assegnato dall'applicativo (solo nel caso di Fruizione)
   =========================================  ==============================================

**Header RateLimiting**

All'applicativo client vengono inoltre restituiti ulteriori header http
informativi se l'applicativo erogatore non è disponibile o se sono stati
attivati meccanismi di Rate Limiting (sezione :ref:`rateLimiting`).

.. table:: Ulteriori header restituiti dal gateway nella risposta all'applicativo client
   :class: longtable
   :widths: 30 30 40
   :name: headerGw2ClientExtraTab

   ========================================================================================================================  =============================================================================================================================  =================
   Nome Header Trasporto                                                                                                     Descrizione                                                                                                                    Motivazione
   ========================================================================================================================  =============================================================================================================================  =================
   Retry-After                                                                                                               Indica al client il numero di secondi dopo i quali ripresentarsi poichè il servizio contattato non è al momento disponibile.   Le principali cause della generazione di tale header sono imputabili alla non raggiungibilità un applicativo erogatore, alla violazione di politiche di RateLimiting o a quando un servizio è temporaneamente disabilitato
   X-RateLimit-Limit                                                                                                         Indica il numero massimo di richieste effettuabili                                                                             Rate-Limiting attivato con policy di tipo 'NumeroRichieste-ControlloRealtime\*' (sezione :ref:`rateLimiting`)                                                                                                                                         
   X-RateLimit-Remaining                                                                                                     Numero di richieste rimanenti prima del prossimo reset                                                                         Rate-Limiting attivato con policy di tipo 'NumeroRichieste-ControlloRealtime\*' (sezione :ref:`rateLimiting`)
   X-RateLimit-Reset                                                                                                         Numero di secondi mancante al prossimo reset                                                                                   Rate-Limiting attivato con policy di tipo 'NumeroRichieste-ControlloRealtime\*' (sezione :ref:`rateLimiting`)                                                                                                                                         
   GovWay-RateLimit-ConcurrentRequest-Limit                                                                                  Indica il numero massimo di richieste concorrenti inviabili                                                                    Rate-Limiting attivato con policy di tipo 'NumeroRichieste-RichiesteSimultanee' (sezione :ref:`rateLimiting`)
   GovWay-RateLimit-ConcurrentRequest-Remaining                                                                              Indica il numero massimo di richieste concorrenti ancora inviabili                                                             Rate-Limiting attivato con policy di tipo 'NumeroRichieste-RichiesteSimultanee' (sezione :ref:`rateLimiting`)                                                                                                                                         
   GovWay-RateLimit-BandwithQuota-Limit                                                                                      Indica la massima banda occupabile                                                                                             Rate-Limiting attivato con policy di tipo 'OccupazioneBanda-\*' (sezione :ref:`rateLimiting`)                                                                                                                                                         
   GovWay-RateLimit-BandwithQuota-Remaining                                                                                  Indica la banda ancora occupabile prima del prossimo reset                                                                     Rate-Limiting attivato con policy di tipo 'OccupazioneBanda-\*' (sezione :ref:`rateLimiting`)                                                                                                                                                         
   GovWay-RateLimit-BandwithQuota-Reset                                                                                      Numero di secondi mancante al prossimo reset                                                                                   Rate-Limiting attivato con policy di tipo 'OccupazioneBanda-\*' (sezione :ref:`rateLimiting`)                                                                                                                                                         
   GovWay-RateLimit-AvgTimeResponse-Limit                                                                                    Tempo medio di risposta atteso                                                                                                 Rate-Limiting attivato con policy di tipo 'TempoMedioRisposta-\*' (sezione :ref:`rateLimiting`)
   GovWay-RateLimit-AvgTimeResponse-Reset                                                                                    Numero di secondi mancante al prossimo reset                                                                                   Rate-Limiting attivato con policy di tipo 'TempoMedioRisposta-\*' (sezione :ref:`rateLimiting`)                                                                                                                                                       
   GovWay-RateLimit-TimeResponseQuota-Limit                                                                                  Tempo complessivo di risposta occupabile                                                                                       Policy creata con risorsa di tipo 'TempoComplessivioRisposta' (sezione :ref:`registroPolicy`)
   GovWay-RateLimit-TimeResponseQuota-Remaining                                                                              Tempo di risposta ancora occupabile prima del prossimo reset                                                                   Policy creata con risorsa di tipo 'TempoComplessivioRisposta' (sezione :ref:`registroPolicy`)                                                                                                                                                           
   GovWay-RateLimit-TimeResponseQuota-Reset                                                                                  Numero di secondi mancante al prossimo reset                                                                                   Policy creata con risorsa di tipo 'TempoComplessivioRisposta' (sezione :ref:`registroPolicy`)
   GovWay-RateLimit-RequestSuccessful-Limit, GovWay-RateLimit-RequestFailed-Limit, GovWay-RateLimit-Fault-Limit              Indica il numero massimo di richieste effettuabili                                                                             Policy creata rispettivamente con risorsa di tipo 'NumeroRichiesteCompletateConSuccesso', 'NumeroRichiesteFallite' e 'NumeroFaultApplicativi' (sezione :ref:`registroPolicy`)                                                                           
   GovWay-RateLimit-RequestSuccessful-Remaining, GovWay-RateLimit-RequestFailed-Remaining, GovWay-RateLimit-Fault-Remaining  Numero di richieste rimanenti prima del prossimo reset                                                                         Policy creata rispettivamente con risorsa di tipo 'NumeroRichiesteCompletateConSuccesso', 'NumeroRichiesteFallite' e 'NumeroFaultApplicativi' (sezione :ref:`registroPolicy`)                                                                           
   GovWay-RateLimit-RequestSuccessful-Reset, GovWay-RateLimit-RequestFailed-Reset, GovWay-RateLimit-Fault-Reset              Numero di secondi mancante al prossimo reset                                                                                   Policy creata rispettivamente con risorsa di tipo 'NumeroRichiesteCompletateConSuccesso', 'NumeroRichiesteFallite' e 'NumeroFaultApplicativi' (sezione :ref:`registroPolicy`)
   ========================================================================================================================  =============================================================================================================================  =================


**Header di Sicurezza**

È stata introdotta una politica di generazione automatica degli header HTTP indicati nella :numref:`headerGw2ClientVulnerabilitàTab`, se non ritornati dal backend che implementa l'API, con lo scopo di evitare alcune vulnerabilità a cui possono essere soggette le implementazioni delle API.

.. note::

   Il caching viene disabilitato per evitare che delle risposte vengano inopportunamente messe in cache, come indicato nelle `Linee Guida - raccomandazioni tecniche per REST 'RAC_REST_NAME_010' <https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/doc/04_Raccomandazioni%20di%20implementazione/05_raccomandazioni-tecniche-per-rest/02_progettazione-e-naming.html#rac-rest-name-010-il-caching-http-deve-essere-disabilitato>`_. Il mancato rispetto di questa raccomandazione può portare all’esposizione accidentale di dati personali. 

.. table:: Header restituiti dal gateway nella risposta all'applicativo client, se non ritornati dal Backend
   :widths: 35 65
   :name: headerGw2ClientVulnerabilitàTab

   =========================================  ============================================== 
   Nome Header Trasporto                      Valore                                                                                                             
   =========================================  ==============================================
   X-Content-Type-Options                     nosniff            
   Cache-Control                              no-cache, no-store, must-revalidate                                                 
   Pragma                                     no-cache                                                 
   Expires                                    0
   Vary                                       \*
   =========================================  ==============================================

È possibile configurare una gestione personalizzata degli header di sicurezza per la singola API registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *securityHeaders.enabled* : consente di disabilitare la generazione degli headers di sicurezza. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *securityHeaders.default* : consente di disabilitare la generazione degli headers di sicurezza di default. I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *securityHeaders* : lista di nomi di header http, separati con la virgola. Per ogni header indicato deve essere registrata una ulteriore proprietà dove va indicato il valore da associare all'header:

	- *securityHeaders.<nomeHeader>* = <valoreHeader>


**Header Peer**

Gli header HTTP descritti nelle tabelle :numref:`headerGw2ClientTab` e :numref:`headerGw2ClientExtraTab` vengono generati da GovWay e restituiti al client. Nel caso in cui la risposta del backend contenga header con gli stessi nomi, questi vengono sostituiti con quelli generati da GovWay.

È stata introdotta una funzionalità che consente di restituire al client anche gli header generati dal backend, rinominandoli mediante l'introduzione del prefisso "Peer" nel nome. Per impostazione predefinita, GovWay restituisce al client, come header "Peer", tutti gli header definiti nelle tabelle :numref:`headerGw2ClientTab` e :numref:`headerGw2ClientExtraTab`, qualora siano presenti nella risposta del backend.

Questa funzionalità, nella configurazione di default, è particolarmente utile negli scenari di fruizione ModI o SPCoop, dove anche la parte erogatrice è esposta tramite GovWay. In tali contesti, permette al client di ricevere gli identificativi generati dalla parte erogatrice (vedi :numref:`headerPeerGw2ClientTab`), migliorando la tracciabilità e la gestione delle richieste. 

.. table:: Header 'Peer' di default restituiti dal gateway nella risposta all'applicativo client
   :widths: 35 65
   :name: headerPeerGw2ClientTab

   =========================================  ==============================================
   Nome Header ritornato al Client            Nome Header Trasporto generato dal backend                                                                    
   =========================================  ==============================================
   GovWay-Peer-Transaction-ID                 GovWay-Transaction-ID     
   GovWay-Peer-Message-ID                     GovWay-Message-ID
   GovWay-Peer-Relates-To                     GovWay-Relates-To                                      
   GovWay-Peer-Conversation-ID                GovWay-Conversation-ID                                                    
   GovWay-Peer-Application-Message-ID         GovWay-Application-Message-ID
   X-RateLimit-Peer-Limit                     X-RateLimit-Limit   
   X-RateLimit-Peer-Remaining                 X-RateLimit-Remaining   
   X-RateLimit-Peer-Reset                     X-RateLimit-Reset  
   GovWay-RateLimit-Peer-...                  GovWay-RateLimit-...
   =========================================  ==============================================

È possibile personalizzare gli header "Peer" restituiti al client sia a livello generale del prodotto sia per la singola API. La generazione di un header "Peer" avviene esclusivamente se è presente l'header corrispondente nel backend.

Per personalizzare la configurazione a livello di singola API, è possibile registrare le seguenti :ref:`configProprieta` a livello di erogazione o fruizione:

- *connettori.peer-header.default.enabled* : consente di disabilitare la configurazione predefinita degli header "Peer". I valori associabili alla proprietà sono 'true' o 'false'. Per default questo controllo è abilitato.

- *connettori.peer-header.NOME-PEER-HEADER* : permette di definire una nuova regola "Peer". Il suffisso NOME-PEER-HEADER rappresenta il nome dell'header HTTP da restituire al client. Il valore della proprietà deve essere una lista di nomi di header HTTP, separati da virgola, da cercare negli header restituiti dal backend. La lista viene analizzata in ordine e, non appena viene trovato un nome corrispondente, il valore di quell'header viene utilizzato per valorizzare l'header "Peer".

- *connettori.peer-header.NOME-PEER-HEADER.regexps* : consente di definire una nuova regola "Peer" utilizzando un'espressione regolare. Il valore della proprietà è un'espressione regolare che viene applicata agli header HTTP restituiti dal backend. Ogni header che soddisfa la regex viene restituito come header "Peer" al client. Il nome dell'header HTTP definito in NOME-PEER-HEADER può referenziare gruppi di cattura dell'espressione regolare utilizzando la sintassi ${numeroPosizioneCattura}. Ad esempio, per implementare la logica predefinita degli header di rate limiting, si può definire la seguente configurazione: 

	- connettori.peer-header.${1}Peer-${2}.regexp=(.+-RateLimit-)(.+)

Per personalizzare la configurazione a livello generale, è possibile modificare il file <directory-lavoro>/govway_local.properties dove può essere configurato un mapping sia in modo specifico che utilizzando espressioni regolari.

   ::

      # mapping puntuale
      org.openspcoop2.pdd.headers.peer.<NOME-PEER-HEADER>.headers=<NOME-BACKEND-HEADER>

      # mapping tramite espressione regolare
      # se l'espressione regolare possiede dei gruppi di cattura è possibile referenziarli tramite la sintassi '${numeroPosizioneCattura}'
      org.openspcoop2.pdd.headers.peer.<NOME-PEER-HEADER>.regexp=<espressioneRegolare>

Di seguito vengono riportate come esempio alcune delle configurazioni di default attive:

   ::

      # mapping puntuale
      org.openspcoop2.pdd.headers.peer.GovWay-Peer-Transaction-ID.headers=GovWay-Transaction-ID

      # mapping tramite espressione regolare
      org.openspcoop2.pdd.headers.peer.${1}Peer-${2}.regexp=(.+-RateLimit-)(.+)

	

