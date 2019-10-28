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

import static org.openspcoop2.core.monitor.rs.server.api.impl.utils.TransazioniHelper.overrideFiltroApiBase;
import static org.openspcoop2.core.monitor.rs.server.api.impl.utils.TransazioniHelper.overrideFiltroEsito;
import static org.openspcoop2.core.monitor.rs.server.api.impl.utils.TransazioniHelper.overrideFiltroFruizione;
import static org.openspcoop2.core.monitor.rs.server.api.impl.utils.TransazioniHelper.searchTransazioni;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.api.MonitoraggioApi;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.Converter;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.Enums;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.MonitoraggioEnv;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.SearchFormUtilities;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.TransazioniHelper;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneSimpleSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.Evento;
import org.openspcoop2.core.monitor.rs.server.model.FiltroApiBase;
import org.openspcoop2.core.monitor.rs.server.model.FiltroErogazione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.FiltroFruizione;
import org.openspcoop2.core.monitor.rs.server.model.FiltroQualsiasi;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.ListaEventi;
import org.openspcoop2.core.monitor.rs.server.model.ListaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIdApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIntervalloTemporale;
import org.openspcoop2.core.monitor.rs.server.model.TipoMessaggioEnum;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.utils.service.beans.FiltroRicercaId;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneExt;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.eventi.bean.EventiSearchForm;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;
import org.openspcoop2.web.monitor.eventi.dao.EventiService;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;

