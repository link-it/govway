/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.properties;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.StatoConfigurazioneAccordo;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.utils.ModISecurityUtils;

/**
 * ModIDynamicConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfiguration extends BasicDynamicConfiguration implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {



	private ModIProperties modiProperties = null;

	public ModIDynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
		this.modiProperties = ModIProperties.getInstance();
	}

	
	
	
	
	/*** SOGGETTI */
	
	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDSoggetto id) throws ProtocolException {
		
		ConsoleConfiguration configuration = ModIDynamicConfigurationSoggettiUtilities.getDynamicConfigSoggetto(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id);
		if(configuration!=null) {
			return configuration;
		}
		else {
			return super.getDynamicConfigSoggetto(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id);
		}

	}

	@Override
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id)
			throws ProtocolException {
		
		ModIDynamicConfigurationSoggettiUtilities.updateDynamicConfigSoggetto(consoleConfiguration,
				consoleOperationType, consoleHelper, 
				properties,
				configIntegrationReader, registryReader, id);
	}

	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id)
			throws ProtocolException {
		
		ModIDynamicConfigurationSoggettiUtilities.validateDynamicConfigSoggetto(consoleConfiguration,
				consoleOperationType, consoleHelper, properties, 
				configIntegrationReader, id,
				registryReader);
		
	}
	
	
	
	

	/*** APPLICATIVI */
	
	@Override
	public ConsoleConfiguration getDynamicConfigServizioApplicativo(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		
		ConsoleConfiguration configuration = ModIDynamicConfigurationApplicativiUtilities.getDynamicConfigServizioApplicativo(consoleOperationType, consoleHelper, registryReader,
				configIntegrationReader, id);
		if(configuration!=null) {
			return configuration;
		}
		else {
			return super.getDynamicConfigServizioApplicativo(consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id);
		}
		
	}

	@Override
	public void updateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {

		ModIDynamicConfigurationApplicativiUtilities.updateDynamicConfigServizioApplicativo(consoleConfiguration,
				consoleOperationType, consoleHelper, 
				properties, 
				configIntegrationReader, id);
		
	}


	@Override
	public void validateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		
		ModIDynamicConfigurationApplicativiUtilities.validateDynamicConfigServizioApplicativo(consoleConfiguration,
				consoleOperationType, consoleHelper, properties, 
				configIntegrationReader, id);
	}

	
	
	
	
	/*** API */
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		
		if(ModIDynamicConfigurationAccordiParteComuneUtilities.isApiSignalHubPushAPI(id, registryReader, this.modiProperties, this.log)) {
			// è un accordo built-in che si assume esista, la configurazone non è necessaria, ma per disattivarla andrebbe fatto anche per api-pdnd
			/**return super.getDynamicConfigAccordoServizioParteComune(consoleOperationType, consoleHelper, registryReader,
					configIntegrationReader, id);*/
		}
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
					
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_API_ID, 
				ModIConsoleCostanti.MODIPA_API_LABEL);
		configuration.addConsoleItem(titolo );

		boolean rest = ModIDynamicConfigurationAccordiParteComuneUtilities.isApiRest(consoleOperationType, consoleHelper, registryReader, id);

		// Sezione Interazione (le risorse massive sono previste solamente per API REST)
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(
				ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_REST_ID,
				ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_REST_LABEL));
		if (rest) {
			BooleanConsoleItem bulkResourceItem = (BooleanConsoleItem)
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
							ConsoleItemType.CHECKBOX,
							ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_BULK_RESOURCE_ID,
							ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_BULK_RESOURCE_LABEL);
			bulkResourceItem.setLabelRight(ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_BULK_RESOURCE_LABEL_RIGHT);
			bulkResourceItem.setDefaultValue(false);
			configuration.addConsoleItem(bulkResourceItem);
		}

		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_PROFILO_CANALE_ID,
				ModIConsoleCostanti.MODIPA_API_PROFILO_CANALE_LABEL));

		StringConsoleItem profiloSicurezzaCanaleItem = (StringConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_ID,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL);
		profiloSicurezzaCanaleItem.addLabelValue((this.modiProperties.isModIVersioneBozza()!=null && this.modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NEW,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01);
		profiloSicurezzaCanaleItem.addLabelValue((this.modiProperties.isModIVersioneBozza()!=null && this.modiProperties.isModIVersioneBozza().booleanValue()) ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NEW,
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02);
		profiloSicurezzaCanaleItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE);
		if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE.equals(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NOTE);
		}
		profiloSicurezzaCanaleItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloSicurezzaCanaleItem);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.addProfiloSicurezzaMessaggio(this.modiProperties,
				configuration, rest, false,
				null);
		
		return configuration;
	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
			throws ProtocolException {
		
		if(ModIDynamicConfigurationAccordiParteComuneUtilities.isApiSignalHubPushAPI(id, registryReader, this.modiProperties, this.log)) {
			// è un accordo built-in che si assume esista, la configurazone non è necessaria, ma per disattivarla andrebbe fatto anche per api-pdnd
			/**super.updateDynamicConfigAccordoServizioParteComune(consoleConfiguration,
					consoleOperationType, consoleHelper, properties,
					registryReader, configIntegrationReader, id);
			return;*/
		}
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.updateProfiloSicurezzaCanale(consoleConfiguration, properties);
		
		boolean rest = ModIDynamicConfigurationAccordiParteComuneUtilities.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
		// lo scambio di dati asincrono fissa alcuni valori della sicurezza messaggio, che devono essere impostati prima del suo aggiornamento
		ModIDynamicConfigurationPdndAsyncUtilities.impostaSicurezzaMessaggioApi(properties);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.updateProfiloSicurezzaMessaggio(this.modiProperties,
				consoleConfiguration, consoleHelper, properties, rest, false);
		
		// dopo la sicurezza messaggio: lo scambio di dati asincrono dipende dalla generazione del token 'Authorization PDND'
		ModIDynamicConfigurationPdndAsyncUtilities.updatePdndAsyncApi(consoleConfiguration, properties, registryReader, this.protocolFactory, rest, id);
		
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException{
		
		if(ModIDynamicConfigurationAccordiParteComuneUtilities.isApiSignalHubPushAPI(id, registryReader, this.modiProperties, this.log)) {
			// è un accordo built-in che si assume esista, la configurazone non è necessaria, ma per disattivarla andrebbe fatto anche per api-pdnd
			/**super.validateDynamicConfigAccordoServizioParteComune(consoleConfiguration, consoleOperationType, consoleHelper, properties, 
					registryReader, configIntegrationReader, id);
			return;*/
		}
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.validateDynamicConfigAccordoServizioParteComune(consoleOperationType, consoleHelper, properties, 
				registryReader, id);
		
		boolean rest = ModIDynamicConfigurationAccordiParteComuneUtilities.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		ModIDynamicConfigurationPdndAsyncUtilities.validatePdndAsyncApi(consoleOperationType, properties, registryReader, rest, id);
	}
	
	@Override
	public StatoConfigurazioneAccordo verifyStatoAccordoServizioParteComune(IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		AccordoServizioParteComune api = null;
		try {
			api = registryReader.getAccordoServizioParteComune(id, false, false);
		}catch(RegistryNotFound notFound) {
			return null;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		// scambi di dati asincroni PDND: le risorse/azioni devono essere associate alle fasi previste
		return ModIPdndAsyncUtils.verificaFasi(api, ServiceBinding.REST.equals(api.getServiceBinding()));
	}
	
	@Override
	public List<StatoConfigurazioneAccordo> findStatoAccordiServizioParteComuneNonConfigurati(IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader) throws ProtocolException {
		// vengono lette solamente le API configurate per gli scambi di dati asincroni PDND, individuate con un'unica ricerca
		List<StatoConfigurazioneAccordo> list = new ArrayList<>();
		for (IDAccordo idAccordo : ModIPdndAsyncUtils.findApiPdndAsync(registryReader)) {
			StatoConfigurazioneAccordo stato = verifyStatoAccordoServizioParteComune(consoleHelper, registryReader, configIntegrationReader, idAccordo);
			if(stato!=null) {
				stato.setIdAccordo(idAccordo);
				list.add(stato);
			}
		}
		return list;
	}
	
	

	
	
	
	
	/*** OPERAZIONI SOAP */
	
	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id) throws ProtocolException {
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_AZIONE_ID, 
				ModIConsoleCostanti.MODIPA_AZIONE_LABEL);
		configuration.addConsoleItem(titolo );
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.addProfiloInterazione(this.modiProperties,
				configuration, false, null);
		
		AccordoServizioParteComune api = null;
		try {
			api = registryReader.getAccordoServizioParteComune(id.getIdPortType().getIdAccordo(), false, false);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		String schemaAuditImpostatoInAPIoAltreAzioni = ModISecurityUtils.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, id.getIdPortType().getNome());
	
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.addProfiloSicurezzaMessaggio(this.modiProperties,
				configuration, false, true,
				schemaAuditImpostatoInAPIoAltreAzioni);
		
		return configuration;
		
	}

	@Override
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
			throws ProtocolException {
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.updateProfiloInterazione(this.modiProperties,this.protocolFactory,this.log,
				consoleConfiguration, consoleOperationType, properties, registryReader, id.getIdPortType().getIdAccordo(), id.getIdPortType().getNome(), id.getNome(), false, null);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.updateProfiloSicurezzaMessaggio(this.modiProperties,
				consoleConfiguration, consoleHelper, properties, false, true);
		
		// una risorsa/azione associata ad una fase dello scambio asincrono non può modificare i valori fissati nella sicurezza messaggio ridefinita
		ModIDynamicConfigurationPdndAsyncUtilities.updateFaseSicurezzaMessaggio(consoleConfiguration, properties);
		
	}

	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
			throws ProtocolException {

		ModIDynamicConfigurationAccordiParteComuneUtilities.validateProfiloInterazione(properties, id.getIdPortType().getNome(), false);
		
		ModIDynamicConfigurationPdndAsyncUtilities.validateFase(properties, registryReader, id.getIdPortType().getIdAccordo(), 
				id.getIdPortType().getNome(), id.getNome(), false);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.validateProfiloSicurezzaMessaggio(properties, false);
	}


	
	
	/*** RISORSE REST */
	
	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path) throws ProtocolException {

		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_AZIONE_ID, 
				ModIConsoleCostanti.MODIPA_AZIONE_LABEL);
		configuration.addConsoleItem(titolo );
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.addProfiloInterazione(this.modiProperties,
				configuration, true, httpMethod);
		
		AccordoServizioParteComune api = null;
		try {
			api = registryReader.getAccordoServizioParteComune(id.getIdAccordo(), false, false);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		String schemaAuditImpostatoInAPIoAltreAzioni = ModISecurityUtils.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, null);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.addProfiloSicurezzaMessaggio(this.modiProperties,
				configuration, true, true,
				schemaAuditImpostatoInAPIoAltreAzioni);
		
		return configuration;
		
	}

	@Override
	public void updateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path)
			throws ProtocolException {
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.updateProfiloInterazione(this.modiProperties,this.protocolFactory,this.log,
				consoleConfiguration, consoleOperationType, properties, registryReader, id.getIdAccordo(), null, id.getNome(), true, httpMethod);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.updateProfiloSicurezzaMessaggio(this.modiProperties,
				consoleConfiguration, consoleHelper, properties, true, true);
		
		// una risorsa/azione associata ad una fase dello scambio asincrono non può modificare i valori fissati nella sicurezza messaggio ridefinita
		ModIDynamicConfigurationPdndAsyncUtilities.updateFaseSicurezzaMessaggio(consoleConfiguration, properties);
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path)
			throws ProtocolException {
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.validateProfiloInterazione(properties, null, true);
		
		ModIDynamicConfigurationPdndAsyncUtilities.validateFase(properties, registryReader, id.getIdAccordo(), 
				null, id.getNome(), true);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.validateProfiloSicurezzaMessaggio(properties, true);
	}
	
	
	
	
	
	
	
	/** EROGAZIONI / FRUIZIONI **/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizio id) throws ProtocolException {
		
		ConsoleConfiguration configuration = ModIDynamicConfigurationAccordiParteSpecificaUtilities.getDynamicConfigParteSpecifica(this.log, this.modiProperties,
				consoleOperationType, consoleHelper, registryReader, configIntegrationReader, id, null, false);
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
		
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.updateDynamicConfigParteSpecifica(this.log, this.modiProperties,
				consoleConfiguration, 
				consoleOperationType, consoleHelper, properties, 
				id, registryReader, configIntegrationReader, false);
		if(!operazioneGestita) {
			super.updateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.validateDynamicConfigParteSpecifica(this.log, this.modiProperties,
				consoleConfiguration, consoleHelper, properties, id, registryReader, configIntegrationReader, false);
		if(!operazioneGestita) {
			super.validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		
		ConsoleConfiguration configuration = ModIDynamicConfigurationAccordiParteSpecificaUtilities.getDynamicConfigParteSpecifica(this.log, this.modiProperties,
				consoleOperationType, consoleHelper, registryReader, configIntegrationReader, 
				id.getIdServizio(), id.getIdFruitore(), true);
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
		
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.updateDynamicConfigParteSpecifica(this.log, this.modiProperties,
				consoleConfiguration, 
				consoleOperationType, consoleHelper, properties, 
				id.getIdServizio(), registryReader, configIntegrationReader, true);
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
	
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.validateDynamicConfigParteSpecifica(this.log, this.modiProperties,
				consoleConfiguration, consoleHelper, properties, id.getIdServizio(), registryReader, configIntegrationReader, true);
		if(!operazioneGestita) {
			super.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType,
					consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
}
