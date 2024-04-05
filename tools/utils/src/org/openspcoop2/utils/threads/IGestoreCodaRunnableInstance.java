/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.threads;

import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;

/**
 * IGestoreCodaRunnableInstance
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IGestoreCodaRunnableInstance {

	public void initialize(RunnableLogger log) throws UtilsException;
	
	default public void logCheckInProgress(Map<String, Object> context) {}
	
	default public void logRegisteredThreads(Map<String, Object> context, int nuoviThreadsAttivati) {}
	
	default public void logCheckFinished(Map<String, Object> context) {}
	
	public List<Runnable> nextRunnable(int limit) throws UtilsException;
	
}
