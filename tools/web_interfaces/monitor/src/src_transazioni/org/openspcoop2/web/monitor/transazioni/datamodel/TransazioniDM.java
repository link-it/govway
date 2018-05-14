package org.openspcoop2.web.monitor.transazioni.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.expression.SortOrder;
import org.richfaces.model.Ordering;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.datamodel.SortableBaseDataModel;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;

public class TransazioniDM extends
SortableBaseDataModel<String, TransazioneBean, ITransazioniService, TransazioniSearchForm> {

	public static final String COL_DATA_INGRESSO_RICHIESTA = "dataIngressoRichiesta";
	public static final String COL_DATA_LATENZA_TOTALE = "latenzaTotale";
	public static final String COL_DATA_LATENZA_SERVIZIO = "latenzaServizio";

	private static final long serialVersionUID = 8645779291901248165L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private transient BaseSearchForm search = null;

	public TransazioniDM() {
	}


	public BaseSearchForm getSearch() {
		return this.search;
	}
	public void setSearch(BaseSearchForm search) {
		this.search = search;

		if(this.search != null && this.search.getSortField()== null)
			this.search.setSortField(this.getDefaultSortField());
	}

	@Override
	public String getId(TransazioneBean object) {
		if(object != null)
			return object.getIdTransazione();

		return null;
	}
	@Override
	protected List<TransazioneBean> findObjects(int start, int limit,	String sortField, SortOrder sortOrder) {
		List<TransazioneBean> list = new ArrayList<TransazioneBean>();
		try{
			log.debug("findObjects Start["+start+"], Limit["+limit+"], SortField["+sortField+"], SortOrder["+sortOrder.toString()+"]...");
			list = this.getDataProvider().findAll(start, limit,sortOrder ,sortField);

			log.debug("findObjects trovati["+(list != null ? list.size() : 0)+"].");
		}
		catch(Exception e){
			log.error("Errore durante la find transazioni: "+e.getMessage(),e);
		}

		return list;
	}

	@Override
	protected int _executeTotalCountWithIService(ISearchFormService<TransazioneBean, String, TransazioniSearchForm> service) throws Exception {
		if (service.getSearch().isUseCount()) {
			if(service.getSearch().isExecuteQuery()) {
				log.debug("Execute Count, SortField["+this.getSortField()+"], SortOrder["+this.getSortOrder().toString()+"]...");
				int count = ((ITransazioniService)service).totalCount(this.getSortOrder(), this.getSortField());
				log.debug("Execute Count ["+count+"]."); 
				return count;
			}else 
				return 0;
		} else return 25;
		//		return super._executeTotalCountWithIService(service);
	}

	@Override
	public String getDefaultSortField() {
		return COL_DATA_INGRESSO_RICHIESTA;
	}
	@Override
	public SortOrder getSortOrder() {
		return this.search != null ? this.search.getSortOrder() : SortOrder.DESC;
	}
	@Override
	public void setSortOrder(SortOrder sortOrder) {
		if(this.search != null)
			this.search.setSortOrder(sortOrder);
	}
	@Override
	public String getSortField() {
		return this.search != null ? this.search.getSortField() : this.getDefaultSortField();
	}
	@Override
	public void setSortField(String sortField) {
		if(this.search != null)
			this.search.setSortField(sortField);
	}

	@Override
	public Map<String, Ordering> getSortOrders() {
		if(this.search != null)
			return this.search.getSortOrders();

		return null;
	}

	@Override
	public void setSortOrders(Map<String, Ordering> sortOrders) {
		if(this.search != null)
			this.search.setSortOrders(sortOrders);
	}

	public boolean isExecuteQuery(){
		return ((TransazioniSearchForm)this.getDataProvider().getSearch()).isExecuteQuery();
	}	
}
