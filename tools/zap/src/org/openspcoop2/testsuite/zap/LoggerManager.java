/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.testsuite.zap;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * LoggerManager
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoggerManager {
	
	private LoggerManager() {}

	private static Logger log = null;
	private static synchronized void initLogger() {
		if(log==null) {
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.DEBUG);
			log = LoggerWrapperFactory.getLogger(LoggerManager.class);
		}
	}
	private static Logger getLogger() {
		if(log==null) { 
			initLogger();
		}
		return log;
	}
	public static void debug(String msg) {
		getLogger().debug(msg);
	}
	public static void info(String msg) {
		getLogger().info(msg);
	}
	public static void error(String msg) {
		getLogger().error(msg);
	}
	public static void error(String msg, Exception e) {
		getLogger().error(msg, e);
	}
	
}
