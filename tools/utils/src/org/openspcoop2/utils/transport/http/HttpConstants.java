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
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_LOCATION = "Content-Location";
	public static final String CONTENT_ID = "Content-ID";
	public static final String RETURN_CODE = "ReturnCode";
	
    /** ContentType */
    public static final String CONTENT_TYPE_NON_VALORIZZATO = "Not valued (null)";
    public static final String CONTENT_TYPE_NON_PRESENTE = "Undefined";
	
	/** Transfer Encoding */
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	public static final String TRANSFER_ENCODING_VALUE_CHUNCKED = "chunked";
	
	/** Content Transfer Encoding */
	public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public static final String CONTENT_TRANSFER_ENCODING_VALUE_BINARY = "binary";
	
	/** Content Encoding */
	public static final String CONTENT_ENCODING = "Content-Encoding";
	
	/** Redirect */
	public static final String REDIRECT_LOCATION = "Location";
	
	/** Proxy */
	public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
	
	/** Accept */
	public static final String ACCEPT = "Accept";
	
	/** Connection */
	public static final String CONNECTION = "Connection";
	public static final String CONNECTION_VALUE_KEEP_ALIVE = "keep-alive";
	public static final String CONNECTION_VALUE_CLOSE = "close";
	
	/** Retry-After */
	public static final String RETRY_AFTER = "Retry-After";
	
	/** Cookie */
	public static final String COOKIE = "Cookie";
	public static final String COOKIE_SEPARATOR = ";";
	public static final String COOKIE_NAME_VALUE_SEPARATOR = "=";
	
	/** SetCookie */
	public static final String SET_COOKIE = "Set-Cookie";
	public static final String SET_COOKIE_EXPIRES_PARAMETER = "Expires"; // <date>
	public static final String SET_COOKIE_MAX_AGE_PARAMETER = "Max-Age"; // <non-zero-digit>
	public static final String SET_COOKIE_PATH_PARAMETER = "Path"; // <path-value>
	public static final String SET_COOKIE_DOMAIN_PARAMETER = "Domain"; // <domain-value>
	public static final String SET_COOKIE_SECURE_PARAMETER = "Secure";
	public static final String SET_COOKIE_HTTP_ONLY_PARAMETER = "HttpOnly";
	public static final String SET_COOKIE_SAME_SITE_PARAMETER = "SameSite";
	
	/** Digest */
	public static final String DIGEST = "Digest";
	public static final String DIGEST_ALGO_MD5 = "MD5";
	public static final String DIGEST_ALGO_SHA = "SHA";
	public static final String DIGEST_ALGO_SHA_1 = "SHA-1";
	public static final String DIGEST_ALGO_SHA_256 = "SHA-256";
	public static final String DIGEST_ALGO_SHA_384 = "SHA-384";
	public static final String DIGEST_ALGO_SHA_512 = "SHA-512";
	
	/** Authentication */
	public static final String AUTHENTICATION_BASIC = "Basic";
	public static final String AUTHENTICATION_BEARER = "Bearer";
	
	/** Authorization */
	public static final String AUTHORIZATION = "Authorization";
	public static final String AUTHORIZATION_PREFIX_BASIC = AUTHENTICATION_BASIC+" ";
	public static final String AUTHORIZATION_PREFIX_BEARER = AUTHENTICATION_BEARER+" ";
	public static final String AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE = "WWW-Authenticate";
	public static final String AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX = "Basic realm=\"\"";
	public static final String AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX = "\"";
	
	/** Download */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String CONTENT_DISPOSITION_ATTACH_FILE_PREFIX = "attachment; filename=";
	public static final String CONTENT_DISPOSITION_NAME_PREFIX = "name=";
	public static final String CONTENT_DISPOSITION_FILENAME_PREFIX = "filename=";
	
	/** Cache */
	public static final String CACHE_STATUS_DIRECTIVE_NO_CACHE = "no-cache";
	public static final String CACHE_STATUS_DIRECTIVE_NO_STORE = "no-store";
	public static final String CACHE_STATUS_DIRECTIVE_MAX_AGE = "max-age";
	public static final String CACHE_STATUS_DIRECTIVE_MUST_REVALIDATE = "must-revalidate";
	public static final String CACHE_STATUS_HTTP_1_1 = "Cache-Control";
	public static final String CACHE_STATUS_HTTP_1_1_DISABLE_CACHE = CACHE_STATUS_DIRECTIVE_NO_CACHE+", "+CACHE_STATUS_DIRECTIVE_NO_STORE+", "+CACHE_STATUS_DIRECTIVE_MUST_REVALIDATE;
	public static final String CACHE_STATUS_HTTP_1_0 = "Pragma";
	public static final String CACHE_STATUS_HTTP_1_0_DISABLE_CACHE = CACHE_STATUS_DIRECTIVE_NO_CACHE;
	public static final String CACHE_STATUS_PROXY_EXPIRES = "Expires";
	public static final long CACHE_STATUS_PROXY_EXPIRES_DISABLE_CACHE = 0;
	public static final String CACHE_STATUS_VARY = "Vary";
	public static final String CACHE_STATUS_VARY_UNCACHABLE = "*"; // Indicates that factors other than request headers influenced the generation of this response. Implies that the response is uncacheable.
	public static final List<String> CACHE_STATUS_HEADERS = new ArrayList<>();
	static {
		CACHE_STATUS_HEADERS.add(CACHE_STATUS_HTTP_1_1);
		CACHE_STATUS_HEADERS.add(CACHE_STATUS_HTTP_1_0);
		CACHE_STATUS_HEADERS.add(CACHE_STATUS_PROXY_EXPIRES);
		CACHE_STATUS_HEADERS.add(CACHE_STATUS_VARY);
	}
	public static boolean isCacheStatusHeader(String hdr) {
		if(hdr==null) {
			return false;
		}
		for (String h : CACHE_STATUS_HEADERS) {
			if(h.toLowerCase().equals(hdr.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	/** CORS Request */
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public static final String ACCESS_CONTROL_REQUEST_ORIGIN = "Origin";
		
	/** CORS Response */
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ALLOW_HEADERS = "Allow";
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE = "*";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN_VARY = "Vary";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN_VARY_ORIGIN_VALUE = "Origin";
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
	public static final String ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE = "-1";
    public static final List<String> ACCESS_CONTROL_SIMPLE_REQUEST_CONTENT_TYPES = new ArrayList<>(Arrays.asList(
    		HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED, 
    		HttpConstants.CONTENT_TYPE_FORM_DATA,
    		HttpConstants.CONTENT_TYPE_PLAIN));

	/** Agent */
	public static final String USER_AGENT = "User-Agent";
	public static final String SERVER = "Server";
	public static final String X_POWERED_BY = "X-Powered-By";
	
	/** Content Type */
	public static final String CONTENT_TYPE_SOAP_1_1 = MimeTypeConstants.MEDIA_TYPE_SOAP_1_1;
	public static final String CONTENT_TYPE_SOAP_1_2 = MimeTypeConstants.MEDIA_TYPE_SOAP_1_2;
	public static final String CONTENT_TYPE_APPLICATION_OCTET_STREAM = MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM;
	public static final String CONTENT_TYPE_ZIP = MimeTypeConstants.MEDIA_TYPE_ZIP;
	public static final String CONTENT_TYPE_PDF = MimeTypeConstants.MEDIA_TYPE_PDF;
	public static final String CONTENT_TYPE_APPLICATION_XOP_XML = MimeTypeConstants.MEDIA_TYPE_APPLICATION_XOP_XML;
	public static final String CONTENT_TYPE_HTML = MimeTypeConstants.MEDIA_TYPE_HTML;
	public static final String CONTENT_TYPE_PLAIN = MimeTypeConstants.MEDIA_TYPE_PLAIN;
	public static final String CONTENT_TYPE_XML = MimeTypeConstants.MEDIA_TYPE_XML;
	public static final String CONTENT_TYPE_TEXT_XML = CONTENT_TYPE_SOAP_1_1;
	public static final String CONTENT_TYPE_JSON = MimeTypeConstants.MEDIA_TYPE_JSON;
	public static final String CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP = MimeTypeConstants.MEDIA_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
	public static final String CONTENT_TYPE_X_DOWNLOAD = MimeTypeConstants.MEDIA_TYPE_X_DOWNLOAD;
	public static final String CONTENT_TYPE_X_WWW_FORM_URLENCODED = MimeTypeConstants.MEDIA_TYPE_X_WWW_FORM_URLENCODED;
	public static final String CONTENT_TYPE_FORM_DATA = MimeTypeConstants.MEDIA_TYPE_FORM_DATA;
	public static final String CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807 = MimeTypeConstants.MEDIA_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
	public static final String CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807 = MimeTypeConstants.MEDIA_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
	public static final String CONTENT_TYPE_OCSP_REQUEST = MimeTypeConstants.MEDIA_TYPE_OCSP_REQUEST;
	public static final String CONTENT_TYPE_OCSP_RESPONSE = MimeTypeConstants.MEDIA_TYPE_OCSP_RESPONSE;
	
	/** SOAP */
	public static final String SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION = "SOAPAction";
	public static final String SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION = "action";
	
	/** Content Type, Parameter */
	public static final String CONTENT_TYPE_PARAMETER_CHARSET = "charset";
	
	/** Multipart Content Type */
	
	public static final String CONTENT_TYPE_MULTIPART_TYPE = "multipart";
	public static final String CONTENT_TYPE_MULTIPART_ALTERNATIVE_SUBTYPE = "alternative";
	public static final String CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE = "mixed";
	public static final String CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE = "related";
	public static final String CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE = "form-data";
	
	public static final String CONTENT_TYPE_MULTIPART_ALTERNATIVE = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_ALTERNATIVE_SUBTYPE;
	public static final String CONTENT_TYPE_MULTIPART_MIXED = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE;
	public static final String CONTENT_TYPE_MULTIPART_RELATED = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE;
	public static final String CONTENT_TYPE_MULTIPART_FORM_DATA = CONTENT_TYPE_MULTIPART_TYPE+"/"+CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE;
	
	@Deprecated
	public static final String CONTENT_TYPE_MULTIPART = CONTENT_TYPE_MULTIPART_RELATED;
	
	public static final String CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY = "boundary";
	public static final String CONTENT_TYPE_MULTIPART_PARAMETER_TYPE = "type";
	public static final String CONTENT_TYPE_MULTIPART_PARAMETER_START = "start";
	public static final String CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO = "start-info";
	public static final String CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA = "form-data";
	
	/** Source */
    public static final String SEPARATOR_SOURCE = ":";
    
    /** Content Security Policy */
	public static final String HEADER_NAME_CONTENT_SECURITY_POLICY = "Content-Security-Policy";
	public static final String HEADER_NAME_CONTENT_SECURITY_POLICY_REPORT_ONLY = "Content-Security-Policy-Report-Only";
	
	/** XSS Protection*/
	public static final String HEADER_NAME_X_FRAME_OPTIONS = "X-Frame-Options";
	public static final String HEADER_NAME_X_XSS_PROTECTION = "X-XSS-Protection";
	public static final String HEADER_NAME_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
}

