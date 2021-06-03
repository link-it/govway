Miglioramenti alle funzionalità base dell'API Gateway
------------------------------------------------------------


Sono stati introdotti significativi miglioramenti prestazionali:

- la gestione dei messaggi di API SOAP è adesso equivalente a quella delle API REST: se non sono attive funzionalità che richiedono l'accesso al contenuto del messaggio, la gestione avviene in 'Passthrough', senza introdurre nessun overhead nella trasmissione;

- per API SOAP, anche se viene richiesta la costruzione in memoria dell'oggetto DOM che rappresenta il messaggio, al backend verrà inoltrata esattamente la richiesta originale ricevuta dal client, preventivamente bufferizzata, se le funzionalità che hanno avuto bisogno di accedere all'oggetto DOM non lo hanno modificato;

- la connessione verso il database 'runtime' viene adesso negoziata solamente se richiesta da funzionalità che ne necessitano;

- i connettori http preservano il 'keep-alive';

- la SSLSocketFactory istanziata per i connettori https viene mantenuta in una apposita cache;

- le chiavi private accedute per funzionalità di firma e decrifratura vengono salvate in cache assieme ai keystore;

- sono adesso gestiti i seguenti generatori di UUID:

	- generatore uuid v4 che utilizza SecureRandom;

	- generatore uuid v1 con mac address configurabile (se non fornito viene utilizzato uno tra quelli appartenenti alle schede di rete disponibili sulla macchina);

	- per tutti i generatori è adesso possibile attivarne una versione 'ThreadLocal';

	- il default del prodotto è stato modificato da UUIDv4 (java.util.UUID senza ThreadLocal) a UUIDv1 (com.fasterxml.uuid.impl.TimeBasedGenerator con ThreadLocal).

È stata migliorata la generazione delle informazioni statistiche:

- i criteri di generazione dei report statistici utilizzano adesso un'identificazione dell'intervallo temporale inclusivo del giorno di interesse del report (es. >=2021-02-10 00:00:00.000), soluzione che risulta maggiormente efficente in presenza di partizionamento giornaliero delle transazioni; 

- l'algoritmo di generazione delle statistiche è stato completamente rivisto:

	- le informazioni statistiche, comprensive di latenze, vengono adesso calcolate tramite un'unica query SQL in modo da essere maggiormente efficente in presenza di grande mole di dati;

	- l'aggiornamento dell'intervallo corrente è adesso transazionale;

	- aggiunto refresh della connessione ogni 300 secondi.

Infine sono stati apportati i seguenti ulteriori miglioramenti:

- per API di tipo REST è stato arricchito il diagnostico di consegna, in presenza di codice di risposta http 3xx, per registrare anche il valore dell'header http 'Location';

- la funzionalità 'ID Collaborazione' è stata ridenominata in 'ID Conversazione' e, se abilitata, l'header HTTP GovWay-Conversion-ID viene adesso sempre generato, anche nella richiesta capostipite della conversazione dove viene valorizzato con l'id di transazione della prima richiesta;

- aggiunti timeout per la lettura dello stream di dati relativo alla richiesta e alla risposta;

- aggiunta la possibilità di configurare ulteriori Content-Type associabili a richieste SOAP 1.2 (per default viene accettato solo 'application/soap+xml');

- l'autenticazione 'https', attivabile nel controllo degli accessi delle erogazioni e fruizioni, effettua adesso anche la verifica della validità temporale del certificato ricevuto;

- la lettura delle proprietà di configurazione dai file locali (es. govway_local.properties) è stata rivista per risolvere le variabili indicate nei file anche come variabile di sistema oltre che come variabili java;

- è stato rivisto il diagnostico relativo ad una richiesta duplicata al fine di fornire un messaggio più generico rispetto a quello precedente specifico per il profilo SPCoop;

- è stato modificata la configurazione di default del CORS per generare un header 'Access-Control-Max-Age' valorizzato con '28800' (8 ore).


