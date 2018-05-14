package org.openspcoop2.web.monitor.core.report;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/***
 * 
 * Classe di Factory per la gestione dei generatori di report Live.
 * 
 * @author pintori
 *
 */
public class ReportFactory {

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static ReportFactory _instance = null;

	private transient ILiveReport transazioniReportManager = null;
	private transient ILiveReport statisticaReportManager = null;

	public static ReportFactory getInstance(){
		if (ReportFactory._instance == null) {
			init();
		}

		return ReportFactory._instance;
	}

	private static synchronized void init(){
		if (ReportFactory._instance == null) {
			ReportFactory._instance = new ReportFactory();
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
			_initTransazioniReportManager();

		return this.transazioniReportManager;
	}

	private synchronized void _initTransazioniReportManager() {
		try{
			log.debug("Init Report Manager Transazioni in corso...");
			if(this.transazioniReportManager == null){
				FacesContext currentInstance = FacesContext.getCurrentInstance();
				ApplicationContext context = FacesContextUtils.getWebApplicationContext(currentInstance);
				this.transazioniReportManager = (ILiveReport) context.getBean("transazioniReportManager");
			}
			log.debug("Init Report Manager Transazioni completato.");
		}catch(Exception e){
			log.debug("Errore durante la creazione del Report Manager Transazioni:" + e.getMessage(),e);
		}
	}

	public ILiveReport getStatisticaReportManager(){
		if(this.statisticaReportManager == null){
			_initStatisticaReportManager();
		}
		return this.statisticaReportManager;
	}
	
	private synchronized void _initStatisticaReportManager() {
		try{
			log.debug("Init Report Manager Statistiche in corso...");
			if(this.statisticaReportManager == null){
				FacesContext currentInstance = FacesContext.getCurrentInstance();
				ApplicationContext context = FacesContextUtils.getWebApplicationContext(currentInstance);
				this.statisticaReportManager = (ILiveReport) context.getBean("statisticaReportManager");
			}
			log.debug("Init Report Manager Statistiche completato.");
		}catch(Exception e){
			log.debug("Errore durante la creazione del Report Manager Statistiche:" + e.getMessage(),e);
		}
	}
}
