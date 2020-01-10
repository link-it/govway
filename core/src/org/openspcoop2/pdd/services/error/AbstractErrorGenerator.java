/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.config.IntegrationErrorCollection;
import org.openspcoop2.message.config.IntegrationErrorConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
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

	protected int getReturnCodeForError(IntegrationError integrationError){
		IntegrationErrorConfiguration config = this.getConfigurationForError(integrationError);
		return config.getHttpReturnCode();
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
	
	public OpenSPCoop2Message buildFault(Exception e){
		//this.log.error(e.getMessage(),e);
		boolean useProblemRFC7807 = this.getRfc7807ForErrorSafeMode(IntegrationError.INTERNAL_ERROR)!=null;
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(IntegrationError.INTERNAL_ERROR);
		return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
	}
	public OpenSPCoop2Message buildFault(String errore){
		//this.log.error(errore);
		boolean useProblemRFC7807 = this.getRfc7807ForErrorSafeMode(IntegrationError.INTERNAL_ERROR)!=null;
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(IntegrationError.INTERNAL_ERROR);
		return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, errore);
	}
}
