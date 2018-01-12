/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.as4.properties;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
import org.openspcoop2.protocol.as4.pmode.PModeRegistryReader;
import org.openspcoop2.protocol.as4.pmode.beans.Policy;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;

/**
 * TrasparenteTestsuiteDynamicConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13445 $, $Date: 2017-11-21 10:10:30 +0100 (Tue, 21 Nov 2017) $
 */
public class AS4DynamicConfiguration extends BasicDynamicConfiguration implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {

	public AS4DynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	/*** SOGGETTO ***/

	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDSoggetto id)
					throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_SOGGETTI_ID, 
				AS4ConsoleCostanti.AS4_SOGGETTI_LABEL);
		configuration.addConsoleItem(titolo );
		
		AbstractConsoleItem<?> userMessagePartyIdItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_ID, 
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_LABEL);
		userMessagePartyIdItem.setRequired(true);
		configuration.addConsoleItem(userMessagePartyIdItem);
		
		StringConsoleItem userMessagePartyTypeNameItem =  (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_ID, 
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_LABEL);
		userMessagePartyTypeNameItem.setDefaultValue(AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_NAME_DEFAULT_VALUE);
		userMessagePartyTypeNameItem.setRequired(true);
		configuration.addConsoleItem(userMessagePartyTypeNameItem);
		
		StringConsoleItem userMessagePartyTypeValueItem = (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_ID, 
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_LABEL);
		userMessagePartyTypeValueItem.setDefaultValue(AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_DEFAULT_VALUE);
		userMessagePartyTypeValueItem.setRequired(true);
		configuration.addConsoleItem(userMessagePartyTypeValueItem);
		
		
		return configuration;
		
	}

	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
			IDSoggetto id) throws ProtocolException {
		
		// La proprietà AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_ID deve essere un valore univoco
		StringProperty userMessagePartyIdItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_ID);
		List<IDSoggetto> idSoggettiList = null;
		FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
		filtroSoggetti.setProtocolProperties(new ProtocolProperties());
		filtroSoggetti.getProtocolProperties().addProperty(userMessagePartyIdItem);
		try{
			idSoggettiList = registryReader.findIdSoggetti(filtroSoggetti);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca dei soggetti: "+e.getMessage(),e);
		}
		if(idSoggettiList!=null && idSoggettiList.size()>0) {
			StringBuffer bfExc = new StringBuffer();
			for (IDSoggetto idSoggetto : idSoggettiList) {
				if(id.equals(idSoggetto)==false) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append(idSoggetto);
				}
			}
			if(bfExc.length()>0) {
				throw new ProtocolException("Party Id '"+userMessagePartyIdItem.getValue()+"', indicato nel parametro '"+
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ID_LABEL+"', già utilizzato per il soggetto: "+bfExc.toString()); // dovrebbe essere uno solo
			}
		}
		
		// La proprietà AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_ID deve essere una uri
		StringProperty userMessagePartyTypeValueItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_ID);
		try {
			URI uri = new URI(userMessagePartyTypeValueItem.getValue());
			uri.toString();
		}catch(Exception e) {
			throw new ProtocolException("Deve essere indicata una URI valida per il parametro '"+AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_LABEL+"'");
		}
		
		
	}

	/*** ACCORDO SERVIZIO PARTE COMUNE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDAccordo id)
					throws ProtocolException {

		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_ID, 
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_LABEL);
		configuration.addConsoleItem(titolo );
		
		AbstractConsoleItem<?> serviceTypeItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID, 
						AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID_LABEL);
		serviceTypeItem.setRequired(false); // vedi validazione sottostante
		configuration.addConsoleItem(serviceTypeItem);
		
		AbstractConsoleItem<?> serviceNameItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID, 
						AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID_LABEL);
		serviceNameItem.setRequired(true);
		configuration.addConsoleItem(serviceNameItem);
		
		AbstractConsoleItem<?> payloadProfileItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.FILE,
						AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID, 
						AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL);
		payloadProfileItem.setRequired(false);
		configuration.addConsoleItem(payloadProfileItem);
		
		return configuration;
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
		
		// Se il tipo non è presente allora è obbligatorio che il nome del servizio sia una URI.
		StringProperty serviceTypeItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID);
		StringProperty serviceNameItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID);
		boolean tipoDefinito = false;
		if(serviceTypeItem.getValue()==null || "".equals(serviceTypeItem.getValue())) {		
			try {
				URI uri = new URI(serviceNameItem.getValue());
				uri.toString();
			}catch(Exception e) {
				throw new ProtocolException("Se non viene definito il parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID_LABEL+"', il valore indicato nel parametro '"+
						AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID_LABEL+"' deve essere una URI valida.");
			}	
		}
		else {
			tipoDefinito = true;
		}
			
		// Tipo e Nome del servizio devono essere univoci
		List<IDAccordo> idAccordiList = null;
		FiltroRicercaAccordi filtroAccordi = new FiltroRicercaAccordi();
		filtroAccordi.setProtocolProperties(new ProtocolProperties());
		filtroAccordi.getProtocolProperties().addProperty(serviceTypeItem);
		filtroAccordi.getProtocolProperties().addProperty(serviceNameItem);
		try{
			idAccordiList = registryReader.findIdAccordiServizioParteComune(filtroAccordi);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di accordi di servizio: "+e.getMessage(),e);
		}
		if(idAccordiList!=null && idAccordiList.size()>0) {
			StringBuffer bfExc = new StringBuffer();
			for (IDAccordo idAccordo : idAccordiList) {
				if(id.equals(idAccordo)==false) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append(idAccordo);
				}
			}
			if(bfExc.length()>0) {
				String tipo = "";
				if(tipoDefinito) {
					tipo = "("+AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE_ID_LABEL+":"+serviceTypeItem.getValue()+") ";
				}
				throw new ProtocolException(tipo+"'"+serviceNameItem.getValue()+"', indicato nel parametro '"+
						AS4ConsoleCostanti.AS4_ACCORDO_USER_MESSAGE_COLLABORATION_INFO_SERVICE_NAME_ID_LABEL+"', già utilizzato per l'accordo: "+bfExc.toString()); // dovrebbe essere uno solo
			}
		}
		
		// Payload Profile
		BinaryProperty payloadProfileItem = (BinaryProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID);
		if(payloadProfileItem!=null && payloadProfileItem.getValue()!=null) {
			try {
				eu.domibus.configuration.utils.PayloadProfilesXSDValidator.getXSDValidator(this.log).valida(new ByteArrayInputStream(payloadProfileItem.getValue()));
			}catch(Exception e) {
				throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL+"', presenta una struttura non valida: "+e.getMessage(),e);
			}	
		}
		
	}
	

//	/*** AZIONE ACCORDO ***/
//
//	@Override
//	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType,
//			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDAccordoAzione id)
//					throws ProtocolException {
//		List<ProtocolProperty> protocolPropertyList = null;
//		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
//	}
//
//	@Override
//	public void updateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
//			ProtocolProperties properties, IRegistryReader registryReader, IDAccordoAzione id)
//					throws ProtocolException {
//		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
//	}
//	
//	@Override
//	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
//			IDAccordoAzione id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
//	}
//
//
//	/*** OPERATION PORT TYPE ***/
//
//	@Override
//	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType,
//			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDPortTypeAzione id)
//					throws ProtocolException {
//		List<ProtocolProperty> protocolPropertyList = null;
//		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
//	}
//
//	@Override
//	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
//			ProtocolProperties properties, IRegistryReader registryReader, IDPortTypeAzione id)
//					throws ProtocolException {
//		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
//	}
//	
//	@Override
//	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
//			IDPortTypeAzione id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
//	}
//
//
//	/*** PORT TYPE ***/
//
//	@Override
//	public ConsoleConfiguration getDynamicConfigPortType(ConsoleOperationType consoleOperationType,
//			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDPortType id)
//					throws ProtocolException {
//		List<ProtocolProperty> protocolPropertyList = null;
//		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
//	}
//
//	@Override
//	public void updateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
//			ProtocolProperties properties, IRegistryReader registryReader, IDPortType id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
//	}
//	
//	@Override
//	public void validateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
//			IDPortType id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
//	}
//	
//	/*** RESOURCE ***/
//
//	@Override
//	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType,
//			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDResource id)
//					throws ProtocolException {
//		List<ProtocolProperty> protocolPropertyList = null;
//		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
//	}
//
//	@Override
//	public void updateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
//			ProtocolProperties properties, IRegistryReader registryReader, IDResource id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
//	}
//	
//	@Override
//	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
//			IDResource id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
//	}
//	
//	/*** ACCORDO COOPERAZIONE ***/
//	
//	@Override
//	public ConsoleConfiguration getDynamicConfigAccordoCooperazione(ConsoleOperationType consoleOperationType,
//			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDAccordo id)
//			throws ProtocolException {
//		List<ProtocolProperty> protocolPropertyList = null;
//		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
//	}
//	
//	@Override
//	public void updateDynamicConfigAccordoCooperazione(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
//			ProtocolProperties properties, IRegistryReader registryReader, IDAccordo id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
//	}
//	
//	@Override
//	public void validateDynamicConfigCooperazione(ConsoleConfiguration consoleConfiguration,
//			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
//			IDAccordo id) throws ProtocolException {
//		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
//	}
//	
//	
//	/*** ACCORDI SERVIZIO PARTE SPECIFICA ***/
//	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDServizio id)
			throws ProtocolException {
		
		PModeRegistryReader pmodeRR = new PModeRegistryReader(registryReader, this.protocolFactory);
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_ID, 
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_LABEL);
		configuration.addConsoleItem(titolo );
		
		StringConsoleItem securityProfileItem = (StringConsoleItem) ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,AS4ConsoleCostanti.AS4_EROGAZIONE_SECURITY_PROFILE_ID, AS4ConsoleCostanti.AS4_EROGAZIONE_SECURITY_PROFILE_LABEL);
		List<Policy> listPolicy = null;
		try {
			listPolicy = pmodeRR.findAllPolicies();
		}catch(Exception e) {
			throw new ProtocolException("Errore durante il recupero delle policy: "+e.getMessage(),e);
		}
		for (Policy policy : listPolicy) {
			securityProfileItem.addLabelValue(policy.getName(),policy.getName());
		}
		configuration.addConsoleItem(securityProfileItem);
		
		return configuration;
	}

	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
			IDServizio id) throws ProtocolException {
		
		StringProperty securityProfileItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_EROGAZIONE_SECURITY_PROFILE_ID);
		if(securityProfileItem==null || "".equals(securityProfileItem)) {
			throw new ProtocolException("Deve essere selezionato uno dei valori presenti nel parametro '"+AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_LABEL+"'");
		}
		
	}

}
