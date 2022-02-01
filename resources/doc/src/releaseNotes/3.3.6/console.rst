Miglioramenti alla Console di Gestione
-------------------------------------------------------

Sono stati apportati i seguenti miglioramenti alla console di gestione:

- per ogni entità del registro, sia dall'elenco che dal dettaglio, è adesso possibile:

	- rimuovere puntualmente dalla cache i dati relativi all'oggetto;

	- visualizzare i riferimenti agli utilizzi di quella entità;

	- visualizzare una scheda riassuntiva della configurazione di una erogazione o di una fruizione;

	- verificare la validità dei certificati inclusi nei keystore riferiti dai connettori http/https, dalla configurazione del profilo di interoperabilità ModI o riferiti negli applicativi e nei soggetti;

	- aggiunta la possibilità di verificare la connettività anche per gli endpoint configurati nelle Token Policy di validazione e negoziazione, nelle Attribute Authority e negli applicativi di tipo server.

- effettuato un restyling dell'elenco delle Token Policy di validazione e negoziazione e delle Attribute Authority;

- aggiunta la validazione delle espressioni regolari, degli xpath e dei jsonPath configurabili nelle varie funzionalità gestite da console;

- migliorata gestione e monitoraggio delle consegne asincrone (attivabili con una installazione in modalità avanzata):

	- aggiunta la possibilità di disabilitare lo scheduling delle consegne di un connettore, mantenendo i messaggi in coda fino alla riabilitazione;

	- nella funzionalità 'Coda Messaggi' è adesso possibile avere una informazione di sintesi delle consegne in corso.
