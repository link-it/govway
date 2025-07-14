.. _avanzate_canaleIO:

Gestione I/O (BIO/NIO)
--------------------------------

GovWay agisce da proxy, ricevendo richieste di servizio dai client e gestendole secondo i criteri di autorizzazione, validazione e tracciamento configurati. Una volta completate queste fasi, la richiesta viene inoltrata al sistema backend, che restituisce la risposta da inoltrare al client.

L'intera gestione dell’I/O — dalla ricezione della richiesta alla restituzione della risposta — può avvenire tramite due modalità alternative: bloccante (BIO) o non bloccante (NIO).

*Modalità bloccante (BIO)*

- Le richieste vengono ricevute tramite un servlet HTTP standard, gestito da un worker thread del web container. Questo thread rimane allocato fino al completamento della risposta verso il client.
- L’inoltro verso il backend è affidato ai :ref:`avanzate_connettori`, i quali, per richieste HTTP, sono implementati tramite la libreria `Apache HttpClient 5 <https://hc.apache.org/httpcomponents-client-5.5.x/index.html>`_, utilizzando la classe 'org.apache.hc.client5.http.classic.HttpClient'.
- Per ulteriori dettagli di configurazione, consultare la sezione: :ref:`avanzate_canaleIO_confBIO`.

*Modalità non bloccante (NIO)*

- Anche in questo caso, le richieste vengono ricevute tramite un servlet HTTP, ma la gestione tra richiesta e risposta è disaccoppiata mediante l’uso di jakarta.servlet.AsyncContext. Il thread del container viene liberato immediatamente e la continuazione della gestione viene delegata a thread applicativi provenienti da un pool dedicato. La risposta viene poi restituita al client riattivando l’AsyncContext.
- L’invio della richiesta al backend avviene sempre tramite i :ref:`avanzate_connettori` che per richieste HTTP sono implementati usando la libreria `Apache HttpClient 5 <https://hc.apache.org/httpcomponents-client-5.5.x/index.html>`_, nella versione asincrona non bloccante 'org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient'.
- Per ulteriori dettagli di configurazione, consultare la sezione: :ref:`avanzate_canaleIO_confNIO`.

*Selezione della modalità I/O tramite URL*

Ogni invocazione di un servizio (sia in modalità erogazione che fruizione) può essere effettuata scegliendo la modalità I/O desiderata (BIO o NIO) direttamente nell’URL di invocazione descritte nella sezione: :ref:`configGenerale_urlInvocazione_urlInterna`. Di seguito vengono riproposti gli esempi di URL già descritti in quella sezione, arricchiti con la specifica del canale I/O: BIO (sync) o NIO (async).

Modalità bloccante: 

- <prefix-erogazione>/\ <profilo>[/in]/**sync/**/\ <soggettoDominioInterno>/<nomeErogazione>/v<versioneErogazione>
- <prefix-fruizione>/\ <profilo>/out/**sync**/\ <soggettoDominioInterno>/<soggettoErogatore>/<nomeFruizione>/v<versioneFruizione>

Modalità non bloccante: 

- <prefix-erogazione>/\ <profilo>[/in]/**async/**/\ <soggettoDominioInterno>/<nomeErogazione>/v<versioneErogazione>
- <prefix-fruizione>/\ <profilo>/out/**async**/\ <soggettoDominioInterno>/<soggettoErogatore>/<nomeFruizione>/v<versioneFruizione>

La modalità predefinita è BIO: essa viene utilizzata in assenza dell'indicazione esplicita di 'sync' o 'async' all'interno dell'URL di invocazione. Tale comportamento è modificabile agendo sul file <directory-lavoro>/govway_local.properties aggiungendo le seguenti righe:

   ::

      # Modalità di default per le fruizioni (BIO/NIO)
      org.openspcoop2.pdd.channel.pd.default=NIO
      # Modalità di default per le erogazioni (BIO/NIO)
      org.openspcoop2.pdd.channel.pa.default=NIO

.. toctree::
        :maxdepth: 2
        
        confBIO
        confNIO
