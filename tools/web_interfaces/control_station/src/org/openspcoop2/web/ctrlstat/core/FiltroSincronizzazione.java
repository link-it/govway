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


package org.openspcoop2.web.ctrlstat.core;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IDServizio;

/**
 * Bean di filtro utilizzato dalla libreria di Sincronizzazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FiltroSincronizzazione {

	private IDSoggetto soggetto;
	private IDServizio servizio;

	public IDServizio getServizio() {
		return this.servizio;
	}

	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}

	public IDSoggetto getSoggetto() {
		return this.soggetto;
	}

	public void setSoggetto(IDSoggetto soggetto) {
		this.soggetto = soggetto;
	}

	@Override
	public String toString() {

		String res = "IDSoggetto [" + this.soggetto.toString() + "] IDServizio[" + this.servizio.toString() + "]";
		return res;
	}

}
