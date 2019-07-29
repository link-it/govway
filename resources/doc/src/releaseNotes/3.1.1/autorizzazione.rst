Miglioramenti alla funzionalità di Autorizzazione
-------------------------------------------------

Nella sezione 'Controllo degli Accessi' di una erogazione o fruizione
sono state introdotte le seguenti modifiche.

La funzionalità di autorizzazione per Token Claims è stata estesa in
modo da supportare i seguenti controlli sui valori dei claim:

- valore non nullo
- valore corrispondente ad un'espressione regolare
- valore atteso contenente parti dinamiche, riferite a header http, parametri della url o parti del messaggio

La funzionalità di autorizzazione basata sui contenuti è stata estesa
per effettuare controlli sulle seguenti risorse:

- header http
- parametri o porzioni della url di invocazione
- credenziali del chiamante (principal, username, subject ...)
- claim presenti in un token
- porzioni del messaggio individuate tramite espressioni xPath o jsonPath
- valori statici
