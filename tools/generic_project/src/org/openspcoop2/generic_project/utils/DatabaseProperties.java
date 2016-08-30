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
package org.openspcoop2.generic_project.utils;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

import java.util.Properties;

import org.openspcoop2.utils.TipiDatabase;


/**
 * DatabaseProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatabaseProperties {

	
	/** Copia Statica */
	private static DatabaseProperties databaseProperties = null;

	public static synchronized void initialize(String PROPERTIES_LOCAL_PATH, String PROPERTIES_NAME, String nomeFileProperties,org.slf4j.Logger log) throws ServiceException{

		if(DatabaseProperties.databaseProperties==null)
			DatabaseProperties.databaseProperties = new DatabaseProperties(PROPERTIES_LOCAL_PATH,PROPERTIES_NAME,nomeFileProperties,log);	

	}

	public static DatabaseProperties getInstance(org.slf4j.Logger log) throws ServiceException{

		if(DatabaseProperties.databaseProperties==null){
			throw new ServiceException("DatabaseProperties not initialized, use initialize method");
		}
		return DatabaseProperties.databaseProperties;
	}
	
	
	
	
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'database.properties' */
	private DatabaseInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public DatabaseProperties(String PROPERTIES_LOCAL_PATH, String PROPERTIES_NAME, String nomeFileProperties,org.slf4j.Logger log) throws ServiceException{

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DatabaseProperties.class.getResourceAsStream("/"+nomeFileProperties);
			if(properties==null){
				throw new ServiceException("Properties "+nomeFileProperties+" not found");
			}
			propertiesReader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file '"+nomeFileProperties+"': "+e.getMessage(),e);
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new ServiceException(e.getMessage(),e);
		}	
	
		try{
			this.reader = new DatabaseInstanceProperties(PROPERTIES_LOCAL_PATH,PROPERTIES_NAME,propertiesReader, log);
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ********  P R O P E R T I E S  ******** */


	private String getProperty(String name,boolean required, boolean convertEnvProperty) throws Exception{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValue_convertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null){
			if(required){
				throw new Exception("Property ["+name+"] not found");
			}
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}
	

	

	// DB

	
	private static final String PROP_TIPO_DATABASE = "db.tipoDatabase";
	public String getTipoDatabase() throws Exception {
		return this.getProperty(PROP_TIPO_DATABASE, true, false);
	}
	public TipiDatabase getTipoDatabaseEnum() throws Exception {
		return TipiDatabase.toEnumConstant(this.getTipoDatabase());
	}
	
	private static final String PROP_SHOW_SQL = "db.showSql";
	public boolean isShowSql() throws Exception {
		return Boolean.parseBoolean(this.getProperty(PROP_SHOW_SQL, true, false));
	}
	
	private static final String PROP_GENERATE_DDL = "db.generateDDL";
	public boolean isGenerateDDL() throws Exception {
		return Boolean.parseBoolean(this.getProperty(PROP_GENERATE_DDL, true, false));
	}
	
	private static final String PROP_AUTO_COMMIT = "db.autoCommit";
	public boolean isAutoCommit() throws Exception {
		return Boolean.parseBoolean(this.getProperty(PROP_AUTO_COMMIT, true, false));
	}
	
	public ServiceManagerProperties getServiceManagerProperties() throws Exception{
		ServiceManagerProperties sm = new ServiceManagerProperties();
		sm.setDatabaseType(this.getTipoDatabase());
		sm.setShowSql(this.isShowSql());
		sm.setGenerateDdl(this.isGenerateDDL());
		sm.setAutomaticTransactionManagement(this.isAutoCommit());
		return sm;
	}
	
	private static final String PROP_TIPO = "db.tipo";
	private static final String PROP_TIPO_VALUE_DATASOURCE = "datasource";
	private static final String PROP_TIPO_VALUE_CONNECTION = "connection";
	private String getTipoAccessoDatabase() throws Exception {
		String v = this.getProperty(PROP_TIPO, true, true);
		if(!PROP_TIPO_VALUE_DATASOURCE.equals(v) && !PROP_TIPO_VALUE_CONNECTION.equals(v)){
			throw new Exception("Tipo di accesso al database fornito ["+v+"] non valido (supportati "+PROP_TIPO_VALUE_DATASOURCE+","+PROP_TIPO_VALUE_CONNECTION+")");
		}
		return v;
	}
	public boolean isTipoAccessoTramiteDatasource() throws Exception{
		return PROP_TIPO_VALUE_DATASOURCE.equals(this.getTipoAccessoDatabase());
	}
	public boolean isTipoAccessoTramiteConnection() throws Exception{
		return PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase());
	}
	
	private static final String PROP_DATASOURCE_JNDI_NAME = "db.datasource.jndiName";
	public String getDatasourceJNDIName() throws Exception {
		return this.getProperty(PROP_DATASOURCE_JNDI_NAME, PROP_TIPO_VALUE_DATASOURCE.equals(this.getTipoAccessoDatabase()), true);
	}
	private static final String PROP_DATASOURCE_JNDI_CONTEXT = "db.datasource.jndiContext";
	public Properties getDatasourceJNDIContext() throws Exception {
		return this.reader.readProperties_convertEnvProperties(PROP_DATASOURCE_JNDI_CONTEXT);
	}
	
	private static final String PROP_CONNECTION_URL = "db.connection.url";
	public String getConnectionUrl() throws Exception {
		return this.getProperty(PROP_CONNECTION_URL, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);
	}
	private static final String PROP_CONNECTION_DRIVER = "db.connection.driver";
	public String getConnectionDriverJDBC() throws Exception {
		return this.getProperty(PROP_CONNECTION_DRIVER, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);
	}
	private static final String PROP_CONNECTION_AUTH_USER = "db.connection.user";
	public String getConnectionAuthUsername() throws Exception {
		//return this.getProperty(PROP_CONNECTION_AUTH_USER, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);
		return this.getProperty(PROP_CONNECTION_AUTH_USER, false, true);
	}
	private static final String PROP_CONNECTION_AUTH_PASSWORD = "db.connection.password";
	public String getConnectionAuthPassword() throws Exception {
		//return this.getProperty(PROP_CONNECTION_AUTH_PASSWORD, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);
		return this.getProperty(PROP_CONNECTION_AUTH_PASSWORD, false, true);
	}
	
}
