/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.mime;

/**
 * MimeTypeConstants
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MimeTypeConstants {
	
	private MimeTypeConstants() {}
	
	/** Content Type */
	public static final String MEDIA_TYPE_SOAP_1_1 = "text/xml";
	public static final String MEDIA_TYPE_SOAP_1_2 = "application/soap+xml";
	public static final String MEDIA_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String MEDIA_TYPE_ZIP = "application/zip";
	public static final String MEDIA_TYPE_PDF = "application/pdf";
	public static final String MEDIA_TYPE_APPLICATION_XOP_XML = "application/xop+xml";
	public static final String MEDIA_TYPE_HTML = "text/html";
	public static final String MEDIA_TYPE_PLAIN = "text/plain";	
	public static final String MEDIA_TYPE_XML = "application/xml";
	public static final String MEDIA_TYPE_JSON = "application/json";
	public static final String MEDIA_TYPE_OPENSPCOOP2_TUNNEL_SOAP = "application/openspcoop2";
	public static final String MEDIA_TYPE_X_DOWNLOAD = "application/x-download";
	public static final String MEDIA_TYPE_X_WWW_FORM_URLENCODED= "application/x-www-form-urlencoded";
	public static final String MEDIA_TYPE_FORM_DATA= "multipart/form-data";
	public static final String MEDIA_TYPE_JSON_PROBLEM_DETAILS_RFC_7807 = "application/problem+json";
	public static final String MEDIA_TYPE_XML_PROBLEM_DETAILS_RFC_7807 = "application/problem+xml";
	public static final String MEDIA_TYPE_OCSP_REQUEST = "application/ocsp-request";
	public static final String MEDIA_TYPE_OCSP_RESPONSE = "application/ocsp-response";
}

