.. _tokenClaims:

Autorizzazione per Token Claims
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Se è stata abilitata la gestione del token si ha la possibilità di autorizzare le richieste inserendo i valori ammessi per i claims contenuti nel token. 

L'autorizzazione per token claims permette di effettuare dei semplici controlli sui valori dei claim presenti nel token, una volta verificato che il token sia valido. La funzionalità è utilizzabile nei contesti in cui il controllo di autorizzazione possiede una logica semplice che si basa sulla verifica del valore di uno o più claim. Dove serve una logica più complessa (ad es. con rami 'if-else' ) il controllo deve essere effettuato utilizzando una XACMLPolicy (:ref:`xacml`).

La configurazione viene effettuata inserendo nel campo di testo un claim da verificare per ogni riga, facendo seguire dopo l'uguale un valore fornito in una delle seguenti modalità:

- ${anyValue} : indica qualsiasi valore non nullo
- ${undefined} : la risorsa indicata non deve esistere o non deve essere valorizzata
- ${regExpMatch:EXPR} : la regola è soddisfatta se l'intero valore del claim ha un match rispetto all'espressione regolare EXPR indicata
- ${regExpFind:EXPR} : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore del claim
- valore : indica esattamente il valore (case sensitive) che deve possedere il claim; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito
- valore1,..,valoreN : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche

Le espressioni utilizzabili come parti dinamiche, risolte a runtime dal gateway, sono:

- ${header:NAME}: valore presente nell'header http che possiede il nome 'NAME'
- ${query:NAME}: valore associato al parametro della url con nome 'NAME'
- ${urlRegExp:EXPR}: espressione regolare applicata sulla url di invocazione (l'espressione deve avere un match con l'intera url)
- ${xPath:EXPR}: espressione XPath applicata su un messaggio XML
- ${jsonPath:EXPR}: espressione JSONPath applicata su un messaggio JSON
- ${transportContext:FIELD}: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext' (es. per il principal usare ${transportContext:credential.principal})
- ${config:NAME}: valore della proprietà configurata sull'API che possiede il nome 'NAME'
- ${clientApplicationConfig:NAME}: valore della proprietà configurata nell'applicativo fruitore che possiede il nome 'NAME'
- ${clientOrganizationConfig:NAME}: valore della proprietà configurata nel soggetto fruitore che possiede il nome 'NAME'
- ${providerOrganizationConfig:NAME}: valore della proprietà configurata nel soggetto erogatore che possiede il nome 'NAME'
- ${system:NAME}: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'
- ${env:NAME}: valore associato alla variabile di sistema con nome 'NAME'
- ${java:NAME}: valore associato alla variabile java con nome 'NAME'

Di seguito alcuni esempi:

- client_id=3 : viene verificato che il claim 'client_id' possieda il valore 3
- client_id=3,5,6 : viene verificato che il claim 'client_id' possieda il valore 3 o 5 o 6
- client_id=${anyValue} : viene verificato che il claim 'client_id' possieda un valore (not null e not empty)
- client_id=${regExpMatch:[0-9]} : viene verificato che il claim 'client_id' possieda esattamente una cifra decimale attraverso la verifica di un match con l'espressione regolare '[0-9]'
- client_id=${regExpFind:[0-9]} : viene verificato che il claim 'client_id' contenga una cifra decimale attraverso l'espressione regolare '[0-9]'
- client_id=${header:X-Prova} : viene verificato che il claim 'client_id' possieda lo stesso valore presente nell'header http 'X-Prova' presente nella richiesta
- client_id=cl-${header:X-Prova} : viene verificato che il claim 'client_id' possieda il valore presente nell'header http 'X-Prova' arricchito del prefisso 'cl-'
- client_id=${query:prova} : viene verificato che il claim 'client_id' possieda lo stesso valore presente nel parametro 'prova' della url di invocazione
- client_id=${urlRegExp:EXPR} : viene verificato che il claim 'client_id' possieda lo stesso valore estratto dalla url di invocazione attraverso l'applicazione dell'espressione regolare EXPR
- client_id=${xPath:EXPR} : viene verificato che il claim 'client_id' possieda lo stesso valore estratto dalla richiesta xml tramite l'espressione XPath EXPR.
- client_id=${jsonPath:EXPR} : viene verificato che il claim 'client_id' possieda lo stesso valore estratto dalla richiesta json tramite l'espressione jsonPath EXPR.

Per verificare un attributo indicarlo con il prefisso 'attribute.' nella forma 'attribute.nome=valore'. Di seguito alcuni esempi

- attribute.sesso=m : viene verificato che l'attributo 'sesso' possieda il valore m
- attribute.stato=3,5,6 : viene verificato che l'attributo 'stato' possieda il valore 3 o 5 o 6

Nel caso la configurazione relativa all':ref:`apiGwIdentificazioneAttributi` prevede più AA, la verifica di un attributo prelevato da un authority va indicato con i prefissi 'aa.' e 'attribute.' nella forma 'aa.nomeAuthority.attribute.nomeAttributo=valore'.

- aa.AA2.attribute.sesso=m : viene verificato che l'attributo 'sesso', prelevato tramite l'Attribute Authority 'AA2', possia il valore m
- aa.AA2.attribute.stato=3,5,6 : viene verificato che l'attributo 'stato', prelevato tramite l'Attribute Authority 'AA2', possia il valore 3 o 5 o 6
