.. _avanzate_connettori_jms:

Connettore JMS
~~~~~~~~~~~~~~

Il connettore JMS consente di configurare i parametri per abilitare la
comunicazione tra GovWay e gli applicativi attraverso il protocollo JMS.

In :numref:`ConnettoreJMSFig` è mostrata la maschera di configurazione del connettore JMS.

   .. figure:: ../../_figure_console/ConnettoreJMS.jpg
    :scale: 100%
    :align: center
    :name: ConnettoreJMSFig

    Dati di configurazione di un connettore JMS

In riferimento alla :numref:`ConnettoreJMSFig` descriviamo in dettaglio il significato dei campi
per la configurazione:

-  **Nome**: identificatore JNDI della risorsa queue/topic JMS

-  **Tipo** (Queue/Topic): Si specifica se la risorsa JMS è di tipo
   queue o topic

-  **Send As** (TextMessage/BytesMessage): Si sceglie la codifica del
   messaggio da inviare tramite broker JMS, tra TextMessage e
   BytesMessage.

-  **Utente**: Username relativo alle credenziali per l'autenticazione e
   la negoziazione di una connessione sul Broker JMS

-  **Password**: Password relativa alle credenziali per l'autenticazione
   e la negoziazione di una connessione sul Broker JMS

-  **Connection Factory**: Identificatore della risorsa JNDI per la
   creazione di una connessione verso il broker JMS

-  **Initial Context Factory**: Class Name per l'inizializzazione del
   server JNDI per la lookup della Connection Factory e della Coda

-  **Url Pkg Prefixes**: Lista sperata da ':' per specificare i prefissi
   dei package da utilizzare per l'inizializzazione del Context JNDI

-  **Provider Url**: Indirizzo che localizza il server JNDI
