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

package org.openspcoop2.message.soap;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;

/**
 * SOAPFaultCode
 *
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum SOAPFaultCode implements Serializable {
	DataEncodingUnknown, MustUnderstand, Receiver, Sender, VersionMismatch;
	
	public QName toQName(MessageType messageType,String prefix) throws MessageException,MessageNotSupportedException{
		String namespace = SoapUtils.getSoapEnvelopeNS(messageType);
		if(prefix!=null)
			return new QName(namespace, this.toString(messageType), prefix);
		else
			return new QName(namespace, this.toString(messageType));
	}

	public String toString(MessageType messageType) throws MessageException {
		
		if(!MessageType.SOAP_11.equals(messageType) && !MessageType.SOAP_12.equals(messageType)){
			throw new MessageException("Require SOAP Message Type, found "+messageType+" Type");
		}
		
		switch (this) {
		case DataEncodingUnknown: 
			return "DataEncodingUnknown";
		case MustUnderstand: 
			return "MustUnderstand";
		case Receiver: 
			return messageType.equals(MessageType.SOAP_11) ? "Server" : "Receiver";
		case Sender: 
			return messageType.equals(MessageType.SOAP_11) ? "Client" : "Sender";
		case VersionMismatch: 
			return "VersionMismatch";
		default:
			return messageType.equals(MessageType.SOAP_11) ? "Server" : "Receiver";
		}
	}
}



