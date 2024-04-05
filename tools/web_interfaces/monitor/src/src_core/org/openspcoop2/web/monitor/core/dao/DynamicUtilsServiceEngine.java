/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.Gruppo;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.IdPortaDominio;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.PortaDominio;
import org.openspcoop2.core.commons.search.ServizioApplicativo;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneGruppoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IFruitoreServiceSearch;
import org.openspcoop2.core.commons.search.dao.IGruppoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortTypeServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDominioServiceSearch;
import org.openspcoop2.core.commons.search.dao.IServizioApplicativoServiceSearch;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCSoggettoServiceSearch;
import org.openspcoop2.core.commons.search.utils.ExpressionProperties;
import org.openspcoop2.core.commons.search.utils.ProjectInfo;
import org.openspcoop2.core.commons.search.utils.RegistroCore;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.dao.IPluginServiceSearch;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
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
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;


/***
 * 
 * Funzionalita' di supporto per la gestione delle maschere di ricerca.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DynamicUtilsServiceEngine implements IDynamicUtilsService{


	private int defaultStart = 0;
	private int LIMIT_SEARCH = 10000;

	private static Logger log = LoggerManager.getPddMonitorSqlLogger(); 

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private ISoggettoServiceSearch soggettoDAO;

	private IGruppoServiceSearch gruppiDAO;
	
	private IAccordoServizioParteComuneServiceSearch aspcDAO = null;
	private IAccordoServizioParteComuneGruppoServiceSearch aspcGruppiDAO = null;
	private IAccordoServizioParteSpecificaServiceSearch aspsDAO = null;

	private IServizioApplicativoServiceSearch serviziApplicativiDAO = null;
	private IPortTypeServiceSearch portTypeDAO = null;

	private IFruitoreServiceSearch fruitoreSearchDAO;

	private IPortaDominioServiceSearch pddDAO = null;

	private IPortaDelegataServiceSearch portaDelegataDAO = null;
	private IPortaApplicativaServiceSearch portaApplicativaDAO  = null;

	private org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager;
	
	private IPluginServiceSearch pluginsServiceSearchDAO;
	
	private transient DriverRegistroServiziDB driverRegistroDB = null;
	private transient DriverConfigurazioneDB driverConfigDB = null;
	
	public DynamicUtilsServiceEngine(){
		this(null, null, null, null);
	}
	public DynamicUtilsServiceEngine(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager, org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager,
			DriverRegistroServiziDB driverRegistroServiziDB, DriverConfigurazioneDB driverConfigurazioneDB){
		try{
			if(serviceManager==null) {
				this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
						.getInstance( log).getServiceManager(ProjectInfo.getInstance(), DynamicUtilsServiceEngine.log);
			}
			else {
				this.utilsServiceManager = serviceManager;
			}
			this.soggettoDAO = this.utilsServiceManager.getSoggettoServiceSearch();

			this.gruppiDAO = this.utilsServiceManager.getGruppoServiceSearch();
			
			this.aspcDAO = this.utilsServiceManager.getAccordoServizioParteComuneServiceSearch();
			this.aspcGruppiDAO = this.utilsServiceManager.getAccordoServizioParteComuneGruppoServiceSearch();
			this.aspsDAO = this.utilsServiceManager.getAccordoServizioParteSpecificaServiceSearch();

			this.serviziApplicativiDAO = this.utilsServiceManager.getServizioApplicativoServiceSearch();

			this.portTypeDAO  = this.utilsServiceManager.getPortTypeServiceSearch();

			this.fruitoreSearchDAO = this.utilsServiceManager.getFruitoreServiceSearch();

			this.pddDAO = this.utilsServiceManager.getPortaDominioServiceSearch();

			this.portaApplicativaDAO = this.utilsServiceManager.getPortaApplicativaServiceSearch();
			this.portaDelegataDAO = this.utilsServiceManager.getPortaDelegataServiceSearch();

			
			if(pluginsServiceManager==null) {
				this.pluginsServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
						.getInstance( log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(), DynamicUtilsServiceEngine.log);
			}
			else {
				this.pluginsServiceManager = pluginsServiceManager;
			}
			
			this.pluginsServiceSearchDAO = this.pluginsServiceManager.getPluginServiceSearch();
			
			String datasourceJNDIName = null;
			Properties datasourceJNDIContext = null;
			String tipoDatabase = null;
			if(driverRegistroServiziDB==null || driverConfigurazioneDB==null) {
				datasourceJNDIName = DAOFactoryProperties.getInstance(log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				datasourceJNDIContext = DAOFactoryProperties.getInstance(log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				tipoDatabase = DAOFactoryProperties.getInstance(log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			}
						
			if(driverRegistroServiziDB==null) {
				this.driverRegistroDB = new DriverRegistroServiziDB(datasourceJNDIName,datasourceJNDIContext, log, tipoDatabase);
			}
			else {
				this.driverRegistroDB = driverRegistroServiziDB;
			}
			if(driverConfigurazioneDB==null) {
				this.driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, log, tipoDatabase);
			}
			else {
				this.driverConfigDB = driverConfigurazioneDB;
			}
			
			PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(log);
			this.LIMIT_SEARCH = monitorProperties.getSearchFormLimit();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	public DynamicUtilsServiceEngine(Connection con, boolean autoCommit){
		this(con, autoCommit, null, DynamicUtilsServiceEngine.log);
	}
	public DynamicUtilsServiceEngine(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public DynamicUtilsServiceEngine(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, DynamicUtilsServiceEngine.log);
	}
	public DynamicUtilsServiceEngine(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try{
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance( log).getServiceManager(ProjectInfo.getInstance(), con,autoCommit,serviceManagerProperties,log);
			this.soggettoDAO = this.utilsServiceManager.getSoggettoServiceSearch();

			this.gruppiDAO = this.utilsServiceManager.getGruppoServiceSearch();
			
			this.aspcDAO = this.utilsServiceManager.getAccordoServizioParteComuneServiceSearch();
			this.aspcGruppiDAO = this.utilsServiceManager.getAccordoServizioParteComuneGruppoServiceSearch();
			this.aspsDAO = this.utilsServiceManager.getAccordoServizioParteSpecificaServiceSearch();

			this.serviziApplicativiDAO = this.utilsServiceManager.getServizioApplicativoServiceSearch();

			this.portTypeDAO  = this.utilsServiceManager.getPortTypeServiceSearch();

			this.fruitoreSearchDAO = this.utilsServiceManager.getFruitoreServiceSearch();

			this.pddDAO = this.utilsServiceManager.getPortaDominioServiceSearch();

			this.portaApplicativaDAO = this.utilsServiceManager.getPortaApplicativaServiceSearch();
			this.portaDelegataDAO = this.utilsServiceManager.getPortaDelegataServiceSearch();

			
			this.pluginsServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance( log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(), con,autoCommit,serviceManagerProperties,log);
			
			this.pluginsServiceSearchDAO = this.pluginsServiceManager.getPluginServiceSearch();
			
			
			String tipoDatabase = DAOFactoryProperties.getInstance(log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			
			this.driverRegistroDB = new DriverRegistroServiziDB(con, log, tipoDatabase);
			this.driverConfigDB = new DriverConfigurazioneDB(con, log, tipoDatabase);
			
			PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(log);
			this.LIMIT_SEARCH = monitorProperties.getSearchFormLimit();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public org.openspcoop2.core.commons.search.dao.IServiceManager getUtilsServiceManager() {
		return this.utilsServiceManager;
	} 
	
	public static IExpression getExpressionTipiSoggettiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiSoggetti = protocolFactory.createProtocolConfiguration().getTipiSoggetti();

			return expr.in(field, tipiSoggetti);
		}

		return null;
	}

	public static IExpression getExpressionTipiServiziCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiServizi = new ArrayList<>();
			
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.SOAP));
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.REST));
			
			return expr.in(field, tipiServizi);
		}

		return null;
	}
	
	public static IExpression getExpressionTipiSoggettiCompatibiliConProtocollo(IServiceSearchWithoutId<?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiSoggetti = protocolFactory.createProtocolConfiguration().getTipiSoggetti();

			return expr.in(field, tipiSoggetti);
		}

		return null;
	}

	public static IExpression getExpressionTipiServiziCompatibiliConProtocollo(IServiceSearchWithoutId<?> dao, IField field, String protocollo) throws Exception{
		if(protocollo != null){
			IExpression expr = dao.newExpression();

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);

			List<String> tipiServizi = new ArrayList<>();
			
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.SOAP));
			tipiServizi.addAll(protocolFactory.createProtocolConfiguration().getTipiServizi(ServiceBinding.REST));

			return expr.in(field, tipiServizi);
		}

		return null;
	}

	@Override
	public int countPdD(String tipoProtocollo) {
		try {
			IExpression expr = this.pddDAO.newExpression();
			NonNegativeNumber nnn = null;
			if(StringUtils.isNotBlank(tipoProtocollo)){

				IPaginatedExpression sogExpr = this.soggettoDAO.newPaginatedExpression();
				if(StringUtils.isNotEmpty(tipoProtocollo)){
					sogExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}

				List<Object> select = this.soggettoDAO.select(sogExpr, true, Soggetto.model().SERVER);

				return select != null ? select.size() : 0;
			} else {
				nnn = this.pddDAO.count(expr);
				if(nnn != null)
					return (int) nnn.longValue();
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage());
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return 0;
	}




	/***
	 * 
	 * Restituisce l'elenco dei primi n soggetti che corrispondono all'input inserito
	 * 
	 * 
	 * @param input
	 * @return elenco risultati trovati
	 */
	@Override
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input) {
		return soggettiAutoComplete(tipoProtocollo,input,false);
	}
	@Override
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input,Boolean searchTipo) {
		return this.findElencoSoggetti(tipoProtocollo, null, input, searchTipo);
	}
	
	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo,String idPorta) {
		return this.findElencoSoggetti(tipoProtocollo, idPorta, null, false);
	}

	@Override
	public int countElencoSoggetti(String tipoProtocollo,String idPorta) {
		return countElencoSoggetti(tipoProtocollo, idPorta, null, false);
	}
	
	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta, String input) {
		return this.findElencoSoggetti(tipoProtocollo, idPorta, input, false);
	}
	
	@Override
	public int countElencoSoggetti(String tipoProtocollo, String idPorta, String input) {
		return countElencoSoggetti(tipoProtocollo, idPorta, input, false);
	}

	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo,String idPorta, String input,Boolean searchTipo) {
		try {
			IExpression expr = this.soggettoDAO.newExpression();

			boolean addAnd = false;
			if(idPorta != null){
				expr.equals(Soggetto.model().IDENTIFICATIVO_PORTA,idPorta);
				addAnd = true;
			}
			
			if(!StringUtils.isEmpty(input)){
				int idx= input.indexOf("/");
				if(idx != -1){
					input = input.substring(idx + 1, input.length());
				}

				if(searchTipo){
					IExpression exprOrTipo = this.soggettoDAO.newExpression();
					exprOrTipo.ilike(Soggetto.model().TIPO_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
					IExpression exprOrNome = this.soggettoDAO.newExpression();
					exprOrNome.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
					expr.or(exprOrTipo,exprOrNome);
				}
				else{
					expr.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
				}
			}

			if(tipoProtocollo!= null){
				if(addAnd)
					expr.and();

				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

			IPaginatedExpression pagExpr = this.soggettoDAO
					.toPaginatedExpression(expr); 
			
			pagExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);

			return this.soggettoDAO.findAll(pagExpr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}
	
	@Override
	public int countElencoSoggetti(String tipoProtocollo,String idPorta, String input,Boolean searchTipo) {
		try {
			IExpression expr = this.soggettoDAO.newExpression();
			boolean addAnd = false;
			if(idPorta != null){
				expr.equals(Soggetto.model().IDENTIFICATIVO_PORTA,idPorta);
				addAnd = true;
			}
			
			if(!StringUtils.isEmpty(input)){
				int idx= input.indexOf("/");
				if(idx != -1){
					input = input.substring(idx + 1, input.length());
				}

				if(searchTipo){
					IExpression exprOrTipo = this.soggettoDAO.newExpression();
					exprOrTipo.ilike(Soggetto.model().TIPO_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
					IExpression exprOrNome = this.soggettoDAO.newExpression();
					exprOrNome.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
					expr.or(exprOrTipo,exprOrNome);
				}
				else{
					expr.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase() , LikeMode.ANYWHERE);
				}
			}

			if(tipoProtocollo!= null){
				if(addAnd)
					expr.and();

				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			NonNegativeNumber nnn = this.soggettoDAO.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public Soggetto findSoggettoByTipoNome(String tipoSoggetto,	String nomeSoggetto) {

		try {
			if(StringUtils.isNotEmpty(tipoSoggetto) && StringUtils.isNotEmpty(nomeSoggetto)){
				return this.soggettoDAO.find(this.soggettoDAO.newExpression()
						.equals(Soggetto.model().NOME_SOGGETTO, nomeSoggetto).and()
						.equals(Soggetto.model().TIPO_SOGGETTO, tipoSoggetto));
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
	
	@Override
	public Soggetto findSoggettoById(Long idSoggetto) {
		try {
			return ((JDBCSoggettoServiceSearch) this.soggettoDAO).get(idSoggetto);
			
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Soggetto> findElencoSoggettiFromTipoSoggetto(String tipoSoggetto) {
		try {
			return this.soggettoDAO.findAll(this.soggettoDAO
					.toPaginatedExpression(this.soggettoDAO
							.newPaginatedExpression()
							.equals(Soggetto.model().TIPO_SOGGETTO,tipoSoggetto)
							.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().NOME_SOGGETTO)));
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int countElencoSoggettiFromTipoSoggetto(String tipoSoggetto) {
		try {
			IExpression expr = this.soggettoDAO.newExpression();
			expr.equals(Soggetto.model().TIPO_SOGGETTO,tipoSoggetto);
			NonNegativeNumber nnn = this.soggettoDAO.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public List<Soggetto> findElencoSoggettiFromTipoPdD(String tipoProtocollo,TipoPdD tipoPdD) {
		try {
			IPaginatedExpression pagExpr = this.pddDAO.newPaginatedExpression();
			pagExpr.equals(PortaDominio.model().TIPO, tipoPdD).
			sortOrder(SortOrder.ASC).addOrder(PortaDominio.model().NOME);
			List<IdPortaDominio> idPorte = this.pddDAO.findAllIds(pagExpr);

			IPaginatedExpression sogPagExpr = this.soggettoDAO.newPaginatedExpression();
			if(idPorte != null && idPorte.size() > 0){
				List<String> nomiPorte = new ArrayList<>();
				for (IdPortaDominio idPorta : idPorte) {
					nomiPorte.add(idPorta.getNome());
				}
				sogPagExpr.in(Soggetto.model().SERVER, nomiPorte);
			}
			sogPagExpr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

			if(StringUtils.isNotEmpty(tipoProtocollo)){
				sogPagExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
			}

			return this.soggettoDAO.findAll(sogPagExpr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ArrayList<Soggetto>(); 
	}

	@Override
	public int countElencoSoggettiFromTipoTipoPdD(String tipoProtocollo,TipoPdD tipoPdD) {
		try {
			if(tipoPdD!=null){
				IPaginatedExpression pagExpr = this.pddDAO.newPaginatedExpression();
				pagExpr.equals(PortaDominio.model().TIPO, tipoPdD).
				sortOrder(SortOrder.ASC).addOrder(PortaDominio.model().NOME);
				List<IdPortaDominio> idPorte = this.pddDAO.findAllIds(pagExpr);
				IExpression sogExpr = this.soggettoDAO.newExpression();
				if(idPorte != null && idPorte.size() > 0){
					List<String> nomiPorte = new ArrayList<>();
					for (IdPortaDominio idPorta : idPorte) {
						nomiPorte.add(idPorta.getNome());
					}
					sogExpr.in(Soggetto.model().SERVER, nomiPorte);
				}
				else{
					// se non esistono porte non esistono nemmeno soggetti
					return 0;
				}
	
				if(StringUtils.isNotEmpty(tipoProtocollo)){
					sogExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}
	
				NonNegativeNumber nnn = this.soggettoDAO.count(sogExpr);
	
				if(nnn != null){
					return (int) nnn.longValue(); 
				}
			}
			else{
				// devo cercare i soggetto a cui non è stata associata una pdd
				IExpression sogExpr = this.soggettoDAO.newExpression();
				
				IExpression sogExprServer = this.soggettoDAO.newExpression();
				sogExprServer.isNull(Soggetto.model().SERVER);
				sogExprServer.isEmpty(Soggetto.model().SERVER);
				sogExprServer.or();
				
				sogExpr.and(sogExprServer);
				
				if(StringUtils.isNotEmpty(tipoProtocollo)){
					sogExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}
				
				NonNegativeNumber nnn = this.soggettoDAO.count(sogExpr);
				
				if(nnn != null){	
					return (int) nnn.longValue(); 
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}
	
	@Override
	public boolean checkTipoPdd(String nome,TipoPdD tipoPdD) {
		try {
			if(nome==null || "".equals(nome) || "-".equals(nome)){
				if(TipoPdD.ESTERNO.equals(tipoPdD)){
					return true;
				}
				else{
					return false;
				}
			}
			
			IPaginatedExpression pagExpr = this.pddDAO.newPaginatedExpression();
			pagExpr.equals(PortaDominio.model().TIPO, tipoPdD).equals(PortaDominio.model().NOME, nome).
			sortOrder(SortOrder.ASC).addOrder(PortaDominio.model().NOME);
			pagExpr.limit(1);
			List<IdPortaDominio> idPorte = this.pddDAO.findAllIds(pagExpr);

			return idPorte != null && idPorte.size() > 0;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false; 
	}


	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo) {
		try {
			IExpression expr = this.aspsDAO.newExpression();
			expr.sortOrder(SortOrder.ASC)
			.addOrder(AccordoServizioParteSpecifica.model().TIPO) 
			.addOrder(AccordoServizioParteSpecifica.model().NOME)
			;

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			if(tipoProtocollo!= null){
				pagExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}

			return this.aspsDAO.select(pagExpr, true, AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME);

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String,Object>>();
	}

	@Override
	public int countElencoServizi(String tipoProtocollo) {
		try {
			IExpression expr = this.aspsDAO.newExpression();
			NonNegativeNumber nnn = this.aspsDAO.count(expr);

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}  

		return 0;
	}

	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto) {
		return findElencoServizi(tipoProtocollo, soggetto, null);
	}
	
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo, Soggetto soggetto, String val) {
		return findElencoServizi(tipoProtocollo,soggetto,val,false);
	}
	
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo, Soggetto soggetto, String val, Boolean searchTipo) {
		log.debug("Get Lista Servizi [Soggetto: " + (soggetto != null ? soggetto.getNomeSoggetto() : "Null")+ "], VAL: ["+val+"]");

		try {
			IExpression expr = this.aspsDAO.newExpression();

			if (soggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						soggetto.getTipoSoggetto());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						soggetto.getNomeSoggetto());
			}

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			
			if(StringUtils.isNotEmpty(val)){
				if(searchTipo){
					IExpression exprOrTipo = this.aspsDAO.newExpression();
					exprOrTipo.ilike(AccordoServizioParteSpecifica.model().TIPO, val , LikeMode.ANYWHERE);
					IExpression exprOrNome = this.aspsDAO.newExpression();
					exprOrNome.ilike(AccordoServizioParteSpecifica.model().NOME, val , LikeMode.ANYWHERE);
					expr.or(exprOrTipo,exprOrNome);
				}
				else{
					expr.and().ilike(AccordoServizioParteSpecifica.model().NOME,val,LikeMode.ANYWHERE);		
				}
			}
			
			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

			List<Map<String, Object>> list = this.aspsDAO.select(pagExpr,true,AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME,cf);

			for (Map<String, Object> map : list) {
				Long idAccordoLong = (Long) map.remove("idAccordo");

				AccordoServizioParteComune aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);

				map.put("idAccordo", aspc);
			}

			return list;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String, Object>>();
	}
	
	@Override
	public AccordoServizioParteComune getAccordoServizio(String tipoProtocollo, IDSoggetto idSoggetto, String tipoServizio, String nomeServizio, Integer versioneServizio) {
		log.debug("Get Lista Servizi [Soggetto: " + (idSoggetto != null ? idSoggetto.getNome() : "Null")
				+ "], Servizio: ["+tipoServizio + "/"+ nomeServizio +"]");
		AccordoServizioParteComune aspc = null;
		try {
			IExpression expr = this.aspsDAO.newExpression();

			if (idSoggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						idSoggetto.getTipo());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						idSoggetto.getNome());
			}

			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			
			if(StringUtils.isNotEmpty(tipoServizio) && StringUtils.isNotEmpty(nomeServizio) && versioneServizio!=null){
					expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio);
					expr.and();
					expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio);
					expr.and();
					expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio);
			}
			
			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

			List<Object> list = this.aspsDAO.select(pagExpr,true,cf);

			for (Object idAccordoObj : list) {
				Long idAccordoLong = (Long) idAccordoObj;
				  aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);
			}

			return aspc;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public List<IdAccordoServizioParteComuneGruppo> getAccordoServizioGruppi(IdAccordoServizioParteComune id){
		try {
			
			IPaginatedExpression pagExpr = this.aspcGruppiDAO.newPaginatedExpression();
			pagExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, id.getNome());
			pagExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, id.getVersione());
			pagExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, id.getIdSoggetto().getNome());
			pagExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, id.getIdSoggetto().getTipo());
			List<IdAccordoServizioParteComuneGruppo> list = this.aspcGruppiDAO.findAllIds(pagExpr);
			return list;
			
		}catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	@Override
	public int countElencoServizi(String tipoProtocollo,Soggetto soggetto) {
		try {
			IExpression expr = this.aspsDAO.newExpression();
			if (soggetto != null) {
				expr.equals(
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
						soggetto.getTipoSoggetto());
				expr.and()
				.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
						soggetto.getNomeSoggetto());
			}
			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			NonNegativeNumber nnn = this.aspsDAO.count(expr);

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return 0;
	}

	//	@Override
	public List<Map<String, Object>> findElencoServizi(List<Soggetto> soggetti) {

		log.debug("Get Lista Servizi dei soggetti");

		try {
			IExpression expr = this.aspsDAO.newExpression();

			if (soggetti != null && soggetti.size() > 0) {
				IExpression exprSoggetti = this.aspsDAO.newExpression();
				for (Soggetto soggetto : soggetti) {
					IExpression expr2 = this.aspsDAO.newExpression();
					expr2.equals(
							AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
							soggetto.getTipoSoggetto());
					expr2.and()
					.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
							soggetto.getNomeSoggetto());
					exprSoggetti.or(expr2);
				}

				expr.and(exprSoggetti);
			}

			expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

			IPaginatedExpression pagExpr = this.aspsDAO
					.toPaginatedExpression(expr);

			CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

			List<Map<String, Object>> list = this.aspsDAO.select(pagExpr,true,AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME,cf);

			for (Map<String, Object> map : list) {
				Long idAccordoLong = (Long) map.remove("idAccordo");

				AccordoServizioParteComune aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);

				map.put("idAccordo", aspc);
			}

			return list;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Map<String, Object>>();
	}


	@Override
	public Map<String, String> findAzioniFromServizio(String tipoProtocollo ,String tipoServizio ,	String nomeServizio,String tipoErogatore ,	String nomeErogatore ,Integer versioneServizio, String val) {
		log.debug("Get Lista Azioni from Accordo Servizio [nome Servizio: " + nomeServizio + "]");

		try {
			Map<String, String> azioniConLabel = RegistroCore.getAzioniConLabel((JDBCServiceManager) this.utilsServiceManager, tipoProtocollo, tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio,val);
			
			return azioniConLabel;
 
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return new HashMap<>();

	}

	@Override
	public int countAzioniFromServizio(String tipoProtocollo,String tipoServizio ,
			String nomeServizio,String tipoErogatore ,	String nomeErogatore,Integer versioneServizio, String val) {
		// Implementazione inefficiente
		return this.findAzioniFromServizio(tipoProtocollo,tipoServizio, nomeServizio,tipoErogatore,nomeErogatore,versioneServizio,val).size();
	}

	@Override
	public List<Object> findElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto, boolean trasporto, boolean token) {

		log.debug("Get Lista Servizi Applicativi [Soggetto: " + (soggetto != null ? soggetto.getNomeSoggetto() : "Null")	+ "]");
		// if (erogatore != null) {
		// list = this.service
		// .getEntityManager()
		// .createQuery(
		// "select s.nome from ServizioApplicativo s where s.soggetto.id = :id_soggetto order by s.nome asc")
		// .setParameter("id_soggetto", erogatore.getId())
		// .getResultList();
		// }
		try {
			boolean viaModiProperties = (tipoProtocollo==null) || org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(tipoProtocollo);
			
			IExpression expr = buildExpressionServiziApplicativi(tipoProtocollo, soggetto, trasporto, token, false);
			expr.sortOrder(SortOrder.ASC).addOrder(ServizioApplicativo.model().NOME);
			IPaginatedExpression pagExpr = this.serviziApplicativiDAO.toPaginatedExpression(expr);
			List<Object> l = null;
			try {
				l = this.serviziApplicativiDAO.select(pagExpr,true,ServizioApplicativo.model().NOME);
			}catch (NotFoundException e) {
				log.debug(e.getMessage(), e);
			}
			if(l==null) {
				l = new ArrayList<>();
			}
			
			if(viaModiProperties) {
			
				expr = buildExpressionServiziApplicativi(tipoProtocollo, soggetto, trasporto, token, true);
				expr.sortOrder(SortOrder.ASC).addOrder(ServizioApplicativo.model().NOME);
				pagExpr = this.serviziApplicativiDAO.toPaginatedExpression(expr);
				try {
					List<Object> lmodi = this.serviziApplicativiDAO.select(pagExpr,true,ServizioApplicativo.model().NOME);
					if(lmodi!=null && !lmodi.isEmpty()) {
						l.addAll(lmodi);
					}
				}catch (NotFoundException e) {
					log.debug(e.getMessage(), e);
				}
				
			}
			
			return l;
			
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		}catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return new ArrayList<>();
	}

	@Override
	public int countElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto, boolean trasporto, boolean token) {
		log.debug("countElencoServiziApplicativi [Soggetto: " + (soggetto != null ? soggetto.getNomeSoggetto() : "Null")	+ "]");
		try {
			boolean viaModiProperties = (tipoProtocollo==null) || org.openspcoop2.protocol.engine.constants.Costanti.MODIPA_PROTOCOL_NAME.equals(tipoProtocollo);
			
			IExpression expr = buildExpressionServiziApplicativi(tipoProtocollo, soggetto, trasporto, token, false);
			NonNegativeNumber nnn = this.serviziApplicativiDAO.count(expr);
			
			int result = 0;
			if(nnn != null){
				result = (int) nnn.longValue();
			}
			
			if(viaModiProperties) {
				expr = buildExpressionServiziApplicativi(tipoProtocollo, soggetto, trasporto, token, false);
				nnn = this.serviziApplicativiDAO.count(expr);
				if(nnn != null){
					int resultModi = (int) nnn.longValue();
					if(resultModi>0) {
						result = result + resultModi;
					}
				}
			}
			
			return result;
			
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}
	
	private IExpression buildExpressionServiziApplicativi(String tipoProtocollo,Soggetto soggetto, boolean trasporto, boolean token,
			boolean viaModiProperties) throws Exception {
		IExpression expr = this.serviziApplicativiDAO.newExpression();

		if (soggetto != null) {
			if(soggetto.getTipoSoggetto()!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(soggetto.getTipoSoggetto())) {
				expr.equals(ServizioApplicativo.model().ID_SOGGETTO.TIPO,
						soggetto.getTipoSoggetto());
			}
			if(soggetto.getNomeSoggetto()!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(soggetto.getNomeSoggetto())) {
				expr.and().equals(ServizioApplicativo.model().ID_SOGGETTO.NOME,
						soggetto.getNomeSoggetto());
			}
			
		}
		
		IExpression exprClient = this.serviziApplicativiDAO.newExpression();
		exprClient.isNotNull(ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE).and().notEquals(ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE, TipologiaFruizione.DISABILITATO);
		IExpression exprServerUseAsClient = this.serviziApplicativiDAO.newExpression();
		exprServerUseAsClient.isNotNull(ServizioApplicativo.model().AS_CLIENT).and().equals(ServizioApplicativo.model().AS_CLIENT, CostantiDB.TRUE);
		expr.or(exprClient, exprServerUseAsClient);
		
		if(tipoProtocollo != null)
			expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.serviziApplicativiDAO, ServizioApplicativo.model().ID_SOGGETTO.TIPO, tipoProtocollo));

		if(trasporto && token) {
			// nop
		}
		else {
			
			if(token) {
			
				if(viaModiProperties) {
					expr.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.NAME, CostantiDB.MODIPA_SICUREZZA_TOKEN_POLICY);
					expr.isNotNull(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_STRING);
				}
				else {
					expr.and().in(ServizioApplicativo.model().TIPOAUTH, CostantiConfigurazione.CREDENZIALE_TOKEN.toString(), CostantiConfigurazione.CREDENZIALE_SSL.toString());
					expr.and().isNotNull(ServizioApplicativo.model().TOKEN_POLICY);
				}
				
			}
			else {// trasporto
				
				expr.and().isNull(ServizioApplicativo.model().TOKEN_POLICY);
				
			}
		}
		return expr;
			
	}


	@Override
	public PortType getPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo ,String nomeServizio) {

		log
		.debug("Get Port Type from Accordo Servizio [Accordo: "+ idAccordo.getNome() + "], [nome Servizio: " + nomeServizio + "]");

		IExpression expr = null; 
		try {
			expr = this.aspsDAO.newExpression();
			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();

			if(idAccordo.getNome() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

			if(idAccordo.getVersione() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

			if(idAccordo.getSoggettoReferente() != null){
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
			}

			AccordoServizioParteSpecifica asps = this.aspsDAO.find(expr);

			if(asps != null && asps.getPortType()!= null){

				expr = this.portTypeDAO.newExpression();

				expr.equals(PortType.model().NOME, asps.getPortType());
				if(idAccordo.getNome() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

				if(idAccordo.getVersione() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

				if(idAccordo.getSoggettoReferente() != null){
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
				}

				return this.portTypeDAO.find(expr);

			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public int countPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo,
			String nomeServizio) {
		log
		.debug("count Port Type from Accordo Servizio [Accordo: "+ idAccordo.getNome() + "], [nome Servizio: " + nomeServizio + "]");

		IExpression expr = null; 
		try {
			expr = this.aspsDAO.newExpression();
			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();

			if(idAccordo.getNome() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

			if(idAccordo.getVersione() != null)
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

			if(idAccordo.getSoggettoReferente() != null){
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
				expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
			}

			AccordoServizioParteSpecifica asps = this.aspsDAO.find(expr);

			if(asps != null && asps.getPortType()!= null){

				expr = this.portTypeDAO.newExpression();

				expr.equals(PortType.model().NOME, asps.getPortType());
				if(idAccordo.getNome() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());

				if(idAccordo.getVersione() != null)
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());

				if(idAccordo.getSoggettoReferente() != null){
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();
					expr.equals(PortType.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
				}

				NonNegativeNumber nnn =  this.portTypeDAO.count(expr);

				if(nnn != null){
					return (int) nnn.longValue();
				}
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public AccordoServizioParteSpecifica getAspsFromValues(String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore,Integer versioneServizio){
		log.debug("Get AccordoServizioParteSpecifica from Tipo/Nome Servizio [Tipo: "+ tipoServizio + "], [nome: " + nomeServizio + "]");

		IExpression expr = null; 
		try {

			expr = this.aspsDAO.newExpression();
			expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoErogatore).and();
			expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeErogatore);
			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and();
			expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and();
			expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio);

			return this.aspsDAO.find(expr);

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
	
	@Override
	public AccordoServizioParteSpecifica getAspsFromId(Long idServizio) {
		log.debug("Get AccordoServizioParteSpecifica from Id ["+ idServizio + "]");

		try {
			return ((JDBCAccordoServizioParteSpecificaServiceSearch)this.aspsDAO).get(idServizio);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.debug(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
	

	/*****
	 * 
	 * Restituisce gli accordi di servizio relativi al soggetto passato come parametro.
	 * Se isReferente == true il soggetto e' il referente degli accordi.
	 * Se isErogatore == true il soggetto e' erogatore di un servizio definito nell'accordo.
	 * 
	 */
	@Override
	public List<AccordoServizioParteComune> getAccordiServizio(String tipoProtocollo,String tipoSoggetto , String nomeSoggetto,
			Boolean isReferente, Boolean isErogatore, String tag) {
		List<AccordoServizioParteComune> toRet = new ArrayList<AccordoServizioParteComune>();
		log.debug("Get AccordiServizio from Tipo/Nome Soggetto [Tipo: "+ tipoSoggetto + "], [nome: " + nomeSoggetto + "] Referente["+isReferente+"] Erogatore ["+isErogatore+"] Tag["+tag+"]");
		try {
			// se il soggetto e' stato selezionato
			if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
				// utilizzo il soggetto passato come parametro come referente
				if(isReferente){
					// restituisco tutti gli accordi parte comune presenti con referente uguale al parametro
					IPaginatedExpression pagExpr = this.aspcDAO.newPaginatedExpression();
					pagExpr.and();

					pagExpr.equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoSoggetto).and().equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME, nomeSoggetto);
					if(tipoProtocollo != null)
						pagExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));
					if(tag!=null && !"".equals(tag)) {
						pagExpr.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO.NOME, tag);
					}
					
					pagExpr.sortOrder(SortOrder.ASC);
					pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.TIPO);
					pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.NOME);
					pagExpr.addOrder(AccordoServizioParteComune.model().NOME);
					pagExpr.addOrder(AccordoServizioParteComune.model().VERSIONE);

					pagExpr.offset(0).limit(this.LIMIT_SEARCH);
					toRet = this.aspcDAO.findAll(pagExpr);
					return toRet;
				}

				//utilizzo il soggetto come erogatore
				if(isErogatore){
					IExpression expr = this.aspsDAO.newExpression();
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);

					if(tipoProtocollo != null)
						expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));

					if(tag!=null && !"".equals(tag)) {
						throw new Exception("Funzionalità non supportata (Ricerca per Tag con isErogatore)");
					}
					
					expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)
					.addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)
					.addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)
					.addOrder(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);

					IPaginatedExpression pagExpr = this.aspsDAO.toPaginatedExpression(expr);
					pagExpr.and();

					CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");
					List<Map<String, Object>> list = null;
					try{
						list = this.aspsDAO.select(pagExpr,true,AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO,
								AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME,
								AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
								AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE	,cf);
					}
					catch (NotFoundException e) {
						log.debug("Nessun risultato trovato");
					} 

					if(list != null && list.size() > 0){
						for (Map<String, Object> map : list) {
							Long idAccordoLong = (Long) map.remove("idAccordo");
							try{
								AccordoServizioParteComune aspc = ((JDBCAccordoServizioParteComuneServiceSearch)this.aspcDAO).get(idAccordoLong);
								toRet.add(aspc);
							}
							catch (NotFoundException e) {
								log.debug("Accordo si servizio non trovato");
							} 
						}
					}
				}
			} else {
				// restituisco tutti gli accordi parte comune presenti
				IPaginatedExpression pagExpr = this.aspcDAO.newPaginatedExpression();
				pagExpr.and();
				
				if(tipoProtocollo != null)
					pagExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));
			
				if(tag!=null && !"".equals(tag)) {
					pagExpr.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO.NOME, tag);
				}
				
				pagExpr.sortOrder(SortOrder.ASC);
				pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.TIPO);
				pagExpr.addOrder(AccordoServizioParteComune.model().ID_REFERENTE.NOME);
				pagExpr.addOrder(AccordoServizioParteComune.model().NOME);
				pagExpr.addOrder(AccordoServizioParteComune.model().VERSIONE);

				pagExpr.offset(0).limit(this.LIMIT_SEARCH);
				toRet = this.aspcDAO.findAll(pagExpr);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return toRet;
	}

	@Override
	public int countAccordiServizio(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto,
			Boolean isReferente, Boolean isErogatore, String tag) {
		NonNegativeNumber nnn = null;
		log.debug("countAccordiServizio from Tipo/Nome Soggetto [Tipo: "+ tipoSoggetto + "], [nome: " + nomeSoggetto + "] Referente["+isReferente+"] Erogatore ["+isErogatore+"] Tag["+tag+"]");
		try {
			if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
				// utilizzo il soggetto passato come parametro come referente
				if(isReferente){
					// restituisco tutti gli accordi parte comune presenti con referente uguale al parametro
					IExpression expr = this.aspcDAO.newExpression();
					expr.and();
					
					if(tipoProtocollo != null)
						expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));

					expr.equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoSoggetto) .and() .equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME, nomeSoggetto);

					if(tag!=null && !"".equals(tag)) {
						expr.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO.NOME, tag);
					}
					
					nnn = this.aspcDAO.count(expr);
				}

				//utilizzo il soggetto come erogatore
				if(isErogatore){
					CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");
					IExpression expr = this.aspsDAO.newExpression();
					expr.and();
					
					if(tipoProtocollo != null)
						expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));

					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto).and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);

					if(tag!=null && !"".equals(tag)) {
						throw new Exception("Funzionalità non supportata (Ricerca per Tag con isErogatore)");
					}
					
					FunctionField countF = new FunctionField(cf, Function.COUNT_DISTINCT, "numeroAccordi");

					Object aggregate = this.aspsDAO.aggregate(expr,countF);

					if(aggregate != null){
						if(aggregate instanceof Long){
							Long count = (Long) aggregate;
							return count.intValue();
						}
					}

				}

			}else {
				IExpression expr = this.aspcDAO.newExpression();
				expr.and();
				
				if(tipoProtocollo != null)
					expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspcDAO, AccordoServizioParteComune.model().ID_REFERENTE.TIPO, tipoProtocollo));

				if(tag!=null && !"".equals(tag)) {
					expr.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO.NOME, tag);
				}
				
				nnn = this.aspcDAO.count(expr);
			}
			if(nnn != null){
				return (int) nnn.longValue();
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}    

		return 0;
	}

	@Override
	public List<IDGruppo> getGruppi(String protocollo){
		if(protocollo==null || "".equals(protocollo) || "-".equals(protocollo) || "--".equals(protocollo)) {
			try {
				IPaginatedExpression expr = this.gruppiDAO.newPaginatedExpression();
				
				expr.sortOrder(SortOrder.ASC).addOrder(Gruppo.model().NOME);
				
				List<Object> list = this.gruppiDAO.select(expr, Gruppo.model().NOME);
				if(list!=null && !list.isEmpty()) {
					List<IDGruppo> r = new ArrayList<>();
					for (Object o : list) {
						r.add(new IDGruppo((String)o));
					}
					return r;
				}
				
			} catch (ServiceException e) {
				log.error(e.getMessage(), e);
			} catch (NotImplementedException e) {
				log.error(e.getMessage(), e);
			} catch(NotFoundException notFound) {
				log.debug(notFound.getMessage(), notFound);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		else {
			FiltroRicercaGruppi filtroGruppi = new FiltroRicercaGruppi();
			filtroGruppi.setProtocollo(protocollo);
			try {
				List<IDGruppo> listGruppi = this.driverRegistroDB.getAllIdGruppi(filtroGruppi);
				return listGruppi;
			}catch(DriverRegistroServiziNotFound notFound) {
				log.debug(notFound.getMessage(), notFound);
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
			
		return new ArrayList<IDGruppo>();
	}
	@Override
	public int countGruppi(String protocollo) {
		if(protocollo==null || "".equals(protocollo) || "-".equals(protocollo) || "--".equals(protocollo)) {
			try {
				IExpression expr = this.gruppiDAO.newExpression();
				return (int) this.gruppiDAO.count(expr).longValue();
			} catch (ServiceException e) {
				log.error(e.getMessage(), e);
			} catch (NotImplementedException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		else {
			FiltroRicercaGruppi filtroGruppi = new FiltroRicercaGruppi();
			filtroGruppi.setProtocollo(protocollo);
			try {
				List<IDGruppo> listGruppi = this.driverRegistroDB.getAllIdGruppi(filtroGruppi);
				return listGruppi!=null ? listGruppi.size() : 0;
			}catch(DriverRegistroServiziNotFound notFound) {
				log.debug(notFound.getMessage(), notFound);
			}catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return 0;
	}
	
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto) {
		return this.getServizi(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, null);
	}
	
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo, String uriAccordoServizio,	String tipoSoggetto, String nomeSoggetto, String val) {
		return getServizi(tipoProtocollo, uriAccordoServizio, tipoSoggetto, nomeSoggetto, val, false);
	}
	
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo, String uriAccordoServizio,
			String tipoSoggetto, String nomeSoggetto, String val, Boolean searchTipo) {
			log.debug("getServizi: UriAccordoServizio: [" + uriAccordoServizio + "] SoggettoErogatore: [" + tipoSoggetto +"/"+nomeSoggetto + "] Val ["+val+"]" );
			IExpression expr;
			try {
				expr = this.aspsDAO.newExpression();
				if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);



					if(idAccordo.getNome() != null){
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					}

					if( idAccordo.getVersione() != null){
						expr.and().equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					} else {
						expr.and().isNull(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);
					}

					if(idAccordo.getSoggettoReferente() != null){
						if(idAccordo.getSoggettoReferente().getTipo() != null)
							expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

						if(idAccordo.getSoggettoReferente().getNome() != null)
							expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
					} else {
						CustomField cf2 = new CustomField("idReferente", Integer.class, "id_referente", "accordi");
						expr.lessEquals(cf2, 0);
					}
				}
				if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
					expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto)
					.and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);
				}
				if(tipoProtocollo!= null){
					expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
				}
				
				if(StringUtils.isNotEmpty(val)){
					if(searchTipo){
						IExpression exprOrTipo = this.aspsDAO.newExpression();
						exprOrTipo.ilike(AccordoServizioParteSpecifica.model().TIPO, val , LikeMode.ANYWHERE);
						IExpression exprOrNome = this.aspsDAO.newExpression();
						exprOrNome.ilike(AccordoServizioParteSpecifica.model().NOME, val , LikeMode.ANYWHERE);
						expr.or(exprOrTipo,exprOrNome);
					}
					else{
						expr.and().ilike(AccordoServizioParteSpecifica.model().NOME,val,LikeMode.ANYWHERE);
					}
				}
					
				
				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME)
				.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO).addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME)
				;

				IPaginatedExpression pagExpr = this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(this.LIMIT_SEARCH);


				return this.aspsDAO.findAll(pagExpr);


		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<AccordoServizioParteSpecifica>();
	}

	@Override
	public int countServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto,
			String nomeSoggetto) {
		log.debug("countServizi: UriAccordoServizio: [" + uriAccordoServizio + "] SoggettoErogatore: [" + tipoSoggetto +"/"+nomeSoggetto + "]" );
		IExpression expr;
		NonNegativeNumber nnn =null;
		try {
			expr = this.aspsDAO.newExpression();
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty()){
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
				}

				if( idAccordo.getVersione() != null){
					expr.and().equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
				} else {
					expr.and().isNull(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				} else {
					CustomField cf2 = new CustomField("idReferente", Integer.class, "id_referente", "accordi");
					expr.lessEquals(cf2, 0);
				}
			}
			if(StringUtils.isNotEmpty(nomeSoggetto) && StringUtils.isNotEmpty(tipoSoggetto)){
				expr.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto)
				.and().equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);
			}
			if(tipoProtocollo!= null){
				expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.aspsDAO, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoProtocollo));
			}
			nnn = this.aspsDAO.count(expr);
			if(nnn != null){
				return (int) nnn.longValue();
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return 0;
	}


	public AccordoServizioParteSpecifica convertPortTypeName (String portTypeName){
		// TODO controllare la versione 
		try{

			int idx = portTypeName.indexOf("(");

			if(idx == -1)
				return null;

			String tipoNomeServizio = portTypeName.substring(0, idx).trim();
			String tipoNomeSoggetto = portTypeName.substring(idx+1).replace(")","").trim();

			if(tipoNomeServizio.indexOf("/") == -1)
				return null;

			if(tipoNomeSoggetto.indexOf("/") == -1)
				return null;

			String tipoServizio = tipoNomeServizio.split("/")[0];
			String nomeServizio = tipoNomeServizio.split("/")[1];

			String tipoSoggetto = tipoNomeSoggetto.split("/")[0];
			String nomeSoggetto = tipoNomeSoggetto.split("/")[1];

			IExpression expr = this.aspsDAO.newExpression();


			expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio).and()
			.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio).and()
			.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggetto).and()
			.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggetto);

			return this.aspsDAO.find(expr);
		}catch(NotFoundException e){
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		}


		return null;
	}

	@Override
	public List<Soggetto> getSoggettiErogatoreAutoComplete(String tipoProtocollo,
			String uriAccordoServizio , String input) {
		IExpression expr;
		try {

			int idx= input.indexOf("/");
			if(idx != -1){
				input = input.substring(idx + 1, input.length());
			}

			// non ho scelto un accordo di servizio
			if(uriAccordoServizio != null && !uriAccordoServizio.isEmpty()) 
			{
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);


				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}

				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(this.LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){
					List<Soggetto> lstSog = new ArrayList<Soggetto>();
					Set<Soggetto> setSog = new HashSet<Soggetto>();
					List<String> soggettiGiaInseriti = new ArrayList<>();

					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.soggettoDAO.newExpression();
						if(asps.getIdErogatore()!= null){
							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Soggetto.model().TIPO_SOGGETTO, asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Soggetto.model().NOME_SOGGETTO, asps.getIdErogatore().getNome());
						}

						expr 
						.ilike(Soggetto.model().NOME_SOGGETTO, input.toLowerCase(), LikeMode.ANYWHERE);

						expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

						Soggetto s = this.soggettoDAO.find(expr);

						if(s != null){
							String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
							if(soggettiGiaInseriti.contains(key)==false){
								setSog.add(s);
								soggettiGiaInseriti.add(key);
							}
						}


					}

					if(setSog.size() > 0)
						lstSog.addAll(setSog);

					return lstSog;
				}
			}
			else 	{
				expr = this.soggettoDAO.newExpression();


				expr.ilike(Soggetto.model().NOME_SOGGETTO,	input.toLowerCase(), LikeMode.ANYWHERE);
				if(tipoProtocollo!= null){
					expr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo( this.soggettoDAO, Soggetto.model().TIPO_SOGGETTO, tipoProtocollo));
				}

				expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

				IPaginatedExpression pagExpr = this.soggettoDAO
						.toPaginatedExpression(expr);
				pagExpr.offset(0).limit(this.LIMIT_SEARCH);

				return this.soggettoDAO.findAll(pagExpr);

			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	@Override
	public List<Soggetto> getSoggettiFruitoreAutoComplete(String tipoProtocollo,
			String uriAccordoServizio,   String input) {

		IExpression expr;
		try {

			int idx= input.indexOf("/");
			if(idx != -1){
				input = input.substring(idx + 1, input.length());
			}

			// non ho scelto un accordo di servizio
			if(uriAccordoServizio != null   && !uriAccordoServizio.isEmpty() ) {
				expr = this.aspsDAO.newExpression();

				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordoServizio);

				boolean addAnd = false;

				if(idAccordo.getNome() != null){
					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome());
					addAnd = true;
				}

				if( idAccordo.getVersione() != null){
					if(addAnd)
						expr.and();

					expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione());
					addAnd = true;
				}

				if(idAccordo.getSoggettoReferente() != null){
					if(addAnd)
						expr.and();

					if(idAccordo.getSoggettoReferente().getTipo() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).and();

					if(idAccordo.getSoggettoReferente().getNome() != null)
						expr.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome()).and();
				}
				expr.sortOrder(SortOrder.ASC).addOrder(AccordoServizioParteSpecifica.model().TIPO).addOrder(AccordoServizioParteSpecifica.model().NOME);

				IPaginatedExpression pagExpr =this.aspsDAO.toPaginatedExpression(expr);

				pagExpr.offset(0).limit(this.LIMIT_SEARCH);

				List<AccordoServizioParteSpecifica> lstAsps = this.aspsDAO.findAll(pagExpr);

				if(lstAsps != null){

					List<Soggetto> lstSog = new ArrayList<Soggetto>();
					Set<Soggetto> setSog = new HashSet<Soggetto>();
					List<String> soggettiGiaInseriti = new ArrayList<>();
					for(AccordoServizioParteSpecifica asps : lstAsps){

						expr = this.fruitoreSearchDAO.newExpression();


						addAnd = false;

						if(asps.getNome() != null){
							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME,asps.getNome());
							addAnd = true;
						}

						if( asps.getTipo()!= null){
							if(addAnd)
								expr.and();

							expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO,  asps.getTipo());
							addAnd = true;
						}

						if(asps.getIdErogatore()!= null){
							if(addAnd)
								expr.and();

							if(asps.getIdErogatore().getTipo() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO,asps.getIdErogatore().getTipo()).and();

							if(asps.getIdErogatore().getNome() != null)
								expr.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME,asps.getIdErogatore().getNome()).and();
						}

						expr.ilike(Fruitore.model().ID_FRUITORE.NOME, input.toLowerCase(), LikeMode.ANYWHERE);

						expr.sortOrder(SortOrder.ASC).addOrder(Fruitore.model().ID_FRUITORE.TIPO).addOrder(Fruitore.model().ID_FRUITORE.NOME);

						pagExpr = this.fruitoreSearchDAO.toPaginatedExpression(expr);

						pagExpr.offset(0).limit(this.LIMIT_SEARCH);

						List<Fruitore> lstFruitori = this.fruitoreSearchDAO.findAll(pagExpr); 

						if(lstFruitori != null){
							for (Fruitore fruitore : lstFruitori) {
								Soggetto s = this.soggettoDAO.get(fruitore.getIdFruitore());

								if(s != null){
									String key = s.getTipoSoggetto() + "/" + s.getNomeSoggetto();
									if(soggettiGiaInseriti.contains(key)==false){
										setSog.add(s);
										soggettiGiaInseriti.add(key);
									}
								}
							}

							if(setSog.size() > 0)
								lstSog.addAll(setSog);


						}				

					}
					return lstSog;			

				}

			} else {
				expr = this.soggettoDAO.newExpression();

				expr.ilike(Soggetto.model().NOME_SOGGETTO,	input.toLowerCase(), LikeMode.ANYWHERE);

				expr.sortOrder(SortOrder.ASC).addOrder(Soggetto.model().TIPO_SOGGETTO).addOrder(Soggetto.model().NOME_SOGGETTO);

				IPaginatedExpression pagExpr = this.soggettoDAO
						.toPaginatedExpression(expr);
				pagExpr.offset(0).limit(this.LIMIT_SEARCH);

				return this.soggettoDAO.findAll(pagExpr);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return new ArrayList<Soggetto>();
	}

	public List<PortaApplicativa> findPorteApplicative(String tipoProtocollo, String tipoSoggetto ,String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {

		log.debug("Get Lista Porta Applicativa [Soggetto Erogatore : " + nomeErogatore	+ "]");
		List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();

		try {
			IExpression paExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, null, null, null, null, null, null, null,null);
			
			IPaginatedExpression pagPdExpr = this.portaApplicativaDAO.toPaginatedExpression(paExpr );
			listaPorte = this.portaApplicativaDAO.findAll(pagPdExpr);


			return listaPorte;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return listaPorte;
	}

	public List<PortaDelegata> findPorteDelegate(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto,String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, PermessiUtenteOperatore permessiUtenteOperatore) {

		log.debug("Get Lista Porta Delegate [Soggetto Erogatore : " + nomeErogatore	+ "]");
		List<PortaDelegata> listaPorte = new ArrayList<PortaDelegata>();

		try {
			IExpression pdExpr = this.getExpressionPD(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, nomeAzione, null, permessiUtenteOperatore);
			
			IPaginatedExpression pagPdExpr = this.portaDelegataDAO.toPaginatedExpression(pdExpr );
			listaPorte = this.portaDelegataDAO.findAll(pagPdExpr);

			return listaPorte;
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return listaPorte;
	}

	
	
	@Override
	public List<IDServizio> getServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto,
			String val, Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct) {
		log.debug("getServiziErogazione [Soggetto Proprietario : " + tipoSoggetto + "/"+ nomeSoggetto	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression pAExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, null, null, null, null, null, null, val,permessiUtenteOperatore);
				 
			List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();
			IPaginatedExpression pagPaExpr = this.portaApplicativaDAO.toPaginatedExpression(pAExpr );
			
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO, SortOrder.ASC);
						
			pagPaExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPaExpr);
			listaPorte = this.portaApplicativaDAO.findAll(pagPaExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaApplicativa portaApplicativa : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(),
							portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), portaApplicativa.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;
	}
	
	@Override
	public List<IDServizio> getServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct){
		log.debug("getServiziErogazione [Soggetto Proprietario : " + tipoSoggetto + "/"+ nomeSoggetto	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression paExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val, permessiUtenteOperatore);
			
			List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();
			IPaginatedExpression pagPaExpr = this.portaApplicativaDAO.toPaginatedExpression(paExpr );
			
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO, SortOrder.ASC);
			
			pagPaExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPaExpr);
			listaPorte = this.portaApplicativaDAO.findAll(pagPaExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaApplicativa portaApplicativa : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(),
							portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), portaApplicativa.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;		
	}
	
	@Override
	public int countServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String val,
			Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore) {
		try {
			IExpression paExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, null, null, null, null, null, null, val,permessiUtenteOperatore);

			NonNegativeNumber nnn = this.portaApplicativaDAO.count(paExpr);
			
			if(nnn != null)
				return (int) nnn.longValue(); 
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}
	
	@Override
	public List<IDServizio> getConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto,
			String val, Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct) {
		log.debug("getServiziErogazione [Soggetto Proprietario : " + tipoSoggetto + "/"+ nomeSoggetto	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression pAExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, null, null, null, null, null, null, val,permessiUtenteOperatore);
				 
			List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();
			IPaginatedExpression pagPaExpr = this.portaApplicativaDAO.toPaginatedExpression(pAExpr );
			
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO, SortOrder.ASC);
			
			pagPaExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPaExpr);
			listaPorte = this.portaApplicativaDAO.findAll(pagPaExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaApplicativa portaApplicativa : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(),
							portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), portaApplicativa.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;
	}
	
	@Override
	public List<IDServizio> getConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct){
		
		log.debug("getServiziErogazione [Soggetto Proprietario : " + tipoSoggetto + "/"+ nomeSoggetto	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression paExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val, permessiUtenteOperatore);

			
			List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();
			IPaginatedExpression pagPaExpr = this.portaApplicativaDAO.toPaginatedExpression(paExpr );
			
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPaExpr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO, SortOrder.ASC);
			
			pagPaExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPaExpr);
			listaPorte = this.portaApplicativaDAO.findAll(pagPaExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaApplicativa portaApplicativa : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(),
							portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), portaApplicativa.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;
		
	}
	
	@Override
	public int countConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto,
			String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore,
			Integer versioneServizio, String nomeAzione, String val, Boolean searchTipo,
			PermessiUtenteOperatore permessiUtenteOperatore) {
		try {
			IExpression paExpr = getExpressionPA(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio,	tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val, permessiUtenteOperatore);

			NonNegativeNumber nnn = this.portaApplicativaDAO.count(paExpr);
			
			if(nnn != null)
				return (int) nnn.longValue(); 
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}


	private IExpression getExpressionPA(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String tipoServizio,
			String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio,
			String nomeAzione, String val, PermessiUtenteOperatore permessiUtenteOperatore) throws ServiceException,
			NotImplementedException, CoreException, ExpressionNotImplementedException, ExpressionException, Exception {
		IExpression paExpr = this.portaApplicativaDAO.newExpression();
		
		if(permessiUtenteOperatore!=null){
			IExpression permessi = permessiUtenteOperatore.toExpressionConfigurazioneServizi(this.portaApplicativaDAO, 
					PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
					PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
					PortaApplicativa.model().TIPO_SERVIZIO, PortaApplicativa.model().NOME_SERVIZIO, PortaApplicativa.model().VERSIONE_SERVIZIO,
					false);
			paExpr.and(permessi);
		}

		if(StringUtils.isNotEmpty(tipoSoggetto ) && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggetto))
			paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoSoggetto);
		if(StringUtils.isNotEmpty(nomeSoggetto ) && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggetto))
			paExpr.and().equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggetto);
		if(StringUtils.isNotEmpty(val)) {
			paExpr.ilike(PortaApplicativa.model().ID_SOGGETTO.NOME, val.toLowerCase(), LikeMode.ANYWHERE);
		}
		
		// Se ho selezionato un servizio, oppure se non ho selezionato niente 
		if(StringUtils.isNotEmpty(nomeServizio ))
			paExpr.equals(PortaApplicativa.model().NOME_SERVIZIO, nomeServizio).and();
		if(StringUtils.isNotEmpty(tipoServizio ))
			paExpr.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipoServizio).and();
		if(versioneServizio != null)
			paExpr.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versioneServizio).and();
		if(StringUtils.isNotEmpty(nomeErogatore ))
			paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeErogatore).and();
		if(StringUtils.isNotEmpty(tipoErogatore ))
			paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoErogatore).and();

		if(StringUtils.isNotEmpty(nomeAzione )){
			IExpression azioneExpr =  this.portaApplicativaDAO.newExpression();
			azioneExpr.equals(PortaApplicativa.model().NOME_AZIONE, nomeAzione).or().isNull(PortaApplicativa.model().NOME_AZIONE); 
			paExpr.and(azioneExpr);
		}

		if(tipoProtocollo!= null){
			paExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaApplicativaDAO, PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoProtocollo));
		}
	
		paExpr.notEquals(PortaApplicativa.model().MODE_AZIONE, org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.DELEGATED_BY);
		return paExpr;
	}
	
	@Override
	public List<IDServizio> getServiziFruizione(String tipoProtocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore, String val, Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct) {
		log.debug("getServiziFruizione [Soggetto Erogatore : " + tipoSoggettoErogatore + "/"+ nomeSoggettoErogatore	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression pdExpr = getExpressionPD(tipoProtocollo, null, null, tipoSoggettoErogatore, nomeSoggettoErogatore, null, null, null, null, val, permessiUtenteOperatore);
			
			List<PortaDelegata> listaPorte = new ArrayList<PortaDelegata>();
			IPaginatedExpression pagPdExpr = this.portaDelegataDAO.toPaginatedExpression(pdExpr );
			
			pagPdExpr.addOrder(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().VERSIONE_SERVIZIO, SortOrder.ASC);
			
			pagPdExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPdExpr);
			listaPorte = this.portaDelegataDAO.findAll(pagPdExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaDelegata portaDelegata : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore(), portaDelegata.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;
	}
	
	@Override
	public List<IDServizio> getServiziFruizione(String tipoProtocollo, 
			String tipoSoggetto, String nomeSoggetto, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, 
			String tipoServizio ,String nomeServizio, Integer versioneServizio, String nomeAzione, 
			String val, Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct) {
		log.debug("getServiziFruizione [Soggetto Erogatore : " + tipoSoggettoErogatore + "/"+ nomeSoggettoErogatore	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression pdExpr = getExpressionPD(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoSoggettoErogatore, nomeSoggettoErogatore,	tipoServizio, nomeServizio, versioneServizio, nomeAzione, val, permessiUtenteOperatore);
						
			List<PortaDelegata> listaPorte = new ArrayList<PortaDelegata>();
			IPaginatedExpression pagPdExpr = this.portaDelegataDAO.toPaginatedExpression(pdExpr );
			
			pagPdExpr.addOrder(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().VERSIONE_SERVIZIO, SortOrder.ASC);
						
			pagPdExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPdExpr);
			listaPorte = this.portaDelegataDAO.findAll(pagPdExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaDelegata portaDelegata : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore(), portaDelegata.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;
	}
	
	@Override
	public int countServiziFruizione(String tipoProtocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore, String val,
			Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore) {
		try {
			IExpression pdExpr = getExpressionPD(tipoProtocollo, null, null, tipoSoggettoErogatore, nomeSoggettoErogatore, null, null,null, null, val, permessiUtenteOperatore);
			
			NonNegativeNumber nnn = this.portaDelegataDAO.count(pdExpr);

			if(nnn != null)
				return (int) nnn.longValue(); 
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}
	
	@Override
	public List<IDServizio> getConfigurazioneServiziFruizione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct){
		log.debug("getServiziFruizione [Soggetto Fruitore : " + tipoSoggetto + "/"+ nomeSoggetto	 + "]");
		List<IDServizio> lista = new ArrayList<IDServizio>();

		try {
			IExpression pdExpr = getExpressionPD(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoErogatore, nomeErogatore,	tipoServizio, nomeServizio, versioneServizio, nomeAzione, val, permessiUtenteOperatore);
			
			List<PortaDelegata> listaPorte = new ArrayList<PortaDelegata>();
			IPaginatedExpression pagPdExpr = this.portaDelegataDAO.toPaginatedExpression(pdExpr );
			
			pagPdExpr.addOrder(PortaDelegata.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().ID_SOGGETTO.NOME, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().TIPO_SERVIZIO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().NOME_SERVIZIO, SortOrder.ASC);
			pagPdExpr.addOrder(PortaDelegata.model().VERSIONE_SERVIZIO, SortOrder.ASC);
						
			pagPdExpr.offset(this.defaultStart).limit(this.LIMIT_SEARCH);
			ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPdExpr);
			listaPorte = this.portaDelegataDAO.findAll(pagPdExpr);
			
			List<String> lstTmp = new ArrayList<>();
			for (PortaDelegata portaDelegata : listaPorte) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore(), portaDelegata.getVersioneServizio());
				String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
				
				// elimino duplicati
				if(!distinct || !lstTmp.contains(uriFromIDServizio)) {
					lstTmp.add(uriFromIDServizio);
					lista.add(idServizio);
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return lista;
	}
	
	@Override
	public int countConfigurazioneServiziFruizione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto,
			String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore,
			Integer versioneServizio, String nomeAzione, String val, Boolean searchTipo,
			PermessiUtenteOperatore permessiUtenteOperatore) {
		try {
			IExpression pdExpr = getExpressionPD(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoErogatore, nomeErogatore,	tipoServizio, nomeServizio, versioneServizio, nomeAzione, val, permessiUtenteOperatore);
			
			NonNegativeNumber nnn = this.portaDelegataDAO.count(pdExpr);

			if(nnn != null)
				return (int) nnn.longValue(); 
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return 0;
	}


	private IExpression getExpressionPD(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore, 
			String tipoServizio, String nomeServizio, Integer versioneServizio, 
			String nomeAzione, String val,
			PermessiUtenteOperatore permessiUtenteOperatore) throws ServiceException, NotImplementedException,
			CoreException, ExpressionNotImplementedException, ExpressionException, Exception {
		IExpression pdExpr = this.portaDelegataDAO.newExpression();

		if(permessiUtenteOperatore != null){
			IExpression permessi = permessiUtenteOperatore.toExpressionConfigurazioneServizi(this.portaDelegataDAO, 
				PortaDelegata.model().ID_SOGGETTO.TIPO, PortaDelegata.model().ID_SOGGETTO.NOME, 
				PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, PortaDelegata.model().NOME_SOGGETTO_EROGATORE, 
				PortaDelegata.model().TIPO_SERVIZIO, PortaDelegata.model().NOME_SERVIZIO, PortaDelegata.model().VERSIONE_SERVIZIO,
				false);
			pdExpr.and(permessi);
		}
		
		// Se ho selezionato un servizio, oppure se non ho selezionato niente 
		if(StringUtils.isNotEmpty(tipoSoggetto ) && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggetto))
			pdExpr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO, tipoSoggetto);
		if(StringUtils.isNotEmpty(nomeSoggetto ) && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggetto))
			pdExpr.and().equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggetto);
		if(StringUtils.isNotEmpty(val)) {
			pdExpr.ilike(PortaDelegata.model().ID_SOGGETTO.NOME, val.toLowerCase(), LikeMode.ANYWHERE);
		}
		
		if(StringUtils.isNotEmpty(nomeErogatore ))
			pdExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeErogatore).and();
		if(StringUtils.isNotEmpty(tipoErogatore ))
			pdExpr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoErogatore).and();

		if(StringUtils.isNotEmpty(tipoServizio ))
			pdExpr.equals(PortaDelegata.model().TIPO_SERVIZIO,		tipoServizio);
		if(StringUtils.isNotEmpty(nomeServizio ))
			pdExpr.and().equals(PortaDelegata.model().NOME_SERVIZIO, nomeServizio);
		if(versioneServizio != null)
			pdExpr.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versioneServizio).and();
		
		if(StringUtils.isNotEmpty(nomeAzione )){
			IExpression azioneExpr =  this.portaDelegataDAO.newExpression();
			azioneExpr.equals(PortaDelegata.model().NOME_AZIONE, nomeAzione).or().isNull(PortaDelegata.model().NOME_AZIONE); 
			pdExpr.and(azioneExpr);
		}
		
		if(tipoProtocollo!= null){
			pdExpr.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(this.portaDelegataDAO, PortaDelegata.model().ID_SOGGETTO.TIPO, tipoProtocollo));
		}
		
		pdExpr.notEquals(PortaDelegata.model().MODE_AZIONE, org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.DELEGATED_BY);
		return pdExpr;
	}
	
	@Override
	public PortaApplicativa getPortaApplicativa(String nomePorta) {
		log.debug("getPortaApplicativa [Nome: " + nomePorta + "]");
		PortaApplicativa portaApplicativa = null;
		try {
			IExpression expr = this.portaApplicativaDAO.newExpression();

			expr.equals(PortaApplicativa.model().NOME, nomePorta);
			
			portaApplicativa = this.portaApplicativaDAO.find(expr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return portaApplicativa;
	}
	
	@Override
	public PortaDelegata getPortaDelegata(String nomePorta) {
		log.debug("getPortaDelegata [Nome: " + nomePorta + "]");
		PortaDelegata portaDelegata = null;
		try {
			IExpression expr = this.portaDelegataDAO.newExpression();

			expr.equals(PortaDelegata.model().NOME, nomePorta);
			
			portaDelegata = this.portaDelegataDAO.find(expr);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return portaDelegata;
	}
	
	@Override
	public List<IDServizio> getServizi(String protocolloSelezionato, List<String> protocolliSupportati, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String tag) {
		log.debug("Get Servizi [Protocollo: " + protocolloSelezionato + "], [Protocolli supportati: " + protocolliSupportati 
				+ "], [TipoServizio: " + tipoServizio + "], [NomeServizio: " + nomeServizio + "], [VersioneServizio: " + versioneServizio + "], [Tag: " + tag + "]");

		try {
			
			if(protocolloSelezionato!=null) {
				return RegistroCore.getServizi((JDBCServiceManager) this.utilsServiceManager, protocolloSelezionato, tipoServizio, nomeServizio, versioneServizio, tag);
			}
			else{
				return RegistroCore.getServizi((JDBCServiceManager) this.utilsServiceManager, protocolliSupportati, tipoServizio, nomeServizio, versioneServizio, tag);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 

		return new ArrayList<IDServizio>();
	}

	@Override
	public MappingFruizionePortaDelegata getMappingFruizione(IDServizio idServizio, IDSoggetto idSoggetto, IDPortaDelegata idPortaDelegata) {
		log.debug("getMappingFruizione [idServizio:" + idServizio + ", idSoggetto:"+idSoggetto+", idPortaDelegata:"+idPortaDelegata+"]");
		try {
			return this.driverConfigDB.getMappingFruizione(idServizio, idSoggetto, idPortaDelegata);
		} catch (DriverConfigurazioneNotFound e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return null;
	}
	@Override
	public MappingErogazionePortaApplicativa getMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) {
		log.debug("MappingErogazionePortaApplicativa [idServizio:" + idServizio + ", idPortaApplicativa:"+idPortaApplicativa+"]");
		try {
			return this.driverConfigDB.getMappingErogazione(idServizio, idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return null;
	}
	
	@Override
	public Plugin getPlugin(IdPlugin idPlugin) {
		log.debug("getPlugin [tipoPlugin:" + idPlugin.getTipoPlugin()+", ClassName:" + idPlugin.getClassName()+", Tipo:" + idPlugin.getTipo()+", Label:" + idPlugin.getLabel()+"]");
		try {
			return this.pluginsServiceSearchDAO.get(idPlugin);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return null;
	}
	
	
	
	
	
	// *** Configurazione (Tracciamento) ***
	
	@Override
	public boolean existsFaseTracciamentoDBRequestIn(boolean erogazioni, boolean fruizioni) {
		log.debug("existsFaseTracciamentoDBRequestIn [erogazioni:" + erogazioni + ", fruizioni:"+fruizioni+"]");
		try {
			return this.driverConfigDB.existsFaseTracciamentoDBRequestIn(erogazioni, fruizioni);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return false;
	}
	@Override
	public boolean existsFaseTracciamentoDBRequestOut(boolean erogazioni, boolean fruizioni) {
		log.debug("existsFaseTracciamentoDBRequestOut [erogazioni:" + erogazioni + ", fruizioni:"+fruizioni+"]");
		try {
			return this.driverConfigDB.existsFaseTracciamentoDBRequestOut(erogazioni, fruizioni);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return false;
	}
	@Override
	public boolean existsFaseTracciamentoDBResponseOut(boolean erogazioni, boolean fruizioni) {
		log.debug("existsFaseTracciamentoDBResponseOut [erogazioni:" + erogazioni + ", fruizioni:"+fruizioni+"]");
		try {
			return this.driverConfigDB.existsFaseTracciamentoDBResponseOut(erogazioni, fruizioni);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return false;
	}
}
