/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */




package org.openspcoop2.pdd.core;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.driver.AccordoServizioUtils;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.rest.AccordoServizioWrapper;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.openapi.UniqueInterfaceGenerator;
import org.openspcoop2.utils.openapi.UniqueInterfaceGeneratorConfig;
import org.openspcoop2.utils.openapi.validator.OpenapiApi4jValidatorConfig;
import org.openspcoop2.utils.openapi.validator.OpenapiApiValidatorConfig;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.BinaryHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.BinaryHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.wsdl.WSDLException;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.slf4j.Logger;


/**
 * Classe utilizzata per validare i messaggi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lezza Aldo (lezza@openspcoop.org)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidatoreMessaggiApplicativiRest {

	/** Registro dei Servizi */
	private RegistroServiziManager registroServiziManager;

	/** SOAPEnvelope */
	private OpenSPCoop2Message message;
	/** Associato al servizio */
	private org.openspcoop2.core.registry.rest.AccordoServizioWrapper accordoServizioWrapper = null;
	/** Logger */
	private Logger logger = null;
	/** ProtocolFactory */
	private IProtocolFactory<?> protocolFactory;
	/** PddContext */
	private PdDContext pddContext;
	/** UseInterface */
	private boolean useInterface;
	/** OpenApi4j config */
	private OpenapiApi4jValidatorConfig configOpenApi4j;
	/** OpenSPCoop2Properties */
	private OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
	
	

	
	/* ------ Costruttore -------------- */
	public ValidatoreMessaggiApplicativiRest(RegistroServiziManager registro,IDServizio idServizio,
			OpenSPCoop2Message message,boolean readInterfaceAccordoServizio, List<Proprieta> proprieta, 
			IProtocolFactory<?> protocolFactory, PdDContext pddContext)throws ValidatoreMessaggiApplicativiException{
		
		if(registro==null){
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("Reader del Registro dei Servizi non fornito");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}
		
		if(idServizio==null || idServizio.getSoggettoErogatore()==null || idServizio.getTipo()==null || idServizio.getNome()==null || idServizio.getVersione()==null ||
				idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null){
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("ID del servizio da validare, non fornito");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}
		
		this.message = message;

		this.registroServiziManager = registro;
		
		this.logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		this.protocolFactory = protocolFactory;
		
		this.pddContext = pddContext;
		
		try{
			this.useInterface = readInterfaceAccordoServizio;
			
			boolean processIncludeForOpenApi = true;
			if(this.op2Properties.isValidazioneContenutiApplicativi_openApi_useOpenApi4j()) {
				this.configOpenApi4j = new OpenapiApi4jValidatorConfig();
				this.configOpenApi4j.setUseOpenApi4J(true);
				this.configOpenApi4j.setMergeAPISpec(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec());
				this.configOpenApi4j.setValidateAPISpec(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec());
				this.configOpenApi4j.setValidateRequestQuery(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery());
				this.configOpenApi4j.setValidateRequestHeaders(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders());
				this.configOpenApi4j.setValidateRequestCookie(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie());
				this.configOpenApi4j.setValidateRequestBody(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody());
				this.configOpenApi4j.setValidateResponseHeaders(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders());
				this.configOpenApi4j.setValidateResponseBody(this.op2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody());
				updateConfigOpenApi4j(proprieta, this.configOpenApi4j); // aggiorno anche se utilizzarlo o meno
				if(this.configOpenApi4j.isUseOpenApi4J()) {
					processIncludeForOpenApi = false;
				}
			}
			
			if(readInterfaceAccordoServizio){
				this.accordoServizioWrapper = this.registroServiziManager.getRestAccordoServizio(idServizio,InformationApiSource.SPECIFIC,true,processIncludeForOpenApi);
			}else{
				this.accordoServizioWrapper = this.registroServiziManager.getRestAccordoServizio(idServizio,InformationApiSource.REGISTRY,true,processIncludeForOpenApi);
			}
		}catch(DriverRegistroServiziNotFound e){
			this.logger.error("Riscontrato errore durante la ricerca del formato di specifica che definisce l'accordo di servizio: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("Riscontrato errore durante la ricerca del formato di specifica: "+e.getMessage(),e);
			ex.setErrore(ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione());
			throw ex;
		}catch(DriverRegistroServiziException e){
			
			FormatoSpecifica formatoSpecifica = null;
			try {
				AccordoServizioParteSpecifica asps = this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false);
				AccordoServizioParteComune apc = this.registroServiziManager.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false);
				formatoSpecifica = apc.getFormatoSpecifica();
			}catch(Exception eIgnore) {}
			
			this.logger.error("Riscontrato errore durante l'inizializzazione: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException("Riscontrato errore durante l'inizializzazione: "+e.getMessage(),e);
			if(e.getMessage()!=null && formatoSpecifica!=null){
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(formatoSpecifica.getValue()));
				throw ex;
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_530_COSTRUZIONE_INTERFACCIA_FALLITA));
				throw ex;
			}
		}
		

	}
	
	
	
	
	
	
	
	/* ----------- INTERFACCE ---------------- */
	public AccordoServizioWrapper getAccordoServizioWrapper() {
		return this.accordoServizioWrapper;
	}
	
	
	
	
	
	
	
	
	
	/* -------------- VALIDAZIONE XSD --------------------- */
	
	public void validateWithSchemiXSD(boolean isRichiesta) throws ValidatoreMessaggiApplicativiException{
		
		if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())){
			throw new ValidatoreMessaggiApplicativiException("Tipo di validazione non supportata con Service Binding SOAP");
		}
		if(!MessageType.XML.equals(this.message.getMessageType())){
			throw new ValidatoreMessaggiApplicativiException("Tipo di validazione non supportata con Service Binding REST e MessageType '"+this.message.getMessageType()+"'");
		}
		
		try{
			if(this.message.castAsRest().hasContent()==false) {
				return;
			}
		}catch(Exception e){
			this.logger.error("Riscontrato errore durante il controllo del messaggio: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException("Riscontrato errore durante l'inizializzazione: "+e.getMessage(),e);
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_530_COSTRUZIONE_INTERFACCIA_FALLITA));
			throw ex;
		}
		
		AbstractValidatoreXSD validatoreBodyApplicativo = null;
		try{
			
			AccordoServizioUtils accordoServizioUtils = new AccordoServizioUtils(this.message.getFactory(), this.logger);
			boolean fromBytes = this.accordoServizioWrapper.isRegistroServiziDB();
			javax.xml.validation.Schema schema = accordoServizioUtils.buildSchema(this.accordoServizioWrapper.getAccordoServizio(), fromBytes);
			
			try{
				if(schema!=null){
					validatoreBodyApplicativo = new ValidatoreXSD(schema);
				}else{
					throw new Exception("Schema non costruito?");
				}
			}catch(Exception e){
				throw new WSDLException("Riscontrato errore durante la costruzione del validatore XSD per il contenuto applicativo: "+e.getMessage(),e);
			}
			
		}catch(Exception e){
			this.logger.error("validateWithSchemiXSD failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage(),e);
			ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD));
			throw ex;
		}
		
		
		try {
			validatoreBodyApplicativo.valida(this.message.castAsRestXml().getContent());	
		}catch(Exception e){ 
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage(),e);
			
			String messaggioErrore = e.getMessage();
			boolean overwriteMessageError = false;
			try {
				messaggioErrore = ErroriProperties.getInstance(this.logger).getGenericDetails_noWrap(isRichiesta ? IntegrationFunctionError.INVALID_REQUEST_CONTENT : IntegrationFunctionError.INVALID_RESPONSE_CONTENT);
				messaggioErrore = messaggioErrore+": "+e.getMessage();
				overwriteMessageError = true;
			}catch(Exception excp) {}
			
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD, messaggioErrore, overwriteMessageError));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD, messaggioErrore, overwriteMessageError));
			}
			throw ex;
		}
		
	}

	
	
	
	
	/* -------------- VALIDAZIONE Interface --------------------- */
	
	public void validateRequestWithInterface(boolean portaApplicativa) throws ValidatoreMessaggiApplicativiException {
		this.validateWithInterface(true, portaApplicativa, null);
	}
	public void validateResponseWithInterface(OpenSPCoop2Message requestMessage, boolean portaApplicativa) throws ValidatoreMessaggiApplicativiException {
		this.validateWithInterface(false, portaApplicativa, requestMessage);
	}
	private void validateWithInterface(boolean isRichiesta, boolean portaApplicativa, OpenSPCoop2Message requestMessage) throws ValidatoreMessaggiApplicativiException {
		
		if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())){
			throw new ValidatoreMessaggiApplicativiException("Tipo di validazione non supportata con Service Binding SOAP");
		}
		
		String interfaceType = null;
		ApiFormats format = null;
		ApiValidatorConfig validatorConfig = null;
		boolean openapi4j = false;
		Api api = this.accordoServizioWrapper.getApi();
		switch (this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()) {
		case WADL:
			interfaceType = "Interfaccia WADL";
			format=ApiFormats.WADL;
			validatorConfig = new ApiValidatorConfig();
			break;
		case SWAGGER_2:
			interfaceType = "Interfaccia Swagger 2";
			format=ApiFormats.SWAGGER_2;
			validatorConfig = new OpenapiApiValidatorConfig();
			((OpenapiApiValidatorConfig)validatorConfig).setJsonValidatorAPI(this.op2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator());
			break;
		case OPEN_API_3:
			interfaceType = "Interfaccia OpenAPI 3";
			format=ApiFormats.OPEN_API_3;
			validatorConfig = new OpenapiApiValidatorConfig();
			((OpenapiApiValidatorConfig)validatorConfig).setJsonValidatorAPI(this.op2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator());
			if(this.useInterface && this.configOpenApi4j!=null) {
				openapi4j = this.configOpenApi4j.isUseOpenApi4J();
				if(openapi4j) {
					((OpenapiApiValidatorConfig)validatorConfig).setOpenApi4JConfig(this.configOpenApi4j);
					if(this.configOpenApi4j.isMergeAPISpec() && api instanceof OpenapiApi) {
						OpenapiApi openapi = (OpenapiApi) api;
						if(openapi.getValidationStructure()==null) {
							api = this.mergeApiSpec(openapi, this.accordoServizioWrapper);
						}
					}
				}
			}
			break;
		default:
			// altre interfacce non supportate per rest
			throw new ValidatoreMessaggiApplicativiException("Tipo di interfaccia ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] non supportata");
		}
		
		IApiValidator apiValidator = null;
		try {
			apiValidator = ApiFactory.newApiValidator(format);
			validatorConfig.setXmlUtils(XMLUtils.getInstance(this.message.getFactory()));
			validatorConfig.setVerbose(this.op2Properties.isValidazioneContenutiApplicativi_debug());
			validatorConfig.setPolicyAdditionalProperties(this.op2Properties.getValidazioneContenutiApplicativi_json_policyAdditionalProperties());
			apiValidator.init(this.logger, api, validatorConfig);
		}catch(Exception e){
			this.logger.error("validateWithInterface failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage(),e);
			ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(interfaceType));
			throw ex;
		}
			
		
		try{
			org.openspcoop2.utils.transport.TransportRequestContext transportContext = null;
			if(isRichiesta) {
				transportContext = this.message.getTransportRequestContext();
			}
			else {
				transportContext = requestMessage.getTransportRequestContext();
			}
			String normalizedInterfaceName = null;
			try {
				String interfaceName = null;
				if(this.pddContext!=null && this.pddContext.containsKey(CostantiPdD.NOME_PORTA_INVOCATA)) {
					interfaceName = (String) this.pddContext.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
				}
				else {
					interfaceName = transportContext.getInterfaceName();
				}
				if(transportContext.getInterfaceName()!=null) {
					PorteNamingUtils namingUtils = new PorteNamingUtils(this.protocolFactory);
					if(portaApplicativa){
						normalizedInterfaceName = namingUtils.normalizePA(interfaceName);
					}
					else {
						normalizedInterfaceName = namingUtils.normalizePD(interfaceName);
					}
				}
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
			String path = RestUtilities.getUrlWithoutInterface(transportContext, normalizedInterfaceName);
			HttpRequestMethod httpMethod = HttpRequestMethod.valueOf(transportContext.getRequestType());
			
			if(isRichiesta) {
				HttpBaseRequestEntity<?> httpRequest = null;
				switch (this.message.getMessageType()) {
				case XML:
					httpRequest = new ElementHttpRequestEntity();
					if(this.message.castAsRest().hasContent()) {
						if(openapi4j) {
							httpRequest = new BinaryHttpRequestEntity();
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							this.message.writeTo(bout, false);
							bout.flush();
							bout.close();
							((BinaryHttpRequestEntity)httpRequest).setContent(bout.toByteArray());
						}
						else {
							((ElementHttpRequestEntity)httpRequest).setContent(this.message.castAsRestXml().getContent());
						}
					}
					break;
				case JSON:
					httpRequest = new TextHttpRequestEntity();
					if(this.message.castAsRest().hasContent()) {
						((TextHttpRequestEntity)httpRequest).setContent(this.message.castAsRestJson().getContent());
					}
					break;
				case BINARY:
					httpRequest = new BinaryHttpRequestEntity();
					if(this.message.castAsRest().hasContent()) {
						((BinaryHttpRequestEntity)httpRequest).setContent(this.message.castAsRestBinary().getContent());
					}
					break;
				case MIME_MULTIPART:
					httpRequest = new BinaryHttpRequestEntity();
					if(this.message.castAsRest().hasContent()) {
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						this.message.writeTo(bout, false);
						bout.flush();
						bout.close();
						((BinaryHttpRequestEntity)httpRequest).setContent(bout.toByteArray());
					}
					break;
				default:
					break;
				}
				httpRequest.setContentType(this.message.getContentType());
				httpRequest.setParametersTrasporto(this.message.getTransportRequestContext().getParametersTrasporto());
				httpRequest.setParametersQuery(this.message.getTransportRequestContext().getParametersFormBased());
				httpRequest.setUrl(path);
				httpRequest.setMethod(httpMethod);
				apiValidator.validate(httpRequest);
			}
			else {
				HttpBaseResponseEntity<?> httpResponse = null;
				switch (this.message.getMessageType()) {
				case XML:
					httpResponse = new ElementHttpResponseEntity();
					if(this.message.castAsRest().hasContent()) {
						if(openapi4j) {
							httpResponse = new BinaryHttpResponseEntity();
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							this.message.writeTo(bout, false);
							bout.flush();
							bout.close();
							((BinaryHttpResponseEntity)httpResponse).setContent(bout.toByteArray());
						}
						else {
							((ElementHttpResponseEntity)httpResponse).setContent(this.message.castAsRestXml().getContent());
						}
					}
					break;
				case JSON:
					httpResponse = new TextHttpResponseEntity();
					if(this.message.castAsRest().hasContent()) {
						((TextHttpResponseEntity)httpResponse).setContent(this.message.castAsRestJson().getContent());
					}
					break;
				case BINARY:
					httpResponse = new BinaryHttpResponseEntity();
					if(this.message.castAsRest().hasContent()) {
						((BinaryHttpResponseEntity)httpResponse).setContent(this.message.castAsRestBinary().getContent());
					}
					break;
				case MIME_MULTIPART:
					httpResponse = new BinaryHttpResponseEntity();
					if(this.message.castAsRest().hasContent()) {
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						this.message.writeTo(bout, false);
						bout.flush();
						bout.close();
						((BinaryHttpResponseEntity)httpResponse).setContent(bout.toByteArray());
					}
					break;
				default:
					break;
				}
				httpResponse.setContentType(this.message.getContentType());
				httpResponse.setParametersTrasporto(this.message.getTransportResponseContext().getParametersTrasporto());
				httpResponse.setUrl(path);
				httpResponse.setMethod(httpMethod);
				httpResponse.setStatus(Integer.parseInt(this.message.getTransportResponseContext().getCodiceTrasporto()));
				apiValidator.validate(httpResponse);
			}
			
		}catch(Throwable e ){ // WSDLValidatorException
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage(),e);
			
			String messaggioErrore = e.getMessage();
			boolean overwriteMessageError = false;
			try {
				messaggioErrore = ErroriProperties.getInstance(this.logger).getGenericDetails_noWrap(isRichiesta ? IntegrationFunctionError.INVALID_REQUEST_CONTENT : IntegrationFunctionError.INVALID_RESPONSE_CONTENT);
				messaggioErrore = messaggioErrore+": "+e.getMessage();
				overwriteMessageError = true;
			}catch(Exception excp) {}
			
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(interfaceType,messaggioErrore, overwriteMessageError));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(interfaceType, messaggioErrore, overwriteMessageError));
			}
			throw ex;
		}finally {
			try {
				apiValidator.close(this.logger, this.accordoServizioWrapper.getApi(), validatorConfig);
			}catch(Exception e){
				this.logger.error("validateWithInterface close error: "+e.getMessage(),e);
			}
		}
		
	}
	
	private Api mergeApiSpec(OpenapiApi api, org.openspcoop2.core.registry.rest.AccordoServizioWrapper accordoServizioWrapper) {
		
		YAMLUtils yamlUtils = YAMLUtils.getInstance();
		JSONUtils jsonUtils = JSONUtils.getInstance();
								
		Map<String, String> attachments = new HashMap<String, String>();
		if(api.getSchemas()!=null && api.getSchemas().size()>0) {

			for (ApiSchema apiSchema : api.getSchemas()) {
			
				if(!ApiSchemaType.JSON.equals(apiSchema.getType()) && !ApiSchemaType.YAML.equals(apiSchema.getType())) {
					continue;
				}
				byte [] schema = apiSchema.getContent();
				if(ApiSchemaType.JSON.equals(apiSchema.getType())) {
					if(jsonUtils.isJson(schema)) {
						attachments.put(apiSchema.getName(), new String(apiSchema.getContent()));
					}
				}
				else {
					if(yamlUtils.isYaml(schema)) {
						attachments.put(apiSchema.getName(), new String(apiSchema.getContent()));
					}
				}
				
			}
		}
		
		if(attachments.isEmpty()) {	
			return api; // non vi sono attachments da aggiungere
		}
		
		String apiRaw = api.getApiRaw();
		boolean apiRawIsYaml = yamlUtils.isYaml(apiRaw);
									
		UniqueInterfaceGeneratorConfig configUniqueInterfaceGeneratorConfig = new UniqueInterfaceGeneratorConfig();
		configUniqueInterfaceGeneratorConfig.setFormat(ApiFormats.OPEN_API_3);
		configUniqueInterfaceGeneratorConfig.setYaml(apiRawIsYaml);
		configUniqueInterfaceGeneratorConfig.setMaster(apiRaw);
		configUniqueInterfaceGeneratorConfig.setAttachments(attachments);
		try {
			String apiMerged = UniqueInterfaceGenerator.generate(configUniqueInterfaceGeneratorConfig, null, null, true, this.logger);
			if(apiMerged==null) {
				throw new Exception("empty ApiSpec");
			}
			
			IApiReader apiReader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			ApiReaderConfig config = new ApiReaderConfig();
			config.setProcessInclude(false);
			config.setProcessInlineSchema(true);
			apiReader.init(this.logger, apiMerged, config);
			Api apiMergedObject = apiReader.read();
			if(apiMergedObject==null) {
				throw new Exception("empty ApiSpec after read");
			}
			accordoServizioWrapper.updateApi(apiMergedObject);
			return apiMergedObject;
		}catch(Throwable t) {
			this.logger.error("Merge API Spec failed: "+t.getMessage(),t);
			return api; // torno al metodo tradizionale utilizzando l'api non merged
		}

	}
	
	private void updateConfigOpenApi4j(List<Proprieta> proprieta, OpenapiApi4jValidatorConfig configOpenApi4j) {
		if(proprieta==null || proprieta.isEmpty()) {
			return;
		}
		
		String useOpenApi4j = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_ENABLED);
		if(useOpenApi4j!=null && !StringUtils.isEmpty(useOpenApi4j)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(useOpenApi4j.trim())) {
				configOpenApi4j.setUseOpenApi4J(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(useOpenApi4j.trim())) {
				configOpenApi4j.setUseOpenApi4J(false);
			}
		}
		
		if(configOpenApi4j.isUseOpenApi4J()==false) {
			return;
		}
		
		String mergeAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_MERGE_API_SPEC);
		if(mergeAPISpec!=null && !StringUtils.isEmpty(mergeAPISpec)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(mergeAPISpec.trim())) {
				configOpenApi4j.setMergeAPISpec(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(mergeAPISpec.trim())) {
				configOpenApi4j.setMergeAPISpec(false);
			}
		}
		
		String validateAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_API_SPEC);
		if(validateAPISpec!=null && !StringUtils.isEmpty(validateAPISpec)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateAPISpec.trim())) {
				configOpenApi4j.setValidateAPISpec(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateAPISpec.trim())) {
				configOpenApi4j.setValidateAPISpec(false);
			}
		}
		
		String validateRequestQuery = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_QUERY);
		if(validateRequestQuery!=null && !StringUtils.isEmpty(validateRequestQuery)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateRequestQuery.trim())) {
				configOpenApi4j.setValidateRequestQuery(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateRequestQuery.trim())) {
				configOpenApi4j.setValidateRequestQuery(false);
			}
		}
		
		String validateRequestHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_HEADERS);
		if(validateRequestHeaders!=null && !StringUtils.isEmpty(validateRequestHeaders)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateRequestHeaders.trim())) {
				configOpenApi4j.setValidateRequestHeaders(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateRequestHeaders.trim())) {
				configOpenApi4j.setValidateRequestHeaders(false);
			}
		}
		
		String validateRequestCookie = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_COOKIES);
		if(validateRequestCookie!=null && !StringUtils.isEmpty(validateRequestCookie)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateRequestCookie.trim())) {
				configOpenApi4j.setValidateRequestCookie(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateRequestCookie.trim())) {
				configOpenApi4j.setValidateRequestCookie(false);
			}
		}
		
		String validateRequestBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_BODY);
		if(validateRequestBody!=null && !StringUtils.isEmpty(validateRequestBody)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateRequestBody.trim())) {
				configOpenApi4j.setValidateRequestBody(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateRequestBody.trim())) {
				configOpenApi4j.setValidateRequestBody(false);
			}
		}
		
		String validateResponseHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_HEADERS);
		if(validateResponseHeaders!=null && !StringUtils.isEmpty(validateResponseHeaders)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateResponseHeaders.trim())) {
				configOpenApi4j.setValidateResponseHeaders(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateResponseHeaders.trim())) {
				configOpenApi4j.setValidateResponseHeaders(false);
			}
		}
		
		String validateResponseBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_BODY);
		if(validateResponseBody!=null && !StringUtils.isEmpty(validateResponseBody)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(validateResponseBody.trim())) {
				configOpenApi4j.setValidateResponseBody(true);
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(validateResponseBody.trim())) {
				configOpenApi4j.setValidateResponseBody(false);
			}
		}
		
	}
	protected static String readValue(List<Proprieta> proprieta, String nome) {
		if(proprieta==null || proprieta.isEmpty()) {
			return null;
		}
		for (Proprieta proprietaCheck : proprieta) {
			if(nome.equalsIgnoreCase(proprietaCheck.getNome())) {
				return proprietaCheck.getValore();
			}
		}
		return null;
	}
	protected static boolean readBooleanValueWithDefault(List<Proprieta> proprieta, String nome, boolean defaultValue) {
		String valueS = ValidatoreMessaggiApplicativiRest.readValue(proprieta, nome);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED.equals(valueS.trim())) {
				return true;
			}
			else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED.equals(valueS.trim())) {
				return false;
			}
		}
		return defaultValue;
	}
	
	public static boolean isValidazioneAbilitata(List<Proprieta> proprieta, OpenSPCoop2Message responseMessage, int codiceRitornato) throws Exception {
		
		OpenSPCoop2RestMessage<?> restMsg = responseMessage.castAsRest();
		
		// Controllo se validare risposte vuote
		boolean default_validateEmptyResponse = true; // default. devo controllare gli header etc...
		boolean validateEmptyResponse = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_EMPTY_RESPONSE_ENABLED, default_validateEmptyResponse); 
		if(!validateEmptyResponse) {
			boolean hasContent = restMsg.hasContent();
			if(!hasContent) {
				return false;
			}
		}
		
		// Controllo se validare i fault generati da govway prima di arrivare alla validazione.
		// Sono errori interni che potrebbero non essere definiti nell'interfaccia.
		// per default questa validazione è disabilitata
		boolean default_validateGovwayFault = false;
		boolean validateGovwayFault = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_FAULT_GOVWAY_ENABLED, default_validateGovwayFault);
		if(!validateGovwayFault) {
			boolean isFaultGovway = MessageRole.FAULT.equals(responseMessage.getMessageRole());
			if(isFaultGovway) {
				return false;
			}
		}
		
		// Controllo se validare problem detail
		boolean default_validateProblemDetail = true; // dovrebbero far parte dell'interfaccia essendo generati dal server o dalla controparte (non sono fault generati da govway)
		boolean validateProblemDetail = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_PROBLEM_DETAIL_ENABLED, default_validateProblemDetail);
		if(!validateProblemDetail) {
			if(restMsg.isProblemDetailsForHttpApis_RFC7807()) {
				return false;
			}
		}
		
		// Controllo se validare solo determinati codici http
		String valueS = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_ENABLED);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			
			boolean default_not = false; 
			boolean not = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_NOT, default_not);
			
			List<String> codici = new ArrayList<String>();
			if(valueS.contains(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR)) {
				String [] tmp = valueS.split(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR);
				if(tmp!=null && tmp.length>0) {
					for (String s : tmp) {
						codici.add(s.trim());
					}
				}
			}
			else {
				codici.add(valueS.trim());
			}
			if(codici!=null && !codici.isEmpty()) {
				
				boolean match = false;
				
				for (String codice : codici) {
					if(codice.contains(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_INTERVAL_SEPARATOR)) {
						String [] tmp = codice.split(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_INTERVAL_SEPARATOR);
						if(tmp==null || tmp.length!=2) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' possiede un formato errato; atteso: codiceMin-codiceMax");
						}
						String codiceMin = tmp[0];
						String codiceMax = tmp[1];
						if(codiceMin==null || StringUtils.isEmpty(codiceMin.trim())) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' possiede un formato errato (intervallo minimo non definito); atteso: codiceMin-codiceMax");
						}
						if(codiceMax==null || StringUtils.isEmpty(codiceMax.trim())) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' possiede un formato errato (intervallo massimo non definito); atteso: codiceMin-codiceMax");
						}
						codiceMin = codiceMin.trim();
						codiceMax = codiceMax.trim();
						int codiceMinInt = -1;
						try {
							codiceMinInt = Integer.valueOf(codiceMin);
						}catch(Exception e) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' contiene un intervallo minimo '"+codiceMin+"' che non è un numero intero");
						}
						int codiceMaxInt = -1;
						try {
							codiceMaxInt = Integer.valueOf(codiceMax);
						}catch(Exception e) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' contiene un intervallo massimo '"+codiceMax+"' che non è un numero intero");
						}
						if(codiceMaxInt<=codiceMinInt) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' contiene un intervallo massimo '"+codiceMax+"' minore o uguale all'intervallo minimo '"+codiceMin+"'");
						}
						if( (codiceMinInt <= codiceRitornato) && (codiceRitornato <= codiceMaxInt)) {
							match = true;
							break;
						}
					}
					else {
						try {
							int codiceInt = Integer.valueOf(codice);
							if(codiceInt == codiceRitornato) {
								match = true;
								break;
							}
						}catch(Exception e) {
							throw new Exception("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' non è un numero intero");
						}
					}
				}
				
				if(match) {
					return not ? false : true ;
				}
				else {
					return not ? true : false;
				}
			}
		}
		
		
		// Controllo se validare solo determinati content-type
		valueS = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_LIST_ENABLED);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			
			boolean default_not = false; 
			boolean not = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_NOT, default_not);
			
			List<String> contentTypes = new ArrayList<String>();
			if(valueS.contains(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_LIST_SEPARATOR)) {
				String [] tmp = valueS.split(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_LIST_SEPARATOR);
				if(tmp!=null && tmp.length>0) {
					for (String s : tmp) {
						contentTypes.add(s.trim());
					}
				}
			}
			else {
				contentTypes.add(valueS.trim());
			}
			if(contentTypes!=null && !contentTypes.isEmpty()) {
				
				String contentTypeRisposta = responseMessage.getContentType();
				String baseTypeHttp = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRisposta);
				
				if(ContentTypeUtilities.isMatch(baseTypeHttp, contentTypes)) {
					return not ? false : true ;
				}
				else {				
					return not ? true : false;
				}
			}
		}
		
		
		return true;
		
	}
	
}
