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

/**
 * Costanti
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class Costanti {
		
	/** SOAP MESSAGE PROPERTY */
	public final static String SOAP_MESSAGE_PROPERTY_MESSAGE_TYPE = "OP2_MESSAGE_TYPE";
	
	/** ContentType Speciali */
	public final static String CONTENT_TYPE_ALL = "*";
	public final static String CONTENT_TYPE_NOT_DEFINED = "NotDefined";
	
	/** SOAP Action */
	public static final String SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION = "SOAPAction";
	public static final String SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION = "action";
	
	/** SOAP Envelope namespace */
	public static final String SOAP_ENVELOPE_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String SOAP12_ENVELOPE_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
	
	/** XMLNamespace */
	public static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";
	public static final String XMLNS_LOCAL_NAME = "xmlns";
	
	/** Tunnel */
	public static final String SOAP_TUNNEL_NAMESPACE = "http://www.openspcoop2.org/pdd/services/PDtoSOAP";
	public static final String SOAP_TUNNEL_ATTACHMENT_ELEMENT_OPENSPCOOP2_TYPE = "SOAPTunnel";
	public static final String SOAP_TUNNEL_ATTACHMENT_ELEMENT = "Attachments";
	
	/** Costanti per posizione errore degli id WSSecurity */
	public static final String FIND_ERROR_ENCRYPTED_REFERENCES = "[WSS-Encrypt ReferencesSearch]";
	public static final String FIND_ERROR_SIGNATURE_REFERENCES = "[WSS-Signature ReferencesSearch]";
	
	/** Context Empty */
	public static final String CONTEXT_EMPTY = "@EMPTY@";
}
