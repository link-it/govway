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

- *Configurazione*. Indica il database da utilizzare come repository delle configurazioni:

	- *Utilizza stesso database del Runtime*: le configurazioni risiedono nel medesimo database di esercizio di Govway

	- *Database dedicato alla configurazione*: le configurazioni risiedono su un database separato da quello di esercizio di Govway

- *Tracce*. Indica il database da utilizzare come repository delle tracce:

	- *Utilizza stesso database del Runtime*: le tracce risiedono nel medesimo database di esercizio di Govway

	- *Database dedicato alle Tracce*: le tracce risiedono su un database separato da quello di esercizio di Govway

	- Opzione *Standard / Full Index*: se si sceglie l'opzione *Full Index* il database sarà configurato in modo da utilizzare un indice complessivo che migliora le performance in fase di accesso alle tracce

- *Statistiche*. Indica il database da utilizzare come repository delle statistiche:

	- *Utilizza stesso database del Runtime*: le statistiche risiedono nel medesimo database di esercizio di Govway

	- *Utilizza stesso database delle Tracce*: le statistiche risiedono nel medesimo database utilizzato per le Tracce

	- *Database dedicato alle Statistiche*: le statistiche risiedono su un database separato da quello di esercizio (e tracce) di Govway

	- Opzione *Standard / Full Index*: se si sceglie l'opzione *Full Index* il database sarà configurato in modo da utilizzare un indice complessivo che migliora le performance in fase di accesso alle statistiche

La sezione **Componenti Applicative** prevede le seguenti opzioni:

- *Configurazione e Monitoraggio*: indica l'ambiente in cui saranno dispiegate le console web:

	- *Utilizza stesso ambiente del Runtime*: le console web risiedono nel medesimo ambiente dove è in esecuzione il runtime di Govway

	- *Ambiente dedicato*: le console web risiedono in un ambiente distinto da quello del runtime di Govway

- *Generazione delle Statistiche*: scelta del meccanismo da adottare per il periodico aggiornamento delle statistiche:

	- *Utilizza stesso ambiente del Runtime*: il meccanismo di generazione delle statistiche è automatizzato con un sistema di integrazione al runtime di Govway

	- *Generazione tramite Applicazione Batch*: la generazione delle statistiche avviene tramite invocazione di un batch appositamente generato

- *Gestione dei Nodi*: Indica la modalità con cui si vuole gestire la configurazione dei diversi nodi del runtime:

	- *Statica*: modalità che prevede la registrazione manuale dei nodi presenti. Il sistema si basa quindi sulla configurazione descritta dall'utente. In questo caso la console di gestione consente l'accesso a specifiche operazioni di manutenzione sui singoli nodi.

	- *Dinamica*: modalità che prevede l'individuazione dinamica dei nodi presenti, senza che sia necessario specificarlo nella configurazione. In questo scenario non risultano accessibili dalla console di gestione alcune operazioni di manutenzione sui singoli nodi.

In funzione delle scelte indicate in questo pannello saranno richiesti alcuni passi aggiuntivi. In particolare, riguardo l'accesso ai database, in funzione del numero di datasource previsto saranno opzionalmente richiesti:

- *Informazioni DataSource dedicato alle Configurazioni*

- *Informazioni DataSource dedicato alle Tracce*

- *Informazioni DataSource dedicato alle Statistiche*

In funzione della modalità di *Gestione dei Nodi* indicata, nel caso si sia scelto la modalità *Dinamica* verrà presentato l'ulteriore pannello *Dispiegamento Dinamico* che consente di indicare i seguenti dati:

- *Servizio Check* URL: **TODO**

- *Servizio Proxy* URL: **TODO**

   .. figure:: ../_figure_installazione/installer-dyndeploy.jpg
    :scale: 100%
    :align: center

    Dispiegamento Dinamico

