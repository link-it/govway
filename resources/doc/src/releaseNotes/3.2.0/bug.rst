Bug Fix
-------

Sono stati risolti i seguenti bug:

- *Riconoscimento Azione per API Soap*: risolto bug che causava il fallimento durante il riconoscimento dell'azione basato sull'interfaccia wsdl se vi erano più operazioni che condividevano la definizione di un medesimo header soap.

- *Token Policy*:  sono stati corretti diversi bug inerenti la gestione dei token OAuth2:

  - La funzionalità di Token Forward non gestiva correttamente il forward o meno del token passato tramite header http 'Authorization' (https://github.com/link-it/govway/issues/45).
  - In uno scenario in cui una funzionalità (Introspection, UserInfo, Forward ..) configurata sulla TokenPolicy veniva abilitata sull'erogazione/fruizione e veniva successivamente disabilitata sulla TokenPolicy, si verificava una inconsistenza di configurazione per cui la funzionalità rimaneva abilitata sull'erogazione/fruizione anche se non veniva più visualizzata nella maschera di controllo degli accessi e quindi non era disabilitabile.
  - Il truststore per gestire le comunicazioni ssl verso Google conteneva un certificato scaduto che è stato rimosso lasciando solamente nel truststore la CA che possiede una scadenza con data Dicembre 2021.

- *Profilo 'Fatturazione Elettronica'*: la riconciliazione sulle notifiche descritta nell'Issue (https://github.com/link-it/govway/issues/27) non funzionava su database di tipo Oracle. Inoltre la riconciliazione specifica per la fatturazione attiva, riguardante il trasmittente e l'applicativo mittente non funzionava correttamente.
