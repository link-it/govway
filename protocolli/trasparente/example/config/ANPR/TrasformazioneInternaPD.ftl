<#function build_soap_fault errore codice>

	<#assign messageFactoryClass = class["org.openspcoop2.message.OpenSPCoop2MessageFactory"]>
	<#assign messageFactoryInstance=messageFactoryClass.getDefaultMessageFactory()/>

	<#assign messageTypeClass = class["org.openspcoop2.message.constants.MessageType"]>
	<#assign messageType=messageTypeClass.valueOf("SOAP_11")/>

	<#assign messageFault=messageFactoryInstance.createFaultMessage(messageType, true, errore)/>
	<#assign byteArrayOutputStream=new("java.io.ByteArrayOutputStream")/>
	<#assign tmp=messageFault.writeTo(byteArrayOutputStream, true)!/>
	<#assign tmp2=byteArrayOutputStream.flush()!/>
	<#assign tmp3=byteArrayOutputStream.close()!/>

	<#assign tmp4=errorHandler.setMessage(errore, byteArrayOutputStream.toByteArray(), "text/xml", codice)!/>

	<#stop errore>

</#function>
<#function build_govway_fault errore>

	<#assign tmp4=errorHandler.setError(errore)!/>

	<#stop errore>

</#function>
<#function build_govway_fault errore codice>

	<#assign tmp4=errorHandler.setError(errore, codice)!/>

	<#stop errore>

</#function>

<#function check_config>
	<#if config??>
		<#return true>
	<#else>
		<#assign errorMsg="Configurazione della fruizione non esistente"/>
		${build_govway_fault(errorMsg,"INTERNAL_REQUEST_ERROR")}
	</#if>
</#function>

<#function get_config id>
	<#assign idConfig = config[id]/>
	<#if idConfig??>
		<#return idConfig>
	<#else>
		<#assign errorMsg="Proprietà '"+id+"' non presente nella configurazione della fruizione"/>
		${build_govway_fault(errorMsg,"INTERNAL_REQUEST_ERROR")}
	</#if>
</#function>

<#function get_config_value id>
	<#assign idConfig = get_config(id)>
	<#assign idConfigIsQuery = idConfig?starts_with("query.")/>
	<#assign idConfigIsHeader = idConfig?starts_with("header.")/>
	<#if idConfigIsQuery >
		<#if query??>
			<#assign idConfigQueryName = idConfig?remove_beginning("query.")/>
			<#if query[idConfigQueryName]??>
				<#return query[idConfigQueryName]>
			<#else>
				<#assign errorMsg="Non è possibile ottenere il valore per '"+id+"'; non è stato fornito il parametro '"+idConfigQueryName+"' nella url di invocazione"/>
				${build_govway_fault(errorMsg,"BAD_REQUEST")}
			</#if>
		<#else>
			<#assign errorMsg="Non è possibile ottenere il valore per '"+id+"'; non sono stati forniti parametri nella url di invocazione"/>
			${build_govway_fault(errorMsg,"BAD_REQUEST")}
		</#if>
	<#elseif idConfigIsHeader>
		<#if header??>
			<#assign idConfigHeaderName = idConfig?remove_beginning("header.")/>
			<#if header[idConfigHeaderName]??>
				<#return header[idConfigHeaderName]>
			<#else>
				<#assign errorMsg="Non è possibile ottenere il valore per '"+id+"'; non è stato fornito il parametro '"+idConfigHeaderName+"' come header http"/>
				${build_govway_fault(errorMsg,"BAD_REQUEST")}
			</#if>
		<#else>
			<#assign errorMsg="Non è possibile ottenere il valore per '"+id+"'; non sono presenti header http"/>
			${build_govway_fault(errorMsg,"BAD_REQUEST")}
		</#if>		
	<#else>
		<#return idConfig>
	</#if>
</#function>

<#assign existsConfig = check_config>

<#assign keystorePath = get_config("IdPostazioneKeystorePath")>
<#assign keystorePassword = get_config("IdPostazioneKeystorePassword")>
<#assign keystoreType = get_config("IdPostazioneKeystoreType")>
<#assign keyAlias = get_config("IdPostazioneKeyAlias")>
<#assign keyPassword = get_config("IdPostazioneKeyPassword")>

