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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class APS {

	private AccordoServizioParteSpecifica base;
	private Map<String, Azione> azioni;
	private API pt;
	private String ebmsSecurityProfile;
	private String id;
	private List<String> cnFruitori = new ArrayList<String>();
	
	public APS(AccordoServizioParteSpecifica base, API pt, int idInizialeLeg, String id) throws Exception {
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
	public API getPt() {
		return this.pt;
	}
	public void setPt(API pt) {
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
	
	public List<String> getCnFruitori() {
		return this.cnFruitori;
	}
	public void setCnFruitori(List<String> cnFruitori) {
		this.cnFruitori = cnFruitori;
	}
	public void initCNFruitori(List<Soggetto> soggetti) {
		for (Fruitore fruitore : this.base.getFruitoreList()) {
			for (Soggetto soggetto : soggetti) {
				if(soggetto.getBase().getNome().equals(fruitore.getNome())) {
					this.cnFruitori.add(soggetto.getEbmsUserMessagePartyCN());
					break;
				}
			}
		}
	}
}
