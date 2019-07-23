Nuova funzionaltà di esposizione dei WSDL
-----------------------------------------

Aggiunta la funzionalità di esposizione dell'interfaccia WSDL di una API SOAP, registrata su GovWay.
È adesso possibile ottenibile il wsdl attraverso una invocazione HTTP GET utilizzando la medesima url di invocazione arricchita del prefisso '?wsdl'.

.. note::
   Nell'installazione di default la gestione delle richieste HTTP GET con prefisso '?wsdl' è disabilitata e tali richieste ottengono un errore 'HTTP 404 Not Found'.

   Per abilitare la funzionalità è possibile agire sul file esterno '/etc/govway/govway_local.properties' abilitando le proprietà 'org.openspcoop2.pdd.pd.generateWsdl' e 'org.openspcoop2.pdd.pa.generateWsdl'
