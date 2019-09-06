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

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.deserializeDefault;
import static org.openspcoop2.utils.service.beans.utils.BaseHelper.deserializev2;
import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

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
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiBase;
import org.openspcoop2.core.monitor.rs.server.model.FiltroErogazione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneSoggetto;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteErogazioneTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteFruizioneTokenClaim;
import org.openspcoop2.core.monitor.rs.server.model.FiltroMittenteIdApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.OccupazioneBandaEnum;
import org.openspcoop2.core.monitor.rs.server.model.OccupazioneBandaTipi;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportBase;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportMultiLine;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatistica;
import org.openspcoop2.core.monitor.rs.server.model.RicercaBaseStatisticaSoggetti;
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
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
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

		if (filtro != null && filtro.getTipo() != null) {

			switch (filtro.getTipo()) {
			case OK:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_OK);
				wrap.overrideParameter(CostantiExporter.ESITO, evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case FAULT:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_FAULT_APPLICATIVO);
				wrap.overrideParameter(CostantiExporter.ESITO, evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case ERROR:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO, CostantiExporter.ESITO_GRUPPO_ERROR);
				wrap.overrideParameter(CostantiExporter.ESITO, evalnull(() -> filtro.getDettaglio().toString()));
				break;
			case ERROR_OR_FAULT:
				wrap.overrideParameter(CostantiExporter.ESITO_GRUPPO,
						CostantiExporter.ESITO_GRUPPO_ERROR_FAULT_APPLICATIVO);
				wrap.overrideParameter(CostantiExporter.ESITO, evalnull(() -> filtro.getDettaglio().toString()));
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
		SimpleDateFormat sdf = Utilities.getSimpleDateFormatMs();
		wrap.overrideParameter(CostantiExporter.DATA_INIZIO,
				sdf.format(body.getIntervalloTemporale().getDataInizio().toDate()));
		wrap.overrideParameter(CostantiExporter.DATA_FINE,
				sdf.format(body.getIntervalloTemporale().getDataFine().toDate()));
		wrap.overrideParameter(CostantiExporter.TIPO_UNITA_TEMPORALE,
				Enums.toStatisticType.get(body.getUnitaTempo()).toString());
		wrap.overrideParameter(CostantiExporter.TIPOLOGIA,
				Enums.toTipologiaRuoloTransazione.get(body.getTipo()).toString());
	}

	public static final void overrideFiltroMittenteIdApplicativo(FiltroMittenteIdApplicativo filtro,
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
	
	public static final FiltroMittenteErogazioneTokenClaim deserializeFiltroMittenteErogazioneTokenClaim(Object o) {
		FiltroMittenteErogazioneTokenClaim fClaim = deserializeDefault(o,FiltroMittenteErogazioneTokenClaim.class);
		if (fClaim.getClaim() == null || StringUtils.isEmpty(fClaim.getId())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(fClaim.getClass().getName() + ": Indicare i campi obbligatori claim e id"); 
		}
		return fClaim;
	}
	
	public static final FiltroMittenteErogazioneApplicativo deserializeFiltroMittenteErogazioneApplicativo(Object o) {
		FiltroMittenteErogazioneApplicativo ret = deserializeDefault(o,FiltroMittenteErogazioneApplicativo.class);
		if (StringUtils.isEmpty(ret.getSoggetto()) || StringUtils.isEmpty(ret.getApplicativo())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteErogazioneApplicativo.class.getName() + ": Indicare i campi obbligatori 'soggetto' e 'applicativo'");
		}
		return ret;
	}
	
	public static final FiltroMittenteErogazioneSoggetto deserializeFiltroMittenteErogazioneSoggetto(Object o) {
		FiltroMittenteErogazioneSoggetto ret= deserializeDefault(o,FiltroMittenteErogazioneSoggetto.class);
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
				FiltroMittenteErogazioneApplicativo fAppl = deserializeFiltroMittenteErogazioneApplicativo(filtro.getId());
				wrap.overrideParameter(CostantiExporter.APPLICATIVO, fAppl.getApplicativo());
				wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.soggetto.getTipo(), fAppl.getSoggetto()).toString());
				break;
			}
			case IDENTIFICATIVO_AUTENTICATO: {
				FiltroMittenteIdApplicativo fIdent = deserializeFiltroMittenteIdApplicativo(filtro.getId());
				overrideFiltroMittenteIdApplicativo(fIdent, wrap, env);
				break;
			}
			case SOGGETTO: {
				FiltroMittenteErogazioneSoggetto fSogg = deserializeFiltroMittenteErogazioneSoggetto(filtro.getId());
				wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.soggetto.getTipo(), fSogg.getSoggetto()).toString());
				break;
			}
			case TOKEN_INFO: {
				FiltroMittenteErogazioneTokenClaim fClaim = deserializeFiltroMittenteErogazioneTokenClaim(filtro.getId());
				wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
				wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
				wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE, fClaim.isCaseSensitive() + "");
				if (fClaim.getSoggetto() != null) {
					wrap.overrideParameter(CostantiExporter.MITTENTE, new IDSoggetto(env.soggetto.getTipo(), fClaim.getSoggetto()).toString());
				}
				break;
			}
			} // switch
		}

	}
	
	public static final FiltroMittenteIdApplicativo deserializeFiltroMittenteIdApplicativo(Object o) {
		FiltroMittenteIdApplicativo ret = deserializeDefault(o, FiltroMittenteIdApplicativo.class);
		if (ret.getAutenticazione() == null || StringUtils.isEmpty(ret.getId()))
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteIdApplicativo.class.getName() + ": Indicare i camp obbligatori 'autenticazione' e 'id'");

		return ret;
	}

	public static final FiltroMittenteFruizioneTokenClaim deserializeFiltroMittenteFruizioneTokenClaim(Object o) {
		FiltroMittenteFruizioneTokenClaim fClaim = deserializeDefault(o,FiltroMittenteFruizioneTokenClaim.class);
		if (fClaim.getClaim() == null || StringUtils.isEmpty(fClaim.getId()) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(FiltroMittenteFruizioneTokenClaim.class.getName() + ": Indicare i campi obbligatori 'claim' e 'id'");
		}
		
		return fClaim;
	}
	
	public static final FiltroMittenteFruizioneApplicativo deserializeFiltroMittenteFruizioneApplicativo(Object o) {
		FiltroMittenteFruizioneApplicativo fAppl = deserializeDefault(o, FiltroMittenteFruizioneApplicativo.class);
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
			FiltroMittenteFruizioneApplicativo fAppl = deserializeFiltroMittenteFruizioneApplicativo(filtro.getId());
			wrap.overrideParameter(CostantiExporter.APPLICATIVO, fAppl.getApplicativo());
			break;
		}
		case IDENTIFICATIVO_AUTENTICATO: {
			FiltroMittenteIdApplicativo fIdent = deserializeFiltroMittenteIdApplicativo(filtro.getId());
			overrideFiltroMittenteIdApplicativo(fIdent, wrap, env);
			break;
		}
		case TOKEN_INFO: {
			FiltroMittenteFruizioneTokenClaim fClaim = deserializeFiltroMittenteFruizioneTokenClaim(filtro.getId());			
			wrap.overrideParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM, Enums.toTokenClaim.get(fClaim.getClaim()));
			wrap.overrideParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE, fClaim.getId());
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA, fClaim.isRicercaEsatta() + "");
			wrap.overrideParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE,fClaim.isCaseSensitive() + "");
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

		overrideOpzioniGenerazioneReportBase(body, wrap, env);

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

		overrideOpzioniGenerazioneReportBase(body, wrap, env);

		// defaults
		if (body.getTipoInformazione().getTipo() == null)
			body.getTipoInformazione().setTipo(TipoInformazioneReportEnum.NUMERO_TRANSAZIONI);

		switch (body.getTipoInformazione().getTipo()) {
		case NUMERO_TRANSAZIONI:
			break;
		case OCCUPAZIONE_BANDA: {
			OccupazioneBandaTipi val = deserializeDefault(body.getTipoInformazione().getValori(),OccupazioneBandaTipi.class);
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
			TempoMedioRispostaTipi val = deserializeDefault(body.getTipoInformazione().getValori(),TempoMedioRispostaTipi.class);
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

		overrideRicercaBaseStatistica(body, wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroErogazione(deserializev2(body.getApi(), FiltroErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			overrideFiltroFruizione(deserializev2(body.getApi(), FiltroFruizione.class), wrap, env);
			break;
		}
	}

	public static final void overrideFiltroFruizione(FiltroFruizione body, HttpRequestWrapper wrap,
			MonitoraggioEnv env) {
		if (body == null) return;
		
		IDSoggetto erogatore = new IDSoggetto(env.soggetto.getTipo(), body.getErogatore());
		
		overrideFiltroApiBase(body, erogatore, wrap, env);
		// TODO: Forse fare l'override di destinatario intendendolo come l'erogatore non
		// va bene, ricontrolla ovunque.
		if (body.getErogatore() != null)
			wrap.overrideParameter(CostantiExporter.DESTINATARIO, erogatore.toString() );
	}

	public static final void overrideFiltroErogazione(FiltroErogazione body, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;
		overrideFiltroApiBase(body, env.soggetto, wrap, env);
	}

	public static final void overrideFiltroApiBase(FiltroApiBase body, IDSoggetto erogatore, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;

		if (body.getTipo() == null) {
			try {
				IProtocolConfiguration protocolConf = env.protocolFactoryMgr
						.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration();
				ServiceBinding defaultBinding = protocolConf.getDefaultServiceBindingConfiguration(null)
						.getDefaultBinding();
				body.setTipo(protocolConf.getTipoServizioDefault(defaultBinding));
			} catch (Exception e) {
				throw FaultCode.ERRORE_INTERNO
						.toException("Impossibile determinare il tipo del servizio: " + e.getMessage());
			}

		}
		
		if (body.getVersione() == null) {
			body.setVersione(1);
		}

		wrap.overrideParameter(CostantiExporter.SERVIZIO,
				buildNomeServizioForOverride(body.getNome(), body.getTipo(), body.getVersione(), Optional.of(erogatore)));
	}

	public static final void overrideRicercaStatisticaDistribuzioneApplicativo(
			RicercaStatisticaDistribuzioneApplicativo body, HttpRequestWrapper wrap, MonitoraggioEnv env) {
		if (body == null)
			return;

		overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());
		overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		overrideFiltroEsito(body.getEsito(), wrap, env);
	}

	// public static final void
	// overridericercaStatisticaDistribuzioneSoggettoLocale(/RicercaStatisticaDistribuzioneSoggettoLocale)

	public static final byte[] generateReport(HttpRequestWrapper request, IContext context) throws Exception {
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		StatisticheGiornaliereService statisticheService = null;
		ServiceManagerProperties smp = null;
		try {
			connection = dbManager.getConnection();
			smp = dbManager.getServiceManagerProperties();
			statisticheService = new StatisticheGiornaliereService(connection, true, smp,
					LoggerProperties.getLoggerDAO());
		} catch (Exception e) {
			dbManager.releaseConnection(connection);
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}

		try {
			return StatsGenerator.generateReport(request, context, statisticheService);
		} catch (Exception e) {
			throw FaultCode.NOT_FOUND.toException(e);
		} finally {
			dbManager.releaseConnection(connection);
		}
	}

	public static final byte[] getReportDistribuzioneApi(RicercaStatisticaDistribuzioneApi body, MonitoraggioEnv env)
			throws Exception {

		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper request = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.api);

		overrideRicercaBaseStatistica(body, request, env);
		// Mittente
		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroMittenteErogazione(
					deserializev2(body.getMittente(), FiltroMittenteErogazione.class), request,
					env);
			break;
		case FRUIZIONE:
			overrideFiltroMittenteFruizione(
					deserializev2(body.getMittente(), FiltroMittenteFruizione.class), request, env);
			break;
		}
		// FiltroEsito
		overrideFiltroEsito(body.getEsito(), request, env);
		// Soggetto Erogatore
		if (body.getTipo() == TransazioneRuoloEnum.FRUIZIONE && body.getSoggettoErogatore() != null) {
			request.overrideParameter(CostantiExporter.DESTINATARIO, new IDSoggetto(env.soggetto.getTipo(),deserializev2(body.getSoggettoErogatore(), String.class)));
		}
		// Opzioni Report
		overrideOpzioniGenerazioneReport(body.getReport(), request, env);

		return generateReport(request, env.context);
	}

	public static final byte[] getReportDistribuzioneApplicativo(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.applicativo);

		overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		overrideFiltroEsito(body.getEsito(), wrap, env);
		overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneAzione(RicercaStatisticaDistribuzioneAzione body,MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.azione);

		overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		overrideFiltroEsito(body.getEsito(), wrap, env);
		overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroMittenteErogazione(deserializev2(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			overrideFiltroMittenteFruizione(deserializev2(body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
		}

		return generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneEsiti(RicercaStatisticaDistribuzioneEsiti body,MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.esiti);

		overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		// Mittente
		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroMittenteErogazione( deserializev2(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			overrideFiltroMittenteFruizione( deserializev2( body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
		}

		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());
		return generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneIdAutenticato(RicercaStatisticaDistribuzioneApplicativo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(),
				TipoReport.identificativo_autenticato);

		ReportisticaHelper.overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);

		return generateReport(wrap, env.context);
	}


	public static final byte[] getReportDistribuzioneSoggettoRemoto(RicercaStatisticaDistribuzioneSoggettoRemoto body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.soggetto_remoto);

		overrideRicercaBaseStatistica(body, wrap, env);
		overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);
		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroMittenteErogazione(deserializev2(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			overrideFiltroErogazione(deserializev2(body.getApi(), FiltroErogazione.class), wrap,env);
			break;
		case FRUIZIONE:
			overrideFiltroMittenteFruizione(deserializev2(body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
			overrideFiltroFruizione(deserializev2(body.getApi(), FiltroFruizione.class), wrap, env);
			break;
		}
		overrideFiltroEsito(body.getEsito(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneTemporale(RicercaStatisticaAndamentoTemporale body,MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.temporale);

		overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		overrideOpzioniGenerazioneReportMultiLine(body.getReport(), wrap, env);
		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroMittenteErogazione(deserializev2(body.getMittente(), FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			overrideFiltroMittenteFruizione(deserializev2(body.getMittente(), FiltroMittenteFruizione.class), wrap, env);
			break;
		}
		overrideFiltroEsito(body.getEsito(), wrap, env);
		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneTokenInfo(RicercaStatisticaDistribuzioneTokenInfo body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo,
				env.soggetto.getNome(), body.getTipo(), body.getReport().getFormato(), TipoReport.token_info);

		overrideRicercaStatisticaDistribuzioneApplicativo(body, wrap, env);
		wrap.overrideParameter(CostantiExporter.CLAIM, Enums.toClaim.get(body.getClaim()).toString());

		if (body.getTipo() == TransazioneRuoloEnum.EROGAZIONE && body.getSoggetto() != null) {
			String nomeMittente = deserializev2(body.getSoggetto(), String.class);
			wrap.overrideParameter(CostantiExporter.MITTENTE,new IDSoggetto(env.soggetto.getTipo(), nomeMittente).toString());
		}

		return generateReport(wrap, env.context);
	}

	public static final byte[] getReportDistribuzioneSoggettoLocale(RicercaStatisticaDistribuzioneSoggettoLocale body,
			MonitoraggioEnv env) throws Exception {
		SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
		HttpRequestWrapper wrap = searchFormUtilities.getHttpRequestWrapper(env.context, env.profilo, null,
				body.getTipo(), body.getReport().getFormato(), TipoReport.soggetto_locale);

		overrideRicercaBaseStatisticaSoggetti(body, wrap, env);
		overrideFiltroEsito(body.getEsito(), wrap, env);
		overrideOpzioniGenerazioneReport(body.getReport(), wrap, env);

		switch (body.getTipo()) {
		case EROGAZIONE:
			overrideFiltroMittenteErogazione(deserializev2(body.getMittente(),FiltroMittenteErogazione.class), wrap, env);
			break;
		case FRUIZIONE:
			overrideFiltroMittenteFruizione(deserializev2(body.getMittente(),FiltroMittenteFruizione.class), wrap, env);
		}

		wrap.overrideParameter(CostantiExporter.AZIONE, body.getAzione());

		return generateReport(wrap, env.context);
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

	public static final Map<String, Object> buildFiltroApiMap(TransazioneRuoloEnum tipo, String nomeServizio,
			String tipoServizio, Integer versioneServizio, String soggettoRemoto) {
		LinkedHashMap<String, Object> filtroApi = new LinkedHashMap<>();
		filtroApi.put("nome", nomeServizio);
		filtroApi.put("tipo", tipoServizio);
		filtroApi.put("versione", versioneServizio);
		// Filtro Api
		switch (tipo) {
		case EROGAZIONE:
			break;
		case FRUIZIONE:
			filtroApi.put("erogatore", soggettoRemoto);
			break;
		}

		return filtroApi;
	}

	/*
	 * public static final <T> Map<String,Object> objectToMap(T object, Class<T>
	 * type) throws IllegalAccessException, InvocationTargetException,
	 * NoSuchMethodException { PropertyDescriptor[] descriptors =
	 * PropertyUtils.getPropertyDescriptors(object);
	 * 
	 * 
	 * //Field[] field = LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
	 * for (Field field : type.getDeclaredFields() ) { field.setAccessible(true);
	 * 
	 * Class<?> source = field.getDeclaringClass(); if ( source == String.class ||
	 * source == Integer.class || source == Boolean.class || source == boolean.class
	 * || source == Integer.class ) { ret.put(field.getName(),
	 * BeanUtils.getProperty(object, field.getName())); // TODO: Trasformare in
	 * jsoncase il secondo field.getName() }
	 * 
	 * if ( source.isEnum() ) { ret.put(field.getName(),
	 * BeanUtils.getProperty(object, field.getName()).toString()); // TODO:
	 * Trasformare in jsoncase il secondo field.getName() }
	 * 
	 * if ( source == (new byte[0]).getClass() ) { ret.put(field.getName(),
	 * BeanUtils.getProperty(object, field.getName()).toString()); // TODO:
	 * Trasformare in jsoncase il secondo field.getName() byte[] v =
	 * BeanUtils.getProperty(object, field.getName()); }
	 * 
	 * 
	 * }
	 * 
	 * return ret;
	 * 
	 * }
	 */

}
