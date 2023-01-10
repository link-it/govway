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

package org.openspcoop2.core.monitor.rs.server.api.impl.utils;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiBase;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiSoggetti;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneSoggetto;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteIdAutenticato;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteIndirizzoIP;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteTokenClaimSoggetto;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.ListaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.OneOfRicercaIntervalloTemporaleMittente;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseTransazione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIdApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIntervalloTemporale;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.FiltroRicercaId;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;

/**
 * TransazioniHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TransazioniHelper {
	
	public static boolean isEmpty(IDSoggetto _this) {
		return StringUtils.isEmpty(_this.getTipo()) || StringUtils.isEmpty(_this.getNome()); 
	}

	public static final void overrideFiltroApiBase(FiltroApiBase filtro_api, String azione, IDSoggetto erogatore, TransazioniSearchForm search, MonitoraggioEnv env) {
		
		search.setNomeAzione(azione);
		
		if (filtro_api == null)
			return;
		
		if ( !StringUtils.isEmpty(filtro_api.getNome()) || filtro_api.getVersione() != null || !StringUtils.isEmpty(azione) || !StringUtils.isEmpty(filtro_api.getTipo())) {
			
			if (StringUtils.isEmpty(filtro_api.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Filtro Api incompleto. Specificare il nome della API");
			}
						
			if (erogatore == null || TransazioniHelper.isEmpty(erogatore)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Filtro Api incompleto. Specificare il Soggetto Erogatore (Nelle fruizioni è il soggetto remoto)");
			}
			
			if (filtro_api.getVersione() == null) {
				filtro_api.setVersione(1);
			}
			
			if (filtro_api.getTipo() == null) {
				try {
					IProtocolConfiguration protocolConf = env.protocolFactoryMgr
							.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration();
					ServiceBinding defaultBinding = protocolConf.getDefaultServiceBindingConfiguration(null)
							.getDefaultBinding();
					filtro_api.setTipo(protocolConf.getTipoServizioDefault(defaultBinding));
				} catch (Exception e) {
					throw FaultCode.ERRORE_INTERNO
							.toException("Impossibile determinare il tipo del servizio: " + e.getMessage());
				}

			}
			
			String uri = ReportisticaHelper.buildNomeServizioForOverride(filtro_api.getNome(), filtro_api.getTipo(), filtro_api.getVersione(), Optional.of(erogatore));
			if (uri == null) {
				throw FaultCode.ERRORE_INTERNO.toException("Non sono riuscito a costruire la URI dell'Id Servizio");
			}
			
			search.setNomeServizio(uri);			
		}
	}

	public static final void overrideFiltroFruizione(FiltroApiSoggetti filtro, String azione,
			TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null)
			return;

		TransazioniHelper.overrideFiltroApiBase(filtro, azione, new IDSoggetto(env.tipoSoggetto, filtro.getErogatore()), search, env);
	}
	
	public static final void overrideFiltroQualsiasi(FiltroApiSoggetti filtro, String azione,
			TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null)
			return;

		TransazioniHelper.overrideFiltroApiBase(filtro, azione, new IDSoggetto(env.tipoSoggetto, filtro.getErogatore()), search, env);
		if (!StringUtils.isEmpty(filtro.getSoggettoRemoto()))
			search.setTipoNomeTrafficoPerSoggetto(new IDSoggetto(env.tipoSoggetto, filtro.getSoggettoRemoto()).toString());	
	}
	
	public static final void overrideFiltroRicercaId(FiltroRicercaId filtro, TransazioniSearchForm search,
			MonitoraggioEnv env, String tipoRiconoscimento) {
		if (filtro == null)
			return;

		search.setMittenteMatchingType(
				(BooleanUtils.isTrue(filtro.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
		search.setMittenteCaseSensitiveType(
				(BooleanUtils.isTrue(filtro.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE
						: CaseSensitiveMatch.INSENSITIVE).toString());
		search.setValoreRiconoscimento(filtro.getId());
		search.setRiconoscimento(tipoRiconoscimento);
	}

	public static final void overrideFiltroMittenteIdApplicativo(FiltroMittenteIdAutenticato filtro,
			TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null)
			return;
		TransazioniHelper.overrideFiltroRicercaId(filtro, search, env, Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO);
		search.setAutenticazione(Enums.toTipoAutenticazione.get(filtro.getAutenticazione()).toString());
	}
	
	public static final void overrideFiltroMittenteIndirizzoIP(FiltroMittenteIndirizzoIP filtro,
			TransazioniSearchForm search, MonitoraggioEnv env) {
		if (filtro == null)
			return;
		TransazioniHelper.overrideFiltroRicercaId(filtro, search, env, Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP);
		if(filtro.getTipo()!=null) {
			search.setClientAddressMode(Enums.toTipoIndirizzzoIP.get(filtro.getTipo()));
		}
	}

	private static void setEsitoCodice(EsitoTransazioneFullSearchEnum tipo , FiltroEsito filtro, TransazioniSearchForm search) {
		if(filtro.getCodice()!=null) {
			search.setEsitoDettaglio(filtro.getCodice());
		}
		else if(filtro.getCodici()!=null && !filtro.getCodici().isEmpty()) {
			if(filtro.getCodici().size()==1) {
				search.setEsitoDettaglio(filtro.getCodici().get(0));
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Con il tipo di esito indicato '"+tipo.toString()+"' può essere indicato solamente un codice");
			}
		}
	}
	public static final void overrideFiltroEsito(FiltroEsito filtro, TransazioniSearchForm search,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;

		if (filtro != null) {
			
			EsitoTransazioneFullSearchEnum tipo = (filtro.getTipo() != null) ? filtro.getTipo() : EsitoTransazioneFullSearchEnum.QUALSIASI;
			
			switch (tipo) {
			case QUALSIASI:
				search.setEsitoGruppo(EsitoUtils.ALL_VALUE);
				setEsitoCodice(tipo, filtro, search);
				if(filtro.isEscludiScartate()!=null) {
					search.setEscludiRichiesteScartate(filtro.isEscludiScartate());
				}
				break;
			case OK:
				search.setEsitoGruppo(EsitoUtils.ALL_OK_VALUE);
				setEsitoCodice(tipo, filtro, search);
				break;
			case FAULT:
				search.setEsitoGruppo(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
				setEsitoCodice(tipo, filtro, search);
				break;
			case FALLITE:
				search.setEsitoGruppo(EsitoUtils.ALL_ERROR_VALUE);
				setEsitoCodice(tipo, filtro, search);
				if(filtro.isEscludiScartate()!=null) {
					search.setEscludiRichiesteScartate(filtro.isEscludiScartate());
				}
				break;
			case FALLITE_E_FAULT:
				search.setEsitoGruppo(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
				setEsitoCodice(tipo, filtro, search);
				if(filtro.isEscludiScartate()!=null) {
					search.setEscludiRichiesteScartate(filtro.isEscludiScartate());
				}
				break;
			case ERRORI_CONSEGNA:
				search.setEsitoGruppo(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE);
				setEsitoCodice(tipo, filtro, search);
				break;
			case RICHIESTE_SCARTATE:
				search.setEsitoGruppo(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE);
				setEsitoCodice(tipo, filtro, search);
				break;
			case PERSONALIZZATO:
				search.setEsitoGruppo(EsitoUtils.ALL_PERSONALIZZATO_VALUE);
				
				if(filtro.getCodice()==null && (filtro.getCodici()==null || filtro.getCodici().isEmpty())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Con il tipo di esito indicato '"+tipo.toString()+"' deve essere indicato almeno un codice");
				}
				List<Integer> dettaglioEsito = filtro.getCodici();
				if(dettaglioEsito==null || dettaglioEsito.isEmpty()) {
					dettaglioEsito = new ArrayList<Integer>();
					dettaglioEsito.add(filtro.getCodice());
				}
				search.setEsitoDettaglioPersonalizzato(dettaglioEsito.toArray(new Integer[1]));

				break;
			}
		}

	}

	public static final void overrideRicercaBaseTransazione(RicercaBaseTransazione body, TransazioniSearchForm search,
			MonitoraggioEnv env) throws Exception {
		if (body == null)
			return;
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}
		
		search.setGruppo(body.getTag());

		if(body.getApi()!=null && body.getApi().getApiImplementata()!=null) {
			search.setApi(ReportisticaHelper.toUriApiImplementata(body.getApi().getApiImplementata(), env));
		}
		
		switch (body.getTipo()) {
		case EROGAZIONE:
			IDSoggetto idSoggettoLocale = new IDSoggetto(env.tipoSoggetto, env.nomeSoggettoLocale);
			TransazioniHelper.overrideFiltroApiBase(body.getApi(), body.getAzione(), idSoggettoLocale, search, env);
			break;
		case FRUIZIONE:
			TransazioniHelper.overrideFiltroFruizione(body.getApi(),body.getAzione(), search, env);
			break;
		case QUALSIASI:
			TransazioniHelper.overrideFiltroQualsiasi(body.getApi(), body.getAzione(), search, env);
			break;
		}

		TransazioniHelper.overrideFiltroEsito(body.getEsito(), search, env);
		search.setEvento(body.getEvento());
		search.setClusterId(body.getIdCluster());
	}
	
	
	public static final void overrideFiltroMittente(RicercaIntervalloTemporale body, TransazioniSearchForm search,
			MonitoraggioEnv env) {
		if (body == null)
			return;

		// Filtraggio Mittente
		
		OneOfRicercaIntervalloTemporaleMittente fMittente = body.getMittente();
		if (fMittente == null)
			return;
		
		final String tipo_soggetto = env.tipoSoggetto;
		switch (body.getTipo()) {
		case FRUIZIONE: {
			
			switch (fMittente.getIdentificazione()) {
			
			case EROGAZIONE_SOGGETTO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
			}
			
			case FRUIZIONE_APPLICATIVO: {
				
				if(! (fMittente instanceof FiltroMittenteFruizioneApplicativo)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteFruizioneApplicativo.class.getName()+")");
				}
				FiltroMittenteFruizioneApplicativo fAppl = (FiltroMittenteFruizioneApplicativo) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
				if(fAppl.getTipoIdentificazioneApplicativo()!=null) {
					switch (fAppl.getTipoIdentificazioneApplicativo()) {
					case TRASPORTO:
						search.setIdentificazione(Costanti.IDENTIFICAZIONE_TRASPORTO_KEY);
						break;
					case TOKEN:
						search.setIdentificazione(Costanti.IDENTIFICAZIONE_TOKEN_KEY);
						break;
					}
				}
				else {
					search.setIdentificazione(Costanti.IDENTIFICAZIONE_TRASPORTO_KEY);
				}
				search.setServizioApplicativo(fAppl.getApplicativo());
				
				search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, env.nomeSoggettoLocale).toString());
				
				break;
			}
			
			case EROGAZIONE_APPLICATIVO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
						+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
			}
			
			case IDENTIFICATIVO_AUTENTICATO: {
				
				if(! (fMittente instanceof FiltroMittenteIdAutenticato)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteIdAutenticato.class.getName()+")");
				}
				FiltroMittenteIdAutenticato fIdAutenticato = (FiltroMittenteIdAutenticato) fMittente;
				TransazioniHelper.overrideFiltroMittenteIdApplicativo(fIdAutenticato, search, env);
				break;
			}

			case EROGAZIONE_TOKEN_INFO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
						+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
			}
			
			case TOKEN_INFO: {
				if(! (fMittente instanceof FiltroMittenteTokenClaim)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaim.class.getName()+")");
				}
				FiltroMittenteTokenClaim fClaim = (FiltroMittenteTokenClaim) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				search.setTokenClaim(Enums.toTipoCredenzialeMittente(fClaim.getClaim()).toString());
				search.setMittenteMatchingType( (BooleanUtils.isTrue(fClaim.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
				search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(fClaim.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString());
				search.setValoreRiconoscimento(fClaim.getId());
				break;
			}
				
			case INDIRIZZO_IP: {
				
				if(! (fMittente instanceof FiltroMittenteIndirizzoIP)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteIndirizzoIP.class.getName()+")");
				}
				FiltroMittenteIndirizzoIP fIndirizzoIP = (FiltroMittenteIndirizzoIP) fMittente;
				TransazioniHelper.overrideFiltroMittenteIndirizzoIP(fIndirizzoIP, search, env);
				break;
			}
				
			}
			
			break;
		}

		case EROGAZIONE: {
			
			switch (fMittente.getIdentificazione()) {

			case EROGAZIONE_SOGGETTO: {
				
				if(! (fMittente instanceof FiltroMittenteErogazioneSoggetto)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteErogazioneSoggetto.class.getName()+")");
				}
				FiltroMittenteErogazioneSoggetto fSogg = (FiltroMittenteErogazioneSoggetto) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO);
				search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, fSogg.getSoggetto()).toString());
				break;
			}
			
			case FRUIZIONE_APPLICATIVO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
						+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.EROGAZIONE.toString()+"'");
			}
			
			case EROGAZIONE_APPLICATIVO: {
				
				if(! (fMittente instanceof FiltroMittenteErogazioneApplicativo)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteErogazioneApplicativo.class.getName()+")");
				}
				FiltroMittenteErogazioneApplicativo fAppl = (FiltroMittenteErogazioneApplicativo) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
				if(fAppl.getTipoIdentificazioneApplicativo()!=null) {
					switch (fAppl.getTipoIdentificazioneApplicativo()) {
					case TRASPORTO:
						search.setIdentificazione(Costanti.IDENTIFICAZIONE_TRASPORTO_KEY);
						break;
					case TOKEN:
						search.setIdentificazione(Costanti.IDENTIFICAZIONE_TOKEN_KEY);
						break;
					}
				}
				else {
					search.setIdentificazione(Costanti.IDENTIFICAZIONE_TRASPORTO_KEY);
				}
				search.setServizioApplicativo(fAppl.getApplicativo());
				search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, fAppl.getSoggetto()).toString());
				break;
			}
			case IDENTIFICATIVO_AUTENTICATO: {
				
				if(! (fMittente instanceof FiltroMittenteIdAutenticato)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteIdAutenticato.class.getName()+")");
				}
				FiltroMittenteIdAutenticato fIdAutenticato = (FiltroMittenteIdAutenticato) fMittente;
				TransazioniHelper.overrideFiltroMittenteIdApplicativo(fIdAutenticato, search, env);
				break;
			}

			case EROGAZIONE_TOKEN_INFO: {
				
				if(! (fMittente instanceof FiltroMittenteTokenClaimSoggetto)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaimSoggetto.class.getName()+")");
				}
				FiltroMittenteTokenClaimSoggetto fClaim = (FiltroMittenteTokenClaimSoggetto) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				search.setTokenClaim(Enums.toTipoCredenzialeMittente(fClaim.getClaim()).toString());
				search.setMittenteMatchingType( (BooleanUtils.isTrue(fClaim.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
				search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(fClaim.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString());
				search.setValoreRiconoscimento(fClaim.getId());
				if (!StringUtils.isEmpty(fClaim.getSoggetto()))
					search.setTipoNomeMittente(new IDSoggetto(tipo_soggetto, fClaim.getSoggetto()).toString());
				break;
			}
			
			case TOKEN_INFO: {
				
				if(! (fMittente instanceof FiltroMittenteTokenClaim)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaim.class.getName()+")");
				}
				FiltroMittenteTokenClaim fClaim = (FiltroMittenteTokenClaim) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				search.setTokenClaim(Enums.toTipoCredenzialeMittente(fClaim.getClaim()).toString());
				search.setMittenteMatchingType( (BooleanUtils.isTrue(fClaim.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
				search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(fClaim.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString());
				search.setValoreRiconoscimento(fClaim.getId());
				break;
			}
			
			case INDIRIZZO_IP: {

				if(! (fMittente instanceof FiltroMittenteIndirizzoIP)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteIndirizzoIP.class.getName()+")");
				}
				FiltroMittenteIndirizzoIP fIndirizzoIP = (FiltroMittenteIndirizzoIP) fMittente;
				TransazioniHelper.overrideFiltroMittenteIndirizzoIP(fIndirizzoIP, search, env);
				break;
			}
			
			}
			break;
		}
		
		case QUALSIASI: {

			switch (fMittente.getIdentificazione()) {
			
			case EROGAZIONE_SOGGETTO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
			}
			
			case FRUIZIONE_APPLICATIVO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
						+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
			}
			
			case EROGAZIONE_APPLICATIVO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
						+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
			}
			
			case IDENTIFICATIVO_AUTENTICATO: {
				
				if(! (fMittente instanceof FiltroMittenteIdAutenticato)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteIdAutenticato.class.getName()+")");
				}
				FiltroMittenteIdAutenticato fIdAutenticato = (FiltroMittenteIdAutenticato) fMittente;
				TransazioniHelper.overrideFiltroMittenteIdApplicativo(fIdAutenticato, search, env);
				break;
			}

			case EROGAZIONE_TOKEN_INFO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
						+ "non utilizzabile in una ricerca di transazioni con il criterio ruolo impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
			}
			
			case TOKEN_INFO: {
				
				if(! (fMittente instanceof FiltroMittenteTokenClaim)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaim.class.getName()+")");
				}
				FiltroMittenteTokenClaim fClaim = (FiltroMittenteTokenClaim) fMittente;
				search.setRiconoscimento(Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				search.setTokenClaim(Enums.toTipoCredenzialeMittente(fClaim.getClaim()).toString());
				search.setMittenteMatchingType( (BooleanUtils.isTrue(fClaim.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
				search.setMittenteCaseSensitiveType( (BooleanUtils.isTrue(fClaim.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString());
				search.setValoreRiconoscimento(fClaim.getId());
				break;
			}
			
			case INDIRIZZO_IP: {
				
				if(! (fMittente instanceof FiltroMittenteIndirizzoIP)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+fMittente.getIdentificazione().toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+fMittente.getClass().getName()+"' (atteso: "+FiltroMittenteIndirizzoIP.class.getName()+")");
				}
				FiltroMittenteIndirizzoIP fIndirizzoIP = (FiltroMittenteIndirizzoIP) fMittente;
				TransazioniHelper.overrideFiltroMittenteIndirizzoIP(fIndirizzoIP, search, env);
				break;
			}
			
			}
			break;
		}
		
		}
	}

	/**
	 * Effettua la ricerca delle transazioni dato un oggetto TransazioniSearchForm correttamente popolato
	 */
	public static final ListaTransazioni searchTransazioni(TransazioniSearchForm search, Integer offset, Integer limit,
			String sort, MonitoraggioEnv env) throws UtilsException, InstantiationException, IllegalAccessException {
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		try {
			connection = dbManager.getConnectionTracce();
			ServerProperties serverProperties = ServerProperties.getInstance();
			ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesTracce();
			TransazioniService transazioniService = new TransazioniService(connection, true, smp,
					LoggerProperties.getLoggerDAO());
			transazioniService.setSearch(search);

			List<TransazioneBean> listTransazioniDB = transazioniService.findAll(Converter.toOffset(offset),
					Converter.toLimit(limit), Converter.toSortOrder(sort), Converter.toSortField(sort));
			if(listTransazioniDB!=null && !listTransazioniDB.isEmpty()) {
				for (TransazioneBean transazioneBean : listTransazioniDB) {
					if(transazioneBean.getGruppiRawValue()!=null && !"".equals(transazioneBean.getGruppiRawValue())) {
						try {
							transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteGruppi(transazioneBean, transazioneBean);
						}catch(Exception e) {
							throw new UtilsException(e.getMessage(),e);
						}
					}
					
					// finisce in principal
					if(transazioneBean.getTrasportoMittenteLabel()==null) {
						if(transazioneBean.getTrasportoMittente()!=null && !"".equals(transazioneBean.getTrasportoMittente())) {
							try {
								transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTrasporto(transazioneBean, transazioneBean);
							}catch(Exception e) {
								throw new UtilsException(e.getMessage(),e);
							}
						}
					}
					
					// finisce in utente
					if(transazioneBean.getTokenUsernameLabel()==null) {
						if(transazioneBean.getTokenUsername()!=null && !"".equals(transazioneBean.getTokenUsername())) {
							try {
								transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenUsername(transazioneBean, transazioneBean);
							}catch(Exception e) {
								throw new UtilsException(e.getMessage(),e);
							}
						}
					}
					
					// finisce in client
					if(transazioneBean.getTokenClientIdLabel()==null) {
						if(transazioneBean.getTokenClientId()!=null && !"".equals(transazioneBean.getTokenClientId())) {
							try {
								transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteTokenClientID(transazioneBean, transazioneBean);
							}catch(Exception e) {
								throw new UtilsException(e.getMessage(),e);
							}
						}
					}
					
					// finisce in indirizzoClient e indirizzoClientInoltrato
					if(transazioneBean.getTransportClientAddressLabel()==null && transazioneBean.getSocketClientAddressLabel()==null) {
						if(transazioneBean.getClientAddress()!=null && !"".equals(transazioneBean.getClientAddress())) {
							try {
								transazioniService.normalizeInfoTransazioniFromCredenzialiMittenteClientAddress(transazioneBean, transazioneBean);
							}catch(Exception e) {
								throw new UtilsException(e.getMessage(),e);
							}
						}
					}
				}
			}
			
			ListaTransazioni ret = ListaUtils.costruisciLista(env.context.getUriInfo(),
					Converter.toOffset(offset), Converter.toLimit(limit),
					listTransazioniDB != null ? listTransazioniDB.size() : 0, ListaTransazioni.class);

			if (serverProperties.isFindall404() && (listTransazioniDB == null || listTransazioniDB.isEmpty()))
				throw FaultCode.NOT_FOUND
						.toException("Nessuna transazione trovata corrispondente ai criteri di ricerca");

			if (listTransazioniDB != null && !listTransazioniDB.isEmpty()) {
				listTransazioniDB.forEach(transazioneDB -> {
					try {
						ret.addItemsItem(Converter.toItemTransazione(transazioneDB, env.log));
					} catch (Exception e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				});
			}

			env.context.getLogger().info("Invocazione completata con successo");
			return ret;
		} finally {
			dbManager.releaseConnectionTracce(connection);
		}
	}
	
	
	public static final ListaTransazioni findAllTransazioniByIdApplicativo(RicercaIdApplicativo body, MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		TransazioniSearchForm search = searchFormUtilities.getIdApplicativoSearchForm(env.context, env.profilo, env.nomeSoggettoLocale, 
				body.getTipo(), body.getIntervalloTemporale().getDataInizio(), body.getIntervalloTemporale().getDataFine());
		
		TransazioniHelper.overrideRicercaBaseTransazione(body, search, env);

		FiltroRicercaId filtro = body.getIdApplicativo();
		search.setCorrelazioneApplicativaCaseSensitiveType((BooleanUtils.isTrue(filtro.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
		search.setCorrelazioneApplicativaMatchingType((BooleanUtils.isTrue(filtro.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
		search.setIdCorrelazioneApplicativa(filtro.getId());			
		
		ListaTransazioni ret = TransazioniHelper.searchTransazioni(search, body.getOffset(), body.getLimit(), body.getSort(), env);
		return ret;
	}
	
	
	public static final ListaTransazioni findAllTransazioni(RicercaIntervalloTemporale body, MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		TransazioniSearchForm search = searchFormUtilities.getAndamentoTemporaleSearchForm(env.context, env.profilo, env.nomeSoggettoLocale, 
				body.getTipo(), body.getIntervalloTemporale().getDataInizio(), body.getIntervalloTemporale().getDataFine());
		
		TransazioniHelper.overrideRicercaBaseTransazione(body, search, env);
		TransazioniHelper.overrideFiltroMittente(body, search, env);
		
		return TransazioniHelper.searchTransazioni(search, body.getOffset(), body.getLimit(), body.getSort(), env);	
	}
}
