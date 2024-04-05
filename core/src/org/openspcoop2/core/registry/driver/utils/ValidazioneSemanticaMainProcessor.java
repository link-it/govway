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

package org.openspcoop2.core.registry.driver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.registry.driver.ValidazioneSemantica;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Validazione Semantica
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneSemanticaMainProcessor {

		/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception  {
		// TODO Auto-generated method stub
		
		if (args.length  < 4) {
			String errorMsg = "ERROR, Usage:  java ValidazioneSemanticaMainProcessor commonProperties tipoRegistro proprietaRegistro checkURI";
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
				URL url = ValidazioneSemanticaMainProcessor.class.getResource("/validator.log4j2.properties");
				if(url!=null){
					LoggerWrapperFactory.setLogConfiguration(url);
				}
				else{
					File logFile = FileSystemUtilities.createTempFile("testValidazioneSemanticaRegistro_", ".log");
					System.out.println("LogMessages write in "+logFile.getAbsolutePath());
					LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%p <%d{dd-MM-yyyy HH:mm:ss}> %C.%M(%L): %m %n %n");
				}
			}
		}catch(Exception e) {
			String errorMsg = "Errore durante il caricamento del file di log args4["+args[4]+"] : "+e.getMessage();
			System.err.println(errorMsg);
			System.out.println("Args.length: "+args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println("Arg["+i+"]=["+args[i]+"]");
			}
			throw new Exception(errorMsg,e);
		}	
		Logger log = LoggerWrapperFactory.getLogger("validatoreDatiRegistro");
		
		
		java.util.Properties commonProperties = new java.util.Properties();
		try{
			try(FileInputStream fin = new FileInputStream(args[0])){
				commonProperties.load(fin);
			}
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+args[0]+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}
		
		
		java.util.Properties reader = new java.util.Properties();
		try{
			try(FileInputStream fin = new FileInputStream(args[2])){
				reader.load(fin);
			}
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+args[2]+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}
		
				
		// Lettura registro
		org.openspcoop2.core.registry.RegistroServizi registro = null;
		Connection connectionDB = null;
		String tipoRegistro = args[1];
		boolean checkURI = Boolean.parseBoolean(args[3]);
		
		if("xml".equals(tipoRegistro)){
			
			// RegistroServizi XML
			String path = reader.getProperty("openspcoop2.registroServizi.xml");
			if(path==null){
				throw new Exception("Non e' stato definito il path dove localizzare il registro servizi xml");
			}else{
				path = path.trim();
			}
			
			DriverRegistroServiziXML driver = new DriverRegistroServiziXML(path,log);
			if(driver.create)
				log.info("Inizializzato Registro dei Servizi XML");
			else
				throw new Exception("RegistroServizi XML non inizializzato");
			
			registro = driver.getImmagineCompletaRegistroServizi();

		}
		else if("db".equals(tipoRegistro)){
			
			// RegistroServizi DB
			
			// Database
			String tipoDatabase = reader.getProperty("openspcoop2.registroServizi.db.tipo");
			if(tipoDatabase==null){
				throw new Exception("Non e' stato definito il tipo di database");
			}else{
				tipoDatabase = tipoDatabase.trim();
			}
			
			String dataSource = reader.getProperty("openspcoop2.registroServizi.db.dataSource");
			if(dataSource!=null){
				dataSource = dataSource.trim();
				java.util.Properties context = Utilities.readProperties("openspcoop2.registroServizi.db.context.",reader);
				
				DriverRegistroServiziDB driver = new DriverRegistroServiziDB(dataSource,context,log,tipoDatabase);
				if(driver.create)
					log.info("Inizializzato Registro dei Servizi DB");
				else
					throw new Exception("RegistroServizi DB non inizializzato");
				
				registro = driver.getImmagineCompletaRegistroServizi();
				
			}else{
				String connection = reader.getProperty("openspcoop2.registroServizi.db.url");
				if(connection==null){
					throw new Exception("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
				}
				connection = connection.trim();
				String driverJDBC = reader.getProperty("openspcoop2.registroServizi.db.driver");
				if(driverJDBC==null){
					throw new Exception("Connessione diretta: non e' stato definito il Driver");
				}
				driverJDBC =driverJDBC.trim();
				String username = reader.getProperty("openspcoop2.registroServizi.db.user");
				String password = reader.getProperty("openspcoop2.registroServizi.db.password");
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
					
					DriverRegistroServiziDB driver = new DriverRegistroServiziDB(connectionDB,log,tipoDatabase);
					if(driver.create)
						log.info("Inizializzato Registro dei Servizi DB");
					else
						throw new Exception("RegistroServizi DB non inizializzato");
					
					registro = driver.getImmagineCompletaRegistroServizi();
					
				}finally{
					try{
						if(connectionDB!=null)
							connectionDB.close();
					}catch(Exception e){
						// close
					}
				}
			}	
			
		}
		else{
			throw new Exception("Tipo di registro servizi ["+tipoRegistro+"] non gestito");
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
			String [] tipoServiziArraySoap = tipiServiziSoap.split(",");
			for (int i = 0; i < tipoServiziArraySoap.length; i++) {
				tipoServiziArraySoap[i]=tipoServiziArraySoap[i].trim();
			}
			
			String tipiServiziRest = commonProperties.getProperty("openspcoop2.tipiServizi.rest");
			if(tipiServiziRest==null){
				throw new Exception("Non sono stati definiti i tipi di Servizi supportati");
			}else{
				tipiServiziRest = tipiServiziRest.trim();
			}
			String [] tipoServiziArrayRest = tipiServiziRest.split(",");
			for (int i = 0; i < tipoServiziArrayRest.length; i++) {
				tipoServiziArrayRest[i]=tipoServiziArrayRest[i].trim();
			}
			
			ValidazioneSemantica validatore = new ValidazioneSemantica(registro,checkURI,tipoConnettoriArray,tipoSoggettiArray,tipoServiziArraySoap,tipoServiziArrayRest,log);
			
			validatore.validazioneSemantica(true);
		}catch(Exception e){
			log.error("Errore: "+e.getMessage());
			throw e;
			//e.printStackTrace(System.out);
		}
		
	}

	
}
