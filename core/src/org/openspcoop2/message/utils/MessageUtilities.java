/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
package org.openspcoop2.message.utils;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * ContentTypeUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtilities {
	
	public static String getDefaultContentType(MessageType messageType){
		String contentType = null;
		switch (messageType) {
		case SOAP_11:
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			break;
		case SOAP_12:		
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
			break;
		case XML:
			contentType = HttpConstants.CONTENT_TYPE_XML;
			break;
		case JSON:		
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			break;
		case BINARY:		
			contentType = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
			break;
		case MIME_MULTIPART:		
			contentType = HttpConstants.CONTENT_TYPE_MULTIPART;
			break;
		}
		return contentType;
	}
	
	public static void checkType(MessageType messageType,OpenSPCoop2Message msg) throws MessageException{
		switch (messageType) {
			case SOAP_11:
				msg.castAsSoap(); // check
				break;
			case SOAP_12:
				msg.castAsSoap(); // check
				break;
			case XML:
				msg.castAsRestXml(); // check
				break;
			case JSON:
				msg.castAsRestJson(); // check
				break;
			case BINARY:
				msg.castAsRestBinary(); // check
				break;
			case MIME_MULTIPART:
				msg.castAsRestMimeMultipart(); // check
				break;
		}
	}
	
	
	public static OpenSPCoop2Message buildEmptyMessage(MessageType messageType,MessageRole messageRole) {

		try{
			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
			OpenSPCoop2Message msg = mf.createEmptyMessage(messageType,messageRole);
			return msg;
		} catch(Exception e) {
			return null;
		}
	}
}
