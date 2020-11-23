.. _profiloFatturaPA:

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

Il profilo "Fatturazione Elettronica" consente, ai sistemi di gestione delle
fatture di un ente, di non occuparsi della gestione del formato di
scambio, previsto dal SdI, mantenendo un grado di interfacciamento
notevolmente semplificato. Più in dettaglio:

-  I gestionali dell'ente, registrati come applicativi su GovWay,
   inviano/ricevono le fatture e le notifiche, previste dal colloquio,
   nel formato originario XML senza ulteriori complessità.

-  I metadati presenti nelle comunicazioni con il SdI vengono estratti
   ed elaborati da GovWay e trasmessi ai gestionali dell'ente tramite
   appositi *Header di Integrazione SdI*.

-  La produzione dei metadati SdI, nel caso delle comunicazioni in
   uscita (fatturazione attiva), è a carico di GovWay che provvede anche
   a generare gli identificativi univoci da associare ai messaggi da
   trasmettere al SdI.

Per la produzione delle configurazioni necessarie a rendere operativo
GovWay sono stati realizzati due wizard che guidano l'utente verso il
corretto inserimento dei dati necessari. Gli scenari di configurazione
supportati sono due e riguardano i casi della *Fatturazione Passiva* e
*Fatturazione Attiva*.

.. toctree::
        :maxdepth: 2

        passiva/index
	attiva/index
