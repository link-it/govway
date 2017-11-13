/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.registry.Operation;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 nov 2017 $
 * 
 */
public class PortType {

	private org.openspcoop2.core.registry.PortType base;
	
	private String id;
	private Map<String, Azione> actions;
	
	public PortType(org.openspcoop2.core.registry.PortType base, String id, int indiceInizialeAzione, PayloadProfiles payloadProfiles) throws Exception {
		this.base = base;
		this.id = id;
		
		this.actions = new HashMap<>();
		for(Operation azione: base.getAzioneList()) {
			String idAzione = "Azione_" + indiceInizialeAzione++;
			this.actions.put(idAzione, new Azione(azione, idAzione, payloadProfiles));
		}
	}

	public org.openspcoop2.core.registry.PortType getBase() {
		return this.base;
	}

	public void setBase(org.openspcoop2.core.registry.PortType base) {
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
