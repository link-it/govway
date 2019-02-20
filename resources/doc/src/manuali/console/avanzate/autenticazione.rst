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
*security-constraint*. A tale scopo l'installazione di GovWay dispone di
un contesto built-in *govwaySec* (definito nel war
govwaySec.war/WEB-INF/web.xml) protetto tramite security constraint con
metodo di autenticazione *HTTP-BASIC*:

::

        <security-constraint>
                <web-resource-collection>
                        <web-resource-name>AuthenticationContainer</web-resource-name>
                        <url-pattern>/*</url-pattern>
                </web-resource-collection>
                <auth-constraint>
                        <role-name>*</role-name>
                </auth-constraint>
        </security-constraint>

        ...

        <security-role>
            <role-name>*</role-name>
        </security-role>

        <login-config>
            <auth-method>BASIC</auth-method>
        </login-config>

Se si intende utilizzare una configurazione differente di quella
built-in si deve procedere con la modifica di tale descrittore web.xml
presente all'interno dell'archivio.

.. note::
    Con questo tipo di configurazione, le URL che gli applicativi devono
    invocare devono essere adeguate sostituendo il contesto *govway* con
    *govwaySec*.

