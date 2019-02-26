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
			
			String tipiServiziSoap = commonProperties.getProperty("openspcoop2.tipiServizi.soap");
			if(tipiServiziSoap==null){
				throw new Exception("Non sono stati definiti i tipi di Servizi supportati");
			}else{
				tipiServiziSoap = tipiServiziSoap.trim();
			}
			String [] tipoServiziSoapArray = tipiServiziSoap.split(",");
			for (int i = 0; i < tipoServiziSoapArray.length; i++) {
				tipoServiziSoapArray[i]=tipoServiziSoapArray[i].trim();
			}
			
			String tipiServiziRest = commonProperties.getProperty("openspcoop2.tipiServizi.rest");
			if(tipiServiziRest==null){
				throw new Exception("Non sono stati definiti i tipi di Servizi supportati");
			}else{
				tipiServiziRest = tipiServiziRest.trim();
			}
			String [] tipoServiziRestArray = tipiServiziRest.split(",");
			for (int i = 0; i < tipoServiziRestArray.length; i++) {
				tipoServiziRestArray[i]=tipoServiziRestArray[i].trim();
			}
			
			String tipiDiagnosticiAppender = commonProperties.getProperty("openspcoop2.diagnostici.appender");
			if(tipiDiagnosticiAppender==null){
				throw new Exception("Non sono stati definiti i tipi di appender supportati (diagnostici)");
			}else{
				tipiDiagnosticiAppender = tipiDiagnosticiAppender.trim();
			}
			String [] tipiDiagnosticiAppenderArray = tipiDiagnosticiAppender.split(",");
			for (int i = 0; i < tipiDiagnosticiAppenderArray.length; i++) {
				tipiDiagnosticiAppenderArray[i]=tipiDiagnosticiAppenderArray[i].trim();
			}
			
			String tipiTracceAppender = commonProperties.getProperty("openspcoop2.tracce.appender");
			if(tipiTracceAppender==null){
				throw new Exception("Non sono stati definiti i tipi di appender supportati (tracce)");
			}else{
				tipiTracceAppender = tipiTracceAppender.trim();
			}
			String [] tipiTracceAppenderArray = tipiTracceAppender.split(",");
			for (int i = 0; i < tipiTracceAppenderArray.length; i++) {
				tipiTracceAppenderArray[i]=tipiTracceAppenderArray[i].trim();
			}
			
			String tipiDumpAppender = commonProperties.getProperty("openspcoop2.dump.appender");
			if(tipiDumpAppender==null){
				throw new Exception("Non sono stati definiti i tipi di appender supportati (dump)");
			}else{
				tipiDumpAppender = tipiDumpAppender.trim();
			}
			String [] tipiDumpAppenderArray = tipiDumpAppender.split(",");
			for (int i = 0; i < tipiDumpAppenderArray.length; i++) {
				tipiDumpAppenderArray[i]=tipiDumpAppenderArray[i].trim();
			}
			
			String tipiAutenticazionePortaDelegata = commonProperties.getProperty("openspcoop2.autenticazione.pd");
			if(tipiAutenticazionePortaDelegata==null){
				throw new Exception("Non sono stati definiti i tipi di autenticazione per la porta delegata supportati");
			}else{
				tipiAutenticazionePortaDelegata = tipiAutenticazionePortaDelegata.trim();
			}
			String [] tipiAutenticazionePortaDelegataArray = tipiAutenticazionePortaDelegata.split(",");
			for (int i = 0; i < tipiAutenticazionePortaDelegataArray.length; i++) {
				tipiAutenticazionePortaDelegataArray[i]=tipiAutenticazionePortaDelegataArray[i].trim();
			}
			
			String tipiAutenticazionePortaApplicativa = commonProperties.getProperty("openspcoop2.autenticazione.pa");
			if(tipiAutenticazionePortaApplicativa==null){
				throw new Exception("Non sono stati definiti i tipi di autenticazione per la porta applicativa supportati");
			}else{
				tipiAutenticazionePortaApplicativa = tipiAutenticazionePortaApplicativa.trim();
			}
			String [] tipiAutenticazionePortaApplicativaArray = tipiAutenticazionePortaApplicativa.split(",");
			for (int i = 0; i < tipiAutenticazionePortaApplicativaArray.length; i++) {
				tipiAutenticazionePortaApplicativaArray[i]=tipiAutenticazionePortaApplicativaArray[i].trim();
			}
			
			String tipiAutorizzazionePortaDelegata = commonProperties.getProperty("openspcoop2.autorizzazione.pd");
			if(tipiAutorizzazionePortaDelegata==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione per la porta delegata supportati");
			}else{
				tipiAutorizzazionePortaDelegata = tipiAutorizzazionePortaDelegata.trim();
			}
			String [] tipiAutorizzazionePortaDelegataArray = tipiAutorizzazionePortaDelegata.split(",");
			for (int i = 0; i < tipiAutorizzazionePortaDelegataArray.length; i++) {
				tipiAutorizzazionePortaDelegataArray[i]=tipiAutorizzazionePortaDelegataArray[i].trim();
			}
			
			String tipiAutorizzazionePortaApplicativa = commonProperties.getProperty("openspcoop2.autorizzazione.pa");
			if(tipiAutorizzazionePortaApplicativa==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione per la porta applicativa supportati");
			}else{
				tipiAutorizzazionePortaApplicativa = tipiAutorizzazionePortaApplicativa.trim();
			}
			String [] tipiAutorizzazionePortaApplicativaArray = tipiAutorizzazionePortaApplicativa.split(",");
			for (int i = 0; i < tipiAutorizzazionePortaApplicativaArray.length; i++) {
				tipiAutorizzazionePortaApplicativaArray[i]=tipiAutorizzazionePortaApplicativaArray[i].trim();
			}
			
			String tipiAutorizzazioneContenutiPortaDelegata = commonProperties.getProperty("openspcoop2.autorizzazioneContenuto.pd");
			if(tipiAutorizzazioneContenutiPortaDelegata==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione contenuti per la porta delegata supportati");
			}else{
				tipiAutorizzazioneContenutiPortaDelegata = tipiAutorizzazioneContenutiPortaDelegata.trim();
			}
			String [] tipiAutorizzazioneContenutiPortaDelegataArray = tipiAutorizzazioneContenutiPortaDelegata.split(",");
			for (int i = 0; i < tipiAutorizzazioneContenutiPortaDelegataArray.length; i++) {
				tipiAutorizzazioneContenutiPortaDelegataArray[i]=tipiAutorizzazioneContenutiPortaDelegataArray[i].trim();
			}
			
			String tipiAutorizzazioneContenutiPortaApplicativa = commonProperties.getProperty("openspcoop2.autorizzazioneContenuto.pa");
			if(tipiAutorizzazioneContenutiPortaApplicativa==null){
				throw new Exception("Non sono stati definiti i tipi di autorizzazione contenuti per la porta applicativa supportati");
			}else{
				tipiAutorizzazioneContenutiPortaApplicativa = tipiAutorizzazioneContenutiPortaApplicativa.trim();
			}
			String [] tipiAutorizzazioneContenutiPortaApplicativaArray = tipiAutorizzazioneContenutiPortaApplicativa.split(",");
			for (int i = 0; i < tipiAutorizzazioneContenutiPortaApplicativaArray.length; i++) {
				tipiAutorizzazioneContenutiPortaApplicativaArray[i]=tipiAutorizzazioneContenutiPortaApplicativaArray[i].trim();
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
						
			ValidazioneSemantica validatore = new ValidazioneSemantica(configurazione,tipoConnettoriArray,
					tipoSoggettiArray,tipoServiziSoapArray,tipoServiziRestArray,
					tipiDiagnosticiAppenderArray,tipiTracceAppenderArray,tipiDumpAppenderArray,
					tipiAutenticazionePortaDelegataArray,tipiAutenticazionePortaApplicativaArray,
					tipiAutorizzazionePortaDelegataArray,tipiAutorizzazionePortaApplicativaArray,
					tipiAutorizzazioneContenutiPortaDelegataArray,tipiAutorizzazioneContenutiPortaApplicativaArray,
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
