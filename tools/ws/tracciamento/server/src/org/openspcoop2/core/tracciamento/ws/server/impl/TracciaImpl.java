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

package org.openspcoop2.core.tracciamento.ws.server.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.tracciamento.DominioIdTraccia;
import org.openspcoop2.core.tracciamento.DominioSoggetto;
import org.openspcoop2.core.tracciamento.Traccia;
import org.openspcoop2.core.tracciamento.ws.server.TracciaSearch;
import org.openspcoop2.core.tracciamento.ws.server.config.Constants;
import org.openspcoop2.core.tracciamento.ws.server.config.DriverTracciamento;
import org.openspcoop2.core.tracciamento.ws.server.config.LoggerProperties;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoMultipleResultException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotAuthorizedException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotFoundException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotImplementedException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoServiceException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.filter.SearchFilterTraccia;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracce;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo;

/**     
 * TracciaImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class TracciaImpl extends BaseImpl  implements TracciaSearch {

	private DriverTracciamento tracciamentoService;

	public TracciaImpl() {
		super();
		try {
			this.tracciamentoService = DriverTracciamento.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Tracciamento Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Tracciamento Service",  e);
		}
	}
	

	
	
	
	private FiltroRicercaTracce toFilterSearch(SearchFilterTraccia filter) throws ServiceException, NotImplementedException, Exception{
		return this.toFilterSearch(filter, false);
	}
	private FiltroRicercaTracce toFilterSearch(SearchFilterTraccia filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
			
		FiltroRicercaTracce filterSearch = null;
		if(paginated){
			filterSearch = new FiltroRicercaTracceConPaginazione();
		}else{
			filterSearch = new FiltroRicercaTracce();
		}
		
		if(filter.getOraRegistrazioneMin()!=null){
			filterSearch.setMinDate(filter.getOraRegistrazioneMin());
		}
		if(filter.getOraRegistrazioneMax()!=null){
			filterSearch.setMaxDate(filter.getOraRegistrazioneMax());
		}
		
		if(filter.getTipo()!=null){
			switch (filter.getTipo()) {
			case RICHIESTA:
				filterSearch.setTipoTraccia(TipoTraccia.RICHIESTA);
				break;
			case RISPOSTA:
				filterSearch.setTipoTraccia(TipoTraccia.RISPOSTA);
				break;
			}
		}
		
		if(filter.getDominio()!=null){
			
			if(filter.getDominio().getFunzione()!=null){
				switch (filter.getDominio().getFunzione()) {
				case PORTA_DELEGATA:
					filterSearch.setTipoPdD(TipoPdD.DELEGATA);
					break;
				case PORTA_APPLICATIVA:
					filterSearch.setTipoPdD(TipoPdD.APPLICATIVA);
					break;
				case ROUTER:
					filterSearch.setTipoPdD(TipoPdD.ROUTER);
					break;
				case INTEGRATION_MANAGER:
					filterSearch.setTipoPdD(TipoPdD.INTEGRATION_MANAGER);
					break;
				}
			}
			
			if(filter.getDominio().getIdentificativoPorta()!=null){
				if(filterSearch.getDominio()==null){
					filterSearch.setDominio(new IDSoggetto());
				}
				filterSearch.getDominio().setCodicePorta(filter.getDominio().getIdentificativoPorta());
			}
			
			if(filter.getDominio().getSoggetto()!=null){
				if(filter.getDominio().getSoggetto().getBase()!=null){
					if(filterSearch.getDominio()==null){
						filterSearch.setDominio(new IDSoggetto());
					}
					filterSearch.getDominio().setNome(filter.getDominio().getSoggetto().getBase());
				}	
				if(filter.getDominio().getSoggetto().getTipo()!=null){
					if(filterSearch.getDominio()==null){
						filterSearch.setDominio(new IDSoggetto());
					}
					filterSearch.getDominio().setTipo(filter.getDominio().getSoggetto().getTipo());
				}	
			}
			
		}
		
		if(filter.getBusta()!=null){
			
			if(filter.getBusta().getIdentificativo()!=null){
				filterSearch.setIdBusta(filter.getBusta().getIdentificativo());
			}
			
			if(filter.getBusta().getRiferimentoMessaggio()!=null){
				filterSearch.setRiferimentoMessaggio(filter.getBusta().getRiferimentoMessaggio());
			}
			
		}
		
		if(filter.getRicercaSoloBusteErrore()!=null){
			filterSearch.setRicercaSoloBusteErrore(filter.getRicercaSoloBusteErrore());
		}
		
		
		if(filter.getBusta()!=null){
			
			if(filter.getBusta().getMittente()!=null){
				if(filter.getBusta().getMittente().getIdentificativoPorta()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filterSearch.getInformazioniProtocollo().getMittente()==null){
						filterSearch.getInformazioniProtocollo().setMittente(new IDSoggetto());
					}
					filterSearch.getInformazioniProtocollo().getMittente().setCodicePorta(filter.getBusta().getMittente().getIdentificativoPorta());
				}
				if(filter.getBusta().getMittente().getIdentificativo()!=null){
					if(filter.getBusta().getMittente().getIdentificativo().getBase()!=null || filter.getBusta().getMittente().getIdentificativo().getTipo()!=null){
						if(filterSearch.getInformazioniProtocollo()==null){
							filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
						}
						if(filterSearch.getInformazioniProtocollo().getMittente()==null){
							filterSearch.getInformazioniProtocollo().setMittente(new IDSoggetto());
						}
						filterSearch.getInformazioniProtocollo().getMittente().setTipo(filter.getBusta().getMittente().getIdentificativo().getTipo());
						filterSearch.getInformazioniProtocollo().getMittente().setNome(filter.getBusta().getMittente().getIdentificativo().getBase());
					}
				}
			}
			
			if(filter.getBusta().getDestinatario()!=null){
				if(filter.getBusta().getDestinatario().getIdentificativoPorta()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filterSearch.getInformazioniProtocollo().getDestinatario()==null){
						filterSearch.getInformazioniProtocollo().setDestinatario(new IDSoggetto());
					}
					filterSearch.getInformazioniProtocollo().getDestinatario().setCodicePorta(filter.getBusta().getDestinatario().getIdentificativoPorta());
				}
				if(filter.getBusta().getDestinatario().getIdentificativo()!=null){
					if(filter.getBusta().getDestinatario().getIdentificativo().getBase()!=null || filter.getBusta().getDestinatario().getIdentificativo().getTipo()!=null){
						if(filterSearch.getInformazioniProtocollo()==null){
							filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
						}
						if(filterSearch.getInformazioniProtocollo().getDestinatario()==null){
							filterSearch.getInformazioniProtocollo().setDestinatario(new IDSoggetto());
						}
						filterSearch.getInformazioniProtocollo().getDestinatario().setTipo(filter.getBusta().getDestinatario().getIdentificativo().getTipo());
						filterSearch.getInformazioniProtocollo().getDestinatario().setNome(filter.getBusta().getDestinatario().getIdentificativo().getBase());
					}
				}
			}
			
			if(filter.getBusta().getServizio()!=null){
				if(filter.getBusta().getServizio().getTipo()!=null || 
						filter.getBusta().getServizio().getBase()!=null ||
						filter.getBusta().getServizio().getVersione()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filter.getBusta().getServizio().getTipo()!=null){
						filterSearch.getInformazioniProtocollo().setTipoServizio(filter.getBusta().getServizio().getTipo());
					}
					if(filter.getBusta().getServizio().getBase()!=null){
						filterSearch.getInformazioniProtocollo().setServizio(filter.getBusta().getServizio().getBase());
					}
					if(filter.getBusta().getServizio().getVersione()!=null){
						filterSearch.getInformazioniProtocollo().setVersioneServizio(filter.getBusta().getServizio().getVersione());
					}
				}	
			}
			
			if(filter.getBusta().getAzione()!=null){
				if(filterSearch.getInformazioniProtocollo()==null){
					filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
				}
				filterSearch.getInformazioniProtocollo().setAzione(filter.getBusta().getAzione());
			}
			
			if(filter.getBusta().getProfiloCollaborazione()!=null){
				if(filter.getBusta().getProfiloCollaborazione().getBase()!=null || 
						filter.getBusta().getProfiloCollaborazione().getTipo()!=null	){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filter.getBusta().getProfiloCollaborazione().getBase()!=null){
						filterSearch.getInformazioniProtocollo().setProfiloCollaborazioneProtocollo(filter.getBusta().getProfiloCollaborazione().getBase());
					}
					if(filter.getBusta().getProfiloCollaborazione().getTipo()!=null){
						switch (filter.getBusta().getProfiloCollaborazione().getTipo()) {
						case ONEWAY:
							filterSearch.getInformazioniProtocollo().setProfiloCollaborazioneEngine(ProfiloDiCollaborazione.ONEWAY);
							break;
						case SINCRONO:
							filterSearch.getInformazioniProtocollo().setProfiloCollaborazioneEngine(ProfiloDiCollaborazione.SINCRONO);
							break;
						case ASINCRONO_ASIMMETRICO:
							filterSearch.getInformazioniProtocollo().setProfiloCollaborazioneEngine(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
							break;
						case ASINCRONO_SIMMETRICO:
							filterSearch.getInformazioniProtocollo().setProfiloCollaborazioneEngine(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
							break;
						case SCONOSCIUTO:
							filterSearch.getInformazioniProtocollo().setProfiloCollaborazioneEngine(ProfiloDiCollaborazione.UNKNOWN);
							break;
						}
					}
				}	
			}
			
			if(filter.getBusta().getServizioApplicativoFruitore()!=null){
				filterSearch.setServizioApplicativoFruitore(filter.getBusta().getServizioApplicativoFruitore());
			}
			
			if(filter.getBusta().getServizioApplicativoErogatore()!=null){
				filterSearch.setServizioApplicativoErogatore(filter.getBusta().getServizioApplicativoErogatore());
			}
			
			if(filter.getIdentificativoCorrelazioneRichiesta()!=null){
				filterSearch.setIdCorrelazioneApplicativa(filter.getIdentificativoCorrelazioneRichiesta());
			}
			if(filter.getIdentificativoCorrelazioneRisposta()!=null){
				filterSearch.setIdCorrelazioneApplicativaRisposta(filter.getIdentificativoCorrelazioneRisposta());
			}
			if(filter.getCorrelazioneApplicativaAndMatch()!=null){
				filterSearch.setIdCorrelazioneApplicativaOrMatch(!filter.getCorrelazioneApplicativaAndMatch());
			}
			
		}
	
		if(filter.getBusta()!=null && filter.getBusta().getProtocollo()!=null && filter.getBusta().getProtocollo().getIdentificativo()!=null){
			filterSearch.setProtocollo(filter.getBusta().getProtocollo().getIdentificativo());
		}
		
		return filterSearch;
	}

	private FiltroRicercaTracceConPaginazione toPaginatedFilterSearch(SearchFilterTraccia filter) throws ServiceException, NotImplementedException, Exception{
	
		FiltroRicercaTracce filterSearch = this.toFilterSearch(filter, true);
		
		if(filter.getLimit()!=null) {
			((FiltroRicercaTracceConPaginazione)filterSearch).setLimit(filter.getLimit());
		}
		
		if(filter.getOffset()!=null) {
			((FiltroRicercaTracceConPaginazione)filterSearch).setOffset(filter.getOffset());
		}
		
		if(filter.getDescOrder()!=null) {
			if(filter.getDescOrder()) {
				((FiltroRicercaTracceConPaginazione)filterSearch).setAsc(false);
			}
			else{
				((FiltroRicercaTracceConPaginazione)filterSearch).setAsc(true);
			}
		}
				
		return ((FiltroRicercaTracceConPaginazione)filterSearch);
	}
	
	



	@Override
	public List<Traccia> findAll(SearchFilterTraccia filter) throws TracciamentoServiceException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTracciamento(this.tracciamentoService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			FiltroRicercaTracceConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			List<Traccia> result = new ArrayList<Traccia>();
			try{
				List<org.openspcoop2.protocol.sdk.tracciamento.Traccia> resultOp2 = this.tracciamentoService.getDriver().getTracce(pagExp);
				if(resultOp2!=null && resultOp2.size()>0){
					for (org.openspcoop2.protocol.sdk.tracciamento.Traccia traccia : resultOp2) {
						result.add(traccia.getTraccia());
					}
				}
			}catch(DriverTracciamentoNotFoundException notFound){
				LoggerProperties.getLoggerWS().debug("NotFound: "+notFound.getMessage(),notFound);
			}
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

	@Override
	public Traccia find(SearchFilterTraccia filter) throws TracciamentoServiceException_Exception,TracciamentoNotFoundException_Exception,TracciamentoMultipleResultException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTracciamento(this.tracciamentoService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			FiltroRicercaTracceConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			Traccia result = null;
			try{
				List<org.openspcoop2.protocol.sdk.tracciamento.Traccia> resultOp2 = this.tracciamentoService.getDriver().getTracce(pagExp);
				if(resultOp2==null || resultOp2.size()<=0){
					throw new NotFoundException("Tracce not found");
				}
				if(resultOp2.size()>1){
					throw new MultipleResultException("Found "+resultOp2.size()+" elements");
				}
				result = resultOp2.get(0).getTraccia();
			}catch(DriverTracciamentoNotFoundException notFound){
				throw new NotFoundException(notFound.getMessage(),notFound);
			}
			this.logEndMethod("find", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("find", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("find", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("find", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (NotFoundException e) {
			throw throwNotFoundException("find", e, Constants.CODE_NOT_FOUND_EXCEPTION, filter);
		} catch (MultipleResultException e) {
			throw throwMultipleResultException("find", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("find", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("find", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("find", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
	}

	@Override
	public long count(SearchFilterTraccia filter) throws TracciamentoServiceException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTracciamento(this.tracciamentoService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			FiltroRicercaTracce exp = this.toFilterSearch(filter);
			long result = this.tracciamentoService.getDriver().countTracce(exp);
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

	@Override
	public Traccia get(org.openspcoop2.core.tracciamento.IdTraccia id) throws TracciamentoServiceException_Exception,TracciamentoNotFoundException_Exception,TracciamentoMultipleResultException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception {
		try{
		
			if(id.getIdentificativo()==null){
				throw new ServiceException("Identificativo busta non fornito");
			}
			IDSoggetto idSoggetto = null;
			if(id.getDominio()!=null){
				if(id.getDominio().getIdentificativoPorta()!=null){
					if(idSoggetto==null)
						idSoggetto = new IDSoggetto();
					idSoggetto.setCodicePorta(id.getDominio().getIdentificativoPorta());
				}
				if(id.getDominio().getSoggetto()!=null){
					if(id.getDominio().getSoggetto().getBase()!=null || id.getDominio().getSoggetto().getTipo()!=null){
						if(idSoggetto==null)
							idSoggetto = new IDSoggetto();
						idSoggetto.setNome(id.getDominio().getSoggetto().getBase());
						idSoggetto.setTipo(id.getDominio().getSoggetto().getTipo());
					}	
				}
			}
			
			checkInitDriverTracciamento(this.tracciamentoService);
			this.logStartMethod("get", id);
			this.authorize(true);
			Traccia result = null;
			try{
				org.openspcoop2.protocol.sdk.tracciamento.Traccia tracciaOp2 = this.tracciamentoService.getDriver().getTraccia(id.getIdentificativo(), idSoggetto);
				if(tracciaOp2==null){
					throw new DriverTracciamentoNotFoundException("Not found");
				}
				result = tracciaOp2.getTraccia();
			}catch(DriverTracciamentoNotFoundException notFound){
				throw new NotFoundException(notFound.getMessage(),notFound);
			}
			this.logEndMethod("get", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("get", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("get", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("get", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (NotFoundException e) {
			throw throwNotFoundException("get", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
//		} catch (MultipleResultException e) {
//			throw throwMultipleResultException("get", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("get", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}
	
	@Override
	public boolean exists(org.openspcoop2.core.tracciamento.IdTraccia id) throws TracciamentoServiceException_Exception,TracciamentoMultipleResultException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception {
		try{
		
			if(id.getIdentificativo()==null){
				throw new ServiceException("Identificativo busta non fornito");
			}
			
			checkInitDriverTracciamento(this.tracciamentoService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			FiltroRicercaTracce exp = new FiltroRicercaTracce();
			exp.setIdBusta(id.getIdentificativo());
			if(id.getDominio()!=null){
				if(id.getDominio().getIdentificativoPorta()!=null){
					if(exp.getDominio()==null){
						exp.setDominio(new IDSoggetto());
					}
					exp.getDominio().setCodicePorta(id.getDominio().getIdentificativoPorta());
				}
				if(id.getDominio().getSoggetto()!=null){
					if(id.getDominio().getSoggetto().getBase()!=null || id.getDominio().getSoggetto().getTipo()!=null){
						if(exp.getDominio()==null){
							exp.setDominio(new IDSoggetto());
						}
						exp.getDominio().setTipo(id.getDominio().getSoggetto().getTipo());
						exp.getDominio().setNome(id.getDominio().getSoggetto().getBase());
					}	
				}
			}
			long result = this.tracciamentoService.getDriver().countTracce(exp);
			if(result>1){
				throw new MultipleResultException("Found "+result+" elements");	
			}
			boolean resultBoolean = result==1;
			this.logEndMethod("exists", resultBoolean);
			return resultBoolean;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("exists", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("exists", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("exists", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (MultipleResultException e) {
			throw throwMultipleResultException("exists", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("exists", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public List<org.openspcoop2.core.tracciamento.IdTraccia> findAllIds(SearchFilterTraccia filter) throws TracciamentoServiceException_Exception,TracciamentoNotImplementedException_Exception,TracciamentoNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTracciamento(this.tracciamentoService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			FiltroRicercaTracceConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			List<org.openspcoop2.core.tracciamento.IdTraccia> result = new ArrayList<org.openspcoop2.core.tracciamento.IdTraccia>();
			try{
				List<org.openspcoop2.protocol.sdk.tracciamento.Traccia> resultOp2 = this.tracciamentoService.getDriver().getTracce(pagExp);
				if(resultOp2!=null && resultOp2.size()>0){
					for (org.openspcoop2.protocol.sdk.tracciamento.Traccia traccia : resultOp2) {
						if(traccia.getBusta()!=null && traccia.getBusta().getID()!=null){
							org.openspcoop2.core.tracciamento.IdTraccia idTraccia = new org.openspcoop2.core.tracciamento.IdTraccia();
							idTraccia.setIdentificativo(traccia.getBusta().getID());
							if(traccia.getIdSoggetto()!=null){
								if(traccia.getIdSoggetto().getCodicePorta()!=null){
									if(idTraccia.getDominio()==null)
										idTraccia.setDominio(new DominioIdTraccia());
									idTraccia.getDominio().setIdentificativoPorta(traccia.getIdSoggetto().getCodicePorta());
								}
								if(traccia.getIdSoggetto().getNome()!=null || traccia.getIdSoggetto().getTipo()!=null){
									if(idTraccia.getDominio()==null)
										idTraccia.setDominio(new DominioIdTraccia());
									if(idTraccia.getDominio().getSoggetto()==null)
										idTraccia.getDominio().setSoggetto(new DominioSoggetto());
									idTraccia.getDominio().getSoggetto().setBase(traccia.getIdSoggetto().getNome());
									idTraccia.getDominio().getSoggetto().setTipo(traccia.getIdSoggetto().getTipo());
								}
							}
							result.add(idTraccia);
						}
						else{
							LoggerProperties.getLoggerWS().error("Traccia without ID: "+traccia.toString());
						}
					}
				}
			}catch(DriverTracciamentoNotFoundException notFound){
				LoggerProperties.getLoggerWS().debug("NotFound: "+notFound.getMessage(),notFound);
			}
			this.logEndMethod("findAllIds", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("findAllIds", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("findAllIds", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("findAllIds", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (ExpressionNotImplementedException e) {
			throw throwServiceException("findAllIds", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ExpressionException e) {
			throw throwServiceException("findAllIds", e, Constants.CODE_EXPRESSION_EXCEPTION, filter);
		} catch (Exception e){
			throw throwServiceException("findAllIds", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, filter);
		}
	}

	



}