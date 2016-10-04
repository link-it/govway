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

package org.openspcoop2.core.diagnostica.ws.server.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione;
import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;
import org.openspcoop2.core.diagnostica.ws.server.InformazioniProtocolloTransazioneSearch;
import org.openspcoop2.core.diagnostica.ws.server.config.Constants;
import org.openspcoop2.core.diagnostica.ws.server.config.DriverDiagnostica;
import org.openspcoop2.core.diagnostica.ws.server.config.LoggerProperties;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaMultipleResultException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotAuthorizedException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotFoundException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotImplementedException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaServiceException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.filter.SearchFilterInformazioniProtocolloTransazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;

/**     
 * InformazioniProtocolloTransazioneImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class InformazioniProtocolloTransazioneImpl extends BaseImpl  implements InformazioniProtocolloTransazioneSearch {

	private DriverDiagnostica diagnosticaService = null; 

	public InformazioniProtocolloTransazioneImpl() {
		super();
		try {
			this.diagnosticaService = DriverDiagnostica.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Diagnostica Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Diagnostica Service",  e);
		} 
	}
	

	
	private FiltroRicercaDiagnostici toFilterSearch(SearchFilterInformazioniProtocolloTransazione filter) throws ServiceException, NotImplementedException, Exception{
		return this.toFilterSearch(filter, false);
	}
	private FiltroRicercaDiagnostici toFilterSearch(SearchFilterInformazioniProtocolloTransazione filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
			
		FiltroRicercaDiagnostici filterSearch = null;
		if(paginated){
			filterSearch = new FiltroRicercaDiagnosticiConPaginazione();
		}else{
			filterSearch = new FiltroRicercaDiagnostici();
		}
		
		if(filter.getOraRegistrazioneMin()!=null){
			filterSearch.setDataInizio(filter.getOraRegistrazioneMin());
		}
		if(filter.getOraRegistrazioneMax()!=null){
			filterSearch.setDataFine(filter.getOraRegistrazioneMax());
		}
		
		if(filter.getTipoPdD()!=null){
			switch (filter.getTipoPdD()) {
			case PORTA_DELEGATA:
				filterSearch.setDelegata(true);
				break;
			case PORTA_APPLICATIVA:
				filterSearch.setDelegata(false);
				break;
			}
		}
		
		if(filter.getNomePorta()!=null){
			filterSearch.setNomePorta(filter.getNomePorta());
		}
		
		if(filter.getFiltroInformazioniDiagnostici()!=null){
			if(filter.getFiltroInformazioniDiagnostici().getModulo()!=null){
				filterSearch.setIdFunzione(filter.getFiltroInformazioniDiagnostici().getModulo());
			}	
		}
		
		if(filter.getDominio()!=null){
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
		
		// non ha senso in questo caso: siamo gia dentro le informazioni di protocollo
		// filterSearch.setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(true/false);
			
		if(filter.getIdentificativoRichiesta()!=null){
			filterSearch.setIdBustaRichiesta(filter.getIdentificativoRichiesta());
		}
		
		if(filter.getFiltroInformazioniDiagnostici()!=null){
			if(filter.getFiltroInformazioniDiagnostici().getIdentificativoRisposta()!=null){
				filterSearch.setIdBustaRisposta(filter.getFiltroInformazioniDiagnostici().getIdentificativoRisposta());
			}	
		}
		
		if(filter.getFruitore()!=null){
			// Filtro non supportato dal driver
//			if(filter.getFruitore().getIdentificativoPorta()!=null){
//				if(filterSearch.getInformazioniProtocollo()==null){
//					filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
//				}
//				if(filterSearch.getInformazioniProtocollo().getFruitore()==null){
//					filterSearch.getInformazioniProtocollo().setFruitore(new IDSoggetto());
//				}
//				filterSearch.getInformazioniProtocollo().getFruitore().setCodicePorta(filter.getFruitore().getIdentificativoPorta());
//			}
			if(filter.getFruitore().getIdentificativo()!=null){
				if(filter.getFruitore().getIdentificativo().getBase()!=null || filter.getFruitore().getIdentificativo().getTipo()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filterSearch.getInformazioniProtocollo().getFruitore()==null){
						filterSearch.getInformazioniProtocollo().setFruitore(new IDSoggetto());
					}
					filterSearch.getInformazioniProtocollo().getFruitore().setTipo(filter.getFruitore().getIdentificativo().getTipo());
					filterSearch.getInformazioniProtocollo().getFruitore().setNome(filter.getFruitore().getIdentificativo().getBase());
				}
			}
		}
		
		if(filter.getErogatore()!=null){
			// Filtro non supportato dal driver
//			if(filter.getErogatore().getIdentificativoPorta()!=null){
//				if(filterSearch.getInformazioniProtocollo()==null){
//					filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
//				}
//				if(filterSearch.getInformazioniProtocollo().getErogatore()==null){
//					filterSearch.getInformazioniProtocollo().setErogatore(new IDSoggetto());
//				}
//				filterSearch.getInformazioniProtocollo().getErogatore().setCodicePorta(filter.getErogatore().getIdentificativoPorta());
//			}
			if(filter.getErogatore().getIdentificativo()!=null){
				if(filter.getErogatore().getIdentificativo().getBase()!=null || filter.getErogatore().getIdentificativo().getTipo()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filterSearch.getInformazioniProtocollo().getErogatore()==null){
						filterSearch.getInformazioniProtocollo().setErogatore(new IDSoggetto());
					}
					filterSearch.getInformazioniProtocollo().getErogatore().setTipo(filter.getErogatore().getIdentificativo().getTipo());
					filterSearch.getInformazioniProtocollo().getErogatore().setNome(filter.getErogatore().getIdentificativo().getBase());
				}
			}
		}
		
		if(filter.getServizio()!=null){
			if(filter.getServizio().getTipo()!=null || 
					filter.getServizio().getBase()!=null ||
					filter.getServizio().getVersione()!=null){
				if(filterSearch.getInformazioniProtocollo()==null){
					filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
				}
				if(filter.getServizio().getTipo()!=null){
					filterSearch.getInformazioniProtocollo().setTipoServizio(filter.getServizio().getTipo());
				}
				if(filter.getServizio().getBase()!=null){
					filterSearch.getInformazioniProtocollo().setServizio(filter.getServizio().getBase());
				}
				if(filter.getServizio().getVersione()!=null){
					filterSearch.getInformazioniProtocollo().setVersioneServizio(filter.getServizio().getVersione());
				}
			}	
		}
		
		if(filter.getAzione()!=null){
			if(filterSearch.getInformazioniProtocollo()==null){
				filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
			}
			filterSearch.getInformazioniProtocollo().setAzione(filter.getAzione());
		}

		if(filter.getFiltroServizioApplicativo()!=null){
			filterSearch.setServizioApplicativo(filter.getFiltroServizioApplicativo());
		}
		
		if(filter.getIdentificativoCorrelazioneRichiesta()!=null){
			filterSearch.setCorrelazioneApplicativa(filter.getIdentificativoCorrelazioneRichiesta());
		}
		if(filter.getIdentificativoCorrelazioneRisposta()!=null){
			filterSearch.setCorrelazioneApplicativaRisposta(filter.getIdentificativoCorrelazioneRisposta());
		}
		if(filter.getCorrelazioneApplicativaAndMatch()!=null){
			filterSearch.setCorrelazioneApplicativaOrMatch(!filter.getCorrelazioneApplicativaAndMatch());
		}

		if(filter.getFiltroInformazioniDiagnostici()!=null){
			
			if(filter.getFiltroInformazioniDiagnostici().getSeverita()!=null){
				filterSearch.setSeverita(filter.getFiltroInformazioniDiagnostici().getSeverita());
			}	
			
			if(filter.getFiltroInformazioniDiagnostici().getCodice()!=null){
				filterSearch.setCodice(filter.getFiltroInformazioniDiagnostici().getCodice());
			}
			
			if(filter.getFiltroInformazioniDiagnostici().getMessaggio()!=null){
				filterSearch.setMessaggioCercatoInternamenteTestoDiagnostico(filter.getFiltroInformazioniDiagnostici().getMessaggio());
			}
			
		}
		
		if(filter.getProtocollo()!=null && filter.getProtocollo().getIdentificativo()!=null){
			filterSearch.setProtocollo(filter.getProtocollo().getIdentificativo());
		}
		
		return filterSearch;
	}

	private FiltroRicercaDiagnosticiConPaginazione toPaginatedFilterSearch(SearchFilterInformazioniProtocolloTransazione filter) throws ServiceException, NotImplementedException, Exception{
	
		FiltroRicercaDiagnostici filterSearch = this.toFilterSearch(filter, true);
		
		if(filter.getLimit()!=null) {
			((FiltroRicercaDiagnosticiConPaginazione)filterSearch).setLimit(filter.getLimit());
		}
		
		if(filter.getOffset()!=null) {
			((FiltroRicercaDiagnosticiConPaginazione)filterSearch).setOffset(filter.getOffset());
		}
		
		if(filter.getDescOrder()!=null) {
			if(filter.getDescOrder()) {
				((FiltroRicercaDiagnosticiConPaginazione)filterSearch).setAsc(false);
			}
			else{
				((FiltroRicercaDiagnosticiConPaginazione)filterSearch).setAsc(true);
			}
		}
				
		return ((FiltroRicercaDiagnosticiConPaginazione)filterSearch);
	}

	

	private void cleanIdDB(InformazioniProtocolloTransazione info){
		if(info.getProtocollo()!=null){
			if(info.getProtocollo().sizeProprietaList()>0){
				for (int i = 0; i < info.getProtocollo().sizeProprietaList(); i++) {
					Proprieta p = info.getProtocollo().getProprieta(i);
					if(org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici.IDDIAGNOSTICI.equals(p.getNome())){
						info.getProtocollo().removeProprieta(i);
						break;
					}
				}
			}
		}
	}
	



	@Override
	public List<InformazioniProtocolloTransazione> findAll(SearchFilterInformazioniProtocolloTransazione filter) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			FiltroRicercaDiagnosticiConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			List<InformazioniProtocolloTransazione> result = new ArrayList<InformazioniProtocolloTransazione>();
			try{
				List<MsgDiagnosticoCorrelazione> resultOp2 = this.diagnosticaService.getDriver().getInfoCorrelazioniMessaggiDiagnostici(pagExp);
				if(resultOp2!=null && resultOp2.size()>0){
					for (MsgDiagnosticoCorrelazione msgDiagnosticoCorrelazione : resultOp2) {
						InformazioniProtocolloTransazione info = msgDiagnosticoCorrelazione.getInformazioniProtocolloTransazione();
						this.cleanIdDB(info);
						result.add(info);
					}
				}
			}catch(DriverMsgDiagnosticiNotFoundException notFound){
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
	public InformazioniProtocolloTransazione find(SearchFilterInformazioniProtocolloTransazione filter) throws DiagnosticaServiceException_Exception,DiagnosticaNotFoundException_Exception,DiagnosticaMultipleResultException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			FiltroRicercaDiagnosticiConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			InformazioniProtocolloTransazione result = null;
			try{
				List<MsgDiagnosticoCorrelazione> resultOp2 = this.diagnosticaService.getDriver().getInfoCorrelazioniMessaggiDiagnostici(pagExp);
				if(resultOp2==null || resultOp2.size()<=0){
					throw new NotFoundException("InfoCorrelazioneMessaggiDiagnostici not found");
				}
				if(resultOp2.size()>1){
					throw new MultipleResultException("Found "+resultOp2.size()+" elements");
				}
				result = resultOp2.get(0).getInformazioniProtocolloTransazione();
				this.cleanIdDB(result);
			}catch(DriverMsgDiagnosticiNotFoundException notFound){
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
	public long count(SearchFilterInformazioniProtocolloTransazione filter) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			FiltroRicercaDiagnostici exp = this.toFilterSearch(filter);
			long result = this.diagnosticaService.getDriver().countInfoCorrelazioniMessaggiDiagnostici(exp);
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
	public InformazioniProtocolloTransazione get(org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione id) throws DiagnosticaServiceException_Exception,DiagnosticaNotFoundException_Exception,DiagnosticaMultipleResultException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			if(id.getIdentificativoRichiesta()==null){
				throw new ServiceException("Param without identificativo richiesta");
			}
			if(id.getTipoPdD()==null){
				throw new ServiceException("Param without tipo pdd");
			}
			
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("get", id);
			this.authorize(true);
			
			FiltroRicercaDiagnosticiConPaginazione filtro = new FiltroRicercaDiagnosticiConPaginazione();
			filtro.setIdBustaRichiesta(id.getIdentificativoRichiesta());
			switch (id.getTipoPdD()) {
			case PORTA_DELEGATA:
				filtro.setDelegata(true);
				break;
			case PORTA_APPLICATIVA:
				filtro.setDelegata(false);
				break;
			}
			InformazioniProtocolloTransazione result = null;
			try{
				List<MsgDiagnosticoCorrelazione> resultOp2 = this.diagnosticaService.getDriver().getInfoCorrelazioniMessaggiDiagnostici(filtro);
				if(resultOp2==null || resultOp2.size()<=0){
					throw new NotFoundException("InfoCorrelazioneMessaggiDiagnostici not found");
				}
				if(resultOp2.size()>1){
					throw new MultipleResultException("Found "+resultOp2.size()+" elements");
				}
				result = resultOp2.get(0).getInformazioniProtocolloTransazione();
				this.cleanIdDB(result);
			}catch(DriverMsgDiagnosticiNotFoundException notFound){
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
		} catch (MultipleResultException e) {
			throw throwMultipleResultException("get", e, Constants.CODE_MULTIPLE_RESULT_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("get", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}
	
	@Override
	public boolean exists(org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione id) throws DiagnosticaServiceException_Exception,DiagnosticaMultipleResultException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			if(id.getIdentificativoRichiesta()==null){
				throw new ServiceException("Param without identificativo richiesta");
			}
			if(id.getTipoPdD()==null){
				throw new ServiceException("Param without tipo pdd");
			}
			
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			
			FiltroRicercaDiagnosticiConPaginazione filtro = new FiltroRicercaDiagnosticiConPaginazione();
			filtro.setIdBustaRichiesta(id.getIdentificativoRichiesta());
			switch (id.getTipoPdD()) {
			case PORTA_DELEGATA:
				filtro.setDelegata(true);
				break;
			case PORTA_APPLICATIVA:
				filtro.setDelegata(false);
				break;
			}
			long result = this.diagnosticaService.getDriver().countInfoCorrelazioniMessaggiDiagnostici(filtro);
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
	public List<org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione> findAllIds(SearchFilterInformazioniProtocolloTransazione filter) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			FiltroRicercaDiagnosticiConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			List<org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione> result = new ArrayList<org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione>();
			try{
				List<MsgDiagnosticoCorrelazione> resultOp2 = this.diagnosticaService.getDriver().getInfoCorrelazioniMessaggiDiagnostici(pagExp);
				if(resultOp2!=null && resultOp2.size()>0){
					for (MsgDiagnosticoCorrelazione msgDiagnosticoCorrelazione : resultOp2) {
						org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione id = new IdInformazioniProtocolloTransazione();
						id.setIdentificativoRichiesta(msgDiagnosticoCorrelazione.getIdBusta());
						if(msgDiagnosticoCorrelazione.isDelegata()){
							id.setTipoPdD(TipoPdD.PORTA_DELEGATA);
						}else{
							id.setTipoPdD(TipoPdD.PORTA_APPLICATIVA);
						}
						result.add(id);
					}
				}
			}catch(DriverMsgDiagnosticiNotFoundException notFound){
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