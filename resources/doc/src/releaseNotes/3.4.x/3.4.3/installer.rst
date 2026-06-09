Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- Aggiunto il supporto per WildFly 40.

- Corretto un errore di packaging dell'archivio 'govwayStats.war' che, su application server WildFly, impediva la corretta elaborazione del descrittore 'jboss-deployment-structure.xml' in fase di deployment.

- Corretta la gestione dell'escape di username e password nei file generati:

  - escape dei caratteri speciali XML (& < > " ') nei datasource, che in precedenza poteva produrre file malformati o troncati (sia per WildFly/JBoss sia per Tomcat);

  - escape stile properties (backslash) nei file 'daoFactory.properties' dei batch a connessione JDBC diretta ('generatoreStatistiche', 'gestoreRuntimeRepository'), dove un carattere ``\`` nella password veniva altrimenti consumato in fase di lettura.

- Introdotta la normalizzazione del prefisso del contesto JNDI ('java:/comp/env/') dei datasource il cui nome viene risolto dal database, così da consentire l'utilizzo della medesima base dati su application server differenti, adattando automaticamente il nome JNDI all'ambiente di esecuzione.
