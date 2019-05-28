Miglioramenti al profilo di Fatturazione Elettronica
------------------------------------------------------

Sono state realizzate le seguenti nuove funzionalità al profilo di Fatturazione Elettronica:
  
- Nella Fatturazione Passiva è stata aggiunta alla traccia delle
  notifiche ricevute l'informazione sul Codice Destinatario della
  Fattura. Tale informazione è utile per smistare le fatture e
  notifiche ricevute per Codice Destinatario.

- Nella Fatturazione Attiva è stata aggiunta alla traccia delle
  notifiche ricevute l'informazione sull'IdTrasmittente (IdPaese +
  IdCodice) e l'identificativo dell'Applicativo che ha inviato la
  fattura. Le informazioni aggiunte possono essere utilizzate per
  collezionare le notifiche in base all'ApplicativoMittente o
  all'IdTrasmittente.

- Nella Fatturazione Passiva è stata aggiunta la possibilità di
  consegnare all'applicativo, oltre alla fattura, anche il file
  Metadati ricevuto dallo SDI.  Il contenuto di tale file viene
  inserito, codificato in base64, nell'header HTTP
  'GovWay-SDI-FileMetadati'.

- Aggiunta la possibilità di disabilitare la generazione, da parte di
  GovWay, dei nomi SDI da associare alle fatture da inviare
  (fatturazione attiva) e alle notifiche esito (fatturazione
  passiva). Se viene disabilitata la funzionalità (attiva per
  default), la gestione dei nomi dei file (correttezza sintattica,
  univocità, ...) è demandata all'Applicativo Client che deve
  obbligatoriamente fornire il nome del file attraverso un parametro
  della query ('NomeFile') o un header http ('GovWay-SDI-NomeFile').

- Realizzato adeguamento necessario per ricevere le notifiche nel
  nuovo formato 'Fatturazione B2B'.

- Corretto problema che causava un salvataggio errato dei dati
  presenti nella traccia della richiesta, nel caso in cui fossero
  rilevate eccezioni di livello 'INFO'. I dati della traccia della
  richiesta riportavano erroneamente i dati relativi alla risposta.

