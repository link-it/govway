.. _contentLengthRisposta:

Content-Length nella risposta
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Per default GovWay utilizza il *Transfer-Encoding: chunked* per inoltrare la risposta al client, come previsto dalla specifica *HTTP/1.1* (`RFC 9112, Section 7.1 <https://www.rfc-editor.org/rfc/rfc9112#section-7.1>`_). In questo modo il gateway non necessita di conoscere la dimensione complessiva del contenuto prima di iniziare a trasmetterlo.

In alcuni scenari può essere necessario che la risposta contenga l'header *Content-Length* al posto del *Transfer-Encoding: chunked*. Ad esempio, le API che supportano il recupero parziale delle risorse tramite *Range Requests* (`RFC 9110, Section 14 <https://www.rfc-editor.org/rfc/rfc9110#section-14>`_) richiedono la presenza dell'header *Content-Length* per consentire al client di determinare la dimensione complessiva della risorsa e gestire correttamente le risposte parziali (status *206 Partial Content*).

È possibile personalizzare il comportamento di GovWay registrando le seguenti :ref:`configProprieta` sulla singola erogazione o fruizione. Tutte le proprietà sono disabilitate per default e i valori associabili sono 'true' o 'false'.

- *connettori.http.contentLength.preserve*: se il backend ha restituito un header *Content-Length*, GovWay lo preserva nella risposta inoltrata al client, a condizione che il contenuto non sia stato modificato da trasformazioni o altre funzionalità del gateway. Se il contenuto è stato modificato, il valore viene ricalcolato automaticamente.

- *connettori.http.contentLength.preserve.recalculate*: come la precedente, ma il valore del *Content-Length* viene sempre ricalcolato automaticamente, indipendentemente dal fatto che il contenuto sia stato modificato o meno.

- *connettori.http.contentLength.force*: GovWay aggiunge sempre l'header *Content-Length* nella risposta, indipendentemente dal fatto che il backend lo abbia restituito o meno. Se il contenuto non è stato modificato e il backend ha fornito un *Content-Length*, viene preservato il valore originale. In caso contrario, il valore viene calcolato automaticamente.

- *connettori.http.contentLength.force.recalculate*: come la precedente, ma il valore del *Content-Length* viene sempre ricalcolato automaticamente.

.. note::
   Le proprietà *preserve* hanno effetto solamente se il backend ha restituito un header *Content-Length* nella risposta, mentre le proprietà *force* agiscono sempre. La variante *recalculate* comporta un costo aggiuntivo in termini di prestazioni poiché richiede il ricalcolo della dimensione effettiva del contenuto.
