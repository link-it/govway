.. _processoConfigurazione:

Il processo di configurazione dei servizi
-----------------------------------------

Le sezioni successive del documento illustrano i passi necessari per
realizzare le configurazioni necessarie per rendere operativi i flussi
di erogazione/fruizione dei servizi nei diversi profili di
interoperabilità supportati.

Per semplificare il processo di configurazione, nel caso di
configurazioni per l'interoperabilità con le note piattaforme di
erogazione di servizi centralizzate, GovWay mette a disposizione
specifici package, denominati *Govlet*. Il Govlet, attraverso un modello
di tipo wizard, consente all'utente di fornire i dati necessari a
produrre le entità di configurazione per uno specifico servizio. I
Govlet disponibili possono essere acquisiti dal sito di Govway al
seguente indirizzo http://www.govway.org/govlets. Alcuni esempi di
Govlet:

-  *FatturaPA - Fatturazione Attiva*: configurazione del servizio per
   l'invio di fatture elettroniche al Sistema d'Interscambio (SDI).

-  *FatturaPA - Fatturazione Passiva*: configurazione del servizio per
   la ricezione di fatture elettroniche dal Sistema d'Interscambio
   (SDI).

-  *SIOPE+*: configurazione del servizio per l'invio degli ordinativi di
   pagamento alla piattaforma SIOPE+ e ricezione delle relative
   notifiche e giornale di cassa.

-  *pagoPA*: configurazione del servizio per l'accesso alla piattaforma
   dei pagamenti elettronici pagoPA.

Una volta entrati in possesso del Govlet è necessario eseguirlo sulla
govwayConsole tramite la funzione *Importa* descritta nella sezione :ref:`importa`.

Per procedere manualmente alla produzione delle configurazioni per i
servizi, si utilizzano le funzionalità presenti nella sezione *Registro*
della GovWayConsole. Il processo manuale di configurazione può essere
schematizzato nei passi seguenti:

1. *Definizione delle API*. Il primo passo prevede la definizione delle
   API relative ai servizi che si vogliono utilizzare. In questa fase
   tipicamente si provvede al caricamento del descrittore formale delle
   interfacce (WSDL, WADL, ...).

2. *Registrazione dell'erogazione o fruizione*. Il secondo passo, dopo
   aver registrato l'API del servizio, prevede la creazione di una
   Erogazione, o di una Fruizione, a seconda del ruolo previsto
   nell'interazione col servizio.

3. *Configurazione Specifica*. Le interfacce della GovWayConsole sono
   state progettate in modo che, il completamento dei primi due passi di
   configurazione, sia sufficiente a disporre di una configurazione
   funzionante del servizio. Il terzo, e quindi opzionale passo,
   consiste nella produzione di tutti i dettaglio aggiuntivi di
   configurazione che sono necessari alla particolare situazione.

   In questo passo si forniscono i dettagli delle funzionalità
   aggiuntive, che riguardano:

   -  *Controllo degli Accessi*: indicazione dei criteri di
      autenticazione e autorizzazione necessari per l'accesso al
      servizio.

   -  *Validazione*: processo di validazione dei messaggi in transito
      sul gateway.

   -  *Sicurezza Messaggio*: misure di sicurezza al livello del
      messaggio richieste.

   -  *Tracciamento*: personalizzazione delle tracce prodotte nel corso
      dell'elaborazione delle richieste di servizio.

   -  *Registrazione Messaggi*: indicazione dei criteri di salvataggio
      degli elementi che compongono le richieste di servizio (payload,
      header, allegati, ...).

La :numref:`console_scenarioGovWay` descrive lo scenario generale in cui opera GovWay.

   .. figure:: ../_figure_console/ArchitetturaFunzionaleGovWay.png
    :scale: 100%
    :align: center
    :name: console_scenarioGovWay

    Scenario Generale


La sezioni successive descrivono in dettaglio il processo di
configurazione di cui sopra, fornendo i dettagli specifici per ciascun
profilo di interoperabilità.

