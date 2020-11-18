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



package org.openspcoop2.pdd.core.handlers;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.ForcedResponseMessage;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**	
 * Contiene la definizione di una eccezione lanciata dalle classi del package org.openspcoop.pdd.core.handlers
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class HandlerException extends Exception {

	private boolean customizedResponse=false;	
	public boolean isCustomizedResponse() {
		return this.customizedResponse;
	}
	public void setCustomizedResponse(boolean customizedResponse) {
		this.customizedResponse = customizedResponse;
	}

	private boolean customizedResponseAs4xxCode=false; // se false viene ritornato un errore 5xx
	public boolean isCustomizedResponseAs4xxCode() {
		return this.customizedResponseAs4xxCode;
	}
	public void setCustomizedResponseAs4xxCode(boolean customizedResponseAs4xxCode) {
		this.customizedResponseAs4xxCode = customizedResponseAs4xxCode;
	}
	
	private String customizedResponseCode=null;
	public String getCustomizedResponseCode() {
		return this.customizedResponseCode;
	}
	public void setCustomizedResponseCode(String customizedResponseCode) {
		this.customizedResponseCode = customizedResponseCode;
	}

	private String responseCode=null;
	public String getResponseCode() {
		return this.responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	private boolean emptyResponse=false;
	public boolean isEmptyResponse() {
		return this.emptyResponse;
	}
	public void setEmptyResponse(boolean emptyResponse) {
		this.emptyResponse = emptyResponse;
	}

	private byte [] response=null;
	public byte[] getResponse() {
		return this.response;
	}
	public void setResponse(byte[] response) {
		this.response = response;
	}

	private String responseContentType;
	public String getResponseContentType() {
		return this.responseContentType;
	}
	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	private boolean emettiDiagnostico=true;
	public boolean isEmettiDiagnostico() {
		return this.emettiDiagnostico;
	}
	public void setEmettiDiagnostico(boolean emettiDiagnostico) {
		this.emettiDiagnostico = emettiDiagnostico;
	}

	private String identitaHandler = null;
	public String getIdentitaHandler() {
		return this.identitaHandler;
	}
	// Impostato dal gestore degli handler
	protected void setIdentitaHandler(String identitaHandler) {
		this.identitaHandler = identitaHandler;
	}

	private IntegrationFunctionError integrationError = null;
	public IntegrationFunctionError getIntegrationFunctionError() {
		return this.integrationError;
	}
	public void setIntegrationFunctionError(IntegrationFunctionError integrationError) {
		this.integrationError = integrationError;
	}
	
	public ErroreIntegrazione convertToErroreIntegrazione() {
		if(this.customizedResponse==false) {
			return null;
		}
		if(this.customizedResponseAs4xxCode) {
			String customizedCode = "400";
			if(this.customizedResponseCode!=null) {
				customizedCode = this.customizedResponseCode;
			}
			return ErroriIntegrazione.ERRORE_4XX_CUSTOM.get4XX_Custom(this.getMessage(), customizedCode);
		}
		else {
			String customizedCode = "500";
			if(this.customizedResponseCode!=null) {
				customizedCode = this.customizedResponseCode;
			}
			return ErroriIntegrazione.ERRORE_5XX_CUSTOM.get5XX_Custom(this.getMessage(), customizedCode);
		}
	}
	public void customized(OpenSPCoop2Message responseMessage) {	
		if(this.customizedResponse==false) {
			return;
		}
		if(this.isEmptyResponse()) {
			responseMessage.forceEmptyResponse();
		}
		else if(this.getResponse()!=null) {
			ForcedResponseMessage force = null;
			if(responseMessage instanceof AbstractBaseOpenSPCoop2Message) {
				force = new ForcedResponseMessage((AbstractBaseOpenSPCoop2Message)responseMessage);
			}
			else {
				force = new ForcedResponseMessage();
			}
			force.setContent(this.getResponse());
			if(this.getResponseContentType()!=null) {
				force.setContentType(this.getResponseContentType());
			}
			responseMessage.forceResponse(force);
		}
		if(this.getResponseCode()!=null) {
			responseMessage.setForcedResponseCode(this.getResponseCode());
		}
	}
	
	public HandlerException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public HandlerException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public HandlerException() {
		super();
	}
	public HandlerException(String msg) {
		super(msg);
	}

}
