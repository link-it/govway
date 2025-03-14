/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.soap.wsaddressing;

/**
 * Costanti
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
		
	public static final String WSA_NAMESPACE = "http://www.w3.org/2005/08/addressing";
	public static final String WSA_PREFIX = "wsa";
	
	public static final String WSA_RELATIONSHIP_TYPE_REPLY = "http://www.w3.org/2005/08/addressing/reply";
	public static final String WSA_SOAP_HEADER_TO = "To";
	public static final String WSA_SOAP_HEADER_FROM = "From";
	public static final String WSA_SOAP_HEADER_ACTION = "Action";
	public static final String WSA_SOAP_HEADER_ID = "MessageID";
	public static final String WSA_SOAP_HEADER_RELATES_TO = "RelatesTo";
	public static final String WSA_SOAP_HEADER_RELATES_TO_ATTRIBUTE = "RelationshipType";
	public static final String WSA_SOAP_HEADER_REPLY_TO = "ReplyTo";
	public static final String WSA_SOAP_HEADER_FAULT_TO = "FaultTo";
		
	public static final String WSA_SOAP_HEADER_EPR_ADDRESS = "Address";
	
	public static final String WSA_SOAP_HEADER_EPR_ADDRESS_ANONYMOUS = "http://www.w3.org/2005/08/addressing/anonymous";
	

}
