Miglioramenti alla funzionalità di Trasformazione dei Messaggi
--------------------------------------------------------------

Sono state introdotte le seguenti nuove funzionalità nella trasformazione dei contenuti tramite i template engine 'Freemarker' e 'Velocity':

- *Archivio di Template*:   è adesso possibile caricare, oltre al singolo file che definisce il nuovo payload, anche un archivio zip contenente più files template collegati tra loro tramite un file indice (index.ftl o index.vm). Questa nuova modalità consente di strutturare una trasformazione complessa in più file, alcuni dei quali possono contenere librerie poi importate da altri file.
- *ErrorHandler*: è adesso possibile utilizzare un oggetto 'errorHandler' che consente di generare una risposta immediata personalizzata in funzione dei dati della richiesta. Tale funzionalità è molto utile, ad esempio, nei contesti in cui il template richiede dei dati prelevati dalla richiesta (dagli header http, dal messaggio, dalla url ...) e tali dati non sono disponibili.

I tipi di trasformazione del contenuto sono stati estesi per supportare la trasformazione delle richieste e/o risposte in archivi compressi. I tipi di compressione supportati sono ZIP, TGZ o TAR.	Il contenuto degli archivi è definibile tramite un file che deve contenere proprietà indicate come nome=valore in ogni linea. Il nome della proprietà corrisponde all'entry name all'interno dell'archivio (es. dir/subDir/name.txt). Il valore della proprietà definisce il contenuto dell'entry. È possibile selezionare specifici header http, parametri delle url, il payload o porzioni di esso, il contenuto di uno specifico attachment e altre informazioni utilizzando le espressioni dinamiche risolte a runtime dal Gateway già presenti negli altri tipi di trasformazioni.

Sono stati inoltre aggiunte nuove risorse accessibili dai template:

- *TransportContext*: è stato aggiunto la possibilità di accedere al contesto http della richiesta. Questa nuova risorsa permette ad esempio di poter ottenere l'informazione sull'identità ('principal') del richiedente.
- *Token Info*: permette di accedere ai claims di un token che ha superato la validazione effettuata durante il processo di autorizzazione.
- *Request / Response*: consente di accedere ai contenuti (payload o attachment) del messaggio di richiesta o di risposta.

Infine è stata aggiunta la possibilità di sospendere una regola di trasformazione.

 
