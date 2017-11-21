/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 nov 2017 $
 * 
 */
public class APS {

	private AccordoServizioParteSpecifica base;
	private Map<String, Azione> azioni;
	private PortType pt;
	private String ebmsSecurityProfile;
	private String id;
	
	public APS(AccordoServizioParteSpecifica base, PortType pt, int idInizialeLeg, String id) throws Exception {
		this.base = base;
		this.pt = pt;
		this.id = id;
		for(ProtocolProperty prop: base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE)) {
				this.ebmsSecurityProfile = prop.getValue();
			}
		}
		
		if(this.ebmsSecurityProfile == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE+" non definita per l'aps ["+base.getNome()+"]");
		
		this.azioni = new HashMap<>();
		for(Azione azione: pt.getActions().values()) {
			this.azioni.put("Leg_" + idInizialeLeg++, azione);
		}
	}
	public AccordoServizioParteSpecifica getBase() {
		return this.base;
	}
	public void setBase(AccordoServizioParteSpecifica base) {
		this.base = base;
	}
	public PortType getPt() {
		return this.pt;
	}
	public void setPt(PortType pt) {
		this.pt = pt;
	}
	public Map<String, Azione> getAzioni() {
		return this.azioni;
	}
	public void setAzioni(Map<String, Azione> azioniMap) {
		this.azioni = azioniMap;
	}
	public String getEbmsSecurityProfile() {
		return this.ebmsSecurityProfile;
	}
	public void setEbmsSecurityProfile(String ebmsSecurityProfile) {
		this.ebmsSecurityProfile = ebmsSecurityProfile;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
