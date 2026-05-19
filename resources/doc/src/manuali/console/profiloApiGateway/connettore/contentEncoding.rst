.. _contentEncodingDecompress:

Decompressione automatica del body (Content-Encoding)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il protocollo HTTP (`RFC 9110, Section 8.4 <https://www.rfc-editor.org/rfc/rfc9110#section-8.4>`_) consente al mittente di codificare il body di un messaggio con uno schema di compressione, dichiarato attraverso l'header *Content-Encoding* (tipicamente *gzip* o *deflate*) al fine di ridurne le dimensioni sul wire.

Per default GovWay si comporta da gateway trasparente: il body viene propagato così come ricevuto, preservando integralmente l'header *Content-Encoding*. È il chiamante a valle a doverlo eventualmente decomprimere. Questo comportamento è ottimale negli scenari di puro passthrough, in cui GovWay non deve ispezionare o trasformare il payload.

In presenza di funzionalità che richiedono di operare sul payload in chiaro (validazione di schema, trasformazioni, correlazione applicativa, etc.), il body compresso deve essere prima decodificato. È possibile abilitare la decompressione automatica registrando le seguenti :ref:`configProprieta` sulla singola erogazione o fruizione. La decompressione è configurabile separatamente per richiesta e risposta:

- *connettori.contentEncoding.request.decompress*: decompressione del body della richiesta ricevuta dal client;

- *connettori.contentEncoding.response.decompress*: decompressione del body della risposta ricevuta dal backend;

- *connettori.contentEncoding.decompress*: ombrello che imposta lo stesso valore sia per richiesta sia per risposta (valido in mancanza di un'impostazione specifica con le proprietà precedenti).

I valori ammessi sono *true* o *false*; il default è *false* (comportamento di puro passthrough).

Quando la decompressione automatica è attiva, GovWay:

- decodifica il body in chiaro così che le logiche a valle lavorino sul payload originale;

- rimuove gli header *Content-Encoding* e *Content-Length* dalla request/response, poiché si riferiscono al payload codificato e non sono più consistenti con il body in chiaro che viene poi propagato (per il body in transito sul wire viene utilizzato il *Transfer-Encoding: chunked*, vedi anche :ref:`contentLengthRisposta`);

- emette un diagnostico dedicato *contentEncoding.decompressed* che evidenzia l'avvenuta decompressione e il valore originale dell'header *Content-Encoding*.

Gli schemi di compressione attualmente gestiti dalla decompressione automatica sono *gzip*, *x-gzip* (alias storico di gzip) e *deflate* (con autodetect tra le varianti `RFC 1950 <https://www.rfc-editor.org/rfc/rfc1950>`_ "zlib-wrapped" e `RFC 1951 <https://www.rfc-editor.org/rfc/rfc1951>`_ "raw"), allineati al default di Apache HttpClient 5.

.. note::
   La gestione dei *Content-Encoding* non supportati (es. *br*, *zstd*, *compress*) avviene esclusivamente quando l'opzione di decompressione automatica è abilitata. In tal caso GovWay rifiuta esplicitamente il messaggio: viene emesso il diagnostico *contentEncoding.unsupported* e la chiamata fallisce con un errore di processamento, evitando di propagare a valle un body in chiaro con header *Content-Encoding* "stale" che ne falsi l'integrità. Quando invece l'opzione è disabilitata (default), il body transita opaco a prescindere dal valore di *Content-Encoding*, in coerenza con il comportamento di puro passthrough.

.. note::
   Quando la decompressione automatica è attiva, sia il dump binario che il tracciamento *FileTrace* registrano il body **già decompresso**. Gli header *Content-Encoding* e *Content-Length* originali restano comunque visibili nel dump della sezione header (richiesta/risposta in ingresso), così da consentire l'analisi del wire originale dal punto di vista della negoziazione HTTP, mentre risultano assenti nella sezione header in uscita verso il chiamante a valle.
