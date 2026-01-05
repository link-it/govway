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
package org.openspcoop2.monitor.engine.statistic;

/**
 * StatisticsEngineException
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsEngineException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public StatisticsEngineException() {
		super();
	}
	
	public StatisticsEngineException(Throwable t) {
		super(t);
	}
	
	public StatisticsEngineException(String message) {
		super(message);
	}
	
	public StatisticsEngineException(Throwable t, String message) {
		super(message, t);
	}
	
	public StatisticsEngineException(String message, Throwable t) {
		super(message, t);
	}

}
