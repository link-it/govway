Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

Sono stati apportati i seguenti miglioramenti:

- ridenominato profilo 'ModI PA' in 'ModI';

- adeguata terminologia da 'profilo' a 'pattern' per i pattern di Interazione, di Sicurezza Canale e di Sicurezza Messaggio;

- per API di tipo SOAP è adesso possibile attivare anche la firma degli allegati utilizzando il pattern di sicurezza 'INTEGRITY_SOAP_01';

- è stato abilitato per default il controllo che rifiuta token creati da troppo tempo (default 5 minuti): la verifica viene effettuata verificando la data presente nel claim 'iat' del JWT per API REST o nell'elemento 'Create' del WSSecurity Timestamp per API SOAP;

- nella configurazione relativa alla sicurezza messaggio ModI di un applicativo di dominio interno è adesso possibile configurare il keystore per la firma indicandolo anche tramite un path su filesystem;
	
- aggiunta la possibilità, su API SOAP, di configurare ulteriori header soap da aggiungere agli elementi inclusi nella firma (la configurazione è disponibile utilizzando la console in modalità avanzata);

- tra le informazioni ModI presenti nella traccia vengono adesso riportati anche gli header soap firmati.


Sono inoltre stati risolti i seguenti problemi:

- nei Pattern di Interazione la validazione dei codici http su API REST bloccanti e non bloccanti viene adesso effettuata esclusivamente su codici di risposta che rientrano nelle casistiche 2xx o 3xx;

- nella validazione di un token JWT Modi per API REST con pattern INTEGRITY_REST_01 non veniva verificato che tra gli header firmati vi fosse obbligatoriamente l'header HTTP 'Digest' se la richiesta presentava un payload;

- la funzionalità 'Verifica Audience', della sezione Sicurezza Messaggio Risposta di una fruizione, veniva ignorata e la validazione veniva effettuata anche se l'opzione era disabilitata.


