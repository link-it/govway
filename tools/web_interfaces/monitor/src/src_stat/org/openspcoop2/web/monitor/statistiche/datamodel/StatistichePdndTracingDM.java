/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.statistiche.datamodel;

import java.util.List;

import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingSearchForm;
import org.openspcoop2.web.monitor.statistiche.dao.IStatistichePdndTracingService;


/**
 * StatistichePdndTracingDM
 * 
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePdndTracingDM extends BaseDataModelWithSearchForm<Long, StatistichePdndTracingBean, IStatistichePdndTracingService, StatistichePdndTracingSearchForm>{

	private static final long serialVersionUID = 1L;

	@Override
	public Long getId(StatistichePdndTracingBean object) {
		if(object != null) 
			return object.getId();
		return null;
	}

	@Override
	protected List<StatistichePdndTracingBean> findObjects(int start, int limit, String sortField,
			SortOrder sortOrder) {
		return this.getDataProvider().findAll(start, limit); 
	}


	public boolean isExecuteQuery(){
		return ((StatistichePdndTracingSearchForm)this.getDataProvider().getSearch()).isExecuteQuery();
	}	
	
	public boolean isTimeoutEvent(){
		return this.getDataProvider().isTimeoutEvent();
	}
}
