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

#. Editare il file <directory-lavoro>/govway_local.properties
   aggiungendo le seguenti righe su ogni PdD in Load Balancing:

   ::

      # JMX Resources
      org.openspcoop2.pdd.check.readJMXResources.enabled=true
      org.openspcoop2.pdd.check.readJMXResources.username=#USERNAME#
      org.openspcoop2.pdd.check.readJMXResources.password=#PASSWORD#
                          

   inserendo al posto di #USERNAME# e #PASSWORD# le credenziali che
   dovranno essere utilizzate dalle console e che dovranno essere
   configurate nei punti successivi di questo paragrafo.

#. Editare il file <directory-lavoro>/console_local.properties
   aggiungendo le seguenti righe al fine di configurare la
   govwayConsole:

   ::

      # Configurazione gateway in Load Balancing
      risorseJmxPdd.tipoAccesso=openspcoop
      risorseJmxPdd.aliases=#IDGW1#,..,#IDGWN#                        
                              

   Devono essere elencati tutti gli identificativi, di ogni nodo gateway
   in Load Balancing, descritti in precedenza e registrati nella
   proprietà:

   ::

      org.openspcoop2.pdd.cluster_id del file govway_local.properties

   Per ogni identificativo devono inoltre essere fornite le seguenti
   informazioni:

   ::

      # Configurazione IDGW1
      #IDGW1#.risorseJmxPdd.descrizione=#DESCRIZIONEGW1#
      #IDGW1#.risorseJmxPdd.remoteAccess.url=http://#HOSTGW1#:#PORTGW1#/govway/check
      #IDGW1#.risorseJmxPdd.remoteAccess.username=#USERNAMEGW1#
      #IDGW1#.risorseJmxPdd.remoteAccess.password=#PASSWORDGW1#
      ...
      # Configurazione IDGWN
      #IDGWN#.risorseJmxPdd.descrizione=#DESCRIZIONEGWN#
      #IDGWN#.risorseJmxPdd.remoteAccess.url=http://#HOSTGWN#:#PORTGWN#/govway/check
      #IDGWN#.risorseJmxPdd.remoteAccess.username=#USERNAMEGWN#
      #IDGWN#.risorseJmxPdd.remoteAccess.password=#PASSWORDGWN#
                              

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

#. Editare il file <directory-lavoro>/monitor_local.properties
   Disabilitare la configurazione per la singola istanza commentando la
   proprietà 'statoPdD.sonde.standard.Gateway.url':

   ::

      # Configurazione PdD in Singola Istanza
      #statoPdD.sonde.standard.Gateway.url=http://127.0.0.1:8080/govway/check
                              

   Aggiungere le seguenti righe al fine di configurare la govwayMonitor
   per il Load Balancing:

   ::

      # Configurazione PdD in Load Balancing
      configurazioni.risorseJmxPdd.tipoAccesso=openspcoop
      configurazioni.risorseJmxPdd.aliases=IDGW1,..,IDGWN
      statoPdD.sonde.standard.nodi=IDGW1,..,IDGWN
      transazioni.idCluster.useSondaPdDList=true
                              

   Devono essere elencati tutti gli identificativi, di ogni PdD in Load
   Balancing, registrati nel file govway_local.properties nella
   proprietà 'org.openspcoop2.pdd.cluster_id' come descritto in precedenza.
   Per ogni identificativo devono inoltre essere fornite le seguenti
   informazioni:

   ::

      # Configurazione IDGW1
      statoPdD.sonde.standard.#IDGW1#.url=http://#HOSTGW1#:#PORTGW1#/govway/check
      #IDGW1#.configurazioni.risorseJmxPdd.remoteAccess.url=http://#HOSTGW1#:#PORTGW1/govway/check
      #IDGW1#.configurazioni.risorseJmxPdd.remoteAccess.username=#USERNAMEGW1#
      #IDGW1#.configurazioni.risorseJmxPdd.remoteAccess.password=#PASSWORDGW1#
      ...
      # Configurazione IDGWN
      statoPdD.sonde.standard.#IDGWN#.url=http://#HOSTGWN#:#PORTGWN#/govway/check
      #IDGWN#.configurazioni.risorseJmxPdd.tipoAccesso=openspcoop
      #IDGWN#.configurazioni.risorseJmxPdd.remoteAccess.url=http://#HOSTGWN#:#PORTGWN/govway/check
      #IDGWN#.configurazioni.risorseJmxPdd.remoteAccess.username=#USERNAMEGWN#
      #IDGWN#.configurazioni.risorseJmxPdd.remoteAccess.password=#PASSWORDGWN#
                              

   Devono essere elencati inserendo al posto di #USERNAMEGW# e
   #PASSWORDGW# le credenziali utilizzate in precedenza nel file 'govway_local.properties', proprietà:

   ::
 
      org.openspcoop2.pdd.check.readJMXResources.username e
      org.openspcoop2.pdd.check.readJMXResources.password

   Indicare inoltre al posto di #HOSTGW# e #PORTGW# l'hostname e la
   porta con cui è raggiungibile GovWay.

