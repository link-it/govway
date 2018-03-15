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

package org.openspcoop2.protocol.as4.builder;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.pmode.TranslatorPayloadProfilesDefault;
import org.openspcoop2.protocol.as4.utils.AS4PropertiesUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;

import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;

/**
 * AS4BuilderUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13576 $, $Date: 2018-01-26 12:39:34 +0100 (Fri, 26 Jan 2018) $
 */
public class AS4BuilderUtils {

	public static String readPropertyInfoAction(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
	}
	public static String readPropertyPayloadProfile(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
	}
	private static String _readProperty(AccordoServizioParteComune aspc, String nomePortType, String azione,
			String propertyName) throws ProtocolException {
		String actionProperty = null;
		String payloadProfile = null;
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
			for (Resource resource : aspc.getResourceList()) {
				if(resource.getNome().equals(azione)) {
					actionProperty = AS4PropertiesUtils.getRequiredStringValue(resource.getProtocolPropertyList(), 
							AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
					payloadProfile = AS4PropertiesUtils.getOptionalStringValue(resource.getProtocolPropertyList(), 
							AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
					break;
				}
			}
		}
		else {
			if(nomePortType!=null) {
				for (PortType pt : aspc.getPortTypeList()) {
					if(pt.getNome().equals(nomePortType)) {
						for (Operation op : pt.getAzioneList()) {
							if(op.getNome().equals(azione)) {
								actionProperty = AS4PropertiesUtils.getRequiredStringValue(op.getProtocolPropertyList(), 
										AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
								payloadProfile = AS4PropertiesUtils.getOptionalStringValue(op.getProtocolPropertyList(), 
										AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
								break;
							}
						}
						break;
					}
				}
			}
			else {
				for (Azione azioneAccordo : aspc.getAzioneList()) {
					if(azioneAccordo.getNome().equals(azione)) {
						actionProperty = AS4PropertiesUtils.getRequiredStringValue(azioneAccordo.getProtocolPropertyList(), 
								AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
						payloadProfile = AS4PropertiesUtils.getOptionalStringValue(azioneAccordo.getProtocolPropertyList(), 
								AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
						break;
					}
				}
			}
		}
		if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(propertyName)) {
			return actionProperty;
		}
		else if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE.equals(propertyName)) {
			return payloadProfile;
		}
		return null;
	}
	
	public static PayloadProfiles readPayloadProfiles(TranslatorPayloadProfilesDefault t, 
			AccordoServizioParteComune as,IDAccordo id, boolean loadDefault) throws ProtocolException {
		byte[] profilesBytes = null;
		try {
			for (ProtocolProperty pp : as.getProtocolPropertyList()) {
				if(AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID.equals(pp.getName())) {
					profilesBytes = pp.getByteFile();
					break;
				}
			}
		}catch(Exception e) {
			throw new ProtocolException("Impossibile recuperare la configurazione del payloadProfile dall'accordo con id ["+id+"]: "+e.getMessage(),e);
		}
		return readPayloadProfiles(t, profilesBytes, id, loadDefault);
	}
	public static PayloadProfiles readPayloadProfiles(TranslatorPayloadProfilesDefault t, 
			byte[] profilesBytes, IDAccordo id, boolean loadDefault) throws ProtocolException {
		PayloadProfiles pps = null;
		
		
		try {
			if(profilesBytes!=null) {
				// aggiungo namespace per poter effettuare unmarshall
				String profiles = new String(profilesBytes);
				profiles = profiles.replace("<payloadProfiles>", "<payloadProfiles xmlns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
				eu.domibus.configuration.utils.serializer.JibxDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JibxDeserializer();
				pps = deserializer.readPayloadProfiles(profiles.getBytes());
			}
		}catch(Exception e) {
			throw new ProtocolException("Errore durante la lettura della configurazione del payloadProfile dall'accordo con id ["+id+"]: "+e.getMessage(),e);
		}

		if(!loadDefault) {
			return pps;
		}
		
		// aggiungo i payload profiles di default presenti nel file pmode-template
		if(pps==null) {
			pps = new PayloadProfiles();
		}
		List<Payload> original = new ArrayList<Payload>();
		for (Payload payload : pps.getPayloadList()) {
			original.add(payload);
		}
		List<Payload> payloadsDefault = t.getListPayloadDefault();
		for (Payload payloadDefault : payloadsDefault) {
			boolean found = false;
			for (Payload payload : original) {
				if(payloadDefault.getName().equals(payload.getName())) {
					found = true;
					break;
				}
			}
			if(!found) {
				pps.addPayload(payloadDefault);
			}
		}
		List<PayloadProfile> originalP = new ArrayList<PayloadProfile>();
		for (PayloadProfile payloadProfile : pps.getPayloadProfileList()) {
			originalP.add(payloadProfile);
		}
		List<PayloadProfile> payloadProfilesDefault = t.getListPayloadProfileDefault();
		for (PayloadProfile payloadProfileDefault : payloadProfilesDefault) {
			boolean found = false;
			for (PayloadProfile payloadProfile : originalP) {
				if(payloadProfileDefault.getName().equals(payloadProfile.getName())) {
					found = true;
					break;
				}
			}
			if(!found) {
				pps.addPayloadProfile(payloadProfileDefault);
			}
		}
		return pps;

	}
	
}
