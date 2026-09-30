.. _headerGWRateLimitingCluster_distribuita_redis_connessione:

Configurazione della connessione
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La connessione verso il database Redis viene configurata tramite le seguenti proprietà del file *<directory-lavoro>/govway_local.properties*:

::

   # Integrazione con database Redis
   org.openspcoop2.pdd.redis.enabled=true

   # Modalita' di connessione: 'cluster' (Redis Cluster) o 'single' (Redis single-endpoint / Redis Enterprise con HA interna)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.connectionMode=cluster

   # Connection Url (possono essere fornite più url separate da virgola)
   # usare rediss:// per TLS (con due s)
   # per autenticazione: redis://username:password@host:port
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.connectionUrl=redis://<HOST>:<PORT>

La proprietà '*org.openspcoop2.pdd.redis.enabled*' abilita l'integrazione con Redis: se abilitata, all'avvio GovWay inizializza il client verso il database Redis indicato. La stessa connessione viene utilizzata sia per il rate limiting distribuito sia per la validazione anti-replay distribuita delle DPoP proof (:ref:`tokenValidazionePolicy_dpop`).

.. note::
   Per default, se all'avvio il database Redis non è raggiungibile, GovWay registra l'errore nei log e completa comunque l'inizializzazione. Per far fallire invece l'avvio di GovWay è possibile impostare la seguente proprietà:

   ::

      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.startupGovWay.throwExceptionIfRedisNotReady=true

*Modalità di connessione*

La proprietà '*connectionMode*' indica la topologia del database Redis:

- *cluster* (default): Redis Cluster. Nella proprietà '*connectionUrl*' possono essere indicati uno o più nodi del cluster, separati da virgola. A partire da tali nodi il client scopre automaticamente la topologia completa del cluster;

- *single*: Redis raggiungibile tramite un unico endpoint, ad esempio un'istanza standalone o Redis Enterprise, dove l'alta affidabilità è gestita internamente dal servizio. In questa modalità viene utilizzata solamente la prima url indicata nella proprietà '*connectionUrl*'.

*Autenticazione*

Se il database Redis richiede l'autenticazione, le credenziali vanno indicate direttamente nella url di connessione:

::

   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.connectionUrl=redis://<USERNAME>:<PASSWORD>@<HOST>:<PORT>

Nei log emessi da GovWay durante l'inizializzazione della connessione la password viene mascherata.

*Connessione TLS*

Per instaurare una connessione TLS si deve utilizzare lo schema '*rediss://*' (con due 's') nella url di connessione. Per default il certificato del server viene validato rispetto alle CA presenti nel truststore della JVM e viene verificato che l'hostname corrisponda al CN o a un SAN del certificato. Tale comportamento può essere personalizzato tramite le seguenti proprietà:

::

   # Truststore per la validazione del certificato server
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ssl.truststore.path=<PATH>
   # Tipo del truststore (default: jks)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ssl.truststore.type=jks
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ssl.truststore.password=<PASSWORD>

   # Verifica che l'hostname del server corrisponda al CN/SAN del certificato (default: true)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ssl.hostnameVerifier=true

   # Accetta qualsiasi certificato server (disabilita anche hostnameVerifier). Solo per test, non usare in produzione.
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ssl.trustAll=false

Le proprietà determinano il tipo di verifica effettuata sul server:

+---------------------------------------+------------------------------------------------------------+
| Configurazione                        | Verifica effettuata                                        |
+=======================================+============================================================+
| Nessuna proprietà 'ssl' indicata      | Certificato validato con il truststore della JVM e         |
|                                       | verifica dell'hostname                                     |
+---------------------------------------+------------------------------------------------------------+
| *truststore.path* indicato            | Certificato validato con il truststore indicato e          |
|                                       | verifica dell'hostname                                     |
+---------------------------------------+------------------------------------------------------------+
| *hostnameVerifier=false*              | Solamente validazione del certificato, senza verifica      |
|                                       | dell'hostname                                              |
+---------------------------------------+------------------------------------------------------------+
| *trustAll=true*                       | Nessuna verifica: qualsiasi certificato viene accettato,   |
|                                       | indipendentemente dalle altre proprietà                    |
+---------------------------------------+------------------------------------------------------------+

Se il file indicato in '*truststore.path*' non esiste o non è leggibile, l'avvio di GovWay fallisce.

.. warning::
   L'opzione '*trustAll*' disabilita qualsiasi verifica del server.
