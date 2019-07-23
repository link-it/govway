Bug Fix
-----------------------------------------------------

Sono stati risolti i seguenti bug:

-  *Negoziazione Token sul Connettore*: la negoziazione del token sul connettore non risultava funzionante in presenza di 'Authorization Header' sulla richiesta originale, se questa non veniva consumata dal modulo di autenticazione. L'header http 'Authorization' originale sovrascriveva il token ottenuto dalla negoziazione.

-  *Dump Binario*: se veniva abilitato il debug sul connettore, la funzionalità di dump binario non registrava gli header gestiti dal connettore (Authorization, Content-Type, SOAPAction...).

-  *Validazione dei Contenuti tramite OpenAPI 3*: sono stati risolti i seguenti problemi:

   - non venivano validati gli elementi presenti nella richiesta o nella risposta se definiti tramite '$ref'.
   - la validazione dei parametri (header, query, path) non considerava eventuali restrizioni sul tipo (es. minLength, pattern ...)

Sulla console di monitoraggio sono stati risolti i seguenti bug:

-  *Summary 'Ultimo Anno'*: risolto problema presente nel report statistico relativo all'intervallo 'Ultimo anno' visualizzato dopo il login alla console. Il report visualizzava un intervallo temporale errato dove il mese corrente invece di essere utilizzato come ultimo mese, era proposto come primo e venivano poi forniti mesi 'futuri'.

-  *Dump Binario*: la console non visualizzava il contenuto del dump binario se differente da xml.


