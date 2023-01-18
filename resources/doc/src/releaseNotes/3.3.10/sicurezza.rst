Miglioramenti alle funzionalità di Sicurezza
---------------------------------------------

È stato introdotto il supporto al protocollo Online Certificate Status Protocol (OCSP), descritto nel RFC 2560, che consente di verificare la validità di un certificato senza ricorrere alle liste di revoca dei certificati (CRL). 

GovWay consente di definire differenti molteplici policy OCSP, ad ognuna delle quali è possibile attribuire una modalità di validazione del certificato differente rispetto a vari parametri: dove reperire la url del servizio OCSP a cui richiedere la validità del certificato e il certificato dell’Issuer che lo ha emesso, come comportarsi se un servizio non è disponibile, eventuale validazione CRL alternativa etc.

Una policy OCSP è utilizzabile nelle seguenti funzionalità per attuare una validazione OCSP dei certificati presenti:

-  *Profilo di Interoperabilità ModI*: certificato utilizzato all'interno dei token di sicurezza 'ID_AUTH' e 'INTEGRITY';

-  *Connettore HTTPS*: certificato server;

-  *WSSecurity* e *JOSE*: certificato utilizzato per firmare il messaggio;

-  *Token OAuth*: certificato utilizzato per firmare il token;

-  *Autenticazione HTTPS*: certificato client;

-  *Frontend HTTPS*: certificati X.509 inoltrati a GovWay su header http dai frontend dove viene attuata la terminazione tls (Apache httpd, IIS, etc).
