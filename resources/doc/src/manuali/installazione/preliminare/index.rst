.. _inst_preliminare:

================
Fase Preliminare
================

Prima di procedere con l'installazione di GovWay è necessario disporre
del software di base nell'ambiente di esercizio. Verificare i passi
seguenti, procedendo eventualmente all'installazione dei componenti
mancanti.

#. *Java Runtime Environment (JRE) 21* 

   Verificare la configurazione dell'ambiente Java dell'Application
   Server. Si raccomanda una configurazione minima dei parametri della
   JVM, come segue:

   -  -XX:MaxMetaspaceSize=512m -Xmx1024m

   Verificare inoltre che il charset utilizzato dalla JVM sia UTF-8:

   - -Dfile.encoding=UTF-8

   .. note::
      A partire da GovWay 3.4.3 è possibile l'esecuzione anche su *Java 25*,
      limitatamente agli Application Server avviabili con tale versione
      (WildFly ≥ 36 e Apache Tomcat 11). Per maggiori dettagli vedi la
      :ref:`nota sulle dipendenze esterne <java25>`.

#. *Application Server WildFly* (http://wildfly.org); viene supportata la versione 27-28 (compatibile con JBoss EAP 8.0) e le versioni dalla 35 alla 41 (compatibile con JBoss EAP 8.1). In alternativa è possibile effettuare
   l'installazione su Apache Tomcat (http://tomcat.apache.org) versione 11.

#. Un *RDBMS* accessibile via JDBC. La release binaria è compatibile con i seguenti database:

   -  *PostgreSQL 8.x o superiore*

   -  *MySQL 5.7.8 o superiore*

   -  *Oracle 10g o superiore*

   -  *HyperSQL 2.0 o superiore*

   -  *MS SQL Server 2019 o superiore*
   
   Il database deve essere configurato con un character encoding UTF-8 e una collation case-sensitive per garantire il corretto funzionamento dell'applicazione.

   .. note::
      **MySQL: charset latin1**

      Su MySQL lo schema di GovWay dichiara esplicitamente, per ogni tabella, il
      charset *latin1* con collation *latin1_general_cs* (case-sensitive). La
      dichiarazione a livello di tabella ha precedenza sul default del database,
      quindi il database va creato in modo coerente:

      ::

         CREATE DATABASE govway CHARACTER SET latin1 COLLATE latin1_general_cs;

      La scelta ha origine storica: *latin1* è stato il charset predefinito di
      MySQL fino alla versione 8.0, ed è quello con cui lo schema fu definito;
      l'unica variazione rispetto al default riguardò la collation, portata a
      *latin1_general_cs* per ottenere il confronto case-sensitive richiesto
      dall'applicazione. Non è più stata modificata in seguito, sia per non
      introdurre modifiche non retrocompatibili sugli schemi già in esercizio,
      sia perché alcune colonne indicizzate hanno oggi dimensioni che, in un
      charset multibyte, eccederebbero il limite di InnoDB sulla chiave di
      indice.

La distribuzione GovWay è stata estesamente testata prima del rilascio
sulla seguente piattaforma di riferimento:

-  *Openjdk 21 (version: 21.0.7+6)* e *Openjdk 25 (version: 25.0.3+9)*

-  *PostgreSQL 13 (version: 13.21)*, *PostgreSQL 16 (version: 16.1)* e *Oracle 11g ExpressEdition (version: 11.2.0.2.0)*

-  *WildFly 39 (version: 39.0.0.Final)*, *WildFly 40 (version: 40.0.0.Final)* e *Tomcat 11 (version: 11.0.8)*
