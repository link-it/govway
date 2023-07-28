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
package org.openspcoop2.utils.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Char2String
 *
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Char2String extends XmlAdapter<String, Character> {


    @Override
	public Character unmarshal(String valueParam) {
    	if(valueParam==null){
    		return null;
    	}
    	String value = valueParam.trim();
    	return Character.valueOf(value.charAt(0));
    }

    @Override
	public String marshal(Character value) {
    	if (value == null) {
        	return null;
        }
        char charValue = value.charValue();
    	if(charValue==0){ // == ''
    		// devo ritornare cmq un carattere, 
    		// se torno il carattere speciale '\u0000' ottengo un errore in fase di  Unmarshalling Error: Illegal character (NULL, unicode 0) encountered: not valid in any content
    		// se torno null, ottengo un errore di validazione xsd, essendo atteso un carattere (length=1)
    		return " "; 
    	}
    	return charValue+"";
    }

}

