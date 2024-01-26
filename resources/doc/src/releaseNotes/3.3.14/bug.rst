Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2023-51074: aggiornata libreria 'com.jayway.jsonpath:json-path' alla versione 2.9.0;

- CVE-2023-45860: aggiornata libreria 'com.hazelcast:hazelcast' alla versione 5.3.5;

- CVE-2023-44483: aggiornata libreria 'org.apache.santuario:xmlsec' alla versione 2.3.4;

- CVE-2023-5072: aggiornata libreria 'org.json:json' alla versione 20231013;

- CVE-2023-4586: aggiornata libreria 'io.netty:\*' alla versione 4.1.100.Final, libreria 'org.redisson:redisson' alla versione 3.23.5 e libreria 'org.jboss.marshalling:\*' alla versione 2.1.3.SP1

- CVE-2023-34042: aggiornata libreria 'org.springframework.security:\*' alla versione 5.8.7;

- CVE-2023-4759: aggiornata libreria 'org.eclipse.jgit:org.eclipse.jgit' alla versione 6.7.0.202309050840-r;

- CVE-2023-40167: aggiornata libreria 'org.eclipse.jetty:\*' alla versione 10.0.16.


Sono stati risolti i seguenti bug:

- le richieste contenenti metodi http 'PATCH', 'LINK' e 'UNLINK' venivano inoltrate al backend erroneamente come metodo POST se la connessione era https;

- non venivano utilizzati i tempi di connection e read timeout impostati a livello globale; l'anomalia è stata risolta e nell'occasione sono stati rivisti i tempi di default utilizzati a livello globale per una nuova installazione:

	- connection timeout: modificato da 10 a 5 secondi
	- read timeout sulle erogazioni: modificato da 120 a 60 secondi
	- read timeout sulle fruizioni: modificato da 150 a 65 secondi


Sono stati risolti i seguenti bug relativi al profilo di interoperabilità "ModI":

- in una fruizione ModI di una API definita tramite i pattern 'ID_AUTH_REST via PDND' e 'AUDIT_REST_01',  se la fruizione risultava configurata per utilizzare un keystore definito nell'applicativo e quest'ultimo non veniva identificato durante la gestione della richiesta, GovWay emetteva un diagnostico malformato: "Il profilo di sicurezza richiesto 'null' richiede l'identificazione di un applicativo";

- in un contesto in cui risultava già attiva una erogazione definita senza pattern di sicurezza messaggio o con un pattern con generazione token 'Authorization ModI',  se veniva modificata l'API per utilizzare un pattern con token 'Authorization PDND', la sezione controllo degli accessi dell'erogazione non consentiva di abilitare la token policy di validazione dei voucher PDND;

- la creazione di una erogazione di API con pattern di sicurezza canale 'ID_AUTH_CHANNEL_02' veniva effettuata definendo un controllo accessi non corretto poichè l'autenticazione canale veniva configurata come opzionale;

- in un API definita con un pattern di sicurezza messaggio con generazione token 'Authorization ModI' e con voce 'Header HTTP del Token' impostata a 'Custom-JWT-Signature', se veniva modificata la voce di generazione token in 'Authorization PDND' rimanevano inconsistenti le successive voci che consentono la configurazione dell'header custom;

- la verifica dei keystore/truststore di una fruizione o erogazione, da utilizzare per i token di risposta, viene adesso effettuata solamente se l'API prevede un token di sicurezza nella risposta;

- una fruizione ModI di una API definita con pattern 'ID_AUTH_REST' e 'Generazione Token via PDND' presentava le seguenti anomalie:

	- la verifica dei certificati non veniva effettuata: la console indicava che tutti i certificati erano validi anche quando non lo erano;

	- la configurazione fornita dalla funzionalità 'Visualizza dettagli della configurazione', presente nelle opzioni della fruizione, non visualizzava le informazioni corrette su eventuali keystore definite nella fruizione stessa;

        - nel caso si configurava prima una API senza pattern di sicurezza messaggio e successivamente si modificava impostando il pattern 'ID_AUTH_REST_01' e 'Generazione Token' via PDND, entrando nella maschera di configurazione del connettore della fruizione si otteneva una informazione errata sulla token policy che sembrava assegnata anche se in realtà non lo era.

- Durante la registrazione di un applicativo con profilo di interoperabilità 'ModI', se nella sezione 'ModI - Sicurezza Messaggio - KeyStore' veniva effettuato con modalità 'Archivio' l'upload di un keystore pkcs12, creato importando un altro archivio pkcs12 al suo interno, si ottevena l'errore: "keystore password was incorrect". Si trattava dello stesso bug risolto nell'issue 'https://github.com/link-it/govway/issues/128' la cui risoluzione non era stata riportata nella maschera di gestione della sicurezza ModI di un applicativo.



Per la console di gestione sono stati risolti i seguenti bug:

- durante la visualizzazione di una pagina, il componente "loading" che inibisce l'utilizzo della pagina stessa terminava la sua funzione prima che il caricamento della pagina fosse completo;

- la creazione di una API tramite interfaccia OpenAPI contenente la definizione di un parametro di tipo 'header' falliva e dai log si poteva riscontrare il seguente errore: "Trovato parametro header 'Authorization' senza tipo";

- se veniva effettuata una configurazione dei nodi in cluster suddivisi per gruppi, l'operazione "Svuota le Cache dei nodi '<nomeGruppo>'" veniva ripetuta erroneamente più volte per ogni nodo;

- su una erogazione configurata per gestire gruppi di risorse differenti in cui in ogni gruppo veniva ridefinito il connettore e attivata la consegna condizionale, se venivano visualizzati i connettori di un gruppo e successivamente si passava a visualizzare i connettori dell'altro gruppo i dati riportati sui connettori erano errati.



Per la console di monitoraggio sono stati risolti i seguenti bug:

- la visualizzazione dei contenuti delle richieste e delle risposte su database SQLServer falliva se la dimensione dei messaggi era superiore a '250Kb'.


Per le API di monitoraggio sono stati risolti i seguenti bug:

- la distribuzione temporale non consentiva di ottenere report contenenti informazioni sull'occupazione della banda e sul tempo medio di risposta dei servizi;

- tra le informazioni restituite per un evento non era presente la sua descrizione.
