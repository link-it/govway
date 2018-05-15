package org.openspcoop2.monitor.engine.statistic;

/**
 * StatisticsLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsLibrary {

	public static void generate(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM){
		try{
			
			if(config.isStatisticheOrarie()){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche orarie ....");
				}
				StatisticheOrarie sg = new StatisticheOrarie( config.getLogCore(), config.isDebug(), 
						config.isGenerazioneStatisticheCustom(),
						config.isAnalisiTransazioniCustom(),
						config.getForceIndexConfig(),
						statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM );
				sg.generaStatistiche( config.isStatisticheOrarie_gestioneUltimoIntervallo() );
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche orarie  terminata");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per generazione statistiche orarie disabilitato");
				}
			}
			
			if(config.isStatisticheGiornaliere()){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche giornaliere ....");
				}
				StatisticheGiornaliere sg = new StatisticheGiornaliere( config.getLogCore(), config.isDebug(), 
						config.isGenerazioneStatisticheCustom(),
						config.isAnalisiTransazioniCustom(),
						config.getForceIndexConfig(),
						statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM );
				sg.generaStatistiche( config.isStatisticheGiornaliere_gestioneUltimoIntervallo() );
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche giornaliere  terminata");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per generazione statistiche giornaliere disabilitato");
				}
			}
			
			if(config.isStatisticheSettimanali()){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche settimanali ....");
				}
				StatisticheSettimanali sg = new StatisticheSettimanali( config.getLogCore(), config.isDebug(), 
						config.isGenerazioneStatisticheCustom(),
						config.isAnalisiTransazioniCustom(),
						config.getForceIndexConfig(),
						statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM );
				sg.generaStatistiche( config.isStatisticheSettimanali_gestioneUltimoIntervallo() );
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche settimanali  terminata");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per generazione statistiche settimanali disabilitato");
				}
			}
			
			if(config.isStatisticheMensili()){
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche mensili ....");
				}
				StatisticheMensili sg = new StatisticheMensili( config.getLogCore(), config.isDebug(), 
						config.isGenerazioneStatisticheCustom(),
						config.isAnalisiTransazioniCustom(),
						config.getForceIndexConfig(),
						statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM );
				sg.generaStatistiche( config.isStatisticheMensili_gestioneUltimoIntervallo() );
				if(config.isDebug()){
					config.getLogCore().debug("Esecuzione thread per generazione statistiche mensili  terminata");
				}
			}else{
				if(config.isDebug()){
					config.getLogCore().debug("Thread per generazione statistiche mensili disabilitato");
				}
			}
			
		}catch(Exception e){
			config.getLogCore().error("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
		} 
	}
	
}
