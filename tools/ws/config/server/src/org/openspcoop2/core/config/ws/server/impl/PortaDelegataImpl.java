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
package org.openspcoop2.core.config.ws.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.IdPortaDelegata;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.ws.server.PortaDelegataCRUD;
import org.openspcoop2.core.config.ws.server.PortaDelegataSearch;
import org.openspcoop2.core.config.ws.server.beans.UseInfo;
import org.openspcoop2.core.config.ws.server.config.Constants;
import org.openspcoop2.core.config.ws.server.config.DriverConfigurazione;
import org.openspcoop2.core.config.ws.server.config.LoggerProperties;
import org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotAuthorizedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotFoundException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotImplementedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigServiceException_Exception;
import org.openspcoop2.core.config.ws.server.filter.SearchFilterPortaDelegata;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;

/**     
 * PortaDelegataImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class PortaDelegataImpl extends BaseImpl  implements PortaDelegataSearch, PortaDelegataCRUD {

	private DriverConfigurazione portaDelegataService = null; 

	public PortaDelegataImpl() {
		super();
		try {
			this.portaDelegataService = DriverConfigurazione.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione PortaDelegata Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del PortaDelegata Service: Service non implementato",  e);
		}

	}
	
	
	
	private IDPortaDelegata convertToIdPortaDelegata(IdPortaDelegata id) throws DriverConfigurazioneException{
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(id.getNome());
		return idPD;
	}

	private IdPortaDelegata convertToIdPortaDelegataWS(IDPortaDelegata id) throws DriverConfigurazioneException{
		IdPortaDelegata IdPortaDelegata = new IdPortaDelegata();
		IdPortaDelegata.setNome(id.getNome());
		return IdPortaDelegata;
	}
	
	private List<IdPortaDelegata> readPorteDelegateIds(SearchFilterPortaDelegata filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<IDPortaDelegata> listIds = this.readIds(filter, true);
		List<org.openspcoop2.core.config.IdPortaDelegata> listIdsWS = new ArrayList<IdPortaDelegata>();
		for (int i = 0; i < listIds.size(); i++) {
			listIdsWS.add(this.convertToIdPortaDelegataWS(listIds.get(i)));
		}
		return listIdsWS;
	}
	private List<PortaDelegata> readPorteDelegate(SearchFilterPortaDelegata filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<Long> listIds = this.readLongIds(filter, true);
		List<PortaDelegata> listPorte = new ArrayList<PortaDelegata>();
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.portaDelegataService.getDriver());
		for (int i = 0; i < listIds.size(); i++) {
			listPorte.add(driverDB.getPortaDelegata(listIds.get(0)));
		}
		return listPorte;
	}
	
	@SuppressWarnings("unchecked")
	private List<Long> readLongIds(SearchFilterPortaDelegata filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<Long>) this.toList(filter, true, paginated);
	}
	@SuppressWarnings("unchecked")
	private List<IDPortaDelegata> readIds(SearchFilterPortaDelegata filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<IDPortaDelegata>) this.toList(filter, false, paginated);
	}
	private List<?> toList(SearchFilterPortaDelegata filter, boolean idLong, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.portaDelegataService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<>();
		
		sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
		
		if(idLong){
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE, "id");
			returnTypes.add(Long.class);
		}
		
		if(!idLong || paginated){
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE, "nome_porta");
			returnTypes.add(String.class);
		}
		
		
		this.setFilter(sqlQueryObject, paramTypes, filter, paginated);
		
		
		List<List<Object>> listaRisultati = driverDB.readCustom(sqlQueryObject, returnTypes, paramTypes);
		
		if(idLong){
			List<Long> listIds = new ArrayList<Long>();
			for (List<Object> list : listaRisultati) {
				listIds.add((Long)list.get(0));
			}
			return listIds;
		}
		else{
			List<IDPortaDelegata> listIds = new ArrayList<IDPortaDelegata>();
			for (List<Object> list : listaRisultati) {
				String name = (String)list.get(0);
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(name);
				listIds.add(idPD);
			}
			return listIds;
		}
		
	}
	
	private long toCount(SearchFilterPortaDelegata filter) throws ServiceException, NotImplementedException, Exception{
		
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.portaDelegataService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<>();
		
		sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE,CostantiDB.PORTE_DELEGATE);
		
		sqlQueryObject.addSelectCountField(CostantiDB.PORTE_DELEGATE, "id", "cont", true);
		returnTypes.add(Long.class);
		
		this.setFilter(sqlQueryObject, paramTypes, filter, false);
		
		List<List<Object>> listaRisultati = driverDB.readCustom(sqlQueryObject, returnTypes, paramTypes);
		
		return (Long) listaRisultati.get(0).get(0);
		
	}
	
	private void setFilter(ISQLQueryObject sqlQueryObjectParam, List<JDBCObject> paramTypes,
			SearchFilterPortaDelegata filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
				
		if(filter.getTipoSoggettoProprietario()!= null || filter.getNomeSoggettoProprietario()!=null) {
			sqlQueryObjectParam.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectParam.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto="+CostantiDB.SOGGETTI+".id");
		}
		
		sqlQueryObjectParam.setANDLogicOperator(true);
		
		
		ISQLQueryObject sqlQueryObjectCondition = sqlQueryObjectParam.newSQLQueryObject();
		
		if(filter.getOrCondition().booleanValue()) {
			sqlQueryObjectCondition.setANDLogicOperator(false);
		} else {
			sqlQueryObjectCondition.setANDLogicOperator(true);
		}

		
		if(filter.getTipoSoggettoProprietario()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			paramTypes.add(new JDBCObject(filter.getTipoSoggettoProprietario(),String.class));
		}
		if(filter.getNomeSoggettoProprietario()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			paramTypes.add(new JDBCObject(filter.getNomeSoggettoProprietario(),String.class));
		}

		if(filter.getNome()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta=?");
			paramTypes.add(new JDBCObject(filter.getNome(),String.class));
		}
		
		if(filter.getDescrizione()!= null) {
			sqlQueryObjectCondition.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE+".descrizione=?",filter.getDescrizione(),true,true);
		}

		if(filter.getRicevutaAsincronaSimmetrica()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ricevuta_asincrona_sim=?");
			paramTypes.add(new JDBCObject(filter.getRicevutaAsincronaSimmetrica().getValue(),String.class));
		}
		if(filter.getRicevutaAsincronaAsimmetrica()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ricevuta_asincrona_asim=?");
			paramTypes.add(new JDBCObject(filter.getRicevutaAsincronaAsimmetrica().getValue(),String.class));
		}
		
		if(filter.getIntegrazione()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".integrazione=?");
			paramTypes.add(new JDBCObject(filter.getIntegrazione(),String.class));
		}
		
		if(filter.getAllegaBody()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".allega_body=?");
			paramTypes.add(new JDBCObject(filter.getAllegaBody(),String.class));
		}
		if(filter.getScartaBody()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".scarta_body=?");
			paramTypes.add(new JDBCObject(filter.getScartaBody(),String.class));
		}
		
		if(filter.getGestioneManifest()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".gestione_manifest=?");
			paramTypes.add(new JDBCObject(filter.getGestioneManifest().getValue(),String.class));
		}
		
		if(filter.getStateless()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".stateless=?");
			paramTypes.add(new JDBCObject(filter.getStateless().getValue(),String.class));
		}
	
		if(filter.getAutenticazione()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".autenticazione=?");
			paramTypes.add(new JDBCObject(filter.getAutenticazione(),String.class));
		}
		if(filter.getAutorizzazione()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".autorizzazione=?");
			paramTypes.add(new JDBCObject(filter.getAutorizzazione(),String.class));
		}
		if(filter.getAutorizzazioneContenuto()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".autorizzazione_contenuto=?");
			paramTypes.add(new JDBCObject(filter.getAutorizzazioneContenuto(),String.class));
		}
		
		if(filter.getLocalForward()!= null) {
			if(filter.getLocalForward().getStato()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".local_forward=?");
				paramTypes.add(new JDBCObject(filter.getLocalForward().getStato().getValue(),String.class));
			}
			if(filter.getLocalForward().getPortaApplicativa()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".local_forward_pa=?");
				paramTypes.add(new JDBCObject(filter.getLocalForward().getPortaApplicativa(),String.class));
			}
		}
		
		if(filter.getSoggettoErogatore()!= null) {
			if(filter.getSoggettoErogatore().getTipo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_soggetto_erogatore=?");
				paramTypes.add(new JDBCObject(filter.getSoggettoErogatore().getTipo(),String.class));
			}
			if(filter.getSoggettoErogatore().getNome()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_soggetto_erogatore=?");
				paramTypes.add(new JDBCObject(filter.getSoggettoErogatore().getNome(),String.class));
			}
		}
		if(filter.getServizio()!= null) {
			if(filter.getServizio().getTipo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_servizio=?");
				paramTypes.add(new JDBCObject(filter.getServizio().getTipo(),String.class));
			}
			if(filter.getServizio().getNome()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_servizio=?");
				paramTypes.add(new JDBCObject(filter.getServizio().getNome(),String.class));
			}
		}
		if(filter.getAzione()!= null) {
			if(filter.getAzione().getIdentificazione()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".mode_azione=?");
				paramTypes.add(new JDBCObject(filter.getAzione().getIdentificazione().getValue(),String.class));
			}
			if(filter.getAzione().getPattern()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".pattern_azione=?");
				paramTypes.add(new JDBCObject(filter.getAzione().getPattern(),String.class));
			}
			if(filter.getAzione().getNome()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".azione=?");
				paramTypes.add(new JDBCObject(filter.getAzione().getNome(),String.class));
			}
			if(filter.getAzione().getForceInterfaceBased()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".force_interface_based_azione=?");
				paramTypes.add(new JDBCObject(filter.getAzione().getForceInterfaceBased().getValue(),String.class));
			}
		}
		
		if(filter.getMtomProcessor()!= null) {
			if(filter.getMtomProcessor().getRequestFlow()!= null) {
				if(filter.getMtomProcessor().getRequestFlow().getMode()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".mtom_request_mode=?");
					paramTypes.add(new JDBCObject(filter.getMtomProcessor().getRequestFlow().getMode().getValue(),String.class));
				}
			}
			if(filter.getMtomProcessor().getResponseFlow()!= null) {
				if(filter.getMtomProcessor().getResponseFlow().getMode()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".mtom_response_mode=?");
					paramTypes.add(new JDBCObject(filter.getMtomProcessor().getResponseFlow().getMode().getValue(),String.class));
				}
			}
		}

		if(filter.getStatoMessageSecurity()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ws_security=?");
			paramTypes.add(new JDBCObject(filter.getStatoMessageSecurity(),String.class));
		}
		
		if(filter.getValidazioneContenutiApplicativi()!= null) {
			if(filter.getValidazioneContenutiApplicativi().getStato()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".validazione_contenuti_stato=?");
				paramTypes.add(new JDBCObject(filter.getValidazioneContenutiApplicativi().getStato().getValue(),String.class));
			}
			if(filter.getValidazioneContenutiApplicativi().getTipo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".validazione_contenuti_tipo=?");
				paramTypes.add(new JDBCObject(filter.getValidazioneContenutiApplicativi().getTipo().getValue(),String.class));
			}
		}
		
		if(filter.getOraRegistrazioneMin()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ora_registrazione>=?");
			paramTypes.add(new JDBCObject(filter.getOraRegistrazioneMin(),Date.class));
		}
		if(filter.getOraRegistrazioneMax()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ora_registrazione<=?");
			paramTypes.add(new JDBCObject(filter.getOraRegistrazioneMax(),Date.class));
		}

		
		
		if(((SQLQueryObjectCore)sqlQueryObjectCondition).sizeConditions()>0)
			sqlQueryObjectParam.addWhereCondition(true, sqlQueryObjectCondition.createSQLConditions());
		
		if(paginated){
			if(filter.getLimit()!=null) {
				sqlQueryObjectParam.setLimit(filter.getLimit());
			}
			if(filter.getOffset()!=null) {
				sqlQueryObjectParam.setOffset(filter.getOffset());
			}
			sqlQueryObjectParam.addOrderBy(CostantiDB.PORTE_DELEGATE+".id_soggetto");
			sqlQueryObjectParam.addOrderBy(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObjectParam.setSortType(true);
		}
		
	}
	
	
	
	


	@Override
	public List<PortaDelegata> findAll(SearchFilterPortaDelegata filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.portaDelegataService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			List<PortaDelegata> result = this.readPorteDelegate(filter, true);
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
	public PortaDelegata find(SearchFilterPortaDelegata filter) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.portaDelegataService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			List<PortaDelegata> resultList = this.readPorteDelegate(filter, false);
			if(resultList==null || resultList.size()<=0){
				throw new DriverConfigurazioneNotFound("NotFound");
			}
			if(resultList.size()>1){
				throw new MultipleResultException("Found "+resultList.size()+" porte delegate");
			}
			PortaDelegata result = resultList.get(0);
			this.logEndMethod("find", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("find", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, filter);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("find", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, filter);
		} catch (ServiceException e) {
			throw throwServiceException("find", e, Constants.CODE_SERVICE_EXCEPTION, filter);
		} catch (DriverConfigurazioneNotFound e) {
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
	public long count(SearchFilterPortaDelegata filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.portaDelegataService);
			this.logStartMethod("count", filter);
			this.authorize(true);
			long result = this.toCount(filter);
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
	public PortaDelegata get(org.openspcoop2.core.config.IdPortaDelegata id) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneGet(this.portaDelegataService);
			this.logStartMethod("get", id);
			this.authorize(true);
			PortaDelegata result = ((IDriverConfigurazioneGet)this.portaDelegataService.getDriver()).getPortaDelegata(this.convertToIdPortaDelegata(id));
			this.logEndMethod("get", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("get", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("get", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("get", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (DriverConfigurazioneNotFound e) {
			throw throwNotFoundException("get", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("get", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}
	
	@Override
	public boolean exists(org.openspcoop2.core.config.IdPortaDelegata id) throws ConfigServiceException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			boolean result = ((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).existsPortaDelegata(this.convertToIdPortaDelegata(id));
			this.logEndMethod("exists", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("exists", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("exists", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("exists", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("exists", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public List<org.openspcoop2.core.config.IdPortaDelegata> findAllIds(SearchFilterPortaDelegata filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.portaDelegataService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			List<org.openspcoop2.core.config.IdPortaDelegata> result = this.readPorteDelegateIds(filter, true);
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

	@Override
	public UseInfo inUse(org.openspcoop2.core.config.IdPortaDelegata id) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			this.logStartMethod("inUse", id);
			this.authorize(true);
			
//			UseInfo useInfo = new UseInfo();
//			InUse inUse = this.getService().inUse(id);
//			useInfo.setUsed(inUse.isInUse());
//			
//			for(@SuppressWarnings("rawtypes") org.openspcoop2.generic_project.beans.InUseCondition inUseCondition: inUse.getInUseConditions()) {
//				InUseCondition inUseCond = new InUseCondition();
//				inUseCond.setCause(inUseCondition.getCause());
//				inUseCond.setName(inUseCondition.getObjectName());
//				if(inUseCondition.getObject()!=null){
//					inUseCond.setType(Identified.toEnumConstant(inUseCondition.getObject().getSimpleName()));
//				}
//				@SuppressWarnings("unchecked")
//				List<Object> list = inUseCondition.getIds();
//				if(list!=null && list.size()>0){
//					for (Object object : list) {
//						IdEntity idEntity = null;
//						if("Soggetto".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdSoggetto wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdSoggetto();
//							wrapperId.setId((org.openspcoop2.core.config.IdSoggetto)object);
//							idEntity.setId(wrapperId);
//						}
//						if("PortaDelegata".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaDelegata wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaDelegata();
//							wrapperId.setId((org.openspcoop2.core.config.IdPortaDelegata)object);
//							idEntity.setId(wrapperId);
//						}
//						if("PortaApplicativa".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaApplicativa wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdPortaApplicativa();
//							wrapperId.setId((org.openspcoop2.core.config.IdPortaApplicativa)object);
//							idEntity.setId(wrapperId);
//						}
//						if("ServizioApplicativo".equals(inUseCondition.getObjectName())){
//							idEntity = new IdEntity();
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdServizioApplicativo wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdServizioApplicativo();
//							wrapperId.setId((org.openspcoop2.core.config.IdServizioApplicativo)object);
//							idEntity.setId(wrapperId);
//						}
//						if(idEntity==null){
//							throw new Exception("Object id unknown. ClassType["+object.getClass().getName()+"] Object["+inUseCondition.getObject()+"] ObjectName["+inUseCondition.getObjectName()+"]");
//						}
//						inUseCond.addId(idEntity);
//					}
//				}
//				useInfo.addInUseCondition(inUseCond);
//			}
//			
//			this.logEndMethod("inUse", useInfo);
//			return useInfo;
			
			throw new NotImplementedException("Not Implemented");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("inUse", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("inUse", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("inUse", e, Constants.CODE_SERVICE_EXCEPTION, id);
//		} catch (NotFoundException e) {
//			throw throwNotFoundException("inUse", e, Constants.CODE_NOT_FOUND_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("inUse", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
	}

	@Override
	public void create(PortaDelegata obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("create", obj);
			this.authorize(false);
			//obj.setSuperUser(ServerProperties.getInstance().getUser());
			((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).createPortaDelegata(obj);
			this.logEndMethod("create");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("create", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, null);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("create", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (ServiceException e) {
			throw throwServiceException("create", e, Constants.CODE_SERVICE_EXCEPTION, null);
		} catch (Exception e){
			throw throwServiceException("create", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, null);
		}
	}
	
	@Override
    public void update(org.openspcoop2.core.config.IdPortaDelegata oldId, PortaDelegata obj) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("update", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				throw new DriverConfigurazioneNotFound("Porta Delegata non esistente");
			}
			//obj.setSuperUser(ServerProperties.getInstance().getUser());
			IDPortaDelegata idPD = this.convertToIdPortaDelegata(oldId);
			obj.setOldIDPortaDelegataForUpdate(idPD);
			((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).updatePortaDelegata(obj);
			this.logEndMethod("update");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("update", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, oldId);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("update", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, oldId);
		} catch (ServiceException e) {
			throw throwServiceException("update", e, Constants.CODE_SERVICE_EXCEPTION, oldId);
		} catch (DriverConfigurazioneNotFound e) {
			throw throwNotFoundException("update", e, Constants.CODE_NOT_FOUND_EXCEPTION, oldId);
		} catch (Exception e){
			throw throwServiceException("update", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, oldId);
		}
    }
	
	@Override
	public void updateOrCreate(org.openspcoop2.core.config.IdPortaDelegata oldId, PortaDelegata obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("updateOrCreate", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				//obj.setSuperUser(ServerProperties.getInstance().getUser());
				((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).createPortaDelegata(obj);	
			}else{
				//obj.setSuperUser(ServerProperties.getInstance().getUser());
				IDPortaDelegata idPD = this.convertToIdPortaDelegata(oldId);
				obj.setOldIDPortaDelegataForUpdate(idPD);
				((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).updatePortaDelegata(obj);
			}
			this.logEndMethod("updateOrCreate");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("updateOrCreate", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, oldId);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("updateOrCreate", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, oldId);
		} catch (ServiceException e) {
			throw throwServiceException("updateOrCreate", e, Constants.CODE_SERVICE_EXCEPTION, oldId);
		} catch (Exception e){
			throw throwServiceException("updateOrCreate", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, oldId);
		}
    }
	
	@Override
	public void deleteById(org.openspcoop2.core.config.IdPortaDelegata id) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("deleteById", id);
			this.authorize(false);
			PortaDelegata pd = null;
			try{
				pd = this.get(id);
			}catch(ConfigNotFoundException_Exception notFound){}
			if(pd!=null)
				((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).deletePortaDelegata(pd);
			this.logEndMethod("deleteById");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("deleteById", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, id);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("deleteById", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, id);
		} catch (ServiceException e) {
			throw throwServiceException("deleteById", e, Constants.CODE_SERVICE_EXCEPTION, id);
		} catch (Exception e){
			throw throwServiceException("deleteById", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, id);
		}
    }
	
	
	@Override
	public long deleteAll() throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("deleteAll");
			this.authorize(false);
			List<IDPortaDelegata> list = null; 
			try{
				list = ((IDriverConfigurazioneGet)this.portaDelegataService.getDriver()).getAllIdPorteDelegate(new FiltroRicercaPorteDelegate());
			}catch(DriverConfigurazioneNotFound notFound){}
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDPortaDelegata idPorta : list) {
					try{
						((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).
							deletePortaDelegata(((IDriverConfigurazioneGet)this.portaDelegataService.getDriver()).getPortaDelegata(idPorta));
					}catch(DriverConfigurazioneNotFound notFound){}
				}
			}
			this.logEndMethod("deleteAll", result);
			return result;
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("deleteAll", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, null);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("deleteAll", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (ServiceException e) {
			throw throwServiceException("deleteAll", e, Constants.CODE_SERVICE_EXCEPTION, null);
		} catch (Exception e){
			throw throwServiceException("deleteAll", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, null);
		}
    }
	
	
	@Override
	public long deleteAllByFilter(SearchFilterPortaDelegata filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("deleteAllByFilter", filter);
			this.authorize(false);
			List<IDPortaDelegata> list = this.readIds(filter, true);
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDPortaDelegata idPorta : list) {
					try{
						((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).
							deletePortaDelegata(((IDriverConfigurazioneGet)this.portaDelegataService.getDriver()).getPortaDelegata(idPorta));
					}catch(DriverConfigurazioneNotFound notFound){}
				}
			}
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
	
	
	@Override
	public void delete(PortaDelegata obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.portaDelegataService);
			this.logStartMethod("delete", obj);
			this.authorize(false);
			((IDriverConfigurazioneCRUD)this.portaDelegataService.getDriver()).deletePortaDelegata(obj);
			this.logEndMethod("delete");
			
		} catch(NotAuthorizedException e){
			throw throwNotAuthorizedException("delete", e, Constants.CODE_NOT_AUTHORIZED_EXCEPTION, null);
		} catch (NotImplementedException e) {
			throw throwNotImplementedException("delete", e, Constants.CODE_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (ServiceException e) {
			throw throwServiceException("delete", e, Constants.CODE_EXPRESSION_NOT_IMPLEMENTED_EXCEPTION, null);
		} catch (Exception e){
			throw throwServiceException("delete", e, Constants.CODE_ERROR_GENERAL_EXCEPTION, null);
		}
    }



}