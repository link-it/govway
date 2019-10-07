.. |br| raw:: html

    <br/>

.. _authXACML:

Autorizzazione XACML
~~~~~~~~~~~~~~~~~~~~

GovWay può essere configurato per effettuare verifiche, dei claims
ottenuti tramite la validazione dell'access token, più complesse
rispetto a quelle descritte nei precedenti paragrafi. Per farlo si deve
utilizzare una policy XACML.

Per simulare lo scenario utilizzeremo sempre il servizio *Playground* e
l'\ *Authorization Server di Google* descritto nella precedente sezione
:ref:`validazioneIntrospection`.

Per l'autorizzazione verrà caricata su GovWay una XACML Policy, di
seguito descritta, che non possiede una vera logica autorizzativa ma
serve solo a titolo di esempio per descrivere la funzionalità.

.. figure:: ../_figure_howto/oauth_scenario_xacml.png
    :scale: 100%
    :align: center
    :name: quick_oauthXACML_fig

    Scenario OAuth con autorizzazione XACMLPoliy

In fase di autorizzazione, il gateway costruisce una XACMLRequest
contenente tutti i parametri della richiesta, comprese le informazioni
relative al chiamante (credenziali ed eventuali ruoli) e le informazioni
presenti nel token. Nella tabella seguente vengono forniti i dettagli
sui nomi dei parametri.

.. table:: Parametri XACML
   :widths: 60 40
   :name: quick_xacml_tab

   ============================================================ ================
   Nome                                                         Descrizione                                                                                                                     
   ============================================================ ================
                                                    *Sezione 'Action'*
   -----------------------------------------------------------------------------
   org:govway:action:token:audience                             Destinatario del token
   org:govway:action:token:scope                                Lista di scopes                                                                                                             
   org:govway:action:token:jwt:claim:<nome>=<valore>            Tutti i claims presenti nel jwt validato
   org:govway:action:token:introspection:claim:<nome>=<valore>  Tutti i claims presenti nella risposta del servizio di introspection
   org:govway:action:provider                                   Indica il soggetto erogatore del servizio
   org:govway:action:service                                    Indica il servizio nel formato tipo/nome
   org:govway:action:action                                     Nome dell'operazione del servizio invocata
   org:govway:action:url                                        Url di invocazione utilizzata dal mittente
   org:govway:action:url:parameter:NOME\_PARAM                  Tutti i parametri presenti nell'url di invocazione saranno inseriti nella XACMLRequest con questo formato
   org:govway:action:transport:header:NOME\_HDR                 Tutti gli header http presenti nell'url di invocazione saranno inseriti nella XACMLRequest con questo formato
   org:govway:action:soapAction                                 Valore della SOAPAction
   org:govway:action:gwService                                  Ruolo della transazione (inbound/outbound)
   org:govway:action:protocol                                   Profilo di utilizzo associata al servizio richiesto (es. spcoop)
                                                     *Sezione 'Subject'*
   -----------------------------------------------------------------------------
   org:govway:subject:token:issuer                              Issuer del token
   org:govway:subject:token:subject                             Subject del token
   org:govway:subject:token:username                            Username dell'utente cui è associato il token
   org:govway:subject:token:clientId                            Identificativo del client che ha negoziato il token
   org:govway:subject:token:userInfo:fullName                   Nome completo dell'utente cui è associato il token
   org:govway:subject:token:userInfo:firstName                  Nome dell'utente cui è associato il token
   org:govway:subject:token:userInfo:middleName                 Secondo nome (o nomi aggiuntivi) dell'utente cui è associato il token
   org:govway:subject:token:userInfo:familyName                 Cognome dell'utente cui è associato il token
   org:govway:subject:token:userInfo:eMail                      Email dell'utente cui è associato il token
   org:govway:subject:token:userInfo:claim:<nome>=<valore>      Tutti i claims presenti nella risposta del servizio di UserInfo
   org:govway:subject:organization                              Indica il soggetto fruitore                                                                                                
   org:govway:subject:client                                    Identificativo del servizio applicativo client
   org:govway:subject:credential                                Rappresenta la credenziale di accesso (username, subject o il principal) utilizzata dal client per richiedere il servizio
   org:govway:subject:role                                      Elenco dei ruoli che possiede il client che ha richiesto il servizio
   ============================================================ ================

