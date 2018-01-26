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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.pmode.beans.APC;
import org.openspcoop2.protocol.as4.pmode.beans.API;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.Policy;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 08 nov 2017 $
 * 
 */
public class PModeRegistryReader {


	private IRegistryReader registryReader;
	private String tipo;
	private Logger log;
	
	public PModeRegistryReader(IRegistryReader registryReader, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this.registryReader = registryReader;
		this.tipo = protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault();
		this.log = LoggerWrapperFactory.getLogger(PModeRegistryReader.class);
	}
	
	public List<APC> findAllAPC() throws Exception {
		
		List<APC> apcList = new ArrayList<>();
		FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
		filtroRicerca.setSoggetto(new IDSoggetto(this.tipo, null));
		List<IDAccordo> allIdAccordiServizioParteComune = this.registryReader.findIdAccordiServizioParteComune(filtroRicerca);
		
		int index = 1;
		for(IDAccordo idAccordo: allIdAccordiServizioParteComune) {
			AccordoServizioParteComune accordoServizioParteComune = this.registryReader.getAccordoServizioParteComune(idAccordo);
			
			apcList.add(new APC(this.log,accordoServizioParteComune, index++));
		}
		
		return apcList;
	}
	
	public PayloadProfiles findPayloadProfile(List<APC> apcList) throws Exception {
		
		List<byte[]> contents = new ArrayList<byte[]>();
		for(APC apc: apcList) {
			
			if(apc.getEbmsServicePayloadProfile() != null)
				contents.add(apc.getEbmsServicePayloadProfile());
		}
		
		return new PayloadProfiles(contents);
	}
	
	public List<Policy> findAllPolicies() throws Exception {
		List<Policy> policies = new ArrayList<>();
		
		String[] list = AS4Properties.getInstance(this.log).getSecurityPoliciesFolder().list();
		
		for(String file: list) {
			Policy policy = new Policy();
			int lastIndexOf = file.lastIndexOf(".");
			String fileWithoutExt = (lastIndexOf > 0) ? file.substring(0, lastIndexOf) : file; 
			policy.setName(fileWithoutExt);
			policy.setPolicy(file);
			policies.add(policy);
		}
		
		return policies;
	}
	
	public List<Soggetto> findAllSoggetti(Map<IDAccordo, API> accordi) throws Exception {
		
		FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
		filtroRicercaSoggetti.setTipo(this.tipo);

		List<IDSoggetto> allIdSoggetti = this.registryReader.findIdSoggetti(filtroRicercaSoggetti);
		
		
		List<Soggetto> soggetti = new ArrayList<>();
		
		AS4Properties props = AS4Properties.getInstance(this.log);
		String defaultGW = null;
		List<String> customGW = new ArrayList<>();
		if(props.isDomibusGatewayRegistry()) {
			defaultGW = props.getDomibusGatewayRegistrySoggettoDefault();
			customGW = props.getDomibusGatewayRegistrySoggettoCustomList();
		}
		
		int legId = 1;
		int processId = 1;
		for(IDSoggetto idSoggetto: allIdSoggetti) {
			org.openspcoop2.core.registry.Soggetto soggetto = this.registryReader.getSoggetto(idSoggetto);
			
			if(defaultGW!=null) {
				if(soggetto.getNome().equals(defaultGW)) {
					continue;
				}
			}
			if(customGW!=null && customGW.size()>0) {
				if(customGW.contains(soggetto.getNome())) {
					continue;
				}
			}
			
			if(soggetto.sizeAccordoServizioParteSpecificaList()>0) {
				// xml
			}
			else {
				// db ?
				FiltroRicercaServizi filtroRicercaServizi = new FiltroRicercaServizi();
				filtroRicercaServizi.setTipoServizio(this.tipo);
				filtroRicercaServizi.setSoggettoErogatore(idSoggetto);
				List<IDServizio> listServizi = null;
				try {
					listServizi = this.registryReader.findIdAccordiServizioParteSpecifica(filtroRicercaServizi);
				}catch(RegistryNotFound e) {}
				if(listServizi!=null && listServizi.size()>0) {
					for (IDServizio idServizio : listServizi) {
						AccordoServizioParteSpecifica asps = this.registryReader.getAccordoServizioParteSpecifica(idServizio);
						soggetto.addAccordoServizioParteSpecifica(asps);
					}
				}
			}
			
			Soggetto soggettoPM = new Soggetto(soggetto, accordi, legId, processId);
			soggetti.add(soggettoPM);
			processId += soggettoPM.getBase().sizeAccordoServizioParteSpecificaList();
			legId += soggettoPM.sizeAzioni();
		}
		
		return soggetti;
		
	}

