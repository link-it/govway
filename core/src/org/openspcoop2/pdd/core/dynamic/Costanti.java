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
	
	private Costanti() {}

    public static final String MAP_DATE_OBJECT = "date";
    public static final String TYPE_MAP_DATE_OBJECT = java.util.Date.class.getName();
    
    public static final String MAP_TRANSACTION_ID_OBJECT = "transactionId";
    public static final String MAP_TRANSACTION_ID_VALUE = "transaction:id";
    public static final String MAP_TRANSACTION_ID = "{"+MAP_TRANSACTION_ID_VALUE+"}";
    public static final String TYPE_MAP_TRANSACTION_ID = java.lang.String.class.getName();
    
    public static final String MAP_BUSTA_OBJECT = "busta";
    public static final String TYPE_MAP_BUSTA_OBJECT = org.openspcoop2.protocol.sdk.Busta.class.getName();
    
    public static final String MAP_CTX_OBJECT = "context";
    public static final String TYPE_MAP_CTX_OBJECT = "java.util.Map<String, Object>";
    public static final String TYPE_MAP_CTX_OBJECT_HTML_ESCAPED = "java.util.Map&amp;lt;String, Object&amp;gt;";
    
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
    public static final String MAP_ELEMENT_URL_REGEXP = "urlRegExp";
    public static final String MAP_ELEMENT_URL_REGEXP_PREFIX = "{"+MAP_ELEMENT_URL_REGEXP+":";
    public static final String TYPE_MAP_ELEMENT_URL_REGEXP = org.openspcoop2.pdd.core.dynamic.URLRegExpExtractor.class.getName();
    
    public static final String MAP_ELEMENT_XML_XPATH = "xPath";
    public static final String MAP_ELEMENT_XML_XPATH_PREFIX = "{"+MAP_ELEMENT_XML_XPATH+":";
    public static final String TYPE_MAP_ELEMENT_XML_XPATH = org.openspcoop2.pdd.core.dynamic.PatternExtractor.class.getName();
    
    public static final String MAP_ELEMENT_JSON_PATH = "jsonPath";
    public static final String MAP_ELEMENT_JSON_PATH_PREFIX = "{"+MAP_ELEMENT_JSON_PATH+":";
    public static final String TYPE_MAP_ELEMENT_JSON_PATH = org.openspcoop2.pdd.core.dynamic.PatternExtractor.class.getName();
    
    public static final String MAP_SYSTEM_PROPERTY = "system";
    public static final String MAP_SYSTEM_PROPERTY_PREFIX = "{"+MAP_SYSTEM_PROPERTY+":";
    private static String typeSystemProperty = org.openspcoop2.pdd.core.dynamic.SystemPropertiesReader.class.getName();
    public static String getTypeSystemProperty() {
		return typeSystemProperty;
	}
	static {
    	typeSystemProperty = org.openspcoop2.pdd.core.dynamic.PropertiesReader.class.getName(); // uniformo con lo stesso reader
    }
    
    public static final String MAP_ENV_PROPERTY = "env";
    public static final String MAP_ENV_PROPERTY_PREFIX = "{"+MAP_ENV_PROPERTY+":";
    private static String typeEnvProperty = org.openspcoop2.pdd.core.dynamic.EnvironmentPropertiesReader.class.getName();
    public static String getTypeEnvProperty() {
		return typeEnvProperty;
	}
	static {
    	typeEnvProperty = org.openspcoop2.pdd.core.dynamic.PropertiesReader.class.getName(); // uniformo con lo stesso reader
    }
    
    public static final String MAP_JAVA_PROPERTY = "java";
    public static final String MAP_JAVA_PROPERTY_PREFIX = "{"+MAP_JAVA_PROPERTY+":";
    private static String typeJavaProperty = org.openspcoop2.pdd.core.dynamic.JavaPropertiesReader.class.getName();
    public static String getTypeJavaProperty() {
		return typeJavaProperty;
	}
	static {
    	typeJavaProperty = org.openspcoop2.pdd.core.dynamic.PropertiesReader.class.getName(); // uniformo con lo stesso reader
    }
    
    public static final String MAP_REQUEST = "request";
    public static final String MAP_RESPONSE = "response";
    public static final String TYPE_MAP_MESSAGE_READER = org.openspcoop2.pdd.core.dynamic.ContentReader.class.getName();
    public static final String TYPE_MAP_MESSAGE_CONTENT = org.openspcoop2.pdd.core.dynamic.ContentExtractor.class.getName();
        
    public static final String MAP_URL_PROTOCOL_CONTEXT_OBJECT = "transportContext";
    public static final String TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT = org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext.class.getName();
    
    public static final String MAP_ATTACHMENTS_OBJECT = "attachments";
    public static final String TYPE_ATTACHMENTS_OBJECT = org.openspcoop2.pdd.core.dynamic.AttachmentsReader.class.getName();
    
    public static final String MAP_INTEGRATION = "integration";
    public static final String TYPE_MAP_INTEGRATION = org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazione.class.getName();
    
    public static final String MAP_TOKEN_INFO = "tokenInfo";
    public static final String TYPE_MAP_TOKEN_INFO = org.openspcoop2.pdd.core.token.InformazioniToken.class.getName();
    
    public static final String MAP_APPLICATIVO_TOKEN = "tokenClient";
    public static final String TYPE_MAP_APPLICATIVO_TOKEN = org.openspcoop2.core.id.IDServizioApplicativo.class.getName();
    
    public static final String MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "tokenClientApplicationConfig";
    public static final String TYPE_MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
    
    public static final String MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "tokenClientOrganizationConfig";
    public static final String TYPE_MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY = "java.util.Map<String, String>";
    public static final String TYPE_MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY_HTML_ESCAPED = "java.util.Map&amp;lt;String, String&amp;gt;";
        
    public static final String MAP_ATTRIBUTES = "aa"; // attributeAuthority, all'interno poi c'è il metodo attributes
    public static final String TYPE_MAP_ATTRIBUTES = org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi.class.getName();
    
    public static final String MAP_SECURITY_TOKEN = "securityToken";
    public static final String TYPE_MAP_SECURITY_TOKEN = org.openspcoop2.protocol.sdk.SecurityToken.class.getName();
    
    public static final String MAP_DYNAMIC_CONFIG_PROPERTY = "dynamicConfig";
    public static final String TYPE_MAP_DYNAMIC_CONFIG_PROPERTY = org.openspcoop2.pdd.core.dynamic.DynamicConfig.class.getName();
    
    public static final String MAP_ERROR_HANDLER_OBJECT = "errorHandler";
    public static final String TYPE_MAP_ERROR_HANDLER_OBJECT = org.openspcoop2.pdd.core.dynamic.ErrorHandler.class.getName();
	
    public static final String MAP_CLASS_LOAD_STATIC = "class";
    public static final String MAP_CLASS_NEW_INSTANCE = "new";
    
    public static final String MAP_SUFFIX_RESPONSE = "Response";
    
    public static final String ZIP_INDEX_ENTRY_FREEMARKER = "index.ftl";
    public static final String ZIP_INDEX_ENTRY_VELOCITY = "index.vm";
    
    public static final String COMPRESS_CONTENT = "content";
    public static final String COMPRESS_ENVELOPE = "soapEnvelope"; // soap
    public static final String COMPRESS_BODY = "soapBody"; // soap
    public static final String COMPRESS_ATTACH_PREFIX = "attachment[";
    public static final String COMPRESS_ATTACH_BY_ID_PREFIX = "attachmentId[";
    public static final String COMPRESS_SUFFIX = "]";
    
    // Aggiunto dentro la gestione dei token, classe: 'GestoreToken'
    public static final String MAP_REQUIRED_ATTRIBUTES = "requiredAttributes";
    public static final String TYPE_REQUIRED_ATTRIBUTES = org.openspcoop2.pdd.core.token.attribute_authority.RequiredAttributes.class.getName();
    
}
