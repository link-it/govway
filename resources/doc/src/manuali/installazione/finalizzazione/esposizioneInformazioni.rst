.. _esposizioneInformazioni:

===========================
Esposizione di Informazioni
===========================

Per limitare l'esposizione di informazioni relative all'architettura e alle tecnologie utilizzate, è possibile adottare le seguenti misure di sicurezza:

Application Server Tomcat
--------------------------

Se viene utilizzato Apache Tomcat come application server, è consigliabile modificare la configurazione per evitare che vengano esposte informazioni tecniche nei messaggi di errore.

Modificare il file **conf/server.xml** di Tomcat inserendo, all'interno del tag ``<Host>``, il seguente valve:

.. code-block:: xml

   <Valve className="org.apache.catalina.valves.ErrorReportValve"
          showReport="false"
          showServerInfo="false"/>

Questa configurazione impedisce che vengano visualizzati:

- **showReport="false"**: dettagli tecnici degli errori (stack trace, descrizioni dettagliate)
- **showServerInfo="false"**: informazioni sulla versione del server Tomcat
