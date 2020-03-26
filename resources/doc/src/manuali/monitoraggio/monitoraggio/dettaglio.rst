.. _mon_dettaglio_transazione:

Dettaglio Transazione
~~~~~~~~~~~~~~~~~~~~~

Dal risutalto di una ricerca transazioni, sia in modalità "live" (:ref:`mon_live`) che
in modalità "storico" (:ref:`mon_transazioni`), cliccando su un qualunque elemento in elenco, si accede
alla pagina di dettaglio, organizzata quest'ultima in quattro distinte sezioni accessibili su altrettanti tab:

-  Informazioni Generali

-  Dettagli Messaggio

-  Diagnostici

-  Informazioni Avanzate

La sezione **Informazioni Generali** (:numref:`mon_DettaglioTransazioneInfo_fig`) mostra:

- le *Informazioni Generali*, come i riferimenti al servizio invocato, identificativo della transazione e l'esito.

- le *Informazioni Mittente*, quindi i dati di riferimento relativi alla provenienza della richiesta:

    -  Metodo HTTP: il metodo http relativo alla richiesta inviata dal mittente

    -  URL Invocazione: la url di invocazione utilizzata dal mittente per contattare il gateway

    -  Indirizzo Client: l'indirizzo di provenienza della richiesta pervenuta

    -  Codice Risposta Client: codice http restituito al mittente

    -  Applicativo Fruitore: identificativo dell'applicativo mittente

    -  Credenziali: Le credenziali utilizzate dall'applicativo per l'autenticazione

    -  X-Forwared-For: presente solamente se viene rilevato tra gli header http della richiesta un header tra i seguenti: 'X-Forwared-For', 'Forwared-For', 'Forwarded', 'X-Client-IP', 'Client-IP'

    -  Token Info: riporta il dettaglio delle informazioni estratte dal token ottenuto in fase di autenticazione della richiesta del mittente

.. figure:: ../_figure_monitoraggio/DettaglioTransazione_Info.png
    :scale: 100%
    :align: center
    :name: mon_DettaglioTransazioneInfo_fig

    Dettaglio Transazione: Informazioni Generali

La sezione **Dettagli Messaggio** (:numref:`mon_DettaglioTransazioneMessaggio_fig`) mostra:

- i *Dettagli Richiesta*: dati relativi al messaggio di richiesta come i timestamp di ingresso e uscita, le dimensioni del payload.

- i *Dettagli Risposta*: dati relativi al messaggio di risposta come i timestamp di ingresso e uscita e le dimensioni del payload.

.. figure:: ../_figure_monitoraggio/DettaglioTransazione_Messaggio.png
    :scale: 100%
    :align: center
    :name: mon_DettaglioTransazioneMessaggio_fig

    Dettaglio Transazione: Dettagli Messaggio

In questa sezione saranno presenti, quando previste, le tracce applicative dei messaggi di richiesta e risposta (:numref:`mon_Traccia_fig`).

.. figure:: ../_figure_monitoraggio/Traccia.png
    :scale: 100%
    :align: center
    :name: mon_Traccia_fig

    Dettaglio della traccia

Quando prevista la registrazione dei messaggi in configurazione, di richiesta e risposta, si
troveranno in questo riquadro i collegamenti per visualizzare:

-  Contenuti Ingresso/Uscita: i contenuti di entrata ed uscita sul
   gateway. Sia in entrata che uscita comprendono (in base al tipo di
   configurazione attiva):

   -  Il messaggio veicolato

   -  Gli eventuali attachment inclusi nel messaggio

   -  Gli header di trasporto relativi alla richiesta

-  Dati Raw Ingresso/Uscita: la versione raw dei contenuti transitati in
   ingresso/uscita sul gateway

Per tutte queste voci sono presenti i link *Esporta* che consentono di
salvare tali informazioni sul proprio filesystem.

.. figure:: ../_figure_monitoraggio/Contenuti.png
    :scale: 100%
    :align: center
    :name: mon_Contenuti_fig

    Visualizzazione contenuti in ingresso per una richiesta

Nei casi di esito "Fault Applicativo", cioè se il servizio erogatore ha
restituito un messaggio di fault, è possibile visualizzarne il contenuto
tramite il link Visualizza Fault (:numref:`mon_Fault_fig`).

.. figure:: ../_figure_monitoraggio/Fault.png
    :scale: 100%
    :align: center
    :name: mon_Fault_fig

    Dettaglio di un errore applicativo (fault)

La sezione **Diagnostici** (:numref:`mon_DettaglioTransazioneDiagnostici_fig`) mostra la sequenza
cronologica dei messaggi diagnostici emessi dal gateway, nel corso dell'elaborazione della transazione,
con la possibilità di effettuare un'esportazione degli stessi.

.. figure:: ../_figure_monitoraggio/DettaglioTransazione_Diagnostici.png
    :scale: 100%
    :align: center
    :name: mon_DettaglioTransazioneDiagnostici_fig

    Dettaglio dei messaggi diagnostici relativi ad una transazione

La sezione **Informazioni Avanzate** (:numref:`mon_DettaglioTransazioneAvanzate_fig`) riporta ulteriori dati della transazione
tra cui:

-  Dominio (ID e Soggetto): dominio del soggetto che ha gestito la
   transazione

-  Porta InBound o OutBound: indica il nome della porta del gateway invocata dal client (InBound nel caso di erogazione e OutBound per la fruizione)

-  Applicativo Fruitore o Erogatore: identità (se disponibile) dell'applicativo coinvolto nell'operazione

-  Connettore: specifica l'endpoint utilizzato per l'inoltro verso il
   dominio esterno (nel caso di fruizione)

-  Codice Risposta: il codice HTTP inviato con il messaggio di risposta

-  Latenza Totale, Servizio e Gateway: indica i tempi di elaborazione
   del messaggi

.. figure:: ../_figure_monitoraggio/DettaglioTransazione_Avanzate.png
    :scale: 100%
    :align: center
    :name: mon_DettaglioTransazioneAvanzate_fig

    Informazioni Avanzate di una Transazione
