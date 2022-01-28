Bug Fix
-------

Sono stati risolti i seguenti bug:

- libreria 'lo4j2' aggiornata alla versione '2.17.1' per risolvere le vulnerabilità 'CVE-2021-45105' e 'CVE-2021-44832';

- il diagnostico "errore di trasporto, codice XXX", che poteva far pensare a problematiche inerenti il trasporto, è stato modificato in "errore HTTP XXX";

- le richieste SOAP che contenevano prima della definizione dell'elemento Envelope un commento xml (<!-- ... -->) contenente al suo interno una definizione Envelope con versione soap differente, venivano interpretati erroneamente con la versione SOAP indicata nella dichiarazione commentata;

- i messaggi SOAP contenenti come rootElement del Body un child element vuoto con localName che iniziava con 'Body', venivano erroneamente interpretati come messaggi SOAP con body vuoto;

- in una configurazione con cluster dinamico, è stata aggiunta la possibilità di configurare lo schema utilizzato (https/http), la porta e i parametri di utilizzo del connettore https per il servizio proxy quando deve effettuare il forward della chiamata al servizio check dei nodi;

- una consegna condizionale basata su template, su richieste senza payload (es. HTTP GET), provocava un errore simile al seguente: "Selettore 'Template' non supportato per il message-type 'BINARY'";

- l'identificativo di una consegna condizionale o l'id di sessione ottenuto tramite un template viene adesso normalizzato per eliminare spazi o ritorni a capo presenti nella parte iniziale o finale;

- differenziato diagnostico emesso in caso di consegna condizionale configurata per utilizzare un connettore di default, in modo da riconoscere un caso di estrazione della condizione fallita rispetto al caso di mancata individuazione di un connettore tramite la condizione estratta dalla richiesta;

- in caso di utilizzo della funzionalità Load Balancer con algoritmo 'SourceIpHash', la transazione terminava in errore in presenza di indirizzi ip per cui il calcolo 'hashCode' produceva un numero negativo;

- è stata corretta un'anomalia che si presentava durante una consegna di una notifica asincrona (connettori multipli) se il backend restituiva un HTTP Redirect 3XX con header 'Location'. L'errore che veniva riportato nel diagnostico era simile al seguente: "Errore durante l'aggiornamento dell'header 'Location' attraverso la funzione di proxy pass reverse: [getAccordoServizioParteComune]: Parametro non definito".


Per la console di gestione sono stati risolti i seguenti bug:

- non era consentito modificare la modalità di caricamento di una credenziale ssl relativamente ad un applicativo;

- è stata rivista la gestione dei connettori multipli in modo che successivamente ad un'operazione di aggiunta o modifica (nome, descrizione, weight ...) si venga riposizionati nella lista dei connettori dove viene selezionato il connettore oggetto della precedente operazione;

- aggiunto controllo di assegnazione univoca di un filtro ad un connettore multiplo, nel caso di consegna condizionale;

- eliminata la possibilità di associare filtri al connettore di default, nel caso di consegna condizionale;

- nella maschera di creazione di una regola specifica per azione/risorsa di un connettore multiplo veniva riportata erroneamente la dicitura 'Container' invece di 'Contenuto';

- nella consegna con notifiche, se si impostava una configurazione delle risposte accettate con "Consegna Completata" su tutti i codici http e "Consegna Fallita" nel fault, si otteneva una segnalazione errata di configurazione non valida;

- nella maschera di configurazione dei parametri di riconsegna, per le consegne con notifiche, è stata eliminata l'opzione '3xx' nel caso di API SOAP;

- nel menù è adesso possibile utilizzare il tasto destro su tutta la voce e non solo sul testo;

- la funzionalità di verifica connettività riportava un errore non corretto di 'Read timed out', invece di 'connect timed out', quando veniva verificato un endpoint non raggiungibile ad esempio per problematiche di firewall. L'anomalia è stata risolta aumentando il tempo di atteso dello stato di verifica connettività recuperato dalla console interrogando i nodi run, portando l'attesa da 5 secondi a 60 secondi per default per questo tipo di operazione.

- nella maschera di modifica di un connettore in cui veniva abilitata la funzionalità 'IntegrationManager/MessageBox' veniva erroneamente riportato il checkbox 'Modifica Password'.


Per la console di monitoraggio sono stati risolti i seguenti bug:

- il download dei report statistici come immagine, non preservava il font visualizzato nella console;

- risolta anomalia presente in tutte le distribuzioni statistiche, eccetto quella temporale e per esiti, dove selezionando un periodo 'personalizzato' compariva erroneamente la scelta dell'unità di tempo che non aveva senso per il tipo di distribuzione;

- in caso di registrazione dei contenuti abilitati, se veniva salvata una richiesta contenente un Content-Type Multipart senza però possedere effettivamente degli attachments, la sua consultazione tramite la console per visualizzare i contenuti multipart produceva un errore non atteso e una pagina bianca. Anche la funzionalità di export dei contenuti multipart generava un errore simile.



