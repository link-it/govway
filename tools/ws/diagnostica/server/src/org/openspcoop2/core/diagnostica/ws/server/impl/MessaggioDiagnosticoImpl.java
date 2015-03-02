package org.openspcoop2.core.diagnostica.ws.server.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.ws.server.MessaggioDiagnosticoSearch;
import org.openspcoop2.core.diagnostica.ws.server.config.Constants;
import org.openspcoop2.core.diagnostica.ws.server.config.DriverDiagnostica;
import org.openspcoop2.core.diagnostica.ws.server.config.LoggerProperties;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotAuthorizedException;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaNotImplementedException;
import org.openspcoop2.core.diagnostica.ws.server.exception.DiagnosticaServiceException;
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
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
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
			filterSearch.setDataInizio(filter.getOraRegistrazioneMax());
		}
		
		if(filter.getFiltroInformazioneProtocollo()!=null){
			
			if(filter.getFiltroInformazioneProtocollo().getTipoPorta()!=null){
				switch (filter.getFiltroInformazioneProtocollo().getTipoPorta()) {
				case PORTA_DELEGATA:
					filterSearch.setDelegata(true);
					break;
				case PORTA_APPLICATIVA:
					filterSearch.setDelegata(false);
					break;
				}
			}
			
			if(filter.getFiltroInformazioneProtocollo().getNomePorta()!=null){
				filterSearch.setNomePorta(filter.getFiltroInformazioneProtocollo().getNomePorta());
			}
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
		
		if(filter.getFiltroInformazioneProtocollo()!=null){
			if(filter.getFiltroInformazioneProtocollo().getRicercaSoloMessaggiCorrelatiInformazioniProtocollo()!=null){
				filterSearch.setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(filter.getFiltroInformazioneProtocollo().getRicercaSoloMessaggiCorrelatiInformazioniProtocollo());
			}	
		}
			
		if(filter.getIdentificativoRichiesta()!=null){
			filterSearch.setIdBustaRichiesta(filter.getIdentificativoRichiesta());
		}
		
		if(filter.getIdentificativoRisposta()!=null){
			filterSearch.setIdBustaRisposta(filter.getIdentificativoRisposta());
		}	
		
		if(filter.getFiltroInformazioneProtocollo()!=null){
			if(filter.getFiltroInformazioneProtocollo().getFruitore()!=null){
				if(filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativoPorta()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filterSearch.getInformazioniProtocollo().getFruitore()==null){
						filterSearch.getInformazioniProtocollo().setFruitore(new IDSoggetto());
					}
					filterSearch.getInformazioniProtocollo().getFruitore().setCodicePorta(filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativoPorta());
				}
				if(filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativo()!=null){
					if(filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativo().getBase()!=null || filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativo().getTipo()!=null){
						if(filterSearch.getInformazioniProtocollo()==null){
							filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
						}
						if(filterSearch.getInformazioniProtocollo().getFruitore()==null){
							filterSearch.getInformazioniProtocollo().setFruitore(new IDSoggetto());
						}
						filterSearch.getInformazioniProtocollo().getFruitore().setTipo(filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativo().getTipo());
						filterSearch.getInformazioniProtocollo().getFruitore().setNome(filter.getFiltroInformazioneProtocollo().getFruitore().getIdentificativo().getBase());
					}
				}
			}
			
			if(filter.getFiltroInformazioneProtocollo().getErogatore()!=null){
				if(filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativoPorta()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filterSearch.getInformazioniProtocollo().getErogatore()==null){
						filterSearch.getInformazioniProtocollo().setErogatore(new IDSoggetto());
					}
					filterSearch.getInformazioniProtocollo().getErogatore().setCodicePorta(filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativoPorta());
				}
				if(filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativo()!=null){
					if(filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativo().getBase()!=null || filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativo().getTipo()!=null){
						if(filterSearch.getInformazioniProtocollo()==null){
							filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
						}
						if(filterSearch.getInformazioniProtocollo().getErogatore()==null){
							filterSearch.getInformazioniProtocollo().setErogatore(new IDSoggetto());
						}
						filterSearch.getInformazioniProtocollo().getErogatore().setTipo(filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativo().getTipo());
						filterSearch.getInformazioniProtocollo().getErogatore().setNome(filter.getFiltroInformazioneProtocollo().getErogatore().getIdentificativo().getBase());
					}
				}
			}
			
			if(filter.getFiltroInformazioneProtocollo().getServizio()!=null){
				if(filter.getFiltroInformazioneProtocollo().getServizio().getTipo()!=null || 
						filter.getFiltroInformazioneProtocollo().getServizio().getBase()!=null ||
						filter.getFiltroInformazioneProtocollo().getServizio().getVersione()!=null){
					if(filterSearch.getInformazioniProtocollo()==null){
						filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
					}
					if(filter.getFiltroInformazioneProtocollo().getServizio().getTipo()!=null){
						filterSearch.getInformazioniProtocollo().setTipoServizio(filter.getFiltroInformazioneProtocollo().getServizio().getTipo());
					}
					if(filter.getFiltroInformazioneProtocollo().getServizio().getBase()!=null){
						filterSearch.getInformazioniProtocollo().setServizio(filter.getFiltroInformazioneProtocollo().getServizio().getBase());
					}
					if(filter.getFiltroInformazioneProtocollo().getServizio().getVersione()!=null){
						filterSearch.getInformazioniProtocollo().setVersioneServizio(filter.getFiltroInformazioneProtocollo().getServizio().getVersione());
					}
				}	
			}
			
			if(filter.getFiltroInformazioneProtocollo().getAzione()!=null){
				if(filterSearch.getInformazioniProtocollo()==null){
					filterSearch.setInformazioniProtocollo(new InformazioniProtocollo());
				}
				filterSearch.getInformazioniProtocollo().setAzione(filter.getFiltroInformazioneProtocollo().getAzione());
			}
			
			if(filter.getFiltroInformazioneProtocollo().getServizioApplicativo()!=null){
				filterSearch.setServizioApplicativo(filter.getFiltroInformazioneProtocollo().getServizioApplicativo());
			}

			if(filter.getFiltroInformazioneProtocollo().getIdentificativoCorrelazioneRichiesta()!=null){
				filterSearch.setCorrelazioneApplicativa(filter.getFiltroInformazioneProtocollo().getIdentificativoCorrelazioneRichiesta());
			}
			if(filter.getFiltroInformazioneProtocollo().getIdentificativoCorrelazioneRisposta()!=null){
				filterSearch.setCorrelazioneApplicativaRisposta(filter.getFiltroInformazioneProtocollo().getIdentificativoCorrelazioneRisposta());
			}
			if(filter.getFiltroInformazioneProtocollo().getCorrelazioneApplicativaAndMatch()!=null){
				filterSearch.setCorrelazioneApplicativaOrMatch(filter.getFiltroInformazioneProtocollo().getCorrelazioneApplicativaAndMatch());
			}
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
		
		return filterSearch;
	}

	private FiltroRicercaDiagnosticiConPaginazione toPaginatedFilterSearch(SearchFilterMessaggioDiagnostico filter) throws ServiceException, NotImplementedException, Exception{
	
		FiltroRicercaDiagnostici filterSearch = this.toFilterSearch(filter);
		
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
	
	
	



	@Override
	public List<MessaggioDiagnostico> findAll(SearchFilterMessaggioDiagnostico filter) throws DiagnosticaServiceException,DiagnosticaNotImplementedException,DiagnosticaNotAuthorizedException {
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
						result.add(msgDiagnostico.getMessaggioDiagnostico());
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
	public long count(SearchFilterMessaggioDiagnostico filter) throws DiagnosticaServiceException,DiagnosticaNotImplementedException,DiagnosticaNotAuthorizedException {
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