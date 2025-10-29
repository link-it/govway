Miglioramenti alla funzionalità di Tracciamento
------------------------------------------------

Migliorata funzionalità di tracciamento introducendo la possibilità di
personalizzare l'aggiornamento delle tracce su database e/o su file (FileTrace) in
corrispondenza dei 4 eventi principali della gestione di una richiesta:

- Richiesta ricevuta
- Richiesta in consegna
- Risposta in consegna
- Risposta consegnata

La modalità di tracciamento può essere personalizzata sia a livello di configurazione generale che per le singole erogazioni o fruizioni. Inoltre, per ogni evento, GovWay può essere configurato per far terminare la richiesta con errore in caso di tracciamento fallito o proseguire segnalando l'anomalia solamente nei log.
	
Per il caso di richiesta terminata con un errore di tracciamento è stato aggiunto il nuovo esito di 'Tracciamento Fallito'.

Sono state introdotte le seguenti nuove funzionalità di tracciamento su file (FileTrace):

- possibilità di attivare il tracciamento rispetto all'esito di una transazione;

- è possibile modificare la configurazione di default per ogni singola erogazione o fruizione, nello specifico per:

  - il file di configurazione che definisce il formato del log;
  - l'attivazione o meno del buffer dei messaggi, che consente di registrare gli header HTTP e il contenuto del payload;

- possibilità di utilizzare una chiave/keystore cifrata e specificare la policy necessaria per decifrarla, prima di utilizzarla per la cifratura dei dati registrati nel log.

Nel menù principale la configurazione a livello globale del tracciamento e della registrazione messaggi è stata suddivisa in due voci distinte.

Infine è stato modificato il comportamento predefinito in modo che il tracciamento delle richieste che violano una politica di RateLimiting sia disabilitato.
