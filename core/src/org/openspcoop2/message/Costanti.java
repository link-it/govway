/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

/**
 * Costanti
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String CONTENT_LENGTH = "Content-Length";
	public final static String CONTENT_LOCATION = "Content-Location";
	public final static String CONTENT_ID = "Content-ID";
	
	public final static String SOAP_ACTION = "SOAPAction";
	
	public final static String CONTENT_TYPE_SOAP_1_1 = "text/xml";
	public final static String CONTENT_TYPE_SOAP_1_2 = "application/soap+xml";
	public final static String CONTENT_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
	public final static String CONTENT_TYPE_ZIP = "application/zip";
	public final static String CONTENT_TYPE_APPLICATION_XOP_XML = "application/xop+xml";
	
	public final static String CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP = "application/openspcoop2";
	
	public final static String CONTENT_TYPE_MULTIPART = "multipart/related";
	public final static String CONTENT_TYPE_MULTIPART_BOUNDARY = "boundary";
	public final static String CONTENT_TYPE_MULTIPART_TYPE = "type";
	public final static String CONTENT_TYPE_MULTIPART_START = "start";
	public final static String CONTENT_TYPE_MULTIPART_START_INFO = "start-info";
	
	/** SOAP Envelope namespace */
	public static final String SOAP_ENVELOPE_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String SOAP12_ENVELOPE_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
	
	public static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";
	public static final String XMLNS_LOCAL_NAME = "xmlns";
	
	/** Tunnel */
	public static final String SOAP_TUNNEL_NAMESPACE = "http://www.openspcoop2.org/pdd/services/PDtoSOAP";
	public static final String SOAP_TUNNEL_ATTACHMENT_ELEMENT_OPENSPCOOP2_TYPE = "SOAPTunnel";
	public static final String SOAP_TUNNEL_ATTACHMENT_ELEMENT = "Attachments";
	
}
