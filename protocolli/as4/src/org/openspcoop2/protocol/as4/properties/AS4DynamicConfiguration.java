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

package org.openspcoop2.protocol.as4.properties;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.protocol.as4.builder.AS4BuilderUtils;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
import org.openspcoop2.protocol.as4.pmode.PModeRegistryReader;
import org.openspcoop2.protocol.as4.pmode.Translator;
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
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordoAzioni;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPortTypeAzioni;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaRisorse;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;

import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;

/**
 * TrasparenteTestsuiteDynamicConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4DynamicConfiguration extends BasicDynamicConfiguration implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {

	public AS4DynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	/*** SOGGETTO 
	 * @throws ProtocolException ***/

//	private boolean isOperativo(IRegistryReader registryReader, IDSoggetto id) throws ProtocolException {
//		try {
//			
//			org.openspcoop2.core.registry.Soggetto soggetto = registryReader.getSoggetto(id);
//			if(soggetto.getPortaDominio()==null || "".equals(soggetto.getPortaDominio())) {
//				return false;
//			}
//			else {
//				List<String> list = registryReader.findIdPorteDominio(true);
//				if(list!=null) {
//					for (String nomeOperativa : list) {
//						if(nomeOperativa.equals(soggetto.getPortaDominio())) {
//							return true;
//						}
//					}
//				}
//				return false;
//			}
//			
//		}catch(Exception e) {
//			throw new ProtocolException(e.getMessage(),e);
//		}
//	}
//	
	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDSoggetto id)
					throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		//boolean soggettoOperativo = this.isOperativo(registryReader, id);
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_SOGGETTI_ID, 
				AS4ConsoleCostanti.AS4_SOGGETTI_LABEL);
		configuration.addConsoleItem(titolo );
		
		BaseConsoleItem subTitleInfo = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_SOGGETTI_PARTY_INFO_ID, 
				AS4ConsoleCostanti.AS4_SOGGETTI_PARTY_INFO_LABEL);
		configuration.addConsoleItem(subTitleInfo );
		
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
		
		BaseConsoleItem subTitleEndpoint = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_SOGGETTI_PARTY_ENDPOINT_ID, 
				AS4ConsoleCostanti.AS4_SOGGETTI_PARTY_ENDPOINT_LABEL);
		configuration.addConsoleItem(subTitleEndpoint );
		
		StringConsoleItem userMessagePartyEndpointItem = (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_ID, 
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_LABEL);
		userMessagePartyEndpointItem.setDefaultValue("http://HOST:PORT/domibus/services/msh");
		//userMessagePartyEndpointItem.setRequired(!soggettoOperativo);
		userMessagePartyEndpointItem.setRequired(true);
		configuration.addConsoleItem(userMessagePartyEndpointItem);
		
		StringConsoleItem userMessagePartyCNItem = (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_ID, 
						AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_COMMON_NAME_LABEL);
		userMessagePartyCNItem.setDefaultValue(id.getNome());
		//userMessagePartyCNItem.setRequired(!soggettoOperativo);
		userMessagePartyCNItem.setRequired(true);
		configuration.addConsoleItem(userMessagePartyCNItem);
			
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
		
		// La proprietà AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_ID deve essere una url
		StringProperty userMessagePartyEndpointValueItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_ID);
		if(userMessagePartyEndpointValueItem.getValue()!=null) {
			try {
				URL url = new URL(userMessagePartyEndpointValueItem.getValue());
				url.toString();
			}catch(Exception e) {
				throw new ProtocolException("Deve essere indicata una URL valida per il parametro '"+AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_ENDPOINT_LABEL+"'");
			}
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
		configuration.addConsoleItem(titolo);
		
		BaseConsoleItem subTitleService = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_SERVICE_ID, 
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_SERVICE_LABEL);
		configuration.addConsoleItem(subTitleService);
		
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
		
		BaseConsoleItem subTitlePayload = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PAYLOAD_ID, 
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PAYLOAD_LABEL);
		configuration.addConsoleItem(subTitlePayload);
		
		AbstractConsoleItem<?> payloadProfileItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.FILE,
						AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID, 
						AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL);
		payloadProfileItem.setRequired(false);
		configuration.addConsoleItem(payloadProfileItem);
		
		if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
			AbstractConsoleItem<?> payloadProfileDefaultItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.BINARY,
							ConsoleItemType.FILE,
							AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_DEFAULT_ID, 
							AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_DEFAULT_LABEL);
			PModeRegistryReader pModeRegistryReader = new PModeRegistryReader(registryReader, this.protocolFactory); 
			Translator t = new Translator(pModeRegistryReader);
			byte[] defaultValue = t.translatePayloadProfileDefaultAsCompleteXml();
			BinaryConsoleItem binary = (BinaryConsoleItem) payloadProfileDefaultItem;
			binary.setDefaultValue(defaultValue);
			binary.setReadOnly(true);
			configuration.addConsoleItem(payloadProfileDefaultItem);
		}
		
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
	

