Miglioramenti alla Console di Gestione
-------------------------------------------------------

Sono stati apportati i seguenti miglioramenti alla console di gestione:


- aggiunta la possibilità di modificare il soggetto erogatore nelle fruizioni e nelle erogazioni (https://github.com/link-it/govway/issues/63 e https://github.com/link-it/govway/issues/64);

- aggiunto supporto per il caricamento di file multipli nelle schermate di aggiunta degli allegati nelle API e nelle Erogazioni e Fruizioni;

- aggiunta la possibilità di filtrare per API sulle erogazioni e fruizioni;

- nellla sezione 'Controllo degli Accessi':

	- l'autenticazione è adesso modificabile solamente se non sono stati indicati puntualmente degli applicativi o dei soggetti nel criterio di autorizzazione "per richiedente";

	- se viene selezionata una autenticazione differente da quella precedentemente impostata, gli eventuali link 'Applicativi' e 'Soggetti' presenti nella sezione "autorizzazione per richiedente" non vengono più visualizzati fino a che non viene effettuato il salvataggio del nuovo tipo di autenticazione

- nella configurazione dei gruppi di risorse di una erogazione o fruizione è adesso possibile filtrare per metodo http e path;

- aggiunta la possibilità di registrare proprietà generiche sui soggetti e sugli applicativi;

- la funzionalità 'In uso' per gli applicativi e per i soggetti mostra adesso anche per quali erogazioni e fruizioni sono compatibili rispetto al criterio di autorizzazione per ruoli impostato nel controllo degli accessi;

- durante la creazione di una erogazione o fruizione fino a che non viene selezionata una API non sono visualizzate le sezioni 'Controllo Accessi', 'Connettore' e 'ModI';

- il parametro 'Access-Control-Max-Age', per le richieste Preflight CORS, è adesso configurabile dalla console senza dover accedere alla modalità avanzata;

- aggiunta funzionalità di filtro per Tag nelle policy di Rate Limiting a livello di configurazione globale;

- nella configurazione del tracciamento, dove è possibile indicare gli esiti delle transazioni da registrare, viene adesso gestita tramite una voce dedicata l'esito relativo alla violazione di una policy di RateLimiting;

- nelle maschere di configurazione dei connettori:

	- è stata aggiunta la nota "Indicazione in millisecondi (ms)" nei campo relativi alla sezione "ridefinisci tempi di risposta";

	- nella maschera di configurazione di un connettore multiplo vengono adesso proposti i valori di default nella sezione "ridefinisci tempi di risposta", se abilitata

- aggiunta funzionalità di export/import per:

	- Token Policy

	- Policy di Rate Limiting del Controllo del Traffico

	- Regole di Proxy Pass

