.. _erroriGovWay:

Errori generati da GovWay
-------------------------

La gestione dei casi di errore, nelle comunicazioni mediate da un Gateway, deve tener conto di ulteriori casi di errore che possono presentarsi rispetto al dialogo diretto tra gli applicativi.
Oltre agli errori già previsti nei descrittori del servizio, gli applicativi client possono pertanto ricevere due tipi di errori generati direttamente da GovWay:

- *Errori Client*: sono identificabili da un codice http 4xx su API REST o da un fault code 'Client' su API SOAP. Indicano che GovWay ha rilevato problemi nella richiesta effettuata dal client (es. errore autenticazione, autorizzazione, validazione contenuti...).

- *Errori Server*: sono identificabili dai codici http 502, 503 e 504 per le API REST o da un fault code 'Server' generato dal Gateway e restituito con codice http 500 per le API SOAP.

La codifica degli errori prodotta dal Gateway permette alle
applicazioni client di discriminare tra errori causati da una richiesta
errata, per i quali è quindi necessario intervenire sull'applicazione
client prima di effettuare nuovi invii, ed errori dovuti allo stato
dei servizi invocati, per i quali è invece possibile continuare ad
effettuare la richiesta. Maggiori dettagli sulla logica di re-invio
delle richieste vengono riportati nella sezione :ref:`gestioneErrori`.

Per ciascun errore GovWay riporta le seguenti informazioni:

- Un codice http su API REST o un fault code su API SOAP come descritto in precedenza.
- Un codice di errore, indicato nell'header http 'GovWay-Transaction-ErrorType', che riporta l'errore rilevato dal gateway (es. AuthenticationRequired, TokenExpired, InvalidRequestContent ...). 
- Un identificativo di transazione, indicato nell'header http 'GovWay-Transaction-ID', che identifica la transazione in errore, utile principalmente per indagini diagnostiche.
- Un payload http, contenente maggiori dettagli sull'errore, opportunamente codificato per API REST (:ref:`rfc7807`) o SOAP (:ref:`soapFault`).

.. note::
      Il codice di errore e l'identificativo di transazione vengono riportati sia tramite header http che all'interno del payload.

Di seguito viene riportato un esempio di errore generato in seguito al rilevamento di una richiesta non conforme all'interfaccia API REST:

::

    HTTP/1.1 400 Bad Request
    Server: GovWay
    Transfer-Encoding: chunked
    GovWay-Transaction-ErrorType: InvalidRequestContent
    GovWay-Transaction-ID: b76b4d1b-cd9d-43a0-bea2-1f352f1e71dd
    Content-Type: application/problem+json
    Date: Thu, 28 May 2020 15:59:14 GMT
 
    {
    	"type":"https://govway.org/handling-errors/400/InvalidRequestContent.html",
	"title":"InvalidRequestContent",
	"status":400,
	"detail":"Request content not conform to API specification",
	"govway_id":"b76b4d1b-cd9d-43a0-bea2-1f352f1e71dd"
    }


Lo stesso tipo di errore, rilevato per una API SOAP, viene riportato di seguito:

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


.. toctree::
        :maxdepth: 2
        
	gestioneErrori
        rfc7807
	soapFault
	codiciErroreSpecifici
