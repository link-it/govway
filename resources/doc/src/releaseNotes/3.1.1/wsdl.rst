Nuova funzionaltà di esposizione dei WSDL
-----------------------------------------

.. deprecated:: 3.3.0
   Il WSDL generato risulta diverso da quello effettivamente esposto dal backend, comportando potenziali errori di interoperabilità.

Aggiunta la funzionalità di esposizione dell'interfaccia WSDL di una API SOAP.

È adesso possibile ottenere il file wsdl attraverso una invocazione HTTP
GET, utilizzando la url di invocazione dell'API, arricchita del prefisso
'?wsdl'.

.. note::
   Nell'installazione di default la gestione delle richieste HTTP GET con prefisso '?wsdl' è disabilitata e tali richieste ottengono un errore 'HTTP 404 Not Found'.

   Per abilitare la funzionalità è possibile agire sul file esterno '/etc/govway/govway_local.properties' abilitando le proprietà 'org.openspcoop2.pdd.pd.generateWsdl' e 'org.openspcoop2.pdd.pa.generateWsdl'
