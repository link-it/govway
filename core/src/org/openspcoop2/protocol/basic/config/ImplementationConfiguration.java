/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.basic.config;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.ImplementationUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.protocol.manifest.IntegrationConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode;
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.Implementation;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;

/**
 * ImplementationConfiguration
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ImplementationConfiguration extends AbstractIntegrationConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ImplementationConfiguration(IntegrationConfiguration integrationConfiguration) {
		super(integrationConfiguration);
	}

	public Implementation createDefaultImplementation(IDServizio idServizio) throws ProtocolException {
		
		Implementation implementation = new Implementation();
		
		PortaApplicativa portaApplicativa = new PortaApplicativa();
		portaApplicativa.setNome(this.getNome(idServizio, null, null, null, 
				this.integrationConfiguration.getName().getParamList()));
		portaApplicativa.setDescrizione("Service implementation "+idServizio.toString());
		if(portaApplicativa.getDescrizione().length()>255) {
			portaApplicativa.setDescrizione("Service implementation "+idServizio.toString());
		}
		if(portaApplicativa.getDescrizione().length()>255) {
			portaApplicativa.setDescrizione(null);
		}
		
		IDPortaApplicativa idPortaApplicativa = ImplementationUtils.setCommonParameter(portaApplicativa, idServizio, true, false);
		
		PortaApplicativaAzione pdAzione = new PortaApplicativaAzione();
		ResourceIdentificationType defaultIdentification = this.integrationConfiguration.getResourceIdentification().getIdentificationModes().getDefault();
		if(defaultIdentification==null) {
			defaultIdentification = this.integrationConfiguration.getResourceIdentification().getIdentificationModes().getMode(0).getName();
		}
		boolean setPattern = false;
		switch (defaultIdentification) {
		case CONTENT:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.CONTENT_BASED);		
			setPattern = true;
			break;
		case HEADER:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.HEADER_BASED);		
			setPattern = true;
			break;
		case URL:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.URL_BASED);		
			setPattern = true;
			break;
		case INPUT:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.INPUT_BASED);		
			break;
		case INTERFACE:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.INTERFACE_BASED);		
			break;
		case SOAP_ACTION:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.SOAP_ACTION_BASED);		
			break;
		case PROTOCOL:
			pdAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED);
			break;
		}
		if(setPattern) {
			pdAzione.setPattern(this.getNome(idServizio, null, portaApplicativa.getNome(), null, 
					this.integrationConfiguration.getResourceIdentification().getIdentificationParameter().getParamList()));
		}		
		if(this.integrationConfiguration.getResourceIdentification().getIdentificationModes().isForceInterfaceMode()) {
			pdAzione.setForceInterfaceBased(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
		}
		else {
			pdAzione.setForceInterfaceBased(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO);
		}
		portaApplicativa.setAzione(pdAzione);

		portaApplicativa.setRicercaPortaAzioneDelegata(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
		
		implementation.setPortaApplicativa(portaApplicativa);		

		MappingErogazionePortaApplicativa mappingErogazione = ImplementationUtils.createMappingDefault(idServizio, idPortaApplicativa); 
				
		implementation.setMapping(mappingErogazione);
		
		return implementation;
		
		
	}
	
	public List<PortaApplicativaAzioneIdentificazione> supportedIdentificationModes(ConsoleInterfaceType consoleType) throws ProtocolException{
		List<PortaApplicativaAzioneIdentificazione> list = new ArrayList<PortaApplicativaAzioneIdentificazione>();
		for (IntegrationConfigurationResourceIdentificationMode mode : 
			this.integrationConfiguration.getResourceIdentification().getIdentificationModes().getModeList()) {
			
			if(mode.isOnlyAdvancedMode()) {
				if(ConsoleInterfaceType.STANDARD.equals(consoleType)) {
					continue;
				}
			}
			
			ResourceIdentificationType type = mode.getName();
			switch (type) {
			case CONTENT:
				list.add(PortaApplicativaAzioneIdentificazione.CONTENT_BASED);		
				break;
			case HEADER:
				list.add(PortaApplicativaAzioneIdentificazione.HEADER_BASED);		
				break;
			case URL:
				list.add(PortaApplicativaAzioneIdentificazione.URL_BASED);		
				break;
			case INPUT:
				list.add(PortaApplicativaAzioneIdentificazione.INPUT_BASED);		
				break;
			case INTERFACE:
				list.add(PortaApplicativaAzioneIdentificazione.INTERFACE_BASED);		
				break;
			case SOAP_ACTION:
				list.add(PortaApplicativaAzioneIdentificazione.SOAP_ACTION_BASED);		
				break;
			case PROTOCOL:
				list.add(PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED);	
//				throw new ProtocolException("IdentificationMode '"+type+"' unsupported");
			}
		}
		return list;
	}
	
	public Implementation createImplementation(IConfigIntegrationReader configIntegrationReader, IDServizio idServizio,
			PortaApplicativa portaApplicativaDefault, 
			String ruleName, String description, String ... azione ) throws ProtocolException {
		return createImplementation(configIntegrationReader, idServizio, 
				portaApplicativaDefault, null, 
				ruleName, description, azione);
	}
	public Implementation createImplementation(IConfigIntegrationReader configIntegrationReader, IDServizio idServizio,
			PortaApplicativa portaApplicativaDefault, PortaApplicativa portaApplicativaDaClonare,
			String ruleName, String description, String ... azione ) throws ProtocolException {
		
		if(azione==null || azione.length<=0) {
			throw new ProtocolException("Actions undefined");
		}
		
		String nomePortaDelegante = portaApplicativaDefault.getNome();
		String nomeNuovaPortaApplicativa = this.getNome(idServizio, null, nomePortaDelegante, ruleName, 
				this.integrationConfiguration.getResourceIdentification().getSpecificResource().getName().getParamList());
		String descrizioneNuovaPortaApplicativa = "Internal Implementation '"+ruleName+"' for "+nomePortaDelegante;	
		if(descrizioneNuovaPortaApplicativa.length()>255) {
			descrizioneNuovaPortaApplicativa = "Internal Implementation '"+ruleName+"'";
		}
		if(descrizioneNuovaPortaApplicativa.length()>255) {
			descrizioneNuovaPortaApplicativa = null;
		}
		
		Implementation implementation = new Implementation();
		
		PortaApplicativa portaApplicativa = null;
		
		// creo una nuova porta applicativa clonando quella selezionata 
		boolean setDatiServizio = false;
		boolean portaClonata = false;
		if(portaApplicativaDaClonare!=null) {
			
			portaApplicativa = (PortaApplicativa) portaApplicativaDaClonare.clone();
			portaApplicativa.setId(null);// annullo il table id
			portaApplicativa.setGestioneCors(null); // annulla la gestione Cors poiche' gestito solo nella porta di default
			portaApplicativa.setCanale(null); // annullo il canale poiche' gestito solo nella porta di default
			
			// riporto Rate Limiting
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(portaApplicativaDaClonare.getNome());
			List<AttivazionePolicy> listAP = null;
			try {
				listAP = configIntegrationReader.getRateLimitingPolicy(idPA);
			}catch(Exception e) {}
			List<String> idPolicyCreate = new ArrayList<String>();
			if(listAP!=null && !listAP.isEmpty()) {
				for (AttivazionePolicy attivazionePolicy : listAP) {
					
					AttivazionePolicy apCloned = (AttivazionePolicy) attivazionePolicy.clone();
					if(apCloned.getIdPolicy()!=null && apCloned.getFiltro()!=null && portaApplicativaDaClonare.getNome().equals(apCloned.getFiltro().getNomePorta())){
						try {
							apCloned.getFiltro().setNomePorta(nomeNuovaPortaApplicativa);
							int counter = configIntegrationReader.getFreeCounterForGlobalPolicy(apCloned.getIdPolicy());
							String idActive = apCloned.getIdPolicy()+":"+counter;
							int limit = 0;
							while(idPolicyCreate.contains(idActive) && limit<1000) { // provo 1000 volte
								limit++;
								counter++;
								idActive = apCloned.getIdPolicy()+":"+counter;
							}
							idPolicyCreate.add(idActive);
							apCloned.setIdActivePolicy(idActive);
							
							if(implementation.getRateLimitingPolicies()==null) {
								implementation.setRateLimitingPolicies(new ArrayList<AttivazionePolicy>());
							}
							implementation.getRateLimitingPolicies().add(apCloned);
							
						}catch(Exception e) {}
					}
				}
			}
			
			portaClonata = true;
		
		} else {
			
			portaApplicativa = new PortaApplicativa();
			setDatiServizio = true;		
			
		}

		portaApplicativa.setNome(nomeNuovaPortaApplicativa);
		portaApplicativa.setDescrizione(descrizioneNuovaPortaApplicativa);
		
		IDPortaApplicativa idPortaApplicativa = ImplementationUtils.setCommonParameter(portaApplicativa, idServizio, setDatiServizio, portaClonata);
		
		ImplementationUtils.setAzioneDelegate(portaApplicativa, nomePortaDelegante, azione);
				
		implementation.setPortaApplicativa(portaApplicativa);		

		MappingErogazionePortaApplicativa mappingErogazione = ImplementationUtils.createMapping(idServizio, idPortaApplicativa, ruleName, description);
		
		implementation.setMapping(mappingErogazione);
		
		return implementation;
		
	}
}





