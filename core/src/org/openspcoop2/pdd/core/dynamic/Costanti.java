/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
    
    public final static String MAP_HEADER = "header";
    public final static String TYPE_MAP_HEADER = java.util.Properties.class.getName();
    
    public final static String MAP_QUERY_PARAMETER = "query";
    public final static String TYPE_MAP_QUERY_PARAMETER = java.util.Properties.class.getName();
    
    public final static String MAP_BUSTA_PROPERTY = "property";
    public final static String TYPE_MAP_BUSTA_PROPERTY = java.util.Properties.class.getName();
    
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
    
    public final static String MAP_URL_PROTOCOL_CONTEXT_OBJECT = "transportContext";
    public final static String TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT = org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext.class.getName();
    
    public final static String MAP_ERROR_HANDLER_OBJECT = "errorHandler";
    public final static String TYPE_MAP_ERROR_HANDLER_OBJECT = org.openspcoop2.pdd.core.dynamic.ErrorHandler.class.getName();
	
    public final static String MAP_CLASS_LOAD_STATIC = "class";
    public final static String MAP_CLASS_NEW_INSTANCE = "new";
    
    public final static String MAP_SUFFIX_RESPONSE = "Response";
    
    public final static String ZIP_INDEX_ENTRY_FREEMARKER = "index.ftl";
    public final static String ZIP_INDEX_ENTRY_VELOCITY = "index.vm";
    
}
