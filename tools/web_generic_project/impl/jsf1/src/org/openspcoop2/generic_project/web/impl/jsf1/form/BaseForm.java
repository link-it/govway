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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;
import org.openspcoop2.generic_project.web.form.ActionListener;
import org.openspcoop2.generic_project.web.form.CostantiForm;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.impl.jsf1.CostantiJsf1Impl;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;
import org.openspcoop2.generic_project.web.input.FormField;
import org.openspcoop2.generic_project.web.mbean.IManagedBean;

/***
 * 
 * Implementazione base di un form.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
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

	private IManagedBean<SearchForm,Form> mBean; 

	private WebGenericProjectFactory factory = null;
	
	private boolean showNotaCampiObbligatori = false;

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

	@Override
	public void setField(String fieldName, FormField<?> field){
		this.fields.put(fieldName, field);
	}

	@Override
	public void setField(FormField<?> field) {
		if(field != null && field.getName() != null)
			this.setField(field.getName(), field);
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
	public IManagedBean<SearchForm,Form> getMBean() {
		return this.mBean;
	}

	@Override
	public void setMBean(IManagedBean<SearchForm,Form> mBean) {
		this.mBean = mBean;
	}

	@Override
	public WebGenericProjectFactory getFactory()
			throws FactoryException {
		if(this.factory == null)
			this.factory = WebGenericProjectFactoryManager.getInstance().getWebGenericProjectFactoryByName(CostantiJsf1Impl.FACTORY_NAME);

		return this.factory;
	}

	@Override
	public void setFactory(WebGenericProjectFactory factory)
			throws FactoryException {
		this.factory  = factory;

	}

	@Override
	public FormField<?> getField(String id) throws Exception{
		return getFieldById(id);
	}		

	@Override
	public void resetFieldValue(String id) throws Exception{
		FormField<?> field = getFieldById(id);

		if(field != null)
			field.reset();
	}


	private FormField<?> getFieldById(String id) throws Exception{
		FormField<?> f = null;

		try {
			// Se il field si trova all'interno della mappa dei field, lo restituisco 
			if(this.getFields() != null)
				f = this.getFields().get(id);

			if(f == null){
				Class<? extends BaseForm> myClass = this.getClass();


				Field[] fields = myClass.getDeclaredFields();

				for (Field field : fields) {
					Class<?> fieldClazz = field.getType();

					// controllo che il field implementi l'interfaccia FormField
					if(FormField.class.isAssignableFrom(fieldClazz)){
						//prelevo accessibilita field
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						FormField<?> formField = (FormField<?>) field.get(this);
						// ripristino accessibilita
						field.setAccessible(accessible);

						if(formField != null){
							String name = (String) formField.getName();

							// se ho trovato il field corretto allora ho terminato la ricerca.
							if(name.equals(id)){
								f = formField;

								// se non l'ho trovato dentro la mappa dei field lo aggiungo per evitare di fare sempre la reflection
								if(!this.fields.containsKey(name))
									this.setField(name, f);

								break;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			throw e;		
		} 
		return f;
	}

	@Override
	public boolean isShowNotaCampiObbligatori() {
		return this.showNotaCampiObbligatori;
	}

	@Override
	public void setShowNotaCampiObbligatori(boolean showNotaCampiObbligatori) {
		this.showNotaCampiObbligatori = showNotaCampiObbligatori;
	}
}
