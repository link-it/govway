/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.utils.logger;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.beans.Event;
import org.openspcoop2.utils.logger.beans.Message;
import org.openspcoop2.utils.logger.constants.LowSeverity;

/**
 * ILogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ILogger {
	
	public void initLogger() throws UtilsException;
	public void initLogger(String idTransaction) throws UtilsException;
	public void initLogger(IContext context) throws UtilsException;
	public void initLogger(String idTransaction,IContext context) throws UtilsException;
	
	// diagnostici
	public void log(String message, LowSeverity severity) throws UtilsException;
	public void log(String message, LowSeverity severity, String function) throws UtilsException;
	public void log(String code) throws UtilsException;
	public void log(String code, String ... params) throws UtilsException;
	public void log(String code, String param) throws UtilsException; // serve per evitare che la chiamata con string ricata erroneamente nella firma Object invece che nella firma String ... params
	public void log(String code, Object o) throws UtilsException;
	public String getLogParam(String logParam) throws UtilsException;
	
	// transazione (+tracce)
	public IContext getContext() throws UtilsException;
	public void log() throws UtilsException;
	public void log(IContext context) throws UtilsException;
	
	// dump
	public void log(Message message) throws UtilsException;
	
	// event
	public void log(Event event) throws UtilsException;
	
}
