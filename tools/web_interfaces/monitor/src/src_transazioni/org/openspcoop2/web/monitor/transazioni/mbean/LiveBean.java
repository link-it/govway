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
package org.openspcoop2.web.monitor.transazioni.mbean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.DynamicPdDBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;

/**
 * LiveBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class LiveBean extends DynamicPdDBean<Transazione,String,ISearchFormService<TransazioneBean, String, TransazioniSearchForm>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private transient Logger log =  LoggerManager.getPddMonitorCoreLogger();

//	private transient TransazioniSearchForm search; 

	private boolean pollEnabled =true;
	
	private Date serverDate = null;
	
	private List<TransazioneBean> list = null;

	public void setServerDate(String serverDate) {
		if(this.serverDate == null)
			if("Live".equals(this.search.getPeriodo())){
				Calendar c = Calendar.getInstance();
				this.serverDate = c.getTime();
			}
	}

	
	@Override
	public void setSearch(BaseSearchForm search) {
		this.search = search;
		
		if (this.search instanceof TransazioniSearchForm)
			((TransazioniSearchForm) this.search)
			.setTransazioniService(((ITransazioniService)this.service));
	}

	public boolean isPollEnabled() {
		return this.pollEnabled;
	}

	public void setPollEnabled(boolean pollEnabled) {
		this.pollEnabled = pollEnabled;
	}

	public String getServerDate(){
		if("Live".equals(this.search.getPeriodo())){
			
			if(this.serverDate != null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return sdf.format(this.serverDate);
			}
		}
		return "";
	}
	
	public List<TransazioneBean> getLiveData(){
		((TransazioniSearchForm) this.search)
		.setTransazioniService(((ITransazioniService)this.service)) ;
				
		if(this.search.getPeriodo().equals("Live")){
			this.log.debug("Chiamata getLiveData PollEnabled["+this.serverDate+"]"); 
			
			if((this.serverDate != null || this.list == null) && this.search.isSessioneLiveValida()){
				this.log.debug("Chiamata getLiveData Eseguo Query Data Esecuzione["+this.serverDate+"]");
				
				if(this.search.validaSezioneDatiMittente()==false) {
					return new ArrayList<TransazioneBean>();
				}
				
				this.list =  (((ITransazioniService)this.service)).findAllLive();
				this.serverDate = null;
			}

			return this.list;
		}
		return new ArrayList<TransazioneBean>();
	}
	
	public boolean isTimeoutEvent(){
		return (((ITransazioniService)this.service)).isTimeoutEvent();
	}
	
	public String submit() {
		this.list = null;
		
		return "transazioni";
	}
}
