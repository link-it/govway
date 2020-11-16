Miglioramenti alle API di Gestione e Monitoraggio
--------------------------------------------------

Sono stati apportati i seguenti miglioramenti alle API di monitoraggio:

- È adesso possibile ricercare transazioni o ottenere report statistici filtrando per API implementata. La funzionalità è utile in presenza di molteplici erogazioni o fruizioni che implementano la stessa API, per ottenere un report che non distingua per la singola erogazione o fruizione ma li raggruppi per API implementata.

Sono stati apportati i seguenti miglioramenti alle API di gestione:

- Le risorse '/fruizioni/{erogatore}/{nome}/{versione}/url-invocazione' e '/erogazioni/{nome}/{versione}/url-invocazione' gestiscono adesso tutte le modalità supportate dal prodotto ('content-based', 'header-based', 'input-based', 'interface-based', 'soap-action-based', 'url-based', 'protocol-based'). È stata inoltre aggiunta la modalità 'static' utilizzabile su API soap contenente un'unica azione.

- È adesso possibile registrare le proprietà di configurazione nelle erogazioni e fruizioni anche tramite api.



