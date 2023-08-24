/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.report;

import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/***
 * 
 * Classe di Factory per la gestione dei generatori di report Live.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ReportFactory {

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static ReportFactory staticInstance = null;

	private ILiveReport transazioniReportManager = null;
	private ILiveReport statisticaReportManager = null;

	public static ReportFactory getInstance(){
		if (ReportFactory.staticInstance == null) {
			init();
		}

		return ReportFactory.staticInstance;
	}

	private static synchronized void init(){
		if (ReportFactory.staticInstance == null) {
			ReportFactory.staticInstance = new ReportFactory();
		}
	}

	public ReportFactory (){
		try{
			log.debug("Init ReportFactory in corso...");
			//		...			
			log.debug("Init ReportFactory completato.");
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	public ILiveReport getTransazioniReportManager(){
		if(this.transazioniReportManager == null)
			initTransazioniReportManagerEngine();

		return this.transazioniReportManager;
	}

	private synchronized void initTransazioniReportManagerEngine() {
		try{
			log.debug("Init Report Manager Transazioni in corso...");
			if(this.transazioniReportManager == null){
				this.transazioniReportManager = (ILiveReport) Loader.getInstance().newInstance("org.openspcoop2.web.monitor.transazioni.report.TransazioniReportManager");
				
				/**FacesContext currentInstance = FacesContext.getCurrentInstance();
				ApplicationContext context = FacesContextUtils.getWebApplicationContext(currentInstance);
				if(context!=null) {
					this.transazioniReportManager = (ILiveReport) context.getBean("transazioniReportManager");
				}*/
			}
			log.debug("Init Report Manager Transazioni completato.");
		}catch(Exception e){
			log.debug("Errore durante la creazione del Report Manager Transazioni:" + e.getMessage(),e);
		}
	}

	public ILiveReport getStatisticaReportManager(){
		if(this.statisticaReportManager == null){
			initStatisticaReportManagerEngine();
		}
		return this.statisticaReportManager;
	}
	
	private synchronized void initStatisticaReportManagerEngine() {
		try{
			log.debug("Init Report Manager Statistiche in corso...");
			if(this.statisticaReportManager == null){
				
				this.statisticaReportManager = (ILiveReport) Loader.getInstance().newInstance("org.openspcoop2.web.monitor.statistiche.report.StatisticaReportManager");
				
				/**FacesContext currentInstance = FacesContext.getCurrentInstance();
				ApplicationContext context = FacesContextUtils.getWebApplicationContext(currentInstance);
				if(context!=null) {
					this.statisticaReportManager = (ILiveReport) context.getBean("statisticaReportManager");
				}*/
			}
			log.debug("Init Report Manager Statistiche completato.");
		}catch(Exception e){
			log.debug("Errore durante la creazione del Report Manager Statistiche:" + e.getMessage(),e);
		}
	}
}
