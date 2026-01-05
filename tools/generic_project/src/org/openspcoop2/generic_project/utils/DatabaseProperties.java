/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.utils;

import org.openspcoop2.generic_project.exception.ServiceException;
import java.util.Properties;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;


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

	public static synchronized void initialize(String propertiesLocalPath, String propertiesName, String nomeFileProperties,org.slf4j.Logger log) throws ServiceException{

		if(DatabaseProperties.databaseProperties==null)
			DatabaseProperties.databaseProperties = new DatabaseProperties(propertiesLocalPath,propertiesName,nomeFileProperties,log);	

	}

	public static DatabaseProperties getInstance(org.slf4j.Logger log) throws ServiceException{
		if(log!=null) {
			// unused
		}
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if(DatabaseProperties.databaseProperties==null){
			synchronized (DatabaseProperties.class) {
				if(DatabaseProperties.databaseProperties==null){
					throw new ServiceException("DatabaseProperties not initialized, use initialize method");
				}
			}
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
	private DatabaseProperties(String propertiesLocalPath, String propertiesName, String nomeFileProperties,org.slf4j.Logger log) throws ServiceException{

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
		}catch(Exception e) {
			doError(log, nomeFileProperties, properties, e);
		}	
	
		try{
			this.reader = new DatabaseInstanceProperties(propertiesLocalPath,propertiesName,propertiesReader, log);
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}
	private void doError(org.slf4j.Logger log, String nomeFileProperties, java.io.InputStream properties, Exception e) throws ServiceException {
		String msg = "Riscontrato errore durante la lettura del file '"+nomeFileProperties+"': "+e.getMessage();
		log.error(msg,e);
		try{
			if(properties!=null)
				properties.close();
		}catch(Exception er){
			// close
		}
		throw new ServiceException(e.getMessage(),e);
	}

	
	
	
	
	/* ********  P R O P E R T I E S  ******** */


	private String getProperty(String name,boolean required, boolean convertEnvProperty) throws UtilsException{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValueConvertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null &&
			required){
			throw new UtilsException("Property ["+name+"] not found");
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}
	

	

	// DB

	
	private static final String PROP_TIPO_DATABASE = "db.tipoDatabase";
	public String getTipoDatabase() throws UtilsException {
		return this.getProperty(DatabaseProperties.PROP_TIPO_DATABASE, true, false);
	}
	public TipiDatabase getTipoDatabaseEnum() throws UtilsException {
		return TipiDatabase.toEnumConstant(this.getTipoDatabase());
	}
	
	private static final String PROP_SHOW_SQL = "db.showSql";
	public boolean isShowSql() throws UtilsException {
		return Boolean.parseBoolean(this.getProperty(DatabaseProperties.PROP_SHOW_SQL, true, false));
	}
	
	private static final String PROP_GENERATE_DDL = "db.generateDDL";
	public boolean isGenerateDDL() throws UtilsException {
		return Boolean.parseBoolean(this.getProperty(DatabaseProperties.PROP_GENERATE_DDL, true, false));
	}
	
	private static final String PROP_AUTO_COMMIT = "db.autoCommit";
	public boolean isAutoCommit() throws UtilsException {
		return Boolean.parseBoolean(this.getProperty(DatabaseProperties.PROP_AUTO_COMMIT, true, false));
	}
	
	private static final String PROP_SECONDS_TO_REFRESH_CONNECTION = "db.secondsToRefreshConnection";
	public int getSecondsToRefreshConnection() throws UtilsException {
		return Integer.parseInt(this.getProperty(DatabaseProperties.PROP_SECONDS_TO_REFRESH_CONNECTION, true, false));
	}
	
	public ServiceManagerProperties getServiceManagerProperties() throws UtilsException{
		ServiceManagerProperties sm = new ServiceManagerProperties();
		sm.setDatabaseType(this.getTipoDatabase());
		sm.setShowSql(this.isShowSql());
		sm.setGenerateDdl(this.isGenerateDDL());
		sm.setAutomaticTransactionManagement(this.isAutoCommit());
		sm.setSecondsToRefreshConnection(this.getSecondsToRefreshConnection());
		return sm;
	}
	
	private static final String PROP_TIPO = "db.tipo";
	private static final String PROP_TIPO_VALUE_DATASOURCE = "datasource";
	private static final String PROP_TIPO_VALUE_CONNECTION = "connection";
	private String getTipoAccessoDatabase() throws UtilsException {
		String v = this.getProperty(DatabaseProperties.PROP_TIPO, true, true);
		if(!DatabaseProperties.PROP_TIPO_VALUE_DATASOURCE.equals(v) && !DatabaseProperties.PROP_TIPO_VALUE_CONNECTION.equals(v)){
			throw new UtilsException("Tipo di accesso al database fornito ["+v+"] non valido (supportati "+DatabaseProperties.PROP_TIPO_VALUE_DATASOURCE+","+DatabaseProperties.PROP_TIPO_VALUE_CONNECTION+")");
		}
		return v;
	}
	public boolean isTipoAccessoTramiteDatasource() throws UtilsException{
		return DatabaseProperties.PROP_TIPO_VALUE_DATASOURCE.equals(this.getTipoAccessoDatabase());
	}
	public boolean isTipoAccessoTramiteConnection() throws UtilsException{
		return DatabaseProperties.PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase());
	}
	
	private static final String PROP_DATASOURCE_JNDI_NAME = "db.datasource.jndiName";
	public String getDatasourceJNDIName() throws UtilsException {
		return this.getProperty(DatabaseProperties.PROP_DATASOURCE_JNDI_NAME, DatabaseProperties.PROP_TIPO_VALUE_DATASOURCE.equals(this.getTipoAccessoDatabase()), true);
	}
	private static final String PROP_DATASOURCE_JNDI_CONTEXT = "db.datasource.jndiContext";
	public Properties getDatasourceJNDIContext() throws UtilsException {
		return this.reader.readPropertiesConvertEnvProperties(DatabaseProperties.PROP_DATASOURCE_JNDI_CONTEXT);
	}
	
	private static final String PROP_CONNECTION_URL = "db.connection.url";
	public String getConnectionUrl() throws UtilsException {
		return this.getProperty(DatabaseProperties.PROP_CONNECTION_URL, DatabaseProperties.PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);
	}
	private static final String PROP_CONNECTION_DRIVER = "db.connection.driver";
	public String getConnectionDriverJDBC() throws UtilsException {
		return this.getProperty(DatabaseProperties.PROP_CONNECTION_DRIVER, DatabaseProperties.PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);
	}
	private static final String PROP_CONNECTION_AUTH_USER = "db.connection.user";
	public String getConnectionAuthUsername() throws UtilsException {
		/**return this.getProperty(PROP_CONNECTION_AUTH_USER, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);*/
		return this.getProperty(DatabaseProperties.PROP_CONNECTION_AUTH_USER, false, true);
	}
	private static final String PROP_CONNECTION_AUTH_PASSWORD = "db.connection.password";
	public String getConnectionAuthPassword() throws UtilsException {
		/**return this.getProperty(PROP_CONNECTION_AUTH_PASSWORD, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase()), true);*/
		return this.getProperty(DatabaseProperties.PROP_CONNECTION_AUTH_PASSWORD, false, true);
	}
	
}