	public String getNomeSoggettoOperativo() throws Exception {
		
		try {
			List<String> allIdPorteDominio = this.registryReader.findIdPorteDominio(true);
			
			if(allIdPorteDominio.size() <= 0)
				throw new Exception("Impossibile trovare una PdD di tipo 'operativo'");
			
			PortaDominio portaDominio = this.registryReader.getPortaDominio(allIdPorteDominio.get(0));
			
			FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
			filtroRicercaSoggetti.setTipo(this.tipo);
			filtroRicercaSoggetti.setNomePdd(portaDominio.getNome());
				
			List<IDSoggetto> allIdSoggetti = this.registryReader.findIdSoggetti(filtroRicercaSoggetti);

			if(allIdSoggetti.size() <= 0)
				throw new Exception("Impossibile trovare il soggetto relativo alla PdD ["+portaDominio.getNome()+"]");
			
			return allIdSoggetti.get(0).getNome();
		} catch(Exception e) {
			throw new Exception("Impossibile trovare il soggetto operativo: " + e.getMessage(), e);
		}
	}

	public Map<IDAccordo, API> findAllAccordi(PayloadProfiles findPayloadProfile) throws Exception {
		
		FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
		filtroRicerca.setSoggetto(new IDSoggetto(this.tipo,null));
		List<IDAccordo> allId = this.registryReader.findIdAccordiServizioParteComune(filtroRicerca);
		
		Map<IDAccordo, API> map = new HashMap<IDAccordo, API>();

		int i = 1;
		int indexAzione = 1;
		for(IDAccordo idAccordo: allId) {
			AccordoServizioParteComune apc = this.registryReader.getAccordoServizioParteComune(idAccordo);
			String nomeApc = "Servizio_" + i++;
			map.put(idAccordo, new API(apc, nomeApc, indexAzione, findPayloadProfile));
			if(ServiceBinding.SOAP.equals(apc.getServiceBinding())) {
				if(apc.sizeAzioneList()>0) {
					indexAzione+= apc.sizeAzioneList();
				}
				if(apc.sizePortTypeList()>0) {
					for (org.openspcoop2.core.registry.PortType pt : apc.getPortTypeList()) {
						indexAzione+= pt.sizeAzioneList();
					}
				}
			}
			else {
				indexAzione+= apc.sizeResourceList();
			}
		}
		return map;
	}

	public List<ProtocolProperty> findAllPartyIdTypes(List<Soggetto> soggetti) throws Exception {
		Map<String, ProtocolProperty> propertiesMap = new HashMap<>();
		
		for(Soggetto soggetto: soggetti) {
			if(!propertiesMap.containsKey(soggetto.getEbmsUserMessagePartyIdTypeName())) {
				ProtocolProperty property = new ProtocolProperty();
				
				property.setName(soggetto.getEbmsUserMessagePartyIdTypeName());
				property.setValue(soggetto.getEbmsUserMessagePartyIdTypeValue());
				
				propertiesMap.put(soggetto.getEbmsUserMessagePartyIdTypeName(), property);
			} else {
				ProtocolProperty protocolProperty = propertiesMap.get(soggetto.getEbmsUserMessagePartyIdTypeName());
				if(!protocolProperty.getValue().equals(soggetto.getEbmsUserMessagePartyIdTypeValue())) {
					throw new Exception("Trovati getEbmsUserMessagePartyIdTypeName ["+soggetto.getEbmsUserMessagePartyIdTypeName()+"] con value in conflitto: ["+protocolProperty.getValue()+"] - ["+soggetto.getEbmsUserMessagePartyIdTypeValue()+"]");
				}
			}
		}
		
		return Arrays.asList(propertiesMap.values().toArray(new ProtocolProperty[]{}));
	}

}
