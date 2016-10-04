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

package org.openspcoop2.message;

import java.io.Serializable;

import javax.xml.namespace.QName;

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
	
	public QName toQName(SOAPVersion version,String prefix){
		String namespace = version.getSoapEnvelopeNS();
		if(prefix!=null)
			return new QName(namespace, this.toString(version), prefix);
		else
			return new QName(namespace, this.toString(version));
	}

	public String toString(SOAPVersion version) {
		switch (this) {
		case DataEncodingUnknown: 
			return "DataEncodingUnknown";
		case MustUnderstand: 
			return "MustUnderstand";
		case Receiver: 
			return version.equals(SOAPVersion.SOAP11) ? "Server" : "Receiver";
		case Sender: 
			return version.equals(SOAPVersion.SOAP11) ? "Client" : "Sender";
		case VersionMismatch: 
			return "VersionMismatch";
		default:
			return version.equals(SOAPVersion.SOAP11) ? "Server" : "Receiver";
		}
	}
}



