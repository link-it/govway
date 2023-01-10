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



package org.openspcoop2.message.config;

import java.util.List;

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
			if(this.rfc7807.isUseAcceptHeader() && request!=null && request.getHeaderValues(HttpConstants.ACCEPT)!=null && !request.getHeaderValues(HttpConstants.ACCEPT).isEmpty()) {
				List<String> values = request.getHeaderValues(HttpConstants.ACCEPT);
				// https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
				/*
				 * Example from rfc:
       				Accept: text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c
				    Verbally, this would be interpreted as "text/html and text/x-c are the preferred media types, but if they do not exist, then send the text/x-dvi entity, and if that does not exist, send the text/plain entity."
				 **/
				boolean readQparameter = true;
				MessageType mt = readMessageTypeFromAccept(values, !readQparameter); // prima priorità a quelli senza q parameter
				if(mt!=null) {
					return mt;
				}
				
				// altrimenti esamino quelli con q parameter
				mt = readMessageTypeFromAccept(values, readQparameter); // prima priorità a quelli senza q parameter
				if(mt!=null) {
					return mt;
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
	
	private MessageType readMessageTypeFromAccept(List<String> values, boolean readQparameter) {
		boolean asJson = false;
		boolean asXml = false;
		// https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
		for (String value : values) {
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
			double asJsonParamterQ = 0;
			double asXmlParamterQ = 0;
			for (String hdr : acceptHeaders) {
				if(readQparameter) {
					if(hdr.contains(";")) {
						String [] tmp = hdr.split(";");
						if(tmp!=null && tmp.length==2) {
							String mediaType = tmp[0];
							String q = tmp[1];
							if(mediaType!=null && q!=null) {
								q = q.trim();
								mediaType = mediaType.trim();
								if(q.contains("=")) {
									String [] tmpQ = q.split("=");
									if(tmpQ!=null && tmpQ.length==2) {
										String qParam = tmpQ[0];
										String qParamValue = tmpQ[1];
										if(qParam!=null && "q".equalsIgnoreCase(qParam.trim()) && qParamValue!=null) {
											double d = 0;
											try {
												d = Double.valueOf(qParamValue.trim());
												if(d>0) {
													if(isJsonMediaType(mediaType)){
														if(d>asJsonParamterQ) {
															asJsonParamterQ = d;
															asJson = true;
														}
													}
													else if(isXmlMediaType(mediaType)){
														if(d>asXmlParamterQ) {
															asXmlParamterQ = d;
															asXml = true;
														}
													}					
												}
											}catch(Throwable t) {}
										}
									}
								}
							}
						}
					}
				}
				else {
					if(isJsonMediaType(hdr)){
						asJson = true;
						break;
					}
					else if(isXmlMediaType(hdr)){
						asXml = true;
						break;
					}
				}
			}
			
			if(asJson && asXml && readQparameter) {
				if(asJsonParamterQ>asXmlParamterQ) {
					return MessageType.JSON;
				}
				else {
					return MessageType.XML;
				}
			}
			
			if(asJson) {
				return MessageType.JSON;
			}
			else if(asXml) {
				return MessageType.XML;
			}
		}
		return null;
	}
	private boolean isJsonMediaType(String hdrValue) {
		if(hdrValue.toLowerCase().endsWith("/x-json") || hdrValue.toLowerCase().endsWith("/json") || hdrValue.toLowerCase().endsWith("+json")){
			return true;
		}
		return false;
	}
	private boolean isXmlMediaType(String hdrValue) {
		if(hdrValue.toLowerCase().endsWith("/x-xml") || hdrValue.toLowerCase().endsWith("/xml") || hdrValue.toLowerCase().endsWith("+xml")){
			return true;
		}
		return false;
	}
}





