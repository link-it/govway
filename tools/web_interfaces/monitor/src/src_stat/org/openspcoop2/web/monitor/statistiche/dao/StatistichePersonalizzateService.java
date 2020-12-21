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
package org.openspcoop2.web.monitor.statistiche.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.base.IdConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita;
import org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.base.dao.IConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IDBConfigurazioneServizioAzioneServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginServiceSearch;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione;
import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.dao.IConfigurazioneStatisticaService;
import org.openspcoop2.monitor.engine.config.statistiche.dao.IConfigurazioneStatisticaServiceSearch;
import org.openspcoop2.monitor.engine.config.statistiche.dao.IDBConfigurazioneStatisticaServiceSearch;
import org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IOperationServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortTypeServiceSearch;
import org.openspcoop2.monitor.engine.config.BasicServiceLibrary;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.StatisticsServiceLibrary;
import org.openspcoop2.monitor.engine.config.StatisticsServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.web.monitor.core.dynamic.DynamicComponentUtils;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * StatistichePersonalizzateService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePersonalizzateService implements
IStatisticaPersonalizzataService {

	private static Logger log = LoggerManager.getPddMonitorSqlLogger(); 

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager statistichePluginsServiceManager;
	private org.openspcoop2.monitor.engine.config.base.dao.IServiceManager basePluginsServiceManager;
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager transazioniPluginsServiceManager;

	private IConfigurazioneStatisticaService statisticaDAO;

	private IConfigurazioneStatisticaServiceSearch statisticaSearchDAO;
	
	private IConfigurazioneServizioAzioneServiceSearch confServAzSearchDAO;
	
	private IPluginServiceSearch pluginsServiceSearchDAO;

	private IAccordoServizioParteComuneServiceSearch accordoDAO;

	private IOperationServiceSearch operationDAO;
	private IPortTypeServiceSearch portTypeDAO;
	private IAccordoServizioParteSpecificaServiceSearch serviziDAO;

	private BasicServiceLibraryReader basicServiceLibraryReader = null;
	private TransactionServiceLibraryReader transactionServiceLibraryReader = null;
	private StatisticsServiceLibraryReader statisticsServiceLibraryReader = null;

	public StatistichePersonalizzateService() {

		try {
			// init Service Manager plugins
			this.basePluginsServiceManager = (org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) DAOFactory
					.getInstance(StatistichePersonalizzateService.log).getServiceManager(
							org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),StatistichePersonalizzateService.log);
			this.confServAzSearchDAO = this.basePluginsServiceManager
					.getConfigurazioneServizioAzioneServiceSearch();
			this.pluginsServiceSearchDAO = this.basePluginsServiceManager.getPluginServiceSearch();
			this.basicServiceLibraryReader = new BasicServiceLibraryReader( ((org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) this.basePluginsServiceManager),
					((org.openspcoop2.core.commons.search.dao.IServiceManager) this.utilsServiceManager), true);
			
			// init Service Manager utils
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory.getInstance(StatistichePersonalizzateService.log)
					.getServiceManager(
							org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),StatistichePersonalizzateService.log);
			this.portTypeDAO = this.utilsServiceManager.getPortTypeServiceSearch();
			this.accordoDAO = this.utilsServiceManager.getAccordoServizioParteComuneServiceSearch();
			this.operationDAO = this.utilsServiceManager.getOperationServiceSearch();
			this.serviziDAO = this.utilsServiceManager.getAccordoServizioParteSpecificaServiceSearch();

			// init Service Manager plugins statistiche
			this.statistichePluginsServiceManager = (IServiceManager) DAOFactory.getInstance(StatistichePersonalizzateService.log)
					.getServiceManager(
							org.openspcoop2.monitor.engine.config.statistiche.utils.ProjectInfo.getInstance(),
							StatistichePersonalizzateService.log);
			this.statisticaDAO = this.statistichePluginsServiceManager.getConfigurazioneStatisticaService();
			this.statisticaSearchDAO = this.statistichePluginsServiceManager.getConfigurazioneStatisticaServiceSearch();
			this.statisticsServiceLibraryReader = 	new StatisticsServiceLibraryReader( ((org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager)
					this.statistichePluginsServiceManager), true);
			
			boolean attivoModuloTransazioniPersonalizzate = PddMonitorProperties.getInstance(log).isAttivoModuloTransazioniPersonalizzate();
			if(attivoModuloTransazioniPersonalizzate){
				
				// init Service Manager plugins transazioni
				this.transazioniPluginsServiceManager = (org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) DAOFactory.getInstance(StatistichePersonalizzateService.log)
						.getServiceManager(
								org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance(),
								StatistichePersonalizzateService.log);
				this.transactionServiceLibraryReader = 	new TransactionServiceLibraryReader( ((org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager)
						this.transazioniPluginsServiceManager), true);
				
			}
			
			
		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
	}

	private ConfigurazioneServizioAzione configurazione;

	@Override
	public void setConfigurazione(ConfigurazioneServizioAzione configurazione) {
		this.configurazione = configurazione;
	}

	@Override
	public List<ConfigurazioneStatistica> findAll(int start, int limit) {

		try {
			IExpression expr = this.statisticaSearchDAO.newExpression();

			IDBConfigurazioneServizioAzioneServiceSearch dbConfSearch = (IDBConfigurazioneServizioAzioneServiceSearch) this.confServAzSearchDAO;

			ConfigurazioneServizioAzione conf = dbConfSearch
					.get(this.configurazione.getId());

			if (conf != null) {
				Map<IField, Object> propertyNameValues = new HashMap<IField, Object>();

				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
						conf.getIdConfigurazioneServizio().getAccordo());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
						conf.getIdConfigurazioneServizio()
						.getNomeSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
						conf.getIdConfigurazioneServizio()
						.getServizio());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
						conf.getIdConfigurazioneServizio()
						.getTipoSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						conf.getIdConfigurazioneServizio()
						.getVersione());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
						this.configurazione.getAzione());

				expr.allEquals(propertyNameValues);

				expr.sortOrder(SortOrder.ASC).addOrder(ConfigurazioneStatistica.model().LABEL)	;

				IPaginatedExpression pagExpr = this.statisticaSearchDAO
						.toPaginatedExpression(expr);
				return this.statisticaSearchDAO.findAll(pagExpr.offset(start)
						.limit(limit));

			}

		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatistichePersonalizzateService.log.debug(e.getMessage(), e);
		}
		return new ArrayList<ConfigurazioneStatistica>();

	}

	@Override
	public int totalCount() {
		try {
			if (this.configurazione == null)
				return 0;

			IExpression expr = this.statisticaSearchDAO.newExpression();

			IDBConfigurazioneServizioAzioneServiceSearch dbConfSearch = (IDBConfigurazioneServizioAzioneServiceSearch) this.confServAzSearchDAO;

			ConfigurazioneServizioAzione conf = dbConfSearch
					.get(this.configurazione.getId());

			if (conf != null) {
				Map<IField, Object> propertyNameValues = new HashMap<IField, Object>();

				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
						conf.getIdConfigurazioneServizio().getAccordo());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
						conf.getIdConfigurazioneServizio()
						.getNomeSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
						conf.getIdConfigurazioneServizio()
						.getServizio());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
						conf.getIdConfigurazioneServizio()
						.getTipoSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						conf.getIdConfigurazioneServizio()
						.getVersione());

				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
						this.configurazione.getAzione());

				expr.allEquals(propertyNameValues);

				NonNegativeNumber nnn = this.statisticaSearchDAO.count(expr);

				return Long.valueOf(nnn.longValue()).intValue();
			}

		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public void store(ConfigurazioneStatistica obj) throws Exception {
		try {

			IdConfigurazioneStatistica idStat = new IdConfigurazioneStatistica();
			org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione idConf = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione();
			org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio idServizio = new org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio();
			idServizio.setAccordo(this.configurazione.getIdConfigurazioneServizio().getAccordo());
			idServizio.setTipoSoggettoReferente(this.configurazione.getIdConfigurazioneServizio().getTipoSoggettoReferente());
			idServizio.setNomeSoggettoReferente(this.configurazione.getIdConfigurazioneServizio().getNomeSoggettoReferente());
			idServizio.setVersione(this.configurazione.getIdConfigurazioneServizio().getVersione());
			idServizio.setServizio(this.configurazione.getIdConfigurazioneServizio().getServizio());
			idConf.setIdConfigurazioneServizio(idServizio);
			idConf.setAzione(this.configurazione.getAzione());

			if (obj.getId() != -1){
				idStat.setId(obj.getId());

				ConfigurazioneStatistica findById = this.findById(obj.getId());
				idStat.setIdConfigurazioneStatistica(findById.getIdConfigurazioneStatistica());
			}else {
				// insert
				idStat.setIdConfigurazioneStatistica(obj.getIdConfigurazioneStatistica());	
			}

			idStat.setIdConfigurazioneServizioAzione(idConf);

			if (this.statisticaDAO.exists(idStat)) {
				this.statisticaDAO.update(idStat, obj);
			} else {
				this.statisticaDAO.create(obj);
			}

		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void deleteById(Long key) {
		try {
			IdConfigurazioneStatistica id = new IdConfigurazioneStatistica();
			id.setId(key);
			
			this.statisticaDAO.deleteById(id);
			
		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public void delete(ConfigurazioneStatistica obj) throws Exception {
		try {
			this.statisticaDAO.delete(obj);
			
		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
	}

	@Override
	public ConfigurazioneStatistica findById(Long key) {
		try {
			return ((IDBConfigurazioneStatisticaServiceSearch) this.statisticaSearchDAO).get(key);
		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ConfigurazioneStatistica> findAll() {

		if (this.configurazione == null)
			return new ArrayList<ConfigurazioneStatistica>();

		try {
			IExpression expr = this.statisticaSearchDAO.newExpression();

			IDBConfigurazioneServizioAzioneServiceSearch dbConfSearch = (IDBConfigurazioneServizioAzioneServiceSearch) this.confServAzSearchDAO;

			ConfigurazioneServizioAzione conf = dbConfSearch
					.get(this.configurazione.getId());

			if (conf != null) {
				Map<IField, Object> propertyNameValues = new HashMap<IField, Object>();

				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
						conf.getIdConfigurazioneServizio().getAccordo());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
						conf.getIdConfigurazioneServizio()
						.getNomeSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
						conf.getIdConfigurazioneServizio()
						.getServizio());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
						conf.getIdConfigurazioneServizio()
						.getTipoSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						conf.getIdConfigurazioneServizio()
						.getVersione());

				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
						this.configurazione.getAzione());

				expr.allEquals(propertyNameValues);

				expr.sortOrder(SortOrder.ASC).addOrder(ConfigurazioneStatistica.model().LABEL)
				;

				IPaginatedExpression pagExpr = this.statisticaSearchDAO
						.toPaginatedExpression(expr);
				return this.statisticaSearchDAO.findAll(pagExpr);

			}

		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatistichePersonalizzateService.log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}  catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
		return new ArrayList<ConfigurazioneStatistica>();

	}

	@Override
	public List<Parameter<?>> instanceParameters(ConfigurazioneStatistica configurazioneStatistica, Context context){
		try {
			List<Parameter<?>> res = null;

			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(configurazioneStatistica.getPlugin().getTipoPlugin(), configurazioneStatistica.getPlugin().getTipo(),
					configurazioneStatistica.getPlugin().getClassName(),StatistichePersonalizzateService.log);
			List<Parameter<?>> sdkParameters = bl.getParameters(context);
			
			if(sdkParameters!=null && sdkParameters.size()>0){
				
				res = new ArrayList<Parameter<?>>();
				
				for (Parameter<?> sdkParameter : sdkParameters) {
					res.add(DynamicComponentUtils.createDynamicComponentParameter(sdkParameter, bl));
				}
			}

			return res;

		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ConfigurazioneStatistica> getStatisticheByValues(
			IDAccordo idAccordo, String nomeServizio, String nomeAzione) {

		try {

			// if(idAccordo==null || nomeServizio==null || nomeAzione==null){
			if (idAccordo == null || nomeServizio == null) {
				StatistichePersonalizzateService.log.error("Impossibile recuperare lista statistiche: idAccordo e/o nomeServizio non forniti.");
				return new ArrayList<ConfigurazioneStatistica>();
			}

			List<ConfigurazioneStatistica> lista = null;
			BasicServiceLibrary basicServiceLibrary = this.basicServiceLibraryReader.read(idAccordo,nomeServizio,nomeAzione, StatistichePersonalizzateService.log);
			if(basicServiceLibrary!=null){
				TransactionServiceLibrary transactionServiceLibrary = null;
				if(this.transactionServiceLibraryReader!=null){
					transactionServiceLibrary = this.transactionServiceLibraryReader.readConfigurazioneTransazione(basicServiceLibrary, log);
				}
				StatisticsServiceLibrary statsServiceLibrary = 
						this.statisticsServiceLibraryReader.readConfigurazioneStatistiche(basicServiceLibrary, transactionServiceLibrary, StatistichePersonalizzateService.log);
				if(statsServiceLibrary!=null){
					lista = statsServiceLibrary.mergeServiceActionSearchLibrary(true,true);
				}
			}

			return lista;

		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneStatistica>();
	}

//	@Override
	public ConfigurazioneStatistica getStatisticaByValues(IDAccordo idAccordo,
			String nomeServizio, String nomeAzione, String nomeStatistica) {
		try {

			if (idAccordo == null || nomeServizio == null || nomeAzione == null
					|| nomeStatistica == null) {
				StatistichePersonalizzateService.log
				.error("Impossibile recuperare lista statistiche: idAccordo, nomeServizio, nomeAzione e/o nomeStatistica non forniti.");
				return null;
			}

			Map<IField, Object> params = new HashMap<IField, Object>();
			if (idAccordo.getSoggettoReferente() != null) {
				if (idAccordo.getSoggettoReferente().getTipo() != null)
					params.put(
							ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
							idAccordo.getSoggettoReferente().getNome());

				if (idAccordo.getSoggettoReferente().getNome() != null)
					params.put(
							ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
							idAccordo.getSoggettoReferente().getTipo());
			}
			if (idAccordo.getVersione() != null)
				params.put(
						ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						idAccordo.getVersione());

			params.put(
					ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					idAccordo.getNome());
			params.put(
					ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					nomeServizio);

			params.put(
					ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
					nomeAzione);

			params.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA,
					nomeStatistica);

			return this.statisticaSearchDAO.find(this.statisticaSearchDAO
					.newExpression().allEquals(params));

		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void deleteAll() throws Exception {
	}

	

	@Override
	public ConfigurazioneStatistica findByStatistica(ConfigurazioneStatistica statisticaToCheck)
			throws NotFoundException, ServiceException {
		IExpression expr;
		try {
			expr = this.statisticaSearchDAO.newExpression();

			Map<IField, Object> params = new HashMap<IField, Object>();

			IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = statisticaToCheck.getIdConfigurazioneServizioAzione();

			if(idConfigurazioneServizioAzione != null){

				params.put(
						ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo());
				params.put(
						ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente());
				params.put(
						ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio().getServizio());
				params.put(
						ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente());
				params.put(
						ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio().getVersione());
			}

			expr.isNotNull(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE);

			params.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA, statisticaToCheck.getIdConfigurazioneStatistica());
			expr.allEquals(params);

			return this.statisticaSearchDAO.find(expr);
		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			// RicerchePersonalizzateService.log.error(e.getMessage(), e);
			throw e;
		} catch (MultipleResultException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public PortType getPortTypeFromAccordoServizio(String nomeAccordo,
			String nomeServizio) {

		IExpression expr;
		try {
			expr = this.accordoDAO.newExpression();
			expr.equals(AccordoServizioParteComune.model().NOME, nomeAccordo);

			AccordoServizioParteComune accordo = this.accordoDAO.find(expr);

			if (accordo == null)
				return null;

			expr = this.portTypeDAO.newExpression();

			expr.equals(PortType.model().NOME, nomeServizio);
			expr.and().equals(
					PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
					accordo.getNome());
			expr.and().equals(
					PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE,
					accordo.getVersione());

			return this.portTypeDAO.find(expr);

		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public List<Operation> getAzioniFromAccordoServizio(String nomeAccordo,
			String nomeServizio) {
		StatistichePersonalizzateService.log
		.info("Get Lista Azioni from Accordo Servizio [Accordo: "
				+ nomeAccordo + "], [nome Servizio: " + nomeServizio
				+ "]");

		IExpression expr;

		try {
			expr = this.accordoDAO.newExpression();
			expr.equals(AccordoServizioParteComune.model().NOME, nomeAccordo);

			AccordoServizioParteComune accordo = this.accordoDAO.find(expr);

			if (accordo == null)
				return new ArrayList<Operation>();

			expr = this.operationDAO.newExpression();

			expr.equals(Operation.model().ID_PORT_TYPE.NOME, nomeServizio);
			expr.and()
			.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
					accordo.getNome());
			expr.and()
			.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE,
					accordo.getVersione());
			expr.sortOrder(SortOrder.ASC).addOrder(Operation.model().NOME);

			IPaginatedExpression pagExpr = this.operationDAO
					.toPaginatedExpression(expr);

			return this.operationDAO.findAll(pagExpr);

		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return new ArrayList<Operation>();

	}

	@Override
	public List<Map<String, Object>> findElencoServizi(
			Soggetto soggetto) {

		StatistichePersonalizzateService.log
		.info("Get Lista Servizi [Soggetto: "
				+ (soggetto != null ? soggetto.getNomeSoggetto()
						: "Null") + "]");

		try {
			IExpression expr = this.serviziDAO.newExpression();

			if (soggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						soggetto.getTipoSoggetto());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						soggetto.getNomeSoggetto());
			}

			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().NOME)	;

			IPaginatedExpression pagExpr = this.serviziDAO
					.toPaginatedExpression(expr);

			return this.serviziDAO.select(pagExpr,true, AccordoServizioParteSpecifica.model().NOME, AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME);
		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String, Object>>();
	}
	
	@Override
	public ConfigurazioneStatistica findByStatisticaPlugin(ConfigurazioneStatistica statisticaToCheck, boolean checkAllActions, boolean checkSpecificActions) throws NotFoundException, ServiceException {
		IExpression expr;
		try {


			expr = this.statisticaSearchDAO.newExpression();
			expr.and();
			
			Map<IField, Object> propertyNameValues = new HashMap<IField, Object>();

			IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = statisticaToCheck.getIdConfigurazioneServizioAzione();

			if(idConfigurazioneServizioAzione != null){

				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio()
						.getNomeSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio()
						.getServizio());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio()
						.getTipoSoggettoReferente());
				propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
						idConfigurazioneServizioAzione.getIdConfigurazioneServizio()
						.getVersione());
			}
			
			expr.isNotNull(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE);
			if(checkAllActions && checkSpecificActions){
				throw new ServiceException("Invocazione metodo non permessa checkAllActions["+checkAllActions+"] checkSpecificActions["+checkSpecificActions+"]");
			}
			if(checkAllActions){
				expr.equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,"*");
			}
			if(checkSpecificActions){
				expr.notEquals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,"*");
			}
			
			propertyNameValues.put(ConfigurazioneStatistica.model().PLUGIN.CLASS_NAME, statisticaToCheck.getPlugin().getClassName());
			propertyNameValues.put(ConfigurazioneStatistica.model().PLUGIN.TIPO, statisticaToCheck.getPlugin().getTipo());

			expr.allEquals(propertyNameValues);

			return this.statisticaSearchDAO.find(expr);
		} catch (ServiceException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatistichePersonalizzateService.log.debug(e.getMessage(), e);
			throw e;
		} catch (MultipleResultException e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}

		return null;
	}
	
	@Override
	public List<Plugin> compatiblePlugins(ConfigurazioneServizioAzione configurazione, Long idStatistica){
		
		List<Plugin> list = new ArrayList<Plugin>();
		try {
		
			IdConfigurazioneServizio confServ = configurazione.getIdConfigurazioneServizio();
			String uriAccordo = IDAccordoFactory.getInstance().getUriFromValues(confServ.getAccordo(), 
					new IDSoggetto(confServ.getTipoSoggettoReferente(), confServ.getNomeSoggettoReferente()), 
					confServ.getVersione());
			String servizio = confServ.getServizio();
			String azione = configurazione.getAzione();
			
			
			// 1. Localizzo ricerche già definite
			
			IPaginatedExpression expr = this.statisticaSearchDAO.newPaginatedExpression();
			Map<IField, Object> propertyNameValues = new HashMap<IField, Object>();
			propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO,
					confServ.getAccordo());
			propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE,
					confServ.getNomeSoggettoReferente());
			propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO,
					confServ.getServizio());
			propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE,
					confServ.getTipoSoggettoReferente());
			propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE,
					confServ.getVersione());
			propertyNameValues
				.put(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE,
						azione);
			expr.allEquals(propertyNameValues);
			List<ConfigurazioneStatistica> listConfigStatistica = this.statisticaSearchDAO.findAll(expr);
			
			if(idStatistica!=null && idStatistica>0){
				for (int i = 0; i < listConfigStatistica.size(); i++) {
					if(listConfigStatistica.get(i).getId().equals(idStatistica)){
						listConfigStatistica.remove(i);
						break;
					}
				}
			}
			
			IPaginatedExpression pagExpr = this.pluginsServiceSearchDAO.newPaginatedExpression();
			pagExpr.limit(10000); // non esisteranno cosi tanti plugin 
			pagExpr.and();
			pagExpr.equals(Plugin.model().TIPO, TipoPlugin.STATISTICA);
			List<Plugin> listSearch = this.pluginsServiceSearchDAO.findAll(pagExpr);
			for (Plugin pluginCheck : listSearch) {
				
				// controllo che il plugin non sia già stato aggiunto alla ricerca
				if(listConfigStatistica!=null && listConfigStatistica.size()>0){
					boolean found = false;
					for (ConfigurazioneStatistica confStatistica : listConfigStatistica) {
						if(confStatistica.getPlugin().getClassName().equals(pluginCheck.getClassName())){
							found = true;
							break;
						}
					}
					if(found){
						continue;
					}
				}
				
				if(pluginCheck.sizePluginServizioCompatibilitaList()<=0){
					// compatibile con qualsiasi servizio
					list.add(pluginCheck);
					continue;
				}
				
				boolean compatible = false;
				for (PluginServizioCompatibilita servizioCompatibilita : pluginCheck.getPluginServizioCompatibilitaList()) {
					if(servizioCompatibilita.getUriAccordo()!=null && (servizioCompatibilita.getUriAccordo().equals(uriAccordo)==false)){
						continue;
					}
					if(servizioCompatibilita.getServizio()!=null && (servizioCompatibilita.getServizio().equals(servizio)==false)){
						continue;
					}
					if(azione==null || "*".equals(azione)){
						if(servizioCompatibilita.sizePluginServizioAzioneCompatibilitaList()>0){
							continue;
						}
					}
					else{
						if(servizioCompatibilita.sizePluginServizioAzioneCompatibilitaList()>0){
							boolean foundAzione = false;
							for (PluginServizioAzioneCompatibilita azioneCompatibilita : servizioCompatibilita.getPluginServizioAzioneCompatibilitaList()) {
								if(azioneCompatibilita.getAzione().equals(azione)){
									foundAzione = true;
								}
							}
							if(!foundAzione){
								continue;
							}
						}
					}
					compatible = true;
					break;
				}
				
				if(compatible){
					list.add(pluginCheck);
				}
			}
			
					
		} catch (Exception e) {
			StatistichePersonalizzateService.log.error(e.getMessage(), e);
		}
		
		return list;
	}
}
