Nuova funzionalità di Negoziazione Token
----------------------------------------

Aggiunta la funzionalità di negoziazione di Bearer Token da inoltrare verso gli endpoint definiti nei connettori.

All'interno di Token Policy, funzionali alla negoziazione di un access token, vengono definiti tutti i parametri necessari per l'accesso all'Authorization Server, tra cui il flusso oauth selezionabile tra 'Client Credentials Grant' o 'Resource Owner Password Credentials Grant'.
La policy, una volta definita, deve essere associata ad un connettore per attivarla. La rinegoziazione del token avviene automaticamente una volta che il token è scaduto.
