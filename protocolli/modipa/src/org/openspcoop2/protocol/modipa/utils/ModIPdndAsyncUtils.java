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

package org.openspcoop2.protocol.modipa.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StatoConfigurazioneAccordo;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.BooleanNullable;

/**
 * Lettura dal registro della configurazione relativa agli scambi di dati asincroni PDND
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIPdndAsyncUtils {
	
	private ModIPdndAsyncUtils() {}
	
	private static final String PREFIX_VERIFICA = "Scambio di dati asincrono: ";

	public static ModIPdndAsyncApiConfig readApiConfig(AccordoServizioParteComune aspc) throws ProtocolException {
		ModIPdndAsyncApiConfig config = new ModIPdndAsyncApiConfig();
		if(aspc==null) {
			return config;
		}
		List<ProtocolProperty> list = aspc.getProtocolPropertyList();
		BooleanNullable enabled = ProtocolPropertiesUtils.getOptionalBooleanValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC);
		config.setEnabled(enabled!=null && enabled.getValue()!=null && enabled.getValue().booleanValue());
		if(!config.isEnabled()) {
			return config;
		}
		config.setRuolo(ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_RUOLO));
		String uri = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_API_CORRELATA);
		if(uri!=null && !ModICostanti.MODIPA_VALUE_UNDEFINED.equals(uri)) {
			config.setUriApiCorrelata(uri);
		}
		config.setTempoMaxCallback(ProtocolPropertiesUtils.getOptionalNumberValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_TEMPO_MAX_CALLBACK, false));
		config.setTempoDisponibilita(ProtocolPropertiesUtils.getOptionalNumberValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_TEMPO_DISPONIBILITA, false));
		BooleanNullable conferma = ProtocolPropertiesUtils.getOptionalBooleanValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_CONFERMA_RICEZIONE);
		config.setConfermaRicezione(conferma!=null && conferma.getValue()!=null && conferma.getValue().booleanValue());
		config.setLimiteEntita(ProtocolPropertiesUtils.getOptionalNumberValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_LIMITE_ENTITA, false));
		return config;
	}
	
	/**
	 * Ritorna la fase dello scambio asincrono associata alla risorsa (REST) o all'azione (SOAP) indicata, o null se non definita
	 */
	public static String readFase(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		Map<String, String> fasi = readFasi(aspc, nomePortType);
		String fase = fasi.get(azione);
		if(fase==null && isFaseCallbackImplicita(aspc, nomePortType)) {
			fase = ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION;
		}
		return fase;
	}
	
	/**
	 * In un'API di callback composta da un'unica risorsa (REST) o da un servizio con un'unica azione (SOAP), 
	 * se non indicata esplicitamente, la fase 'callback_invocation' è associata implicitamente a tale risorsa/azione
	 */
	private static boolean isFaseCallbackImplicita(AccordoServizioParteComune aspc, String nomePortType) throws ProtocolException {
		if(aspc==null || !readApiConfig(aspc).isRuoloCallback()) {
			return false;
		}
		if(nomePortType==null) {
			return aspc.sizeResourceList()==1 && readFasi(aspc, null).isEmpty();
		}
		PortType pt = getPortType(aspc, nomePortType);
		return pt!=null && pt.sizeAzioneList()==1 && readFasi(aspc, nomePortType).isEmpty();
	}
	private static PortType getPortType(AccordoServizioParteComune aspc, String nomePortType) {
		for (PortType pt : aspc.getPortTypeList()) {
			if(nomePortType.equals(pt.getNome())) {
				return pt;
			}
		}
		return null;
	}
	
	/**
	 * Ritorna gli identificativi delle API configurate per gli scambi di dati asincroni PDND (un'unica ricerca sulle proprietà di protocollo)
	 */
	public static List<IDAccordo> findApiPdndAsync(IRegistryReader registryReader) throws ProtocolException {
		try {
			ProtocolFiltroRicercaAccordi filtro = new ProtocolFiltroRicercaAccordi();
			ProtocolProperties pp = new ProtocolProperties();
			pp.addProperty(ModICostanti.MODIPA_PDND_ASYNC, true);
			filtro.setProtocolProperties(pp);
			return registryReader.findIdAccordiServizioParteComune(filtro);
		}catch(RegistryNotFound notFound) {
			return new ArrayList<>();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Verifica che un'API configurata per gli scambi di dati asincroni possieda le risorse (REST) o le azioni (SOAP) associate alle fasi previste:
	 * - API con ruolo 'Erogazione dati': start_interaction, get_resource e, se richiesta la conferma di ricezione, confirmation;
	 * - API con ruolo 'Callback': callback_invocation (implicita se l'API possiede un'unica risorsa o il servizio un'unica azione).
	 * Un'API REST priva di una fase risulta in errore.
	 * Per le API SOAP sono verificati i servizi che possiedono almeno un'azione associata ad una fase (o con fase implicita), mentre gli altri servizi 
	 * non sono coinvolti negli scambi asincroni: l'API risulta in errore se nessun servizio possiede tutte le fasi, 
	 * mentre se convivono servizi completi e incompleti viene segnalato un avviso indicando i servizi incompleti, che non devono essere utilizzati.
	 * 
	 * @return l'esito della verifica, o null se l'API risulta correttamente configurata o non prevede gli scambi asincroni
	 */
	public static StatoConfigurazioneAccordo verificaFasi(AccordoServizioParteComune aspc, boolean rest) throws ProtocolException {
		ModIPdndAsyncApiConfig config = readApiConfig(aspc);
		if(!config.isEnabled()) {
			return null;
		}
		List<String> fasiRichieste = getFasiRichieste(config);
		if(rest) {
			List<String> mancanti = getFasiMancanti(fasiRichieste, readFasi(aspc, null), isFaseCallbackImplicita(aspc, null));
			if(!mancanti.isEmpty()) {
				return StatoConfigurazioneAccordo.newErrore(PREFIX_VERIFICA+"nessuna risorsa associata "+toLabelFasi(mancanti));
			}
			return null;
		}
		
		int serviziConfigurati = 0;
		List<String> serviziIncompleti = new ArrayList<>();
		StringBuilder dettaglioIncompleti = new StringBuilder();
		for (PortType pt : aspc.getPortTypeList()) {
			Map<String, String> fasi = readFasi(aspc, pt.getNome());
			boolean implicita = isFaseCallbackImplicita(aspc, pt.getNome());
			if(fasi.isEmpty() && !implicita) {
				// servizio non coinvolto negli scambi asincroni
				continue;
			}
			List<String> mancanti = getFasiMancanti(fasiRichieste, fasi, implicita);
			if(mancanti.isEmpty()) {
				serviziConfigurati++;
			}
			else {
				serviziIncompleti.add(pt.getNome());
				if(dettaglioIncompleti.length()>0) {
					dettaglioIncompleti.append("; ");
				}
				dettaglioIncompleti.append("nel servizio '").append(pt.getNome()).append("' nessuna azione associata ").append(toLabelFasi(mancanti));
			}
		}
		if(serviziConfigurati==0) {
			if(serviziIncompleti.isEmpty()) {
				return StatoConfigurazioneAccordo.newErrore(PREFIX_VERIFICA+"nessun servizio possiede azioni associate "+toLabelFasi(fasiRichieste));
			}
			return StatoConfigurazioneAccordo.newErrore(PREFIX_VERIFICA+dettaglioIncompleti.toString());
		}
		if(!serviziIncompleti.isEmpty()) {
			return StatoConfigurazioneAccordo.newAvviso(PREFIX_VERIFICA+dettaglioIncompleti.toString()+
					(serviziIncompleti.size()==1 ? " (servizio non utilizzabile)" : " (servizi non utilizzabili)"), serviziIncompleti);
		}
		return null;
	}
	private static List<String> getFasiRichieste(ModIPdndAsyncApiConfig config){
		List<String> l = new ArrayList<>();
		if(config.isRuoloCallback()) {
			l.add(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION);
		}
		else {
			l.add(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION);
			l.add(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_GET_RESOURCE);
			if(config.isConfermaRicezione()) {
				l.add(ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION);
			}
		}
		return l;
	}
	private static List<String> getFasiMancanti(List<String> fasiRichieste, Map<String, String> fasi, boolean callbackImplicita){
		List<String> mancanti = new ArrayList<>();
		for (String fase : fasiRichieste) {
			boolean presente = fasi.containsValue(fase) || 
					(callbackImplicita && ModICostanti.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION.equals(fase));
			if(!presente) {
				mancanti.add(fase);
			}
		}
		return mancanti;
	}
	private static String toLabelFasi(List<String> fasi) {
		StringBuilder sb = new StringBuilder(fasi.size()==1 ? "alla fase " : "alle fasi ");
		for (int i = 0; i < fasi.size(); i++) {
			if(i>0) {
				sb.append(i==fasi.size()-1 ? " e " : ", ");
			}
			sb.append("'").append(fasi.get(i)).append("'");
		}
		return sb.toString();
	}
	
	/**
	 * Ritorna le fasi dello scambio asincrono associate alle risorse (REST) o alle azioni del port type indicato (SOAP),
	 * indicizzate per nome della risorsa o dell'azione
	 */
	public static Map<String, String> readFasi(AccordoServizioParteComune aspc, String nomePortType) throws ProtocolException {
		Map<String, String> map = new HashMap<>();
		if(aspc==null) {
			return map;
		}
		if(nomePortType!=null) {
			for (PortType pt : aspc.getPortTypeList()) {
				if(nomePortType.equals(pt.getNome())) {
					for (Operation op : pt.getAzioneList()) {
						addFase(map, op.getNome(), op.getProtocolPropertyList());
					}
				}
			}
		}
		else {
			for (Resource r : aspc.getResourceList()) {
				addFase(map, r.getNome(), r.getProtocolPropertyList());
			}
		}
		return map;
	}
	/**
	 * Verifica che il codice HTTP sia compreso nell'elenco indicato: codici singoli o intervalli separati da virgola (es. '200-299' oppure '200,202,204-206')
	 */
	public static boolean isCodiceHttpSuccesso(int codice, String codici) throws ProtocolException {
		for (int[] intervallo : parseCodiciHttp(codici)) {
			if(codice>=intervallo[0] && codice<=intervallo[1]) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Interpreta un elenco di codici HTTP (codici singoli o intervalli separati da virgola); viene sollevata un'eccezione se il formato non è valido
	 */
	public static List<int[]> parseCodiciHttp(String codici) throws ProtocolException {
		if(codici==null || "".equals(codici.trim())) {
			throw new ProtocolException("HTTP status codes not provided");
		}
		List<int[]> list = new ArrayList<>();
		for (String token : codici.split(",")) {
			String t = token.trim();
			if("".equals(t)) {
				throw new ProtocolException("Empty value in the HTTP status codes '"+codici+"'");
			}
			int[] intervallo = new int[2];
			int idx = t.indexOf('-');
			try {
				if(idx>0) {
					intervallo[0] = Integer.parseInt(t.substring(0, idx).trim());
					intervallo[1] = Integer.parseInt(t.substring(idx+1).trim());
				}
				else {
					intervallo[0] = Integer.parseInt(t);
					intervallo[1] = intervallo[0];
				}
			}catch(NumberFormatException e) {
				throw new ProtocolException("Value '"+t+"' in the HTTP status codes '"+codici+"' is not valid", e);
			}
			if(intervallo[0]<100 || intervallo[1]>599 || intervallo[0]>intervallo[1]) {
				throw new ProtocolException("Value '"+t+"' in the HTTP status codes '"+codici+"' is not a valid HTTP status code or range (100-599)");
			}
			list.add(intervallo);
		}
		return list;
	}
	
	/**
	 * Codifica il valore secondo la codifica indicata (none, base64, hex)
	 */
	public static String encode(String value, String codifica) throws ProtocolException {
		if(value==null || codifica==null || ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_NONE.equals(codifica)) {
			return value;
		}
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_BASE64.equals(codifica)) {
			return Base64.getEncoder().encodeToString(bytes);
		}
		if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_HEX.equals(codifica)) {
			return HexFormat.of().formatHex(bytes);
		}
		throw new ProtocolException("Codifica '"+codifica+"' non supportata");
	}
	
	/**
	 * Decodifica il valore secondo la codifica indicata (none, base64, hex)
	 */
	public static String decode(String value, String codifica) throws ProtocolException {
		if(value==null || codifica==null || ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_NONE.equals(codifica)) {
			return value;
		}
		try {
			if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_BASE64.equals(codifica)) {
				return new String(Base64.getDecoder().decode(value.trim()), StandardCharsets.UTF_8);
			}
			if(ModICostanti.MODIPA_PDND_ASYNC_VALUE_CODIFICA_HEX.equals(codifica)) {
				return new String(HexFormat.of().parseHex(value.trim()), StandardCharsets.UTF_8);
			}
		}catch(IllegalArgumentException e) {
			// messaggio restituito al client: il dettaglio dell'eccezione resta solo nella causa
			throw new ProtocolException("value is not a valid '"+codifica+"' encoded string",e);
		}
		throw new ProtocolException("Codifica '"+codifica+"' non supportata");
	}
	
	private static void addFase(Map<String, String> map, String nome, List<ProtocolProperty> list) throws ProtocolException {
		String fase = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(list, ModICostanti.MODIPA_PDND_ASYNC_FASE);
		if(fase!=null && !"".equals(fase) && !ModICostanti.MODIPA_VALUE_UNDEFINED.equals(fase)) {
			map.put(nome, fase);
		}
	}
}
