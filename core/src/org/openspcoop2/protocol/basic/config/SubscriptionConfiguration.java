/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mapping.SubscriptionUtils;
import org.openspcoop2.protocol.manifest.IntegrationConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode;
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ProtocolSubscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;

/**
 * SubscriptionConfiguration
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SubscriptionConfiguration extends AbstractIntegrationConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected SubscriptionConfiguration(IntegrationConfiguration integrationConfiguration) {
		super(integrationConfiguration);
	}

	public static boolean isDescriptionDefault(String descrizione) {
		return descrizione!=null &&
				(descrizione.startsWith(SubscriptionConfiguration.SUBSCRIPTION_PREFIX) ||
				descrizione.startsWith(SubscriptionConfiguration.SUBSCRIPTION_PREFIX_2)||
				descrizione.startsWith(SubscriptionConfiguration.INTERNAL_SUBSCRIPTION_PREFIX));
	}
	
	private static final String SUBSCRIPTION_PREFIX = "Subscription from ";
	private static final String SUBSCRIPTION_PREFIX_2 = "Subscription for service ";
	public ProtocolSubscription createDefaultSubscription(IDSoggetto idFruitore, IDServizio idServizio) throws ProtocolException {
		
		ProtocolSubscription subscription = new ProtocolSubscription();
		
		PortaDelegata portaDelegata = new PortaDelegata();
		portaDelegata.setNome(this.getNome(idServizio, idFruitore, null, null, 
				this.integrationConfiguration.getName().getParamList()));
		portaDelegata.setDescrizione(SUBSCRIPTION_PREFIX+idFruitore.toString()+" for service "+idServizio.toString());
		if(portaDelegata.getDescrizione().length()>255) {
			portaDelegata.setDescrizione(SUBSCRIPTION_PREFIX_2+idServizio.toString());
		}
		if(portaDelegata.getDescrizione().length()>255) {
			portaDelegata.setDescrizione(null);
		}
		
		IDPortaDelegata idPortaDelegata = SubscriptionUtils.setCommonParameter(portaDelegata, idFruitore, idServizio, true, false);

		PortaDelegataAzione pdAzione = new PortaDelegataAzione();
		ResourceIdentificationType defaultIdentification = this.integrationConfiguration.getResourceIdentification().getIdentificationModes().getDefault();
		if(defaultIdentification==null) {
			defaultIdentification = this.integrationConfiguration.getResourceIdentification().getIdentificationModes().getMode(0).getName();
		}
		boolean setPattern = false;
		switch (defaultIdentification) {
		case CONTENT:
			pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.CONTENT_BASED);		
			setPattern = true;
			break;
		case HEADER:
			pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.HEADER_BASED);		
			setPattern = true;
			break;
		case URL:
			pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.URL_BASED);		
			setPattern = true;
			break;
		case INPUT:
			pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.INPUT_BASED);		
			break;
		case INTERFACE:
			pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.INTERFACE_BASED);		
			break;
		case SOAP_ACTION:
			pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.SOAP_ACTION_BASED);		
			break;
		case PROTOCOL:
			throw new ProtocolException("IdentificationMode '"+defaultIdentification+"' unsupported");
		}
		if(setPattern) {
			pdAzione.setPattern(this.getNome(idServizio, idFruitore, portaDelegata.getNome(), null, 
					this.integrationConfiguration.getResourceIdentification().getIdentificationParameter().getParamList()));
		}		
		if(this.integrationConfiguration.getResourceIdentification().getIdentificationModes().isForceInterfaceMode()) {
			pdAzione.setForceInterfaceBased(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
		}
		else {
			pdAzione.setForceInterfaceBased(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO);
		}
		portaDelegata.setAzione(pdAzione);

		portaDelegata.setRicercaPortaAzioneDelegata(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
		
		subscription.setPortaDelegata(portaDelegata);		

		MappingFruizionePortaDelegata mappingFruizione = SubscriptionUtils.createMappingDefault(idFruitore, idServizio, idPortaDelegata);
		
		subscription.setMapping(mappingFruizione);
		
		return subscription;
		
		
	}
	
	public List<PortaDelegataAzioneIdentificazione> supportedIdentificationModes(ConsoleInterfaceType consoleType) throws ProtocolException{
		List<PortaDelegataAzioneIdentificazione> list = new ArrayList<>();
		for (IntegrationConfigurationResourceIdentificationMode mode : 
			this.integrationConfiguration.getResourceIdentification().getIdentificationModes().getModeList()) {
			
			if(mode.isOnlyAdvancedMode() &&
				ConsoleInterfaceType.STANDARD.equals(consoleType)) {
				continue;
			}
			
			ResourceIdentificationType type = mode.getName();
			switch (type) {
			case CONTENT:
				list.add(PortaDelegataAzioneIdentificazione.CONTENT_BASED);		
				break;
			case HEADER:
				list.add(PortaDelegataAzioneIdentificazione.HEADER_BASED);		
				break;
			case URL:
				list.add(PortaDelegataAzioneIdentificazione.URL_BASED);		
				break;
			case INPUT:
				list.add(PortaDelegataAzioneIdentificazione.INPUT_BASED);		
				break;
			case INTERFACE:
				list.add(PortaDelegataAzioneIdentificazione.INTERFACE_BASED);		
				break;
			case SOAP_ACTION:
				list.add(PortaDelegataAzioneIdentificazione.SOAP_ACTION_BASED);		
				break;
			case PROTOCOL:
				throw new ProtocolException("IdentificationMode '"+type+"' unsupported");
			}
		}
		return list;
	}
	
	public ProtocolSubscription createSubscription(IConfigIntegrationReader configIntegrationReader, IDSoggetto idFruitore, IDServizio idServizio,
			PortaDelegata portaDelegataDefault, 
			String ruleName, String description, String ... azione ) throws ProtocolException {
		return createSubscription(configIntegrationReader, idFruitore, idServizio, 
				portaDelegataDefault, null, 
				ruleName, description, azione);
	}
	private static final String INTERNAL_SUBSCRIPTION_PREFIX = "Internal Subscription '"; 
	public ProtocolSubscription createSubscription(IConfigIntegrationReader configIntegrationReader, IDSoggetto idFruitore, IDServizio idServizio,
			PortaDelegata portaDelegataDefault, PortaDelegata portaDelegataDaClonare,
			String ruleName, String description, String ... azione ) throws ProtocolException {
		
		if(azione==null || azione.length<=0) {
			throw new ProtocolException("Actions undefined");
		}
		
		String nomePortaDelegante = portaDelegataDefault.getNome();
		String nomeNuovaPortaDelegata = this.getNome(idServizio, idFruitore, nomePortaDelegante, ruleName, 
				this.integrationConfiguration.getResourceIdentification().getSpecificResource().getName().getParamList());
		String descrizioneNuovaPortaDelegata = INTERNAL_SUBSCRIPTION_PREFIX+ruleName+"' for "+nomePortaDelegante;	
		if(descrizioneNuovaPortaDelegata.length()>255) {
			descrizioneNuovaPortaDelegata = INTERNAL_SUBSCRIPTION_PREFIX+ruleName+"'";
		}
		if(descrizioneNuovaPortaDelegata.length()>255) {
			descrizioneNuovaPortaDelegata = null;
		}
		
		ProtocolSubscription subscription = new ProtocolSubscription();
		
		PortaDelegata portaDelegata = null;
		
		// creo una nuova porta applicativa clonando quella selezionata
		boolean setDatiServizio = false;
		boolean portaClonata = false;
		if(portaDelegataDaClonare!=null) {
			
			portaDelegata = (PortaDelegata) portaDelegataDaClonare.clone();
			portaDelegata.setId(null);// annullo il table id
			portaDelegata.setGestioneCors(null); // annulla la gestione Cors poiche' gestito solo nella porta di default
			portaDelegata.setCanale(null); // annullo il canale poiche' gestito solo nella porta di default
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(portaDelegataDaClonare.getNome());
						
			// riporto Rate Limiting
			List<AttivazionePolicy> listAP = null;
			try {
				listAP = configIntegrationReader.getRateLimitingPolicy(idPD);
			}catch(Exception e) {
				// ignore
			}
			List<String> idPolicyCreate = new ArrayList<>();
			if(listAP!=null && !listAP.isEmpty()) {
				for (AttivazionePolicy attivazionePolicy : listAP) {
					
					AttivazionePolicy apCloned = (AttivazionePolicy) attivazionePolicy.clone();
					if(apCloned.getIdPolicy()!=null && apCloned.getFiltro()!=null && portaDelegataDaClonare.getNome().equals(apCloned.getFiltro().getNomePorta())){
						try {
							apCloned.getFiltro().setNomePorta(nomeNuovaPortaDelegata);
							String serialId = configIntegrationReader.getNextPolicyInstanceSerialId(apCloned.getIdPolicy());
							String idActive = ControlloTrafficoDriverUtils.buildIdActivePolicy(apCloned.getIdPolicy(), serialId);
							int limit = 0;
							while(idPolicyCreate.contains(idActive) && limit<1000) { // provo 1000 volte
								limit++;
								serialId = ControlloTrafficoDriverUtils.incrementPolicyInstanceSerialId(serialId);
								idActive = ControlloTrafficoDriverUtils.buildIdActivePolicy(apCloned.getIdPolicy(), serialId);
							}
							idPolicyCreate.add(idActive);
							apCloned.setIdActivePolicy(idActive);
							
							if(subscription.getRateLimitingPolicies()==null) {
								subscription.setRateLimitingPolicies(new ArrayList<>());
							}
							subscription.getRateLimitingPolicies().add(apCloned);
							
						}catch(Exception e) {
							// ignore
						}
					}
				}
			}
			
			// riporto Allarmi
			List<Allarme> listAllarmi = null;
			try {
				listAllarmi = configIntegrationReader.getAllarmi(idPD);
			}catch(Exception e) {
				// ignore
			}
			List<String> idAllarmiCreate = new ArrayList<>();
			if(listAllarmi!=null && !listAllarmi.isEmpty()) {
				for (Allarme allarme : listAllarmi) {
					
					Allarme allarmeCloned = (Allarme) allarme.clone();
					if(allarmeCloned.getTipo()!=null && allarmeCloned.getFiltro()!=null && portaDelegataDaClonare.getNome().equals(allarmeCloned.getFiltro().getNomePorta())){
						try {
							allarmeCloned.getFiltro().setNomePorta(nomeNuovaPortaDelegata);
							String serialId = configIntegrationReader.getNextAlarmInstanceSerialId(allarmeCloned.getTipo());
							String uniqueName = AllarmiDriverUtils.buildIdAlarm(allarmeCloned.getTipo(), serialId);
							int limit = 0;
							while(idAllarmiCreate.contains(uniqueName) && limit<1000) { // provo 1000 volte
								limit++;
								serialId = AllarmiDriverUtils.incrementAlarmInstanceSerialId(serialId);
								uniqueName = AllarmiDriverUtils.buildIdAlarm(allarmeCloned.getTipo(), serialId);
							}
							idAllarmiCreate.add(uniqueName);
							allarmeCloned.setNome(uniqueName);
							
							if(subscription.getAllarmi()==null) {
								subscription.setAllarmi(new ArrayList<>());
							}
							subscription.getAllarmi().add(allarmeCloned);
							
						}catch(Exception e) {
							// ignore
						}
					}
				}
			}
			
			portaClonata = true;
			
		} else {
			
			portaDelegata = new PortaDelegata();
			setDatiServizio = true;		
			
		}

		portaDelegata.setNome(nomeNuovaPortaDelegata);
		portaDelegata.setDescrizione(descrizioneNuovaPortaDelegata);
		
		IDPortaDelegata idPortaDelegata = SubscriptionUtils.setCommonParameter(portaDelegata, idFruitore, idServizio, setDatiServizio, portaClonata);
		
		SubscriptionUtils.setAzioneDelegate(portaDelegata, nomePortaDelegante, azione);
						
		subscription.setPortaDelegata(portaDelegata);		

		MappingFruizionePortaDelegata mappingFruizione = SubscriptionUtils.createMapping(idFruitore, idServizio, idPortaDelegata, ruleName, description);
		
		subscription.setMapping(mappingFruizione);
		
		return subscription;
		
	}
}





