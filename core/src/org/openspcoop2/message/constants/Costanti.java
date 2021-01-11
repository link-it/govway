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

package org.openspcoop2.message.constants;

import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Costanti
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
		
	/** SOAP MESSAGE PROPERTY */
	public final static String SOAP_MESSAGE_PROPERTY_MESSAGE_TYPE = "OP2_MESSAGE_TYPE";
	
	/** ContentType Speciali */
	public final static String CONTENT_TYPE_ALL = "*";
	public final static String CONTENT_TYPE_NOT_DEFINED = "NotDefined";
	
	/** SOAP Action */
	public static final String SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION = HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION;
	public static final String SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION = HttpConstants.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION;
	
	/** SOAP Envelope namespace */
	public static final String SOAP_ENVELOPE_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String SOAP12_ENVELOPE_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
	
	/** SOAP Fault Code */
	public static final String SOAP11_FAULT_CODE_SEPARATOR = ".";
	public static final String SOAP11_FAULT_CODE_SERVER = "Server";
	public static final String SOAP11_FAULT_CODE_CLIENT = "Client";
	public static final String SOAP12_FAULT_CODE_SERVER = "Receiver";
	public static final String SOAP12_FAULT_CODE_CLIENT = "Sender";
	
	/** XMLNamespace */
	public static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";
	public static final String XMLNS_LOCAL_NAME = "xmlns";
	
	/** Tunnel */
	public static final String SOAP_TUNNEL_NAMESPACE = "http://www.govway.org/out/xml2soap";
	public static final String SOAP_TUNNEL_ATTACHMENT_ELEMENT_OPENSPCOOP2_TYPE = "SOAPTunnel";
	public static final String SOAP_TUNNEL_ATTACHMENT_ELEMENT = "Attachments";
	
	/** Costanti per posizione errore degli id WSSecurity */
	public static final String FIND_ERROR_ENCRYPTED_REFERENCES = "[WSS-Encrypt ReferencesSearch]";
	public static final String FIND_ERROR_SIGNATURE_REFERENCES = "[WSS-Signature ReferencesSearch]";
		
	/** Context Empty */
	public static final String CONTEXT_EMPTY = "@EMPTY@";
	
	/** SAML */
	public static final String SAML_20_NAMESPACE = "urn:oasis:names:tc:SAML:2.0:assertion";
	public static final String SAML_20_ASSERTION_ID = "ID";
	public static final String XPATH_SAML_20_ASSERTION = "//{"+SAML_20_NAMESPACE+"}:Assertion";
	
	public static final String SAML_11_NAMESPACE = "urn:oasis:names:tc:SAML:1.0:assertion";
	public static final String SAML_11_ASSERTION_ID = "AssertionID";
	public static final String XPATH_SAML_11_ASSERTION = "//{"+SAML_11_NAMESPACE+"}:Assertion";
	
	/** FAULT DEFAULT */
	public static final String DEFAULT_SOAP_FAULT_STRING = "InternalError";
	public static final String DEFAULT_SOAP_FAULT_ACTOR = "http://govway.org/fault";
	
	/** PROBLEM */
    public final static String ERRORE_GOVWAY = "ERRORE_GOVWAY";
    public final static String TIPO_GOVWAY = "govway";
    public final static String TIPO_RFC7807 = "rfc7807";
    public final static String ERRORE_GOVWAY_PREFIX_CODE = "ERRORE_GOVWAY_PREFIX_CODE";
    public final static String ERRORE_GOVWAY_CODE = "ERRORE_GOVWAY_CODE";
    public final static String ERRORE_GOVWAY_DETAILS = "ERRORE_GOVWAY_DETAILS";
}
