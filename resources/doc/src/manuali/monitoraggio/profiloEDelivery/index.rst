.. _monitor_profiloEdev:

======================
Profilo 'eDelivery'
======================

Il profilo eDelivery consente di produrre
configurazioni di scenari di interoperabilità che si basano sullo
standard europeo eDelivery. Per rendere il trattamento dei messaggi
conforme a tale standard, GovWay si interfaccia ad una installazione del
software Domibus
(https://ec.europa.eu/digital-building-blocks/sites/display/DIGITAL/Domibus).


La struttura complessiva del processo di monitoraggio si mantiene analoga a quanto già descritto per il profilo API Gateway. Le differenze, rispetto al profilo API Gateway, presentate in questa sezione, riguardano informazioni aggiuntive presenti nelle tracce di Richiesta o Risposta di una transazione.

All'interno di una traccia di richiesta, sia inerente una comunicazione in uscita dal dominio (Fruizione) che una comuncazione in ingresso nel dominio gestito (Erogazione), sono presenti ulteriori informazioni inerenti i dati del servizio e le parti che si sono scambiate il messaggio (figura :numref:`EDelivery-DettaglioTracciaRichiesta`).

   .. figure:: ../_figure_monitoraggio/EDelivery-DettaglioTracciaRichiesta.jpg
    :scale: 100%
    :align: center
    :name: EDelivery-DettaglioTracciaRichiesta

    eDelivery: informazioni aggiuntive sulla Traccia di Richiesta.

Inoltre in una transazione relativa ad una comunicazione in uscita dal dominio (Fruizione), all'interno della traccia di risposta è possibile visionare il riscontro ottenuto dalla controparte di avvenuta ricezione del messaggio inoltrato (figura :numref:`EDelivery-DettaglioTracciaRiscontro`).

   .. figure:: ../_figure_monitoraggio/EDelivery-DettaglioTracciaRiscontro.jpg
    :scale: 100%
    :align: center
    :name: EDelivery-DettaglioTracciaRiscontro

    eDelivery: informazioni sul Riscontro ricevuto in seguito ad un invio.
