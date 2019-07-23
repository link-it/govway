Miglioramenti alla funzionalità di Autorizzazione
-------------------------------------------------

Nella sezione 'Controllo degli Accessi' di una erogazione o fruizione sono stati introdotte le seguenti nuove funzionalità.

La funzionalità di autorizzazione per Token Claims è stata estesa in modo da supportare i seguenti controlli sui valori dei claim:

- il valore sia non nullo
- il valore abbia un match rispetto ad una espressione regolare
- il valore atteso può adesso essere indicato con parti dinamiche, risolte a runtime dal gateway, che consentono di selezionare header http, parametri della url o parti del messaggio

La funzionalità di autorizzazione basata sui contenuti è stata estesa per fornire una implementazione built-in che consente di effettuare controlli di uguaglianza tra i seguenti dati:

- header http
- parametri o porzioni della url di invocazione
- credenziali del chiamante (principal, username, subject ...)
- claim presente in un token
- porzioni del messaggio individuate tramite espressioni xPath o jsonPath
- valori statici
