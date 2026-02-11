.. _tracciamentoTransazioniDBSlowLog:

Slow Log
~~~~~~~~~

Il gateway può registrare nel file di log *govway_transazioni_slow.log* le operazioni di archiviazione su database che impiegano un tempo superiore a una soglia configurabile. Questa funzionalità è utile per diagnosticare problemi di performance del database durante il tracciamento delle transazioni.

Quando un'operazione di scrittura (insert o update sulla tabella *transazioni* e sulle tabelle correlate) supera la soglia configurata, viene registrata una entry nel log con i dettagli dell'operazione e il tempo impiegato.

La configurazione avviene nel file '/etc/govway/govway_local.properties'.

   ::

      # Abilita la registrazione delle operazioni lente sul file 'govway_transazioni_slow.log'.
      # Default: true
      org.openspcoop2.pdd.transazioni.slowLog.enabled=true

      # Soglia in millisecondi: le operazioni che impiegano un tempo superiore vengono registrate.
      # Default: 1000 (1 secondo)
      org.openspcoop2.pdd.transazioni.slowLog.thresholdMs=1000

Le proprietà seguenti consentono di includere nel log informazioni di dettaglio sulle singole fasi dell'archiviazione, utili per individuare quale specifica operazione contribuisce al rallentamento.

   ::

      # Abilita il dettaglio relativo alla costruzione dei dati della transazione.
      # Default: true
      org.openspcoop2.pdd.transazioni.slowLog.buildTransactionDetails.enabled=true

      # Abilita il dettaglio relativo alla verifica delle policy di rate limiting.
      # Default: true
      org.openspcoop2.pdd.transazioni.slowLog.rateLimitingDetails.enabled=true

      # Abilita il dettaglio relativo all'elaborazione dei connettori multipli
      # nella fase di processamento dei servizi applicativi.
      # Default: true
      org.openspcoop2.pdd.transazioni.slowLog.connettoriMultipli.processTransactionSADetails.enabled=true

      # Abilita il dettaglio relativo all'elaborazione dei connettori multipli
      # nella fase di aggiornamento della transazione.
      # Default: true
      org.openspcoop2.pdd.transazioni.slowLog.connettoriMultipli.updateTransactionDetails.enabled=true
