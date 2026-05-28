/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import java.io.InputStream;
import java.util.ArrayList;
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
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.openapi.validator.OpenAPILibrary;
import org.openspcoop2.utils.openapi.validator.OpenapiLibraryValidatorConfig;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.entity.BinaryHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.BinaryHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.Cookie;
import org.openspcoop2.utils.rest.entity.ElementHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.rest.entity.InputStreamHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.InputStreamHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.wsdl.WSDLException;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.slf4j.Logger;
import org.w3c.dom.Element;


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
	/** RequestInfo */
	private RequestInfo requestInfo;
	/** UseInterface */
	private boolean useInterface;
	/** Proprieta di porta passate al costruttore (servono in validateWithInterface per buildValidatorConfigForCurrentApi). */
	private List<Proprieta> proprieta;
	/** OpenSPCoop2Properties */
	private OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
	/** Buffer */
	private boolean bufferMessage_readOnly = true;
	
	

	
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
		
		if(this.pddContext!=null && this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			this.requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		try{
			this.useInterface = readInterfaceAccordoServizio;
			this.proprieta = proprieta;

			// processIncludeForOpenApi: serve a parametrizzare getRestAccordoServizio.
			// Quando l'engine selezionato risolve da sé i $ref esterni (openapi4j / swagger_request_validator / kappa)
			// il flag deve essere false; vale true solo per json_schema (validatore basic, ha bisogno dello spec già appiattito).
			// In costruttore non sappiamo ancora se la spec sarà swagger 2 / 3.0 / 3.1 (l'Api viene letta dal wrapper
			// subito dopo), quindi adottiamo una valutazione conservativa: se ALMENO uno dei 3 default globali
			// (swagger.2.library / openApi.30.library / openApi.31.library) risolve a un engine non-json_schema,
			// mettiamo false.
			boolean processIncludeForOpenApi = true;
			if (readInterfaceAccordoServizio) {
				OpenAPILibrary lib30 = this.op2Properties.getValidazioneContenutiApplicativiOpenApi30Library();
				OpenAPILibrary lib31 = this.op2Properties.getValidazioneContenutiApplicativiOpenApi31Library();
				OpenAPILibrary libSwagger2 = this.op2Properties.getValidazioneContenutiApplicativiSwagger2Library();
				if (!OpenAPILibrary.json_schema.equals(lib30)
						|| !OpenAPILibrary.json_schema.equals(lib31)
						|| !OpenAPILibrary.json_schema.equals(libSwagger2)) {
					processIncludeForOpenApi = false;
				}
			}
			
			this.bufferMessage_readOnly = OpenSPCoop2Properties.getInstance().isValidazioneContenutiApplicativiBufferContentRead();
			if(proprieta!=null && !proprieta.isEmpty()) {
				boolean defaultBehaviour = this.bufferMessage_readOnly;
				this.bufferMessage_readOnly = ValidatoreMessaggiApplicativiRest.readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_BUFFER_ENABLED, defaultBehaviour);
			}
			
			if(readInterfaceAccordoServizio){
				this.accordoServizioWrapper = this.registroServiziManager.getRestAccordoServizio(idServizio,InformationApiSource.SPECIFIC,true,processIncludeForOpenApi,false, this.requestInfo);
			}else{
				this.accordoServizioWrapper = this.registroServiziManager.getRestAccordoServizio(idServizio,InformationApiSource.REGISTRY,true,processIncludeForOpenApi,true, this.requestInfo);
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
				AccordoServizioParteSpecifica asps = this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false, this.requestInfo);
				AccordoServizioParteComune apc = this.registroServiziManager.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false, false, this.requestInfo);
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
			String idTransazione = null;
			if(this.pddContext!=null) {
				idTransazione = (String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			
			OpenSPCoop2RestXmlMessage xmlMsg = this.message.castAsRestXml();
			Element content = xmlMsg.getContent(this.bufferMessage_readOnly, idTransazione);						
			validatoreBodyApplicativo.valida(content);
			
		}catch(Exception e){ 
			
			String messaggioErrore = e.getMessage();
			boolean overwriteMessageError = false;
			try {
				messaggioErrore = ErroriProperties.getInstance(this.logger).getGenericDetails_noWrap(isRichiesta ? IntegrationFunctionError.INVALID_REQUEST_CONTENT : IntegrationFunctionError.INVALID_RESPONSE_CONTENT);
				messaggioErrore = messaggioErrore+": "+ValidatoreMessaggiApplicativi.processValidationErrorMessage(e.getMessage(), this.op2Properties);
				overwriteMessageError = true;
			}catch(Exception excp) {
				// ignore
			}

			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(messaggioErrore,e);
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
		
		if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
			throw new ValidatoreMessaggiApplicativiException("Tipo di validazione non supportata con Service Binding SOAP");
		}
		
		String interfaceType = null;
		ApiValidatorConfig validatorConfig = null;
		OpenAPILibrary openApiLibrary = null;
		Api api = this.accordoServizioWrapper.getApi();

		switch (this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()) {
		case SWAGGER_2:
			interfaceType = "Interfaccia Swagger 2";
			break;
		case OPEN_API_3:
			interfaceType = "Interfaccia OpenAPI 3";
			break;
		default:
			// altre interfacce non supportate per rest
			throw new ValidatoreMessaggiApplicativiException("Tipo di interfaccia ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] non supportata");
		}

		try {
			openApiLibrary = this.useInterface
					? resolveLibraryForCurrentApi(api, this.proprieta)
					: OpenAPILibrary.json_schema;
			validatorConfig = buildValidatorConfigForCurrentApi(api, this.proprieta, openApiLibrary);
		} catch(Exception e) {
			this.logger.error("validateWithInterface failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex
				= new ValidatoreMessaggiApplicativiException(e.getMessage(),e);
			ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(interfaceType));
			throw ex;
		}

		IApiValidator apiValidator = null;
		try {
			apiValidator = ApiFactory.newApiValidator(openApiLibrary.name());
			validatorConfig.setXmlUtils(MessageXMLUtils.getInstance(this.message.getFactory()));
			validatorConfig.setVerbose(this.op2Properties.isValidazioneContenutiApplicativiDebug());
			validatorConfig.setPolicyAdditionalProperties(this.op2Properties.getValidazioneContenutiApplicativiJsonPolicyAdditionalProperties());
			apiValidator.init(this.logger, api, validatorConfig);
		} catch(Exception e){
			this.logger.error("validateWithInterface failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage(),e);
			ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(interfaceType));
			throw ex;
		}
			
		InputStream isContent = null;
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
			
			String idTransazione = null;
			if(this.pddContext!=null) {
				idTransazione = (String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			
			if(isRichiesta) {
				HttpBaseRequestEntity<?> httpRequest = null;
				switch (this.message.getMessageType()) {
				case XML:
					httpRequest = new ElementHttpRequestEntity();
					if(this.message.castAsRest().hasContent()) {
						
						// serve anche in opeanpi4j per bufferizzare la richiesta
						OpenSPCoop2RestXmlMessage xmlMsg = this.message.castAsRestXml();
						Element contentXml = xmlMsg.getContent(this.bufferMessage_readOnly, idTransazione);
						
						if(OpenAPILibrary.openapi4j.equals(openApiLibrary) || OpenAPILibrary.swagger_request_validator.equals(openApiLibrary)) {
							httpRequest = new BinaryHttpRequestEntity();
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							this.message.writeTo(bout, false);
							bout.flush();
							bout.close();
							((BinaryHttpRequestEntity)httpRequest).setContent(bout.toByteArray());
						}
						else {
							((ElementHttpRequestEntity)httpRequest).setContent(contentXml);
						}
					}
					break;
				case JSON:
					httpRequest = new TextHttpRequestEntity();
					if(this.message.castAsRest().hasContent()) {
						OpenSPCoop2RestJsonMessage jsonMsg = this.message.castAsRestJson();
						String contentString = jsonMsg.getContent(this.bufferMessage_readOnly, idTransazione);						
						((TextHttpRequestEntity)httpRequest).setContent(contentString);
					}
					break;
				case BINARY:
				case MIME_MULTIPART:
					OpenSPCoop2RestMessage<?> restMsg = this.message.castAsRest();
					if(restMsg.hasContent()) {
						boolean lazy = restMsg.setInputStreamLazyBuffer(idTransazione); // bufferizzazione lazy
						InputStream isLazyContent = lazy ? restMsg.getInputStream() : null;
						if(isLazyContent!=null) {
							// isContent verra' chiuso sotto nel finally, mentre il lazy no
							httpRequest = new InputStreamHttpRequestEntity();
							((InputStreamHttpRequestEntity)httpRequest).setContent(isLazyContent);
						}
						else {
							restMsg.initContent(this.bufferMessage_readOnly, idTransazione); // bufferizzo
							isContent = restMsg.getInputStreamFromContentBuffer();
							if(isContent!=null) {
								httpRequest = new InputStreamHttpRequestEntity();
								((InputStreamHttpRequestEntity)httpRequest).setContent(isContent);
							}
							else {
								httpRequest = new BinaryHttpRequestEntity();
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								this.message.writeTo(bout, false);
								bout.flush();
								bout.close();
								((BinaryHttpRequestEntity)httpRequest).setContent(bout.toByteArray());
							}
						}
					}
					else {
						httpRequest = new BinaryHttpRequestEntity();
					}
					break;
				default:
					break;
				}
				if(httpRequest==null) {
					throw new Exception("HttpRequest undefined");
				}
				httpRequest.setContentType(this.message.getContentType());
				httpRequest.setHeaders(this.message.getTransportRequestContext().getHeaders());
				httpRequest.setParameters(this.message.getTransportRequestContext().getParameters());
				Map<String,String> mapCookies = this.message.getTransportRequestContext().getCookiesValue();
				if(mapCookies!=null && !mapCookies.isEmpty()) {
					for (String name : mapCookies.keySet()) {
						String value = mapCookies.get(name);
						Cookie cookie = new Cookie(name, value);
						if(httpRequest.getCookies()==null) {
							httpRequest.setCookies(new ArrayList<Cookie>());
						}
						httpRequest.getCookies().add(cookie);
					}
				}
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

						// serve anche in opeanpi4j per bufferizzare la richiesta
						OpenSPCoop2RestXmlMessage xmlMsg = this.message.castAsRestXml();
						Element contentXml = xmlMsg.getContent(this.bufferMessage_readOnly, idTransazione);
						
						if(OpenAPILibrary.openapi4j.equals(openApiLibrary) || OpenAPILibrary.swagger_request_validator.equals(openApiLibrary)) {
							httpResponse = new BinaryHttpResponseEntity();
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							this.message.writeTo(bout, false);
							bout.flush();
							bout.close();
							((BinaryHttpResponseEntity)httpResponse).setContent(bout.toByteArray());
						}
						else {
							((ElementHttpResponseEntity)httpResponse).setContent(contentXml);
						}
					}
					break;
				case JSON:
					httpResponse = new TextHttpResponseEntity();
					if(this.message.castAsRest().hasContent()) {
						OpenSPCoop2RestJsonMessage jsonMsg = this.message.castAsRestJson();
						String contentString = jsonMsg.getContent(this.bufferMessage_readOnly, idTransazione);						
						((TextHttpResponseEntity)httpResponse).setContent(contentString);
					}
					break;
				case BINARY:
				case MIME_MULTIPART:
					OpenSPCoop2RestMessage<?> restMsg = this.message.castAsRest();
					if(restMsg.hasContent()) {
						boolean lazy = restMsg.setInputStreamLazyBuffer(idTransazione); // bufferizzazione lazy
						InputStream isLazyContent = lazy ? restMsg.getInputStream() : null;
						if(isLazyContent!=null) {
							// isContent verra' chiuso sotto nel finally, mentre il lazy no
							httpResponse = new InputStreamHttpResponseEntity();
							((InputStreamHttpResponseEntity)httpResponse).setContent(isLazyContent);
						}
						else {
							restMsg.initContent(this.bufferMessage_readOnly, idTransazione); // bufferizzo
							isContent = restMsg.getInputStreamFromContentBuffer();
							if(isContent!=null) {
								httpResponse = new InputStreamHttpResponseEntity();
								((InputStreamHttpResponseEntity)httpResponse).setContent(isContent);
							}
							else {
								httpResponse = new BinaryHttpResponseEntity();
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								this.message.writeTo(bout, false);
								bout.flush();
								bout.close();
								((BinaryHttpResponseEntity)httpResponse).setContent(bout.toByteArray());
							}
						}
					}
					else {
						httpResponse = new BinaryHttpResponseEntity();
					}
					break;
				default:
					break;
				}
				if(httpResponse==null) {
					throw new Exception("httpResponse undefined");
				}
				httpResponse.setContentType(this.message.getContentType());
				httpResponse.setHeaders(this.message.getTransportResponseContext().getHeaders());
				httpResponse.setUrl(path);
				httpResponse.setMethod(httpMethod);
				httpResponse.setStatus(Integer.parseInt(this.message.getTransportResponseContext().getCodiceTrasporto()));
				apiValidator.validate(httpResponse);
			}
			
		}catch(Throwable e ){ // WSDLValidatorException
			
			String messaggioErrore = e.getMessage();
			boolean overwriteMessageError = false;
			try {
				messaggioErrore = ErroriProperties.getInstance(this.logger).getGenericDetails_noWrap(isRichiesta ? IntegrationFunctionError.INVALID_REQUEST_CONTENT : IntegrationFunctionError.INVALID_RESPONSE_CONTENT);
				messaggioErrore = messaggioErrore+": "+ValidatoreMessaggiApplicativi.processValidationErrorMessage(e.getMessage(), this.op2Properties);
				overwriteMessageError = true;
			}catch(Exception excp) {
				// ignore
			}

			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException(messaggioErrore,e);
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(interfaceType,messaggioErrore, overwriteMessageError));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(interfaceType, messaggioErrore, overwriteMessageError));
			}
			throw ex;
		}finally {
			try {
				apiValidator.close(this.logger, this.accordoServizioWrapper.getApi(), validatorConfig);
			}catch(Throwable e){
				this.logger.error("validateWithInterface close error: "+e.getMessage(),e);
			}
			try {
				if(isContent!=null) {
					isContent.close();
					isContent = null;
				}
			}catch(Throwable t) {}
		}
		
	}
	
	/**
	 * Risolve la libreria di validazione da usare per l'api corrente in base a 3 branche
	 * disgiunte sul formato dello spec:
	 * <ul>
	 *   <li><b>Swagger 2.0</b>: default da {@link OpenSPCoop2Properties#getValidazioneContenutiApplicativiSwagger2Library()},
	 *       override a livello porta tramite {@code validation.swagger.2.library};</li>
	 *   <li><b>OpenAPI 3.0</b>: priorità (decrescente):
	 *     <ol>
	 *       <li>override esplicito port-level {@code validation.openApi.30.library};</li>
	 *       <li>flag legacy port-level (deprecati): {@code validation.openApi4j.enabled} /
	 *           {@code validation.swaggerRequestValidator.enabled}, con la convenzione legacy
	 *           che entrambi disabilitati ⇒ {@code json_schema};</li>
	 *       <li>default globale {@link OpenSPCoop2Properties#getValidazioneContenutiApplicativiOpenApi30Library()}
	 *           (che legge {@code ...openApi.30.library} con fallback retrocompat su {@code ...openApi.library}).</li>
	 *     </ol>
	 *   </li>
	 *   <li><b>OpenAPI 3.1</b>: default da {@link OpenSPCoop2Properties#getValidazioneContenutiApplicativiOpenApi31Library()},
	 *       override port via {@code validation.openApi.31.library}.</li>
	 * </ul>
	 * Gli override port-level con libreria non compatibile col formato dell'api (vedi
	 * {@link OpenAPILibrary#supportsSwagger2()}, {@link OpenAPILibrary#supportsOpenApi30()},
	 * {@link OpenAPILibrary#supportsOpenApi31()}) provocano una {@link ValidatoreMessaggiApplicativiException}
	 * fail-fast al caricamento della porta.
	 */
	private OpenAPILibrary resolveLibraryForCurrentApi(Api api, List<Proprieta> proprieta) throws ValidatoreMessaggiApplicativiException {

		FormatoSpecifica formato = this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica();

		// === Swagger 2.0 ===
		if (FormatoSpecifica.SWAGGER_2.equals(formato)) {
			OpenAPILibrary override = readLibraryOverride(proprieta,
					CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_2_LIBRARY);
			if (override != null) {
				if (!override.supportsSwagger2()) {
					throw libraryNotCompatible(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_2_LIBRARY, override, "Swagger 2.0");
				}
				return override;
			}
			return this.op2Properties.getValidazioneContenutiApplicativiSwagger2Library();
		}

		// === OpenAPI 3.1 ===
		if (OpenapiApi.isOpenApi31(api)) {
			OpenAPILibrary override = readLibraryOverride(proprieta,
					CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_31_LIBRARY);
			if (override != null) {
				if (!override.supportsOpenApi31()) {
					throw libraryNotCompatible(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_31_LIBRARY, override, "OpenAPI 3.1");
				}
				return override;
			}
			return this.op2Properties.getValidazioneContenutiApplicativiOpenApi31Library();
		}

		// === OpenAPI 3.0 ===
		// 1. override esplicito port-level
		OpenAPILibrary override30 = readLibraryOverride(proprieta,
				CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_30_LIBRARY);
		if (override30 != null) {
			if (!override30.supportsOpenApi30()) {
				throw libraryNotCompatible(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_30_LIBRARY, override30, "OpenAPI 3.0");
			}
			return override30;
		}
		// 2. flag legacy port-level (deprecati)
		OpenAPILibrary legacyOverride = applyLegacyEnabledFlags30(proprieta);
		if (legacyOverride != null) {
			return legacyOverride;
		}
		// 3. default globale (govway.properties)
		return this.op2Properties.getValidazioneContenutiApplicativiOpenApi30Library();
	}

	/**
	 * Override port-level via flag legacy (deprecati):
	 * <pre>
	 *   validation.openApi4j.enabled = enabled               → openapi4j
	 *   validation.swaggerRequestValidator.enabled = enabled → swagger_request_validator
	 *   entrambi = disabled                                  → json_schema
	 * </pre>
	 * Ritorna {@code null} se nessun flag è impostato (il chiamante usa il default globale).
	 * I flag in oggetto sono in via di dismissione: i nuovi setup usano
	 * {@code validation.openApi.30.library}.
	 */
	@SuppressWarnings("deprecation")
	private OpenAPILibrary applyLegacyEnabledFlags30(List<Proprieta> proprieta) {
		String useOpenApi4j = readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_ENABLED);
		String useSwagger = readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_ENABLED);

		OpenAPILibrary lib = null;
		boolean openApi4jForceDisabled = false;
		boolean swaggerForceDisabled = false;

		if (useOpenApi4j != null && !StringUtils.isEmpty(useOpenApi4j)) {
			if (CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_ENABLED.equals(useOpenApi4j.trim())) {
				lib = OpenAPILibrary.openapi4j;
			} else if (CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_OPENAPI4J_DISABLED.equals(useOpenApi4j.trim())) {
				openApi4jForceDisabled = true;
			}
		}
		if (useSwagger != null && !StringUtils.isEmpty(useSwagger)) {
			if (CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_SWAGGER_REQUEST_VALIDATOR_ENABLED.equals(useSwagger.trim())) {
				lib = OpenAPILibrary.swagger_request_validator;
			} else if (CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_SWAGGER_REQUEST_VALIDATOR_DISABLED.equals(useSwagger.trim())) {
				swaggerForceDisabled = true;
			}
		}
		if (openApi4jForceDisabled && swaggerForceDisabled) {
			lib = OpenAPILibrary.json_schema;
		}
		return lib;
	}

	/**
	 * Legge un override port-level del tipo {@code validation.openApi.30.library = openapi4j}.
	 * Restituisce {@code null} se la property non è impostata o è vuota.
	 * Lancia {@link ValidatoreMessaggiApplicativiException} se il valore non corrisponde
	 * ad alcun engine noto.
	 */
	private OpenAPILibrary readLibraryOverride(List<Proprieta> proprieta, String propertyName) throws ValidatoreMessaggiApplicativiException {
		String raw = readValue(proprieta, propertyName);
		if (raw == null || raw.trim().isEmpty()) {
			return null;
		}
		try {
			return OpenAPILibrary.valueOf(raw.trim());
		} catch (Exception e) {
			throw new ValidatoreMessaggiApplicativiException("Property di porta '" + propertyName
					+ "' con valore '" + raw + "' non valido: nessuna libreria di validazione con tale nome");
		}
	}

	private static ValidatoreMessaggiApplicativiException libraryNotCompatible(String propertyName, OpenAPILibrary library, String formato) {
		return new ValidatoreMessaggiApplicativiException("Property di porta '" + propertyName
				+ "' con valore '" + library + "' non valido: la libreria non supporta " + formato);
	}

	/**
	 * Costruisce per la singola porta un {@link ApiValidatorConfig} engine-specific:
	 * <ol>
	 *   <li>recupera la <em>config base</em> cached da {@link OpenSPCoop2Properties} per la libreria data
	 *       e ne fa una <strong>copia</strong> (clone via {@link ApiValidatorConfig#mapProperties()});</li>
	 *   <li>applica gli override property a livello di porta riusando {@link #updateOpenapiValidatorConfig}
	 *       e quindi ricostruisce l'engine config finale.</li>
	 * </ol>
	 * I metodi legacy non sono toccati.
	 */
	private ApiValidatorConfig buildValidatorConfigForCurrentApi(Api api, List<Proprieta> proprieta, OpenAPILibrary library) throws ProcessingException {
		boolean is31 = OpenapiApi.isOpenApi31(api);

		// 1. recupero la base cached e ne creo una copia (transferendo solo le coppie chiave/valore)
		ApiValidatorConfig base = is31
				? this.op2Properties.getValidator31ConfigBase(library)
				: this.op2Properties.getValidatorConfigBase(library);

		OpenapiLibraryValidatorConfig src = new OpenapiLibraryValidatorConfig();
		src.setOpenApiLibrary(library);
		if (base != null) {
			applyMapToLibConfig(src, base.mapProperties());
		}

		// 2. override a livello di porta
		updateOpenapiValidatorConfig(proprieta, src);

		// 3. costruisco la config engine-specific finale dal src risultante
		ApiValidatorConfig engineConfig = ApiFactory.newApiValidatorConfig(library.name());
		engineConfig.readProperties(src::getProperty);
		return engineConfig;
	}

	private static void applyMapToLibConfig(OpenapiLibraryValidatorConfig dest, Map<String, String> props) {
		String s = props.get(OpenapiLibraryValidatorConfig.PROPERTY_KEY_JSON_VALIDATOR_API);
		if (s != null) {
			try { dest.setJsonValidatorAPI(ApiName.valueOf(s)); } catch (Exception e) { /* ignore */ }
		}
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_MERGE_API_SPEC, dest::setMergeAPISpec);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_API_SPEC, dest::setValidateAPISpec);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_PATH, dest::setValidateRequestPath);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_QUERY, dest::setValidateRequestQuery);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM, dest::setValidateRequestUnexpectedQueryParam);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_HEADERS, dest::setValidateRequestHeaders);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_COOKIES, dest::setValidateRequestCookie);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_REQUEST_BODY, dest::setValidateRequestBody);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_RESPONSE_HEADERS, dest::setValidateResponseHeaders);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_RESPONSE_BODY, dest::setValidateResponseBody);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_WILDCARD_SUBTYPE_AS_JSON, dest::setValidateWildcardSubtypeAsJson);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_VALIDATE_MULTIPART_OPTIMIZATION, dest::setValidateMultipartOptimization);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_SWAGGER_INJECTING_ADDITIONAL_PROPERTIES_FALSE, dest::setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse);
		applyBool(props, OpenapiLibraryValidatorConfig.PROPERTY_KEY_SWAGGER_RESOLVE_FULLY_API_SPEC, dest::setSwaggerRequestValidator_ResolveFullyApiSpec);
	}

	private static void applyBool(Map<String, String> props, String key, java.util.function.Consumer<Boolean> setter) {
		String v = props.get(key);
		if (v != null) {
			setter.accept(Boolean.parseBoolean(v));
		}
	}

	private void updateOpenapiValidatorConfig(List<Proprieta> proprieta, OpenapiLibraryValidatorConfig configOpenApi4j) {
		if(proprieta==null || proprieta.isEmpty()) {
			return;
		}

		// La scelta della libreria (resolveLibraryForCurrentApi) è già stata effettuata dal chiamante:
		// qui applichiamo gli override port-level sulle property booleane (mergeAPISpec, validateAPISpec, ecc.)
		// in base alla libreria già impostata in configOpenApi4j.
		//
		// Razionale property engine-specific vs commons:
		//   - Le property engine-specific (validation.openApi4j.<flag>, validation.swaggerRequestValidator.<flag>)
		//     sono LEGACY: nel vecchio modello affiancavano i flag '<engine>.enabled=true' (oggi deprecati)
		//     come pacchetto di configurazione con lo stesso prefisso. Vengono lette qui solo per
		//     back-compat di configurazioni esistenti e non sono più documentate.
		//   - Le property commons (validation.openApi.<flag>) sono engine-agnostic e sono la modalità
		//     raccomandata (l'unica documentata). Hanno lo stesso significato per openapi4j,
		//     swagger_request_validator e kappa.
		//   - kappa non ha property legacy dedicate: ricade direttamente sulla commons. Non si aggiungono
		//     prefissi 'validation.kappa.<flag>', che perpetuerebbero il pattern legacy senza beneficio.
		//   - json_schema non legge dal config alcun flag di validazione/merge (il validator usa solo
		//     verbose / policyAdditionalProperties / emitLogError): esce subito.
		//
		// Gli engine ignorano i flag che non supportano senza danni (es. kappa non legge
		// validateRequestUnexpectedQueryParam / validateWildcardSubtypeAsJson; swagger_request_validator
		// non legge validateMultipartOptimization). Il cacheKey dei validator esposti
		// (ApiValidatorConfig.cacheKey + override swagger) include solo i flag che effettivamente
		// influenzano la struttura caricata.
		if(OpenAPILibrary.json_schema.equals(configOpenApi4j.getOpenApiLibrary())) {
			return;
		}
		boolean openapi4j = OpenAPILibrary.openapi4j.equals(configOpenApi4j.getOpenApiLibrary());
		boolean swaggerRV = OpenAPILibrary.swagger_request_validator.equals(configOpenApi4j.getOpenApiLibrary());
		// I valori 'enabled'/'disabled' sono comuni a tutte le property port-level (engine-agnostic),
		// usiamo le costanti generiche non deprecate.
		String enabled = CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED;
		String disabled = CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED;

		String mergeAPISpec = null;
		if(openapi4j) {
			mergeAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_MERGE_API_SPEC);
		}
		else if(swaggerRV) {
			mergeAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_MERGE_API_SPEC);
		}
		if(mergeAPISpec==null || StringUtils.isEmpty(mergeAPISpec)) {
			mergeAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_MERGE_API_SPEC);
		}
		if(mergeAPISpec!=null && !StringUtils.isEmpty(mergeAPISpec)) {
			if(enabled.equals(mergeAPISpec.trim())) {
				configOpenApi4j.setMergeAPISpec(true);
			}
			else if(disabled.equals(mergeAPISpec.trim())) {
				configOpenApi4j.setMergeAPISpec(false);
			}
		}

		String validateAPISpec = null;
		if(openapi4j) {
			validateAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_API_SPEC);
		}
		else if(swaggerRV) {
			validateAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_API_SPEC);
		}
		if(validateAPISpec==null || StringUtils.isEmpty(validateAPISpec)) {
			validateAPISpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_API_SPEC);
		}
		if(validateAPISpec!=null && !StringUtils.isEmpty(validateAPISpec)) {
			if(enabled.equals(validateAPISpec.trim())) {
				configOpenApi4j.setValidateAPISpec(true);
			}
			else if(disabled.equals(validateAPISpec.trim())) {
				configOpenApi4j.setValidateAPISpec(false);
			}
		}

		String validateRequestPath = null;
		if(openapi4j) {
			validateRequestPath = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_PATH);
		}
		else if(swaggerRV) {
			validateRequestPath = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_PATH);
		}
		if(validateRequestPath==null || StringUtils.isEmpty(validateRequestPath)) {
			validateRequestPath = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_PATH);
		}
		if(validateRequestPath!=null && !StringUtils.isEmpty(validateRequestPath)) {
			if(enabled.equals(validateRequestPath.trim())) {
				configOpenApi4j.setValidateRequestPath(true);
			}
			else if(disabled.equals(validateRequestPath.trim())) {
				configOpenApi4j.setValidateRequestPath(false);
			}
		}

		String validateRequestQuery = null;
		if(openapi4j) {
			validateRequestQuery = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_QUERY);
		}
		else if(swaggerRV) {
			validateRequestQuery = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_QUERY);
		}
		if(validateRequestQuery==null || StringUtils.isEmpty(validateRequestQuery)) {
			validateRequestQuery = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_QUERY);
		}
		if(validateRequestQuery!=null && !StringUtils.isEmpty(validateRequestQuery)) {
			if(enabled.equals(validateRequestQuery.trim())) {
				configOpenApi4j.setValidateRequestQuery(true);
			}
			else if(disabled.equals(validateRequestQuery.trim())) {
				configOpenApi4j.setValidateRequestQuery(false);
			}
		}

		String validateRequestQueryUnexpectedQueryParam = null;
		if(openapi4j) {
			validateRequestQueryUnexpectedQueryParam = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM);
		}
		else if(swaggerRV) {
			validateRequestQueryUnexpectedQueryParam = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM);
		}
		if(validateRequestQueryUnexpectedQueryParam==null || StringUtils.isEmpty(validateRequestQueryUnexpectedQueryParam)) {
			validateRequestQueryUnexpectedQueryParam = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_UNEXPECTED_QUERY_PARAM);
		}
		if(validateRequestQueryUnexpectedQueryParam!=null && !StringUtils.isEmpty(validateRequestQueryUnexpectedQueryParam)) {
			if(enabled.equals(validateRequestQueryUnexpectedQueryParam.trim())) {
				configOpenApi4j.setValidateRequestUnexpectedQueryParam(true);
			}
			else if(disabled.equals(validateRequestQueryUnexpectedQueryParam.trim())) {
				configOpenApi4j.setValidateRequestUnexpectedQueryParam(false);
			}
		}

		String validateRequestHeaders = null;
		if(openapi4j) {
			validateRequestHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_HEADERS);
		}
		else if(swaggerRV) {
			validateRequestHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_HEADERS);
		}
		if(validateRequestHeaders==null || StringUtils.isEmpty(validateRequestHeaders)) {
			validateRequestHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_HEADERS);
		}
		if(validateRequestHeaders!=null && !StringUtils.isEmpty(validateRequestHeaders)) {
			if(enabled.equals(validateRequestHeaders.trim())) {
				configOpenApi4j.setValidateRequestHeaders(true);
			}
			else if(disabled.equals(validateRequestHeaders.trim())) {
				configOpenApi4j.setValidateRequestHeaders(false);
			}
		}

		String validateRequestCookie = null;
		if(openapi4j) {
			validateRequestCookie = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_COOKIES);
		}
		else if(swaggerRV) {
			validateRequestCookie = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_COOKIES);
		}
		if(validateRequestCookie==null || StringUtils.isEmpty(validateRequestCookie)) {
			validateRequestCookie = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_COOKIES);
		}
		if(validateRequestCookie!=null && !StringUtils.isEmpty(validateRequestCookie)) {
			if(enabled.equals(validateRequestCookie.trim())) {
				configOpenApi4j.setValidateRequestCookie(true);
			}
			else if(disabled.equals(validateRequestCookie.trim())) {
				configOpenApi4j.setValidateRequestCookie(false);
			}
		}

		String validateRequestBody = null;
		if(openapi4j) {
			validateRequestBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_REQUEST_BODY);
		}
		else if(swaggerRV) {
			validateRequestBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_REQUEST_BODY);
		}
		if(validateRequestBody==null || StringUtils.isEmpty(validateRequestBody)) {
			validateRequestBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_REQUEST_BODY);
		}
		if(validateRequestBody!=null && !StringUtils.isEmpty(validateRequestBody)) {
			if(enabled.equals(validateRequestBody.trim())) {
				configOpenApi4j.setValidateRequestBody(true);
			}
			else if(disabled.equals(validateRequestBody.trim())) {
				configOpenApi4j.setValidateRequestBody(false);
			}
		}

		String validateResponseHeaders = null;
		if(openapi4j) {
			validateResponseHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_HEADERS);
		}
		else if(swaggerRV) {
			validateResponseHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_RESPONSE_HEADERS);
		}
		if(validateResponseHeaders==null || StringUtils.isEmpty(validateResponseHeaders)) {
			validateResponseHeaders = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_HEADERS);
		}
		if(validateResponseHeaders!=null && !StringUtils.isEmpty(validateResponseHeaders)) {
			if(enabled.equals(validateResponseHeaders.trim())) {
				configOpenApi4j.setValidateResponseHeaders(true);
			}
			else if(disabled.equals(validateResponseHeaders.trim())) {
				configOpenApi4j.setValidateResponseHeaders(false);
			}
		}

		String validateResponseBody = null;
		if(openapi4j) {
			validateResponseBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_RESPONSE_BODY);
		}
		else if(swaggerRV) {
			validateResponseBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_RESPONSE_BODY);
		}
		if(validateResponseBody==null || StringUtils.isEmpty(validateResponseBody)) {
			validateResponseBody = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_RESPONSE_BODY);
		}
		if(validateResponseBody!=null && !StringUtils.isEmpty(validateResponseBody)) {
			if(enabled.equals(validateResponseBody.trim())) {
				configOpenApi4j.setValidateResponseBody(true);
			}
			else if(disabled.equals(validateResponseBody.trim())) {
				configOpenApi4j.setValidateResponseBody(false);
			}
		}

		String validateWildcardSubtypeAsJson = null;
		if(openapi4j) {
			validateWildcardSubtypeAsJson = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_WILDCARD_SUBTYPE_AS_JSON);
		}
		else if(swaggerRV) {
			validateWildcardSubtypeAsJson = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_VALIDATE_WILDCARD_SUBTYPE_AS_JSON);
		}
		if(validateWildcardSubtypeAsJson==null || StringUtils.isEmpty(validateWildcardSubtypeAsJson)) {
			validateWildcardSubtypeAsJson = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_WILDCARD_SUBTYPE_AS_JSON);
		}
		if(validateWildcardSubtypeAsJson!=null && !StringUtils.isEmpty(validateWildcardSubtypeAsJson)) {
			if(enabled.equals(validateWildcardSubtypeAsJson.trim())) {
				configOpenApi4j.setValidateWildcardSubtypeAsJson(true);
			}
			else if(disabled.equals(validateWildcardSubtypeAsJson.trim())) {
				configOpenApi4j.setValidateWildcardSubtypeAsJson(false);
			}
		}

		// validateMultipartOptimization è letto da openapi4j e kappa (swaggerRV non lo usa).
		// La property legacy 'validation.openApi4j.validateMultipartOptimization' è disponibile
		// solo per openapi4j (back-compat); per kappa si usa direttamente la commons.
		if(!swaggerRV) {
			String validateMultipartOptimization = null;
			if(openapi4j) {
				validateMultipartOptimization = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_OPENAPI4J_VALIDATE_MULTIPART_OPTIMIZATION);
			}
			if(validateMultipartOptimization==null || StringUtils.isEmpty(validateMultipartOptimization)) {
				validateMultipartOptimization = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_COMMONS_VALIDATE_MULTIPART_OPTIMIZATION);
			}
			if(validateMultipartOptimization!=null && !StringUtils.isEmpty(validateMultipartOptimization)) {
				if(enabled.equals(validateMultipartOptimization.trim())) {
					configOpenApi4j.setValidateMultipartOptimization(true);
				}
				else if(disabled.equals(validateMultipartOptimization.trim())) {
					configOpenApi4j.setValidateMultipartOptimization(false);
				}
			}
		}

		// Flag esclusivi di swagger_request_validator (specifici della libreria, niente commons).
		if(swaggerRV) {
			String injectingAdditionalPropertiesFalse = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_INJECTING_ADDITIONAL_PROPERTIES_FALSE);
			if(injectingAdditionalPropertiesFalse!=null && !StringUtils.isEmpty(injectingAdditionalPropertiesFalse)) {
				if(enabled.equals(injectingAdditionalPropertiesFalse.trim())) {
					configOpenApi4j.setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse(true);
				}
				else if(disabled.equals(injectingAdditionalPropertiesFalse.trim())) {
					configOpenApi4j.setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse(false);
				}
			}

			String resolveFullyApiSpec = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_SWAGGER_REQUEST_VALIDATOR_RESOLVE_FULLY_API_SPEC);
			if(resolveFullyApiSpec!=null && !StringUtils.isEmpty(resolveFullyApiSpec)) {
				if(enabled.equals(resolveFullyApiSpec.trim())) {
					configOpenApi4j.setSwaggerRequestValidator_ResolveFullyApiSpec(true);
				}
				else if(disabled.equals(resolveFullyApiSpec.trim())) {
					configOpenApi4j.setSwaggerRequestValidator_ResolveFullyApiSpec(false);
				}
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
	public static boolean readBooleanValueWithDefault(List<Proprieta> proprieta, String nome, boolean defaultValue) {
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
	
	public static boolean isValidazioneAbilitata(Logger log, List<Proprieta> proprieta, OpenSPCoop2Message responseMessage, int codiceRitornato) throws UtilsException, MessageException, MessageNotSupportedException {
		
		OpenSPCoop2RestMessage<?> restMsg = responseMessage.castAsRest();
		
		// Controllo se validare risposte vuote
		boolean defaultValidateEmptyResponse = true; // default. devo controllare gli header etc...
		boolean validateEmptyResponse = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_EMPTY_RESPONSE_ENABLED, defaultValidateEmptyResponse); 
		if(!validateEmptyResponse) {
			boolean hasContent = restMsg.hasContent();
			if(!hasContent) {
				return false;
			}
		}
		
		// Controllo se validare i fault generati da govway prima di arrivare alla validazione.
		// Sono errori interni che potrebbero non essere definiti nell'interfaccia.
		// per default questa validazione è disabilitata
		boolean defaultValidateGovwayFault = false;
		boolean validateGovwayFault = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_FAULT_GOVWAY_ENABLED, defaultValidateGovwayFault);
		if(!validateGovwayFault) {
			boolean isFaultGovway = MessageRole.FAULT.equals(responseMessage.getMessageRole());
			if(isFaultGovway) {
				return false;
			}
		}
		
		// Controllo se validare problem detail
		boolean defaultValidateProblemDetail = true; // dovrebbero far parte dell'interfaccia essendo generati dal server o dalla controparte (non sono fault generati da govway)
		boolean validateProblemDetail = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_PROBLEM_DETAIL_ENABLED, defaultValidateProblemDetail);
		if(!validateProblemDetail &&
			restMsg.isProblemDetailsForHttpApis_RFC7807()) {
			return false;
		}
		
		// Controllo se validare solo determinati codici http
		String valueS = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_ENABLED);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			
			boolean defaultNot = false; 
			boolean not = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_NOT, defaultNot);
			
			List<String> codici = new ArrayList<>();
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
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' possiede un formato errato; atteso: codiceMin-codiceMax");
						}
						String codiceMin = tmp[0];
						String codiceMax = tmp[1];
						if(codiceMin==null || StringUtils.isEmpty(codiceMin.trim())) {
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' possiede un formato errato (intervallo minimo non definito); atteso: codiceMin-codiceMax");
						}
						if(codiceMax==null || StringUtils.isEmpty(codiceMax.trim())) {
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' possiede un formato errato (intervallo massimo non definito); atteso: codiceMin-codiceMax");
						}
						codiceMin = codiceMin.trim();
						codiceMax = codiceMax.trim();
						int codiceMinInt = -1;
						try {
							codiceMinInt = Integer.valueOf(codiceMin);
						}catch(Exception e) {
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' contiene un intervallo minimo '"+codiceMin+"' che non è un numero intero");
						}
						int codiceMaxInt = -1;
						try {
							codiceMaxInt = Integer.valueOf(codiceMax);
						}catch(Exception e) {
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' contiene un intervallo massimo '"+codiceMax+"' che non è un numero intero");
						}
						if(codiceMaxInt<=codiceMinInt) {
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' contiene un intervallo massimo '"+codiceMax+"' minore o uguale all'intervallo minimo '"+codiceMin+"'");
						}
						if( (codiceMinInt <= codiceRitornato) && (codiceRitornato <= codiceMaxInt)) {
							match = true;
							break;
						}
					}
					else {
						try {
							int codiceInt = Integer.parseInt(codice);
							if(codiceInt == codiceRitornato) {
								match = true;
								break;
							}
						}catch(Exception e) {
							throw new UtilsException("Codice '"+codice+"' indicato nella proprietà '"+CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_RETURN_CODE_LIST_SEPARATOR+"' non è un numero intero");
						}
					}
				}
				
				if(match) {
					return !not ;
				}
				else {
					return not;
				}
			}
		}
		
		
		// Controllo se validare solo determinati content-type
		valueS = ValidatoreMessaggiApplicativiRest.readValue(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_LIST_ENABLED);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			
			boolean defaultNot = false; 
			boolean not = readBooleanValueWithDefault(proprieta, CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_REST_CONTENT_TYPE_NOT, defaultNot);
			
			List<String> contentTypes = new ArrayList<>();
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
								
				if(ContentTypeUtilities.isMatch(log, contentTypeRisposta, contentTypes)) {
					return !not ;
				}
				else {				
					return not;
				}
			}
		}
		
		
		return true;
		
	}
	
}
