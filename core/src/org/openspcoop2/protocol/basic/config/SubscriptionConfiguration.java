/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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



package org.openspcoop2.protocol.basic.config;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mapping.SubscriptionUtils;
import org.openspcoop2.protocol.manifest.IntegrationConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode;
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;

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

	public Subscription createDefaultSubscription(IDSoggetto idFruitore, IDServizio idServizio) throws ProtocolException {
		
		Subscription subscription = new Subscription();
		
		PortaDelegata portaDelegata = new PortaDelegata();
		portaDelegata.setNome(this.getNome(idServizio, idFruitore, null, null, 
				this.integrationConfiguration.getName().getParamList()));
		portaDelegata.setDescrizione("Subscription from "+idFruitore.toString()+" for service "+idServizio.toString());
		if(portaDelegata.getDescrizione().length()>255) {
			portaDelegata.setDescrizione("Subscription for service "+idServizio.toString());
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
		List<PortaDelegataAzioneIdentificazione> list = new ArrayList<PortaDelegataAzioneIdentificazione>();
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
	
	public Subscription createSubscription(IDSoggetto idFruitore, IDServizio idServizio,
			PortaDelegata portaDelegataDefault, 
			String ruleName, String ... azione ) throws ProtocolException {
		return createSubscription(idFruitore, idServizio, 
				portaDelegataDefault, null, 
				ruleName, azione);
	}
	public Subscription createSubscription(IDSoggetto idFruitore, IDServizio idServizio,
			PortaDelegata portaDelegataDefault, PortaDelegata portaDelegataDaClonare,
			String ruleName, String ... azione ) throws ProtocolException {
		
		if(azione==null || azione.length<=0) {
			throw new ProtocolException("Actions undefined");
		}
		
		String nomePortaDelegante = portaDelegataDefault.getNome();
		String nomeNuovaPortaDelegata = this.getNome(idServizio, idFruitore, nomePortaDelegante, ruleName, 
				this.integrationConfiguration.getResourceIdentification().getSpecificResource().getName().getParamList());
		String descrizioneNuovaPortaDelegata = "Internal Subscription '"+ruleName+"' for "+nomePortaDelegante;	
		if(descrizioneNuovaPortaDelegata.length()>255) {
			descrizioneNuovaPortaDelegata = "Internal Subscription '"+ruleName+"'";
		}
		if(descrizioneNuovaPortaDelegata.length()>255) {
			descrizioneNuovaPortaDelegata = null;
		}
		
		Subscription subscription = new Subscription();
		
		PortaDelegata portaDelegata = null;
		
		// creo una nuova porta applicativa clonando quella selezionata
		boolean setDatiServizio = false;
		boolean portaClonata = false;
		if(portaDelegataDaClonare!=null) {
			
			portaDelegata = (PortaDelegata) portaDelegataDaClonare.clone();
			portaDelegata.setId(null);// annullo il table id
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

		MappingFruizionePortaDelegata mappingFruizione = SubscriptionUtils.createMapping(idFruitore, idServizio, idPortaDelegata, ruleName);
		
		subscription.setMapping(mappingFruizione);
		
		return subscription;
		
	}
}





