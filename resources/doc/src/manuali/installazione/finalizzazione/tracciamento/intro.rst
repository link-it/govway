Configurazione del Tracciamento
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

GovWay registra le informazioni relative alle comunicazioni gestite attraverso un sistema di tracciamento altamente configurabile. Durante la fase di finalizzazione dell'installazione è fondamentale valutare attentamente le modalità di tracciamento da attivare, in quanto impattano sia sulle prestazioni del sistema che sui requisiti di storage.

Configurazione di Default
"""""""""""""""""""""""""

Il sistema viene distribuito con una configurazione di default che prevede:

- **Tracciamento su Database**: abilitato
- **Tracciamento su File**: disabilitato
- **Fase di Tracciamento Attiva**: "*Risposta consegnata*" (ultima fase, dopo l'inoltro della risposta al client)
- **Modalità**: non bloccante (gli errori di tracciamento non bloccano la transazione)

Questa configurazione rappresenta un buon compromesso tra completezza delle informazioni registrate e impatto sulle prestazioni, in quanto il tracciamento avviene dopo che la risposta è stata consegnata al client.

Fasi di Tracciamento
""""""""""""""""""""""

GovWay consente di configurare il tracciamento in diverse fasi del processamento di una richiesta, come descritto dettagliatamente nella sezione :ref:`tracciamentoTransazioniFasi` della documentazione della console:

1. **Richiesta ricevuta**: tracciamento prima di iniziare il processamento
2. **Richiesta in consegna**: tracciamento prima di inoltrare la richiesta al backend
3. **Risposta in consegna**: tracciamento prima di inoltrare la risposta al client
4. **Risposta consegnata**: tracciamento dopo aver inoltrato la risposta al client (**default attivo**)

.. note::
   L'attivazione di fasi di tracciamento anticipate (richiesta ricevuta, richiesta in consegna, risposta in consegna) aumenta la completezza delle informazioni registrate ma può impattare sulle prestazioni e sulla disponibilità del servizio se configurate in modalità bloccante.

Tracciamento su Database
""""""""""""""""""""""""

Il tracciamento su database, descritto nella sezione :ref:`tracciamentoTransazioniDB`, è la modalità principale per registrare le transazioni gestite dal gateway. Le informazioni vengono salvate in tabelle dedicate e sono consultabili tramite la console di monitoraggio (:ref:`mon_intro`).

**Processo di Failover**

Un aspetto critico del tracciamento su database è il **processo di failover** che si attiva automaticamente quando non è possibile registrare le tracce nel database (es. database non disponibile, connessione interrotta, problemi di rete). In questo caso la traccia viene serializzata su filesystem nella directory configurata (default: */var/govway/resources*) e un timer dedicato (default: ogni 5 minuti) tenta di recuperarla e riversarla nel database fino al numero massimo di tentativi (default: 10), dopo il quale viene spostata in una directory '*dlq*' (Dead Letter Queue).

Una volta risolto il problema del database, è possibile recuperare manualmente le tracce dalla DLQ come descritto nella sezione :ref:`tracciamentoInstallDLQ`.

.. warning::
   **Requisiti di Spazio Disco per il Failover**

   Il processo di failover è una soluzione temporanea per gestire l'indisponibilità del database e **richiede adeguato spazio disco**.

   Lo spazio necessario dipende da:

   - **Volume di traffico**: numero di richieste al secondo gestite dal gateway
   - **Dimensione delle tracce**: ogni traccia include metadati, header HTTP, contenuti dei messaggi (payload), informazioni di sicurezza
   - **Tempo di indisponibilità del database**: più a lungo il database rimane indisponibile, più tracce si accumulano su disco
   - **Numero di istanze**: in un cluster ogni nodo serializza le proprie tracce nella propria directory locale

   È **fondamentale** monitorare lo spazio disco della directory di failover e configurare alert per prevenire l'esaurimento dello spazio.

.. note::
   In ambienti cloud orchestrati (Kubernetes, OpenShift, etc.) è **fortemente consigliato** utilizzare un **volume persistente condiviso** tra i pod (es. NFS, AWS EFS, Azure Files) montato sulla directory `/var/log/govway/resources` per implementare efficacemente la funzionalità di failover.

**Recupero dalla Dead Letter Queue**

Per maggiori dettagli sul recupero manuale delle tracce dalla directory DLQ, consultare la sezione :ref:`tracciamentoInstallDLQ`.

Tracciamento su File
""""""""""""""""""""""

Il tracciamento su file, descritto nella sezione :ref:`tracciamentoTransazioniFileTrace`, consente di registrare le informazioni delle transazioni direttamente su file di log, facilitando l'integrazione con sistemi di log management esterni (FileBeat, Logstash, Fluentd, Kafka, Splunk, ...).

Questa modalità può essere utilizzata:

- **In alternativa al tracciamento su database**: per ridurre il carico sul database o per ambienti che preferiscono soluzioni basate su file
- **In aggiunta al tracciamento su database**: per avere una doppia registrazione delle informazioni

**Caratteristiche del Tracciamento su File**

- **Nessun failover**: le informazioni vengono scritte direttamente sui file configurati
- **Formato personalizzabile**: è possibile definire il formato, l'ordine e quali informazioni salvare
- **Multi-topic**: le informazioni possono essere suddivise in più file di log
- **Prestazioni**: generalmente più veloce del tracciamento su database

.. note::
   Anche per il tracciamento su file è importante:

   - Dimensionare adeguatamente lo spazio disco
   - Configurare la rotazione dei log (es. logrotate)
   - Monitorare lo spazio disco disponibile
   - Prevedere l'archiviazione o l'invio dei log a sistemi esterni

Accesso alla Configurazione
""""""""""""""""""""""^^^

Per configurare le modalità di tracciamento:

1. Accedere alla console di gestione GovWay (govwayConsole)
2. Navigare in *Configurazione > Tracciamento* (:ref:`tracciamento`)
3. Configurare le sezioni:

   - **Transazioni**: per configurare fasi di tracciamento e modalità (database/file)
   - **Messaggi Diagnostici**: per configurare il livello di verbosità dei log

.. note::
   Le configurazioni effettuate nella console hanno valenza globale. È possibile ridefinire puntualmente il comportamento su ogni singola erogazione/fruizione tramite la voce di configurazione *Tracciamento* del dettaglio API.

Monitoraggio e Manutenzione
""""""""""""""""""""""^^^

Dopo aver configurato il tracciamento, è importante monitorare:

1. **Spazio disco directory failover**: verificare regolarmente l'occupazione di */var/govway/resources*
2. **Directory dlq**: controllare periodicamente se sono presenti tracce che hanno superato il numero massimo di tentativi
3. **Log di GovWay**: verificare la presenza di errori di tracciamento
4. **Prestazioni database**: monitorare il carico sul database di tracciamento
5. **Crescita database**: pianificare politiche di archiviazione e retention
6. **Rotazione log**: verificare che la rotazione dei file di log sia configurata correttamente

Il sistema fornisce metriche e informazioni attraverso i log diagnostici configurabili nella stessa sezione *Configurazione > Tracciamento*.

.. toctree::
   :hidden:

   dlq
