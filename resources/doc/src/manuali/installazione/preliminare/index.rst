.. _inst_preliminare:

================
Fase Preliminare
================

Prima di procedere con l'installazione di GovWay è necessario disporre
del software di base nell'ambiente di esercizio. Verificare i passi
seguenti, procedendo eventualmente all'installazione dei componenti
mancanti.

#. *Java Runtime Environment (JRE) 11* (è possibile scaricare
   JRE al seguente indirizzo:
   https://jdk.java.net/archive/)

   Verificare la configurazione dell'ambiente Java dell'Application
   Server. Si raccomanda una configurazione minima dei parametri della
   JVM, come segue:

   -  -XX:MaxMetaspaceSize=512m -Xmx1024m

   Verificare inoltre che il charset utilizzato dalla JVM sia UTF-8:

   - -Dfile.encoding=UTF-8

#. *Application Server WildFly* (http://wildfly.org); viene supportato dalla versione 18 alla versione 26. In alternativa è possibile effettuare
   l'installazione su Apache Tomcat (http://tomcat.apache.org) versione 9.

   .. note::
      GovWay supporta anche altri application server j2ee diversi da
      quelli citati, partendo dalla distribuzione sorgente.

#. Un *RDBMS* accessibile via JDBC. La binary release supporta le
   seguenti piattaforme:

   -  *PostgreSQL 8.x o superiore*

   -  *MySQL 5.7.8 o superiore*

   -  *Oracle 10g o superiore*

   -  *HyperSQL 2.0 o superiore*

   -  *MS SQL Server 2019 o superiore*

La distribuzione GovWay è stata estesamente testata prima del rilascio
sulla seguente piattaforma di riferimento:

-  *Openjdk 11 (version: 11.0.12+7)*

-  *PostgreSQL 9 (version: 9.2.24)*, *PostgreSQL 13 (version: 13.2)* e *Oracle 11g ExpressEdition (version: 11.2.0.2.0)*

-  *WildFly 18 (version: 18.0.1.Final)*, *WildFly 25 (version: 25.0.0.Final)*, *WildFly 26 (version: 26.1.1.Final)* e *Tomcat 9 (version: 9.0.31)*
