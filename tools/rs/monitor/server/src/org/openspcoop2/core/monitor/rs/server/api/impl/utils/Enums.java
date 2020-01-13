/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.constants.TipoAutenticazione;

import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.OccupazioneBandaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TempoMedioRispostaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteErogazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteErogazioneSoggettoRemotoEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteFruizioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteIdentificativoAutenticatoEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteIndirizzoIPEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteQualsiasiEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimEnum;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;

/**
 * Enums
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Enums {

	public static final <T1, T2> Map<T1, T2> dualizeMap(Map<T2, T1> map) {
		HashMap<T1, T2> ret = new HashMap<T1, T2>();

		map.forEach((t2, t1) -> ret.put(t1, t2));

		return ret;
	}

	public static final Map<DiagnosticoSeveritaEnum, TipoSeverita> toTipoSeverita = new HashMap<>();
	static {
		toTipoSeverita.put(DiagnosticoSeveritaEnum.DEBUG, TipoSeverita.DEBUG);
		toTipoSeverita.put(DiagnosticoSeveritaEnum.ERROR, TipoSeverita.ERROR);
		toTipoSeverita.put(DiagnosticoSeveritaEnum.FATAL, TipoSeverita.FATAL);
		toTipoSeverita.put(DiagnosticoSeveritaEnum.INFO, TipoSeverita.INFO);
		toTipoSeverita.put(DiagnosticoSeveritaEnum.WARNING, TipoSeverita.WARN);
	}
	public static final Map<TipoSeverita, DiagnosticoSeveritaEnum> toDiagnosticoSeverita = Enums
			.dualizeMap(toTipoSeverita);

	public static final Map<EsitoTransazioneFullSearchEnum, Integer> toEsitoGruppo = new HashMap<>();
	static {
		toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.OK, EsitoUtils.ALL_OK_VALUE);
		toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.FAULT, EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
		toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.ERROR, EsitoUtils.ALL_ERROR_VALUE);
		toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.ERROR_OR_FAULT, EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
		toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.PERSONALIZZATO, EsitoUtils.ALL_PERSONALIZZATO_VALUE);
	}

	public static final Map<TipoFiltroMittenteIdentificativoAutenticatoEnum, TipoAutenticazione> toTipoAutenticazione = new HashMap<>();
	static {
		toTipoAutenticazione.put(TipoFiltroMittenteIdentificativoAutenticatoEnum.BASIC, TipoAutenticazione.BASIC);
		toTipoAutenticazione.put(TipoFiltroMittenteIdentificativoAutenticatoEnum.PRINCIPAL, TipoAutenticazione.PRINCIPAL);
		toTipoAutenticazione.put(TipoFiltroMittenteIdentificativoAutenticatoEnum.SSL, TipoAutenticazione.SSL);
	}
	
	public static final Map<TipoFiltroMittenteIndirizzoIPEnum, String> toTipoIndirizzzoIP = new HashMap<>();
	static {
		toTipoIndirizzzoIP.put(TipoFiltroMittenteIndirizzoIPEnum.CLIENT_IP, org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_SOCKET);
		toTipoIndirizzzoIP.put(TipoFiltroMittenteIndirizzoIPEnum.X_FORWARDED_FOR, org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO);
	}

	public static final Map<TokenClaimEnum, String> toTokenClaim = new HashMap<>();
	static {
		toTokenClaim.put(TokenClaimEnum.CLIENT_ID, CostantiExporter.CLAIM_CLIENT_ID);
		toTokenClaim.put(TokenClaimEnum.EMAIL, CostantiExporter.CLAIM_EMAIL);
		toTokenClaim.put(TokenClaimEnum.ISSUER, CostantiExporter.CLAIM_ISSUER);
		toTokenClaim.put(TokenClaimEnum.SUBJECT, CostantiExporter.CLAIM_SUBJECT);
		toTokenClaim.put(TokenClaimEnum.USERNAME, CostantiExporter.CLAIM_USERNAME);
	}

	public static final Map<FormatoReportEnum, String> toTipoFormato = new HashMap<>();
	static {
		toTipoFormato.put(FormatoReportEnum.CSV, CostantiExporter.TIPO_FORMATO_CSV);
		toTipoFormato.put(FormatoReportEnum.JSON, CostantiExporter.TIPO_FORMATO_JSON);
		toTipoFormato.put(FormatoReportEnum.PDF, CostantiExporter.TIPO_FORMATO_PDF);
		// toTipoFormato.put(FormatoReportEnum.PNG, CostantiExporter.TIPO_FORMATO_PNG);
		toTipoFormato.put(FormatoReportEnum.XLS, CostantiExporter.TIPO_FORMATO_XLS);
		toTipoFormato.put(FormatoReportEnum.XML, CostantiExporter.TIPO_FORMATO_XML);

	}

	public static final Map<TipoReportEnum, org.openspcoop2.core.statistiche.constants.TipoReport> toTipoReport = new HashMap<>();
	static {
		toTipoReport.put(TipoReportEnum.BAR, org.openspcoop2.core.statistiche.constants.TipoReport.BAR_CHART);
		toTipoReport.put(TipoReportEnum.LINE, org.openspcoop2.core.statistiche.constants.TipoReport.LINE_CHART);
		toTipoReport.put(TipoReportEnum.PIE, org.openspcoop2.core.statistiche.constants.TipoReport.PIE_CHART);
		toTipoReport.put(TipoReportEnum.TABLE, org.openspcoop2.core.statistiche.constants.TipoReport.TABELLA);
	}

	public static final Map<TipoInformazioneReportEnum, TipoVisualizzazione> toTipoVisualizzazione = new HashMap<>();
	static {
		toTipoVisualizzazione.put(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI,
				TipoVisualizzazione.NUMERO_TRANSAZIONI);
		toTipoVisualizzazione.put(TipoInformazioneReportEnum.OCCUPAZIONE_BANDA,
				TipoVisualizzazione.DIMENSIONE_TRANSAZIONI);
		toTipoVisualizzazione.put(TipoInformazioneReportEnum.TEMPO_MEDIO_RISPOSTA,
				TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA);
	}

	public static final Map<OccupazioneBandaEnum, TipoBanda> toTipoBanda = new HashMap<>();
	static {
		toTipoBanda.put(OccupazioneBandaEnum.COMPLESSIVA, TipoBanda.COMPLESSIVA);
		toTipoBanda.put(OccupazioneBandaEnum.ESTERNA, TipoBanda.ESTERNA);
		toTipoBanda.put(OccupazioneBandaEnum.INTERNA, TipoBanda.INTERNA);
	}

	public static final Map<TempoMedioRispostaEnum, TipoLatenza> toTipoLatenza = new HashMap<>();
	static {
		toTipoLatenza.put(TempoMedioRispostaEnum.GATEWAY, TipoLatenza.LATENZA_PORTA);
		toTipoLatenza.put(TempoMedioRispostaEnum.SERVIZIO, TipoLatenza.LATENZA_SERVIZIO);
		toTipoLatenza.put(TempoMedioRispostaEnum.TOTALE, TipoLatenza.LATENZA_TOTALE);
	}

	public static final Map<TokenClaimEnum, String> toClaim = new HashMap<>();
	static {
		toClaim.put(TokenClaimEnum.CLIENT_ID, CostantiExporter.CLAIM_CLIENT_ID);
		toClaim.put(TokenClaimEnum.EMAIL, CostantiExporter.CLAIM_EMAIL);
		toClaim.put(TokenClaimEnum.ISSUER, CostantiExporter.CLAIM_ISSUER);
		toClaim.put(TokenClaimEnum.SUBJECT, CostantiExporter.CLAIM_SUBJECT);
		toClaim.put(TokenClaimEnum.USERNAME, CostantiExporter.CLAIM_USERNAME);
	}

	public static final Map<UnitaTempoReportEnum, StatisticType> toStatisticType = new HashMap<>();
	static {
		toStatisticType.put(UnitaTempoReportEnum.GIORNALIERO, StatisticType.GIORNALIERA);
		toStatisticType.put(UnitaTempoReportEnum.MENSILE, StatisticType.MENSILE);
		toStatisticType.put(UnitaTempoReportEnum.ORARIO, StatisticType.ORARIA);
		toStatisticType.put(UnitaTempoReportEnum.SETTIMANALE, StatisticType.SETTIMANALE);
	}

	public static final Map<TransazioneRuoloEnum, String> toTipologiaRuoloTransazione = new HashMap<>();
	static {
		toTipologiaRuoloTransazione.put(TransazioneRuoloEnum.EROGAZIONE, CostantiExporter.TIPOLOGIA_EROGAZIONE);
		toTipologiaRuoloTransazione.put(TransazioneRuoloEnum.FRUIZIONE, CostantiExporter.TIPOLOGIA_FRUIZIONE);
	}
	
	public static final Map<FiltroRicercaRuoloTransazioneEnum, String> toTipologiaFiltroRicercaRuoloTransazioneEnum = new HashMap<>();
	static {
		toTipologiaFiltroRicercaRuoloTransazioneEnum.put(FiltroRicercaRuoloTransazioneEnum.EROGAZIONE, CostantiExporter.TIPOLOGIA_EROGAZIONE);
		toTipologiaFiltroRicercaRuoloTransazioneEnum.put(FiltroRicercaRuoloTransazioneEnum.FRUIZIONE, CostantiExporter.TIPOLOGIA_FRUIZIONE);
		toTipologiaFiltroRicercaRuoloTransazioneEnum.put(FiltroRicercaRuoloTransazioneEnum.QUALSIASI, CostantiExporter.TIPOLOGIA_EROGAZIONE_FRUIZIONE);
	}
	
	public static final String toTipoRicercaMittente(TipoFiltroMittenteErogazioneSoggettoRemotoEnum v) {
		if (v == null) return null;
		
		switch (v) {
		case IDENTIFICATIVO_AUTENTICATO: return CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO;
		case TOKEN_INFO: return CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO;
		case INDIRIZZO_IP: return CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP;
		default: return null;
		}
	}
	
	public static final String toTipoRicercaMittente(TipoFiltroMittenteErogazioneEnum v) {
		if (v == null) return null;
		
		switch (v) {
		case APPLICATIVO: return CostantiExporter.TIPO_RICERCA_MITTENTE_APPLICATIVO;
		case IDENTIFICATIVO_AUTENTICATO: return CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO;
		case SOGGETTO: return CostantiExporter.TIPO_RICERCA_MITTENTE_SOGGETTO;
		case TOKEN_INFO: return CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO;
		case INDIRIZZO_IP: return CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP;
		default: return null;
		}
	}
	
	public static final String toTipoRicercaMittente(TipoFiltroMittenteQualsiasiEnum v) {
		if (v == null) return null;
		
		switch (v) {
		case IDENTIFICATIVO_AUTENTICATO: return CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO;
		case TOKEN_INFO: return CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO;
		case INDIRIZZO_IP: return CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP;
		default: return null;
		}
	}
	
	public static final String toTipoRicercaMittente(TipoFiltroMittenteFruizioneEnum v) {
		if (v == null) return null;
		
		switch (v) {
		case APPLICATIVO: return CostantiExporter.TIPO_RICERCA_MITTENTE_APPLICATIVO;
		case IDENTIFICATIVO_AUTENTICATO: return CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO;
		case TOKEN_INFO: return CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO;
		case INDIRIZZO_IP: return CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP;
		default: return null;
		}
	}
	
	public static final TipoCredenzialeMittente toTipoCredenzialeMittente(TokenClaimEnum v) {
		if (v==null) return null;
		switch (v) {
		case CLIENT_ID: return TipoCredenzialeMittente.token_clientId;
		case EMAIL: return TipoCredenzialeMittente.token_eMail;
		case ISSUER: return TipoCredenzialeMittente.token_issuer;
		case SUBJECT: return TipoCredenzialeMittente.token_subject;
		case USERNAME: return TipoCredenzialeMittente.token_username;
		default: return null;
		}
	}

}
