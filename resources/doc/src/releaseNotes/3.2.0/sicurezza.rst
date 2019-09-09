Miglioramenti alle Funzionalità di Sicurezza
---------------------------------------------

Sono state introdotte le seguenti nuove funzionalità:

-  *Connettore HTTPS*: è stata aggiunta la possibilità di indicare opzionalmente l'alias della chiave privata da utilizzare per l'autenticazione client; funzionalità utile quando il keystore contiene più chiavi private. 

-  *CRL*: è adesso possibile indicare una lista di CRL per la validazione dei certificati sia sul connettore https che nelle configurazioni relative alla sicurezza messaggio (es. WSSecurity, JOSE Signature, OAuth2 ...). 

-  *Cache*: tutti i keystore e CRL acceduti da GovWay, sia per la sicurezza a livello trasporto che a livello messaggio, sono ora gestiti tramite cache.
