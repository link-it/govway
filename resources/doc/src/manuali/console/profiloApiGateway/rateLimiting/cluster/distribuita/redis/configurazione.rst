.. _headerGWRateLimitingCluster_distribuita_redis_ttl:

Configurazione TTL
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nel rate limiting distribuito con Redis, GovWay crea **nuovi contatori per ogni finestra temporale**. Ad esempio, con una policy di 100 richieste al minuto, ogni minuto vengono creati nuovi contatori con un nome che include il timestamp della finestra corrente.

GovWay implementa un meccanismo di pulizia ("cestino") che elimina i contatori delle finestre precedenti, ma questo meccanismo funziona **solo per i client che continuano a effettuare richieste**. Quando un client smette di effettuare richieste, i suoi contatori rimangono in Redis indefinitamente poiché nessuna nuova richiesta attiva il meccanismo di pulizia.

Per risolvere questo problema, GovWay supporta la configurazione di un **TTL (Time To Live)** sui contatori Redis. Il TTL permette la pulizia automatica dei contatori indipendentemente dall'attività del client, delegando a Redis l'eliminazione delle chiavi scadute.

.. note::
   **Quando abilitare il TTL**

   Si consiglia di abilitare il TTL in tutti gli scenari di rate limiting distribuito con Redis, in particolare quando:

   - Le policy sono raggruppate per *tokenClientId* o altri identificativi con elevata variabilità
   - I client possono accedere sporadicamente o cessare l'attività
   - Si vuole garantire una gestione automatica della memoria Redis senza dipendere dal comportamento dei client

*Principio di funzionamento*

Il TTL viene calcolato automaticamente in base al tipo di policy di rate limiting:

+----------------------------------+-------------------------------+--------------------------------+
| Tipo Policy                      | TTL applicato                 | Rinnovo TTL (default)          |
+==================================+===============================+================================+
| Con intervallo temporale         | intervallo × moltiplicatore   | Disabilitato (non necessario   |
| (es. 100 req/minuto)             |                               | perché ad ogni finestra        |
|                                  |                               | vengono creati nuovi contatori)|
+----------------------------------+-------------------------------+--------------------------------+
| Con intervallo temporale ma      | maxSeconds                    | Abilitato (per evitare         |
| TTL calcolato > maxSeconds       | (TTL troncato)                | scadenze premature)            |
+----------------------------------+-------------------------------+--------------------------------+
| Richieste simultanee             | defaultSeconds                | Abilitato (per mantenere       |
| (senza intervallo temporale)     |                               | attivi i contatori)            |
+----------------------------------+-------------------------------+--------------------------------+

**Policy con intervallo temporale**

Per le policy che definiscono un intervallo di osservazione (es. "100 richieste al minuto"), il TTL viene calcolato come:

::

   TTL = Intervallo della Policy × Moltiplicatore

Ad esempio, per una policy di 100 richieste al minuto con moltiplicatore 2, il TTL sarà di 2 minuti. Il rinnovo del TTL è disabilitato per default poiché ad ogni nuova finestra temporale vengono creati nuovi contatori: i contatori delle finestre precedenti non ricevono più scritture e scadranno naturalmente.

Se il TTL calcolato supera il valore di ``maxSeconds``, viene troncato e il rinnovo del TTL viene automaticamente abilitato per garantire che i contatori non scadano mentre la finestra è ancora attiva.

**Policy per richieste simultanee**

Per le policy che limitano il numero di richieste simultanee (senza intervallo temporale), viene utilizzato il valore ``defaultSeconds`` come TTL. Il rinnovo del TTL è abilitato per default: ad ogni richiesta il TTL viene esteso, garantendo che i contatori rimangano attivi finché il client effettua richieste.

*Configurazione*

La configurazione del TTL avviene tramite le seguenti proprietà nel file *<directory-lavoro>/govway_local.properties*:

::

   # =============================================================
   # Rate Limiting - Configurazione TTL per contatori Redis
   # =============================================================

   # Abilita il TTL per i contatori Redis (default: true)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.enabled=true

   # TTL di default in secondi, usato per le policy di richieste simultanee
   # che non hanno un intervallo temporale definito (default: 300 = 5 minuti)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.defaultSeconds=300

   # Moltiplicatore per calcolare il TTL dall'intervallo della policy
   # Formula: TTL = intervallo_policy × moltiplicatore (default: 2)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.intervalMultiplier=2

   # TTL minimo in secondi (default: 60 = 1 minuto)
   # Protezione per policy con intervalli molto brevi
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.minSeconds=60

   # TTL massimo in secondi (default: 604800 = 7 giorni)
   # Protezione per policy con intervalli molto lunghi
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.maxSeconds=604800

   # Rinnova TTL per policy CON intervallo temporale (es. 100 req/minuto)
   # Per queste policy, ad ogni finestra vengono creati nuovi contatori,
   # quindi rinnovare il TTL sui vecchi è inutile (default: false)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.renewOnWrite.intervalBased=false

   # Rinnova TTL per policy SENZA intervallo (es. richieste simultanee) o con
   # TTL troncato al massimo. In questi casi serve rinnovare per evitare
   # scadenze premature mentre il client è attivo (default: true)
   org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.REDIS.ttl.renewOnWrite.withoutInterval=true


*Impatto sulla memoria*

L'abilitazione del TTL può ridurre drasticamente l'utilizzo di memoria Redis in scenari ad alta cardinalità. Ogni clientId con una policy raggruppata occupa circa **450-500 byte** in Redis.

+-------------------------------+-------------------+--------------------+
| Scenario                      | Senza TTL         | Con TTL            |
+===============================+===================+====================+
| 1M clientId, 1 policy         | ~450 MB           | ~2.5 MB            |
| (5.000 client attivi/min)     |                   |                    |
+-------------------------------+-------------------+--------------------+
| 1M clientId, 5 policy         | ~2.2 GB           | ~12 MB             |
| (5.000 client attivi/min)     |                   |                    |
+-------------------------------+-------------------+--------------------+

La riduzione dipende dal rapporto tra client totali e client attivi nell'intervallo di TTL.
