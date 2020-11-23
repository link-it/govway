.. _monitor_profiloFatturaPA:

==================================
Profilo 'Fatturazione Elettronica'
==================================

Il profilo "Fatturazione Elettronica" consente di utilizzare GovWay come nodo di
interconnessione al Sistema di Interscambio (SdI), responsabile della
gestione dei flussi di fatturazione elettronica.

GovWay supporta la connessione al SdI attraverso lo scenario di
interoperabilità su rete Internet basato sull'accesso al servizio
*SdICoop*. Il servizio SdICoop prevede un protocollo di comunicazione,
basato su SOAP, che veicola messaggi (fatture, archivi, notifiche e
metadati) secondo la codifica dettata dalle specifiche tecniche (Per
dettagli in merito si faccia riferimento alle Specifiche Tecniche
SdI (https://www.fatturapa.gov.it/it/norme-e-regole/DocumentazioneSDI/).

Gli scenari supportati sono due e riguardano i casi della *Fatturazione Passiva* e
*Fatturazione Attiva*. 

La struttura complessiva del processo di monitoraggio si mantiene analoga a quanto già descritto per il profilo API Gateway. Le differenze, con rispetto al profilo API Gateway, presentate in questa sezione, riguardano informazioni aggiuntive presenti nelle tracce e ricerche per Identificativo SDI.

.. toctree::
        :maxdepth: 2

        passiva/index
	attiva/index
