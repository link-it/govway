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

package org.openspcoop2.protocol.as4.properties;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.protocol.as4.builder.AS4BuilderUtils;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
import org.openspcoop2.protocol.as4.pmode.PModeRegistryReader;
import org.openspcoop2.protocol.as4.pmode.TranslatorPayloadProfilesDefault;
import org.openspcoop2.protocol.as4.pmode.TranslatorPropertiesDefault;
import org.openspcoop2.protocol.as4.pmode.beans.Policy;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
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
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;

import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;
import eu.domibus.configuration.Property;
import eu.domibus.configuration.PropertySet;
import eu.domibus.configuration.Properties;

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

//	private boolean isOperativo(IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id) throws ProtocolException {
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
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id)
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
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
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
			StringBuilder bfExc = new StringBuilder();
			for (IDSoggetto idSoggetto : idSoggettiList) {
				if(ConsoleOperationType.ADD.equals(consoleOperationType) || id.equals(idSoggetto)==false) {
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
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
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
		
		String labelSubTitlePayload = AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PAYLOAD_LABEL;
		if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
			labelSubTitlePayload = AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PAYLOAD_PROFILES_LABEL;
		}
		BaseConsoleItem subTitlePayload = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PAYLOAD_ID, 
				labelSubTitlePayload);
		configuration.addConsoleItem(subTitlePayload);
		
		String labelPayloadProfileItem = AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL;
		if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
			labelPayloadProfileItem = "";
		}
		AbstractConsoleItem<?> payloadProfileItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.FILE,
						AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_ID, 
						labelPayloadProfileItem);
		payloadProfileItem.setRequired(false);
		configuration.addConsoleItem(payloadProfileItem);
		
		if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
			AbstractConsoleItem<?> payloadProfileDefaultItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.BINARY,
							ConsoleItemType.FILE,
							AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_DEFAULT_ID, 
							AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_DEFAULT_LABEL);
			TranslatorPayloadProfilesDefault t = TranslatorPayloadProfilesDefault.getTranslator();
			byte[] defaultValue = t.getPayloadProfilesDefaultAsCompleteXml();
			BinaryConsoleItem binary = (BinaryConsoleItem) payloadProfileDefaultItem;
			binary.setDefaultValue(defaultValue);
			binary.setReadOnly(true);
			configuration.addConsoleItem(payloadProfileDefaultItem);
		}
		
		BaseConsoleItem subTitleProperties = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PROPERTIES_ID, 
				AS4ConsoleCostanti.AS4_TITLE_ACCORDO_PROPERTIES_LABEL);
		configuration.addConsoleItem(subTitleProperties);
		
		String labelPropertiesItem = AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_LABEL;
		if(ConsoleOperationType.ADD.equals(consoleOperationType)) {
			labelPropertiesItem = "";
		}
		AbstractConsoleItem<?> propertiesItem = 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BINARY,
						ConsoleItemType.FILE,
						AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_ID, 
						labelPropertiesItem);
		propertiesItem.setRequired(false);
		configuration.addConsoleItem(propertiesItem);
		
		if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
			AbstractConsoleItem<?> propertiesDefaultItem = 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.BINARY,
							ConsoleItemType.FILE,
							AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_DEFAULT_ID, 
							AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_DEFAULT_LABEL);
			TranslatorPropertiesDefault t = TranslatorPropertiesDefault.getTranslator();
			byte[] defaultValue = t.getPropertiesDefaultAsCompleteXml(false);
			BinaryConsoleItem binary = (BinaryConsoleItem) propertiesDefaultItem;
			binary.setDefaultValue(defaultValue);
			binary.setReadOnly(true);
			configuration.addConsoleItem(propertiesDefaultItem);
		}
		
		return configuration;
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException{
		
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
			StringBuilder bfExc = new StringBuilder();
			for (IDAccordo idAccordo : idAccordiList) {
				if(ConsoleOperationType.ADD.equals(consoleOperationType) || (id.equals(idAccordo)==false)) {
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
			
			TranslatorPayloadProfilesDefault t = TranslatorPayloadProfilesDefault.getTranslator();
			PayloadProfiles pps = AS4BuilderUtils.readPayloadProfiles(t, payloadProfileItem.getValue(), id, false);
			if(pps==null) {
				throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL+"', presenta una struttura vuota ?");
			}
			if(pps.sizePayloadList()<=0 && pps.sizePayloadProfileList()<=0) {
				throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL+"', presenta una struttura senza payload o profile");
			}
			if(pps.sizePayloadList()>0) {
				List<Payload> l = t.getListPayloadDefault();
				for (int i = 0; i < pps.sizePayloadList(); i++) {
					Payload p = pps.getPayload(i);
					for (Payload payloadDefault : l) {
						if(p.getName().equals(payloadDefault.getName())) {
							throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL+
									"', presenta un payload '"+p.getName()+"' con un nome già utilizzato tra i payload di default");
						}
					}
				}
			}
			if(pps.sizePayloadProfileList()>0) {
				List<PayloadProfile> l = t.getListPayloadProfileDefault();
				for (int i = 0; i < pps.sizePayloadProfileList(); i++) {
					PayloadProfile p = pps.getPayloadProfile(i);
					for (PayloadProfile payloadProfileDefault : l) {
						if(p.getName().equals(payloadProfileDefault.getName())) {
							throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PAYLOAD_PROFILE_LABEL+
									"', presenta un payload profile '"+p.getName()+"' con un nome già utilizzato tra i payload di default");
						}
					}
				}
			}
		}
		
		// PropertySet
		BinaryProperty propertiesItem = (BinaryProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_ID);
		if(propertiesItem!=null && propertiesItem.getValue()!=null) {
			try {
				eu.domibus.configuration.utils.PropertiesXSDValidator.getXSDValidator(this.log).valida(new ByteArrayInputStream(propertiesItem.getValue()));
			}catch(Exception e) {
				throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_LABEL+"', presenta una struttura non valida: "+e.getMessage(),e);
			}	
			
			TranslatorPropertiesDefault t = TranslatorPropertiesDefault.getTranslator();
			Properties pps = AS4BuilderUtils.readProperties(t, propertiesItem.getValue(), id, false);
			if(pps==null) {
				throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_LABEL+"', presenta una struttura vuota ?");
			}
			if(pps.sizePropertyList()<=0 && pps.sizePropertySetList()<=0) {
				throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_LABEL+"', presenta una struttura senza property o propertySet");
			}
			if(pps.sizePropertyList()>0) {
				List<Property> l = t.getListPropertyDefault();
				for (int i = 0; i < pps.sizePropertyList(); i++) {
					Property p = pps.getProperty(i);
					for (Property pDefault : l) {
						if(p.getName().equals(pDefault.getName())) {
							throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_LABEL+
									"', presenta una proprietà '"+p.getName()+"' con un nome già utilizzato tra le proprietà di default");
						}
					}
				}
			}
			if(pps.sizePropertySetList()>0) {
				List<PropertySet> l = t.getListPropertySetDefault();
				for (int i = 0; i < pps.sizePropertySetList(); i++) {
					PropertySet p = pps.getPropertySet(i);
					for (PropertySet psDefault : l) {
						if(p.getName().equals(psDefault.getName())) {
							throw new ProtocolException("File caricato nel parametro '"+AS4ConsoleCostanti.AS4_ACCORDO_SERVICE_PROPERTIES_LABEL+
									"', presenta un insieme di proprietà (property-set) '"+p.getName()+"' con un nome già utilizzato tra gli insiemi di default");
						}
					}
				}
			}
		}
		
	}
	

