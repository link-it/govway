/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.pmode.beans.APC;
import org.openspcoop2.protocol.as4.pmode.beans.PayloadProfiles;
import org.openspcoop2.protocol.as4.pmode.beans.Policy;
import org.openspcoop2.protocol.as4.pmode.beans.PortType;
import org.openspcoop2.protocol.as4.pmode.beans.Soggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
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
		List<IDAccordo> allIdAccordiServizioParteComune = this.registryReader.findIdAccordiServizioParteComune(filtroRicerca);
		
		int index = 1;
		for(IDAccordo idAccordo: allIdAccordiServizioParteComune) {
			AccordoServizioParteComune accordoServizioParteComune = this.registryReader.getAccordoServizioParteComune(idAccordo);
			
			apcList.add(new APC(accordoServizioParteComune, index++));
		}
		
		return apcList;
	}
	
	public PayloadProfiles findPayloadProfile(List<APC> apcList) throws Exception {
		
		List<String> fileNames = new ArrayList<>();
		for(APC apc: apcList) {
			
			if(apc.getEbmsServicePayloadProfile() != null)
				fileNames.add(apc.getEbmsServicePayloadProfile());
		}
		
		return new PayloadProfiles(AS4Properties.getInstance(this.log).getPModeTranslatorPayloadProfilesFolder(), fileNames);
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
	
	public List<Soggetto> findAllSoggetti(Map<IDPortType, PortType> portTypes) throws Exception {
		
		FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
		filtroRicercaSoggetti.setTipo(this.tipo);

		List<IDSoggetto> allIdSoggetti = this.registryReader.findIdSoggetti(filtroRicercaSoggetti);
		
		
		List<Soggetto> soggetti = new ArrayList<>();
		
		int legId = 1;
		int processId = 1;
		for(IDSoggetto idSoggetto: allIdSoggetti) {
			org.openspcoop2.core.registry.Soggetto soggetto = this.registryReader.getSoggetto(idSoggetto);
			Soggetto soggettoPM = new Soggetto(soggetto, portTypes, legId, processId);
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

	public Map<IDPortType, PortType> findAllPortTypes(PayloadProfiles findPayloadProfile) throws Exception {
		
		FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
		filtroRicerca.setSoggetto(new IDSoggetto(this.tipo,null));
		List<IDAccordo> allId = this.registryReader.findIdAccordiServizioParteComune(filtroRicerca);
		
		Map<IDPortType, PortType> map = new HashMap<>();

		int i = 1;
		int indexAzione = 1;
		for(IDAccordo idAccordo: allId) {
			AccordoServizioParteComune apc = this.registryReader.getAccordoServizioParteComune(idAccordo);
			String nomeApc = "Servizio_" + i++;
			for(org.openspcoop2.core.registry.PortType pt: apc.getPortTypeList()) {
				IDPortType id = new IDPortType();
				id.setIdAccordo(idAccordo);
				id.setNome(pt.getNome());
				
				map.put(id, new PortType(pt, nomeApc, indexAzione, findPayloadProfile));
				indexAzione+= pt.sizeAzioneList();
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
