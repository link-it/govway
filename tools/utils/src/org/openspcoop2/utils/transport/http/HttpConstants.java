/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public final static String CONTENT_TYPE_NON_VALORIZZATO = "Not valued (null)";
    public final static String CONTENT_TYPE_NON_PRESENTE = "Undefined";
	
	/** Transfer Encoding */
	public final static String TRANSFER_ENCODING = "Transfer-Encoding";
	public final static String TRANSFER_ENCODING_VALUE_CHUNCKED = "chunked";
	
	/** Content Transfer Encoding */
	public final static String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public final static String CONTENT_TRANSFER_ENCODING_VALUE_BINARY = "binary";
	
	/** Content Encoding */
	public final static String CONTENT_ENCODING = "Content-Encoding";
	
	/** Redirect */
	public final static String REDIRECT_LOCATION = "Location";
	
	/** Proxy */
	public final static String PROXY_AUTHORIZATION = "Proxy-Authorization";
	
	/** Accept */
	public final static String ACCEPT = "Accept";
	
	/** Retry-After */
	public final static String RETRY_AFTER = "Retry-After";
	
	/** Cookie */
	public final static String COOKIE = "Cookie";
	public final static String COOKIE_SEPARATOR = ";";
	public final static String COOKIE_NAME_VALUE_SEPARATOR = "=";
	
	/** Digest */
	public final static String DIGEST = "Digest";
	public final static String DIGEST_ALGO_MD5 = "MD5";
	public final static String DIGEST_ALGO_SHA_1 = "SHA";
	public final static String DIGEST_ALGO_SHA_256 = "SHA-256";
	public final static String DIGEST_ALGO_SHA_384 = "SHA-384";
	public final static String DIGEST_ALGO_SHA_512 = "SHA-512";
	
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
	public final static String CACHE_STATUS_DIRECTIVE_NO_CACHE = "no-cache";
	public final static String CACHE_STATUS_DIRECTIVE_NO_STORE = "no-store";
	public final static String CACHE_STATUS_DIRECTIVE_MAX_AGE = "max-age";
	public final static String CACHE_STATUS_DIRECTIVE_MUST_REVALIDATE = "must-revalidate";
	public final static String CACHE_STATUS_HTTP_1_1 = "Cache-Control";
	public final static String CACHE_STATUS_HTTP_1_1_DISABLE_CACHE = CACHE_STATUS_DIRECTIVE_NO_CACHE+", "+CACHE_STATUS_DIRECTIVE_NO_STORE+", "+CACHE_STATUS_DIRECTIVE_MUST_REVALIDATE;
	public final static String CACHE_STATUS_HTTP_1_0 = "Pragma";
	public final static String CACHE_STATUS_HTTP_1_0_DISABLE_CACHE = CACHE_STATUS_DIRECTIVE_NO_CACHE;
	public final static String CACHE_STATUS_PROXY_EXPIRES = "Expires";
	public final static long CACHE_STATUS_PROXY_EXPIRES_DISABLE_CACHE = 0;

	/** CORS Request */
	public final static String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	public final static String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public final static String ACCESS_CONTROL_REQUEST_ORIGIN = "Origin";
		
	/** CORS Response */
	public final static String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public final static String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public final static String ALLOW_HEADERS = "Allow";
	public final static String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE = "*";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN_VARY = "Vary";
	public final static String ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE = "Origin";
	public final static String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	public final static String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	public final static String ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE = "-1";
    public final static List<String> ACCESS_CONTROL_SIMPLE_REQUEST_CONTENT_TYPES = new ArrayList<String>(Arrays.asList(
    		HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED, 
    		HttpConstants.CONTENT_TYPE_FORM_DATA,
    		HttpConstants.CONTENT_TYPE_PLAIN));

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
	public final static String CONTENT_TYPE_FORM_DATA = MimeTypeConstants.MEDIA_TYPE_FORM_DATA;
	public final static String CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807 = MimeTypeConstants.MEDIA_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
	public final static String CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807 = MimeTypeConstants.MEDIA_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
	
	/** SOAP */
	public static final String SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION = "SOAPAction";
	public static final String SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION = "action";
	
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

