<#assign secToken = header["X-Security-Token"]!/>
<#if secToken?has_content>
	<#assign secTokenStringDebug = " [X-Security-Token: "+secToken+"]"/>
	<#list secToken?split(";") as token>
		<#if token?index == 0>
			<#assign tipoToken=token/>
		<#elseif token?index == 1>
			<#assign nomeToken=token/>
		<#elseif token?index == 2>
			<#assign valoreToken=token/>
		<#else>
			<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Trovata porzione interna (separatore ';') alla posizione "+token?index+" non attesa "+token/>
			<#stop msgError+secTokenStringDebug>
		</#if>
	</#list>
	<#if tipoToken??>
		<#assign cookieMatch=(tipoToken?capitalize == "COOKIE"?capitalize)/>
		<#assign headerSoapMatch=(tipoToken?capitalize == "HEADER-SOAP"?capitalize)/>
		<#assign jwtMatch=(tipoToken?capitalize == "JWT"?capitalize)/>
		<#if cookieMatch>
			<#if nomeToken??>
				<#if valoreToken??>
					<#assign cookieValue=nomeToken+"="+valoreToken/>
					<#assign tmp=request?api.addTransportHeader("Cookie", cookieValue)!/>
				<#else>
					<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Valore del token '"+tipoToken+"' sconosciuto"/>
					<#stop msgError+secTokenStringDebug>
				</#if>
			<#else>
				<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Nome del token '"+tipoToken+"' sconosciuto"/>
				<#stop msgError+secTokenStringDebug>
			</#if>
		<#elseif headerSoapMatch>
			<#if request.isSoap()>
				<#if request.isSoap11()>
					<#assign soapNamespace="http://schemas.xmlsoap.org/soap/envelope/"/>
					<#assign soapActorNode="actor"/>
					<#assign soapMustUnderstandTrue="1"/>
					<#assign soapMustUnderstandFalse="1"/>
				<#else>
					<#assign soapNamespace="http://www.w3.org/2003/05/soap-envelope"/>
					<#assign soapActorNode="role"/>
					<#assign soapMustUnderstandTrue="true"/>
					<#assign soapMustUnderstandFalse="false"/>
				</#if>
				<#if nomeToken??>
					<#assign custom336Match=(nomeToken?capitalize == "3.3.6"?capitalize)/>
					<#if custom336Match>
						<#if valoreToken??>
							<#assign soapNode="      <wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" 
			xmlns:soapenv=\""+soapNamespace+"\"
			soapenv:mustUnderstand=\""+soapMustUnderstandTrue+"\">
			 <wsse:BinarySecurityToken xmlns:wsst=\"http://www.govway.org/example/tokentype/3.3.6\"
				                   xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"
				                   EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\"
				                   ValueType=\"wsst:EXAMPLE\"
				                   wsu:Id=\"SecurityToken-fe0d4f93-60d1-466c-bd8d-59412f553ae9\">"+valoreToken+"</wsse:BinarySecurityToken>
		      </wsse:Security>" />
							<#assign tmp=request?api.disableExceptionIfFoundMoreSecurityHeader()!/>
							<#assign tmp=request?api.addSoapHeader(soapNode)!/>
						<#else>
							<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Valore del token '"+tipoToken+"' sconosciuto"/>
							<#stop msgError+secTokenStringDebug>
						</#if>
					<#else>
						<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Nome token '"+nomeToken+"' sconosciuto (valori attesi: 3.3.6)"/>
						<#stop msgError+secTokenStringDebug>
					</#if>
				<#else>
					<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Nome del token '"+tipoToken+"' sconosciuto"/>
					<#stop msgError+secTokenStringDebug>
				</#if>
			<#else>
				<#assign msgError = "Header 'X-Security-Token' richiede la generazione di un token SOAP su un messaggio REST"/>
				<#stop msgError+secTokenStringDebug>
			</#if>
		<#elseif jwtMatch>
			<#if nomeToken??>
				<#assign bearerMatch=(nomeToken?capitalize == "Bearer"?capitalize)/>
				<#if bearerMatch>
					<#if valoreToken??>
						<#assign bearerValue="Bearer "+valoreToken/>
						<#assign tmp=request?api.removeTransportHeader("Authorization")!/>
						<#assign tmp=request?api.addTransportHeader("Authorization", bearerValue)!/>
					<#else>
						<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Valore del token '"+tipoToken+"' sconosciuto"/>
						<#stop msgError+secTokenStringDebug>
					</#if>
				<#else>
					<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Nome token '"+nomeToken+"' sconosciuto (valore atteso per tipo token 'JWT': Bearer)"/>
					<#stop msgError+secTokenStringDebug>
				</#if>
			<#else>
				<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Nome del token '"+tipoToken+"' sconosciuto"/>
				<#stop msgError+secTokenStringDebug>
			</#if>
		<#else>
			<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Tipo token '"+tipoToken+"' sconosciuto (valori attesi: COOKIE/HEADER-SOAP/JWT)"/>
			<#stop msgError+secTokenStringDebug>
		</#if>
	<#else>
		<#assign msgError = "Header 'X-Security-Token' contiene un formato diverso da quello atteso. Comprensione tipo token (COOKIE/HEADER-SOAP/JWT) fallita"/>
		<#stop msgError+secTokenStringDebug>
	</#if>
</#if>


