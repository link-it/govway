/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.Expression;
import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.monitor.core.bean.AbstractCoreSearchForm;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.richfaces.model.FilterField;
import org.richfaces.model.Modifiable;
import org.richfaces.model.Ordering;
import org.richfaces.model.SortField2;
import org.slf4j.Logger;

/***
 * SortableBaseDataModel Estende la classe {@link BaseDataModel} aggiungendo la funzionalita' di ordinamento delle colonne della tabella.
 *  
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 * @param <K> Tipo della chiave del bean da visualizzare
 * @param <T> Tipo del bean da visualizzare
 * @param <D> Tipo del DataProvider
 * @param <S> Tipo del SearchForm
 */
public abstract class SortableBaseDataModel<K, T, D, S extends AbstractCoreSearchForm> extends BaseDataModelWithSearchForm<K, T, D, S> implements Modifiable{
	/** */
	private static final long serialVersionUID = 2954923950179861809L;
	/** */
//	protected SortOrder sortOrder = SortOrder.DESC;
	/** */
//	protected String sortField = getDefaultSortField();
	
	private static Logger log = LoggerWrapperFactory.getLogger(SortableBaseDataModel.class);
	
//	private Map<String, Ordering> sortOrders = new HashMap<String, Ordering>();
	
	@Override
	public void modify(List<FilterField> filterFields,
			List<SortField2> sortFields) {
		log.trace("modify numero entries richieste: ["+this.rowsToDisplay+"] ID["+this.toString()+"]");
		if (sortFields != null && !sortFields.isEmpty())
	    {
	        SortField2 sortField2 = sortFields.get(0);
	        Expression expression = sortField2.getExpression();
	        String expressionStr = expression.getExpressionString();
	        
	        if (!expression.isLiteralText())
	        {
	            expressionStr = expressionStr.replaceAll("[#|$]{1}\\{.*?\\.", "").replaceAll("\\}", "");
	        }
	        
	        this.setSortField(expressionStr);
	        log.debug("Nuovo Sort Field ["+this.getSortField()+"]");
	 	        
	        Ordering ordering = sortField2.getOrdering();
	 
	        this.aggiornaSortOrder(expressionStr, ordering);
	      
	        if (ordering == Ordering.DESCENDING)
	        {
	            this.setSortOrder(SortOrder.DESC);
	        }
	        else
	        {
	            this.setSortOrder(SortOrder.ASC);
	        }
	    }
	}
	
	private void aggiornaSortOrder(String field, Ordering order) {
		Map<String, Ordering> tmpMap = new HashMap<String, Ordering>();
		for (String f : this.getSortOrders().keySet()) {
			if(f.equals(field)){
				tmpMap.put(f, order);
			} else {
				tmpMap.put(f, Ordering.UNSORTED);
			}
		}
		
		this.getSortOrders().clear();
		this.getSortOrders().putAll(tmpMap); 
	}

	/**
	 * @see org.ajax4jsf.model.ExtendedDataModel#walk(javax.faces.context.FacesContext,
	 *      org.ajax4jsf.model.DataVisitor, org.ajax4jsf.model.Range,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument)	throws IOException	{
		try{
			this.checkDataProvider();
			AbstractCoreSearchForm searchForm =  null;
			
			if(this.dataProvider instanceof ISearchFormService) {
				searchForm = ((ISearchFormService<T, K, AbstractCoreSearchForm>)this.dataProvider).getSearch();
				
				boolean usaBuffer = (this.detached || this.wrappedKeys != null);
				log.trace("walk numero entries richieste: ["+this.rowsToDisplay+"], usaBuffer ["+usaBuffer+"] ID["+this.toString()+"]");
				
				if (usaBuffer){
					for (final Object key : searchForm.getWrappedKeys()) {
						setRowKey(key);
						visitor.process(context, key, argument);
					}
				} else {
					int start = 0; int limit = 0;
					if(searchForm.isUseCount()) {
						// ripristino la ricerca.
						if(searchForm.isRestoreSearch()){
							start = searchForm.getStart();
							limit = searchForm.getLimit();
							searchForm.setRestoreSearch(false);

							int pageIndex = (start / limit) + 1;
							//					searchForm.setPageIndex(pageIndex);
							searchForm.setCurrentPage(pageIndex);
							// Aggiorno valori paginazione
							//range = new SequenceRange(start,limit);
						}
						else{
							start = ((SequenceRange)range).getFirstRow();
							limit = ((SequenceRange)range).getRows();
						}

						//				log.debug("Richiesti Record S["+start+"] L["+limit+"], FiltroPagina ["+searchForm.getCurrentPage()+"]"); 

						searchForm.setStart(start);
						searchForm.setLimit(limit); 
					}else {
						// se non uso la count allora start e limit sono gestiti dai tasti nella pagina
						start = searchForm.getStart();
						limit = searchForm.getLimit();
					}
					
					this.wrappedKeys = new ArrayList<K>();
					
					log.trace("walk numero entries richieste: ["+this.rowsToDisplay+"], Start ["+start+"]. Limit ["+limit+"] ID["+this.toString()+"]");
					List<T> bufferList = findObjects(start, limit, this.getSortField(), this.getSortOrder());
					this.currentSearchSize = bufferList != null ?  bufferList.size() : 0;
					searchForm.setCurrentSearchSize(this.currentSearchSize);
					if(bufferList!=null) {
						for (final T obj : bufferList) {
							this.wrappedData.put(getId(obj), obj);
							this.wrappedKeys.add(getId(obj));
							visitor.process(context,getId(obj) , argument);
						}
					}
					searchForm.setWrappedKeys(this.wrappedKeys);
				}
			} else {
				
				boolean usaBuffer = (this.detached || this.wrappedKeys != null); // && isSortObjectNull;
				log.trace("walk numero entries richieste: ["+this.rowsToDisplay+"], usaBuffer ["+usaBuffer+"] ID["+this.toString()+"]");
				
				if (usaBuffer){
					for (final K key : this.wrappedKeys) {
						setRowKey(key);
						visitor.process(context, key, argument);
					}
				} else {
					int start = 0; int limit = 0;
					start = ((SequenceRange)range).getFirstRow();
					limit = ((SequenceRange)range).getRows();
					
					this.wrappedKeys = new ArrayList<K>();

					List<T> bufferList = findObjects(start, limit, this.getSortField(), this.getSortOrder());
					this.currentSearchSize = bufferList != null ?  bufferList.size() : 0;
					if(bufferList!=null) {
						for (final T obj : bufferList) {
							this.wrappedData.put(getId(obj), obj);
							this.wrappedKeys.add(getId(obj));
							visitor.process(context,getId(obj) , argument);
						}
					}
				}
			}
		}catch(Exception e){
			log.error("Errore durante la walk: "+e.getMessage(),e); 
		}
	}
	
	/**
	 * @return String
	 */
	public abstract String getDefaultSortField();

	public abstract SortOrder getSortOrder();

	public abstract void setSortOrder(SortOrder sortOrder);

	public abstract String getSortField(); 

	public abstract void setSortField(String sortField);

	public abstract Map<String, Ordering> getSortOrders();

	public abstract void setSortOrders(Map<String, Ordering> sortOrders) ;
}