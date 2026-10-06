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

package org.openspcoop2.pdd.core.observability;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.RequestConfig;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.UtilsException;

/**
 * ServiceMetricsLabels
 *
 * Costruisce le label delle metriche di dettaglio per servizio a partire dalla configurazione
 * della richiesta. Le label identificano univocamente l'erogazione o la fruizione tramite
 * 'interface_id', ricavato dal nome della porta di default (anche quando la richiesta è gestita
 * da una configurazione specifica per gruppo di azioni) nella forma normalizzata utilizzata
 * nella url di invocazione; il gruppo è indicato dalla label 'group'.
 * I tag dell'API sono riportati nella label 'tags', in ordine alfabetico e separati da virgola.
 * I nomi delle label riprendono quelli degli header di integrazione e del file trace.
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceMetricsLabels {

	private ServiceMetricsLabels() {}

	public static final String LABEL_INTERFACE_ID = "interface_id";
	public static final String LABEL_GROUP = "group";
	public static final String LABEL_PROVIDER_TYPE = "provider_type";
	public static final String LABEL_PROVIDER = "provider";
	public static final String LABEL_SENDER_TYPE = "sender_type";
	public static final String LABEL_SENDER = "sender";
	public static final String LABEL_SERVICE_TYPE = "service_type";
	public static final String LABEL_SERVICE = "service";
	public static final String LABEL_SERVICE_VERSION = "service_version";
	public static final String LABEL_ACTION = "action";
	public static final String LABEL_API = "api";
	public static final String LABEL_API_VERSION = "api_version";
	public static final String LABEL_API_PROVIDER_TYPE = "api_provider_type";
	public static final String LABEL_API_PROVIDER = "api_provider";
	public static final String LABEL_TAGS = "tags";

	/**
	 * Indica se le metriche di dettaglio sono abilitate tramite la proprietà
	 * {@link GovwayMeterRegistry#PROPRIETA_METRICHE_DETAILS} definita sulla configurazione
	 * (gruppo) dell'erogazione o della fruizione che ha gestito la richiesta.
	 */
	public static boolean isEnabled(RequestInfo requestInfo) {
		if(requestInfo==null || requestInfo.getRequestConfig()==null) {
			return false;
		}
		RequestConfig requestConfig = requestInfo.getRequestConfig();
		List<Proprieta> props = null;
		if(requestConfig.getPortaApplicativa()!=null) {
			props = requestConfig.getPortaApplicativa().getProprietaList();
		}
		else if(requestConfig.getPortaDelegata()!=null) {
			props = requestConfig.getPortaDelegata().getProprietaList();
		}
		if(props!=null) {
			for(Proprieta p : props) {
				if(p!=null && GovwayMeterRegistry.PROPRIETA_METRICHE_DETAILS.equals(p.getNome())) {
					return "true".equalsIgnoreCase(p.getValore());
				}
			}
		}
		return false;
	}

	/**
	 * Restituisce le label che identificano l'erogazione o la fruizione (comprensive dell'azione e dell'API implementata).
	 * Tutte le label sono sempre presenti (valore vuoto se non applicabile): 'sender' e 'sender_type'
	 * sono valorizzate solo per le fruizioni, 'api_provider' e 'api_provider_type' solo per i profili
	 * che prevedono il soggetto referente dell'API.
	 */
	public static Map<String,String> build(RequestInfo requestInfo, String action, IDAccordo idAccordo) throws UtilsException {
		try {
			return buildEngine(requestInfo, action, idAccordo);
		}catch(ProtocolException | DriverConfigurazioneException | DriverRegistroServiziException | DriverRegistroServiziNotFound e) {
			throw new UtilsException("Costruzione delle label delle metriche di dettaglio fallita: "+e.getMessage(), e);
		}
	}
	private static Map<String,String> buildEngine(RequestInfo requestInfo, String action, IDAccordo idAccordo)
			throws ProtocolException, DriverConfigurazioneException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Map<String,String> labels = new LinkedHashMap<>();
		RequestConfig requestConfig = requestInfo!=null ? requestInfo.getRequestConfig() : null;
		IProtocolFactory<?> protocolFactory = requestInfo!=null ? requestInfo.getProtocolFactory() : null;
		boolean fruizione = requestConfig!=null && requestConfig.getIdPortaDelegataDefault()!=null;

		IDServizio idServizio = requestConfig!=null ? requestConfig.getIdServizio() : null;
		// 'fruizione' implica requestConfig valorizzato
		IDSoggetto idFruitore = fruizione ? requestConfig.getIdFruitore() : null;

		labels.put(LABEL_INTERFACE_ID, nz(readInterfaceId(requestConfig, protocolFactory, fruizione)));
		labels.put(LABEL_GROUP, nz(fruizione ? readGroupFruizione(requestInfo, idServizio, idFruitore) : readGroupErogazione(requestInfo, idServizio)));
		putSoggetto(labels, LABEL_PROVIDER_TYPE, LABEL_PROVIDER, idServizio!=null ? idServizio.getSoggettoErogatore() : null);
		putSoggetto(labels, LABEL_SENDER_TYPE, LABEL_SENDER, idFruitore);
		putServizio(labels, idServizio);
		labels.put(LABEL_ACTION, nz(action));
		putApi(labels, idAccordo, protocolFactory);
		labels.put(LABEL_TAGS, nz(readTags(requestInfo, idAccordo)));
		return labels;
	}

	private static void putSoggetto(Map<String,String> labels, String labelTipo, String labelNome, IDSoggetto idSoggetto) {
		labels.put(labelTipo, nz(idSoggetto!=null ? idSoggetto.getTipo() : null));
		labels.put(labelNome, nz(idSoggetto!=null ? idSoggetto.getNome() : null));
	}

	private static void putServizio(Map<String,String> labels, IDServizio idServizio) {
		if(idServizio==null) {
			labels.put(LABEL_SERVICE_TYPE, "");
			labels.put(LABEL_SERVICE, "");
			labels.put(LABEL_SERVICE_VERSION, "");
			return;
		}
		labels.put(LABEL_SERVICE_TYPE, nz(idServizio.getTipo()));
		labels.put(LABEL_SERVICE, nz(idServizio.getNome()));
		labels.put(LABEL_SERVICE_VERSION, idServizio.getVersione()!=null ? idServizio.getVersione().toString() : "");
	}

	/** API implementata; il soggetto referente è riportato solo per i profili che lo prevedono. */
	private static void putApi(Map<String,String> labels, IDAccordo idAccordo, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		labels.put(LABEL_API, nz(idAccordo!=null ? idAccordo.getNome() : null));
		labels.put(LABEL_API_VERSION, (idAccordo!=null && idAccordo.getVersione()!=null) ? idAccordo.getVersione().toString() : "");
		IDSoggetto idReferente = null;
		if(idAccordo!=null && protocolFactory!=null &&
				protocolFactory.createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune()) {
			idReferente = idAccordo.getSoggettoReferente();
		}
		putSoggetto(labels, LABEL_API_PROVIDER_TYPE, LABEL_API_PROVIDER, idReferente);
	}

	/** Tag dell'API in ordine alfabetico separati da virgola (vuoto se l'API non possiede tag). */
	private static String readTags(RequestInfo requestInfo, IDAccordo idAccordo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		if(idAccordo==null) {
			return null;
		}
		AccordoServizioParteComune aspc = RegistroServiziManager.getInstance().getAccordoServizioParteComune(idAccordo, null, false, false, requestInfo);
		if(aspc==null || aspc.getGruppi()==null || aspc.getGruppi().sizeGruppoList()<=0) {
			return null;
		}
		Set<String> tags = new TreeSet<>();
		for (GruppoAccordo gruppo : aspc.getGruppi().getGruppoList()) {
			if(gruppo!=null && gruppo.getNome()!=null) {
				tags.add(gruppo.getNome());
			}
		}
		return String.join(",", tags);
	}

	/** Nome della porta di default nella forma normalizzata utilizzata nella url di invocazione (senza i tipi di default del profilo). */
	private static String readInterfaceId(RequestConfig requestConfig, IProtocolFactory<?> protocolFactory, boolean fruizione) throws ProtocolException {
		if(requestConfig==null) {
			return null;
		}
		String nomePortaDefault = null;
		if(fruizione) {
			nomePortaDefault = requestConfig.getIdPortaDelegataDefault().getNome();
		}
		else if(requestConfig.getIdPortaApplicativaDefault()!=null) {
			nomePortaDefault = requestConfig.getIdPortaApplicativaDefault().getNome();
		}
		if(nomePortaDefault==null || protocolFactory==null) {
			return nomePortaDefault;
		}
		PorteNamingUtils namingUtils = new PorteNamingUtils(protocolFactory);
		return fruizione ? namingUtils.normalizePD(nomePortaDefault) : namingUtils.normalizePA(nomePortaDefault);
	}

	/** Nome del gruppo (configurazione) che ha gestito la richiesta di una erogazione, come indicato nella console ('Predefinito' per la configurazione di default). */
	private static String readGroupErogazione(RequestInfo requestInfo, IDServizio idServizio) throws DriverConfigurazioneException {
		RequestConfig requestConfig = requestInfo!=null ? requestInfo.getRequestConfig() : null;
		if(requestConfig==null || idServizio==null || requestConfig.getIdPortaApplicativa()==null) {
			return null;
		}
		String nomePorta = requestConfig.getIdPortaApplicativa().getNome();
		List<MappingErogazionePortaApplicativa> list = ConfigurazionePdDManager.getInstance().getMappingErogazionePortaApplicativaList(idServizio, requestInfo);
		if(list!=null) {
			for (MappingErogazionePortaApplicativa mapping : list) {
				if(mapping.getIdPortaApplicativa()!=null && nomePorta.equals(mapping.getIdPortaApplicativa().getNome())) {
					return getDescrizioneGruppo(mapping.isDefault(), mapping.getDescrizione());
				}
			}
		}
		return null;
	}

	/** Nome del gruppo (configurazione) che ha gestito la richiesta di una fruizione, come indicato nella console ('Predefinito' per la configurazione di default). */
	private static String readGroupFruizione(RequestInfo requestInfo, IDServizio idServizio, IDSoggetto idFruitore) throws DriverConfigurazioneException {
		RequestConfig requestConfig = requestInfo!=null ? requestInfo.getRequestConfig() : null;
		if(requestConfig==null || idServizio==null || idFruitore==null || requestConfig.getIdPortaDelegata()==null) {
			return null;
		}
		String nomePorta = requestConfig.getIdPortaDelegata().getNome();
		List<MappingFruizionePortaDelegata> list = ConfigurazionePdDManager.getInstance().getMappingFruizionePortaDelegataList(idFruitore, idServizio, requestInfo);
		if(list!=null) {
			for (MappingFruizionePortaDelegata mapping : list) {
				if(mapping.getIdPortaDelegata()!=null && nomePorta.equals(mapping.getIdPortaDelegata().getNome())) {
					return getDescrizioneGruppo(mapping.isDefault(), mapping.getDescrizione());
				}
			}
		}
		return null;
	}
	private static String getDescrizioneGruppo(boolean isDefault, String descrizione) {
		if(isDefault && StringUtils.isEmpty(descrizione)) {
			return Costanti.MAPPING_DESCRIZIONE_DEFAULT;
		}
		return descrizione;
	}

	private static String nz(String s) {
		return s!=null ? s : "";
	}
}
