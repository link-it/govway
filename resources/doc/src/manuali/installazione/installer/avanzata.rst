.. _inst_installer_avanzata:

Modalità Avanzata
-----------------

Se si è optato per la modalità di installazione "Avanzata", dopo aver visualizzato il pannello delle "Informazioni Preliminari",
il processo d'installazione mostra un pannello aggiuntivo denominato "Informazioni Dispiegamento".

   .. figure:: ../_figure_installazione/installer-infodeploy.jpg
    :scale: 100%
    :align: center

    Informazioni Dispiegamento

Le informazioni richieste da questo pannello servono a fornire le preferenze su come devono essere pacchettizzati i datasource
e i singoli componenti applicativi, in accordo all'architettura di dispiegamento che si vuole adottare.

La sezione **DBMS** prevede le seguenti opzioni:

- *Configurazione*. Indica lo schema del database da utilizzare come repository delle configurazioni:

	- *Utilizza stesso database del Runtime*: le configurazioni risiedono nel medesimo schema di esercizio di Govway

	- *Database dedicato alla configurazione*: le configurazioni risiedono su uno schema separato da quello di esercizio di Govway

- *Tracce*. Indica lo schema database da utilizzare come repository delle tracce:

	- *Utilizza stesso database del Runtime*: le tracce risiedono nel medesimo schema di esercizio di Govway

	- *Database dedicato alle Tracce*: le tracce risiedono su uno schema separato da quello di esercizio di Govway

	- Opzione *Standard / Full Index*: se si sceglie l'opzione *Full Index*, il database sarà configurato con un indice contenente molteplici colonne che consente di migliorare le performance in fase di accesso alle tracce

- *Statistiche*. Indica lo schema database da utilizzare come repository delle statistiche:

	- *Utilizza stesso database del Runtime*: le statistiche risiedono nel medesimo schema di esercizio di Govway

	- *Utilizza stesso database delle Tracce*: le statistiche risiedono nel medesimo schema utilizzato per le Tracce

	- *Database dedicato alle Statistiche*: le statistiche risiedono su uno schema separato da quello di esercizio (e tracce) di Govway

	- Opzione *Standard / Full Index*: se si sceglie l'opzione *Full Index* il database sarà configurato con un indice contenente molteplici colonne che consente di migliorare le performance in fase di accesso alle statistiche

La sezione **Componenti Applicative** prevede le seguenti opzioni:

- *Configurazione e Monitoraggio*: indica l'ambiente in cui saranno dispiegate le console web:

	- *Utilizza stesso ambiente del Runtime*: le console web risiedono nel medesimo ambiente (application server) dove è in esecuzione il runtime di Govway

	- *Ambiente dedicato*: le console web risiedono in un ambiente (application server) distinto da quello del runtime di Govway

- *Generazione delle Statistiche*: scelta del meccanismo da adottare per il periodico aggiornamento delle statistiche:

	- *Utilizza stesso ambiente del Runtime*: il meccanismo di generazione delle statistiche è automatizzato con un sistema di integrazione al runtime di Govway

	- *Generazione tramite Applicazione Batch*: la generazione delle statistiche avviene tramite invocazione di un batch appositamente generato

- *Gestione dei Nodi*: Indica la modalità con cui si vuole gestire la configurazione dei diversi nodi di runtime:

	- *Statica*: modalità che prevede la registrazione manuale dei nodi presenti come descritto nella sezione :ref:`cluster`. In questo caso la console di gestione consente l'accesso a specifiche operazioni di manutenzione sui singoli nodi.

	- *Dinamica*: modalità che prevede l'individuazione dinamica dei nodi presenti, senza che sia necessario specificarlo nella configurazione (utile in ambienti container). In questo scenario non risultano accessibili dalla console di gestione alcune operazioni di manutenzione sui singoli nodi.

In funzione delle opzioni, fornite in questo pannello, saranno proposti dalla procedura d'installazione alcuni passaggi aggiuntivi.
In particolare, riguardo l'accesso agli schemi del database, in funzione del numero di datasource previsto saranno opzionalmente richiesti in altrettanti pannelli:

- *Informazioni DataSource dedicato alle Configurazioni*

- *Informazioni DataSource dedicato alle Tracce*

- *Informazioni DataSource dedicato alle Statistiche*

In funzione della modalità di *Gestione dei Nodi* indicata, nel caso si sia scelto la modalità *Dinamica* verrà presentato l'ulteriore pannello *Dispiegamento Dinamico* che consente di indicare i seguenti dati:

- *Servizio Check* URL: servizio che consente di monitorare lo stato dei nodi; è possibile utilizzare il servizio '/govway/check' di un qualsiasi nodo (es. acceduto tramite un bilanciatore) o un servizio fornito dal container.

- *Servizio Proxy* URL: servizio che consente di inviare un comando ad ogni nodo di runtime attivo; deve essere indirizzato il servizio '/govway/proxy' di un qualsiasi nodo attivo.

   .. figure:: ../_figure_installazione/installer-dyndeploy.jpg
    :scale: 100%
    :align: center

    Dispiegamento Dinamico

I restanti passi del processo di installazione rimangono invariati rispetto alla modalità Standard.
