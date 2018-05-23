/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.core.controllo_traffico.driver;

import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.ConfigurazioneControlloCongestione;
import org.slf4j.Logger;

/**
 * IGestorePolicyAttive 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IGestorePolicyAttive {

	public void initialize(Logger log, Object ... params) throws PolicyException;
	
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(ActivePolicy activePolicy) throws PolicyShutdownException,PolicyException;
	
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException,PolicyException,PolicyNotFoundException;
	
	public long sizeActivePolicyThreads(boolean sum) throws PolicyShutdownException,PolicyException;
	
	public String printKeysPolicy(String separator) throws PolicyShutdownException,PolicyException;
	
	public String printInfoPolicy(String id, String separatorGroups) throws PolicyShutdownException,PolicyException,PolicyNotFoundException;
	
	public void removeActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException,PolicyException;
	
	public void removeAllActiveThreadsPolicy() throws PolicyShutdownException,PolicyException;
	
	public void resetCountersActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException,PolicyException;
	
	public void resetCountersAllActiveThreadsPolicy() throws PolicyShutdownException,PolicyException;
	
	// ---- Per salvare
	
	public void serialize(OutputStream out) throws PolicyException;
	
	public void initialize(InputStream in,ConfigurazioneControlloCongestione configurazioneControlloCongestione) throws PolicyException;
	
}
