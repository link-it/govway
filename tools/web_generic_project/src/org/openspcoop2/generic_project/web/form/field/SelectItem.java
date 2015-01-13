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
package org.openspcoop2.generic_project.web.form.field;

import org.openspcoop2.generic_project.web.core.Utils;
import org.richfaces.taglib.ComboBoxTag;

/**
* SelectItem Classe che serve come wrapper per gli oggetti complessi da utilizzare come valori nelle pagine
* e le label che verranno' visualizzate nelle pagine. Per esempio nel componente {@link ComboBoxTag}. 
*
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SelectItem extends Object{
	private String value;
	private String label;
	
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
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
	
	public SelectItem(String value, String label){
		this.setLabel(label);
		this.setValue(value);
	}
	
	public SelectItem(){
		this.value = null;
		this.label = null;
	}

	@Override
	public String toString() {
		return this.label;
	}
}
