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
package org.openspcoop2.core.monitor.rs.server.config;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.config.driver.IDBuilder;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.appender.AuditAppender;
import org.openspcoop2.web.lib.audit.appender.AuditDBAppender;
import org.openspcoop2.web.lib.audit.appender.AuditLog4JAppender;
import org.openspcoop2.web.lib.audit.costanti.Costanti;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.AppenderProperty;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.slf4j.Logger;

/**
 * Singleton AuditAppender per le API REST di monitoraggio.
 *
 * Replica il pattern di {@code MonitorCoreAuditManager} della Console di Monitoraggio:
 * legge la configurazione audit dal DB di configurazione (condivisa con Console JSF e
 * Monitor JSF), normalizza l'appender DB con il datasource locale dell'API, e
 * sostituisce a runtime il file log4j2 dell'appender Log4J con
 * {@code monitor.audit.log4j2.properties} incluso nel WAR.
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ApiMonitorAuditManager {

	private static final String AUDIT_LOG4J2_PROPERTIES_API_MONITOR = "monitor.audit.log4j2.properties";

	private static AuditAppender auditManager = null;
	private static Semaphore semaphoreAuditLock = new Semaphore("ApiMonitorAuditManager");

	private ApiMonitorAuditManager() {}

	private static final String MODULE_ID = "initializeAuditManager";
	
	public static void clearAuditManager(){
		SemaphoreLock lock = semaphoreAuditLock.acquireThrowRuntime("clearAuditManager");
		try {
			auditManager = null;
		}finally {
			semaphoreAuditLock.release(lock, "clearAuditManager");
		}
	}

	public static synchronized void initializeAuditManager() throws ApiMonitorAuditException {
		SemaphoreLock lock = semaphoreAuditLock.acquireThrowRuntime(MODULE_ID);
		try {
			if(auditManager==null){
				auditManager = new AuditAppender();

				Logger sqlLogger = LoggerWrapperFactory.getLogger(ApiMonitorAuditManager.class);

				Configurazione configurazioneAuditing = null;
				try {
					DBManager dbManager = DBManager.getInstance();
					String tipoDatabase = dbManager.getServiceManagerPropertiesConfig().getDatabaseType();
					String datasourceJNDIName = dbManager.getDataSourceConfigName();
					Properties datasourceJNDIContext = dbManager.getDataSourceConfigContext();

					DriverConfigurazioneDB driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName, datasourceJNDIContext, sqlLogger, tipoDatabase);

					Connection con = null;
					try {
						con = driverConfigDB.getConnection(MODULE_ID);
						if(con==null) {
							throw new ApiMonitorAuditException("Connessione non disponibile");
						}
						DriverAudit driverAudit = new DriverAudit(con, tipoDatabase);
						configurazioneAuditing = driverAudit.getConfigurazione();
						checkAuditDBAppender(configurazioneAuditing, datasourceJNDIName, datasourceJNDIContext, tipoDatabase);
						checkAuditLog4jAppender(configurazioneAuditing);
					} finally {
						driverConfigDB.releaseConnection(MODULE_ID, con);
					}
				} catch(Exception e) {
					throw new ApiMonitorAuditException("Inizializzazione auditManager non riuscita (LetturaConfigurazione): "+e.getMessage(), e);
				}

				try {
					auditManager.initializeAudit(configurazioneAuditing, IDBuilder.getIDBuilder());
				} catch(Exception e) {
					throw new ApiMonitorAuditException("Inizializzazione auditManager non riuscita: "+e.getMessage(), e);
				}
			}
		} finally {
			semaphoreAuditLock.release(lock, MODULE_ID);
		}
	}

	public static AuditAppender getAuditManagerInstance() throws ApiMonitorAuditException {
		if(auditManager==null){
			initializeAuditManager();
		}
		return auditManager;
	}

	private static void checkAuditDBAppender(Configurazione configurazioneAuditing,
			String datasourceJNDIName, Properties datasourceJNDIContext, String tipoDatabase){
		for(int i=0; i<configurazioneAuditing.sizeAppender(); i++){
			Appender appender = configurazioneAuditing.getAppender(i);
			if(AuditDBAppender.class.getName().equals(appender.getClassName())){
				boolean findDBKeyword = false;
				for(int j=0; j<appender.sizeProperties(); j++){
					if(Costanti.AUDIT_APPENDER_DB_KEYWORD_INTERFACCIA.equals(appender.getProperty(j).getValue())){
						findDBKeyword = true;
					}
				}
				if(findDBKeyword){
					while(appender.sizeProperties()>0){
						appender.removeProperty(0);
					}
					AppenderProperty apDS = new AppenderProperty();
					apDS.setName(Costanti.AUDIT_APPENDER_DB_PROPERTY_DATASOURCE);
					apDS.setValue(datasourceJNDIName);
					appender.addProperty(apDS);
					AppenderProperty apTipoDatabase = new AppenderProperty();
					apTipoDatabase.setName(Costanti.AUDIT_APPENDER_DB_PROPERTY_TIPO_DATABASE);
					apTipoDatabase.setValue(tipoDatabase);
					appender.addProperty(apTipoDatabase);
					if(datasourceJNDIContext!=null && !datasourceJNDIContext.isEmpty()){
						Enumeration<?> keys = datasourceJNDIContext.keys();
						while(keys.hasMoreElements()){
							String key = (String) keys.nextElement();
							AppenderProperty apCTX = new AppenderProperty();
							apCTX.setName(key);
							apCTX.setValue(datasourceJNDIContext.getProperty(key));
							appender.addProperty(apCTX);
						}
					}
				}
			}
		}
	}

	private static void checkAuditLog4jAppender(Configurazione configurazioneAuditing){
		for(int i=0; i<configurazioneAuditing.sizeAppender(); i++){
			Appender appender = configurazioneAuditing.getAppender(i);
			if(AuditLog4JAppender.class.getName().equals(appender.getClassName())){
				for(int j=0; j<appender.sizeProperties(); j++){
					AppenderProperty p = appender.getProperty(j);
					if(Costanti.AUDIT_APPENDER_LOG4J_PROPERTY_FILE_CONFIGURAZIONE.equals(p.getName())){
						p.setValue(AUDIT_LOG4J2_PROPERTIES_API_MONITOR);
					}
				}
			}
		}
	}
}
