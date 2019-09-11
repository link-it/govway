.. _monitor_profiloFatturaPA_passiva:

Fatturazione Passiva
--------------------

Nello scenario di fatturazione passiva si utilizza GovWay per la
ricezione delle fatture in arrivo dal SdI. GovWay attua la decodifica
del messaggio SdI ricevuto, al fine di estrarre i file fattura in esso
contenuti e trasmetterli, nel formato FatturaPA, all'applicativo
registrato come destinatario.

Al fine di rendere più efficaci le attività di monitoraggio dei flussi di comunicazione relativi ai servizi di fatturazione, 
le configurazioni realizzate tramite i Govlet, possiedono attiva la correlazione applicativa per l'estrazione dell'identificativo SdI dalle chiamate in transito.
Grazie alla correlazione applicativa è possibile effettuare ricerche sulla console govwayMonitor, utilizzando l'identificativo SdI come filtro e visualizzare tale dato nel dettaglio delle transazioni.

Per effettuare una ricerca per identificativo SdI si utilizza la funzione di consultazione, della console govwayMonitor, alla sezione "Monitoraggio > Storico", selezionando l'opzione "Identificativo Applicativo". La ricerca si perfeziona inserendo il valore dell'identificativo SdI
da cercare nel campo "ID Applicativo" (:numref:`ElencoTransazioniIdSDI-FatturazionePassiva`).

   .. figure:: ../../_figure_monitoraggio/ElencoTransazioniIdSDI-FatturazionePassiva.jpg
    :scale: 100%
    :align: center
    :name: ElencoTransazioniIdSDI-FatturazionePassiva

    Fatturazione Passiva: elenco delle transazioni relative ad un identificativo SDI

.. note::

	**Ricezione delle Fatture/Notifiche e Invio dell'Esito Committente**

	Per ricercare la ricezione di fatture o notifiche, indicare all'interno della sezione 'Modalità Ricerca' il tipo 'Erogazione', mentre per ricercare gli esiti committente inviati si deve indicare il tipo 'Fruizione'.

Consultando il dettaglio di una delle transazioni (:numref:`DettaglioTransazioneIdSDI-FatturazionePassiva`) individuate è possibile vedere in ogni intestazione la presenza dell'identificativo SdI nella voce 'ID Applicativo Richiesta'. 

   .. figure:: ../../_figure_monitoraggio/DettaglioTransazioneIdSDI-FatturazionePassiva.jpg
    :scale: 100%
    :align: center
    :name: DettaglioTransazioneIdSDI-FatturazionePassiva

    Fatturazione Passiva: identificativo SDI associato ad ogni transazione

Cliccando sul dettaglio della Traccia di Richiesta di una fattura ricevuta è possibile ottenere maggiori informazioni riguardanti la fattura ricevuta (:numref:`InformazioniFatturazione-FatturazionePassiva`).

   .. figure:: ../../_figure_monitoraggio/InformazioniFatturazione-FatturazionePassiva.jpg
    :scale: 100%
    :align: center
    :name: InformazioniFatturazione-FatturazionePassiva

    Fatturazione Passiva: informazioni aggiuntive sulla Traccia inerenti la Fatturazione Elettronica

Cliccando sul dettaglio della Traccia di Risposta di una notifica esito committente inviata è possibile visualizzare tra le informazioni specifiche relative al profilo di Fatturazione Elettronica l'EsitoNotifica' ricevuto dallo SDI (ES00 = NOTIFICA NON ACCETTATA, ES01 = NOTIFICA ACCETTATA).
(:numref:`InformazioniFatturazione-FatturazionePassiva-esito`).

   .. figure:: ../../_figure_monitoraggio/InformazioniFatturazione-FatturazionePassiva-esito.jpg
    :scale: 100%
    :align: center
    :name: InformazioniFatturazione-FatturazionePassiva-esito

    Fatturazione Passiva: informazioni aggiuntive sulla Traccia di una notifica esito committente

Infine cliccando sul dettaglio della Traccia di Richiesta delle notifiche ricevute è possibile visualizzare, oltre alle informazioni inerenti la notifica ricevuta, ulteriori informazioni ottenute riconciliando la notifica con la precedente fattura tra cui il CodiceDestinatario a cui è destinata la fattura (:numref:`InformazioniFatturazione-FatturazionePassiva-notifica`).

   .. figure:: ../../_figure_monitoraggio/InformazioniFatturazione-FatturazionePassiva-notifica.jpg
    :scale: 100%
    :align: center
    :name: InformazioniFatturazione-FatturazionePassiva-notifica

    Fatturazione Passiva: informazioni aggiuntive sulla Traccia di una notifica ricevuta



