.. _configSpecificaValidazioneSoap:

Configurazione per API SOAP
~~~~~~~~~~~~~~~~~~~~~~~~~~~

È possibile configurare il tipo di validazione attuata su API SOAP registrando le seguenti :ref:`configProprieta` sull'erogazione o sulla fruizione:

- *validation.soapAction.enabled* : consente di disabilitare la verifica della SOAPAction. I valori associabili alle proprietà sono 'true' o 'false';

- *validation.rpc.rootElementUnqualified.accept* : consente di indicare se devono essere accettate o meno richieste RPC il cui root-element non appartiene ad alcun namespace. Il comportamento di default del prodotto (configurabile anche a livello generale agendo sulla proprietà 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpc.rootElement.unqualified.accept' in govway_local.properties) è di accettare le richieste per essere compatibile con framework soap datati. I valori associabili alle proprietà sono 'true' o 'false'.
