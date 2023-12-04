.. _AvanzataAuthPrincipal:

Autenticazione e Autorizzazione Principal (Security Constraint)
---------------------------------------------------------------

In precedenza, relativamente alla configurazione del controllo degli
accessi, ed in particolare del meccanismo di autenticazione, si è
indicata anche la possibilità di utilizzare il tipo *principal*. Questa
configurazione richiede che l'autenticazione sia delegata
all'application server o qualunque altra modalità che permetta a GovWay
di accedere al principal tramite la api
*HttpServletRequest.getUserPrincipal()*.

In precedenza, relativamente all'autorizzazione, si è descritta la
possibilità di utilizzare ruoli con fonte *esterna*. Questa fonte
richiede che la gestione dei ruoli sia delegata all'Application Server o
a qualunque altra modalità che permetta a GovWay di accedere ai ruoli
tramite la api *HttpServletRequest.isUserInRole()*.

Le modalità di configurazione di utenti e ruoli sull'application server
variano in funzione della versione utilizzata e pertanto si rimanda alla
documentazione del prodotto.

È inoltre richiesto che l'applicazione utente sia protetta tramite un
*security-constraint*. 

Per abilitare la protezione è necessario intervenire all'interno dell'archivio govway.ear, editando il file definito nel war
*'govway.war/WEB-INF/web.xml'*, dove è presente una configurazione di security constraint di default progettata per le url dei servizi esposti da GovWay nei vari profili di interoperabilità. Il metodo di autenticazione impostato per default è *HTTP-BASIC*. Per abilitare la protezione è sufficiente scommentare l'intera sezione che viene riportata di seguito.

::

        <!-- start Security Constraint Authentication Container
        <security-constraint>
                <web-resource-collection>
                        <web-resource-name>AuthenticationContainer</web-resource-name>
                        <url-pattern>/*</url-pattern>
                </web-resource-collection>
                <auth-constraint>
                        <role-name>*</role-name>
                </auth-constraint>
        </security-constraint>

        <security-constraint>
                <web-resource-collection>
        		<web-resource-name>AuthenticationContainer</web-resource-name>
        		<url-pattern>/IntegrationManager/PD</url-pattern>
        		<url-pattern>/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/check</url-pattern>
        		<url-pattern>/api/IntegrationManager/PD</url-pattern>
        		<url-pattern>/api/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/api/check</url-pattern>
        		<url-pattern>/modipa/IntegrationManager/PD</url-pattern>
        		<url-pattern>/modipa/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/modipa/check</url-pattern>
        		<url-pattern>/soap/IntegrationManager/PD</url-pattern>
        		<url-pattern>/soap/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/soap/check</url-pattern>
        		<url-pattern>/rest/IntegrationManager/PD</url-pattern>
        		<url-pattern>/rest/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/rest/check</url-pattern>
        		<url-pattern>/spcoop/IntegrationManager/PD</url-pattern>
        		<url-pattern>/spcoop/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/spcoop/check</url-pattern>
        		<url-pattern>/sdi/IntegrationManager/PD</url-pattern>
        		<url-pattern>/sdi/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/sdi/check</url-pattern>
        		<url-pattern>/as4/IntegrationManager/PD</url-pattern>
        		<url-pattern>/as4/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/as4/check</url-pattern>
        		<http-method>GET</http-method>
                </web-resource-collection>
        </security-constraint>

        <security-constraint>
                <web-resource-collection>
        		<web-resource-name>AuthenticationContainer</web-resource-name>
        		<url-pattern>/IntegrationManager/PD</url-pattern>
        		<url-pattern>/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/check</url-pattern>
        		<url-pattern>/api/IntegrationManager/PD</url-pattern>
        		<url-pattern>/api/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/api/check</url-pattern>
        		<url-pattern>/modipa/IntegrationManager/PD</url-pattern>
        		<url-pattern>/modipa/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/modipa/check</url-pattern>
        		<url-pattern>/soap/IntegrationManager/PD</url-pattern>
        		<url-pattern>/soap/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/soap/check</url-pattern>
        		<url-pattern>/rest/IntegrationManager/PD</url-pattern>
        		<url-pattern>/rest/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/rest/check</url-pattern>
        		<url-pattern>/spcoop/IntegrationManager/PD</url-pattern>
        		<url-pattern>/spcoop/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/spcoop/check</url-pattern>
        		<url-pattern>/sdi/IntegrationManager/PD</url-pattern>
        		<url-pattern>/sdi/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/sdi/check</url-pattern>
        		<url-pattern>/as4/IntegrationManager/PD</url-pattern>
        		<url-pattern>/as4/IntegrationManager/MessageBox</url-pattern>
        		<url-pattern>/as4/check</url-pattern>
        		<http-method>TRACE</http-method>
        		<http-method>HEAD</http-method>
        		<http-method>DELETE</http-method>
        		<http-method>POST</http-method>
        		<http-method>CONNECT</http-method>
        		<http-method>OPTIONS</http-method>
        		<http-method>PUT</http-method>
                </web-resource-collection>
                <auth-constraint>
                        <role-name>*</role-name>
                </auth-constraint>
        </security-constraint>

        <security-role>
        	<role-name>*</role-name>
        </security-role>

        <login-config>
        	<auth-method>BASIC</auth-method>
        </login-config>
        end Security Constraint Authentication Container -->

