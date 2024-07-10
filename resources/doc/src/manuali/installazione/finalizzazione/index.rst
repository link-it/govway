.. _finalizzazione:

=================================
Finalizzazione dell'Installazione
=================================

Terminati i passi descritti nelle precedenti sezioni, GovWay è
pienamente operativo e può essere utilizzato per proteggere le proprie
API. Il prodotto viene dispiegato con dei parametri di configurazione
che possiedono dei valori di default relativamente alle seguenti
tematiche:

#. *GovWay Config Map*

   La mappa '*govway.map.properties*' consente di definire una serie di variabili Java che possono essere riferite da tutte le proprietà presenti nei vari file di configurazione e dalle configurazioni attivate tramite la console di gestione, come descritto nella sezione :ref:`govwayConfigMap`.

#. *Cifratura delle informazioni confidenziali*

   GovWay supporta la gestione delle informazioni confidenziali salvate su database e delle chiavi/keystore presenti su filesystem attraverso la cifratura/decifratura mediante una master key, utilizzando un approccio HYOK (Hold Your Own Key) o BYOK (Bring Your Own Key).

   Con l'approccio HYOK, le operazioni di cifratura e decifratura avvengono utilizzando una master key presente in un keystore locale (risiedente su filesystem) o all'interno di un HSM come descritto nella sezione :ref:`pkcs11Install`.

   In alternativa, è possibile adottare una gestione BYOK, dove la master key è depositata su un servizio cloud. In questo caso, le operazioni di wrap-key e unwrap-key delle informazioni confidenziali vengono gestite tramite chiamate API esposte dal servizio cloud.
   
   Maggiori indicazioni a riguardo sono presenti nella sezione :ref:`byokInstall`.

#. *GovWay Secrets*

   La mappa '*govway.secrets.properties*' consente di definire una serie di variabili Java, in maniera simile a :ref:`govwayConfigMap`, con la differenza che i valori saranno forniti cifrati e GovWay si occuperà di decifrarli prima del loro caricamento nel sistema. Maggiori dettagli vengono forniti nella sezione :ref:`govwaySecretsMap`.

#. *URL di Invocazione*

   Per conoscere l'url di invocazione di una API protetta da GovWay è
   possibile accedere al dettaglio di una erogazione o fruizione tramite
   la govwayConsole. L'url fornita avrà un prefisso
   *http://localhost:8080/govway*.

   Se il gateway è stato dispiegato in modo da essere raggiungibile
   tramite un host, porta o contesto differente è possibile configurare
   tale prefisso seguendo le indicazioni descritte nella sezione :ref:`urlInvocazione`.

#. *Multi-Tenant*

   I processi di configurazione e monitoraggio attuabili tramite le
   console sono ottimizzati nell'ottica di gestire sempre un unico
   dominio rappresentato da un soggetto interno il cui nome è stato
   fornito durante l'esecuzione dell'installer (:numref:`interop_fig`).

   Per estendere l'ambito delle configurazioni e del monitoraggio
   tramite console a più di un soggetto interno al dominio seguire le
   indicazioni presenti nella sezione :ref:`multiTenant`.

#. *Gestione CORS*

   Nella configurazione di default di GovWay è abilitata la gestione del
   *cross-origin HTTP request (CORS)*; è possibile modificarne la
   configurazione seguendo le indicazioni presenti nella sezione :ref:`cors`.

#. *Rate Limiting*

   GovWay permette definire un rate limiting sulle singole erogazioni o
   fruizioni di API. Le metriche utilizzabili riguardano il numero di
   richieste all'interno di un intervallo temporale, l'occupazione di
   banda, il tempo di risposta etc. 

   In presenza di una installazione con più nodi gateway attivi, GowWay per default effettua il conteggio delle metriche utilizzate dalle policy di rate limiting indipendentemente su ogni singolo nodo del cluster. Questa soluzione è certamente la più efficiente, ma presenta dei limiti evidenti se è necessaria una contabilità precisa del numero di accessi consentiti globalmente da parte dell’intero cluster. In tali situazioni è necessario modificare la configurazioni di default per attivare modalità di conteggio distribuite le quali richiedono alcune configurazioni del sistema descritte nella sezione :ref:`finalizzazioneHazelcast`.

   Oltre al rate limiting GovWay consente di fissare un numero limite
   complessivo, indipendente dalle APIs, riguardo alle richieste
   gestibili simultaneamente dal gateway, bloccando le richieste in
   eccesso.

   Per default GovWay è configurato per gestire simultaneamente al
   massimo 200 richieste. Per modificare tale configurazione seguire le
   indicazioni presenti nella sezione :ref:`maxRequests`.

   Sempre a livello globale, GovWay limita la dimensione massima accettata di una richiesta e di una risposta a 10MB. Per modificare i livelli di soglia della policy seguire le
   indicazioni presenti nella sezione :ref:`dimensioneMassimaMessaggi`.

#. *Tempi Risposta*

   GovWay è preconfigurato con dei parametri di timeout per quanto
   concerne la gestione delle connessioni verso gli applicativi interni
   (erogazioni) o esterni (fruizioni) dal dominio di gestione. Per
   effettuare un tuning di tali parametri seguire le indicazioni
   descritte nella sezione :ref:`tempiRisposta`.
  
