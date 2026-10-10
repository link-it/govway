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

import java.util.Arrays;
import java.util.regex.Pattern;

import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncApiConfig;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ConsoleItemInfo;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;

/**
 * Configurazione delle fruizioni e delle erogazioni (sezione 'ModI - Scambi Asincroni') relativa agli scambi di dati asincroni PDND.
 * 
 * La sezione viene aggiunta in fondo alle altre sezioni ModI:
 * - fruizione di un'API con ruolo 'Erogazione dati': modalità con cui viene determinata la URL di callback e invio del purposeId nelle fasi get_resource e confirmation;
 * - fruizione di un'API con ruolo 'Callback': modalità con cui il client fornisce il numero di entità (entityNumber);
 * - erogazione di un'API con ruolo 'Erogazione dati': verifica della URL di callback e codifica dell'header di integrazione che la riporta;
 * - in tutti i casi: codici HTTP della risposta che indicano il completamento della fase (default in modipa.properties).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationPdndAsyncParteSpecificaUtilities {
	
	private ModIDynamicConfigurationPdndAsyncParteSpecificaUtilities() {}
	
	private static final Pattern NOME_VALIDO = Pattern.compile("[!#$%&'*+.^_`|~0-9A-Za-z-]+");

	static boolean isSezionePresente(ModIPdndAsyncApiConfig config, boolean fruizione) {
		if(config==null || !config.isEnabled()) {
			return false;
		}
		// tutte le fruizioni ed erogazioni possiedono almeno i codici HTTP che indicano il completamento della fase
		return true;
	}
	
	static void add(ConsoleConfiguration configuration, ModIPdndAsyncApiConfig config, boolean fruizione) throws ProtocolException {
		
		if(!isSezionePresente(config, fruizione)) {
			return;
		}
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_TITLE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_TITLE_LABEL));
		
		if(fruizione && config.isRuoloEService()) {
			addFruizioneEService(configuration);
		}
		else if(fruizione) {
			addFruizioneCallback(configuration);
		}
		else if(config.isRuoloEService()) {
			addErogazioneEService(configuration);
		}
		
		addHttpStatus(configuration);
	}
	
	private static void addHttpStatus(ConsoleConfiguration configuration) throws ProtocolException {
		StringConsoleItem modeItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_MODE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_MODE_LABEL);
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_LABEL_DEFAULT, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_VALUE_DEFAULT);
		modeItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_LABEL_RIDEFINITO, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_VALUE_RIDEFINITO);
		modeItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_VALUE_DEFAULT);
		modeItem.setReloadOnChange(true);
		// nell'info viene riportato il valore effettivamente utilizzato con la scelta 'Default' (configurato in modipa.properties)
		modeItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_MODE_LABEL, 
				String.format(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_MODE_INFO, ModIProperties.getInstance().getPdndAsyncHttpStatusSuccesso())));
		configuration.addConsoleItem(modeItem);
		
		StringConsoleItem statusItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_LABEL);
		statusItem.setNote(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_NOTE);
		configuration.addConsoleItem(statusItem);
	}
	private static boolean isHttpStatusRidefinito(ProtocolProperties properties) {
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_MODE_ID);
		return sp!=null && ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_VALUE_RIDEFINITO.equals(sp.getValue());
	}
	private static void updateHttpStatus(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		boolean ridefinito = isHttpStatusRidefinito(properties);
		AbstractConsoleItem<?> statusItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_ID);
		statusItem.setType(ridefinito ? ConsoleItemType.TEXT_EDIT : ConsoleItemType.HIDDEN);
		statusItem.setRequired(ridefinito);
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_ID);
		if(sp!=null) {
			if(!ridefinito) {
				sp.setValue(null);
			}
			else if(sp.getValue()==null || "".equals(sp.getValue().trim())) {
				// viene proposto il valore di default
				sp.setValue(ModIProperties.getInstance().getPdndAsyncHttpStatusSuccesso());
			}
		}
	}
	private static void validateHttpStatus(ProtocolProperties properties) throws ProtocolException {
		if(!isHttpStatusRidefinito(properties)) {
			return;
		}
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_ID);
		String codici = sp!=null ? sp.getValue() : null;
		if(codici==null || "".equals(codici.trim())) {
			throw new ProtocolException("Devono essere indicati i '"+ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_LABEL+"' ("+ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_MODE_LABEL+")");
		}
		try {
			ModIPdndAsyncUtils.parseCodiciHttp(codici);
		}catch(ProtocolException e) {
			throw new ProtocolException("Il valore '"+codici+"' indicato per '"+ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_HTTP_STATUS_LABEL+
					"' non è valido: indicare codici HTTP (100-599) singoli o intervalli separati da virgola (es. 200-299 oppure 200,202,204-206)");
		}
	}
	
	private static void addFruizioneEService(ConsoleConfiguration configuration) throws ProtocolException {
		StringConsoleItem sorgenteItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_LABEL);
		sorgenteItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_LABEL_EROGAZIONE, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_EROGAZIONE);
		sorgenteItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_LABEL_CLIENT, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_CLIENT);
		sorgenteItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_DEFAULT_VALUE);
		ConsoleItemInfo infoSorgente = newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_LABEL, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_INFO);
		infoSorgente.setListBody(Arrays.asList(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_INFO_EROGAZIONE,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_INFO_CLIENT));
		sorgenteItem.setInfo(infoSorgente);
		sorgenteItem.setReloadOnChange(true);
		configuration.addConsoleItem(sorgenteItem);
		
		configuration.addConsoleItem(newModalitaItem(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_MODALITA_ID));
		configuration.addConsoleItem(newNomeItem(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_DEFAULT_VALUE));
		
		StringConsoleItem codificaItem = newCodificaItem(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_CODIFICA_ID,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_CODIFICA_LABEL, false);
		codificaItem.setType(ConsoleItemType.HIDDEN);
		configuration.addConsoleItem(codificaItem);
		
		BooleanConsoleItem obbligatoriaItem = (BooleanConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA_ID,
						ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA_LABEL);
		obbligatoriaItem.setLabelRight(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA_LABEL_RIGHT);
		obbligatoriaItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA_LABEL, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA_INFO));
		obbligatoriaItem.setDefaultValue(false);
		configuration.addConsoleItem(obbligatoriaItem);
		
		StringConsoleItem purposeIdItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_PURPOSE_ID_ID, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_PURPOSE_ID_LABEL);
		purposeIdItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_DEFAULT, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_DEFAULT);
		purposeIdItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_PURPOSE_ID_ABILITATO, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_ABILITATO);
		purposeIdItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_PURPOSE_ID_DISABILITATO, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_DISABILITATO);
		purposeIdItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_DEFAULT);
		purposeIdItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_PURPOSE_ID_LABEL, getInfoPurposeId()));
		configuration.addConsoleItem(purposeIdItem);
	}
	
	private static void addFruizioneCallback(ConsoleConfiguration configuration) throws ProtocolException {
		StringConsoleItem modalitaItem = newModalitaItem(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_MODALITA_ID);
		modalitaItem.setType(ConsoleItemType.SELECT);
		modalitaItem.setLabel(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_LABEL);
		modalitaItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_LABEL, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_INFO));
		configuration.addConsoleItem(modalitaItem);
		
		AbstractConsoleItem<?> nomeItem = newNomeItem(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_NOME_ID,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_NOME_DEFAULT_VALUE);
		nomeItem.setType(ConsoleItemType.TEXT_EDIT);
		nomeItem.setRequired(true);
		configuration.addConsoleItem(nomeItem);
	}
	
	private static void addErogazioneEService(ConsoleConfiguration configuration) throws ProtocolException {
		BooleanConsoleItem verificaItem = (BooleanConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.CHECKBOX,
						ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VERIFICA_URL_CALLBACK_ID,
						ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VERIFICA_URL_CALLBACK_LABEL);
		verificaItem.setLabelRight(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VERIFICA_URL_CALLBACK_LABEL_RIGHT);
		verificaItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VERIFICA_URL_CALLBACK_LABEL, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VERIFICA_URL_CALLBACK_INFO));
		verificaItem.setDefaultValue(false);
		configuration.addConsoleItem(verificaItem);
		
		StringConsoleItem codificaItem = newCodificaItem(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA_ID,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA_LABEL, true);
		codificaItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA_LABEL, getInfoCodificaHeader()));
		configuration.addConsoleItem(codificaItem);
	}
	
	// nelle info viene riportata l'impostazione effettivamente utilizzata con la scelta 'Default' (configurata in modipa.properties)
	private static String getInfoPurposeId() throws ProtocolException {
		boolean abilitato = ModIProperties.getInstance().isPdndAsyncPurposeIdGetResourceConfirmation();
		return String.format(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_PURPOSE_ID_INFO, 
				(abilitato ? ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_PURPOSE_ID_ABILITATO : ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_PURPOSE_ID_DISABILITATO).toLowerCase());
	}
	private static String getInfoCodificaHeader() throws ProtocolException {
		ModIProperties modiProperties = ModIProperties.getInstance();
		String codifica = modiProperties.getPdndAsyncUrlCallbackHeaderEncoding();
		String labelCodifica = codifica;
		if(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_NONE.equals(codifica)) {
			labelCodifica = ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_NONE;
		}
		else if(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_BASE64.equals(codifica)) {
			labelCodifica = ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_BASE64;
		}
		else if(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_HEX.equals(codifica)) {
			labelCodifica = ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_HEX;
		}
		return String.format(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA_INFO, 
				modiProperties.getPdndAsyncUrlCallbackHeaderName(), labelCodifica);
	}
	
	private static ConsoleItemInfo newInfo(String header, String body) {
		ConsoleItemInfo info = new ConsoleItemInfo(header);
		info.setHeaderBody(body);
		return info;
	}
	
	private static StringConsoleItem newModalitaItem(String id) throws ProtocolException {
		StringConsoleItem item = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				id, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_MODALITA_LABEL);
		item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_MODALITA_HEADER, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_MODALITA_HEADER);
		item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_MODALITA_QUERY, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_MODALITA_QUERY);
		item.setDefaultValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_MODALITA_DEFAULT_VALUE);
		item.setReloadOnChange(true);
		return item;
	}
	private static AbstractConsoleItem<?> newNomeItem(String id, String defaultValue) throws ProtocolException {
		StringConsoleItem item = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				id, 
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_NOME_LABEL);
		item.setDefaultValue(defaultValue);
		return item;
	}
	private static StringConsoleItem newCodificaItem(String id, String label, boolean addDefault) throws ProtocolException {
		StringConsoleItem item = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				id, label);
		if(addDefault) {
			item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_DEFAULT, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_DEFAULT);
		}
		item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_NONE, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_NONE);
		item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_BASE64, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_BASE64);
		item.addLabelValue(ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_LABEL_CODIFICA_HEX, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_HEX);
		item.setDefaultValue(addDefault ? ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_DEFAULT : ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_CODIFICA_NONE);
		return item;
	}
	
	
	
	static void update(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, ModIPdndAsyncApiConfig config, boolean fruizione) throws ProtocolException {
		if(!isSezionePresente(config, fruizione)) {
			return;
		}
		updateHttpStatus(consoleConfiguration, properties);
		if(!fruizione) {
			return;
		}
		if(!config.isRuoloEService()) {
			// fruizione di un'API di callback: nome di default coerente con la modalità (header o parametro della URL)
			allineaNomeDefault(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_MODALITA_ID, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_NOME_ID,
					ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_NOME_DEFAULT_VALUE, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_NOME_QUERY_DEFAULT_VALUE);
			return;
		}
		allineaNomeDefault(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_MODALITA_ID, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_ID,
				ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_DEFAULT_VALUE, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_QUERY_DEFAULT_VALUE);
		
		// fruizione di un'API con ruolo 'Erogazione dati': le opzioni relative alla URL fornita dal client sono visibili solamente se selezionata tale modalità
		boolean client = isUrlCallbackClient(properties);
		
		AbstractConsoleItem<?> modalitaItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_MODALITA_ID);
		modalitaItem.setType(client ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		
		AbstractConsoleItem<?> nomeItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_ID);
		nomeItem.setType(client ? ConsoleItemType.TEXT_EDIT : ConsoleItemType.HIDDEN);
		nomeItem.setRequired(client);
		
		AbstractConsoleItem<?> codificaItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_CODIFICA_ID);
		codificaItem.setType(client ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		
		AbstractConsoleItem<?> obbligatoriaItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA_ID);
		obbligatoriaItem.setType(client ? ConsoleItemType.CHECKBOX : ConsoleItemType.HIDDEN);
	}
	
	
	
	/**
	 * Il nome di default rispetta le convenzioni di GovWay: 'GovWay-PDND-Xxx' per gli header HTTP e 'govway_pdnd_xxx' per i parametri della URL.
	 * Al cambio di modalità viene proposto il default della nuova modalità, se il nome non è stato personalizzato.
	 */
	private static void allineaNomeDefault(ProtocolProperties properties, String idModalita, String idNome, String defaultHeader, String defaultQuery) {
		StringProperty modalita = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idModalita);
		StringProperty nome = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idNome);
		if(modalita==null || nome==null) {
			return;
		}
		boolean query = ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_VALUE_MODALITA_QUERY.equals(modalita.getValue());
		String valore = nome.getValue();
		if(valore==null || "".equals(valore.trim()) || defaultHeader.equals(valore) || defaultQuery.equals(valore)) {
			nome.setValue(query ? defaultQuery : defaultHeader);
		}
	}
	
		static void validate(ProtocolProperties properties, ModIPdndAsyncApiConfig config, boolean fruizione) throws ProtocolException {
		if(!isSezionePresente(config, fruizione)) {
			return;
		}
		validateHttpStatus(properties);
		if(!fruizione) {
			return;
		}
		if(config.isRuoloEService()) {
			if(isUrlCallbackClient(properties)) {
				checkNome(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_NOME_ID, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_LABEL);
			}
		}
		else {
			checkNome(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_NOME_ID, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_ENTITY_NUMBER_LABEL);
		}
	}
	private static void checkNome(ProtocolProperties properties, String id, String labelSezione) throws ProtocolException {
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
		String nome = sp!=null ? sp.getValue() : null;
		if(nome==null || "".equals(nome.trim())) {
			throw new ProtocolException("Deve essere indicato il nome dell'header HTTP o del parametro della URL ("+labelSezione+")");
		}
		// caratteri ammessi in un nome di header HTTP (token RFC 9110), validi anche come nome di parametro della URL
		if(!NOME_VALIDO.matcher(nome).matches()) {
			throw new ProtocolException("Il nome '"+nome+"' indicato per '"+labelSezione+"' contiene caratteri non ammessi in un header HTTP");
		}
	}
	
	private static boolean isUrlCallbackClient(ProtocolProperties properties) {
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_ID);
		return sp!=null && ModIConsoleCostanti.MODIPA_API_IMPL_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_CLIENT.equals(sp.getValue());
	}
}
