/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.autenticazione.pd;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.AbstractDatiInvocazione;

/**
 * DatiInvocazionePortaDelegata
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiInvocazionePortaDelegata extends AbstractDatiInvocazione {

	private IDPortaDelegata idPD;
	private PortaDelegata pd;
	
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

	
	@Override
	public String getKeyCache(){
		return this._toString(true);
	}
	
	@Override
	public String toString(){
		return this._toString(false);
	}
	@Override
	public String _toString(boolean keyCache){
		StringBuilder bf = new StringBuilder();
		
		bf.append(super._toString(keyCache));
		
		if(this.idPD!=null){
			bf.append(" IDPortaDelegata(");
			bf.append(this.idPD.toString());
			bf.append(")");
		}
		
		if(keyCache==false){
			if(this.pd!=null){
				bf.append(" PortaDelegata:defined");
			}
		}
		
		return bf.toString();
	}
}
