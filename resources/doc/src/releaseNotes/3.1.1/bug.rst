Bug Fix
-------

Sono stati risolti i seguenti bug:

- *Negoziazione Token sul Conettore*: nelle token policy di tipo
  "Negoziazione", in
  presenza di un 'Authorization Header' nella richiesta originale, se
  questa non veniva consumata dal modulo di autenticazione, veniva
  erroneamente sovrascritto il token ottenuto dalla negoziazione.

- *Dump Binario*: abilitando il debug sul connettore, la funzionalità di dump binario non registrava gli header gestiti dal connettore (Authorization, Content-Type, SOAPAction...).

- *Validazione dei Contenuti tramite OpenAPI 3*: sono stati risolti i seguenti problemi:

  - non venivano validati gli elementi presenti nella richiesta o
    nella risposta se definiti tramite '$ref';

  - la validazione dei parametri (header, query, path) non considerava
    eventuali restrizioni sul tipo (es. minLength, pattern ...).

- *Gestione Header HTTP case-insensitive*: gli heder non venivano gestiti completamente in maniera 'case-insensitive' come richiesto dalla specifica rfc7230#page-22. Venivano processati correttamente se dichiarati nella forma standard (es. Content-Type) o in una forma completamente minuscola o maiuscola (es. content-type). Non venivano invece riconosciuti se possedevano un nome che non rientrava nei casi precedenti (es. Content-type o Soapaction).

Sulla console di monitoraggio sono stati risolti i seguenti bug:

- *Summary 'Ultimo Anno'*: risolto problema presente nel report
  statistico relativo all'intervallo 'Ultimo anno' visualizzato dopo
  il login alla console. Il report visualizzava un intervallo
  temporale errato dove il mese corrente invece di essere utilizzato
  come ultimo mese, era proposto come primo e venivano poi forniti
  mesi 'futuri'.

- *Dump Binario*: la console non visualizzava il contenuto del dump binario se differente da xml.

- *Modifica Password*: la modifica della password dell'utente non funzionava.

