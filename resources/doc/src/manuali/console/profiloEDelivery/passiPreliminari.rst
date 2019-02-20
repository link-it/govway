.. _edelivery_passiPreliminari:

Passi preliminari di configurazione
-----------------------------------

Per gestire in maniera più semplice i passi di configurazione dei
servizi eDelivery è consigliabile impostare l'opportuna modalità
operativa della govwayConsole selezionando la voce *eDelivery* sul
selettore di modalità presente nella testata dell'applicazione.

Prima di procedere con la configurazione dei servizi si devono
verificare i dati relativi ai soggetti interlocutori. Nel caso del
soggetto interno al proprio dominio, i dati di configurazione possono
essere gestiti alla sezione *Configurazione > Generale* (:numref:`baseUrlEdelivery`).

   .. figure:: ../_figure_console/edev-BaseURL-SoggettoInterno.png
    :scale: 100%
    :align: center
    :name: baseUrlEdelivery

    Configurazione delle Base URL eDelivery per il soggetto interno

Sono presenti valori iniziali, inseriti dal processo di installazione,
che devono essere verificati ed eventualmente aggiornati:

-  *Base URL Erogazione*: Indirizzo pubblico del Domibus per la
   ricezione dei messaggi sul canale eDelivery.

-  *Base URL Fruizione*: Indirizzo del servizio di GovWay riservato ai
   client per l'invio di messaggi sul canale eDelivery.

Tramite il collegamento *Visualizza Dati Soggetto* è possibile accedere
alla conffigurazione del soggetto interno (:numref:`soggettoEdelivery`).

   .. figure:: ../_figure_console/edev-DatiSoggettoInterno.png
    :scale: 100%
    :align: center
    :name: soggettoEdelivery

    Configurazione delle proprietà eDelivery per il soggetto interno

Le proprietà eDelivery da fornire sono le seguenti:

-  *Party Info - Id*: Identificativo del soggetto utilizzato nel canale
   eDelivery.

-  *Party Info - Type Name*: Nome assegnato internamente allo schema
   indicato nel Type Value.

-  *Party Info - Type Value*: Schema di generazione riferito
   all'identificativo del soggetto eDelivery.

-  *Party Endpoint - URL*: Indirizzo pubblico del Domibus per la
   ricezione dei messaggi sul canale eDelivery.

-  *Party Endpoint - Common Name*: Valore della omonima proprietà del
   certificato utilizzato dall'access point Domibus cui afferisce.
   Questo nome coincide con quello dell'access point.
