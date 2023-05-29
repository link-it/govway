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

package org.openspcoop2.protocol.modipa.properties;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

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
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
					
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_API_ID, 
				ModIConsoleCostanti.MODIPA_API_LABEL);
		configuration.addConsoleItem(titolo );
		
		
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
		
		boolean rest = ModIDynamicConfigurationAccordiParteComuneUtilities.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
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
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.updateProfiloSicurezzaCanale(consoleConfiguration, properties);
		
		boolean rest = ModIDynamicConfigurationAccordiParteComuneUtilities.isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.updateProfiloSicurezzaMessaggio(this.modiProperties,
				consoleConfiguration, consoleHelper, properties, rest, false);
		
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException{
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.validateDynamicConfigAccordoServizioParteComune(consoleOperationType, consoleHelper, properties, 
				registryReader, id);
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
		String schemaAuditImpostatoInAPIoAltreAzioni = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, id.getIdPortType().getNome());
	
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
		
	}

	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
			throws ProtocolException {

		ModIDynamicConfigurationAccordiParteComuneUtilities.validateProfiloInterazione(properties, id.getIdPortType().getNome(), false);
		
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
		String schemaAuditImpostatoInAPIoAltreAzioni = ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(api, null);
		
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
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path)
			throws ProtocolException {
		
		ModIDynamicConfigurationAccordiParteComuneUtilities.validateProfiloInterazione(properties, null, true);
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.validateProfiloSicurezzaMessaggio(properties, true);
	}
	
	
	
	
	
	
	
	/** EROGAZIONI / FRUIZIONI **/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizio id) throws ProtocolException {
		
		ConsoleConfiguration configuration = ModIDynamicConfigurationAccordiParteSpecificaUtilities.getDynamicConfigParteSpecifica(this.modiProperties,
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
		
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.updateDynamicConfigParteSpecifica(this.modiProperties,
				consoleConfiguration, consoleOperationType, consoleHelper, properties, 
				id, registryReader, false);
		if(!operazioneGestita) {
			super.updateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.validateDynamicConfigParteSpecifica(this.modiProperties,
				consoleConfiguration, consoleHelper, properties, id, registryReader, false);
		if(!operazioneGestita) {
			super.validateDynamicConfigAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		
		ConsoleConfiguration configuration = ModIDynamicConfigurationAccordiParteSpecificaUtilities.getDynamicConfigParteSpecifica(this.modiProperties,
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
		
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.updateDynamicConfigParteSpecifica(this.modiProperties,
				consoleConfiguration, consoleOperationType, consoleHelper, properties, id.getIdServizio(), registryReader, true);
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
	
		boolean operazioneGestita = ModIDynamicConfigurationAccordiParteSpecificaUtilities.validateDynamicConfigParteSpecifica(this.modiProperties,
				consoleConfiguration, consoleHelper, properties, id.getIdServizio(), registryReader, true);
		if(!operazioneGestita) {
			super.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(consoleConfiguration, consoleOperationType,
					consoleHelper, properties, registryReader, configIntegrationReader, id);
		}
		
	}
	
}
