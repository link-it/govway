/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.utils.xml;



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
public enum XPathReturnType {

	STRING (XPathConstants.STRING),
	NUMBER (XPathConstants.NUMBER),
	BOOLEAN (XPathConstants.BOOLEAN),
	NODE (XPathConstants.NODE),
	NODESET (XPathConstants.NODESET);

	private final QName valore;

	XPathReturnType(QName valore)
	{
		this.valore = valore;
	}

	public QName getValore()
	{
		return this.valore;
	}

	public static String[] toStringArray(){
		String[] res = new String[XPathReturnType.values().length];
		int i=0;
		for (XPathReturnType tmp : XPathReturnType.values()) {
			res[i]=tmp.getValore().getLocalPart();
			i++;
		}
		return res;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[XPathReturnType.values().length];
		int i=0;
		for (XPathReturnType tmp : XPathReturnType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore.toString();
	}
	public boolean equals(XPathReturnType esito){
		return this.toString().equals(esito.toString());
	}

}

