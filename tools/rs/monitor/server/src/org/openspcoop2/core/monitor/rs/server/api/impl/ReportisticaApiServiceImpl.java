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
package org.openspcoop2.core.monitor.rs.server.api.impl;

import java.sql.Connection;
import java.util.List;

import org.joda.time.DateTime;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.monitor.rs.server.api.ReportisticaApi;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.Converter;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.MonitoraggioEnv;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.ReportisticaHelper;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.SearchFormUtilities;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneSimpleSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.InfoImplementazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.ListaRiepilogoApi;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportMultiLine;
import org.openspcoop2.core.monitor.rs.server.model.RicercaConfigurazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaAndamentoTemporale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneAzione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneEsiti;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoLocale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoRemoto;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneTokenInfo;
import org.openspcoop2.core.monitor.rs.server.model.Riepilogo;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportMultiLine;
import org.openspcoop2.core.monitor.rs.server.model.TipoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimEnum;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.dao.ConfigurazioniGeneraliService;

/**
 * ReportisticaApiServiceImpl
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReportisticaApiServiceImpl extends BaseImpl implements ReportisticaApi {

	public ReportisticaApiServiceImpl() {
		super(org.slf4j.LoggerFactory.getLogger(ReportisticaApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception {
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

	/**
	 * Recupera la configurazione di un servizio
	 *
	 * Consente di recuperare la configurazione di un servizio esportandola in
	 * formato csv
	 *
	 */
	@Override
	public byte[] exportConfigurazioneApiByFullSearch(RicercaConfigurazioneApi body, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.exportConfigurazioneApi(body, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Recupera la configurazione di un servizio
	 *
	 * Consente di recuperare la configurazione di un servizio esportandola in
	 * formato csv
	 *
	 */
	@Override
	public byte[] exportConfigurazioneApiBySimpleSearch(TransazioneRuoloEnum tipo, ProfiloEnum profilo, String soggetto,
			String soggettoRemoto, String nomeServizio, String tipoServizio, Integer versioneServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			RicercaConfigurazioneApi ricerca = new RicercaConfigurazioneApi();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto));
						
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.exportConfigurazioneApi(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;			

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Consente di recuperare l'elenco delle erogazioni o fruizioni che coinvolgono
	 * il soggetto scelto
	 *
	 * Ricerca le erogazioni e fruizioni registrate sul sistema filtrandole per
	 * tipologia servizio
	 *
	 */
	@Override
	public ListaRiepilogoApi getConfigurazioneApi(TransazioneRuoloEnum tipo, ProfiloEnum profilo, String soggetto,
			Integer offset, Integer limit) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnectionConfig();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
				ConfigurazioniGeneraliService configurazioniService = new ConfigurazioniGeneraliService(connection, true, smp,
						LoggerProperties.getLoggerDAO());

				ServerProperties serverProperties = ServerProperties.getInstance();
				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();

				ConfigurazioniGeneraliSearchForm search = searchFormUtilities.getConfigurazioniGeneraliSearchForm(context,
						profilo, soggetto, tipo);
				configurazioniService.setSearch(search);
				List<ConfigurazioneGenerale> listDB = configurazioniService.findAll(Converter.toOffset(offset),
						Converter.toLimit(limit));
		
				ListaRiepilogoApi ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(),
						Converter.toOffset(offset), Converter.toLimit(limit), configurazioniService.totalCount(),
						ListaRiepilogoApi.class);

				if (serverProperties.isFindall404() && (listDB == null || listDB.isEmpty()))
					throw FaultCode.NOT_FOUND.toException("Nessuna configurazione trovata corrispondente ai criteri di ricerca");

				if (listDB != null && !listDB.isEmpty()) {
					listDB.forEach(configurazioneDB -> {
						try {
							ret.addItemsItem(Converter.toRiepilogoApiItem(configurazioneDB, this.log));
						} catch (Exception e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					});
				}

				context.getLogger().info("Invocazione completata con successo");
				return ret;
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera per mezzo di una ricerca articolata, un report statistico raggruppato
	 * per servizi
	 *
	 * Questa operazione consente di generare un report statistico raggrupato per
	 * servizi esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneApiByFullSearch(RicercaStatisticaDistribuzioneApi body, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneApi(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico organizzato per API utilizzando una ricerca
	 * semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico organizzato per API esportandolo nei formati più comuni
	 *
	 */
	@Override
	    public byte[] getReportDistribuzioneApiBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String tag, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);

			RicercaStatisticaDistribuzioneApi ricerca = new RicercaStatisticaDistribuzioneApi();
			ricerca.setSoggettoErogatore(soggettoRemoto);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneApi(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per applicativo utilizzando una ricerca
	 * articolata
	 *
	 * Questa operazione consente di generare un report statistico raggruppato per
	 * applicativo ed esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneApplicativoByFullSearch(RicercaStatisticaDistribuzioneApplicativo body,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneApplicativo(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico organizzato per Applicativi utilizzando una
	 * ricerca semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per applicativo esportandolo nei formati più comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneApplicativoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneApplicativo ricerca = new RicercaStatisticaDistribuzioneApplicativo();
			ricerca.setTipo(tipo);		
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneApplicativo(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico distribuito per azione utilizzando una ricerca
	 * articolata
	 *
	 * Questa operazione consente di generare un report statistico raggruppato
	 * distribuito per azione ed esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneAzioneByFullSearch(RicercaStatisticaDistribuzioneAzione body, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneAzione(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico organizzato per Azioni utilizzando una ricerca
	 * semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per azione esportandolo nei formati più comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneAzioneBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneAzione ricerca = new RicercaStatisticaDistribuzioneAzione();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore));
			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneAzione(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per andamento esiti per mezzo di una ricerca
	 * articolata
	 *
	 * Questa operazione consente di generare un report statistico per andamento
	 * esiti esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneEsitiByFullSearch(RicercaStatisticaDistribuzioneEsiti body, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneEsiti(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per andamento esiti per mezzo di una ricerca
	 * semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per andamento esiti esportandolo nei formati più comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneEsitiBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneEsiti ricerca = new RicercaStatisticaDistribuzioneEsiti();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);

			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			byte[] ret = ReportisticaHelper.getReportDistribuzioneEsiti(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per identificativo autenticato utilizzando una
	 * ricerca articolata
	 *
	 * Questa operazione consente di generare un report statistico raggruppato per
	 * identificativo autenticato ed esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneIdAutenticatoByFullSearch(RicercaStatisticaDistribuzioneApplicativo body,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneIdAutenticato(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico organizzato per Identificativo Autenticato
	 * utilizzando una ricerca semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per identificativo autenticato esportandolo nei formati più
	 * comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneIdAutenticatoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneApplicativo ricerca = new RicercaStatisticaDistribuzioneApplicativo();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneIdAutenticato(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico raggruppato per soggetto locale per mezzo di una
	 * ricerca articolata
	 *
	 * Questa operazione consente di generare un report statistico raggruppato per
	 * soggetto locale esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneSoggettoLocaleByFullSearch(RicercaStatisticaDistribuzioneSoggettoLocale body,
			ProfiloEnum profilo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");


			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, null, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneSoggettoLocale(body, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per soggetto locale per mezzo di una ricerca
	 * semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per soggetto locale esportandolo nei formati più comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneSoggettoLocaleBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, null, this.log);

			RicercaStatisticaDistribuzioneSoggettoLocale ricerca = new RicercaStatisticaDistribuzioneSoggettoLocale();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore));
			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneSoggettoLocale(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico raggruppato per soggetto remoto per mezzo di una
	 * ricerca articolata
	 *
	 * Questa operazione consente di generare un report statistico raggruppato per
	 * soggetto remoto esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneSoggettoRemotoByFullSearch(RicercaStatisticaDistribuzioneSoggettoRemoto body,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneSoggettoRemoto(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per soggetto remoto per mezzo di una ricerca
	 * semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per soggetto remoto esportandolo nei formati più comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneSoggettoRemotoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneSoggettoRemoto ricerca = new RicercaStatisticaDistribuzioneSoggettoRemoto();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoErogatore, null));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneSoggettoRemoto(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per andamento temporale per mezzo di una ricerca
	 * articolata
	 *
	 * Questa operazione consente di generare un report statistico per andamento
	 * temporale esportandolo nei formati più comuni
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneTemporaleByFullSearch(RicercaStatisticaAndamentoTemporale body,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneTemporale(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico per andamento temporale per mezzo di una ricerca
	 * semplice
	 *
	 * Questa operazione consente di generare per mezzo di una ricerca semplice, un
	 * report statistico per andamento temporale esportandolo nei formati più comuni
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneTemporaleBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaAndamentoTemporale ricerca = new RicercaStatisticaAndamentoTemporale();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReportMultiLine opzioni = new OpzioniGenerazioneReportMultiLine();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReportMultiLine tipo_info_report = new TipoInformazioneReportMultiLine();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneTemporale(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico organizzato per Token Info
	 *
	 * Consente di generare un report raggruppato secondo un claim del token
	 *
	 */
	@Override
	public byte[] getReportDistribuzioneTokenInfoByFullSearch(RicercaStatisticaDistribuzioneTokenInfo body,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);

			byte[] ret = ReportisticaHelper.getReportDistribuzioneTokenInfo(body, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Genera un report statistico organizzato organizzato per Token Info
	 * utilizzando una ricerca semplice
	 *
	 * Consente di generare un report raggruppato secondo un claim del token per
	 * mezzo di una ricerca semplice
	 *
	 */
	@Override
    public byte[] getReportDistribuzioneTokenInfoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, TokenClaimEnum claim, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneTokenInfo ricerca = new RicercaStatisticaDistribuzioneTokenInfo();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			ricerca.setClaim(claim);

			byte[] ret = ReportisticaHelper.getReportDistribuzioneTokenInfo(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	
	 /**
     * Genera un report statistico organizzato per Indirizzi IP
     *
     * Consente di generare un report degli indirizzo IP
     *
     */
    @Override
	public byte[] getReportDistribuzioneIndirizzoIPByFullSearch(RicercaStatisticaDistribuzioneApplicativo body, ProfiloEnum profilo, String soggetto) {
    	IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneIndirizzoIP(body, env);

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

    /**
     * Genera un report statistico organizzato organizzato per Indirizzo IP utilizzando una ricerca semplice
     *
     * Consente di generare un report degli indirizzo IP per mezzo di una ricerca semplice
     *
     */
    @Override
    public byte[] getReportDistribuzioneIndirizzoIPBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String soggettoRemoto, String soggettoErogatore, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

    	IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneApplicativo ricerca = new RicercaStatisticaDistribuzioneApplicativo();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			TipoInformazioneReport tipo_info_report = new TipoInformazioneReport();
			tipo_info_report.setTipo(tipoInformazioneReport);
			opzioni.setTipoInformazione(tipo_info_report);
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null) {
				FiltroEsito filtro_esito = new FiltroEsito();
				filtro_esito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				ricerca.setEsito(filtro_esito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneIndirizzoIP(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	
	
	/**
	 * Ottieni le informazioni generali sulle implementazioni di un Api
	 *
	 * Recupera le informazioni generali di una Api, come il nome e il numero di
	 * erogazioni e fruizioni registrate per esso
	 *
	 */
	@Override
	public InfoImplementazioneApi getRiepilogoApi(TransazioneRuoloEnum tipo, String nomeServizio, ProfiloEnum profilo,
			String soggetto, String soggettoRemoto, String tipoServizio, Integer versioneServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnectionConfig();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
				ConfigurazioniGeneraliService configurazioniService = new ConfigurazioniGeneraliService(connection, true, smp,
						LoggerProperties.getLoggerDAO());

				ServerProperties serverProperties = ServerProperties.getInstance();
				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();

				ConfigurazioniGeneraliSearchForm search = searchFormUtilities.getConfigurazioniGeneraliSearchForm(context,
						profilo, soggetto, tipo);
				IDServizio idServizio = Converter.toIDServizio(tipo, profilo, soggetto, soggettoRemoto, nomeServizio,
						tipoServizio, versioneServizio);
				if (idServizio != null && idServizio.getNome() != null && !"".equals(idServizio.getNome())) {
					search.setNomeServizio(ParseUtility.convertToServizioSoggetto(idServizio));
				}
				configurazioniService.setSearch(search);

				List<ConfigurazioneGenerale> listDB_infoServizi_left = configurazioniService.findAllInformazioniServizi();
				if (serverProperties.isFindall404() && (listDB_infoServizi_left == null || listDB_infoServizi_left.isEmpty()))
					throw FaultCode.NOT_FOUND.toException("Nessuna configurazione trovata corrispondente ai criteri di ricerca");

				InfoImplementazioneApi info = Converter.toInfoImplementazioneApi(listDB_infoServizi_left, this.log);

				context.getLogger().info("Invocazione completata con successo");
				return info;
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Ottieni le informazioni generali sulle api e servizi di un soggetto
	 *
	 * Restituisce il numero e tipo di api che coinvolgono il soggetto, e un
	 * riepilogo del registro circa i soggetti e gli applicativi registrati.
	 *
	 */
	@Override
	public Riepilogo getRiepologoConfigurazioni(ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnectionConfig();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesConfig();
				ConfigurazioniGeneraliService configurazioniService = new ConfigurazioniGeneraliService(connection, true, smp,
						LoggerProperties.getLoggerDAO());

				ServerProperties serverProperties = ServerProperties.getInstance();
				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();

				ConfigurazioniGeneraliSearchForm search = searchFormUtilities.getConfigurazioniGeneraliSearchForm(context,
						profilo, soggetto, null);
				configurazioniService.setSearch(search);

				List<ConfigurazioneGenerale> listDB_infoGenerali_right = configurazioniService.findAllInformazioniGenerali();
				List<ConfigurazioneGenerale> listDB_infoServizi_left = configurazioniService.findAllInformazioniServizi();
				if (serverProperties.isFindall404()
						&& (listDB_infoGenerali_right == null || listDB_infoGenerali_right.isEmpty())
						&& (listDB_infoServizi_left == null || listDB_infoServizi_left.isEmpty()))
					throw FaultCode.NOT_FOUND.toException("Nessuna configurazione trovata corrispondente ai criteri di ricerca");

				Riepilogo riepilogo = Converter.toRiepilogo(listDB_infoGenerali_right, listDB_infoServizi_left, this.log);

				context.getLogger().info("Invocazione completata con successo");
				return riepilogo;
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

}
