Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- *Contemporaneità degli Header 'Authorization' e 'Agid-JWT-Signature'*: su API REST, per quanto concerne la sicurezza messaggio, è stata aggiunta la possibilità di generare contemporaneamente gli header 'Authorization' e 'Agid-JWT-Signature' consentendo di agire sulle seguenti configurazioni:

	- è possibile indicare se la contemporaneità vale solo per la richiesta o in entrambi i flussi;

	- aspetti configurabili in fase di produzione del token:

		- generazione dei claim 'jti' e 'aud' identici o differenti nei 2 token;
	
		- possibilità di personalizzare ulteriori claim (compresi 'sub' e 'iss' e 'client_id') anche solo in uno dei due header;

	- aspetti configurabili in fase di validazione del token:

		- selezione dell'header da cui estrarre l'identificativo 'jti' utilizzato per filtrare le richieste duplicate;

		- indicare un audience atteso differente tra i due token.

  La generazione contemporanea dei 2 header nella richiesta e del solo header 'Agid-JWT-Signature' nella risposta diventa il default proposto con un pattern di sicurezza 'Integrity'.

- *Custom Claims*: è adesso possibile aggiungere nel payload del token JWT, su API REST, ulteriori claims oltre a quelli standard generati da GovWay oltre a poter sovrascrivere i valori di default assegnati ai claims standard (es. 'iss', 'sub', 'client_id') o disabilitarne la generazione;

- *PKCS11*: è stata aggiunta la possibilità di configurare un keystore HSM via 'PKCS11' per accedere alla chiave privata da utilizzare per la firma del token di sicurezza; il keystore è associabile all'applicativo che deve firmare il token di richiesta e all'erogazione che deve firmare il token di risposta;

- *Validazione Audience Risposta*: come audience atteso per un token della risposta è adesso possibile configurare un valore statico sulla fruizione, invece di usare il valore associato ad ogni applicativo fruitore;

- *TTL in Validazione*: sulla singola erogazione (configurazione 'ModI' della richiesta) o sulla fruizione (configurazione 'Modi' della risposta) è adesso possibile configurare un intervallo temporale (in secondi) per cui i token creati precedentemente all'intervallo indicato verranno rifiutati; la nuova opzione consente di sovrascrivere la configurazione di default in cui i token vengono rifiutati se sono stati creati da più di 5 minuti;

- *Token Sicurezza nelle Tracce*: in accordo a quanto richiesto dalle linee guida ModI, è stato modificato il comportamento di default di GovWay il quale non salva più i token di sicurezza scambiati;

- *Multi-Tenant Intra-Dominio con modalità 'FileSystem'*: nella registrazione degli applicativi client sul profilo ModI, nella sezione relativa alla sicurezza messaggio, è adesso possibile caricare il certificato anche con la modalità 'filesystem' e 'hsm'. Il caricamento del certificato consente la corretta identificazione dell'applicativo su un altro dominio gestito sempre nella stessa installazione govway.

Sono inoltre stati risolti i seguenti problemi:

- la configurazione delle proprietà 'org.openspcoop2.protocol.modipa.rest.securityToken.claims.iat.minutes' e 'org.openspcoop2.protocol.modipa.soap.securityToken.timestamp.created.minutes' con valori superiori alle 3 settimane venivano ignorati.


Sono stati infine apportati i seguenti miglioramenti alla console di gestione:

- nei criteri di ricerca delle API, delle Erogazioni e delle Fruizioni è stata aggiunta una sezione dedicata al profilo 'ModI' che consente di filtrare per pattern di sicurezza canale, sicurezza messaggio, digest della richiesta e informazioni utente. Inoltre nelle Erogazioni/Fruizioni è consentito filtrare per keystore e audience;

- nei criteri di ricerca degli Applicativi è stata aggiunta una sezione dedicata al profilo 'ModI' che consente di individuare gli applicativi per i quali è stata abilitata la sicurezza messaggio. La sezione consente anche di filtrare per keystore e identificativo client inserito nel token di sicurezza ModI;

- è stato aggiunta la possibilità di espandere o richiudere le sezioni 'Informazioni Utente' e 'Contemporaneità Token Authorization e Agid-JWT-Signature' presenti nella configurazione ModI delle Erogazioni o delle Fruizioni.




