/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.List;

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

	// soap
	private org.openspcoop2.core.registry.Azione baseAzione;
	private Operation baseOperation;
	// rest
	private org.openspcoop2.core.registry.Resource baseResource;
	
	private String id;
	private String ebmsUserMessageCollaborationInfoActionName;
	private String ebmsActionPayloadProfile;
	private Boolean ebmsActionCompressPayload;
	
	public Azione(org.openspcoop2.core.registry.Azione base, String id, PayloadProfiles payloadProfiles) throws Exception {
		this(base.getProtocolPropertyList(),id,base.getNome(),payloadProfiles);
		this.baseAzione = base;
	}
	public Azione(Operation base, String id, PayloadProfiles payloadProfiles) throws Exception {
		this(base.getProtocolPropertyList(),id,base.getNome(),payloadProfiles);
		this.baseOperation = base;
	}
	public Azione(org.openspcoop2.core.registry.Resource base, String id, PayloadProfiles payloadProfiles) throws Exception {
		this(base.getProtocolPropertyList(),id,base.getNome(),payloadProfiles);
		this.baseResource = base;
	}
	private Azione(List<ProtocolProperty> list, String id, String nomeAzione, PayloadProfiles payloadProfiles) throws Exception {
		
		this.id = id;
		for(ProtocolProperty prop: list) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION)) {
				this.ebmsUserMessageCollaborationInfoActionName = prop.getValue();
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE)) {
				
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
		
		if(this.ebmsUserMessageCollaborationInfoActionName == null)
			throw new Exception("Property ["+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION+"] non definita per l'azione ["+nomeAzione+"]");
		
		if(this.ebmsActionPayloadProfile == null)
			this.ebmsActionPayloadProfile = "MessageProfile";
		
		if(this.ebmsActionCompressPayload == null)
			this.ebmsActionCompressPayload = true;
		
	}

	public org.openspcoop2.core.registry.Azione getBaseAzione() {
		return this.baseAzione;
	}
	public void setBaseAzione(org.openspcoop2.core.registry.Azione baseAzione) {
		this.baseAzione = baseAzione;
	}
	public String getEbmsUserMessageCollaborationInfoActionName() {
		return this.ebmsUserMessageCollaborationInfoActionName;
	}
	public void setEbmsUserMessageCollaborationInfoActionName(String ebmsUserMessageCollaborationInfoActionName) {
		this.ebmsUserMessageCollaborationInfoActionName = ebmsUserMessageCollaborationInfoActionName;
	}
	public Operation getBaseOperation() {
		return this.baseOperation;
	}
	public void setBaseOperation(Operation baseOperation) {
		this.baseOperation = baseOperation;
	}
	public org.openspcoop2.core.registry.Resource getBaseResource() {
		return this.baseResource;
	}
	public void setBaseResource(org.openspcoop2.core.registry.Resource baseResource) {
		this.baseResource = baseResource;
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
