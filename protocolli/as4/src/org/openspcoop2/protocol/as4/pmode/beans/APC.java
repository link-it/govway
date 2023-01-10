/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import java.io.File;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class APC {

	private AccordoServizioParteComune base;
	private byte[] ebmsServicePayloadProfile;
	private byte[] ebmsServiceProperties;
	private String ebmsUserMessageCollaborationInfoServiceName;
	private String ebmsUserMessageCollaborationInfoServiceType;
	private String id;
	
	public APC(Logger log, AccordoServizioParteComune base, int index) throws Exception {
		this.base = base;
		this.id = "Servizio_" + index;
		for(ProtocolProperty prop: base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_SERVICE_PAYLOAD_PROFILE)) {
				if(prop.getByteFile()!=null) {
					this.ebmsServicePayloadProfile = prop.getByteFile();
				}
				else {			
					File fCheck = new File(prop.getFile());
					if(fCheck.exists()==false) {
						fCheck = new File(AS4Properties.getInstance().getPModeTranslatorPayloadProfilesFolder(), prop.getFile());
					}
					this.ebmsServicePayloadProfile = FileSystemUtilities.readBytesFromFile(fCheck);
				}
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_SERVICE_PROPERTIES)) {
				if(prop.getByteFile()!=null) {
					this.ebmsServiceProperties = prop.getByteFile();
				}
				else {			
					File fCheck = new File(prop.getFile());
					if(fCheck.exists()==false) {
						fCheck = new File(AS4Properties.getInstance().getPModeTranslatorPropertiesFolder(), prop.getFile());
					}
					this.ebmsServiceProperties = FileSystemUtilities.readBytesFromFile(fCheck);
				}
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE)) {
				this.ebmsUserMessageCollaborationInfoServiceType = prop.getValue();
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE)) {
				this.ebmsUserMessageCollaborationInfoServiceName = prop.getValue();
			}
		}
		
		if(this.ebmsUserMessageCollaborationInfoServiceName == null)
			throw new Exception("Property ["+AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE+"] non definita per l'apc ["+base+"]");
	}

	public AccordoServizioParteComune getBase() {
		return this.base;
	}

	public void setBase(AccordoServizioParteComune base) {
		this.base = base;
	}

	public String getEbmsUserMessageCollaborationInfoServiceName() {
		return this.ebmsUserMessageCollaborationInfoServiceName;
	}

	public void setEbmsUserMessageCollaborationInfoServiceName(String ebmsUserMessageCollaborationInfoServiceName) {
		this.ebmsUserMessageCollaborationInfoServiceName = ebmsUserMessageCollaborationInfoServiceName;
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

	public byte[] getEbmsServicePayloadProfile() {
		return this.ebmsServicePayloadProfile;
	}

	public void setEbmsServicePayloadProfile(byte[] ebmsServicePayloadProfile) {
		this.ebmsServicePayloadProfile = ebmsServicePayloadProfile;
	}
	
	public byte[] getEbmsServiceProperties() {
		return this.ebmsServiceProperties;
	}

	public void setEbmsServiceProperties(byte[] ebmsServiceProperties) {
		this.ebmsServiceProperties = ebmsServiceProperties;
	}
}
