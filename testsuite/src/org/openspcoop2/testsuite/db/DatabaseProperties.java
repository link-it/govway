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



package org.openspcoop2.testsuite.db;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;

/**
 * Reader delle proprieta' per l'accesso al Database
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatabaseProperties {

	/** Logger utilizzato per errori eventuali. */
	private static Logger log = LoggerWrapperFactory.getLogger("DatabaseProperties");

	/** File di configurazione dell'accesso al database */
	public static final String DATABASE_PROPERTIES = "database.properties";

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'database.properties' */
	private Properties reader;

	/** Copia Statica */
	private static DatabaseProperties databaseProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 */
	public DatabaseProperties() throws Exception {

		/* ---- Lettura del cammino del file di configurazione ---- */
		this.reader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DatabaseProperties.class.getResourceAsStream("/"+DATABASE_PROPERTIES);
			this.reader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file '"+DATABASE_PROPERTIES+"': \n\n"+e.getMessage());
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
	public static boolean initialize(){

		try {
			DatabaseProperties.databaseProperties = new DatabaseProperties();	
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
	public static DatabaseProperties getInstance(){
		if(DatabaseProperties.databaseProperties==null)
			DatabaseProperties.initialize();
		return DatabaseProperties.databaseProperties;
	}




	private static DatabaseComponent databaseErogatore = null;
	public static DatabaseComponent getDatabaseComponentErogatore(String protocollo){
		if(databaseErogatore==null){
			synchronized(DatabaseProperties.getInstance()){
				try{
					DatabaseProperties dbP = DatabaseProperties.getInstance();
					if(dbP.databasePdD_is_DatabaseTracciamento()){
						return new DatabaseComponent(dbP.getDataSourceErogatore(),dbP.getJNDIContext_DataSourceErogatore(),protocollo);
					}else  {
						return new DatabaseComponent(dbP.getDataSourceErogatore(),dbP.getJNDIContext_DataSourceErogatore(),
								dbP.getDataSourcePdDErogatore(),dbP.getJNDIContext_DataSourcePdDErogatore(),protocollo);
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
	public static DatabaseComponent getDatabaseComponentFruitore(String protocollo){
		if(databaseFruitore==null){
			synchronized(DatabaseProperties.getInstance()){
				try{
					DatabaseProperties dbP = DatabaseProperties.getInstance();
					if(dbP.databasePdD_is_DatabaseTracciamento()){
						return new DatabaseComponent(dbP.getDataSourceFruitore(),dbP.getJNDIContext_DataSourceFruitore(),protocollo);
					}else  {
						return new DatabaseComponent(dbP.getDataSourceFruitore(),dbP.getJNDIContext_DataSourceFruitore(),
								dbP.getDataSourcePdDFruitore(),dbP.getJNDIContext_DataSourcePdDFruitore(),protocollo);
					}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}
		return databaseFruitore;
	}





	
	
	
	
	
	/* ********  M E T O D I  ******** */

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
			return Utilities.readProperties("org.openspcoop2.datasource.tracciamento.fruitore.property.",this.reader);
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
			return Utilities.readProperties("org.openspcoop2.datasource.tracciamento.erogatore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.tracciamento.erogatore.property.':"+e.getMessage();
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
			return Utilities.readProperties("org.openspcoop2.datasource.openspcoop.fruitore.property.",this.reader);
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
			return Utilities.readProperties("org.openspcoop2.datasource.openspcoop.erogatore.property.",this.reader);
		}catch(Exception e){
			String msgErrore = "DatabaseProperties, errore durante la lettura della proprieta' 'org.openspcoop2.datasource.openspcoop.erogatore.property.':"+e.getMessage();
			log.error(msgErrore);
			throw new Exception(msgErrore);
		}
	}
}
