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

package org.openspcoop2.web.monitor.allarmi.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiSearchForm;
import org.openspcoop2.web.monitor.allarmi.dao.IAllarmiService;
import org.openspcoop2.web.monitor.allarmi.mbean.AllarmiBean;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModelWithSearchForm;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * AllarmeHistoryDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AllarmeHistoryDM extends BaseDataModelWithSearchForm<Long, AllarmeHistory, IAllarmiService,AllarmiSearchForm>{

	private static final long serialVersionUID = 4494962270743603849L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private AllarmiBean allarmiBean = null;
	
	public AllarmiBean getAllarmiBean() {
		return this.allarmiBean;
	}

	public void setAllarmiBean(AllarmiBean allarmiBean) {
		this.allarmiBean = allarmiBean;
	}

	@Override
	protected List<AllarmeHistory> findObjects(int start, int limit, String sortField, SortOrder sortOrder) {
		try {
			return this.getDataProvider().findAllHistory(this.allarmiBean.getAllarme().getId(),start, limit);
		} catch (ServiceException e) {
			AllarmeHistoryDM.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			AllarmeHistoryDM.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmeHistoryDM.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmeHistoryDM.log.error(e.getMessage(), e);
		}
		
		return new ArrayList<AllarmeHistory>();
	}
	
	@Override
	public int getRowCount() {
		try{
			return (int) this.getDataProvider().countAllHistory(this.allarmiBean.getAllarme().getId());
		} catch (Exception e) {
			AllarmeHistoryDM.log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public Long getId(AllarmeHistory object) {
		if(object != null) 
			return object.getId();
		return null;
	}
}
