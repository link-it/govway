Bug Fix
-------

Sono stati risolti i seguenti bug:

- Corretta sezione 'Configurazione in Load Balancing' della Guida di Installazione che presentava nomi di proprietà errati.

- Risolte problemi presenti sulla diagnostica emessa per le funzionalità di trasformazione e gestione dei token.



Sulla console di gestione sono stati risolti i seguenti bug:

- Non era possibile eliminare un'azione da un'API di tipo SOAP se esisteva un'azione con lo stesso nome in un'altra API.

- La console schiantava al momento di registrare una nuova regola di Proxy Pass. Il problema sussisteva solamente su nuove installazioni dove non era mai stato effettuato il salvataggio della configurazione.



Sulla console di monitoraggio sono stati risolti i seguenti bug:

- Nella ricerca transazioni, in ciascuna delle ricerche per mittente (Token Info, Soggetto ecc..), quando veniva cambiata la Tipologia 	(Fruzione,Erogazione,Qualsiasi), scomparivano gli input sotto la sezione "Dati Mittente".

- Sia per la ricerca di transazioni che per la generazione di report statistici, quando il valore della lista 'Tipologia' era indefinito, non veniva visualizzato il campo che consente di indicare il Soggetto Remoto.



Per l'API di configurazione e monitoraggio sono stati risolti i seguenti bug:

- Le liste vengono adesso correttamente valorizzate con gli elementi 'next', 'prev', 'last' e 'first' attraverso url relative che preservano i parametri della query e non contengono la base url.

- Migliorate validazioni degli oggetti oneOf dove è stata agganciata una validazione sintattica degli oggetti forniti rispetto al parametro discriminator.

- Per l'API di monitoraggio:

	- Nei metodi GET è adesso possibile indicare il soggetto erogatore di una api.

	- Nei metodi POST è adesso possibile indicare, con tipologia di ricerca qualsiasi, il soggetto remoto e/o il soggetto erogatore di una api.

	- Nell'interfaccia yaml sono stati aggiunti i criteri di obbligatorietà per i campi 'data_inizio' e 'data_fine' dell'oggetto FiltroTemporale e per il campo tipo dell'oggetto FiltroEsito. Inoltre il FiltroMittenteErogazioneDistribuzioneSoggettoRemoto è stato modificato per utilizzare una enumeration personalizzata, relativamente al tipo di ricerca, che non contiene il soggetto.

- Nell'API di configurazione, l'opzione 'forward' relativa all'autenticazione basic o principal di un applicativo/soggetto veniva salvata con valore invertivo.

