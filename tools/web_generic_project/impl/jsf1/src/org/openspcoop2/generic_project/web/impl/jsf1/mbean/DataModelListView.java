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
package org.openspcoop2.generic_project.web.impl.jsf1.mbean;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.web.bean.IBean;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.impl.jsf1.datamodel.ParameterizedDataModel;
import org.openspcoop2.generic_project.web.iservice.IBaseService;
import org.openspcoop2.generic_project.web.mbean.ManagedBean;
import org.openspcoop2.generic_project.web.table.PagedDataTable;

/***
 * 
 * Classe base di un managed Bean con form di ricerca, form di edit e tabella con supporto alla paginazione.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * 
 */
public abstract class DataModelListView<BeanType extends IBean<DTOType,KeyType>, KeyType, SearchFormType extends SearchForm, FormType extends Form,
DMType extends ParameterizedDataModel<KeyType, BeanType, DataProviderType, DTOType, SearchFormType>, DTOType,DataProviderType extends IBaseService<BeanType,KeyType,SearchFormType>>    
extends BaseMBean<BeanType, KeyType, SearchFormType> implements ManagedBean<FormType, SearchFormType>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected FormType form;
	protected PagedDataTable<DMType,FormType,SearchFormType> table; 

	public DataModelListView () {
		super();
	}
	
	public DataModelListView (Logger log) {
		super(log);
	}

	@Override
	public FormType getForm() {
		return this.form;
	}

	@Override
	public void setForm(FormType form) {
		this.form = form;
	}

	public PagedDataTable<DMType,FormType,SearchFormType> getTable() throws Exception{
		if(this.table==null){
			try{
				this.table = this.factory.getTableFactory().createPagedDataTable();
				this.table.setMBean(this);
			}catch (Exception e) {
				throw e;
			}
		}

		return this.table;
	}

	public void setTable(PagedDataTable<DMType,FormType,SearchFormType> table) {
		this.table = table;
	}

}
