.. _monitor_profiloFatturaPA_attiva:

Fatturazione Attiva
-------------------

Nello scenario di fatturazione attiva si utilizza GovWay per l'invio
delle fatture al SdI. 

Al fine di rendere più efficaci le attività di monitoraggio dei flussi di comunicazione relativi ai servizi di fatturazione, 
le configurazioni realizzate tramite i Govlet, possiedono attiva la correlazione applicativa per l'estrazione dell'identificativo SdI dalle chiamate in transito.
Grazie alla correlazione applicativa è possibile effettuare ricerche sulla console govwayMonitor, utilizzando l'identificativo SdI come filtro e visualizzare tale dato nel dettaglio delle transazioni.

Per effettuare una ricerca per identificativo SdI si utilizza la funzione di consultazione, della console govwayMonitor, alla sezione "Monitoraggio > Storico", selezionando l'opzione "Identificativo Applicativo". La ricerca si perfeziona inserendo il valore dell'identificativo SdI
da cercare nel campo "ID Applicativo" (:numref:`ElencoTransazioniIdSDI-FatturazioneAttiva`).

   .. figure:: ../../_figure_monitoraggio/ElencoTransazioniIdSDI-FatturazioneAttiva.jpg
    :scale: 100%
    :align: center
    :name: ElencoTransazioniIdSDI-FatturazioneAttiva

    Fatturazione Attiva: elenco delle transazioni relative ad un identificativo SDI

.. note::

	**Invio delle Fatture e Ricezione delle Notifiche**

	Per ricercare l'invio di fatture, indicare all'interno della sezione 'Modalità Ricerca' il tipo 'Fruizione', mentre per ricercare le notifiche ricevute si deve indicare il tipo 'Erogazione'.

Consultando il dettaglio di una delle transazioni (:numref:`DettaglioTransazioneIdSDI-FatturazioneAttiva`) individuate è possibile vedere in ogni intestazione la presenza dell'identificativo SdI nella voce 'ID Applicativo Risposta' per le fatture inviate e nella voce 'ID Applicativo Richiesta' per le notifiche ricevute. 

   .. figure:: ../../_figure_monitoraggio/DettaglioTransazioneIdSDI-FatturazioneAttiva.jpg
    :scale: 100%
    :align: center
    :name: DettaglioTransazioneIdSDI-FatturazioneAttiva

    Fatturazione Attiva: identificativo SDI associato ad ogni transazione

Cliccando sul dettaglio della Traccia di Richiesta di una fattura inviata è possibile ottenere maggiori informazioni riguardanti la fattura inviata (:numref:`InformazioniFatturazione-FatturazioneAttiva-invioRichiesta`).

   .. figure:: ../../_figure_monitoraggio/InformazioniFatturazione-FatturazioneAttiva-invioRichiesta.jpg
    :scale: 100%
    :align: center
    :name: InformazioniFatturazione-FatturazioneAttiva-invioRichiesta

    Fatturazione Attiva: informazioni aggiuntive sulla Traccia di una Fattura Elettronica inviata

Cliccando sul dettaglio della Traccia di Risposta di una fattura inviata è possibile invece visualizzare tra le informazioni specifiche relative al profilo di Fatturazione Elettronica la 'DataOraRicezione' della fattura e l'identificativo assegnato dallo SDI (:numref:`InformazioniFatturazione-FatturazioneAttiva-invioRisposta`).

   .. figure:: ../../_figure_monitoraggio/InformazioniFatturazione-FatturazioneAttiva-invioRisposta.jpg
    :scale: 100%
    :align: center
    :name: InformazioniFatturazione-FatturazioneAttiva-invioRisposta

    Fatturazione Attiva: informazioni aggiuntive sulla Traccia ricevute dallo SDI

Infine cliccando sul dettaglio della Traccia di Richiesta delle notifiche ricevute è possibile visualizzare, oltre alle informazioni inerenti la notifica ricevuta, ulteriori informazioni ottenute riconciliando la notifica con la precedente fattura: l'IdTrasmittente (IdPaese + IdCodice) e l'Applicativo che ha inviato la fattura (:numref:`InformazioniFatturazione-FatturazioneAttiva-notifica`).

   .. figure:: ../../_figure_monitoraggio/InformazioniFatturazione-FatturazioneAttiva-notifica.jpg
    :scale: 100%
    :align: center
    :name: InformazioniFatturazione-FatturazioneAttiva-notifica

    Fatturazione Attiva: informazioni aggiuntive sulla Traccia di una notifica ricevuta
