Miglioramenti alla funzionalità di Trasformazione dei Messaggi
--------------------------------------------------------------

Sono state introdotte le seguenti nuove funzionalità nella trasformazione dei contenuti tramite i template engine 'Freemarker' e 'Velocity':

- *Archivio di Template*: è adesso possibile caricare, oltre al
  singolo file che individua il nuovo payload, anche un archivio zip
  contenente più template collegati tra loro tramite un file indice
  (index.ftl o index.vm).

- *ErrorHandler*: è possibile utilizzare un oggetto 'errorHandler' che
  consente di generare una risposta immediata in funzione dei dati
  della richiesta, utile, ad esempio, nei casi in cui il template
  richiede dei dati prelevati dalla richiesta (dagli header http, dal
  messaggio, dalla url ...) e tali dati non sono disponibili.

Sono stati introdotti nuovi tipi di trasformazione (ZIP, TGZ o TAR) per
supportare la trasformazione di richieste e risposte in archivi
compressi.

Sono state inoltre aggiunte nuove risorse accessibili dai template:

- *TransportContext*: è ora possibile accedere al
  contesto http della richiesta. Questa nuova risorsa permette ad
  esempio di poter ottenere l'informazione sull'identità ('principal')
  del richiedente.
  
- *Token Info*: permette di accedere ai claims di un token che ha
  superato la validazione effettuata durante il processo di
  autorizzazione.
  
- *Request / Response*: consente di accedere ai contenuti (payload o
  attachment) del messaggio di richiesta o di risposta.

Infine è stata aggiunta la possibilità di sospendere una regola di trasformazione.
