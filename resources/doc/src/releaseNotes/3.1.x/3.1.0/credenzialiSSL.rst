Nuova modalità di gestione delle Credenziali SSL
------------------------------------------------

Introdotta la possibilità di registrare le credenziali 'ssl' di
applicativi e soggetti anche tramite upload del corrispondente certificato (formati DER,
PEM, PKCS12, JKS).

La verifica dei certificati client viene ora effettuata confrontando non solamente il Subject ma anche l’Issuer. 
Inoltre è possibile configurare opzionalmente la verifica anche degli altri campi del certificato tra cui il serial number.

La nuova modalità di gestione dei certificati risolve anche i seguenti problemi:

- I certificati che contengono molteplici campi 'OU' vengono adesso gestiti correttamente.
- È possibile salvare anche i certificati che possiedono un subject con lunghezza superiore ai 255 caratteri.
- Corretta la gestione dei certificati in presenza di caratteri speciali.

