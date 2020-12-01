curl -v -X POST "http://localhost:8080/govway/ENTE/api-soap-status/v1" \
-H "accept: text/xml" \
-H "Content-Type: text/xml" \
-H "SOAPAction: \"status\"" \
-d '
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <soapenv:Body>
	   <ns1:getStatus xmlns:ns1="https://govway.org/apiStatus"/> 
	</soapenv:Body>
</soapenv:Envelope>
'
