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
package org.openspcoop2.generic_project.web.impl.jsf1.mbean;

import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.impl.jsf1.datamodel.ParameterizedDataModel;
import org.openspcoop2.generic_project.web.iservice.IBaseService;
import org.openspcoop2.generic_project.web.mbean.IManagedBean;
import org.openspcoop2.generic_project.web.table.PagedDataTable;
import org.openspcoop2.generic_project.web.view.IViewBean;

/***
 * 
 * Classe base di un managed Bean con form di ricerca, form di edit e tabella con supporto alla paginazione.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public abstract class DataModelListView<DTOType, KeyType, BeanType extends IViewBean<DTOType,KeyType>,  SearchFormType extends SearchForm, FormType extends Form,
DMType extends ParameterizedDataModel<DTOType,KeyType, BeanType, DataProviderType, SearchFormType>, DataProviderType extends IBaseService<BeanType,KeyType,SearchFormType>>    
extends BaseMBean<BeanType, KeyType, SearchFormType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected FormType form;
	protected PagedDataTable<DMType,SearchFormType,FormType> table; 
	protected DMType dataModel = null;

	public DataModelListView () {
		super();
	}

	public DataModelListView (Logger log) {
		super(log);
	}

	@Override
	public void setSearch(SearchFormType search) {
		super.setSearch(search);
		try {
			this.getDataModel().setForm(search);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setService(
			IBaseService<BeanType, KeyType, SearchFormType> service) {
		super.setService(service);

		try {
			if(service != null)
				this.getDataModel().setDataProvider((DataProviderType) service);
		} catch (Exception e) {
		} 
	}

	@Override
	public FormType getForm() {
		return this.form;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setForm(Form form) {
		this.form = (FormType) form;
	}

	@SuppressWarnings("unchecked")
	public PagedDataTable<DMType,SearchFormType,FormType> getTable() throws Exception{
		if(this.table==null){
			try{
				this.table = this.factory.getTableFactory().createPagedDataTable();
				this.table.setMBean((IManagedBean<SearchFormType, FormType>) this);
				this.table.setMetadata(this.getMetadata()); 
			}catch (Exception e) {
				throw e;
			}
		}

		return this.table;
	}

	public void setTable(PagedDataTable<DMType,SearchFormType,FormType> table) {
		this.table = table;
	}

	@SuppressWarnings("unchecked")
	public DMType getDataModel() throws Exception {
		if(this.dataModel==null){
			try{

				ParameterizedType parameterizedType =  (ParameterizedType) getClass().getGenericSuperclass();
				this.dataModel = ((Class<DMType>)parameterizedType.getActualTypeArguments()[4]).newInstance();
			}catch (Exception e) {
				this.getLog().error(e.getMessage(),e);
				throw e;
			}
		}
		return this.dataModel;
	}

	public void setDataModel(DMType dataModel) {
		this.dataModel = dataModel;
	}


	
	@Override
	@SuppressWarnings("unchecked")
	public BeanType getMetadata() throws Exception{
		if(this.metadata==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.metadata = ((Class<BeanType>)parameterizedType.getActualTypeArguments()[2]).newInstance();
			}catch (Exception e) {
				this.getLog().error(e.getMessage(),e);
				throw e;
			}
		}
		return this.metadata;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public BeanType getSelectedElement() throws Exception{
		if(this.selectedElement==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.selectedElement = ((Class<BeanType>)parameterizedType.getActualTypeArguments()[2]).newInstance();
			}catch (Exception e) {
				this.getLog().error(e.getMessage(),e);
				throw e;
			}
		}
		return this.selectedElement;
	}

}
