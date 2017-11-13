/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 nov 2017 $
 * 
 */
public class APC {

	private AccordoServizioParteComune base;
	private String ebmsServicePayloadProfile;
	private String ebmsUserMessageCollaborationInfoServiceType;
	private String id;
	
	public APC(AccordoServizioParteComune base, int index) throws Exception {
		this.base = base;
		this.id = "Servizio_" + index;
		for(ProtocolProperty prop: base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_SERVICE_PAYLOAD_PROFILE)) {
				this.ebmsServicePayloadProfile = prop.getFile();
			}else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE)) {
				this.ebmsUserMessageCollaborationInfoServiceType = prop.getValue();
			}
		}
		
		if(this.ebmsUserMessageCollaborationInfoServiceType == null)
			throw new Exception("Property ["+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE+"] non definita per l'apc ["+base+"]");
	}

	public AccordoServizioParteComune getBase() {
		return this.base;
	}

	public void setBase(AccordoServizioParteComune base) {
		this.base = base;
	}

	public String getEbmsUserMessageCollaborationInfoServiceType() {
		return this.ebmsUserMessageCollaborationInfoServiceType;
	}

	public void setEbmsUserMessageCollaborationInfoServiceType(String ebmsUserMessageCollaborationInfoServiceType) {
		this.ebmsUserMessageCollaborationInfoServiceType = ebmsUserMessageCollaborationInfoServiceType;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEbmsServicePayloadProfile() {
		return this.ebmsServicePayloadProfile;
	}

	public void setEbmsServicePayloadProfile(String ebmsServicePayloadProfile) {
		this.ebmsServicePayloadProfile = ebmsServicePayloadProfile;
	}
}
