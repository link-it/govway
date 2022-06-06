Miglioramenti alla funzionalità di Gestione dei Token
-----------------------------------------------------

Migliorata la gestione degli access token negoziati con gli Authorization Server:

- un access token, precedentemente negoziato e disponibile in cache, viene adesso rinegoziato prima della scadenza effettiva (indicata in expireIn) per evitare che possa risultare scaduto una volta ricevuto dall'erogatore, quando utilizzato per richieste gestite in prossimità della scadenza;

- è adesso possibile configurare il criterio di generazione del claim 'jti' inserito nell'asserzione JWT generata nella modalità 'JWT Signed';

- i criteri di negoziazione (asserzione JWT, url, clientId) e l'access token ottenuto sono adesso consultabili tramite la console di monitoraggio, accedendo ai dati della transazione (Token Info).

Sono inoltre stati risolti i seguenti problemi:

- risolto problema di performance in cui l'accesso sincronizzato all'interno della gestione della cache per i token avveniva erroneamente per ogni richiesta anche se il token era già presente in cache;

- i token ottenuti venivano salvati in cache tramite una chiave formata dal solo nome della Token Policy di negoziazione, ignorando gli eventuali parametri dinamici risolti a runtime. In tale situazione, se una policy con parametri dinamici veniva utilizzata su più fruizioni, poteva succedere che il token negoziato in seguito all'invocazione di una prima fruizione, venisse erroneamente riutilizzato (disponibile in cache) per l'invocazione di una seconda fruizione. Scenario tipico in presenza di PDND dove il purposeId è rappresentato da un parametro dinamico ad esempio indicato dal client tramite un header HTTP. 

Infine nelle policy di validazione dei token è adesso possibile indicare come formato dell'access token anche la struttura definita nel RFC 9068.
