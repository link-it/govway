/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.generic_project.web.impl.jsf1.table.factory.impl;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.impl.jsf1.table.impl.BasePagedDataTable;
import org.openspcoop2.generic_project.web.impl.jsf1.table.impl.BaseTable;
import org.openspcoop2.generic_project.web.table.PagedDataTable;
import org.openspcoop2.generic_project.web.table.Table;
import org.openspcoop2.generic_project.web.table.factory.TableFactory;

/***
 * 
 * Implementazione della factory JSF1 per le tabelle.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class Jsf1TableFactoryImpl implements TableFactory{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	@SuppressWarnings("unused")
	private transient WebGenericProjectFactory webGenericProjectFactory;
	@SuppressWarnings("unused")
	private transient Logger log = null;

	public Jsf1TableFactoryImpl(WebGenericProjectFactory factory,Logger log){
		this.webGenericProjectFactory = factory;
		this.log = log;
	}

	@Override
	public <V> Table<V> createTable() throws FactoryException {
		return new BaseTable<V>();
	}

	@Override
	public <V, SearchFormType extends SearchForm, FormType extends Form> PagedDataTable<V, SearchFormType,FormType> createPagedDataTable()
			throws FactoryException {
		return  new BasePagedDataTable<V, SearchFormType, FormType>();
	}
 
}
