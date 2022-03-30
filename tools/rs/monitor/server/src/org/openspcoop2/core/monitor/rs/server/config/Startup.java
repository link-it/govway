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


package org.openspcoop2.core.monitor.rs.server.config;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.transazioni.utils.DumpUtils;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.slf4j.Logger;
/**
 * Questa classe si occupa di inizializzare tutte le risorse necessarie al webService.
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class Startup implements ServletContextListener {

	private static Logger log = null;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(Startup.log!=null)
			Startup.log.info("Undeploy webService in corso...");

		if(Startup.log!=null)
			Startup.log.info("Undeploy webService effettuato.");

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		Startup.initLog();
		
		Startup.initResources();

	}
	
	
	// LOG
	
	public static boolean initializedLog = false;
	
	public static synchronized String initLog(){
		
		String confDir = null;
		try{
			InputStream is = Startup.class.getResourceAsStream("/rs-api-monitor.properties");
			try{
				if(is!=null){
					Properties p = new Properties();
					p.load(is);
					confDir = p.getProperty("confDirectory");
					if(confDir!=null){
						confDir = confDir.trim();
					}
				}
			}finally{
				try{
					if(is!=null){
						is.close();
					}
				}catch(Exception eClose){}
			}

		}catch(Exception e){}
		
		if(Startup.initializedLog==false){
			
			try{
				Startup.log = LoggerWrapperFactory.getLogger(Startup.class);
				LoggerProperties.initialize(Startup.log, confDir, null);
				Startup.initializedLog = true;
				Startup.log = LoggerProperties.getLoggerCore();
				
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		
		return confDir;
	}
	
	
	// RESOURCES
	
	public static boolean initializedResources = false;
	
	public static synchronized void initResources(){
		if(Startup.initializedResources==false){
			
			String confDir = Startup.initLog();
			
			Startup.log.info("Inizializzazione rs api monitor in corso...");
			
			if(ServerProperties.initialize(confDir,Startup.log)==false){
				return;
			}
			
			if(DatasourceProperties.initialize(confDir,Startup.log)==false){
				return;
			}
			
			Startup.log.info("Inizializzazione DBManager in corso...");
			try{
				DatasourceProperties dbProperties = DatasourceProperties.getInstance();
				DBManager.initialize(dbProperties.getConfigDataSource(), dbProperties.getConfigDataSourceContext(), dbProperties.getConfigTipoDatabase(),
						dbProperties.getTracceDataSource(), dbProperties.getTracceDataSourceContext(), dbProperties.getTracceTipoDatabase(),
						dbProperties.getStatisticheDataSource(), dbProperties.getStatisticheDataSourceContext(), dbProperties.getStatisticheTipoDatabase(),
						dbProperties.isShowSql());
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione DBManager effettuata con successo");
			
			Startup.log.info("Inizializzazione ExtendedInfoManager in corso...");
			try{
				ExtendedInfoManager.initialize(new Loader(), null, null, null);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione ExtendedInfoManager effettuata con successo");
			
			Startup.log.info("Inizializzazione ProtocolFactoryManager in corso...");
			ServerProperties properties = null;
			try {
				properties = ServerProperties.getInstance();
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new Loader(Startup.class.getClassLoader()));
				configPdD.setLog(Startup.log);
				ProtocolFactoryManager.initialize(Startup.log, configPdD,
						properties.getProtocolloDefault());
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("ProtocolFactoryManager DBManager effettuata con successo");
			
			Startup.log.info("Inizializzazione Risorse Statiche Console in corso...");
			try {
				initResourceConsole();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione Risorse Statiche Console effettuata con successo");
			
			Startup.log.info("Inizializzazione Soglia per Dimensione Messaggi in corso...");
			try {
				DumpUtils.setThreshold_readInMemory(properties.getTransazioniDettaglioVisualizzazioneMessaggiThreshold());
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
			Startup.log.info("Inizializzazione Soglia per Dimensione Messaggi effettuata con successo");
						
			Startup.initializedResources = true;
			
			Startup.log.info("Inizializzazione rs api monitor effettuata con successo.");
		}
	}

	
	private static void initResourceConsole() throws UtilsException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		try {
			connection = dbManager.getConnectionConfig();
			ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
			
			Logger logSql = LoggerProperties.getLoggerDAO();
			
			LoginBean lb = new LoginBean(connection, true, smp, logSql);
			
			Utility.setStaticConfigurazioneGenerale(lb.getConfigurazioneGenerale());
			
			boolean multitenantAbilitato = Utility.isMultitenantAbilitato();
			
			if(multitenantAbilitato) {
				Utility.setStaticFiltroDominioAbilitato(true); // TODO: logiche piu' complesse vanno viste come realizzarle.
			}
			else {
				Utility.setStaticFiltroDominioAbilitato(false);
			}
			
			DriverRegistroServiziDB driverDB = new DriverRegistroServiziDB(connection, logSql, DatasourceProperties.getInstance().getConfigTipoDatabase());
			
			FiltroRicerca filtroRicercaPdd = new FiltroRicerca();
			filtroRicercaPdd.setTipo(PddTipologia.OPERATIVO.toString());
			List<String> idsPdd = null;
			try {
				idsPdd = driverDB.getAllIdPorteDominio(filtroRicercaPdd);
			}catch(DriverRegistroServiziNotFound notFound) {		
			}
			if(idsPdd!=null && !idsPdd.isEmpty()) {
				for (String idPdd : idsPdd) {
					FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
					filtroSoggetti.setNomePdd(idPdd);
					List<IDSoggetto> idsSoggetti = null;
					try {
						idsSoggetti = driverDB.getAllIdSoggetti(filtroSoggetti);
					}catch(DriverRegistroServiziNotFound notFound) {		
					}
					if(idsSoggetti!=null && !idsSoggetti.isEmpty()) {
						for (IDSoggetto idSoggetto : idsSoggetti) {
							Utility.putIdentificativoPorta(idSoggetto.getTipo(), idSoggetto.getNome(), driverDB.getSoggetto(idSoggetto).getIdentificativoPorta());
						}
					}
				}
			}
			
		} finally {
			dbManager.releaseConnectionConfig(connection);
		}
	}
}
