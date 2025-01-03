.. _tokenClaims:

Autorizzazione per Token Claims
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Se è stata abilitata la gestione del token si ha la possibilità di autorizzare le richieste inserendo i valori ammessi per i claims contenuti nel token. 

L'autorizzazione per token claims permette di effettuare dei semplici controlli sui valori dei claim presenti nel token, una volta verificato che il token sia valido. La funzionalità è utilizzabile nei contesti in cui il controllo di autorizzazione possiede una logica semplice che si basa sulla verifica del valore di uno o più claim. Dove serve una logica più complessa (ad es. con rami 'if-else' ) il controllo deve essere effettuato utilizzando una XACMLPolicy (:ref:`xacml`).

La configurazione viene effettuata inserendo nel campo di testo un claim da verificare per ogni riga, facendo seguire dopo l'uguale un valore fornito in una delle seguenti modalità:

- ${anyValue} : indica qualsiasi valore non nullo.
- ${undefined} : la risorsa indicata non deve esistere o non deve essere valorizzata.
- ${regExpMatch:EXPR} : la regola è soddisfatta se l'intero valore del claim ha un match rispetto all'espressione regolare EXPR indicata. È possibile utilizzare anche la versione ${regExpNotMatch:EXPR} che consente di attuare una negazione della condizione.
- ${regExpFind:EXPR} : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore del claim. Come per la precedente esiste anche la versione ${regExpNotFind:EXPR}.
- valore : indica esattamente il valore (case sensitive) che deve possedere il claim; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito.
- valore1,..,valoreN : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche.
- ${ignoreCase:valore} o ${ignoreCase:valore1,...,valoreN} : simile alle precedenti regole consente di attuare una verifica case insensitive.
- ${not:valore} o ${not:valore1,...,valoreN} : simile alle precedenti regole consente di indicare esattamente i valori (case sensitive) che non deve possedere la risorsa. È possibile utilizzarla anche in combinazione con il controllo case-insensitive: ${not:${ignoreCase:valore}} o ${not:${ignoreCase:valore1,...,valoreN}}

Le espressioni utilizzabili come parti dinamiche, risolte a runtime dal gateway, sono:

