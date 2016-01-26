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
package org.openspcoop2.generic_project.web.impl.jsf1.form;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.LoginBean;
import org.openspcoop2.generic_project.web.input.FormField;
import org.openspcoop2.generic_project.web.input.SelectList;

/**
 * LanguageForm Form per la gestione dell'internazionalizzazione
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LanguageForm extends BaseForm  implements Form,Serializable { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private LoginBean mBean = null;
	private SelectList lingua = null; 

	public LanguageForm(){
		try{
			init();
		}catch(Exception e){

		}
	}

	@Override
	public void init() throws FactoryException {
		// Properties del form
		this.setId("formLingua");
		this.setClosable(false); 
		this.setRenderRegionOnly(false);

		this.lingua = this.getFactory().getInputFieldFactory().createSelectList();
		this.lingua.setName("linguaPagina");
		this.lingua.setRequired(false);
		this.lingua.setLabel("");
		this.lingua.setFieldsToUpdate(this.getId() + "_formPnl,headerct,mainct,menuct"); //"this.getIdForm() + "_formPnl
		this.lingua.setForm(this);
		this.lingua.setWidth(70);
		this.lingua.setStyle("width:70px"); 

	}

	@Override
	public void reset() {
		this.lingua.reset();
	}



	public LoginBean getmBean() {
		return this.mBean;
	}

	public void setmBean(LoginBean mBean) {
		this.mBean = mBean;

	}

	public SelectList  getLingua() {
		return this.lingua;
	}

	public void setLingua(SelectList  lingua) {
		this.lingua = lingua;
	}

	public void linguaPaginaSelectListener(ActionEvent ae){
		this.mBean.cambiaLinguaListener(ae); 
	}

	@Override
	public FormField<?> getField(String id) {
		return this.lingua;
	}

	@Override
	public void resetFieldValue(String id) {
		this.lingua.reset();		
	}

	@Override
	public void setObject(Object object) throws Exception {

	}

	@Override
	public Object getObject() throws Exception {return null;
	}

	@Override
	public String valida() throws Exception {
		return null;
	}

}
