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

#. Un *RDBMS* accessibile via JDBC. La release binaria è compatibile con i seguenti database:

   -  *PostgreSQL 8.x o superiore*

   -  *MySQL 5.7.8 o superiore*

   -  *Oracle 10g o superiore*

   -  *HyperSQL 2.0 o superiore*

   -  *MS SQL Server 2019 o superiore*
   
   Il database deve essere configurato con un character encoding UTF-8 e una collation case-sensitive per garantire il corretto funzionamento dell'applicazione.

La distribuzione GovWay è stata estesamente testata prima del rilascio
sulla seguente piattaforma di riferimento:

-  *Openjdk 11 (version: 11.0.24+8)*

-  *PostgreSQL 13 (version: 13.7)*, *PostgreSQL 16 (version: 16.2)* e *Oracle 11g ExpressEdition (version: 11.2.0.2.0)*

-  *WildFly 18 (version: 18.0.1.Final)*, *WildFly 25 (version: 25.0.0.Final)*, *WildFly 26 (version: 26.1.3.Final)* e *Tomcat 9 (version: 9.0.91)*
