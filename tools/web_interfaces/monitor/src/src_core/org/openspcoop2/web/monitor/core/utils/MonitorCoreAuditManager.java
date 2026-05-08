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
package org.openspcoop2.web.monitor.core.utils;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.driver.IDBuilder;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.appender.AuditAppender;
import org.openspcoop2.web.lib.audit.appender.AuditDBAppender;
import org.openspcoop2.web.lib.audit.appender.AuditLog4JAppender;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.AppenderProperty;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

/**
 * MonitorCoreAuditManager - Gestisce il singleton AuditAppender per la Console di Monitoraggio.
 *
 * Speculare a ControlStationCore.initializeAuditManager/getAuditManagerInstance.
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MonitorCoreAuditManager {

	private static final String AUDIT_LOG4J2_PROPERTIES_MONITOR = "monitor.audit.log4j2.properties";
	private static final String DB_INTERFACCIA_KEYWORD = "@DB_INTERFACCIA@";

	private static AuditAppender auditManager = null;
	private static Semaphore semaphoreAuditLock = new Semaphore("MonitorCoreAuditManager");

	private MonitorCoreAuditManager() {}

	public static void clearAuditManager(){
		SemaphoreLock lock = semaphoreAuditLock.acquireThrowRuntime("clearAuditManager");
		try {
			auditManager = null;
		}finally {
			semaphoreAuditLock.release(lock, "clearAuditManager");
		}
	}

	public static synchronized void initializeAuditManager() throws MonitorCoreAuditException {
		SemaphoreLock lock = semaphoreAuditLock.acquireThrowRuntime("initializeAuditManager");
		try {
			if(auditManager==null){
				auditManager = new AuditAppender();

				Configurazione configurazioneAuditing = null;
				try{
					Logger sqlLogger = LoggerManager.getPddMonitorSqlLogger();
					DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(sqlLogger);
					String tipoDatabase = daoFactoryProperties.getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
					String datasourceJNDIName = daoFactoryProperties.getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
					Properties datasourceJNDIContext = daoFactoryProperties.getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());

					DriverConfigurazioneDB driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName, datasourceJNDIContext, sqlLogger, tipoDatabase);

					Connection con = null;
					try{
						con = driverConfigDB.getConnection("initializeAuditManager");
						if (con == null)
							throw new MonitorCoreAuditException("Connessione non disponibile");

						DriverAudit driverAudit = new DriverAudit(con, tipoDatabase);
						configurazioneAuditing = driverAudit.getConfigurazione();
						checkAuditDBAppender(configurazioneAuditing, datasourceJNDIName, datasourceJNDIContext, tipoDatabase);
						checkAuditLog4jAppender(configurazioneAuditing);

					}finally{
						driverConfigDB.releaseConnection("initializeAuditManager", con);
					}
				}catch(Exception e){
					throw new MonitorCoreAuditException("Inizializzazione auditManager non riuscita (LetturaConfigurazione): "+e.getMessage(),e);
				}

				try{
					auditManager.initializeAudit(configurazioneAuditing, IDBuilder.getIDBuilder());
				}catch(Exception e){
					throw new MonitorCoreAuditException("Inizializzazione auditManager non riuscita: "+e.getMessage(),e);
				}
			}
		}finally {
			semaphoreAuditLock.release(lock, "initializeAuditManager");
		}
	}

	public static AuditAppender getAuditManagerInstance() throws MonitorCoreAuditException {
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
					if(DB_INTERFACCIA_KEYWORD.equals(appender.getProperty(j).getValue())){
						findDBKeyword = true;
					}
				}
				if(findDBKeyword){
					while(appender.sizeProperties()>0){
						appender.removeProperty(0);
					}
					AppenderProperty apDS = new AppenderProperty();
					apDS.setName("datasource");
					apDS.setValue(datasourceJNDIName);
					appender.addProperty(apDS);
					AppenderProperty apTipoDatabase = new AppenderProperty();
					apTipoDatabase.setName("tipoDatabase");
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
					if("fileConfigurazione".equals(p.getName())){
						p.setValue(AUDIT_LOG4J2_PROPERTIES_MONITOR);
					}
				}
			}
		}
	}
}
