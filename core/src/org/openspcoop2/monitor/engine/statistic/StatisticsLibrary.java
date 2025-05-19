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
package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerBase;

/**
 * StatisticsLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsLibrary {

	private StatisticsConfig config;
	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;
	private org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM;
	private org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM;
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM;
	
	private static final String ERRORE = "Errore durante la generazione delle statistiche: ";
	
	public StatisticsLibrary(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM){
		this.config = config;
		this.statisticheSM = statisticheSM;
		this.transazioniSM = transazioniSM;
		this.pluginsStatisticheSM = pluginsStatisticheSM;
		this.pluginsBaseSM = pluginsBaseSM;
		this.utilsSM = utilsSM;
		this.pluginsTransazioniSM = pluginsTransazioniSM;
	}
	
	public void close() {
		if(this.statisticheSM instanceof JDBCServiceManagerBase) {
			closeEngine((JDBCServiceManagerBase)this.statisticheSM,"Statistiche");
		}
		if(this.transazioniSM instanceof JDBCServiceManagerBase) {
			closeEngine((JDBCServiceManagerBase)this.transazioniSM,"Transazioni");
		}
		if(this.utilsSM instanceof JDBCServiceManagerBase) {
			closeEngine((JDBCServiceManagerBase)this.utilsSM,"Utils");
		}
		if(this.pluginsBaseSM instanceof JDBCServiceManagerBase) {
			closeEngine((JDBCServiceManagerBase)this.pluginsBaseSM,"PluginBase");
		}
		if(this.pluginsStatisticheSM instanceof JDBCServiceManagerBase) {
			closeEngine((JDBCServiceManagerBase)this.pluginsStatisticheSM,"PluginStatistiche");
		}
		if(this.pluginsTransazioniSM instanceof JDBCServiceManagerBase) {
			closeEngine((JDBCServiceManagerBase)this.pluginsTransazioniSM,"PluginTransazioni");
		}
	}
	private void closeEngine(JDBCServiceManagerBase serviceManager, String tipo) {
		try {
			serviceManager.close();
		}catch(Exception t) {
			String msgError = "Rilascio connessione '"+tipo+"' fallita: "+t.getMessage();
			if(this.config.getLogSql()!=null) {
				this.config.getLogSql().error(msgError, t);
			}
			else if(this.config.getLogCore()!=null) {
				this.config.getLogCore().error(msgError, t);
			}
			else {
				t.printStackTrace(System.err);
			}
		}
	}
	
	public void generateStatistics(IStatisticsEngine engine, String name) {
		try{
			
			if(engine.isEnabled(this.config)){
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread {} ....", name);
				}
				engine.init( this.config,
						this.statisticheSM, this.transazioniSM, 
						this.pluginsStatisticheSM, this.pluginsBaseSM, this.utilsSM, this.pluginsTransazioniSM );
				engine.generate();
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread {} terminata", name);
				}
			}else{
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Thread {} disabilitato", name);
				}
			}
			
		}catch(Exception e){
			String msg = ERRORE+e.getMessage();
			this.config.getLogCore().error(msg,e);
		} 
	}

	
	public void generateStatisticaOraria(){
		generateStatistics(new StatisticheOrarie(), "per generazione statistiche orarie");
	}
	
	public void generateStatisticaGiornaliera(){
		generateStatistics(new StatisticheGiornaliere(), "per generazione statistiche giornaliere");
	}
			
	public void generateStatisticaSettimanale(){
		generateStatistics(new StatisticheSettimanali(), "per generazione statistiche settimanali");
	}
			
	public void generateStatisticaMensile(){
		generateStatistics(new StatisticheMensili(), "per generazione statistiche mensili");
	}
	
}
