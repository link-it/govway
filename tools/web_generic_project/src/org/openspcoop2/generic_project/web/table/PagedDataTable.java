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
package org.openspcoop2.generic_project.web.table;

import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.mbean.IManagedBean;


/***
 * 
 * Interfaccia base che definisce una tabella paginata.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 * @param <V> Oggetto che contiene i dati da visualizzare nella tabella.
 * @param <FormType> Tipo del form.
 * @param <SearchFormType> Tipo del SearchForm.
 */
public interface PagedDataTable<V,SearchFormType extends SearchForm,FormType extends Form> extends Table<V> {

	// Getter/Setter per il ManagedBean 
	public IManagedBean<SearchFormType, FormType> getMBean();
	public void setMBean(IManagedBean<SearchFormType, FormType> mBean); 

	// Mostra/Nascondi check box per la selezione di tutte le righe
	public boolean isShowSelectAll();
	public void setShowSelectAll(boolean showSelectAll);

	// Abilita/disabilita cancellazione righe
	public boolean isEnableDelete();
	public void setEnableDelete(boolean enableDelete);

	// Indica se la tabella contiene una lista finita di valori
	public boolean isIsList();
	public void setIsList(boolean isList);

	// Indica se l'utente ha definito delle procedure di cancellazione diverse da quella standard fornita dalla libreria
	public boolean isCustomDelete();
	public void setCustomDelete(boolean customDelete);

	// Mostra/Nascondi pulsante nuovo Elemento
	public boolean isShowAddButton();
	public void setShowAddButton(boolean showAddButton);

}
