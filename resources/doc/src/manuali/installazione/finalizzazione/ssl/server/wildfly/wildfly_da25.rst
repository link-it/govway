.. _install_ssl_server_wf_da25:

Wildfly (elytron - server-ssl-contexts)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::

   La seguente sezione fornisce degli esempi utili ad attuare la configurazione https. Per conoscere maggiori dettagli e modalità di configurazioni differenti fare riferimento a quanto indicato nella documentazione ufficiale dell'Application Server Wildfly (http://wildfly.org).


La configurazione può essere attuata nel file standalone.xml che si trova all’interno della cartella 'WILDFLY_HOME/standalone/configuration/'.

- Deve prima essere definito un keystore contenente il certificato che il server deve esporre, associandolo ad un nuovo 'server-ssl-context' aggiunto tra i contesti esistenti.

   ::

       <subsystem xmlns="urn:wildfly:elytron:xx" ....>
            <providers>
		...
	    </sasl>
            <tls>
                <key-stores>
                    <key-store name="applicationKS">
			...
                    </key-store>
		    <key-store name="govwayExampleKeyStore">
		        <credential-reference clear-text="changeit"/>
		        <implementation type="JKS"/>
		        <file path="/etc/govway/keys/govway_server.jks"/>
		    </key-store>
                </key-stores>
                <key-managers>
                    <key-manager name="applicationKM" ...>
                        ...
                    </key-manager>
		    <key-manager name="govwayExampleKeyManager" key-store="govwayExampleKeyStore" alias-filter="aliasInKeystore">
		        <credential-reference clear-text="changeit"/> <!-- password chiave privata -->
		    </key-manager>
                </key-managers>
                <server-ssl-contexts>
                    <server-ssl-context name="applicationSSC" .../>
		    <server-ssl-context name="govwayExampleSSC" need-client-auth="false" key-manager="govwayExampleKeyManager"/>
                </server-ssl-contexts>
            </tls>
       </subsystem> 
   
  se oltre ad esporre un certificato server, si deve autenticare il certificato client del chiamante, la configurazione deve essere estesa con la definizione di un trustStore che contenga i certificati necessari a validarli e un ssl-contest configurato per richiedere il certificato client tramite l'attributo 'need-client-auth' se si desidera obbligare il client a presentarsi con un certificato o tramite l'attributo 'want-client-auth' se il certificato client è opzionale ma verrà comunque validato, se presente.

   ::

       <subsystem xmlns="urn:wildfly:elytron:xx" ....>
            <providers>
		...
	    </sasl>
            <tls>
                <key-stores>
                    <key-store name="applicationKS">
			...
                    </key-store>
		    <key-store name="govwayExampleKeyStore">
		        <credential-reference clear-text="changeit"/>
		        <implementation type="JKS"/>
		        <file path="/etc/govway/keys/govway_server.jks"/>
		    </key-store>
		    <key-store name="govwayExampleTrustStore">
		        <credential-reference clear-text="changeit"/>
		        <implementation type="JKS"/>
		        <file path="/etc/govway/keys/govway_https_truststore.jks"/>
		    </key-store>
                </key-stores>
                <key-managers>
                    <key-manager name="applicationKM" ...>
                        ...
                    </key-manager>
		    <key-manager name="govwayExampleKeyManager" key-store="govwayExampleKeyStore" alias-filter="aliasInKeystore">
		        <credential-reference clear-text="changeit"/> <!-- password chiave privata -->
		    </key-manager>
                </key-managers>
		<trust-managers>
		    <trust-manager name="govwayExampleTrustManager" key-store="govwayExampleTrustStore"/>
        	</trust-managers>
                <server-ssl-contexts>
                    <server-ssl-context name="applicationSSC" .../>
		    <server-ssl-context name="govwayExampleSSC" need-client-auth="true" key-manager="govwayExampleKeyManager" trust-manager="govwayExampleTrustManager"/>
                </server-ssl-contexts>
            </tls>
       </subsystem>

- Il server ssl context creato deve essere associato ad un 'https-listener'.

    ::
   
        <https-listener name="httpsGovWay" socket-binding="httpsGovWaySB" ssl-context="govwayExampleSSC"/>

- Si deve infine associare al socket-binding indicato nell'https listener una porta su cui l'application server gestisce le richieste https.

    ::
   
        <socket-binding name="httpsGovWaySB" port="${jboss.https.port:8445}"/>

.. note::

   A partire dalla versione 25 di wildfly, nella configurazione di default è abilitato un application-security-domain 'other' che rende obbligatoria la presenza di credenziali valide per invocare applicazioni web. Come indicato nella sezione :ref:`securityDomainOther`, poichè la gestione delle autorizzazioni avviene normalmente su GovWay si deve procedere a disabilitare l'application security domain commentandone la definizione all'interno della configurazione 'undertow':

   ::

       <subsystem xmlns="urn:jboss:domain:undertow:x.0" ...>
	...
	    <application-security-domains>
               <!-- <application-security-domain name="other" security-domain="ApplicationDomain"/> -->
            </application-security-domains>
       </subsystem>

