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
package org.openspcoop2.core.commons.dao;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

import java.util.Iterator;
import java.util.Properties;

import org.openspcoop2.utils.TipiDatabase;
import org.slf4j.Logger;


/**
 * DAOFactoryProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DAOFactoryProperties {


	/** Copia Statica */
	private static DAOFactoryProperties daoFactoryProperties = null;

	private static synchronized void initialize(Logger log) throws Exception{

		if(DAOFactoryProperties.daoFactoryProperties==null)
			DAOFactoryProperties.daoFactoryProperties = new DAOFactoryProperties(log);	

	}

	public static DAOFactoryProperties getInstance(Logger log) throws Exception{

		if(DAOFactoryProperties.daoFactoryProperties==null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED'
			synchronized (DAOFactoryProperties.class) {
				DAOFactoryProperties.initialize(log);
			}
		}

		return DAOFactoryProperties.daoFactoryProperties;
	}





	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'server.properties' */
	private DAOFactoryInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	protected DAOFactoryProperties(Logger log) throws Exception{

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReaded = new Properties();
		
		// internal (required)
		java.io.InputStream propertiesInternal = null;
		try{  
			propertiesInternal = DAOFactoryProperties.class.getResourceAsStream("/daoFactory.internal.properties");
			if(propertiesInternal==null){
				throw new Exception("Properties daoFactory.internal.properties not found");
			}
			propertiesReaded.load(propertiesInternal);
			propertiesInternal.close();
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file 'daoFactory.internal.properties': "+e.getMessage(),e);
			try{
				if(propertiesInternal!=null)
					propertiesInternal.close();
			}catch(Exception er){
				// close
			}
			throw e;
		}
		
		// opzionale e sovrascrive eventuali properties
		java.io.InputStream properties = null;
		try{  
			properties = DAOFactoryProperties.class.getResourceAsStream("/daoFactory.properties");
			if(properties!=null){
				Properties tmp = new Properties();
				tmp.load(properties);
				properties.close();
				if(tmp.size()>0) {
					Iterator<?> itTmp = tmp.keySet().iterator();
					while (itTmp.hasNext()) {
						Object oKey = (Object) itTmp.next();
						if(oKey instanceof String) {
							String key = (String) oKey;
							String value = tmp.getProperty(key);
							if(propertiesReaded.containsKey(key)) {
								propertiesReaded.remove(key);
							}
							propertiesReaded.setProperty(key, value);
						}
					}
				}
			}
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file 'daoFactory.properties': "+e.getMessage(),e);
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){
				// close
			}
			throw e;
		}	

		try{
			this.reader = new DAOFactoryInstanceProperties(propertiesReaded, log);
		}catch(Exception e){
			throw new DAOFactoryException(e.getMessage(),e);
		}
	}





	/* ********  P R O P E R T I E S  ******** */


	private String getProperty(String name,boolean required, boolean convertEnvProperty) throws Exception{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValueConvertEnvProperties(name);
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


	// NOME classe DAO
	public String getDAOClassName(IProjectInfo tipoDAO) throws Exception {
		return this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName(), true, false);
	}


	// DB

	private static final String PREFIX_FACTORY = "factory.";

	private static final String PROP_TIPO_DATABASE = "db.tipoDatabase";
	public String getTipoDatabase(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_TIPO_DATABASE, false, false);
		if(v==null){
			v = this.getProperty(PROP_TIPO_DATABASE, true, false);
		}
		return v;
	}
	public TipiDatabase getTipoDatabaseEnum(IProjectInfo tipoDAO) throws Exception {
		return TipiDatabase.toEnumConstant(this.getTipoDatabase(tipoDAO));
	}

	private static final String PROP_SHOW_SQL = "db.showSql";
	public boolean isShowSql(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_SHOW_SQL, false, false);
		if(v==null){
			v = this.getProperty(PROP_SHOW_SQL, true, false);
		}
		return Boolean.parseBoolean(v);
	}

	private static final String PROP_GENERATE_DDL = "db.generateDDL";
	public boolean isGenerateDDL(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_GENERATE_DDL, false, false);
		if(v==null){
			v = this.getProperty(PROP_GENERATE_DDL, true, false);
		}
		return Boolean.parseBoolean(v);
	}

	private static final String PROP_AUTO_COMMIT = "db.autoCommit";
	public boolean isAutoCommit(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_AUTO_COMMIT, false, false);
		if(v==null){
			v = this.getProperty(PROP_AUTO_COMMIT, true, false);
		}
		return Boolean.parseBoolean(v);
	}
	
	private static final String PROP_SECONDS_TO_REFRESH_CONNECTION = "db.secondsToRefreshConnection";
	public int getSecondsToRefreshConnection(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_SECONDS_TO_REFRESH_CONNECTION, false, false);
		if(v==null){
			v = this.getProperty(PROP_SECONDS_TO_REFRESH_CONNECTION, true, false);
		}
		return Integer.parseInt(v);
	}

	public ServiceManagerProperties getServiceManagerProperties(IProjectInfo tipoDAO) throws Exception{
		ServiceManagerProperties sm = new ServiceManagerProperties();
		sm.setDatabaseType(this.getTipoDatabase(tipoDAO));
		sm.setShowSql(this.isShowSql(tipoDAO));
		sm.setGenerateDdl(this.isGenerateDDL(tipoDAO));
		sm.setAutomaticTransactionManagement(this.isAutoCommit(tipoDAO));
		sm.setSecondsToRefreshConnection(this.getSecondsToRefreshConnection(tipoDAO));
		return sm;
	}

	private static final String PROP_TIPO = "db.tipo";
	protected static final String PROP_TIPO_VALUE_DATASOURCE = "datasource";
	protected static final String PROP_TIPO_VALUE_CONNECTION = "connection";
	protected String getTipoAccessoDatabase(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_TIPO, false, true);
		if(v==null){
			v = this.getProperty(PROP_TIPO, true, true);
		}
		if(!PROP_TIPO_VALUE_DATASOURCE.equals(v) && !PROP_TIPO_VALUE_CONNECTION.equals(v)){
			throw new Exception("Tipo di accesso al database fornito ["+v+"] non valido (supportati "+PROP_TIPO_VALUE_DATASOURCE+","+PROP_TIPO_VALUE_CONNECTION+")");
		}
		return v;
	}
	public boolean isTipoAccessoTramiteDatasource(IProjectInfo tipoDAO) throws Exception{
		return PROP_TIPO_VALUE_DATASOURCE.equals(this.getTipoAccessoDatabase(tipoDAO));
	}
	public boolean isTipoAccessoTramiteConnection(IProjectInfo tipoDAO) throws Exception{
		return PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase(tipoDAO));
	}

	private static final String PROP_DATASOURCE_JNDI_NAME = "db.datasource.jndiName";
	public String getDatasourceJNDIName(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_DATASOURCE_JNDI_NAME, false, true);
		if(v==null){
			v = this.getProperty(PROP_DATASOURCE_JNDI_NAME, PROP_TIPO_VALUE_DATASOURCE.equals(this.getTipoAccessoDatabase(tipoDAO)), true);
		}
		return v;
	}
	private static final String PROP_DATASOURCE_JNDI_CONTEXT = "db.datasource.jndiContext";
	public Properties getDatasourceJNDIContext(IProjectInfo tipoDAO) throws Exception {
		Properties p = this.reader.readPropertiesConvertEnvProperties(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_DATASOURCE_JNDI_CONTEXT);
		if(p==null || p.size()<=0){
			p = this.reader.readPropertiesConvertEnvProperties(PROP_DATASOURCE_JNDI_CONTEXT);
		}
		return p;
	}

	private static final String PROP_CONNECTION_URL = "db.connection.url";
	public String getConnectionUrl(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_CONNECTION_URL, false, true);
		if(v==null){
			v = this.getProperty(PROP_CONNECTION_URL, true, true);
		}
		return v;
	}
	private static final String PROP_CONNECTION_DRIVER = "db.connection.driver";
	public String getConnectionDriverJDBC(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_CONNECTION_DRIVER, false, true);
		if(v==null){
			v = this.getProperty(PROP_CONNECTION_DRIVER, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase(tipoDAO)), true);
		}
		return v;
	}
	private static final String PROP_CONNECTION_AUTH_USER = "db.connection.user";
	public String getConnectionAuthUsername(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_CONNECTION_AUTH_USER, false, true);
		if(v==null){
			v = this.getProperty(PROP_CONNECTION_AUTH_USER, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase(tipoDAO)), true);
		}
		return v;
	}
	private static final String PROP_CONNECTION_AUTH_PASSWORD = "db.connection.password";
	public String getConnectionAuthPassword(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_CONNECTION_AUTH_PASSWORD, false, true);
		if(v==null){
			v = this.getProperty(PROP_CONNECTION_AUTH_PASSWORD, PROP_TIPO_VALUE_CONNECTION.equals(this.getTipoAccessoDatabase(tipoDAO)), true);
		}
		return v;
	}

}
