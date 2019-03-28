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
    public final static String MAP_TRANSACTION_OBJECT = "transaction";
    public final static String MAP_TRANSACTION_ID = "{transaction:id}";
    public final static String MAP_BUSTA_OBJECT = "busta";
    public final static String MAP_CTX_OBJECT = "context";
    public final static String MAP_HEADER = "header";
    public final static String MAP_QUERY_PARAMETER = "query";
    public final static String MAP_BUSTA_PROPERTY = "property";
    // Per ora messi solamente nelle trasformazioni, valutare se poi metterli anche nel connettore
    public final static String MAP_ELEMENT_URL_REGEXP = "urlRegExp";
    public final static String MAP_ELEMENT_URL_REGEXP_PREFIX = "{"+MAP_ELEMENT_URL_REGEXP+":";
    public final static String MAP_ELEMENT_XML_XPATH = "xPath";
    public final static String MAP_ELEMENT_XML_XPATH_PREFIX = "{"+MAP_ELEMENT_XML_XPATH+":";
    public final static String MAP_ELEMENT_JSON_PATH = "jsonPath";
    public final static String MAP_ELEMENT_JSON_PATH_PREFIX = "{"+MAP_ELEMENT_JSON_PATH+":";
	
}
