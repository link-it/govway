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

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.openspcoop2.generic_project.web.form.field.SelectListField;
import org.openspcoop2.generic_project.web.mbean.LoginBean;

/**
* LanguageForm Form per la gestione dell'internazionalizzazione
* 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class LanguageForm extends BaseForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private LoginBean mBean = null;
	private SelectListField lingua = null;
	
	public LanguageForm(){
		init();
	}

	@Override
	protected void init() {
		// Properties del form
		this.setIdForm("formLingua");
		this.setClosable(false); 
		this.setRenderRegionOnly(false);

		this.lingua = new SelectListField();
		this.lingua.setName("linguaPagina");
		this.lingua.setRequired(false);
		this.lingua.setLabel("");
		this.lingua.setFieldsToUpdate(this.getIdForm() + "_formPnl,headerct,mainct,menuct"); //"this.getIdForm() + "_formPnl
		this.lingua.setForm(this);
		this.lingua.setWidth(70);
		this.lingua.setStyle("width:70px"); 
	}

	@Override
	public void reset() {
		this.lingua.reset();
	}

	public void linguaPaginaSelectListener(ActionEvent ae){
		this.mBean.cambiaLinguaListener(ae); 
	}

	public LoginBean getmBean() {
		return this.mBean;
	}

	public void setmBean(LoginBean mBean) {
		this.mBean = mBean;
	}

	public SelectListField getLingua() {
		return this.lingua;
	}

	public void setLingua(SelectListField lingua) {
		this.lingua = lingua;
	}
}
