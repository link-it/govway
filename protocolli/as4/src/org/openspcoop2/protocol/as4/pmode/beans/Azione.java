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

import java.util.List;

import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
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
	
	public Azione(org.openspcoop2.core.registry.Azione base, String id, PayloadProfiles payloadProfiles,
			List<eu.domibus.configuration.PayloadProfile> listPayloadProfileDefault) throws Exception {
		this(base.getProtocolPropertyList(),id,base.getNome(),payloadProfiles, listPayloadProfileDefault);
		this.baseAzione = base;
	}
	public Azione(Operation base, String id, PayloadProfiles payloadProfiles,
			List<eu.domibus.configuration.PayloadProfile> listPayloadProfileDefault) throws Exception {
		this(base.getProtocolPropertyList(),id,base.getNome(),payloadProfiles, listPayloadProfileDefault);
		this.baseOperation = base;
	}
	public Azione(org.openspcoop2.core.registry.Resource base, String id, PayloadProfiles payloadProfiles,
			List<eu.domibus.configuration.PayloadProfile> listPayloadProfileDefault) throws Exception {
		this(base.getProtocolPropertyList(),id,base.getNome(),payloadProfiles, listPayloadProfileDefault);
		this.baseResource = base;
	}
	private Azione(List<ProtocolProperty> list, String id, String nomeAzione, PayloadProfiles payloadProfiles, 
			List<eu.domibus.configuration.PayloadProfile> listPayloadProfileDefault) throws Exception {
		
		this.id = id;
		for(ProtocolProperty prop: list) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION)) {
				this.ebmsUserMessageCollaborationInfoActionName = prop.getValue();
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE)) {
				
				this.ebmsActionPayloadProfile = prop.getValue();
				
				boolean isDefault = false;
				if(listPayloadProfileDefault!=null && listPayloadProfileDefault.size()>0) {
					for (eu.domibus.configuration.PayloadProfile payloadProfileDefault : listPayloadProfileDefault) {
						if(payloadProfileDefault.getName().equals(this.ebmsActionPayloadProfile)) {
							isDefault = true;
							break;
						}
					}
				}
				
				if(isDefault) {
					continue;
				}
				
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
		
		if(this.ebmsActionPayloadProfile == null) {
			this.ebmsActionPayloadProfile = listPayloadProfileDefault.get(0).getName();
		}
		
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
