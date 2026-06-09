Miglioramenti alla funzionalità dei Connettori
----------------------------------------------

- Su API SOAP è stata aggiunta una funzionalità che compensa la mancanza del parametro type nel Content-Type multipart/related (RFC 2387, 3.1) ricevuto da backend o client non conformi. La strategia è configurabile tramite property (reject, inferFromRequest, inferFromBody, forceSoap11, forceSoap12), sia a livello globale sia per singola API.

- Aggiunta la possibilità di impostare la versione del protocollo HTTP (negoziazione automatica, HTTP/1 o HTTP/2) usata nelle connessioni verso i servizi, configurabile sia a livello globale sia per singola API. Utile negli ambienti in cui proxy o apparati di rete interferiscono con la negoziazione automatica di HTTP/2.

- Aggiunta funzionalità opzionale di decompressione automatica del body (gzip, x-gzip, deflate) lato richiesta e/o risposta, configurabile sia globalmente (per modulo di ingresso/uscita) sia per singola API. 

- Rivista la gestione dello streaming SSE (Server-Sent Events) per consegnare correttamente al client gli eventi anche quando la risposta del backend risulta compressa.

- Risolta un'anomalia per cui, con connettori HTTP/HTTPS in modalità asincrona NIO, il path della risorsa REST veniva omesso dalla URL invocata al backend.

