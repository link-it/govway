.. _apiGwAutorizzazioneContenuti:

Autorizzazione Contenuti
^^^^^^^^^^^^^^^^^^^^^^^^

L'autorizzazione dei contenuti è un ulteriore meccanismo per il controllo degli
accessi tramite il quale è possibile specificare regole di autorizzazione che verificano aspetti della richiesta quali ad esempio gli header http, l'url di invocazione, parti del messaggio etc.

Una volta abilitata l'autorizzazione per contenuto si possono configuare una serie di controlli di autorizzazione nella forma (risorsa=valore).

Una risorsa identifica un header, una parte dell'url o del messaggio, un claim del token o un principal etc.
Per identificare una risorsa sono utilizzabili le seguenti espressioni dinamiche:

- ${header:NAME}: valore presente nell'header http che possiede il nome 'NAME'
- ${query:NAME}: valore associato al parametro della url con nome 'NAME'
- ${urlRegExp:EXPR}: espressione regolare applicata sulla url di invocazione
- ${xPath:EXPR}: espressione XPath applicata su un messaggio XML
- ${jsonPath:EXPR}: espressione JSONPath applicata su un messaggio JSON
- ${tokenInfo:FIELD}: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.token.InformazioniToken' (es. per ottenere il valore del claim 'sub' usare ${tokenInfo:sub})
- ${aa:FIELD} : permette di accedere agli attributi recuperati tramite Attribute Authority; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi' (es. per ottenere il valore dell'attributo 'attr1' usare ${aa:attributes[attr1]}, se configurata solamente 1 A.A., altrimenti usare ${aa:attributes[nomeAttributeAuthority][attr1]} )
- ${transportContext:FIELD}: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext' (es. per il principal usare ${transportContext:credential.principal})
- ${config:NAME}: valore della proprietà configurata sull'API che possiede il nome 'NAME'
- ${clientApplicationConfig:NAME}: valore della proprietà configurata nell'applicativo fruitore che possiede il nome 'NAME'
- ${clientOrganizationConfig:NAME}: valore della proprietà configurata nel soggetto fruitore che possiede il nome 'NAME'
- ${providerOrganizationConfig:NAME}: valore della proprietà configurata nel soggetto erogatore che possiede il nome 'NAME'
- ${system:NAME}: valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'
- ${env:NAME}: valore associato alla variabile di sistema con nome 'NAME'
- ${java:NAME}: valore associato alla variabile java con nome 'NAME'

Ogni valore atteso per una risorsa può essere fornito in una delle seguenti modalità:

- ${anyValue} : indica qualsiasi valore non nullo
- ${regExpMatch:EXPR} : la regola è soddisfatta se il valore della risorsa ha un match completo rispetto all'espressione regolare EXPR indicata
- ${regExpFind:EXPR} : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore della risorsa
- valore : indica esattamente il valore (case sensitive) che deve possedere la risorsa; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway nella forma descritta precedentemente per le risorse
- valore1,..,valoreN : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche

.. _controlloAccessiAutorizzazioneContenuti:

.. figure:: ../../_figure_console/AutorizzazioneContenuti.png
 :scale: 80%
 :align: center

 Configurazione Autorizzazione Contenuti

Di seguito alcuni esempi:

- ${header:X-Prova}=test : viene verificato che l'header 'X-Prova' possieda il valore 'test'
- ${header:X-Prova}=test,test2,test3 : viene verificato che l'header 'X-Prova' possieda il valore 'test' o 'test2' o 'test3'
- ${transportContext:credential.principal}=${header:X-SSO} : viene verificato che l'identità principal del chiamante corrisponda al valore fornito nell'header 'X-SSO'
- ${transportContext:credential.principal}=prefix${header:X-SSO}suffix : simile alla precedente regola, dove l'identità principal viene controntata con il valore presente nell'header concatenato da un prefisso e da un suffisso statico.
- ${xPath:EXPR}=${regExpMatch:[0-9]} : viene estratto il contenuto dalla richiesta xml tramite l'espressione XPath EXPR e verificato che sia corrispondente ad una cifra decimale attraverso l'espressione regolare '[0-9]'
- ${jsonPath:EXPR}=${transportContext:credential.principal} : viene estratto il contenuto dalla richiesta json tramite l'espressione jsonPath EXPR e verificato che sia uguale all'identità principal del chiamante
- ${context:CLIENT_IP_REMOTE_ADDRESS}=10.114.44.3,10.114.44.4,10.114.44.5 : viene verificato che l'indirizzo ip del client sia tra gli indirizzi ip elencati.
- ${context:CLIENT_IP_TRANSPORT_ADDRESS}=${regExpMatch:10\.114\.44\..*|10\.114\.43\..*} : viene verificato che l'indirizzo ip del client sia nella sottorete 10.114.44.0/255 o 10.114.43.0/255; l'indirizzo ip viene estratto dagli header http utilizzati per il mantenimento dell’IP di origine nel caso di nodi intermedi (es. X-Forwarded-For).

