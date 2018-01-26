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
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
import org.openspcoop2.protocol.as4.pmode.Translator;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;

/**
 * AS4PayloadProfilesUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13384 $, $Date: 2017-10-26 12:24:53 +0200 (Thu, 26 Oct 2017) $
 */
public class AS4PayloadProfilesUtils {

	public static PayloadProfiles read(IRegistryReader registryReader, IProtocolFactory<?> protocolFactory, 
			AccordoServizioParteComune as,IDAccordo id, boolean loadDefault) throws ProtocolException {
		PayloadProfiles pps = null;
		
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
		Translator t = new Translator(registryReader, protocolFactory);
		List<Payload> original = new ArrayList<Payload>();
		for (Payload payload : pps.getPayloadList()) {
			original.add(payload);
		}
		List<Payload> payloadsDefault = t.translatePayloadDefault();
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
		List<PayloadProfile> payloadProfilesDefault = t.translatePayloadProfileDefault();
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
