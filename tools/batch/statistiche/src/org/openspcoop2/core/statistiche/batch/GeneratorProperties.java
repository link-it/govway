package org.openspcoop2.core.statistiche.batch;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.monitor.engine.statistic.StatisticsForceIndexConfig;

public class GeneratorProperties {
	
	private static GeneratorProperties staticInstance = null;
	private static synchronized void init() throws Exception{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.staticInstance = new GeneratorProperties();
		}
	}
	public static GeneratorProperties getInstance() throws Exception{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.init();
		}
		return GeneratorProperties.staticInstance;
	}
	
	
	
	
	private static String PROPERTIES_FILE = "/batch-statistiche.properties";
		
	private boolean statisticheGenerazioneDebug=false;
	
	private boolean statisticheGenerazioneBaseOrariaGestioneUltimaOra=false;
	private boolean statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno=false;
	private boolean statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana=false;
	private boolean statisticheGenerazioneBaseMensileGestioneUltimoMese=false;
	
	private StatisticsForceIndexConfig statisticheGenerazioneForceIndexConfig = null;
	
	private boolean generazioneStatisticheCustom = false;
	private boolean analisiTransazioniCustom = false;

	private File pddMonitorFrameworkRepositoryJars = null;
	

	
	
	public GeneratorProperties() throws Exception {

		Properties props = new Properties();
		try {
			InputStream is = GeneratorProperties.class.getResourceAsStream(GeneratorProperties.PROPERTIES_FILE);
			props.load(is);
		} catch(Exception e) {
			throw new Exception("Errore durante l'init delle properties", e);
		}
		
		// PROPERTIES
				
		this.statisticheGenerazioneDebug = this.getBooleanProperty(props, "statistiche.generazione.debug", true);
		
		this.statisticheGenerazioneBaseOrariaGestioneUltimaOra = this.getBooleanProperty(props, "statistiche.generazione.baseOraria.gestioneUltimaOra", true);
		this.statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno = this.getBooleanProperty(props, "statistiche.generazione.baseGiornaliera.gestioneUltimoGiorno", true);
		this.statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana = this.getBooleanProperty(props, "statistiche.generazione.baseSettimanale.gestioneUltimaSettimana", true);
		this.statisticheGenerazioneBaseMensileGestioneUltimoMese = this.getBooleanProperty(props, "statistiche.generazione.baseMensile.gestioneUltimoMese", true);
	
		this.statisticheGenerazioneForceIndexConfig = new StatisticsForceIndexConfig(props);
		
		this.generazioneStatisticheCustom = this.getBooleanProperty(props, "statistiche.generazione.custom.enabled", true);
		this.analisiTransazioniCustom = this.getBooleanProperty(props, "statistiche.generazione.custom.transazioniSdk.enabled", true);
		
		String tmp = this.getProperty(props, "statistiche.pddmonitorframework.sdk.repositoryJars", false);
		if(tmp!=null){
			this.pddMonitorFrameworkRepositoryJars = new File(tmp);
		}
	}
	
	private String getProperty(Properties props,String name,boolean required) throws Exception{
		String tmp = props.getProperty(name);
		if(tmp==null){
			if(required){
				throw new Exception("Property '"+name+"' not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	private boolean getBooleanProperty(Properties props,String name,boolean required) throws Exception{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp);
			}catch(Exception e){
				throw new Exception("Property '"+name+"' wrong int format: "+e.getMessage());
			}
		}
		else{
			return false;
		}
	}
	
	public boolean isStatisticheGenerazioneDebug() {
		return this.statisticheGenerazioneDebug;
	}

	public boolean isStatisticheGenerazioneBaseOrariaGestioneUltimaOra() {
		return this.statisticheGenerazioneBaseOrariaGestioneUltimaOra;
	}
	public boolean isStatisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno() {
		return this.statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno;
	}
	public boolean isStatisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana() {
		return this.statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana;
	}
	public boolean isStatisticheGenerazioneBaseMensileGestioneUltimoMese() {
		return this.statisticheGenerazioneBaseMensileGestioneUltimoMese;
	}
	
	public StatisticsForceIndexConfig getStatisticheGenerazioneForceIndexConfig() {
		return this.statisticheGenerazioneForceIndexConfig;
	}
		
	public boolean isGenerazioneStatisticheCustom() {
		return this.generazioneStatisticheCustom;
	}
	public boolean isAnalisiTransazioniCustom() {
		return this.analisiTransazioniCustom;
	}
	
	public File getPddMonitorFrameworkRepositoryJars() {
		return this.pddMonitorFrameworkRepositoryJars;
	}
}
