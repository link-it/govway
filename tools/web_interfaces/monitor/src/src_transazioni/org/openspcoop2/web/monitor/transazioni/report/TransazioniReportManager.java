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
package org.openspcoop2.web.monitor.transazioni.report;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
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
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
			TransazioniReportManager.log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,String periodo,String esitoContesto,String protocollo, TipologiaRicerca tipologiaRicerca) {
		return this.transazioniService.getEsiti(permessiUtente, min, max, esitoContesto,protocollo, tipologiaRicerca);
	}
}
