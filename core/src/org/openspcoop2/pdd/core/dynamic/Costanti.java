/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

/**
 * Costanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

    public final static String MAP_DATE_OBJECT = "date";
    public final static String TYPE_MAP_DATE_OBJECT = java.util.Date.class.getName();
    
    public final static String MAP_TRANSACTION_ID_OBJECT = "transactionId";
    public final static String MAP_TRANSACTION_ID_VALUE = "transaction:id";
    public final static String MAP_TRANSACTION_ID = "{"+MAP_TRANSACTION_ID_VALUE+"}";
    public final static String TYPE_MAP_TRANSACTION_ID = java.lang.String.class.getName();
    
    public final static String MAP_BUSTA_OBJECT = "busta";
    public final static String TYPE_MAP_BUSTA_OBJECT = org.openspcoop2.protocol.sdk.Busta.class.getName();
    
    public final static String MAP_CTX_OBJECT = "context";
    public final static String TYPE_MAP_CTX_OBJECT = "java.util.Map<String, Object>";
    public final static String TYPE_MAP_CTX_OBJECT_HTML_ESCAPED = "java.util.Map&amp;lt;String, Object&amp;gt;";
    
    public static final String MAP_HEADER = "header";
    public static final String TYPE_MAP_HEADER = "java.util.Map<String, String>";
    public static final String TYPE_MAP_HEADER_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_HEADER_VALUES = "headerValues";
    public static final String TYPE_MAP_HEADER_VALUES = "java.util.Map<String, List<String>>";
    public static final String TYPE_MAP_HEADER_VALUES_HTML_ESCAPED = "java.util.Map&amp;lt;String, List&amp;lt;String&amp;gt;&amp;gt;";
    
    public static final String MAP_HEADER_RESPONSE_VALUES = "headerResponseValues";
    
    public static final String MAP_QUERY_PARAMETER = "query";
    public static final String TYPE_MAP_QUERY_PARAMETER = "java.util.Map<String, String>";
    public static final String TYPE_MAP_QUERY_PARAMETER_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_QUERY_PARAMETER_VALUES = "queryValues";
    public static final String TYPE_MAP_QUERY_PARAMETER_VALUES = "java.util.Map<String, List<String>>";
    public static final String TYPE_MAP_QUERY_PARAMETER_VALUES_HTML_ESCAPED = "java.util.Map&amp;lt;String, List&amp;lt;String&amp;gt;&amp;gt;";
    
    public static final String MAP_FORM_PARAMETER = "form";
    public static final String TYPE_MAP_FORM_PARAMETER = "java.util.Map<String, String>";
    public static final String TYPE_MAP_FORM_PARAMETER_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_FORM_PARAMETER_VALUES = "formValues";
    public static final String TYPE_MAP_FORM_PARAMETER_VALUES = "java.util.Map<String, List<String>>";
    public static final String TYPE_MAP_FORM_PARAMETER_VALUES_HTML_ESCAPED = "java.util.Map&amp;lt;String, List&amp;lt;String&amp;gt;&amp;gt;";
    
    public static final String MAP_BUSTA_PROPERTY = "property";
    public static final String TYPE_MAP_BUSTA_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_BUSTA_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_API_IMPL_CONFIG_PROPERTY = "config";
    public static final String TYPE_MAP_API_IMPL_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_API_IMPL_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_APPLICATIVO_CONFIG_PROPERTY = "clientApplicationConfig";
    public static final String TYPE_MAP_APPLICATIVO_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_APPLICATIVO_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY = "clientOrganizationConfig";
    public static final String TYPE_MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY = "providerOrganizationConfig";
    public static final String TYPE_MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    // Per ora messi solamente nelle trasformazioni, valutare se poi metterli anche nel connettore
    public final static String MAP_ELEMENT_URL_REGEXP = "urlRegExp";
    public final static String MAP_ELEMENT_URL_REGEXP_PREFIX = "{"+MAP_ELEMENT_URL_REGEXP+":";
    public final static String TYPE_MAP_ELEMENT_URL_REGEXP = org.openspcoop2.pdd.core.dynamic.URLRegExpExtractor.class.getName();
    
    public final static String MAP_ELEMENT_XML_XPATH = "xPath";
    public final static String MAP_ELEMENT_XML_XPATH_PREFIX = "{"+MAP_ELEMENT_XML_XPATH+":";
    public final static String TYPE_MAP_ELEMENT_XML_XPATH = org.openspcoop2.pdd.core.dynamic.PatternExtractor.class.getName();
    
    public final static String MAP_ELEMENT_JSON_PATH = "jsonPath";
    public final static String MAP_ELEMENT_JSON_PATH_PREFIX = "{"+MAP_ELEMENT_JSON_PATH+":";
    public final static String TYPE_MAP_ELEMENT_JSON_PATH = org.openspcoop2.pdd.core.dynamic.PatternExtractor.class.getName();
    
    public final static String MAP_SYSTEM_PROPERTY = "system";
    public final static String MAP_SYSTEM_PROPERTY_PREFIX = "{"+MAP_SYSTEM_PROPERTY+":";
    public static String TYPE_SYSTEM_PROPERTY = org.openspcoop2.pdd.core.dynamic.SystemPropertiesReader.class.getName();
    static {
    	TYPE_SYSTEM_PROPERTY = org.openspcoop2.pdd.core.dynamic.PropertiesReader.class.getName(); // uniformo con lo stesso reader
    }
    
    public final static String MAP_ENV_PROPERTY = "env";
    public final static String MAP_ENV_PROPERTY_PREFIX = "{"+MAP_ENV_PROPERTY+":";
    public static String TYPE_ENV_PROPERTY = org.openspcoop2.pdd.core.dynamic.EnvironmentPropertiesReader.class.getName();
    static {
    	TYPE_ENV_PROPERTY = org.openspcoop2.pdd.core.dynamic.PropertiesReader.class.getName(); // uniformo con lo stesso reader
    }
    
    public final static String MAP_JAVA_PROPERTY = "java";
    public final static String MAP_JAVA_PROPERTY_PREFIX = "{"+MAP_JAVA_PROPERTY+":";
    public static String TYPE_JAVA_PROPERTY = org.openspcoop2.pdd.core.dynamic.JavaPropertiesReader.class.getName();
    static {
    	TYPE_JAVA_PROPERTY = org.openspcoop2.pdd.core.dynamic.PropertiesReader.class.getName(); // uniformo con lo stesso reader
    }
    
    public final static String MAP_REQUEST = "request";
    public final static String MAP_RESPONSE = "response";
    public final static String TYPE_MAP_MESSAGE_READER = org.openspcoop2.pdd.core.dynamic.ContentReader.class.getName();
    public final static String TYPE_MAP_MESSAGE_CONTENT = org.openspcoop2.pdd.core.dynamic.ContentExtractor.class.getName();
        
    public final static String MAP_URL_PROTOCOL_CONTEXT_OBJECT = "transportContext";
    public final static String TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT = org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext.class.getName();
    
    public final static String MAP_ATTACHMENTS_OBJECT = "attachments";
    public final static String TYPE_ATTACHMENTS_OBJECT = org.openspcoop2.pdd.core.dynamic.AttachmentsReader.class.getName();
    
    public final static String MAP_INTEGRATION = "integration";
    public final static String TYPE_MAP_INTEGRATION = org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazione.class.getName();
    
    public final static String MAP_TOKEN_INFO = "tokenInfo";
    public final static String TYPE_MAP_TOKEN_INFO = org.openspcoop2.pdd.core.token.InformazioniToken.class.getName();
    
    public final static String MAP_APPLICATIVO_TOKEN = "tokenClient";
    public final static String TYPE_MAP_APPLICATIVO_TOKEN = org.openspcoop2.core.id.IDServizioApplicativo.class.getName();
    
    public static final String MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "tokenClientApplicationConfig";
    public static final String TYPE_MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "tokenClientOrganizationConfig";
    public static final String TYPE_MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
        
    public final static String MAP_ATTRIBUTES = "aa"; // attributeAuthority, all'interno poi c'è il metodo attributes
    public final static String TYPE_MAP_ATTRIBUTES = org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi.class.getName();
    
    public final static String MAP_SECURITY_TOKEN = "securityToken";
    public final static String TYPE_MAP_SECURITY_TOKEN = org.openspcoop2.protocol.sdk.SecurityToken.class.getName();
    
    public final static String MAP_DYNAMIC_CONFIG_PROPERTY = "dynamicConfig";
    public final static String TYPE_MAP_DYNAMIC_CONFIG_PROPERTY = org.openspcoop2.pdd.core.dynamic.DynamicConfig.class.getName();
    
    public final static String MAP_ERROR_HANDLER_OBJECT = "errorHandler";
    public final static String TYPE_MAP_ERROR_HANDLER_OBJECT = org.openspcoop2.pdd.core.dynamic.ErrorHandler.class.getName();
	
    public final static String MAP_CLASS_LOAD_STATIC = "class";
    public final static String MAP_CLASS_NEW_INSTANCE = "new";
    
    public final static String MAP_SUFFIX_RESPONSE = "Response";
    
    public final static String ZIP_INDEX_ENTRY_FREEMARKER = "index.ftl";
    public final static String ZIP_INDEX_ENTRY_VELOCITY = "index.vm";
    
    public final static String COMPRESS_CONTENT = "content";
    public final static String COMPRESS_ENVELOPE = "soapEnvelope"; // soap
    public final static String COMPRESS_BODY = "soapBody"; // soap
    public final static String COMPRESS_ATTACH_PREFIX = "attachment[";
    public final static String COMPRESS_ATTACH_BY_ID_PREFIX = "attachmentId[";
    public final static String COMPRESS_SUFFIX = "]";
    
    // Aggiunto dentro la gestione dei token, classe: 'GestoreToken'
    public final static String MAP_REQUIRED_ATTRIBUTES = "requiredAttributes";
    public final static String TYPE_REQUIRED_ATTRIBUTES = org.openspcoop2.pdd.core.token.attribute_authority.RequiredAttributes.class.getName();
    
}
