.. _profiloFatturaPA_passiva:

Fatturazione Passiva
--------------------

Nello scenario di fatturazione passiva si utilizza GovWay per la
ricezione delle fatture in arrivo dal SdI. GovWay attua la decodifica
del messaggio SdI ricevuto, al fine di estrarre i file fattura in esso
contenuti e trasmetterli, nel formato FatturaPA, all'applicativo
registrato come destinatario.

Lo scenario complessivo, relativo alla Fatturazione Passiva, è quello
illustrato in :numref:`scenarioFatturazionePassiva`.

   .. figure:: ../../_figure_console/Govway-FatturazionePassiva.jpg
    :scale: 100%
    :align: center
    :name: scenarioFatturazionePassiva

    Scenario di interoperabilità relativo alla Fatturazione Passiva

Descriviamo per punti i passi significativi di questo scenario:

-  *Servizio Ricezione Fatture*. Per consentire a GovWay di consegnare
   le fatture ricevute dal SdI è necessario esporre un servizio i cui
   riferimenti per l'accesso dovranno essere configurati nel contesto
   del *Connettore RicezioneFatture*, presente nella configurazione di
   GovWay.

   Le fatture vengono ricevute da GovWay formato codificato dal
   protocollo SdI, e comprendono il lotto delle fatture, con i relativi
   allegati, e un insieme di metadati che descrivono il contesto di
   invocazione. GovWay si occupa di estrarre le informazioni presenti,
   elaborando il messaggio SdI, provvedendo quindi a consegnare il lotto
   di fatture al servizio destinatario, nel formato *FatturaPA*
   attraverso l'invocazione di una HTTP POST. I metadati raccolti dal
   messaggio SdI vengono forniti, nel contesto della medesima richiesta,
   sotto forma di HTTP Headers (fare riferimento alla :numref:`headerRicezioneFatturaTab`).

.. note::
	Nella configurazione di default GovWay non consegna il file Metadati all'applicativo.
	È possibile attivare la consegna abilitando la proprietà 'org.openspcoop2.protocol.sdi.fatturazionePassiva.consegnaFileMetadati'	
	all'interno del file /etc/govway/sdi_local.properties.
	Il file Metadati verrà consegnato, codificato in base64, nell'header HTTP 'GovWay-SDI-FileMetadati'.

-  *Client Invio Notifica EC*. I sistemi dell'ente, dopo aver ricevuto
   le fatture, inviano le *Notifiche di Esito Committente*, previste dal
   protocollo SdI, utilizzando un apposito servizio di GovWay. La URL di
   invocazione di tale servizio sarà disponibile al termine del processo
   di configurazione descritto più avanti. GovWay provvede a codificare
   il messaggio SdI di richiesta contenente il messaggio di notifica
   ricevuto dall'applicativo mittente. I metadati prodotti per il
   messaggio SdI, unitamente all'identificativo messaggio univoco
   generato, vengono restituiti all'applicativo mittente sotto forma di
   HTTP Headers (fare riferimento alla :numref:`headerInvioNECTab`).

-  *Servizio Ricezione NDT*. Per consentire a GovWay di consegnare le
   eventuali *Notifiche di Decorrenza Termini* è necessario esporre un
   servizio i cui riferimenti per l'accesso dovranno essere configurati
   nel contesto del *Connettore NotificaDT*, presente nella
   configurazione di GovWay.

   GovWay consegna le notifiche DT nel formato originale tramite una
   HTTP POST, includendo come HTTP Headers i metadati estratti dal
   messaggio SdI originariamente ricevuto (fare riferimento alla :numref:`headerRicezioneNDTTab`).


