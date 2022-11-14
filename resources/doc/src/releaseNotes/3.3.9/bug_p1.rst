.. _3.3.9.1_bug:

Bug Fix 3.3.9.p1
----------------

Sono stati risolti i seguenti bug:

- nel profilo di interoperabilità 'SPCoop' è stata eliminata la dichiarazione del namespace con prefisso 'SOAP_ENV' che veniva inserito nell'header eGov generato senza poi essere effettivamente utilizzato;

- nelle informazioni salvate durante la negoziazione di un token non venivano memorizzati gli eventuali parametri indicati nella form: audience, scope, client_id, resource (PDND).

Per la console di gestione sono stati risolti i seguenti bug:

- aggiunta protezione agli attacchi CSRF;

- aggiunta gestione dell'header http "Content Security Policy (CSP);

- la segnalazione 'Visualizza Riferimenti' di un applicativo non funzionava se l'applicativo possedeva dei ruoli compatibili con le erogazioni associate;

- l'associazione di credenziali di tipo basic/ssl/principal ai soggetti e agli applicativi viene adesso effettuata controllando l'univocità della credenziale su entrambi;

- nella sezione 'sicurezza messaggio' di un applicativo con profilo di interoperabilità ModI, la configurazione relativa ai dati di accesso al keystore risultava eliminabile da console, ma l'operazione non comportava una effettiva pulizia nella base dati dove i dati rimanevano anche se non più visualizzabili.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- aggiunta protezione agli attacchi CSRF.


