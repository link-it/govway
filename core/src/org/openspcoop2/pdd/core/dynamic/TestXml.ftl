<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<soapenv:Body>
			<prova>${jsonpath.read("$.Year")}</prova>
			<prova2>${jsonPath.read("$.Acronym")}</prova2>
			<#assign x = jsonPath.readList("$.List")>
			<#list x as value>
			<list>${value}</list>
			</#list>
		</soapenv:Body>
</soapenv:Envelope>
	