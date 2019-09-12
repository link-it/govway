.. _monitor_profiloAPIGateway_pagoPA:

Pago PA
--------

Il Govlet "pagoPA" è un wizard di configurazione per creare, nella maniera più rapida, le entità del registro di Govway per rendere operativi i flussi di pagamento con la piattaforma pagoPA.

Al fine di rendere più efficaci le attività di monitoraggio dei flussi di comunicazione le configurazioni prodotte prevedono l'attivazione della correlazione applicativa, utilizzata per arricchire i dati di tracciamento, con i riferimenti specifici del dominio pagoPA, associati ai flussi in transito. In tal modo sarà possibile effettuare ricerca e monitoraggio dei flussi gestiti da GovWay sulla base di tali informazioni. 

La seguente tabella indica, per ciascun servizio configurato, quali sono gli identificativi di correlazione estratti:

.. table:: Identificativi PagoPA estratti
   :widths: 35 65
   :class: longtable
   :name: IdentificativoPagoPA

   ============================    ======================   ==============
   Servizio                        Azione                   Identificativo
   ============================    ======================   ==============
   PagamentiTelematiciRPT          nodoInviaRPT             identificativoDominio, identificativoUnivocoVersamento e codiceContestoPagamento
   PagamentiTelematiciRPT          nodoInviaCarrelloRPT     identificativoCarrello e dimensione del carrello.  Per il primo elemento della lista carrello: IdentificativoDominio, identificativoUnivocoVersamento e codiceContestoPagamento
   PagamentiTelematiciRT           paaInviaRT               identificativoDominio, identificativoUnivocoVersamento e codiceContestoPagamento
   PagamentiTelematiciCCP          \*                       identificativoDominio, identificativoUnivocoVersamento e codiceContestoPagamento
   GenerazioneAvvisi               \*                       identificativoDominio e identificativoUnivocoVersamento
   ChiediElencoAvvisiDigitali      \*                       codiceFiscaleDebitore
   NodoInviaAvvisoDigitale         \*                       codiceAvviso
   ============================    ======================   ==============

Ad esempio, per effettuare una ricerca per IUV, si utilizza la funzione di consultazione della console govwayMonitor, alla sezione "Monitoraggio > Storico", selezionando l'opzione "Identificativo Applicativo". La ricerca si perfeziona inserendo il codice IUV da cercare nel campo "ID Applicativo", specificando che si tratta di una ricerca non esatta, poiché l'identificativo estratto è la concatenazione di tre valori distinti (:numref:`PagoPA-Ricerca`).

   .. figure:: ../_figure_monitoraggio/PagoPA-Ricerca.jpg
    :scale: 100%
    :align: center
    :name: PagoPA-Ricerca

    Pago PA: elenco delle transazioni relative ad un identificativo IUV

Consultando il dettaglio della transazione è possibile visualizzare l'intero identificativo estratto, tra le proprietà dell'elemento (:numref:`PagoPA-Dettaglio`).

   .. figure:: ../_figure_monitoraggio/PagoPA-Dettaglio.jpg
    :scale: 100%
    :align: center
    :name: PagoPA-Dettaglio

    Pago PA: identificativo IUV associato ad ogni transazione
