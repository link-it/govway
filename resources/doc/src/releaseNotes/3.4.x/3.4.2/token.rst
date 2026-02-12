Miglioramenti alla funzionalità di Gestione dei Token
-----------------------------------------------------

Introdotto il supporto DPoP (Demonstrating Proof of Possession):

- nelle policy di validazione token è adesso possibile abilitare la validazione del proof-of-possession con protezione anti-replay basata su JTI tramite cache locale (Caffeine) o distribuita (Redis).
- nelle policy di negoziazione è adesso possibile negoziare un token DPoP-bound verso l'Authorization Server, con generazione automatica del proof JWT e gestione della chiave pubblica (jwk) nell'header, e l'invio del DPoP proof verso il backend.

Nella validazione di un token JWT tramite token policy, è ora possibile sovrascrivere l'intervallo temporale di validità del claim 'iat' definito a livello globale.
