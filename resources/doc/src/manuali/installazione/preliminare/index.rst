.. _inst_preliminare:

================
Fase Preliminare
================

Prima di procedere con l'installazione di GovWay è necessario disporre
del software di base nell'ambiente di esercizio. Verificare i passi
seguenti, procedendo eventualmente all'installazione dei componenti
mancanti.

#. *Java Runtime Environment (JRE) 8* (è possibile scaricare
   JRE al seguente indirizzo:
   http://www.oracle.com/technetwork/java/javase/downloads/index.html)

   Verificare la configurazione dell'ambiente Java dell'Application
   Server. Si raccomanda una configurazione minima dei parametri della
   JVM, come segue:

   -  -XX:MaxMetaspaceSize=516m -Xmx1024m

#. *Application Server WildFly* (http://wildfly.org) (versione 10.x,
   11.x, 12.x, 13.x, 14.x, 15.x o 16.x). In alternativa è possibile effettuare
   l'installazione su Apache Tomcat (http://tomcat.apache.org) (versione
   7.x, 8.x o 9.x).

   .. note::
      GovWay supporta anche altri application server j2ee diversi da
      quelli citati, partendo dalla distribuzione sorgente.

#. Un *RDBMS* accessibile via JDBC. La binary release supporta le
   seguenti piattaforme:

   -  *PostgreSQL 8.x o superiore*

   -  *MySQL 5.7.8 o superiore*

   -  *Oracle 10g o superiore*

   -  *HyperSQL 2.0 o superiore*

   -  *MS SQL Server 2008 o superiore*

   -  *IBM DB2 9.5 o superiore*

La distribuzione GovWay è stata estesamente testata prima del rilascio
sulla seguente piattaforma di riferimento:

-  *Sun JRE 8 (version: 1.8.0_201)*

-  *PostgreSQL 9 (version: 9.5.10)*

-  *WildFly 16 (version: 16.0.0.Final) e Tomcat 8 (version: 8.5.37)*
