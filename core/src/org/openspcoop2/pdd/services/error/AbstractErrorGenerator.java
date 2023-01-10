/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.services.error;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.config.FaultBuilderConfig;
import org.openspcoop2.message.config.IntegrationErrorCollection;
import org.openspcoop2.message.config.IntegrationErrorConfiguration;
import org.openspcoop2.message.config.IntegrationErrorReturnConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.slf4j.Logger;


/**
 * RequestInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractErrorGenerator {

	protected boolean internalErrorConfiguration;
	
	protected Logger log;
	protected IProtocolFactory<?> protocolFactory;
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}
	protected String idModulo;
	protected IDSoggetto identitaPdD;
	
	protected IDSoggetto mittente;
	protected IDServizio idServizio;
	protected String servizioApplicativo;
	
	protected OpenSPCoop2Properties openspcoopProperties;
	protected RequestInfo requestInfo;
	protected MessageType requestMessageType;
	protected ServiceBinding serviceBinding;
	
	protected TipoPdD tipoPdD;
	public void updateTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	
	protected MessageType forceMessageTypeResponse;
	public void setForceMessageTypeResponse(MessageType forceMessageTypeResponse) {
		this.forceMessageTypeResponse = forceMessageTypeResponse;
	}
	
	protected AbstractErrorGenerator(Logger log, String idModulo, RequestInfo requestInfo,
			TipoPdD tipoPdD, boolean internalErrorConfiguration) throws ProtocolException{
		this.log = log;
		this.protocolFactory = requestInfo.getProtocolFactory();
		this.idModulo = idModulo;
		this.identitaPdD = requestInfo.getIdentitaPdD();
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
		this.requestInfo = requestInfo;
		
		this.tipoPdD = tipoPdD;
		
		this.internalErrorConfiguration = internalErrorConfiguration;
		
		if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
			this.requestMessageType = requestInfo.getProtocolRequestMessageType();
			this.serviceBinding = requestInfo.getProtocolServiceBinding();
		}
		else{
			this.requestMessageType = requestInfo.getIntegrationRequestMessageType();
			this.serviceBinding = requestInfo.getIntegrationServiceBinding();
		}
	}
	
	protected String getInterfaceName() {
		String nomePorta = null;
		if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null) {
			nomePorta = this.requestInfo.getProtocolContext().getInterfaceName();
		}
		return nomePorta;
	}
	
	public void updateRequestMessageType(MessageType requestMessageType){
		this.requestMessageType = requestMessageType;
	}
	
	public void updateServiceBinding(ServiceBinding serviceBinding){
		this.serviceBinding = serviceBinding;
	}
	
	public void updateDominio(IDSoggetto identitaPdD){
		this.identitaPdD = identitaPdD;
	}
	
	public void updateInformazioniCooperazione(IDSoggetto mittente, IDServizio idServizio){
		this.mittente = mittente;
		this.idServizio = idServizio;
	}
	public void updateInformazioniCooperazione(String servizioApplicativo){
		this.servizioApplicativo = servizioApplicativo;
	}
	
	protected IntegrationErrorConfiguration getConfigurationForError(IntegrationError integrationError){
		IntegrationErrorCollection errorCollection = null;
		if(this.internalErrorConfiguration){
			errorCollection = this.requestInfo.getBindingConfig().getInternalIntegrationErrorConfiguration(this.serviceBinding);
		}else{
			errorCollection = this.requestInfo.getBindingConfig().getExternalIntegrationErrorConfiguration(this.serviceBinding);
		}
		IntegrationErrorConfiguration config = errorCollection.getIntegrationError(integrationError);
		if(config==null){
			config = errorCollection.getIntegrationError(IntegrationError.DEFAULT);
		}
		return config;
	}
	protected MessageType getMessageTypeForError(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		return config.getMessageType(this.requestInfo.getProtocolContext(), this.serviceBinding, this.requestMessageType);
	}

	protected IntegrationErrorReturnConfiguration getReturnConfigForError(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		IntegrationErrorReturnConfiguration configR = config.getErrorReturnConfig();
		boolean portaDelegata = (this instanceof RicezioneContenutiApplicativiInternalErrorGenerator);
		if(portaDelegata) {
			configR.setRetryAfterSeconds(this.openspcoopProperties.getServiceUnavailableRetryAfterSeconds_pd());
			configR.setRetryRandomBackoffSeconds(this.openspcoopProperties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd());
		}
		else {
			configR.setRetryAfterSeconds(this.openspcoopProperties.getServiceUnavailableRetryAfterSeconds_pa());
			configR.setRetryRandomBackoffSeconds(this.openspcoopProperties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa());
		}
		return configR;
	}
	
	protected boolean isUseInternalFault(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		return config.isUseInternalFault();
	}
	
	protected ConfigurationRFC7807 getRfc7807ForError(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		return config.getRfc7807();
	}

	protected MessageType getMessageTypeForErrorSafeMode(IntegrationError integrationError){
		MessageType msgTypeErrorResponse = null;
		try{
			msgTypeErrorResponse = this.requestMessageType;
			if(this.forceMessageTypeResponse!=null){
				msgTypeErrorResponse = this.forceMessageTypeResponse;
			}
			else{
				msgTypeErrorResponse = getMessageTypeForError(integrationError);
			}
		}catch(Exception eError){
			// non dovrebbero avvenire errori, altrimenti si utilizza il message type della richiesta 
		}
		return msgTypeErrorResponse;
	}
	
	protected ConfigurationRFC7807 getRfc7807ForErrorSafeMode(IntegrationError integrationError){
		ConfigurationRFC7807 rfc7807 = null;
		try{
			rfc7807 = getRfc7807ForError(integrationError);
		}catch(Exception eError){
			// non dovrebbero avvenire errori, altrimenti si utilizza il message type della richiesta 
		}
		return rfc7807;
	}
	
	public static IntegrationFunctionError getIntegrationInternalError(Context context) {
		IntegrationFunctionError integrationError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
		if(context!=null && context.containsKey(Costanti.RICHIESTA_INOLTRATA_BACKEND)) {
			Object o = context.getObject(Costanti.RICHIESTA_INOLTRATA_BACKEND);
			if(o!=null && o instanceof String) {
				String s = (String) o;
				if(Costanti.RICHIESTA_INOLTRATA_BACKEND_VALORE.equals(s)) {
					integrationError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
				}
			}
		}
		return integrationError;
	}
	
	private FaultBuilderConfig getFaultBuilderConfig(ErroriProperties erroriProperties, IntegrationFunctionError integrationFunctionError, 
			MessageType msgTypeErrorResponse, boolean useProblemRFC7807, String errore) {
		try {
			FaultBuilderConfig config = new FaultBuilderConfig();
			IntegrationError integrationError = erroriProperties.getIntegrationError(integrationFunctionError);
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			
			config.setHttpReturnCode(returnConfig.getHttpReturnCode());
			config.setGovwayReturnCode(returnConfig.getGovwayReturnCode());
			
			config.setRfc7807Type(erroriProperties.isTypeEnabled());
			config.setRfc7807WebSite(erroriProperties.getWebSite(integrationFunctionError));
			
			if(MessageType.SOAP_11.equals(msgTypeErrorResponse) || MessageType.SOAP_12.equals(msgTypeErrorResponse) || !useProblemRFC7807) {
				config.setActor(this.openspcoopProperties.getProprietaGestioneErrorePD(this.protocolFactory.createProtocolManager()).getFaultActor());
			}
			
			String codiceEccezioneGW = org.openspcoop2.protocol.basic.Costanti.getTransactionSoapFaultCode(returnConfig.getGovwayReturnCode(),erroriProperties.getErrorType(integrationFunctionError));
			if(MessageType.SOAP_11.equals(msgTypeErrorResponse) || MessageType.SOAP_12.equals(msgTypeErrorResponse)) {
				if(MessageType.SOAP_11.equals(msgTypeErrorResponse)) {
					String code11 = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER;
					if(returnConfig.getGovwayReturnCode()<=499) {
						code11 = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT;
					}
					codiceEccezioneGW = code11 +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+codiceEccezioneGW;
				}
				QName eccezioneNameGovway = null;
				if(MessageType.SOAP_11.equals(msgTypeErrorResponse)) {
					String prefix = "SOAP-ENV";
					eccezioneNameGovway = new QName(org.openspcoop2.message.constants.Costanti.SOAP_ENVELOPE_NAMESPACE, codiceEccezioneGW, prefix);
					config.setPrefixSoap(prefix);
				}
				else {
					eccezioneNameGovway = this.getProtocolFactory().createErroreApplicativoBuilder().getQNameEccezioneIntegrazione(this.openspcoopProperties.getProprietaGestioneErrorePD(this.protocolFactory.createProtocolManager()).getDefaultFaultCodeIntegrationNamespace(),
							codiceEccezioneGW);
				}
				config.setErrorCode(eccezioneNameGovway);
			}
			
			boolean genericDetails = returnConfig.isGenericDetails();
			if(!genericDetails && erroriProperties.isForceGenericDetails(integrationFunctionError)) {
				genericDetails = true;
			}
			if (org.openspcoop2.protocol.basic.Costanti.isTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS()) {
				genericDetails = false;
			}
			if(errore!=null && !"null".equals(errore) && !genericDetails) {
				config.setDetails(errore);
			}
			else {
				config.setDetails(erroriProperties.getGenericDetails(integrationFunctionError));
			}
			
			String govwayType = erroriProperties.getErrorType(integrationFunctionError);
			config.setHeaderErrorTypeName(this.openspcoopProperties.getErroriHttpHeaderGovWayType());
			config.setHeaderErrorTypeValue(govwayType);
			
			if(org.openspcoop2.protocol.basic.Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE()) {
				if(org.openspcoop2.protocol.basic.Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE()) {
					config.setRfc7807Title(StringUtils.join(
						     StringUtils.splitByCharacterTypeCamelCase(govwayType),
						     ' '));
				}
				else {
					config.setRfc7807Title(govwayType);
				}
				
				if(org.openspcoop2.protocol.basic.Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM()) {
					config.setRfc7807GovWayTypeHeaderErrorTypeName(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TYPE());
					config.setRfc7807GovWayTypeHeaderErrorTypeValue(govwayType);
				}
			}
			else {
				config.setRfc7807GovWayTypeHeaderErrorTypeName(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TYPE());
				config.setRfc7807GovWayTypeHeaderErrorTypeValue(govwayType);
			}
			
			return config;
		}catch(Throwable t) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("FaultBuilderConfig non costruibile: "+t.getMessage(),t);
			return null;
		}
	}
	
	protected IntegrationError convertToIntegrationError(IntegrationFunctionError integrationFunctionError) {
		ErroriProperties erroriProperties = null;
		IntegrationError integrationError = null;
		try {
			erroriProperties = ErroriProperties.getInstance(this.log);
			integrationError = erroriProperties.getIntegrationError(integrationFunctionError);
		}catch(Throwable t) {
			integrationError = IntegrationError.INTERNAL_REQUEST_ERROR;
		}
		return integrationError;
	}
	
	public OpenSPCoop2Message buildFault(Throwable e, Context context){
		IntegrationFunctionError integrationFunctionError = getIntegrationInternalError(context);
		return _buildFault(e, context, integrationFunctionError);
	}
	public OpenSPCoop2Message buildFault(Throwable e, Context context, IntegrationFunctionError integrationFunctionError){
		return _buildFault(e, context, integrationFunctionError);
	}
	public OpenSPCoop2Message _buildFault(Throwable e, Context context, IntegrationFunctionError integrationFunctionError){
		//this.log.error(e.getMessage(),e);
		ErroriProperties erroriProperties = null;
		IntegrationError integrationError = null;
		try {
			erroriProperties = ErroriProperties.getInstance(this.log);
			integrationError = erroriProperties.getIntegrationError(integrationFunctionError);
		}catch(Throwable t) {
			integrationError = IntegrationError.INTERNAL_REQUEST_ERROR;
		}
		boolean useProblemRFC7807 = this.getRfc7807ForErrorSafeMode(integrationError)!=null;
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		FaultBuilderConfig config = null;
		if(erroriProperties!=null) {
			config = getFaultBuilderConfig(erroriProperties, integrationFunctionError, msgTypeErrorResponse, useProblemRFC7807, e!=null ? e.getMessage() : null);
		}
		OpenSPCoop2Message msgFault = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, config, e);
		return msgFault;
	}
	

	public OpenSPCoop2Message buildFault(String errore, Context context){
		IntegrationFunctionError integrationFunctionError = getIntegrationInternalError(context);
		return _buildFault(errore, context, integrationFunctionError);
	}
	public OpenSPCoop2Message buildFault(String errore, Context context, IntegrationFunctionError integrationFunctionError){
		return _buildFault(errore, context, integrationFunctionError);
	}
	
	private OpenSPCoop2Message _buildFault(String errore, Context context, IntegrationFunctionError integrationFunctionError){
		//this.log.error(errore);
		ErroriProperties erroriProperties = null;
		IntegrationError integrationError = null;
		try {
			erroriProperties = ErroriProperties.getInstance(this.log);
			integrationError = erroriProperties.getIntegrationError(integrationFunctionError);
		}catch(Throwable t) {
			integrationError = IntegrationError.INTERNAL_REQUEST_ERROR;
			integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
		}
		boolean useProblemRFC7807 = this.getRfc7807ForErrorSafeMode(integrationError)!=null;
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		FaultBuilderConfig config = null;
		if(erroriProperties!=null) {
			config = getFaultBuilderConfig(erroriProperties, integrationFunctionError, msgTypeErrorResponse, useProblemRFC7807, errore);
		}
		OpenSPCoop2Message msgFault = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, config, errore);
		return msgFault;
	}
}
