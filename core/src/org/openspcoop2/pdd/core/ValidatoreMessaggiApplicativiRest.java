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
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.entity.BinaryHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.BinaryHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.ElementHttpResponseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
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

	
	

	
	/* ------ Costruttore -------------- */
	public ValidatoreMessaggiApplicativiRest(RegistroServiziManager registro,IDServizio idServizio,
			OpenSPCoop2Message message,boolean readInterfaceAccordoServizio, IProtocolFactory<?> protocolFactory)throws ValidatoreMessaggiApplicativiException{
		
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
		
		try{
			if(readInterfaceAccordoServizio){
				this.accordoServizioWrapper = this.registroServiziManager.getRestAccordoServizio(idServizio,InformationApiSource.SPECIFIC,true);
			}else{
				this.accordoServizioWrapper = this.registroServiziManager.getRestAccordoServizio(idServizio,InformationApiSource.REGISTRY,true);
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
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD, e.getMessage()));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD, e.getMessage()));
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
		switch (this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()) {
		case WADL:
			interfaceType = "Interfaccia WADL";
			format=ApiFormats.WADL;
			break;
		case SWAGGER_2:
			interfaceType = "Interfaccia Swagger 2.0";
			format=ApiFormats.SWAGGER_2;
			break;
		case OPEN_API_3:
			interfaceType = "Interfaccia OpenAPI 3.0";
			format=ApiFormats.OPEN_API_3;
			break;
		default:
			// altre interfacce non supportate per rest
			throw new ValidatoreMessaggiApplicativiException("Tipo di interfaccia ["+this.accordoServizioWrapper.getAccordoServizio().getFormatoSpecifica()+"] non supportata");
		}
		
		IApiValidator apiValidator = null;
		ApiValidatorConfig validatorConfig = null;
		try {
			OpenSPCoop2Properties op2PropertieS = OpenSPCoop2Properties.getInstance();
			apiValidator = ApiFactory.newApiValidator(format);
			validatorConfig = new ApiValidatorConfig();
			validatorConfig.setXmlUtils(XMLUtils.getInstance(this.message.getFactory()));
			validatorConfig.setVerbose(op2PropertieS.isValidazioneContenutiApplicativi_debug());
			validatorConfig.setPolicyAdditionalProperties(op2PropertieS.getValidazioneContenutiApplicativi_json_policyAdditionalProperties());
			apiValidator.init(this.logger, this.accordoServizioWrapper.getApi(), validatorConfig);
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
				if(transportContext.getInterfaceName()!=null) {
					PorteNamingUtils namingUtils = new PorteNamingUtils(this.protocolFactory);
					if(portaApplicativa){
						normalizedInterfaceName = namingUtils.normalizePA(transportContext.getInterfaceName());
					}
					else {
						normalizedInterfaceName = namingUtils.normalizePD(transportContext.getInterfaceName());
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
						((ElementHttpRequestEntity)httpRequest).setContent(this.message.castAsRestXml().getContent());
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
						((ElementHttpResponseEntity)httpResponse).setContent(this.message.castAsRestXml().getContent());
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
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(interfaceType,e.getMessage()));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(interfaceType, e.getMessage()));
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
	
}
