Bug Fix
-------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- CVE-2025-48976: aggiornata libreria 'commons-fileupload:commons-fileupload' alla versione 1.6.0

- CVE-2025-48734: aggiornata libreria 'commons-beanutils:commons-beanutils' alla versione 1.11.0

Sono stati risolti i seguenti bug che avvenivano in alcune condizioni limite caratterizzate da un numero elevato di richieste simultanee:

- il contatore delle richieste in corso non veniva decrementato correttamente, causando il rifiuto delle richieste con errore 429, anche in assenza del numero effettivo di richieste simultanee previsto dal limite;

- in caso di elevato numero di richieste SOAP simultanee, in situazioni limite di blocco dovuto a interazione con componenti esterni nella gestione delle richiesta (es: autorizzazione verso servizi esterni), poteva essere saturato il pool dei SAX Parser utilizzati per la gestione dei messaggi, provocando il fallimento anche di richieste non coinvolte nel problema generando il seguente errore nei log: "Couldn't get a SAX parser while constructing a envelope".

Per la console di gestione sono stati risolti i seguenti bug:

- Aggiunto supporto, nel caricamento di un'interfaccia OpenAPI, per le seguenti casistiche, precedentemente segnalate erroneamente come anomalie:

	- utilizzo delle anchor YAML (&chiave) e dei relativi riferimenti (\*chiave);
	- definizione di parametri con schema: {}
	
- Corretto un malfunzionamento relativo alla creazione di un utente amministratore in ambiente single-tenant con successiva abilitazione dei flag DR. Sebbene il sistema impedisse correttamente la selezione dei soggetti (non previsti in modalità single-tenant), l'utenza creata nella base dati risultava non conforme e non era utilizzabile per l’accesso alla console di monitoraggio;

- Risolto problema di paginazione: durante la creazione di un nuovo oggetto, se l’utente si trovava su una pagina successiva alla prima all’interno di una lista paginata, l’interfaccia presentava comportamenti anomali.
	
- Nella funzionalità 'Importa', utilizzata per modificare i dati di una govlet mediante input acquisiti in fase di importazione,  è stato corretto un malfunzionamento che impediva la corretta sostituzione dei segnaposto nelle proprietà associate ai profili di interoperabilità.	

Per le API di configurazione sono stati risolti i seguenti bug:

- corretta un'anomalia relativa all'utilizzo del parametro 'profilo_qualsiasi' impostato a true nelle chiamate che restituiscono la lista delle erogazioni, delle fruizioni o delle API. Gli oggetti presenti nella lista risultante venivano erroneamente associati al profilo di interoperabilità di default, anziché a quello effettivamente configurato.


