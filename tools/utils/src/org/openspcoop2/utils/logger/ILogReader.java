/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
