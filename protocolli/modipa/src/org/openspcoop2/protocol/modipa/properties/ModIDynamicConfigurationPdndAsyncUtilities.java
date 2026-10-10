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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncApiConfig;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleItemInfo;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.NumberConsoleItem;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.engine.utils.NamingUtils;

/**
 * Configurazione delle API e delle risorse/azioni relativa agli scambi di dati asincroni PDND
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationPdndAsyncUtilities {
	
	private ModIDynamicConfigurationPdndAsyncUtilities() {}
	
	private static final String PREFIX_FASE = "La fase '";
	private static final String PREFIX_API = "L'API '";
	
	
	
	
	/* **** API **** */

	static void addPdndAsyncApiCheckbox(ConsoleConfiguration configuration) throws ProtocolException {
		
		BooleanConsoleItem asyncItem = (BooleanConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.CHECKBOX,
						ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_ID,
						ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LABEL);
		asyncItem.setLabelRight(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LABEL_RIGHT);
		asyncItem.setDefaultValue(false);
		asyncItem.setReloadOnChange(true);
		configuration.addConsoleItem(asyncItem);
	}
	
	static void addPdndAsyncApiSezione(ConsoleConfiguration configuration) throws ProtocolException {
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_SUBTITLE_ID,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_SUBTITLE_LABEL));
		
		StringConsoleItem ruoloItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_ID, 
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL);
		ruoloItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_ESERVICE, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_VALUE_ESERVICE);
		ruoloItem.addLabelValue(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_CALLBACK, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_VALUE_CALLBACK);
		ruoloItem.setDefaultValue(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_DEFAULT_VALUE);
		ruoloItem.setReloadOnChange(true);
		configuration.addConsoleItem(ruoloItem);
		
		StringConsoleItem apiCorrelataItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_ID, 
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_LABEL);
		apiCorrelataItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		apiCorrelataItem.setNote(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_NOTE);
		apiCorrelataItem.setReloadOnChange(true);
		configuration.addConsoleItem(apiCorrelataItem);
		
		configuration.addConsoleItem(newNumberItem(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_MAX_CALLBACK_ID,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_MAX_CALLBACK_LABEL, null,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_MAX_CALLBACK_INFO));
		
		configuration.addConsoleItem(newNumberItem(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_ID,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_LABEL, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_NOTE,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_INFO));
		
		BooleanConsoleItem confermaItem = (BooleanConsoleItem)
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.BOOLEAN,
						ConsoleItemType.HIDDEN,
						ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_ID,
						ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_LABEL);
		confermaItem.setLabelRight(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_LABEL_RIGHT);
		confermaItem.setDefaultValue(false);
		confermaItem.setInfo(newInfo(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_LABEL, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_INFO));
		configuration.addConsoleItem(confermaItem);
		
		configuration.addConsoleItem(newNumberItem(ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LIMITE_ENTITA_ID,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LIMITE_ENTITA_LABEL, null,
				ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LIMITE_ENTITA_INFO));
	}
	private static NumberConsoleItem newNumberItem(String id, String label, String note, String info) throws ProtocolException {
		NumberConsoleItem item = (NumberConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.NUMBER,
				ConsoleItemType.HIDDEN,
				id, label);
		if(note!=null) {
			item.setNote(note);
		}
		item.setInfo(newInfo(label, info));
		item.setMin(1);
		return item;
	}
	
	private static ConsoleItemInfo newInfo(String header, String body) {
		ConsoleItemInfo info = new ConsoleItemInfo(header);
		info.setHeaderBody(body);
		return info;
	}
	
	private static final String[] ID_ITEMS_ESERVICE = {
			ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_MAX_CALLBACK_ID,
			ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_ID,
			ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_ID,
			ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LIMITE_ENTITA_ID
	};
	
	static void updatePdndAsyncApi(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IRegistryReader registryReader, IProtocolFactory<?> protocolFactory, boolean rest, IDAccordo idAccordo) throws ProtocolException {
		
		boolean async = isAsync(properties);
		updateDipendenzaGenerazioneTokenPdnd(consoleConfiguration, properties, async);
		String ruolo = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_ID, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_DEFAULT_VALUE);
		boolean callback = async && ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_VALUE_CALLBACK.equals(ruolo);
		boolean eservice = async && !callback;
		
		// la sezione contiene sia il ruolo e l'API correlata sia i parametri dell'API principale
		ProtocolPropertiesUtils.getBaseConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_SUBTITLE_ID).setType(async ? ConsoleItemType.SUBTITLE : ConsoleItemType.HIDDEN);
		
		AbstractConsoleItem<?> ruoloItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_ID);
		ruoloItem.setType(async ? ConsoleItemType.SELECT : ConsoleItemType.HIDDEN);
		
		AbstractConsoleItem<?> apiCorrelataItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_ID);
		if(callback) {
			updateApiCorrelata(apiCorrelataItem, properties, registryReader, protocolFactory, rest, idAccordo);
		}
		else {
			apiCorrelataItem.setType(ConsoleItemType.HIDDEN);
			apiCorrelataItem.setRequired(false);
		}
		
		updateItemsEService(consoleConfiguration, eservice);
	}
	
	/**
	 * Lo scambio di dati asincrono è disponibile solamente se la sicurezza messaggio prevede la generazione del token 'Authorization PDND':
	 * la voce viene visualizzata solo in tal caso (o se già abilitata, per consentirne la disabilitazione) e, una volta abilitata,
	 * la generazione del token viene visualizzata in sola lettura.
	 */
	private static void updateDipendenzaGenerazioneTokenPdnd(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties, boolean async) throws ProtocolException {
		AbstractConsoleItem<?> sorgenteItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID);
		boolean sorgenteVisibile = sorgenteItem!=null && !ConsoleItemType.HIDDEN.equals(sorgenteItem.getType());
		boolean sorgentePdnd = sorgenteVisibile && isGenerazioneTokenPdnd(properties);
		
		AbstractConsoleItem<?> asyncItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_ID);
		asyncItem.setType(sorgentePdnd || async ? ConsoleItemType.CHECKBOX : ConsoleItemType.HIDDEN);
		
		if(async && sorgentePdnd) {
			ModIDynamicConfigurationAccordiParteComuneUtilities.setLabelInUse(consoleConfiguration, properties, sorgenteItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID_INUSE_READONLY);
			filtraSceltePrivePdnd(consoleConfiguration, properties);
			bloccaStatoRichiesta(consoleConfiguration, properties);
		}
	}
	/**
	 * Con lo scambio di dati asincrono abilitato vengono rimosse le scelte della sicurezza messaggio con cui il token 'Authorization PDND'
	 * non verrebbe generato sulla richiesta: pattern non definito ('-') e applicabilità alla sola risposta.
	 * Una scelta viene mantenuta se è quella attualmente configurata, per non alterare la visualizzazione di una configurazione esistente.
	 */
	private static void filtraSceltePrivePdnd(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) {
		AbstractConsoleItem<?> patternItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID);
		String pattern = getStringValue(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID, null);
		if(patternItem instanceof StringConsoleItem && !ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(pattern)) {
			((StringConsoleItem) patternItem).removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_UNDEFINED);
		}
		
		AbstractConsoleItem<?> applicabilitaItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID);
		String applicabilita = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, null);
		if(applicabilitaItem instanceof StringConsoleItem) {
			StringConsoleItem sci = (StringConsoleItem) applicabilitaItem;
			if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(applicabilita)) {
				sci.removeLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA);
			}
			if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(applicabilita)) {
				sci.removeLabelValue(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS);
			}
		}
	}
	private static boolean isGenerazioneTokenPdnd(ProtocolProperties properties) {
		return ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(
				getStringValue(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID, null));
	}
	
	private static void updateApiCorrelata(AbstractConsoleItem<?> apiCorrelataItem, ProtocolProperties properties,
			IRegistryReader registryReader, IProtocolFactory<?> protocolFactory, boolean rest, IDAccordo idAccordo) throws ProtocolException {
		apiCorrelataItem.setType(ConsoleItemType.SELECT);
		apiCorrelataItem.setRequired(true);
		StringConsoleItem sci = (StringConsoleItem) apiCorrelataItem;
		sci.addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		// API con ruolo 'Erogazione dati' non ancora associate ad altre API di callback
		List<IDAccordo> list = findApiEService(registryReader, protocolFactory, rest);
		for (IDAccordo idAccordoTrovato : list) {
			if(idAccordo!=null && isStessaApi(idAccordoTrovato, idAccordo)) {
				continue;
			}
			if(getAltraApiCallback(registryReader, toUri(idAccordoTrovato), idAccordo)==null) {
				addLabelValueApi(sci, idAccordoTrovato);
			}
		}
		String uri = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_ID, null);
		if(uri!=null && !ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(uri)) {
			apiCorrelataItem.setLinkAccordoServizioParteComune(uri);
		}
	}
	
	private static void updateItemsEService(ConsoleConfiguration consoleConfiguration, boolean eservice) throws ProtocolException {
		for (String id : ID_ITEMS_ESERVICE) {
			updateItemEService(ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), id), eservice);
		}
	}
	private static void updateItemEService(AbstractConsoleItem<?> item, boolean eservice) throws ProtocolException {
		boolean checkbox = item instanceof BooleanConsoleItem;
		ConsoleItemType typeVisibile = checkbox ? ConsoleItemType.CHECKBOX : ConsoleItemType.NUMBER;
		item.setType(eservice ? typeVisibile : ConsoleItemType.HIDDEN);
		item.setRequired(eservice && !checkbox);
	}
	
	private static String toUri(IDAccordo idAccordo) throws ProtocolException {
		try {
			return IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	private static void addLabelValueApi(StringConsoleItem sci, IDAccordo idAccordo) throws ProtocolException {
		try {
			sci.addLabelValue(NamingUtils.getLabelAccordoServizioParteComune(idAccordo), 
					IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private static List<IDAccordo> findApiEService(IRegistryReader registryReader, IProtocolFactory<?> protocolFactory, boolean rest) throws ProtocolException {
		try {
			ProtocolFiltroRicercaAccordi filtro = new ProtocolFiltroRicercaAccordi();
			filtro.setServiceBinding(rest ? ServiceBinding.REST : ServiceBinding.SOAP);
			filtro.setSoggetto(new IDSoggetto(protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault(), null));
			ProtocolProperties pp = new ProtocolProperties();
			pp.addProperty(ModICostanti.MODIPA_PDND_ASYNC, true);
			pp.addProperty(ModICostanti.MODIPA_PDND_ASYNC_RUOLO, ModICostanti.MODIPA_PDND_ASYNC_RUOLO_VALUE_ESERVICE);
			filtro.setProtocolProperties(pp);
			return registryReader.findIdAccordiServizioParteComune(filtro);
		}catch(RegistryNotFound notFound) {
			return new ArrayList<>();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	/**
	 * API di callback che riferiscono l'API di erogazione dati indicata
	 */
	private static List<IDAccordo> findApiCallback(IRegistryReader registryReader, String uriApiEService) throws ProtocolException {
		try {
			ProtocolFiltroRicercaAccordi filtro = new ProtocolFiltroRicercaAccordi();
			ProtocolProperties pp = new ProtocolProperties();
			pp.addProperty(ModICostanti.MODIPA_PDND_ASYNC_API_CORRELATA, uriApiEService);
			filtro.setProtocolProperties(pp);
			return registryReader.findIdAccordiServizioParteComune(filtro);
		}catch(RegistryNotFound notFound) {
			return new ArrayList<>();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	/**
	 * Ritorna l'eventuale API di callback, diversa da quella indicata, già associata all'API di erogazione dati
	 */
	private static IDAccordo getAltraApiCallback(IRegistryReader registryReader, String uriApiEService, IDAccordo idApiCallback) throws ProtocolException {
		for (IDAccordo idCallback : findApiCallback(registryReader, uriApiEService)) {
			if(idApiCallback==null || !isStessaApi(idCallback, idApiCallback)) {
				return idCallback;
			}
		}
		return null;
	}
	private static boolean isStessaApi(IDAccordo a, IDAccordo b) throws ProtocolException {
		try {
			return IDAccordoFactory.getInstance().getUriFromIDAccordo(a).equals(IDAccordoFactory.getInstance().getUriFromIDAccordo(b));
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	static void validatePdndAsyncApi(ConsoleOperationType consoleOperationType, ProtocolProperties properties,
			IRegistryReader registryReader, boolean rest, IDAccordo idAccordo) throws ProtocolException {
		
		boolean async = isAsync(properties);
		String ruolo = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_ID, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_DEFAULT_VALUE);
		boolean callback = async && ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_VALUE_CALLBACK.equals(ruolo);
		boolean eservice = async && !callback;
		boolean conferma = eservice && isTrue(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_ID);
		
		if(async && !isGenerazioneTokenPdnd(properties)) {
			throw new ProtocolException("Lo scambio di dati asincrono richiede che nella sicurezza messaggio sia selezionata la generazione del token '"+
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_PDND+"'");
		}
		if(async) {
			checkSicurezzaMessaggioRichiesta(properties, "Lo scambio di dati asincrono");
		}
		
		if(eservice) {
			checkNumber(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_MAX_CALLBACK_ID, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_MAX_CALLBACK_LABEL);
			checkNumber(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_ID, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_TEMPO_DISPONIBILITA_LABEL);
			checkNumber(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LIMITE_ENTITA_ID, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_LIMITE_ENTITA_LABEL);
		}
		
		if(callback) {
			String uri = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_ID, null);
			if(uri==null || ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(uri)) {
				throw new ProtocolException("Un'API con ruolo '"+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_CALLBACK+"' richiede che sia indicata l'"+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_LABEL+" a cui la callback si riferisce");
			}
			checkApiEService(registryReader, rest, uri);
			IDAccordo altraCallback = getAltraApiCallback(registryReader, uri, idAccordo);
			if(altraCallback!=null) {
				throw new ProtocolException(PREFIX_API+uri+"' indicata come "+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_LABEL+
						" risulta già associata all'API di callback '"+NamingUtils.getLabelAccordoServizioParteComune(altraCallback)+"'");
			}
		}
		
		if(!ConsoleOperationType.CHANGE.equals(consoleOperationType) || idAccordo==null) {
			return;
		}
		
		// controlli incrociati con le risorse/azioni dell'API e con le API di callback che riferiscono l'API
		AccordoServizioParteComune aspc = null;
		try {
			aspc = registryReader.getAccordoServizioParteComune(idAccordo, false, false);
		}catch(RegistryNotFound notFound) {
			return;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		checkFasiCompatibili(aspc, rest, async, eservice, conferma);
		if(!eservice) {
			checkApiCallbackCorrelate(registryReader, idAccordo);
		}
	}
	private static void checkNumber(ProtocolProperties properties, String id, String label) throws ProtocolException {
		NumberProperty np = (NumberProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
		if(np==null || np.getValue()==null || np.getValue().longValue()<=0) {
			throw new ProtocolException("Deve essere indicato un valore maggiore di zero per il campo '"+label+"'");
		}
	}
	private static void checkApiEService(IRegistryReader registryReader, boolean rest, String uri) throws ProtocolException {
		AccordoServizioParteComune aspcCorrelata = null;
		try {
			IDAccordo idCorrelato = IDAccordoFactory.getInstance().getIDAccordoFromUri(uri);
			aspcCorrelata = registryReader.getAccordoServizioParteComune(idCorrelato, false, false);
		}catch(RegistryNotFound notFound) {
			throw new ProtocolException(PREFIX_API+uri+"' indicata come "+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_LABEL+" non esiste");
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		ModIPdndAsyncApiConfig config = ModIPdndAsyncUtils.readApiConfig(aspcCorrelata);
		if(!config.isRuoloEService()) {
			throw new ProtocolException(PREFIX_API+uri+"' indicata come "+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_LABEL+" non è configurata per gli scambi di dati asincroni con ruolo '"+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_ESERVICE+"'");
		}
		boolean restCorrelata = ServiceBinding.REST.equals(aspcCorrelata.getServiceBinding());
		if(rest!=restCorrelata) {
			throw new ProtocolException(PREFIX_API+uri+"' indicata come "+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_API_CORRELATA_LABEL+" deve essere di tipo "+(rest ? "REST" : "SOAP"));
		}
	}
	private static void checkFasiCompatibili(AccordoServizioParteComune aspc, boolean rest, boolean async, boolean eservice, boolean conferma) throws ProtocolException {
		String oggetto = rest ? "risorsa" : "azione";
		if(rest) {
			checkFasiCompatibili(ModIPdndAsyncUtils.readFasi(aspc, null), oggetto, async, eservice, conferma);
		}
		else {
			for (org.openspcoop2.core.registry.PortType pt : aspc.getPortTypeList()) {
				checkFasiCompatibili(ModIPdndAsyncUtils.readFasi(aspc, pt.getNome()), oggetto, async, eservice, conferma);
			}
		}
	}
	private static void checkFasiCompatibili(Map<String, String> fasi, String oggetto, boolean async, boolean eservice, boolean conferma) throws ProtocolException {
		for (Map.Entry<String, String> entry : fasi.entrySet()) {
			String fase = entry.getValue();
			String prefix = "La "+oggetto+" '"+entry.getKey()+"' è associata alla fase '"+fase+"' dello scambio asincrono";
			if(!async) {
				throw new ProtocolException(prefix+"; prima di disabilitare lo scambio di dati asincrono è necessario eliminare l'associazione");
			}
			checkFaseCompatibile(prefix, fase, eservice, conferma);
		}
	}
	private static void checkFaseCompatibile(String prefix, String fase, boolean eservice, boolean conferma) throws ProtocolException {
		boolean faseCallback = ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase);
		if(eservice == faseCallback) {
			String ruoloAtteso = faseCallback ? ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_CALLBACK : ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_ESERVICE;
			throw new ProtocolException(prefix+", prevista solamente per un'API con ruolo '"+ruoloAtteso+"'");
		}
		if(eservice && !conferma && ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION.equals(fase)) {
			throw new ProtocolException(prefix+"; prima di disabilitare l'opzione '"+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_LABEL+"' è necessario eliminare l'associazione");
		}
	}
	private static void checkApiCallbackCorrelate(IRegistryReader registryReader, IDAccordo idAccordo) throws ProtocolException {
		List<IDAccordo> list = null;
		try {
			list = findApiCallback(registryReader, IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
		}catch(ProtocolException e) {
			throw e;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(list!=null) {
			for (IDAccordo idCallback : list) {
				if(!idCallback.equals(idAccordo)) {
					throw new ProtocolException("L'API è riferita dall'API di callback '"+NamingUtils.getLabelAccordoServizioParteComune(idCallback)+
							"'; il ruolo '"+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_RUOLO_LABEL_ESERVICE+"' non può essere modificato");
				}
			}
		}
	}
	
	
	
	
	/* **** Risorse / Azioni **** */
	
	static void addFase(ConsoleConfiguration configuration) throws ProtocolException {
		StringConsoleItem faseItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_ID, 
				ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_LABEL);
		faseItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA);
		faseItem.setReloadOnChange(true);
		configuration.addConsoleItem(faseItem);
	}
	
	/**
	 * Visualizza la fase dello scambio asincrono se l'API è configurata per gli scambi di dati asincroni.
	 * Se viene selezionata una fase, il pattern di interazione viene fissato ad 'Accesso CRUD' (REST) o 'Bloccante' (SOAP).
	 * 
	 * @return true se è stata selezionata una fase dello scambio asincrono
	 */
	static boolean updateFase(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IRegistryReader registryReader, IDAccordo idAccordo, boolean rest, String nomePortType, String nomeAzione) throws ProtocolException {
		
		AbstractConsoleItem<?> faseItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_ID);
		if(faseItem==null) {
			return false;
		}
		
		ModIPdndAsyncApiConfig config = null;
		AccordoServizioParteComune aspc = null;
		try {
			aspc = registryReader.getAccordoServizioParteComune(idAccordo, false, false);
			config = ModIPdndAsyncUtils.readApiConfig(aspc);
		}catch(RegistryNotFound notFound) {
			config = new ModIPdndAsyncApiConfig();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		StringProperty faseValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_ID);
		if(!config.isEnabled()) {
			faseItem.setType(ConsoleItemType.HIDDEN);
			if(faseValue!=null) {
				faseValue.setValue(ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA);
			}
			return false;
		}
		
		String fase = faseValue!=null ? faseValue.getValue() : null;
		boolean faseSelezionata = fase!=null && !"".equals(fase) && !ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA.equals(fase);
		
		// vengono proposte solamente le fasi non ancora associate ad altre risorse/azioni (dello stesso servizio per le API SOAP);
		// la fase attualmente selezionata viene sempre mantenuta
		Set<String> fasiAssegnate = new HashSet<>();
		if(aspc!=null) {
			for (Map.Entry<String, String> entry : ModIPdndAsyncUtils.readFasi(aspc, rest ? null : nomePortType).entrySet()) {
				if(!entry.getKey().equals(nomeAzione) && !entry.getValue().equals(fase)) {
					fasiAssegnate.add(entry.getValue());
				}
			}
		}
		int fasiDisponibili = addFasi((StringConsoleItem) faseItem, config, fasiAssegnate);
		if(fasiDisponibili==0 && !faseSelezionata) {
			// tutte le fasi sono già associate ad altre risorse/azioni
			faseItem.setType(ConsoleItemType.HIDDEN);
			if(faseValue!=null) {
				faseValue.setValue(ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA);
			}
			return false;
		}
		faseItem.setType(ConsoleItemType.SELECT);
		if(faseSelezionata) {
			// il pattern di interazione viene fissato
			StringProperty profiloInterazioneValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
			if(profiloInterazioneValue!=null) {
				profiloInterazioneValue.setValue(rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
			}
			// se la sicurezza messaggio viene ridefinita sulla risorsa/azione vengono fissati i valori richiesti dallo scambio asincrono
			if(isSicurezzaMessaggioRidefinita(properties)) {
				impostaSicurezzaMessaggio(properties);
			}
		}
		return faseSelezionata;
	}
	
	/**
	 * Dopo l'aggiornamento della sicurezza messaggio ridefinita su una risorsa/azione associata ad una fase dello scambio asincrono:
	 * la generazione del token 'Authorization PDND' e lo stato della sicurezza sulla richiesta vengono visualizzati in sola lettura
	 * e vengono proposte solo le scelte con cui il token viene generato sulla richiesta.
	 */
	static void updateFaseSicurezzaMessaggio(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		AbstractConsoleItem<?> faseItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_ID);
		if(faseItem==null || ConsoleItemType.HIDDEN.equals(faseItem.getType()) || !isFaseSelezionata(properties) || !isSicurezzaMessaggioRidefinita(properties)) {
			return;
		}
		AbstractConsoleItem<?> sorgenteItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID);
		if(sorgenteItem!=null) {
			ModIDynamicConfigurationAccordiParteComuneUtilities.setLabelInUse(consoleConfiguration, properties, sorgenteItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID_INUSE_READONLY);
		}
		filtraSceltePrivePdnd(consoleConfiguration, properties);
		bloccaStatoRichiesta(consoleConfiguration, properties);
	}
	
	/**
	 * Con lo scambio di dati asincrono abilitato sull'API vengono fissati, prima dell'aggiornamento della sicurezza messaggio, i valori richiesti.
	 */
	static void impostaSicurezzaMessaggioApi(ProtocolProperties properties) {
		if(isAsync(properties)) {
			impostaSicurezzaMessaggio(properties);
		}
	}
	/**
	 * Il token 'Authorization PDND' deve essere sempre generato sulla richiesta: viene fissata la generazione del token e,
	 * se l'applicabilità era relativa alla sola risposta o lo stato della richiesta non era abilitato, vengono riportati ai valori che includono la richiesta.
	 */
	private static void impostaSicurezzaMessaggio(ProtocolProperties properties) {
		setStringValue(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND);
		
		String applicabilita = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, null);
		if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(applicabilita)) {
			setStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI);
		}
		else if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(applicabilita)) {
			setStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS);
		}
		else if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(applicabilita)) {
			setStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO);
		}
	}
	/**
	 * Con criteri di applicabilità personalizzati, lo stato della sicurezza sulla richiesta è fissato ad 'Abilitato'.
	 */
	private static void bloccaStatoRichiesta(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) throws ProtocolException {
		AbstractConsoleItem<?> statoRichiestaItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID);
		if(statoRichiestaItem!=null) {
			ModIDynamicConfigurationAccordiParteComuneUtilities.setLabelInUse(consoleConfiguration, properties, statoRichiestaItem, 
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID,
					ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID_INUSE_READONLY);
		}
	}
	
	/**
	 * Verifica che la sicurezza messaggio preveda la generazione del token 'Authorization PDND' sulla richiesta.
	 */
	private static void checkSicurezzaMessaggioRichiesta(ProtocolProperties properties, String prefix) throws ProtocolException {
		String applicabilita = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID, null);
		if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(applicabilita) ||
				ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(applicabilita)) {
			throw new ProtocolException(prefix+" richiede che la sicurezza messaggio sia applicata alla richiesta");
		}
		if(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(applicabilita)) {
			String statoRichiesta = getStringValue(properties, ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID, null);
			if(!ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO.equals(statoRichiesta)) {
				throw new ProtocolException(prefix+" richiede che lo stato della sicurezza messaggio nella richiesta sia '"+
						ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_ABILITATO+"'");
			}
		}
	}
	
	private static boolean isFaseSelezionata(ProtocolProperties properties) {
		String fase = getStringValue(properties, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_ID, null);
		return fase!=null && !"".equals(fase) && !ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA.equals(fase);
	}
	private static boolean isSicurezzaMessaggioRidefinita(ProtocolProperties properties) {
		String actionMode = getStringValue(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_ID, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_DEFAULT_VALUE);
		return !ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_DEFAULT_VALUE.equals(actionMode);
	}
	
	private static int addFasi(StringConsoleItem sci, ModIPdndAsyncApiConfig config, Set<String> fasiAssegnate) {
		sci.addLabelValue(ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_LABEL_NESSUNA, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA);
		int count = 0;
		if(config.isRuoloCallback()) {
			count += addFase(sci, fasiAssegnate, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_LABEL_CALLBACK_INVOCATION, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION);
			return count;
		}
		count += addFase(sci, fasiAssegnate, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_LABEL_START_INTERACTION, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION);
		count += addFase(sci, fasiAssegnate, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_LABEL_GET_RESOURCE, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_GET_RESOURCE);
		if(config.isConfermaRicezione()) {
			count += addFase(sci, fasiAssegnate, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_LABEL_CONFIRMATION, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION);
		}
		return count;
	}
	private static int addFase(StringConsoleItem sci, Set<String> fasiAssegnate, String label, String value) {
		if(fasiAssegnate.contains(value)) {
			return 0;
		}
		sci.addLabelValue(label, value);
		return 1;
	}
	
	static void validateFase(ProtocolProperties properties, IRegistryReader registryReader, IDAccordo idAccordo, 
			String nomePortType, String nomeAzione, boolean rest) throws ProtocolException {
		
		StringProperty faseValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_ID);
		String fase = faseValue!=null ? faseValue.getValue() : null;
		if(fase==null || "".equals(fase) || ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_NESSUNA.equals(fase)) {
			return;
		}
		
		AccordoServizioParteComune aspc = null;
		try {
			aspc = registryReader.getAccordoServizioParteComune(idAccordo, false, false);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		checkFaseApi(fase, ModIPdndAsyncUtils.readApiConfig(aspc));
		
		// se la risorsa/azione ridefinisce la sicurezza messaggio, deve comunque prevedere la generazione del token 'Authorization PDND'
		if(isSicurezzaMessaggioRidefinita(properties)) {
			if(!isGenerazioneTokenPdnd(properties)) {
				throw new ProtocolException(PREFIX_FASE+fase+"' dello scambio asincrono richiede che la sicurezza messaggio ridefinita preveda la generazione del token '"+
						ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_PDND+"'");
			}
			checkSicurezzaMessaggioRichiesta(properties, PREFIX_FASE+fase+"' dello scambio asincrono");
		}
		
		// una fase può essere associata ad una sola risorsa/azione
		Map<String, String> fasi = ModIPdndAsyncUtils.readFasi(aspc, rest ? null : nomePortType);
		for (Map.Entry<String, String> entry : fasi.entrySet()) {
			if(fase.equals(entry.getValue()) && !entry.getKey().equals(nomeAzione)) {
				throw new ProtocolException(PREFIX_FASE+fase+"' dello scambio asincrono risulta già associata "+(rest ? "alla risorsa" : "all'azione")+" '"+entry.getKey()+"'");
			}
		}
	}
	private static void checkFaseApi(String fase, ModIPdndAsyncApiConfig config) throws ProtocolException {
		boolean faseCallback = ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase);
		boolean compatibile = config.isEnabled() && (faseCallback ? config.isRuoloCallback() : config.isRuoloEService());
		if(!compatibile) {
			throw new ProtocolException(PREFIX_FASE+fase+"' dello scambio asincrono non è compatibile con la configurazione ModI dell'API");
		}
		if(ModIConsoleCostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION.equals(fase) && !config.isConfermaRicezione()) {
			throw new ProtocolException(PREFIX_FASE+fase+"' richiede che nell'API sia abilitata l'opzione '"+ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_CONFERMA_RICEZIONE_LABEL+"'");
		}
	}
	
	
	
	
	/* **** Utilities **** */
	
	private static boolean isAsync(ProtocolProperties properties) {
		return isTrue(properties, ModIConsoleCostanti.MODIPA_API_PDND_ASYNC_ID);
	}
	private static boolean isTrue(ProtocolProperties properties, String id) {
		BooleanProperty bp = (BooleanProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
		return bp!=null && bp.getValue()!=null && bp.getValue().booleanValue();
	}
	private static String getStringValue(ProtocolProperties properties, String id, String defaultValue) {
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
		if(sp!=null && sp.getValue()!=null && !"".equals(sp.getValue())) {
			return sp.getValue();
		}
		return defaultValue;
	}
	private static void setStringValue(ProtocolProperties properties, String id, String value) {
		StringProperty sp = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
		if(sp!=null) {
			sp.setValue(value);
		}
	}
}