Di seguito un esempio di XACMLPolicy che traduce in policy l'esempio
descritto nella precedente sezione :ref:`authClaims`. La verifica che andiamo a definire
è la seguente:

-  *Audience* (claim 'aud'): contenga l'identificativo dell'applicazione
   *Playground* come destinatario del token

-  *Applicazione Client* (claim 'azp'): controlleremo che il client
   appartenga ad uno delle applicazioni conosciute. Nell'elenco, non
   inseriremo immediatamente l'identificativo di *Playground* in modo
   che l'autorizzazione fallisca in un primo test.

.. note:: **Per conoscere l’identificativo dell’applicazione Playground**
    |br|
    È possibile vedere una precedente transazione terminata con
    successo per conoscere l'esatto valore associato all'applicazione
    *Playground* (es. :numref:`quick_oauthStoricoTransazioniOKTokenInfo_fig`).

::

    <Policy PolicyId="Policy"
        RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides"
        xmlns="urn:oasis:names:tc:xacml:2.0:policy:schema:os" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-policy-schema-os.xsd">
        <Target />
        <Rule Effect="Permit" RuleId="ok">
            <Condition>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">

                    <Apply
                        FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                        <
                            AttributeId=""
                            DataType="http://www.w3.org/2001/XMLSchema#string" />
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                        </Apply>
                    </Apply>

                    <Apply
                        FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                        <
                            AttributeId=""
                            DataType="http://www.w3.org/2001/XMLSchema#string" />
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                        </Apply>
                    </Apply>

                </Apply>
            </Condition>
        </Rule>
        <Rule Effect="Deny" RuleId="ko" />
    </Policy>

-  **Configurazione Controllo degli Accessi**

   Accedere alla sezione *'Erogazioni'* e selezionare l'API
   precedentemente registrata *'PetStore v1'*. Dopodichè accedere, dal
   dettaglio dell'erogazione, alla sezione *'Configurazione'* dove
   vengono visualizzate le funzionalità attive. Cliccare sulla voce
   presente nella colonna '*Controllo Accessi*\ ' e procedere con la
   seguente configurazione all'interno della sezione *'Gestione Token'*:

   -  *Stato*: abilitato

   -  *Policy*: Google

   -  *Validazione JWT*: disabilitato

   -  *Introspection*: abilitato

   -  *User Info*: disabilitato

   -  *Token Forward*: abilitato

   Procedere inoltre con la seguente configurazione all'interno della
   sezione *'Autorizzazione'*:

   -  *Autorizzazione - Stato*: xacml-Policy

   -  *Policy*: caricare la xacml policy descritta precedentemente

   Effettuata la configurazione salvarla cliccando sul pulsante 'Salva'.

   .. figure:: ../_figure_howto/oauthAutorizzazioneXACMLConfigControlloAccessi.png
       :scale: 50%
       :align: center
       :name: quick_oauthAuthXACML_fig

       Configurazione OAuth2 - Autorizzazione XACML Policy

-  **Invocazione API**

   .. note:: **Reset Cache delle Configurazioni prima di un nuovo test**
       |br|
       Le configurazioni accedute da GovWay vengono mantenute in una
       cache dopo il primo accesso per 2 ore, è quindi necessario
       forzare un reset della cache. Per farlo accedere alla sezione
       *'Strumenti' - 'Runtime'* e selezionare la voce
       *'ResetAllCaches'*.

   Per effettuare il test utilizzare il token ottenuto come descritto
   nella sezione :ref:`validazioneIntrospection`.

   ::

       curl -v -X PUT "http://127.0.0.1:8080/govway/Ente/PetStore/v2/pet?access_token=ACCESS_TOKEN" \
       -H "accept: application/json" \
       -H "Content-Type: application/json" \
       -d '{
               "id": 3,
               "category": { "id": 22, "name": "dog" },
               "name": "doggie",
               "photoUrls": [ "http://image/dog.jpg" ],
               "tags": [ { "id": 23, "name": "white" } ],
               "status": "available"
       }'

   L'esito dell'aggiornamento termina con un codice di errore http 403 e
   una risposta problem+json che riporta la motivazione:

   ::

       HTTP/1.1 403 Forbidden
       Content-Type: application/problem+json
       Transfer-Encoding: chunked
       Server: GovWay
       GovWay-Transaction-ID: 6c13b9ac-3d60-45a6-9130-297a4d832824

       {
           "type":"https://httpstatuses.com/403",
           "title":"Forbidden",
           "status":403,
           "detail":"Il mittente non è autorizzato ad invocare il servizio gw/PetStore (versione:2) erogato da gw/Ente (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)",
           "govway_status":"protocol:GOVWAY-1352"
       }

