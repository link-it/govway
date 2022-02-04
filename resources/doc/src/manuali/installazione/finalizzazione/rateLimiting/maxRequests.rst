.. _maxRequests:

Numero Complessivo Richieste Simultanee
-------------------------------------------------------

GovWay consente di fissare un numero limite complessivo, indipendente
dalle singole APIs, riguardo alle richieste gestibili simultaneamente
dal gateway, bloccando le richieste in eccesso. Nell'installazione di
default tale limite è fissato a 200 richieste simultanee.

Per modificare la configurazione sul numero limite di richieste
simultanee accedere alla voce *'Configurazione - Controllo Traffico'*
del menù, sezione *'Limitazione Numero di Richieste Complessive'*.

    .. figure:: ../../_figure_installazione/govwayConsole_maxThreads.png
        :scale: 100%
        :align: center
	:name: inst_maxThreadsFig

        Numero Richieste Simultanee

Anche in presenza della policy, precedentemente descritta, si potrebbe rilevare un limite inferiore se a livello di application server esistono ulteriori limitazioni. Di seguito vengono fornite alcune indicazioni a riguardo.

**Tomcat**

L'application server, per default, limita il numero di richieste a 200.
Per poter modificare il limite si deve agire sull'attributo 'maxThreads' degli elementi 'connector' preseneti nella configurazione di Tomcat (es. in tomcat_home/conf/server.xml).

**WildFly**

L'application server, per default, limita il numero di worker threads tramite la formula 'cpuCount * 16' (es. https://docs.wildfly.org/26/wildscribe/subsystem/io/worker/index.html). 
Inoltre i worker threads sono condivisi da tutti gli http-listener che per default utilizzando il worker 'default' (es. https://docs.wildfly.org/26/wildscribe/subsystem/undertow/server/http-listener/index.html).

Per modificare il livello di soglia e configurare per ogni http-listener un pool di threads dedicato si deve agire sulla configurazione di WildFly (es. in standalone/configuration/standalone.xml) prima creando un worker che definisce il pool di thread.

   ::

        <subsystem xmlns="urn:jboss:domain:io:...">
            <worker name="default"/>
            <worker name="customWorker" task-max-threads="200"/>
            ...
        </subsystem>

Si deve poi effettuare l'associazione definendo nell'elemento 'http-listener' l'attributo 'worker'.

   ::

       <server name="default-server">
                <http-listener name="default" socket-binding="http" worker="customWorker" ... />
		...
       </server>

