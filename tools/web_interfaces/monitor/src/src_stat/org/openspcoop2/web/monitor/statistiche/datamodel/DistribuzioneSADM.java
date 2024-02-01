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
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerSABean;
import org.slf4j.Logger;

/**
 * DistribuzioneSADM
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DistribuzioneSADM extends BaseDataModel<String, ResDistribuzione, IStatisticheGiornaliere> {

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
			int count = this.getDataProvider().countAllDistribuzioneServizioApplicativo();
			
			if(count > 0)
				this.visualizzaComandiExport = true;
			
			return count;
			
		} catch (ServiceException e) {
			DistribuzioneSADM.log.error(e.getMessage(), e);
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

				StatisticheGiornaliereService sgs = null;
				if(this.getDataProvider() instanceof StatisticheGiornaliereService) {
					sgs = (StatisticheGiornaliereService) this.getDataProvider();
				}
				
				boolean countApplicativo = false;
				/**System.out.println("===== WALK =====");
				System.out.println("PRIMA offset["+start+"] limit["+limit+"]");*/
				if((sgs!=null && sgs.getDistribSaSearch()!=null && !sgs.getDistribSaSearch().isUseCount())) {
					countApplicativo = true;
					start = sgs.getDistribSaSearch().getStart();
					limit = sgs.getDistribSaSearch().getLimit();
				}
				/**System.out.println("DOPO countApplicativo["+countApplicativo+"] offset["+start+"] limit["+limit+"]");*/
				
				this.wrappedKeys = new ArrayList<>();
				
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.search.getNumeroDimensioni())) {
					this.search.setSortOrder(SortOrder.DESC);
				}
				List<ResDistribuzione> list =  new ArrayList<>();
				
				try {
					list =  this.getDataProvider().findAllDistribuzioneServizioApplicativo(start, limit);
				} catch (ServiceException e) {
					DistribuzioneSADM.log.error(e.getMessage(), e);
				}
				
				DistribuzionePerSABean.calcolaLabels(list, ((StatisticheGiornaliereService) this.getDataProvider()).getDistribSaSearch().getProtocollo(),((StatisticheGiornaliereService) this.getDataProvider()).getDistribSaSearch());
				
				if(countApplicativo) {
					this.currentSearchSize = list != null ?  list.size() : 0;
					this.search.setCurrentSearchSize(this.currentSearchSize);
					/**System.out.println("currentSearchSize["+this.currentSearchSize+"]");*/
				}
				
				for (ResDistribuzione r : list) {
					this.wrappedData.put(r.getRisultato(), r);
					this.wrappedKeys.add(r.getRisultato());
					visitor.process(context, r.getRisultato(), argument);
				}
			}
		} catch (Exception e) {
			DistribuzioneSADM.log.error(e.getMessage(), e);
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
	
	/** Metodi richiesto dal dataTable quando disabilito setUseCount tramite metodo setTipoReport in StatsSearchForm */
	
	public boolean isFirstEnabled() {
		/**System.out.println("===== isFirstEnabled =====");*/
		
		StatisticheGiornaliereService sgs = null;
		if(this.getDataProvider() instanceof StatisticheGiornaliereService) {
			sgs = (StatisticheGiornaliereService) this.getDataProvider();
		}
		
		if(this.search!=null && 
				sgs!=null && sgs.getDistribSaSearch()!=null && !sgs.getDistribSaSearch().isUseCount()) {
			/**System.out.println("START ["+sgs.getDistribSaSearch().getStart()+"]");*/
			return sgs.getDistribSaSearch().getStart()!=null && sgs.getDistribSaSearch().getStart().intValue()>0;
		}

		return false;
	}
	
	public boolean isPrevEnabled() {
		return this.isFirstEnabled();
	}
	
	public boolean isNextEnabled() {
		StatisticheGiornaliereService sgs = null;
		if(this.getDataProvider() instanceof StatisticheGiornaliereService) {
			sgs = (StatisticheGiornaliereService) this.getDataProvider();
		}
		
		if(this.search != null && 
				sgs!=null && sgs.getDistribSaSearch()!=null && !sgs.getDistribSaSearch().isUseCount()) {
			return this.search.getCurrentSearchSize() != null && this.search.getLimit()!=null && 
					this.search.getCurrentSearchSize().intValue() == this.search.getLimit().intValue();
		}

		return false;
	}
	
	public String getRecordLabel() {
		return null;
	}
	
	public String nextPage() {
		
		/**System.out.println("===== nextPage =====");*/
		
		StatisticheGiornaliereService sgs = null;
		if(this.getDataProvider() instanceof StatisticheGiornaliereService) {
			sgs = (StatisticheGiornaliereService) this.getDataProvider();
		}
		
		if(sgs!=null && sgs.getDistribSaSearch()!=null && !sgs.getDistribSaSearch().isUseCount() &&
				sgs.getDistribSaSearch().getStart()!=null && sgs.getDistribSaSearch().getLimit()!=null) {
			int start = sgs.getDistribSaSearch().getStart().intValue();
			int limit = sgs.getDistribSaSearch().getLimit().intValue();
			int newStart = start + limit;
			sgs.getDistribSaSearch().setStart(newStart);
		}
		
		Integer currentPage = this.getCurrentPage();
		if(currentPage != null)
			this.setCurrentPage(currentPage + 1);
		else 
			this.setCurrentPage(1);
		/**System.out.println("currentPage ["+getCurrentPage()+"]");*/
		
		this.update();
		
		return null;
	}

	public String prevPage() {
		
		/**System.out.println("===== prevPage =====");*/
		
		StatisticheGiornaliereService sgs = null;
		if(this.getDataProvider() instanceof StatisticheGiornaliereService) {
			sgs = (StatisticheGiornaliereService) this.getDataProvider();
		}
		
		if(sgs!=null && sgs.getDistribSaSearch()!=null && !sgs.getDistribSaSearch().isUseCount() &&
				sgs.getDistribSaSearch().getStart()!=null && sgs.getDistribSaSearch().getLimit()!=null) {
			int start = sgs.getDistribSaSearch().getStart().intValue();
			int limit = sgs.getDistribSaSearch().getLimit().intValue();
			int newStart = start - limit;
			sgs.getDistribSaSearch().setStart(newStart);
		}
		
		Integer currentPage = this.getCurrentPage();
		if(currentPage != null)
			this.setCurrentPage(currentPage - 1);
		else 
			this.setCurrentPage(1);
		/**System.out.println("currentPage ["+getCurrentPage()+"]");*/

		this.update();
		 
		return null;
	}

	public String firstPage() {
		
		/**System.out.println("===== firstPage =====");*/
		
		StatisticheGiornaliereService sgs = null;
		if(this.getDataProvider() instanceof StatisticheGiornaliereService) {
			sgs = (StatisticheGiornaliereService) this.getDataProvider();
		}
		
		if(sgs!=null && sgs.getDistribSaSearch()!=null && !sgs.getDistribSaSearch().isUseCount() &&
				sgs.getDistribSaSearch().getStart()!=null && sgs.getDistribSaSearch().getLimit()!=null) {
			sgs.getDistribSaSearch().setStart(0);
		}
		
		this.setCurrentPage(1);
		/**System.out.println("currentPage dopo ["+getCurrentPage()+"]");*/
		
		this.update();
		
		return null;
	}
	
}
