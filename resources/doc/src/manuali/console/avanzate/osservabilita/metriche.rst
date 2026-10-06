.. _osservabilitaMetriche:

Catalogo delle metriche
~~~~~~~~~~~~~~~~~~~~~~~~~

Questa sezione descrive tutte le metriche esposte da GovWay: significato, tipo, label e
significato di ciascuna label. I nomi degli istogrammi seguono le convenzioni Prometheus: per una
metrica ``<name>`` di tipo *histogram* vengono esposte le serie ``<name>_bucket`` (una per confine
``le``), ``<name>_count`` (numero di osservazioni) e ``<name>_sum`` (somma dei valori); la loro
lettura è descritta nella sezione :ref:`osservabilitaIstogrammi`.

.. _osservabilitaLabelComuni:

Label comuni
^^^^^^^^^^^^

Alcune label ricorrono in più metriche; se ne riporta qui il significato una volta sola.

.. list-table::
   :header-rows: 1
   :widths: 20 80

   * - Label
     - Significato
   * - ``role``
     - Ruolo del gateway nella transazione: ``inbound`` = erogazione (GovWay espone verso l'esterno un
       servizio interno), ``outbound`` = fruizione (GovWay invoca per conto di un applicativo
       interno un servizio esterno), ``unknown`` = non determinabile.
   * - ``result_class``
     - Classe a cui appartiene l'esito della transazione: ``FAULT`` = fault applicativo restituito
       dal backend (codice ``2``), ``OK`` = completata con successo (codici dell'esito complessivo
       'Completata con Successo'), ``KO`` = tutti gli altri esiti.
       Utilizza la stessa codifica del tracciamento su file; le classi di esito sono descritte nella
       sezione :ref:`mon_esito_transazione` della Guida alla Console di Monitoraggio.
   * - ``result_code``
     - Esito della transazione nella codifica numerica di GovWay (es. ``0`` = Ok, ``2`` = Fault
       Applicativo, ``30`` = Risposta HTTP 5xx); l'elenco completo dei codici è riportato nella
       sezione :ref:`mon_esito_transazione`. Presente solamente sulle metriche di tipo *counter*.
   * - ``http_status``
     - Codice HTTP restituito al client dal gateway. Presente solamente sulle metriche di tipo
       *counter*.
   * - ``protocol``
     - Profilo di interoperabilità/protocollo della transazione (es. ``trasparente``, ``modipa``,
       ``spcoop``, ``sdi``).

.. _osservabilitaLabelDettaglio:

Label delle metriche di dettaglio
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Le metriche di dettaglio per servizio (sezione :ref:`osservabilitaCatalogoDettaglio`) riportano le
seguenti label, che identificano l'erogazione o la fruizione che ha gestito la richiesta. I nomi
riprendono quelli utilizzati negli header di integrazione e nel tracciamento su file. Le label sono
sempre presenti; quelle non applicabili hanno valore vuoto.

.. list-table::
   :header-rows: 1
   :widths: 20 80

   * - Label
     - Significato
   * - ``interface_id``
     - Identificativo univoco dell'erogazione o della fruizione, nella stessa forma utilizzata nella
       url di invocazione (i tipi dei soggetti e del servizio sono indicati solo se diversi da quelli
       di default del profilo): ``<erogatore>/<servizio>/v<versione>`` per un'erogazione,
       ``<fruitore>/<erogatore>/<servizio>/v<versione>`` per una fruizione. Viene riportata sempre la
       configurazione predefinita, anche quando la richiesta è gestita da un gruppo.
   * - ``group``
     - Nome del gruppo (configurazione specifica per un insieme di azioni) che ha gestito la
       richiesta, come indicato nella console; ``Predefinito`` per la configurazione di default.
   * - ``provider``, ``provider_type``
     - Nome e tipo del soggetto erogatore.
   * - ``sender``, ``sender_type``
     - Nome e tipo del soggetto fruitore; valorizzate solamente per le fruizioni.
   * - ``service``, ``service_type``, ``service_version``
     - Nome, tipo e versione del servizio (erogazione/fruizione).
   * - ``action``
     - Risorsa (API REST) o azione (API SOAP) invocata.
   * - ``api``, ``api_version``
     - Nome e versione dell'API implementata dall'erogazione o dalla fruizione.
   * - ``api_provider``, ``api_provider_type``
     - Nome e tipo del soggetto referente dell'API; valorizzate solamente per i profili di
       interoperabilità che prevedono il soggetto referente (es. ``spcoop``).
   * - ``tags``
     - Tag associati all'API, in ordine alfabetico e separati da virgola (es.
       ``Anagrafica,PagamentiTelematici``); vuota se l'API non possiede tag. Per selezionare le
       serie di tutte le API che possiedono un determinato tag si può utilizzare l'espressione
       regolare ``tags=~"(.*,)?Anagrafica(,.*)?"``.

