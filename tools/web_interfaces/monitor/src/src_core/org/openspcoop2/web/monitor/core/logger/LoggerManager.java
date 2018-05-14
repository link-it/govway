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

	
	private static Logger logger_pddMonitor_core = null;
	private static Logger logger_pddMonitor_sql = null;
	private static Logger logger_pddMonitor_audit = null;
	
	private static synchronized void initialize(){
		if(LoggerManager.logger_pddMonitor_core==null){
			LoggerManager.logger_pddMonitor_core = LoggerWrapperFactory.getLogger("pddMonitor.core");
		}
		if(LoggerManager.logger_pddMonitor_sql==null){
			LoggerManager.logger_pddMonitor_sql = LoggerWrapperFactory.getLogger("pddMonitor.sql");
		}
		if(LoggerManager.logger_pddMonitor_audit==null){
			LoggerManager.logger_pddMonitor_audit = LoggerWrapperFactory.getLogger("pddMonitor.audit");
		}
	}
	
	public static Logger getPddMonitorCoreLogger(){
		if(LoggerManager.logger_pddMonitor_core==null){
			LoggerManager.initialize();
		}
		return LoggerManager.logger_pddMonitor_core;
	}
	
	public static Logger getPddMonitorSqlLogger(){
		if(LoggerManager.logger_pddMonitor_sql==null){
			LoggerManager.initialize();
		}
		return LoggerManager.logger_pddMonitor_sql;
	}

	public static Logger getPddMonitorAuditLogger(){
		if(LoggerManager.logger_pddMonitor_audit==null){
			LoggerManager.initialize();
		}
		return LoggerManager.logger_pddMonitor_audit;
	}
}
