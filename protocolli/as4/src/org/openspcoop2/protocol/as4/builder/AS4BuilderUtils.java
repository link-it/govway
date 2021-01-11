/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import org.openspcoop2.protocol.as4.pmode.TranslatorPropertiesDefault;
import org.openspcoop2.protocol.as4.utils.AS4PropertiesUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;

import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;
import eu.domibus.configuration.Property;
import eu.domibus.configuration.PropertySet;
import eu.domibus.configuration.Properties;

/**
 * AS4BuilderUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4BuilderUtils {

	// ACTION
	
	public static String readPropertyInfoAction(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
	}
	public static String readPropertyPayloadProfile(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
	}
	public static String readPropertyPropertySet(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET);
	}
	private static String _readProperty(AccordoServizioParteComune aspc, String nomePortType, String azione,
			String propertyName) throws ProtocolException {
		String actionProperty = null;
		String payloadProfile = null;
		String propertySet = null;
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
			for (Resource resource : aspc.getResourceList()) {
				if(resource.getNome().equals(azione)) {
					actionProperty = AS4PropertiesUtils.getRequiredStringValue(resource.getProtocolPropertyList(), 
							AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION);
					payloadProfile = AS4PropertiesUtils.getOptionalStringValue(resource.getProtocolPropertyList(), 
							AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PAYLOAD_PROFILE);
					propertySet = AS4PropertiesUtils.getOptionalStringValue(resource.getProtocolPropertyList(), 
							AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET);
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
								propertySet = AS4PropertiesUtils.getOptionalStringValue(op.getProtocolPropertyList(), 
										AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET);
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
						propertySet = AS4PropertiesUtils.getOptionalStringValue(azioneAccordo.getProtocolPropertyList(), 
								AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET);
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
		else if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_ACTION_PROPERTY_SET.equals(propertyName)) {
			return propertySet;
		}
		return null;
	}
	
	
	// PAYLOAD PROFILE
	
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
				profiles = profiles.replace("<payloadProfiles>", "<ns:payloadProfiles xmlns:ns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
				profiles = profiles.replace("</payloadProfiles","</ns:payloadProfiles"); 
				eu.domibus.configuration.utils.serializer.JaxbDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JaxbDeserializer();
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
	
	
	
	
	
	
	// PROPERTIES
	
	public static Properties readProperties(TranslatorPropertiesDefault t, 
			AccordoServizioParteComune as,IDAccordo id, boolean loadDefault) throws ProtocolException {
		byte[] profilesBytes = null;
		try {
			for (ProtocolProperty pp : as.getProtocolPropertyList()) {
				if(AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_ID.equals(pp.getName())) {
					profilesBytes = pp.getByteFile();
					break;
				}
			}
		}catch(Exception e) {
			throw new ProtocolException("Impossibile recuperare la configurazione delle proprietà dall'accordo con id ["+id+"]: "+e.getMessage(),e);
		}
		return readProperties(t, profilesBytes, id, loadDefault);
	}
	public static Properties readProperties(TranslatorPropertiesDefault t, 
			byte[] profilesBytes, IDAccordo id, boolean loadDefault) throws ProtocolException {
		Properties pps = null;
		
		
		try {
			if(profilesBytes!=null) {
				// aggiungo namespace per poter effettuare unmarshall
				String profiles = new String(profilesBytes);
				profiles = profiles.replace("<properties>", "<ns:properties xmlns:ns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
				profiles = profiles.replace("</properties","</ns:properties"); 
				eu.domibus.configuration.utils.serializer.JaxbDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JaxbDeserializer();
				pps = deserializer.readProperties(profiles.getBytes());
			}
		}catch(Exception e) {
			throw new ProtocolException("Errore durante la lettura della configurazione delle proprietà dall'accordo con id ["+id+"]: "+e.getMessage(),e);
		}

		if(!loadDefault) {
			return pps;
		}
		
		// aggiungo i payload profiles di default presenti nel file pmode-template
		if(pps==null) {
			pps = new Properties();
		}
		List<Property> original = new ArrayList<Property>();
		for (Property p : pps.getPropertyList()) {
			original.add(p);
		}
		List<Property> propertyDefault = t.getListPropertyDefault();
		for (Property pDefault : propertyDefault) {
			boolean found = false;
			for (Property payload : original) {
				if(pDefault.getName().equals(payload.getName())) {
					found = true;
					break;
				}
			}
			if(!found) {
				pps.addProperty(pDefault);
			}
		}
		List<PropertySet> originalP = new ArrayList<PropertySet>();
		for (PropertySet p : pps.getPropertySetList()) {
			originalP.add(p);
		}
		List<PropertySet> propertySetDefault = t.getListPropertySetDefault();
		for (PropertySet psDefault : propertySetDefault) {
			boolean found = false;
			for (PropertySet payloadProfile : originalP) {
				if(psDefault.getName().equals(payloadProfile.getName())) {
					found = true;
					break;
				}
			}
			if(!found) {
				pps.addPropertySet(psDefault);
			}
		}
		return pps;

	}
	
}