.. table:: Header di Integrazione Ricezione Fattura
   :widths: auto
   :name: headerRicezioneFatturaTab

   =========================================  ==============================================
   Header                                     Descrizione                                                                       
   =========================================  ==============================================
   GovWay-SDI-FormatoArchivioBase64           Indica se il file fattura è codificato in formato Base64
   GovWay-SDI-FormatoArchivioInvioFattura     Indica se è stata utilizzata la modalità di firma CAdES o XAdES (P7M o XML)
   GovWay-SDI-FormatoFatturaPA                Indice di versione del formato FatturaPA adottato                             
   GovWay-SDI-IdentificativoSdI               Identificativo assegnato dal SdI alla fattura                                     
   GovWay-SDI-MessageId                       Identificativo assegnato alla fattura dall'ente trasmittente
   GovWay-SDI-NomeFile                        Nome del file fattura                                                             
   GovWay-SDI-NomeFileMetadati                Nome del file di metadati                                                        
   GovWay-Transaction-ID                      Identificativo della transazione assegnato da GovWay
   =========================================  ==============================================

.. table:: Header di Integrazione Invio Notifica EC
   :widths: auto
   :name: headerInvioNECTab

   =========================================  ==============================================
   Header                                     Descrizione                                                                       
   =========================================  ==============================================
   GovWay-Transaction-ID                      Identificativo della transazione assegnato da GovWay
   =========================================  ==============================================

.. table:: Header di Integrazione Ricezione Notifica DT
   :widths: auto
   :name: headerRicezioneNDTTab

   =========================================  ==============================================
   Header                                     Descrizione                                                                       
   =========================================  ==============================================
   GovWay-SDI-IdentificativoSdI               Identificativo assegnato dal SdI alla fattura
   GovWay-SDI-NomeFile                        Nome del file fattura
   GovWay-Transaction-ID                      Identificativo della transazione assegnato da GovWay
   =========================================  ==============================================

Per produrre le configurazioni necessarie all'utilizzo dello scenario di
fatturazione passiva, è possibile utilizzare il wizard messo a
disposizione per semplificare l'attività di configurazione di GovWay. I
passi da eseguire sono i seguenti:

1. Scaricare il govlet per la fatturazione passiva al seguente
   indirizzo:

   - http://www.govway.org/govlets/fatturazione-passiva.zip

2. Avviare il govlet posizionandosi sulla sezione *Configurazione >
   Importa* della GovWayConsole e selezionare il file appena scaricato
   come oggetto da importare.

3. *Soggetto SDI*: al primo step del wizard viene richiesto di indicare,
   tra gli elementi presenti nella lista a discesa, il soggetto interno
   destinatario delle fatture. Si tratta di un soggetto appartenente al
   profilo "FatturaPA".

4. *Servizio SdIRiceviNotifica erogato dal Sistema di Interscambio*: al
   secondo step viene richiesto di indicare la URL che corrisponde
   all'endpoint del servizio SdIRiceviNotifica, necessario per l'invio
   delle *Notifiche di Esito Committente*.

.. note::
       il valore suggerito dalla maschera di configurazione del govlet
       fa riferimento alla url del sistema di produzione SDI. Se si
       vuole configurare un servizio di test è necessario cambiare tale
       valore ed impostare il riferimento all'ambiente di test SDI. I
       certificati, sia per l'ambiente di test che di produzione, devono
       essere stati inseriti nel truststore di GovWay dopo averli
       prelevati all'indirizzo
       http://www.fatturapa.gov.it/export/fatturazione/it/normativa/f-3.htm

5. *Credenziali per accesso URL NotificaEsito*: al terzo step viene
   richiesto di fornire il criterio di autenticazione utilizzato
   dall'applicativo per inviare la notifica di esito committente.

6. *Applicativo per consegna FatturaPA*: al quarto step viene richiesto
   di fornire i dati di configurazione del connettore, utilizzato da
   GovWay per la consegna delle fatture. La configurazione del
   connettore comprende: endpoint, credenziali di autenticazione ed
   eventualmente i riferimenti del proxy.

7. *Applicativo per consegna NotificaDecorrenzaTermini*: al quinto ed
   ultimo step viene richiesto di fornire i dati di configurazione del
   connettore, utilizzato da GovWay per la consegna della notifica di
   decorrenza termini. La configurazione del connettore comprende:
   endpoint, credenziali di autenticazione ed eventualmente i
   riferimenti del proxy.

.. toctree::
        :maxdepth: 2

	ricezioneFatture
	invioNotifiche
