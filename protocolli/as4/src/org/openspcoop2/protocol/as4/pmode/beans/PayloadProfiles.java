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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PayloadProfiles {

	private List<Payload> payloads;
	private List<PayloadProfile> payloadProfiles;
	
	public static List<String> payloadsProfilesDefault = new ArrayList<String>(); // presenti nel file pmode-template.xml
	static {
		payloadsProfilesDefault.add("MessageProfile");
	}
	
	public static List<String> payloadsDefault = new ArrayList<String>(); // presenti nel file pmode-template.xml
	static {
		payloadsDefault.add("businessContentPayload");
		payloadsDefault.add("businessContentAttachment");
	}
	
	public PayloadProfiles(List<byte[]> contents) throws Exception {

		this.payloads = new ArrayList<>();
		this.payloadProfiles = new ArrayList<>();
		
		Map<String, Payload> payloadsMap = new HashMap<>();
		Map<String, PayloadProfile> payloadProfilesMap = new HashMap<>();

		for(byte[] content: contents) {
			
			Document doc = XMLUtils.getInstance().newDocument(content);
			doc.getDocumentElement().normalize();
	
			NodeList payloadList = doc.getElementsByTagName("payload");
			for (int temp = 0; temp < payloadList.getLength(); temp++) {
				Node node = payloadList.item(temp);
				Payload payload = new Payload(node);
				if(payloadsDefault.contains(payload.getName())) {
					throw new Exception("Il payload con nome ["+payload.getName()+"] è già presente nella configurazione di default; modificare il nome");
				}
				if(payloadsMap.containsKey(payload.getName())) {
					if(!payloadsMap.get(payload.getName()).equals(payload)) {
						throw new Exception("Payload con nome ["+payload.getName()+"] risulta utilizzato su più accordi");
					}
				}
				payloadsMap.put(payload.getName(), payload);
			}
	
			NodeList payloadProfileList = doc.getElementsByTagName("payloadProfile");
			for (int temp = 0; temp < payloadProfileList.getLength(); temp++) {
				Node node = payloadProfileList.item(temp);
				PayloadProfile payloadProfile = new PayloadProfile(node);
				if(payloadsProfilesDefault.contains(payloadProfile.getName())) {
					throw new Exception("Il payload-profile con nome ["+payloadProfile.getName()+"] è già presente nella configurazione di default; modificare il nome");
				}
				if(payloadProfilesMap.containsKey(payloadProfile.getName())) {
					if(!payloadProfilesMap.get(payloadProfile.getName()).equals(payloadProfile)) {
						throw new Exception("Payload-profile con nome ["+payloadProfile.getName()+"] risulta utilizzato su più accordi");
					}
				}
				payloadProfilesMap.put(payloadProfile.getName(), payloadProfile);
			}
		}
		
		this.payloads.addAll(payloadsMap.values());
		this.payloadProfiles.addAll(payloadProfilesMap.values());
	}
	
	public List<Payload> getPayloads() {
		return this.payloads;
	}
	public void setPayloads(List<Payload> payloads) {
		this.payloads = payloads;
	}
	public List<PayloadProfile> getPayloadProfiles() {
		return this.payloadProfiles;
	}
	public void setPayloadProfiles(List<PayloadProfile> payloadProfiles) {
		this.payloadProfiles = payloadProfiles;
	}
}
