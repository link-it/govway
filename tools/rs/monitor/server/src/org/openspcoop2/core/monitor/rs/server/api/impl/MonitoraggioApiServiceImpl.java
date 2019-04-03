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
import java.util.UUID;

import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.api.MonitoraggioApi;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.Converter;
import org.openspcoop2.core.monitor.rs.server.api.impl.utils.SearchFormUtilities;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneSimpleSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.Evento;
import org.openspcoop2.core.monitor.rs.server.model.ListaEventi;
import org.openspcoop2.core.monitor.rs.server.model.ListaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIdApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIntervalloTemporale;
import org.openspcoop2.core.monitor.rs.server.model.TipoMessaggioEnum;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneExt;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.eventi.bean.EventiSearchForm;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;
import org.openspcoop2.web.monitor.eventi.dao.EventiService;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.TransazioniService;
/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
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
				connection = dbManager.getConnection();
				ServiceManagerProperties smp = dbManager.getServiceManagerProperties();
				EventiService eventiService = new EventiService(connection, true, smp, LoggerProperties.getLoggerDAO());
				
				ServerProperties serverProperties = ServerProperties.getInstance();
				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
				
				EventiSearchForm search = searchFormUtilities.getEventiSearchForm(context, dataInizio, dataFine);
				// TODO: altri criteri...
				// TODO: altre impostazioni
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
				dbManager.releaseConnection(connection);
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
                    
			if(body==null) {
				FaultCode.RICHIESTA_NON_VALIDA.throwException("Body request undefined");
			}
			
			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			try {
				connection = dbManager.getConnection();
				ServiceManagerProperties smp = dbManager.getServiceManagerProperties();
				TransazioniService transazioniService = new TransazioniService(connection, true, smp, LoggerProperties.getLoggerDAO());
				
				ServerProperties serverProperties = ServerProperties.getInstance();
				SearchFormUtilities searchFormUtilities = new SearchFormUtilities();
				
				TransazioniSearchForm search = searchFormUtilities.getAndamentoTemporaleSearchForm(context, profilo, soggetto, 
						body.getTipo(), body.getIntervalloTemporale().getDataInizio(), body.getIntervalloTemporale().getDataFine());
				// TODO: altri criteri...
				if(body.getEsito()!=null && body.getEsito().getTipo()!=null) {
					switch (body.getEsito().getTipo()){
					case OK:
						search.setEsitoGruppo(EsitoUtils.ALL_OK_VALUE);
						break;
					case FAULT:
						search.setEsitoGruppo(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
						break;
					case ERROR:
						search.setEsitoGruppo(EsitoUtils.ALL_ERROR_VALUE);
						break;
					case ERROR_OR_FAULT:
						search.setEsitoGruppo(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
						break;
					case PERSONALIZZATO:
						search.setEsitoGruppo(EsitoUtils.ALL_PERSONALIZZATO_VALUE);
						break;
					}	
				}
				// TODO: altre impostazioni
				transazioniService.setSearch(search);
				
				List<TransazioneBean> listTransazioniDB = transazioniService.findAll(Converter.toOffset(body.getOffset()), Converter.toLimit(body.getLimit()), 
						Converter.toSortOrder(body.getSort()), Converter.toSortField(body.getSort()));
				ListaTransazioni ret = ListaUtils.costruisciLista(
						context.getServletRequest().getRequestURI(),
						Converter.toOffset(body.getOffset()), Converter.toLimit(body.getLimit()), 
						listTransazioniDB!=null ? listTransazioniDB.size() : 0, 
								ListaTransazioni.class
					); 
				
				if ( serverProperties.isFindall404() && (listTransazioniDB==null || listTransazioniDB.isEmpty()) )
					throw FaultCode.NOT_FOUND.toException("Nessuna transazione trovata corrispondente ai criteri di ricerca");
				
				if(listTransazioniDB!=null && !listTransazioniDB.isEmpty()) {
					listTransazioniDB.forEach( transazioneDB -> 
						{
							try {
								ret.addItemsItem( Converter.toItemTransazione(transazioneDB, this.log) );
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
				dbManager.releaseConnection(connection);
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
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        return null;
     
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
    public ListaTransazioni findAllTransazioniByIdApplicativoSimpleSearch(DateTime dataInizio, DateTime dataFine, TransazioneRuoloEnum tipo, String idApplicativo, ProfiloEnum profilo, String soggetto, Integer offset, Integer limit, String sort, String idCluster,  String soggettoRemoto, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito, Boolean ricercaEsatta, Boolean caseSensitive) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        return null;
     
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
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        return null;
     
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
    public ListaTransazioni findAllTransazioniBySimpleSearch(DateTime dataInizio, DateTime dataFine, TransazioneRuoloEnum tipo, ProfiloEnum profilo, String soggetto, Integer offset, Integer limit, String sort,  String idCluster,  String soggettoRemoto, String nomeServizio, String tipoServizio, Integer versioneServizio, String azione, EsitoTransazioneSimpleSearchEnum esito) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
        // TODO: Implement...
        
			context.getLogger().info("Invocazione completata con successo");
        return null;
     
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
				connection = dbManager.getConnection();
				ServiceManagerProperties smp = dbManager.getServiceManagerProperties();
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
				dbManager.releaseConnection(connection);
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
				connection = dbManager.getConnection();
				ServiceManagerProperties smp = dbManager.getServiceManagerProperties();
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
				dbManager.releaseConnection(connection);
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

