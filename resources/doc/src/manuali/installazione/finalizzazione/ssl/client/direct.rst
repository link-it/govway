.. _install_ssl_client_direct:

Wildfly / Tomcat
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le comunicazioni in uscita utilizzano una configurazione ssl differente a seconda dell'impostazione utilizzata nei connettori configurati per ogni API. 

GovWay consente di indicare esplicitamente, nella configurazione di un connettore, i keystore e truststore da utilizzare. Per questa modalità seguire le indicazioni riportate nella Guida alla Console di Gestione, nella sezione 'Funzionalità Avanzate - Connettori' al paragrafo :ref:`avanzate_connettori_https`.

In alternativa, se viene solamente indicato un endpoint https senza fornire keystore specifici per l'API, GovWay eredita la configurazione https impostata nella JVM dell'Application Server per la quale viene fornito un esempio di configurazione.

**Configurazione HTTPS della JVM**

.. note::

   La seguente sezione fornisce degli esempi utili ad attuare la configurazione https. Per conoscere maggiori dettagli e modalità di configurazioni differenti fare riferimento a quanto indicato nella documentazione ufficiale della JVM e dell'Application Server utilizzato.

- Deve essere fornito un trustStore che contenga i certificati necessari a validare i certificati server ricevuti. Il trustStore deve essere fornito attraverso le proprietà java 'javax.net.ssl.trustStore', 'javax.net.ssl.trustStorePassword' e 'javax.net.ssl.trustStoreType'. Per farlo è possibile ad esempio aggiungere la seguente riga al file 'TOMCAT_HOME/bin/setenv.sh' per Tomcat o al file 'WILDFLY_HOME/bin/standalone.conf' per Wildfly:

    ::
   
        JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=/etc/govway/keys/govway_https_truststore.jks -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=jks"

- Deve essere fornito un keyStore che contenga il certificato client utilizzato da GovWay. Il keyStore deve essere fornito attraverso le proprietà java 'javax.net.ssl.keyStore', 'javax.net.ssl.keyStorePassword' e 'javax.net.ssl.keyStoreType'. Per farlo è possibile ad esempio aggiungere la seguente riga al file 'TOMCAT_HOME/bin/setenv.sh' per Tomcat o al file 'WILDFLY_HOME/bin/standalone.conf' per Wildfly:

    ::
   
        JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStore=/etc/govway/keys/govway_https_keystore.p12 -Djavax.net.ssl.keyStorePassword=changeit -Djavax.net.ssl.keyStoreType=pkcs12"

  .. note::

     La passowrd della chiave privata del certificato client deve coincidere con la password del keystore.
