.. _avanzate_connettori_jms:

Connettore JMS
~~~~~~~~~~~~~~

Il connettore JMS consente di configurare i parametri per abilitare la
comunicazione tra GovWay e gli applicativi attraverso il protocollo JMS.

In :numref:`ConnettoreJMSFig` è mostrata la maschera di configurazione del connettore JMS.

   .. figure:: ../../../_figure_console/ConnettoreJMS.png
    :scale: 70%
    :align: center
    :name: ConnettoreJMSFig

    Dati di configurazione di un connettore JMS

In riferimento alla :numref:`ConnettoreJMSFig` descriviamo in dettaglio il significato dei campi
per la configurazione:

-  **Nome**: identificatore JNDI della risorsa queue/topic JMS; può contenere parti dinamiche risolte a runtime (:ref:`avanzate_connettori_jms_codaDinamica`)

-  **Tipo** (Queue/Topic): Si specifica se la risorsa JMS è di tipo
   queue o topic

-  **Send As** (TextMessage/BytesMessage): Si sceglie la codifica del
   messaggio da inviare tramite broker JMS, tra TextMessage e
   BytesMessage.

-  **Utente**: Username relativo alle credenziali per l'autenticazione e
   la negoziazione di una connessione sul Broker JMS; può contenere parti dinamiche risolte a runtime (:ref:`valoriDinamici`)

-  **Password**: Password relativa alle credenziali per l'autenticazione
   e la negoziazione di una connessione sul Broker JMS; può contenere parti dinamiche risolte a runtime (es. ${envj:JMS_PASSWORD} per leggerla da una variabile di sistema o java, :ref:`valoriDinamici`)

-  **Connection Factory**: Identificatore della risorsa JNDI per la
   creazione di una connessione verso il broker JMS

-  **Initial Context Factory**: Class Name per l'inizializzazione del
   server JNDI per la lookup della Connection Factory e della Coda

-  **Url Pkg Prefixes**: Lista sperata da ':' per specificare i prefissi
   dei package da utilizzare per l'inizializzazione del Context JNDI

-  **Provider Url**: Indirizzo che localizza il server JNDI

-  **Proprietà**: consente di definire ulteriori proprietà del connettore, descritte nella sezione :ref:`avanzate_connettori_jms_proprieta`; il link è disponibile dopo aver salvato il connettore.

La risposta restituita al client al termine della pubblicazione è descritta nella sezione :ref:`avanzate_connettori_jms_risposta`.

.. toctree::
        :maxdepth: 2

        proprieta
        codaDinamica
        risposta
