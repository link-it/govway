/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.monitor.ws.server.impl;

import java.util.List;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.pdd.monitor.Busta;
import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.BustaSoggetto;
import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.pdd.monitor.ws.server.config.Constants;
import org.openspcoop2.pdd.monitor.ws.server.config.DriverMonitor;
import org.openspcoop2.pdd.monitor.ws.server.config.LoggerProperties;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotAuthorizedException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorNotImplementedException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.exception.MonitorServiceException_Exception;
import org.openspcoop2.pdd.monitor.ws.server.filter.SearchFilterMessaggio;


/**     
 * MessaggioImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class MessaggioImpl extends BaseImpl  {

	private DriverMonitor messaggioService = null; 

	public MessaggioImpl() {
		super();
		try {
			this.messaggioService = DriverMonitor.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Messaggio Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Messaggio Service",  e);
		} 
	}
	



	private FilterSearch toFilterSearch(SearchFilterMessaggio filter) throws ServiceException, NotImplementedException, Exception{
		
		FilterSearch filterSearch = new FilterSearch();
		

		if(filter.getFiltro()!= null) {

			if(filter.getFiltro().getCorrelazioneApplicativa()!= null) {
				filterSearch.setCorrelazioneApplicativa(filter.getFiltro().getCorrelazioneApplicativa());
			}
			
			filterSearch.setBusta(new Busta()); // il filtro sulla busta deve essere impostato per far ritornare gli elementi della busta
			
			if(filter.getFiltro().getBusta()!= null) {
				
				if(filter.getFiltro().getBusta().getAttesaRiscontro()!= null) {
					filterSearch.getBusta().setAttesaRiscontro(filter.getFiltro().getBusta().getAttesaRiscontro());
				}
				if(filter.getFiltro().getBusta().getMittente()!= null) {
					filterSearch.getBusta().setMittente(new BustaSoggetto());
					if(filter.getFiltro().getBusta().getMittente().getTipo()!= null) {
						filterSearch.getBusta().getMittente().setTipo(filter.getFiltro().getBusta().getMittente().getTipo());
					}
					if(filter.getFiltro().getBusta().getMittente().getNome()!= null) {
						filterSearch.getBusta().getMittente().setNome(filter.getFiltro().getBusta().getMittente().getNome());
					}
				}
				if(filter.getFiltro().getBusta().getDestinatario()!= null) {
					filterSearch.getBusta().setDestinatario(new BustaSoggetto());
					if(filter.getFiltro().getBusta().getDestinatario().getTipo()!= null) {
						filterSearch.getBusta().getDestinatario().setTipo(filter.getFiltro().getBusta().getDestinatario().getTipo());
					}
					if(filter.getFiltro().getBusta().getDestinatario().getNome()!= null) {
						filterSearch.getBusta().getDestinatario().setNome(filter.getFiltro().getBusta().getDestinatario().getNome());
					}
				}
				if(filter.getFiltro().getBusta().getServizio()!= null) {
					filterSearch.getBusta().setServizio(new BustaServizio());
					if(filter.getFiltro().getBusta().getServizio().getTipo()!= null) {
						filterSearch.getBusta().getServizio().setTipo(filter.getFiltro().getBusta().getServizio().getTipo());
					}
					if(filter.getFiltro().getBusta().getServizio().getNome()!= null) {
						filterSearch.getBusta().getServizio().setNome(filter.getFiltro().getBusta().getServizio().getNome());
					}
				}
				if(filter.getFiltro().getBusta().getAzione()!= null) {
					filterSearch.getBusta().setAzione(filter.getFiltro().getBusta().getAzione());
				}
				if(filter.getFiltro().getBusta().getProfiloCollaborazione()!= null) {
					filterSearch.getBusta().setProfiloCollaborazione(filter.getFiltro().getBusta().getProfiloCollaborazione());
				}
				if(filter.getFiltro().getBusta().getCollaborazione()!= null) {
					filterSearch.getBusta().setCollaborazione(filter.getFiltro().getBusta().getCollaborazione());
				}
				if(filter.getFiltro().getBusta().getRiferimentoMessaggio()!= null) {
					filterSearch.getBusta().setRiferimentoMessaggio(filter.getFiltro().getBusta().getRiferimentoMessaggio());
				}
			}
			if(filter.getFiltro().getIdMessaggio()!= null) {
				filterSearch.setIdMessaggio(filter.getFiltro().getIdMessaggio());
			}
			if(filter.getFiltro().getMessagePattern()!= null) {
				filterSearch.setMessagePattern(filter.getFiltro().getMessagePattern());
			}
			if(filter.getFiltro().getSoglia()!= null) {
				filterSearch.setSoglia(filter.getFiltro().getSoglia());
			}
			if(filter.getFiltro().getStato()!= null) {
				filterSearch.setStato(filter.getFiltro().getStato());
			}
			if(filter.getFiltro().getTipo()!= null) {
				filterSearch.setTipo(filter.getFiltro().getTipo());
			}
		}

		return filterSearch;
	}

	private FilterSearch toPaginatedFilterSearch(SearchFilterMessaggio filter) throws ServiceException, NotImplementedException, Exception{
	
		FilterSearch filterSearch = this.toFilterSearch(filter);
		
		if(filter.getLimit()!=null) {
			filterSearch.setLimit(filter.getLimit());
		}
		if(filter.getOffset()!=null) {
			filterSearch.setOffset(filter.getOffset());
		}
		
		return filterSearch;
	}



	
	public List<Messaggio> findAll(SearchFilterMessaggio filter) throws MonitorServiceException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception {
		try{
		
			checkInitDriverMonitoraggio(this.messaggioService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			FilterSearch filterSearch = this.toPaginatedFilterSearch(filter);
			List<Messaggio> result = this.messaggioService.getDriver().getListaRichiestePendenti(filterSearch);
			this.logEndMethod("findAll", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("findAll", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("findAll", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("findAll", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("findAll", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("findAll", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("findAll", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
		
	}

	

	
	public long count(SearchFilterMessaggio filter) throws MonitorServiceException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception {
		try{
		
			checkInitDriverMonitoraggio(this.messaggioService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			FilterSearch filterSearch = this.toFilterSearch(filter);
			long result = this.messaggioService.getDriver().countListaRichiestePendenti(filterSearch);
			this.logEndMethod("count", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("count", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("count", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("count", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("count", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("count", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("count", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
	}

	
	
	
	
	
	
	
	public long deleteAllByFilter(SearchFilterMessaggio filter) throws MonitorServiceException_Exception,MonitorNotImplementedException_Exception,MonitorNotAuthorizedException_Exception {
		try{
		
			checkInitDriverMonitoraggio(this.messaggioService);
			this.logStartMethod("deleteAllByFilter", filter);
			this.authorize(false);
			FilterSearch filterSearch = this.toFilterSearch(filter);
			long result = this.messaggioService.getDriver().deleteRichiestePendenti(filterSearch);
			this.logEndMethod("deleteAllByFilter", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("deleteAllByFilter", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("deleteAllByFilter", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("deleteAllByFilter", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
    }
	
	



}