<#assign idSede = get_config("IdSede")>
<#assign tmpIdSedeCheckEqualsCodMittente=get_config_value("IdSedeCheckEqualsCodMittente")/>
<#if tmpIdSedeCheckEqualsCodMittente?boolean>
	<#assign codMittente = xPath.read("//testataRichiesta/codMittente/text()")!/>
	<#if codMittente?has_content>
		<#assign tmpIdSedeCodMittenteMatch=(idSede == codMittente)/>
		<#if tmpIdSedeCheckEqualsCodMittente?boolean>
			<#assign tmpCodMittente=context?api.put("codMittente", codMittente)!/>
		<#else>
			<#assign errorMsg="Il codiceMittente presente nella richiesta '"+codMittente+"' risulta diverso da quello indicato in configurazione per l'idSede '"+idSede+"'"/>
			${build_govway_fault(errorMsg,"BAD_REQUEST")}
		</#if>
	<#else>
		<#assign errorMsg="Il codiceMittente non risulta presente nella richiesta"/>
		${build_govway_fault(errorMsg,"BAD_REQUEST")}
	</#if>
</#if>

<#assign idOperatore=get_config_value("IdOperatore")/>
<#assign tmpIdOperatore=context?api.put("IdOperatore", idOperatore)!/>

<#assign tmpIdPostazione=get_config_value("IdPostazione")/>
<#assign tmpIdPostazioneAddPrefixSede=get_config_value("IdPostazioneAddPrefixSede")/>
<#if tmpIdPostazioneAddPrefixSede?boolean>
	<#assign tmpIdPostazioneConSede=idSede+"-"+tmpIdPostazione/>
	<#assign tmpIdPostazione=context?api.put("IdPostazione", tmpIdPostazioneConSede)!/>
<#else>
	<#assign tmpIdPostazione=context?api.put("IdPostazione", tmpIdPostazione)!/>
</#if>

<#assign tmpIdPostazioneSignature=get_config_value("IdPostazioneSignature")/>
<#assign tmpIdPostazioneSignatureMatch=(tmpIdPostazioneSignature == "FirmaDelegataGateway")/>
<#if tmpIdPostazioneSignatureMatch>
	<#attempt>
		<#assign keystore=new("org.openspcoop2.utils.certificate.KeyStore",keystorePath,keystoreType,keystorePassword)/>
	<#recover>
        	<#assign errorMsg="Firma PKCS7 dell'IdPostazione '"+context["IdPostazione"]+"' fallita; accesso keystore '"+keystorePath+"' non riuscito"/>
		${build_govway_fault(errorMsg,"INTERNAL_REQUEST_ERROR")}
	</#attempt>
	<#attempt>
		<#assign signerPKCS7=new("org.openspcoop2.utils.security.PKCS7Signature",keystore,keyAlias,keyPassword)/>
	<#recover>
        	<#assign errorMsg="Firma PKCS7 dell'IdPostazione '"+context["IdPostazione"]+"' fallita; inizializzazione engine PKCS7 di firma fallito"/>
		${build_govway_fault(errorMsg,"INTERNAL_REQUEST_ERROR")}
	</#attempt>
	<#attempt>
		<#assign idPostazioneFirmataByteArray=signerPKCS7?api.sign(context["IdPostazione"],"UTF8","SHA256withRSA")/>
	<#recover>
        	<#assign errorMsg="Firma PKCS7 dell'IdPostazione '"+context["IdPostazione"]+"' fallita; firma PKCS7 fallita"/>
		${build_govway_fault(errorMsg,"INTERNAL_REQUEST_ERROR")}
	</#attempt>
	<#attempt>
		<#assign base64Utilities = class["org.openspcoop2.utils.io.Base64Utilities"]>
		<#assign idPostazioneFirmata=base64Utilities.encodeAsString(idPostazioneFirmataByteArray)/>
	<#recover>
        	<#assign errorMsg="Firma PKCS7 dell'IdPostazione '"+context["IdPostazione"]+"' fallita; fallita la codifica in base64"/>
		${build_govway_fault(errorMsg,"INTERNAL_REQUEST_ERROR")}
	</#attempt>
	<#assign tmpIdPostazioneFirmato=context?api.put("IdPostazioneFirmato", idPostazioneFirmata)!/>
<#else>
	<#assign tmpIdPostazioneFirmato=context?api.put("IdPostazioneFirmato", tmpIdPostazioneSignature)!/>
</#if>

<#assign applicativo = busta.servizioApplicativoFruitore/>
<#if applicativo??>
	<#assign idApplicazioneConfigPropertyName = "IdApplicazione_"+applicativo/>
	<#assign idApplicazione=get_config_value(idApplicazioneConfigPropertyName)/>
	<#assign tmpIdApplicazione=context?api.put("IdApplicazione", idApplicazione)!/>
<#else>
	<#assign errorMsg="Applicativo che ha invocato la fruizione non identificato"/>
	${build_govway_fault(errorMsg,"BAD_REQUEST")}
</#if>
