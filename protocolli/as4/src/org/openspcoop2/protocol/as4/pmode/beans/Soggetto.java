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
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Soggetto  {

	private org.openspcoop2.core.registry.Soggetto base;
	private String ebmsUserMessagePartyId;
	private String ebmsUserMessagePartyIdTypeName;
	private String ebmsUserMessagePartyIdTypeValue;
	private String ebmsUserMessagePartyEndpoint;
	private String ebmsUserMessagePartyCN;
	
	private List<APS> aps;

	public Soggetto(org.openspcoop2.core.registry.Soggetto base, Map<IDAccordo, API> accordi, int indiceInizialeLeg, int indiceInizialeProcess) throws Exception {
		this.base = base;
		for(ProtocolProperty prop: this.base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME)) {
				this.ebmsUserMessagePartyIdTypeName = prop.getValue();
			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE)) {
				this.ebmsUserMessagePartyIdTypeValue = prop.getValue();
			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE)) {
				this.ebmsUserMessagePartyId = prop.getValue();
			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ENDPOINT)) {
				this.ebmsUserMessagePartyEndpoint = prop.getValue();
			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_COMMON_NAME)) {
				this.ebmsUserMessagePartyCN = prop.getValue();
			}
		}

		if(base.getConnettore() == null || TipiConnettore.DISABILITATO.getNome().equals(base.getConnettore().getTipo())) {
			throw getException("Connettore non definito",base);
		}

		if(this.ebmsUserMessagePartyIdTypeName == null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME+" trovata", base);

		if(this.ebmsUserMessagePartyIdTypeValue== null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE+" trovata", base);

		if(this.ebmsUserMessagePartyId== null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE+" trovata", base);
		
		if(this.ebmsUserMessagePartyEndpoint== null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ENDPOINT+" trovata", base);
		
		if(this.ebmsUserMessagePartyCN== null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_COMMON_NAME+" trovata", base);
		
		
		this.aps = new ArrayList<>();
		int numeroLegPerSoggetto = 0;
		int numeroProcessPerSoggetto = 0;
		for(AccordoServizioParteSpecifica aps: base.getAccordoServizioParteSpecificaList()) {

			IDAccordo apcKey = IDAccordoFactory.getInstance().getIDAccordoFromUri(aps.getAccordoServizioParteComune());
			
			if(accordi.containsKey(apcKey)) {
				API api = accordi.get(apcKey);
				this.aps.add(new APS(aps, api, indiceInizialeLeg+numeroLegPerSoggetto, "Process_" + (indiceInizialeProcess+numeroProcessPerSoggetto)));
				numeroLegPerSoggetto += api.getActions().size();
				numeroProcessPerSoggetto++;
			} else {
				throw new Exception("APC["+apcKey+"] erogato dal soggetto ["+base.getTipo()+"/"+base.getNome()+"] non trovato");
			}
		}
	}

	private Exception getException(String msg, org.openspcoop2.core.registry.Soggetto soggetto) {
		return new Exception(msg + " per il soggetto "+soggetto.getTipo()+"/"+soggetto.getNome());
	}

	public org.openspcoop2.core.registry.Soggetto getBase() {
		return this.base;
	}
	public void setBase(org.openspcoop2.core.registry.Soggetto base) {
		this.base = base;
	}
	public String getEbmsUserMessagePartyIdTypeName() {
		return this.ebmsUserMessagePartyIdTypeName;
	}
	public void setEbmsUserMessagePartyIdTypeName(String ebmsUserMessagePartyIdTypeName) {
		this.ebmsUserMessagePartyIdTypeName = ebmsUserMessagePartyIdTypeName;
	}
	public String getEbmsUserMessagePartyIdTypeValue() {
		return this.ebmsUserMessagePartyIdTypeValue;
	}
	public void setEbmsUserMessagePartyIdTypeValue(String ebmsUserMessagePartyIdTypeValue) {
		this.ebmsUserMessagePartyIdTypeValue = ebmsUserMessagePartyIdTypeValue;
	}
	public String getEbmsUserMessagePartyId() {
		return this.ebmsUserMessagePartyId;
	}
	public void setEbmsUserMessagePartyId(String ebmsUserMessagePartyId) {
		this.ebmsUserMessagePartyId = ebmsUserMessagePartyId;
	}
	public String getEbmsUserMessagePartyCN() {
		return this.ebmsUserMessagePartyCN;
	}
	public void setEbmsUserMessagePartyCN(String ebmsUserMessagePartyCN) {
		this.ebmsUserMessagePartyCN = ebmsUserMessagePartyCN;
	}

	public String getEbmsUserMessagePartyEndpoint() {
		return this.ebmsUserMessagePartyEndpoint;
	}

	public void setEbmsUserMessagePartyEndpoint(String ebmsUserMessagePartyEndpoint) {
		this.ebmsUserMessagePartyEndpoint = ebmsUserMessagePartyEndpoint;
	}

	public int sizeAzioni() {
		int numAzioni = 0;
		for(APS aps: this.aps) {
			numAzioni =+ aps.getAzioni().size();
		}
		return numAzioni;
	}

	public List<APS> getAps() {
		return this.aps;
	}

	public void setAps(List<APS> aps) {
		this.aps = aps;
	}
}
