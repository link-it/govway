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

import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.beans.Diagnostic;
import org.openspcoop2.utils.logger.beans.Event;
import org.openspcoop2.utils.logger.beans.Message;

/**
 * ILogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ILogReader {
	
	public void initReader() throws UtilsException;
	
	// diagnostici
	public long countDiagnostics(ISearchContext searchContext) throws UtilsException;
	public List<Diagnostic> findDiagnostics(IPaginatedSearchContext searchContext) throws UtilsException;

	// transazione (+tracce)
	public IContext getContext(String idTransaction) throws UtilsException;
	public long countContexts(ISearchContext searchContext) throws UtilsException;
	public List<IContext> findContexts(IPaginatedSearchContext searchContext) throws UtilsException;
	
	// dump
	public List<Message> getMessages(String idTransaction) throws UtilsException;
	
	// event
	public long countEvents(IEventSearchContext searchContext) throws UtilsException;
	public List<Event> findEvents(IPaginatedEventSearchContext searchContext) throws UtilsException;
	
}
