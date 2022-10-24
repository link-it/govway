1. Istruzioni

Di seguito vengono fornite le istruzioni per utilizzare la console con un'utenticazione configurata a livello del container dell'application server che ospita le console.

1) Devono essere registrate sull'application server le utenze che si desidera far autenticare.
Nell'Appendice A di questa guida vengono forniti indicazioni per i vari tipi di application server supportati.
NOTA: le utenze devono corrispondere ai principal degli utenti registrati tramite la 'govwayConsole' che hanno i permessi di accesso alla 'govwayMonitor' ('D' e 'R').

2) Deve essere modificato il file 'web.xml' dell'applicazione per definire: <security-constraint> + <login-config> + <security-role>.
   Per farlo aggiungere in fondo alla definizione del file il contenuto presente in:
   ./tools/web_interfaces/monitor/src/WEB-INF/web/web.xml.secure

3) Su wildfly attivare un security domain sull'application server ed indicarlo all'interno del nuovo file che deve essere creato in 'WEB-INF/jboss-web.xml' della console con un contenuto simile a quello presente in:
   ./tools/web_interfaces/monitor/src/WEB-INF/jboss-web.xml

   Esempio di creazione del security domain:
       <subsystem xmlns="urn:jboss:domain:undertow:12.0" default-server="default-server" .....>
           ....
            <handlers>
                ....
            </handlers>
            <application-security-domains>
               <application-security-domain name="govwayMonitor" security-domain="ApplicationDomain"/>
            </application-security-domains>
        </subsystem>




Appendice A: Registrazione delle Utenze

Di seguito viene fornito un esempio per la registrazione dell'utenza 'operatore'.

==============================================================================
JBoss7:
Per la registrazione utilizzare: JBOSS-DIR/bin/add-user.sh

Di seguito viene riportato un esempio di flusso di creazione di un utenza:
[poli@ws13 bin]$ ./add-user.sh 

What type of user do you wish to add? 
 a) Management User (mgmt-users.properties) 
 b) Application User (application-users.properties)
(a): b

Enter the details of the new user to add.
Using realm 'ApplicationRealm' as discovered from the existing property files.
Username : operatore
Password requirements are listed below. To modify these restrictions edit the add-user.properties configuration file.
 - The password must not be one of the following restricted values {root, admin, administrator}
 - The password must contain at least 8 characters, 1 alphabetic character(s), 1 digit(s), 1 non-alphanumeric symbol(s)
 - The password must be different from the username
Password : 
Re-enter Password : 
What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[  ]: 
About to add user 'operatore' for realm 'ApplicationRealm'
Is this correct yes/no? yes
Added user 'operatore' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/standalone/configuration/application-users.properties'
Added user 'operatore' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/domain/configuration/application-users.properties'
Added user 'operatore' with groups  to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/standalone/configuration/application-roles.properties'
Added user 'operatore' with groups  to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/domain/configuration/application-roles.properties'
Is this new user going to be used for one AS process to connect to another AS process? 
e.g. for a slave host controller connecting to the master or for a Remoting connection for server to server EJB calls.
yes/no? no
==============================================================================


==============================================================================
Wildfly:
Per la registrazione utilizzare: WILDFLY-DIR/bin/add-user.sh

[poli@ws13 bin]$ ./add-user.sh 

What type of user do you wish to add? 
 a) Management User (mgmt-users.properties) 
 b) Application User (application-users.properties)
(a): b

Enter the details of the new user to add.
Using realm 'ApplicationRealm' as discovered from the existing property files.
Username : operatore
Password recommendations are listed below. To modify these restrictions edit the add-user.properties configuration file.
 - The password should be different from the username
 - The password should not be one of the following restricted values {root, admin, administrator}
 - The password should contain at least 8 characters, 1 alphabetic character(s), 1 digit(s), 1 non-alphanumeric symbol(s)
Password : 
Re-enter Password : 
What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[  ]:
About to add user 'operatore' for realm 'ApplicationRealm'
Is this correct yes/no? yes
Added user 'operatore' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/standalone/configuration/application-users.properties'
Added user 'operatore' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/domain/configuration/application-users.properties'
Added user 'operatore' with groups  to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/standalone/configuration/application-roles.properties'
Added user 'operatore' with groups  to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/domain/configuration/application-roles.properties'
Is this new user going to be used for one AS process to connect to another AS process? 
e.g. for a slave host controller connecting to the master or for a Remoting connection for server to server EJB calls.
yes/no? no
==============================================================================


==============================================================================
Tomcat:
Aggiungere all'interno del file 'conf/tomcat-users.xml' la seguente registrazione:

  <user username="operatore" password="123456" roles=""/>

==============================================================================


