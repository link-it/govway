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


package org.openspcoop2.web.monitor.allarmi.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IDBAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.IServiceManager;
import org.openspcoop2.core.allarmi.dao.jdbc.JDBCAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.ProjectInfo;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeHistoryBean;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginServiceSearch;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.web.monitor.allarmi.bean.AllarmiSearchForm;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.dynamic.DynamicComponentUtils;
import org.openspcoop2.web.monitor.core.dynamic.components.BaseComponent;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.slf4j.Logger;

/**     
 * AllarmiService
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiService implements IAllarmiService {

	private static Logger log = LoggerManager.getPddMonitorSqlLogger();

	private int LIMIT_SEARCH = 10000;
	
	private IServiceManager pluginsServiceManager;
	private IAllarmeService allarmeDAO;
	private IAllarmeServiceSearch allarmeSearchDAO;
	private IAllarmeHistoryServiceSearch allarmeHistorySearchDAO;
	private IAllarmeHistoryService allarmeHistoryDAO;
	private org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseServiceManager;
	private IPluginServiceSearch pluginsServiceSearchDAO;

	private AllarmiSearchForm searchForm;

	public AllarmiService() {
		try {
			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(log);

			// init Service Manager allarmi
			this.pluginsServiceManager = (IServiceManager) DAOFactory
					.getInstance(AllarmiService.log).getServiceManager(ProjectInfo.getInstance(),AllarmiService.log);
			this.allarmeDAO = this.pluginsServiceManager.getAllarmeService();
			this.allarmeSearchDAO = this.pluginsServiceManager.getAllarmeServiceSearch();
			this.allarmeHistorySearchDAO = 
					this.pluginsServiceManager.getAllarmeHistoryServiceSearch();
			this.allarmeHistoryDAO = 
					this.pluginsServiceManager.getAllarmeHistoryService();

			// init Service Manager plugins
			this.pluginsBaseServiceManager = (org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) DAOFactory
					.getInstance(AllarmiService.log).getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),AllarmiService.log);
			this.pluginsServiceSearchDAO = this.pluginsBaseServiceManager.getPluginServiceSearch();

			this.LIMIT_SEARCH = pddMonitorProperties.getSearchFormLimit();
			
		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public AllarmiSearchForm getSearch() {
		return this.searchForm;
	}
	
	@Override
	public void setSearch(AllarmiSearchForm search) {
		this.searchForm = search;
	}

	@Override
	public Long getCountAllarmiByStato(String stato, Integer acknowledged) {
		Long retValue = 0L;
		try {

			AllarmiSearchForm search = (AllarmiSearchForm) this.searchForm.clone();

			search.setStatoSelezionato(stato);

			IExpression e = createQuery(true, search);
			
			if(acknowledged!=null){
				e.equals(Allarme.model().ACKNOWLEDGED,acknowledged);
			}

			NonNegativeNumber nnn = this.allarmeSearchDAO.count(e);

			//			IPaginatedExpression pagExpr = this.allarmeSearchDAO
			//					.toPaginatedExpression(e);
			//
			//			List<ConfigurazioneAllarme> lst = this.allarmeSearchDAO
			//					.findAll(pagExpr);

			if (nnn != null) {
				retValue = nnn.longValue();//new Long(lst.size());
			}
		} catch (ServiceException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (CloneNotSupportedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
		}

		// Long res =
		// (Long)this.em.createQuery("select count(a) from Allarme a where a.stato=:stato").setParameter("stato",
		// stato).getSingleResult();
		return retValue;
		// res!=null ? res : 0L;
	}

	@Override
	public List<ConfigurazioneAllarmeBean> findAll(int start, int limit) {
		// this.searchForm.getConfigurazione() puo essere null, in caso in cui
		// sia nella visualizzazione
		// dello stato degli allarmi dell'amministratore, in futuro si puo'
		// prevedere di
		// inserire come filtro per l'amministratore il servizio di
		// configurazione
		if (this.searchForm == null)
			return new ArrayList<ConfigurazioneAllarmeBean>();

		try {
			IExpression e = createQuery(false,this.searchForm);
			IPaginatedExpression pagExpr = this.allarmeSearchDAO
					.toPaginatedExpression(e);

			List<Allarme> findAll = this.allarmeSearchDAO.findAll(pagExpr.offset(start)
					.limit(limit));

			if(findAll != null && findAll.size() > 0){
				List<ConfigurazioneAllarmeBean> toRet = new ArrayList<ConfigurazioneAllarmeBean>();

				for (Allarme al : findAll) {
					IdPlugin idPlugin = new IdPlugin();
					idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
					idPlugin.setTipo(al.getTipo());
					toRet.add(new ConfigurazioneAllarmeBean(al, this.pluginsServiceSearchDAO.get(idPlugin)));
				}

				return toRet;
			}
		} catch (ServiceException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (NotImplementedException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (ExpressionNotImplementedException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (ExpressionException e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		} catch (Exception e1) {
			AllarmiService.log.error(
					"Errore durante la ricerca degli Allarmi", e1);
		}

		return new ArrayList<ConfigurazioneAllarmeBean>();
	}

	@Override
	public int totalCount() {
		if (this.searchForm == null)
			return 0;

		NonNegativeNumber nnn = null;
		try {
			IExpression e = createQuery(true,this.searchForm);
			nnn = this.allarmeSearchDAO.count(e);
		} catch (ServiceException e1) {
			AllarmiService.log.error("Errore durante il calcolo del numero dei record", e1);
		} catch (NotImplementedException e1) {
			AllarmiService.log.error("Errore durante il calcolo del numero dei record", e1);
		} catch (Exception e1) {
			AllarmiService.log.error("Errore durante il calcolo del numero dei record", e1);
		}

		// Long res = (Long) this.createQuery(true).getSingleResult();
		return nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
	}

	@Override
	public void delete(ConfigurazioneAllarmeBean obj) {

		try {
			
			//1. elimino l'allarme
			this.allarmeDAO.delete(obj);

		} catch (ServiceException e) {
			AllarmiService.log.error( "Errore durante la rimozione dell'Allarme con id:" + obj.getId(), e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error( "Errore durante la rimozione dell'Allarme con id:" + obj.getId(), e);
		} 
	}

	@Override
	public void deleteById(Long key) {
		IdAllarme idConfigurazioneAllarme = new IdAllarme();
		idConfigurazioneAllarme.setId(key);

		try {
			this.allarmeDAO.deleteById(idConfigurazioneAllarme);
		} catch (ServiceException e) {
			AllarmiService.log.error("Errore durante la rimozione dell'Allarme con id:" + key,
					e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error("Errore durante la rimozione dell'Allarme con id:" + key,
					e);
		}
	}

	@Override
	public List<ConfigurazioneAllarmeBean> findAll() {
		if (this.searchForm == null)
			return new ArrayList<ConfigurazioneAllarmeBean>();

		try {
			IExpression e = createQuery(false,this.searchForm);

			IPaginatedExpression pagExpr = this.allarmeSearchDAO
					.toPaginatedExpression(e);
			List<Allarme> findAll =  this.allarmeSearchDAO.findAll(pagExpr);

			if(findAll != null && findAll.size() > 0){
				List<ConfigurazioneAllarmeBean> toRet = new ArrayList<ConfigurazioneAllarmeBean>();

				for (Allarme al : findAll) {
					IdPlugin idPlugin = new IdPlugin();
					idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
					idPlugin.setTipo(al.getTipo());
					toRet.add(new ConfigurazioneAllarmeBean(al, this.pluginsServiceSearchDAO.get(idPlugin)));
				}

				return toRet;
			}
		} catch (ServiceException e1) {
			AllarmiService.log.error("Errore durante la ricerca degli Allarmi", e1);
		} catch (NotImplementedException e1) {
			AllarmiService.log.error("Errore durante la ricerca degli Allarmi", e1);
		} catch (Exception e1) {
			AllarmiService.log.error("Errore durante la ricerca degli Allarmi", e1);
		}

		return new ArrayList<ConfigurazioneAllarmeBean>();
	}

	@Override
	public ConfigurazioneAllarmeBean findById(Long key) {
		IdAllarme idAllarme = new IdAllarme();
		idAllarme.setId(key);

		IDBAllarmeServiceSearch idSearch = (IDBAllarmeServiceSearch) this.allarmeSearchDAO;

		try {
			Allarme configurazioneAllarme = idSearch.get(idAllarme);

			if(configurazioneAllarme != null) {
				IdPlugin idPlugin = new IdPlugin();
				idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
				idPlugin.setTipo(configurazioneAllarme.getTipo());
				return new ConfigurazioneAllarmeBean(configurazioneAllarme, this.pluginsServiceSearchDAO.get(idPlugin));
			}
		} catch (ServiceException e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (NotFoundException e) {
			AllarmiService.log.debug("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (MultipleResultException e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error("Errore durante la ricerca dell'Allarme con id:" + key, e);
		}

		return null;
	}

	@Override
	public void store(ConfigurazioneAllarmeBean allarme) throws Exception {

		IdAllarme idAll = new IdAllarme();
		idAll.setNome(allarme.getNome());
		
		if (this.allarmeDAO.exists(idAll)) {
			this.allarmeDAO.update(idAll, allarme);
		} else {
			// imposto lo stato di default per l'allarme:
			allarme.setStato(AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
			allarme.setStatoPrecedente(AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
			allarme.setLasttimestampCreate(new Date());
			allarme.setAcknowledged(Integer.valueOf(0));
			this.allarmeDAO.create(allarme);
		}
	}

	private IExpression createQuery(boolean isCount,AllarmiSearchForm formRicerca) throws Exception{

		IExpression expr = null;
		try {
			expr = this.allarmeSearchDAO.newExpression();

			boolean addAnd = false;

			if (formRicerca.getNomeAzione() != null && StringUtils.isNotEmpty(formRicerca.getNomeAzione()) && !formRicerca.getNomeAzione().equals("*")) {
				expr.equals(Allarme.model().FILTRO.AZIONE,	formRicerca.getNomeAzione());
				addAnd = true;
			}

			if (StringUtils.isNotEmpty(formRicerca.getNomeAllarme())) {
				if (addAnd) {
					expr.and();
				}

				expr.ilike(Allarme.model().NOME,
						formRicerca.getNomeAllarme(), LikeMode.ANYWHERE);
				addAnd = true;
			}

			if (formRicerca.getNomeServizio() != null
					&& StringUtils.isNotEmpty(formRicerca.getNomeServizio()) && !formRicerca.getNomeServizio().equals("*")) {
				if (addAnd) {
					expr.and();
				}

				IDServizio idServizio = ParseUtility.parseServizioSoggetto(formRicerca.getNomeServizio());
				
				expr.and().
					equals(Allarme.model().FILTRO.TIPO_EROGATORE,	idServizio.getSoggettoErogatore().getTipo()).
					equals(Allarme.model().FILTRO.NOME_EROGATORE,	idServizio.getSoggettoErogatore().getNome()).
					equals(Allarme.model().FILTRO.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(Allarme.model().FILTRO.NOME_SERVIZIO,	idServizio.getNome()).
					equals(Allarme.model().FILTRO.VERSIONE_SERVIZIO,	idServizio.getVersione());

				addAnd = true;
			}

			// se e' stato selezionato uno stato != all allora imposto il filtro
			if (StringUtils.isNotEmpty(formRicerca.getStatoSelezionato())
					&& !StringUtils.equals(
							formRicerca.getStatoSelezionato(), "All")) {
				if (addAnd) {
					expr.and();
				}

				if (StringUtils.equals(formRicerca.getStatoSelezionato(),
						"Disabilitato")) {
					expr.equals(Allarme.model().ENABLED, 0);
				} else {
					
					expr.equals(Allarme.model().ENABLED, 1).and();

					if (StringUtils.equals(formRicerca.getStatoSelezionato(),
							"Non Disabilitato")) {
						expr.in(Allarme.model().STATO, 0,1,2);
					} else {
					
						// voglio quelli abilitati con un determinato stato
						String stato = formRicerca.getStatoSelezionato();
						if ("Ok".equals(stato)){
							expr.equals(Allarme.model().STATO, 0);
						}
						
						if ("Warn".equals(stato)){
							expr.equals(Allarme.model().STATO, 1);
						}
						if ("Warn (Acknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 1);
							expr.equals(Allarme.model().ACKNOWLEDGED, 1);
						}
						if ("Warn (Unacknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 1);
							expr.equals(Allarme.model().ACKNOWLEDGED, 0);
						}
						
						if ("Error".equals(stato)){
							expr.equals(Allarme.model().STATO, 2);
						}
						if ("Error (Acknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 2);
							expr.equals(Allarme.model().ACKNOWLEDGED, 1);
						}
						if ("Error (Unacknowledged)".equals(stato)){
							expr.equals(Allarme.model().STATO, 2);
							expr.equals(Allarme.model().ACKNOWLEDGED, 0);
						}
					
					}
					
				}

				addAnd = true;
			}


			// Se la lista degli identificativi e' popolata vuol dire che non sono admin, in questo caso devo impostare il filtro su soggetti e IdPorta.
			
			// permessi utente operatore
			if(formRicerca.getPermessiUtenteOperatore()!=null){
				IExpression permessi = formRicerca.getPermessiUtenteOperatore().
						toExpressionAllarmi(this.allarmeSearchDAO, 
								Allarme.model().FILTRO.TIPO_FRUITORE, 
								Allarme.model().FILTRO.NOME_FRUITORE, 
								Allarme.model().FILTRO.TIPO_EROGATORE, 
								Allarme.model().FILTRO.NOME_EROGATORE, 
								Allarme.model().FILTRO.TIPO_SERVIZIO, 
								Allarme.model().FILTRO.NOME_SERVIZIO,
								Allarme.model().FILTRO.VERSIONE_SERVIZIO);
				expr.and(permessi);
				addAnd = true;
			}
			

			// Soggetto selezionato dall'elenco soggetti disponibili
			boolean tipoNomeSoggettoLocale = StringUtils
					.isNotBlank(formRicerca.getSoggettoLocale());
			List<Soggetto> soggettiGestione = formRicerca.getSoggettiGestione();
			if(soggettiGestione != null && soggettiGestione.size() ==1){
				tipoNomeSoggettoLocale=false;
			}

			if (tipoNomeSoggettoLocale) {
				IExpression erogatore = this.allarmeSearchDAO.newExpression();

				erogatore
				.equals(Allarme.model().FILTRO.NOME_EROGATORE,	formRicerca.getSoggettoLocale())
				.and()
				.equals(Allarme.model().FILTRO.TIPO_EROGATORE, 	formRicerca.getTipoSoggettoLocale());

				// tipo e nome fruitore
				IExpression fruitore = this.allarmeSearchDAO.newExpression();

				fruitore
				.equals(Allarme.model().FILTRO.NOME_FRUITORE,	formRicerca.getSoggettoLocale())
				.and()
				.equals(Allarme.model().FILTRO.TIPO_FRUITORE, 	formRicerca.getTipoSoggettoLocale());

				IExpression soggetti = this.allarmeSearchDAO.newExpression();

				soggetti.or(erogatore, fruitore);

				expr.and(soggetti);
			}

			// se non e' una count inserisco l'ordinamento
			if (!isCount) {
				expr.sortOrder(SortOrder.ASC).addOrder(Allarme.model().NOME);

			}

		} catch (ServiceException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (NotImplementedException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (ExpressionException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		} catch (CoreException e) {
			AllarmiService.log.error("Errore durante la creazione della query di ricerca", e);
			throw e;
		}

		return expr;
	}

	@Override
	public List<Parameter<?>> instanceParameters(Allarme configurazioneAllarme, Context context) throws Exception {

		try {
			List<Parameter<?>> res = null;

			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.pluginsServiceSearchDAO.get(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			List<Parameter<?>> sdkParameters = bl.getParameters(context);
			
			if(sdkParameters!=null && sdkParameters.size()>0){
				
				res = new ArrayList<Parameter<?>>();
				
				for (Parameter<?> sdkParameter : sdkParameters) {
					Parameter<?> par = DynamicComponentUtils.createDynamicComponentParameter(sdkParameter, bl);
					((BaseComponent<?>)par).setContext(context);
					res.add(par);
				}
			}

			return res;

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
		//		return null;
	}

	@Override
	public boolean isUsableFilter(Allarme configurazioneAllarme) throws Exception{
		return _isUsable(configurazioneAllarme, true);
	}
	@Override
	public boolean isUsableGroupBy(Allarme configurazioneAllarme) throws Exception{
		return _isUsable(configurazioneAllarme, false);
	}
	public boolean _isUsable(Allarme configurazioneAllarme, boolean filter) throws Exception{
		
		try {
			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
			idPlugin.setTipo(configurazioneAllarme.getTipo());
			
			Plugin plugin = this.pluginsServiceSearchDAO.get(idPlugin);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getTipo(), plugin.getClassName(), AllarmiService.log);
			IAlarmProcessing alarmProcessing = (IAlarmProcessing) bl.newInstance();
			return filter ? alarmProcessing.isUsableFilter() : alarmProcessing.isUsableGroupBy();

		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public List<Plugin> plugins() throws Exception{
		
		try {
		
			IPaginatedExpression pagExpr = this.pluginsServiceSearchDAO.newPaginatedExpression();
			pagExpr.limit(this.LIMIT_SEARCH); // non esisteranno cosi tanti plugin 
			pagExpr.and();
			pagExpr.equals(Plugin.model().TIPO, TipoPlugin.ALLARME);
			List<Plugin> listSearch = this.pluginsServiceSearchDAO.findAll(pagExpr);
			return listSearch;
		} catch (Exception e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		}
		
	}

	

	@Override
	public void deleteAll() throws Exception {
	}

	
	@Override
	public ConfigurazioneAllarmeBean getAllarme(String nome)
			throws NotFoundException, ServiceException {
		try {
			Allarme find = this.allarmeSearchDAO.find(this.allarmeSearchDAO
					.newExpression().equals(Allarme.model().NOME,
							nome));

			if(find != null){
				IdPlugin idPlugin = new IdPlugin();
				idPlugin.setTipoPlugin(TipoPlugin.ALLARME.getValue());
				idPlugin.setTipo(find.getTipo());
				return new ConfigurazioneAllarmeBean(find, this.pluginsServiceSearchDAO.get(idPlugin));
			}
		} catch (ServiceException e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotFoundException e) {
			AllarmiService.log.debug(e.getMessage(), e);
			throw e;
		} catch (MultipleResultException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmiService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public AllarmeParametro getParametroByIdParametro(
			ConfigurazioneAllarmeBean allarme, String idParametro)
					throws NotFoundException, ServiceException {

		IExpression expr;
		try {
			expr = this.allarmeSearchDAO.newExpression();

			expr.equals(
					Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO,
					idParametro);
			expr.and().equals(Allarme.model().NOME,
					allarme.getNome());

			Allarme c = this.allarmeSearchDAO.find(expr);

			if (c != null
					&& c.getAllarmeParametroList().size() > 0) {

				for (AllarmeParametro par : allarme
						.getAllarmeParametroList()) {

					if (par.getIdParametro().equals(idParametro)) {
						return par;
					}
				}
			}

			return null;
		} catch (ServiceException e) {
			AllarmiService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			AllarmiService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			AllarmiService.log.debug(e.getMessage(), e);
			throw e;
		} catch (MultipleResultException e) {
			AllarmiService.log.error(e.getMessage(), e);
		}

		return null;
	}


	@Override
	public List<String> nomeAllarmeAutoComplete(String input) {
		List<String> list = new ArrayList<String>();


		IExpression expr;
		try {
			//			expr = this.allarmeSearchDAO.newExpression();

			// utilizzo la stessa condizione dei ricerca che mi restituisce solo gli allarmi consentiti per utente
			expr  = this.createQuery(false, this.searchForm);

			if(!StringUtils.isEmpty(input)){
				int idx= input.indexOf("/");
				if(idx != -1){
					input = input.substring(idx + 1, input.length());
				}

				expr.ilike(Allarme.model().NOME, input.toLowerCase() , LikeMode.ANYWHERE);
			}

			// Ordinamento gia' presente nella query standard
			//			expr.sortOrder(SortOrder.ASC).addOrder(ConfigurazioneAllarme.model().NOME);

			IPaginatedExpression pagExpr = this.allarmeSearchDAO
					.toPaginatedExpression(expr);
			pagExpr.offset(0).limit(this.LIMIT_SEARCH);

			List<Object> select = this.allarmeSearchDAO.select(pagExpr, Allarme.model().NOME);

			if(select != null && select.size() > 0)
				for (Object object : select) {
					list.add((String) object);
				}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage());
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return list;
	}
	

	@Override
	public List<AllarmeHistory> findAllHistory(long idAllarme,int start,int limit) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		IPaginatedExpression pagExpr = this.allarmeHistorySearchDAO.newPaginatedExpression();
		JDBCAllarmeHistoryServiceSearch jdbcInterfaceAudit = (JDBCAllarmeHistoryServiceSearch) this.allarmeHistorySearchDAO;
		CustomField cf = new CustomField("id_allarme", Long.class, "id_allarme", jdbcInterfaceAudit.getFieldConverter().toTable(AllarmeHistory.model()));
		pagExpr.equals(cf, idAllarme);
		pagExpr.offset(start);
		pagExpr.limit(limit);
		pagExpr.sortOrder(SortOrder.DESC);
		pagExpr.addOrder(AllarmeHistory.model().TIMESTAMP_UPDATE);
		
		List<AllarmeHistory> findAll = this.allarmeHistorySearchDAO.findAll(pagExpr);

		if(findAll != null && findAll.size() > 0){
			List<AllarmeHistory> toRet = new ArrayList<AllarmeHistory>();

			for (AllarmeHistory al : findAll) {
				toRet.add(new ConfigurazioneAllarmeHistoryBean(al));
			}

			return toRet;
		}
		
		return new ArrayList<AllarmeHistory>();
		
	}
	
	@Override
	public long countAllHistory(long idAllarme) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		
		IExpression expr = this.allarmeHistorySearchDAO.newExpression();
		JDBCAllarmeHistoryServiceSearch jdbcInterfaceAudit = (JDBCAllarmeHistoryServiceSearch) this.allarmeHistorySearchDAO;
		CustomField cf = new CustomField("id_allarme", Long.class, "id_allarme", jdbcInterfaceAudit.getFieldConverter().toTable(AllarmeHistory.model()));
		expr.equals(cf, idAllarme);
		return this.allarmeHistorySearchDAO.count(expr).longValue();
		
	}
	
	@Override
	public void addHistory(AllarmeHistory history) throws ServiceException, NotImplementedException{
		this.allarmeHistoryDAO.create(history);
	}
	
	@Override
	public IAlarm getAlarm(String name) throws AlarmException{
		try{
			return AlarmManager.getAlarm(name, LoggerManager.getPddMonitorCoreLogger(), DAOFactory.getInstance(AllarmiService.log));
		}catch(Exception e){
			throw new AlarmException(e.getMessage(),e);
		}
	}
}
