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

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.protocol.as4.pmode.beans.APC;
import org.openspcoop2.protocol.as4.pmode.beans.API;
import org.openspcoop2.protocol.as4.pmode.beans.APS;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.TemplateUtils;

import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import freemarker.template.Template;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Translator {

	private Template template;
	private Template templatePayloadDefault;
	private Template templatePayloadProfileDefault;
	private Template templatePayloadProfileDefault_complete;
	private PModeRegistryReader reader;


	public Translator() throws ProtocolException {
		this(null);
	}
	public Translator(PModeRegistryReader pmodeRegistryReader) throws ProtocolException {
		try {
			this.templatePayloadProfileDefault_complete = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-payloadProfileDefault-template.ftl");
			this.templatePayloadDefault = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-payloadDefault.ftl");
			this.templatePayloadProfileDefault = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-payloadProfileDefault.ftl");
			if(pmodeRegistryReader!=null) {
				this.template = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-template.ftl");
				this.reader = pmodeRegistryReader;
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private void checkInitDriver() throws ProtocolException {
		if(this.reader==null) {
			throw new ProtocolException("Translator initialized without IRegistryReader. Use different constructor");
		}
	}

	public byte[] translatePayloadProfileDefaultAsCompleteXml() throws ProtocolException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			StringWriter writer = new StringWriter();
			this.templatePayloadProfileDefault_complete.process(map, writer);
			String s = writer.toString();
			return s.getBytes();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public List<Payload> translatePayloadDefault() throws ProtocolException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			StringWriter writer = new StringWriter();
			this.templatePayloadDefault.process(map, writer);
			String s = writer.toString();
			BufferedReader br = new BufferedReader(new StringReader(s));
			List<Payload> list = new ArrayList<Payload>();
			String line;
			StringBuffer bf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append(line);
				if(line.contains("/>") || line.contains("</payload>")) {
					String finalString = bf.toString();
					if(finalString!=null && !"".equals(finalString)) {
						String sWithNamespace = finalString.replace("<payload>", 
								"<payload xmlns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
						sWithNamespace = sWithNamespace.replace("<payload ", 
								"<payload xmlns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\" ");
						eu.domibus.configuration.utils.serializer.JibxDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JibxDeserializer();
						Payload p = deserializer.readPayload(sWithNamespace.getBytes());
						list.add(p);
					}
					bf = new StringBuffer();
				}
			}
			return list;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	public List<PayloadProfile> translatePayloadProfileDefault() throws ProtocolException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			StringWriter writer = new StringWriter();
			this.templatePayloadProfileDefault.process(map, writer);
			String s = writer.toString();
			BufferedReader br = new BufferedReader(new StringReader(s));
			List<PayloadProfile> list = new ArrayList<PayloadProfile>();
			String line;
			StringBuffer bf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append(line);
				if(line.contains("</payloadProfile>")) {
					String finalString = bf.toString();
					if(finalString!=null && !"".equals(finalString)) {
						String sWithNamespace = 
								finalString.replace("<payloadProfile>", "<payloadProfile xmlns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
						sWithNamespace = sWithNamespace.replace("<payloadProfile ", 
								"<payloadProfile xmlns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\" ");
						eu.domibus.configuration.utils.serializer.JibxDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JibxDeserializer();
						PayloadProfile p = deserializer.readPayloadProfile(sWithNamespace.getBytes());
						list.add(p);
					}
					bf = new StringBuffer();
				}
			}
			return list;
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
		Map<IDAccordo, API> accordi = this.reader.findAllAccordi(findPayloadProfile);
		map.put("apis", accordi);
		List<Soggetto> soggetti = this.reader.findAllSoggetti(accordi);
		for (Soggetto soggetto : soggetti) {
			List<APS> listAPS = soggetto.getAps();
			for (APS aps : listAPS) {
				aps.initCNFruitori(soggetti);
			}
		}
		map.put("soggetti", soggetti);
		map.put("partyIdTypes", this.reader.findAllPartyIdTypes(soggetti));
		return map;
	}

}
