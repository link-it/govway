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

package org.openspcoop2.message.soap.mtom;


/**
 * Costanti
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	public final static String OPENSPCOOP2_MTOM_NAMESPACE = "http://www.openspcoop2.org/core/mtom";
	
	public final static String XOP_INCLUDE_NAMESPACE = "http://www.w3.org/2004/08/xop/include";
	public final static String XOP_INCLUDE_LOCAL_NAME = "Include";
	public final static String XOP_INCLUDE_ATTRIBUTE_HREF ="href";
	public final static String XOP_INCLUDE_ATTRIBUTE_HREF_CID_PREFIX_VALUE ="cid:";
	public final static String MTOM_XOP_REFERENCES = "//{"+Costanti.XOP_INCLUDE_NAMESPACE+"}:"+Costanti.XOP_INCLUDE_LOCAL_NAME;
	
	public final static String XMIME_NAMESPACE = "http://www.w3.org/2005/05/xmlmime";
	public final static String XMIME_ATTRIBUTE_CONTENT_TYPE = "contentType";
	
}
