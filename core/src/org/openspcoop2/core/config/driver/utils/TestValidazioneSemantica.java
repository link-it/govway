/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.core.config.driver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.config.driver.ValidazioneSemantica;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Validazione Semantica
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestValidazioneSemantica {

		/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception  {
		// TODO Auto-generated method stub
		
		if (args.length  < 4) {
			String errorMsg = "ERROR, Usage:  java TestValidazioneSemantica commonProperties tipoConfigurazione proprietaConfigurazione validazioneConfigurazione";
			System.err.println(errorMsg);
			throw new Exception(errorMsg);
		}

		// Inizializzo logger
//		System.out.println("LUNGHEZZA: "+args.length);
//		for (int i = 0; i < args.length; i++) {
//			System.out.println("ARG["+i+"]=["+args[i]+"]");
//		}
		try{
			if(args.length==5){
				LoggerWrapperFactory.setLogConfiguration(args[4]);
			}else{
				URL url = TestValidazioneSemantica.class.getResource("/validator.log4j2.properties");
				if(url!=null){
					LoggerWrapperFactory.setLogConfiguration(url);
				}
				else{
					File logFile = File.createTempFile("testValidazioneSemanticaConfigurazione_", ".log");
					System.out.println("LogMessages write in "+logFile.getAbsolutePath());
					LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%p <%d{dd-MM-yyyy HH:mm:ss}> %C.%M(%L): %m %n %n");
				}
			}
		}catch(Exception e) {
			String errorMsg = "Errore durante il caricamento del file di log: "+e.getMessage();
			System.err.println(errorMsg);
			System.out.println("Args.length: "+args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println("Arg["+i+"]=["+args[i]+"]");
			}
			throw new Exception(errorMsg,e);
		}	
		Logger log = LoggerWrapperFactory.getLogger("validatoreDatiConfigurazione");
		
		
		java.util.Properties commonProperties = new java.util.Properties();
		try{
			commonProperties.load(new FileInputStream(args[0]));
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+args[0]+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}
		
		java.util.Properties reader = new java.util.Properties();
		try{
			reader.load(new FileInputStream(args[2]));
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+args[2]+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}
		
				
		// Lettura configurazione
		org.openspcoop2.core.config.Openspcoop2 configurazione = null;
		Connection connectionDB = null;
		String tipoConfigurazione = args[1];
		boolean validaSezioneConfigurazione = Boolean.parseBoolean(args[2]);
		
		if("xml".equals(tipoConfigurazione)){
			
			// Configurazione XML
			String path = reader.getProperty("openspcoop2.configurazione.xml");
			if(path==null){
				throw new Exception("Non e' stato definito il path dove localizzare la configurazione xml");
			}else{
				path = path.trim();
			}
			
			DriverConfigurazioneXML driver = new DriverConfigurazioneXML(path,log);
			if(driver.create)
				log.info("Inizializzata Configurazione PdD XML");
			else
				throw new Exception("ConfigurazionePdD XML non inizializzata");
			
			configurazione = driver.getImmagineCompletaConfigurazionePdD();

		}
		else if("db".equals(tipoConfigurazione)){
			
			// Configurazione DB
			
			// Database
			String tipoDatabase = reader.getProperty("openspcoop2.configurazione.db.tipo");
			if(tipoDatabase==null){
				throw new Exception("Non e' stato definito il tipo di database");
			}else{
				tipoDatabase = tipoDatabase.trim();
			}
			
			String dataSource = reader.getProperty("openspcoop2.configurazione.db.dataSource");
			if(dataSource!=null){
				dataSource = dataSource.trim();
				java.util.Properties context = Utilities.readProperties("openspcoop2.configurazione.db.context.",reader);
				
				DriverConfigurazioneDB driver = new DriverConfigurazioneDB(dataSource,context,log,tipoDatabase);
				if(driver.create)
					log.info("Inizializzata Configurazione DB");
				else
					throw new Exception("Configurazione DB non inizializzata");
				
				configurazione = driver.getImmagineCompletaConfigurazionePdD();
				
			}else{
				String connection = reader.getProperty("openspcoop2.configurazione.db.url");
				if(connection==null){
					throw new Exception("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
				}
				connection = connection.trim();
				String driverJDBC = reader.getProperty("openspcoop2.configurazione.db.driver");
				if(driverJDBC==null){
					throw new Exception("Connessione diretta: non e' stato definito il Driver");
				}
				driverJDBC =driverJDBC.trim();
				String username = reader.getProperty("openspcoop2.configurazione.db.user");
				String password = reader.getProperty("openspcoop2.configurazione.db.password");
				if(username!=null){
					username = username.trim();
				}
				if(password!=null){
					password = password.trim();
				}
				
				// Carico driver JDBC
				Loader.getInstance().newInstance(driverJDBC);
				
				try{
					if(username!=null){
						connectionDB = DriverManager.getConnection(connection, username, password); 
					}else{
						connectionDB = DriverManager.getConnection(connection); 
					}
					
					DriverConfigurazioneDB driver = new DriverConfigurazioneDB(connectionDB,log,tipoDatabase);
					if(driver.create)
						log.info("Inizializzata Configurazione DB");
					else
						throw new Exception("Configurazione DB non inizializzata");
					
					configurazione = driver.getImmagineCompletaConfigurazionePdD();
					
				}finally{
					try{
						if(connectionDB!=null)
							connectionDB.close();
					}catch(Exception e){}
				}
			}	
			
		}
		else{
			throw new Exception("Tipo di configurazione ["+tipoConfigurazione+"] non gestita");
		}
		

		// VALIDAZIONE SEMANTICA
		try{
			String tipiConnettori = commonProperties.getProperty("openspcoop2.tipiConnettori");
			if(tipiConnettori==null){
				throw new Exception("Non sono stati definiti i tipi di connettori supportati");
			}else{
				tipiConnettori = tipiConnettori.trim();
			}
			String [] tipoConnettoriArray = tipiConnettori.split(",");
			for (int i = 0; i < tipoConnettoriArray.length; i++) {
				tipoConnettoriArray[i]=tipoConnettoriArray[i].trim();
			}
			
			String tipiSoggetti = commonProperties.getProperty("openspcoop2.tipiSoggetti");
			if(tipiSoggetti==null){
				throw new Exception("Non sono stati definiti i tipi di soggetti supportati");
			}else{
				tipiSoggetti = tipiSoggetti.trim();
			}
			String [] tipoSoggettiArray = tipiSoggetti.split(",");
			for (int i = 0; i < tipoSoggettiArray.length; i++) {
				tipoSoggettiArray[i]=tipoSoggettiArray[i].trim();
			}
			
			String tipiServizi = commonProperties.getProperty("openspcoop2.tipiServizi");
			if(tipiServizi==null){
				throw new Exception("Non sono stati definiti i tipi di Servizi supportati");
			}else{
				tipiServizi = tipiServizi.trim();
			}
			String [] tipoServiziArray = tipiServizi.split(",");
			for (int i = 0; i < tipoServiziArray.length; i++) {
				tipoServiziArray[i]=tipoServiziArray[i].trim();
			}
			
			
			String tipiDiagnosticiAppender = commonProperties.getProperty("openspcoop2.diagnostici.appender");
			if(tipiDiagnosticiAppender==null){
				throw new Exception("Non sono stati definiti i tipi di appender supportati (diagnostici)");
			}else{
				tipiDiagnosticiAppender = tipiServizi.trim();
			}
			String [] tipiDiagnosticiAppenderArray = tipiDiagnosticiAppender.split(",");
			for (int i = 0; i < tipiDiagnosticiAppenderArray.length; i++) {
				tipiDiagnosticiAppenderArray[i]=tipiDiagnosticiAppenderArray[i].trim();
			}
			
			String tipiTracceAppender = commonProperties.getProperty("openspcoop2.tracce.appender");
			if(tipiTracceAppender==null){
				throw new Exception("Non sono stati definiti i tipi di appender supportati (tracce)");
			}else{
				tipiTracceAppender = tipiServizi.trim();
			}
			String [] tipiTracceAppenderArray = tipiTracceAppender.split(",");
			for (int i = 0; i < tipiTracceAppenderArray.length; i++) {
				tipiTracceAppenderArray[i]=tipiTracceAppenderArray[i].trim();
			}
			
			String tipiAutenticazione = commonProperties.getProperty("openspcoop2.autenticazione");
			if(tipiAutenticazione==null){
				throw new Exception("Non sono stati definiti i tipi di autenticazione supportati");
			}else{
				tipiAutenticazione = tipiAutenticazione.trim();
			}
			String [] tipiAutenticazioneArray = tipiAutenticazione.split(",");
			for (int i = 0; i < tipiAutenticazioneArray.length; i++) {
				tipiAutenticazioneArray[i]=tipiAutenticazioneArray[i].trim();
			}
			
			String tipiAutorizzazione = commonProperties.getProperty("openspcoop2.autorizzazione");
			if(tipiAutorizzazione==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione supportati");
			}else{
				tipiAutorizzazione = tipiAutorizzazione.trim();
			}
			String [] tipiAutorizzazioneArray = tipiAutorizzazione.split(",");
			for (int i = 0; i < tipiAutorizzazioneArray.length; i++) {
				tipiAutorizzazioneArray[i]=tipiAutorizzazioneArray[i].trim();
			}
			
			String tipiAutorizzazioneContenutoPD = commonProperties.getProperty("openspcoop2.autorizzazioneContenuto.pd");
			if(tipiAutorizzazioneContenutoPD==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione per contenuto supportati (PD)");
			}else{
				tipiAutorizzazioneContenutoPD = tipiAutorizzazioneContenutoPD.trim();
			}
			String [] tipiAutorizzazioneContenutoPDArray = tipiAutorizzazioneContenutoPD.split(",");
			for (int i = 0; i < tipiAutorizzazioneContenutoPDArray.length; i++) {
				tipiAutorizzazioneContenutoPDArray[i]=tipiAutorizzazioneContenutoPDArray[i].trim();
			}
			
			String tipiAutorizzazioneContenutoPA = commonProperties.getProperty("openspcoop2.autorizzazioneContenuto.pa");
			if(tipiAutorizzazioneContenutoPA==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione per contenuto supportati (PA)");
			}else{
				tipiAutorizzazioneContenutoPA = tipiAutorizzazioneContenutoPA.trim();
			}
			String [] tipiAutorizzazioneContenutoPAArray = tipiAutorizzazioneContenutoPA.split(",");
			for (int i = 0; i < tipiAutorizzazioneContenutoPAArray.length; i++) {
				tipiAutorizzazioneContenutoPAArray[i]=tipiAutorizzazioneContenutoPAArray[i].trim();
			}

			String tipiIntegrazionePD = commonProperties.getProperty("openspcoop2.integrazione.pd");
			if(tipiIntegrazionePD==null){
				throw new Exception("Non sono stati definiti i tipi di Integrazione supportati (PD)");
			}else{
				tipiIntegrazionePD = tipiIntegrazionePD.trim();
			}
			String [] tipiIntegrazionePDArray = tipiIntegrazionePD.split(",");
			for (int i = 0; i < tipiIntegrazionePDArray.length; i++) {
				tipiIntegrazionePDArray[i]=tipiIntegrazionePDArray[i].trim();
			}
			
			String tipiIntegrazionePA = commonProperties.getProperty("openspcoop2.integrazione.pa");
			if(tipiIntegrazionePA==null){
				throw new Exception("Non sono stati definiti i tipi di Integrazione supportati (PA)");
			}else{
				tipiIntegrazionePA = tipiIntegrazionePA.trim();
			}
			String [] tipiIntegrazionePAArray = tipiIntegrazionePA.split(",");
			for (int i = 0; i < tipiIntegrazionePAArray.length; i++) {
				tipiIntegrazionePAArray[i]=tipiIntegrazionePAArray[i].trim();
			}
						
			ValidazioneSemantica validatore = new ValidazioneSemantica(configurazione,tipoConnettoriArray,tipoSoggettiArray,tipoServiziArray,
					tipiDiagnosticiAppenderArray,tipiTracceAppenderArray,
					tipiAutenticazioneArray,tipiAutorizzazioneArray,
					tipiAutorizzazioneContenutoPDArray,tipiAutorizzazioneContenutoPAArray,
					tipiIntegrazionePDArray,tipiIntegrazionePAArray,
					validaSezioneConfigurazione,log);
			
			validatore.validazioneSemantica(true);
		}catch(Exception e){
			log.error("Errore: "+e.getMessage());
			throw e;
			//e.printStackTrace(System.out);
		}
		
	}

	
}
