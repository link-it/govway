/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.statistiche.report;

import java.util.Date;

import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
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
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,String periodo, String esitoContesto,String protocollo, TipologiaRicerca tipologiaRicerca) {
		return  this.statService.getEsiti(permessiUtente, min, max,periodo,esitoContesto,protocollo, tipologiaRicerca);
	}
}