Metriche di sistema di GovWay
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Gauge che campionano *live* (ad ogni scrape) lo stato interno del gateway. Sono sempre esposte.

.. list-table::
   :header-rows: 1
   :widths: 34 12 54

   * - Metrica
     - Tipo
     - Descrizione e label
   * - ``govway_active_transactions``
     - gauge
     - Numero di transazioni attualmente in corso. Nessuna label.
   * - ``govway_active_protocol_ids``
     - gauge
     - Numero di identificativi di protocollo presenti nel filtro anti-duplicati in memoria.
       Nessuna label.
   * - ``govway_rate_limiting_active_threads``
     - gauge
     - Numero di richieste concorrenti attualmente in gestione dal controllo del traffico.
       Nessuna label.
   * - ``govway_congested``
     - gauge
     - Flag di congestione del gateway: ``1`` se congestionato, ``0`` altrimenti. Nessuna label.
   * - ``govway_active_connectors``
     - gauge
     - Numero di connettori con un inoltro/consegna in corso. Label: ``role``
       (``inbound``/``outbound``).
   * - ``govway_datasource_allocated_connections``
     - gauge
     - Connessioni attualmente allocate su ciascun datasource. Label: ``datasource`` = nome JNDI
       del datasource (es. ``org.govway.datasource``). Le serie corrispondono ai datasource
       riportati tra le 'Connessioni Attive' della sezione :ref:`strumenti_runtime` e compaiono dopo il
       primo utilizzo del datasource; un datasource condiviso da più componenti è riportato
       in una sola serie.
   * - ``govway_db_connections_held``
     - gauge
     - Connessioni al database attualmente trattenute da ciascun componente di GovWay. Label:
       ``component`` con valori ``runtime`` (gestione delle richieste), ``transactions``
       (tracciamento delle transazioni), ``statistics`` (generazione delle statistiche),
       ``scheduled_deliveries_dispatcher``, ``scheduled_deliveries_runtime`` e
       ``scheduled_deliveries_transactions`` (consegne prese in carico),
       ``message_box_runtime`` e ``message_box_transactions`` (servizio di Message Box).
       Un componente configurato per utilizzare il datasource di un altro componente (ad esempio
       il tracciamento sullo stesso database del runtime) gli delega l'allocazione delle
       connessioni: queste vengono conteggiate sotto quest'ultimo e la serie del componente
       resta a ``0``.
   * - ``govway_queue_allocated_connections``
     - gauge
     - Connessioni verso il broker JMS attualmente allocate. Nessuna label.
   * - ``govway_http_pool_connections``
     - gauge
     - Stato dei pool di connessioni del client HTTP verso i backend, aggregato su tutti i pool.
       Label: ``mode`` = ``bio`` (client sincrono) | ``nio`` (client asincrono); ``state`` con
       valori ``leased`` (connessioni in uso), ``pending`` (richieste in attesa di una connessione,
       indicatore di saturazione), ``available`` (connessioni idle disponibili), ``max`` (massimo
       configurato). Nota: con la configurazione BIO di default il pool non è utilizzato, quindi i
       valori significativi sono sul ``mode="nio"``.
   * - ``govway_cache_elements``
     - gauge
     - Numero di elementi presenti in ciascuna cache interna. Label: ``cache`` = nome della cache
       (es. ``configurazionePdD``, ``autenticazione``, ``autorizzazione``, ``gestoreRichieste-API``,
       ``responseCaching``, ...).

