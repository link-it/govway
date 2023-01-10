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

package org.openspcoop2.pdd.core.token.pa;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.pdd.core.token.AbstractDatiInvocazione;

/**
 * DatiInvocazionePortaDelegata
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiInvocazionePortaApplicativa extends AbstractDatiInvocazione {

	// Richieste normali
	private IDPortaApplicativa idPA;
	private PortaApplicativa pa;
	
	// Nel caso di risposta asincrona simmetrica e per ricevute asincrone.
	private IDPortaDelegata idPD;
	private PortaDelegata pd;
	

	public IDPortaApplicativa getIdPA() {
		return this.idPA;
	}
	public void setIdPA(IDPortaApplicativa idPA) {
		this.idPA = idPA;
	}

	public PortaApplicativa getPa() {
		return this.pa;
	}
	public void setPa(PortaApplicativa pa) {
		this.pa = pa;
	}

	public IDPortaDelegata getIdPD() {
		return this.idPD;
	}
	public void setIdPD(IDPortaDelegata idPD) {
		this.idPD = idPD;
	}

	public PortaDelegata getPd() {
		return this.pd;
	}
	public void setPd(PortaDelegata pd) {
		this.pd = pd;
	}
	
}
