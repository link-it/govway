Devono essere registrate due utenze applicative con i seguenti dati:
- username: esempioFruitoreTrasparentePrincipal password:Op3nSPC@@p2 
  roles: role1AllAllOpenSPCoopTest,role2ExtAllOpenSPCoopTest,role3AllPD_OpenSPCoopTest,role4AllPA_OpenSPCoopTest
- username: esempioFruitoreTrasparentePrincipal2 password:Op3nSPC@@p2
  roles: role1AllAllOpenSPCoopTest,role8ExtPD_OpenSPCoopTest,role9ExtPA_OpenSPCoopTest
- username: esempioFruitoreTrasparentePrincipal3 password:Op3nSPC@@p2
  roles: roleTerzaUtenzaAllOpenSPCoopTest


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
Username : esempioFruitoreTrasparentePrincipal2
Password requirements are listed below. To modify these restrictions edit the add-user.properties configuration file.
 - The password must not be one of the following restricted values {root, admin, administrator}
 - The password must contain at least 8 characters, 1 alphabetic character(s), 1 digit(s), 1 non-alphanumeric symbol(s)
 - The password must be different from the username
Password : 
Re-enter Password : 
What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[  ]: role1AllAllOpenSPCoopTest,role8ExtPD_OpenSPCoopTest,role9ExtPA_OpenSPCoopTest
About to add user 'esempioFruitoreTrasparentePrincipal2' for realm 'ApplicationRealm'
Is this correct yes/no? yes
Added user 'esempioFruitoreTrasparentePrincipal2' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/standalone/configuration/application-users.properties'
Added user 'esempioFruitoreTrasparentePrincipal2' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/domain/configuration/application-users.properties'
Added user 'esempioFruitoreTrasparentePrincipal2' with groups role1AllAllOpenSPCoopTest,role8ExtPD_OpenSPCoopTest,role9ExtPA_OpenSPCoopTest to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/standalone/configuration/application-roles.properties'
Added user 'esempioFruitoreTrasparentePrincipal2' with groups role1AllAllOpenSPCoopTest,role8ExtPD_OpenSPCoopTest,role9ExtPA_OpenSPCoopTest to file '/home/opt/local/Programmi/applicationServer/jboss-eap-6.4.testsuite/domain/configuration/application-roles.properties'
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
Username : esempioFruitoreTrasparentePrincipal
Password recommendations are listed below. To modify these restrictions edit the add-user.properties configuration file.
 - The password should be different from the username
 - The password should not be one of the following restricted values {root, admin, administrator}
 - The password should contain at least 8 characters, 1 alphabetic character(s), 1 digit(s), 1 non-alphanumeric symbol(s)
Password : 
Re-enter Password : 
What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[  ]: role1AllAllOpenSPCoopTest,role2ExtAllOpenSPCoopTest,role3AllPD_OpenSPCoopTest,role4AllPA_OpenSPCoopTest
About to add user 'esempioFruitoreTrasparentePrincipal' for realm 'ApplicationRealm'
Is this correct yes/no? yes
Added user 'esempioFruitoreTrasparentePrincipal' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/standalone/configuration/application-users.properties'
Added user 'esempioFruitoreTrasparentePrincipal' to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/domain/configuration/application-users.properties'
Added user 'esempioFruitoreTrasparentePrincipal' with groups role1AllAllOpenSPCoopTest,role2ExtAllOpenSPCoopTest,role3AllPD_OpenSPCoopTest,role4AllPA_OpenSPCoopTest to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/standalone/configuration/application-roles.properties'
Added user 'esempioFruitoreTrasparentePrincipal' with groups role1AllAllOpenSPCoopTest,role2ExtAllOpenSPCoopTest,role3AllPD_OpenSPCoopTest,role4AllPA_OpenSPCoopTest to file '/home/opt/local/Programmi/applicationServer/jboss-eap-7.0.testsuite/domain/configuration/application-roles.properties'
Is this new user going to be used for one AS process to connect to another AS process? 
e.g. for a slave host controller connecting to the master or for a Remoting connection for server to server EJB calls.
yes/no? no
==============================================================================




==============================================================================
Tomcat:
Aggiungere all'interno del file 'conf/tomcat-users.xml' la seguente registrazione di ruoli ed utenze:

  <role rolename="role1AllAllOpenSPCoopTest"/>
  <role rolename="role2ExtAllOpenSPCoopTest"/>
  <role rolename="role3AllPD_OpenSPCoopTest"/>
  <role rolename="role4AllPA_OpenSPCoopTest"/>
  <role rolename="role5IntAllOpenSPCoopTest"/>
  <role rolename="role6IntPD_OpenSPCoopTest"/>
  <role rolename="role7IntPA_OpenSPCoopTest"/>
  <role rolename="role8ExtPD_OpenSPCoopTest"/>
  <role rolename="role9ExtPA_OpenSPCoopTest"/>
  <role rolename="roleTerzaUtenzaAllOpenSPCoopTest"/>

  <user username="esempioFruitoreTrasparentePrincipal" password="Op3nSPC@@p2" roles="role1AllAllOpenSPCoopTest,role2ExtAllOpenSPCoopTest,role3AllPD_OpenSPCoopTest,role4AllPA_OpenSPCoopTest"/>
  <user username="esempioFruitoreTrasparentePrincipal2" password="Op3nSPC@@p2" roles="role1AllAllOpenSPCoopTest,role8ExtPD_OpenSPCoopTest,role9ExtPA_OpenSPCoopTest"/>
  <user username="esempioFruitoreTrasparentePrincipal3" password="Op3nSPC@@p2" roles="roleTerzaUtenzaAllOpenSPCoopTest"/>

==============================================================================