//	/*** AZIONE ACCORDO / OPERATION PORT TYPE / RESOURCE  ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordoAzione id)
					throws ProtocolException {	
		return _getDynamicConfigAzione(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id.getIdAccordo());		
	}

	@Override
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordoAzione id) throws ProtocolException{
		
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
			StringBuilder bfExc = new StringBuilder();
			for (IDAccordoAzione idAccordoAzione : idAccordiAzioniList) {
				if(ConsoleOperationType.ADD.equals(consoleOperationType) || id.equals(idAccordoAzione)==false) {
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
		
		// FIX: Domibus utilizza i nomi delle azioni globalmente, quindi non possono essere utilizzati due nomi uguali su due accordi differenti.
		//      durante la ricezione del messaggio, lui controlla semplicemente che sia registrata una azione con quel nome, indifferentemente dal servizi.
		// Nota: Lascio comunque il controllo sopra per dare un msg piu' preciso nel caso sia ridefinita una azione uguale all'interno della solita API
//		filtroAccordi.setNome(null);
//		filtroAccordi.setSoggetto(null);
//		filtroAccordi.setVersione(null);
//		try{
//			idAccordiAzioniList = registryReader.findIdAzioneAccordo(filtroAccordi);
//		}catch(RegistryNotFound notFound) {}
//		catch(Exception e) {
//			throw new ProtocolException("Errore durante la ricerca di azioni di un accordo di servizio: "+e.getMessage(),e);
//		}
//		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
//			StringBuilder bfExc = new StringBuilder();
//			for (IDAccordoAzione idAccordoAzione : idAccordiAzioniList) {
//				if(id.equals(idAccordoAzione)==false) {
//					if(bfExc.length()>0) {
//						bfExc.append(",");
//					}
//					bfExc.append("api:"+idAccordoAzione.getIdAccordo().getNome()+" versione:"+ idAccordoAzione.getIdAccordo().getVersione()+" azione:"+ idAccordoAzione.getNome());
//				}
//			}
//			if(bfExc.length()>0) {
//				throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
//						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato in un'altra azione: "+bfExc.toString()); // dovrebbe essere uno solo
//			}
//		}
		// FIX: il controllo deve essere globale su tutte le tipologie di accordi (rest/soap) e azioni (azioni,operations,resource)
		_validateDynamicConfigResource(actionTypeItem, registryReader, id, null, null);
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
					throws ProtocolException {
		return _getDynamicConfigAzione(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id.getIdPortType().getIdAccordo());
	}

	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id) throws ProtocolException{
		
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
			StringBuilder bfExc = new StringBuilder();
			for (IDPortTypeAzione idAccordoAzione : idAccordiAzioniList) {
				if(ConsoleOperationType.ADD.equals(consoleOperationType) || id.equals(idAccordoAzione)==false) {
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
		
		// FIX: Domibus utilizza i nomi delle azioni globalmente, quindi non possono essere utilizzati due nomi uguali su due accordi differenti.
		//      durante la ricezione del messaggio, lui controlla semplicemente che sia registrata una azione con quel nome, indifferentemente dal servizi.
		// Nota: Lascio comunque il controllo sopra per dare un msg piu' preciso nel caso sia ridefinita una azione uguale all'interno della solita API
//		filtroAccordi.setNome(null);
//		filtroAccordi.setSoggetto(null);
//		filtroAccordi.setVersione(null);
//		filtroAccordi.setNomePortType(null);
//		try{
//			idAccordiAzioniList = registryReader.findIdAzionePortType(filtroAccordi);
//		}catch(RegistryNotFound notFound) {}
//		catch(Exception e) {
//			throw new ProtocolException("Errore durante la ricerca di azioni di un accordo di servizio: "+e.getMessage(),e);
//		}
//		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
//			StringBuilder bfExc = new StringBuilder();
//			for (IDPortTypeAzione idAccordoAzione : idAccordiAzioniList) {
//				if(id.equals(idAccordoAzione)==false) {
//					if(bfExc.length()>0) {
//						bfExc.append(",");
//					}
//					bfExc.append("api:"+idAccordoAzione.getIdPortType().getIdAccordo().getNome()+" versione:"+ idAccordoAzione.getIdPortType().getIdAccordo().getVersione()+
//							" servizio:"+idAccordoAzione.getIdPortType().getNome()+" azione:"+ idAccordoAzione.getNome());
//				}
//			}
//			if(bfExc.length()>0) {
//				throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
//						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato in un'altra azione: "+bfExc.toString()); // dovrebbe essere uno solo
//			}
//		}
		// FIX: il controllo deve essere globale su tutte le tipologie di accordi (rest/soap) e azioni (azioni,operations,resource)
		_validateDynamicConfigResource(actionTypeItem, registryReader, null, id, null);
		
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IDResource id, String httpMethod, String path)
					throws ProtocolException {
		return _getDynamicConfigAzione(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id.getIdAccordo());
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path) throws ProtocolException{
		
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
			StringBuilder bfExc = new StringBuilder();
			for (IDResource idAccordoAzione : idAccordiAzioniList) {
				if(ConsoleOperationType.ADD.equals(consoleOperationType) || id.equals(idAccordoAzione)==false) {
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
		
		// FIX: Domibus utilizza i nomi delle azioni globalmente, quindi non possono essere utilizzati due nomi uguali su due accordi differenti.
		//      durante la ricezione del messaggio, lui controlla semplicemente che sia registrata una azione con quel nome, indifferentemente dal servizi.
		// Nota: Lascio comunque il controllo sopra per dare un msg piu' preciso nel caso sia ridefinita una azione uguale all'interno della solita API
//		filtroAccordi.setNome(null);
//		filtroAccordi.setSoggetto(null);
//		filtroAccordi.setVersione(null);
//		try{
//			idAccordiAzioniList = registryReader.findIdResourceAccordo(filtroAccordi);
//		}catch(RegistryNotFound notFound) {}
//		catch(Exception e) {
//			throw new ProtocolException("Errore durante la ricerca di risorse di un accordo di servizio: "+e.getMessage(),e);
//		}
//		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
//			StringBuilder bfExc = new StringBuilder();
//			for (IDResource idAccordoAzione : idAccordiAzioniList) {
//				if(id.equals(idAccordoAzione)==false) {
//					if(bfExc.length()>0) {
//						bfExc.append(",");
//					}
//					bfExc.append("api:"+idAccordoAzione.getIdAccordo().getNome()+" versione:"+ idAccordoAzione.getIdAccordo().getVersione()+" azione:"+ idAccordoAzione.getNome());
//				}
//			}
//			if(bfExc.length()>0) {
//				throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
//						AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato in un'altra risorsa: "+bfExc.toString()); // dovrebbe essere uno solo
//			}
//		}
		
		// FIX: il controllo deve essere globale su tutte le tipologie di accordi (rest/soap) e azioni (azioni,operations,resource)
		_validateDynamicConfigResource(actionTypeItem, registryReader, null, null, id);
	}
	
	private ConsoleConfiguration _getDynamicConfigAzione(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		
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
		
		StringConsoleItem actionBindingItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID, 
				AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_LABEL);
		actionBindingItem.addLabelValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_LABEL,
				AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_VALUE);
		actionBindingItem.addLabelValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_LABEL,
				AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_PUSH_AND_PUSH_VALUE);
		actionBindingItem.setDefaultValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
		configuration.addConsoleItem(actionBindingItem);
		
		
		
		
		BaseConsoleItem subTitlePayload = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_PAYLOAD_ID, 
				AS4ConsoleCostanti.AS4_TITLE_AZIONE_PAYLOAD_LABEL);
		configuration.addConsoleItem(subTitlePayload );
		
		TranslatorPayloadProfilesDefault tPayload = TranslatorPayloadProfilesDefault.getTranslator();
		
		PayloadProfiles pps = AS4BuilderUtils.readPayloadProfiles(tPayload, as, id, false);
		List<String> profiles = new ArrayList<>();
		if(pps!=null && pps.sizePayloadProfileList()>0) {
			for (PayloadProfile pp : pps.getPayloadProfileList()) {
				profiles.add(pp.getName());
			}
		}
		
		List<PayloadProfile> listDefault = tPayload.getListPayloadProfileDefault();
		
		if(profiles.size()>0 || listDefault.size()>1) {
			StringConsoleItem actionPayloadProfile = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.SELECT,
							AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_PROFILE_ID, 
							AS4ConsoleCostanti.AS4_AZIONE_ACTION_PAYLOAD_PROFILE_LABEL);
			
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
		
		
		

		
		TranslatorPropertiesDefault tProperties = TranslatorPropertiesDefault.getTranslator();
		
		Properties properties = AS4BuilderUtils.readProperties(tProperties, as, id, false);
		List<String> propertySetList = new ArrayList<>();
		if(properties!=null && properties.sizePropertySetList()>0) {
			for (PropertySet pp : properties.getPropertySetList()) {
				propertySetList.add(pp.getName());
			}
		}
		
		List<PropertySet> listPropertySetDefault = tProperties.getListPropertySetDefault();
		
		if(propertySetList.size()>0 || listPropertySetDefault.size()>1) {
			
			
			BaseConsoleItem subTitleProperties = ProtocolPropertiesFactory.newSubTitleItem(
					AS4ConsoleCostanti.AS4_TITLE_AZIONE_PROPERTIES_ID, 
					AS4ConsoleCostanti.AS4_TITLE_AZIONE_PROPERTIES_LABEL);
			configuration.addConsoleItem(subTitleProperties );
			
			
			StringConsoleItem actionPropertySet = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(
							ConsoleItemValueType.STRING,
							ConsoleItemType.SELECT,
							AS4ConsoleCostanti.AS4_AZIONE_ACTION_PROPERTY_SET_ID, 
							AS4ConsoleCostanti.AS4_AZIONE_ACTION_PROPERTY_SET_LABEL);
			
			String defaultPropertySet = null;
			if(listPropertySetDefault!=null && listPropertySetDefault.size()>0) {
				for (PropertySet propertySetDefault : listPropertySetDefault) {
					String defaultPropertySetL = propertySetDefault.getName();
					if(defaultPropertySet==null) {
						defaultPropertySet = defaultPropertySetL;
					}
					if(propertySetList.contains(defaultPropertySetL)==false) {
						actionPropertySet.addLabelValue(defaultPropertySetL, defaultPropertySetL);
					}
				}
			}
			
			for (String p : propertySetList) {
				actionPropertySet.addLabelValue(p, p);
			}
			actionPropertySet.setDefaultValue(defaultPropertySet);
			actionPropertySet.setRequired(false);
			configuration.addConsoleItem(actionPropertySet);
		}
		
		return configuration;
	}
	

	public void _validateDynamicConfigResource(StringProperty actionTypeItem,
			IRegistryReader registryReader, 
			IDAccordoAzione idAccordoAzione, IDPortTypeAzione idPTOperation, IDResource idResource) throws ProtocolException{
		
		
		
		// FIX: Domibus utilizza i nomi delle azioni globalmente, quindi non possono essere utilizzati due nomi uguali su due accordi differenti.
		//      durante la ricezione del messaggio, lui controlla semplicemente che sia registrata una azione con quel nome, indifferentemente dal servizi.
		// Nota: Lascio comunque il controllo nel singolo tipo per dare un msg piu' preciso nel caso sia ridefinita una azione uguale all'interno della solita API

		StringBuilder bfExc = new StringBuilder();
		
		List<IDAccordoAzione> idAccordiAzioniList = null;
		FiltroRicercaAccordoAzioni filtroAccordi = new FiltroRicercaAccordoAzioni();
		filtroAccordi.setProtocolPropertiesAzione(new ProtocolProperties());
		filtroAccordi.getProtocolPropertiesAzione().addProperty(actionTypeItem);
		try{
			idAccordiAzioniList = registryReader.findIdAzioneAccordo(filtroAccordi);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di azioni di un accordo di servizio: "+e.getMessage(),e);
		}
		if(idAccordiAzioniList!=null && idAccordiAzioniList.size()>0) {
			for (IDAccordoAzione id : idAccordiAzioniList) {
				if(idAccordoAzione == null || (idAccordoAzione.equals(id)==false)) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append("api:"+id.getIdAccordo().getNome()+" versione:"+ id.getIdAccordo().getVersione()+" azione:"+ id.getNome());
				}
			}
		}
		
		List<IDPortTypeAzione> idAccordiPTOperationsList = null;
		FiltroRicercaPortTypeAzioni filtroOperations = new FiltroRicercaPortTypeAzioni();
		filtroOperations.setProtocolPropertiesAzione(new ProtocolProperties());
		filtroOperations.getProtocolPropertiesAzione().addProperty(actionTypeItem);
		try{
			idAccordiPTOperationsList = registryReader.findIdAzionePortType(filtroOperations);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di azioni di un accordo di servizio: "+e.getMessage(),e);
		}
		if(idAccordiPTOperationsList!=null && idAccordiPTOperationsList.size()>0) {
			for (IDPortTypeAzione id : idAccordiPTOperationsList) {
				if(idPTOperation == null || (idPTOperation.equals(id)==false)) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append("api:"+id.getIdPortType().getIdAccordo().getNome()+" versione:"+ id.getIdPortType().getIdAccordo().getVersione()+
							" servizio:"+ id.getIdPortType().getNome()+
							" azione:"+ id.getNome());
				}
			}
		}
		
		
		List<IDResource> idAccordiResourceList = null;
		FiltroRicercaRisorse filtroResource = new FiltroRicercaRisorse();
		filtroResource.setProtocolPropertiesRisorsa(new ProtocolProperties());
		filtroResource.getProtocolPropertiesRisorsa().addProperty(actionTypeItem);
		try{
			idAccordiResourceList = registryReader.findIdResourceAccordo(filtroResource);
		}catch(RegistryNotFound notFound) {}
		catch(Exception e) {
			throw new ProtocolException("Errore durante la ricerca di risorse di un accordo di servizio: "+e.getMessage(),e);
		}
		if(idAccordiResourceList!=null && idAccordiResourceList.size()>0) {
			for (IDResource id : idAccordiResourceList) {
				if(idResource == null || (idResource.equals(id)==false)) {
					if(bfExc.length()>0) {
						bfExc.append(",");
					}
					bfExc.append("api:"+id.getIdAccordo().getNome()+" versione:"+ id.getIdAccordo().getVersione()+" risorsa:"+ id.getNome());
				}
			}
		}
		
		if(bfExc.length()>0) {
			throw new ProtocolException("'"+actionTypeItem.getValue()+"', indicato nel parametro '"+
					AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID+"', già utilizzato in un'altra azione: "+bfExc.toString()); // dovrebbe essere uno solo
		}
	}
	


	// /** EROGAZIONI / FRUIZIONI **/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizio id) throws ProtocolException {
		
		ConsoleConfiguration configuration = _getDynamicConfigAccordoServizioParteSpecifica(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id, false);
		if(configuration!=null) {
			return configuration;
		}
		return super.getDynamicConfigAccordoServizioParteSpecifica(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id);
		
	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		
		boolean operazioneGestita = _updateDynamicConfigParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, id, registryReader, false);
		if(!operazioneGestita) {
			super.updateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
			return;
		}
		
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		
		boolean operazioneGestita = _validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, 
				registryReader, configIntegrationReader, id,false);
		if(!operazioneGestita) {
			super.validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
			return;
		}
		
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		
		ConsoleConfiguration configuration = _getDynamicConfigAccordoServizioParteSpecifica(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id.getIdServizio(), true);
		if(configuration!=null) {
			return configuration;
		}
		return super.getDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleOperationType, consoleHelper, registryReader,
				configIntegrationReader, id);
		
	}

	@Override
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id)
			throws ProtocolException {
		
		boolean operazioneGestita = _updateDynamicConfigParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, id.getIdServizio(), registryReader, true);
		if(!operazioneGestita) {
			super.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType,
					consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}

	@Override
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id)
			throws ProtocolException {
	
		boolean operazioneGestita = _validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, 
				registryReader, configIntegrationReader, id.getIdServizio(), true);
		if(!operazioneGestita) {
			super.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType,
					consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
	private ConsoleConfiguration _getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id, boolean fruizioni)
			throws ProtocolException {
		
		PModeRegistryReader pmodeRR = new PModeRegistryReader(registryReader, configIntegrationReader, this.protocolFactory);
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_ID, 
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_LABEL);
		configuration.addConsoleItem(titolo );
		
		
		BaseConsoleItem subTitleSecurity = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_SECURITY_ID, 
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_SECURITY_LABEL);
		configuration.addConsoleItem(subTitleSecurity );
		
		StringConsoleItem securityProfileItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.STRING,
						ConsoleItemType.SELECT,
						AS4ConsoleCostanti.AS4_EROGAZIONE_SECURITY_PROFILE_ID, 
						AS4ConsoleCostanti.AS4_EROGAZIONE_SECURITY_PROFILE_LABEL);
		List<Policy> listPolicy = null;
		try {
			listPolicy = pmodeRR.findAllPolicies();
		}catch(Exception e) {
			throw new ProtocolException("Errore durante il recupero delle policy: "+e.getMessage(),e);
		}
		for (Policy policy : listPolicy) {
			securityProfileItem.addLabelValue(policy.getName(),policy.getName());
		}
		AS4Properties props = AS4Properties.getInstance();
		if(props.getSecurityPolicyDefault()!=null) {
			securityProfileItem.setDefaultValue(props.getSecurityPolicyDefault());
		}
		configuration.addConsoleItem(securityProfileItem);
		
		
		
		BaseConsoleItem subTitleReliability = ProtocolPropertiesFactory.newSubTitleItem(
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_RELIABILITY_ID, 
				AS4ConsoleCostanti.AS4_TITLE_EROGAZIONE_RELIABILITY_LABEL);
		configuration.addConsoleItem(subTitleReliability );
		
		
		
		BooleanConsoleItem reliabilityNonRepudiationItem = (BooleanConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(
						ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.CHECKBOX,
						AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_NON_REPUDIATION_ID, 
						AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_NON_REPUDIATION_LABEL);
		reliabilityNonRepudiationItem.setRequired(false);
		reliabilityNonRepudiationItem.setDefaultValue(AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_NON_REPUDIATION_DEFAULT);
		configuration.addConsoleItem(reliabilityNonRepudiationItem);
		
		
		
		StringConsoleItem reliabilityReplyPatternItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_ID, 
				AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_LABEL);
		reliabilityReplyPatternItem.addLabelValue(AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_RESPONSE_LABEL,
				AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_RESPONSE_VALUE);
		reliabilityReplyPatternItem.addLabelValue(AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_CALLBACK_LABEL,
				AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_CALLBACK_VALUE);
		reliabilityReplyPatternItem.setDefaultValue(AS4ConsoleCostanti.AS4_EROGAZIONE_RELIABILITY_REPLY_PATTERN_DEFAULT);
		configuration.addConsoleItem(reliabilityReplyPatternItem);
		
		
		return configuration;
	}

	private boolean _updateDynamicConfigParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IDServizio id, IRegistryReader registryReader, boolean fruizioni) throws ProtocolException {
		return false;
	}
	
	private boolean _validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDServizio id, boolean fruizioni) throws ProtocolException {
		
		StringProperty securityProfileItem = (StringProperty) 
				ProtocolPropertiesUtils.getAbstractPropertyById(properties, AS4ConsoleCostanti.AS4_EROGAZIONE_SECURITY_PROFILE_ID);
		if(securityProfileItem==null || "".equals(securityProfileItem)) {
			throw new ProtocolException("Deve essere selezionato uno dei valori presenti nel parametro '"+AS4ConsoleCostanti.AS4_SOGGETTO_USER_MESSAGE_PARTY_TYPE_VALUE_LABEL+"'");
		}
		
		return true;
	}

}
