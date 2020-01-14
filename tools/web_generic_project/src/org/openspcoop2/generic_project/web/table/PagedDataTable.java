/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
	
	// Mostra/Nascondi comandi per l'export di tutti i risultati anche quelli non visualizzati nella pagina corrente
	public boolean isShowSelectAllCommands();
	public void setShowSelectAllCommands(boolean showSelectAllCommands);

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
	
	// Mostra/Nascondi comandi di paginazione quando il numero di pagine di risultati e' uno
	public boolean isDsTopRenderIfSinglePage();
	public void setDsTopRenderIfSinglePage(boolean dsTopRenderIfSinglePage);
	public boolean isDsBottomRenderIfSinglePage();
	public void setDsBottomRenderIfSinglePage(boolean dsBottomRenderIfSinglePage);

}
