/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.constants.TipoAutenticazione;

import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.monitor.rs.server.model.DimensioniReportCustomEnum;
import org.openspcoop2.core.monitor.rs.server.model.DimensioniReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.OccupazioneBandaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TempoMedioRispostaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteIdentificativoAutenticatoEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteIndirizzoIPEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimDistribuzioneStatisticaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.web.monitor.statistiche.bean.DimensioneCustom;
import org.openspcoop2.web.monitor.statistiche.bean.NumeroDimensioni;
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
		Enums.toTipoSeverita.put(DiagnosticoSeveritaEnum.DEBUG, TipoSeverita.DEBUG);
		Enums.toTipoSeverita.put(DiagnosticoSeveritaEnum.ERROR, TipoSeverita.ERROR);
		Enums.toTipoSeverita.put(DiagnosticoSeveritaEnum.FATAL, TipoSeverita.FATAL);
		Enums.toTipoSeverita.put(DiagnosticoSeveritaEnum.INFO, TipoSeverita.INFO);
		Enums.toTipoSeverita.put(DiagnosticoSeveritaEnum.WARNING, TipoSeverita.WARN);
	}
	public static final Map<TipoSeverita, DiagnosticoSeveritaEnum> toDiagnosticoSeverita = Enums
			.dualizeMap(Enums.toTipoSeverita);

	public static final Map<EsitoTransazioneFullSearchEnum, Integer> toEsitoGruppo = new HashMap<>();
	static {
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.QUALSIASI, EsitoUtils.ALL_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.OK, EsitoUtils.ALL_OK_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.FAULT, EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.FALLITE, EsitoUtils.ALL_ERROR_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.FALLITE_E_FAULT, EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.ERRORI_CONSEGNA, EsitoUtils.ALL_ERROR_CONSEGNA_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.RICHIESTE_SCARTATE, EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE);
		Enums.toEsitoGruppo.put(EsitoTransazioneFullSearchEnum.PERSONALIZZATO, EsitoUtils.ALL_PERSONALIZZATO_VALUE);
	}

	public static final Map<TipoFiltroMittenteIdentificativoAutenticatoEnum, TipoAutenticazione> toTipoAutenticazione = new HashMap<>();
	static {
		Enums.toTipoAutenticazione.put(TipoFiltroMittenteIdentificativoAutenticatoEnum.BASIC, TipoAutenticazione.BASIC);
		Enums.toTipoAutenticazione.put(TipoFiltroMittenteIdentificativoAutenticatoEnum.PRINCIPAL, TipoAutenticazione.PRINCIPAL);
		Enums.toTipoAutenticazione.put(TipoFiltroMittenteIdentificativoAutenticatoEnum.SSL, TipoAutenticazione.SSL);
	}
	
	public static final Map<TipoFiltroMittenteIndirizzoIPEnum, String> toTipoIndirizzzoIP = new HashMap<>();
	static {
		Enums.toTipoIndirizzzoIP.put(TipoFiltroMittenteIndirizzoIPEnum.CLIENT_IP, org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_SOCKET);
		Enums.toTipoIndirizzzoIP.put(TipoFiltroMittenteIndirizzoIPEnum.X_FORWARDED_FOR, org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO);
	}

	public static final Map<TokenClaimEnum, String> toTokenClaim = new HashMap<>();
	static {
		Enums.toTokenClaim.put(TokenClaimEnum.CLIENT_ID, CostantiExporter.CLAIM_CLIENT_ID);
		Enums.toTokenClaim.put(TokenClaimEnum.EMAIL, CostantiExporter.CLAIM_EMAIL);
		Enums.toTokenClaim.put(TokenClaimEnum.ISSUER, CostantiExporter.CLAIM_ISSUER);
		Enums.toTokenClaim.put(TokenClaimEnum.SUBJECT, CostantiExporter.CLAIM_SUBJECT);
		Enums.toTokenClaim.put(TokenClaimEnum.USERNAME, CostantiExporter.CLAIM_USERNAME);
	}
	
	public static final Map<TokenClaimSearchEnum, String> toTokenSearchClaim = new HashMap<>();
	static {
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.CLIENT_ID, CostantiExporter.CLAIM_CLIENT_ID);
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.EMAIL, CostantiExporter.CLAIM_EMAIL);
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.ISSUER, CostantiExporter.CLAIM_ISSUER);
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.SUBJECT, CostantiExporter.CLAIM_SUBJECT);
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.USERNAME, CostantiExporter.CLAIM_USERNAME);
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.PDND_ORGANIZATION_NAME, CostantiExporter.CLAIM_PDND_ORGANIZATION_NAME);
		Enums.toTokenSearchClaim.put(TokenClaimSearchEnum.PDND_EXTERNAL_O_CONSUMER_ID, CostantiExporter.CLAIM_PDND_EXTERNAL_O_CONSUMER_ID);
	}

	public static final Map<FormatoReportEnum, String> toTipoFormato = new HashMap<>();
	static {
		Enums.toTipoFormato.put(FormatoReportEnum.CSV, CostantiExporter.TIPO_FORMATO_CSV);
		Enums.toTipoFormato.put(FormatoReportEnum.JSON, CostantiExporter.TIPO_FORMATO_JSON);
		Enums.toTipoFormato.put(FormatoReportEnum.PDF, CostantiExporter.TIPO_FORMATO_PDF);
		// toTipoFormato.put(FormatoReportEnum.PNG, CostantiExporter.TIPO_FORMATO_PNG);
		Enums.toTipoFormato.put(FormatoReportEnum.XLS, CostantiExporter.TIPO_FORMATO_XLS);
		Enums.toTipoFormato.put(FormatoReportEnum.XML, CostantiExporter.TIPO_FORMATO_XML);

	}

	public static final Map<TipoReportEnum, org.openspcoop2.core.statistiche.constants.TipoReport> toTipoReport = new HashMap<>();
	static {
		Enums.toTipoReport.put(TipoReportEnum.BAR, org.openspcoop2.core.statistiche.constants.TipoReport.BAR_CHART);
		Enums.toTipoReport.put(TipoReportEnum.LINE, org.openspcoop2.core.statistiche.constants.TipoReport.LINE_CHART);
		Enums.toTipoReport.put(TipoReportEnum.PIE, org.openspcoop2.core.statistiche.constants.TipoReport.PIE_CHART);
		Enums.toTipoReport.put(TipoReportEnum.TABLE, org.openspcoop2.core.statistiche.constants.TipoReport.TABELLA);
	}

	public static final Map<TipoInformazioneReportEnum, TipoVisualizzazione> toTipoVisualizzazione = new HashMap<>();
	static {
		Enums.toTipoVisualizzazione.put(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI,
				TipoVisualizzazione.NUMERO_TRANSAZIONI);
		Enums.toTipoVisualizzazione.put(TipoInformazioneReportEnum.OCCUPAZIONE_BANDA,
				TipoVisualizzazione.DIMENSIONE_TRANSAZIONI);
		Enums.toTipoVisualizzazione.put(TipoInformazioneReportEnum.TEMPO_MEDIO_RISPOSTA,
				TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA);
	}
	
	private static final EnumMap<DimensioniReportEnum, NumeroDimensioni> toNumeroDimensioni = new EnumMap<>(DimensioniReportEnum.class);
	public static Map<DimensioniReportEnum, NumeroDimensioni> getNumeroDimensioniMap() {
		return toNumeroDimensioni;
	}
	static {
		Enums.toNumeroDimensioni.put(DimensioniReportEnum._2D,
				NumeroDimensioni.DIMENSIONI_2);
		Enums.toNumeroDimensioni.put(DimensioniReportEnum._3D,
				NumeroDimensioni.DIMENSIONI_3);
		Enums.toNumeroDimensioni.put(DimensioniReportEnum._3DCUSTOM,
				NumeroDimensioni.DIMENSIONI_3_CUSTOM);
	}
	
	private static final EnumMap<DimensioniReportCustomEnum, DimensioneCustom> toInformazioneDimensioneCustom = new EnumMap<>(DimensioniReportCustomEnum.class);
	public static Map<DimensioniReportCustomEnum, DimensioneCustom> getInformazioneDimensioneCustom() {
		return toInformazioneDimensioneCustom;
	}
	static {
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TAG,
				DimensioneCustom.TAG);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.API,
				DimensioneCustom.API);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.API_IMPLEMENTATION,
				DimensioneCustom.IMPLEMENTAZIONE_API);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.OPERATION,
				DimensioneCustom.OPERAZIONE);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.LOCAL_ORGANIZATION,
				DimensioneCustom.SOGGETTO_LOCALE);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.REMOTE_ORGANIZATION,
				DimensioneCustom.SOGGETTO_REMOTO);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.CLIENT_ORGANIZATION,
				DimensioneCustom.SOGGETTO_FRUITORE);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.PROVIDER_ORGANIZATION,
				DimensioneCustom.SOGGETTO_EROGATORE);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_ISSUER,
				DimensioneCustom.TOKEN_ISSUER);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_CLIENTID,
				DimensioneCustom.TOKEN_CLIENT_ID);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_SUBJECT,
				DimensioneCustom.TOKEN_SUBJECT);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_USERNAME,
				DimensioneCustom.TOKEN_USERNAME);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_EMAIL,
				DimensioneCustom.TOKEN_EMAIL);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_PDND_ORGANIZATION,
				DimensioneCustom.TOKEN_PDND_ORGANIZATION);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_PDND_EXTERNAL_ID,
				DimensioneCustom.TOKEN_PDND_ORGANIZATION_EXTERNAL_ID);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_PDND_CONSUMER_ID,
				DimensioneCustom.TOKEN_PDND_ORGANIZATION_CONSUMER_ID);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.PRINCIPAL,
				DimensioneCustom.PRINCIPAL);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.CLIENT,
				DimensioneCustom.APPLICATIVO_TRASPORTO);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.TOKEN_CLIENT,
				DimensioneCustom.APPLICATIVO_TOKEN);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.IP_ADDRESS,
				DimensioneCustom.INDIRIZZO_IP);
		Enums.toInformazioneDimensioneCustom.put(DimensioniReportCustomEnum.RESULT,
				DimensioneCustom.ESITO);
	}

	public static final Map<OccupazioneBandaEnum, TipoBanda> toTipoBanda = new HashMap<>();
	static {
		Enums.toTipoBanda.put(OccupazioneBandaEnum.COMPLESSIVA, TipoBanda.COMPLESSIVA);
		Enums.toTipoBanda.put(OccupazioneBandaEnum.ESTERNA, TipoBanda.ESTERNA);
		Enums.toTipoBanda.put(OccupazioneBandaEnum.INTERNA, TipoBanda.INTERNA);
	}

	public static final Map<TempoMedioRispostaEnum, TipoLatenza> toTipoLatenza = new HashMap<>();
	static {
		Enums.toTipoLatenza.put(TempoMedioRispostaEnum.GATEWAY, TipoLatenza.LATENZA_PORTA);
		Enums.toTipoLatenza.put(TempoMedioRispostaEnum.SERVIZIO, TipoLatenza.LATENZA_SERVIZIO);
		Enums.toTipoLatenza.put(TempoMedioRispostaEnum.TOTALE, TipoLatenza.LATENZA_TOTALE);
	}

	public static final Map<TokenClaimEnum, String> toClaim = new HashMap<>();
	static {
		Enums.toClaim.put(TokenClaimEnum.CLIENT_ID, CostantiExporter.CLAIM_CLIENT_ID);
		Enums.toClaim.put(TokenClaimEnum.EMAIL, CostantiExporter.CLAIM_EMAIL);
		Enums.toClaim.put(TokenClaimEnum.ISSUER, CostantiExporter.CLAIM_ISSUER);
		Enums.toClaim.put(TokenClaimEnum.SUBJECT, CostantiExporter.CLAIM_SUBJECT);
		Enums.toClaim.put(TokenClaimEnum.USERNAME, CostantiExporter.CLAIM_USERNAME);
	}
	
	public static final Map<TokenClaimDistribuzioneStatisticaEnum, String> toDistribuzioneTokenClaim = new HashMap<>();
	static {
		Enums.toDistribuzioneTokenClaim.put(TokenClaimDistribuzioneStatisticaEnum.CLIENT_ID, CostantiExporter.CLAIM_CLIENT_ID);
		Enums.toDistribuzioneTokenClaim.put(TokenClaimDistribuzioneStatisticaEnum.CLIENT_ID_PDND_INFORMAZIONI, CostantiExporter.CLAIM_PDND_ORGANIZATION_NAME);
		Enums.toDistribuzioneTokenClaim.put(TokenClaimDistribuzioneStatisticaEnum.EMAIL, CostantiExporter.CLAIM_EMAIL);
		Enums.toDistribuzioneTokenClaim.put(TokenClaimDistribuzioneStatisticaEnum.ISSUER, CostantiExporter.CLAIM_ISSUER);
		Enums.toDistribuzioneTokenClaim.put(TokenClaimDistribuzioneStatisticaEnum.SUBJECT, CostantiExporter.CLAIM_SUBJECT);
		Enums.toDistribuzioneTokenClaim.put(TokenClaimDistribuzioneStatisticaEnum.USERNAME, CostantiExporter.CLAIM_USERNAME);
	}

	public static final Map<UnitaTempoReportEnum, StatisticType> toStatisticType = new HashMap<>();
	static {
		Enums.toStatisticType.put(UnitaTempoReportEnum.GIORNALIERO, StatisticType.GIORNALIERA);
		Enums.toStatisticType.put(UnitaTempoReportEnum.MENSILE, StatisticType.MENSILE);
		Enums.toStatisticType.put(UnitaTempoReportEnum.ORARIO, StatisticType.ORARIA);
		Enums.toStatisticType.put(UnitaTempoReportEnum.SETTIMANALE, StatisticType.SETTIMANALE);
	}

	public static final Map<TransazioneRuoloEnum, String> toTipologiaRuoloTransazione = new HashMap<>();
	static {
		Enums.toTipologiaRuoloTransazione.put(TransazioneRuoloEnum.EROGAZIONE, CostantiExporter.TIPOLOGIA_EROGAZIONE);
		Enums.toTipologiaRuoloTransazione.put(TransazioneRuoloEnum.FRUIZIONE, CostantiExporter.TIPOLOGIA_FRUIZIONE);
	}
	
	public static final Map<FiltroRicercaRuoloTransazioneEnum, String> toTipologiaFiltroRicercaRuoloTransazioneEnum = new HashMap<>();
	static {
		Enums.toTipologiaFiltroRicercaRuoloTransazioneEnum.put(FiltroRicercaRuoloTransazioneEnum.EROGAZIONE, CostantiExporter.TIPOLOGIA_EROGAZIONE);
		Enums.toTipologiaFiltroRicercaRuoloTransazioneEnum.put(FiltroRicercaRuoloTransazioneEnum.FRUIZIONE, CostantiExporter.TIPOLOGIA_FRUIZIONE);
		Enums.toTipologiaFiltroRicercaRuoloTransazioneEnum.put(FiltroRicercaRuoloTransazioneEnum.QUALSIASI, CostantiExporter.TIPOLOGIA_EROGAZIONE_FRUIZIONE);
	}
	
	public static final String toTipoRicercaMittente(TipoFiltroMittenteEnum v) {
		if (v == null) return null;
		
		switch (v) {
		case EROGAZIONE_SOGGETTO: return CostantiExporter.TIPO_RICERCA_MITTENTE_SOGGETTO;
		case FRUIZIONE_APPLICATIVO: return CostantiExporter.TIPO_RICERCA_MITTENTE_APPLICATIVO;
		case EROGAZIONE_APPLICATIVO: return CostantiExporter.TIPO_RICERCA_MITTENTE_APPLICATIVO;
		case IDENTIFICATIVO_AUTENTICATO: return CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO;
		case EROGAZIONE_TOKEN_INFO: return CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO;
		case TOKEN_INFO: return CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO;
		case INDIRIZZO_IP: return CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP;
		default: return null;
		}
	}

	public static final TipoCredenzialeMittente toTipoCredenzialeMittente(TokenClaimEnum v) {
		if (v==null) return null;
		switch (v) {
		case CLIENT_ID: return TipoCredenzialeMittente.TOKEN_CLIENT_ID;
		case EMAIL: return TipoCredenzialeMittente.TOKEN_EMAIL;
		case ISSUER: return TipoCredenzialeMittente.TOKEN_ISSUER;
		case SUBJECT: return TipoCredenzialeMittente.TOKEN_SUBJECT;
		case USERNAME: return TipoCredenzialeMittente.TOKEN_USERNAME;
		default: return null;
		}
	}
	
	public static final TipoCredenzialeMittente toTipoCredenzialeMittente(TokenClaimSearchEnum v) {
		if (v==null) return null;
		switch (v) {
		case CLIENT_ID: return TipoCredenzialeMittente.TOKEN_CLIENT_ID;
		case EMAIL: return TipoCredenzialeMittente.TOKEN_EMAIL;
		case ISSUER: return TipoCredenzialeMittente.TOKEN_ISSUER;
		case SUBJECT: return TipoCredenzialeMittente.TOKEN_SUBJECT;
		case USERNAME: return TipoCredenzialeMittente.TOKEN_USERNAME;
		case PDND_ORGANIZATION_NAME: return TipoCredenzialeMittente.PDND_ORGANIZATION_NAME;
		case PDND_EXTERNAL_O_CONSUMER_ID: return TipoCredenzialeMittente.PDND_ORGANIZATION_EXTERNAL_ID; // viene usato questo
		default: return null;
		}
	}

}
