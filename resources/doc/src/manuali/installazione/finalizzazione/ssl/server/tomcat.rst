.. _install_ssl_server_tomcat:

Tomcat
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::

   La seguente sezione fornisce degli esempi utili ad attuare la configurazione https. Per conoscere maggiori dettagli e modalità di configurazioni differenti fare riferimento a quanto indicato nella documentazione ufficiale dell'Application Server Apache Tomcat (http://tomcat.apache.org).


La configurazione può essere attuata nel file server.xml che si trova all’interno della cartella 'TOMCAT_HOME/conf'.

Deve essere definito un connettore contenente il certificato che il server deve esporre e la porta su cui deve gestire le richieste https. 

   ::

       <Connector port="8445" protocol="HTTP/1.1" SSLEnabled="true"
           strategy="ms" maxHttpHeaderSize="8192"
           emptySessionPath="true"
           scheme="https" secure="true" clientAuth="false" sslProtocol = "TLS"
           keyAlias="aliasInKeystore"
           keystoreFile="/etc/govway/keys/govway_https_server.jks"
           keystorePass="changeit"/>

.. note::

   Nell'esempio fornito la passowrd della chiave privata del certificato server deve coincidere con la password del keystore.

Per rendere obbligatorio che il chiamante debba fornire un proprio certificato client:

- deve essere abilitato l'attributo 'clientAuth'.

   ::

       <Connector port="8445" ... clientAuth="true"  .../>

- deve essere fornito un trustStore che contenga i certificati necessari a validarle i certificati client ricevuti. Il trustStore deve essere fornito attraverso le proprietà java 'javax.net.ssl.trustStore' e 'javax.net.ssl.trustStorePassword'. Per farlo è possibile ad esempio aggiungere la seguente riga al file 'TOMCAT_HOME/bin/setenv.sh' (creare il file se non esiste):

    ::
   
        JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=/etc/govway/keys/govway_https_truststore.jks -Djavax.net.ssl.trustStorePassword=changeit"



