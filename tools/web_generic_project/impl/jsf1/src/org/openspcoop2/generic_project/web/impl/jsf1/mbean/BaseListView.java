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

import java.util.List;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.mbean.ManagedBean;
import org.openspcoop2.generic_project.web.table.Table;

/***
 * 
 * Classe base di un ManagedBean per una pagina con Form di ricerca, form di update e tabella per la presentazione dati.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * 
 */
public abstract class BaseListView<BeanType, KeyType, SearchFormType extends SearchForm, FormType extends Form, ValueType> 
extends BaseMBean<BeanType, KeyType, SearchFormType> implements ManagedBean<FormType, SearchFormType>{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected FormType form;
	protected Table<List<ValueType>> table; 

	public BaseListView() {
		super();
	}
	public BaseListView(Logger log) {
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

	public Table<List<ValueType>> getTable() throws Exception{
		if(this.table==null){
			try{
				this.table = this.factory.getTableFactory().createTable();
			}catch (Exception e) {
				throw e;
			}
		}
		
		return this.table;
	}

	public void setTable(Table<List<ValueType>> table) {
		this.table = table;
	}
}
