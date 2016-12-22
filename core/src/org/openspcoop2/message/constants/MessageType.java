/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
	
	public String getMessageVersionAsString(){
		switch (this) {
			case SOAP_11:
				return "Soap-1.1";
			case SOAP_12:
				return "Soap-1.2";
			case XML:
				return "Xml";
			case JSON:
				return "Json";
			case BINARY:
				return "Binary";
			case MIME_MULTIPART:
				return "MIME-Multipart";
			default:
				throw new RuntimeException("Unsupported-Type");
		}
		
	}
	
	
}
