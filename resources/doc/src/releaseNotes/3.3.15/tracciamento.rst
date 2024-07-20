Miglioramenti alla funzionalità di Tracciamento
------------------------------------------------

Migliorata funzionalità di tracciamento introducendo la possibilità di attivare il tracciamento su database e/o su file (FileTrace) in 4 momenti differenti della gestione di una richiesta:

- Richiesta ricevuta
- Richiesta in consegna
- Risposta in consegna
- Risposta consegnata

Ogni fase è attivabile sia a livello di configurazione generale che sulla singola erogazione o fruizione. Inoltre, ogni fase è configurabile per far terminare la richiesta con errore in caso di tracciamento fallito o proseguire segnalando l'anomalia solamente nei log.
	
Nel caso di richiesta terminata con un errore di tracciamento, è stato aggiunto un nuovo esito 'Tracciamento Fallito'.

Sono state attuate le seguenti migliorie agli aspetti di configurazione del tracciamento su file (FileTrace):

- possibilità di attivare il tracciamento rispetto all'esito di una transazione;
- maschera di configurazione sulla singola erogazione o fruizione che consente di indicare il file di configurazione e l'attivazione o meno del buffer dei messaggi;
- possibilità di utilizzare funzionalità di 'unwrap' della chiave per la cifratura dei dati.

Nel menù principale la configurazione a livello globale del tracciamento e della registrazione messaggi è stata suddivisa in due voci distinte.

Infine è stato modificato il comportamento predefinito in modo che il tracciamento delle richieste che violano una politica di RateLimiting sia disabilitato.
