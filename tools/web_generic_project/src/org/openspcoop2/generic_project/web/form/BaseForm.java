/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.web.form;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.generic_project.web.core.Utils;
import org.openspcoop2.generic_project.web.form.field.FormField;


/**
 * BaseForm Class base per la gestione dei form create/update  all'interno della console.
 * 
 * La classe definisce le informazioni di base per una form:
 * 
 * fields: mappa contenente i fields della ricerca.
 * nomeForm: label della form da visualizzare;
 * closable: indica se la form sara' visualizzata dentro un pannello che si puo' nascondere oppure no;
 * idForm: id della form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseForm {

	protected Map<String, FormField<?>> fields = null;

	protected String nomeForm = null;

	protected boolean closable = false;

	protected String idForm = null;

	private boolean rendered = true;
	
	private boolean renderRegionOnly = true;

	public boolean isClosable() {
		return this.closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	public String getIdForm() {
		if(this.idForm == null)
			this.idForm= CostantiForm.DEFAULT_FORM_ID;

		return this.idForm;
	}

	public void setIdForm(String idForm) {
		this.idForm = idForm;
	}

	public BaseForm(){
		this.fields = new HashMap<String, FormField<?>>();
	}

	protected abstract void init();

	public abstract void reset();

	public Map<String, FormField<?>> getFields() {
		return this.fields;
	}

	public void setFields(Map<String, FormField<?>> fields) {
		this.fields = fields; 
	}

	public void setField(String fieldName, FormField<?> field){
		this.fields.put(fieldName, field);
	}

	public String getNomeForm() {
		if(this.nomeForm != null){
			try{
				String tmp = Utils.getMessageFromResourceBundle(this.nomeForm);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
			}catch(Exception e){}
		}
		return this.nomeForm;
	}

	public void setNomeForm(String nomeForm) {
		this.nomeForm = nomeForm;
	}

	public boolean isRendered() {
		return this.rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public boolean isRenderRegionOnly() {
		return this.renderRegionOnly;
	}

	public void setRenderRegionOnly(boolean renderRegionOnly) {
		this.renderRegionOnly = renderRegionOnly;
	}

	

}
