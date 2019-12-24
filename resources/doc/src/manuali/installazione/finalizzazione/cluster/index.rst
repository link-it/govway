.. _cluster:

Configurazione in Load Balancing
--------------------------------

Per realizzare un'installazione in load balancing è necessario
predisporre più istanze dell'Application Server, ognuna con una propria
installazione del software. Sarà inoltre necessario:

#. Che tutte le istanze di GovWay siano configurate per condividere lo
   stesso DB.

#. Che esista un Load Balancer in grado di bilanciare il flusso di
   richieste in arrivo sulle varie istanze di AS ospitanti il software
   GovWay.

#. Che GovWay sia opportunamente configurato con un identificatore unico
   che contraddistingua lo specifico nodo.

In particolare per realizzare la configurazione descritta al punto 3, è
necessario:

a. Editare il file <directory-lavoro>/govway_local.properties
   aggiungendo la seguente riga:

   ::

      # Identificativo univoco della macchina 
      org.openspcoop2.pdd.cluster_id=#IDGW#
                              

   inserendo al posto di #IDGW# l'identificatore unico associato alla
   specifica istanza che si sta configurando. Scegliere un
   identificativo con cui si possa facilmente riconoscere la macchina,
   ad esempio l'hostname.

b. Nel caso del protocollo SPCoop, editare il file
   <directory-lavoro>/spcoop_local.properties aggiungendo le seguenti
   righe:

   ::

      # Tipo di generazione dell'identificativo
      org.openspcoop2.protocol.spcoop.id.tipo=static
      # Prefisso dell'identificativo (opzionale)
      org.openspcoop2.protocol.spcoop.id.prefix=#NUMERO#
                              

   inserendo al posto di #NUMERO# l'identificatore unico associato a
   quella istanza (da 0 a 99). Scegliere un identificativo numerico
   progressivo, a partire da 0, per ciascuna istanza del software GovWay
   nel cluster.

c. Effettuata la modifica dei files è necessario un riavvio
   dell'Application Server per rendere operative le modifiche.

NOTA: La directory '<directory-lavoro>' è la directory contenente tutti
i files di configurazione. Verificare quale directory è stata indicata
durante l'esecuzione del setup (vedi Esecuzione dell'Installer).

.. toctree::
        :maxdepth: 2

	console
