Nuova funzionalità di Load Balancer
--------------------------------------------------------------

Per le erogazioni di API è possibile definire connettori multipli con finalità di bilanciamento delle richieste in arrivo.
Vengono forniti differenti tipi di bilanciamento del carico:

- *Round Robin*: le richieste vengono distribuite in ordine tra i connettori registrati;

- *Weight Round Robin*: rispetto al Round Robin consente di riequilibrare eventuali server eterogenei tramite una distribuzione bilanciata rispetto al peso associato ad ogni connettore;

- *Random*: le richieste vengono distribuite casualmente tra i connettori registrati;

- *Weight Random*: rispetto al Random si ha una distribuzione casuale che considerà però il peso associato ad ogni connettore;

- *Source IP hash*: combina l'indirizzo IP del client e l'eventuale indirizzo IP portato nell'header 'Forwarded-For' per generare una chiave hash che viene designata per un connettore specifico;

- *Least Connections*: la richiesta viene indirizzata verso il connettore che ha il numero minimo di connessioni attive.

La configurazione permette anche di abilitare una sessione sticky in
modo che tutte le richieste che presentano lo stesso id di sessione
vengano servite tramite lo stesso connettore.  Se l'identificativo di
sessione si riferisce ad una nuova sessione, viene selezionato un
connettore rispetto alla strategia indicata. L'identificativo di
sessione utilizzato è individuabile tramite una delle seguenti
modalità:

- *Cookie*: nome di un cookie;

- *Header HTTP*: nome di un header http;

- *Url di Invocazione*: espressione regolare applicata sulla url di invocazione;

- *Parametro della Url*: nome del parametro presente nella url di invocazione;

- *Contenuto*: espressione (xPath o jsonPath) utilizzata per estrarre un identificativo dal body della richiesta;

- *Client IP*: indirizzo IP del client;

- *X-Forwared-For*: header http appartenente alla classe di header 'Forwarded-For';

- *Template*: l'identificativo di sessione è il risultato dell'istanziazione del template fornito rispetto ai dati della richiesta;

- *Freemarker Template*: l'identificativo di sessione è ottenuto tramite il processamento di un Freemarker Template;

- *Velocity Template*: l'identificativo di sessione è ottenuto tramite il processamento di un Velocity Template;

È infine possibile attivare un 'Passive Health Check' che verifica la
connettività verso i connettori configurati. Un utilizzo di un
connettore che provoca un errore di connettività comporta la sua
esclusione dal pool dei connettori utilizzabili per un intervallo di
tempo configurabile.

