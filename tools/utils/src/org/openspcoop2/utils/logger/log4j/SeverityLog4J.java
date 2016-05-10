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
package org.openspcoop2.utils.logger.log4j;

import org.apache.log4j.Level;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * SeverityLog4J
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SeverityLog4J extends org.apache.log4j.Level{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Definisce un Livello di Severita' FATAL per un messaggio Diagnostico: valore Log4J = 50001 */
	public static final Level LOG_LEVEL_FATAL = 
		new SeverityLog4J(Severity.FATAL,50001);

	/** Definisce un Livello di Severita' ERROR per un messaggio Diagnostico: valore Log4J = 40001 */
	public static final Level LOG_LEVEL_ERROR = 
		new SeverityLog4J(Severity.ERROR,40001);

	/** Definisce un Livello di Severita' WARN per un messaggio Diagnostico: valore Log4J = 30001 */
	public static final Level LOG_LEVEL_WARN = 
		new SeverityLog4J(Severity.WARN,30001);

	/** Definisce un Livello di Severita' INFO per un messaggio Diagnostico: valore Log4J = 20001 */
	public static final Level LOG_LEVEL_INFO = 
		new SeverityLog4J(Severity.INFO,20001);

	/** Definisce un Livello di Severita' DEBUG-LOW per un messaggio Diagnostico: valore Log4J = 10003 */
	public static final Level LOG_LEVEL_DEBUG_LOW = 
		new SeverityLog4J(Severity.DEBUG_LOW,10003);

	/** Definisce un Livello di Severita' DEBUG-MEDIUM per un messaggio Diagnostico: valore Log4J = 10002 */
	public static final Level LOG_LEVEL_DEBUG_MEDIUM = 
		new SeverityLog4J(Severity.DEBUG_MEDIUM,10002);

	/** Definisce un Livello di Severita' DEBUG-HIGH per un messaggio Diagnostico: valore Log4J = 10001 */
	public static final Level LOG_LEVEL_DEBUG_HIGH = 
		new SeverityLog4J(Severity.DEBUG_HIGH,10001);
	
	protected SeverityLog4J(Severity levelStr,int level) {
		super(level, "OP_"+levelStr.name(), level);
	}

	public static Level getSeverityLog4J(Severity severity){
		switch (severity) {
		case FATAL:
			return LOG_LEVEL_FATAL;
		case ERROR:
			return LOG_LEVEL_ERROR;
		case WARN:
			return LOG_LEVEL_WARN;
		case INFO:
			return LOG_LEVEL_INFO;
		case DEBUG_LOW:
			return LOG_LEVEL_DEBUG_LOW;
		case DEBUG_MEDIUM:
			return LOG_LEVEL_DEBUG_MEDIUM;
		case DEBUG_HIGH:
			return LOG_LEVEL_DEBUG_HIGH;
		}
		return null;
	}
	
	
}
