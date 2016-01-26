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
package org.openspcoop2.generic_project.web.impl.jsf1.input;

import java.util.List;

import javax.faces.model.SelectItem;

import org.openspcoop2.generic_project.web.factory.Costanti;

/***
 * 
 * Implementazione base di un elemento di tipo Choice.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 * @param <T> tipo del valore da utilizzare.
 */
public abstract class ChoiceFormField<T> extends BaseFormField<T>{  

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<SelectItem> elencoSelectItems;

	protected List<org.openspcoop2.generic_project.web.input.SelectItem> elencoHtmlOptions; 
	
	protected String direzione;
	
	public ChoiceFormField(){
		this.direzione = Costanti.CHOICE_ORIENTATION_HORIZONTAL;
	}
	
	public List<SelectItem> getElencoSelectItems() {
		return this.elencoSelectItems;
	}

	public void setElencoSelectItems(List<SelectItem> elencoSelectItems) {
		this.elencoSelectItems = elencoSelectItems;
	}
	

	public List<org.openspcoop2.generic_project.web.input.SelectItem> getOptions() {
		return this.elencoHtmlOptions;
	}

	public void setOptions(List<org.openspcoop2.generic_project.web.input.SelectItem> elencoHtmlOptions) {
		this.elencoHtmlOptions = elencoHtmlOptions;
	}

	public String getDirezione() {
		return this.direzione;
	}

	public void setDirezione(String direzione) {
		this.direzione = direzione;
	}
	
	
}
