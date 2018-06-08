package org.openspcoop2.web.monitor.statistiche.report;

import java.util.Date;

import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.report.ILiveReport;
import org.openspcoop2.web.monitor.statistiche.dao.IStatisticheGiornaliere;
import org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService;

import org.slf4j.Logger;

/****
 * 
 * Restituisce gli esiti delle transazioni calcolandoli in base ai dati salvati nella tabella delle statistiche.
 * 
 * @author pintori
 *
 */
public class StatisticaReportManager implements ILiveReport {

	private IStatisticheGiornaliere statService = null;
	
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	public StatisticaReportManager(){
		try{
			this.statService = new StatisticheGiornaliereService();
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,String periodo, String esitoContesto,String protocollo) {
		return  this.statService.getEsiti(permessiUtente, min, max,periodo,esitoContesto,protocollo);
	}
}
