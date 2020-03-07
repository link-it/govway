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

package org.openspcoop2.core.diagnostica.ws.server.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.ws.server.MessaggioDiagnosticoSearch;
import org.openspcoop2.core.diagnostica.ws.server.config.Constants;
import org.openspcoop2.core.diagnostica.ws.server.config.DriverDiagnostica;
import org.openspcoop2.core.diagnostica.ws.server.config.LoggerProperties;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotAuthorizedException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotImplementedException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaServiceException_Exception;
import org.openspcoop2.core.diagnostica.ws.server.filter.SearchFilterMessaggioDiagnostico;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;

/**     
 * MessaggioDiagnosticoImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class MessaggioDiagnosticoImpl extends BaseImpl  implements MessaggioDiagnosticoSearch {

	private DriverDiagnostica diagnosticaService = null; 

	public MessaggioDiagnosticoImpl() {
		super();
		try {
			this.diagnosticaService = DriverDiagnostica.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Diagnostica Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Diagnostica Service",  e);
		}
	}
	
	
	
	
	private FiltroRicercaDiagnostici toFilterSearch(SearchFilterMessaggioDiagnostico filter) throws ServiceException, NotImplementedException, Exception{
		return this.toFilterSearch(filter, false);
	}
	private FiltroRicercaDiagnostici toFilterSearch(SearchFilterMessaggioDiagnostico filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
			
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
		
		if(filter.getIdTransazione()!=null){
			filterSearch.setIdTransazione(filter.getIdTransazione());
		}
					
		if(filter.getDominio()!=null){
			
			if(filter.getDominio().getModulo()!=null){
				filterSearch.setIdFunzione(filter.getDominio().getModulo());
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
		
		if(filter.getIdentificativoRichiesta()!=null){
			filterSearch.setIdBustaRichiesta(filter.getIdentificativoRichiesta());
		}
		
		if(filter.getIdentificativoRisposta()!=null){
			filterSearch.setIdBustaRisposta(filter.getIdentificativoRisposta());
		}	
		
		
		if(filter.getSeverita()!=null){
			filterSearch.setSeverita(filter.getSeverita());
		}	
		
		if(filter.getCodice()!=null){
			filterSearch.setCodice(filter.getCodice());
		}
		
		if(filter.getMessaggio()!=null){
			filterSearch.setMessaggioCercatoInternamenteTestoDiagnostico(filter.getMessaggio());
		}
		
		if(filter.getProtocollo()!=null && filter.getProtocollo().getIdentificativo()!=null){
			filterSearch.setProtocollo(filter.getProtocollo().getIdentificativo());
		}
		
		if(filter.getApplicativo()!=null) {
			filterSearch.setApplicativo(filter.getApplicativo());
		}
		
		return filterSearch;
	}

	private FiltroRicercaDiagnosticiConPaginazione toPaginatedFilterSearch(SearchFilterMessaggioDiagnostico filter) throws ServiceException, NotImplementedException, Exception{
	
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
	
	

	private void cleanIdDB(MessaggioDiagnostico msgDiag){
		if(msgDiag.getProtocollo()!=null){
			if(msgDiag.getProtocollo().sizeProprietaList()>0){
				for (int i = 0; i < msgDiag.getProtocollo().sizeProprietaList(); i++) {
					Proprieta p = msgDiag.getProtocollo().getProprieta(i);
					if(org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver.IDDIAGNOSTICI.equals(p.getNome())){
						msgDiag.getProtocollo().removeProprieta(i);
						break;
					}
				}
			}
		}
	}



	@Override
	public List<MessaggioDiagnostico> findAll(SearchFilterMessaggioDiagnostico filter) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			FiltroRicercaDiagnosticiConPaginazione pagExp = this.toPaginatedFilterSearch(filter);
			List<MessaggioDiagnostico> result = new ArrayList<MessaggioDiagnostico>();
			try{
				List<MsgDiagnostico> resultOp2 = this.diagnosticaService.getDriver().getMessaggiDiagnostici(pagExp);
				if(resultOp2!=null && resultOp2.size()>0){
					for (MsgDiagnostico msgDiagnostico : resultOp2) {
						MessaggioDiagnostico msgDiag = msgDiagnostico.getMessaggioDiagnostico();
						cleanIdDB(msgDiag);
						result.add(msgDiag);
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
	public long count(SearchFilterMessaggioDiagnostico filter) throws DiagnosticaServiceException_Exception,DiagnosticaNotImplementedException_Exception,DiagnosticaNotAuthorizedException_Exception {
		try{
		
			checkInitDriverDiagnostica(this.diagnosticaService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			FiltroRicercaDiagnostici exp = this.toFilterSearch(filter);
			long result = this.diagnosticaService.getDriver().countMessaggiDiagnostici(exp);
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



}