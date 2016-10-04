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
package org.openspcoop2.pdd.core.behaviour;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizioApplicativo;

/**
 * BehaviourForwardToFilter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BehaviourForwardToFilter {

	/**
	 * Se valorizzata questa lista, verranno consegnati solo ai servizi applicativi indicati in lista
	 */
	private List<IDServizioApplicativo> accessListServiziApplicativi = new ArrayList<IDServizioApplicativo>();
	
	/**
	 * Se la precedente lista non risulta valorizzata, verranno consegnati a tutti i servizi applicativi fatta eccezione per quelli definiti in questa lista
	 */
	private List<IDServizioApplicativo> denyListServiziApplicativi = new ArrayList<IDServizioApplicativo>();
	
	public List<IDServizioApplicativo> getAccessListServiziApplicativi() {
		return this.accessListServiziApplicativi;
	}
	public void setAccessListServiziApplicativi(
			List<IDServizioApplicativo> accessListServiziApplicativi) {
		this.accessListServiziApplicativi = accessListServiziApplicativi;
	}
	public List<IDServizioApplicativo> getDenyListServiziApplicativi() {
		return this.denyListServiziApplicativi;
	}
	public void setDenyListServiziApplicativi(
			List<IDServizioApplicativo> denyListServiziApplicativi) {
		this.denyListServiziApplicativi = denyListServiziApplicativi;
	}

	
	public List<IDServizioApplicativo> aggiornaDestinatariAbilitati(List<IDServizioApplicativo> list){
		List<IDServizioApplicativo> aggiornata = new ArrayList<IDServizioApplicativo>();
		for (IDServizioApplicativo sa : list) {
			if(this.accessListServiziApplicativi!=null && this.accessListServiziApplicativi.size()>0){
				if(this.accessListServiziApplicativi.contains(sa)){
					aggiornata.add(sa);
				}
			}
			else if(this.denyListServiziApplicativi!=null && this.denyListServiziApplicativi.size()>0){
				if(this.denyListServiziApplicativi.contains(sa)==false){
					aggiornata.add(sa);
				}
			}
			else{
				aggiornata.add(sa);
			}
		}
		return aggiornata;
	}
}
