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

package org.openspcoop2.message.soap.reference;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Classe che rappresenta un elemento 
 * 
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class Reference {
	
    public static final String REFERENCE_ATTACH = "Attach";
    public static final String REFERENCE_CONTENT = "Content";
    public static final String REFERENCE_ELEMENT = "Element";
	
	protected String reference;
	protected int type;
	

	public Reference(int type, String reference) {
		// Le reference possono essere escaped in xml se contengono caratteri speciali
		this.reference = StringEscapeUtils.unescapeXml(reference);
		this.type = type;
	}
	
	public String getReference(){
		return this.reference;
	}
	
	public int getType(){
		return this.type;
	}
}
