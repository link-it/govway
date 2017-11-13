/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 nov 2017 $
 * 
 */
public class Soggetto  {

	private org.openspcoop2.core.registry.Soggetto base;
	private String ebmsUserMessagePartyId;
	private String ebmsUserMessagePartyIdTypeName;
	private String ebmsUserMessagePartyIdTypeValue;
	private String location;
	private List<APS> aps;

	public Soggetto(org.openspcoop2.core.registry.Soggetto base, Map<IDPortType, PortType> ptList, int indiceInizialeLeg, int indiceInizialeProcess) throws Exception {
		this.base = base;
		for(ProtocolProperty prop: this.base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME)) {
				this.ebmsUserMessagePartyIdTypeName = prop.getValue();
			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE)) {
				this.ebmsUserMessagePartyIdTypeValue = prop.getValue();
			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE)) {
				this.ebmsUserMessagePartyId = prop.getValue();
			}
		}

		if(base.getConnettore() != null) {
			for(Property prop: this.base.getConnettore().getPropertyList()) {
				if(prop.getNome().equals("location")) {
					this.location = prop.getValore();
				}
			}
		}

		if(this.ebmsUserMessagePartyIdTypeName == null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_NAME+" trovata", base);

		if(this.ebmsUserMessagePartyIdTypeValue== null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_TYPE_VALUE+" trovata", base);

		if(this.ebmsUserMessagePartyId== null)
			throw getException("nessuna property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE+" trovata", base);

		if(this.location== null)
			throw getException("nessuna property location trovata", base);
		
		
		this.aps = new ArrayList<>();
		int numeroLegPerSoggetto = 0;
		int numeroProcessPerSoggetto = 0;
		for(AccordoServizioParteSpecifica aps: base.getAccordoServizioParteSpecificaList()) {

			IDPortType apcKey = new IDPortType();
			apcKey.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromUri(aps.getAccordoServizioParteComune()));
			apcKey.setNome(aps.getNome());
			
			if(ptList.containsKey(apcKey)) {
				PortType pt = ptList.get(apcKey);
				this.aps.add(new APS(aps, pt, indiceInizialeLeg+numeroLegPerSoggetto, "Process_" + (indiceInizialeProcess+numeroProcessPerSoggetto)));
				numeroLegPerSoggetto += pt.getBase().sizeAzioneList();
				numeroProcessPerSoggetto++;
			} else {
				throw new Exception("APC["+apcKey+"] erogato dal soggetto ["+base+"] non trovato");
			}
		}
	}

	private Exception getException(String msg, org.openspcoop2.core.registry.Soggetto soggetto) {
		return new Exception(msg + " per il soggetto nome["+soggetto.getNome()+"] tipo ["+soggetto.getTipo()+"]");
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

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return
	 */
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
