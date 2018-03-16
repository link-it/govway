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
/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.registry.constants.ServiceBinding;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class API {

	private org.openspcoop2.core.registry.AccordoServizioParteComune base;
	
	private String id;
	private Map<String, Azione> actions;
	
	public API(org.openspcoop2.core.registry.AccordoServizioParteComune base, String id, int indiceInizialeAzione, PayloadProfiles payloadProfiles) throws Exception {
		this.base = base;
		this.id = id;
		
		this.actions = new HashMap<>();
		if(ServiceBinding.SOAP.equals(base.getServiceBinding())) {
			if(base.sizeAzioneList()>0) {
				for (org.openspcoop2.core.registry.Azione az : base.getAzioneList()) {
					String idAzione = this.id+"_"+ "AzioneAccordo_" + indiceInizialeAzione++; // serve anche prefisso servizio, poiche' diversi servizi possono avere stessa azione e si confonde
					this.actions.put(idAzione, new Azione(az, idAzione, payloadProfiles));
				}
			}
			if(base.sizePortTypeList()>0) {
				for (org.openspcoop2.core.registry.PortType pt : base.getPortTypeList()) {
					if(pt.sizeAzioneList()>0) {
						for (org.openspcoop2.core.registry.Operation az : pt.getAzioneList()) {
							String idAzione = this.id+"_"+"Azione_" + indiceInizialeAzione++; // serve anche prefisso servizio, poiche' diversi servizi possono avere stessa azione e si confonde
							this.actions.put(idAzione, new Azione(az, idAzione, payloadProfiles));
						}
					}
				}
			}
		}
		else {
			if(base.sizeResourceList()>0) {
				for (org.openspcoop2.core.registry.Resource resource : base.getResourceList()) {
					String idAzione = this.id+"_"+"Resource_" + indiceInizialeAzione++; // serve anche prefisso servizio, poiche' diversi servizi possono avere stessa azione e si confonde
					this.actions.put(idAzione, new Azione(resource, idAzione, payloadProfiles));
				}
			}
		}
		
	}

	public org.openspcoop2.core.registry.AccordoServizioParteComune getBase() {
		return this.base;
	}

	public void setBase(org.openspcoop2.core.registry.AccordoServizioParteComune base) {
		this.base = base;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Azione> getActions() {
		return this.actions;
	}

	public void setActions(Map<String, Azione> actions) {
		this.actions = actions;
	}
}
