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

Le proprietà vanno registrate sull'erogazione o sulla fruizione e non sul singolo gruppo di risorse: la decompressione della richiesta deve essere decisa prima di leggerne il contenuto, che può essere necessario per identificare l'azione (ad esempio nelle API SOAP).

Il default per l'intera installazione è definito, separatamente per ciascun modulo, dalle seguenti proprietà del file *govway_local.properties*, che le proprietà registrate sull'erogazione o sulla fruizione ridefiniscono:

- *org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.contentEncoding.decompress*: richiesta ricevuta dalle fruizioni;

- *org.openspcoop2.pdd.services.ricezioneBuste.contentEncoding.decompress*: richiesta ricevuta dalle erogazioni;

- *org.openspcoop2.pdd.services.inoltroBuste.contentEncoding.decompress*: risposta ricevuta dalle fruizioni;

- *org.openspcoop2.pdd.services.consegnaContenutiApplicativi.contentEncoding.decompress*: risposta ricevuta dalle erogazioni.

Quando la decompressione automatica è attiva, GovWay:

- decodifica il body in chiaro così che le logiche a valle lavorino sul payload originale;

- rimuove gli header *Content-Encoding* e *Content-Length* dalla request/response, poiché si riferiscono al payload codificato e non sono più consistenti con il body in chiaro che viene poi propagato (per il body in transito sul wire viene utilizzato il *Transfer-Encoding: chunked*, vedi anche :ref:`contentLengthRisposta`);

- emette un diagnostico che evidenzia l'avvenuta decompressione e il valore originale dell'header *Content-Encoding* (es. *"... ricevuto con 'Content-Encoding: gzip' (size: 1432 bytes); applicata decompressione automatica"*).

.. note::
   La dimensione massima dei messaggi configurata tramite le policy di :ref:`rateLimiting` viene verificata sui byte decompressi, a protezione da contenuti che si espandono in modo anomalo (zip bomb). Poiché la risposta viene consegnata al chiamante in streaming, il superamento del limite ne interrompe la consegna e la transazione termina con esito di violazione della policy.

Gli schemi di compressione attualmente gestiti dalla decompressione automatica sono *gzip*, *x-gzip* (alias storico di gzip) e *deflate* (con autodetect tra le varianti `RFC 1950 <https://www.rfc-editor.org/rfc/rfc1950>`_ "zlib-wrapped" e `RFC 1951 <https://www.rfc-editor.org/rfc/rfc1951>`_ "raw"), allineati al default di Apache HttpClient 5.

.. note::
   La gestione dei *Content-Encoding* non supportati (es. *br*, *zstd*, *compress*) avviene esclusivamente quando l'opzione di decompressione automatica è abilitata. In tal caso GovWay rifiuta esplicitamente il messaggio: viene emesso un diagnostico (es. *"... con 'Content-Encoding: br' non gestibile dalla decompressione automatica (encoding supportati: gzip, x-gzip, deflate)"*) e la chiamata fallisce con un errore di processamento, evitando di propagare a valle un body in chiaro con header *Content-Encoding* "stale" che ne falsi l'integrità. Quando invece l'opzione è disabilitata (default), il body transita opaco a prescindere dal valore di *Content-Encoding*, in coerenza con il comportamento di puro passthrough.

.. note::
   Quando la decompressione automatica è attiva, sia il dump binario che il tracciamento *FileTrace* registrano il body **già decompresso**. Gli header *Content-Encoding* e *Content-Length* originali restano comunque visibili nel dump della sezione header (richiesta/risposta in ingresso), così da consentire l'analisi del wire originale dal punto di vista della negoziazione HTTP, mentre risultano assenti nella sezione header in uscita verso il chiamante a valle.

.. _contentEncodingSenzaDecompressione:

Contenuti compressi senza decompressione automatica
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Con la decompressione automatica disabilitata (default) un contenuto compresso viene inoltrato sempre così come ricevuto: i byte e l'header *Content-Encoding* non vengono modificati.

