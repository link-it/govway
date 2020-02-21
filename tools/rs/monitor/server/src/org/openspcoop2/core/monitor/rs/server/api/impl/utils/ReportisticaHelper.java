/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiBase;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiQualsiasi;
import org.openspcoop2.core.monitor.rs.server.model.FiltroErogazione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneDistribuzioneSoggettoRemoto;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneSoggetto;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteIdAutenticato;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteIndirizzoIP;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteQualsiasi;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.OccupazioneBandaEnum;
import org.openspcoop2.core.monitor.rs.server.model.OccupazioneBandaTipi;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportBase;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportMultiLine;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatistica;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatisticaSoggetti;
import org.openspcoop2.core.monitor.rs.server.model.RicercaConfigurazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaAndamentoTemporale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneAzione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneEsiti;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoLocale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoRemoto;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneTokenInfo;
import org.openspcoop2.core.monitor.rs.server.model.TempoMedioRispostaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TempoMedioRispostaTipi;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
import org.openspcoop2.web.monitor.statistiche.dao.ConfigurazioniGeneraliService;
import org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService;


/**
 * ReportisticaHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ReportisticaHelper {

	@SuppressWarnings("unchecked")
	public static final void overrideFiltroEsito(FiltroEsito filtro, HttpRequestWrapper wrap, MonitoraggioEnv env) {

		if (filtro != null) {

			EsitoTransazioneFullSearchEnum tipo = (filtro.getTipo() != null) ? filtro.getTipo() : EsitoTransazioneFullSearchEnum.QUALSIASI;
			
			switch (tipo) {
			case QUALSIASI:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_OK);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				if(filtro.isEscludiScartate()!=null) {
					wrap.overrideParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE, 
							filtro.isEscludiScartate() ? CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_TRUE : CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_FALSE);
				}
				break;
			case OK:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_OK);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case FAULT:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_FAULT_APPLICATIVO);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case FALLITE:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_FALLITE);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				if(filtro.isEscludiScartate()!=null) {
					wrap.overrideParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE, 
							filtro.isEscludiScartate() ? CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_TRUE : CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_FALSE);
				}
				break;
			case FALLITE_E_FAULT:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO,
						CostantiExporter.ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				if(filtro.isEscludiScartate()!=null) {
					wrap.overrideParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE, 
							filtro.isEscludiScartate() ? CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_TRUE : CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_FALSE);
				}
				break;
			case ERRORI_CONSEGNA:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_ERRORI_CONSEGNA);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case RICHIESTE_SCARTATE:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_RICHIESTE_SCARTATE);
				wrap.overrideParameter(CostantiExporter.ESITO, BaseHelper.evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case PERSONALIZZATO:
				if (filtro.getDettaglio() != null) {
					Iterable<String> esiti = ((ArrayList<Integer>) filtro.getDettaglio()).stream()
							.map(e -> e.toString())::iterator;

					wrap.overrideParameter(CostantiExporter.ESITO, String.join(",", esiti));
				}
				break;
			}
		}
	}

	public static final void overrideRicercaBaseStatistica(RicercaBaseStatistica body, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (body == null)
			return;

		// defaults:
		if (body.getUnitaTempo() == null)
			body.setUnitaTempo(UnitaTempoReportEnum.GIORNALIERO);
		// Intervallo Temporale
		SimpleDateFormat sdf = DateUtils.getSimpleDateFormatMs();
		wrap.overrideParameter(CostantiExporter.DATA_INIZIO,
				sdf.format(body.getIntervalloTemporale().getDataInizio().toDate()));
		wrap.overrideParameter(CostantiExporter.DATA_FINE,
				sdf.format(body.getIntervalloTemporale().getDataFine().toDate()));
		wrap.overrideParameter(CostantiExporter.TIPO_UNITA_TEMPORALE,
				Enums.toStatisticType.get(body.getUnitaTempo()).toString());
		wrap.overrideParameter(CostantiExporter.TIPOLOGIA,
				Enums.toTipologiaFiltroRicercaRuoloTransazioneEnum.get(body.getTipo()).toString());
		if(body.getTag()!=null) {
			wrap.overrideParameter(CostantiExporter.GRUPPO,
					body.getTag());
		}
	}

	public static final void overrideFiltroMittenteIdApplicativo(FiltroMittenteIdAutenticato filtro,
			HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (filtro == null)
			return;
		
		wrap.overrideParameter(CostantiExporter.TIPO_AUTENTICAZIONE,Enums.toTipoAutenticazione.get(filtro.getAutenticazione()).getValue());
		if (filtro.getId() != null) {
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, filtro.getId());
		}
		if (filtro.isRicercaEsatta() != null) {
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, filtro.isRicercaEsatta() + "");
		}
		if (filtro.isCaseSensitive() != null) {
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,
					filtro.isCaseSensitive() + "");
		}
	}
	
	public static final void overrideFiltroMittenteIndirizzoIP(FiltroMittenteIndirizzoIP filtro,
			HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (filtro == null)
			return;
		
		if(filtro.getTipo()!=null) {
			wrap.overrideParameter(CostantiExporter.TIPO_INDIRIZZO_IP,Enums.toTipoIndirizzzoIP.get(filtro.getTipo()));
		}
		if (filtro.getId() != null) {
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, filtro.getId());
		}
		if (filtro.isRicercaEsatta() != null) {
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, filtro.isRicercaEsatta() + "");
		}
		if (filtro.isCaseSensitive() != null) {
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,
					filtro.isCaseSensitive() + "");
		}
	}
	
	public static final FiltroMittenteErogazioneTokenClaim deserializeFiltroMittenteErogazioneTokenClaim(Object o) {
		FiltroMittenteErogazioneTokenClaim fClaim = BaseHelper.deserializeDefault(o,FiltroMittenteErogazioneTokenClaim.class);
		if (fClaim.getClaim() == null || StringUtils.isEmpty(fClaim.getId())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(fClaim.getClass().getName() + ": Indicare i campi obbligatori claim e id"); 
		}
		return fClaim;
	}
	
	public static final FiltroMittenteErogazioneApplicativo deserializeFiltroMittenteErogazioneApplicativo(Object o) {
		FiltroMittenteErogazioneApplicativo ret = BaseHelper.deserializeDefault(o,FiltroMittenteErogazioneApplicativo.class);
		if (StringUtils.isEmpty(ret.getSoggetto()) || StringUtils.isEmpty(ret.getApplicativo())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteErogazioneApplicativo.class.getName() + ": Indicare i campi obbligatori 'soggetto' e 'applicativo'");
		}
		return ret;
	}
	
	public static final FiltroMittenteErogazioneSoggetto deserializeFiltroMittenteErogazioneSoggetto(Object o) {
		FiltroMittenteErogazioneSoggetto ret= BaseHelper.deserializeDefault(o,FiltroMittenteErogazioneSoggetto.class);
		if (StringUtils.isEmpty(ret.getSoggetto())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteErogazioneSoggetto.class.getName() + ": Indicare i campi obbligatori 'soggetto'");
		}
		return ret;
	}

	public static final void overrideFiltroMittenteErogazione(FiltroMittenteErogazione filtro, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;
		
		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(filtro.getTipo()));
		
		if (filtro.getId() != null) {
			switch (filtro.getTipo()) {
			case APPLICATIVO: {
				FiltroMittenteErogazioneApplicativo fAppl = ReportisticaHelper.deserializeFiltroMittenteErogazioneApplicativo(filtro.getId());
				wrap.overrideParameter(CostantiExporter.APPLICATIVO, fAppl.getApplicativo());
				wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.soggetto.getTipo(), fAppl.getSoggetto()).toString());
				break;
			}
			case IDENTIFICATIVO_AUTENTICATO: {
				FiltroMittenteIdAutenticato fIdent = ReportisticaHelper.deserializeFiltroMittenteIdAutenticato(filtro.getId());
				ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdent, wrap, env);
				break;
			}
			case SOGGETTO: {
				FiltroMittenteErogazioneSoggetto fSogg = ReportisticaHelper.deserializeFiltroMittenteErogazioneSoggetto(filtro.getId());
				wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.soggetto.getTipo(), fSogg.getSoggetto()).toString());
				break;
			}
			case TOKEN_INFO: {
				FiltroMittenteErogazioneTokenClaim fClaim = ReportisticaHelper.deserializeFiltroMittenteErogazioneTokenClaim(filtro.getId());
				wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
				wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE, fClaim.isCaseSensitive() + "");
				if (fClaim.getSoggetto() != null) {
					wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.soggetto.getTipo(), fClaim.getSoggetto()).toString());
				}
				break;
			}
			case INDIRIZZO_IP: {
				FiltroMittenteIndirizzoIP indirizzoIP = ReportisticaHelper.deserializeFiltroMittenteIndirizzoIP(filtro.getId());
				ReportisticaHelper.overrideFiltroMittenteIndirizzoIP(indirizzoIP, wrap, env);
				break;
			}
			} // switch
		}
	}
	
	public static final void overrideFiltroMittenteErogazioneDistribuzioneSoggettoRemoto(FiltroMittenteErogazioneDistribuzioneSoggettoRemoto filtro, HttpRequestWrapper wrap,
		MonitoraggioEnv env)
	{
		if (filtro == null)
			return;
		
		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(filtro.getTipo()));
		
		if (filtro.getId() != null) {
			switch (filtro.getTipo()) {
		
			case IDENTIFICATIVO_AUTENTICATO: {
				FiltroMittenteIdAutenticato fIdent = ReportisticaHelper.deserializeFiltroMittenteIdAutenticato(filtro.getId());
				ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdent, wrap, env);
				break;
			}
			case TOKEN_INFO: {
				FiltroMittenteErogazioneTokenClaim fClaim = ReportisticaHelper.deserializeFiltroMittenteErogazioneTokenClaim(filtro.getId());
				wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
				wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE, fClaim.isCaseSensitive() + "");
				if (fClaim.getSoggetto() != null) {
					wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.soggetto.getTipo(), fClaim.getSoggetto()).toString());
				}
				break;
			}
			case INDIRIZZO_IP: {
				FiltroMittenteIndirizzoIP indirizzoIP = ReportisticaHelper.deserializeFiltroMittenteIndirizzoIP(filtro.getId());
				ReportisticaHelper.overrideFiltroMittenteIndirizzoIP(indirizzoIP, wrap, env);
				break;
			}
			} // switch
		}
	}
	
	public static final FiltroMittenteIdAutenticato deserializeFiltroMittenteIdAutenticato(Object o) {
		FiltroMittenteIdAutenticato ret = BaseHelper.deserializeDefault(o, FiltroMittenteIdAutenticato.class);
		if (ret.getAutenticazione() == null || StringUtils.isEmpty(ret.getId()))
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteIdAutenticato.class.getName() + ": Indicare i campi obbligatori 'autenticazione' e 'id'");

		return ret;
	}
	
	public static final FiltroMittenteIndirizzoIP deserializeFiltroMittenteIndirizzoIP(Object o) {
		FiltroMittenteIndirizzoIP ret = BaseHelper.deserializeDefault(o, FiltroMittenteIndirizzoIP.class);
		if (StringUtils.isEmpty(ret.getId()))
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteIndirizzoIP.class.getName() + ": Indicare il campo obbligatorio 'id'");

		return ret;
	}

	public static final FiltroMittenteFruizioneTokenClaim deserializeFiltroMittenteFruizioneTokenClaim(Object o) {
		FiltroMittenteFruizioneTokenClaim fClaim = BaseHelper.deserializeDefault(o,FiltroMittenteFruizioneTokenClaim.class);
		if (fClaim.getClaim() == null || StringUtils.isEmpty(fClaim.getId()) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteFruizioneTokenClaim.class.getName() + ": Indicare i campi obbligatori 'claim' e 'id'");
		}
		
		return fClaim;
	}
	
	public static final FiltroMittenteFruizioneApplicativo deserializeFiltroMittenteFruizioneApplicativo(Object o) {
		FiltroMittenteFruizioneApplicativo fAppl = BaseHelper.deserializeDefault(o, FiltroMittenteFruizioneApplicativo.class);
		if (StringUtils.isEmpty(fAppl.getApplicativo())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteFruizioneApplicativo.class.getName() + ": Indicare il campo obbligatorio 'applicativo'");
		}
		return fAppl;
	}
	
	public static final void overrideFiltroMittenteFruizione(FiltroMittenteFruizione filtro, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;
		if (filtro.getTipo() == null)
			return;

		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(filtro.getTipo()));
		switch (filtro.getTipo()) {
		case APPLICATIVO: {
			FiltroMittenteFruizioneApplicativo fAppl = ReportisticaHelper.deserializeFiltroMittenteFruizioneApplicativo(filtro.getId());
			wrap.overrideParameter(CostantiExporter.APPLICATIVO, fAppl.getApplicativo());
			break;
		}
		case IDENTIFICATIVO_AUTENTICATO: {
			FiltroMittenteIdAutenticato fIdent = ReportisticaHelper.deserializeFiltroMittenteIdAutenticato(filtro.getId());
			ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdent, wrap, env);
			break;
		}
		case TOKEN_INFO: {
			FiltroMittenteFruizioneTokenClaim fClaim = ReportisticaHelper.deserializeFiltroMittenteFruizioneTokenClaim(filtro.getId());			
			wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,fClaim.isCaseSensitive() + "");
			break;
		}
		case INDIRIZZO_IP: {
			FiltroMittenteIndirizzoIP indirizzoIP = ReportisticaHelper.deserializeFiltroMittenteIndirizzoIP(filtro.getId());
			ReportisticaHelper.overrideFiltroMittenteIndirizzoIP(indirizzoIP, wrap, env);
			break;
		}
		}
	}
	
	public static final void overrideFiltroMittenteQualsiasi(FiltroMittenteQualsiasi filtro, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;
		if (filtro.getTipo() == null)
			return;

		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(filtro.getTipo()));
		switch (filtro.getTipo()) {
		case IDENTIFICATIVO_AUTENTICATO: {
			FiltroMittenteIdAutenticato fIdent = ReportisticaHelper.deserializeFiltroMittenteIdAutenticato(filtro.getId());
			ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdent, wrap, env);
			break;
		}
		case TOKEN_INFO: {
			FiltroMittenteFruizioneTokenClaim fClaim = ReportisticaHelper.deserializeFiltroMittenteFruizioneTokenClaim(filtro.getId());			
			wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,fClaim.isCaseSensitive() + "");
			break;
		}
		case INDIRIZZO_IP: {
			FiltroMittenteIndirizzoIP indirizzoIP = ReportisticaHelper.deserializeFiltroMittenteIndirizzoIP(filtro.getId());
			ReportisticaHelper.overrideFiltroMittenteIndirizzoIP(indirizzoIP, wrap, env);
			break;
		}
		}
	}

	public static final void overrideOpzioniGenerazioneReportBase(OpzioniGenerazioneReportBase body,
			HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;

		wrap.overrideParameter(CostantiExporter.TIPO_FORMATO, Enums.toTipoFormato.get(body.getFormato()));

		org.openspcoop2.core.statistiche.constants.TipoReport tipoReport = Enums.toTipoReport.get(body.getTipo());
		if (tipoReport == null)
			tipoReport = org.openspcoop2.core.statistiche.constants.TipoReport.TABELLA;

		wrap.overrideParameter(CostantiExporter.TIPO_REPORT, tipoReport.toString());
	}

	public static final void overrideOpzioniGenerazioneReport(OpzioniGenerazioneReport body, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (body == null)
			return;

		ReportisticaHelper.overrideOpzioniGenerazioneReportBase(body, wrap, env);

		if (body.getTipoInformazione() == null)
			body.setTipoInformazione(new TipoInformazioneReport());
		if (body.getTipoInformazione().getTipo() == null)
			body.getTipoInformazione().setTipo(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI);

		wrap.overrideParameter(CostantiExporter.TIPO_INFORMAZIONE_VISUALIZZATA, Enums.toTipoVisualizzazione.get(body.getTipoInformazione().getTipo()).toString());
		switch (body.getTipoInformazione().getTipo()) {
		case NUMERO_TRANSAZIONI:
			break;
		case OCCUPAZIONE_BANDA: {
			OccupazioneBandaEnum val = OccupazioneBandaEnum.fromValue((String) body.getTipoInformazione().getValori());
			wrap.overrideParameter(CostantiExporter.TIPO_BANDA_VISUALIZZATA, Enums.toTipoBanda.get(val).toString());
			break;
		}
		case TEMPO_MEDIO_RISPOSTA: {
			TempoMedioRispostaEnum val = TempoMedioRispostaEnum.fromValue((String) body.getTipoInformazione().getValori());
			wrap.overrideParameter(CostantiExporter.TIPO_LATENZA_VISUALIZZATA, Enums.toTipoLatenza.get(val).toString());
			break;
		}
		}
	}

	public static final void overrideOpzioniGenerazioneReportMultiLine(OpzioniGenerazioneReportMultiLine body,HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;

		ReportisticaHelper.overrideOpzioniGenerazioneReportBase(body, wrap, env);

		// defaults
		if (body.getTipoInformazione().getTipo() == null)
			body.getTipoInformazione().setTipo(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI);

		switch (body.getTipoInformazione().getTipo()) {
		case NUMERO_TRANSAZIONI:
			break;
		case OCCUPAZIONE_BANDA: {
			OccupazioneBandaTipi val = BaseHelper.deserializeDefault(body.getTipoInformazione().getValori(),OccupazioneBandaTipi.class);
			ArrayList<String> abilitati = new ArrayList<>();
			if (val.isBandaComplessiva())
				abilitati.add(Enums.toTipoBanda.get(OccupazioneBandaEnum.COMPLESSIVA).toString());
			if (val.isBandaEsterna())
				abilitati.add(Enums.toTipoBanda.get(OccupazioneBandaEnum.ESTERNA).toString());
			if (val.isBandaInterna())
				abilitati.add(Enums.toTipoBanda.get(OccupazioneBandaEnum.INTERNA).toString());

			wrap.overrideParameter(CostantiExporter.TIPO_BANDA_VISUALIZZATA, String.join(",", abilitati));
			break;
		}
		case TEMPO_MEDIO_RISPOSTA: {
			TempoMedioRispostaTipi val = BaseHelper.deserializeDefault(body.getTipoInformazione().getValori(),TempoMedioRispostaTipi.class);
			ArrayList<String> abilitati = new ArrayList<>();
			if (val.isLatenzaGateway())
				abilitati.add(Enums.toTipoLatenza.get(TempoMedioRispostaEnum.GATEWAY).toString());
			if (val.isLatenzaServizio())
				abilitati.add(Enums.toTipoLatenza.get(TempoMedioRispostaEnum.SERVIZIO).toString());
			if (val.isLatenzaTotale())
				abilitati.add(Enums.toTipoLatenza.get(TempoMedioRispostaEnum.TOTALE).toString());

			wrap.overrideParameter(CostantiExporter.TIPO_LATENZA_VISUALIZZATA, String.join(",", abilitati));
			break;
		}
		}
	}


	public static final void overrideRicercaBaseStatisticaSoggetti(RicercaBaseStatisticaSoggetti body,
			HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;

		ReportisticaHelper.overrideRicercaBaseStatistica(body, wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroErogazione(body.getTag(),BaseHelper.deserialize(body.getApi(), FiltroErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroFruizione(body.getTag(),BaseHelper.deserialize(body.getApi(), FiltroFruizione.class), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroQualsiasi(body.getTag(),BaseHelper.deserialize(body.getApi(), FiltroApiQualsiasi.class), wrap, env);
			break;
		}
	}

	public static final void overrideFiltroFruizione(String tag, FiltroFruizione body, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (body == null) return;
		
		IDSoggetto erogatore = new IDSoggetto(env.soggetto.getTipo(), body.getErogatore());
		ReportisticaHelper.overrideFiltroApiBase(tag, body, erogatore, wrap, env);
	}

	public static final void overrideFiltroErogazione(String tag, FiltroErogazione body, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;
		ReportisticaHelper.overrideFiltroApiBase(tag, body, env.soggetto, wrap, env);
	}
	
	public static final void overrideFiltroQualsiasi(String tag, FiltroApiQualsiasi body, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;
		
		IDSoggetto erogatore = new IDSoggetto(env.soggetto.getTipo(), body.getErogatore());
		ReportisticaHelper.overrideFiltroApiBase(tag, body, erogatore, wrap, env);
		if (!StringUtils.isEmpty(body.getSoggettoRemoto())) {
			IDSoggetto remoto = new IDSoggetto(env.soggetto.getTipo(), body.getSoggettoRemoto());
			wrap.overrideParameter(CostantiExporter.TRAFFICO_PER_SOGGETTO, remoto.toString() );
		}
		
	}

	public static final void overrideFiltroApiBase(String tag, FiltroApiBase filtro_api, IDSoggetto erogatore, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if(tag!=null) {
			wrap.overrideParameter(CostantiExporter.GRUPPO, tag);
		}
		
		if (filtro_api == null)
			return;
		
		if ( !StringUtils.isEmpty(filtro_api.getNome()) || filtro_api.getVersione() != null || !StringUtils.isEmpty(filtro_api.getTipo())) {
			
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
			wrap.overrideParameter(CostantiExporter.SERVIZIO,
					ReportisticaHelper.buildNomeServizioForOverride(filtro_api.getNome(), filtro_api.getTipo(), filtro_api.getVersione(), Optional.of(erogatore)));
		}
	}

	public static final void overrideRicercaStatisticaDistribuzioneApplicativo(
			RicercaStatisticaDistribuzioneApplicativo body, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
	}
	

	public static final byte[] generateReport(HttpRequestWrapper request, IContext context) throws Exception {
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		StatisticheGiornaliereService statisticheService = null;
		ServiceManagerProperties smp = null;
		try {
			connection = dbManager.getConnectionStatistiche();
			smp = dbManager.getServiceManagerPropertiesTracce();
			statisticheService = new StatisticheGiornaliereService(connection, true, smp,
					LoggerProperties.getLoggerDAO());
		} catch (Exception e) {
			dbManager.releaseConnectionStatistiche(connection);
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}

		try {
			return StatsGenerator.generateReport(request, context, statisticheService);
		} catch (Exception e) {
			throw FaultCode.NOT_FOUND.toException(e);
		} finally {
			dbManager.releaseConnectionStatistiche(connection);
		}
	}

	public static final byte[] getReportDistribuzioneApi(RicercaStatisticaDistribuzioneApi body, MonitoraggioEnv env)
			throws Exception {

		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper request = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.api);

		ReportisticaHelper.overrideRicercaBaseStatistica(body, request, env);
		// Mittente
		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroMittenteErogazione(
					BaseHelper.deserialize(body.getMittente(), FiltroMittenteErogazione.class), request,
					env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroMittenteFruizione(
					BaseHelper.deserialize(body.getMittente(), FiltroMittenteFruizione.class), request, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroMittenteQualsiasi(
					BaseHelper.deserialize(body.getMittente(), FiltroMittenteQualsiasi.class), request, env);
			break;
		}
		// FiltroEsito
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), request, env);
		// Soggetto Erogatore
		if ( FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.equals(body.getTipo()) && body.getSoggettoErogatore() != null) {
			request.overrideParameter(CostantiExporter.DESTINATARIO, new IDSoggetto(env.soggetto.getTipo(),BaseHelper.deserialize(body.getSoggettoErogatore(), String.class)));
		}
		// Opzioni Report
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), request, env);

		return ReportisticaHelper.generateReport(request, env.context);
	}

	public static final byte[] getReportDistribuzioneApplicativo(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.applicativo);
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneAzione(RicercaStatisticaDistribuzioneAzione body,MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.azione);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroMittenteErogazione(BaseHelper.deserialize(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroMittenteFruizione(BaseHelper.deserialize(body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroMittenteQualsiasi(BaseHelper.deserialize(body.getMittente(), FiltroMittenteQualsiasi.class), wrap, env);
			break;
		}

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneEsiti(RicercaStatisticaDistribuzioneEsiti body,MonitoraggioEnv env) throws Exception {
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}
		
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.esiti);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		// Mittente
		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroMittenteErogazione( BaseHelper.deserialize(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroMittenteFruizione( BaseHelper.deserialize( body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroMittenteQualsiasi( BaseHelper.deserialize( body.getMittente(), FiltroMittenteQualsiasi.class), wrap, env);
			break;
		}
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());
		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneIdAutenticato(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(),
				TipoReport.identificativo_autenticato);

		ReportisticaHelper.overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);

		return ReportisticaHelper.generateReport(wrap, env.context);
	}
	
	public static final byte[] getReportDistribuzioneIndirizzoIP(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(),
				TipoReport.indirizzo_ip);

		ReportisticaHelper.overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);

		return ReportisticaHelper.generateReport(wrap, env.context);
	}


	public static final byte[] getReportDistribuzioneSoggettoRemoto(RicercaStatisticaDistribuzioneSoggettoRemoto body,
			MonitoraggioEnv env) throws Exception {
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.soggetto_remoto);
		ReportisticaHelper.overrideRicercaBaseStatistica(body, wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroMittenteErogazioneDistribuzioneSoggettoRemoto(BaseHelper.deserialize(body.getMittente(), FiltroMittenteErogazioneDistribuzioneSoggettoRemoto.class), wrap, env); 
			ReportisticaHelper.overrideFiltroErogazione(body.getTag(), BaseHelper.deserialize(body.getApi(), FiltroErogazione.class), wrap,env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroMittenteFruizione(BaseHelper.deserialize(body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
			ReportisticaHelper.overrideFiltroFruizione(body.getTag(), BaseHelper.deserialize(body.getApi(), FiltroFruizione.class), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroMittenteQualsiasi(BaseHelper.deserialize(body.getMittente(), FiltroMittenteQualsiasi.class), wrap, env);
			ReportisticaHelper.overrideFiltroQualsiasi(body.getTag(), BaseHelper.deserialize(body.getApi(), FiltroApiQualsiasi.class), wrap, env);
			break;
		}
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneTemporale(RicercaStatisticaAndamentoTemporale body,MonitoraggioEnv env) throws Exception {
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}
		
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.temporale);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReportMultiLine(body.getReport(), wrap, env);
		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroMittenteErogazione(BaseHelper.deserialize(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroMittenteFruizione(BaseHelper.deserialize(body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroMittenteQualsiasi(BaseHelper.deserialize(body.getMittente(), FiltroMittenteQualsiasi.class), wrap, env);
			break;
		}
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneTokenInfo(RicercaStatisticaDistribuzioneTokenInfo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.token_info);

		ReportisticaHelper.overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);
		wrap.overrideParameter(CostantiExporter.CLAIM, Enums.toClaim.get(body.getClaim()).toString());

		if (FiltroRicercaRuoloTransazioneEnum.EROGAZIONE.equals(body.getTipo()) && body.getSoggetto() != null) {
			String nomeMittente = BaseHelper.deserialize(body.getSoggetto(), String.class);
			wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.soggetto.getTipo(), nomeMittente).toString());
		}

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneSoggettoLocale(RicercaStatisticaDistribuzioneSoggettoLocale body,
			MonitoraggioEnv env) throws Exception {
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}
		
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo, null,
				body.getTipo(), body.getReport().getFormato(), TipoReport.soggetto_locale);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroMittenteErogazione(BaseHelper.deserialize(body.getMittente(),FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroMittenteFruizione(BaseHelper.deserialize(body.getMittente(),FiltroMittenteFruizione.class), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroMittenteQualsiasi(BaseHelper.deserialize(body.getMittente(),FiltroMittenteQualsiasi.class), wrap, env);
			break;
		}

		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final String buildNomeServizioForOverride(String nomeServizio, String tipoServizio,
			Integer versioneServizio, Optional<IDSoggetto> erogatore) {
		if (nomeServizio == null || tipoServizio == null || versioneServizio == null)
			return null;

		StringBuilder uri = new StringBuilder();
		String nomeAsps = nomeServizio;
		String tipoAsps = tipoServizio;

		// Per popolare il campo NomeServizio seguo quanto fatto in
		// DynamicPdDBeanUtils._getListaSelectItemsElencoServiziErogazione
		uri.append(tipoAsps).append("/");
		uri.append(nomeAsps).append(":").append(versioneServizio);
		if (erogatore.isPresent())
			uri.append(" (").append(erogatore.get().getTipo()).append("/").append(erogatore.get().getNome())
					.append(")");

		return uri.toString();
	}

	public static final String getTipoServizioDefault(MonitoraggioEnv env) {

		try {
			IProtocolConfiguration protocolConf = env.protocolFactoryMgr.getProtocolFactoryByName(env.tipo_protocollo)
					.createProtocolConfiguration();
			ServiceBinding defaultBinding = protocolConf.getDefaultServiceBindingConfiguration(null)
					.getDefaultBinding();
			return protocolConf.getTipoServizioDefault(defaultBinding);
		} catch (Exception e) {
			throw FaultCode.ERRORE_INTERNO
					.toException("Impossibile determinare il tipo del servizio: " + e.getMessage());
		}
	}

	public static final byte[] exportConfigurazioneApi(RicercaConfigurazioneApi body, MonitoraggioEnv env) {
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		ConfigurazioniGeneraliService configurazioniService = null;
		SearchFormUtilities searchFormUtilities = null;
		HttpRequestWrapper request = null;
				
		try {
			connection = dbManager.getConnectionConfig();
			ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
			configurazioniService = new ConfigurazioniGeneraliService(connection, true, smp, LoggerProperties.getLoggerDAO());
			searchFormUtilities = new SearchFormUtilities();
			request = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo, env.soggetto.getNome(),
					body.getTipo(), FormatoReportEnum.CSV, TipoReport.api);
		}
		catch (Exception e) {
			dbManager.releaseConnectionConfig(connection);
			throw new RuntimeException(e);
		}
		
		String tag = null;
		switch (body.getTipo()) {
			case EROGAZIONE: {			
				ReportisticaHelper.overrideFiltroErogazione(tag, BaseHelper.deserialize(body.getApi(), FiltroErogazione.class), request, env);
				break;
			}
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroFruizione(tag, BaseHelper.deserialize(body.getApi(), FiltroFruizione.class), request, env);
				break;
		}

		try {
			byte[] report = StatsGenerator.generateReport(request, env.context, configurazioniService);
			return report;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			dbManager.releaseConnectionConfig(connection);
		}
	}
	
	public static final Map<String, Object> parseFiltroApiMap(FiltroRicercaRuoloTransazioneEnum tipo,
			String nomeServizio, String tipoServizio, Integer versioneServizio, String soggettoRemoto,
			String soggettoErogatore) {
		
		// In caso di Fruizione, fin'ora si è sempre specificato l'erogatore per mezzo del parametro soggettoRemoto.
		// Per mantenere i controlli corretti sul filtroApi, nel caso FRUIZIONE se viene specificato il soggettoRemoto
		// allora si sta specificando un pezzo del filtroAPI, che va specificato per intero.
		if (tipo == FiltroRicercaRuoloTransazioneEnum.FRUIZIONE) {
			soggettoErogatore = soggettoRemoto;
		}

		if (tipo == FiltroRicercaRuoloTransazioneEnum.EROGAZIONE 
				&& (!StringUtils.isEmpty(soggettoRemoto) || !StringUtils.isEmpty(soggettoErogatore))) {
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nel caso di ruolo <" + tipo
					+ ">, non bisogna specificare né soggetto_remoto né soggetto_erogatore");
		}

		if (tipo == FiltroRicercaRuoloTransazioneEnum.FRUIZIONE 
				&& !StringUtils.isEmpty(soggettoErogatore)
				&& !StringUtils.isEmpty(soggettoRemoto) 
				&& !StringUtils.equals(soggettoRemoto, soggettoErogatore)) {
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Ricerca ambigua. Nel caso di ruolo <"
					+ tipo
					+ ">, soggettoErogatore e soggettoRemoto specificano la stessa informazione."
					+ "Se specificati entrambi allora devono essere uguali");
		}	

		// Se specifico un pezzo del filtroAPI, allora devo avere un filtro completo.
		// (meno i defaults)
		if (nomeServizio != null || tipoServizio != null || versioneServizio != null || soggettoErogatore != null) {
		
			if (nomeServizio == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare il nome_servizio");
			}

			if (tipo == FiltroRicercaRuoloTransazioneEnum.FRUIZIONE && (StringUtils.isEmpty(soggettoRemoto) || StringUtils.isEmpty(soggettoErogatore)) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificando un filtro API per il ruolo <" + tipo
						+ ">, è necessario specificare anche il soggetto_remoto (Erogatore in caso di Fruizioni)");
			}
			
			if (tipo == FiltroRicercaRuoloTransazioneEnum.QUALSIASI && StringUtils.isEmpty(soggettoErogatore)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificando un filtro API per il ruolo <" + tipo
						+ ">, è necessario specificare anche il soggetto_erogatore");
			}
			// TODO: Gli altri due hanno i defaults. (Che dovrei metttere ora?)
		}

		return ReportisticaHelper.buildFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore);
	}
	
	 
	public static final Map<String, Object> parseFiltroApiMap(TransazioneRuoloEnum tipo, String nomeServizio,
			String tipoServizio, Integer versioneServizio, String soggettoRemoto) {
		// In Questo caso non abbiamo il tipo QUALSIASI, quindi parsiamo il filtro senza il soggettoErogatore (ultimo parametro a null)
		FiltroRicercaRuoloTransazioneEnum newTipo = tipo == TransazioneRuoloEnum.EROGAZIONE ? FiltroRicercaRuoloTransazioneEnum.EROGAZIONE : FiltroRicercaRuoloTransazioneEnum.FRUIZIONE;
		return ReportisticaHelper.parseFiltroApiMap(newTipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,null);
	}
	
	private static final Map<String, Object> buildFiltroApiMap(FiltroRicercaRuoloTransazioneEnum tipo, String nomeServizio,
			String tipoServizio, Integer versioneServizio, String soggettoRemoto, String soggettoErogatore) {
		
		// Se non ho specificato nessun pezzo del filtro api, allora per le versioni full è come aver passato un FiltroApi a null.
		if (StringUtils.isEmpty(nomeServizio) && StringUtils.isEmpty(tipoServizio) && StringUtils.isEmpty(soggettoRemoto) && StringUtils.isEmpty(soggettoErogatore) && versioneServizio == null) {
			return null;
		}
		
		LinkedHashMap<String, Object> filtroApi = new LinkedHashMap<>();
		filtroApi.put("nome", nomeServizio);
		filtroApi.put("tipo", tipoServizio);
		filtroApi.put("versione", versioneServizio);
		// Filtro Api
		switch (tipo) {
		case EROGAZIONE:
			break;
		case FRUIZIONE:
			if (!StringUtils.isEmpty(soggettoErogatore)) {
				soggettoRemoto = soggettoErogatore;
			}
			filtroApi.put("erogatore", soggettoRemoto);
			break;
		case QUALSIASI:
			filtroApi.put("erogatore", soggettoErogatore);
			filtroApi.put("soggetto_remoto", soggettoRemoto);
			break;
		}

		return filtroApi;
	}
	
}
