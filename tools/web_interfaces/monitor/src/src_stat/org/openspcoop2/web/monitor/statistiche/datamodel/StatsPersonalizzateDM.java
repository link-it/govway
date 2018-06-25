/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.web.monitor.core.datamodel.BaseDataModel;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * StatsPersonalizzateDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatsPersonalizzateDM extends BaseDataModel<String, ResDistribuzione, IStatisticheGiornaliere> {

	private static final long serialVersionUID = 500153520162806619L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private boolean visualizzaComandiExport = false;
	
	@Override
	public int getRowCount() {
		try {
			this.visualizzaComandiExport = false;
			int count = this.getDataProvider().countAllDistribuzionePersonalizzata();
			
			if(count > 0)
				this.visualizzaComandiExport = true;
			
			return count;
			
		} catch (ServiceException e) {
			StatsPersonalizzateDM.log.error(e.getMessage(), e);
			return 0;
		}
	}
	
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range,
			Object argument) throws IOException {
		try{	
			if(this.detached){
				for (String key : this.wrappedKeys) {
					setRowKey(key);
					visitor.process(context, key, argument);
				}
			}else{
				int start = ((SequenceRange)range).getFirstRow();
				int limit = ((SequenceRange)range).getRows();

				this.wrappedKeys = new ArrayList<String>();
				List<ResDistribuzione> list =  new ArrayList<ResDistribuzione>();
				
				try {
					list =  this.getDataProvider().findAllDistribuzionePersonalizzata(start, limit);
				} catch (ServiceException e) {
					StatsPersonalizzateDM.log.error(e.getMessage(), e);
				}
				
				for (ResDistribuzione r : list) {
					this.wrappedData.put(r.getRisultato(), r);
					this.wrappedKeys.add(r.getRisultato());
					visitor.process(context, r.getRisultato(), argument);
				}
			}
		} catch (Exception e) {
			StatsPersonalizzateDM.log.error(e.getMessage(), e);
		}

	}
	
	public boolean isVisualizzaComandiExport() {
		return this.visualizzaComandiExport;
	}

	public void setVisualizzaComandiExport(boolean visualizzaComandiExport) {
		this.visualizzaComandiExport = visualizzaComandiExport;
	}
	
}
