Miglioramenti al Profilo di Interoperabilità 'ModI PA'
------------------------------------------------------

Adeguato il profilo 'ModI PA' alla versione finale delle Linee Guida di Interoperabilità rilasciate in data 15/09/2020: https://trasparenza.agid.gov.it/archivio19_regolamenti_0_5386.html

- *Nomenclatura*: le maschere di configurazione sono state allineate alla nuova terminologia riguardante i pattern di interazione e sicurezza.

- *Interazione CRUD*: alle risorse di una API REST viene adesso per default assegnato il pattern di interazione 'CRUD_REST'.

- *Interazione Bloccante e Non Bloccante su API REST*: i profili bloccanti e non bloccanti sono adesso assegnabili alle risorse solamente se compatibili con i metodi HTTP e i codici di risposta richiesti dalla specifica.

- *Firma del payload su API REST*: sostituito l'header 'Authorization' con il nuovo header 'Agid-JWT-Signature' per il pattern di sicurezza 'INTEGRITY_REST_01'.

- *Rate Limiting*: aggiunta gestione 'window' come descritto in 'https://datatracker.ietf.org/doc/draft-polli-ratelimit-headers/'. La funzionalità è attivabile tramite la proprietà 'org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.limit.windows=true' da registrare nel file govway_local.properties

- *X5U per Applicativo*: la url indicata nel claim 'x5u' di un token di sicurezza per API REST deve adesso essere registrata sull'applicativo e non più sulla fruizione in modo da consentire url differenti per applicativi differenti.

- *Certificate Chain per API REST*: aggiunta la possibilità di inviare l'intera catena dei certificati anche per API REST all'interno del claim 'x5c'.

- *Sicurezza Messaggio su Richiesta/Risposta*: è adesso possibile attivare la sicurezza messaggio puntualmente solamente sulla richiesta o sulla risposta di una operazione. Per API REST è possibile anche definire dei criteri di applicabilità della sicurezza messaggio in base a Content-Type o codici di risposta HTTP.






