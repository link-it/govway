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

package org.openspcoop2.core.controllo_traffico.driver;

import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * IPolicyGroupByActiveThreadsInMemory 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IPolicyGroupByActiveThreadsInMemory extends IPolicyGroupByActiveThreads {

	public ActivePolicy getActivePolicy();
	
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads();
		
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map);
	
	public long getActiveThreads();
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro);
	
	public void resetCounters();
	
	public void remove() throws UtilsException;
	
	public String printInfos(Logger log, String separatorGroups) throws UtilsException;
	
}
