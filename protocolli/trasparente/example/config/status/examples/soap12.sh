curl -v -X POST "http://localhost:8080/govway/ENTE/api-soap-status/v1" \
-H "Content-Type: application/soap+xml" \
-H "SOAPAction: \"status\"" \
-d '
<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <soapenv:Body>
	   <ns1:getStatus xmlns:ns1="https://govway.org/apiStatus"/> 
	</soapenv:Body>
</soapenv:Envelope>
'
