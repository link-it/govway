/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.modipa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPortTypeAzioni;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaRisorse;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ModIImbustamentoUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIUtilities {

	public static String extractCorrelationIdFromLocation(String resourcePath, String location, boolean throwException, Logger log) throws Exception {
		if(resourcePath==null) {
			throw new Exception("Resource path della risorsa correlata non trovato");
		}
		
		String [] rSplit = resourcePath.split("/");
		int lastDynamicPosition = -1;
		for (String r : rSplit) {
			if(r.startsWith("{")) {
				lastDynamicPosition++;
			}
		}
		
		int lastDynamicPos = 0;
		StringBuilder sb = new StringBuilder();
		for (String r : rSplit) {
			if(r==null || "".equals(r)) {
				continue;
			}
			sb.append("/+");
			if(r.startsWith("{")) {
				if(lastDynamicPos == lastDynamicPosition) {
					sb.append("([^/]+)");
				}
				else {
					sb.append(".*");
				}
				lastDynamicPos++;
			}
			else {
				sb.append(r);
			}
		}
		sb.append("/?");
		String pattern = sb.toString();
		
		try {
			return RegularExpressionEngine.getStringFindPattern(location, pattern);
		}catch(Exception e) {
			log.error("Estrazione Id Correlazione dalla Location '"+location+"' non riuscita (resourcePath: "+resourcePath+") (pattern: "+pattern+")");
			if(throwException) {
				throw e;
			}
		}
		
		return null;
	}
	
	public static String extractCorrelationId(String location, AccordoServizioParteComune apiContenenteRisorsa, 
			String azione, String asyncInteractionRole, Logger log) throws Exception {
		if(location!=null) {
			location = location.trim();
			
			String resourcePath = null;
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
				for (Resource r : apiContenenteRisorsa.getResourceList()) {
					if(r.getNome().equals(azione)) {
						continue;
					}
					String ruolo = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(r.getProtocolPropertyList(), 
							ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
					if(ruolo!=null && ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(ruolo)) {
						String actionCorrelata = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(r.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);	
						if(actionCorrelata!=null && actionCorrelata.equals(azione)) {
							resourcePath = r.getPath();
							break;
						}
					}
					
				}
			}
			else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole)) {
				
				String azioneRichiesta = null;
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(asyncInteractionRole)) {
					Resource rRichiestaStato = null;
					for (Resource r : apiContenenteRisorsa.getResourceList()) {
						if(r.getNome().equals(azione)) {
							rRichiestaStato = r;
							break;
						}
					}
					if(rRichiestaStato==null) {
						throw new Exception("Risorsa con ruolo 'Richiesta Stato' non trovata");
					}
					azioneRichiesta = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(rRichiestaStato.getProtocolPropertyList(), 
							ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);	
				}
				
				for (Resource r : apiContenenteRisorsa.getResourceList()) {
					if(r.getNome().equals(azioneRichiesta)) {
						continue;
					}
					String ruolo = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(r.getProtocolPropertyList(), 
							ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
					if(ruolo!=null && ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(ruolo)) {
						String actionCorrelata = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(r.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);	
						if(actionCorrelata!=null && actionCorrelata.equals(azioneRichiesta)) {
							resourcePath = r.getPath();
							break;
						}
					}
					
				}
			}
			 
			if(resourcePath==null) {
				throw new Exception("Resource path della risorsa correlata non trovato");
			}
			return ModIUtilities.extractCorrelationIdFromLocation(resourcePath, location, false, log);
						
		}
		return null;
	}
	
	public static String getReplyToErogazione(IDSoggetto idSoggettoErogatoreServizioCorrelato, 
			AccordoServizioParteComune aspc, String portType, String azione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IProtocolFactory<?> protocolFactory, IState state) throws Exception {
		return getReplyTo(false, null, idSoggettoErogatoreServizioCorrelato, 
				aspc, portType, azione,
				registryReader, configIntegrationReader, 
				protocolFactory, state);
	}
	public static String getReplyToFruizione(IDSoggetto idSoggettoFrutore, IDSoggetto idSoggettoErogatoreServizioCorrelato, 
			AccordoServizioParteComune aspc, String portType, String azione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IProtocolFactory<?> protocolFactory, IState state) throws Exception {
		return getReplyTo(true, idSoggettoFrutore, idSoggettoErogatoreServizioCorrelato, 
				aspc, portType, azione,
				registryReader, configIntegrationReader, 
				protocolFactory, state);
	}
	private static String getReplyTo(boolean fruizione, IDSoggetto idSoggettoFrutore, IDSoggetto idSoggettoErogatoreServizioCorrelato, 
			AccordoServizioParteComune aspc, String portType, String azione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IProtocolFactory<?> protocolFactory, IState state) throws Exception {
		
		String uriAPC = IDAccordoFactory.getInstance().getUriFromAccordo(aspc);
		boolean rest = ServiceBinding.REST.equals(aspc.getServiceBinding());
		
		IDAccordo idAccordoRisposta = null;
		String portTypeRisposta = null;
		
		if(rest) {
			FiltroRicercaRisorse filtroRisorse = new FiltroRicercaRisorse();
			filtroRisorse.setProtocolPropertiesRisorsa(new ProtocolProperties());
			filtroRisorse.getProtocolPropertiesRisorsa().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, 
					ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
			filtroRisorse.getProtocolPropertiesRisorsa().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, 
					uriAPC);
			filtroRisorse.getProtocolPropertiesRisorsa().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, 
					azione);
			List<IDResource> l = null;
			try {
				l = registryReader.findIdResourceAccordo(filtroRisorse);
			}catch(RegistryNotFound notFound) {
			}
			if(l==null || l.size()<=0) {
				throw new RegistryNotFound("Non è stata individuata alcun API contenente una risorsa correlata alla richiesta in corso");
			}
			if(l.size()>1) {
				throw new RegistryNotFound("Sono state individuate più di una risorsa contenente una correlazione verso la richiesta in corso: "+l);
			}
			IDResource idResource = l.get(0);
			idAccordoRisposta = idResource.getIdAccordo();
		}
		else {
			FiltroRicercaPortTypeAzioni filtroPTAzioni = new FiltroRicercaPortTypeAzioni();
			filtroPTAzioni.setProtocolPropertiesAzione(new ProtocolProperties());
			filtroPTAzioni.getProtocolPropertiesAzione().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, 
					ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
			filtroPTAzioni.getProtocolPropertiesAzione().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, 
					uriAPC);
			filtroPTAzioni.getProtocolPropertiesAzione().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA, 
					portType);
			filtroPTAzioni.getProtocolPropertiesAzione().addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, 
					azione);
			List<IDPortTypeAzione> l = null;
			try {
				l = registryReader.findIdAzionePortType(filtroPTAzioni);
			}catch(RegistryNotFound notFound) {
			}
			if(l==null || l.size()<=0) {
				throw new RegistryNotFound("Non è stata individuata alcun API contenente un'azione correlata alla richiesta in corso");
			}
			if(l.size()>1) {
				throw new RegistryNotFound("Sono state individuate più di un'azione contenente una correlazione verso la richiesta in corso: "+l);
			}
			IDPortTypeAzione idPTAzione = l.get(0);
			idAccordoRisposta = idPTAzione.getIdPortType().getIdAccordo();
			portTypeRisposta = idPTAzione.getIdPortType().getNome();
		}
			
		FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi(); 
		filtroServizi.setIdAccordoServizioParteComune(idAccordoRisposta);
		filtroServizi.setSoggettoErogatore(idSoggettoErogatoreServizioCorrelato);
		if(!rest) {
			filtroServizi.setPortType(portTypeRisposta);
		}
		List<IDServizio> lIdServizio = null;
		try {
			lIdServizio = registryReader.findIdAccordiServizioParteSpecifica(filtroServizi);
		}catch(RegistryNotFound notFound) {
		}
		if(lIdServizio==null || lIdServizio.size()<=0) {
			if(fruizione) {
				throw new RegistryNotFound("Non è stata individuata alcuna fruzione verso l'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta)+
						" erogata dal soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato));
			}
			else {
				throw new RegistryNotFound("Non è stata individuata alcun'erogazione da parte del soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato)+
					" relativamente all'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta));
			}
		}
		if(lIdServizio.size()>1) {
			if(fruizione) {
				throw new RegistryNotFound("Sono state individuate più fruzioni verso l'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta)+
						" erogata dal soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato));
			}
			else {
				throw new RegistryNotFound("Sono state individuate più di un'erogazione da parte del soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato)+
					" relativamente all'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta));
			}
		}
		IDServizio idServizioCorrelato = lIdServizio.get(0);
		
		if(fruizione) {
			List<MappingFruizionePortaDelegata> listMapping = ConfigurazionePdDManager.getInstance(state).getMappingFruizionePortaDelegataList(idSoggettoFrutore, idServizioCorrelato);
			if(listMapping==null) {
				throw new RegistryNotFound("(outbound empty) Non è stata individuata alcuna fruzione da parte del soggetto "+NamingUtils.getLabelSoggetto(idSoggettoFrutore)+
						" verso l'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta)+
						" erogata dal soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato));
			}
			String nomePD = null;
			IDSoggetto proprietarioPD = null;
			for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : listMapping) {
				if(mappingFruizionePortaDelegata.isDefault()) {
					nomePD = mappingFruizionePortaDelegata.getIdPortaDelegata().getNome();
					proprietarioPD = idSoggettoFrutore;
				}
			}
			if(nomePD==null) {
				throw new RegistryNotFound("(outbound) Non è stata individuata alcuna fruzione da parte del soggetto "+NamingUtils.getLabelSoggetto(idSoggettoFrutore)+
						" verso l'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta)+
						" erogata dal soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato));
			}
			return getUrlInvocazione(protocolFactory, state, rest, aspc, RuoloContesto.PORTA_DELEGATA, nomePD, proprietarioPD);
		}
		else {
			List<MappingErogazionePortaApplicativa> listMapping = ConfigurazionePdDManager.getInstance(state).getMappingErogazionePortaApplicativaList(idServizioCorrelato);
			if(listMapping==null) {
				throw new RegistryNotFound("(inbound empty) Non è stata individuata alcun'erogazione da parte del soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato)+
						" relativamente all'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta));
			}
			String nomePA = null;
			IDSoggetto proprietarioPA = null;
			for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : listMapping) {
				if(mappingErogazionePortaApplicativa.isDefault()) {
					nomePA = mappingErogazionePortaApplicativa.getIdPortaApplicativa().getNome();
					proprietarioPA = idServizioCorrelato.getSoggettoErogatore();
				}
			}
			if(nomePA==null) {
				throw new RegistryNotFound("(inbound) Non è stata individuata alcun'erogazione da parte del soggetto "+NamingUtils.getLabelSoggetto(idSoggettoErogatoreServizioCorrelato)+
						" relativamente all'API "+NamingUtils.getLabelAccordoServizioParteComune(idAccordoRisposta));
			}
			return getUrlInvocazione(protocolFactory, state, rest, aspc, RuoloContesto.PORTA_APPLICATIVA, nomePA, proprietarioPA);
		}
	}
	
	
	public static String getUrlInvocazione(IProtocolFactory<?> protocolFactory, IState state, boolean rest, AccordoServizioParteComune aspc, RuoloContesto ruoloContesto, String nomePorta, IDSoggetto proprietarioPorta) throws Exception {
		
		List<String> tags = new ArrayList<String>();
		if(aspc!=null && aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
			for (int i = 0; i < aspc.getGruppi().sizeGruppoList(); i++) {
				tags.add(aspc.getGruppi().getGruppo(i).getNome());
			}
		}
		
		IConfigIntegrationReader configReader = protocolFactory.getCachedConfigIntegrationReader(state);
		CanaliConfigurazione canaliConfigurazione = configReader.getCanaliConfigurazione();
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}
		String canalePorta = null;
		if(nomePorta!=null) {
			if(RuoloContesto.PORTA_APPLICATIVA.equals(ruoloContesto)) {
				try {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(nomePorta);
					PortaApplicativa pa = configReader.getPortaApplicativa(idPA);
					canalePorta = pa.getNome();
				}catch(Throwable t) {}
			}
			else {
				try {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(nomePorta);
					PortaDelegata pd = configReader.getPortaDelegata(idPD);
					canalePorta = pd.getNome();
				}catch(Throwable t) {}
			}
		}
		String canale = CanaliUtils.getCanale(canaliConfigurazione, canaleApi, canalePorta);
		
		UrlInvocazioneAPI urlInvocazioneApi = ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(protocolFactory, 
				ruoloContesto,
				rest ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP,
				nomePorta,
				proprietarioPorta,
				tags, canale);		 
		String prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
		String contesto = urlInvocazioneApi.getContext();
		return Utilities.buildUrl(prefixGatewayUrl, contesto);
	}
	
	public static String getSOAPHeaderReplyToValue(OpenSPCoop2SoapMessage msg) throws Exception {
		SOAPHeaderElement hdrElement = getSOAPHeaderReplyTo(msg);
		return hdrElement!=null ? hdrElement.getValue() : null;
	}
	public static SOAPHeaderElement getSOAPHeaderReplyTo(OpenSPCoop2SoapMessage msg) throws Exception {
		ModIProperties modIProperties = ModIProperties.getInstance();
		return getSOAPHeaderElement(msg, modIProperties.getSoapReplyToName(), 
				modIProperties.useSoapBodyReplyToNamespace() ? getSOAPChildBodyNamespace(msg) : modIProperties.getSoapReplyToNamespace(),
				modIProperties.getSoapReplyToActor());
	}
	public static String getSOAPHeaderCorrelationIdValue(OpenSPCoop2SoapMessage msg) throws Exception {
		SOAPHeaderElement hdrElement = getSOAPHeaderCorrelationId(msg);
		return hdrElement!=null ? hdrElement.getValue() : null;
	}
	public static SOAPHeaderElement getSOAPHeaderCorrelationId(OpenSPCoop2SoapMessage msg) throws Exception {
		ModIProperties modIProperties = ModIProperties.getInstance();
		return getSOAPHeaderElement(msg, modIProperties.getSoapCorrelationIdName(), 
				modIProperties.useSoapBodyCorrelationIdNamespace() ? getSOAPChildBodyNamespace(msg) : modIProperties.getSoapCorrelationIdNamespace(),
				modIProperties.getSoapCorrelationIdActor());
	}
	
	public static SOAPHeaderElement getSOAPHeaderRequestDigest(OpenSPCoop2SoapMessage msg) throws Exception {
		ModIProperties modIProperties = ModIProperties.getInstance();
		return getSOAPHeaderElement(msg, modIProperties.getSoapRequestDigestName(), 
				modIProperties.useSoapBodyRequestDigestNamespace() ? getSOAPChildBodyNamespace(msg) : modIProperties.getSoapRequestDigestNamespace(),
				modIProperties.getSoapRequestDigestActor());
	}
	
	public static SOAPHeaderElement getSOAPHeaderElement(OpenSPCoop2SoapMessage msg, String localName, String namespace, String actor) throws Exception {
		
		SOAPHeader header = msg.getSOAPHeader();
		if(header==null){
			return null;
		}
		java.util.Iterator<?> it = header.examineAllHeaderElements();
		while( it.hasNext()  ){
			// Test Header Element
			SOAPHeaderElement headerElementCheck = (SOAPHeaderElement) it.next();
			
			if(!namespace.equals(headerElementCheck.getNamespaceURI())) {
				continue;
			}
			if(!localName.equals(headerElementCheck.getLocalName())) {
				continue;
			}
			
			//Controllo Actor
			String actorCheck = SoapUtils.getSoapActor(headerElementCheck, msg.getMessageType());
			if(actor==null) {
				if(actorCheck==null) {
					return headerElementCheck;
				}
			}
			else {
				if( actor.equals(actorCheck) ){
					return headerElementCheck;
				}
			}
		}
		
		return null;
	}
	
	public static void addSOAPHeaderReplyTo(OpenSPCoop2SoapMessage msg, String value) throws Exception {
		ModIProperties modIProperties = ModIProperties.getInstance();
		addSOAPHeaderElement(msg, modIProperties.getSoapReplyToName(), 
				modIProperties.useSoapBodyReplyToNamespace() ? getSOAPChildBodyPrefix(msg) : modIProperties.getSoapReplyToPrefix(),
				modIProperties.useSoapBodyReplyToNamespace() ? getSOAPChildBodyNamespace(msg) : modIProperties.getSoapReplyToNamespace(),
				modIProperties.getSoapReplyToActor(),
				modIProperties.isSoapReplyToMustUnderstand(),
				value);
	}
	public static void addSOAPHeaderCorrelationId(OpenSPCoop2SoapMessage msg, String value) throws Exception {
		ModIProperties modIProperties = ModIProperties.getInstance();
		addSOAPHeaderElement(msg, modIProperties.getSoapCorrelationIdName(), 
				modIProperties.useSoapBodyCorrelationIdNamespace() ? getSOAPChildBodyPrefix(msg) : modIProperties.getSoapCorrelationIdPrefix(),
				modIProperties.useSoapBodyCorrelationIdNamespace() ? getSOAPChildBodyNamespace(msg) : modIProperties.getSoapCorrelationIdNamespace(),
				modIProperties.getSoapCorrelationIdActor(),
				modIProperties.isSoapCorrelationIdMustUnderstand(),
				value);
	}
	
	public static SOAPHeaderElement addSOAPHeaderRequestDigest(OpenSPCoop2SoapMessage msg, NodeList value) throws Exception {
		ModIProperties modIProperties = ModIProperties.getInstance();
		return addSOAPHeaderElement(msg, modIProperties.getSoapRequestDigestName(), 
				modIProperties.useSoapBodyRequestDigestNamespace() ? getSOAPChildBodyPrefix(msg) : modIProperties.getSoapRequestDigestPrefix(),
				modIProperties.useSoapBodyRequestDigestNamespace() ? getSOAPChildBodyNamespace(msg) : modIProperties.getSoapRequestDigestNamespace(),
				modIProperties.getSoapRequestDigestActor(),
				modIProperties.isSoapRequestDigestMustUnderstand(),
				value);
	}
	
	public static SOAPHeaderElement addSOAPHeaderElement(OpenSPCoop2SoapMessage msg, String localName, String prefix, String namespace, String actor, boolean mustUnderstand, Object value) throws Exception {
		
		// se gia esiste aggiorno il valore
		SOAPHeaderElement hdrElement = getSOAPHeaderElement(msg, localName, namespace, actor);
		
		if(hdrElement==null) {
			SOAPHeader header = msg.getSOAPHeader();
			if(header==null){
				header = msg.getSOAPPart().getEnvelope().addHeader();
			}
			hdrElement = msg.newSOAPHeaderElement(header, new QName(namespace,localName,prefix));

			if(actor!=null && !"".equals(actor)) {
				hdrElement.setActor(actor);
			}
			hdrElement.setMustUnderstand(mustUnderstand);
				
			msg.addHeaderElement(header, hdrElement);
		}
		if(value instanceof String) {
			hdrElement.setValue( (String) value);
		}
		else {
			NodeList nodeList = (NodeList) value;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node n = nodeList.item(i);
				byte[] ref = msg.getAsByte(n, false);
				Element element = XMLUtils.getInstance(msg.getFactory()).newElement(ref);
				hdrElement.addChildElement(SoapUtils.getSoapFactory(msg.getFactory(), msg.getMessageType()).createElement(element));
			}
		}
		
		return hdrElement;
	}
	
	private static String getSOAPChildBodyNamespace(OpenSPCoop2SoapMessage msg) throws Exception {
		
		if(msg.getSOAPBody()==null) {
			throw new Exception("Messaggio senza Body");
		}
		SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody());
		if(child==null) {
			throw new Exception("Messaggio senza un contenuto nel Body");
		}
		return child.getNamespaceURI();
	}
	
	private static String getSOAPChildBodyPrefix(OpenSPCoop2SoapMessage msg) throws Exception {
		
		if(msg.getSOAPBody()==null) {
			throw new Exception("Messaggio senza Body");
		}
		SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(msg.getSOAPBody());
		if(child==null) {
			throw new Exception("Messaggio senza un contenuto nel Body");
		}
		return child.getPrefix();
	}
	
	public static String getDynamicValue(String tipoRisorsa, String template, Map<String, Object> dynamicMap, Context context) throws Exception {
		String valoreRisorsa = null;
		try {
			valoreRisorsa = DynamicUtils.convertDynamicPropertyValue(tipoRisorsa, template, dynamicMap, context, true);
		}catch(Exception e) {
			throw new Exception("Conversione valore della risorsa ("+tipoRisorsa+") '"+template+"' non riuscita: "+e.getMessage(),e);
		}
		return valoreRisorsa;
	}
	public static String getDynamicValue(String tipoRisorsa, List<String> templates, Map<String, Object> dynamicMap, Context context) throws Exception {
		List<Throwable> exceptions = new ArrayList<Throwable>();
		for (String template : templates) {
			try {
				String v = getDynamicValue(tipoRisorsa, template, dynamicMap, context);
				if(v!=null && !"".equals(v)) {
					return v;
				}
			}catch(Throwable t) {
				exceptions.add(t);
			}
		}
		throw new UtilsMultiException("Non è stato possibile recuperare un valore da associare alla risorsa '"+tipoRisorsa+"'", exceptions.toArray(new Throwable[1]));
	}
}
