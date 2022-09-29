.. _console_profiliInteroperabilita:

I Profili di Interoperabilità
-----------------------------

GovWay si differenzia dagli API Gateway tradizionali per essere
progettato in conformità con i principali profili di interoperabilità in
uso nella Pubblica Amministrazione italiana ed europea. Per tale motivo,
le modalità di configurazione del prodotto si differenziano in funzione
dello specifico profilo a cui le API debbano conformarsi. I profili di
interoperabilità supportati dalla distribuzione standard del prodotto
sono i seguenti:

-  *API Gateway*: è il profilo di interoperabilità di base che consente di supportare qualunque
   generica API basata su scambio di messaggi SOAP e REST.

-  *ModI*: è il profilo che consente di supportare gli scenari di comunicazione basati sul Modello di Interoperabilità rilasciato da AGID, che fornisce i requisiti per l'integrazione tra il sistema informativo complessivo della Pubblica Amministrazione, Cittadini e Imprese.

-  *eDelivery*: è il profilo standard adottato a livello europeo
   nell'ambito del progetto *CEF*, e basato sul protocollo AS4.

-  *SPCoop*: il profilo SPCoop è il profilo basato sull'uso della busta
   eGov e sulla Porta di Dominio, recentemente deprecato da AGID, ma
   ancora in uso per la quasi totalità dei servizi centrali erogati
   dalla Pubblica Amministrazione italiana.

-  *Fatturazione Elettronica*: questo profilo supporta le modalità di scambio delle
   fatture elettroniche, nel formato FatturaPA, veicolate tramite il Sistema di Interscambio.

In fase di installazione possono essere scelti i profili di proprio
interesse (per default viene proposto il solo profilo di API Gateway).

Durante l'utilizzo della Console di Gestione è preferibile selezionare
il profilo di interoperabilità adeguato in base al tipo di
configurazioni sui quali si lavora. La selezione del profilo di
interoperabilità, tramite il menù presente in testata (:numref:`profilo`), comporta la
visualizzazione dei soli elementi dell'interfaccia, e relativi dati,
attinenti con tale profilo.

   .. figure:: ../_figure_console/profiloInteroperabilita.png
    :scale: 50%
    :align: center
    :name: profilo

    Selezione del profilo di interoperabilità

.. note::
    La selezione del profilo tramite il menù presente in testata non è persistente e al successivo login verrà nuovamente presentato il profilo di interoperabiltà di default associato al profilo utente. Per modificarlo si rimanda alla sezione :ref:`console_utente_profilo`.

Esiste la possibilità (non consigliata) di operare sulla console
selezionando il profilo *Tutti*. In tal caso non saranno applicati
filtri sui contenuti e le maschere di visualizzazione e di
configurazione potranno apparire più complesse di quanto avviene
selezionando lo specifico profilo su cui si sta lavorando.

.. note::
    Ulteriori profili sono programmabili in GovWay ed alcuni di questi
    sono in uso in importanti progetti della pubblica amministrazione,
    come la Porta di Comunicazione del Sistema di Interscambio del
    Mercato dell'Energia.
