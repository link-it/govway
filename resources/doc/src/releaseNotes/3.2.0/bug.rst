Bug Fix
-------

Sono stati risolti i seguenti bug:

- *Riconoscimento Azione per API Soap*: risolto bug che causava il
  fallimento durante il riconoscimento dell'azione basato
  sull'interfaccia wsdl se vi erano più operazioni che condividevano
  la definizione di un medesimo header soap.

- *Token Policy*: sono stati corretti alcuni bug inerenti la gestione dei token OAuth2:

  - Malfunzionamento nella funzione di "Token Forward"
    tramite header http
    'Authorization' (https://github.com/link-it/govway/issues/45).

  - Malfunzionamento nella gestione di alcune funzioni delle
    TokenPolicy. Quando disabilitate, se già in uso nella
    configurazione delle API, la funzionalità rimaneva abilitata
    sull'API anche se non più visualizzata
    nella maschera di controllo degli accessi e quindi non più
    disabilitabile.

  - Il truststore per gestire le comunicazioni ssl verso Google
    conteneva un certificato scaduto che è stato rimosso lasciando
    nel truststore la sola CA che possiede una scadenza con data
    Dicembre 2021.

- *Dump Binario*: risolto malfunzionamento che si verificava nel caso
  di messaggi senza payload. Non venivano salvati gli
  header HTTP presenti se era stata abilitata la funzionalità di dump
  binario.

- *Informazioni Runtime* e *Verifica Connettività*: abilitando la
  configurazione in cluster delle console, l'accesso alla sezione
  'Runtime' e l'accesso alla funzionalità di 'Verifica Connettività'
  di un connettore produceva il seguente errore:
  java.lang.NoClassDefFoundError:
  org/springframework/web/util/UriUtils ...

- *Profilo 'Fatturazione Elettronica'*: la riconciliazione sulle
  notifiche descritta nell'Issue
  (https://github.com/link-it/govway/issues/27) non funzionava su
  database di tipo Oracle. Inoltre la riconciliazione specifica per la
  fatturazione attiva, riguardante il trasmittente e l'applicativo
  mittente non funzionava correttamente.
