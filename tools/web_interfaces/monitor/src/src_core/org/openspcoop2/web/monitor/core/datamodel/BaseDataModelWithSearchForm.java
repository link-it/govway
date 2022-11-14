/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.ajax4jsf.model.SerializableDataModel;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.monitor.core.bean.AbstractCoreSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.slf4j.Logger;

/**
 * BaseDataModelWithSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 * @param <K> Il tipo della chiave (Integer, Long ...)
 * @param <T> Il tipo di dato che rappresenta una righa della tabella
 * @param <D> Il tipo del dataprovider
 */
public abstract class BaseDataModelWithSearchForm<K, T , D, S extends AbstractCoreSearchForm> extends SerializableDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8413193446969383169L;
	private static Logger log = LoggerWrapperFactory.getLogger(BaseDataModelWithSearchForm.class);
	protected transient D dataProvider;
	protected boolean detached = false;
	protected K currentPk;
	protected Map<K,T> wrappedData = new HashMap<K,T>();
	protected List<K> wrappedKeys = null;
	protected Integer rowCount;
	protected IExpression countFilter= null;
	protected Integer currentPage = 1;
	protected Integer rowsToDisplay = 25;
	protected S search;
	protected Integer currentSearchSize;

	/**
	 * The boolean field, detached, starts as false.  
	 * It will be set to “true” when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
	 * In this manner, the model will not be updated when the jsf component is rebuilt on postback, but rather when the new model is being built.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void update() {
		//nothing to do
		this.detached = false;
		this.wrappedKeys = null;
		
		log.trace("update numero entries richieste: ["+this.rowsToDisplay+"], detached ["+this.detached+"] wrappedKeys ["+this.wrappedKeys+"] ID["+this.toString()+"]");
		
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				if(((ISearchFormService)this.dataProvider).getSearch() != null) {
					((ISearchFormService)this.dataProvider).getSearch().setDetached(this.detached);
					((ISearchFormService)this.dataProvider).getSearch().setWrappedKeys(this.wrappedKeys);
				}
			}
		}
	}

	/**
	 * This method never called from framework.
	 * (non-Javadoc)
	 * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
	 */
	@Override
	public Object getRowKey() {
		return this.currentPk;
	}
	/**
	 * This method normally called by Visitor before request Data Row.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setRowKey(Object key) {
		this.currentPk = (K) key;
	}

	/**
	 * 
	 * @param object
	 * @return K
	 */
	public abstract K getId(T object);

	/**
	 * This is main part of Visitor pattern. Method called by framework many times during request processing. 
	 * 
	 * We have two checks to see if we should return cached data, or if we should fetch new data.  
	 * The boolean field, detached, starts as false.  
	 * It will be set to “true” when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
	 * In this manner, the model will not be updated when the jsf component is rebuilt on postback, but rather when the new model is being built.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException {
		try{
			this.checkDataProvider();
			AbstractCoreSearchForm searchForm =  null;
			
			if(this.dataProvider instanceof ISearchFormService) {
				searchForm = ((ISearchFormService<T, K, AbstractCoreSearchForm>)this.dataProvider).getSearch();
				boolean usaBuffer = (this.detached || this.wrappedKeys != null); 
				
				log.trace("walk numero entries richieste: ["+this.rowsToDisplay+"], usaBuffer ["+usaBuffer+"] ID["+this.toString()+"]");
				
				if(usaBuffer) {
					for (Object key : searchForm.getWrappedKeys()) {
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

						searchForm.setStart(start);
						searchForm.setLimit(limit); 
					}else {
						// se non uso la count allora start e limit sono gestiti dai tasti nella pagina
						start = searchForm.getStart();
						limit = searchForm.getLimit();
					}
					
					log.trace("walk numero entries richieste: ["+this.rowsToDisplay+"], Start ["+start+"]. Limit ["+limit+"] ID["+this.toString()+"]");
					List<T> bufferList = this.findObjects(start, limit,null,null);
					this.currentSearchSize = bufferList != null ?  bufferList.size() : 0;
					searchForm.setCurrentSearchSize( this.currentSearchSize);
					
					this.wrappedKeys = new ArrayList<K>();
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
				
				if(usaBuffer){
					for (K key : this.wrappedKeys) {
						setRowKey(key);
						visitor.process(context, key, argument);
					}
				}else{
					int start = 0; int limit = 0;
					start = ((SequenceRange)range).getFirstRow();
					limit = ((SequenceRange)range).getRows();
					
					List<T> bufferList = this.findObjects(start, limit,null,null);
					this.currentSearchSize = bufferList != null ?  bufferList.size() : 0;
					
					this.wrappedKeys = new ArrayList<K>();
					if(bufferList!=null) {
						for (final T obj : bufferList) {
							this.wrappedData.put(getId(obj), obj);
							this.wrappedKeys.add(getId(obj));
							visitor.process(context,getId(obj) , argument);
						}
					}
				}
			}
		}catch (Exception e) {
			log.error("Errore durante la walk: "+e.getMessage(),e); 
		}
	}
	
	/**
	 * This method must return actual data rows count from the Data Provider. It is used by pagination control
	 * to determine total number of data items.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int getRowCount(){
		try {

			this.checkDataProvider();

			if(this.rowCount==null){
				if(this.dataProvider instanceof ISearchFormService)
					this.rowCount=_executeTotalCountWithIService((ISearchFormService<T, K, S>) this.dataProvider);
				if(this.dataProvider instanceof IServiceSearchWithId){
					IExpression filter =  null;

					if(this.countFilter == null)
						filter = ((IServiceSearchWithId) this.dataProvider).newExpression();
					else 
						filter = this.countFilter;

					this.rowCount=(int)((IServiceSearchWithId)this.dataProvider).count(filter).longValue();
				}
			}
			return this.rowCount;
		} catch (Exception e) {
			BaseDataModelWithSearchForm.log.error(e.getMessage(), e);
		}
		return 0;
	}

	protected int _executeTotalCountWithIService(ISearchFormService<T, K, S> service) throws Exception { 
		if (service.getSearch().isUseCount()) {
			if(service.getSearch().isExecuteQuery())
				return service.totalCount();
			else return 0;
		} else return this.rowsToDisplay;
	}

	/**
	 * @param start
	 * @param limit
	 * @param sortField
	 * @param sortOrder
	 * @return List
	 */
	protected abstract List<T> findObjects(int start, int limit, String sortField, SortOrder sortOrder);
	
	/**
	 * This is main way to obtain data row. It is intensively used by framework. 
	 * We strongly recommend use of local cache in that method. 
	 * 
	 * <pre>
      <code>if (currentPk==null) {
            return null;
        } else {
            User ret = wrappedData.get(currentPk);
            if (ret==null) {
                ret = getDataProvider().getAuctionItemByPk(currentPk);
                wrappedData.put(currentPk, ret);
                return ret;
            } else {
                return ret;
            }
        }</code>
     	</pre>
	 * 
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getRowData(){
		try {

			this.checkDataProvider();


			if(this.currentPk==null)
				return null;
			else{
				Object m = this.wrappedData.get(this.currentPk);
				if(m==null){
					if(this.dataProvider instanceof ISearchFormService){
						m=((ISearchFormService)this.dataProvider).findById(this.currentPk);
					}
					if(this.dataProvider instanceof IServiceSearchWithId){
						m=((IServiceSearchWithId<T,K>)this.dataProvider).get(this.currentPk);
					}
					//					if(this.dataProvider instanceof org.spcoop.web.customer.ospcconsole.core.IService){
					//						m=((org.spcoop.web.customer.ospcconsole.core.IService)this.dataProvider).read(this.currentPk);
					//					}
					this.wrappedData.put(this.currentPk, (T)m);
				}
				return m;
			}

		} catch (Exception e) {
			BaseDataModelWithSearchForm.log.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public int getRowIndex() {
		return 0;
	}

	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public Object getWrappedData() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRowAvailable() {
		if(this.wrappedData.isEmpty()){
			return false;
		}else{
			boolean isAvailable = this.wrappedData.containsKey(this.currentPk);
			return isAvailable;
		}
	}
	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public void setRowIndex(int arg0) {
		// ignore
		BaseDataModelWithSearchForm.log.debug("called setRowIndex "+arg0);
	}
	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public void setWrappedData(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public D getDataProvider(){
		return this.dataProvider; 
	}

	public void setDataProvider(D dataProvider){
		this.dataProvider = dataProvider;
	}

	/**
	 * This method suppose to produce SerializableDataModel that will be serialized into View State and used on a post-back.
	 * In current implementation we just mark current model as serialized. In more complicated cases we may need to 
	 * transform data to actually serialized form.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public  SerializableDataModel getSerializableModel(Range range) {
		if (this.wrappedKeys!=null) {
			this.detached = true;
			
			if(this.dataProvider instanceof ISearchFormService) {
				((ISearchFormService<T, K, AbstractCoreSearchForm>)this.dataProvider).getSearch().setDetached(this.detached);
			} 
			
			return this; 
		} else {
			return null;
		}
	}

	protected void checkDataProvider() throws Exception{

		if(this.dataProvider instanceof ISearchFormService) return;
		if(this.dataProvider instanceof IServiceSearchWithId) return;

		//data provider sconosciuto
		throw new Exception("DataProvider ["+this.dataProvider.getClass().getCanonicalName()+"] sconosciuto, non so come gestirlo.");
	}

	@SuppressWarnings("rawtypes")
	public Integer getCurrentPage() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				if(((ISearchFormService)this.dataProvider).getSearch() != null)
					return ((ISearchFormService)this.dataProvider).getSearch().getCurrentPage();
			}
		}

		return this.currentPage;
	}

	@SuppressWarnings("rawtypes")
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				AbstractCoreSearchForm searchForm = ((ISearchFormService)this.dataProvider).getSearch();
				if(searchForm != null) {
					searchForm.setCurrentPage(currentPage);
					Integer limit = searchForm.getLimit();
					int start = limit * (currentPage  -1 );
					searchForm.setStart(start); 
				}
			}
		}
	}

	public String nextPage() {
		Integer currentPage = this.getCurrentPage();
		if(currentPage != null)
			this.setCurrentPage(currentPage + 1);
		else 
			this.setCurrentPage(1);
		this.update();

		return null;
	}

	public String prevPage() {
		Integer currentPage = this.getCurrentPage();
		if(currentPage != null)
			this.setCurrentPage(currentPage - 1);
		else 
			this.setCurrentPage(1);
		this.update();

		return null;
	}

	public String firstPage() {
		this.setCurrentPage(1);
		this.update();
		return null;
	}

	private boolean firstEnabled = false;
	private boolean prevEnabled = false;
	private boolean nextEnabled = false;

	@SuppressWarnings("rawtypes")
	public boolean isFirstEnabled() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				if(((ISearchFormService)this.dataProvider).getSearch() != null)
					this.firstEnabled = ((ISearchFormService)this.dataProvider).getSearch().getCurrentPage().intValue() > 1;
			}
		}

		return this.firstEnabled;
	}

	public void setFirstEnabled(boolean firstEnabled) {
		this.firstEnabled = firstEnabled;
	}

	@SuppressWarnings("rawtypes")
	public boolean isPrevEnabled() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				if(((ISearchFormService)this.dataProvider).getSearch() != null)
					this.prevEnabled = ((ISearchFormService)this.dataProvider).getSearch().getCurrentPage().intValue() > 1;
			}
		}
		return this.prevEnabled;
	}

	public void setPrevEnabled(boolean prevEnabled) {
		this.prevEnabled = prevEnabled;
	}

	@SuppressWarnings("rawtypes")
	public boolean isNextEnabled() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				AbstractCoreSearchForm searchForm = ((ISearchFormService)this.dataProvider).getSearch();
				if(searchForm != null) {
					this.nextEnabled = searchForm.getCurrentSearchSize() != null && searchForm.getCurrentSearchSize().intValue() == searchForm.getLimit().intValue();
				}
			}
		}

		return this.nextEnabled;
	}

	public void setNextEnabled(boolean nextEnabled) {
		this.nextEnabled = nextEnabled;
	}

	@SuppressWarnings("rawtypes")
	public String getRecordLabel() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				AbstractCoreSearchForm searchForm = ((ISearchFormService)this.dataProvider).getSearch();
				if(searchForm != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("record [");

						// se ci sono dati nella schermata corrente
						if(searchForm.getCurrentSearchSize() > 0) {
							int start = searchForm.getStart() != null ? (searchForm.getStart().intValue() + 1) : 1;
							int end = searchForm.getStart() != null ? (searchForm.getStart().intValue() + searchForm.getCurrentSearchSize().intValue() ) : searchForm.getCurrentSearchSize().intValue();
							sb.append(start);
							sb.append(" - ");
							sb.append(end);
						} else {
							sb.append("0 - 0");
						}
					sb.append("]");
					return sb.toString();
				}
			}
		}

		return null;
	}

	public void setRecordLabel(String recordLabel) {
	}
	
	@SuppressWarnings("rawtypes")
	public Integer getRowsToDisplay() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				if(((ISearchFormService)this.dataProvider).getSearch() != null)
					return ((ISearchFormService)this.dataProvider).getSearch().getLimit();
			}
		}
		
		return  this.rowsToDisplay;
	}

	@SuppressWarnings("rawtypes")
	public void setRowsToDisplay(Integer rowsToDisplay) {
		this.rowsToDisplay = rowsToDisplay;
		log.trace("setRowsToDisplay numero entries richieste: ["+this.rowsToDisplay+"] ID["+this.toString()+"]");
		
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				AbstractCoreSearchForm searchForm = ((ISearchFormService)this.dataProvider).getSearch();
				if(searchForm != null) {
					searchForm.setLimit(this.rowsToDisplay);
				}
			}
		}
		
		
	}
	
	public void rowsToDisplaySelected(ActionEvent ae) {
		log.trace("rowsToDisplaySelected numero entries richieste: ["+this.rowsToDisplay+"] ID["+this.toString()+"]");
		firstPage();
	}
	
	@SuppressWarnings("rawtypes")
	public List<SelectItem> getListaNumeroRisultati(){
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				AbstractCoreSearchForm searchForm = ((ISearchFormService)this.dataProvider).getSearch();
				if(searchForm != null) {
					return searchForm.getListaNumeroRisultati();
				}
			}
		}
				
		return Costanti.SELECT_ITEM_ENTRIES;
	}
	
	public Integer getNumeroMassimoRisultati() {
		return Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES;
	}
	
	public String getNumeroMassimoRisultatiLabel() {
		return MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES_LABEL_KEY, Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES);
	}
	
	public String getSelezionatiPrimiElementiLabel() {
		return MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.SELEZIONATI_PRIMI_X_ELEMENTI_LABEL_KEY, Costanti.SELECT_ITEM_VALORE_MASSIMO_ENTRIES);
	}

	@SuppressWarnings("rawtypes")
	public Integer getCurrentSearchSize() {
		if(this.dataProvider != null) {
			if(this.dataProvider instanceof ISearchFormService){
				if(((ISearchFormService)this.dataProvider).getSearch() != null)
					this.currentSearchSize = ((ISearchFormService)this.dataProvider).getSearch().getCurrentSearchSize();
			}
		}
		return this.currentSearchSize;
	}

	public void setCurrentSearchSize(Integer currentSearchSize) {
		this.currentSearchSize = currentSearchSize;
	}
	
	public boolean isVisualizzaSelezionePrimiElementi() {
		// se ho selezionato il valore massimo disponibile nella tendina allora non ha senso visualizzare il link 'visualizza i primi X elementi'
		if(this.getRowsToDisplay().intValue() == this.getNumeroMassimoRisultati().intValue())
			return false;
		
		// si visualizza la selezione se sono almeno in pagina 2, oppure se sono in pagina 1 e presumo di avere almeno un'altra pagina
		if(this.getCurrentPage()==null) {
			return false;
		}
		int currentPage = this.getCurrentPage().intValue();
		if(currentPage > 1 || 
				(currentPage == 1 && this.getCurrentSearchSize()!=null && this.getRowsToDisplay()!=null && (this.getCurrentSearchSize().intValue() == this.getRowsToDisplay().intValue())))
			return true;

		return false;
	}
	
	public void setVisualizzaSelezionePrimiElementi(boolean b) {}
}
