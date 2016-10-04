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



package org.openspcoop2.testsuite.units.utils;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.UnitsDatabaseProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Reader delle proprieta' per l'accesso al Database
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatabaseProperties implements UnitsDatabaseProperties {

	/** Logger utilizzato per errori eventuali. */
	private static Logger log = LoggerWrapperFactory.getLogger("DatabaseProperties");



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'database.properties' */
	private Properties reader;

	/** Copia Statica */
	private static DatabaseProperties databaseProperties = null;

	/** Tipologia di gestione delle connessioni
	 */
	private boolean datasourceVersion = false; 
	public boolean isDatasourceVersion() {
		return this.datasourceVersion;
	}
	
	private String protocolName;
	
	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 */
	public DatabaseProperties(String protocolName) throws Exception {

		this.protocolName = protocolName;
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		this.reader = new Properties();
		java.io.InputStream properties = null;
		try{  
			if(DatabaseProperties.class.getResourceAsStream("/database_testunits.properties")!=null){
				properties = DatabaseProperties.class.getResourceAsStream("/database_testunits.properties");
				this.datasourceVersion = true;
			}else{
				properties = DatabaseProperties.class.getResourceAsStream("/database_testunits_connections.properties");
				this.datasourceVersion = false;
			}
			this.reader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file 'database_testunits.properties': \n\n"+e.getMessage());
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new Exception("ClassName initialize error: "+e.getMessage());
		}

	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 */
	public static boolean initialize(String protocolName){

		try {
			DatabaseProperties.databaseProperties = new DatabaseProperties(protocolName);	
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna l'indicazione se questa classe e' inizializzata
	 *
	 * @return indicazione se questa classe e' inizializzata
	 */
	public static synchronized boolean isInitialized(){
		if(DatabaseProperties.databaseProperties!=null)
			return true;
		else
			return false;
	}
	
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di DatabaseProperties
	 */
	public static DatabaseProperties getInstance(String protocolName){
		if(DatabaseProperties.databaseProperties==null)
			DatabaseProperties.initialize(protocolName);
		return DatabaseProperties.databaseProperties;
	}



	
	@Override
	public DatabaseComponent newInstanceDatabaseComponentErogatore() {
		return getDatabaseComponentErogatore(this.protocolName);
	}

	@Override
	public DatabaseComponent newInstanceDatabaseComponentFruitore() {
		return getDatabaseComponentFruitore(this.protocolName);
	}

	@Override
	public DatabaseMsgDiagnosticiComponent newInstanceDatabaseComponentDiagnosticaErogatore() {
		return getDatabaseComponentDiagnosticaErogatore(this.protocolName);
	}

	@Override
	public DatabaseMsgDiagnosticiComponent newInstanceDatabaseComponentDiagnosticaFruitore() {
		return getDatabaseComponentDiagnosticaFruitore(this.protocolName);
	}
	
	
	

	private static DatabaseComponent databaseErogatore = null;
	public static DatabaseComponent getDatabaseComponentErogatore(String protocolName){
		if(databaseErogatore==null){
			synchronized(DatabaseProperties.getInstance(protocolName)){
				try{
					DatabaseProperties dbP = DatabaseProperties.getInstance(protocolName);
					if(dbP.databasePdD_is_DatabaseTracciamento()){
						if(dbP.isDatasourceVersion()){
							return new DatabaseComponent(dbP.getDataSourceErogatore(),dbP.getJNDIContext_DataSourceErogatore(),protocolName);
						}else{
							return new DatabaseComponent(dbP.getDriverJDBC(),dbP.getConnectionUrlTracciamentoErogatore(),
									dbP.getUsernameTracciamentoErogatore(),dbP.getPasswordTracciamentoErogatore(),protocolName);
						}
					}else  {
						if(dbP.isDatasourceVersion()){
							return new DatabaseComponent(dbP.getDataSourceErogatore(),dbP.getJNDIContext_DataSourceErogatore(),
									dbP.getDataSourcePdDErogatore(),dbP.getJNDIContext_DataSourcePdDErogatore(),protocolName);
						}else{
							return new DatabaseComponent(dbP.getDriverJDBC(),
									dbP.getConnectionUrlTracciamentoErogatore(),
									dbP.getUsernameTracciamentoErogatore(),dbP.getPasswordTracciamentoErogatore(),
									dbP.getConnectionUrlOpenspcoopErogatore(),
									dbP.getUsernameOpenspcoopErogatore(),dbP.getPasswordOpenspcoopErogatore(),protocolName);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}
		return databaseErogatore;
	}
	
	private static DatabaseComponent databaseFruitore = null;
	public static DatabaseComponent getDatabaseComponentFruitore(String protocolName){
		if(databaseFruitore==null){
			synchronized(DatabaseProperties.getInstance(protocolName)){
				try{
					DatabaseProperties dbP = DatabaseProperties.getInstance(protocolName);
					if(dbP.databasePdD_is_DatabaseTracciamento()){
						if(dbP.isDatasourceVersion()){
							return new DatabaseComponent(dbP.getDataSourceFruitore(),dbP.getJNDIContext_DataSourceFruitore(),protocolName);
						}else{
							return new DatabaseComponent(dbP.getDriverJDBC(),dbP.getConnectionUrlTracciamentoFruitore(),
									dbP.getUsernameTracciamentoFruitore(),dbP.getPasswordTracciamentoFruitore(),protocolName);
						}
					}else  {
						if(dbP.isDatasourceVersion()){
							return new DatabaseComponent(dbP.getDataSourceFruitore(),dbP.getJNDIContext_DataSourceFruitore(),
									dbP.getDataSourcePdDFruitore(),dbP.getJNDIContext_DataSourcePdDFruitore(),protocolName);
						}else{
							return new DatabaseComponent(dbP.getDriverJDBC(),
									dbP.getConnectionUrlTracciamentoFruitore(),
									dbP.getUsernameTracciamentoFruitore(),dbP.getPasswordTracciamentoFruitore(),
									dbP.getConnectionUrlOpenspcoopFruitore(),
									dbP.getUsernameOpenspcoopFruitore(),dbP.getPasswordOpenspcoopFruitore(),protocolName);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}
		return databaseFruitore;
	}


	private static DatabaseMsgDiagnosticiComponent databaseDiagnosticaErogatore = null;
	public static DatabaseMsgDiagnosticiComponent getDatabaseComponentDiagnosticaErogatore(String protocolName){
		if(databaseDiagnosticaErogatore==null){
			synchronized(DatabaseProperties.getInstance(protocolName)){
				try{
					DatabaseProperties dbP = DatabaseProperties.getInstance(protocolName);
					if(dbP.databaseDiagnostica_is_DatabaseTracciamento()){
						if(dbP.isDatasourceVersion()){
							return new DatabaseMsgDiagnosticiComponent(dbP.getDataSourceErogatore(),dbP.getJNDIContext_DataSourceErogatore(),protocolName);
						}else{
							return new DatabaseMsgDiagnosticiComponent(dbP.getDriverJDBC(),dbP.getConnectionUrlTracciamentoErogatore(),
									dbP.getUsernameTracciamentoErogatore(),dbP.getPasswordTracciamentoErogatore(),protocolName);
						}
					}else  {
						if(dbP.isDatasourceVersion()){
							return new DatabaseMsgDiagnosticiComponent(dbP.getDataSourceDiagnosticaPdDErogatore(),dbP.getJNDIContext_DataSourceDiagnosticaPdDErogatore(),protocolName);
						}else{
							return new DatabaseMsgDiagnosticiComponent(dbP.getDriverJDBC(),dbP.getConnectionUrlDiagnosticaErogatore(),
									dbP.getUsernameDiagnosticaErogatore(),dbP.getPasswordDiagnosticaErogatore(),protocolName);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}
		return databaseDiagnosticaErogatore;
	}
	
	private static DatabaseMsgDiagnosticiComponent databaseDiagnosticaFruitore = null;
	public static DatabaseMsgDiagnosticiComponent getDatabaseComponentDiagnosticaFruitore(String protocolName){
		if(databaseDiagnosticaFruitore==null){
			synchronized(DatabaseProperties.getInstance(protocolName)){
				try{
					DatabaseProperties dbP = DatabaseProperties.getInstance(protocolName);
					if(dbP.databaseDiagnostica_is_DatabaseTracciamento()){
						if(dbP.isDatasourceVersion()){
							return new DatabaseMsgDiagnosticiComponent(dbP.getDataSourceFruitore(),dbP.getJNDIContext_DataSourceFruitore(),protocolName);
						}else{
							return new DatabaseMsgDiagnosticiComponent(dbP.getDriverJDBC(),dbP.getConnectionUrlTracciamentoFruitore(),
									dbP.getUsernameTracciamentoFruitore(),dbP.getPasswordTracciamentoFruitore(),protocolName);
						}
					}else  {
						if(dbP.isDatasourceVersion()){
							return new DatabaseMsgDiagnosticiComponent(dbP.getDataSourceDiagnosticaPdDFruitore(),dbP.getJNDIContext_DataSourceDiagnosticaPdDFruitore(),protocolName);
						}else{
							return new DatabaseMsgDiagnosticiComponent(dbP.getDriverJDBC(),dbP.getConnectionUrlDiagnosticaFruitore(),
									dbP.getUsernameDiagnosticaFruitore(),dbP.getPasswordDiagnosticaFruitore(),protocolName);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}
		return databaseDiagnosticaErogatore;
	}
	
	
	
	
	
	
	


	
	
	
	/* ********** UTILITY COMUNI ***************** */
	/**
	 * Ritorna il driver JDBC
	 *
	 */
	public String getDriverJDBC() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.driver").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.driver':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'indicazione se il database utilizzato dalla PdD e' lo stesso di quello utilizzato per il tracciamento
	 *
	 */
	public boolean databasePdD_is_DatabaseTracciamento() throws Exception{
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.datasource.terminazioneMessaggi.isDataSourceTracciamento").trim());
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.terminazioneMessaggi.isDataSourceTracciamento':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}

	/**
	 * Ritorna l'indicazione se il database utilizzato per i msg diagnostici e' lo stesso di quello utilizzato per il tracciamento
	 *
	 */
	public boolean databaseDiagnostica_is_DatabaseTracciamento() throws Exception{
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.datasource.diagnostica.isDataSourceTracciamento").trim());
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.isDataSourceTracciamento':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	
	
	
	
	
	/* ********  M E T O D I (Datasource)  ******** */

	/**
	 * Ritorna il nome del dataSource per l'accesso al database tracciamento fruitore
	 *
	 */
	public String getDataSourceFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.fruitore").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.fruitore':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database tracciamento fruitore
	 *
	 */
	public Properties getJNDIContext_DataSourceFruitore() throws Exception{
		try{
			return org.openspcoop2.utils.Utilities.readProperties("org.openspcoop2.datasource.tracciamento.fruitore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.fruitore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	
	
	/**
	 * Ritorna il nome del dataSource per l'accesso al database tracciamento erogatore
	 *
	 */
	public String getDataSourceErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.erogatore").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.erogatore':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database tracciamento erogatore
	 *
	 */
	public Properties getJNDIContext_DataSourceErogatore() throws Exception{
		try{
			return org.openspcoop2.utils.Utilities.readProperties("org.openspcoop2.datasource.tracciamento.erogatore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.erogatore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	
	/**
	 * Ritorna il nome del dataSource per l'accesso al database della PdD fruitore
	 *
	 */
	public String getDataSourcePdDFruitore() throws Exception{
		try{
			String p = this.reader.getProperty("org.openspcoop2.datasource.openspcoop.fruitore");
			if(p!=null)
				return p.trim();
			else
				return null;
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.fruitore':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database della PdD fruitore
	 *
	 */
	public Properties getJNDIContext_DataSourcePdDFruitore() throws Exception{
		try{
			return org.openspcoop2.utils.Utilities.readProperties("org.openspcoop2.datasource.openspcoop.fruitore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.fruitore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	
	
	/**
	 * Ritorna il nome del dataSource per l'accesso al database della PdD erogatore
	 *
	 */
	public String getDataSourcePdDErogatore() throws Exception{
		try{
			String p = this.reader.getProperty("org.openspcoop2.datasource.openspcoop.erogatore");
			if(p!=null)
				return p.trim();
			else
				return null;
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.erogatore':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database della PdD erogatore
	 *
	 */
	public Properties getJNDIContext_DataSourcePdDErogatore() throws Exception{
		try{
			return org.openspcoop2.utils.Utilities.readProperties("org.openspcoop2.datasource.openspcoop.erogatore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.erogatore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	/* ********  M E T O D I (Connections)  ******** */

	
	
	/**
	 * Ritorna la connection url del database tracciamento (erogatore)
	 *
	 */
	public String getConnectionUrlTracciamentoErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.erogatore.connection-url").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.erogatore.connection-url':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'username del database tracciamento (erogatore)
	 *
	 */
	public String getUsernameTracciamentoErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.erogatore.username").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.erogatore.username':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la password del database tracciamento (erogatore)
	 *
	 */
	public String getPasswordTracciamentoErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.erogatore.password").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.erogatore.password':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la connection url del database tracciamento (fruitore)
	 *
	 */
	public String getConnectionUrlTracciamentoFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.fruitore.connection-url").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.fruitore.connection-url':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'username del database tracciamento (fruitore)
	 *
	 */
	public String getUsernameTracciamentoFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.fruitore.username").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.fruitore.username':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la password del database tracciamento (fruitore)
	 *
	 */
	public String getPasswordTracciamentoFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.tracciamento.fruitore.password").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.fruitore.password':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la connection url del database openspcoop (erogatore)
	 *
	 */
	public String getConnectionUrlOpenspcoopErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.openspcoop.erogatore.connection-url").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.erogatore.connection-url':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'username del database openspcoop (erogatore)
	 *
	 */
	public String getUsernameOpenspcoopErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.openspcoop.erogatore.username").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.erogatore.username':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la password del database openspcoop (erogatore)
	 *
	 */
	public String getPasswordOpenspcoopErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.openspcoop.erogatore.password").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.erogatore.password':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la connection url del database openspcoop (fruitore)
	 *
	 */
	public String getConnectionUrlOpenspcoopFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.openspcoop.fruitore.connection-url").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.fruitore.connection-url':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'username del database openspcoop (fruitore)
	 *
	 */
	public String getUsernameOpenspcoopFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.openspcoop.fruitore.username").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.fruitore.username':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la password del database openspcoop (fruitore)
	 *
	 */
	public String getPasswordOpenspcoopFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.openspcoop.fruitore.password").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.fruitore.password':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	
	
	
	
	
	
	
	/* -------------------- Messaggi Diagnostici ----------------------- */
	
	
	/* ----------------------- Datasource ------------------------ */
	
	/**
	 * Ritorna il nome del dataSource per l'accesso al database della PdD fruitore per i msg diagnostici
	 *
	 */
	public String getDataSourceDiagnosticaPdDFruitore() throws Exception{
		try{
			String p = this.reader.getProperty("org.openspcoop2.datasource.diagnostica.fruitore");
			if(p!=null)
				return p.trim();
			else
				return null;
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.fruitore':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database della PdD fruitore per i msg diagnostici
	 *
	 */
	public Properties getJNDIContext_DataSourceDiagnosticaPdDFruitore() throws Exception{
		try{
			return org.openspcoop2.utils.Utilities.readProperties("org.openspcoop2.datasource.diagnostica.fruitore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.fruitore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	
	
	
	/**
	 * Ritorna il nome del dataSource per l'accesso al database della PdD erogatore dei msg diagnostici
	 *
	 */
	public String getDataSourceDiagnosticaPdDErogatore() throws Exception{
		try{
			String p = this.reader.getProperty("org.openspcoop2.datasource.diagnostica.erogatore");
			if(p!=null)
				return p.trim();
			else
				return null;
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.erogatore':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	/**
	 * Ritorna il contesto JNDI del dataSource per l'accesso al database della PdD erogatore
	 *
	 */
	public Properties getJNDIContext_DataSourceDiagnosticaPdDErogatore() throws Exception{
		try{
			return org.openspcoop2.utils.Utilities.readProperties("org.openspcoop2.datasource.diagnostica.erogatore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.erogatore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	

	
	/* ---------------------- Single Connection -------------------- */
	/**
	 * Ritorna la connection url del database diagnostica (erogatore)
	 *
	 */
	public String getConnectionUrlDiagnosticaErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.diagnostica.erogatore.connection-url").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.erogatore.connection-url':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'username del database diagnostica (erogatore)
	 *
	 */
	public String getUsernameDiagnosticaErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.diagnostica.erogatore.username").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.erogatore.username':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la password del database diagnostica (erogatore)
	 *
	 */
	public String getPasswordDiagnosticaErogatore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.diagnostica.erogatore.password").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.erogatore.password':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la connection url del database diagnostica (fruitore)
	 *
	 */
	public String getConnectionUrlDiagnosticaFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.diagnostica.fruitore.connection-url").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.fruitore.connection-url':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna l'username del database diagnostica (fruitore)
	 *
	 */
	public String getUsernameDiagnosticaFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.diagnostica.fruitore.username").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.fruitore.username':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
	
	/**
	 * Ritorna la password del database diagnostica (fruitore)
	 *
	 */
	public String getPasswordDiagnosticaFruitore() throws Exception{
		try{
			return this.reader.getProperty("org.openspcoop2.datasource.diagnostica.fruitore.password").trim();
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.diagnostica.fruitore.password':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}


}


