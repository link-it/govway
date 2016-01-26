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
package org.openspcoop2.generic_project.web.input;

import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;
import org.openspcoop2.generic_project.web.input.HtmlOption;

/**
* SelectItem Classe che serve come wrapper per gli oggetti complessi da utilizzare come valori nelle pagine
* e le label che verranno' visualizzate nelle pagine. 
*
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SelectItem implements HtmlOption{

	protected String value;
	protected String label;

	@Override
	public String getValue() {
		return this.value;
	}
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
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
		return this.getLabel() != null ? this.getLabel().toString() :  "" ;
	}
	
	   // This must return true for another SelectItem object with same key/id.
    @Override
	public boolean equals(Object other) {
        return other instanceof SelectItem && (this.value != null) ? this.value.equals(((SelectItem) other).value) : (other == this);
    }

    // This must return the same hashcode for every SelectItem object with the same key.
    @Override
	public int hashCode() {
        return this.value != null ? this.getClass().hashCode() + this.value.hashCode() : super.hashCode();
    }
	
	@Override
	public String getLabel() {
		try{
			String tmp = WebGenericProjectFactoryManager.getInstance().getDefaultFactory().getUtils().getMessageFromResourceBundle(this.label);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.label;
	}
}
