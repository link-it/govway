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
package org.openspcoop2.utils.logger;

import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.beans.Diagnostic;
import org.openspcoop2.utils.logger.beans.Event;
import org.openspcoop2.utils.logger.beans.Message;

/**
 * ILogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 11425 $, $Date: 2016-01-26 11:12:54 +0100 (Tue, 26 Jan 2016) $
 */
public interface ILogReader {
	
	public void initReader() throws UtilsException;
	
	// diagnostici
	public List<Diagnostic> findDiagnostics(IPaginatedSearchContext searchContext);

	// transazione (+tracce)
	public IContext getContext(String idTransaction) throws UtilsException;
	public List<IContext> findContexts(IPaginatedSearchContext searchContext) throws UtilsException;
	
	// dump
	public List<Message> findMessages(String idTransaction) throws UtilsException;
	public List<Message> findMessages(IPaginatedSearchContext searchContext) throws UtilsException;
	
	// event
	public List<Event> findEvents(IEventSearchContext searchContext) throws UtilsException;
	
}
