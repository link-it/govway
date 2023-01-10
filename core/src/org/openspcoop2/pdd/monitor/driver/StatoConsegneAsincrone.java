/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.monitor.driver;

import java.util.List;

import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;

/**
 * StatoConsegne
 * 
 * @author Andrea Poli (apoli@link.it)
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class StatoConsegneAsincrone {

	private SortedMap<StatoConsegnaAsincrona> stato = new SortedMap<StatoConsegnaAsincrona>();
	
	public List<String> getServiziApplicativi() {
		return this.stato.keys();
	}
	public void addStato(StatoConsegnaAsincrona stato) throws UtilsException {
		this.stato.add(stato.getServizioApplicativo(), stato);
	}
	public StatoConsegnaAsincrona getStato(String servizioApplicativo){
		return this.stato.get(servizioApplicativo);
	}
	public int size() {
		return this.stato.size();
	}
	
}
