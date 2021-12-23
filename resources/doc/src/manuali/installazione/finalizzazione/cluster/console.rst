.. _cluster-console:

Configurazione delle Console
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La configurazione del Load Balancing si completa fornendo ulteriori dati
di configurazione alle console grafiche. Queste configurazioni
consentono alle console di avere i corretti riferimenti ai nodi presenti
in modo da poter dettagliare questo aspetto nelle proprie maschere ed
inoltre poter propagare eventuali modifiche su ogni nodo senza attendere
il timeout della cache o richiedere riavvii dell'AS.

A tale scopo sarà necessario:

#. Editare il file *<directory-lavoro>/govway_local.properties*
   aggiungendo le seguenti righe su ogni GovWay in Load Balancing:

   ::

      # JMX Resources
      org.openspcoop2.pdd.check.readJMXResources.enabled=true
      org.openspcoop2.pdd.check.readJMXResources.username=#USERNAME#
      org.openspcoop2.pdd.check.readJMXResources.password=#PASSWORD#
                          

   inserendo al posto di #USERNAME# e #PASSWORD# le credenziali che
   dovranno essere utilizzate dalle console e che dovranno essere
   configurate nei punti successivi di questo paragrafo.

#. Editare il file *<directory-lavoro>/govway.nodirun.properties*

   Disabilitare la configurazione per la singola istanza commentando la
   proprietà 'remoteAccess.checkStatus.url':

   ::

      # Configurazione in Singola Istanza
      #remoteAccess.checkStatus.url=http://127.0.0.1:8080/govway/check

   Abilitare la configurazione della gestione in Load Balancing scommentando le seguenti righe:

   ::

      # Configurazione in Load Balancing
      tipoAccesso=govway
      aliases=#IDGW1#,..,#IDGWN#                        
                              

   Devono essere elencati tutti gli identificativi, di ogni nodo gateway
   in Load Balancing, descritti in precedenza e registrati nella
   proprietà:

   ::

      org.openspcoop2.pdd.cluster_id del file govway_local.properties

   Per ogni identificativo devono inoltre essere fornite le seguenti
   informazioni:

   ::

      # Configurazione IDGW1
      #IDGW1#.descrizione=#DESCRIZIONEGW1#
      #IDGW1#.remoteAccess.url=http://#HOSTGW1#:#PORTGW1#/govway/check
      #IDGW1#.remoteAccess.username=#USERNAMEGW1#
      #IDGW1#.remoteAccess.password=#PASSWORDGW1#
      ...
      # Configurazione IDGWN
      #IDGWN#.descrizione=#DESCRIZIONEGWN#
      #IDGWN#.remoteAccess.url=http://#HOSTGWN#:#PORTGWN#/govway/check
      #IDGWN#.remoteAccess.username=#USERNAMEGWN#
      #IDGWN#.remoteAccess.password=#PASSWORDGWN#
                              

   Devono essere elencati inserendo al posto di #USERNAMEGW# e
   #PASSWORDGW# le credenziali utilizzate in precedenza nel file:

   ::

      govway_local.properties, proprietà
      org.openspcoop2.pdd.check.readJMXResources.username e
      org.openspcoop2.pdd.check.readJMXResources.password

   Indicare inoltre al posto di #HOSTGW# e #PORTGW# l'hostname e la
   porta con cui è raggiungibile GovWay. Infine deve anche essere
   fornita una descrizione per ogni nodo in Load Balancing al posto di
   #DESCRIZIONEGW#.

.. note::
   Per mantenere una retrocompatibilità con le configurazioni descritte nelle precedenti versioni e attuate sui file *<directory-lavoro>/console_local.properties* e *<directory-lavoro>/monitor_local.properties*, le console utilizzeranno tali configurazioni se non riscontrano la presenza del nuovo file *<directory-lavoro>/govway.nodirun.properties*.

**Configurazione HTTPS**

È possibile configurare un accesso ad una url https tramite le seguenti proprietà aggiuntive, definendo il truststore da utilizzare per verificare il certificato ritornato dal server:

   ::

      # Esempio per nodo IDGWX di un accesso tramite connettore https
      #IDGWX.remoteAccess.https=true
      #IDGWX.remoteAccess.https.verificaHostName=true
      #IDGWX.remoteAccess.https.autenticazioneServer=true
      #IDGWX.remoteAccess.https.autenticazioneServer.truststorePath=PATH
      #IDGWX.remoteAccess.https.autenticazioneServer.truststoreType=jks
      #IDGWX.remoteAccess.https.autenticazioneServer.truststorePassword=PASSWORD

Disabilitando l'autenticazione server, non sarà invece necessario definire un truststore ma verrà accettato qualsiasi certificato server (insecure):

   ::

      # Esempio per nodo IDGWX di un accesso tramite connettore https
      #IDGWX.remoteAccess.https=true
      #IDGWX.remoteAccess.https.verificaHostName=true
      #IDGWX.remoteAccess.https.autenticazioneServer=false

Le proprietà suddette, oltre a poter essere definite per ogni nodo possono anche essere configurate una volta sola eliminando il prefisso che identifica un nodo. Ad esempio:

   ::

      # Esempio di un accesso tramite connettore https valido per tutti i nodi
      remoteAccess.https=true
      remoteAccess.https.verificaHostName=true
      remoteAccess.https.autenticazioneServer=false

**Configurazione Timeout**

È possibile configurare i parametri di timeout (valori in millisecondi) agendo sulle seguenti proprietà:

   ::

      #IDGW1.remoteAccess.readConnectionTimeout=5000
      #IDGW1.remoteAccess.connectionTimeout=5000

**Gruppi di Nodi (govwayConsole)**

La console di gestione consente, nella sezione 'Runtime', di svuotare le cache di tutti i nodi tramite un'unica operazione. Per attuare un comportamento simile ma limitato ad un gruppo di nodi è possibile configurare le seguenti proprietà classificando i nodi in gruppi:

   ::

      # Classificazione dei nodi in gruppi
      aliases.<idGruppo1>=#IDGW1,#IDGW2
      aliases.<idGruppo2>=#IDGW2,#IDGWN

**Configurazione Avanzata delle Sonde (govwayMonitor)**

La console di monitoraggio invoca periodicamente un servizio 'sonda' di ogni nodo registrato per verificarne il corretto funzionamento. Per default la url invocata è quella configurata nella proprietà '#IDGWN#.remoteAccess.url' descritta in precedenza. È possibile far utilizzare alla console di monitoraggio una url differente aggiungendo al file *<directory-lavoro>/govway.nodirun.properties* la seguente configurazione aggiuntiva:
   
   ::

      # Configurazione IDGW1
      #IDGW1#.remoteAccess.checkStatus.url=http://#HOSTGW1#:#PORTGW1#/govway/check
      ...
      # Configurazione IDGWN
      #IDGWN#.remoteAccess.checkStatus.url=http://#HOSTGWN#:#PORTGWN#/govway/check

È inoltre possibile elencare un numero di nodi differenti aggiungendo nel file *<directory-lavoro>/monitor_local.properties* la seguente proprietà:

   ::

      # Configurazione in Load Balancing
      statoPdD.sonde.standard.nodi=IDGW1,..,IDGWN


