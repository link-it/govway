/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.registry.constants.ServiceBinding;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 nov 2017 $
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
					String idAzione = "AzioneAccordo_" + indiceInizialeAzione++;
					this.actions.put(idAzione, new Azione(az, idAzione, payloadProfiles));
				}
			}
			if(base.sizePortTypeList()>0) {
				for (org.openspcoop2.core.registry.PortType pt : base.getPortTypeList()) {
					if(pt.sizeAzioneList()>0) {
						for (org.openspcoop2.core.registry.Operation az : pt.getAzioneList()) {
							String idAzione = "Azione_" + indiceInizialeAzione++;
							this.actions.put(idAzione, new Azione(az, idAzione, payloadProfiles));
						}
					}
				}
			}
		}
		else {
			if(base.sizeResourceList()>0) {
				for (org.openspcoop2.core.registry.Resource resource : base.getResourceList()) {
					String idAzione = "Resource_" + indiceInizialeAzione++;
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