- ${header:NAME}: valore presente nell'header http che possiede il nome 'NAME'
- ${query:NAME}: valore associato al parametro della url con nome 'NAME'
- ${urlRegExp:EXPR}: espressione regolare applicata sulla url di invocazione (l'espressione deve avere un match con l'intera url)
- ${xPath:EXPR}: espressione XPath applicata su un messaggio XML
- ${jsonPath:EXPR}: espressione JSONPath applicata su un messaggio JSON
- ${tokenClient:FIELD}: identità dell'applicativo client identificato tramite il clientId presente nel token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.core.id.IDServizioApplicativo' (es. per ottenere il nome dell'applicativo usare ${tokenClient:nome})
- ${transportContext:FIELD}: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext' (es. per il principal usare ${transportContext:credential.principal})
- ${busta:FIELD}: permette di utilizzare informazioni generiche del profilo; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.protocol.sdk.Busta' (es. per il mittente usare ${busta:mittente})
- ${property:NAME}: utilizzabile solamente su erogazioni, permette di riferire informazioni specifiche del profilo presenti nella traccia (es. identificativo SDI). Il valore 'NAME' indica il nome della proprietà da utilizzare
- ${securityToken:FIELD}: permette di accedere ai certificati e ai security token presenti nella richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.protocol.sdk.SecurityToken' (es. per accedere al CN del certificato presente nel token 'Authorization' usare ${securityToken:authorization.certificate.subject.info(CN)})
- ${config:NAME}: valore della proprietà configurata sull'API che possiede il nome 'NAME'
- ${clientApplicationConfig:NAME}: valore della proprietà configurata nell'applicativo fruitore che possiede il nome 'NAME'
- ${clientOrganizationConfig:NAME}: valore della proprietà configurata nel soggetto fruitore che possiede il nome 'NAME'
- ${providerOrganizationConfig:NAME}: valore della proprietà configurata nel soggetto erogatore che possiede il nome 'NAME'
- ${tokenClientApplicationConfig:NAME}: permette di accedere alla proprietà, configurata nell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'
- ${tokenClientOrganizationConfig:NAME}: permette di accedere alla proprietà, configurata nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'
- ${dynamicConfig:FIELD}: permette di accedere alle proprietà degli attori coinvolti nella richiesta (api, applicativi, soggetti); il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.DynamicConfig'; maggiori informazioni sulla funzionalità sono disponibili nella sezione ':ref:`avanzate_dynamic_config`'.
- ${system:NAME}: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'
- ${env:NAME}: valore associato alla variabile di sistema con nome 'NAME'
- ${java:NAME}: valore associato alla variabile java con nome 'NAME'
- ${envj:NAME}: valore associato alla variabile di sistema o java con nome 'NAME'; la variabile viene cercata prima come variabile di sistema e se non presente come variabile della jvm

Di seguito alcuni esempi:

- client_id=3 : viene verificato che il claim 'client_id' possieda il valore 3
- client_id=${not:3} : viene verificato che il claim 'client_id' non possieda il valore 3
- client_id=3,5,6 : viene verificato che il claim 'client_id' possieda il valore 3 o 5 o 6
- client_id=${not:3,5,6} : viene verificato che il claim 'client_id' non possieda nessuno dei valori indicati: 3, 5 e 6
- username=${ignoreCase:paolo rossi} : la verifica sul valore del claim 'username' avviene con un criterio case insensitive. Nell'esempio i valori 'Paolo Rossi', 'paolo rossi', 'PAOLO ROSSI' hanno tutti un match.
- username=${not${ignoreCase:paolo rossi,marco verdi}} :  viene verificato che il claim 'username' non possieda nessuno dei valori indicati. I valori vengon controllati con un criterio case insensitive.
- client_id=${anyValue} : viene verificato che il claim 'client_id' possieda un valore (not null e not empty)
- client_id=${regExpMatch:[0-9]} : viene verificato che il claim 'client_id' possieda esattamente una cifra decimale attraverso la verifica di un match con l'espressione regolare '[0-9]'
- client_id=${regExpNotMatch:[0-9]} : viene verificato che il claim 'client_id' non sia composto da una cifra decimale (l'espressione regolare '[0-9]' non deve essere soddisfatta)
- client_id=${regExpFind:[0-9]} : viene verificato che il claim 'client_id' contenga una cifra decimale attraverso l'espressione regolare '[0-9]'
- client_id=${regExpNotFind:[0-9]} : viene verificato che il claim 'client_id' non contenga una cifra decimale (l'espressione regolare '[0-9]' non deve essere soddisfatta)
- client_id=${header:X-Prova} : viene verificato che il claim 'client_id' possieda lo stesso valore presente nell'header http 'X-Prova' presente nella richiesta
- client_id=cl-${header:X-Prova} : viene verificato che il claim 'client_id' possieda il valore presente nell'header http 'X-Prova' arricchito del prefisso 'cl-'
- client_id=${query:prova} : viene verificato che il claim 'client_id' possieda lo stesso valore presente nel parametro 'prova' della url di invocazione
- client_id=${urlRegExp:EXPR} : viene verificato che il claim 'client_id' possieda lo stesso valore estratto dalla url di invocazione attraverso l'applicazione dell'espressione regolare EXPR
- client_id=${xPath:EXPR} : viene verificato che il claim 'client_id' possieda lo stesso valore estratto dalla richiesta xml tramite l'espressione XPath EXPR.
- client_id=${jsonPath:EXPR} : viene verificato che il claim 'client_id' possieda lo stesso valore estratto dalla richiesta json tramite l'espressione jsonPath EXPR.
- client_id=${transportContext:credential.certificateChain.certificate.subject.info(CN)}: viene verificato che il claim 'client_id' possieda lo stesso valore estratto dal 'CN' del certificato TLS client.

Per verificare un attributo indicarlo con il prefisso 'attribute.' nella forma 'attribute.nome=valore'. Di seguito alcuni esempi

- attribute.sesso=m : viene verificato che l'attributo 'sesso' possieda il valore m
- attribute.stato=3,5,6 : viene verificato che l'attributo 'stato' possieda il valore 3 o 5 o 6

Nel caso la configurazione relativa all':ref:`apiGwIdentificazioneAttributi` prevede più AA, la verifica di un attributo prelevato da un authority va indicato con i prefissi 'aa.' e 'attribute.' nella forma 'aa.nomeAuthority.attribute.nomeAttributo=valore'.

- aa.AA2.attribute.sesso=m : viene verificato che l'attributo 'sesso', prelevato tramite l'Attribute Authority 'AA2', possia il valore m
- aa.AA2.attribute.stato=3,5,6 : viene verificato che l'attributo 'stato', prelevato tramite l'Attribute Authority 'AA2', possia il valore 3 o 5 o 6
