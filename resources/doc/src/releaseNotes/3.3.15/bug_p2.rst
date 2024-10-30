.. _3.3.15.2_bug:

Bug Fix 3.3.15.p2
------------------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2024-38821: aggiornate librerie 'org.springframework.security:\*' alla versione 5.8.15

- CVE-2024-38820: aggiornate librerie 'org.springframework:\*' alla versione 5.3.39-gov4j-1

- CVE-2024-47554: 

	- aggiornata libreria 'commons-io:commons-io' alla versione 2.15.1
	- aggiornata libreria 'org.apache.velocity:velocity-engine-core' alla versione 2.4

 CVE-2024-45772: aggiornate librerie 'org.apache.lucene:\*' alla versione 9.12.0.

Sono stati risolti i seguenti bug:

- Quando si definivano erogazioni o fruizioni con gruppi di configurazioni specifiche per operazioni, eventuali modifiche alla configurazione CORS venivano applicate solo alle operazioni del gruppo predefinito. Di conseguenza, per le operazioni degli altri gruppi, continuava ad essere utilizzata la configurazione CORS di default.

- La validazione dei contenuti, successiva alla verifica di una firma WSSecurity, falliva se l'elemento da validare conteneva elementi tipizzati tramite "xsi:type" e la dichiarazione dei namespace dei prefissi associati era presente nell'elemento Envelope della busta SOAP. L'errore riscontrato era simile al seguente: "UndeclaredPrefix: Cannot resolve 'test:EsempioType' as a QName: the prefix 'test' is not declared.".

- La validazione di un certificato di firma utilizzato in un header WSSecurity, inclusa l'analisi delle CRL, veniva eseguita su tutti i certificati della catena, compresi quelli intermedi, anche se era stato fornito solo il file CRL relativo al certificato finale. Di conseguenza, durante la validazione di un certificato intermedio, compariva l'errore: "No CRLs found for issuer 'cn=ExampleCA,ou=TEST,o=Example,c=IT'". Per risolvere il problema, era necessario fornire un file CRL per ogni certificato, inclusi quelli intermedi. Per evitare questa configurazione complessa e prevenire il fallimento della validazione quando viene fornito un solo file CRL, la configurazione predefinita ora presume che il file CRL sia relativo solo al certificato finale e non viene utilizzato per validare i certificati intermedi.

Inoltre sono state aggiunte utility:

- per la gestione dell'ora legale e solare, utilizzate nella funzionalità di riconsegna con presenza in carico dei messaggi.

Per la console di gestione sono stati risolti i seguenti bug:

- nella configurazione di un'API ModI con pattern INTEGRITY_REST, la scelta dell'header HTTP "Custom-JWT-Signature" comporta che la gestione dell'integrità non venga eseguita in modo integrato, ma sia demandata all'applicazione. A causa di questo comportamento, la maschera di configurazione non era del tutto intuitiva e poteva far pensare che si stesse solo modificando il nome dell'header HTTP, mentre in realtà cambiava anche la modalità di gestione dell'integrità. È stata quindi aggiunta una nota esplicativa per chiarire meglio il funzionamento.
