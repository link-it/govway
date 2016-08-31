/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Inizializza una configurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestXMLDataConverter {


	public static void main(String[] argoments) throws Exception {

		
		                               
		if (argoments.length  < 8) {
			String errorMsg = "ERROR, Usage:  java TestXMLDataConverter sorgenteXML tipoConfigurazioneCRUD proprietaConfigurazioneCRUD reset isGestioneConfigurazione tipoConversione gestioneSoggetti protocolloDefault [Logger]";
			System.err.println(errorMsg);
			throw new Exception(errorMsg);
		}

		String args_sorgenteXML = argoments[0].trim();
		String args_tipoConfigurazioneCRUD = argoments[1].trim();
		String args_proprietaConfigurazioneCRUD = argoments[2].trim();
		String args_reset = argoments[3].trim();
		String args_isGestioneConfigurazione = argoments[4].trim();
		String args_tipoConversione = argoments[5].trim();
		String args_gestioneSoggetti = argoments[6].trim();
		String args_protocolloDefault = argoments[7].trim();
		String args_logger = null;
		if(argoments.length>8){
			args_logger = argoments[8].trim();
		}
		
		// Inizializzo logger
		String loggerValue = null;
		try{
			if(args_logger!=null){
				LoggerWrapperFactory.setLogConfiguration(args_logger);
			}else{
				URL url = TestXMLDataConverter.class.getResource("/xml2backend.log4j2.properties");
				if(url!=null){
					LoggerWrapperFactory.setLogConfiguration(url);
				}
				else{
					File logFile = File.createTempFile("testXMLDataConverterConfigurazione_", ".log");
					System.out.println("LogMessages write in "+logFile.getAbsolutePath());
					LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%p <%d{dd-MM-yyyy HH:mm:ss}> %C.%M(%L): %m %n %n");
				}
			}
		}catch(Exception e) {
			String errorMsg = "Errore durante il caricamento del file di log loggerValue["+loggerValue+"] : "+e.getMessage();
			System.err.println(errorMsg);
			System.out.println("Args.length: "+argoments.length);
			for (int i = 0; i < argoments.length; i++) {
				System.out.println("Arg["+i+"]=["+argoments[i]+"]");
			}
			throw new Exception(errorMsg,e);
		}	
		Logger log = LoggerWrapperFactory.getLogger("gestoreDatiConfigurazione");
		
		java.util.Properties reader = new java.util.Properties();
		try{
			reader.load(new FileInputStream(args_proprietaConfigurazioneCRUD));
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+args_proprietaConfigurazioneCRUD+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}

		// ProtocolloDefault
		String protocolloDefault = args_protocolloDefault;
		
		// Reset
		boolean reset = Boolean.parseBoolean(args_reset);
		
		// GestioneSoggetti
		boolean gestioneSoggetti =  Boolean.parseBoolean(args_gestioneSoggetti);
		
		// isGestioneConfigurazione
		boolean isGestioneConfigurazione = Boolean.parseBoolean(args_isGestioneConfigurazione);
		
		// Raccolta proprieta'
		AccessoConfigurazionePdD acCRUD = new AccessoConfigurazionePdD();
		String superUser = null;
		boolean connectionDB = false;
		String connection = null;
		String username = null;
		String password = null;
		String driver =  null;
		String tipoDatabase = null;
		boolean condivisioneDBRegservPddValue = false;
		try{
			acCRUD.setTipo(args_tipoConfigurazioneCRUD);
						
			superUser = reader.getProperty("openspcoop2.superuser");
			if(superUser!=null)
				superUser.trim();
			
			if("db".equals(args_tipoConfigurazioneCRUD)){
				// Database
				tipoDatabase = reader.getProperty("openspcoop2.configurazione.db.tipo");
				if(tipoDatabase==null){
					throw new Exception("Non e' stato definito il tipo di database");
				}else{
					tipoDatabase = tipoDatabase.trim();
				}
				
				String condivisioneDBRegservPdd = reader.getProperty("openspcoop2.configurazione.db.condivisioneDBRegserv");
				try{
					condivisioneDBRegservPddValue = Boolean.parseBoolean(condivisioneDBRegservPdd.trim());
				}catch(Exception e){
					throw new Exception("Non e' stato definita o e' definita non correttamente la proprieta' [openspcoop2.configurazione.db.condivisioneDBRegserv]: "+e.getMessage());
				}
				
				String dataSource = reader.getProperty("openspcoop2.configurazione.db.dataSource");
				if(dataSource!=null){
					dataSource = dataSource.trim();
					java.util.Properties context = Utilities.readProperties("openspcoop2.configurazione.db.context.",reader);
					acCRUD.setLocation(dataSource);
					acCRUD.setContext(context);
					acCRUD.setTipoDatabase(tipoDatabase);
				}else{
					connectionDB = true;
					connection = reader.getProperty("openspcoop2.configurazione.db.url");
					if(connection==null){
						throw new Exception("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
					}
					connection = connection.trim();
					driver = reader.getProperty("openspcoop2.configurazione.db.driver");
					if(driver==null){
						throw new Exception("Connessione diretta: non e' stato definito il Driver");
					}
					driver = driver.trim();
					username = reader.getProperty("openspcoop2.configurazione.db.user");
					password = reader.getProperty("openspcoop2.configurazione.db.password");
					if(username!=null){
						username = username.trim();
					}
					if(password!=null){
						password = password.trim();
					}
				}
			}

		}catch(Exception e) {
			String errorMsg = "Errore durante la lettura del file di properties ["+args_proprietaConfigurazioneCRUD+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}

		try{
			ExtendedInfoManager.initialize(new Loader(TestXMLDataConverter.class.getClassLoader()), null, null, null);
		}catch(Exception e){
			log.error("Inizializzazione [ExtendedInfoManager] fallita",e);
			return;
		}
		
		Connection connectionSQL = null;
		try{
			// XMLDataConverter
			File fSorgente = new File(args_sorgenteXML);
			if(connectionDB){	
				Loader.getInstance().newInstance(driver);
				if(username!=null && password!=null){
					connectionSQL = DriverManager.getConnection(connection,username,password);
				}else{
					connectionSQL = DriverManager.getConnection(connection);
				}
			}
			
			TestXMLDataConverter.letturaSorgenti(fSorgente, connectionDB, connectionSQL, tipoDatabase, log, acCRUD, 
					condivisioneDBRegservPddValue, superUser,protocolloDefault, args_tipoConversione, isGestioneConfigurazione, reset, gestioneSoggetti);
			
		}catch(Exception e){
			String errorMsg = "Errore durante la conversione XML dei dati: "+e.getMessage(); 
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}finally{
			try{
				if(connectionSQL!=null)
					connectionSQL.close();
			}catch(Exception e){}
		}
	}
	
	
	private static Boolean resetEffettuata = false;
	private static boolean reset(boolean b){
		if(b){
			// Se si desidera la reset, controllo se e' gia stata effettuata
			synchronized (TestXMLDataConverter.resetEffettuata) {
				if(TestXMLDataConverter.resetEffettuata==false){
					TestXMLDataConverter.resetEffettuata=true;
					return true;
				}
				else{
					return false;
				}
			}
		}
		return false;
	}
	
	
	private static void letturaSorgenti(File fSorgente,boolean connectionDB,Connection connectionSQL,
			String tipoDatabase,Logger log,AccessoConfigurazionePdD acCRUD,
			boolean condivisioneDBRegservPddValue,String superUser,String protocolloDefault,
			String tipoConversione, boolean isGestioneConfigurazione,boolean reset,boolean gestioneSoggetti) throws Exception{
		if(fSorgente.isFile()){
			if(fSorgente.canRead()==false){
				throw new Exception("Sorgente XML ["+fSorgente.getAbsolutePath()+"] non accessibile in lettura");
			}
			if(fSorgente.getName().endsWith(".xml")){
				// Per non convertire i wsdl e i xml
				TestXMLDataConverter.converti(fSorgente, connectionDB, connectionSQL, tipoDatabase, log, acCRUD, 
						condivisioneDBRegservPddValue, superUser,protocolloDefault, tipoConversione, isGestioneConfigurazione, reset, gestioneSoggetti);
			}
			else{
				log.debug("File ["+fSorgente.getAbsolutePath()+"] ignorato. Non possiede l'estensione .xml");
			}
		}
		else if(fSorgente.isDirectory()){
			
			if(fSorgente.canRead()==false){
				throw new Exception("Directory contenente gli XML ["+fSorgente.getAbsolutePath()+"] non accessibile in lettura");
			}
			
			File [] f = fSorgente.listFiles();
			if(f==null || f.length<=0){
				throw new Exception("Directory ["+fSorgente.getAbsolutePath()+"] non contiene XML");
			}
			for(int i=0; i<f.length; i++){
			
				TestXMLDataConverter.letturaSorgenti(f[i], connectionDB, connectionSQL, tipoDatabase, log, acCRUD, 
						condivisioneDBRegservPddValue, superUser,protocolloDefault, tipoConversione, isGestioneConfigurazione, reset, gestioneSoggetti);
				
			}
		}
	}
	
	
	private static void converti(File f,boolean connectionDB,Connection connectionSQL,
			String tipoDatabase,Logger log,AccessoConfigurazionePdD acCRUD,
			boolean condivisioneDBRegservPddValue,String superUser,String protocolloDefault,
			String tipoConversione, boolean isGestioneConfigurazione,boolean reset,boolean gestioneSoggetti) throws Exception{
		// XMLDataConverter
		XMLDataConverter dataConverter = null;
		Logger logDriver = null;
		if(connectionDB){		
			dataConverter = new XMLDataConverter(f,connectionSQL,tipoDatabase,
					isGestioneConfigurazione,condivisioneDBRegservPddValue,superUser,protocolloDefault,
					log,logDriver);
		}else{
			dataConverter = new XMLDataConverter(f,acCRUD,
					isGestioneConfigurazione,condivisioneDBRegservPddValue,superUser,protocolloDefault,
					log,logDriver);
		}
		
		if("insertUpdate".equals(tipoConversione)){
			log.info("Inizio conversione...");
			dataConverter.convertXML(TestXMLDataConverter.reset(reset),gestioneSoggetti);
			log.info("Conversione terminata.");
		}
		else if("delete".equals(tipoConversione)){
			log.info("Inizio conversione...");
			dataConverter.delete(gestioneSoggetti);
			log.info("Conversione terminata.");
		}else{
			throw new Exception("Valore opzione 'tipoConversioneConfigurazione' non gestito (valori possibili insertUpdate/delete): "+tipoConversione);
		}
	}
}
