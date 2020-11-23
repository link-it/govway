.. _profiloFatturaPA_attiva:

Fatturazione Attiva
-------------------

Nello scenario di fatturazione attiva si utilizza GovWay per l'invio
delle fatture al SdI. GovWay attua la codifica dei file ricevuti al fine
di produrre un messaggio valido per l'invio al SdI.

Lo scenario complessivo, relativo alla Fatturazione Attiva, è quello
illustrato in :numref:`scenarioFatturazioneAttiva`.

   .. figure:: ../../_figure_console/Govway-FatturazioneAttiva.jpg
    :scale: 100%
    :align: center
    :name: scenarioFatturazioneAttiva

    Scenario di interoperabilità relativo alla Fatturazione Attiva

Descriviamo per punti i passi significativi di questo scenario:

-  *Client Trasmissione Fatture*. I sistemi dell'ente possono
   trasmettere le fatture al SdI tramite un apposito servizio di
   ricezione di GovWay. La URL di invocazione di tale servizio sarà
   disponibile al termine del processo di configurazione dello scenario
   di fatturazione attiva descritto più avanti. Una volta ricevuta la
   fattura, nel formato previsto da FatturaPA, GovWay provvede a
   codificare il messaggio SdI di richiesta contenente la fattura da
   trasmettere. I metadati prodotti per il messaggio SdI, unitamente
   all'identificativo SdI, vengono restituiti all'applicativo mittente
   sotto forma di HTTP Headers (fare riferimento alla :numref:`headerTrasmissioneFattureTab`).

-  *Servizio Ricezione Notifiche*. I sistemi dell'ente devono esporre un
   servizio adibilito alla ricezione delle notifiche che il SdI invia
   successivamente alla trasmissione di una fattura. I riferimenti per
   l'accesso a tale servizio dovranno essere configurati nel contesto
   del *Connettore NotificheSDI*, presente nella configurazione di
   GovWay.

   GovWay consegna le notifiche, al servizio dell'ente, nel formato
   originale tramite una HTTP POST, includendo come HTTP Headers i
   metadati estratti dal messaggio SdI originariamente ricevuto (fare
   riferimento alla :numref:`headerRicezioneNotificheTab`).

.. table:: Header di Integrazione 'Trasmissione Fatture'
   :widths: auto
   :name: headerTrasmissioneFattureTab

   =========================================  ==============================================
   Header                                     Descrizione                                                                       
   =========================================  ==============================================
   GovWay-SDI-IdentificativoSdI               Identificativo assegnato dal SdI alla fattura         
   GovWay-SDI-NomeFile                        Nome del file fattura
   GovWay-Transaction-ID                      Identificativo della transazione assegnato da GovWay
   =========================================  ==============================================

.. table:: Header di Integrazione 'Ricezione Notifiche'
   :widths: auto
   :name: headerRicezioneNotificheTab

   =========================================  ==============================================
   Header                                     Descrizione                                                                       
   =========================================  ==============================================
   GovWay-SDI-IdentificativoSdI               Identificativo assegnato dal SdI alla fattura
   GovWay-SDI-NomeFile                        Nome del file fattura
   GovWay-Transaction-ID                      Identificativo della transazione assegnato da GovWay
   =========================================  ==============================================

Per produrre le configurazioni necessarie all'utilizzo dello scenario di
fatturazione attiva, è possibile utilizzare il wizard messo a
disposizione per semplificare l'attività di configurazione di GovWay. I
passi da eseguire sono i seguenti:

1. Scaricare il govlet per la fatturazione attiva al seguente indirizzo

   -  `<http://www.govway.org/govlets/fatturazione-attiva.zip>`__

2. Avviare il govlet posizionandosi sulla sezione *Configurazione >
   Importa* della GovWayConsole e selezionare il file appena scaricato
   come oggetto da importare.

3. *Soggetto SDI*: al primo step viene richiesto di indicare, tra gli
   elementi presenti nella lista a discesa, il soggetto interno mittente
   delle fatture. Si tratta di un soggetto appartenente al profilo
   "FatturaPA".

4. *Servizio SdIRiceviFile erogato dal Sistema di Interscambio*: al
   secondo step viene richiesto di indicare la URL che corrisponde
   all'endpoint del servizio SdIRiceviFile, erogato dal SdI per la
   trasmissione delle fatture.

.. note::
       il valore suggerito dalla maschera di configurazione del govlet
       fa riferimento alla url del sistema di produzione SDI. Se si
       vuole configurare un servizio di test è necessario cambiare tale
       valore ed impostare il riferimento all'ambiente di test SDI. I
       certificati, sia per l'ambiente di test che di produzione, devono
       essere stati inseriti nel truststore di GovWay.

5. *Credenziali per accesso URL RiceviFile*: al terzo step viene
   richiesto di fornire il criterio di autenticazione utilizzato
   dall'applicativo per invocare la url del GovWay per la trasmissione
   delle fatture.

6. *Applicativo per consegna Notifiche*: al quarto ed ultimo step viene
   richiesto di fornire i dati di configurazione del connettore,
   utilizzato da GovWay per la consegna delle notifiche inviate dal SdI,
   successivamente alla trasmissione di una determinata fattura. La
   configurazione del connettore comprende: endpoint, credenziali di
   autenticazione ed eventualmente i riferimenti del proxy.

.. toctree::
        :maxdepth: 2

	invioFatture
	ricezioneNotifiche
