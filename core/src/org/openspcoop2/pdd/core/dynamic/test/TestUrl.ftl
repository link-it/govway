<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<soapenv:Body>
			<prova>${urlRegExp.read(".+azione=([^&]*).*")}</prova>
			<prova2>${urlRegExp.read(".+prova=([^&]*).*")}</prova2>
		</soapenv:Body>
</soapenv:Envelope>
	