Metriche di transazione
^^^^^^^^^^^^^^^^^^^^^^^^

Registrate una volta per transazione. Sono sempre attive (a prescindere dalla proprietà di
dettaglio per servizio).

.. list-table::
   :header-rows: 1
   :widths: 34 12 54

   * - Metrica
     - Tipo
     - Descrizione e label
   * - ``govway_requests_total``
     - counter
     - Numero di richieste gestite. Label: ``role``, ``result_class``, ``result_code``,
       ``http_status``, ``protocol``.
   * - ``govway_request_duration_seconds``
     - histogram
     - Latenza di elaborazione della richiesta, in secondi. Label: ``role``, ``result_class``,
       ``protocol`` e ``phase`` con valori: ``total`` (tempo totale di attraversamento del
       gateway), ``service`` (latenza del servizio di backend), ``gateway`` (tempo speso
       internamente dal gateway). Bucket: ``latency-slotMs``.
   * - ``govway_request_size_bytes``
     - histogram
     - Dimensione dei messaggi, in byte. Label: ``role``, ``result_class``, ``protocol`` e
       ``direction`` con valori: ``in_req`` (richiesta in ingresso al gateway), ``out_req``
       (richiesta inoltrata al backend), ``in_resp`` (risposta ricevuta dal backend),
       ``out_resp`` (risposta restituita al client). Bucket: ``size-slotByte``.

.. _osservabilitaCatalogoDettaglio:

Metriche di dettaglio per servizio
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Vengono prodotte **solo per le erogazioni/fruizioni su cui sono abilitate le metriche di
dettaglio** (sezione :ref:`osservabilitaMetricheDettaglio`). Consentono di analizzare latenze,
dimensioni e tempi di elaborazione per singola API/servizio/operazione.

Rispetto alle metriche di transazione aggiungono le label descritte nella sezione
:ref:`osservabilitaLabelDettaglio`.

.. list-table::
   :header-rows: 1
   :widths: 34 12 54

   * - Metrica
     - Tipo
     - Descrizione e label
   * - ``govway_service_requests_total``
     - counter
     - Come ``govway_requests_total`` ma con il dettaglio per servizio. Label: ``role``,
       ``result_class``, ``result_code``, ``http_status``, ``protocol`` e le label di dettaglio.
   * - ``govway_service_request_duration_seconds``
     - histogram
     - Come ``govway_request_duration_seconds`` ma con il dettaglio per servizio. Label:
       ``role``, ``result_class``, ``protocol``, ``phase`` (``total``/``service``/``gateway``) e le
       label di dettaglio. Bucket: ``latency-slotMs``.
   * - ``govway_service_request_size_bytes``
     - histogram
     - Come ``govway_request_size_bytes`` ma con il dettaglio per servizio. Label: ``role``,
       ``result_class``, ``protocol``, ``direction`` (``in_req``/``out_req``/``in_resp``/``out_resp``) e
       le label di dettaglio. Bucket: ``size-slotByte``.
   * - ``govway_processing_phase_seconds``
     - histogram
     - Latenza delle singole fasi funzionali di elaborazione, in secondi. Label: ``role``, ``phase``
       = fase funzionale (vedi elenco sotto) e le label di dettaglio. Bucket: ``latency-slotMs``.

I valori possibili della label ``phase`` per ``govway_processing_phase_seconds`` corrispondono alle
fasi di elaborazione di GovWay: ``token``, ``authentication``, ``tokenAuthentication``,
``tokenApplicationAuthentication``, ``authorization``, ``contentAuthorization``,
``requestValidation``, ``responseValidation``, ``trafficControl_maxRequests``,
``trafficControl_rateLimiting``, ``requestMessageSecurity``, ``responseMessageSecurity``,
``requestAttachmentsHandling``, ``responseAttachmentsHandling``, ``requestApplicationCorrelation``,
``responseApplicationCorrelation``, ``requestTracing``, ``responseTracing``, ``dumpRequestInbound``,
``dumpRequestOutbound``, ``dumpResponseInbound``, ``dumpResponseOutbound``,
``dumpBinaryRequestInbound``, ``dumpBinaryRequestOutbound``, ``dumpBinaryResponseInbound``,
``dumpBinaryResponseOutbound``, ``dumpIntegrationManager``, ``responseCachingDigestComputation``,
``responseCachingReadFromCache``, ``responseCachingSaveInCache``, ``requestTransformation``,
``responseTransformation``, ``attributeAuthority``.

