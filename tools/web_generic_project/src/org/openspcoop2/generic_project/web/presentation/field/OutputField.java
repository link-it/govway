/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.presentation.field;

import org.openspcoop2.generic_project.web.core.Utils;


/**
*
* OutputField Tipo generico che definisce i metadati di presentazione dei dati della pagina.
* name = nome del Field dell'oggetto da visualizzare;
* type = tipo di output da visualizzare (text,date,ecc..);
* value = valore del Field dell'oggetto da visualizzare;
* label = label del field da visualizzare.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public abstract class OutputField<T> {

	private String name;
	
	private String label;
	
	private T value;
	
	private String type;

	private boolean rendered;
	
	private boolean insideGroup;
	
	private boolean secret ;
	
	private String pattern;
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		try{
			String tmp = Utils.getMessageFromResourceBundle(this.label);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isInsideGroup() {
		return this.insideGroup;
	}

	public void setInsideGroup(boolean insideGroup) {
		this.insideGroup = insideGroup;
	}

	public boolean isRendered() {
		return this.rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public boolean isSecret() {
		return this.secret;
	}

	public void setSecret(boolean secret) {
		this.secret = secret;
	}

	public String getPattern() {
		return this.pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
	
}
