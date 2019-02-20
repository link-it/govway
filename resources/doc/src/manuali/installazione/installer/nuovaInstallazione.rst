.. _inst_installer_nuova:

Nuova Installazione
-------------------

Supponiamo che la scelta sia quella di una nuova installazione. Vediamo
come si sviluppa il processo di installazione:

#. Si procede con l'inserimento delle *Informazioni Preliminari*, che
   prevede i seguenti dati: 
   
   .. figure:: ../_figure_installazione/installer-scr1.jpg
    :scale: 100%
    :align: center
   
    Informazioni Preliminari
   
   Operare le scelte sulla maschera di
   *Informazioni Preliminari* tenendo presente che:

   -  *Directory di lavoro*: una directory utilizzata da GovWay per
      inserire i diversi file di tracciamento prodotti. Non è necessario
      che questa directory esista sulla macchina dove si sta eseguendo
      l'installer; tale directory dovrà esistere nell'ambiente di
      esercizio dove verrà effettivamente installato il software GovWay.

   -  *Directory di log*: una directory utilizzata da GovWay per
      produrre i file di log. Non è necessario che questa directory
      esista sulla macchina dove si sta eseguendo l'installer; tale
      directory dovrà esistere nell'ambiente di esercizio dove verrà
      effettivamente installato il software GovWay.

   -  *DBMS*: il tipo di database scelto tra quelli supportati:
      PostgreSQL, MySQL, Oracle, HyperSQL, SQLServer, IBM DB2.

   -  *Application Server*: Application server utilizzato selezionato
      tra: WildFly (10.x, 11.x, 12.x, 13.x o 14.x) e Apache Tomcat (9.x,
      8.x, 7.x).

#. Al passo successivo si dovranno inserire tutti i dati per l'accesso
   al database ed in particolare:

   .. figure:: ../_figure_installazione/installer-scr2.jpg
    :scale: 100%
    :align: center

    Informazioni Accesso Database

   -  *Hostname*: indirizzo per raggiungere il database

   -  *Porta*: la porta da associare all'host per la connessione al
      database

   -  *Nome Database*: il nome dell'istanza del database a supporto di
      GovWay. Non è necessario che questo database esista in questa
      fase. Il database di GovWay infatti potrà essere creato nella fase
      successiva purché il nome assegnato coincida con il valore
      inserito in questo campo.

   -  *Username*: l'utente con diritti di lettura/scrittura sul database
      sopra indicato. Analogamente al punto precedente, l'utente potrà
      essere creato nella fase successiva dopo aver creato il database.
      Ricordarsi però di utilizzare il medesimo username indicato in
      questo campo.

   -  *Password*: la password dell'utente del database.

#. Il successivo passo richiede di stabilire le credenziali relative
   alle utenze di amministrazione per l'accesso ai cruscotti di
   gestione: 

   .. figure:: ../_figure_installazione/installer-scr3.jpg
    :scale: 100%
    :align: center

    Informazioni Utenza Amministratore

   I dati da inserire sono:

   -  *Username/Password* relativi all'utente amministratore della
      govwayConsole.

   -  *Username/Password* relativi all'utente operatore della
      govwayMonitor.

#. Al passo successivo si dovranno inserire i dati relativi ai profili
   di interoperabilità supportati dal gateway:

   .. _interop_fig:
   
   .. figure:: ../_figure_installazione/installer-scr4.jpg
    :scale: 100%
    :align: center

    Profili di Interoperabilità

   -  *Profilo*: contrassegnare con un flag i profili che saranno
      gestite da GovWay, scelti tra quelli offerti built-in dal
      prodotto:

      -  *API Gateway*

      -  *SPCoop*

      -  *eDelivery*

      -  *SdI (Fatturazione Elettronica)*

   -  *Soggetto*: nome del soggetto interno che verrà creato
      automaticamente.

#. Se si è scelto di includere la modalità eDelivery viene richiesto di
   immettere i relativi dati di configurazione. 

   .. figure:: ../_figure_installazione/installer-scr5.jpg
    :scale: 100%
    :align: center

    Configurazione eDelivery

   I dati di configurazione
   da immettere in questo step riguardano l'installazione di Domibus con
   la quale GovWay deve integrarsi per il dialogo con altri access point
   tramite il protocollo eDelivery. I dati richiesti sono:

   -  HTTP Endpoint: gli endpoint per contattare l'access point domibus
      interno

      -  Domibus MSH URL: endpoint pubblico per la raggiungibilità dagli
         altri access point

      -  Domibus Backend WS URL: endpoint dei servizi di backend che
         saranno utilizzati da GovWay per l'integrazione a Domibus

   -  Broker JMS: i dati di accesso al broker JMS utilizzato
      internamente da Domibus

      -  Provider URL: endpoint del Broker JMS

      -  Username/Password: credenziali per l'accesso ai servizi del
         Broker JMS

   -  DBMS

      -  Tipo: la piattaforma database utilizzata da Domibus.

#. Se si è scelto di includere il protocollo eDelivery, in aggiunta allo
   step precedente, verranno richiesti i dati di accesso al database
   utilizzato da Domibus:
   
   .. figure:: ../_figure_installazione/installer-scr6.jpg
    :scale: 100%
    :align: center

    Configurazione eDelivery (DBMS)

   -  *Hostname*: indirizzo per raggiungere il database

   -  *Porta*: la porta da associare all'host per la connessione al
      database

   -  *Nome Database*: il nome dell'istanza del database a supporto di
      Domibus.

   -  *Username*: l'utente con diritti di lettura/scrittura sul database
      sopra indicato.

   -  *Password*: la password dell'utente del database.

#. All'ultimo passo, premendo il pulsante *Install* il processo di
   configurazione si conclude con la produzione dei file necessari per
   l'installazione di GovWay che verranno inseriti nella nuova directory
   *dist* creata al termine di questo processo. 

   .. figure:: ../_figure_installazione/installer-scr8.jpg
    :scale: 100%
    :align: center

    Installazione

   I files presenti nella
   directory **dist** dovranno essere utilizzati nella fase successiva
   di dispiegamento di GovWay