-  **Consultazione Tracce in errore**

   Attraverso la console *govwayMonitor* è possibile consultare lo
   storico delle transazioni che sono transitate nel gateway. Dalla
   :numref:`quick_oauthAuthXACMLNegata_fig` si può vedere come le transazioni generate dopo la
   configurazione sopra indicata sono terminate con errore con esito
   *Autorizzazione Negata*.

   .. figure:: ../_figure_howto/oauthConsultazioneStoricoTransazioniErroreXACML.png
       :scale: 100%
       :align: center
       :name: quick_oauthAuthXACMLNegata_fig

       Tracce delle invocazioni terminate con errore 'Autorizzazione Negata'

   Accedendo al dettaglio di una transazione terminata in errore, e
   visualizzandone i diagnostici è possibile comprendere che l'errore è
   dovuto ad una decisione 'deny' ottenuta dopo la valutazione della
   policy: *'(result-1 DENY
   code:urn:oasis:names:tc:xacml:1.0:status:ok)'*.

   .. figure:: ../_figure_howto/oauthConsultazioneStoricoTransazioniErroreXACML_diagnostici.png
       :scale: 100%
       :align: center
       :name: quick_oauthAuthXACMLDiagnostici_fig

       Diagnostici di una invocazione terminata con errore

-  **Registrazione ClientId corretto nella XACMLPolicy**

   Di seguito un esempio di XACMLPolicy nella quale tra i valori
   consentiti per l'applicazione client viene aggiunto l'identificativo
   di *Playground* in modo che l'autorizzazione termini con successo.

   ::

       <Policy PolicyId="Policy"
           RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides"
           xmlns="urn:oasis:names:tc:xacml:2.0:policy:schema:os" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-policy-schema-os.xsd">
           <Target />
           <Rule Effect="Permit" RuleId="ok">
               <Condition>
                   <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">

                       <Apply
                           FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                           <
                               AttributeId=""
                               DataType="http://www.w3.org/2001/XMLSchema#string" />
                           <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                           </Apply>
                       </Apply>

                       <Apply
                           FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                           <
                               AttributeId=""
                               DataType="http://www.w3.org/2001/XMLSchema#string" />
                           <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"></AttributeValue>
                           </Apply>
                       </Apply>

                   </Apply>
               </Condition>
           </Rule>
           <Rule Effect="Deny" RuleId="ko" />
       </Policy>

-  **Aggiornamento XACMLPolicy in Controllo degli Accessi**

   Tramite la *govwayConsole* accedere nuovamente alla maschera di
   configurazione '*Controllo Accessi*\ ' dell'API *'PetStore v1'*;
   all'interno della sezione *'Autorizzare'* caricare la policy
   aggiornata.

-  **Nuova invocazione API**

   .. note:: **Reset Cache delle Configurazioni prima di un nuovo test**
       |br|
       Effettuare il reset della cache accedendo alla sezione
       *'Strumenti' - 'Runtime'* e selezionare la voce
       *'ResetAllCaches'*.

   Effettuare una nuova invocazione del test.

   ::

       curl -v -X PUT "http://127.0.0.1:8080/govway/Ente/PetStore/v2/pet?access_token=ACCESS_TOKEN" \
       -H "accept: application/json" \
       -H "Content-Type: application/json" \
       -d '{
               "id": 3,
               "category": { "id": 22, "name": "dog" },
               "name": "doggie",
               "photoUrls": [ "http://image/dog.jpg" ],
               "tags": [ { "id": 23, "name": "white" } ],
               "status": "available"
       }'

   L'esito dell'aggiornamento termina stavolta con successo con un
   codice http 200 e una risposta json equivalente alla richiesta.
