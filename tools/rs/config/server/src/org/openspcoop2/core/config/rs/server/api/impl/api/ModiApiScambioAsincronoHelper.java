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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.ApiModI;
import org.openspcoop2.core.config.rs.server.model.ApiModIScambioAsincronoApiErogazioneDati;
import org.openspcoop2.core.config.rs.server.model.ApiModIScambioAsincronoCallback;
import org.openspcoop2.core.config.rs.server.model.ApiModIScambioAsincronoErogazioneDati;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaCustomEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioGenerazioneTokenEnum;
import org.openspcoop2.core.config.rs.server.model.ModIScambioAsincronoFaseEnum;
import org.openspcoop2.core.config.rs.server.model.ModIScambioAsincronoRuoloEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfApiModIScambioAsincrono;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * Gestione delle informazioni relative agli scambi di dati asincroni PDND nella configurazione ModI di API, risorse e azioni.
 *
 * Lo scambio asincrono viene abilitato nella sicurezza messaggio dell'API ('scambio_asincrono'), mentre la relativa configurazione
 * (ruolo dell'API e parametri) è riportata nella sezione 'scambio_asincrono' della configurazione ModI dell'API.
 * Alle risorse (REST) e alle azioni (SOAP) può essere associata la fase dello scambio ('fase_asincrona').
 * I controlli di coerenza (generazione del token PDND, fasi compatibili con il ruolo, API riferita) sono effettuati dalla validazione del profilo ModI.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ModiApiScambioAsincronoHelper {

	private ModiApiScambioAsincronoHelper() {}

	private static final String SEZIONE = "scambio_asincrono";
	private static final String PREFIX_SCAMBIO_ASINCRONO = "Lo scambio di dati asincrono";


	/* **** API **** */

	static void readApi(Map<String, AbstractProperty<?>> p, ApiModI apimodi) throws CoreException, DriverRegistroServiziException {

		Boolean asyncValue = ProtocolPropertiesHelper.getBooleanProperty(p, CostantiDB.MODIPA_PDND_ASYNC, false, true);
		boolean async = asyncValue!=null && asyncValue.booleanValue();
		if(apimodi.getSicurezzaMessaggio()!=null) {
			apimodi.getSicurezzaMessaggio().setScambioAsincrono(async);
		}
		if(!async) {
			return;
		}

		String ruolo = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_RUOLO, false);
		if(CostantiDB.MODIPA_PDND_ASYNC_RUOLO_VALUE_CALLBACK.equals(ruolo)) {
			ApiModIScambioAsincronoCallback callback = new ApiModIScambioAsincronoCallback();
			callback.setRuolo(ModIScambioAsincronoRuoloEnum.CALLBACK);
			String uri = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_API_CORRELATA, false);
			if(uri!=null && !"".equals(uri) && !CostantiDB.MODIPA_VALUE_UNDEFINED.equals(uri)) {
				IDAccordo idApi = IDAccordoFactory.getInstance().getIDAccordoFromUri(uri);
				ApiModIScambioAsincronoApiErogazioneDati api = new ApiModIScambioAsincronoApiErogazioneDati();
				api.setApiNome(idApi.getNome());
				api.setApiVersione(idApi.getVersione());
				callback.setApiErogazioneDati(api);
			}
			apimodi.setScambioAsincrono(callback);
		}
		else {
			ApiModIScambioAsincronoErogazioneDati eservice = new ApiModIScambioAsincronoErogazioneDati();
			eservice.setRuolo(ModIScambioAsincronoRuoloEnum.EROGAZIONE_DATI);
			eservice.setTempoMassimoRisposta(getLongProperty(p, CostantiDB.MODIPA_PDND_ASYNC_TEMPO_MAX_CALLBACK));
			eservice.setDurataDisponibilita(getLongProperty(p, CostantiDB.MODIPA_PDND_ASYNC_TEMPO_DISPONIBILITA));
			Boolean conferma = ProtocolPropertiesHelper.getBooleanProperty(p, CostantiDB.MODIPA_PDND_ASYNC_CONFERMA_RICEZIONE, false, true);
			eservice.setConfermaRecupero(conferma!=null && conferma.booleanValue());
			Long limite = getLongProperty(p, CostantiDB.MODIPA_PDND_ASYNC_LIMITE_ENTITA);
			eservice.setNumeroMassimoRisultati(limite!=null ? limite.intValue() : null);
			apimodi.setScambioAsincrono(eservice);
		}
	}
	private static Long getLongProperty(Map<String, AbstractProperty<?>> p, String key) throws CoreException {
		AbstractProperty<?> prop = ProtocolPropertiesHelper.getProperty(p, key, false);
		if(prop == null) {
			return null;
		}
		if(prop instanceof NumberProperty np) {
			return np.getValue();
		}
		throw new CoreException("Property "+key+" non è una NumberProperty:" + prop.getClass().getName());
	}

	static void addApi(ApiModI modi, IDSoggetto soggettoReferente, ProtocolProperties p) {

		ApiModISicurezzaMessaggio sicurezzaMessaggio = modi.getSicurezzaMessaggio();
		boolean async = sicurezzaMessaggio!=null && sicurezzaMessaggio.isScambioAsincrono()!=null && sicurezzaMessaggio.isScambioAsincrono().booleanValue();
		OneOfApiModIScambioAsincrono configurazione = modi.getScambioAsincrono();
		if(async && configurazione==null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Lo scambio di dati asincrono abilitato nella sicurezza messaggio richiede la configurazione '"+SEZIONE+"'");
		}
		if(!async && configurazione!=null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione '"+SEZIONE+"' richiede che lo scambio di dati asincrono sia abilitato nella sicurezza messaggio");
		}

		p.addProperty(CostantiDB.MODIPA_PDND_ASYNC, async);
		if(!async) {
			return;
		}

		// la console, con lo scambio asincrono abilitato, fissa i valori della sicurezza messaggio prima della validazione:
		// nella API i valori vengono invece forniti dal client e devono quindi essere verificati esplicitamente
		checkSicurezzaMessaggioApi(sicurezzaMessaggio);

		if(configurazione instanceof ApiModIScambioAsincronoCallback callback) {
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_RUOLO, CostantiDB.MODIPA_PDND_ASYNC_RUOLO_VALUE_CALLBACK);
			ApiModIScambioAsincronoApiErogazioneDati api = callback.getApiErogazioneDati();
			if(api==null || api.getApiNome()==null || api.getApiVersione()==null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Un'API con ruolo 'callback' richiede che sia indicata l'API 'erogazione dati' a cui la callback si riferisce (api_erogazione_dati)");
			}
			try {
				// il referente di un'API ModI è sempre il soggetto di default del profilo
				IDAccordo idApi = IDAccordoFactory.getInstance().getIDAccordoFromValues(api.getApiNome(), soggettoReferente, api.getApiVersione());
				p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_API_CORRELATA, IDAccordoFactory.getInstance().getUriFromIDAccordo(idApi));
			}catch(DriverRegistroServiziException e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
		}
		else if(configurazione instanceof ApiModIScambioAsincronoErogazioneDati eservice) {
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_RUOLO, CostantiDB.MODIPA_PDND_ASYNC_RUOLO_VALUE_ESERVICE);
			// valori obbligatori: se assenti la validazione del profilo ModI segnala il campo mancante
			addLongProperty(p, CostantiDB.MODIPA_PDND_ASYNC_TEMPO_MAX_CALLBACK, eservice.getTempoMassimoRisposta());
			addLongProperty(p, CostantiDB.MODIPA_PDND_ASYNC_TEMPO_DISPONIBILITA, eservice.getDurataDisponibilita());
			p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_CONFERMA_RICEZIONE, eservice.isConfermaRecupero()!=null && eservice.isConfermaRecupero().booleanValue());
			addLongProperty(p, CostantiDB.MODIPA_PDND_ASYNC_LIMITE_ENTITA, eservice.getNumeroMassimoRisultati()!=null ? eservice.getNumeroMassimoRisultati().longValue() : null);
		}
	}
	private static void checkSicurezzaMessaggioApi(ApiModISicurezzaMessaggio sicurezzaMessaggio) {
		if(!ModISicurezzaMessaggioGenerazioneTokenEnum.PDND.equals(sicurezzaMessaggio.getGenerazioneToken())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Lo scambio di dati asincrono richiede che nella sicurezza messaggio sia selezionata la generazione del token 'Authorization PDND' (generazione_token: pdnd)");
		}
		if(ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA.equals(sicurezzaMessaggio.getApplicabilita())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(PREFIX_SCAMBIO_ASINCRONO+" richiede che la sicurezza messaggio sia applicata alla richiesta");
		}
		if(ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM.equals(sicurezzaMessaggio.getApplicabilita()) &&
				(sicurezzaMessaggio.getApplicabilitaCustom()==null ||
				!ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO.equals(sicurezzaMessaggio.getApplicabilitaCustom().getRichiesta()))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(PREFIX_SCAMBIO_ASINCRONO+" richiede che lo stato della sicurezza messaggio nella richiesta sia 'abilitato'");
		}
	}
	private static void addLongProperty(ProtocolProperties p, String id, Long value) {
		if(value!=null) {
			p.addProperty(id, value.longValue());
		}
	}

	/**
	 * Lo scambio di dati asincrono è configurabile solamente a livello di API e non nella sicurezza messaggio ridefinita di una risorsa o azione
	 */
	static void checkSicurezzaMessaggioOperazione(ApiModISicurezzaMessaggio sicurezzaMessaggio) {
		if(sicurezzaMessaggio!=null && sicurezzaMessaggio.isScambioAsincrono()!=null && sicurezzaMessaggio.isScambioAsincrono().booleanValue()) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Lo scambio di dati asincrono è configurabile solamente nella sicurezza messaggio dell'API");
		}
	}


	/* **** Risorse e azioni **** */

	static ModIScambioAsincronoFaseEnum readFase(Map<String, AbstractProperty<?>> p) throws CoreException {
		String fase = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_PDND_ASYNC_FASE, false);
		if(fase==null || "".equals(fase) || CostantiDB.MODIPA_VALUE_UNDEFINED.equals(fase)) {
			return null;
		}
		return ModIScambioAsincronoFaseEnum.fromValue(fase);
	}

	static void addFase(ModIScambioAsincronoFaseEnum fase, ProtocolProperties p) {
		p.addProperty(CostantiDB.MODIPA_PDND_ASYNC_FASE, fase!=null ? fase.toString() : CostantiDB.MODIPA_VALUE_UNDEFINED);
	}
}
