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

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.pmode.beans.APC;
import org.openspcoop2.protocol.as4.pmode.beans.API;
import org.openspcoop2.protocol.as4.pmode.beans.Index;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.Policy;
import org.openspcoop2.protocol.as4.pmode.beans.Properties;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PModeRegistryReader {


	private IRegistryReader registryReader;
	private IConfigIntegrationReader configIntergrationReader;
	private String tipo;
	private Logger log;
	private AS4Properties as4Properties;
	
	public PModeRegistryReader(IRegistryReader registryReader, IConfigIntegrationReader configIntergrationReader, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this.registryReader = registryReader;
		this.configIntergrationReader = configIntergrationReader;
		this.tipo = protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault();
		this.log = LoggerWrapperFactory.getLogger(PModeRegistryReader.class);
		this.as4Properties = AS4Properties.getInstance();
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
	
	public Properties findProperties(List<APC> apcList) throws Exception {
		
		List<byte[]> contents = new ArrayList<byte[]>();
		for(APC apc: apcList) {
			
			if(apc.getEbmsServiceProperties() != null)
				contents.add(apc.getEbmsServiceProperties());
		}
		
		return new Properties(contents);
	}
	
	public List<Policy> findAllPolicies() throws Exception {
		List<Policy> policies = new ArrayList<>();
		
		String[] list = this.as4Properties.getSecurityPoliciesFolder().list();
		
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
		
		String defaultGW = null;
		List<String> customGW = new ArrayList<>();
		if(this.as4Properties.isDomibusGatewayRegistry()) {
			defaultGW = this.as4Properties.getDomibusGatewayRegistrySoggettoDefault();
			customGW = this.as4Properties.getDomibusGatewayRegistrySoggettoCustomList();
		}
		
		Index index = new Index();
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
			
			Soggetto soggettoPM = new Soggetto(soggetto, accordi, index);
			soggetti.add(soggettoPM);
		}
		
		return soggetti;
		
	}
	
	public List<IDSoggetto> findSoggettoAutorizzati(AccordoServizioParteSpecifica asps) throws Exception{
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		
		FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
		filtroRicercaSoggetti.setTipo(this.tipo);
		List<IDSoggetto> allIdSoggettiTmp = this.registryReader.findIdSoggetti(filtroRicercaSoggetti);
		List<IDSoggetto> allIdSoggetti = new ArrayList<>(); // escludo soggetto erogatore
		for (IDSoggetto idSoggetto : allIdSoggettiTmp) {
			if(idSoggetto.equals(idServizio.getSoggettoErogatore())==false) {
				allIdSoggetti.add(idSoggetto);
			}
		}
		
		List<IDSoggetto> list = new ArrayList<>();
		FiltroRicercaPorteApplicative filtroRicerca = new FiltroRicercaPorteApplicative();
		filtroRicerca.setTipoSoggetto(idServizio.getSoggettoErogatore().getTipo());
		filtroRicerca.setNomeSoggetto(idServizio.getSoggettoErogatore().getNome());
		filtroRicerca.setTipoServizio(idServizio.getTipo());
		filtroRicerca.setNomeServizio(idServizio.getNome());
		filtroRicerca.setVersioneServizio(idServizio.getVersione());
		List<IDPortaApplicativa> idsPA = null;
		try {
			idsPA = this.configIntergrationReader.findIdPorteApplicative(filtroRicerca);
		}catch(RegistryNotFound notFound) {}
		if(idsPA!=null && idsPA.size()>0) {
			for (IDPortaApplicativa idPortaApplicativa : idsPA) {
				PortaApplicativa pa = this.configIntergrationReader.getPortaApplicativa(idPortaApplicativa);
				if(TipoAutorizzazione.AUTHENTICATED.equals(pa.getAutorizzazione())) {
					// aggiungo solo i soggetti indicati nella lista
					if(pa.getSoggetti()!=null && pa.getSoggetti().sizeSoggettoList()>0) {
						for (PortaApplicativaAutorizzazioneSoggetto paAuthSoggetto : pa.getSoggetti().getSoggettoList()) {
							IDSoggetto idSogg = new IDSoggetto(paAuthSoggetto.getTipo(), paAuthSoggetto.getNome());
							if(list.contains(idSogg)==false) {
								list.add(idSogg);
							}
						}
					}
				}
				else {
					// autorizzo tutti
					// poi se la PA è invocabile da tutti (authz disabilitata) o per ruoli si vedrà sulla PdD
					return allIdSoggetti; // e' inutile continuare a collezionare la lista
				}
			}
		}
		
		return list;
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

	public Map<IDAccordo, API> findAllAccordi(PayloadProfiles findPayloadProfile,Properties findProperties) throws Exception {
		
		FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
		filtroRicerca.setSoggetto(new IDSoggetto(this.tipo,null));
		List<IDAccordo> allId = this.registryReader.findIdAccordiServizioParteComune(filtroRicerca);
		
		Map<IDAccordo, API> map = new HashMap<IDAccordo, API>();

		Index index = new Index();
		for(IDAccordo idAccordo: allId) {
			AccordoServizioParteComune apc = this.registryReader.getAccordoServizioParteComune(idAccordo);
			String nomeApc = "Servizio_" + index.getNextServiceId();
			map.put(idAccordo, new API(apc, nomeApc, index, findPayloadProfile,findProperties));
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
