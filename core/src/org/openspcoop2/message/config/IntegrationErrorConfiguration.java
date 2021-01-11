/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.message.config;

import org.openspcoop2.message.constants.IntegrationErrorMessageType;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;

/**
 * IntegrationErrorConfiguration
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IntegrationErrorConfiguration implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private IntegrationErrorMessageType errorType;
	private IntegrationErrorMessageType defaultErrorType;
	private ConfigurationRFC7807 rfc7807;
	private IntegrationErrorReturnConfiguration errorReturnConfig;
	private boolean useInternalFault; // in cooperazione
		
	public IntegrationErrorConfiguration(ConfigurationRFC7807 rfc7807, IntegrationErrorMessageType errorType, 
			IntegrationErrorReturnConfiguration errorReturnConfig,  
			boolean useInternalFault){
		this.rfc7807 = rfc7807;
		this.errorType = errorType;
		this.errorReturnConfig = errorReturnConfig;
		this.useInternalFault = useInternalFault;
	}
	
	void setDefaultErrorType(IntegrationErrorMessageType defaultErrorType) {
		this.defaultErrorType = defaultErrorType;
	}

	public ConfigurationRFC7807 getRfc7807() {
		return this.rfc7807;
	}
	public IntegrationErrorReturnConfiguration getErrorReturnConfig() {
		return this.errorReturnConfig;
	}
	public boolean isUseInternalFault() {
		return this.useInternalFault;
	}
	
	public MessageType getMessageType(HttpServletTransportRequestContext request, ServiceBinding serviceBinding, MessageType requestMsgType){
		if(ServiceBinding.REST.equals(serviceBinding) && this.rfc7807!=null) {
			if(this.rfc7807.isUseAcceptHeader() && request!=null && request.getParameterTrasporto(HttpConstants.ACCEPT)!=null) {
				boolean asJson = false;
				boolean asXml = false;
				String value = request.getParameterTrasporto(HttpConstants.ACCEPT);
				String [] acceptHeaders = null;
				if(value.contains(",")) {
					acceptHeaders = value.split(",");
					for (int i = 0; i < acceptHeaders.length; i++) {
						acceptHeaders[i] = acceptHeaders[i].trim();
					}
				}
				else {
					acceptHeaders = new String [] {value.trim()};
				}
				for (String hdr : acceptHeaders) {
					if(hdr.toLowerCase().endsWith("/x-json") || hdr.toLowerCase().endsWith("/json") || hdr.toLowerCase().endsWith("+json")){
						asJson = true;
						break;
					}
					else if(hdr.toLowerCase().endsWith("/x-xml") || hdr.toLowerCase().endsWith("/xml") || hdr.toLowerCase().endsWith("+xml")){
						asXml = true;
						break;
					}
				}
				if(asJson) {
					return MessageType.JSON;
				}
				else if(asXml) {
					return MessageType.XML;
				}
			}
		}
		
		switch (this.errorType) {
		case SOAP_AS_REQUEST:
			if(MessageType.SOAP_11.equals(requestMsgType) || MessageType.SOAP_12.equals(requestMsgType) )
				return requestMsgType;			
			return getDefaultMessageType(requestMsgType);
		case SOAP_11:
			return MessageType.SOAP_11;
		case SOAP_12:
			return MessageType.SOAP_12;
		case XML:
			return MessageType.XML;
		case JSON:
			return MessageType.JSON;
		case SAME_AS_REQUEST:
			if(MessageType.SOAP_11.equals(requestMsgType) || MessageType.SOAP_12.equals(requestMsgType) )
				return requestMsgType;
			if(MessageType.XML.equals(requestMsgType) || MessageType.JSON.equals(requestMsgType) )
				return requestMsgType;
			return getDefaultMessageType(requestMsgType);
		default:
			return MessageType.BINARY; // content is null
		}
	}
	public MessageType getDefaultMessageType(MessageType requestMsgType){
		switch (this.defaultErrorType) {
		case SOAP_11:
			return MessageType.SOAP_11;
		case SOAP_12:
			return MessageType.SOAP_12;
		case XML:
			return MessageType.XML;
		case JSON:
			return MessageType.JSON;
		default:
			return MessageType.BINARY; // content is null
		}
	}
}





