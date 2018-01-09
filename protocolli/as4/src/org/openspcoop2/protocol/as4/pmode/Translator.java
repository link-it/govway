package org.openspcoop2.protocol.as4.pmode;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.protocol.as4.pmode.beans.APC;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.PortType;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.resources.TemplateUtils;

import freemarker.template.Template;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 07 nov 2017 $
 * 
 */
public class Translator {

	private Template template;


	/**
	 * @param p 
	 * @throws IOException 
	 * 
	 */
	public Translator() throws IOException {
		this.template = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-template.xml");
	}
	

	public void translate(IRegistryReader driver, IProtocolFactory<?> protocolFactory, Writer out) throws Exception {
		PModeRegistryReader reader = new PModeRegistryReader(driver, protocolFactory);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("policies", reader.findAllPolicies());
		List<APC> apcList = reader.findAllAPC();
		map.put("apcList", apcList);
		PayloadProfiles findPayloadProfile = reader.findPayloadProfile(apcList);
		map.put("payloadProfiles", findPayloadProfile);
		Map<IDPortType, PortType> portTypes = reader.findAllPortTypes(findPayloadProfile);
		map.put("portTypes", portTypes);
		List<Soggetto> soggetti = reader.findAllSoggetti(portTypes);
		map.put("soggetti", soggetti);
		map.put("partyIdTypes", reader.findAllPartyIdTypes(soggetti));
		map.put("soggettoOperativo", reader.getNomeSoggettoOperativo());
		this.template.process(map, out);
	}
	
}
