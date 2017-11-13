/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 09 nov 2017 $
 * 
 */
public class Azione {

	private Operation base;
	private String id;
	private String ebmsActionPayloadProfile;
	private Boolean ebmsActionCompressPayload;
	
	public Azione(Operation base, String id, PayloadProfiles payloadProfiles) throws Exception {
		this.base = base;
		this.id = id;
		for(ProtocolProperty prop: base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE)) {
				
				this.ebmsActionPayloadProfile = prop.getValue();
				
				boolean exists = false;
				for(PayloadProfile prof: payloadProfiles.getPayloadProfiles()) {
					if(prof.getName().equals(this.ebmsActionPayloadProfile))
						exists = true;
				}

				if(!exists)
					throw new Exception("PayloadProfile ["+this.ebmsActionPayloadProfile+"] non dichiarato");

			} else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_COMPRESS_PAYLOAD)) {
				this.ebmsActionCompressPayload = Boolean.parseBoolean(prop.getValue());
			}
		}
		
		if(this.ebmsActionPayloadProfile == null)
			this.ebmsActionPayloadProfile = "MessageProfile";
		
		if(this.ebmsActionCompressPayload == null)
			this.ebmsActionCompressPayload = true;
		
	}
	public Operation getBase() {
		return this.base;
	}
	public void setBase(Operation base) {
		this.base = base;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getEbmsActionCompressPayload() {
		return this.ebmsActionCompressPayload;
	}
	public void setEbmsActionCompressPayload(Boolean ebmsActionCompressPayload) {
		this.ebmsActionCompressPayload = ebmsActionCompressPayload;
	}
	public String getEbmsActionPayloadProfile() {
		return this.ebmsActionPayloadProfile;
	}
	public void setEbmsActionPayloadProfile(String ebmsActionPayloadProfile) {
		this.ebmsActionPayloadProfile = ebmsActionPayloadProfile;
	}
}
