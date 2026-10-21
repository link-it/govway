Miglioramenti alle funzionalità di Attribute Authority e Gestione Token
-------------------------------------------------------------------------

**Cache di Introspection e UserInfo**

L'esito di Introspection e UserInfo non resta più in cache per l'intera durata della cache dei token, che consentiva di accettare un token revocato fino alla sua scadenza: i servizi vengono adesso reinvocati trascorso un intervallo configurabile (default 60 secondi), ridefinibile nella token policy di validazione.

**Dati Richiesta delle Attribute Authority**

Nella configurazione di una Attribute Authority è possibile indicare, nella nuova sottosezione 'Dati Richiesta', ulteriori parametri della url e header HTTP, anche con valori dinamici (es. '${header:NOME}'), come già previsto per le token policy di negoziazione. L'header 'Authorization' può inoltre essere definito manualmente, sia nelle Attribute Authority che nelle token policy di negoziazione, quando non è attiva alcuna autenticazione client.

**Migliore esperienza utente nella console**

Nella console di gestione, all'interno delle erogazioni e delle fruizioni, sono disponibili nuove icone che aprono direttamente, in un nuovo tab del browser, la configurazione della token policy (di negoziazione o di validazione) o dell'Attribute Authority utilizzata, senza doverla cercare nella sezione di configurazione del prodotto.
