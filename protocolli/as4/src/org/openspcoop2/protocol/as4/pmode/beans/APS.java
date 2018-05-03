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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class APS {

	private AccordoServizioParteSpecifica base;
	private Map<String, Azione> azioni;
	private API api;
	private String ebmsSecurityProfile;
	private Boolean ebmsReliabilityNonRepudiation;
	private String ebmsReliabilityReplyPattern;
	private String id;
	private List<String> cnFruitori = new ArrayList<String>();
	
	public APS(AccordoServizioParteSpecifica base, API api, Index index, String id) throws Exception {
		this.base = base;
		this.api = api;
		this.id = id;
		for(ProtocolProperty prop: base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE)) {
				this.ebmsSecurityProfile = prop.getValue();
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION)) {
				if(prop.getBooleanValue()!=null) {
					this.ebmsReliabilityNonRepudiation = prop.getBooleanValue();
				}
				else if(prop.getValue()!=null) {
					this.ebmsReliabilityNonRepudiation = Boolean.parseBoolean(prop.getValue());
				}
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN)) {
				this.ebmsReliabilityReplyPattern = prop.getValue();
			}
		}
		
		if(this.ebmsSecurityProfile == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE+" non definita per l'aps ["+base.getNome()+"] erogato dal soggetto ["+base.getNomeSoggettoErogatore()+"]");
		if(this.ebmsReliabilityNonRepudiation == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION+" non definita per l'aps ["+base.getNome()+"] erogato dal soggetto ["+base.getNomeSoggettoErogatore()+"]");
		if(this.ebmsReliabilityReplyPattern == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN+" non definita per l'aps ["+base.getNome()+"] erogato dal soggetto ["+base.getNomeSoggettoErogatore()+"]");
		
		this.azioni = new HashMap<>();
		for(Azione azione: api.getActions().values()) {
			this.azioni.put("Leg_" + index.getNextLegId(), azione);
		}
	}
	public AccordoServizioParteSpecifica getBase() {
		return this.base;
	}
	public void setBase(AccordoServizioParteSpecifica base) {
		this.base = base;
	}
	public API getApi() {
		return this.api;
	}
	public void setApi(API pt) {
		this.api = pt;
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
	public Boolean getEbmsReliabilityNonRepudiation() {
		return this.ebmsReliabilityNonRepudiation;
	}
	public void setEbmsReliabilityNonRepudiation(Boolean ebmsReliabilityNonRepudiation) {
		this.ebmsReliabilityNonRepudiation = ebmsReliabilityNonRepudiation;
	}
	public String getEbmsReliabilityReplyPattern() {
		return this.ebmsReliabilityReplyPattern;
	}
	public void setEbmsReliabilityReplyPattern(String ebmsReliabilityReplyPattern) {
		this.ebmsReliabilityReplyPattern = ebmsReliabilityReplyPattern;
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
					if(this.cnFruitori.contains(soggetto.getEbmsUserMessagePartyCN())==false) {
						this.cnFruitori.add(soggetto.getEbmsUserMessagePartyCN());
					}
					break;
				}
			}
		}
	}
	public void initCNFruitori(List<Soggetto> soggetti,List<IDSoggetto> soggettiAutorizzati) {
		for (IDSoggetto soggettoAutorizzato : soggettiAutorizzati) {
			for (Soggetto soggetto : soggetti) {
				if(soggetto.getBase().getNome().equals(soggettoAutorizzato.getNome())) {
					if(this.cnFruitori.contains(soggetto.getEbmsUserMessagePartyCN())==false) {
						this.cnFruitori.add(soggetto.getEbmsUserMessagePartyCN());
					}
					break;
				}
			}
		}
	}
	public void checkCNFruitori() throws Exception {
		if(this.cnFruitori.size()<=0) {
			throw new Exception("Non sono stati definiti soggetto autorizzati a fruire del servizio "+IDServizioFactory.getInstance().getUriFromAccordo(this.base));
		}
	}
}
