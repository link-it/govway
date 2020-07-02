.. _install_ssl_client_reverse:

Reverse Proxy
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

GovWay consente di gestire correttamente le situazioni in cui le comunicazioni tra il gateway e l'endpoint destinatario siano mediate dalla presenza di un proxy. 

Nei casi più comuni si tratta di un "forward proxy". In questi casi l'indirizzo del proxy può essere censito sul connettore dell'Erogazione.  Per questa modalità seguire le indicazioni riportate nella Guida alla Console di Gestione, nella sezione 'Funzionalità Avanzate - Connettori - Proxy' al paragrafo :ref:`avanzate_connettori_proxy`.

In scenari più complessi possono essere presenti reverse proxy che intervengono nella gestione delle connessioni https, utilizzando certificati client e/o trustStore differenti per diversi contesti applicativi. In queste situazioni l'endpoint indicato nella configurazione del connettore su GovWay non è l'indirizzo remoto dell'applicativo ma bensì l'indirizzo del reverse proxy il quale a sua volta si occuperà di inoltrare la richiesta agli indirizzi a lui noti.

In questa situazione, è necessario configurare gli endpoint delle API sia su GovWay (indirizzo del reverse proxy), che sul reverse proxy (indirizzo dell'Erogatore finale).

Per semplificare la gestione di questo scenario architetturale è possibile passare l'indirizzo remoto dell'applicativo al proxy tramite un header HTTP o un parametro della url. In questo modo il censimento degli applicativi viene effettuato esclusivamente su GovWay. Per questa modalità seguire le indicazioni riportate nella Guida alla Console di Gestione, nella sezione 'Funzionalità Avanzate - Gestione Proxy' al paragrafo :ref:`avanzate_govway_proxy`.
