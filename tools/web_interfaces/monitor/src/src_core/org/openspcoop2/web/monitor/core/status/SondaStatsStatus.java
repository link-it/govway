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
package org.openspcoop2.web.monitor.core.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoServiceSearch;
import org.openspcoop2.monitor.engine.constants.SondaStatus;
import org.openspcoop2.monitor.engine.status.IStatus;
import org.openspcoop2.monitor.engine.status.StatusUtilities;
import org.openspcoop2.pdd.services.connector.CheckStatoPdDHealthCheckStats;
import org.openspcoop2.pdd.services.connector.CheckStatoPdDHealthCheckStatsUtils;
import org.slf4j.Logger;

/**
 * SondaStatsStatus
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SondaStatsStatus extends BaseSondaPdd implements ISondaPdd{

		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	public SondaStatsStatus(String identificativo, Logger log, Properties prop) throws Exception{
		super(identificativo, log, prop);
	}
	
	private boolean readBoolendSondaProperty(String nomeProprieta, boolean defaultV) {
		String p = this.propertiesSonda.getProperty(nomeProprieta);
		if(p!=null) {
			return "true".equals(p.trim()); // default false
		}
		return defaultV;
	}
	private int readIntSondaProperty(String nomeProprieta, int defaultV) {
		String p = this.propertiesSonda.getProperty(nomeProprieta);
		if(p!=null) {
			return Integer.valueOf(p); 
		}
		return defaultV;
	}
	
	@Override
	protected void init() throws Exception {
		try{
			this.log.debug("Init Sonda Stats Status in corso...");
			this.listaStatus = new ArrayList<IStatus>();

			boolean verificaStatisticaOraria = readBoolendSondaProperty("executeHourlyHealthCheckStats", true);
			boolean verificaStatisticaGiornaliera = readBoolendSondaProperty("executeDailyHealthCheckStats", true);
			boolean verificaStatisticaSettimanale = readBoolendSondaProperty("executeWeeklyHealthCheckStats", false);
			boolean verificaStatisticaMensile = readBoolendSondaProperty("executeMonthlyHealthCheckStats", false);
		
			if(verificaStatisticaOraria) {
				CheckStatoPdDHealthCheckStats hourlyCheck = new CheckStatoPdDHealthCheckStats();
				hourlyCheck.setVerificaStatisticaOraria(verificaStatisticaOraria);
				int verificaStatisticaOrariaSoglia = readIntSondaProperty("hourlyHealthCheckStatsThreshold",1);
				hourlyCheck.setVerificaStatisticaOrariaSoglia(verificaStatisticaOrariaSoglia);
				String hourlyCheckName = this.propertiesSonda.getProperty("hourlyHealthCheckStatsName");
				if(hourlyCheckName==null || StringUtils.isEmpty(hourlyCheckName)) {
					throw new CoreException("Property 'hourlyHealthCheckStatsName' undefined");
				}
				
				StatsStatus pddStat = new StatsStatus();
				pddStat.setNome(hourlyCheckName);
				pddStat.setCheck(hourlyCheck);
				this.listaStatus.add(pddStat);
			}
			
			if(verificaStatisticaGiornaliera) {
				CheckStatoPdDHealthCheckStats dailyCheck = new CheckStatoPdDHealthCheckStats();
				dailyCheck.setVerificaStatisticaGiornaliera(verificaStatisticaGiornaliera);
				int verificaStatisticaGiornalieraSoglia = readIntSondaProperty("dailyHealthCheckStatsThreshold",1);
				dailyCheck.setVerificaStatisticaGiornalieraSoglia(verificaStatisticaGiornalieraSoglia);
				String dailyCheckName = this.propertiesSonda.getProperty("dailyHealthCheckStatsName");
				if(dailyCheckName==null || StringUtils.isEmpty(dailyCheckName)) {
					throw new CoreException("Property 'dailyHealthCheckStatsName' undefined");
				}
				
				StatsStatus pddStat = new StatsStatus();
				pddStat.setNome(dailyCheckName);
				pddStat.setCheck(dailyCheck);
				this.listaStatus.add(pddStat);
			}
			
			if(verificaStatisticaSettimanale) {
				CheckStatoPdDHealthCheckStats weeklyCheck = new CheckStatoPdDHealthCheckStats();
				weeklyCheck.setVerificaStatisticaSettimanale(verificaStatisticaSettimanale);
				int verificaStatisticaSettimanaleSoglia = readIntSondaProperty("weeklyHealthCheckStatsThreshold",1);
				weeklyCheck.setVerificaStatisticaSettimanaleSoglia(verificaStatisticaSettimanaleSoglia);
				String weeklyCheckName = this.propertiesSonda.getProperty("weeklyHealthCheckStatsName");
				if(weeklyCheckName==null || StringUtils.isEmpty(weeklyCheckName)) {
					throw new CoreException("Property 'weeklyHealthCheckStatsName' undefined");
				}
				
				StatsStatus pddStat = new StatsStatus();
				pddStat.setNome(weeklyCheckName);
				pddStat.setCheck(weeklyCheck);
				this.listaStatus.add(pddStat);
			}
			
			if(verificaStatisticaMensile) {
				CheckStatoPdDHealthCheckStats monthlyCheck = new CheckStatoPdDHealthCheckStats();
				monthlyCheck.setVerificaStatisticaMensile(verificaStatisticaMensile);
				int verificaStatisticaMensileSoglia = readIntSondaProperty("monthlyHealthCheckStatsThreshold",1);
				monthlyCheck.setVerificaStatisticaMensileSoglia(verificaStatisticaMensileSoglia);
				String monthlyCheckName = this.propertiesSonda.getProperty("monthlyHealthCheckStatsName");
				if(monthlyCheckName==null || StringUtils.isEmpty(monthlyCheckName)) {
					throw new CoreException("Property 'monthlyHealthCheckStatsName' undefined");
				}
				
				StatsStatus pddStat = new StatsStatus();
				pddStat.setNome(monthlyCheckName);
				pddStat.setCheck(monthlyCheck);
				this.listaStatus.add(pddStat);
			}

			this.log.debug("Init Sonda Pdd Status completato.");

		}catch(Exception e){
			this.logError("Si e' verificato un errore durante l'Init Sonda Pdd Status: " + e.getMessage(),e); 
			throw e;
		}
	}



	@Override
	public List<IStatus> updateStato() throws Exception {

		for (IStatus status : this.listaStatus) {
			updateStato(status, this.log);
		}
		
		return this.listaStatus; 
		
	}
	private void updateStato( IStatus status, Logger log ) {

		try{
			CheckStatoPdDHealthCheckStats check = null;
			if(status instanceof StatsStatus) {
				check = ((StatsStatus)status).getCheck();
			}
			else {
				throw new CoreException("Tipo sonda '"+status.getClass().getName()+"' sconosciuto");
			}
			
			org.openspcoop2.core.statistiche.dao.IServiceManager serviceManager =  (org.openspcoop2.core.statistiche.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),log);
			IStatisticaInfoServiceSearch search = serviceManager.getStatisticaInfoServiceSearch();
			
			CheckStatoPdDHealthCheckStatsUtils.verificaInformazioniStatistiche(log, search, check);
			
			status.setStato(SondaStatus.OK);
			status.setDescrizione(null);
		}catch(Exception e){
			status.setStato(SondaStatus.WARNING);
			status.setDescrizione(e.getMessage());
			log.error("Verifica '"+status.getNome()+"' fallita: "+e.getMessage(),e);
		}

	}

	@Override
	public String getMessaggioStatoSondaPdd() throws Exception {
		int totOk = StatusUtilities.getTotOk(this.listaStatus);
		// Tutti in errore
		if(totOk == 0){
			return "Dati non aggiornati";
		}else {
			// parzialmente in errore
			if(totOk< this.listaStatus.size()){
				if(this.listaStatus.size()>2) {
					String numero = (this.listaStatus.size() - totOk) + " su " +this.listaStatus.size();
					return "Dati parzialmente aggiornati ("+numero+")";
				}
				else {
					return "Dati parzialmente aggiornati";
				}
			}else {
				// tutti ok
				return "Dati aggiornati";
			}
		}
	}

	@Override
	public SondaStatus getStatoSondaPdd() throws Exception {
		return StatusUtilities.statesProcess(this.listaStatus);
	}

}
