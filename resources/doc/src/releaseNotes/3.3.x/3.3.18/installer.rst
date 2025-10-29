Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- rivisto lo script di inizializzazione della tabella 'transazioni_esiti' per associare alla colonna 'govway_status_class' il numero della classe anziché l’identificativo della tabella di riferimento;

- introdotta procedura di svecchiamento per db MySQL.

Nel caso di utilizzo dell’installer in modalità avanzata, con base dati dedicata alle informazioni statistiche, sono state risolte le seguenti anomalie nelle patch di aggiornamento da versioni precedenti:

	- l'aggiunta della colonna id nella tabella statistiche risultava inclusa erroneamente nello script SQL relativo alle tracce, anziché in quello dedicato alla base dati statistica;
	- le inizializzazioni dei semafori per la generazione delle statistiche erano presenti nello script SQL relativo alle configurazioni, anziché in quello dedicato alla base dati statistica. L'errata inizializzazione dei semafori comportava malfunzionamenti nella generazione delle statistiche, in particolare nelle installazioni in cui la gestione delle statistiche avveniva direttamente sul nodo run, anziché tramite il modulo batch.
