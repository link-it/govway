package org.openspcoop2.web.monitor.core.logger;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/****
 * 
 * Classe manager per i logger
 * 
 * 
 * @author pintori
 *
 */
public class LoggerManager {

	
	private static Logger logger_govwayMonitor_core = null;
	private static Logger logger_govwayMonitor_sql = null;
	private static Logger logger_govwayMonitor_audit = null;
	
	private static synchronized void initialize(){
		if(LoggerManager.logger_govwayMonitor_core==null){
			LoggerManager.logger_govwayMonitor_core = LoggerWrapperFactory.getLogger("govwayMonitor.core");
		}
		if(LoggerManager.logger_govwayMonitor_sql==null){
			LoggerManager.logger_govwayMonitor_sql = LoggerWrapperFactory.getLogger("govwayMonitor.sql");
		}
		if(LoggerManager.logger_govwayMonitor_audit==null){
			LoggerManager.logger_govwayMonitor_audit = LoggerWrapperFactory.getLogger("govwayMonitor.audit");
		}
	}
	
	public static Logger getPddMonitorCoreLogger(){
		if(LoggerManager.logger_govwayMonitor_core==null){
			LoggerManager.initialize();
		}
		return LoggerManager.logger_govwayMonitor_core;
	}
	
	public static Logger getPddMonitorSqlLogger(){
		if(LoggerManager.logger_govwayMonitor_sql==null){
			LoggerManager.initialize();
		}
		return LoggerManager.logger_govwayMonitor_sql;
	}

	public static Logger getPddMonitorAuditLogger(){
		if(LoggerManager.logger_govwayMonitor_audit==null){
			LoggerManager.initialize();
		}
		return LoggerManager.logger_govwayMonitor_audit;
	}
}
