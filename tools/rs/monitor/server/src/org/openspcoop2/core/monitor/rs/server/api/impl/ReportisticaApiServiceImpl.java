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
package org.openspcoop2.core.monitor.rs.server.api.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.monitor.rs.server.api.ReportisticaApi;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.Converter;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.MonitoraggioEnv;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.ReportisticaHelper;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.SearchFormUtilities;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.BaseOggettoWithSimpleName;
import org.openspcoop2.core.monitor.rs.server.model.DetailsTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.DimensioniReportCustomEnum;
import org.openspcoop2.core.monitor.rs.server.model.DimensioniReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneSimpleSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportConfigEnum;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.InfoImplementazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.ListaRiepilogoApi;
import org.openspcoop2.core.monitor.rs.server.model.ListaTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportDimensioni;
import org.openspcoop2.core.monitor.rs.server.model.OpzioniGenerazioneReportMultiLine;
import org.openspcoop2.core.monitor.rs.server.model.RicercaConfigurazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaAndamentoTemporale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApplicativoRegistrato;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneAzione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneErrori;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneEsiti;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoLocale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoRemoto;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneTokenInfo;
import org.openspcoop2.core.monitor.rs.server.model.Riepilogo;
import org.openspcoop2.core.monitor.rs.server.model.StatoTracing;
import org.openspcoop2.core.monitor.rs.server.model.StatoTracingPDND;
import org.openspcoop2.core.monitor.rs.server.model.TipoIdentificazioneApplicativoEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimDistribuzioneStatisticaEnum;
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
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.ModalitaRicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.dao.ConfigurazioniGeneraliService;
import org.openspcoop2.web.monitor.statistiche.dao.StatistichePdndTracingService;


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

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
			String soggettoRemoto, String nomeServizio, String tipoServizio, Integer versioneServizio, FormatoReportConfigEnum formatoReport) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
						
			RicercaConfigurazioneApi ricerca = new RicercaConfigurazioneApi();
			ricerca.setTipo(tipo);
			ricerca.setFormato(formatoReport);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,
					env, null));
						
			byte[] ret = ReportisticaHelper.exportConfigurazioneApi(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;			

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
	    public byte[] getReportDistribuzioneApiBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
	    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String tag, Boolean distinguiApiImplementata, 
	    		EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
	    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);

			RicercaStatisticaDistribuzioneApi ricerca = new RicercaStatisticaDistribuzioneApi();
			
			BaseOggettoWithSimpleName soggettoRemotoBase = null;
			if(soggettoRemoto!=null) {
				soggettoRemotoBase = new BaseOggettoWithSimpleName();
				soggettoRemotoBase.setNome(soggettoRemoto);
			}
			ricerca.setSoggettoErogatore(soggettoRemotoBase);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			ricerca.setDistinguiApiImplementata(distinguiApiImplementata);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneApi(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
	public byte[] getReportDistribuzioneApplicativoByFullSearch(RicercaStatisticaDistribuzioneApplicativoRegistrato body,
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneApplicativoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore,String soggettoMittente, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, TipoIdentificazioneApplicativoEnum tipoIdentificazione, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneApplicativoRegistrato ricerca = new RicercaStatisticaDistribuzioneApplicativoRegistrato();
			ricerca.setTipo(tipo);		
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setSoggettoMittente(soggettoMittente);
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			ricerca.setTipoIdentificazioneApplicativo(tipoIdentificazione);
			
			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneApplicativo(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneAzioneBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneAzione ricerca = new RicercaStatisticaDistribuzioneAzione();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore,
					env, uriApiImplementata));
			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneAzione(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

    
    /**
     * Genera un report statistico distribuito per esiti di errore utilizzando una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico raggruppato distribuito per esiti di errore ed esportandolo nei formati più comuni 
     *
     */
	@Override
    public byte[] getReportDistribuzioneErroriByFullSearch(RicercaStatisticaDistribuzioneErrori body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			byte[] ret = ReportisticaHelper.getReportDistribuzioneErrori(body, env);
        
			context.getLogger().info("Invocazione completata con successo");
			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Genera un report statistico organizzato per esiti di errore utilizzando una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per esiti di errore esportandolo nei formati più comuni
     *
     */
	@Override
    public byte[] getReportDistribuzioneErroriBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneErrori ricerca = new RicercaStatisticaDistribuzioneErrori();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setAzione(azione);
			
			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					EsitoTransazioneFullSearchEnum esitoT = EsitoTransazioneFullSearchEnum.valueOf(esito.name());
					filtroEsito.setTipo(esitoT);
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.FALLITE_E_FAULT);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneErrori(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
     
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
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

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneEsitiBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneEsiti ricerca = new RicercaStatisticaDistribuzioneEsiti();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReport opzioni = new OpzioniGenerazioneReport();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, null, null);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			byte[] ret = ReportisticaHelper.getReportDistribuzioneEsiti(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneIdAutenticatoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata,
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneApplicativo ricerca = new RicercaStatisticaDistribuzioneApplicativo();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneIdAutenticato(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneSoggettoLocaleBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, null, this.log);

			RicercaStatisticaDistribuzioneSoggettoLocale ricerca = new RicercaStatisticaDistribuzioneSoggettoLocale();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto,soggettoErogatore,
					env, uriApiImplementata));
			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneSoggettoLocale(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneSoggettoRemotoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneSoggettoRemoto ricerca = new RicercaStatisticaDistribuzioneSoggettoRemoto();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoErogatore, null,
					env, uriApiImplementata));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneSoggettoRemoto(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneTemporaleBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaAndamentoTemporale ricerca = new RicercaStatisticaAndamentoTemporale();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportMultiLine opzioni = new OpzioniGenerazioneReportMultiLine();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReportMultiLine(opzioni, tipoInformazioneReport);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneTemporale(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneTokenInfoBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		TokenClaimDistribuzioneStatisticaEnum claim, ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneTokenInfo ricerca = new RicercaStatisticaDistribuzioneTokenInfo();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			ricerca.setClaim(claim);

			byte[] ret = ReportisticaHelper.getReportDistribuzioneTokenInfo(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
    public byte[] getReportDistribuzioneIndirizzoIPBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, FormatoReportEnum formatoReport, 
    		ProfiloEnum profilo, String soggetto, String idCluster, String soggettoRemoto, String soggettoErogatore, String tag, String uriApiImplementata, 
    		String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean escludiScartate, UnitaTempoReportEnum unitaTempo, TipoReportEnum tipoReport, TipoInformazioneReportEnum tipoInformazioneReport,
    		DimensioniReportEnum dimensioniReport, DimensioniReportCustomEnum dimensioniReportCustomInfo) {

    	IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			RicercaStatisticaDistribuzioneApplicativo ricerca = new RicercaStatisticaDistribuzioneApplicativo();
			ricerca.setTipo(tipo);
			ricerca.setApi(ReportisticaHelper.parseFiltroApiMap(tipo, nomeServizio, tipoServizio, versioneServizio, soggettoRemoto, soggettoErogatore,
					env, uriApiImplementata));
			ricerca.setAzione(azione);

			FiltroTemporale intervallo = new FiltroTemporale();
			intervallo.setDataInizio(dataInizio);
			intervallo.setDataFine(dataFine);

			ricerca.setIntervalloTemporale(intervallo);
			ricerca.setUnitaTempo(unitaTempo);
			ricerca.setTipo(tipo);
			ricerca.setIdCluster(idCluster);

			OpzioniGenerazioneReportDimensioni opzioni = new OpzioniGenerazioneReportDimensioni();
			opzioni.setFormato(formatoReport);
			opzioni.setTipo(tipoReport);
			ReportisticaHelper.setTipoInformazioneReport(opzioni, tipoInformazioneReport, dimensioniReport, dimensioniReportCustomInfo);		
			ricerca.setReport(opzioni);

			ricerca.setTag(tag);
			
			if (esito != null || escludiScartate!=null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				if(esito!=null) {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				}
				else {
					filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.QUALSIASI);
				}
				if(escludiScartate!=null) {
					filtroEsito.setEscludiScartate(escludiScartate);
				}
				ricerca.setEsito(filtroEsito);
			}

			byte[] ret = ReportisticaHelper.getReportDistribuzioneIndirizzoIP(ricerca, env);
			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
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
				ConfigurazioneGenerale soggettiOperativi = configurazioniService.getSoggettiOperativi();
				if (serverProperties.isFindall404()
						&& (listDB_infoGenerali_right == null || listDB_infoGenerali_right.isEmpty())
						&& (listDB_infoServizi_left == null || listDB_infoServizi_left.isEmpty()))
					throw FaultCode.NOT_FOUND.toException("Nessuna configurazione trovata corrispondente ai criteri di ricerca");

				Riepilogo riepilogo = Converter.toRiepilogo(listDB_infoGenerali_right, listDB_infoServizi_left, soggettiOperativi, this.log);

				context.getLogger().info("Invocazione completata con successo");
				return riepilogo;
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}
	
    /**
     * Recupera la lista di tracciati della pdnd
     *
     * Consente di recuperare la configurazione di un servizio esportandola in formato csv, xls
     *
     */
	@Override
    public ListaTracingPDND getTracingPdndList(LocalDate dataInizio, LocalDate dataFine, String soggetto, Integer offset, Integer limit, Integer numeroTentativi, StatoTracing stato, StatoTracingPDND statoPdnd, UUID tracingId) {
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
				StatistichePdndTracingService pdndService = new StatistichePdndTracingService(connection, true, smp, LoggerProperties.getLoggerDAO());

				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();

				StatistichePdndTracingSearchForm search = searchFormUtilities.getStatistichePdndTracingSearchForm(context, soggetto);
				search.setDataInizio(dataInizio.toDate());
				search.setDataFine(dataFine.toDate());
				if (stato != null && !StatoTracing.QUALSIASI.equals(stato))
					search.setStato(Converter.toStatoTracing(stato).toString());
				if (statoPdnd != null && !StatoTracingPDND.QUALSIASI.equals(statoPdnd))
					search.setStatoPdnd(Converter.toStatoTracingPDND(statoPdnd).toString());
				if (tracingId != null) {
					search.setTracingId(tracingId.toString());
					search.setModalitaRicerca(ModalitaRicercaStatistichePdnd.TRACING_ID.toString());
				}
				if (numeroTentativi != null)
					search.setTentativiPubblicazione(numeroTentativi);
				limit = Objects.requireNonNullElse(limit, 100);
				
				pdndService.setSearch(search);

				List<StatistichePdndTracingBean> listDB = pdndService.findAll(offset, limit);
				ListaTracingPDND list = Converter.toListaTracingPDND(context, listDB, offset, limit, pdndService.totalCount());
				
				context.getLogger().info("Invocazione completata con successo");
				
				return list;
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
	 * mostra i dettagli di un tracciamento
	 * 
	 * mostra i dettagli di un tracciamento
	 * 
	 */
	@Override
    public DetailsTracingPDND getDetailsTracingPdnd(Long id) {
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
				StatistichePdndTracingService pdndService = new StatistichePdndTracingService(connection, true, smp, LoggerProperties.getLoggerDAO());

				StatistichePdndTracingBean bean = pdndService.findById(id);
				if(bean==null) {
					FaultCode.NOT_FOUND.throwException("Traccia con id '"+id+"' non esistente");
				}
				DetailsTracingPDND details = Converter.toDetailsTracingPDND(bean);
				
				context.getLogger().info("Invocazione completata con successo");
				
				return details;
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
	
    /**
     * esporta il csv inerente al tracciato
     *
     * Consente di recuperare il csv del tracciato
     *
     */
	@Override
    public byte[] exportTracingPdnd(Long id) {
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
				StatistichePdndTracingService pdndService = new StatistichePdndTracingService(connection, true, smp, LoggerProperties.getLoggerDAO());

				StatistichePdndTracingBean bean = pdndService.findById(id);
				if(bean==null) {
					FaultCode.NOT_FOUND.throwException("Traccia con id '"+id+"' non esistente");
				}
				context.getLogger().info("Invocazione completata con successo");
				
				return bean.getCsv();
			} finally {
				dbManager.releaseConnectionConfig(connection);
			}

		} catch (jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

}
