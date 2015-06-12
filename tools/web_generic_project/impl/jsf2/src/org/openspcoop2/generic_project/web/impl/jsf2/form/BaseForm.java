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
package org.openspcoop2.generic_project.web.impl.jsf2.form;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;
import org.openspcoop2.generic_project.web.form.ActionListener;
import org.openspcoop2.generic_project.web.form.CostantiForm;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.impl.jsf2.CostantiJsf2Impl;
import org.openspcoop2.generic_project.web.impl.jsf2.utils.Utils;
import org.openspcoop2.generic_project.web.input.FormField;
import org.openspcoop2.generic_project.web.mbean.ManagedBean;

/**
 * Implementazione base per un oggetto Form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *
 */
public abstract class BaseForm implements Form {

	protected Map<String, FormField<?>> fields = null;

	protected String nome = null;

	protected boolean closable = false;

	protected String id = null;

	private boolean rendered = true;
	
	protected ActionListener actionListener = null;
	
	private boolean renderRegionOnly = true;

	private ManagedBean<Form, SearchForm> mBean; 
	
	private WebGenericProjectFactory factory = null;
	
	public BaseForm(){
		this.fields = new HashMap<String, FormField<?>>();
	}

	@Override
	public String getNomeForm() {
		if(this.nome != null){
			try{
				String tmp = Utils.getInstance().getMessageFromResourceBundle(this.nome);

				if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
					return tmp;
			}catch(Exception e){}
		}
		return this.nome;
	}

	public boolean isRenderRegionOnly() {
		return this.renderRegionOnly;
	}

	public void setRenderRegionOnly(boolean renderRegionOnly) {
		this.renderRegionOnly = renderRegionOnly;
	}

	public ActionListener getActionListener(){
		return this.actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
	
	@Override
	public boolean isClosable() {
		return this.closable;
	}

	@Override
	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	@Override
	public String getId() {
		if(this.id == null)
			this.id= CostantiForm.DEFAULT_FORM_ID;

		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Map<String, FormField<?>> getFields() {
		return this.fields;
	}

	@Override
	public void setFields(Map<String, FormField<?>> fields) {
		this.fields = fields; 
	}

	public void setField(String fieldName, FormField<?> field){
		this.fields.put(fieldName, field);
	}

	 
	@Override
	public void setNomeForm (String nomeForm) {
		this.nome = nomeForm;
	}

	@Override
	public boolean isRendered() {
		return this.rendered;
	}

	@Override
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	@Override
	public ManagedBean<Form, SearchForm> getMBean() {
		return this.mBean;
	}

	@Override
	public void setMBean(ManagedBean<Form, SearchForm> mBean) {
		this.mBean = mBean;
	}

	@Override
	public WebGenericProjectFactory getWebGenericProjectFactory()
			throws FactoryException {
		if(this.factory == null)
			this.factory = WebGenericProjectFactoryManager.getInstance().getWebGenericProjectFactoryByName(CostantiJsf2Impl.FACTORY_NAME);
		
		return this.factory;
	}

	@Override
	public void setWebGenericProjectFactory(WebGenericProjectFactory factory)
			throws FactoryException {
		this.factory  = factory;
		
	}

}
