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
package org.openspcoop2.web.monitor.statistiche.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.web.monitor.core.datamodel.BaseDataModel;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.NumeroDimensioni;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerSoggettoBean;
import org.slf4j.Logger;

/**
 * DistribuzioneSoggettoDM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DistribuzioneSoggettoDM extends BaseDataModel<String, ResDistribuzione, IStatisticheGiornaliere> {

	private static final long serialVersionUID = 500153520162806619L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private transient StatsSearchForm search;
	private boolean visualizzaComandiExport = false;
	
	public void setSearch(StatsSearchForm search) {
		this.search = search;
	}
	
	@Override
	public int getRowCount() {
		try {
			this.visualizzaComandiExport = false;
			int count = this.getDataProvider().countAllDistribuzioneSoggetto();
			
			if(count > 0)
				this.visualizzaComandiExport = true;
			
			return count;
			
		} catch (ServiceException e) {
			DistribuzioneSoggettoDM.log.error(e.getMessage(), e);
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

				this.wrappedKeys = new ArrayList<>();
				
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.search.getNumeroDimensioni())) {
					this.search.setSortOrder(SortOrder.DESC);
				}
				List<ResDistribuzione> list =  new ArrayList<>();
				
				try {
					list =  this.getDataProvider().findAllDistribuzioneSoggetto(start, limit);
				} catch (ServiceException e) {
					DistribuzioneSoggettoDM.log.error(e.getMessage(), e);
				}
				
				DistribuzionePerSoggettoBean.calcolaLabels(list, ((StatisticheGiornaliereService) this.getDataProvider()).getDistribSoggettoSearch().getProtocollo());
				
				for (ResDistribuzione r : list) {
					this.wrappedData.put(r.getRisultato(), r);
					this.wrappedKeys.add(r.getRisultato());
					visitor.process(context, r.getRisultato(), argument);
				}
			}
		} catch (Exception e) {
			DistribuzioneSoggettoDM.log.error(e.getMessage(), e);
		}

	}
	
	public boolean isVisualizzaComandiExport() {
		return this.visualizzaComandiExport;
	}

	public void setVisualizzaComandiExport(boolean visualizzaComandiExport) {
		this.visualizzaComandiExport = visualizzaComandiExport;
	}
	
	public boolean isTimeoutEvent(){
		return this.getDataProvider().isTimeoutEvent();
	}
}
