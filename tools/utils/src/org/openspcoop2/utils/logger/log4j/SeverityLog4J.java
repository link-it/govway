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
import org.apache.log4j.Logger;
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

	
	/*
	 * Log4J 1.x
	 * 
	 * 	Standard intLevel
	 *     OFF 	   2147483647
	 *     FATAL     50000
	 *     ERROR     40000
	 *     WARN      30000
	 *     INFO      20000
	 *     DEBUG     10000
	 *     ALL     -2147483648
	 */
	
	/** Definisce un Livello di Severita' FATAL per un messaggio Diagnostico: valore Log4J = 50001 */
	public static final Level LOG_V1_LEVEL_FATAL = 
		new SeverityLog4J(Severity.FATAL,50001);

	/** Definisce un Livello di Severita' ERROR per un messaggio Diagnostico: valore Log4J = 40001 */
	public static final Level LOG_V1_LEVEL_ERROR = 
		new SeverityLog4J(Severity.ERROR,40001);

	/** Definisce un Livello di Severita' WARN per un messaggio Diagnostico: valore Log4J = 30001 */
	public static final Level LOG_V1_LEVEL_WARN = 
		new SeverityLog4J(Severity.WARN,30001);

	/** Definisce un Livello di Severita' INFO per un messaggio Diagnostico: valore Log4J = 20001 */
	public static final Level LOG_V1_LEVEL_INFO = 
		new SeverityLog4J(Severity.INFO,20001);

	/** Definisce un Livello di Severita' DEBUG-LOW per un messaggio Diagnostico: valore Log4J = 10003 */
	public static final Level LOG_V1_LEVEL_DEBUG_LOW = 
		new SeverityLog4J(Severity.DEBUG_LOW,10003);

	/** Definisce un Livello di Severita' DEBUG-MEDIUM per un messaggio Diagnostico: valore Log4J = 10002 */
	public static final Level LOG_V1_LEVEL_DEBUG_MEDIUM = 
		new SeverityLog4J(Severity.DEBUG_MEDIUM,10002);

	/** Definisce un Livello di Severita' DEBUG-HIGH per un messaggio Diagnostico: valore Log4J = 10001 */
	public static final Level LOG_V1_LEVEL_DEBUG_HIGH = 
		new SeverityLog4J(Severity.DEBUG_HIGH,10001);
	
	/*
	 * Log4J 2.x
	 * 
	 * 	Standard intLevel
	 *    OFF 		  0
	 *    FATAL 	100
	 *    ERROR 	200
	 *    WARN 		300
	 *    INFO 		400
	 *    DEBUG 	500
	 *    TRACE 	600
	 *    ALL 	Integer.MAX_VALUE
	 */
	
	/** Definisce un Livello di Severita' FATAL per un messaggio Diagnostico: valore Log4J = 99 */
	public static final Level LOG_V2_LEVEL_FATAL = 
		new SeverityLog4J(Severity.FATAL,99);

	/** Definisce un Livello di Severita' ERROR per un messaggio Diagnostico: valore Log4J = 199 */
	public static final Level LOG_V2_LEVEL_ERROR = 
		new SeverityLog4J(Severity.ERROR,199);

	/** Definisce un Livello di Severita' WARN per un messaggio Diagnostico: valore Log4J = 299 */
	public static final Level LOG_V2_LEVEL_WARN = 
		new SeverityLog4J(Severity.WARN,299);

	/** Definisce un Livello di Severita' INFO per un messaggio Diagnostico: valore Log4J = 399 */
	public static final Level LOG_V2_LEVEL_INFO = 
		new SeverityLog4J(Severity.INFO,399);

	/** Definisce un Livello di Severita' DEBUG-LOW per un messaggio Diagnostico: valore Log4J = 498 */
	public static final Level LOG_V2_LEVEL_DEBUG_LOW = 
		new SeverityLog4J(Severity.DEBUG_LOW,498);

	/** Definisce un Livello di Severita' DEBUG-MEDIUM per un messaggio Diagnostico: valore Log4J = 499 */
	public static final Level LOG_V2_LEVEL_DEBUG_MEDIUM = 
		new SeverityLog4J(Severity.DEBUG_MEDIUM,499);

	/** Definisce un Livello di Severita' DEBUG-HIGH per un messaggio Diagnostico: valore Log4J = 599 */
	public static final Level LOG_V2_LEVEL_DEBUG_HIGH = 
		new SeverityLog4J(Severity.DEBUG_HIGH,599);
	


	

	
	
	

	
	protected SeverityLog4J(Severity levelStr,int level) {
		super(level, "OP_"+levelStr.name(), level);
	}

	public static Level getSeverityLog4J(Severity severity,Log4jType log4jType){
		if(Log4jType.LOG4Jv1.equals(log4jType)){
			switch (severity) {
			case FATAL:
				return LOG_V1_LEVEL_FATAL;
			case ERROR:
				return LOG_V1_LEVEL_ERROR;
			case WARN:
				return LOG_V1_LEVEL_WARN;
			case INFO:
				return LOG_V1_LEVEL_INFO;
			case DEBUG_LOW:
				return LOG_V1_LEVEL_DEBUG_LOW;
			case DEBUG_MEDIUM:
				return LOG_V1_LEVEL_DEBUG_MEDIUM;
			case DEBUG_HIGH:
				return LOG_V1_LEVEL_DEBUG_HIGH;
			}
		}
		else{
			
			// TODO: Per log4j 2 non funziona la conversione in Priority. Per i custom level è tutto differente: http://logging.apache.org/log4j/2.x/manual/customloglevels.html
			// Viene quindi implementato un metodo che per ora mappa suil livelli standard di log4j. Vedi metodo sottostante
			
			switch (severity) {
			case FATAL:
				return LOG_V2_LEVEL_FATAL;
			case ERROR:
				return LOG_V2_LEVEL_ERROR;
			case WARN:
				return LOG_V2_LEVEL_WARN;
			case INFO:
				return LOG_V2_LEVEL_INFO;
			case DEBUG_LOW:
				return LOG_V2_LEVEL_DEBUG_LOW;
			case DEBUG_MEDIUM:
				return LOG_V2_LEVEL_DEBUG_MEDIUM;
			case DEBUG_HIGH:
				return LOG_V2_LEVEL_DEBUG_HIGH;
			}
		}
		return null;
	}
	
	public static void log4j2(Logger log,Severity severity, String msg){
		switch (severity) {
		case FATAL:
			log.fatal(msg);
			break;
		case ERROR:
			log.error(msg);
			break;
		case WARN:
			log.warn(msg);
			break;
		case INFO:
			log.info(msg);
			break;
		case DEBUG_LOW:
			log.debug(msg);
			break;
		case DEBUG_MEDIUM:
			log.debug(msg);
			break;
		case DEBUG_HIGH:
			log.trace(msg);
			break;
		}
	}
	
}
