Nuova funzionalità di Autenticazione 'Api Key'
----------------------------------------------

La nuova funzionalità consente di autenticare gli applicativi e i
soggetti tramite una chiave di identificazione 'Api Key' veicolata in
un header http, un parametro della url o un cookie come descritto
nella specifica 'OAS3 API Keys'
(https://swagger.io/docs/specification/authentication/api-keys/).

Viene supportata anche la modalità 'App ID' che prevede oltre
all'ApiKey un identificatore dell'applicazione; modalità denominata
'Multiple API Keys' nella specifica 'OAS3 API Keys'.

È quindi possibile registrare applicativi e soggetti associandogli
credenziali 'api-key'; la registrazione comporta la generazione di una
chiave univoca da utilizzare per accedere all'API su GovWay.

La configurazione dell'autenticazione consente di indicare la modalità
di identificazione della chiave di accesso tra header http,
parametro della url e cookie (viene supportata la scelta multipla),
permettendo anche di personalizzare i nomi dei parametri, rispetto a quanto
indicato nella specifica 'OAS3 API Keys'.
