Miglioramenti alla Console di Gestione
-------------------------------------------------------

Sono stati apportati i seguenti miglioramenti alla console di gestione:

- per ogni entità del registro, sia dall'elenco che dal dettaglio, è adesso possibile intraprendere le seguenti azioni:

	- rimuovere puntualmente dalla cache i dati relativi all'oggetto;

	- visualizzare i riferimenti di dove un oggetto viene utilizzato;

	- visualizzare una scheda riassuntiva della configurazione di una erogazione o di una fruizione;

	- verificare i certificati inclusi nei keystore riferiti dai connettori http/https, dalla configurazione del profilo di interoperabilità ModI o caricati negli applicativi e nei soggetti;

	- aggiunta la possibilità di verificare la connettività anche per gli endpoint configurati nelle Token Policy di validazione e negoziazione, negli Attribute Authority e negli applicativi di tipo server.

- effettuato un restyling dell'elenco delle Token Policy di validazione e negoziazione e delle Attribute Authority;

- aggiunta una validazione delle espressioni regolari, xpath e jsonPath configurabili nelle varie funzionalità del gateway;

- migliorata gestione e monitoraggio delle consegne asincrone (attivabili con una installazione in modalità avanzata):

	- aggiunto la possibilità di disabilitare lo scheduling delle consegne di un connettore, consentendone l'acquisizione dei messaggi senza consegnarli fino alla riabilitazione;

	- nella funzionalità 'Coda Messaggi' è adesso possibile avere una informazione sintetica sulle consegne in corso.