//	/*** AZIONE ACCORDO / OPERATION PORT TYPE / RESOURCE  ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDAccordoAzione id)
					throws ProtocolException {	
		return _getDynamicConfigAzione(consoleOperationType, consoleInterfaceType, registryReader, id.getIdAccordo());		
	}

	@Override
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordoAzione id) throws ProtocolException{
		
		StringProperty actionTypeItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
		
		// IdAzione deve essere univoca
		List<IDAccordoAzione> idAccordiAzioniList = null;
		FiltroRicercaAccordoAzioni filtroAccordi = new FiltroRicercaAccordoAzioni();
		filtroAccordi.setNome(id.getIdAccordo().getNome());
		filtroAccordi.setSoggetto(id.getIdAccordo().getSoggettoReferente());
		filtroAccordi.setVersione(id.getIdAccordo().getVersione());
		filtroAccordi.setProtocolPropertiesAzione(new ProtocolProperties());
		filtroAccordi.getProtocolPropertiesAzione().addProperty(actionTypeItem);
		try{
			idAccordiAzioniList = registryReader.findIdAzioneAccordo(filtroAccordi);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di azioni di un accordo di servizio: "+e.getMessage(),e);
		}
		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
			StringBuffer bfExc = new StringBuffer();
			for (IDAccordoAzione idAccordoAzione : idAccordiAzioniList) {
				if(id.equals(idAccordoAzione)==false) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append(idAccordoAzione.getNome());
				}
			}
			if(bfExc.length()>0) {
				throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato all'interno dell'accordo per l'azione: "+bfExc.toString()); // dovrebbe essere uno solo
			}
		}
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDPortTypeAzione id)
					throws ProtocolException {
		return _getDynamicConfigAzione(consoleOperationType, consoleInterfaceType, registryReader, id.getIdPortType().getIdAccordo());
	}

	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortTypeAzione id) throws ProtocolException{
		
		StringProperty actionTypeItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
		
		// IdAzione deve essere univoca
		List<IDPortTypeAzione> idAccordiAzioniList = null;
		FiltroRicercaPortTypeAzioni filtroAccordi = new FiltroRicercaPortTypeAzioni();
		filtroAccordi.setNome(id.getIdPortType().getIdAccordo().getNome());
		filtroAccordi.setSoggetto(id.getIdPortType().getIdAccordo().getSoggettoReferente());
		filtroAccordi.setVersione(id.getIdPortType().getIdAccordo().getVersione());
		filtroAccordi.setNomePortType(id.getIdPortType().getNome());
		filtroAccordi.setProtocolPropertiesAzione(new ProtocolProperties());
		filtroAccordi.getProtocolPropertiesAzione().addProperty(actionTypeItem);
		try{
			idAccordiAzioniList = registryReader.findIdAzionePortType(filtroAccordi);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di azioni di un accordo di servizio: "+e.getMessage(),e);
		}
		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
			StringBuffer bfExc = new StringBuffer();
			for (IDPortTypeAzione idAccordoAzione : idAccordiAzioniList) {
				if(id.equals(idAccordoAzione)==false) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append(idAccordoAzione.getNome());
				}
			}
			if(bfExc.length()>0) {
				throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato all'interno dell'accordo per l'azione: "+bfExc.toString()); // dovrebbe essere uno solo
			}
		}
		
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDResource id)
					throws ProtocolException {
		return _getDynamicConfigAzione(consoleOperationType, consoleInterfaceType, registryReader, id.getIdAccordo());
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDResource id) throws ProtocolException{
		
		StringProperty actionTypeItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
		
		// IdAzione deve essere univoca
		List<IDResource> idAccordiAzioniList = null;
		FiltroRicercaRisorse filtroAccordi = new FiltroRicercaRisorse();
		filtroAccordi.setNome(id.getIdAccordo().getNome());
		filtroAccordi.setSoggetto(id.getIdAccordo().getSoggettoReferente());
		filtroAccordi.setVersione(id.getIdAccordo().getVersione());
		filtroAccordi.setProtocolPropertiesRisorsa(new ProtocolProperties());
		filtroAccordi.getProtocolPropertiesRisorsa().addProperty(actionTypeItem);
		try{
			idAccordiAzioniList = registryReader.findIdResourceAccordo(filtroAccordi);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di risorse di un accordo di servizio: "+e.getMessage(),e);
		}
		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
			StringBuffer bfExc = new StringBuffer();
			for (IDResource idAccordoAzione : idAccordiAzioniList) {
				if(id.equals(idAccordoAzione)==false) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append(idAccordoAzione.getNome());
				}
			}
			if(bfExc.length()>0) {
				throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato all'interno dell'accordo per la risorsa: "+bfExc.toString()); // dovrebbe essere uno solo
			}
		}
		
	}
	
	private ConsoleConfiguration _getDynamicConfigAzione(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDAccordo id) throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_ID, 
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_LABEL);
		configuration.addConsoleItem(titolo );
		
		AccordoServizioParteComune as = null;
		try {
			as = registryReader.getAccordoServizioParteComune(id);
		}catch(Exception e) {
			throw new ProtocolException("Impossibile recuperare l'accordo con id ["+id+"]: "+e.getMessage(),e);
		}	
		
		PModeRegistryReader pModeRegistryReader = new PModeRegistryReader(registryReader, this.protocolFactory); 
		Translator t = new Translator(pModeRegistryReader);
		
		PayloadProfiles pps = AS4BuilderUtils.readPayloadProfiles(t, as, id, false);
		List<String> profiles = new ArrayList<>();
		if(pps!=null && pps.sizePayloadProfileList()>0) {
			for (PayloadProfile pp : pps.getPayloadProfileList()) {
				profiles.add(pp.getName());
			}
		}
		
		BaseConsoleItem subTitleAction = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_ACTION_ID, 
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_ACTION_LABEL);
		configuration.addConsoleItem(subTitleAction );
		
		AbstractConsoleItem<?> actionTypeItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.TEXT_EDIT,
						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID, 
						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID_LABEL);
		actionTypeItem.setRequired(true);
		configuration.addConsoleItem(actionTypeItem);
		
		BaseConsoleItem subTitlePayload = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_PAYLOAD_ID, 
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_PAYLOAD_LABEL);
		configuration.addConsoleItem(subTitlePayload );
		
		if(profiles.size()>0) {
			StringConsoleItem actionPayloadProfile = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.SELECT,
							AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_PROFILE_ID, 
							AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_PROFILE_LABEL);
			
			List<PayloadProfile> listDefault = t.translatePayloadProfileDefault();
			String defaultProfile = null;
			if(listDefault!=null && listDefault.size()>0) {
				for (PayloadProfile payloadProfileDefault : listDefault) {
					String defaultProfileL = payloadProfileDefault.getName();
					if(defaultProfile==null) {
						defaultProfile = defaultProfileL;
					}
					if(profiles.contains(defaultProfileL)==false) {
						actionPayloadProfile.addLabelValue(defaultProfileL, defaultProfileL);
					}
				}
			}
			
			for (String p : profiles) {
				actionPayloadProfile.addLabelValue(p, p);
			}
			actionPayloadProfile.setDefaultValue(defaultProfile);
			actionPayloadProfile.setRequired(false);
			configuration.addConsoleItem(actionPayloadProfile);
		}
		
		BooleanConsoleItem actionCompressPayloadItem = (BooleanConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.CHECKBOX,
						AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_ID, 
						AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_LABEL);
		actionCompressPayloadItem.setRequired(false);
		actionCompressPayloadItem.setDefaultValue(AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_COMPRESS_DEFAULT);
		configuration.addConsoleItem(actionCompressPayloadItem);
		
		return configuration;
	}
	
	


//	/*** ACCORDI SERVIZIO PARTE SPECIFICA ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDServizio id)
			throws ProtocolException {
		
		PModeRegistryReader pmodeRR = new PModeRegistryReader(registryReader, this.protocolFactory);
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_ID, 
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_LABEL);
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
		AS4Properties props = AS4Properties.getInstance(this.log);
		if(props.getSecurityPolicyDefault()!=null) {
			securityProfileItem.setDefaultValue(props.getSecurityPolicyDefault());
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
