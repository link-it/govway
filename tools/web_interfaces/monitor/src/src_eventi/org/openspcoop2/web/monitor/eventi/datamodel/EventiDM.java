package org.openspcoop2.web.monitor.eventi.datamodel;

import java.util.List;

import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;
import org.openspcoop2.web.monitor.eventi.bean.EventiSearchForm;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;
import org.openspcoop2.web.monitor.eventi.dao.IEventiService;

/***
 * 
 * classe DataModel per la configurazione delle sonde
 * 
 * 
 * @author pintori
 *
 */
public class EventiDM extends BaseDataModelWithSearchForm<Long, EventoBean, IEventiService, EventiSearchForm>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7168150212275378014L;

	
	@Override
	public List<EventoBean> findObjects(int start, int limit, String sortField, SortOrder sortOrder) {
		return this.getDataProvider().findAll(start, limit);
	}

	public boolean isExecuteQuery(){
		return ((EventiSearchForm)this.getDataProvider().getSearch()).isExecuteQuery();
	}
	
	@Override
	public Long getId(EventoBean object) {
		if(object != null) 
			return object.getId();
		return null;
	}
}
