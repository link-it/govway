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
package org.openspcoop2.pdd.timers.statistiche.server;

/**
 * StatisticheCostanti
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheCostanti {
	
	private StatisticheCostanti() {}

	public static final String LOGGER_PREFIX = "govway.statistiche.server";
	
	public static final String LOG4J_PROPERTIES = "timer-statistiche.log4j2.properties";
	public static final String LOG4J_PROPERTIES_LOCAL = "timer-statistiche_local.log4j2.properties";
	public static final String LOG4J_PROPERTIES_LOCAL_VARIABLE = "TIMER_STATISTICHE_LOG_PROPERTIES";
	
	public static final String PROPERTIES_FILE = "timer-statistiche.properties";
	
	/** Nome della variabile di sistema/java per specificare il path al file di properties */
	public static final String STATISTICHE_SERVER_PROPERTIES = "TIMER_STATISTICHE_PROPERTIES";

	/** Nome del file di properties locale (per override) */
	public static final String LOCAL_PROPERTIES_FILE = "timer-statistiche_local.properties";
	
}
