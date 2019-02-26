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

package org.openspcoop2.core.transazioni.ws.server.impl;

import org.openspcoop2.core.transazioni.ws.server.filter.SearchFilterTransazione;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.ExpressionException;


import java.util.List;
import org.openspcoop2.core.transazioni.ws.server.config.Constants;
import org.openspcoop2.core.transazioni.ws.server.config.DriverTransazioni;
import org.openspcoop2.core.transazioni.ws.server.config.LoggerProperties;
import org.openspcoop2.core.transazioni.Transazione;

import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException_Exception;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException_Exception;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException_Exception;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotImplementedException_Exception;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotAuthorizedException_Exception;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;

import org.openspcoop2.core.transazioni.ws.server.TransazioneSearch;

/**     
 * TransazioneImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class TransazioneImpl extends BaseImpl  implements TransazioneSearch {

	private DriverTransazioni transazioniService;

	public TransazioneImpl() {
		super();
		try {
			this.transazioniService = DriverTransazioni.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione Transazioni Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Transazioni Service",  e);
		}
	}
	
	private org.openspcoop2.core.transazioni.dao.ITransazioneService getService() throws Exception{
		if(this.transazioniService==null){
			throw new Exception("Service isn't correct initialized");
		}
		return this.transazioniService.getDriver().getTransazioneService();
	}


	private IExpression toExpression(SearchFilterTransazione filter) throws ServiceException, NotImplementedException, Exception{
		IExpression exp = this.getService().newExpression();
		exp.and();

		if(filter.getIdTransazione()!= null) {
			exp.equals(Transazione.model().ID_TRANSAZIONE, filter.getIdTransazione());
		}
		if(filter.getStato()!= null) {
			exp.equals(Transazione.model().STATO, filter.getStato());
		}
		if(filter.getEsito()!= null) {
			exp.equals(Transazione.model().ESITO, filter.getEsito());
		}
		if(filter.getEsitoContesto()!= null) {
			exp.equals(Transazione.model().ESITO_CONTESTO, filter.getEsitoContesto());
		}
		if(filter.getProtocollo()!= null) {
			exp.equals(Transazione.model().PROTOCOLLO, filter.getProtocollo());
		}
		if(filter.getDataAccettazioneRichiestaMin()!= null) {
			exp.greaterEquals(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA, filter.getDataAccettazioneRichiestaMin());
		}
		if(filter.getDataAccettazioneRichiestaMax()!= null) {
			exp.lessEquals(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA, filter.getDataAccettazioneRichiestaMax());
		}
		if(filter.getDataIngressoRichiestaMin()!= null) {
			exp.greaterEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, filter.getDataIngressoRichiestaMin());
		}
		if(filter.getDataIngressoRichiestaMax()!= null) {
			exp.lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, filter.getDataIngressoRichiestaMax());
		}
		if(filter.getDataUscitaRichiestaMin()!= null) {
			exp.greaterEquals(Transazione.model().DATA_USCITA_RICHIESTA, filter.getDataUscitaRichiestaMin());
		}
		if(filter.getDataUscitaRichiestaMax()!= null) {
			exp.lessEquals(Transazione.model().DATA_USCITA_RICHIESTA, filter.getDataUscitaRichiestaMax());
		}
		if(filter.getDataAccettazioneRispostaMin()!= null) {
			exp.greaterEquals(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA, filter.getDataAccettazioneRispostaMin());
		}
		if(filter.getDataAccettazioneRispostaMax()!= null) {
			exp.lessEquals(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA, filter.getDataAccettazioneRispostaMax());
		}
		if(filter.getDataIngressoRispostaMin()!= null) {
			exp.greaterEquals(Transazione.model().DATA_INGRESSO_RISPOSTA, filter.getDataIngressoRispostaMin());
		}
		if(filter.getDataIngressoRispostaMax()!= null) {
			exp.lessEquals(Transazione.model().DATA_INGRESSO_RISPOSTA, filter.getDataIngressoRispostaMax());
		}
		if(filter.getDataUscitaRispostaMin()!= null) {
			exp.greaterEquals(Transazione.model().DATA_USCITA_RISPOSTA, filter.getDataUscitaRispostaMin());
		}
		if(filter.getDataUscitaRispostaMax()!= null) {
			exp.lessEquals(Transazione.model().DATA_USCITA_RISPOSTA, filter.getDataUscitaRispostaMax());
		}
		if(filter.getRichiestaIngressoBytesMin()!= null) {
			exp.greaterEquals(Transazione.model().RICHIESTA_INGRESSO_BYTES, filter.getRichiestaIngressoBytesMin());
		}
		if(filter.getRichiestaIngressoBytesMax()!= null) {
			exp.lessEquals(Transazione.model().RICHIESTA_INGRESSO_BYTES, filter.getRichiestaIngressoBytesMax());
		}
		if(filter.getRichiestaUscitaBytesMin()!= null) {
			exp.greaterEquals(Transazione.model().RICHIESTA_USCITA_BYTES, filter.getRichiestaUscitaBytesMin());
		}
		if(filter.getRichiestaUscitaBytesMax()!= null) {
			exp.lessEquals(Transazione.model().RICHIESTA_USCITA_BYTES, filter.getRichiestaUscitaBytesMax());
		}
		if(filter.getRispostaIngressoBytesMin()!= null) {
			exp.greaterEquals(Transazione.model().RISPOSTA_INGRESSO_BYTES, filter.getRispostaIngressoBytesMin());
		}
		if(filter.getRispostaIngressoBytesMax()!= null) {
			exp.lessEquals(Transazione.model().RISPOSTA_INGRESSO_BYTES, filter.getRispostaIngressoBytesMax());
		}
		if(filter.getRispostaUscitaBytesMin()!= null) {
			exp.greaterEquals(Transazione.model().RISPOSTA_USCITA_BYTES, filter.getRispostaUscitaBytesMin());
		}
		if(filter.getRispostaUscitaBytesMax()!= null) {
			exp.lessEquals(Transazione.model().RISPOSTA_USCITA_BYTES, filter.getRispostaUscitaBytesMax());
		}
		if(filter.getPddCodice()!= null) {
			exp.equals(Transazione.model().PDD_CODICE, filter.getPddCodice());
		}
		if(filter.getPddTipoSoggetto()!= null) {
			exp.equals(Transazione.model().PDD_TIPO_SOGGETTO, filter.getPddTipoSoggetto());
		}
		if(filter.getPddNomeSoggetto()!= null) {
			exp.equals(Transazione.model().PDD_NOME_SOGGETTO, filter.getPddNomeSoggetto());
		}
		if(filter.getPddRuolo()!= null) {
			exp.equals(Transazione.model().PDD_RUOLO, filter.getPddRuolo());
		}
		if(filter.getTipoSoggettoFruitore()!= null) {
			exp.equals(Transazione.model().TIPO_SOGGETTO_FRUITORE, filter.getTipoSoggettoFruitore());
		}
		if(filter.getNomeSoggettoFruitore()!= null) {
			exp.equals(Transazione.model().NOME_SOGGETTO_FRUITORE, filter.getNomeSoggettoFruitore());
		}
		if(filter.getIdportaSoggettoFruitore()!= null) {
			exp.equals(Transazione.model().IDPORTA_SOGGETTO_FRUITORE, filter.getIdportaSoggettoFruitore());
		}
		if(filter.getIndirizzoSoggettoFruitore()!= null) {
			exp.equals(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE, filter.getIndirizzoSoggettoFruitore());
		}
		if(filter.getTipoSoggettoErogatore()!= null) {
			exp.equals(Transazione.model().TIPO_SOGGETTO_EROGATORE, filter.getTipoSoggettoErogatore());
		}
		if(filter.getNomeSoggettoErogatore()!= null) {
			exp.equals(Transazione.model().NOME_SOGGETTO_EROGATORE, filter.getNomeSoggettoErogatore());
		}
		if(filter.getIdportaSoggettoErogatore()!= null) {
			exp.equals(Transazione.model().IDPORTA_SOGGETTO_EROGATORE, filter.getIdportaSoggettoErogatore());
		}
		if(filter.getIndirizzoSoggettoErogatore()!= null) {
			exp.equals(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE, filter.getIndirizzoSoggettoErogatore());
		}
		if(filter.getIdMessaggioRichiesta()!= null) {
			exp.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filter.getIdMessaggioRichiesta());
		}
		if(filter.getIdMessaggioRisposta()!= null) {
			exp.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filter.getIdMessaggioRisposta());
		}
		if(filter.getProfiloCollaborazioneOp2()!= null) {
			exp.equals(Transazione.model().PROFILO_COLLABORAZIONE_OP_2, filter.getProfiloCollaborazioneOp2());
		}
		if(filter.getProfiloCollaborazioneProt()!= null) {
			exp.equals(Transazione.model().PROFILO_COLLABORAZIONE_PROT, filter.getProfiloCollaborazioneProt());
		}
		if(filter.getIdCollaborazione()!= null) {
			exp.equals(Transazione.model().ID_COLLABORAZIONE, filter.getIdCollaborazione());
		}
		if(filter.getUriAccordoServizio()!= null) {
			exp.equals(Transazione.model().URI_ACCORDO_SERVIZIO, filter.getUriAccordoServizio());
		}
		if(filter.getTipoServizio()!= null) {
			exp.equals(Transazione.model().TIPO_SERVIZIO, filter.getTipoServizio());
		}
		if(filter.getNomeServizio()!= null) {
			exp.equals(Transazione.model().NOME_SERVIZIO, filter.getNomeServizio());
		}
		if(filter.getVersioneServizio()!= null) {
			exp.equals(Transazione.model().VERSIONE_SERVIZIO, filter.getVersioneServizio());
		}
		if(filter.getAzione()!= null) {
			exp.equals(Transazione.model().AZIONE, filter.getAzione());
		}
		if(filter.getIdAsincrono()!= null) {
			exp.equals(Transazione.model().ID_ASINCRONO, filter.getIdAsincrono());
		}
		if(filter.getTipoServizioCorrelato()!= null) {
			exp.equals(Transazione.model().TIPO_SERVIZIO_CORRELATO, filter.getTipoServizioCorrelato());
		}
		if(filter.getNomeServizioCorrelato()!= null) {
			exp.equals(Transazione.model().NOME_SERVIZIO_CORRELATO, filter.getNomeServizioCorrelato());
		}
		if(filter.getIdCorrelazioneApplicativa()!= null) {
			exp.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, filter.getIdCorrelazioneApplicativa());
		}
		if(filter.getIdCorrelazioneApplicativaRisposta()!= null) {
			exp.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, filter.getIdCorrelazioneApplicativaRisposta());
		}
		if(filter.getServizioApplicativoFruitore()!= null) {
			exp.equals(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE, filter.getServizioApplicativoFruitore());
		}
		if(filter.getServizioApplicativoErogatore()!= null) {
			exp.equals(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE, filter.getServizioApplicativoErogatore());
		}
		if(filter.getOperazioneIm()!= null) {
			exp.equals(Transazione.model().OPERAZIONE_IM, filter.getOperazioneIm());
		}
		if(filter.getLocationRichiesta()!= null) {
			exp.equals(Transazione.model().LOCATION_RICHIESTA, filter.getLocationRichiesta());
		}
		if(filter.getLocationRisposta()!= null) {
			exp.equals(Transazione.model().LOCATION_RISPOSTA, filter.getLocationRisposta());
		}
		if(filter.getNomePorta()!= null) {
			exp.equals(Transazione.model().NOME_PORTA, filter.getNomePorta());
		}
		if(filter.getCredenziali()!= null) {
			exp.equals(Transazione.model().CREDENZIALI, filter.getCredenziali());
		}
		if(filter.getLocationConnettore()!= null) {
			exp.equals(Transazione.model().LOCATION_CONNETTORE, filter.getLocationConnettore());
		}
		if(filter.getUrlInvocazione()!= null) {
			exp.equals(Transazione.model().URL_INVOCAZIONE, filter.getUrlInvocazione());
		}
		if(filter.getClusterId()!= null) {
			exp.equals(Transazione.model().CLUSTER_ID, filter.getClusterId());
		}
		if(filter.getSocketClientAddress()!= null) {
			exp.equals(Transazione.model().SOCKET_CLIENT_ADDRESS, filter.getSocketClientAddress());
		}

		return exp;
	}

	private IPaginatedExpression toPaginatedExpression(SearchFilterTransazione filter) throws ServiceException, NotImplementedException, Exception{
		IPaginatedExpression pagExp = this.getService().toPaginatedExpression(this.toExpression(filter));
		if(filter.getLimit()!=null) {
			pagExp.limit(filter.getLimit());
		}
		if(filter.getOffset()!=null) {
			pagExp.offset(filter.getOffset());
		}
		if(filter.getDescOrder()!=null) {
			if(filter.getDescOrder()) {
				pagExp.sortOrder(SortOrder.DESC);
			}
			else {
				pagExp.sortOrder(SortOrder.ASC);
			}
		}
		else {
			pagExp.sortOrder(SortOrder.DESC);
		}
		pagExp.addOrder(Transazione.model().DATA_INGRESSO_RICHIESTA);
		
		return pagExp;
	}



	@Override
	public List<Transazione> findAll(SearchFilterTransazione filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			IPaginatedExpression pagExp = this.toPaginatedExpression(filter);	
			List<Transazione> result = this.getService().findAll(pagExp);
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
	public Transazione find(SearchFilterTransazione filter) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			IExpression exp = this.toExpression(filter);
			Transazione result = this.getService().find(exp);
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
	public long count(SearchFilterTransazione filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			IExpression exp = this.toExpression(filter);
			long result = this.getService().count(exp).longValue();
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
	public Transazione get(java.lang.String id) throws TransazioniServiceException_Exception,TransazioniNotFoundException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("get", id);
			this.authorize(true);
			Transazione result = this.getService().get(id);
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
	public boolean exists(java.lang.String id) throws TransazioniServiceException_Exception,TransazioniMultipleResultException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			boolean result = this.getService().exists(id);
			this.logEndMethod("exists", result);
			return result;
			
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
	public List<java.lang.String> findAllIds(SearchFilterTransazione filter) throws TransazioniServiceException_Exception,TransazioniNotImplementedException_Exception,TransazioniNotAuthorizedException_Exception {
		try{
		
			checkInitDriverTransazioni(this.transazioniService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			IPaginatedExpression pagExp = this.toPaginatedExpression(filter);	
			List<java.lang.String> result = this.getService().findAllIds(pagExp);
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