#. *Caching della Risposta - Disk Cache*

   In GovWay è possibile abilitare il salvataggio delle risposte in una
   cache sia globalmente, in modo che sia attivo per tutte le APIs, che
   singolarmente sulla singola erogazione o fruizione. Questa
   funzionalità permette ad un backend server di non dover riprocessare
   le stesse richieste più volte.

   La configurazione di default prevede di salvare in una cache, che
   risiede in memoria RAM, fino a 5.000 risposte (ogni risposta comporta
   il salvataggio di due elementi in cache). In caso venga superato il
   numero massimo di elementi che possano risiedere in cache, vengono
   eliminate le risposte meno recenti secondo una politica *LRU*.

   GovWay consente di personalizzare la configurazione della cache in
   modo da aggiungere una memoria secondaria dove salvare gli elementi
   in eccesso. Per abilitare la memoria secondaria seguire le
   indicazioni descritte nella sezione :ref:`cachingRisposta`.

#. *Device PKCS11*

   Il Cryptographic Token Interface Standard, PKCS#11, definisce interfacce di programmazione native per token crittografici, come acceleratori crittografici hardware e smartcard. 
   Per consentire a GovWay di accedere ai token PKCS#11 nativi è necessario configurare correttamente il provider PKCS#11 e registrarlo tra i token conosciuti da GovWay seguendo le indicazioni descritte nella sezione :ref:`pkcs11Install`. 

#. *Online Certificate Status Protocol (OCSP)*

   Il protocollo Online Certificate Status Protocol (OCSP), descritto nel RFC 2560, consente di verificare la validità di un certificato senza ricorrere alle liste di revoca dei certificati (CRL).
   Le modalità di verifica possono differire per svariati motivi: dove reperire la url del servizio OCSP a cui richiedere la validità del certificato e il certificato dell'Issuer che lo ha emesso, come comportarsi se un servizio non è disponibile, eventuale validazione CRL alternativa etc.
   GovWay consente di definire differenti policy OCSP che saranno disponibili per la scelta in fase di configurazione di una funzionalità che richiede la validazione di un certificato; in ognuna delle policy sarà possibile configurare modalità di verifica dei certificati differenti descritte nella sezione :ref:`ocspInstall`. 

#. *API di Configurazione e Monitoraggio*

   GovWay fornisce sia una console che dei servizi che espongono API REST per la sua configurazione e per il monitoraggio.
   L'installer genera per default le console mentre i servizi devono essere selezionati puntualmente dall'utente (:numref:`apiREST_fig`).

   Gli indirizzi per accedere alle console sono già stati forniti nella fase di :ref:`inst_verifica`.

   Nel caso invece siano stati generati i servizi, gli indirizzi base per utilizzarli sono:

   - *http://<hostname-pdd>/govway/ENTE/api-config/v1/*
   - *http://<hostname-pdd>/govway/ENTE/api-monitor/v1/*

   ma deve essere completata la configurazione del Controllo degli Accessi per poterli invocare correttamente seguendo le indicazioni descritte nella sezione :ref:`apiRest`.

#. *Load Balancing*

   Il prodotto è preconfigurato per funzionare su di una singola
   istanza. Per realizzare un'installazione in load balancing seguire le
   indicazioni descritte nella sezione :ref:`cluster`.

#. *Configurazione HTTPS*

   GovWay processa ogni richiesta in una duplice veste agendo sia da server al momento della ricezione della richiesta che da client al momento di inoltrare la richiesta verso i backend applicativi. 

   In entrambi i ruoli la configurazione varia a seconda dell'architettura in cui è stato dispiegato GovWay (es. presenza di un Web Server). Indicazioni sulla configurazione vengono fornite nella sezione :ref:`install_ssl_config`.

#. *Integrazione delle Console con IdM esterno*

   Nella sezione :ref:`idmEsterno` vengono fornite indicazioni su come sia possibile delegare l'autenticazione delle utenze ad un IdM esterno da cui ottenere il principal dell'utenza.

#. *Richieste 'application/x-www-form-urlencoded' su WildFly*

   Per poter gestire correttamente richieste con Content-Type 'application/x-www-form-urlencoded' su application server 'WildFly', è richiesto di abilitare l'attributo 'allow-non-standard-wrappers' nella configurazione dell'A.S. 
   Indicazioni sulla configurazione vengono fornite nella sezione :ref:`wfUrlEncoded`.

#. *ApplicationSecurityDomain 'other' su WildFly 25 o superiore*

   A partire dalla versione 25 di wildfly, nella configurazione di default è abilitato un application-security-domain 'other' che rende obbligatoria la presenza di credenziali valide per invocare i contesti di GovWay. 
   Questa configurazione deve essere disabilitata come indicato nella sezione :ref:`securityDomainOther`.

#. *Cache*

   In GovWay è possibile abilitare l'utilizzo di cache che mantengono i dati di configurazioni acceduti, i keystore e i certificati, il risultato dei processi di validazione, autenticazione, autorizzazione e altri aspetti minori.
   Ogni funzionalità è associata ad una cache dedicata i cui parametri sono configurabili come indicato nella sezione :ref:`govWayCaches`.
 

.. toctree::
        :maxdepth: 2

        map/index
        byok/index
        secrets/index
        urlInvocazione
	multiTenant
	cors
	rateLimiting/index
	tempiRisposta
	cachingRisposta
	pkcs11
        ocsp/index
	apiRest
	cluster/index
	ssl/index
        idmEsterno
	wildflyUrlEncoded
	securityDomainOther
	cache