/**
 * MonitoraggioApiServiceImpl
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitoraggioApiServiceImpl extends BaseImpl implements MonitoraggioApi {

	public MonitoraggioApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(MonitoraggioApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Ricerca di Eventi
     *
     * Questa operazione consente di effettuare una ricerca nell&#x27;archivio degli eventi specificando i criteri di filtraggio
     *
     */
	@Override
    public ListaEventi findAllEventi(DateTime dataInizio, DateTime dataFine, Integer offset, Integer limit, DiagnosticoSeveritaEnum severita, String tipo, String codice, String origine, Boolean ricercaEsatta, Boolean caseSensitive) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnectionTracce();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesTracce();
				EventiService eventiService = new EventiService(connection, true, smp, LoggerProperties.getLoggerDAO());
				
				ServerProperties serverProperties = ServerProperties.getInstance();
				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
				
				EventiSearchForm search = searchFormUtilities.getEventiSearchForm(context, dataInizio, dataFine);
				search.setCodice(codice);
				search.setTipo(tipo);
				search.setSeverita(BaseHelper.evalnull( () -> Enums.toTipoSeverita.get(severita).toString()));
				search.setCaseSensitiveType( (BooleanUtils.isTrue(caseSensitive) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
				search.setMatchingType( (BooleanUtils.isTrue(ricercaEsatta) ? TipoMatch.EQUALS : TipoMatch.LIKE ).toString());
				search.setIdConfigurazione(origine);
			
				eventiService.setSearch(search);	
				List<EventoBean> listEventiDB = eventiService.findAll(Converter.toOffset(offset), Converter.toLimit(limit));
				ListaEventi ret = ListaUtils.costruisciLista(
						context.getServletRequest().getRequestURI(),
						Converter.toOffset(offset), Converter.toLimit(limit), 
						listEventiDB!=null ? listEventiDB.size() : 0, 
								ListaEventi.class
					); 
				
				if ( serverProperties.isFindall404() && (listEventiDB==null || listEventiDB.isEmpty()) )
					throw FaultCode.NOT_FOUND.toException("Nessun evento trovato corrispondente ai criteri di ricerca");
				
				if(listEventiDB!=null && !listEventiDB.isEmpty()) {
					listEventiDB.forEach( eventoDB -> 
						{
							try {
								ret.addItemsItem( Converter.toEvento(eventoDB, this.log) );
							} catch (Exception e) {
								throw new RuntimeException(e.getMessage(),e);
							}
						}
						);
				}
		
				context.getLogger().info("Invocazione completata con successo");
		        return ret;
			}
			finally {
				dbManager.releaseConnectionTracce(connection);
			}
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Ricerca completa delle transazioni per intervallo temporale
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, limitando i risultati di ricerca ad un intervallo di date
     *
     */
	@Override
    public ListaTransazioni findAllTransazioniByFullSearch(RicercaIntervalloTemporale body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		
		try {
			context.getLogger().info("Invocazione in corso ...");     
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			BaseHelper.throwIfNull(body);

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
			TransazioniSearchForm search = searchFormUtilities.getAndamentoTemporaleSearchForm(env.context, env.profilo, env.soggetto.getNome(), 
					body.getTipo(), body.getIntervalloTemporale().getDataInizio(), body.getIntervalloTemporale().getDataFine());
			
			TransazioniHelper.overrideRicercaBaseTransazione(body, search, env);
			TransazioniHelper.overrideFiltroMittente(body, search, env);
			
			ListaTransazioni ret = searchTransazioni(search, body.getOffset(), body.getLimit(), body.getSort(), env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Ricerca completa delle transazioni in base all&#x27;identificativo applicativo
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base all&#x27;identificativo applicativo e i parametri di filtro completi
     *
     */
	@Override
    public ListaTransazioni findAllTransazioniByIdApplicativoFullSearch(RicercaIdApplicativo body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");                             
			
			BaseHelper.throwIfNull(body);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
			TransazioniSearchForm search = searchFormUtilities.getIdApplicativoSearchForm(context, profilo, soggetto, 
					body.getTipo(), body.getIntervalloTemporale().getDataInizio(), body.getIntervalloTemporale().getDataFine());
			
			TransazioniHelper.overrideRicercaBaseTransazione(body, search, env);
			//TransazioniHelper.overrideFiltroRicercaId(body.getIdApplicativo(), search, env);
			// Qui non è overrideFiltroRicercaId ma:

			FiltroRicercaId filtro = body.getIdApplicativo();
			search.setCorrelazioneApplicativaCaseSensitiveType((BooleanUtils.isTrue(filtro.isCaseSensitive()) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
			search.setCorrelazioneApplicativaMatchingType((BooleanUtils.isTrue(filtro.isRicercaEsatta()) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
			search.setIdCorrelazioneApplicativa(filtro.getId());			
			
			ListaTransazioni ret = searchTransazioni(search, body.getOffset(), body.getLimit(), body.getSort(), env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Ricerca semplificata delle transazioni in base all&#x27;identificativo applicativo
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base all&#x27;identificativo applicativo e i parametri di filtro di uso più comune
     *
     */
	@Override
	public ListaTransazioni findAllTransazioniByIdApplicativoSimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, String idApplicativo, ProfiloEnum profilo, String soggetto, Integer offset, Integer limit, String sort, String idCluster,  String soggettoRemoto, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean ricercaEsatta, Boolean caseSensitive) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     
			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                       
			SearchFormUtilities searchFormUtilities = new SearchFormUtilities();	
			TransazioniSearchForm search = searchFormUtilities.getIdApplicativoSearchForm(context, profilo, soggetto, 
					tipo, dataInizio, dataFine);
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			
			// FiltroApi
			FiltroApiBase filtroApi = null;
			switch (tipo) {
			case EROGAZIONE:
				filtroApi = new FiltroErogazione();
				break;
			case FRUIZIONE:
				filtroApi = new FiltroFruizione();
				break;
			case QUALSIASI:
				filtroApi = new FiltroQualsiasi();
				break;
			}
			filtroApi.setNome(nomeServizio);
			filtroApi.setTipo(tipoServizio);
			filtroApi.setVersione(versioneServizio);
			switch (tipo) {
			case EROGAZIONE: {
				overrideFiltroApiBase(tag, filtroApi, azione, env.soggetto, search, env);
				break;
			}
			case FRUIZIONE: {
				FiltroFruizione filtro = (FiltroFruizione) filtroApi;
				filtro.setErogatore(soggettoRemoto);
				overrideFiltroFruizione(tag, filtro, azione, search, env);
				break;
			}
			case QUALSIASI: {
				overrideFiltroApiBase(tag, filtroApi, azione, env.soggetto, search, env);
				break;
			}
			}
			
			// FiltroEsito
			if (esito != null) {
				FiltroEsito filtroEsito = new FiltroEsito();
				filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				overrideFiltroEsito(filtroEsito, search, env);
			}
			
			// Filtro Correlazione Applicativa
			search.setCorrelazioneApplicativaCaseSensitiveType((BooleanUtils.isTrue(caseSensitive) ? CaseSensitiveMatch.SENSITIVE : CaseSensitiveMatch.INSENSITIVE).toString() );
			search.setCorrelazioneApplicativaMatchingType((BooleanUtils.isTrue(ricercaEsatta) ? TipoMatch.EQUALS : TipoMatch.LIKE).toString());
			search.setIdCorrelazioneApplicativa(idApplicativo);			
						
			search.setClusterId(idCluster);
			
			ListaTransazioni ret = searchTransazioni(search, offset, limit, sort, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Ricerca semplificata delle transazioni in base all&#x27;identificativo messaggio
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base all&#x27;identificativo messaggio
     *
     */
	@Override
    public ListaTransazioni findAllTransazioniByIdMessaggio(TipoMessaggioEnum tipoMessaggio, String id, ProfiloEnum profilo, String soggetto, Integer offset, Integer limit, String sort) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			SearchFormUtilities searchFormUtilities = new SearchFormUtilities();	
			TransazioniSearchForm search = searchFormUtilities.getIdMessaggioSearchForm(context, profilo, soggetto);
			
			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			search.setIdEgov(id);
			search.setTipoIdMessaggio(tipoMessaggio == TipoMessaggioEnum.RICHIESTA ? "Richiesta" : "Risposta" );
        
			ListaTransazioni ret = searchTransazioni(search, offset, limit, sort, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Ricerca semplificata delle transazioni in base ai parametri di uso più comune
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base a parametri di filtro di uso più comune
     *
     */
	@Override
    public ListaTransazioni findAllTransazioniBySimpleSearch(DateTime dataInizio, DateTime dataFine, FiltroRicercaRuoloTransazioneEnum tipo, ProfiloEnum profilo, String soggetto, Integer offset, Integer limit, String sort,  String idCluster,  String soggettoRemoto, String tag, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     

			MonitoraggioEnv env = new MonitoraggioEnv(context, profilo, soggetto, this.log);
			SearchFormUtilities searchFormUtilities = new SearchFormUtilities();	
			TransazioniSearchForm search = searchFormUtilities.getAndamentoTemporaleSearchForm(context, env.profilo, env.soggetto.getNome(), 
					tipo, dataInizio, dataFine);
		
			// FiltroApi
			FiltroApiBase filtroApi = null;
			switch (tipo) {
			case EROGAZIONE:
				filtroApi = new FiltroErogazione();
				break;
			case FRUIZIONE:
				filtroApi = new FiltroFruizione();
				break;
			case QUALSIASI:
				filtroApi = new FiltroQualsiasi();
				break;
			}
			filtroApi.setNome(nomeServizio);
			filtroApi.setTipo(tipoServizio);
			filtroApi.setVersione(versioneServizio);	
			switch (tipo) {
			case EROGAZIONE: {
				overrideFiltroApiBase(tag, filtroApi, azione, env.soggetto, search, env);
				break;
			}
			case FRUIZIONE:{
				FiltroFruizione filtro = (FiltroFruizione) filtroApi;
				filtro.setErogatore(soggettoRemoto);
				overrideFiltroFruizione(tag, filtro, azione, search, env);
				break;
			}
			case QUALSIASI: {
				overrideFiltroApiBase(tag, filtroApi, azione, env.soggetto, search, env);
				break;
			}
			}
			
			if (esito != null) {
				// FiltroEsito
				FiltroEsito filtroEsito = new FiltroEsito();
				filtroEsito.setTipo(EsitoTransazioneFullSearchEnum.valueOf(esito.name()));
				overrideFiltroEsito(filtroEsito, search, env);
			}
			
			search.setClusterId(idCluster);
        
			ListaTransazioni ret = searchTransazioni(search, offset, limit, sort, env);
			context.getLogger().info("Invocazione completata con successo");
			return ret;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Dettaglio di un evento
     *
     * Permette di recuperare il dettaglio di un evento in base al suo identificativo
     *
     */
	@Override
    public Evento getEvento(Long id) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
               
			if(id==null) {
				FaultCode.RICHIESTA_NON_VALIDA.throwException("Id undefined");
			}
			
			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnectionTracce();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesTracce();
				EventiService eventiService = new EventiService(connection, true, smp, LoggerProperties.getLoggerDAO());
				
				EventoBean eventoDB = eventiService.findById(id);
				if(eventoDB==null) {
					FaultCode.NOT_FOUND.throwException("Evento con id '"+id+"' non esistente");
				}
				Evento evento = Converter.toEvento(eventoDB, this.log);		
				context.getLogger().info("Invocazione completata con successo");
		        return evento;
			}
			finally {
				dbManager.releaseConnectionTracce(connection);
			}
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
    /**
     * Dettaglio della transazione
     *
     * Questa operazione consente di ottenere il dettaglio di una transazione
     *
     */
	@Override
    public TransazioneExt getTransazione(UUID id) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
            
			if(id==null) {
				FaultCode.RICHIESTA_NON_VALIDA.throwException("Id undefined");
			}
			
			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnectionTracce();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesTracce();
				TransazioniService transazioniService = new TransazioniService(connection, true, smp, LoggerProperties.getLoggerDAO());
				TransazioneBean transazioneDB = transazioniService.findByIdTransazione(id.toString());
				if(transazioneDB==null) {
					FaultCode.NOT_FOUND.throwException("Traccia con id '"+id+"' non esistente");
				}
				TransazioneExt transazione = org.openspcoop2.core.monitor.rs.server.api.impl.utils.Converter.toTransazioneExt(transazioneDB, transazioniService, 
						connection, smp,
						this.log);
				context.getLogger().info("Invocazione completata con successo");
				return transazione;
			}
			finally {
				dbManager.releaseConnectionTracce(connection);
			}
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

