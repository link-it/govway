Nuova funzionalità di registrazione dei Plugins
------------------------------------------------------

Aggiunta la possibilità di registrare, tramite la console di gestione, le classi dei plugin che implementano le funzionalità personalizzabili: autenticazione, autorizzazione, autorizzazione dei contenuti, connettori, rate-limiting, header di integrazione e handlers. Successivamente alla registrazione, il plugin risulta selezionabile nella configurazione dell'erogazione o della fruizione relativa alla funzionalità che il plugin implementa. 

È inoltre possibile caricare tramite la console gli archivi jar che implementano le personalizzazioni; gli archivi caricati risultano subito disponibili al gateway essendo stato implementato un meccanismo di class loader dedicato a tali componenti. 

È stata rivista la configurazione degli header di integrazione, tra le opzioni avanzate delle erogazioni e delle fruizioni, con il fine di rendere più agevole la configurazione tramite una semplice selezione da una lista dei meccanismi di integrazione disponibili.

Infine è stata aggiunta la possibilità di registrare handler che agiscono sulle varie fasi di una richiesta in transito su GovWay. Gli handler sono registrabili sia a livello di configurazione generale che sulle specifiche erogazioni e fruizioni di API.
