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
package org.openspcoop2.monitor.engine.statistic;

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
	private org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM;
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM;
	
	public StatisticsLibrary(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM,
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
	
	public void generateStatisticaOraria(){
		try{
			
			if(this.config.isStatisticheOrarie()){
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche orarie ....");
				}
				StatisticheOrarie sg = new StatisticheOrarie( this.config.getLogCore(), this.config.isDebug(), 
						this.config.isGenerazioneStatisticheCustom(),
						this.config.isAnalisiTransazioniCustom(),
						this.config.getForceIndexConfig(),
						this.statisticheSM, this.transazioniSM, 
						this.pluginsStatisticheSM, this.pluginsBaseSM, this.utilsSM, this.pluginsTransazioniSM );
				sg.generaStatistiche( this.config.isStatisticheOrarie_gestioneUltimoIntervallo() );
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche orarie  terminata");
				}
			}else{
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Thread per generazione statistiche orarie disabilitato");
				}
			}
			
		}catch(Exception e){
			this.config.getLogCore().error("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
		} 
	}
	
	public void generateStatisticaGiornaliera(){
		try{
			
			if(this.config.isStatisticheGiornaliere()){
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche giornaliere ....");
				}
				StatisticheGiornaliere sg = new StatisticheGiornaliere( this.config.getLogCore(), this.config.isDebug(), 
						this.config.isGenerazioneStatisticheCustom(),
						this.config.isAnalisiTransazioniCustom(),
						this.config.getForceIndexConfig(),
						this.statisticheSM, this.transazioniSM, 
						this.pluginsStatisticheSM, this.pluginsBaseSM, this.utilsSM, this.pluginsTransazioniSM );
				sg.generaStatistiche( this.config.isStatisticheGiornaliere_gestioneUltimoIntervallo() );
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche giornaliere  terminata");
				}
			}else{
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Thread per generazione statistiche giornaliere disabilitato");
				}
			}
			
		}catch(Exception e){
			this.config.getLogCore().error("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
		} 
	}
			
	public void generateStatisticaSettimanale(){
		try{
			if(this.config.isStatisticheSettimanali()){
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche settimanali ....");
				}
				StatisticheSettimanali sg = new StatisticheSettimanali( this.config.getLogCore(), this.config.isDebug(), 
						this.config.isGenerazioneStatisticheCustom(),
						this.config.isAnalisiTransazioniCustom(),
						this.config.getForceIndexConfig(),
						this.statisticheSM, this.transazioniSM, 
						this.pluginsStatisticheSM, this.pluginsBaseSM, this.utilsSM, this.pluginsTransazioniSM );
				sg.generaStatistiche( this.config.isStatisticheSettimanali_gestioneUltimoIntervallo() );
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche settimanali  terminata");
				}
			}else{
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Thread per generazione statistiche settimanali disabilitato");
				}
			}
		}catch(Exception e){
			this.config.getLogCore().error("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
		} 
	}
			
	public void generateStatisticaMensile(){
		try{
			if(this.config.isStatisticheMensili()){
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche mensili ....");
				}
				StatisticheMensili sg = new StatisticheMensili( this.config.getLogCore(), this.config.isDebug(), 
						this.config.isGenerazioneStatisticheCustom(),
						this.config.isAnalisiTransazioniCustom(),
						this.config.getForceIndexConfig(),
						this.statisticheSM, this.transazioniSM, 
						this.pluginsStatisticheSM, this.pluginsBaseSM, this.utilsSM, this.pluginsTransazioniSM );
				sg.generaStatistiche( this.config.isStatisticheMensili_gestioneUltimoIntervallo() );
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Esecuzione thread per generazione statistiche mensili  terminata");
				}
			}else{
				if(this.config.isDebug()){
					this.config.getLogCore().debug("Thread per generazione statistiche mensili disabilitato");
				}
			}
			
		}catch(Exception e){
			this.config.getLogCore().error("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
		} 
	}
	
}
