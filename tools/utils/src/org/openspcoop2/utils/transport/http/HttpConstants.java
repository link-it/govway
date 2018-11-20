/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.transport.http;

import org.openspcoop2.utils.mime.MimeTypeConstants;

/**
 * HttpConstants
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpConstants {
	
	public static byte[] CR_LF_BREAK_LINE = { 13, 10 };
	public static String HTTP_HEADER_SEPARATOR = ": ";
	
	/** HTTP HEADERS */
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String CONTENT_LENGTH = "Content-Length";
	public final static String CONTENT_LOCATION = "Content-Location";
	public final static String CONTENT_ID = "Content-ID";
	public final static String RETURN_CODE = "ReturnCode";
	
    /** ContentType */
    public final static String CONTENT_TYPE_NON_VALORIZZATO = "Notv alued (null)";
    public final static String CONTENT_TYPE_NON_PRESENTE = "Undefined";
	
	/** Transfer Encoding */
	public final static String TRANSFER_ENCODING = "Transfer-Encoding";
	public final static String TRANSFER_ENCODING_VALUE_CHUNCKED = "chunked";
	
	/** Content Transfer Encoding */
	public final static String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public final static String CONTENT_TRANSFER_ENCODING_VALUE_BINARY = "binary";
	
	/** Redirect */
	public final static String REDIRECT_LOCATION = "Location";
	
	/** Proxy */
	public final static String PROXY_AUTHORIZATION = "Proxy-Authorization";
	
	/** Accept */
	public final static String ACCEPT = "Accept";
	
	/** Retry-After */
	public final static String RETRY_AFTER = "Retry-After";
	
	/** Authorization */
	public final static String AUTHORIZATION = "Authorization";
	public final static String AUTHORIZATION_PREFIX_BASIC = "Basic ";
	public final static String AUTHORIZATION_PREFIX_BEARER = "Bearer ";
	public final static String AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE = "WWW-Authenticate";
	public final static String AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX = "Basic realm=\"\"";
	public final static String AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX = "\"";
	
	/** Download */
	public final static String CONTENT_DISPOSITION = "Content-Disposition";
	public final static String CONTENT_DISPOSITION_ATTACH_FILE_PREFIX = "attachment; filename=";
	
	/** Cache */
	public final static String CACHE_STATUS_HTTP_1_1 = "Cache-Control";
	public final static String CACHE_STATUS_HTTP_1_1_DISABLE_CACHE = "no-cache, no-store, must-revalidate";
	public final static String CACHE_STATUS_HTTP_1_0 = "Pragma";
	public final static String CACHE_STATUS_HTTP_1_0_DISABLE_CACHE = "no-cache";
	public final static String CACHE_STATUS_PROXY_EXPIRES = "Expires";
	public final static long CACHE_STATUS_PROXY_EXPIRES_DISABLE_CACHE = 0;

	/** CORS Request */
	public final static String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	public final static String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public final static String ACCESS_CONTROL_REQUEST_ORIGIN = "Origin";
		
	/** CORS Response */
	public final static String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public final static String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public final static String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE = "*";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN_VARY = "Vary";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE = "Origin";
	public final static String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	public final static String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	public final static String ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE = "-1";

	/** Agent */
	public final static String USER_AGENT = "User-Agent";
	public final static String SERVER = "Server";
	public final static String X_POWERED_BY = "X-Powered-By";
	
	/** Content Type */
	public final static String CONTENT_TYPE_SOAP_1_1 = MimeTypeConstants.MEDIA_TYPE_SOAP_1_1;
	public final static String CONTENT_TYPE_SOAP_1_2 = MimeTypeConstants.MEDIA_TYPE_SOAP_1_2;
	public final static String CONTENT_TYPE_APPLICATION_OCTET_STREAM = MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM;
	public final static String CONTENT_TYPE_ZIP = MimeTypeConstants.MEDIA_TYPE_ZIP;
	public final static String CONTENT_TYPE_APPLICATION_XOP_XML = MimeTypeConstants.MEDIA_TYPE_APPLICATION_XOP_XML;
	public final static String CONTENT_TYPE_HTML = MimeTypeConstants.MEDIA_TYPE_HTML;
	public final static String CONTENT_TYPE_PLAIN = MimeTypeConstants.MEDIA_TYPE_PLAIN;
	public final static String CONTENT_TYPE_XML = MimeTypeConstants.MEDIA_TYPE_XML;
	public final static String CONTENT_TYPE_TEXT_XML = CONTENT_TYPE_SOAP_1_1;
	public final static String CONTENT_TYPE_JSON = MimeTypeConstants.MEDIA_TYPE_JSON;
	public final static String CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP = MimeTypeConstants.MEDIA_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
	public final static String CONTENT_TYPE_X_DOWNLOAD = MimeTypeConstants.MEDIA_TYPE_X_DOWNLOAD;
	public final static String CONTENT_TYPE_X_WWW_FORM_URLENCODED = MimeTypeConstants.MEDIA_TYPE_X_WWW_FORM_URLENCODED;
	public final static String CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807 = MimeTypeConstants.MEDIA_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
	public final static String CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807 = MimeTypeConstants.MEDIA_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
	
	/** Content Type, Parameter */
	public final static String CONTENT_TYPE_PARAMETER_CHARSET = "charset";
	
	/** Multipart Content Type */
	
	public final static String CONTENT_TYPE_MULTIPART_TYPE = "multipart";
	public final static String CONTENT_TYPE_MULTIPART_ALTERNATIVE_SUBTYPE = "alternative";
	public final static String CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE = "mixed";
	public final static String CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE = "related";
	
	public final static String CONTENT_TYPE_MULTIPART_ALTERNATIVE = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_ALTERNATIVE_SUBTYPE;
	public final static String CONTENT_TYPE_MULTIPART_MIXED = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE;
	public final static String CONTENT_TYPE_MULTIPART_RELATED = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE;
	
	public final static String CONTENT_TYPE_MULTIPART = CONTENT_TYPE_MULTIPART_RELATED;
	public final static String CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY = "boundary";
	public final static String CONTENT_TYPE_MULTIPART_PARAMETER_TYPE = "type";
	public final static String CONTENT_TYPE_MULTIPART_PARAMETER_START = "start";
	public final static String CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO = "start-info";
	
	/** Source */
    public final static String SEPARATOR_SOURCE = ":";
	
}

