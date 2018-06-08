package org.openspcoop2.web.monitor.transazioni.report;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.report.ILiveReport;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;

import org.slf4j.Logger;

/***
 * 
 * Classe per la lettura degli esiti a partire dalla tabella transazioni.
 * 
 * @author pintori
 *
 */
public class TransazioniReportManager implements ILiveReport,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient ITransazioniService transazioniService = null;
	
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	public TransazioniReportManager(){
		try{
			this.transazioniService = new TransazioniService();
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,String periodo,String esitoContesto,String protocollo) {
		return this.transazioniService.getEsiti(permessiUtente, min, max, esitoContesto,protocollo);
	}
}
