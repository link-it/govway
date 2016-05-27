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

package org.openspcoop2.utils.logger.config;

import org.openspcoop2.utils.logger.constants.Severity;

/**
 * MultiLoggerConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiLoggerConfig {

	private boolean log4jLoggerEnabled = true;
	private boolean dbLoggerEnabled = false;
	
	private Severity diagnosticSeverityFilter = Severity.INFO;
	private Severity eventSeverityFilter = Severity.INFO;
	
	private DiagnosticConfig diagnosticConfig;
	
	private Log4jConfig log4jConfig;
	
	private DatabaseConfig databaseConfig;
	
	
	public boolean isLog4jLoggerEnabled() {
		return this.log4jLoggerEnabled;
	}

	public void setLog4jLoggerEnabled(boolean log4jLoggerEnabled) {
		this.log4jLoggerEnabled = log4jLoggerEnabled;
	}

	public boolean isDbLoggerEnabled() {
		return this.dbLoggerEnabled;
	}

	public void setDbLoggerEnabled(boolean dbLoggerEnabled) {
		this.dbLoggerEnabled = dbLoggerEnabled;
	}

	public Severity getDiagnosticSeverityFilter() {
		return this.diagnosticSeverityFilter;
	}

	public void setDiagnosticSeverityFilter(Severity diagnosticSeverityFilter) {
		this.diagnosticSeverityFilter = diagnosticSeverityFilter;
	}

	public Severity getEventSeverityFilter() {
		return this.eventSeverityFilter;
	}

	public void setEventSeverityFilter(Severity eventSeverityFilter) {
		this.eventSeverityFilter = eventSeverityFilter;
	}
	
	public DiagnosticConfig getDiagnosticConfig() {
		return this.diagnosticConfig;
	}

	public void setDiagnosticConfig(DiagnosticConfig diagnosticConfig) {
		this.diagnosticConfig = diagnosticConfig;
	}

	public Log4jConfig getLog4jConfig() {
		return this.log4jConfig;
	}

	public void setLog4jConfig(Log4jConfig log4jConfig) {
		this.log4jConfig = log4jConfig;
	}

	public DatabaseConfig getDatabaseConfig() {
		return this.databaseConfig;
	}

	public void setDatabaseConfig(DatabaseConfig databaseConfig) {
		this.databaseConfig = databaseConfig;
	}
}
