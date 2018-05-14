package org.openspcoop2.web.monitor.statistiche.datamodel;

import java.util.List;

import org.openspcoop2.generic_project.expression.SortOrder;

import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.dao.IConfigurazioniGeneraliService;

public class ConfigurazioniDM extends BaseDataModelWithSearchForm<ConfigurazioneGeneralePK, ConfigurazioneGenerale, 
	IConfigurazioniGeneraliService,ConfigurazioniGeneraliSearchForm>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected List<ConfigurazioneGenerale> findObjects(int start, int limit, String sortField, SortOrder sortOrder) {
		return this.getDataProvider().findAll(start, limit);
	}
	
	@Override
	public ConfigurazioneGeneralePK getId(ConfigurazioneGenerale object) {
		if(object != null) 
			return object.getId();  
		return null;
	}

	public boolean isExecuteQuery(){
		return ((ConfigurazioniGeneraliSearchForm)this.getDataProvider().getSearch()).isExecuteQuery();
	}	
}
