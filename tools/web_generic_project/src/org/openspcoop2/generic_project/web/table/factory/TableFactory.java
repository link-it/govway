/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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
package org.openspcoop2.generic_project.web.table.factory;

import java.io.Serializable;

import org.openspcoop2.generic_project.web.table.PagedDataTable;
import org.openspcoop2.generic_project.web.table.Table;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;

/***
 * 
 * Interfaccia base che definisce la factory delle tabelle
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface TableFactory extends Serializable{

	public <V> Table<V> createTable() throws FactoryException;

	public <V,SearchFormType extends SearchForm,FormType extends Form> PagedDataTable<V, SearchFormType,FormType> createPagedDataTable() throws FactoryException;
}
