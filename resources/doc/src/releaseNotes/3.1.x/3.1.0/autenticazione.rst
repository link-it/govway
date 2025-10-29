Miglioramenti alla funzionalità di Autenticazione
-------------------------------------------------

Nella sezione 'Controllo degli Accessi' di una erogazione o fruizione di API sono ora disponibili nuove modalità di accesso all'identità del client per l'autenticazione di tipo "principal":

-  *Container*: rappresenta l'unica modalità presente nelle precedenti versioni di GovWay.

-  *Header HTTP*: il principal viene letto da un header http il cui nome viene indicato nella configurazione dell'erogazione o fruizione. La configurazione permette inoltre di indicare se l'header vada consumato dopo il processo di autenticazione o invece inoltrato.

-  *Parametri della URL*: il principal viene letto da un parametro della url di invocazione il cui nome viene indicato nella configurazione dell'erogazione o fruizione. La configurazione permette inoltre di indicare se l'header vada consumato dopo il processo di autenticazione o invece inoltrato.

-  *Indirizzo IP*: come principal viene utilizzato l'indirizzo IP del mittente.

- *Token*: il principal viene letto da uno dei claim presenti nel token.

Per quanto concerne invece l'autenticazione di tipo 'http-basic' è stata aggiunta la possibilità di configurare se l'header http 'Authorization' vada consumato dopo il processo di autenticazione o invece inoltrato.
