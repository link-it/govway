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
package org.openspcoop2.core.config.ws.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.IdServizioApplicativo;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.ws.server.ServizioApplicativoCRUD;
import org.openspcoop2.core.config.ws.server.ServizioApplicativoSearch;
import org.openspcoop2.core.config.ws.server.beans.UseInfo;
import org.openspcoop2.core.config.ws.server.config.Constants;
import org.openspcoop2.core.config.ws.server.config.DriverConfigurazione;
import org.openspcoop2.core.config.ws.server.config.LoggerProperties;
import org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotAuthorizedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotFoundException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigNotImplementedException_Exception;
import org.openspcoop2.core.config.ws.server.exception.ConfigServiceException_Exception;
import org.openspcoop2.core.config.ws.server.filter.SearchFilterServizioApplicativo;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
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
 * ServizioApplicativoImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class ServizioApplicativoImpl extends BaseImpl  implements ServizioApplicativoSearch, ServizioApplicativoCRUD {

	private DriverConfigurazione servizioApplicativoService = null; 
	
	public ServizioApplicativoImpl() {
		super();
		try {
			this.servizioApplicativoService = DriverConfigurazione.getInstance();
			LoggerProperties.getLoggerWS().info("Inizializzazione ServizioApplicativo Service effettuata con successo");
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del ServizioApplicativo Service: Service non implementato",  e);
		}
		
	}
	

	private IDServizioApplicativo convertToIdServizioApplicativo(IdServizioApplicativo id) throws DriverConfigurazioneException{
		IDSoggetto idSoggettoProprietario = null;
		if(id.getIdSoggetto()!=null){
			idSoggettoProprietario = new IDSoggetto(id.getIdSoggetto().getTipo(), id.getIdSoggetto().getNome());
		}
		return this.buildIdServizioApplicativo(id.getNome(), idSoggettoProprietario);
	}
	private IDServizioApplicativo buildIdServizioApplicativo(String nome, IDSoggetto idSoggetto) throws DriverConfigurazioneException{
		IDServizioApplicativo IdServizioApplicativo = new IDServizioApplicativo();
		IdServizioApplicativo.setNome(nome);
		IdServizioApplicativo.setIdSoggettoProprietario(idSoggetto);
		return IdServizioApplicativo;
	}

	private IdServizioApplicativo convertToIdServizioApplicativoWS(IDServizioApplicativo id) throws DriverConfigurazioneException{
		IdServizioApplicativo IdServizioApplicativo = new IdServizioApplicativo();
		IdServizioApplicativo.setNome(id.getNome());
		IdSoggetto soggettoProprietario = new IdSoggetto();
		soggettoProprietario.setTipo(id.getIdSoggettoProprietario().getTipo());
		soggettoProprietario.setNome(id.getIdSoggettoProprietario().getNome());
		IdServizioApplicativo.setIdSoggetto(soggettoProprietario);
		return IdServizioApplicativo;
	}
	
	private List<IdServizioApplicativo> readServiziApplicativiIds(SearchFilterServizioApplicativo filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<IDServizioApplicativo> listIds = this.readIds(filter, true);
		List<org.openspcoop2.core.config.IdServizioApplicativo> listIdsWS = new ArrayList<IdServizioApplicativo>();
		for (int i = 0; i < listIds.size(); i++) {
			listIdsWS.add(this.convertToIdServizioApplicativoWS(listIds.get(i)));
		}
		return listIdsWS;
	}
	private List<ServizioApplicativo> readServiziApplicativi(SearchFilterServizioApplicativo filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		List<Long> listIds = this.readLongIds(filter, true);
		List<ServizioApplicativo> listServiziApplicativi = new ArrayList<ServizioApplicativo>();
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.servizioApplicativoService.getDriver());
		for (int i = 0; i < listIds.size(); i++) {
			listServiziApplicativi.add(driverDB.getServizioApplicativo(listIds.get(0)));
		}
		return listServiziApplicativi;
	}
	
	@SuppressWarnings("unchecked")
	private List<Long> readLongIds(SearchFilterServizioApplicativo filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<Long>) this.toList(filter, true, paginated);
	}
	@SuppressWarnings("unchecked")
	private List<IDServizioApplicativo> readIds(SearchFilterServizioApplicativo filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		return (List<IDServizioApplicativo>) this.toList(filter, false, paginated);
	}
	private List<?> toList(SearchFilterServizioApplicativo filter, boolean idLong, boolean paginated) throws ServiceException, NotImplementedException, Exception{
		
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.servizioApplicativoService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<JDBCObject>();
		
		sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
		
		if(idLong){
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI, "id");
			returnTypes.add(Long.class);
		}
		
		if(!idLong || paginated){
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI, "id_soggetto");
			returnTypes.add(Long.class);
			
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI, "nome");
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
			List<IDServizioApplicativo> listIds = new ArrayList<IDServizioApplicativo>();
			for (List<Object> list : listaRisultati) {
				Long idProprietario = (Long)list.get(0);
				String name = (String)list.get(1);
				IDSoggetto idSoggettoProprietario = null;
				if(idProprietario!=null && idProprietario>0){
					idSoggettoProprietario = driverDB.getIdSoggetto(idProprietario);
				}
				listIds.add(this.buildIdServizioApplicativo(name, idSoggettoProprietario));
			}
			return listIds;
		}
		
	}
	
	private long toCount(SearchFilterServizioApplicativo filter) throws ServiceException, NotImplementedException, Exception{
		
		DriverConfigurazioneDB driverDB = ((DriverConfigurazioneDB)this.servizioApplicativoService.getDriver());
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driverDB.getTipoDB());
		List<Class<?>> returnTypes = new ArrayList<Class<?>>();
		List<JDBCObject> paramTypes = new ArrayList<JDBCObject>();
		
		sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI,CostantiDB.SERVIZI_APPLICATIVI);
		
		sqlQueryObject.addSelectCountField(CostantiDB.SERVIZI_APPLICATIVI, "id", "cont", true);
		returnTypes.add(Long.class);
		
		this.setFilter(sqlQueryObject, paramTypes, filter, false);
		
		List<List<Object>> listaRisultati = driverDB.readCustom(sqlQueryObject, returnTypes, paramTypes);
		
		return (Long) listaRisultati.get(0).get(0);
		
	}
	
	private void setFilter(ISQLQueryObject sqlQueryObjectParam, List<JDBCObject> paramTypes,
			SearchFilterServizioApplicativo filter, boolean paginated) throws ServiceException, NotImplementedException, Exception{
				
		if(filter.getTipoSoggettoProprietario()!= null || filter.getNomeSoggettoProprietario()!=null) {
			sqlQueryObjectParam.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectParam.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
		}
		String aliasConnettoriInvocazioneServizio = "connettoriInvServizio";
		if(filter.getInvocazioneServizio()!= null && filter.getInvocazioneServizio().getConnettore()!= null) {
			sqlQueryObjectParam.addFromTable(CostantiDB.CONNETTORI,aliasConnettoriInvocazioneServizio);
			sqlQueryObjectParam.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_inv="+aliasConnettoriInvocazioneServizio+".id");
		}
		String aliasConnettoriRispostaAsincrona = "connettoriRispAsincrona";
		if(filter.getRispostaAsincrona()!= null && filter.getRispostaAsincrona().getConnettore()!= null) {
			sqlQueryObjectParam.addFromTable(CostantiDB.CONNETTORI,aliasConnettoriRispostaAsincrona);
			sqlQueryObjectParam.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_risp="+aliasConnettoriRispostaAsincrona+".id");
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
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome=?");
			paramTypes.add(new JDBCObject(filter.getNome(),String.class));
		}
		
		if(filter.getDescrizione()!= null) {
			sqlQueryObjectCondition.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".descrizione=?",filter.getDescrizione(),true,true);
		}

		if(filter.getTipologiaFruizione()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipologia_fruizione=?");
			paramTypes.add(new JDBCObject(filter.getTipologiaFruizione(),String.class));
		}
		if(filter.getTipologiaErogazione()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipologia_erogazione=?");
			paramTypes.add(new JDBCObject(filter.getTipologiaErogazione(),String.class));
		}
		
		
		if(filter.getInvocazionePorta()!= null) {
			if(filter.getInvocazionePorta().getGestioneErrore()!= null) {
				if(filter.getInvocazionePorta().getGestioneErrore().getFault()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".fault=?");
					paramTypes.add(new JDBCObject(filter.getInvocazionePorta().getGestioneErrore().getFault().getValue(),String.class));
				}
				if(filter.getInvocazionePorta().getGestioneErrore().getFaultActor()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".fault_actor=?");
					paramTypes.add(new JDBCObject(filter.getInvocazionePorta().getGestioneErrore().getFaultActor(),String.class));
				}
				if(filter.getInvocazionePorta().getGestioneErrore().getGenericFaultCode()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".generic_fault_code=?");
					paramTypes.add(new JDBCObject(filter.getInvocazionePorta().getGestioneErrore().getGenericFaultCode().getValue(),String.class));
				}
				if(filter.getInvocazionePorta().getGestioneErrore().getPrefixFaultCode()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".prefix_fault_code=?");
					paramTypes.add(new JDBCObject(filter.getInvocazionePorta().getGestioneErrore().getPrefixFaultCode(),String.class));
				}
			}
			if(filter.getInvocazionePorta().getInvioPerRiferimento()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".invio_x_rif=?");
				paramTypes.add(new JDBCObject(filter.getInvocazionePorta().getInvioPerRiferimento().getValue(),String.class));
			}
			if(filter.getInvocazionePorta().getSbustamentoInformazioniProtocollo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".sbustamento_protocol_info=?");
				paramTypes.add(new JDBCObject(StatoFunzionalita.ABILITATO.equals(filter.getInvocazionePorta().getSbustamentoInformazioniProtocollo())?1:0,Integer.class));
			}
		}
		

		if(filter.getInvocazioneServizio()!= null) {
			if(filter.getInvocazioneServizio().getCredenziali()!= null) {
				if(filter.getInvocazioneServizio().getCredenziali().getTipo()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauthinv=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getCredenziali().getTipo().getValue(),String.class));
				}
				if(filter.getInvocazioneServizio().getCredenziali().getUser()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".utenteinv=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getCredenziali().getUser(),String.class));
				}
				if(filter.getInvocazioneServizio().getCredenziali().getPassword()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".passwordinv=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getCredenziali().getPassword(),String.class));
				}
				if(filter.getInvocazioneServizio().getCredenziali().getSubject()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".subjectinv=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getCredenziali().getSubject(),String.class));
				}
			}
			if(filter.getInvocazioneServizio().getConnettore()!= null) {
				if(filter.getInvocazioneServizio().getConnettore().getCustom()!= null) {
					sqlQueryObjectCondition.addWhereCondition(aliasConnettoriInvocazioneServizio+".custom=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getConnettore().getCustom()?1:0,Integer.class));
				}
				if(filter.getInvocazioneServizio().getConnettore().getTipo()!= null) {
					sqlQueryObjectCondition.addWhereCondition(aliasConnettoriInvocazioneServizio+".endpointtype=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getConnettore().getTipo(),String.class));
				}
				if(filter.getInvocazioneServizio().getConnettore().getNome()!= null) {
					sqlQueryObjectCondition.addWhereCondition(aliasConnettoriInvocazioneServizio+".nome_connettore=?");
					paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getConnettore().getNome(),String.class));
				}
			}
			if(filter.getInvocazioneServizio().getSbustamentoSoap()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".sbustamentoinv=?");
				paramTypes.add(new JDBCObject(StatoFunzionalita.ABILITATO.equals(filter.getInvocazioneServizio().getSbustamentoSoap())?1:0,Integer.class));
			}
			if(filter.getInvocazioneServizio().getSbustamentoInformazioniProtocollo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".sbustamento_protocol_info_inv=?");
				paramTypes.add(new JDBCObject(StatoFunzionalita.ABILITATO.equals(filter.getInvocazioneServizio().getSbustamentoInformazioniProtocollo())?1:0,Integer.class));
			}
			if(filter.getInvocazioneServizio().getGetMessage()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".getmsginv=?");
				paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getGetMessage().getValue(),String.class));
			}
			if(filter.getInvocazioneServizio().getAutenticazione()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauthinv=?");
				paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getAutenticazione().getValue(),String.class));
			}
			if(filter.getInvocazioneServizio().getInvioPerRiferimento()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".invio_x_rif_inv=?");
				paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getInvioPerRiferimento().getValue(),String.class));
			}
			if(filter.getInvocazioneServizio().getRispostaPerRiferimento()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".risposta_x_rif_inv=?");
				paramTypes.add(new JDBCObject(filter.getInvocazioneServizio().getRispostaPerRiferimento().getValue(),String.class));
			}
		}
		
		if(filter.getRispostaAsincrona()!= null) {
			if(filter.getRispostaAsincrona().getCredenziali()!= null) {
				if(filter.getRispostaAsincrona().getCredenziali().getTipo()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauthrisp=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getCredenziali().getTipo().getValue(),String.class));
				}
				if(filter.getRispostaAsincrona().getCredenziali().getUser()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".utenterisp=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getCredenziali().getUser(),String.class));
				}
				if(filter.getRispostaAsincrona().getCredenziali().getPassword()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".passwordrisp=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getCredenziali().getPassword(),String.class));
				}
				if(filter.getRispostaAsincrona().getCredenziali().getSubject()!= null) {
					sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".subjectrisp=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getCredenziali().getSubject(),String.class));
				}
			}
			if(filter.getRispostaAsincrona().getConnettore()!= null) {
				if(filter.getRispostaAsincrona().getConnettore().getCustom()!= null) {
					sqlQueryObjectCondition.addWhereCondition(aliasConnettoriRispostaAsincrona+".custom=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getConnettore().getCustom()?1:0,Integer.class));
				}
				if(filter.getRispostaAsincrona().getConnettore().getTipo()!= null) {
					sqlQueryObjectCondition.addWhereCondition(aliasConnettoriRispostaAsincrona+".endpointtype=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getConnettore().getTipo(),String.class));
				}
				if(filter.getRispostaAsincrona().getConnettore().getNome()!= null) {
					sqlQueryObjectCondition.addWhereCondition(aliasConnettoriRispostaAsincrona+".nome_connettore=?");
					paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getConnettore().getNome(),String.class));
				}
			}
			if(filter.getRispostaAsincrona().getSbustamentoSoap()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".sbustamentorisp=?");
				paramTypes.add(new JDBCObject(StatoFunzionalita.ABILITATO.equals(filter.getRispostaAsincrona().getSbustamentoSoap())?1:0,Integer.class));
			}
			if(filter.getRispostaAsincrona().getSbustamentoInformazioniProtocollo()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".sbustamento_protocol_info_risp=?");
				paramTypes.add(new JDBCObject(StatoFunzionalita.ABILITATO.equals(filter.getRispostaAsincrona().getSbustamentoInformazioniProtocollo())?1:0,Integer.class));
			}
			if(filter.getRispostaAsincrona().getGetMessage()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".getmsgrisp=?");
				paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getGetMessage().getValue(),String.class));
			}
			if(filter.getRispostaAsincrona().getAutenticazione()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauthrisp=?");
				paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getAutenticazione().getValue(),String.class));
			}
			if(filter.getRispostaAsincrona().getInvioPerRiferimento()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".invio_x_rif_risp=?");
				paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getInvioPerRiferimento().getValue(),String.class));
			}
			if(filter.getRispostaAsincrona().getRispostaPerRiferimento()!= null) {
				sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".risposta_x_rif_risp=?");
				paramTypes.add(new JDBCObject(filter.getRispostaAsincrona().getRispostaPerRiferimento().getValue(),String.class));
			}
		}
		
		if(filter.getOraRegistrazioneMin()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".ora_registrazione>=?");
			paramTypes.add(new JDBCObject(filter.getOraRegistrazioneMin(),Date.class));
		}
		if(filter.getOraRegistrazioneMax()!= null) {
			sqlQueryObjectCondition.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".ora_registrazione<=?");
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
			sqlQueryObjectParam.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto");
			sqlQueryObjectParam.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObjectParam.setSortType(true);
		}
		
	}
	
	
	
	

	


	@Override
	public List<ServizioApplicativo> findAll(SearchFilterServizioApplicativo filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.servizioApplicativoService);
			this.logStartMethod("findAll", filter);
			this.authorize(true);
			List<ServizioApplicativo> result = this.readServiziApplicativi(filter, true);
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
	public ServizioApplicativo find(SearchFilterServizioApplicativo filter) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.servizioApplicativoService);
			this.logStartMethod("find", filter);
			this.authorize(true);
			List<ServizioApplicativo> resultList = this.readServiziApplicativi(filter, false);
			if(resultList==null || resultList.size()<=0){
				throw new DriverConfigurazioneNotFound("NotFound");
			}
			if(resultList.size()>1){
				throw new MultipleResultException("Found "+resultList.size()+" servizi applicativi");
			}
			ServizioApplicativo result = resultList.get(0);
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
	public long count(SearchFilterServizioApplicativo filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.servizioApplicativoService);
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
	public ServizioApplicativo get(org.openspcoop2.core.config.IdServizioApplicativo id) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneGet(this.servizioApplicativoService);
			this.logStartMethod("get", id);
			this.authorize(true);
			ServizioApplicativo result = ((IDriverConfigurazioneGet)this.servizioApplicativoService.getDriver()).getServizioApplicativo(this.convertToIdServizioApplicativo(id));
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
	public boolean exists(org.openspcoop2.core.config.IdServizioApplicativo id) throws ConfigServiceException_Exception,ConfigMultipleResultException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("exists", id);
			this.authorize(true);
			boolean result = ((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).existsServizioApplicativo(this.convertToIdServizioApplicativo(id));
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
	public List<org.openspcoop2.core.config.IdServizioApplicativo> findAllIds(SearchFilterServizioApplicativo filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneDB(this.servizioApplicativoService);
			this.logStartMethod("findAllIds", filter);
			this.authorize(true);
			List<org.openspcoop2.core.config.IdServizioApplicativo> result = this.readServiziApplicativiIds(filter, true);
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
	public UseInfo inUse(org.openspcoop2.core.config.IdServizioApplicativo id) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
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
//							org.openspcoop2.core.config.ws.server.beans.WrapperIdServizioApplicativo wrapperId = new 
//								org.openspcoop2.core.config.ws.server.beans.WrapperIdServizioApplicativo();
//							wrapperId.setId((org.openspcoop2.core.config.IdServizioApplicativo)object);
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
	public void create(ServizioApplicativo obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("create", obj);
			this.authorize(false);
			//obj.setSuperUser(ServerProperties.getInstance().getUser());
			((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).createServizioApplicativo(obj);
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
    public void update(org.openspcoop2.core.config.IdServizioApplicativo oldId, ServizioApplicativo obj) throws ConfigServiceException_Exception,ConfigNotFoundException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("update", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				throw new DriverConfigurazioneNotFound("ServizioApplicativo non esistente");
			}
			//obj.setSuperUser(ServerProperties.getInstance().getUser());
			IDServizioApplicativo idSA = this.convertToIdServizioApplicativo(oldId);
			obj.setOldNomeForUpdate(idSA.getNome());
			if(idSA.getIdSoggettoProprietario()!=null){
				obj.setOldTipoSoggettoProprietarioForUpdate(idSA.getIdSoggettoProprietario().getTipo());
				obj.setOldNomeSoggettoProprietarioForUpdate(idSA.getIdSoggettoProprietario().getNome());
			}
			((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).updateServizioApplicativo(obj);
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
	public void updateOrCreate(org.openspcoop2.core.config.IdServizioApplicativo oldId, ServizioApplicativo obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("updateOrCreate", oldId, obj);
			this.authorize(false);
			if(this.exists(oldId)==false){
				//obj.setSuperUser(ServerProperties.getInstance().getUser());
				((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).createServizioApplicativo(obj);	
			}else{
				//obj.setSuperUser(ServerProperties.getInstance().getUser());
				IDServizioApplicativo idSA = this.convertToIdServizioApplicativo(oldId);
				obj.setOldNomeForUpdate(idSA.getNome());
				if(idSA.getIdSoggettoProprietario()!=null){
					obj.setOldTipoSoggettoProprietarioForUpdate(idSA.getIdSoggettoProprietario().getTipo());
					obj.setOldNomeSoggettoProprietarioForUpdate(idSA.getIdSoggettoProprietario().getNome());
				}
				((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).updateServizioApplicativo(obj);
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
	public void deleteById(org.openspcoop2.core.config.IdServizioApplicativo id) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("deleteById", id);
			this.authorize(false);
			ServizioApplicativo sa = null;
			try{
				sa = this.get(id);
			}catch(ConfigNotFoundException_Exception notFound){}
			if(sa!=null)
				((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).deleteServizioApplicativo(sa);
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
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("deleteAll");
			this.authorize(false);
			List<IDServizioApplicativo> list = null; 
			try{
				list = ((IDriverConfigurazioneGet)this.servizioApplicativoService.getDriver()).getAllIdServiziApplicativi(new FiltroRicercaServiziApplicativi());
			}catch(DriverConfigurazioneNotFound notFound){}
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDServizioApplicativo idSA : list) {
					try{
						((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).
							deleteServizioApplicativo(((IDriverConfigurazioneGet)this.servizioApplicativoService.getDriver()).getServizioApplicativo(idSA));
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
	public long deleteAllByFilter(SearchFilterServizioApplicativo filter) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("deleteAllByFilter", filter);
			this.authorize(false);
			List<IDServizioApplicativo> list = this.readIds(filter, true);
			long result = 0;
			if(list!=null && list.size()>0){
				result = list.size();
				for (IDServizioApplicativo idSA : list) {
					try{
						((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).
							deleteServizioApplicativo(((IDriverConfigurazioneGet)this.servizioApplicativoService.getDriver()).getServizioApplicativo(idSA));
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
	public void delete(ServizioApplicativo obj) throws ConfigServiceException_Exception,ConfigNotImplementedException_Exception,ConfigNotAuthorizedException_Exception {
		try{
		
			checkInitDriverConfigurazioneCRUD(this.servizioApplicativoService);
			this.logStartMethod("delete", obj);
			this.authorize(false);
			((IDriverConfigurazioneCRUD)this.servizioApplicativoService.getDriver()).deleteServizioApplicativo(obj);
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