Nuova funzionalità di Consegna Condizionale
--------------------------------------------------------------

Per le erogazioni di API è possibile definire connettori diversi, selezionati dinamicamente al verificarsi di specifiche condizioni.

Il connettore da utilizzare può essere selezionato in base al nome o a
un filtro associato al connettore stesso. Il nome o il valore del
filtro può essere estratto dalla richiesta attraverso una delle
seguenti modalità:

- *Header HTTP*: nome di un header http;

- *Url di Invocazione*: espressione regolare applicata sulla url di invocazione;

- *Parametro della Url*: nome del parametro presente nella url di invocazione;

- *SOAPAction*: individua una operazione SOAP;

- *Contenuto*: espressione (xPath o jsonPath) utilizzata per estrarre un identificativo dal body della richiesta;

- *Client IP*: indirizzo IP del client;

- *X-Forwared-For*: header http appartenente alla classe di header 'Forwarded-For';

- *Template*: l'identificativo di sessione è il risultato dell'istanziazione del template fornito rispetto ai dati della richiesta;

- *Freemarker Template*: l'identificativo di sessione è ottenuto tramite il processamento di un Freemarker Template;

- *Velocity Template*: l'identificativo di sessione è ottenuto tramite il processamento di un Velocity Template;

E' infine possibile configurare l'erogazione per utilizzare uno
specifico connettore di default nel caso la condizione non permetta di
identificare alcun connettore all'interno del pool, o in alternativa
per restituire un errore.

