package org.openspcoop2.protocol.as4.pmode;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.protocol.as4.pmode.beans.APC;
import org.openspcoop2.protocol.as4.pmode.beans.API;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
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
	private PModeRegistryReader reader;


	public Translator(IRegistryReader driver, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		try {
			this.template = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-template.xml");
			this.reader = new PModeRegistryReader(driver, protocolFactory);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	public void translate(OutputStream out) throws Exception {
		this.translate(out, this.reader.getNomeSoggettoOperativo());
	}
	public void translate(OutputStream out,String nomeSoggetto) throws Exception {
		OutputStreamWriter oow = new OutputStreamWriter(out);
		this.translate(oow, nomeSoggetto);
		oow.flush();
		oow.close();
	}
	
	public void translate(Writer out) throws Exception {
		this.translate(out, this.reader.getNomeSoggettoOperativo());
	}
	public void translate(Writer out,String nomeSoggetto) throws Exception {
		try {
			Map<String, Object> map = this.buildMap();
			map.put("soggettoOperativo", nomeSoggetto);
			this.template.process(map, out);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private Map<String, Object> buildMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("policies", this.reader.findAllPolicies());
		List<APC> apcList = this.reader.findAllAPC();
		map.put("apcList", apcList);
		PayloadProfiles findPayloadProfile = this.reader.findPayloadProfile(apcList);
		map.put("payloadProfiles", findPayloadProfile);
		Map<IDAccordo, API> accordi = this.reader.findAllAccordi(findPayloadProfile);
		map.put("apis", accordi);
		List<Soggetto> soggetti = this.reader.findAllSoggetti(accordi);
		map.put("soggetti", soggetti);
		map.put("partyIdTypes", this.reader.findAllPartyIdTypes(soggetti));
		return map;
	}
	
}
