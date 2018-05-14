package org.openspcoop2.web.monitor.core.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.ajax4jsf.model.SerializableDataModel;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.bean.AbstractCoreSearchForm;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;

/**
 * 
 * @author corallo
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
	protected S search;

	/**
	 * The boolean field, detached, starts as false.  
	 * It will be set to “true” when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
	 * In this manner, the model will not be updated when the jsf component is rebuilt on postback, but rather when the new model is being built.
	 */
	@Override
	public void update() {
		//nothing to do
		this.detached = false;
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
			if(this.detached){
				for (K key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			}else{
				this.checkDataProvider();
				int start = 0; int limit = 0;
				AbstractCoreSearchForm searchForm =  null;
				if(this.dataProvider instanceof ISearchFormService) {
					searchForm = ((ISearchFormService<T, K, AbstractCoreSearchForm>)this.dataProvider).getSearch();

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
							range = new SequenceRange(start,limit);
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
				}
				if(this.dataProvider instanceof IServiceSearchWithId){
					start = ((SequenceRange)range).getFirstRow();
					limit = ((SequenceRange)range).getRows();
				}

				List<T> bufferList = this.findObjects(start, limit,null,null);
				if(searchForm != null) {
					searchForm.setCurrentSearchSize( bufferList != null ?  bufferList.size() : 0);
				} 

				this.wrappedKeys = new ArrayList<K>();
				for (final T obj : bufferList) {
					this.wrappedData.put(getId(obj), obj);
					this.wrappedKeys.add(getId(obj));
					visitor.process(context,getId(obj) , argument);
				}

			}
		}catch (Exception e) {
			//			log.error(e,e);
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
		} else return 25;
	}

	/**
	 * @param firstRow
	 * @param numberOfRows
	 * @param sortField
	 * @param descending
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
	@Override
	public  SerializableDataModel getSerializableModel(Range range) {
		if (this.wrappedKeys!=null) {
			this.detached = true;
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
					this.firstEnabled = ((ISearchFormService)this.dataProvider).getSearch().getCurrentPage() > 1;
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
					this.prevEnabled = ((ISearchFormService)this.dataProvider).getSearch().getCurrentPage() > 1;
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
					this.nextEnabled = searchForm.getCurrentSearchSize() != null && searchForm.getCurrentSearchSize() == searchForm.getLimit();
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
							int start = searchForm.getStart() != null ? (searchForm.getStart() + 1) : 1;
							int end = searchForm.getStart() != null ? (searchForm.getStart() + searchForm.getCurrentSearchSize() ) : searchForm.getCurrentSearchSize();
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
}
