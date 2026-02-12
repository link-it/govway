Miglioramenti alla funzionalità di Tracciamento
------------------------------------------------

Sono stati introdotti i seguenti miglioramenti al processo di tracciamento su database:

- Aggiunta sanitizzazione delle porte negli indirizzi IP presenti negli header di trasporto (X-Forwarded-For e header analoghi) al fine di evitare la moltiplicazione delle entry nella tabella 'credenziale_mittente' dove viene salvato l'indirizzo per consentire la ricerca tramite indici. Nell'occasione è stato aggiunto il supporto al parsing dell'header Forwarded (RFC 7239) con estrazione corretta dell'indirizzo IP dal parametro 'for='.

- Aggiunta possibilità di disabilitare selettivamente l'indicizzazione delle credenziali mittente (credenziali di autenticazione, token, indirizzo IP client, eventi, tag, API) a livello globale o per singola erogazione/fruizione, consentendo di risparmiare risorse database per i tipi non utilizzati nelle ricerche dalla console di monitoraggio e dalle relative API.

- Abilitata di default la registrazione, nel file di slow log, del dettaglio delle singole fasi di persistenza su database per le transazioni con tempo di salvataggio superiore a 1 secondo.
