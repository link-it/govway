/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.logger;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/****
 * 
 * Classe manager per i logger
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
