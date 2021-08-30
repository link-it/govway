.. _xacml:

XACML-Policy
^^^^^^^^^^^^

Questa tipologia di autorizzazione prevede di limitare l'accesso ai soli
applicativi o soggetti fruitori che soddisfino una determinata policy
XACML. La policy deve essere caricata nel contesto dell'autorizzazione
sul controllo degli accessi, come mostrato in :numref:`xacmlAdd`.

   .. figure:: ../../_figure_console/xacmlPolicyAdd.png
    :scale: 100%
    :align: center
    :name: xacmlAdd

    Registrazione di una XACML-Policy per l’erogazione

In fase di autorizzazione, il gateway costruisce una XACMLRequest
contenente tutti i parametri della richiesta, comprese le informazioni
relative al chiamante (credenziali ed eventuali ruoli), e la valida
rispetto alla XACML-Policy associata all'erogazione. I parametri
inseriti nella XACMLRequest, che possono essere utilizzati per
effettuare la verifica all'interno di una XACML-Policy, sono i seguenti:

.. table:: Parametri inseriti in una XACMLRequest
   :class: longtable
   :widths: 60 40

   ============================================================  ===========
     Nome                                                        Descrizione
   ============================================================  ===========
   *Sezione 'Action'*                                          
   org:govway:action:provider                                    Indica il soggetto erogatore del servizio
   org:govway:action:provider:config:<nome>                      Proprietà configurate nel soggetto erogatore del servizio
   org:govway:action:service                                     Indica il servizio nel formato tipo/nome
   org:govway:action:service:config:<nome>                       Proprietà configurate nell'erogazione o nella fruizione                                                                                   
   org:govway:action:action                                      Nome dell'operazione del servizio invocata                                                                                 
   org:govway:action:url                                         Url di invocazione utilizzata dal mittente                                                                                 
   org:govway:action:url:parameter:NOME\_PARAM                   Tutti i parametri presenti nell'url di invocazione saranno inseriti nella XACMLRequest con questo formato
   org:govway:action:transport:header:NOME\_HDR                  Tutti gli header http presenti nell'url di invocazione saranno inseriti nella XACMLRequest con questo formato
   org:govway:action:soapAction                                  Valore della SOAPAction                                                                                                    
   org:govway:action:gwService                                   Ruolo della transazione (inbound/outbound)                                                                                 
   org:govway:action:protocol                                    Modalità associata al servizio richiesto (es. spcoop)                                                                      
   org:govway:action:token:audience                              Destinatario del token                                                                                                     
   org:govway:action:token:scope                                 Lista di scopes                                                                                                            
   org:govway:action:token:jwt:claim:<nome>                      Tutti i claims presenti nel jwt validato                                                                                   
   org:govway:action:token:introspection:claim:<nome>            Tutti i claims presenti nella risposta del servizio di introspection                                                       
   *Sezione 'Subject'*
   org:govway:subject:organization                               Indica il soggetto fruitore
   org:govway:subject:organization:config:<nome>                 Proprietà configurate nel soggetto fruitore                 
   org:govway:subject:client                                     Identificativo dell'applicativo client
   org:govway:subject:client:config:<nome>                       Proprietà configurate nell'applicativo client                                                                     
   org:govway:subject:credential                                 Rappresenta la credenziale di accesso (username, subject o il principal) utilizzata dal client per richiedere il servizio
   org:govway:subject:role                                       Elenco dei ruoli che possiede il client che ha richiesto il servizio                                                       
   org:govway:subject:token:issuer                               Issuer del token                                                                                                           
   org:govway:subject:token:subject                              Subject del token
   org:govway:subject:token:username                             Username dell'utente cui è associato il token
   org:govway:subject:token:clientId                             Identificativo del client che ha negoziato il token                                                                        
   org:govway:subject:token:userInfo:fullName                    Nome completo dell'utente cui è associato il token                                                                         
   org:govway:subject:token:userInfo:firstName                   Nome dell'utente cui è associato il token                                                                                  
   org:govway:subject:token:userInfo:middleName                  Secondo nome (o nomi aggiuntivi) dell'utente cui è associato il token                                                      
   org:govway:subject:token:userInfo:familyName                  Cognome dell'utente cui è associato il token                                                                               
   org:govway:subject:token:userInfo:eMail                       Email dell'utente cui è associato il token
   org:govway:subject:token:userInfo:claim:<nome>                Tutti i claims presenti nella risposta del servizio di UserInfo
   org:govway:subject:attributes                                 Elenco dei nomi degli attributi recuperati interagendo con gli Attribute Authority configurati
   org:govway:subject:attribute:<nome>                           In caso sia configurato un unico Attribute Authority, nella configurazione relativa all':ref:`apiGwIdentificazioneAttributi`, tutti gli attributi recuperati saranno inseriti nella XACMLRequest con questo formato
   org:govway:subject:aa:<attributeAuthority>:attribute:<nome>   In caso siano configurate più Attribute Authority, nella configurazione relativa all':ref:`apiGwIdentificazioneAttributi`, tutti gli attributi recuperati saranno inseriti nella XACMLRequest con questo formato
   ============================================================  ===========

Di seguito un esempio di XACMLPolicy che autorizza le richieste dei
chiamanti che possiedono il ruolo 'Amministratore' ed uno tra i due
ruoli 'Operatore1' e 'Operatore2':

.. code-block:: xml

    <Policy xmlns="urn:oasis:names:tc:xacml:2.0:policy:schema:os" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" PolicyId="Policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides" xsi:schemaLocation="urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-policy-schema-os.xsd">
       <Target />
       <Rule Effect="Permit" RuleId="ok">
          <Condition>
             <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                    <SubjectAttributeDesignator AttributeId="org:govway:subject:role" DataType="http://www.w3.org/2001/XMLSchema#string" />
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Amministratore</AttributeValue>
                   </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                   <SubjectAttributeDesignator AttributeId="org:govway:subject:role" DataType="http://www.w3.org/2001/XMLSchema#string" />
                   <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Operatore1</AttributeValue>
                      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Operatore2</AttributeValue>
                   </Apply>
                </Apply>
             </Apply>
          </Condition>
        </Rule>
        <Rule Effect="Deny" RuleId="ko" />
    </Policy>

Un altro esempio di policy che verifica l'uguaglianza tra il valore del claim 'sub' presente nel token e quello fornito nel query parameter 'sub' è la seguente:

.. code-block:: xml

   <Policy PolicyId="Policy"
	RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides"
	xmlns="urn:oasis:names:tc:xacml:2.0:policy:schema:os" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="urn:oasis:names:tc:xacml:2.0:policy:schema:os http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-policy-schema-os.xsd">
	<Target />
	<Rule Effect="Permit" RuleId="ok">
		<Condition>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">

				 <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:any-of-any">
					<Function FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal"/>
					<ActionAttributeDesignator 
					    AttributeId="org:govway:action:url:parameter:sub"
					    DataType="http://www.w3.org/2001/XMLSchema#string"
					    MustBePresent="false"
					/>
					<ActionAttributeDesignator 
					    AttributeId="org:govway:action:token:introspection:claim:sub"
					    DataType="http://www.w3.org/2001/XMLSchema#string"
					    MustBePresent="false"
					/>
				</Apply>

			</Apply>
		</Condition>
	</Rule>
	<Rule Effect="Deny" RuleId="ko" />
   </Policy>
