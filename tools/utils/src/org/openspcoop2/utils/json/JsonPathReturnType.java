/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.json;



import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;


/**
 * XPathReturnType
 *
 *
 * @author Poli Andrea
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum JsonPathReturnType {

	STRING (XPathConstants.STRING),
	NUMBER (XPathConstants.NUMBER),
	BOOLEAN (XPathConstants.BOOLEAN),
	NODE (XPathConstants.NODE),
	JSON_PATH_OBJECT(new QName("net.minidev.json.JSONObject"));
//	NODESET (XPathConstants.NODESET);

	private final QName valore;

	JsonPathReturnType(QName valore)
	{
		this.valore = valore;
	}

	public QName getValore()
	{
		return this.valore;
	}

	public static String[] toStringArray(){
		String[] res = new String[JsonPathReturnType.values().length];
		int i=0;
		for (JsonPathReturnType tmp : JsonPathReturnType.values()) {
			res[i]=tmp.getValore().getLocalPart();
			i++;
		}
		return res;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[JsonPathReturnType.values().length];
		int i=0;
		for (JsonPathReturnType tmp : JsonPathReturnType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore.toString();
	}
	public boolean equals(JsonPathReturnType esito){
		return this.toString().equals(esito.toString());
	}

}

