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
import org.openspcoop2.protocol.as4.pmode.beans.APS;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.Properties;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.TemplateUtils;

import freemarker.template.Template;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Translator {

	private Template template;
	private PModeRegistryReader reader;


	public Translator(PModeRegistryReader pmodeRegistryReader) throws ProtocolException {
		try {
			this.template = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-template.ftl");
			this.reader = pmodeRegistryReader;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private void checkInitDriver() throws ProtocolException {
		if(this.reader==null) {
			throw new ProtocolException("Translator initialized without IRegistryReader. Use different constructor");
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
			@SuppressWarnings("unchecked")
			List<Soggetto> soggetti = (List<Soggetto>) map.get("soggetti");
			for (Soggetto soggetto : soggetti) {
				if(nomeSoggetto.equals(soggetto.getBase().getNome())) {
					map.put("soggettoOperativo", soggetto.getEbmsUserMessagePartyCN());
					break;
				}
			}
			this.template.process(map, out);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	private Map<String, Object> buildMap() throws Exception {
		
		this.checkInitDriver();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("policies", this.reader.findAllPolicies());
		List<APC> apcList = this.reader.findAllAPC();
		map.put("apcList", apcList);
		PayloadProfiles findPayloadProfile = this.reader.findPayloadProfile(apcList);
		map.put("payloadProfiles", findPayloadProfile);
		Properties findProperties = this.reader.findProperties(apcList);
		map.put("properties", findProperties);
		Map<IDAccordo, API> accordi = this.reader.findAllAccordi(findPayloadProfile,findProperties);
		map.put("apis", accordi);
		List<Soggetto> soggetti = this.reader.findAllSoggetti(accordi);
		for (Soggetto soggetto : soggetti) {
			List<APS> listAPS = soggetto.getAps();
			for (APS aps : listAPS) {
				aps.initCNFruitori(soggetti);
				aps.initCNFruitori(soggetti, this.reader.findSoggettoAutorizzati(aps.getBase()));
				aps.checkCNFruitori();
			}
		}
		map.put("soggetti", soggetti);
		map.put("partyIdTypes", this.reader.findAllPartyIdTypes(soggetti));
		return map;
	}

}
