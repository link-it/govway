.. _avanzate_connettori_contenutoRichiesta:

Contenuto della Richiesta
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il contenuto di una richiesta viene inoltrato al backend in funzione del metodo HTTP, in conformità con la specifica 'RFC 9110 - HTTP Semantics' e con la specifica OpenAPI 3.0, secondo la quale il campo 'requestBody' di un'operazione è supportato solamente per i metodi HTTP che ne definiscono esplicitamente una semantica e deve essere ignorato negli altri casi:

- *POST, PUT, PATCH, LINK, UNLINK*: il contenuto viene inoltrato;
- *OPTIONS*: il contenuto viene inoltrato se la richiesta possiede l'header 'Content-Type' (RFC 9110 - sezione 9.3.7);
- *GET, HEAD, DELETE*: il contenuto non viene inoltrato poiché per questi metodi la specifica non ne definisce una semantica (RFC 9110 - sezioni 9.3.1, 9.3.2 e 9.3.5);
- *TRACE*: il contenuto non viene mai inoltrato (RFC 9110 - sezione 9.3.8).

Se una richiesta presenta un contenuto con un metodo HTTP per il quale non è previsto l'inoltro (GET, HEAD, DELETE o TRACE), il contenuto viene scartato insieme all'header 'Content-Type': il backend riceve la richiesta priva di entrambi e viene registrato un diagnostico, classificato come warning, che riporta il motivo per cui il contenuto non è stato inoltrato; la transazione assume l'esito 'OK con anomalie'. Una richiesta che indica l'header 'Content-Type' senza alcun contenuto viene invece inoltrata senza l'header e senza la registrazione del diagnostico.

È possibile abilitare l'inoltro del contenuto anche per i metodi GET, HEAD e DELETE, su una singola erogazione o fruizione di API, attraverso la definizione della seguente :ref:`configProprieta`:

- *connettori.request.forceSendContent.enabled* (true/false default:false): se abilitata, il contenuto della richiesta viene inoltrato, se presente insieme all'header 'Content-Type', anche per i metodi GET, HEAD e DELETE. La proprietà non ha effetto sul metodo TRACE.

.. note::
     La libreria client 'java.net.HttpURLConnection', selezionabile nella configurazione http avanzata del connettore (sezione :ref:`avanzate_connettori_httpOpzioniAvanzate`), non consente l'invio di un contenuto con il metodo GET, poiché trasformerebbe la richiesta in una POST: abilitando la proprietà, una richiesta GET con contenuto termina con un errore di invocazione del servizio che ne riporta il motivo. I metodi HEAD e DELETE sono invece supportati, così come tutti e tre i metodi con la libreria di default 'org.apache.hc.client5'.
