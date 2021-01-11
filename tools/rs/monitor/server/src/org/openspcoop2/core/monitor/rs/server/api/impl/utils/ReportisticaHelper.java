/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.model.BaseOggettoWithSimpleName;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiBase;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiImplementata;
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
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApplicativoRegistrato;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneAzione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneEsiti;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoLocale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoRemoto;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneTokenInfo;
import org.openspcoop2.core.monitor.rs.server.model.TempoMedioRispostaEnum;
import org.openspcoop2.core.monitor.rs.server.model.TempoMedioRispostaTipi;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportMultiLineNumeroTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportMultiLineOccupazioneBanda;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportMultiLineTempoMedioRisposta;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportNumeroTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportOccupazioneBanda;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportTempoMedioRisposta;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
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

	private static void setEsitoCodice(EsitoTransazioneFullSearchEnum tipo , FiltroEsito filtro, HttpRequestWrapper wrap) {
		if(filtro.getCodice()!=null) {
			wrap.overrideParameter(CostantiExporter.ESITO, filtro.getCodice().toString());
		}
		else if(filtro.getCodici()!=null && !filtro.getCodici().isEmpty()) {
			if(filtro.getCodici().size()==1) {
				wrap.overrideParameter(CostantiExporter.ESITO, filtro.getCodici().get(0).toString());
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Con il tipo di esito indicato '"+tipo.toString()+"' può essere indicato solamente un codice");
			}
		}
	}
	public static final void overrideFiltroEsito(FiltroEsito filtro, HttpRequestWrapper wrap, MonitoraggioEnv env) {

		if (filtro != null) {

			EsitoTransazioneFullSearchEnum tipo = (filtro.getTipo() != null) ? filtro.getTipo() : EsitoTransazioneFullSearchEnum.QUALSIASI;
			
			switch (tipo) {
			case QUALSIASI:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_OK);
				setEsitoCodice(tipo, filtro, wrap);
				if(filtro.isEscludiScartate()!=null) {
					wrap.overrideParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE, 
							filtro.isEscludiScartate() ? CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_TRUE : CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_FALSE);
				}
				break;
			case OK:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_OK);
				setEsitoCodice(tipo, filtro, wrap);
				break;
			case FAULT:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_FAULT_APPLICATIVO);
				setEsitoCodice(tipo, filtro, wrap);
				break;
			case FALLITE:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_FALLITE);
				setEsitoCodice(tipo, filtro, wrap);
				if(filtro.isEscludiScartate()!=null) {
					wrap.overrideParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE, 
							filtro.isEscludiScartate() ? CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_TRUE : CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_FALSE);
				}
				break;
			case FALLITE_E_FAULT:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO,
						CostantiExporter.ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO);
				setEsitoCodice(tipo, filtro, wrap);
				if(filtro.isEscludiScartate()!=null) {
					wrap.overrideParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE, 
							filtro.isEscludiScartate() ? CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_TRUE : CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE_FALSE);
				}
				break;
			case ERRORI_CONSEGNA:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_ERRORI_CONSEGNA);
				setEsitoCodice(tipo, filtro, wrap);
				break;
			case RICHIESTE_SCARTATE:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_RICHIESTE_SCARTATE);
				setEsitoCodice(tipo, filtro, wrap);
				break;
			case PERSONALIZZATO:
				
				if(filtro.getCodice()==null && (filtro.getCodici()==null || filtro.getCodici().isEmpty())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Con il tipo di esito indicato '"+tipo.toString()+"' deve essere indicato almeno un codice");
				}
				List<Integer> dettaglioEsito = filtro.getCodici();
				if(dettaglioEsito==null || dettaglioEsito.isEmpty()) {
					dettaglioEsito = new ArrayList<Integer>();
					dettaglioEsito.add(filtro.getCodice());
				}
				
				Iterable<String> esiti = dettaglioEsito.stream()
						.map(e -> e.toString())::iterator;

				wrap.overrideParameter(CostantiExporter.ESITO, String.join(",", esiti));
				
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
	
	public static final void overrideFiltroMittenteFruizione(TipoFiltroMittenteEnum tipo, Object filtro, boolean isDistribuzioneSoggettoRemoto, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;
		if (tipo == null)
			return;

		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(tipo));
		
		switch (tipo) {
		
		case EROGAZIONE_SOGGETTO: {
			throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
		}
		
		case FRUIZIONE_APPLICATIVO: {
			
			if(! (filtro instanceof FiltroMittenteFruizioneApplicativo)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteFruizioneApplicativo.class.getName()+")");
			}
			FiltroMittenteFruizioneApplicativo fAppl = (FiltroMittenteFruizioneApplicativo) filtro;
			wrap.overrideParameter(CostantiExporter.APPLICATIVO, fAppl.getApplicativo());
			break;
		}
		
		case EROGAZIONE_APPLICATIVO: {
			throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
		}
		
		case IDENTIFICATIVO_AUTENTICATO: {
			
			if(! (filtro instanceof FiltroMittenteIdAutenticato)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteIdAutenticato.class.getName()+")");
			}
			FiltroMittenteIdAutenticato fIdAutenticato = (FiltroMittenteIdAutenticato) filtro;
			ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdAutenticato, wrap, env);
			break;
		}
		
		case EROGAZIONE_TOKEN_INFO: {
			throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
		}
		
		case TOKEN_INFO: {
			if(! (filtro instanceof FiltroMittenteTokenClaim)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaim.class.getName()+")");
			}
			FiltroMittenteTokenClaim fClaim = (FiltroMittenteTokenClaim) filtro;
			wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,fClaim.isCaseSensitive() + "");
			break;
		}
		case INDIRIZZO_IP: {
			
			if(! (filtro instanceof FiltroMittenteIndirizzoIP)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteIndirizzoIP.class.getName()+")");
			}
			FiltroMittenteIndirizzoIP indirizzoIP = (FiltroMittenteIndirizzoIP) filtro;
			ReportisticaHelper.overrideFiltroMittenteIndirizzoIP(indirizzoIP, wrap, env);
			break;
		}
		}
	}
	
	public static final void overrideFiltroMittenteErogazione(TipoFiltroMittenteEnum tipo, Object filtro, boolean isDistribuzioneSoggettoRemoto, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;
		if (tipo == null)
			return;
		
		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(tipo));
		
		switch (tipo) {
			case EROGAZIONE_SOGGETTO: {
				
				if(isDistribuzioneSoggettoRemoto) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile nella generazione di un report con distribuzione per soggetto remoto");
				}
				
				if(! (filtro instanceof FiltroMittenteErogazioneSoggetto)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteErogazioneSoggetto.class.getName()+")");
				}
				FiltroMittenteErogazioneSoggetto fSogg = (FiltroMittenteErogazioneSoggetto) filtro;
				wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.tipoSoggetto, fSogg.getSoggetto()).toString());
				break;
			}
			
			case FRUIZIONE_APPLICATIVO: {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.EROGAZIONE.toString()+"'");
			}
			
			case EROGAZIONE_APPLICATIVO: {
				
				if(isDistribuzioneSoggettoRemoto) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile nella generazione di un report con distribuzione per soggetto remoto");
				}
				
				if(! (filtro instanceof FiltroMittenteErogazioneApplicativo)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteErogazioneApplicativo.class.getName()+")");
				}
				FiltroMittenteErogazioneApplicativo fAppl = (FiltroMittenteErogazioneApplicativo) filtro;
				wrap.overrideParameter(CostantiExporter.APPLICATIVO, fAppl.getApplicativo());
				wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.tipoSoggetto, fAppl.getSoggetto()).toString());
				break;
			}
			
			case IDENTIFICATIVO_AUTENTICATO: {
				
				if(! (filtro instanceof FiltroMittenteIdAutenticato)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteIdAutenticato.class.getName()+")");
				}
				FiltroMittenteIdAutenticato fIdAutenticato = (FiltroMittenteIdAutenticato) filtro;
				ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdAutenticato, wrap, env);
				break;
			}
			
			case EROGAZIONE_TOKEN_INFO: {
				if(isDistribuzioneSoggettoRemoto) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile nella generazione di un report con distribuzione per soggetto remoto");
				}
				else {
					if(! (filtro instanceof FiltroMittenteTokenClaimSoggetto)) {
						throw FaultCode.RICHIESTA_NON_VALIDA
						.toException("Identificazione '"+tipo.toString()+"' "
								+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaimSoggetto.class.getName()+")");
					}
					FiltroMittenteTokenClaimSoggetto fClaim = (FiltroMittenteTokenClaimSoggetto) filtro;
					wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
					wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
					wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
					wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE, fClaim.isCaseSensitive() + "");
					if (fClaim.getSoggetto() != null) {
						wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.tipoSoggetto, fClaim.getSoggetto()).toString());
					}
					break;
				}
			}
			
			case TOKEN_INFO: {
				if(! (filtro instanceof FiltroMittenteTokenClaim)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaim.class.getName()+")");
				}
				FiltroMittenteTokenClaim fClaim = (FiltroMittenteTokenClaim) filtro;
				wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
				wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE, fClaim.isCaseSensitive() + "");
				break;
			}
			
			case INDIRIZZO_IP: {
				
				if(! (filtro instanceof FiltroMittenteIndirizzoIP)) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("Identificazione '"+tipo.toString()+"' "
							+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteIndirizzoIP.class.getName()+")");
				}
				FiltroMittenteIndirizzoIP indirizzoIP = (FiltroMittenteIndirizzoIP) filtro;
				ReportisticaHelper.overrideFiltroMittenteIndirizzoIP(indirizzoIP, wrap, env);
				break;
			}
		} // switch
		
	}
		

	
	
	
	public static final void overrideFiltroMittenteQualsiasi(TipoFiltroMittenteEnum tipo, Object filtro, boolean isDistribuzioneSoggettoRemoto, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (filtro == null)
			return;
		if (tipo == null)
			return;

		wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE, Enums.toTipoRicercaMittente(tipo));
		
		switch (tipo) {
		
		case EROGAZIONE_SOGGETTO: {
			throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
		}
		
		case FRUIZIONE_APPLICATIVO: {
			throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
		}
		
		case EROGAZIONE_APPLICATIVO: {
			throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
		}
		
		case IDENTIFICATIVO_AUTENTICATO: {
			
			if(! (filtro instanceof FiltroMittenteIdAutenticato)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteIdAutenticato.class.getName()+")");
			}
			FiltroMittenteIdAutenticato fIdAutenticato = (FiltroMittenteIdAutenticato) filtro;
			ReportisticaHelper.overrideFiltroMittenteIdApplicativo(fIdAutenticato, wrap, env);
			break;
		}
		
		case EROGAZIONE_TOKEN_INFO: {
			if(isDistribuzioneSoggettoRemoto) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con distribuzione per soggetto remoto");
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile nella generazione di un report con il criterio 'ruolo transazione' impostato al valore '"+FiltroRicercaRuoloTransazioneEnum.QUALSIASI.toString()+"'");
			}
		}
		
		case TOKEN_INFO: {
			if(! (filtro instanceof FiltroMittenteTokenClaim)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteTokenClaim.class.getName()+")");
			}
			FiltroMittenteTokenClaim fClaim = (FiltroMittenteTokenClaim) filtro;
			wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,fClaim.isCaseSensitive() + "");
			break;
		}
		
		case INDIRIZZO_IP: {
			
			if(! (filtro instanceof FiltroMittenteIndirizzoIP)) {
				throw FaultCode.RICHIESTA_NON_VALIDA
				.toException("Identificazione '"+tipo.toString()+"' "
						+ "non utilizzabile con il tipo di filtro mittente utilizzato '"+filtro.getClass().getName()+"' (atteso: "+FiltroMittenteIndirizzoIP.class.getName()+")");
			}
			FiltroMittenteIndirizzoIP indirizzoIP = (FiltroMittenteIndirizzoIP) filtro;
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

	public static final void setTipoInformazioneReport(OpzioniGenerazioneReport opzioni, TipoInformazioneReportEnum tipoInformazioneReport) {
		TipoInformazioneReportEnum tipoInfo = tipoInformazioneReport!=null ? tipoInformazioneReport : TipoInformazioneReportEnum.NUMERO_TRANSAZIONI;
		switch (tipoInfo) {
		case NUMERO_TRANSAZIONI:
			opzioni.setTipoInformazione(new TipoInformazioneReportNumeroTransazioni());
			((TipoInformazioneReportNumeroTransazioni)opzioni.getTipoInformazione()).setTipo(tipoInfo);
			break;
		case OCCUPAZIONE_BANDA:
			opzioni.setTipoInformazione(new TipoInformazioneReportOccupazioneBanda());
			((TipoInformazioneReportOccupazioneBanda)opzioni.getTipoInformazione()).setTipo(tipoInfo);
			break;
		case TEMPO_MEDIO_RISPOSTA:
			opzioni.setTipoInformazione(new TipoInformazioneReportTempoMedioRisposta());
			((TipoInformazioneReportTempoMedioRisposta)opzioni.getTipoInformazione()).setTipo(tipoInfo);
			break;
		}
	}
	
	public static final void overrideOpzioniGenerazioneReport(OpzioniGenerazioneReport body, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (body == null)
			return;

		ReportisticaHelper.overrideOpzioniGenerazioneReportBase(body, wrap, env);

		// defaults
		if (body.getTipoInformazione() == null)
			body.setTipoInformazione(new TipoInformazioneReportNumeroTransazioni());
		if (body.getTipoInformazione().getTipo() == null) {
			if(body.getTipoInformazione() instanceof TipoInformazioneReportNumeroTransazioni) {
				((TipoInformazioneReportNumeroTransazioni)body.getTipoInformazione()).setTipo(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI);
			}
			else if(body.getTipoInformazione() instanceof TipoInformazioneReportOccupazioneBanda) {
				((TipoInformazioneReportOccupazioneBanda)body.getTipoInformazione()).setTipo(TipoInformazioneReportEnum.OCCUPAZIONE_BANDA);
			}
			else if(body.getTipoInformazione() instanceof TipoInformazioneReportTempoMedioRisposta) {
				((TipoInformazioneReportTempoMedioRisposta)body.getTipoInformazione()).setTipo(TipoInformazioneReportEnum.TEMPO_MEDIO_RISPOSTA);
			}
		}

		wrap.overrideParameter(CostantiExporter.TIPO_INFORMAZIONE_VISUALIZZATA, Enums.toTipoVisualizzazione.get(body.getTipoInformazione().getTipo()).toString());
		switch (body.getTipoInformazione().getTipo()) {
		case NUMERO_TRANSAZIONI:
			break;
		case OCCUPAZIONE_BANDA: {
			TipoInformazioneReportOccupazioneBanda ob = (TipoInformazioneReportOccupazioneBanda) body.getTipoInformazione();
			OccupazioneBandaEnum val = ob.getOccupazioneBanda();
			if(val==null) {
				val = OccupazioneBandaEnum.COMPLESSIVA;
			}
			wrap.overrideParameter(CostantiExporter.TIPO_BANDA_VISUALIZZATA, Enums.toTipoBanda.get(val).toString());
			break;
		}
		case TEMPO_MEDIO_RISPOSTA: {
			TipoInformazioneReportTempoMedioRisposta tm = (TipoInformazioneReportTempoMedioRisposta) body.getTipoInformazione();
			TempoMedioRispostaEnum val = tm.getTempoMedioRisposta();
			if(val==null) {
				val = TempoMedioRispostaEnum.TOTALE;
			}
			wrap.overrideParameter(CostantiExporter.TIPO_LATENZA_VISUALIZZATA, Enums.toTipoLatenza.get(val).toString());
			break;
		}
		}
	}

	public static final void setTipoInformazioneReportMultiLine(OpzioniGenerazioneReportMultiLine opzioni, TipoInformazioneReportEnum tipoInformazioneReport) {
		TipoInformazioneReportEnum tipoInfo = tipoInformazioneReport!=null ? tipoInformazioneReport : TipoInformazioneReportEnum.NUMERO_TRANSAZIONI;
		switch (tipoInfo) {
		case NUMERO_TRANSAZIONI:
			opzioni.setTipoInformazione(new TipoInformazioneReportMultiLineNumeroTransazioni());
			((TipoInformazioneReportMultiLineNumeroTransazioni)opzioni.getTipoInformazione()).setTipo(tipoInfo);
			break;
		case OCCUPAZIONE_BANDA:
			opzioni.setTipoInformazione(new TipoInformazioneReportMultiLineOccupazioneBanda());
			((TipoInformazioneReportMultiLineOccupazioneBanda)opzioni.getTipoInformazione()).setTipo(tipoInfo);
			break;
		case TEMPO_MEDIO_RISPOSTA:
			opzioni.setTipoInformazione(new TipoInformazioneReportMultiLineTempoMedioRisposta());
			((TipoInformazioneReportMultiLineTempoMedioRisposta)opzioni.getTipoInformazione()).setTipo(tipoInfo);
			break;
		}
	}
	
	public static final void overrideOpzioniGenerazioneReportMultiLine(OpzioniGenerazioneReportMultiLine body,HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;

		ReportisticaHelper.overrideOpzioniGenerazioneReportBase(body, wrap, env);

		// defaults
		if (body.getTipoInformazione() == null)
			body.setTipoInformazione(new TipoInformazioneReportMultiLineNumeroTransazioni());
		if (body.getTipoInformazione().getTipo() == null) {
			if(body.getTipoInformazione() instanceof TipoInformazioneReportMultiLineNumeroTransazioni) {
				((TipoInformazioneReportMultiLineNumeroTransazioni)body.getTipoInformazione()).setTipo(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI);
			}
			else if(body.getTipoInformazione() instanceof TipoInformazioneReportMultiLineOccupazioneBanda) {
				((TipoInformazioneReportMultiLineOccupazioneBanda)body.getTipoInformazione()).setTipo(TipoInformazioneReportEnum.OCCUPAZIONE_BANDA);
			}
			else if(body.getTipoInformazione() instanceof TipoInformazioneReportMultiLineTempoMedioRisposta) {
				((TipoInformazioneReportMultiLineTempoMedioRisposta)body.getTipoInformazione()).setTipo(TipoInformazioneReportEnum.TEMPO_MEDIO_RISPOSTA);
			}
		}
		
		switch (body.getTipoInformazione().getTipo()) {
		case NUMERO_TRANSAZIONI:
			break;
		case OCCUPAZIONE_BANDA: {
			TipoInformazioneReportMultiLineOccupazioneBanda ob = (TipoInformazioneReportMultiLineOccupazioneBanda) body.getTipoInformazione();
			OccupazioneBandaTipi val = ob.getOccupazioneBanda();
			if(val==null) {
				val = new OccupazioneBandaTipi(); // dentro il bean ci sono i default
			}
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
			TipoInformazioneReportMultiLineTempoMedioRisposta tm = (TipoInformazioneReportMultiLineTempoMedioRisposta) body.getTipoInformazione();
			TempoMedioRispostaTipi val = tm.getTempoMedioRisposta();
			if(val==null) {
				val = new TempoMedioRispostaTipi(); // dentro il bean ci sono i default
			}
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
			HttpRequestWrapper wrap, MonitoraggioEnv env) throws Exception {
		if (body == null)
			return;

		ReportisticaHelper.overrideRicercaBaseStatistica(body, wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			ReportisticaHelper.overrideFiltroErogazione(body.getTag(), body.getApi(), wrap, env);
			break;
		case FRUIZIONE:
			ReportisticaHelper.overrideFiltroFruizione(body.getTag(), body.getApi(), wrap, env);
			break;
		case QUALSIASI:
			ReportisticaHelper.overrideFiltroQualsiasi(body.getTag(), body.getApi(), wrap, env);
			break;
		}
	}

	public static final void overrideFiltroFruizione(String tag, FiltroApiSoggetti body, HttpRequestWrapper wrap,
			MonitoraggioEnv env) throws Exception {
		if (body == null) return;
		
		IDSoggetto erogatore = new IDSoggetto(env.tipoSoggetto, body.getErogatore());
		ReportisticaHelper.overrideFiltroApiBase(tag, body, erogatore, wrap, env);
	}

	public static final void overrideFiltroErogazione(String tag, FiltroApiSoggetti body, HttpRequestWrapper wrap, MonitoraggioEnv env) throws Exception {
		if (body == null)
			return;
		IDSoggetto idSoggettoLocale = new IDSoggetto(env.tipoSoggetto, env.nomeSoggettoLocale);
		ReportisticaHelper.overrideFiltroApiBase(tag, body, idSoggettoLocale, wrap, env);
	}
	
	public static final void overrideFiltroQualsiasi(String tag, FiltroApiSoggetti body, HttpRequestWrapper wrap, MonitoraggioEnv env) throws Exception {
		if (body == null)
			return;
		
		IDSoggetto erogatore = new IDSoggetto(env.tipoSoggetto, body.getErogatore());
		ReportisticaHelper.overrideFiltroApiBase(tag, body, erogatore, wrap, env);
		if (!StringUtils.isEmpty(body.getSoggettoRemoto())) {
			IDSoggetto remoto = new IDSoggetto(env.tipoSoggetto, body.getSoggettoRemoto());
			wrap.overrideParameter(CostantiExporter.TRAFFICO_PER_SOGGETTO, remoto.toString() );
		}
		
	}

	public static final void overrideFiltroApiBase(String tag, FiltroApiBase filtro_api, IDSoggetto erogatore, HttpRequestWrapper wrap, MonitoraggioEnv env) throws Exception {
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
		
		if(filtro_api instanceof FiltroApiSoggetti) {
			FiltroApiSoggetti filtroApiSoggetti = (FiltroApiSoggetti) filtro_api;
			if(filtroApiSoggetti.getApiImplementata()!=null) {
				wrap.overrideParameter(CostantiExporter.API,
						ReportisticaHelper.toUriApiImplementata(filtroApiSoggetti.getApiImplementata(), env));
			}
		}
	}

	public static final void overrideRicercaStatisticaDistribuzioneApplicativo(
			RicercaStatisticaDistribuzioneApplicativo body, HttpRequestWrapper wrap, MonitoraggioEnv env) throws Exception {
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
		Connection connectionConfig = null;
		Connection connectionStats = null;
		Connection connectionTransazioni = null;
		StatisticheGiornaliereService statisticheService = null;
		ServiceManagerProperties smp = null;
		boolean uniqueConnection = false;
		try {
			connectionStats = dbManager.getConnectionStatistiche();
			smp = dbManager.getServiceManagerPropertiesTracce();
			
			if(dbManager.getDataSourceStatisticheName().equals(dbManager.getDataSourceTracceName()) && 
					dbManager.getDataSourceStatisticheName().equals(dbManager.getDataSourceConfigName())) {
				// unico datasource
				uniqueConnection = true;
			}
			
			if(uniqueConnection) {
				statisticheService = new StatisticheGiornaliereService(connectionStats, true, smp,
						LoggerProperties.getLoggerDAO());
			}
			else {
				
				Connection ctracce = connectionStats;
				if( !dbManager.getDataSourceStatisticheName().equals(dbManager.getDataSourceTracceName()) ) {
					connectionTransazioni = dbManager.getConnectionTracce();
					ctracce = connectionTransazioni;
				}
				
				Connection cconfig = connectionStats;
				if( !dbManager.getDataSourceStatisticheName().equals(dbManager.getDataSourceConfigName()) ) {
					connectionConfig = dbManager.getConnectionConfig();
					cconfig = connectionConfig;
				}
				
				statisticheService = new StatisticheGiornaliereService(cconfig, connectionStats, ctracce, true, smp,
						LoggerProperties.getLoggerDAO());
			}

		} catch (Exception e) {
			dbManager.releaseConnectionStatistiche(connectionStats);
			if(!uniqueConnection) {
				dbManager.releaseConnectionConfig(connectionConfig);
				dbManager.releaseConnectionTracce(connectionTransazioni);
			}
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}

		try {
			return StatsGenerator.generateReport(request, context, statisticheService);
		} catch (NotFoundException e) {
			throw FaultCode.NOT_FOUND.toException(e);
		} catch (Exception e) {
			throw FaultCode.ERRORE_INTERNO.toException(e);
		} finally {
			dbManager.releaseConnectionStatistiche(connectionStats);
			if(!uniqueConnection) {
				dbManager.releaseConnectionConfig(connectionConfig);
				dbManager.releaseConnectionTracce(connectionTransazioni);
			}
		}
	}

	public static final byte[] getReportDistribuzioneApi(RicercaStatisticaDistribuzioneApi body, MonitoraggioEnv env)
			throws Exception {

		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper request = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.api);

		ReportisticaHelper.overrideRicercaBaseStatistica(body, request, env);
		// Api Implementata
		if(body.isDistinguiApiImplementata()!=null) {
			request.overrideParameter(CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE,
					body.isDistinguiApiImplementata() ? CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE_TRUE : CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE_FALSE);
		}
		// Mittente
		if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
			switch (body.getTipo()) {
			case EROGAZIONE:
				ReportisticaHelper.overrideFiltroMittenteErogazione(body.getMittente().getIdentificazione(), body.getMittente(), false,
						request,env);
				break;
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroMittenteFruizione(body.getMittente().getIdentificazione(), body.getMittente(), false,
						request, env);
				break;
			case QUALSIASI:
				ReportisticaHelper.overrideFiltroMittenteQualsiasi(body.getMittente().getIdentificazione(), body.getMittente(), false,
						request, env);
				break;
			}
		}
		// FiltroEsito
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), request, env);
		// Soggetto Erogatore
		if(body.getSoggettoErogatore()!=null) {
			if(!FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.equals(body.getTipo())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il criterio di ricerca per soggetto erogatore è indicabile solamente se si genera report relativi a transazioni con ruolo '"+FiltroRicercaRuoloTransazioneEnum.FRUIZIONE.toString()+"'");
			}
			BaseOggettoWithSimpleName base = body.getSoggettoErogatore();
			if(base.getNome()!=null) {
				request.overrideParameter(CostantiExporter.DESTINATARIO, new IDSoggetto(env.tipoSoggetto,base.getNome()));
			}
		}
		// Opzioni Report
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), request, env);

		return ReportisticaHelper.generateReport(request, env.context);
	}

	public static final byte[] getReportDistribuzioneApplicativo(RicercaStatisticaDistribuzioneApplicativoRegistrato body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.applicativo);
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		if(body.getSoggettoMittente()!=null && !StringUtils.isEmpty(body.getSoggettoMittente())) {
			if(body.getTipo()!=null && FiltroRicercaRuoloTransazioneEnum.EROGAZIONE.equals(body.getTipo())) {
				wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.tipoSoggetto, body.getSoggettoMittente()).toString());
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nel caso di ruolo <" + body.getTipo()
						+ ">, non è possibile specificare il soggetto mittente");
			}
		}
		
		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneAzione(RicercaStatisticaDistribuzioneAzione body,MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.azione);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);

		if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
			switch (body.getTipo()) {
			case EROGAZIONE:
				ReportisticaHelper.overrideFiltroMittenteErogazione(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroMittenteFruizione(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case QUALSIASI:
				ReportisticaHelper.overrideFiltroMittenteQualsiasi(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			}
		}

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneEsiti(RicercaStatisticaDistribuzioneEsiti body,MonitoraggioEnv env) throws Exception {
		
		if (body.getAzione() != null && body.getApi() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Se viene specificato il filtro 'azione' è necessario specificare anche il filtro Api");
		}
		
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.esiti);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		// Mittente
		if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
			switch (body.getTipo()) {
			case EROGAZIONE:
				ReportisticaHelper.overrideFiltroMittenteErogazione( body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroMittenteFruizione( body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case QUALSIASI:
				ReportisticaHelper.overrideFiltroMittenteQualsiasi( body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			}
		}
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());
		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneIdAutenticato(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(),
				TipoReport.identificativo_autenticato);

		ReportisticaHelper.overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);

		return ReportisticaHelper.generateReport(wrap, env.context);
	}
	
	public static final byte[] getReportDistribuzioneIndirizzoIP(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(),
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
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.soggetto_remoto);
		ReportisticaHelper.overrideRicercaBaseStatistica(body, wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		switch (body.getTipo()) {
		case EROGAZIONE:
			if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
				ReportisticaHelper.overrideFiltroMittenteErogazione(body.getMittente().getIdentificazione(), body.getMittente(), true, wrap, env);
			}
			if(body.getApi()!=null) {
				ReportisticaHelper.overrideFiltroErogazione(body.getTag(), body.getApi(), wrap,env);
			}
			break;
		case FRUIZIONE:
			if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
				ReportisticaHelper.overrideFiltroMittenteFruizione(body.getMittente().getIdentificazione(), body.getMittente(), true, wrap, env);
			}
			if(body.getApi()!=null) {
				ReportisticaHelper.overrideFiltroFruizione(body.getTag(), body.getApi(), wrap, env);
			}
			break;
		case QUALSIASI:
			if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
				ReportisticaHelper.overrideFiltroMittenteQualsiasi(body.getMittente().getIdentificazione(), body.getMittente(), true, wrap, env);
			}
			if(body.getApi()!=null) {
				ReportisticaHelper.overrideFiltroQualsiasi(body.getTag(), body.getApi(), wrap, env);
			}
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
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.temporale);

		ReportisticaHelper.overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		ReportisticaHelper.overrideOpzioniGenerazioneReportMultiLine(body.getReport(), wrap, env);
		if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
			switch (body.getTipo()) {
			case EROGAZIONE:
				ReportisticaHelper.overrideFiltroMittenteErogazione(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroMittenteFruizione(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case QUALSIASI:
				ReportisticaHelper.overrideFiltroMittenteQualsiasi(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			}
		}
		ReportisticaHelper.overrideFiltroEsito(body.getEsito(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return ReportisticaHelper.generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneTokenInfo(RicercaStatisticaDistribuzioneTokenInfo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.nomeSoggettoLocale, body.getTipo(), body.getReport().getFormato(), TipoReport.token_info);

		ReportisticaHelper.overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);
		wrap.overrideParameter(CostantiExporter.CLAIM, Enums.toClaim.get(body.getClaim()).toString());

		if(body.getSoggetto()!=null) {
			if(!FiltroRicercaRuoloTransazioneEnum.EROGAZIONE.equals(body.getTipo())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il criterio di ricerca per soggetto è indicabile solamente se si genera report relativi a transazioni con ruolo '"+FiltroRicercaRuoloTransazioneEnum.EROGAZIONE.toString()+"'");
			}
			BaseOggettoWithSimpleName base = body.getSoggetto();
			if(base.getNome()!=null) {
				wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.tipoSoggetto, base.getNome()).toString());
			}
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

		if(body.getMittente()!=null && body.getMittente().getIdentificazione()!=null) {
			switch (body.getTipo()) {
			case EROGAZIONE:
				ReportisticaHelper.overrideFiltroMittenteErogazione(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroMittenteFruizione(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			case QUALSIASI:
				ReportisticaHelper.overrideFiltroMittenteQualsiasi(body.getMittente().getIdentificazione(), body.getMittente(), false, wrap, env);
				break;
			}
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

	public static final byte[] exportConfigurazioneApi(RicercaConfigurazioneApi body, MonitoraggioEnv env) throws Exception {
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
			request = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo, env.nomeSoggettoLocale,
					body.getTipo(), FormatoReportEnum.CSV, TipoReport.api);
		}
		catch (Exception e) {
			dbManager.releaseConnectionConfig(connection);
			throw new RuntimeException(e);
		}
		
		String tag = null;
		switch (body.getTipo()) {
			case EROGAZIONE: {			
				ReportisticaHelper.overrideFiltroErogazione(tag, body.getApi(), request, env);
				break;
			}
			case FRUIZIONE:
				ReportisticaHelper.overrideFiltroFruizione(tag, body.getApi(), request, env);
				break;
		}

		try {
			byte[] report = StatsGenerator.generateReport(request, env.context, configurazioniService);
			return report;
		} catch (NotFoundException e) {
			throw FaultCode.NOT_FOUND.toException(e);
		} catch (Exception e) {
			throw FaultCode.ERRORE_INTERNO.toException(e);
		} finally {
			dbManager.releaseConnectionConfig(connection);
		}
	}
	
	public static final FiltroApiImplementata parseUriApiImplementata(String uriApiImplementata, MonitoraggioEnv env) throws Exception {
		//  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione
		String pattern1 = "^[a-z]{2,20}/[0-9A-Za-z]+:[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$";
		String pattern2 = "^[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$";
		IDAccordo idAccordo = null;
		try {
			if(RegularExpressionEngine.isMatch(uriApiImplementata, pattern1)) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriApiImplementata);
			}
			else if(RegularExpressionEngine.isMatch(uriApiImplementata, pattern2)) {
				String uriCompleto = env.tipoSoggetto+"/"+env.nomeSoggettoLocale+":"+uriApiImplementata;
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriCompleto);
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La uri fornita '"+uriApiImplementata+"' non rispetta il formato atteso '"+pattern1+"|"+pattern2+"'");
			}
		}catch(RegExpNotFoundException e) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("La uri fornita '"+uriApiImplementata+"' non rispetta il formato atteso '"+pattern1+"|"+pattern2+"': "+e.getMessage());
		}
		FiltroApiImplementata filtro = new FiltroApiImplementata();
		filtro.setNome(idAccordo.getNome());
		filtro.setReferente(idAccordo.getSoggettoReferente().getNome());
		filtro.setVersione(idAccordo.getVersione());
		return filtro;
	}
	
	public static final String toUriApiImplementata(FiltroApiImplementata filtroApiImplementata, MonitoraggioEnv env) throws Exception {
		//  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione
		String tipoSoggetto = env.tipoSoggetto;
		String nomeSoggetto = filtroApiImplementata.getReferente()!=null ? filtroApiImplementata.getReferente() : env.nomeSoggettoLocale;
		if(nomeSoggetto==null || "".equals(nomeSoggetto)) {
			if(!env.supportatoSoggettoReferenteAPI) {
				// Recupero Soggetto Default per l'API
				if(env.soggettoReferenteAPIDefault!=null) {
					nomeSoggetto = env.soggettoReferenteAPIDefault.getNome();
				}
				
			}
		}
		return IDAccordoFactory.getInstance().getUriFromValues(filtroApiImplementata.getNome(), tipoSoggetto, nomeSoggetto, filtroApiImplementata.getVersione());
	}
	
	public static final FiltroApiSoggetti parseFiltroApiMap(FiltroRicercaRuoloTransazioneEnum tipo,
			String nomeServizio, String tipoServizio, Integer versioneServizio, String soggettoRemoto,
			String soggettoErogatore,
			MonitoraggioEnv env, String uriApiImplementata) throws Exception {
		
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
		if (nomeServizio != null || tipoServizio != null || soggettoErogatore != null
				// || versioneServizio != null  default value is 1
				) {
		
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

		return ReportisticaHelper.buildFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore, 
				env, uriApiImplementata);
	}
	
	public static final FiltroApiSoggetti parseFiltroApiMap(TransazioneRuoloEnum tipo, String nomeServizio,
			String tipoServizio, Integer versioneServizio, String soggettoRemoto,
			MonitoraggioEnv env, String uriApiImplementata) throws Exception {
		// In Questo caso non abbiamo il tipo QUALSIASI, quindi parsiamo il filtro senza il soggettoErogatore (ultimo parametro a null)
		FiltroRicercaRuoloTransazioneEnum newTipo = tipo == TransazioneRuoloEnum.EROGAZIONE ? FiltroRicercaRuoloTransazioneEnum.EROGAZIONE : FiltroRicercaRuoloTransazioneEnum.FRUIZIONE;
		return ReportisticaHelper.parseFiltroApiMap(newTipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,null, 
				env, uriApiImplementata);
	}
	
	private static final FiltroApiSoggetti buildFiltroApiMap(FiltroRicercaRuoloTransazioneEnum tipo, String nomeServizio,
			String tipoServizio, Integer versioneServizio, String soggettoRemoto, String soggettoErogatore,
			MonitoraggioEnv env, String uriApiImplementata) throws Exception {

		FiltroApiImplementata apiImplementata = null;
		if(uriApiImplementata!=null && !"".equals(uriApiImplementata)) {
			apiImplementata = ReportisticaHelper.parseUriApiImplementata(uriApiImplementata, env);
		}
		
		// Se non ho specificato nessun pezzo del filtro api, allora per le versioni full è come aver passato un FiltroApi a null.
		if (StringUtils.isEmpty(nomeServizio) && StringUtils.isEmpty(tipoServizio) && StringUtils.isEmpty(soggettoRemoto) && StringUtils.isEmpty(soggettoErogatore) 
				//&& versioneServizio == null default value is 1
				) {
			if(apiImplementata!=null) {
				FiltroApiSoggetti filtroApiBase = new FiltroApiSoggetti();
				filtroApiBase.setApiImplementata(apiImplementata);
				return filtroApiBase;
			}
			return null;
		}
		
		FiltroApiSoggetti filtroApiBase = new FiltroApiSoggetti();
		
		filtroApiBase.setNome(nomeServizio);
		filtroApiBase.setTipo(tipoServizio);
		if(nomeServizio!=null || tipoServizio!=null) {
			filtroApiBase.setVersione(versioneServizio!=null ? versioneServizio : 1);
		}
		
		switch (tipo) {
		case EROGAZIONE:
			break;
		case FRUIZIONE:
			if (!StringUtils.isEmpty(soggettoErogatore)) {
				soggettoRemoto = soggettoErogatore;
			}
			filtroApiBase.setErogatore(soggettoRemoto);
			break;
		case QUALSIASI:
			filtroApiBase.setErogatore(soggettoErogatore);
			filtroApiBase.setSoggettoRemoto(soggettoRemoto);
			break;
		}
		
		filtroApiBase.setApiImplementata(apiImplementata);
		
		return filtroApiBase;
	}
	
}
