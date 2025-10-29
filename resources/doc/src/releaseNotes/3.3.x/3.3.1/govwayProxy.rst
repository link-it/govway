Nuova funzionalità Gestione Proxy
---------------------------------

Permette di gestire correttamente le situazioni in cui le comunicazioni tra GovWay e l'endpoint destinatario siano mediate dalla presenza di un proxy. 

Nei casi più comuni si tratta di un "forward proxy". In questi casi
l'indirizzo del proxy può essere censito sul connettore
dell'Erogazione.

In scenari più complessi possono essere presenti reverse proxy che
intervengono nella gestione delle connessioni https, utilizzando
certificati client e/o trustStore differenti per diversi contesti applicativi.
In queste situazioni l'endpoint indicato nella
configurazione del connettore su GovWay non è l'indirizzo remoto
dell'applicativo ma bensì l'indirizzo del reverse proxy il quale a
sua volta si occuperà di inoltrare la richiesta agli indirizzi a lui noti.

In questa situazione, è necessario configurare gli endpoint delle API
sia su GovWay (indirizzo del reverse proxy), che sul reverse proxy
(indirizzo dell'Erogatore finale)

Per semplificare la gestione di questo scenario architetturale, dalla
versione 3.3.1 GovWay può passare l'indirizzo remoto dell'applicativo
al proxy tramite un header HTTP o un parametro della url. In questo
modo il censimento degli applicativi viene effettuato esclusivamente
su GovWay.

