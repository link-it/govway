/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.io.File;
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
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 10 nov 2017 $
 * 
 */
public class PayloadProfiles {

	List<Payload> payloads;
	List<PayloadProfile> payloadProfiles;
	
	public PayloadProfiles(File file, List<String> fileNames) throws Exception {

		this.payloads = new ArrayList<>();
		this.payloadProfiles = new ArrayList<>();
		
		Map<String, Payload> payloadsMap = new HashMap<>();
		Map<String, PayloadProfile> payloadProfilesMap = new HashMap<>();

		for(String fileName: fileNames) {
			Document doc = XMLUtils.getInstance().newDocument(new File(file, fileName));
			doc.getDocumentElement().normalize();
	
			NodeList payloadList = doc.getElementsByTagName("payload");
			for (int temp = 0; temp < payloadList.getLength(); temp++) {
				Node node = payloadList.item(temp);
				Payload payload = new Payload(node);
				if(payloadsMap.containsKey(payload.getName())) {
					if(!payloadsMap.get(payload.getName()).equals(payload)) {
						throw new Exception("Conflitto di payload per il nome ["+payload.getName()+"]");
					}
				}
				payloadsMap.put(payload.getName(), payload);
			}
	
			NodeList payloadProfileList = doc.getElementsByTagName("payloadProfile");
			for (int temp = 0; temp < payloadProfileList.getLength(); temp++) {
				Node node = payloadProfileList.item(temp);
				PayloadProfile payloadProfile = new PayloadProfile(node);
				if(payloadProfilesMap.containsKey(payloadProfile.getName())) {
					if(!payloadProfilesMap.get(payloadProfile.getName()).equals(payloadProfile)) {
						throw new Exception("Conflitto di payloadProfile per il nome ["+payloadProfile.getName()+"]");
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
