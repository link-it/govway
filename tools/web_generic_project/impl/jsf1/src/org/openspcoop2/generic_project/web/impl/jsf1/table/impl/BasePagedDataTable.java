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
package org.openspcoop2.generic_project.web.impl.jsf1.table.impl;

import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.mbean.ManagedBean;
import org.openspcoop2.generic_project.web.table.PagedDataTable;

/***
 * 
 * Implementazione base per una tabella paginata che presenta un'insieme di valori.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 * @param <V> Lista dei valori da visualizzare.
 * @param <FormType> Tipo del form da utilizzare per effettuare delle azioni di modifica dei dati.
 * @param <SearchFormType> Tipo di form di ricerca da utilizzare per gestire la paginazione.
 */
public class BasePagedDataTable<V,FormType extends Form, SearchFormType extends SearchForm> extends BaseTable<V>
implements PagedDataTable<V,FormType,SearchFormType>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private ManagedBean<FormType, SearchFormType> mBean;
	protected boolean isList;
	protected boolean showSelectAll;
	protected boolean enableDelete;
	protected boolean customDelete;
	
	
	@Override
	public boolean isIsList() {
		return this.isList;
	}
	@Override
	public void setIsList(boolean isList) {
		this.isList = isList;
	}
	@Override
	public boolean isShowSelectAll() {
		return this.showSelectAll;
	}
	@Override
	public void setShowSelectAll(boolean showSelectAll) {
		this.showSelectAll = showSelectAll;
	}
	@Override
	public boolean isEnableDelete() {
		return this.enableDelete;
	}
	@Override
	public void setEnableDelete(boolean enableDelete) {
		this.enableDelete = enableDelete;
	}
	@Override
	public boolean isCustomDelete() {
		return this.customDelete;
	}
	@Override
	public void setCustomDelete(boolean customDelete) {
		this.customDelete = customDelete;
	}
	
	@Override
	public ManagedBean<FormType, SearchFormType> getMBean() {
		return this.mBean;
	}
	@Override
	public void setMBean(ManagedBean<FormType, SearchFormType> mBean) {
		this.mBean = mBean;
	}

}