Le funzionalità che richiedono di interpretare il payload non possono operare su un contenuto compresso e lo segnalano esplicitamente, indicando la proprietà con cui abilitarne la decompressione, ad esempio:

   *The response message content is not accessible because it is compressed (Content-Encoding: gzip): to use features that require access to the content enable decompression of the response (property 'connettori.contentEncoding.response.decompress')*

In particolare:

- la validazione dei contenuti di messaggi JSON, XML e multipart fallisce con l'errore sopra indicato, gestito come un normale esito di validazione (rispettando quindi l'eventuale modalità 'warning only'); i messaggi binari vengono invece validati sui byte ricevuti;

- le trasformazioni che sostituiscono il contenuto producono un nuovo payload in chiaro: in tal caso l'header *Content-Encoding* ereditato dal messaggio originale viene rimosso (anche con valore *identity*), poiché non si riferisce più al contenuto inoltrato;

- la registrazione dei messaggi con l'analisi del payload abilitata memorizza il contenuto compresso così come ricevuto, senza analizzarlo, segnalandolo con un diagnostico (es. *"... è compresso (Content-Encoding: gzip) e viene registrato così come ricevuto, senza l'analisi del payload ..."*);

- la memorizzazione in cache delle risposte conserva anche l'header *Content-Encoding*, in modo che la risposta servita dalla cache sia identica a quella ricevuta;

- un messaggio SOAP compresso viene sempre rifiutato, poiché GovWay deve comunque interpretarne l'envelope: per le API SOAP che ricevono contenuti compressi è necessario abilitare la decompressione automatica.

.. note::
   Un contenuto il cui Content-Type indica un formato compresso (es. *application/gzip*), senza l'header *Content-Encoding*, non è considerato compresso: viene gestito come un normale contenuto binario.

.. _contentEncodingProblemCompressi:

Problem Details e fault compressi
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Anche senza la decompressione automatica, GovWay interpreta un Problem Details (:ref:`rfc7807`) o un fault ricevuto compresso, poiché le relative informazioni sono utilizzate dalle regole della gestione della consegna (es. nelle notifiche), dai diagnostici e dal fault registrato nella transazione. L'interpretazione avviene su una copia decompressa, lasciando invariata la risposta inoltrata al chiamante.

La copia decompressa non può superare la dimensione indicata dalla proprietà *org.openspcoop2.pdd.contentEncoding.fault.decompress.maxSizeKb* del file *govway_local.properties* (default 1024 KB), a protezione da contenuti che si espandono in modo anomalo. Quando il Problem Details non può essere interpretato, perché compresso con un encoding non supportato, non decomprimibile oppure oltre la soglia, le regole basate sul problem non vengono applicate e un diagnostico ne indica il motivo, ad esempio:

   *Problem Detail (RFC 7807) ricevuto con 'Content-Encoding: br' non interpretato: encoding non supportato (encoding supportati: gzip, x-gzip, deflate)*

.. _contentEncodingDigest:

Integrità del payload (Digest) e decompressione
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

L'header *Digest* (`RFC 3230 <https://www.rfc-editor.org/rfc/rfc3230>`_), utilizzato ad esempio dai pattern di integrità REST del profilo ModI, si riferisce ai byte trasmessi, cioè al contenuto compresso:

- quando chi genera il Digest (la fruizione per la richiesta, l'erogazione per la risposta) opera in modalità passthrough, cioè senza decompressione automatica, il contenuto viene inoltrato così come ricevuto e il Digest viene calcolato sui byte compressi;

- quando chi verifica il Digest (l'erogazione per la richiesta, la fruizione per la risposta) ha la decompressione automatica abilitata, il Digest viene verificato sui byte ricevuti, calcolandolo durante la decompressione, e non sul contenuto decompresso; allo stesso modo, se l'header *Content-Encoding* è presente tra gli header firmati, viene verificato il valore ricevuto anche se l'header è stato rimosso dalla decompressione.

Se invece chi genera il Digest non opera in passthrough, avendo la decompressione automatica abilitata (es. sulla richiesta ricevuta dalla fruizione), il contenuto viene inoltrato in chiaro e il Digest viene calcolato sul contenuto decompresso.
