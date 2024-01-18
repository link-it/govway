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

package org.openspcoop2.pdd.core.controllo_traffico;

/**     
 * StatoTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoTraffico {

	
	private long activeThreads = 0l;
	private boolean pddCongestionata = false;
	
	public long getActiveThreads() {
		return this.activeThreads;
	}
	public void setActiveThreads(long activeThreads) {
		this.activeThreads = activeThreads;
	}
	public boolean isPddCongestionata() {
		return this.pddCongestionata;
	}
	public void setPddCongestionata(boolean pddCongestionata) {
		this.pddCongestionata = pddCongestionata;
	}
	
}
