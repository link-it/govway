.. _3.4.2.1_bug:

Bug Fix 3.4.2.p1
------------------

Sono state risolte le seguenti vulnerabilità relative alle librerie di terza parte:

- GHSA-72hv-8253-57qq: aggiornata libreria 'com.fasterxml.jackson.core:\*' alla versione 2.21.1.

Sono stati risolti i seguenti bug:

- Migliorata la stabilità e le prestazioni del rate limiting distribuito:

	- Corretta la gestione dei limiti di traffico in ambiente cluster: in determinate condizioni di carico, i nodi del cluster potevano perdere il riferimento al contatore attivo durante i cambi di intervallo temporale, causando errori intermittenti e conteggi non aggiornati. Il meccanismo è stato rivisto per garantire coerenza tra tutti i nodi in ogni situazione.
	- Maggiore robustezza durante le transizioni di intervallo: le richieste in corso al momento del cambio di intervallo vengono ora gestite correttamente, evitando che eventuali errori interni si propaghino verso il chiamante. Migliorata anche la gestione degli errori transitori del cluster (perdita di coerenza, nodo temporaneamente non disponibile), con retry automatico e utilizzo dell'ultimo valore noto come fallback.
	- Riduzione della latenza in caso di errore: in presenza di contatori non più validi, il sistema ora interrompe immediatamente i tentativi di recupero invece di riprovare inutilmente, eliminando ritardi fino a circa 4,5 secondi e riducendo significativamente i log di errore.
	- Ottimizzazioni delle prestazioni per la modalità Redis: ridotto il numero di operazioni remote nel percorso critico grazie a una cache locale, con aggiornamento asincrono dei metadati per non impattare i tempi di risposta.
	- Corretta la gestione delle policy temporaneamente inattive: il processo di pulizia automatica non elimina più i contatori associati a policy che non ricevono traffico al momento, evitando che queste diventassero inutilizzabili alla ripresa delle richieste.
	- Configurazione più flessibile delle soglie di cleanup: la proprietà orphanedProxy.threshold ora accetta valori espressi in ore (h) o minuti (m), senza necessità di conversioni manuali.
	- Riduzione del rumore nei log: soppressi automaticamente messaggi di errore interni già gestiti dall'applicazione, per log più leggibili e facili da analizzare.

- Corretta la validazione del claim 'htu' del token DPoP (RFC 9449) in presenza di regole di proxy pass che riscrivono il contesto dell'URL di invocazione rimuovendo il soggetto: l'URL ricostruita per il confronto ora utilizza correttamente il contesto riscritto dalla regola e il resource path della risorsa invocata.

- Per il profilo di interoperabilità 'ModI' sono stati apportati i seguenti miglioramenti:

	- Aggiunta token policy built-in 'PDND-DPoP' per semplificare la configurazione della validazione DPoP (RFC 9449) nelle erogazioni con profilo ModI e generazione token di tipo 'Authorization PDND'.
	- Aggiunta la possibilità di fornire i parametri per la pubblicazione dei segnali SignalHub anche tramite payload JSON, oltre alle modalità già supportate tramite header HTTP e query parameter.

- Per la console di gestione, relativamente al profilo di interoperabilità 'ModI', sono state corrette le seguenti anomalie:

	- Corretta la selezione delle token policy proposte nel controllo degli accessi in presenza di risorse con configurazione DPoP mista (ereditata dall'API e ridefinita).
	- Corretta la configurazione del controllo degli accessi nella fruizione built-in 'api-pdnd-push-signals': rimossa l'autorizzazione per richiedente, non applicabile in quanto la verifica viene delegata puntualmente all'eService che pubblica i segnali.
	- Riviste etichette 'Authorization Bearer' utilizzate nelle token policy per renderle coerenti con l’introduzione del DPoP.

  È stata inoltre risolta un’anomalia che impediva l’utilizzo dei caratteri speciali \|, \< e altri nei campi password. È ora inoltre consentito l’utilizzo dello spazio nei valori delle proprietà e nella password impiegata per l’autenticazione Basic dei connettori e delle token policy.

- Nel batch di generazione delle statistiche è stata corretta la generazione del CSV di tracciamento PDND per aggregare correttamente le richieste con lo stesso (purpose_id, status, token_id) quando più riferimenti 'eventi_gestione' distinti risolvono allo stesso codice HTTP, eliminando le righe duplicate segnalate con errore 'PURPOSE_AND_STATUS_AND_TOKEN_NOT_UNIQUE'.
