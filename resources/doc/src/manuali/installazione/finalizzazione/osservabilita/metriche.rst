.. _osservabilitaMetriche:

Catalogo delle metriche
~~~~~~~~~~~~~~~~~~~~~~~~~

Questa sezione descrive tutte le metriche esposte da GovWay: significato, tipo, label e
significato di ciascuna label. I nomi degli istogrammi seguono le convenzioni Prometheus: per una
metrica ``<name>`` di tipo *histogram* vengono esposte le serie ``<name>_bucket`` (una per confine
``le``), ``<name>_count`` (numero di osservazioni) e ``<name>_sum`` (somma dei valori).

.. _osservabilitaLabelComuni:

Label comuni
^^^^^^^^^^^^

Alcune label ricorrono in più metriche; se ne riporta qui il significato una volta sola.

.. list-table::
   :header-rows: 1
   :widths: 20 80

   * - Label
     - Significato
   * - ``pdd_role``
     - Ruolo della porta di dominio: ``inbound`` = erogazione (GovWay espone verso l'esterno un
       servizio interno), ``outbound`` = fruizione (GovWay invoca per conto di un applicativo
       interno un servizio esterno), ``unknown`` = non determinabile.
   * - ``outcome``
     - Esito della transazione: ``ok`` = completata con successo, ``fault`` = fault applicativo,
       ``ko`` = errore, ``unknown`` = non classificabile.
   * - ``protocol``
     - Profilo di interoperabilità/protocollo della transazione (es. ``trasparente``, ``modipa``,
       ``spcoop``, ``sdi``, ``as4``, ``fatturapa``).
   * - ``service``
     - Nome del servizio (erogazione/fruizione) che ha gestito la richiesta.
   * - ``service_version``
     - Versione del servizio (erogazione/fruizione).
   * - ``api``
     - Nome dell'API (accordo di servizio parte comune) a cui appartiene il servizio.
   * - ``api_version``
     - Versione dell'API (accordo di servizio parte comune).
   * - ``action``
     - Operazione/risorsa dell'API invocata (identificativo dell'operazione).

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
   * - ``govway_pdd_congested``
     - gauge
     - Flag di congestione del gateway: ``1`` se congestionato, ``0`` altrimenti. Nessuna label.
   * - ``govway_active_connectors``
     - gauge
     - Numero di connettori con un inoltro/consegna in corso. Label: ``pdd_role``
       (``inbound``/``outbound``).
   * - ``govway_datasource_allocated_connections``
     - gauge
     - Connessioni al database attualmente allocate, per pool. Label: ``pool`` con valori
       ``runtime`` (configurazione/runtime), ``transactions`` (tracciamento transazioni),
       ``statistics`` (statistiche), ``scheduled_deliveries`` (consegne prese in carico),
       ``message_box_deliveries`` (message box).
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
     - Numero di richieste gestite. Label: ``pdd_role``, ``outcome``, ``protocol``.
   * - ``govway_request_duration_seconds``
     - histogram
     - Latenza di elaborazione della richiesta, in secondi. Label: ``pdd_role``, ``outcome``,
       ``protocol`` e ``phase`` con valori: ``total`` (tempo totale di attraversamento del
       gateway), ``service`` (latenza del servizio di backend), ``gateway`` (tempo speso
       internamente dal gateway). Bucket: ``latency-slotMs``.
   * - ``govway_request_size_bytes``
     - histogram
     - Dimensione dei messaggi, in byte. Label: ``pdd_role``, ``outcome``, ``protocol`` e
       ``direction`` con valori: ``in_req`` (richiesta in ingresso al gateway), ``out_req``
       (richiesta inoltrata al backend), ``in_resp`` (risposta ricevuta dal backend),
       ``out_resp`` (risposta restituita al client). Bucket: ``size-slotByte``.

.. _osservabilitaMetricheDettaglio:

Metriche di dettaglio per servizio
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Vengono prodotte **solo per le erogazioni/fruizioni su cui è abilitata** la proprietà
``observability.metrics.details=true`` (configurabile tra le proprietà del servizio nella console
di gestione). Consentono di analizzare latenze, dimensioni e tempi di elaborazione per singola
API/servizio/operazione.

Rispetto alle metriche di transazione aggiungono le label ``service``, ``service_version``,
``action``, ``api``, ``api_version`` (vedi :ref:`osservabilitaLabelComuni`).

.. list-table::
   :header-rows: 1
   :widths: 34 12 54

   * - Metrica
     - Tipo
     - Descrizione e label
   * - ``govway_service_request_duration_seconds``
     - histogram
     - Come ``govway_request_duration_seconds`` ma con il dettaglio per servizio. Label:
       ``pdd_role``, ``outcome``, ``protocol``, ``phase`` (``total``/``service``/``gateway``),
       ``service``, ``service_version``, ``action``, ``api``, ``api_version``. Bucket:
       ``latency-slotMs``.
   * - ``govway_service_request_size_bytes``
     - histogram
     - Come ``govway_request_size_bytes`` ma con il dettaglio per servizio. Label: ``pdd_role``,
       ``outcome``, ``protocol``, ``direction`` (``in_req``/``out_req``/``in_resp``/``out_resp``),
       ``service``, ``service_version``, ``action``, ``api``, ``api_version``. Bucket:
       ``size-slotByte``.
   * - ``govway_processing_phase_seconds``
     - histogram
     - Latenza delle singole fasi funzionali di elaborazione, in secondi. Label: ``service``,
       ``service_version``, ``action``, ``api``, ``api_version``, ``pdd_role`` e ``phase`` = fase
       funzionale (vedi elenco sotto). Bucket: ``latency-slotMs``.

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

Metriche di sistema JVM/process
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Se abilitate (``observability.metrics.system-metrics.enabled=true``), vengono esposte le metriche
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
