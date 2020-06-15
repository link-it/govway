.. _soapFault:

SOAP Fault
~~~~~~~~~~

Negli errori generati da GovWay, relativi alla gestione di richieste verso API di tipo SOAP, il payload http ritornato al chiamante contiene un SOAP Fault.

Gli elementi del fault sono valorizzati come segue:

- *faultactor* (Soap 1.1) o *Role* (Soap 1.2) possiede il valore *http://govway.org/integration*.
- *faultcode* (Soap 1.1) o *Code/Subcode* (Soap 1.2): contiene uno standard SOAP fault code ( Server/Client per Soap 1.1, Receiver/Sender per Soap 1.2) concatenato con un codice di errore di GovWay che specifica la problematica rilevata (es. AuthenticationRequired, TokenExpired, InvalidRequestContent ...). Tutti i codici di errore vengono descritti nella sezione :ref:`gestioneErrori`.
- *faultstring* (Soap 1.1) o *Reason* (Soap 1.2):  fornisce informazioni di dettaglio sull'errore avvenuto.
- *detail*: è presente l'oggetto *Problem Details*, nella rappresentazione xml descritta nella sezione :ref:`rfc7807`. 

.. note::
      Il formato di errore (*Soap 1.1* o *Soap 1.2*) assume lo stesso formato della richiesta.


Di seguito viene riportato un esempio di errore rilevato per una API SOAP 1.1:

::

    HTTP/1.1 500 Internal Server Error
    Server: GovWay
    Transfer-Encoding: chunked
    GovWay-Transaction-ErrorType: InvalidRequestContent
    GovWay-Transaction-ID: b76b4d1b-cd9d-43a0-bea2-1f352f1e71dd
    Content-Type: text/xml
    Date: Thu, 28 May 2020 15:59:14 GMT
 
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
      <SOAP-ENV:Body>
        <SOAP-ENV:Fault>
          <faultcode>SOAP-ENV:Client.InvalidRequestContent</faultcode>
          <faultstring>Received request is not conform to API specification</faultstring>
          <faultactor>http://govway.org/integration</faultactor>
          <detail>
             <problem xmlns="urn:ietf:rfc:7807">
                <type>https://govway.org/handling-errors/400/InvalidRequestContent.html</type>
                <title>InvalidRequestContent</title>
                <status>400</status>
                <detail>Request content not conform to API specification</detail>
                <govway_id>9876b03e-0377-4a02-9fb8-07094b0cdf06</govway_id>
             </problem>
          </detail>
        </SOAP-ENV:Fault>
      </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

Lo stesso tipo di errore, rilevato per una API SOAP 1.2, viene riportato di seguito:

::

    HTTP/1.1 500 Internal Server Error
    Server: GovWay
    Transfer-Encoding: chunked
    GovWay-Transaction-ErrorType: InvalidRequestContent
    GovWay-Transaction-ID: b76b4d1b-cd9d-43a0-bea2-1f352f1e71dd
    Content-Type: application/soap+xml
    Date: Thu, 28 May 2020 15:59:14 GMT
 
    <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
      <env:Body>
        <env:Fault>
	   <env:Code>
	      <env:Value>env:Sender</env:Value>
	      <env:Subcode>
		<env:Value xmlns:integration="http://govway.org/integration/fault">
		   integration:InvalidRequestContent
		</env:Value>
	      </env:Subcode>
	   </env:Code>
	   <env:Reason>
	      <env:Text xml:lang="en-US">Operation undefined in the API specification</env:Text>
	   </env:Reason>
	   <env:Role>http://govway.org/integration</env:Role>
	   <env:Detail>
             <problem xmlns="urn:ietf:rfc:7807">
                <type>https://govway.org/handling-errors/400/InvalidRequestContent.html</type>
                <title>InvalidRequestContent</title>
                <status>400</status>
                <detail>Request content not conform to API specification</detail>
                <govway_id>9876b03e-0377-4a02-9fb8-07094b0cdf06</govway_id>
             </problem>
          </env:Detail>
        </env:Fault>
      </env:Body>
    </env:Envelope>

