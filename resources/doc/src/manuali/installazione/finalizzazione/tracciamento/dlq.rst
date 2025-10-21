.. _tracciamentoInstallDLQ:

Recupero Tracce dalla Dead Letter Queue
----------------------------------------

Quando il processo di failover del tracciamento su database non riesce a riversare le tracce nel database dopo il numero massimo di tentativi configurati (default: 10), queste vengono automaticamente spostate in una directory speciale chiamata **Dead Letter Queue (DLQ)**.

Struttura Directory DLQ
^^^^^^^^^^^^^^^^^^^^^^^

La directory DLQ si trova all'interno della directory di failover configurata (default: */var/govway/resources*) con la struttura */var/govway/resources/TIPO/dlq/*, dove TIPO può essere: transazioni, diagnostici, messaggi, o altri tipi di informazioni tracciabili.

Verifica Presenza Tracce in DLQ
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Per verificare se sono presenti tracce in DLQ:

   ::

      # Conta tutti i file presenti nelle directory DLQ
      find /var/govway/resources/*/dlq/ -type f ! -name "*README" | wc -l

      # Lista le directory DLQ con tracce presenti
      for dir in /var/govway/resources/*/dlq/; do
          count=$(find "$dir" -type f ! -name "*README" 2>/dev/null | wc -l)
          [ $count -gt 0 ] && echo "$(basename $(dirname $dir)): $count file"
      done

Recupero Manuale delle Tracce
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Una volta risolto il problema che impediva la registrazione nel database, è possibile recuperare manualmente le tracce spostate in DLQ.

.. warning::
   Prima di procedere, verificare che:

   - Il database sia nuovamente disponibile e funzionante
   - Il timer di failover sia attivo (se presente la proprietà 'org.openspcoop2.pdd.resources.fileSystemRecovery.enabled' in govway_local.properties deve essere valorizzata a 'true')

**Procedura di Recupero**

1. Rimuovere i file README da tutte le directory DLQ:

   ::

      find /var/log/govway/resources/*/dlq/ -name "*README" -delete

2. Spostare tutte le tracce dalla DLQ alle directory di recovery:

   ::

      for tipo_dir in /var/log/govway/resources/*/; do
          tipo=$(basename "$tipo_dir")
          [ -d "${tipo_dir}dlq" ] && find "${tipo_dir}dlq/*/" -type f -exec mv {} "${tipo_dir}" \; 2>/dev/null
      done

3. Rimuovere le directory vuote:

   ::

      rmdir /var/log/govway/resources/*/dlq/*/ 2>/dev/null

Il timer di failover rileverà i file spostati e tenterà di riversarli nel database al prossimo ciclo (default: ogni 5 minuti).
