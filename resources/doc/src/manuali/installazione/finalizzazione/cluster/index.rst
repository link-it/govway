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

In particolare per realizzare la configurazione descritta al punto 3, è necessario:


- Editare il file <directory-lavoro>/govway_local.properties aggiungendo le seguenti righe:

   ::

      # Identificativo univoco della macchina 
      org.openspcoop2.pdd.cluster_id=#IDGW#
      # Identificativo univoco numerico della macchina
      org.openspcoop2.pdd.cluster_id.numeric=#NUMERO#
      # Cifre utilizzate per l’utilizzo dell'identificativo univoco numerico come prefisso di un numero seriale (es. identificativo eGov)
      org.openspcoop2.pdd.cluster_id.numeric.dinamico.cifre=#NUMEROCIFRE#
                              
   - inserendo al posto di #IDGW# l'identificatore unico associato alla specifica istanza che si sta configurando. Scegliere un identificativo con cui si possa facilmente riconoscere la macchina, ad esempio l'hostname.

   - inserendo al posto di #NUMERO# l'identificatore unico numerico associato all'istanza. Scegliere un identificativo numerico progressivo, a partire da 0, per ciascuna istanza del software GovWay nel cluster (da 0 a 99).

   - inserendo al posto di #NUMEROCIFRE# le cifre utilizzate per l’utilizzo dell'identificativo univoco numerico come prefisso di un numero seriale (es. identificativo eGov); il numero di cifre consente di aggiungere un prefisso '0' all'identificativo numerico se inferiore al numero di cifre indicate. Ad esempio un identificativo 5 verrà serializzato come '05' in caso di 'org.openspcoop2.pdd.cluster_id.numeric.dinamico.cifre=2'.

      - 1: permette di avere 10 macchine (da 0 a 9)
      - 2: permette di avere 100 macchine (da 00 a 99)

- Effettuata la modifica dei files è necessario un riavvio dell'Application Server per rendere operative le modifiche.

.. note::

	La directory '<directory-lavoro>' è la directory contenente tutti i files di configurazione. Verificare quale directory è stata indicata durante l'esecuzione del setup (vedi Esecuzione dell'Installer).

.. toctree::
        :maxdepth: 2

	console
