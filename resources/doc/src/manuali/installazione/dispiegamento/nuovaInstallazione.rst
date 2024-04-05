.. _deploy_new:

Nuova Installazione
-------------------

Per completare il processo di installazione si devono effettuare i passi
seguenti:

#.  Creare un utente sul RDBMS avente i medesimi valori di username e
    password indicati in fase di setup.

#.  Creare un database, per ospitare le tabelle dell'applicazione,
    avente il nome indicato durante la fase di setup. Il charset da
    utilizzare è UTF-8, la collation deve essere case sensitive.

#.  Impostare i permessi di accesso in modo che l'utente creato al passo
    1 abbia i diritti di lettura/scrittura sul database creato al *passo
    2*. Si può consultare un esempio relativo a questi primi 3 passi,
    riferito alla piattaforma PostgreSQL, in sezione :ref:`inst_setupDB`.

#.  Eseguire lo script *sql/GovWay.sql* per la creazione dello schema
    del database.

    Successivamente eseguire lo script *sql/GovWay_init.sql* per
    inserire i dati di inizializzazione del database.

    Ad esempio, nel caso di PostgreSQL, si potranno eseguire i comandi:

    - *psql <hostname> <username> -f sql/GovWay.sql*

    - *psql <hostname> <username> -f sql/GovWay_init.sql*

	.. note::
		In caso di installazione *Avanzata*:
	
		-	Gli script *sql/GovWay.sql* e *sql/GovWay_init.sql* saranno eseguiti limitatamente al database del runtime di Govway.
	
		-	Se presente lo script *sql/GovWayConfigurazione.sql*, deve essere eseguito per la creazione dello schema database dedicato alle configurazioni. Successivamente eseguire lo script *GovWayConfigurazione_init.sql* per l'inserimento dei dati di inizializzazione del database.
	
		-	Se presente lo script *sql/GovWayStatistiche.sql*, deve essere eseguito per la creazione dello schema database dedicato alle statistiche. Successivamente eseguire lo script *GovWayStatistiche_init.sql* per l'inserimento dei dati di inizializzazione del database.
	
		-	Se presente lo script *sql/GovWayTracciamento.sql*, deve essere eseguito per la creazione dello schema database dedicato alle tracce. Successivamente eseguire lo script *GovWayTracciamento_init.sql* per l'inserimento dei dati di inizializzazione del database.
	
#.  Installare il DriverJDBC, relativo al tipo di RDBMS indicato in fase
    di setup, nella directory:

    -  *<WILDFLY_HOME>/standalone/deployments*, nel caso di Wildfly.

    -  *<TOMCAT_HOME>/lib*, nel caso di Tomcat.

#.  Per le connessioni al database è necessario configurare i seguenti
    datasource impostati con i parametri forniti durante l'esecuzione
    dell'utility di installazione:

    -  Il gateway necessita di un datasource con nome JNDI:

       -  *org.govway.datasource*

    -  Le console grafiche necessitano di un datasource con nome JNDI:

       -  *org.govway.datasource.console*

    -  Nel caso si sia richiesto il supporto al protocollo eDelivery, è
       necessario un terzo datasource con nome JNDI:

       -  *org.govway.datasource.console.domibus*

    I datasource, preconfigurati per l'Application Server indicato, sono
    disponibili nella directory datasource e contengono le
    configurazioni di accesso al database indicate (ip, db_name, utenza,
    password). Tali files possono essere utilizzati come riferimento per
    la definizione dei datasource richiesti nelle modalità disponibili
    per l'Application Server scelto. Tali files possono anche essere
    utilizzati direttamente per un rapido dispiegamento copiandoli nelle
    seguenti posizioni nel file system:

    -  *<WILDFLY_HOME>/standalone/deployments*, nel caso di Wildfly.

    -  *<TOMCAT_HOME>/conf/Catalina/localhost*, nel caso di Tomcat

    Utilizzando i file preconfigurati, su WildFly è necessario
    sostituire al loro interno il placeholder *NOME_DRIVER_JDBC.jar* con
    il nome del driver JDBC installato in precedenza.

	.. note::
		In caso di installazione *Avanzata*, in base alle scelte effettuate per differenziare gli schemi database saranno disponibili nella directory *dist/datasource* tutte le definizioni richieste. In particolare, se previsto, potranno essere presenti:
	
		-	*org.govway.datasource.tracciamento*, per l'accesso al database dedicato alle tracce
	
		-	*org.govway.datasource.statistiche*, per l'accesso al database dedicato alle statistiche
	
		
		Se inoltre è stato indicato un ambiente dedicato per *Configurazione e Monitoraggio", quindi distinto dal runtime, dentro la directory *dist/datasource* saranno presenti le due directory:
	
		-	*runtime*, contenente i datasource da dispiegare nell'ambiente dedicato al runtime di Govway
	
		-	*manager*, contenente i datasource da dispiegare nell'ambiente dedicato alle console di configurazione e monitoraggio
	
#.  Eseguire il dispiegamento delle applicazioni presenti nella
    directory *archivi* secondo le modalità disponibili per
    l'Application Server scelto. Per un rapido dispiegamento è possibile
    copiare gli archivi nelle seguenti posizioni nel file system:

    -  *<WILDFLY_HOME>/standalone/deployments*, nel caso di Wildfly.

    -  *<TOMCAT_HOME>/webapps*, nel caso di Tomcat

	.. note::
		In caso di installazione *Avanzata*, se è stata indicata la scelta di un ambiente dedicato per Configurazione e Monitoraggio, la directory *dist/archivi* conterrà due subdirectory:

		-	*runtime*, contenente gli archivi applicativi da dispiegare nell'ambiente dedicato al runtime di Govway
	
		-	*manager*, contenente gli archivi applicativi da dispiegare nell'ambiente dedicato alle console di configurazione e monitoraggio

#.  Verificare che la directory di lavoro di GovWay, fornita con le
    informazioni preliminari dell'utility di installazione, esista o
    altrimenti crearla con permessi tali da consentire la scrittura
    all'utente di esecuzione dell'application server

#.  Copiare nella directory di lavoro tutti i files di configurazioni
    presenti nella directory *cfg*. Ad esempio con il comando: 

    - *cp cfg/\*.properties /etc/govway/*

    La directory di destinazione deve essere accessibile in lettura
    all'utente con cui si esegue l'Application Server.

	.. note::
		In caso di installazione *Avanzata*, se è stata indicata la scelta di un ambiente dedicato per Configurazione e Monitoraggio, la directory *dist/cfg* conterrà due subdirectory:

		-	*runtime*, contenente i file di configurazione da copiare nella directory di lavoro dell'ambiente dedicato al runtime di Govway
	
		-	*manager*, contenente i file di configurazione da copiare nella directory di lavoro dell'ambiente dedicato alle console di configurazione e monitoraggio

#. Avviare l'application server con il relativo service oppure utilizzando la linea di comando:

    -  *<WILDFLY_HOME>/bin/standalone.sh*, nel caso di Wildfly.

    -  *<TOMCAT_HOME>/bin/startup.sh*, nel caso di Tomcat.

.. note::
	In caso di installazione *Avanzata*, se è stata indicata la scelta *Generazione tramite Applicazione Batch* relativamente all'opzione di *Generazione delle Statistiche*, sarà presente la directory *dist/batch*. Per il dispiegamento del batch fare riferimento alla sezione :ref:`deploy_batch`.
