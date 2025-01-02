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
package org.openspcoop2.generic_project.web.impl.jsf1.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.iservice.IBaseService;
import org.openspcoop2.generic_project.web.view.IViewBean;

/**
 * 
 * Fornisce il supporto al componente di Richfaces DataTable.
 * Si aggancia con il livello dei dati tramite il DataProvider.
 *
 * @param <KeyType> Il tipo della chiave (Integer, Long ...)
 * @param <BeanType> Il tipo di dato che rappresenta una righa della tabella
 * @param <DataProvider> Il tipo del dataprovider
 * @param <DTOType> Il tipo di dato primitivo incapsulato nel Bean dell'interfaccia
 * @param <SearchFormType> Il tipo di search form utilizzato
*
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$

*/

public abstract class ParameterizedDataModel<DTOType, KeyType, BeanType extends IViewBean<DTOType, KeyType>, DataProvider extends IBaseService<BeanType, KeyType, SearchFormType>,  SearchFormType extends SearchForm>

	extends BaseDataModelWithForm<DTOType, KeyType,BeanType,DataProvider,SearchFormType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range rangeParams,
			Object argument) throws IOException {
		try{
			if(this.detached){
				for (KeyType  key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			}else{
				int start = 0; int limit = 0;
				// ripristino la ricerca.
				if(this.getDataProvider().getForm().isRestoreSearch()){
					start = this.getDataProvider().getForm().getStart();
					limit = this.getDataProvider().getForm().getLimit();
					this.getDataProvider().getForm().setRestoreSearch(false);
					
					int pageIndex = (start / limit) + 1;
//					this.getDataProvider().getForm().setPageIndex(pageIndex);
					this.getDataProvider().getForm().setCurrentPage(pageIndex);
					// Aggiorno valori paginazione
					//range = new SequenceRange(start,limit);
				}
				else{
					  start = ((SequenceRange)rangeParams).getFirstRow();
					  limit = ((SequenceRange)rangeParams).getRows();
				}

//				log.debug("Richiesti Record S["+start+"] L["+limit+"], FiltroPagina ["+this.getDataProvider().getForm().getCurrentPage()+"]"); 

				this.getDataProvider().getForm().setStart(start);
				this.getDataProvider().getForm().setLimit(limit); 

				this.wrappedKeys = new ArrayList<KeyType>();
				List<BeanType> list = this.getDataProvider().findAll(start, limit);
				for (BeanType bean : list) {
					KeyType id = bean.getId();

					this.wrappedData.put(id, bean);
					this.wrappedKeys.add(id);
					visitor.process(context, id, argument);
				}
			}
		}catch (Exception e) {
//			log.error(e,e);
		}

	}

	@Override
	public int getRowCount() {
		if(this.rowCount==null){
			if(this.getDataProvider().getForm().isNewSearch()){
				try {
					this.rowCount = this.getDataProvider().totalCount();
					this.getDataProvider().getForm().setTotalCount(this.rowCount);
				} catch (ServiceException e) {
					this.getDataProvider().getForm().setTotalCount(0);
					return 0;
				}
				this.getDataProvider().getForm().setNewSearch(false); 
			}
			else {
				this.rowCount = this.getDataProvider().getForm().getTotalCount();
			}
		}
		return this.rowCount;
	}

	@Override
	public Object getRowData() {
		if(this.currentPk==null)
			return null;
		else{
			BeanType t = this.wrappedData.get(this.currentPk);
			if(t==null){
				try {
					t=this.getDataProvider().findById(this.currentPk);
					this.wrappedData.put(this.currentPk, t);
				} catch (ServiceException e) {}
			}
			return t;
		}
	}
	
    @Override
	public KeyType getId(BeanType object){
    	if(object != null)
    		return object.getId();
    	
    	return null;
    }
    
    @Override
	public Integer getCurrentPage() {
    	if(this.getDataProvider().getForm() != null)
    		return this.getDataProvider().getForm().getCurrentPage();
    		
		return this.currentPage;
	}
    
    @Override
	public void setCurrentPage(Integer currentPage) {
    	if(this.getDataProvider().getForm() != null)
    		this.getDataProvider().getForm().setCurrentPage(currentPage);
    	
		this.currentPage = currentPage;
	}
}
