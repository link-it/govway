.. _install_ssl_server_wf_fino24:

Wildfly (security-realms)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::

   La seguente sezione fornisce degli esempi utili ad attuare la configurazione https. Per conoscere maggiori dettagli e modalità di configurazioni differenti fare riferimento a quanto indicato nella documentazione ufficiale dell'Application Server Wildfly (http://wildfly.org).


La configurazione può essere attuata nel file standalone.xml che si trova all’interno della cartella 'WILDFLY_HOME/standalone/configuration/'.

- Deve prima essere definito un security realm contenente il certificato che il server deve esporre, aggiungendolo ai security realms esistenti.

   ::

       <security-realms>
            <security-realm name="mySecurityRealm">
                <server-identities>
                        <ssl>
                                <keystore path="/etc/govway/keys/govway_server.jks" keystore-password="changeit" 
					  alias="aliasInKeystore" key-password="changeit" />
                        </ssl>
                </server-identities>
            </security-realm>
            ...
        </security-realms>
   
  se oltre ad esporre un certificato server, si deve autenticare il certificato client del chiamante, la configurazione del security realm deve essere estesa con la definizione di un trustStore che contenga i certificati necessari a validarli.

   ::

       <security-realms>
            <security-realm name="mySecurityRealmClientAuth">
                <server-identities>
                        <ssl>
                                <keystore path="/etc/govway/keys/govway_https_server.jks" keystore-password="changeit" 
					  alias="aliasInKeystore" key-password="changeit" />
                        </ssl>
                </server-identities>
                <authentication>
                        <truststore path="/etc/govway/keys/govway_https_truststore.jks" keystore-password="changeit"/>
                </authentication>
            </security-realm>
            ...
        </security-realms>

- Il security realm creato deve essere associato ad un 'https-listener'.

    ::
   
        <https-listener name="httpsGovWay" socket-binding="httpsGovWay" security-realm="mySecurityRealm"/>

  Per rendere obbligatorio che il chiamante debba fornire un proprio certificato client deve essere aggiunto l'attributo 'verify-client' valorizzato con il valore 'REQUIRED'. Se tale attributo viene valorizzato invece con il valore 'REQUESTED' il certificato client non è obbligatorio ma verrà comunque validato, se presente.

    ::
   
        <https-listener name="httpsGovWayClientAuth" socket-binding="httpsGovWayClientAuth" security-realm="mySecurityRealmClientAuth" verify-client="REQUIRED"/>

- Si deve infine associare al socket-binding indicato nell'https listener una porta su cui l'application server gestisce le richieste https.

    ::
   
        <socket-binding name="httpsGovWayClientAuth" port="${jboss.https.port:8445}"/>


