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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIScambioAsincrono;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIScambioAsincrono;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIScambioAsincronoUrlCallbackClient;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIScambioAsincronoUrlCallbackErogazione;
import org.openspcoop2.core.config.rs.server.model.ModIScambioAsincronoCodificaEnum;
import org.openspcoop2.core.config.rs.server.model.ModIScambioAsincronoModalitaEnum;
import org.openspcoop2.core.config.rs.server.model.ModIScambioAsincronoParametro;
import org.openspcoop2.core.config.rs.server.model.ModIScambioAsincronoUrlCallbackSorgenteEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfFruizioneModIScambioAsincronoUrlCallback;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncApiConfig;
import org.openspcoop2.protocol.modipa.utils.ModIPdndAsyncUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * Gestione delle informazioni relative agli scambi di dati asincroni PDND nella configurazione ModI di erogazioni e fruizioni ('modi_scambio_asincrono').
 *
 * Le informazioni ammesse dipendono dal ruolo dell'API implementata:
 * - API con ruolo 'erogazione_dati': nell'erogazione verifica della URL di callback e codifica dell'header che la riporta,
 *   nella fruizione modalità con cui viene determinata la URL di callback e invio del purposeId;
 * - API con ruolo 'callback': nella fruizione modalità con cui il client fornisce il numero di entità;
 * - in tutti i casi: codici HTTP che indicano il completamento della fase.
 * Le informazioni non indicate assumono il valore di default (lo stesso proposto dalla console);
 * i valori di default non vengono riportati in lettura, ad eccezione delle informazioni che determinano la modalità di funzionamento.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ModiErogazioniScambioAsincronoHelper {

	private ModiErogazioniScambioAsincronoHelper() {}

	private static final String SEZIONE = "modi_scambio_asincrono";
	private static final String RUOLO_EROGAZIONE_DATI = "erogazione_dati";
	private static final String RUOLO_CALLBACK = "callback";

	private static ModIPdndAsyncApiConfig readApiConfig(AccordoServizioParteComune aspc) {
		try {
			return ModIPdndAsyncUtils.readApiConfig(aspc);
		}catch(ProtocolException e) {
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}
	}
	private static boolean isAsync(ModIPdndAsyncApiConfig config) {
		return config!=null && config.isEnabled();
	}
	private static void checkNonPrevisto(Object value, String campo, String ruolo) {
		if(value!=null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo '"+SEZIONE+"."+campo+"' non è previsto per un'API con ruolo '"+ruolo+"'");
		}
	}
	private static void checkSezione(Object sezione, ModIPdndAsyncApiConfig config) {
		if(sezione!=null && !isAsync(config)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione '"+SEZIONE+"' non è prevista: l'API implementata non è configurata per gli scambi di dati asincroni");
		}
	}


	/* **** Codici HTTP di esito positivo (erogazione e fruizione) **** */

	private static String readCodiciHttp(Map<String, AbstractProperty<?>> p) throws CoreException {
		String mode = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_HTTP_STATUS_MODE, false);
		if(!CostantiDB.MODIPA_PDND_ASYNC_VALUE_RIDEFINITO.equals(mode)) {
			return null;
		}
		return ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_HTTP_STATUS, false);
	}
	private static void addCodiciHttp(String codici, ProtocolProperties p) {
		if(codici!=null) {
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_HTTP_STATUS_MODE, CostantiDB.MODIPA_PDND_ASYNC_VALUE_RIDEFINITO);
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_HTTP_STATUS, codici);
		}
		else {
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_HTTP_STATUS_MODE, CostantiDB.MODIPA_PDND_ASYNC_VALUE_DEFAULT);
		}
	}


	/* **** Erogazione **** */

	static ErogazioneModIScambioAsincrono readErogazione(AccordoServizioParteComune aspc, Map<String, AbstractProperty<?>> p) throws CoreException {
		ModIPdndAsyncApiConfig config = readApiConfig(aspc);
		if(!isAsync(config)) {
			return null;
		}
		ErogazioneModIScambioAsincrono sa = new ErogazioneModIScambioAsincrono();
		if(config.isRuoloEService()) {
			Boolean verifica = ProtocolPropertiesHelper.getBooleanProperty(p, CostantiDB.MODIPA_PDND_ASYNC_VERIFICA_URL_CALLBACK, false, true);
			sa.setVerificaUrlCallback(verifica!=null && verifica.booleanValue());
			String codifica = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA, false);
			sa.setCodificaHeaderUrlCallback(toCodifica(codifica));
		}
		else {
			sa.setVerificaUrlCallback(null);
		}
		sa.setCodiciHttpEsitoPositivo(readCodiciHttp(p));
		return sa;
	}

	static ProtocolProperties addErogazione(ErogazioneModIScambioAsincrono sa, AccordoServizioParteComune aspc, ProtocolProperties p) {
		ModIPdndAsyncApiConfig config = readApiConfig(aspc);
		checkSezione(sa, config);
		if(!isAsync(config)) {
			return p;
		}
		ProtocolProperties pp = p;
		if(pp==null) {
			if(sa==null) {
				return null;
			}
			pp = new ProtocolProperties();
		}

		boolean verifica = false;
		String codifica = CostantiDB.MODIPA_PDND_ASYNC_VALUE_CODIFICA_DEFAULT;
		if(sa!=null) {
			if(config.isRuoloEService()) {
				verifica = sa.isVerificaUrlCallback()!=null && sa.isVerificaUrlCallback().booleanValue();
				if(sa.getCodificaHeaderUrlCallback()!=null) {
					codifica = sa.getCodificaHeaderUrlCallback().toString();
				}
			}
			else {
				// il campo boolean possiede un valore di default: viene segnalato solamente se abilitato
				checkNonPrevisto(sa.isVerificaUrlCallback()!=null && sa.isVerificaUrlCallback().booleanValue() ? sa.isVerificaUrlCallback() : null, "verifica_url_callback", RUOLO_CALLBACK);
				checkNonPrevisto(sa.getCodificaHeaderUrlCallback(), "codifica_header_url_callback", RUOLO_CALLBACK);
			}
		}
		if(config.isRuoloEService()) {
			pp.addProperty(CostantiDB.MODIPA_PDND_ASYNC_VERIFICA_URL_CALLBACK, verifica);
			pp.addProperty(CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_HEADER_CODIFICA, codifica);
		}
		addCodiciHttp(sa!=null ? sa.getCodiciHttpEsitoPositivo() : null, pp);
		return pp;
	}


	/* **** Fruizione **** */

	static FruizioneModIScambioAsincrono readFruizione(AccordoServizioParteComune aspc, Map<String, AbstractProperty<?>> p) throws CoreException {
		ModIPdndAsyncApiConfig config = readApiConfig(aspc);
		if(!isAsync(config)) {
			return null;
		}
		FruizioneModIScambioAsincrono sa = new FruizioneModIScambioAsincrono();
		if(config.isRuoloEService()) {
			String sorgente = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE, false);
			if(CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_CLIENT.equals(sorgente)) {
				FruizioneModIScambioAsincronoUrlCallbackClient client = new FruizioneModIScambioAsincronoUrlCallbackClient();
				client.setSorgente(ModIScambioAsincronoUrlCallbackSorgenteEnum.CLIENT);
				readParametro(p, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_MODALITA, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME, client);
				String codifica = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_CODIFICA, false);
				ModIScambioAsincronoCodificaEnum codificaEnum = toCodifica(codifica);
				client.setCodifica(codificaEnum!=null ? codificaEnum : ModIScambioAsincronoCodificaEnum.NONE);
				Boolean obbligatoria = ProtocolPropertiesHelper.getBooleanProperty(p, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA, false, true);
				client.setObbligatoria(obbligatoria!=null && obbligatoria.booleanValue());
				sa.setUrlCallback(client);
			}
			else {
				FruizioneModIScambioAsincronoUrlCallbackErogazione erogazione = new FruizioneModIScambioAsincronoUrlCallbackErogazione();
				erogazione.setSorgente(ModIScambioAsincronoUrlCallbackSorgenteEnum.EROGAZIONE);
				sa.setUrlCallback(erogazione);
			}
			String purposeId = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_PURPOSE_ID, false);
			if(CostantiDB.MODIPA_PDND_ASYNC_VALUE_ABILITATO.equals(purposeId)) {
				sa.setInvioPurposeId(true);
			}
			else if(CostantiDB.MODIPA_PDND_ASYNC_VALUE_DISABILITATO.equals(purposeId)) {
				sa.setInvioPurposeId(false);
			}
		}
		else {
			ModIScambioAsincronoParametro entityNumber = new ModIScambioAsincronoParametro();
			readParametro(p, CostantiDB.MODIPA_PDND_ASYNC_ENTITY_NUMBER_MODALITA, CostantiDB.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME, entityNumber);
			sa.setEntityNumber(entityNumber);
		}
		sa.setCodiciHttpEsitoPositivo(readCodiciHttp(p));
		return sa;
	}
	private static void readParametro(Map<String, AbstractProperty<?>> p, String idModalita, String idNome, ModIScambioAsincronoParametro parametro) throws CoreException {
		String modalita = ProtocolPropertiesHelper.getStringProperty(p, idModalita, false);
		parametro.setModalita(CostantiDB.MODIPA_PDND_ASYNC_VALUE_MODALITA_QUERY.equals(modalita) ? ModIScambioAsincronoModalitaEnum.QUERY : ModIScambioAsincronoModalitaEnum.HEADER);
		parametro.setNome(ProtocolPropertiesHelper.getStringProperty(p, idNome, false));
	}

	static ProtocolProperties addFruizione(FruizioneModIScambioAsincrono sa, AccordoServizioParteComune aspc, ProtocolProperties p) {
		ModIPdndAsyncApiConfig config = readApiConfig(aspc);
		checkSezione(sa, config);
		if(!isAsync(config)) {
			return p;
		}
		ProtocolProperties pp = p;
		if(pp==null) {
			if(sa==null) {
				return null;
			}
			pp = new ProtocolProperties();
		}

		if(config.isRuoloEService()) {
			if(sa!=null) {
				checkNonPrevisto(sa.getEntityNumber(), "entity_number", RUOLO_EROGAZIONE_DATI);
			}
			addUrlCallback(sa!=null ? sa.getUrlCallback() : null, pp);
			String purposeId = CostantiDB.MODIPA_PDND_ASYNC_VALUE_DEFAULT;
			if(sa!=null && sa.isInvioPurposeId()!=null) {
				purposeId = sa.isInvioPurposeId().booleanValue() ? CostantiDB.MODIPA_PDND_ASYNC_VALUE_ABILITATO : CostantiDB.MODIPA_PDND_ASYNC_VALUE_DISABILITATO;
			}
			pp.addProperty(CostantiDB.MODIPA_PDND_ASYNC_PURPOSE_ID, purposeId);
		}
		else {
			if(sa!=null) {
				checkNonPrevisto(sa.getUrlCallback(), "url_callback", RUOLO_CALLBACK);
				checkNonPrevisto(sa.isInvioPurposeId(), "invio_purpose_id", RUOLO_CALLBACK);
			}
			addParametro(sa!=null ? sa.getEntityNumber() : null,
					CostantiDB.MODIPA_PDND_ASYNC_ENTITY_NUMBER_MODALITA, CostantiDB.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME,
					CostantiDB.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME_DEFAULT, CostantiDB.MODIPA_PDND_ASYNC_ENTITY_NUMBER_NOME_QUERY_DEFAULT, pp);
		}
		addCodiciHttp(sa!=null ? sa.getCodiciHttpEsitoPositivo() : null, pp);
		return pp;
	}
	private static void addUrlCallback(OneOfFruizioneModIScambioAsincronoUrlCallback urlCallback, ProtocolProperties p) {
		if(urlCallback instanceof FruizioneModIScambioAsincronoUrlCallbackClient) {
			FruizioneModIScambioAsincronoUrlCallbackClient client = (FruizioneModIScambioAsincronoUrlCallbackClient) urlCallback;
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_CLIENT);
			addParametro(client, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_MODALITA, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME,
					CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME_DEFAULT, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_NOME_QUERY_DEFAULT, p);
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_CODIFICA,
					client.getCodifica()!=null ? client.getCodifica().toString() : CostantiDB.MODIPA_PDND_ASYNC_VALUE_CODIFICA_NONE);
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_OBBLIGATORIA, client.isObbligatoria()!=null && client.isObbligatoria().booleanValue());
		}
		else {
			// default: URL di invocazione dell'erogazione dell'API di callback
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE, CostantiDB.MODIPA_PDND_ASYNC_URL_CALLBACK_SORGENTE_VALUE_EROGAZIONE);
		}
	}
	private static void addParametro(ModIScambioAsincronoParametro parametro, String idModalita, String idNome, String nomeDefaultHeader, String nomeDefaultQuery, ProtocolProperties p) {
		boolean query = parametro!=null && ModIScambioAsincronoModalitaEnum.QUERY.equals(parametro.getModalita());
		p.addProperty(idModalita, query ? CostantiDB.MODIPA_PDND_ASYNC_VALUE_MODALITA_QUERY : CostantiDB.MODIPA_PDND_ASYNC_VALUE_MODALITA_HEADER);
		String nome = parametro!=null ? parametro.getNome() : null;
		if(nome==null || "".equals(nome.trim())) {
			// nome di default coerente con la modalità
			nome = query ? nomeDefaultQuery : nomeDefaultHeader;
		}
		p.addProperty(idNome, nome);
	}

	private static ModIScambioAsincronoCodificaEnum toCodifica(String codifica) {
		if(codifica==null || "".equals(codifica) || CostantiDB.MODIPA_PDND_ASYNC_VALUE_CODIFICA_DEFAULT.equals(codifica)) {
			return null;
		}
		return ModIScambioAsincronoCodificaEnum.fromValue(codifica);
	}
}
