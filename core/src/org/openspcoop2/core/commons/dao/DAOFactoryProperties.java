package org.openspcoop2.core.commons.dao;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

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

		if(DAOFactoryProperties.daoFactoryProperties==null)
			DAOFactoryProperties.initialize(log);

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
	public DAOFactoryProperties(Logger log) throws Exception{

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DAOFactoryProperties.class.getResourceAsStream("/daoFactory.properties");
			if(properties==null){
				throw new Exception("Properties daoFactory.properties not found");
			}
			propertiesReader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			log.error("Riscontrato errore durante la lettura del file 'daoFactory.properties': "+e.getMessage(),e);
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw e;
		}	

		try{
			this.reader = new DAOFactoryInstanceProperties(propertiesReader, log);
		}catch(Exception e){
			throw new DAOFactoryException(e.getMessage(),e);
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

	public ServiceManagerProperties getServiceManagerProperties(IProjectInfo tipoDAO) throws Exception{
		ServiceManagerProperties sm = new ServiceManagerProperties();
		sm.setDatabaseType(this.getTipoDatabase(tipoDAO));
		sm.setShowSql(this.isShowSql(tipoDAO));
		sm.setGenerateDdl(this.isGenerateDDL(tipoDAO));
		sm.setAutomaticTransactionManagement(this.isAutoCommit(tipoDAO));
		return sm;
	}

	private static final String PROP_TIPO = "db.tipo";
	private static final String PROP_TIPO_VALUE_DATASOURCE = "datasource";
	private static final String PROP_TIPO_VALUE_CONNECTION = "connection";
	private String getTipoAccessoDatabase(IProjectInfo tipoDAO) throws Exception {
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
		Properties p = this.reader.readProperties_convertEnvProperties(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_DATASOURCE_JNDI_CONTEXT);
		if(p==null || p.size()<=0){
			p = this.reader.readProperties_convertEnvProperties(PROP_DATASOURCE_JNDI_CONTEXT);
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


	// Retrocompatibilita con openspcoop1
	private static final String PROP_BACKWARD_COMPATIBILITY_OPENSPCOOP1 = "backwardCompatibilityOpenspcoop1";
	public boolean isBackwardCompatibilityOpenspcoop1(IProjectInfo tipoDAO) throws Exception{
		return "true".equals(this.getBackwardCompatibilityOpenspcoop1(tipoDAO));
	}

	private String getBackwardCompatibilityOpenspcoop1(IProjectInfo tipoDAO) throws Exception {
		String v = this.getProperty(PREFIX_FACTORY+tipoDAO.getProjectName()+"."+PROP_BACKWARD_COMPATIBILITY_OPENSPCOOP1, false, true);
		if(v==null){
			v = this.getProperty(PROP_BACKWARD_COMPATIBILITY_OPENSPCOOP1, true, true);
		}

		return v;
	}
}
