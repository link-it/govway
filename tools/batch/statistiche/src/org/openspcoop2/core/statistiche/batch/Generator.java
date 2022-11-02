/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.core.statistiche.batch;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.monitor.engine.statistic.StatisticsConfig;
import org.openspcoop2.monitor.engine.statistic.StatisticsLibrary;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
* Generator
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Generator {

	public static void main(String[] args) throws Exception {

		StringBuilder bf = new StringBuilder();
		String [] tipi = TipoIntervalloStatistico.toStringArray();
		if(tipi!=null) {
			for (String t : tipi) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append(t);
			}
		}
		String usage = "\n\nUse: generator.sh tipo\n\ttipo: "+bf.toString();
		
		if(args.length<=0) {
			throw new Exception("ERROR: tipo di statistica da generare non fornito"+usage);
		}
		
		String tipo = args[0].trim();
		TipoIntervalloStatistico tipoStatistica = null;
		try {
			tipoStatistica = TipoIntervalloStatistico.toEnumConstant(tipo, true);
		}catch(Exception e) {
			throw new Exception("ERROR: tipo di statistica fornita ("+tipo+") sconosciuta"+usage);
		}

		String nomeLogger = null;
		switch (tipoStatistica) {
		case STATISTICHE_ORARIE:
			nomeLogger = "statistiche_orarie";
			break;
		case STATISTICHE_GIORNALIERE:
			nomeLogger = "statistiche_giornaliere";
			break;
		case STATISTICHE_SETTIMANALI:
			nomeLogger = "statistiche_settimanali";
			break;
		case STATISTICHE_MENSILI:
			nomeLogger = "statistiche_mensili";
			break;
		}
		
		Logger logCore = null;
		Logger logSql = null;
		try{
			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(Generator.class.getResource("/batch-statistiche.log4j2.properties").getFile());
			props.load(fis);
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
			LoggerWrapperFactory.setLogConfiguration(props);
			logCore = LoggerWrapperFactory.getLogger("govway.batch."+nomeLogger+".generazione.error");
			logSql = LoggerWrapperFactory.getLogger("govway.batch."+nomeLogger+".generazione.sql.error");
		}catch(Exception e) {
			throw new Exception("Impostazione logging fallita: "+e.getMessage());
		}
		
		GeneratorProperties generatorProperties = GeneratorProperties.getInstance();
		if(generatorProperties.isStatisticheGenerazioneDebug()) {
			logCore = LoggerWrapperFactory.getLogger("govway.batch."+nomeLogger+".generazione");
			logSql = LoggerWrapperFactory.getLogger("govway.batch."+nomeLogger+".generazione.sql");
		}
		
		try {
			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setAttesaAttivaJDBC(-1);
			configPdD.setCheckIntervalJDBC(-1);
			configPdD.setLoader(new Loader(Generator.class.getClassLoader()));
			configPdD.setLog(logCore);
			ProtocolFactoryManager.initialize(logCore, configPdD,
					generatorProperties.getProtocolloDefault());
		} catch (Exception e) {
			throw new Exception("Errore durante la generazione delle statistiche (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
		}
		
		StatisticsConfig statisticsConfig = null;
		try{
			statisticsConfig = new StatisticsConfig(false);
			
			statisticsConfig.setLogCore(logCore);
			statisticsConfig.setLogSql(logSql);
			statisticsConfig.setGenerazioneStatisticheCustom(generatorProperties.isGenerazioneStatisticheCustom());
			statisticsConfig.setAnalisiTransazioniCustom(generatorProperties.isAnalisiTransazioniCustom());
			statisticsConfig.setDebug(generatorProperties.isStatisticheGenerazioneDebug());
			statisticsConfig.setUseUnionForLatency(generatorProperties.isGenerazioneStatisticheUseUnionForLatency());
			switch (tipoStatistica) {
			case STATISTICHE_ORARIE:
				statisticsConfig.setStatisticheOrarie(true);
				statisticsConfig.setStatisticheOrarie_gestioneUltimoIntervallo(true);
				break;
			case STATISTICHE_GIORNALIERE:
				statisticsConfig.setStatisticheGiornaliere(true);
				statisticsConfig.setStatisticheGiornaliere_gestioneUltimoIntervallo(true);
				break;
			case STATISTICHE_SETTIMANALI:
				statisticsConfig.setStatisticheSettimanali(true);
				statisticsConfig.setStatisticheSettimanali_gestioneUltimoIntervallo(true);
				break;
			case STATISTICHE_MENSILI:
				statisticsConfig.setStatisticheMensili(true);
				statisticsConfig.setStatisticheMensili_gestioneUltimoIntervallo(true);
				break;
			}
			
			// aggiorno configurazione per forceIndex
			statisticsConfig.setForceIndexConfig(generatorProperties.getStatisticheGenerazioneForceIndexConfig());
			
		}catch(Exception e){
			throw new Exception("Errore durante la generazione delle statistiche (InitConfigurazione): "+e.getMessage(),e);
		}
		
		StatisticsLibrary sLibrary = null;
		try {
			try {
				DAOFactory daoFactory = DAOFactory.getInstance(logSql);
				
				org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) 
						daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(), 
							logSql);
					
				org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) 
						daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
							logSql);
				
				org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
				org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM = null;
				org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM = null;
				org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM = null;
				
				if(generatorProperties.isGenerazioneStatisticheCustom()){
					
					pluginsStatisticheSM = (org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager) 
						daoFactory.getServiceManager(
							org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(), 
								logSql);
					
					pluginsBaseSM = (org.openspcoop2.core.plugins.dao.IServiceManager) 
						daoFactory.getServiceManager(
							org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),
								logSql);
					
					utilsSM = (org.openspcoop2.core.commons.search.dao.IServiceManager) 
						daoFactory.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), 
								logSql);
					
					if(generatorProperties.isAnalisiTransazioniCustom()){
						
						pluginsTransazioniSM = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) 
							daoFactory.getServiceManager(
								org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(), 
									logSql);
						
					}
					
				}
				
				sLibrary = new StatisticsLibrary(statisticsConfig, statisticheSM, transazioniSM, 
						pluginsStatisticheSM, pluginsBaseSM, utilsSM, pluginsTransazioniSM);
			}catch(Exception e){
				throw new Exception("Errore durante la generazione delle statistiche (InitConnessioni): "+e.getMessage(),e);
			}
			
			try {
				switch (tipoStatistica) {
				case STATISTICHE_ORARIE:
					sLibrary.generateStatisticaOraria();
					break;
				case STATISTICHE_GIORNALIERE:
					sLibrary.generateStatisticaGiornaliera();
					break;
				case STATISTICHE_SETTIMANALI:
					sLibrary.generateStatisticaSettimanale();
					break;
				case STATISTICHE_MENSILI:
					sLibrary.generateStatisticaMensile();
					break;
				}	
			}catch(Exception e){
				throw new Exception("Errore durante la generazione delle statistiche: "+e.getMessage(),e);
			}
		}finally {
			if(sLibrary!=null) {
				sLibrary.close();
			}
		}
	}

}
