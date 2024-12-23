Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

È stato introdotta la gestione del 'soggetto intermediario', che consente di autorizzare una richiesta proveniente da un soggetto identificato sul canale e da un applicativo appartenente a un soggetto differente, identificato tramite token di sicurezza.

Nell'occasione, è stato affinato il processo di autenticazione:

- Il processo di identificazione degli applicativi veniva inutilmente attivato per l'autenticazione MTLS delle erogazioni, anche se con tale profilo gli applicativi possono essere censiti solamente con credenziale di tipo 'token' o con certificato di firma; tale controllo è stato pertanto eliminato.

- I controlli di esistenza di un applicativo già registrato con lo stesso certificato sono stati affinati al fine di escludere gli applicativi con profilo di interoperabilità 'ModI' di un dominio esterno, poiché tali certificati non si riferiscono a credenziali TLS ma vengono utilizzati solo per firmare token di sicurezza.

Sono stati apportati i seguenti miglioramenti alla funzionalità di integrazione con la PDND:

- aggiunta, nelle politiche di Rate Limiting, la possibilità di conteggiare per nome dell'organizzazione ottenuta tramite le API di interoperabilità della PDND;

- aggiunta la possibilità di modificare sulla singola erogazione o fruizione il comportamento di default per far fallire la transazione nel caso in cui il recupero delle informazioni sul client o sull'organizzazione tramite API PDND fallisca;

- le informazioni sull'organizzazione, recuperate tramite le API PDND, vengono ora propagate al backend tramite gli header di integrazione: GovWay-Token-PDND-OrganizationName, GovWay-Token-PDND-OrganizationCategory e GovWay-Token-PDND-OrganizationExternal.

Sono infine stati apportati i seguenti miglioramenti:

- (https://github.com/link-it/govway/issues/161) aggiunta validazione dei campi contenenti codici crittografici come ad esempio il clientId o il KID relativo ai token pdnd;

- in una configurazione che prevedeva un access token PDND (ID_AUTH_REST_01), il claim 'jti' presente all'interno del token non veniva utilizzato come identificativo messaggio della richiesta, alla quale veniva invece associato un identificativo generato da GovWay. L'anomalia comportava:
   
   - un doppio tracciamento sia del claim 'jti' che dell'identificativo generato da GovWay;
   - una valorizzazione dell'header di integrazione 'GovWay-Message-ID' con l'identificativo generato da GovWay invece del jti.
