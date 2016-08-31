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

package org.openspcoop2.core.registry.driver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Inizializza un registro Servizi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestXMLDataConverter {


	public static void main(String[] argoments) throws Exception {

		if (argoments.length  < 9) {
			String errorMsg = "ERROR, Usage:  java TestXMLDataConverter sorgenteXML tipoRegistroCRUD proprietaRegistroCRUD reset tipoConversione gestioneSoggetti mantieniFruitori statoAccordiImportati protocolloDefault [[logger] [nomePddOperativa logger]]";
			System.err.println(errorMsg);
			throw new Exception(errorMsg);
		}

		String args_sorgenteXML = argoments[0].trim();
		String args_tipoRegistroCRUD = argoments[1].trim();
		String args_proprietaRegistroCRUD = argoments[2].trim();
		String args_reset = argoments[3].trim();
		String args_tipoConversione = argoments[4].trim();
		String args_gestioneSoggetti = argoments[5].trim();
		String args_mantieniFruitori = argoments[6].trim();
		String args_statoAccordiImportati = argoments[7].trim();
		String args_protocolloDefault = argoments[8].trim();
		String args_nomePddOperativa = null;
		String args_logger = null;
		if(argoments.length==10){
			args_logger = argoments[9].trim();
		}
		else{
			args_nomePddOperativa = argoments[9].trim();
			args_logger = argoments[10].trim();
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
					File logFile = File.createTempFile("testXMLDataConverterRegistro_", ".log");
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
		Logger log = LoggerWrapperFactory.getLogger("gestoreDatiRegistro");
		
		// StatoAccordo
		String statoAccordo = args_statoAccordiImportati;
		if(!StatiAccordo.bozza.toString().equals(statoAccordo) && !StatiAccordo.operativo.toString().equals(statoAccordo) && !StatiAccordo.finale.toString().equals(statoAccordo)){
			String errorMsg = "Opzione 'statoAccordiImportati' non valida ("+statoAccordo+"), valori possibili sono: "+StatiAccordo.bozza.toString()+","+StatiAccordo.operativo.toString()+","+StatiAccordo.finale.toString();
			System.err.println(errorMsg);
			throw new Exception(errorMsg);
		}
		StatiAccordo statoAccordoObject = StatiAccordo.valueOf(statoAccordo);

		// ProtocolloDefault
		String protocolloDefault = args_protocolloDefault;
		
		// Reset
		boolean reset = Boolean.parseBoolean(args_reset);
		
		// GestioneSoggetti
		boolean gestioneSoggetti =  Boolean.parseBoolean(args_gestioneSoggetti);
		
		// MantieniFruitori
		boolean mantieniFruitori = Boolean.parseBoolean(args_mantieniFruitori);
		
		// Properties
		java.util.Properties reader = new java.util.Properties();
		try{
			reader.load(new FileInputStream(args_proprietaRegistroCRUD));
		}catch(java.io.IOException e) {
			String errorMsg = "Errore durante il caricamento del file di properties ["+args_proprietaRegistroCRUD+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}

		// Raccolta proprieta'
		AccessoRegistroRegistro acCRUD = new AccessoRegistroRegistro();

		String superUser = null;
		boolean connectionDB = false;
		String connection = null;
		String username = null;
		String password = null;
		String driver =  null;
		String tipoDatabase = null;
		try{
			acCRUD.setTipo(RegistroTipo.toEnumConstant(args_tipoRegistroCRUD));
			
			superUser = reader.getProperty("openspcoop2.superuser");
			if(superUser!=null)
				superUser.trim();
			
			if("db".equals(args_tipoRegistroCRUD)){
				// Database
				tipoDatabase = reader.getProperty("openspcoop2.registroServizi.db.tipo");
				if(tipoDatabase==null){
					throw new Exception("Non e' stato definito il tipo di database");
				}else{
					tipoDatabase = tipoDatabase.trim();
				}
				
				String dataSource = reader.getProperty("openspcoop2.registroServizi.db.dataSource");
				if(dataSource!=null){
					dataSource = dataSource.trim();
					java.util.Properties context = Utilities.readProperties("openspcoop2.registroServizi.db.context.",reader);
					acCRUD.setLocation(dataSource);
					acCRUD.setTipoDatabase(tipoDatabase);
					Enumeration<?> keys = context.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String value = context.getProperty(key);
						acCRUD.putGenericProperties(key, value);
					}
				}else{
					connectionDB = true;
					connection = reader.getProperty("openspcoop2.registroServizi.db.url");
					if(connection==null){
						throw new Exception("Non e' stata definita una destinazione ne attraverso un datasource, ne attraverso una connessione diretta");
					}
					connection = connection.trim();
					driver = reader.getProperty("openspcoop2.registroServizi.db.driver");
					if(driver==null){
						throw new Exception("Connessione diretta: non e' stato definito il Driver");
					}
					driver = driver.trim();
					username = reader.getProperty("openspcoop2.registroServizi.db.user");
					password = reader.getProperty("openspcoop2.registroServizi.db.password");
					if(username!=null){
						username = username.trim();
					}
					if(password!=null){
						password = password.trim();
					}
				}		
			}else if("uddi".equals(args_tipoRegistroCRUD)){
				// UDDI
				String inquiryUrl = reader.getProperty("openspcoop2.registroServizi.uddi.inquiryUrl");
				if(inquiryUrl!=null)
					inquiryUrl = inquiryUrl.trim();
				String publishUrl = reader.getProperty("openspcoop2.registroServizi.uddi.publishUrl");
				if(publishUrl!=null)
					publishUrl = publishUrl.trim();
				String usernameUDDI = reader.getProperty("openspcoop2.registroServizi.uddi.username");
				if(usernameUDDI!=null)
					usernameUDDI = usernameUDDI.trim();
				String passwordUDDI = reader.getProperty("openspcoop2.registroServizi.uddi.password");
				if(passwordUDDI!=null)
					passwordUDDI = passwordUDDI.trim();
				String urlPrefix = reader.getProperty("openspcoop2.registroServizi.uddi.urlPrefix");
				if(urlPrefix!=null)
					urlPrefix = urlPrefix.trim();
				String pathPrefix = reader.getProperty("openspcoop2.registroServizi.uddi.pathPrefix");
				if(pathPrefix!=null)
					pathPrefix = pathPrefix.trim();
				acCRUD.setLocation(inquiryUrl);
				acCRUD.setUser(usernameUDDI);
				acCRUD.setPassword(passwordUDDI);
				acCRUD.putGenericProperties("publishUrl", publishUrl);
				acCRUD.putGenericProperties("urlPrefix", urlPrefix);
				acCRUD.putGenericProperties("pathPrefix", pathPrefix);
			}else if("web".equals(args_tipoRegistroCRUD)){
				// WEB
				String urlPrefix = reader.getProperty("openspcoop2.registroServizi.web.urlPrefix");
				if(urlPrefix!=null)
					urlPrefix = urlPrefix.trim();
				String pathPrefix = reader.getProperty("openspcoop2.registroServizi.web.pathPrefix");
				if(pathPrefix!=null)
					pathPrefix = pathPrefix.trim();
				acCRUD.setLocation(urlPrefix);
				acCRUD.putGenericProperties("pathPrefix", pathPrefix);
			}

		}catch(Exception e) {
			String errorMsg = "Errore durante la lettura del file di properties ["+args_proprietaRegistroCRUD+"] : "+e.getMessage();
			log.error(errorMsg,e);
			throw new Exception(errorMsg,e);
		}

		Connection connectionSQL = null;
		try{
			File fSorgente = new File(args_sorgenteXML);
			if(connectionDB){		
				Loader.getInstance().newInstance(driver);
				if(username!=null && password!=null){
					connectionSQL = DriverManager.getConnection(connection,username,password);
				}else{
					connectionSQL = DriverManager.getConnection(connection);
				}
			}
			
			TestXMLDataConverter.letturaSorgenti(fSorgente, connectionDB, connectionSQL, tipoDatabase, log, superUser, acCRUD, 
					statoAccordoObject, protocolloDefault, args_tipoConversione, args_nomePddOperativa, gestioneSoggetti, reset, mantieniFruitori);
			
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
			String tipoDatabase,Logger log,String superUser,AccessoRegistroRegistro acCRUD,
			StatiAccordo statoAccordoObject,String protocolloDefault,
			String tipoConversione,String nomePddOperativa,
			boolean gestioneSoggetti,boolean reset, boolean mantieniFruitori) throws Exception{
		if(fSorgente.isFile()){
			if(fSorgente.canRead()==false){
				throw new Exception("Sorgente XML ["+fSorgente.getAbsolutePath()+"] non accessibile in lettura");
			}
			if(fSorgente.getName().endsWith(".xml")){
				// Per non convertire i wsdl e i xml
				TestXMLDataConverter.converti(fSorgente, connectionDB, connectionSQL, tipoDatabase, log, superUser, acCRUD, 
						statoAccordoObject, protocolloDefault,tipoConversione,nomePddOperativa,gestioneSoggetti,reset,mantieniFruitori);
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
			
				TestXMLDataConverter.letturaSorgenti(f[i], connectionDB, connectionSQL, tipoDatabase, log, superUser, acCRUD, 
						statoAccordoObject, protocolloDefault,tipoConversione,nomePddOperativa,gestioneSoggetti,reset,mantieniFruitori);
				
			}
		}
	}
	
	
	private static void converti(File f,boolean connectionDB,Connection connectionSQL,
			String tipoDatabase,Logger log,String superUser,AccessoRegistroRegistro acCRUD,
			StatiAccordo statoAccordoObject,String protocolloDefault,
			String tipoConversione,String nomePddOperativa,
			boolean gestioneSoggetti,boolean reset, boolean mantieniFruitori) throws Exception{
		
		Logger logDriver = null;
		if( "uddi".equals(acCRUD.getTipo()) || "web".equals(acCRUD.getTipo()) ){
			logDriver = log;
		}
		
		// XMLDataConverter
		XMLDataConverter dataConverter = null;
		if(connectionDB){		
			dataConverter = new XMLDataConverter(f.getAbsolutePath(),connectionSQL,tipoDatabase,superUser,protocolloDefault,log,logDriver);
		}else{
			dataConverter = new XMLDataConverter(f.getAbsolutePath(),acCRUD,superUser,protocolloDefault,log,logDriver);
		}
		
		if("insertUpdate".equals(tipoConversione)){
			log.info("Inizio conversione...");
			if(nomePddOperativa!=null){
				PdDConfig pddConfig = new PdDConfig();
				String pddOperativa = nomePddOperativa;
				pddConfig.setPddOperativaCtrlstatSinglePdd(pddOperativa);
				dataConverter.convertXML(TestXMLDataConverter.reset(reset),pddConfig,mantieniFruitori,gestioneSoggetti,statoAccordoObject);
			}else{
				dataConverter.convertXML(TestXMLDataConverter.reset(reset),mantieniFruitori,gestioneSoggetti,statoAccordoObject);
			}
			log.info("Conversione terminata.");
		}
		else if("delete".equals(tipoConversione)){
			log.info("Inizio conversione...");
			dataConverter.delete(gestioneSoggetti);
			log.info("Conversione terminata.");
		}else{
			throw new Exception("Valore opzione 'tipoConversioneRegistroServizi' non gestito (valori possibili insertUpdate/delete): "+tipoConversione);
		}
	}
}
