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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
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
	private String ebmsBinding; // viene inizializzato una volta raccolte le azioni con stesso binding
	private String ebmsMep; // viene inizializzato una volta raccolte le azioni con stesso binding
	private String id;
	private List<String> cnFruitori = new ArrayList<String>();
	
	private APS() {} // per clone
	@Override
	public APS clone() {
		APS aps = new APS();
		aps.base = this.base;
		aps.azioni = new HashMap<>();
		Iterator<String> itAz = this.azioni.keySet().iterator();
		while (itAz.hasNext()) {
			String key = (String) itAz.next();
			aps.azioni.put(key, this.azioni.get(key));
		}
		aps.api = this.api;
		aps.ebmsSecurityProfile = this.ebmsSecurityProfile;
		aps.ebmsReliabilityNonRepudiation = this.ebmsReliabilityNonRepudiation;
		aps.ebmsReliabilityReplyPattern = this.ebmsReliabilityReplyPattern;
		aps.id = this.id;
		aps.cnFruitori = this.cnFruitori;
		return aps;
	}
	
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
		
		/*
		 * 
		 *  Le 3 proprietà sono in una fruizione se stiamo configurando una fruizione.
		if(this.ebmsSecurityProfile == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE+" non definita per l'aps ["+base.getNome()+"] erogato dal soggetto ["+base.getNomeSoggettoErogatore()+"]");
		if(this.ebmsReliabilityNonRepudiation == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION+" non definita per l'aps ["+base.getNome()+"] erogato dal soggetto ["+base.getNomeSoggettoErogatore()+"]");
		if(this.ebmsReliabilityReplyPattern == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN+" non definita per l'aps ["+base.getNome()+"] erogato dal soggetto ["+base.getNomeSoggettoErogatore()+"]");
		*/
		
		Map<String, Azione> azioniAPI = null;
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(api.getBase().getServiceBinding())){
			azioniAPI = api.getActions();
		}
		else {
			if(base.getPortType()==null) {
				azioniAPI = api.getActionsWithoutPortType();
			}
			else {
				azioniAPI = api.getActions(base.getPortType());
			}
		}
		this.azioni = new HashMap<>();
		for(Azione azione: azioniAPI.values()) {
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
	public String getEbmsBinding() {
		return this.ebmsBinding;
	}
	public void setEbmsBinding(String ebmsBinding) {
		this.ebmsBinding = ebmsBinding;
		if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_VALUE.equals(ebmsBinding)) {
			this.ebmsMep = AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_VALUE_MEP; 
		}
		else if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_VALUE.equals(ebmsBinding)) {
			this.ebmsMep = AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_VALUE_MEP; 
		}
		else {
			throw new RuntimeException("Binding ["+ebmsBinding+"] unsupported");
		}
	}
	public String getEbmsMep() {
		return this.ebmsMep;
	}
	public void setEbmsMep(String ebmsMep) {
		this.ebmsMep = ebmsMep;
	}
	
	public List<String> getCnFruitori() {
		return this.cnFruitori;
	}
	public void setCnFruitori(List<String> cnFruitori) {
		this.cnFruitori = cnFruitori;
	}
	public void initCNFruitori(List<Soggetto> soggetti) throws Exception {
		
		// Metodo che viene chiamato per le fruizioni
		
		for (Fruitore fruitore : this.base.getFruitoreList()) {
			for (Soggetto soggetto : soggetti) {
				if(soggetto.getBase().getNome().equals(fruitore.getNome())) {
					if(this.cnFruitori.contains(soggetto.getEbmsUserMessagePartyCN())==false) {
						this.cnFruitori.add(soggetto.getEbmsUserMessagePartyCN());
						
						org.openspcoop2.protocol.as4.pmode.beans.Fruitore fruitoreBuild = new org.openspcoop2.protocol.as4.pmode.beans.Fruitore(fruitore, this.base, soggetto.getEbmsUserMessagePartyCN());
						String oggettoFruizione = "fruizione da parte del soggetto '"+fruitore.getNome()+"' dell'api '"+this.base.getNome()+"' erogata dal soggetto '"+this.base.getNomeSoggettoErogatore()+"'";
					    
						if(this.ebmsSecurityProfile==null) {
							this.ebmsSecurityProfile = fruitoreBuild.getEbmsSecurityProfile();
						}
						else {
							if(this.ebmsSecurityProfile.equals(fruitoreBuild.getEbmsSecurityProfile())==false) {
								throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE+" ("+fruitoreBuild.getEbmsSecurityProfile()+") definita definita per la "+oggettoFruizione+
										" differente dal valore ("+this.ebmsSecurityProfile+") indicato per l'erogazione della medesima API da parte del soggetto "+this.base.getNomeSoggettoErogatore());
							}
						}
						
						if(this.ebmsReliabilityNonRepudiation==null) {
							this.ebmsReliabilityNonRepudiation = fruitoreBuild.getEbmsReliabilityNonRepudiation();
						}
						else {
							if(this.ebmsReliabilityNonRepudiation.equals(fruitoreBuild.getEbmsReliabilityNonRepudiation())==false) {
								throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION+" ("+fruitoreBuild.getEbmsReliabilityNonRepudiation()+") definita definita per la "+oggettoFruizione+
										" differente dal valore ("+this.ebmsReliabilityNonRepudiation+") indicato per l'erogazione della medesima API da parte del soggetto "+this.base.getNomeSoggettoErogatore());
							}
						}
						
						if(this.ebmsReliabilityReplyPattern==null) {
							this.ebmsReliabilityReplyPattern = fruitoreBuild.getEbmsReliabilityReplyPattern();
						}
						else {
							if(this.ebmsReliabilityReplyPattern.equals(fruitoreBuild.getEbmsReliabilityReplyPattern())==false) {
								throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN+" ("+fruitoreBuild.getEbmsReliabilityReplyPattern()+") definita definita per la "+oggettoFruizione+
										" differente dal valore ("+this.ebmsReliabilityReplyPattern+") indicato per l'erogazione della medesima API da parte del soggetto "+this.base.getNomeSoggettoErogatore());
							}
						}
						
					}
					break;
				}
			}
		}
	}
	public void initCNFruitori(List<Soggetto> soggetti,List<IDSoggetto> soggettiAutorizzati) throws Exception {
		
		// Metodo che viene chiamato per le erogazioni
		
		if(this.ebmsSecurityProfile == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE+" non definita per l'erogazione dell'API ["+this.base.getNome()+"] erogata dal soggetto ["+this.base.getNomeSoggettoErogatore()+"]");
		if(this.ebmsReliabilityNonRepudiation == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION+" non definita per l'erogazione dell'API ["+this.base.getNome()+"] erogata dal soggetto ["+this.base.getNomeSoggettoErogatore()+"]");
		if(this.ebmsReliabilityReplyPattern == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN+" non definita per l'erogazione dell'API ["+this.base.getNome()+"] erogata dal soggetto ["+this.base.getNomeSoggettoErogatore()+"]");
				
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