Metriche di persistenza del tracciamento
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Misurano il tempo impiegato per la persistenza del tracciamento su database. Vengono registrate per
**tutte** le transazioni (non solo quelle "lente"), ma **richiedono l'abilitazione dello slow-log**
(proprietà ``org.openspcoop2.pdd.transazioni.slowLog.enabled=true``): senza tale abilitazione non
vengono prodotte.

.. list-table::
   :header-rows: 1
   :widths: 40 12 48

   * - Metrica
     - Tipo
     - Descrizione e label
   * - ``govway_tracing_persistence_seconds``
     - histogram
     - Durata totale della persistenza del tracciamento su DB. Label: ``phase`` = fase di
       tracciamento in cui avviene la persistenza (``IN_REQUEST``, ``OUT_REQUEST``,
       ``OUT_RESPONSE``, ``POST_OUT_RESPONSE``). Bucket: ``persistence-slotMs``.
   * - ``govway_tracing_persistence_components_seconds``
     - histogram
     - Durata delle componenti *aggregate* della persistenza. Label: ``phase`` e ``component`` con
       valori ``fillTransaction`` (costruzione del record di transazione), ``checkTraffic``
       (controllo del traffico), ``writeDatabase`` (complessivo delle operazioni su DB). Bucket:
       ``persistence-slotMs``.
   * - ``govway_tracing_persistence_components_details_seconds``
     - histogram
     - Durata delle componenti *di dettaglio* della persistenza. Label: ``phase`` e ``component``
       con valori: ``fillTransaction``, ``checkTraffic``, ``checkTrafficRemoveThread``,
       ``checkTrafficPreparePolicy``, ``fileTrace``, ``processTransactionInfo``, ``getConnection``,
       ``insertTransaction``, ``insertDiagnostics``, ``insertTrace``, ``insertContents``,
       ``insertResources``, ``commit``. Bucket: ``persistence-slotMs``.

Metriche degli eventi
^^^^^^^^^^^^^^^^^^^^^^

.. list-table::
   :header-rows: 1
   :widths: 34 12 54

   * - Metrica
     - Tipo
     - Descrizione e label
   * - ``govway_events_total``
     - counter
     - Numero di eventi registrati dal gateway. Label: ``type`` (tipo di evento), ``code`` (codice
       dell'evento), ``severity`` (severità), ``cluster_id`` (identificativo del nodo del cluster
       che ha generato l'evento).

.. _osservabilitaMetricheJvm:

Metriche di sistema JVM/process
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Se abilitate (``observability.metrics.system-metrics.enabled=true``, sezione
:ref:`osservabilitaConfigurazioneAvanzata`), vengono esposte le metriche
standard dei binder JVM/process di Micrometer, tra cui:

- ``jvm_memory_used_bytes`` / ``jvm_memory_committed_bytes`` / ``jvm_memory_max_bytes`` (label
  ``area``, ``id``): utilizzo della memoria JVM per area (heap/nonheap) e pool;
- ``jvm_gc_pause_seconds`` e altre ``jvm_gc_*``: attività del garbage collector;
- ``jvm_threads_live_threads`` / ``jvm_threads_daemon_threads`` / ``jvm_threads_states_threads``:
  stato dei thread;
- ``jvm_classes_loaded_classes``: numero di classi caricate;
- ``jvm_buffer_*``: buffer pool NIO;
- ``system_cpu_count``, ``system_cpu_usage``, ``process_cpu_usage``: CPU disponibili e utilizzo;
- ``process_uptime_seconds``, ``process_start_time_seconds``: uptime e istante di avvio del processo;
- ``process_files_open_files`` / ``process_files_max_files``: descrittori di file aperti/massimi.

Per il dettaglio completo di queste metriche si rimanda alla documentazione di Micrometer.
