/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
	private Map<String, Map<String, Azione>> actionsByPortType;
	protected final static String PORT_TYPE_NON_ESISTENTE = "@@ACCORDO_DIRETTO@@";
	
	public API(org.openspcoop2.core.registry.AccordoServizioParteComune base, String id, Index index, 
			PayloadProfiles payloadProfiles, Properties properties) throws Exception {
		this.base = base;
		this.id = id;
		
		this.actions = new HashMap<>();
		if(ServiceBinding.SOAP.equals(base.getServiceBinding())) {
			this.actionsByPortType = new HashMap<>();
			if(base.sizeAzioneList()>0) {
				Map<String, Azione> map = new HashMap<>();
				for (org.openspcoop2.core.registry.Azione az : base.getAzioneList()) {
					String idAzione = this.id+"_"+ "AzioneAccordo_" + index.getNextActionId(); // serve anche prefisso servizio, poiche' diversi servizi possono avere stessa azione e si confonde
					Azione azione = new Azione(az, idAzione, payloadProfiles, properties);
					this.actions.put(idAzione, azione);
					map.put(idAzione, azione);
				}
				this.actionsByPortType.put(PORT_TYPE_NON_ESISTENTE, map);
			}
			if(base.sizePortTypeList()>0) {
				for (org.openspcoop2.core.registry.PortType pt : base.getPortTypeList()) {
					if(pt.sizeAzioneList()>0) {
						Map<String, Azione> map = new HashMap<>();
						for (org.openspcoop2.core.registry.Operation az : pt.getAzioneList()) {
							String idAzione = this.id+"_"+"Azione_" + index.getNextActionId(); // serve anche prefisso servizio, poiche' diversi servizi possono avere stessa azione e si confonde
							Azione azione = new Azione(az, idAzione, payloadProfiles, properties);
							this.actions.put(idAzione, azione);
							map.put(idAzione, azione);
						}
						this.actionsByPortType.put(pt.getNome(), map);
					}
				}
			}
		}
		else {
			if(base.sizeResourceList()>0) {
				for (org.openspcoop2.core.registry.Resource resource : base.getResourceList()) {
					String idAzione = this.id+"_"+"Resource_" + index.getNextActionId(); // serve anche prefisso servizio, poiche' diversi servizi possono avere stessa azione e si confonde
					this.actions.put(idAzione, new Azione(resource, idAzione, payloadProfiles, properties));
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
	
	public Map<String, Azione> getActionsWithoutPortType() {
		if(this.actionsByPortType!=null) {
			return this.actionsByPortType.get(PORT_TYPE_NON_ESISTENTE);
		}
		return null;
	}
	public Map<String, Azione> getActions(String portType) {
		if(this.actionsByPortType!=null) {
			return this.actionsByPortType.get(portType);
		}
		return null;
	}

	public void setActions(Map<String, Azione> actions) {
		this.actions = actions;
	}
}
