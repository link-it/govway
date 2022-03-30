/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.constants;

import java.io.Serializable;

/**
 * MessageType
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum MessageType implements Serializable {
	
	SOAP_11, SOAP_12, XML, JSON, BINARY, MIME_MULTIPART;

	
	// Generic Utilities
	
	private static final String LABEL_SOAP_11 = "Soap-1.1";
	private static final String LABEL_SOAP_12 = "Soap-1.2";
	private static final String LABEL_XML = "Xml";
	private static final String LABEL_JSON = "Json";
	private static final String LABEL_BINARY = "Binary";
	private static final String LABEL_MIME_MULTIPART = "MIME-Multipart";
	
	public String getMessageVersionAsString(){
		switch (this) {
			case SOAP_11:
				return LABEL_SOAP_11;
			case SOAP_12:
				return LABEL_SOAP_12;
			case XML:
				return LABEL_XML;
			case JSON:
				return LABEL_JSON;
			case BINARY:
				return LABEL_BINARY;
			case MIME_MULTIPART:
				return LABEL_MIME_MULTIPART;
			default:
				throw new RuntimeException("Unsupported-Type");
		}
	}
	public String getLabelMessageVersion(){
		return this.getMessageVersionAsString();
	}
	
	public static MessageType getMessageTypeFromLabel(String v) {
		if(v==null) {
			return null;
		}
		if(LABEL_SOAP_11.equals(v)) {
			return MessageType.SOAP_11;
		}
		else if(LABEL_SOAP_12.equals(v)) {
			return MessageType.SOAP_12;
		}
		else if(LABEL_XML.equals(v)) {
			return MessageType.XML;
		}
		else if(LABEL_JSON.equals(v)) {
			return MessageType.JSON;
		}
		else if(LABEL_BINARY.equals(v)) {
			return MessageType.BINARY;
		}
		else if(LABEL_MIME_MULTIPART.equals(v)) {
			return MessageType.MIME_MULTIPART;
		}
		return null;
	}
	
}
