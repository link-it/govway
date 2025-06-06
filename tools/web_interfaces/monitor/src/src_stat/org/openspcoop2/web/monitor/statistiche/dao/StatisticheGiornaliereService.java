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
package org.openspcoop2.web.monitor.statistiche.dao;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.core.statistiche.dao.IStatisticaGiornalieraServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaMensileServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaOrariaServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaSettimanaleServiceSearch;
import org.openspcoop2.core.statistiche.model.StatisticaContenutiModel;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.core.statistiche.utils.StatisticheUtils;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractCredenzialeList;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractSearchCredenziale;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchApi;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchGruppo;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchToken;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchTokenClient;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchTrasporto;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCUtilities;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.Index;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.condition.FilterImpl;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.monitor.engine.statistic.StatisticByResource;
import org.openspcoop2.monitor.engine.statistic.StatisticheMensili;
import org.openspcoop2.monitor.engine.statistic.StatisticheSettimanali;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.pdd.config.DynamicClusterManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.PDNDTokenInfoDetails;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.CredenzialiMittenteUtils;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsServiceEngine;
import org.openspcoop2.web.monitor.core.dao.MBeanUtilsService;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione3D;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione3DCustom;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.report.CostantiReport;
import org.openspcoop2.web.monitor.core.thread.ThreadExecutorManager;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.statistiche.bean.DimensioneCustom;
import org.openspcoop2.web.monitor.statistiche.bean.NumeroDimensioni;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerServizioBean;
import org.openspcoop2.web.monitor.statistiche.utils.ExportUtils;
import org.openspcoop2.web.monitor.statistiche.utils.StatsUtils;
import org.slf4j.Logger;

/**
 * StatisticheGiornaliereService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatisticheGiornaliereService implements IStatisticheGiornaliere {

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();
	private static void logError(String msg, Throwable e) {
		if(log!=null) {
			log.error(msg,e);
		}
	}
	private static void logDebug(String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	private static void logDebug(String msg, Throwable e) {
		if(log!=null) {
			log.debug(msg,e);
		}
	}
	
	private boolean timeoutEvent = false;
	private Integer timeoutRicerche = null;

	private static final String FALSA_UNION_DEFAULT_VALUE = "gbyfake";
	private static final Integer FALSA_UNION_DEFAULT_VALUE_INT = -99999;
	private static final Integer FALSA_UNION_DEFAULT_VALUE_VERSIONE = 1;
	private static Date FALSA_UNION_DEFAULT_VALUE_TIMESTAMP = null; 
	
	private static final String ALIAS_FIELD_DATA_3D = "data3d";
	private static final String ALIAS_FIELD_DATA_3D_CUSTOM = "c3d_";
	
	static {
		try {
			FALSA_UNION_DEFAULT_VALUE_TIMESTAMP = DateUtils.getSimpleDateFormatDay().parse("1970-01-01");
		} catch (ParseException e) {
			log.error("Errore durante la init della data fake ", e);
		}
	}

	private StatsSearchForm andamentoTemporaleSearch;
	private StatsSearchForm distribErroriSearch;
	private StatsSearchForm distribSoggettoSearch;
	private StatsSearchForm distribServizioSearch;
	private StatsSearchForm distribAzioneSearch;
	private StatsSearchForm distribSaSearch;
	private StatistichePersonalizzateSearchForm statistichePersonalizzateSearch;

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;
	
	private org.openspcoop2.core.plugins.dao.IServiceManager pluginsServiceManager;
	
	private org.openspcoop2.core.statistiche.dao.IServiceManager transazioniStatisticheServiceManager;
	
	private DriverRegistroServiziDB driverRegistroServiziDB;

	private DriverConfigurazioneDB driverConfigurazioneDB;

	private IStatisticaGiornalieraServiceSearch statGiornaliereSearchDAO;

	private IStatisticaMensileServiceSearch statMensileSearchDAO;

	private IStatisticaOrariaServiceSearch statOrariaSearchDAO;

	private IStatisticaSettimanaleServiceSearch statSettimanaleSearchDAO;
	
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager;
	private org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialiMittenteDAO;

	private PddMonitorProperties govwayMonitorProperties;
	
	private boolean useStatisticheGiornaliereCalcoloDistribuzioneSettimanale;
	private boolean useStatisticheGiornaliereCalcoloDistribuzioneMensile;
	private boolean isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere;
	
	private boolean clusterDinamico = false;
	private int clusterDinamicoRefresh;
	
	private IServiceSearchWithoutId<?> dao = null;
	
	private boolean isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato = false;
	
	public StatisticheGiornaliereService() {

		try {
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(StatisticheGiornaliereService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), StatisticheGiornaliereService.log);
			
			this.pluginsServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance(StatisticheGiornaliereService.log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(), StatisticheGiornaliereService.log);
			
			this.transazioniStatisticheServiceManager = (org.openspcoop2.core.statistiche.dao.IServiceManager) DAOFactory
					.getInstance(StatisticheGiornaliereService.log).getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),StatisticheGiornaliereService.log);

			this.statGiornaliereSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaGiornalieraServiceSearch();
			this.statMensileSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaMensileServiceSearch();
			this.statOrariaSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaOrariaServiceSearch();
			this.statSettimanaleSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaSettimanaleServiceSearch();
			
			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(StatisticheGiornaliereService.log).getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),StatisticheGiornaliereService.log);
			
			this.credenzialiMittenteDAO = this.transazioniServiceManager.getCredenzialeMittenteService();

			this.govwayMonitorProperties = PddMonitorProperties.getInstance(StatisticheGiornaliereService.log);
			
			this.useStatisticheGiornaliereCalcoloDistribuzioneSettimanale = this.govwayMonitorProperties.isUseStatisticheGiornaliereCalcoloDistribuzioneSettimanale();
			this.useStatisticheGiornaliereCalcoloDistribuzioneMensile = this.govwayMonitorProperties.isUseStatisticheGiornaliereCalcoloDistribuzioneMensile();
			this.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere = this.govwayMonitorProperties.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere();
			
			String datasourceJNDIName = DAOFactoryProperties.getInstance(StatisticheGiornaliereService.log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(StatisticheGiornaliereService.log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			String tipoDatabase = DAOFactoryProperties.getInstance(StatisticheGiornaliereService.log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			this.driverRegistroServiziDB = new DriverRegistroServiziDB(datasourceJNDIName,datasourceJNDIContext, StatisticheGiornaliereService.log, tipoDatabase);
			this.driverConfigurazioneDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, StatisticheGiornaliereService.log, tipoDatabase);
			
			this.clusterDinamico = this.govwayMonitorProperties.isClusterDinamico();
			if(this.clusterDinamico) {
				this.clusterDinamicoRefresh = this.govwayMonitorProperties.getClusterDinamicoRefresh();
			}
			
			this.timeoutRicerche = this.govwayMonitorProperties.getIntervalloTimeoutRicercaStatistiche();
			
			this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato = this.govwayMonitorProperties.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato();
			ExportUtils.setDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato(this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato);
			
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
	}

	public StatisticheGiornaliereService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, StatisticheGiornaliereService.log);
	}
	public StatisticheGiornaliereService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public StatisticheGiornaliereService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, StatisticheGiornaliereService.log);
	}
	public StatisticheGiornaliereService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log) {

		try {
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,log);
			
			this.pluginsServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,log);
			
			this.transazioniStatisticheServiceManager = (org.openspcoop2.core.statistiche.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,log);

			this.statGiornaliereSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaGiornalieraServiceSearch();
			this.statMensileSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaMensileServiceSearch();
			this.statOrariaSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaOrariaServiceSearch();
			this.statSettimanaleSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaSettimanaleServiceSearch();
			
			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),con,autoCommit,serviceManagerProperties,log);
			
			this.credenzialiMittenteDAO = this.transazioniServiceManager.getCredenzialeMittenteService();

			this.govwayMonitorProperties = PddMonitorProperties.getInstance(StatisticheGiornaliereService.log);
			
			this.useStatisticheGiornaliereCalcoloDistribuzioneSettimanale = this.govwayMonitorProperties.isUseStatisticheGiornaliereCalcoloDistribuzioneSettimanale();
			this.useStatisticheGiornaliereCalcoloDistribuzioneMensile = this.govwayMonitorProperties.isUseStatisticheGiornaliereCalcoloDistribuzioneMensile();
			this.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere = this.govwayMonitorProperties.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere();
			
			String tipoDatabase = (serviceManagerProperties!=null && serviceManagerProperties.getDatabaseType()!=null) ? serviceManagerProperties.getDatabaseType() : DAOFactoryProperties.getInstance(log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			this.driverRegistroServiziDB = new DriverRegistroServiziDB(con, log, tipoDatabase);
			this.driverConfigurazioneDB = new DriverConfigurazioneDB(con, log, tipoDatabase);
			
			this.clusterDinamico = this.govwayMonitorProperties.isClusterDinamico();
			if(this.clusterDinamico) {
				this.clusterDinamicoRefresh = this.govwayMonitorProperties.getClusterDinamicoRefresh();
			}
			
			this.timeoutRicerche = this.govwayMonitorProperties.getIntervalloTimeoutRicercaStatistiche();
			
			this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato = this.govwayMonitorProperties.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato();
			ExportUtils.setDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato(this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato);
			
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
	}
	
	public StatisticheGiornaliereService(Connection conConfig, Connection conStatistiche, Connection conTransazioni, boolean autoCommit){
		this(conConfig, conStatistiche, conTransazioni, autoCommit, null, StatisticheGiornaliereService.log);
	}
	public StatisticheGiornaliereService(Connection conConfig, Connection conStatistiche, Connection conTransazioni, boolean autoCommit, Logger log){
		this(conConfig, conStatistiche, conTransazioni, autoCommit, null, log);
	}
	public StatisticheGiornaliereService(Connection conConfig, Connection conStatistiche, Connection conTransazioni, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(conConfig, conStatistiche, conTransazioni, autoCommit, serviceManagerProperties, StatisticheGiornaliereService.log);
	}
	public StatisticheGiornaliereService(Connection conConfig, Connection conStatistiche, Connection conTransazioni, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log) {

		try {
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(),conConfig,autoCommit,serviceManagerProperties,log);
			
			this.pluginsServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),conConfig,autoCommit,serviceManagerProperties,log);
			
			this.transazioniStatisticheServiceManager = (org.openspcoop2.core.statistiche.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),conStatistiche,autoCommit,serviceManagerProperties,log);

			this.statGiornaliereSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaGiornalieraServiceSearch();
			this.statMensileSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaMensileServiceSearch();
			this.statOrariaSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaOrariaServiceSearch();
			this.statSettimanaleSearchDAO = this.transazioniStatisticheServiceManager.getStatisticaSettimanaleServiceSearch();
			
			this.transazioniServiceManager = 
					(org.openspcoop2.core.transazioni.dao.IServiceManager) DAOFactory
					.getInstance(log).getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(),conTransazioni,autoCommit,serviceManagerProperties,log);
			
			this.credenzialiMittenteDAO = this.transazioniServiceManager.getCredenzialeMittenteService();

			this.govwayMonitorProperties = PddMonitorProperties.getInstance(StatisticheGiornaliereService.log);
			
			this.useStatisticheGiornaliereCalcoloDistribuzioneSettimanale = this.govwayMonitorProperties.isUseStatisticheGiornaliereCalcoloDistribuzioneSettimanale();
			this.useStatisticheGiornaliereCalcoloDistribuzioneMensile = this.govwayMonitorProperties.isUseStatisticheGiornaliereCalcoloDistribuzioneMensile();
			this.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere = this.govwayMonitorProperties.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere();
			
			String tipoDatabase = (serviceManagerProperties!=null && serviceManagerProperties.getDatabaseType()!=null) ? serviceManagerProperties.getDatabaseType() : DAOFactoryProperties.getInstance(log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			this.driverRegistroServiziDB = new DriverRegistroServiziDB(conConfig, log, tipoDatabase);
			this.driverConfigurazioneDB = new DriverConfigurazioneDB(conConfig, log, tipoDatabase);
			
			this.clusterDinamico = this.govwayMonitorProperties.isClusterDinamico();
			if(this.clusterDinamico) {
				this.clusterDinamicoRefresh = this.govwayMonitorProperties.getClusterDinamicoRefresh();
			}
			
			this.timeoutRicerche = this.govwayMonitorProperties.getIntervalloTimeoutRicercaStatistiche();
			
			this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato = this.govwayMonitorProperties.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato();
			ExportUtils.setDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato(this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato);
			
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
	}
	
	public org.openspcoop2.core.commons.search.dao.IServiceManager getUtilsServiceManager() {
		return this.utilsServiceManager;
	}
	
	public org.openspcoop2.core.plugins.dao.IServiceManager getPluginsServiceManager() {
		return this.pluginsServiceManager;
	}
	
	public DriverRegistroServiziDB getDriverRegistroServiziDB() {
		return this.driverRegistroServiziDB;
	}

	public DriverConfigurazioneDB getDriverConfigurazioneDB() {
		return this.driverConfigurazioneDB;
	}
	
	public void setAndamentoTemporaleSearch(
			StatsSearchForm andamentoTemporaleSearch) {
		this.andamentoTemporaleSearch = andamentoTemporaleSearch;
	}

	public void setDistribErroriSearch(StatsSearchForm distribErroriSearch) {
		this.distribErroriSearch = distribErroriSearch;
	}
	
	public void setDistribSoggettoSearch(StatsSearchForm distribSoggettoSearch) {
		this.distribSoggettoSearch = distribSoggettoSearch;
	}

	public void setDistribServizioSearch(StatsSearchForm distribServizioSearch) {
		this.distribServizioSearch = distribServizioSearch;
	}
	
	public void setDistribAzioneSearch(StatsSearchForm distribAzioneSearch) {
		this.distribAzioneSearch = distribAzioneSearch;
	}
	
	public void setDistribSaSearch(StatsSearchForm distribSaSearch) {
		this.distribSaSearch = distribSaSearch;
	}

	public void setStatistichePersonalizzateSearch(
			StatistichePersonalizzateSearchForm statistichePersonalizzateSearch) {
		this.statistichePersonalizzateSearch = statistichePersonalizzateSearch;
	}
	
	public StatsSearchForm getDistribErroriSearch() {
		return this.distribErroriSearch;
	}
	
	public StatsSearchForm getDistribSoggettoSearch() {
		return this.distribSoggettoSearch;
	}

	public StatsSearchForm getDistribServizioSearch() {
		return this.distribServizioSearch;
	}

	public StatsSearchForm getDistribAzioneSearch() {
		return this.distribAzioneSearch;
	}

	public StatsSearchForm getDistribSaSearch() {
		return this.distribSaSearch;
	}

	@Override
	public List<ResBase> findAll(int start, int limit) {
		return null;
	}

	@Override
	public int totalCount() {
		return 0;
	}

	public void delete(StatisticaGiornaliera obj) throws Exception {
	}

	@Override
	public void deleteById(Integer key) {
	}

	@Override
	public List<ResBase> findAll() {
		return null;
	}

	public StatisticaGiornaliera findById(Long key) {
		return null;
	}

	public void store(StatisticaGiornaliera obj) throws Exception {
	}

	@Override
	public void delete(ResBase obj) throws Exception {
	}

	@Override
	public ResBase findById(Integer key) {
		return null;
	}

	@Override
	public void store(ResBase obj) throws Exception {
	}
	
	@Override
	public void deleteAll() throws Exception {

	}
	
	private List<Index> convertForceIndexList(IModel<?> model, List<String> l){
		if(l!=null && l.size()>0){
			List<Index> li = new ArrayList<Index>();
			for (String index : l) {
				li.add(new Index(model,index));
			}
			return li;
		}
		return null;
	}
	
	public StatisticType checkStatisticType(StatsSearchForm form) {
		
		boolean onlyForCount = true;
		boolean distribuzione3d = form.getNumeroDimensioni()!=null && 
				(NumeroDimensioni.DIMENSIONI_3.equals(form.getNumeroDimensioni()) || NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(form.getNumeroDimensioni()));
		if(distribuzione3d) {
			onlyForCount = false;
		}
		return StatsUtils.checkStatisticType(form, onlyForCount);
	}
	
	
	
	
	
	
	// ********** ANDAMENTO TEMPORALE (e DISTRUBUZIONE PER ESITI) ******************
	
	
	
	@Override
	public int countAllAndamentoTemporale() throws ServiceException {
		try {
			return (int) countAndamentoTemporale().longValue();
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
		return 0;
	}
	
	@Override
	public List<Res> findAllAndamentoTemporale(int start, int limit) throws ServiceException {
		List<Res> res = this.executeAndamentoTemporaleSearch(false, true, start, limit);
		return res;
	}

	@Override
	public List<Res> findAllAndamentoTemporale() throws ServiceException {
		List<Res> res = this.executeAndamentoTemporaleSearch(false, false, -1,-1);
		return res;
	}

	private NonNegativeNumber countAndamentoTemporale() throws ServiceException {
		try {
			StatisticType tipologiaSearch = this.andamentoTemporaleSearch.getModalitaTemporale();
//			IExpression gByExpr = null;

			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			boolean forceUseDistribGiornaliera = false;
			StatisticType tipologia = tipologiaSearch;
			StatisticheSettimanali statisticheSettimanaliUtils = null;
			StatisticheMensili statisticheMensiliUtils = null;
			
			switch (tipologiaSearch) {
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case SETTIMANALE:
				forceUseDistribGiornaliera = this.useStatisticheGiornaliereCalcoloDistribuzioneSettimanale;
				if(forceUseDistribGiornaliera) {
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = this.statGiornaliereSearchDAO;
					tipologia = StatisticType.GIORNALIERA;
					statisticheSettimanaliUtils = StatisticheSettimanali.getInstanceForUtils();	
				}
				else {
					model = StatisticaSettimanale.model().STATISTICA_BASE;
					dao = this.statSettimanaleSearchDAO;
				}
				break;
			case MENSILE:
				forceUseDistribGiornaliera = this.useStatisticheGiornaliereCalcoloDistribuzioneMensile;
				if(forceUseDistribGiornaliera) {
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = this.statGiornaliereSearchDAO;
					tipologia = StatisticType.GIORNALIERA;
					statisticheMensiliUtils = StatisticheMensili.getInstanceForUtils();
				}
				else {
					model = StatisticaMensile.model().STATISTICA_BASE;
					dao = this.statMensileSearchDAO;
				}
				break;
			}
			if(dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexAndamentoTemporaleCount(tipologia, 
										this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}

			IExpression gByExpr = dao.newExpression();
			gByExpr = createGenericAndamentoTemporaleExpression(gByExpr, dao, model,	true);
			TipoVisualizzazione tipoVisualizzazione = this.andamentoTemporaleSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:
			case NUMERO_TRANSAZIONI:
				break;

			case TEMPO_MEDIO_RISPOSTA:{
				List<TipoLatenza> tipiLatenza = this.andamentoTemporaleSearch.getTipiLatenzaImpostati();

				for (TipoLatenza tipoLatenza : tipiLatenza) {
					switch (tipoLatenza) {
					case LATENZA_PORTA:
						gByExpr.isNotNull(model.LATENZA_PORTA);
						break;
					case LATENZA_SERVIZIO:
						gByExpr.isNotNull(model.LATENZA_SERVIZIO);
						break;

					case LATENZA_TOTALE:
					default:
						gByExpr.isNotNull(model.LATENZA_TOTALE);
						break;
					}
				}
			}
			}

			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			if(forceUseDistribGiornaliera) {
				List<Map<String, Object>> list = null;
				try {
					FunctionField fCount = new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma"); // uso sempre e comunque la somma, non e' importante
					list = dao.groupBy(gByExpr, fCount);
				} catch (NotFoundException e) {
					StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente: "+e.getMessage(),e);
					return new NonNegativeNumber(0);
				}
				if(list==null || list.isEmpty()) {
					return new NonNegativeNumber(0);
				}
				List<Date> dateFound = new ArrayList<>();
				for (Map<String, Object> row : list) {
					Date data = (Date) row.get(GenericJDBCUtilities.getAlias(model.DATA));
					Date truncDate = null;
					if(statisticheSettimanaliUtils!=null) {
						truncDate = statisticheSettimanaliUtils.truncDate(data, false);
					}
					else {
						truncDate = statisticheMensiliUtils.truncDate(data, false);
					}
					boolean found = false;
					for (Date date : dateFound) {
						if(truncDate.equals(date)) {
							found = true;
							break;
						}
					}
					if(!found) {
						dateFound.add(truncDate);
					}
				}
				NonNegativeNumber nnn = new NonNegativeNumber(dateFound.size());
				return nnn; 
			}
			else {
				NonNegativeNumber nnn = dao.count(gByExpr);
				return nnn;
			}
			
		} catch (ServiceException | NotImplementedException | ExpressionNotImplementedException | ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} 
		return new NonNegativeNumber(0);
	}

	private List<Res> executeAndamentoTemporaleSearch(boolean isCount,	boolean isPaginated, int offset, int limit) {
		try {
			StatisticType tipologiaSearch = this.andamentoTemporaleSearch.getModalitaTemporale();
//			IExpression gByExpr = null;

			StatisticaModel model = null;
//			IServiceSearchWithoutId<?> dao = null;

			boolean forceUseDistribGiornaliera = false;
			StatisticType tipologia = tipologiaSearch;
			StatisticheSettimanali statisticheSettimanaliUtils = null;
			StatisticheMensili statisticheMensiliUtils = null;
			boolean calcolaSommeMediaPesata = false;
			
			switch (tipologiaSearch) {
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				this.dao = this.statOrariaSearchDAO;
				break;
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case SETTIMANALE:
				forceUseDistribGiornaliera = this.useStatisticheGiornaliereCalcoloDistribuzioneSettimanale;
				if(forceUseDistribGiornaliera) {
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					this.dao = this.statGiornaliereSearchDAO;
					tipologia = StatisticType.GIORNALIERA;
					statisticheSettimanaliUtils = StatisticheSettimanali.getInstanceForUtils();	
					calcolaSommeMediaPesata = this.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere;
				}
				else {
					model = StatisticaSettimanale.model().STATISTICA_BASE;
					this.dao = this.statSettimanaleSearchDAO;
				}
				break;
			case MENSILE:
				forceUseDistribGiornaliera = this.useStatisticheGiornaliereCalcoloDistribuzioneMensile;
				if(forceUseDistribGiornaliera) {
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					this.dao = this.statGiornaliereSearchDAO;
					tipologia = StatisticType.GIORNALIERA;
					statisticheMensiliUtils = StatisticheMensili.getInstanceForUtils();
					calcolaSommeMediaPesata = this.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere;
				}
				else {
					model = StatisticaMensile.model().STATISTICA_BASE;
					this.dao = this.statMensileSearchDAO;
				}
				break;
			}
			
			if(model==null) {
				throw new Exception("Model sconosciuta");
			}
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.dao).getFieldConverter(); 
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexAndamentoTemporaleGroupBy(tipologia, 
										this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = this.dao.newExpression();
			createGenericAndamentoTemporaleExpression(gByExpr, this.dao, model,	isCount);
			boolean isLatenza = false;	
			boolean isLatenza_totale = false;	
			boolean isLatenza_servizio = false;	
			boolean isLatenza_porta = false;	
			boolean isBanda = false;
			boolean isBanda_complessiva = false;
			boolean isBanda_interna = false;
			boolean isBanda_esterna = false;
			List<FunctionField> listaFunzioni = new ArrayList<FunctionField>();
			TipoVisualizzazione tipoVisualizzazione = this.andamentoTemporaleSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
				case DIMENSIONE_TRANSAZIONI:
					isBanda = true;
					if(this.andamentoTemporaleSearch.isAndamentoTemporalePerEsiti()){
						TipoBanda tipoBanda = this.andamentoTemporaleSearch.getTipoBanda();
						switch (tipoBanda) {
						case COMPLESSIVA:
							listaFunzioni.add(new  FunctionField(model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA, Function.SUM, "somma_banda_complessiva"));
							isBanda_complessiva = true;
							break;
						case INTERNA:
							listaFunzioni.add(new  FunctionField(model.DIMENSIONI_BYTES_BANDA_INTERNA, Function.SUM, "somma_banda_interna"));
							isBanda_interna = true;
							break;
						case ESTERNA:
						default:
							listaFunzioni.add(new  FunctionField(model.DIMENSIONI_BYTES_BANDA_ESTERNA, Function.SUM, "somma_banda_esterna"));
							isBanda_esterna = true;
							break;
						}
					}
					else{
						List<TipoBanda> tipiBanda = this.andamentoTemporaleSearch.getTipiBandaImpostati();
	
						for (TipoBanda tipoBanda : tipiBanda) {
							switch (tipoBanda) {
							case COMPLESSIVA:
								listaFunzioni.add(new  FunctionField(model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA, Function.SUM, "somma_banda_complessiva"));
								isBanda_complessiva = true;
								break;
							case INTERNA:
								listaFunzioni.add(new  FunctionField(model.DIMENSIONI_BYTES_BANDA_INTERNA, Function.SUM, "somma_banda_interna"));
								isBanda_interna = true;
								break;	
							case ESTERNA:
							default:
								listaFunzioni.add(new  FunctionField(model.DIMENSIONI_BYTES_BANDA_ESTERNA, Function.SUM, "somma_banda_esterna"));
								isBanda_esterna = true;
								break;
							}
						}
					}
					break;
	
				case NUMERO_TRANSAZIONI:
					listaFunzioni.add(new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma"));
					break;
	
				case TEMPO_MEDIO_RISPOSTA:{
					isLatenza = true;
					if(this.andamentoTemporaleSearch.isAndamentoTemporalePerEsiti()){
						TipoLatenza tipoLatenza = this.andamentoTemporaleSearch.getTipoLatenza();
						switch (tipoLatenza) {
						case LATENZA_PORTA:{
							gByExpr.isNotNull(model.LATENZA_PORTA);
							
							//listaFunzioni.add(new  FunctionField(model.LATENZA_PORTA, Function.AVG, "somma_latenza_porta"));
							listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "somma_latenza_porta"));
							
							isLatenza_porta = true;
							
							if(calcolaSommeMediaPesata) {
								// per media pesata
								listaFunzioni.add(new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma_media_pesata"));
							}
							
							break;
						}
						case LATENZA_SERVIZIO:{
							gByExpr.isNotNull(model.LATENZA_SERVIZIO);
							
							//listaFunzioni.add(new FunctionField(model.LATENZA_SERVIZIO, Function.AVG, "somma_latenza_servizio"));
							listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "somma_latenza_servizio"));
							
							isLatenza_servizio = true;
							
							if(calcolaSommeMediaPesata) {
								// per media pesata
								listaFunzioni.add(new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma_media_pesata"));
							}
							
							break;
						}
						case LATENZA_TOTALE:
						default:{
							gByExpr.isNotNull(model.LATENZA_TOTALE);
														
							//listaFunzioni.add(new  FunctionField(model.LATENZA_TOTALE, 	Function.AVG, "somma_latenza_totale"));
							listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "somma_latenza_totale"));
							
							isLatenza_totale = true;
							
							if(calcolaSommeMediaPesata) {
								// per media pesata
								listaFunzioni.add(new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma_media_pesata"));
							}
							
							break;
						}
						}
					}
					else{
						List<TipoLatenza> tipiLatenza = this.andamentoTemporaleSearch.getTipiLatenzaImpostati();
	
						for (TipoLatenza tipoLatenza : tipiLatenza) {
							switch (tipoLatenza) {
							case LATENZA_PORTA:
								gByExpr.isNotNull(model.LATENZA_PORTA);
								//listaFunzioni.add(new  FunctionField(model.LATENZA_PORTA, Function.AVG, "somma_latenza_porta"));
								listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "somma_latenza_porta"));
								isLatenza_porta = true;
								break;
							case LATENZA_SERVIZIO:
								gByExpr.isNotNull(model.LATENZA_SERVIZIO);
								//listaFunzioni.add(new FunctionField(model.LATENZA_SERVIZIO, Function.AVG, "somma_latenza_servizio"));
								listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "somma_latenza_servizio"));
								isLatenza_servizio = true;
								break;	
							case LATENZA_TOTALE:
							default:
								gByExpr.isNotNull(model.LATENZA_TOTALE);
								//listaFunzioni.add(new  FunctionField(model.LATENZA_TOTALE, 	Function.AVG, "somma_latenza_totale"));
								listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "somma_latenza_totale"));
								isLatenza_totale = true;
								break;
							}
						}
						
						if(calcolaSommeMediaPesata) {
							// per media pesata
							listaFunzioni.add(new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma_media_pesata"));
						}
					}
				}
			}

			List<Map<String, Object>> list = null;
			this.timeoutEvent = false;
			

			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			if (!isPaginated) {
				if(this.timeoutRicerche == null) {
					try{
						list = this.dao.groupBy(gByExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
					} catch (NotFoundException e) {
						StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente: "+e.getMessage(),e);
						list = new ArrayList<>(); // per evitare il nullPointer
					} 
				} else {
					try {
						list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.groupBy(gByExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						StatisticheGiornaliereService.logError(e.getMessage(), e);
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						if(e.getCause() instanceof NotFoundException) {
							StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente: "+e.getMessage(),e);
							list = new ArrayList<>(); // per evitare il nullPointer
						} else {
							if(e.getCause() instanceof ServiceException) {
								throw (ServiceException) e.getCause();
							}
							if(e.getCause() instanceof NotImplementedException) {
								throw (NotImplementedException) e.getCause();
							}
							StatisticheGiornaliereService.logError(e.getMessage(), e);
						}
					} catch (TimeoutException e) {
						this.timeoutEvent = true;
						list = new ArrayList<>(); // per evitare il nullPointer
						StatisticheGiornaliereService.logError(e.getMessage(), e);
					}
				}
			} else {
				IPaginatedExpression pagExpr = this.dao.toPaginatedExpression(gByExpr);

				if(!forceUseDistribGiornaliera) {
					pagExpr.offset(offset).limit(limit);
				}
				
				if(this.timeoutRicerche == null) {
					try{
						list = this.dao.groupBy(pagExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
					} catch (NotFoundException e) {
						StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente: "+e.getMessage(),e);
						list = new ArrayList<>(); // per evitare il nullPointer
					} 
				} else {
					try {
						list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.groupBy(pagExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						StatisticheGiornaliereService.logError(e.getMessage(), e);
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						if(e.getCause() instanceof NotFoundException) {
							StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente: "+e.getMessage(),e);
							list = new ArrayList<>(); // per evitare il nullPointer
						} else {
							if(e.getCause() instanceof ServiceException) {
								throw (ServiceException) e.getCause();
							}
							if(e.getCause() instanceof NotImplementedException) {
								throw (NotImplementedException) e.getCause();
							}
							StatisticheGiornaliereService.logError(e.getMessage(), e);
						}
					} catch (TimeoutException e) {
						this.timeoutEvent = true;
						list = new ArrayList<>(); // per evitare il nullPointer
						StatisticheGiornaliereService.logError(e.getMessage(), e);
					} 
				}
			}
			
			
			List<Res> res = new ArrayList<>();
			if(list!=null) {
				for (Map<String, Object> row : list) {
	
					Res r = new Res();
					Date data = (Date) row.get(GenericJDBCUtilities.getAlias(model.DATA));
					r.setId(data != null ? data.getTime() : null);
					r.setRisultato(data);
					List<Number> rSommaMediaPesata = new ArrayList<>();
					
					//collezione dei risultati
					if(isLatenza){
						Number obLT = StatsUtils.converToNumber(row.get("somma_latenza_totale"));
						Number obLS = StatsUtils.converToNumber(row.get("somma_latenza_servizio"));
						Number obLP = StatsUtils.converToNumber(row.get("somma_latenza_porta"));
						Number obSommaMediaPesata = null;
						if(calcolaSommeMediaPesata) {
							obSommaMediaPesata = StatsUtils.converToNumber(row.get("somma_media_pesata"));
						}
	
						if(obLT!=null){
							r.inserisciSomma(obLT);
							if(calcolaSommeMediaPesata) {
								rSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
							}
						}
						else{
							if(isLatenza_totale){
								r.inserisciSomma(0);
								if(calcolaSommeMediaPesata) {
									rSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
								}
							}
						}
	
						if(obLS!=null){
							r.inserisciSomma(obLS);
							if(calcolaSommeMediaPesata) {
								rSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
							}
						}
						else{
							if(isLatenza_servizio){
								r.inserisciSomma(0);
								if(calcolaSommeMediaPesata) {
									rSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
								}
							}
						}
	
						if(obLP!=null){
							r.inserisciSomma(obLP);
							if(calcolaSommeMediaPesata) {
								rSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
							}
						}
						else{
							if(isLatenza_porta){
								r.inserisciSomma(0);
								if(calcolaSommeMediaPesata) {
									rSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
								}
							}
						}
						
					}
					else if(isBanda){
						Number obComplessiva = StatsUtils.converToNumber(row.get("somma_banda_complessiva"));
						Number obInterna = StatsUtils.converToNumber(row.get("somma_banda_interna"));
						Number obEsterna = StatsUtils.converToNumber(row.get("somma_banda_esterna"));
	
						if(obComplessiva!=null){
							r.inserisciSomma(obComplessiva);
						}
						else{
							if(isBanda_complessiva){
								r.inserisciSomma(0);
							}
						}
	
						if(obInterna!=null){
							r.inserisciSomma(obInterna);
						}
						else{
							if(isBanda_interna){
								r.inserisciSomma(0);
							}
						}
	
						if(obEsterna!=null){
							r.inserisciSomma(obEsterna);
						}
						else{
							if(isBanda_esterna){
								r.inserisciSomma(0);
							}
						}
					}
					else{
						Number somma = StatsUtils.converToNumber(row.get("somma"));
						if(somma!=null){
							r.setSomma(somma);
						}
						else{
							r.setSomma(0);
						}
					}
	
					//System.out.println("MISURAZIONE NORMALE: \n"+r.toString());
	
					if(this.andamentoTemporaleSearch.isAndamentoTemporalePerEsiti()){
	
						//System.out.println(" CALCOLO ESITI ");
	
						// ************ ESITI ******************
						IExpression expOk = this.dao.newExpression();
						IExpression expKo = this.dao.newExpression();
						IExpression expFault = this.dao.newExpression();
						
						this.createGenericAndamentoTemporaleExpression(expOk, this.dao, model,	isCount,data,false);
						this.createGenericAndamentoTemporaleExpression(expKo, this.dao, model,	isCount,data,false);
						this.createGenericAndamentoTemporaleExpression(expFault, this.dao, model,	isCount,data,false);
						switch (tipoVisualizzazione) {
						case TEMPO_MEDIO_RISPOSTA:{
							TipoLatenza tipoLatenza = this.andamentoTemporaleSearch.getTipoLatenza();
							switch (tipoLatenza) {
							case LATENZA_PORTA:
								expOk.isNotNull(model.LATENZA_PORTA);
								expKo.isNotNull(model.LATENZA_PORTA);
								expFault.isNotNull(model.LATENZA_PORTA);
								break;
							case LATENZA_SERVIZIO:
								expOk.isNotNull(model.LATENZA_SERVIZIO);
								expKo.isNotNull(model.LATENZA_SERVIZIO);
								expFault.isNotNull(model.LATENZA_SERVIZIO);
								break;
							case LATENZA_TOTALE:
							default:
								expOk.isNotNull(model.LATENZA_TOTALE);
								expKo.isNotNull(model.LATENZA_TOTALE);
								expFault.isNotNull(model.LATENZA_TOTALE);
								break;
							}
							break;
						}
						default:
							break;
						}
						
						if(forceIndexes!=null && !forceIndexes.isEmpty()){
							for (Index index : forceIndexes) {
								expOk.addForceIndex(index);	
								expKo.addForceIndex(index);	
								expFault.addForceIndex(index);	
							}
						}
						
						EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(StatisticheGiornaliereService.log, this.andamentoTemporaleSearch.getProtocollo());
						List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
						List<Integer> esitiKo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
						List<Integer> esitiFault = esitiProperties.getEsitiCodeFaultApplicativo();
	
						expOk.and().in(model.ESITO, esitiOk);
						expKo.and().in(model.ESITO, esitiKo);
						expFault.and().in(model.ESITO, esitiFault);
	
						List<Map<String, Object>> listOk = null;
						Res rEsito = new Res();
						rEsito.setId(data.getTime());
						rEsito.setRisultato(data);
						List<Number> rEsitoSommaMediaPesata = new ArrayList<>();
						
						this.timeoutEvent = false;
						
						if(this.timeoutRicerche == null) {
							try{
							listOk = this.dao.groupBy(expOk, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
							} catch (NotFoundException e) {
								StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente con esiti Ok: "+esitiOk);
								//collezione dei risultati
								rEsito.inserisciSomma(0);
							} 
						} else {
							try {
								listOk =  ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> 
								this.dao.groupBy(expOk, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))
									).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
							} catch (InterruptedException e) {
								StatisticheGiornaliereService.logError(e.getMessage(), e);
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								if(e.getCause() instanceof NotFoundException) {
									StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente con esiti Ok: "+esitiOk);
									//collezione dei risultati
									rEsito.inserisciSomma(0);
								}else {
									if(e.getCause() instanceof ServiceException) {
										throw (ServiceException) e.getCause();
									}
									if(e.getCause() instanceof NotImplementedException) {
										throw (NotImplementedException) e.getCause();
									}
									StatisticheGiornaliereService.logError(e.getMessage(), e);
								}
							} catch (TimeoutException e) {
								this.timeoutEvent = true;
								//collezione dei risultati
								rEsito.inserisciSomma(0);
								StatisticheGiornaliereService.logError(e.getMessage(), e);
							}
						}
						
						if(listOk!=null && listOk.size()>0){
							if(listOk.size()>1){
								throw new Exception("Expected only one result, found: "+listOk.size());
							}
							Map<String, Object> rowOk = listOk.get(0);
							//					rEsito = new Res();
							//					Date dataOk = (Date) rowOk.get(JDBCUtilities.getAlias(model.DATA));
							//					rEsito.setId(dataOk != null ? dataOk.getTime() : null);
							//					rEsito.setRisultato(dataOk);
	
							//collezione dei risultati
							if(isLatenza){
								Number obLT = StatsUtils.converToNumber(rowOk.get("somma_latenza_totale"));
								Number obLS = StatsUtils.converToNumber(rowOk.get("somma_latenza_servizio"));
								Number obLP = StatsUtils.converToNumber(rowOk.get("somma_latenza_porta"));
								Number obSommaMediaPesata = null;
								if(calcolaSommeMediaPesata) {
									obSommaMediaPesata = StatsUtils.converToNumber(row.get("somma_media_pesata"));
								}
	
								if(obLT != null){
									rEsito.inserisciSomma(obLT);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_totale){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
								if(obLS != null){
									rEsito.inserisciSomma(obLS);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_servizio){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
								if(obLP != null){
									rEsito.inserisciSomma(obLP);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_porta){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
							}
							else if(isBanda){
								Number obComplessiva = StatsUtils.converToNumber(rowOk.get("somma_banda_complessiva"));
								Number obInterna = StatsUtils.converToNumber(rowOk.get("somma_banda_interna"));
								Number obEsterna = StatsUtils.converToNumber(rowOk.get("somma_banda_esterna"));
	
								if(obComplessiva != null){
									rEsito.inserisciSomma(obComplessiva);
								}
								else{
									if(isBanda_complessiva){
										rEsito.inserisciSomma(0);
									}
								}
	
								if(obInterna != null){
									rEsito.inserisciSomma(obInterna);
								}
								else{
									if(isBanda_interna){
										rEsito.inserisciSomma(0);
									}
								}
	
								if(obEsterna != null){
									rEsito.inserisciSomma(obEsterna);
								}
								else{
									if(isBanda_esterna){
										rEsito.inserisciSomma(0);
									}
								}
							}
							else{
								Number somma = StatsUtils.converToNumber(rowOk.get("somma"));
								if(somma!=null){
									rEsito.inserisciSomma(somma);
								}
								else{
									rEsito.inserisciSomma(0);
								}
							}
	
						}
						else {
							if(isLatenza && calcolaSommeMediaPesata) {
								rEsitoSommaMediaPesata.add(0);
							}
						}
						//System.out.println("MISURAZIONE OK: \n"+rEsito.toString());
	
						List<Map<String, Object>> listFaultApplicativo = null;
						
						this.timeoutEvent = false;
						
						if(this.timeoutRicerche == null) {
							try{
								listFaultApplicativo = this.dao.groupBy(expFault, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
							} catch (NotFoundException e) {
								StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente con esiti Fault: "+esitiFault);
								//collezione dei risultati
								rEsito.inserisciSomma(0);
							} 
						} else {
							try {
								listFaultApplicativo = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> 
								 this.dao.groupBy(expFault, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))
									).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
							} catch (InterruptedException e) {
								StatisticheGiornaliereService.logError(e.getMessage(), e);
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								if(e.getCause() instanceof NotFoundException) {
									StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente con esiti Fault: "+esitiFault);
									//collezione dei risultati
									rEsito.inserisciSomma(0);
								}else {
									if(e.getCause() instanceof ServiceException) {
										throw (ServiceException) e.getCause();
									}
									if(e.getCause() instanceof NotImplementedException) {
										throw (NotImplementedException) e.getCause();
									}
									StatisticheGiornaliereService.logError(e.getMessage(), e);
								}
							} catch (TimeoutException e) {
								this.timeoutEvent = true;
								//collezione dei risultati
								rEsito.inserisciSomma(0);
								StatisticheGiornaliereService.logError(e.getMessage(), e);
							}
						}
					
						if(listFaultApplicativo!=null && listFaultApplicativo.size()>0){
							if(listFaultApplicativo.size()>1){
								throw new Exception("Expected only one result, found: "+listFaultApplicativo.size());
							}
							Map<String, Object> rowFault = listFaultApplicativo.get(0);
	
							//collezione dei risultati
							if(isLatenza){
								Number obLT = StatsUtils.converToNumber(rowFault.get("somma_latenza_totale"));
								Number obLS = StatsUtils.converToNumber(rowFault.get("somma_latenza_servizio"));
								Number obLP = StatsUtils.converToNumber(rowFault.get("somma_latenza_porta"));
								Number obSommaMediaPesata = null;
								if(calcolaSommeMediaPesata) {
									obSommaMediaPesata = StatsUtils.converToNumber(row.get("somma_media_pesata"));
								}
	
								if(obLT != null){
									rEsito.inserisciSomma(obLT);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_totale){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
								if(obLS != null){
									rEsito.inserisciSomma(obLS);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_servizio){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
								if(obLP != null){
									rEsito.inserisciSomma(obLP);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_porta){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
								
							}
							else if(isBanda){
								Number obComplessiva = StatsUtils.converToNumber(rowFault.get("somma_banda_complessiva"));
								Number obInterna = StatsUtils.converToNumber(rowFault.get("somma_banda_interna"));
								Number obEsterna = StatsUtils.converToNumber(rowFault.get("somma_banda_esterna"));
	
								if(obComplessiva != null){
									rEsito.inserisciSomma(obComplessiva);
								}
								else{
									if(isBanda_complessiva){
										rEsito.inserisciSomma(0);
									}
								}
	
								if(obInterna != null){
									rEsito.inserisciSomma(obInterna);
								}
								else{
									if(isBanda_interna){
										rEsito.inserisciSomma(0);
									}
								}
	
								if(obEsterna != null){
									rEsito.inserisciSomma(obEsterna);
								}
								else{
									if(isBanda_esterna){
										rEsito.inserisciSomma(0);
									}
								}						
	
							}
							else{
								Number somma = StatsUtils.converToNumber(rowFault.get("somma"));
								if(somma!=null){
									rEsito.inserisciSomma(somma);
								}
								else{
									rEsito.inserisciSomma(0);
								}
							}
	
						}
						else {
							if(isLatenza && calcolaSommeMediaPesata) {
								rEsitoSommaMediaPesata.add(0);
							}
						}
						//System.out.println("MISURAZIONE FAULT: \n"+rEsito.toString());
	
						List<Map<String, Object>> listKo = null;
						
						this.timeoutEvent = false;
						
						if(this.timeoutRicerche == null) {
							try{
								listKo = this.dao.groupBy(expKo, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
							} catch (NotFoundException e) {
								StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente con esiti Ko: "+esitiKo);
								//collezione dei risultati
								rEsito.inserisciSomma(0);
							} 
						} else {
							try {
								listKo = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> 
								this.dao.groupBy(expKo, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))
									).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
							} catch (InterruptedException e) {
								StatisticheGiornaliereService.logError(e.getMessage(), e);
								Thread.currentThread().interrupt();
							} catch (ExecutionException e) {
								if(e.getCause() instanceof NotFoundException) {
									StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente con esiti Ko: "+esitiKo);
									//collezione dei risultati
									rEsito.inserisciSomma(0);
								}else {
									if(e.getCause() instanceof ServiceException) {
										throw (ServiceException) e.getCause();
									}
									if(e.getCause() instanceof NotImplementedException) {
										throw (NotImplementedException) e.getCause();
									}
									StatisticheGiornaliereService.logError(e.getMessage(), e);
								}
							} catch (TimeoutException e) {
								this.timeoutEvent = true;
								//collezione dei risultati
								rEsito.inserisciSomma(0);
								StatisticheGiornaliereService.logError(e.getMessage(), e);
							}
						}
						
						if(listKo!=null && listKo.size()>0){
							if(listKo.size()>1){
								throw new Exception("Expected only one result, found: "+listKo.size());
							}
							Map<String, Object> rowKo = listKo.get(0);
	
							//collezione dei risultati
							if(isLatenza){
								Number obLT = StatsUtils.converToNumber(rowKo.get("somma_latenza_totale"));
								Number obLS = StatsUtils.converToNumber(rowKo.get("somma_latenza_servizio"));
								Number obLP = StatsUtils.converToNumber(rowKo.get("somma_latenza_porta"));
								Number obSommaMediaPesata = null;
								if(calcolaSommeMediaPesata) {
									obSommaMediaPesata = StatsUtils.converToNumber(row.get("somma_media_pesata"));
								}
	
								if(obLT != null){
									rEsito.inserisciSomma(obLT);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_totale){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
								if(obLS != null){
									rEsito.inserisciSomma(obLS);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_servizio){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
								if(obLP != null){
									rEsito.inserisciSomma(obLP);
									if(calcolaSommeMediaPesata) {
										rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
									}
								}
								else{
									if(isLatenza_porta){
										rEsito.inserisciSomma(0);
										if(calcolaSommeMediaPesata) {
											rEsitoSommaMediaPesata.add((obSommaMediaPesata!=null) ? obSommaMediaPesata : 0);
										}
									}
								}
	
							}
							else if(isBanda){
								Number obComplessiva = StatsUtils.converToNumber(rowKo.get("somma_banda_complessiva"));
								Number obInterna = StatsUtils.converToNumber(rowKo.get("somma_banda_interna"));
								Number obEsterna = StatsUtils.converToNumber(rowKo.get("somma_banda_esterna"));
	
								if(obComplessiva != null){
									rEsito.inserisciSomma(obComplessiva);
								}
								else{
									if(isBanda_complessiva){
										rEsito.inserisciSomma(0);
									}
								}
	
								if(obInterna != null){
									rEsito.inserisciSomma(obInterna);
								}
								else{
									if(isBanda_interna){
										rEsito.inserisciSomma(0);
									}
								}
	
								if(obEsterna != null){
									rEsito.inserisciSomma(obEsterna);
								}
								else{
									if(isBanda_esterna){
										rEsito.inserisciSomma(0);
									}
								}
							}
							else{
								Number somma = StatsUtils.converToNumber(rowKo.get("somma"));
								if(somma!=null){
									rEsito.inserisciSomma(somma);
								}
								else{
									rEsito.inserisciSomma(0);
								}
							}
	
						}
						else {
							if(isLatenza && calcolaSommeMediaPesata) {
								rEsitoSommaMediaPesata.add(0);
							}
						}
						//System.out.println("MISURAZIONE KO: \n"+rEsito.toString());
	
	
						// ************ FINE ESITI ***********************
	
						//System.out.println("ESITI");
						if(forceUseDistribGiornaliera) {
						
							Date truncDate = null;
							if(statisticheSettimanaliUtils!=null) {
								truncDate = statisticheSettimanaliUtils.truncDate(rEsito.getRisultato(), false);
							}
							else {
								truncDate = statisticheMensiliUtils.truncDate(rEsito.getRisultato(), false);
							}
							
							elaboraIntervalloTemporale(truncDate, res, rEsito, isLatenza, rEsitoSommaMediaPesata);
							
						}
						else {
							res.add(rEsito);
						}
	
					}
					else{
	
						//System.out.println("NORMALE");
						if(forceUseDistribGiornaliera) {
							
							Date truncDate = null;
							if(statisticheSettimanaliUtils!=null) {
								truncDate = statisticheSettimanaliUtils.truncDate(r.getRisultato(), false);
							}
							else {
								truncDate = statisticheMensiliUtils.truncDate(r.getRisultato(), false);
							}
							
							elaboraIntervalloTemporale(truncDate, res, r, isLatenza, rSommaMediaPesata);
							
						}
						else {
							res.add(r);
						}
					}
	
				}
			}

			if(forceUseDistribGiornaliera) {
				
				if(offset<=0 && res.size()<=limit) {
					return res;
				}
				
				ArrayList<Res> resPaginated = new ArrayList<>();
				for (int i = 0; i < res.size(); i++) {
					if(i<offset) {
						continue;
					}
					resPaginated.add(res.get(i));
					if(resPaginated.size()==limit) {
						break;
					}
				}
				return resPaginated;
			}
			
			return res;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			if(e!=null && e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return new ArrayList<>();
	}

	private void elaboraIntervalloTemporale(Date truncDate, List<Res> res, Res r, boolean isLatenza, List<Number> sommaMediaPesata) throws Exception {

		boolean mediaPesata = this.isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere;
		
		int indexFound = -1;
		for (int i = 0; i < res.size(); i++) {
			Res resCheck = res.get(i);
			if(resCheck.getRisultato().equals(truncDate)) {
				indexFound = i;
				break;
			}
		}
		Res alreadyExistsRes = null;
		if(indexFound>=0) {
			alreadyExistsRes = res.remove(indexFound);
		}
		if(alreadyExistsRes==null) {
			// prima entry
			r.setId(truncDate != null ? truncDate.getTime() : null);
			r.setRisultato(truncDate);
			r.setSommeMediaPesata(sommaMediaPesata);
			res.add(r);
		}
		else {
			// devo calcolare la somma o la media con quello esistente
			
			List<Number> listaEsistente = alreadyExistsRes.getSomme();
			List<Number> sommeMediaPesataEsistente = null;
			List<Number> listaNuovo = r.getSomme();
			if(listaEsistente.size()!=listaNuovo.size()) {
				throw new Exception("La dimensione dei risultati è differente; esistente:"+listaEsistente.size()+", nuovo:"+listaNuovo.size());
			}
			if(isLatenza && mediaPesata) {
				if(sommaMediaPesata.size()!=listaNuovo.size()) {
					throw new Exception("La dimensione dei risultati rispetto alle somme per la media pesata è differente; sommeMediaPesata:"+sommaMediaPesata.size()+", nuovo:"+listaNuovo.size());
				}
				sommeMediaPesataEsistente = alreadyExistsRes.getSommeMediaPesata();
			}
			Res rEsitoRicalcolato = new Res();
			rEsitoRicalcolato.setId(truncDate.getTime());
			rEsitoRicalcolato.setRisultato(truncDate);
			List<Number> sommaMediaPesataRicalcolata = new ArrayList<>();
			for (int i = 0; i < listaEsistente.size(); i++) {
				Number nEsistente = listaEsistente.get(i);
				Number nNuovo = listaNuovo.get(i);
				if(isLatenza){
					if(mediaPesata) {
						Number nSommaMediaEsistente = sommeMediaPesataEsistente.get(i);
						Number nSommaMediaNuovo = sommaMediaPesata.get(i);
						
						StatisticheGiornaliereService.logDebug("Latenza già registrata: "+nEsistente+" per un totale di record "+nSommaMediaEsistente);
						StatisticheGiornaliereService.logDebug("Latenza nuova: "+nNuovo+" per un totale di record "+nSommaMediaNuovo);
						
						Number totale = nSommaMediaEsistente.longValue() + nSommaMediaNuovo.longValue();
						sommaMediaPesataRicalcolata.add(totale);
						StatisticheGiornaliereService.logDebug("TOT: "+totale);
						Number nMediaEsistente = nSommaMediaEsistente.longValue() * nEsistente.longValue();
						Number nMediaNuovo = nSommaMediaNuovo.longValue() * nNuovo.longValue();
						Number nMedia = nMediaEsistente.longValue() + nMediaNuovo.longValue();
						StatisticheGiornaliereService.logDebug("MEDIA: esi "+nMediaEsistente+" + nuovo "+nMediaNuovo+" = "+nMedia);
						Number nMediaPesata = (totale.longValue()>0) ? (nMedia.longValue() / totale.longValue()) : 0;
						StatisticheGiornaliereService.logDebug("MEDIA PESATA: "+nMediaPesata);
						//logDebug("MEDIA REALE: "+((nEsistente.longValue() + nNuovo.longValue())/2));
						rEsitoRicalcolato.inserisciSomma(nMediaPesata);
					}
					else {
						rEsitoRicalcolato.inserisciSomma((nEsistente.longValue() + nNuovo.longValue())/2);
					}
				}
				else {
					rEsitoRicalcolato.inserisciSomma(nEsistente.longValue() + nNuovo.longValue());
				}
			}
			if(isLatenza && mediaPesata) {
				rEsitoRicalcolato.setSommeMediaPesata(sommaMediaPesataRicalcolata);
			}
			res.add(rEsitoRicalcolato);
			
		}
		
	}
	
	private IExpression createGenericAndamentoTemporaleExpression(IExpression expr, IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			boolean isCount) {
		return this.createGenericAndamentoTemporaleExpression(expr, dao, model, isCount, null, true);
	}
	private IExpression createGenericAndamentoTemporaleExpression(IExpression expr, IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			boolean isCount, Date date, boolean setEsito) {

//		IExpression expr = null;

		StatisticheGiornaliereService.log
		.debug("creo  Expression per Andamento Temporale!");

		try {

			List<Soggetto> listaSoggettiGestione = this.andamentoTemporaleSearch
					.getSoggettiGestione();

//			expr = dao.newExpression();

			// Data
			if(date==null){
				expr.between(model.DATA, this.andamentoTemporaleSearch.getDataInizio(),	this.andamentoTemporaleSearch.getDataFine());
			}
			else{
				expr.equals(model.DATA,date);
			}
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
			if (this.andamentoTemporaleSearch.isSetFiltroProtocollo()) {
				protocollo = this.andamentoTemporaleSearch.getProtocollo();
				impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo, this.andamentoTemporaleSearch.getTipologiaRicercaEnum());
			}

			// permessi utente operatore
			if(this.andamentoTemporaleSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.andamentoTemporaleSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO,model.VERSIONE_SERVIZIO);
				expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.andamentoTemporaleSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.andamentoTemporaleSearch.getSoggettoLocale()) && 
					!"--".equals(this.andamentoTemporaleSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.andamentoTemporaleSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.andamentoTemporaleSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.andamentoTemporaleSearch
					.getNomeAzione()))
				expr.and().equals(model.AZIONE,
						this.andamentoTemporaleSearch.getNomeAzione());

			// nome servizio
			if (StringUtils.isNotBlank(this.andamentoTemporaleSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.andamentoTemporaleSearch.getNomeServizio());
				
				expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			} 

			// esito
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.andamentoTemporaleSearch.getProtocollo());
			if(setEsito){
				esitoUtils.setExpression(expr, this.andamentoTemporaleSearch.getEsitoGruppo(), 
						this.andamentoTemporaleSearch.getEsitoDettaglio(),
						this.andamentoTemporaleSearch.getEsitoDettaglioPersonalizzato(),
						this.andamentoTemporaleSearch.getEsitoContesto(),
						this.andamentoTemporaleSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO, 
						dao.newExpression());
			}
			else{
				esitoUtils.setExpressionContesto(expr, model.ESITO_CONTESTO, this.andamentoTemporaleSearch.getEsitoContesto());
			}

			// ho 3 diversi tipi di query in base alla tipologia di ricerca

			// imposto il soggetto (loggato) come mittente o destinatario in
			// base
			// alla tipologia di ricerca selezionata
			if (this.andamentoTemporaleSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.andamentoTemporaleSearch.getTipologiaRicercaEnum())) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome
				// del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils.isNotBlank(this.andamentoTemporaleSearch.getTrafficoPerSoggetto());
				boolean soggetto = !listaSoggettiGestione.isEmpty();
				String tipoTrafficoSoggetto = null;
				String nomeTrafficoSoggetto = null;
				if (trafficoSoggetto) {
					tipoTrafficoSoggetto = this.andamentoTemporaleSearch
							.getTipoTrafficoPerSoggetto();
					nomeTrafficoSoggetto = this.andamentoTemporaleSearch
							.getTrafficoPerSoggetto();
				}

				IExpression e1 = dao.newExpression();
				IExpression e2 = dao.newExpression();

				// se trafficoSoggetto e soggetto sono impostati allora devo
				// fare la
				// OR
				if (trafficoSoggetto && soggetto) {
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					e1.and().equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO, nomeTrafficoSoggetto);

					e2.and().equals(model.TIPO_MITTENTE, tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE, nomeTrafficoSoggetto);

					// OR
					expr.or(e1, e2);
				} else if (trafficoSoggetto && !soggetto) {
					// il mio soggetto non e' stato impostato (soggetto in
					// gestione,
					// puo succedero solo in caso admin)
					expr.and();

					e1.equals(model.TIPO_DESTINATARIO, tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO, nomeTrafficoSoggetto);

					e2.equals(model.TIPO_MITTENTE, tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE, nomeTrafficoSoggetto);
					// OR
					expr.or(e1, e2);
				} else if (!trafficoSoggetto && soggetto) {
					// e' impostato solo il soggetto in gestione
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					// OR
					expr.or(e1, e2);
				} else {
					// nessun filtro da impostare
				}

			} else if (TipologiaRicerca.ingresso.equals(this.andamentoTemporaleSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				expr.and().equals(model.TIPO_PORTA, "applicativa");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.andamentoTemporaleSearch);
				if (StringUtils.isNotBlank(this.andamentoTemporaleSearch.getNomeMittente()) && !ignoreSetMittente) {
					expr.and().equals(model.TIPO_MITTENTE,
							this.andamentoTemporaleSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,
							this.andamentoTemporaleSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().equals(model.TIPO_PORTA, "delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.andamentoTemporaleSearch
						.getNomeDestinatario())) {
					expr.and().equals(model.TIPO_DESTINATARIO,
							this.andamentoTemporaleSearch.getTipoDestinatario());
					expr.and().equals(model.DESTINATARIO,
							this.andamentoTemporaleSearch.getNomeDestinatario());
				}
			}
			
			this.impostaFiltroDatiMittente(expr, this.andamentoTemporaleSearch, model, isCount);
			
			this.impostaFiltroGruppo(expr, this.andamentoTemporaleSearch, model, isCount);
			
			this.impostaFiltroApi(expr, this.andamentoTemporaleSearch, model, isCount);
			
			this.impostaFiltroIdClusterOrCanale(expr, this.andamentoTemporaleSearch, model, isCount);

			if(date==null){
				// ORDER BY
				SortOrder s = 	this.andamentoTemporaleSearch.getSortOrder() != null ? 	this.andamentoTemporaleSearch.getSortOrder() : SortOrder.ASC;

				if (!isCount) {
					expr.sortOrder(s).addOrder(model.DATA);

				}
			}

			expr.addGroupBy(model.DATA);

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return expr;
	}

	
	
	
	
	
	// ********** ESITI LIVE ******************
	
	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,	String periodo, String esitoContesto,
			String protocolloSelected, String protocolloDefault, TipologiaRicerca tipologiaRicerca) {

		// StringBuilder pezzoIdPorta = new StringBuilder();
		StatisticheGiornaliereService.logDebug("Get Esiti [id porta: " + permessiUtente + "],[ Date Min: " + min + "], [Date Max: " + max + "]");
		try {
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;
			StatisticType tipologia = null;
			
			boolean forceUseDistribGiornaliera = false;
			
			if(periodo.equals(CostantiReport.ULTIMO_ANNO)){
				forceUseDistribGiornaliera = this.useStatisticheGiornaliereCalcoloDistribuzioneMensile;
				if(forceUseDistribGiornaliera) {
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = this.statGiornaliereSearchDAO;
					tipologia = StatisticType.GIORNALIERA;
				}
				else {
					model = StatisticaMensile.model().STATISTICA_BASE;
					dao = this.statMensileSearchDAO;
					tipologia = StatisticType.MENSILE;
				}
			} else if(periodo.equals(CostantiReport.ULTIMI_30_GIORNI)){
				forceUseDistribGiornaliera = this.useStatisticheGiornaliereCalcoloDistribuzioneSettimanale;
				if(forceUseDistribGiornaliera) {
					model = StatisticaGiornaliera.model().STATISTICA_BASE;
					dao = this.statGiornaliereSearchDAO;
					tipologia = StatisticType.GIORNALIERA;
				}
				else {
					model = StatisticaSettimanale.model().STATISTICA_BASE;
					dao = this.statSettimanaleSearchDAO;
					tipologia = StatisticType.SETTIMANALE;
				}
			} else if(periodo.equals(CostantiReport.ULTIMI_7_GIORNI)){
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				tipologia = StatisticType.GIORNALIERA;
			} else{//24h
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				tipologia = StatisticType.ORARIA;
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexEsitiLiveGroupBy(tipologia, 
										this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}			

			String protocolloP = protocolloDefault;
			if(protocolloSelected!=null) {
				protocolloP = protocolloSelected;
			}
			
			EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(StatisticheGiornaliereService.log, protocolloP);
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, protocolloP);
			List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			List<Integer> esitiKo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
			List<Integer> esitiFault = esitiProperties.getEsitiCodeFaultApplicativo();

			// ok 
			IExpression exprOk =  dao.newExpression();
			exprOk.between(model.DATA, min,max);
			// Record validi
			StatisticheUtils.selezionaRecordValidi(exprOk, model);
			exprOk.and().in(model.ESITO, esitiOk);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO, model.DESTINATARIO, 
						model.TIPO_SERVIZIO, model.SERVIZIO, model.VERSIONE_SERVIZIO);
				exprOk.and(permessi);
			}
			esitoUtils.setExpressionContesto(exprOk, model.ESITO_CONTESTO, esitoContesto);
			if(tipologiaRicerca!=null) {
				if (TipologiaRicerca.ingresso.equals(tipologiaRicerca)) {
					exprOk.and().equals(model.TIPO_PORTA, "applicativa");
				}
				else if (TipologiaRicerca.uscita.equals(tipologiaRicerca)) {
					exprOk.and().equals(model.TIPO_PORTA, "delegata");
				}
			}
			exprOk.addGroupBy(model.DATA);

			// fault
			IExpression exprFault =  dao.newExpression();
			exprFault.between(model.DATA, min,max);
			// Record validi
			StatisticheUtils.selezionaRecordValidi(exprFault, model);
			exprFault.and().in(model.ESITO, esitiFault);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO, model.DESTINATARIO, 
						model.TIPO_SERVIZIO, model.SERVIZIO, model.VERSIONE_SERVIZIO);
				exprFault.and(permessi);
			}
			esitoUtils.setExpressionContesto(exprFault, model.ESITO_CONTESTO, esitoContesto);
			if(tipologiaRicerca!=null) {
				if (TipologiaRicerca.ingresso.equals(tipologiaRicerca)) {
					exprFault.and().equals(model.TIPO_PORTA, "applicativa");
				}
				else if (TipologiaRicerca.uscita.equals(tipologiaRicerca)) {
					exprFault.and().equals(model.TIPO_PORTA, "delegata");
				}
			}
			exprFault.addGroupBy(model.DATA);

			// ko
			IExpression exprKo =  dao.newExpression();
			exprKo.between(model.DATA, min,max);
			// Record validi
			StatisticheUtils.selezionaRecordValidi(exprKo, model);
			exprKo.and().in(model.ESITO, esitiKo);
			if (permessiUtente != null) {
				IExpression permessi = permessiUtente.toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO, model.DESTINATARIO, 
						model.TIPO_SERVIZIO, model.SERVIZIO, model.VERSIONE_SERVIZIO);
				exprKo.and(permessi);
			}
			esitoUtils.setExpressionContesto(exprKo, model.ESITO_CONTESTO, esitoContesto);
			if(tipologiaRicerca!=null) {
				if (TipologiaRicerca.ingresso.equals(tipologiaRicerca)) {
					exprKo.and().equals(model.TIPO_PORTA, "applicativa");
				}
				else if (TipologiaRicerca.uscita.equals(tipologiaRicerca)) {
					exprKo.and().equals(model.TIPO_PORTA, "delegata");
				}
			}
			exprKo.addGroupBy(model.DATA);

			if(protocolloSelected!=null) {
				impostaTipiCompatibiliConProtocollo(dao, model, exprOk, protocolloSelected, null);
				impostaTipiCompatibiliConProtocollo(dao, model, exprFault, protocolloSelected, null);
				impostaTipiCompatibiliConProtocollo(dao, model, exprKo, protocolloSelected, null);
			}
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					exprOk.addForceIndex(index);	
					exprKo.addForceIndex(index);	
					exprFault.addForceIndex(index);	
				}
			}
			
			
			Long numOk = 0L, numFault = 0L, numKo = 0L;

			FunctionField fSum = new FunctionField(model.NUMERO_TRANSAZIONI,Function.SUM, "somma");

			// ok
			List<Map<String, Object>> list = null;
			try {
				list = dao.groupBy(exprOk,fSum);
			} catch (NotFoundException e) {
				StatisticheGiornaliereService.logDebug("Non sono presenti statistiche con esito OK");
			}
			long s = 0l;
			if(list != null && list.size() > 0){
				for (Map<String, Object> row : list) {

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						s = s + somma.longValue();
					}

				}
			}
			numOk = Long.valueOf(s);

			// fault
			list = new ArrayList<>();
			try {
				list = dao.groupBy(exprFault,fSum);
			} catch (NotFoundException e) {
				StatisticheGiornaliereService.logDebug("Non sono presenti statistiche con esito Fault");
			}
			s = 0l;
			if(list != null && list.size() > 0){
				for (Map<String, Object> row : list) {

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						s = s + somma.longValue();
					}

				}
			}
			numFault = Long.valueOf(s);

			// ko
			list = new ArrayList<>();
			try {
				list = dao.groupBy(exprKo,fSum);
			} catch (NotFoundException e) {
				StatisticheGiornaliereService.logDebug("Non sono presenti statistiche con esito KO");
			}
			s = 0l;
			if(list != null && list.size() > 0){
				for (Map<String, Object> row : list) {

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						s = s + somma.longValue();
					}

				}
			}
			numKo = Long.valueOf(s);

			return new ResLive(numOk.longValue(), numFault.longValue(), numKo.longValue(), new Date());

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return new ResLive(Long.valueOf("0"), Long.valueOf("0"), Long.valueOf("0"));
	}

	
	
	

	
		
	// ********** DISTRIBUZIONE PER ERRORI ******************
	
	@Override
	public int countAllDistribuzioneErrori() throws ServiceException {
		try {
			StatisticType tipologia = checkStatisticType(this.distribErroriSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneErroriCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = createDistribuzioneErroriExpression(dao, model, true);
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber nnn = dao.count(gByExpr);

			return nnn != null ? ((int)nnn.longValue()) : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e.getMessage(),e);
		}
	}
	@Override
	public List<ResDistribuzione> findAllDistribuzioneErrori() throws ServiceException {
		return this.executeDistribuzioneErrori(null, null);
	}
	@Override
	public List<ResDistribuzione> findAllDistribuzioneErrori(int start, int limit) throws ServiceException {
		return this.executeDistribuzioneErrori(start, limit);
	}

	private IExpression createDistribuzioneErroriExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, boolean isCount) throws ServiceException {
		IExpression expr = null;

		StatisticheGiornaliereService.logDebug("creo Expression per distribuzione Errori!");

		List<Soggetto> listaSoggettiGestione = this.distribErroriSearch.getSoggettiGestione();

		try {

			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribErroriSearch.getProtocollo());

			expr = dao.newExpression();
			// Data
			expr.between(model.DATA,
					this.distribErroriSearch.getDataInizio(),
					this.distribErroriSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
//				if (StringUtils.isNotEmpty(this.distribErroriSearch.getProtocollo()) && this.distribErroriSearch.isShowListaProtocolli()) {
			if (this.distribErroriSearch.isSetFiltroProtocollo()) {
				//				expr.and().equals(model.PROTOCOLLO,	this.distribErroriSearch.getProtocollo());
				protocollo = this.distribErroriSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo, this.distribErroriSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.distribErroriSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribErroriSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribErroriSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribErroriSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribErroriSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribErroriSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribErroriSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				expr.and().equals(model.ID_PORTA, idPorta);
			}

			// esito
			int esitoGruppo = EsitoUtils.ALL_ERROR_VALUE;
			if(this.distribErroriSearch.getEsitoGruppo()!=null) {
				int esito = this.distribErroriSearch.getEsitoGruppo();
				if(EsitoUtils.ALL_VALUE == esitoGruppo) {
					// non compatibile con questa distribuzione
				}
				else if(EsitoUtils.ALL_OK_VALUE == esitoGruppo) {
					// non compatibile con questa distribuzione
				}
				else {
					esitoGruppo = esito;
				}
			}
			
			esitoUtils.setExpression(expr, esitoGruppo, 
					this.distribErroriSearch.getEsitoDettaglio(),
					this.distribErroriSearch.getEsitoDettaglioPersonalizzato(),
					this.distribErroriSearch.getEsitoContesto(),
					this.distribErroriSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());

			// ho 3 diversi tipi di query in base alla tipologia di ricerca

			// imposto il soggetto (loggato) come mittente o destinatario in
			// base
			// alla tipologia di ricerca selezionata
			if (this.distribErroriSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribErroriSearch.getTipologiaRicercaEnum())) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome
				// del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils
						.isNotBlank(this.distribErroriSearch
								.getTrafficoPerSoggetto());
				boolean soggetto = !listaSoggettiGestione.isEmpty();
				String tipoTrafficoSoggetto = null;
				String nomeTrafficoSoggetto = null;
				if (trafficoSoggetto) {
					tipoTrafficoSoggetto = this.distribErroriSearch
							.getTipoTrafficoPerSoggetto();
					nomeTrafficoSoggetto = this.distribErroriSearch
							.getTrafficoPerSoggetto();
				}

				IExpression e1 = dao.newExpression();
				IExpression e2 = dao.newExpression();

				// se trafficoSoggetto e soggetto sono impostati allora devo
				// fare la
				// OR
				if (trafficoSoggetto && soggetto) {
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					e1.and().equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.and().equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);

					// OR
					expr.or(e1, e2);
				} else if (trafficoSoggetto && !soggetto) {
					// il mio soggetto non e' stato impostato (soggetto in
					// gestione,
					// puo succedero solo in caso admin)
					expr.and();

					e1.equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);
					// OR
					expr.or(e1, e2);
				} else if (!trafficoSoggetto && soggetto) {
					// e' impostato solo il soggetto in gestione
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					// OR
					expr.or(e1, e2);
				} else {
					// nessun filtro da impostare
				}

			} else if (TipologiaRicerca.ingresso.equals(this.distribErroriSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.distribErroriSearch);
				if (StringUtils.isNotBlank(this.distribErroriSearch.getNomeMittente()) && !ignoreSetMittente) {
					expr.and().equals(model.TIPO_MITTENTE,
							this.distribErroriSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,
							this.distribErroriSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"applicativa");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribErroriSearch
						.getNomeDestinatario())) {
					expr.and().equals(model.TIPO_DESTINATARIO,
							this.distribErroriSearch.getTipoDestinatario());
					expr.and().equals(model.DESTINATARIO,
							this.distribErroriSearch.getNomeDestinatario());
				}
			}

			// azione
			if (StringUtils.isNotBlank(this.distribErroriSearch
					.getNomeAzione()))
				expr.and().equals(model.AZIONE,
						this.distribSoggettoSearch.getNomeAzione());
			
			// nome servizio  e tipo
			if (StringUtils.isNotBlank(this.distribErroriSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribErroriSearch.getNomeServizio());
				
				expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO, idServizio.getVersione()); 

			}
			
			this.impostaFiltroDatiMittente(expr, this.distribErroriSearch, model, isCount); 
			
			this.impostaFiltroGruppo(expr, this.distribErroriSearch, model, isCount);
			
			this.impostaFiltroApi(expr, this.distribErroriSearch, model, isCount);

			this.impostaFiltroIdClusterOrCanale(expr, this.distribErroriSearch, model, isCount);
			
			// raggruppamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				expr.addGroupBy(model.DATA);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				impostaGroupByFiltro3dCustom(expr, model, this.distribErroriSearch, this.distribErroriSearch.getNumeroDimensioniCustom());
			}
			
			expr.addGroupBy(model.ESITO);

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return expr;
	}

	private List<ResDistribuzione> executeDistribuzioneErrori(Integer start, Integer limit) throws ServiceException {
		try {
			StatisticType tipologia = checkStatisticType(this.distribErroriSearch);
			StatisticaModel model = null;
//			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				this.dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				this.dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				this.dao = this.statSettimanaleSearchDAO;
				break;
			}
			
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.dao).getFieldConverter(); 
			
			IExpression gByExpr = this.createDistribuzioneErroriExpression(this.dao,	model, false);

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribErroriSearch.getSortOrder() != null ? 	this.distribErroriSearch.getSortOrder() : SortOrder.ASC;
				gByExpr.sortOrder(s).addOrder(model.DATA);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribErroriSearch.getSortOrder() != null ? 	this.distribErroriSearch.getSortOrder() : SortOrder.ASC;
				impostaSortOrder3dCustom(gByExpr, model, this.distribErroriSearch, this.distribErroriSearch.getNumeroDimensioniCustom(), s);
			}
			
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.ESITO);

			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneErroriGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			
			List<String> aliases3dCustom = new ArrayList<>();
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				unionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(unionExpr, model, this.distribErroriSearch, this.distribErroriSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			String aliasFieldEsito = "esito";
			
			unionExpr.addSelectField(model.ESITO, aliasFieldEsito);

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = this.dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
						model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustomFake(unionExprFake, model, this.distribErroriSearch, this.distribErroriSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			unionExprFake.addSelectField(new ConstantField(aliasFieldEsito, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_INT,
					model.ESITO.getFieldType()), aliasFieldEsito);
			
			Union union = new Union();
			union.setUnionAll(true);
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			union.addField(aliasFieldEsito);
			// group by field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			union.addGroupBy(aliasFieldEsito);
			
			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribErroriSearch.getSortOrder() != null ? 	this.distribErroriSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribErroriSearch.getSortOrder() != null ? 	this.distribErroriSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}

			TipoVisualizzazione tipoVisualizzazione = this.distribErroriSearch.getTipoVisualizzazione();
			String sommaAliasName = "somma";
			String datoParamAliasName = "dato";
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribErroriSearch.getTipoBanda();

				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);

				switch (tipoBanda) {
				case COMPLESSIVA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, datoParamAliasName));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, datoParamAliasName));
					break;
				case INTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, datoParamAliasName));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, datoParamAliasName));
					break;
				case ESTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, datoParamAliasName));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, datoParamAliasName));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);
				unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni",
						Integer.valueOf(0), model.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, datoParamAliasName));

				unionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						datoParamAliasName));
				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.distribErroriSearch.getTipoLatenza();

				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				//union.addField(sommaAliasName, Function.AVG, datoParamAliasName);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					gByExpr.isNotNull(model.LATENZA_PORTA);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
//							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, datoParamAliasName));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, datoParamAliasName));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta", 
							Integer.valueOf(0), model.LATENZA_PORTA.getFieldType()), Function.SUM, datoParamAliasName));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					gByExpr.isNotNull(model.LATENZA_SERVIZIO);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
//							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, datoParamAliasName));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, datoParamAliasName));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(0), model.LATENZA_SERVIZIO.getFieldType()), Function.SUM, datoParamAliasName));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					gByExpr.isNotNull(model.LATENZA_TOTALE);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
//							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, datoParamAliasName));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, datoParamAliasName));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
							Integer.valueOf(0), model.LATENZA_TOTALE.getFieldType()), Function.SUM, datoParamAliasName));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<>();

			if(start != null)
				union.setOffset(start);
			if(start != null)
				union.setLimit(limit);

			this.timeoutEvent = false;
			
			List<Map<String, Object>> list = null;
			if(this.timeoutRicerche == null) {
				list = this.dao.union(union, unionExpr, unionExprFake);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.union(union, unionExpr, unionExprFake)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				EsitiProperties esitiProperties = null;
				try {
					esitiProperties = EsitiProperties.getInstanceFromProtocolName(StatisticheGiornaliereService.log, this.distribErroriSearch.getProtocollo());
				}catch(Exception t) {
					StatisticheGiornaliereService.logError("EsitiProperties reader non disponibile: "+t.getMessage(), t);
				}
				if(esitiProperties==null) {
					throw new ServiceException("EsitiProperties unavailable");
				}
								
				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {
					
					int esito = ((Integer) row.get(aliasFieldEsito));
					if(esito == StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_INT) {
						continue;
					} 	
					
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribErroriSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					} 
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribErroriSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribErroriSearch.getNumeroDimensioniCustom(), this.distribErroriSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("Esito '"+esito+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione esito("+esito+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					} 
					else {
						r = new ResDistribuzione();
					}
					
					try {
						r.setRisultato(esitiProperties.getEsitoLabel(esito));
					}catch(Exception t) {
						r.setRisultato("Esito '"+esito+"'");
						StatisticheGiornaliereService.logError("Traduzione esito '"+esito+"' non riuscita: "+t.getMessage(), t);
					}
					
					try {
						r.getParentMap().put("0",esitiProperties.getEsitoDescription(esito));
					}catch(Exception t) {
						r.getParentMap().put("0","");
						StatisticheGiornaliereService.logError("Traduzione esito '"+esito+"' in descrizione non riuscita: "+t.getMessage(), t);
					}
						
					Number somma = StatsUtils.converToNumber(row.get(sommaAliasName));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					res.add(r);
				}

			}

			return res;

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		}
		return new ArrayList<>();
	}
	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER SOGGETTO ******************

	@Override
	public int countAllDistribuzioneSoggetto() throws ServiceException{

		try {

			StatisticType tipologia = checkStatisticType(this.distribSoggettoSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			Long countValue = this.countDistribuzioneSoggetto( dao,model);
			return countValue != null ? countValue.intValue() : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		}

		//		return 0;
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneSoggetto() throws ServiceException{
		try {

			StatisticType tipologia = checkStatisticType(this.distribSoggettoSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(dao==null) {
				throw new ServiceException("DAO is null");
			}
			if(model==null) {
				throw new ServiceException("Model is null");
			}
			return this.executeDistribuzioneSoggetto( dao,model, false, -1, -1);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneSoggetto(int start,int limit)  throws ServiceException{
		try {

			StatisticType tipologia = checkStatisticType(this.distribSoggettoSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(dao==null) {
				throw new ServiceException("DAO is null");
			}
			if(model==null) {
				throw new ServiceException("Model is null");
			}
			return this.executeDistribuzioneSoggetto(dao,model, true, start, limit);

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return new ArrayList<>();
	}

	private Long countDistribuzioneSoggetto(IServiceSearchWithoutId<?> dao, StatisticaModel model) throws ServiceException {

		StatisticheGiornaliereService.log
		.debug("creo  Expression per distribuzione Soggetto!");

		List<Soggetto> listaSoggettiGestione = this.distribSoggettoSearch
				.getSoggettiGestione();
		try {
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribSoggettoSearch.getProtocollo());
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneSoggettoCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			// ho 3 diversi tipi di query in base alla tipologia di ricerca
			if (this.distribSoggettoSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribSoggettoSearch.getTipologiaRicercaEnum())) {
				// erogazione/fruizione

				// EROGAZIONE
				IExpression erogazione_portaApplicativa_Expr = dao.newExpression();

				//tipo porta
				erogazione_portaApplicativa_Expr.equals(model.TIPO_PORTA,
						"applicativa");

				// Data
				erogazione_portaApplicativa_Expr.and().between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());
				
				// Record validi
				StatisticheUtils.selezionaRecordValidi(erogazione_portaApplicativa_Expr, model);

				// Protocollo
				String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				// protocollo e' impostato anche scegliendo la modalita'
//				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
				if (this.distribSoggettoSearch.isSetFiltroProtocollo()) {
					//					mitExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, erogazione_portaApplicativa_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					erogazione_portaApplicativa_Expr.and(permessi);
				}

				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					erogazione_portaApplicativa_Expr.and().equals(model.ID_PORTA, idPorta);
				}
				
				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					erogazione_portaApplicativa_Expr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					erogazione_portaApplicativa_Expr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					erogazione_portaApplicativa_Expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					erogazione_portaApplicativa_Expr.or(orSoggetti);
				}
				
				if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					// il mittente puo nn essere specificato
					if (StringUtils.isNotBlank(this.distribSoggettoSearch.getTrafficoPerSoggetto())) {
						erogazione_portaApplicativa_Expr.and().equals(	model.TIPO_MITTENTE,	this.distribSoggettoSearch.getTipoTrafficoPerSoggetto());
						erogazione_portaApplicativa_Expr.and().equals(	model.MITTENTE, this.distribSoggettoSearch.getTrafficoPerSoggetto());
					}
				}

				// FRUIZIONE
				IExpression fruizione_portaDelegata_Expr = dao.newExpression();

				fruizione_portaDelegata_Expr.equals(model.TIPO_PORTA,
						"delegata");

				// Data
				fruizione_portaDelegata_Expr.and().between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());
				
				// Record validi
				StatisticheUtils.selezionaRecordValidi(fruizione_portaDelegata_Expr, model);

				// Protocollo
				//String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
					//					destExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, fruizione_portaDelegata_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					fruizione_portaDelegata_Expr.and(permessi);
				}
				
				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					fruizione_portaDelegata_Expr.and().equals(model.ID_PORTA, idPorta);
				}

				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					fruizione_portaDelegata_Expr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					fruizione_portaDelegata_Expr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(fruizione_portaDelegata_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					fruizione_portaDelegata_Expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					fruizione_portaDelegata_Expr.or(orSoggetti);
				}
				
				if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					// il destinatario puo nn essere specificato
					if (StringUtils.isNotBlank(this.distribSoggettoSearch.getTrafficoPerSoggetto())) {
						fruizione_portaDelegata_Expr.and().equals(	model.TIPO_DESTINATARIO,	this.distribSoggettoSearch.getTipoTrafficoPerSoggetto());
						fruizione_portaDelegata_Expr.and().equals(	model.DESTINATARIO, this.distribSoggettoSearch.getTrafficoPerSoggetto());
					}
				}
				
				// filtro dati m
				this.impostaFiltroDatiMittente(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				this.impostaFiltroDatiMittente(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroGruppo(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				this.impostaFiltroGruppo(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroApi(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				this.impostaFiltroApi(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroIdClusterOrCanale(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				this.impostaFiltroIdClusterOrCanale(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);

				// UNION

				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					
					// raggruppamento per data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						erogazione_portaApplicativa_Expr.addGroupBy(model.DATA);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						impostaGroupByFiltro3dCustom(erogazione_portaApplicativa_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
					}
					erogazione_portaApplicativa_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_MITTENTE);
					erogazione_portaApplicativa_Expr.addGroupBy(model.MITTENTE);

					// raggruppamento per data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						fruizione_portaDelegata_Expr.addGroupBy(model.DATA);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						impostaGroupByFiltro3dCustom(fruizione_portaDelegata_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
					}
					fruizione_portaDelegata_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_DESTINATARIO);
					fruizione_portaDelegata_Expr.addGroupBy(model.DESTINATARIO);
				}
				else{
					// raggruppamento per data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						erogazione_portaApplicativa_Expr.addGroupBy(model.DATA);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						impostaGroupByFiltro3dCustom(erogazione_portaApplicativa_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
					}
					erogazione_portaApplicativa_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_DESTINATARIO);
					erogazione_portaApplicativa_Expr.addGroupBy(model.DESTINATARIO);

					// raggruppamento per data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						fruizione_portaDelegata_Expr.addGroupBy(model.DATA);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						impostaGroupByFiltro3dCustom(fruizione_portaDelegata_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
					}
					fruizione_portaDelegata_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_MITTENTE);
					fruizione_portaDelegata_Expr.addGroupBy(model.MITTENTE);
				}

				if(forceIndexes!=null && !forceIndexes.isEmpty()){
					for (Index index : forceIndexes) {
						erogazione_portaApplicativa_Expr.addForceIndex(index);	
						fruizione_portaDelegata_Expr.addForceIndex(index);
					}
				}
								
				List<String> aliases3dCustom = new ArrayList<>();
				UnionExpression erogazione_portaApplicativa_UnionExpr = new UnionExpression(erogazione_portaApplicativa_Expr);
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustom(erogazione_portaApplicativa_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
				}
				
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.TIPO_MITTENTE,
							"tipo_soggetto");
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.MITTENTE,
							"soggetto");
				}
				else{
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.TIPO_DESTINATARIO,
							"tipo_soggetto");
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.DESTINATARIO,
							"soggetto");
				}

				UnionExpression fruizione_portaDelegata_UnionExpr = new UnionExpression(fruizione_portaDelegata_Expr);
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					fruizione_portaDelegata_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustom(fruizione_portaDelegata_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
				}		
				
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					fruizione_portaDelegata_UnionExpr.addSelectField(
							model.TIPO_DESTINATARIO, "tipo_soggetto");
					fruizione_portaDelegata_UnionExpr.addSelectField(model.DESTINATARIO,
							"soggetto");
				}
				else{
					fruizione_portaDelegata_UnionExpr.addSelectField(
							model.TIPO_MITTENTE, "tipo_soggetto");
					fruizione_portaDelegata_UnionExpr.addSelectField(model.MITTENTE,
							"soggetto");
				}

				Union union = new Union();
				union.setUnionAll(true);
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					union.addField(ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					for (String a3dCustom : aliases3dCustom) {
						union.addField(a3dCustom);
					}
				}
				union.addField("tipo_soggetto");
				union.addField("soggetto");
				
				// group by field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					union.addGroupBy(ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					for (String a3dCustom : aliases3dCustom) {
						union.addGroupBy(a3dCustom);
					}
				}
				union.addGroupBy("tipo_soggetto");
				union.addGroupBy("soggetto");

				NonNegativeNumber nnn = dao.unionCount(union, erogazione_portaApplicativa_UnionExpr, fruizione_portaDelegata_UnionExpr); 
				return nnn != null ? nnn.longValue() : 0L;

			} else if (TipologiaRicerca.ingresso.equals(this.distribSoggettoSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				// il destinatario e' l'utente loggato (sempre presente se non
				// sono
				// admin)

				// EROGAZIONE
				IExpression erogazione_portaApplicativa_Expr = dao.newExpression();

				// Data
				erogazione_portaApplicativa_Expr.between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());
				
				// Record validi
				StatisticheUtils.selezionaRecordValidi(erogazione_portaApplicativa_Expr, model);

				// Protocollo
				String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
					//					mitExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, erogazione_portaApplicativa_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				erogazione_portaApplicativa_Expr.and().equals(model.TIPO_PORTA,
						"applicativa");

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					erogazione_portaApplicativa_Expr.and(permessi);
				}
				
				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					erogazione_portaApplicativa_Expr.and().equals(model.ID_PORTA, idPorta);
				}

				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					erogazione_portaApplicativa_Expr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					erogazione_portaApplicativa_Expr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					erogazione_portaApplicativa_Expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					erogazione_portaApplicativa_Expr.or(orSoggetti);
				}
				
				if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					
					// il mittente puo nn essere specificato
					boolean ignoreSetMittente = isIgnoreSetMittente(this.distribSoggettoSearch);
					if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeMittente()) && !ignoreSetMittente) {
						erogazione_portaApplicativa_Expr.and().equals(	model.TIPO_MITTENTE,	this.distribSoggettoSearch.getTipoMittente());
						erogazione_portaApplicativa_Expr.and().equals(	model.MITTENTE, this.distribSoggettoSearch.getNomeMittente());
					}
				}

				// filtro dati m
				this.impostaFiltroDatiMittente(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroGruppo(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroApi(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroIdClusterOrCanale(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
				
				// raggruppamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					erogazione_portaApplicativa_Expr.addGroupBy(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaGroupByFiltro3dCustom(erogazione_portaApplicativa_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
				}
				
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					erogazione_portaApplicativa_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_MITTENTE);
					erogazione_portaApplicativa_Expr.addGroupBy(model.MITTENTE);
				}
				else{
					erogazione_portaApplicativa_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_DESTINATARIO);
					erogazione_portaApplicativa_Expr.addGroupBy(model.DESTINATARIO);
				}

				if(forceIndexes!=null && !forceIndexes.isEmpty()){
					for (Index index : forceIndexes) {
						erogazione_portaApplicativa_Expr.addForceIndex(index);		
					}
				}
				
				UnionExpression erogazione_portaApplicativa_UnionExpr = new UnionExpression(erogazione_portaApplicativa_Expr);
				List<String> aliases3dCustom = new ArrayList<>();
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustom(erogazione_portaApplicativa_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom );
				}	
				
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					erogazione_portaApplicativa_UnionExpr.addSelectField(
							model.TIPO_MITTENTE, "tipo_soggetto");
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.MITTENTE,
							"soggetto");
				}
				else{
					erogazione_portaApplicativa_UnionExpr.addSelectField(
							model.TIPO_DESTINATARIO, "tipo_soggetto");
					erogazione_portaApplicativa_UnionExpr.addSelectField(model.DESTINATARIO,
							"soggetto");
				}

				// Espressione finta per usare l'ordinamento
				IExpression fakeExpr = dao.newExpression();
				UnionExpression unionExprFake = new UnionExpression(fakeExpr);
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
							model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustomFake(unionExprFake, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
				}
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_MITTENTE.getFieldType()), "tipo_soggetto");
					unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.MITTENTE.getFieldType()), "soggetto");
				}
				else{
					unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_DESTINATARIO.getFieldType()), "tipo_soggetto");
					unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.DESTINATARIO.getFieldType()), "soggetto");
				}
				
				Union union = new Union();
				union.setUnionAll(true);
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					union.addField(ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					for (String a3dCustom : aliases3dCustom) {
						union.addField(a3dCustom);
					}
				}
				union.addField("tipo_soggetto");
				union.addField("soggetto");
				
				// group by field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					union.addGroupBy(ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					for (String a3dCustom : aliases3dCustom) {
						union.addGroupBy(a3dCustom);
					}
				}
				union.addGroupBy("tipo_soggetto");
				union.addGroupBy("soggetto");

				NonNegativeNumber nnn = dao.unionCount(union, erogazione_portaApplicativa_UnionExpr, unionExprFake); 
				return nnn != null ? nnn.longValue() - 1 : 0L;
			} else {
				// FRUIZIONE
				// il mittente e' l'utente loggato (sempre presente)

				// FRUIZIONE
				IExpression fruizione_portaDelegata_Expr = dao.newExpression();

				// Data
				fruizione_portaDelegata_Expr.between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());
				
				// Record validi
				StatisticheUtils.selezionaRecordValidi(fruizione_portaDelegata_Expr, model);

				// Protocollo
				String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
					//					destExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, fruizione_portaDelegata_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				fruizione_portaDelegata_Expr.and().equals(model.TIPO_PORTA,
						"delegata");

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					fruizione_portaDelegata_Expr.and(permessi);
				}
				
				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					fruizione_portaDelegata_Expr.and().equals(model.ID_PORTA, idPorta);
				}

				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					fruizione_portaDelegata_Expr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					fruizione_portaDelegata_Expr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(fruizione_portaDelegata_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					fruizione_portaDelegata_Expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					fruizione_portaDelegata_Expr.or(orSoggetti);
				}
				
				if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					// il destinatario puo nn essere specificato
					if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeDestinatario())) {
						fruizione_portaDelegata_Expr.and().equals(	model.TIPO_DESTINATARIO,	this.distribSoggettoSearch.getTipoDestinatario());
						fruizione_portaDelegata_Expr.and().equals(	model.DESTINATARIO, this.distribSoggettoSearch.getNomeDestinatario());
					}
				}

				// filtro dati m
				this.impostaFiltroDatiMittente(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroGruppo(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
				
				this.impostaFiltroApi(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);

				this.impostaFiltroIdClusterOrCanale(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
				
				// raggruppamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					fruizione_portaDelegata_Expr.addGroupBy(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaGroupByFiltro3dCustom(fruizione_portaDelegata_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
				}
				
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					fruizione_portaDelegata_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_DESTINATARIO);
					fruizione_portaDelegata_Expr.addGroupBy(model.DESTINATARIO);
				}
				else{
					fruizione_portaDelegata_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_MITTENTE);
					fruizione_portaDelegata_Expr.addGroupBy(model.MITTENTE);
				}

				if(forceIndexes!=null && !forceIndexes.isEmpty()){
					for (Index index : forceIndexes) {
						fruizione_portaDelegata_Expr.addForceIndex(index);	
					}
				}
				
				UnionExpression fruizione_portaDelegata_UnionExpr = new UnionExpression(fruizione_portaDelegata_Expr);
				List<String> aliases3dCustom = new ArrayList<>();
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					fruizione_portaDelegata_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustom(fruizione_portaDelegata_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom  );
				}
				
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					fruizione_portaDelegata_UnionExpr.addSelectField(
							model.TIPO_DESTINATARIO,
							"tipo_soggetto");
					fruizione_portaDelegata_UnionExpr.addSelectField(
							model.DESTINATARIO, "soggetto");
				}
				else{
					fruizione_portaDelegata_UnionExpr.addSelectField(
							model.TIPO_MITTENTE,
							"tipo_soggetto");
					fruizione_portaDelegata_UnionExpr.addSelectField(
							model.MITTENTE, "soggetto");
				}

				// Espressione finta per usare l'ordinamento
				IExpression fakeExpr = dao.newExpression();
				UnionExpression unionExprFake = new UnionExpression(fakeExpr);
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
							model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustomFake(unionExprFake, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
				}
				if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
					unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_DESTINATARIO.getFieldType()), "tipo_soggetto");
					unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.DESTINATARIO.getFieldType()), "soggetto");
				}
				else{
					unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_MITTENTE.getFieldType()), "tipo_soggetto");
					unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.MITTENTE.getFieldType()), "soggetto");
				}
				
				Union union = new Union();
				union.setUnionAll(true);
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					union.addField(ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					for (String a3dCustom : aliases3dCustom) {
						union.addField(a3dCustom);
					}
				}
				union.addField("tipo_soggetto");
				union.addField("soggetto");
				
				// group by field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					union.addGroupBy(ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					for (String a3dCustom : aliases3dCustom) {
						union.addGroupBy(a3dCustom);
					}
				}
				union.addGroupBy("tipo_soggetto");
				union.addGroupBy("soggetto");

				NonNegativeNumber nnn =  dao.unionCount(union, fruizione_portaDelegata_UnionExpr, unionExprFake); 
				return nnn != null ? nnn.longValue() - 1 : 0L;

				//				return dao.count(destExpr);
			}
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
		return 0L;
	}

	private List<ResDistribuzione> executeDistribuzioneSoggetto(
			IServiceSearchWithoutId<?> dao,
			StatisticaModel model, boolean isPaginated, int start,
			int limit) throws ExpressionNotImplementedException,
			ExpressionException, ServiceException, NotImplementedException,
			CoreException, NotFoundException, ProtocolException, UserInvalidException, UtilsException {

		StatisticType tipologia = checkStatisticType(this.distribSoggettoSearch);
		
		List<Index> forceIndexes = null;
		try{
			forceIndexes = 
					this.convertForceIndexList(model, 
							this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneSoggettoGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		
		ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)dao).getFieldConverter(); 
		
		List<Map<String, Object>> list = null;
		ArrayList<ResDistribuzione> res = new ArrayList<>();
		StatisticheGiornaliereService.log
		.debug("creo Expression per distribuzione Soggetto!");

		EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribSoggettoSearch.getProtocollo());
		
		List<Soggetto> listaSoggettiGestione = this.distribSoggettoSearch
				.getSoggettiGestione();
		// ho 3 diversi tipi di query in base alla tipologia di ricerca
		if (this.distribSoggettoSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribSoggettoSearch.getTipologiaRicercaEnum())) {
			// erogazione/fruizione

			// EROGAZIONE
			IExpression erogazione_portaApplicativa_Expr = dao.newExpression();

			//tipo porta
			erogazione_portaApplicativa_Expr.equals(model.TIPO_PORTA,
					"applicativa");

			// Data
			erogazione_portaApplicativa_Expr.and().between(model.DATA,
					this.distribSoggettoSearch.getDataInizio(),
					this.distribSoggettoSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(erogazione_portaApplicativa_Expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
			//			if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
			if (this.distribSoggettoSearch.isSetFiltroProtocollo()) {
				//				mitExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
				protocollo = this.distribSoggettoSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, erogazione_portaApplicativa_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				erogazione_portaApplicativa_Expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				erogazione_portaApplicativa_Expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.distribSoggettoSearch
					.getNomeAzione()))
				erogazione_portaApplicativa_Expr.and().equals(model.AZIONE,
						this.distribSoggettoSearch.getNomeAzione());

			// nome servizio
			if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
				
				erogazione_portaApplicativa_Expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			}

			// esito
			esitoUtils.setExpression(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
					this.distribSoggettoSearch.getEsitoDettaglio(),
					this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
					this.distribSoggettoSearch.getEsitoContesto(),
					this.distribSoggettoSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// il mittente e' l'utente loggato (sempre presente se non
			// sn admin)
			if (!listaSoggettiGestione.isEmpty()) {
				erogazione_portaApplicativa_Expr.and();

				IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
				                                           .size()];
				int i = 0;
				for (Soggetto soggetto : listaSoggettiGestione) {
					IExpression se = dao.newExpression();
					se.equals(model.TIPO_DESTINATARIO,
							soggetto.getTipoSoggetto());
					se.and().equals(model.DESTINATARIO,
							soggetto.getNomeSoggetto());
					orSoggetti[i] = se;
					i++;
				}
				erogazione_portaApplicativa_Expr.or(orSoggetti);
			}

			if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				// il mittente puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getTrafficoPerSoggetto())) {
					erogazione_portaApplicativa_Expr.and().equals(	model.TIPO_MITTENTE,	this.distribSoggettoSearch.getTipoTrafficoPerSoggetto());
					erogazione_portaApplicativa_Expr.and().equals(	model.MITTENTE, this.distribSoggettoSearch.getTrafficoPerSoggetto());
				}
			}

			// FRUIZIONE
			IExpression fruizione_portaDelegata_Expr = dao.newExpression();


			fruizione_portaDelegata_Expr.equals(model.TIPO_PORTA, "delegata");
			// Data
			fruizione_portaDelegata_Expr.and().between(model.DATA,
					this.distribSoggettoSearch.getDataInizio(),
					this.distribSoggettoSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(fruizione_portaDelegata_Expr, model);

			// Protocollo
			//			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
				//				destExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
				protocollo = this.distribSoggettoSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, fruizione_portaDelegata_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				fruizione_portaDelegata_Expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				fruizione_portaDelegata_Expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.distribSoggettoSearch
					.getNomeAzione()))
				fruizione_portaDelegata_Expr.and().equals(model.AZIONE,
						this.distribSoggettoSearch.getNomeAzione());

			// nome servizio
			if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
				
				fruizione_portaDelegata_Expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			}

			// esito
			esitoUtils.setExpression(fruizione_portaDelegata_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
					this.distribSoggettoSearch.getEsitoDettaglio(),
					this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
					this.distribSoggettoSearch.getEsitoContesto(),
					this.distribSoggettoSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// il mittente e' l'utente loggato (sempre presente se non
			// sn admin)
			if (!listaSoggettiGestione.isEmpty()) {
				fruizione_portaDelegata_Expr.and();

				IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
				                                           .size()];
				int i = 0;
				for (Soggetto soggetto : listaSoggettiGestione) {
					IExpression se = dao.newExpression();
					se.equals(model.TIPO_MITTENTE,
							soggetto.getTipoSoggetto());
					se.and().equals(model.MITTENTE,
							soggetto.getNomeSoggetto());
					orSoggetti[i] = se;
					i++;
				}
				fruizione_portaDelegata_Expr.or(orSoggetti);
			}

			if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getTrafficoPerSoggetto())) {
					fruizione_portaDelegata_Expr.and().equals(	model.TIPO_DESTINATARIO,	this.distribSoggettoSearch.getTipoTrafficoPerSoggetto());
					fruizione_portaDelegata_Expr.and().equals(	model.DESTINATARIO, this.distribSoggettoSearch.getTrafficoPerSoggetto());
				}
			}
			
			// filtro dati m
			this.impostaFiltroDatiMittente(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			this.impostaFiltroDatiMittente(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroGruppo(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			this.impostaFiltroGruppo(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroApi(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			this.impostaFiltroApi(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroIdClusterOrCanale(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			this.impostaFiltroIdClusterOrCanale(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);

			// UNION

			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				
				// raggruppamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					erogazione_portaApplicativa_Expr.addGroupBy(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaGroupByFiltro3dCustom(erogazione_portaApplicativa_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
				}
				
				erogazione_portaApplicativa_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_MITTENTE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.MITTENTE);

				// raggruppamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					fruizione_portaDelegata_Expr.addGroupBy(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaGroupByFiltro3dCustom(fruizione_portaDelegata_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
				}
				
				fruizione_portaDelegata_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_DESTINATARIO);
				fruizione_portaDelegata_Expr.addGroupBy(model.DESTINATARIO);
			}
			else{
				
				// raggruppamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					erogazione_portaApplicativa_Expr.addGroupBy(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaGroupByFiltro3dCustom(erogazione_portaApplicativa_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
				}
				
				erogazione_portaApplicativa_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_DESTINATARIO);
				erogazione_portaApplicativa_Expr.addGroupBy(model.DESTINATARIO);

				// raggruppamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					fruizione_portaDelegata_Expr.addGroupBy(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
					impostaGroupByFiltro3dCustom(fruizione_portaDelegata_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
				}
				
				fruizione_portaDelegata_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_MITTENTE);
				fruizione_portaDelegata_Expr.addGroupBy(model.MITTENTE);
			}

			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					erogazione_portaApplicativa_Expr.addForceIndex(index);	
					fruizione_portaDelegata_Expr.addForceIndex(index);	
				}
			}
			
			List<String> aliases3dCustom = new ArrayList<>();
			UnionExpression erogazione_portaApplicativa_UnionExpr = new UnionExpression(erogazione_portaApplicativa_Expr);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(erogazione_portaApplicativa_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.TIPO_MITTENTE,
						"tipo_soggetto");
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.MITTENTE,
						"soggetto");
			}
			else{
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.TIPO_DESTINATARIO,
						"tipo_soggetto");
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.DESTINATARIO,
						"soggetto");
			}

			UnionExpression fruizione_portaDelegata_UnionExpr = new UnionExpression(fruizione_portaDelegata_Expr);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				fruizione_portaDelegata_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(fruizione_portaDelegata_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}	
			
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				fruizione_portaDelegata_UnionExpr.addSelectField(
						model.TIPO_DESTINATARIO, "tipo_soggetto");
				fruizione_portaDelegata_UnionExpr.addSelectField(model.DESTINATARIO,
						"soggetto");
			}
			else{
				fruizione_portaDelegata_UnionExpr.addSelectField(
						model.TIPO_MITTENTE, "tipo_soggetto");
				fruizione_portaDelegata_UnionExpr.addSelectField(model.MITTENTE,
						"soggetto");
			}

			Union union = new Union();
			union.setUnionAll(true);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			union.addField("tipo_soggetto");
			union.addField("soggetto");
			
			// group by field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			union.addGroupBy("tipo_soggetto");
			union.addGroupBy("soggetto");

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribSoggettoSearch.getSortOrder() != null ? 	this.distribSoggettoSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribSoggettoSearch.getSortOrder() != null ? 	this.distribSoggettoSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}
			
			TipoVisualizzazione tipoVisualizzazione = this.distribSoggettoSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribSoggettoSearch.getTipoBanda();

				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");

				switch (tipoBanda) {
				case COMPLESSIVA:
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, "dato"));
					break;
				case INTERNA:
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, "dato"));
					break;
				case ESTERNA:
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, "dato"));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");
				erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato"));
				fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato"));
				break;

			case TEMPO_MEDIO_RISPOSTA:{
				
				TipoLatenza tipoLatenza = this.distribSoggettoSearch.getTipoLatenza();

				union.addOrderBy("somma",SortOrder.DESC);
				
				union.addField("somma_richieste", Function.SUM, "dato_richieste"); // dato non utilizzato, ma richiesto per avere lo stesso numero dei dati ritornati nelle expression
				//union.addField("somma", Function.AVG, "dato");
				union.addCustomField("somma",StatisticheUtils.getSqlCalcolaMedia("dato", "dato_richieste"));
				
				erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato_richieste"));
				fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato_richieste"));
				
				switch (tipoLatenza) {
				case LATENZA_PORTA:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_PORTA);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_PORTA);
//					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, "dato"));
//					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, "dato"));
					
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "dato"));
															
					break;
				case LATENZA_SERVIZIO:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_SERVIZIO);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_SERVIZIO);
//					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, "dato"));
//					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, "dato"));
					
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;

				case LATENZA_TOTALE:
				default:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_TOTALE);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_TOTALE);
//					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, "dato"));
//					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, "dato"));
					
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;
				}
				break;
			}
			}



			if (isPaginated) {
				union.setOffset(start);
				union.setLimit(limit);
			}


			// union.append("SELECT tipo_soggetto, soggetto, sum(dato) as somma ");
			// union.append(" FROM ( (" + mittente.toString() + ") UNION ("
			// + destinatario.toString()
			// + ") ) GROUP BY tipo_soggetto, soggetto ");
			// union.append(" ORDER BY somma DESC ");
			
			this.timeoutEvent = false;
			
			if(this.timeoutRicerche == null) {
				list = dao.union(union, erogazione_portaApplicativa_UnionExpr, fruizione_portaDelegata_UnionExpr);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> dao.union(union, erogazione_portaApplicativa_UnionExpr, fruizione_portaDelegata_UnionExpr)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {
					String risultato = ((String) row.get("tipo_soggetto")) + "/" + ((String) row.get("soggetto"));
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					} 
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribSoggettoSearch.getNumeroDimensioniCustom(), this.distribSoggettoSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("Soggetto '"+risultato+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione Soggetto("+risultato+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					} 
					else {
						r = new ResDistribuzione();
					}
					
					r.setRisultato(risultato);

					Number somma = StatsUtils.converToNumber( row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}
					res.add(r);
				}

			}

		} else if (TipologiaRicerca.ingresso.equals(this.distribSoggettoSearch.getTipologiaRicercaEnum())) {
			// EROGAZIONE
			// il destinatario e' l'utente loggato (sempre presente se non
			// sono
			// admin)

			// EROGAZIONE
			IExpression erogazione_portaApplicativa_Expr = dao.newExpression();

			// Data
			erogazione_portaApplicativa_Expr.between(model.DATA,
					this.distribSoggettoSearch.getDataInizio(),
					this.distribSoggettoSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(erogazione_portaApplicativa_Expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
				//				mitExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
				protocollo = this.distribSoggettoSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, erogazione_portaApplicativa_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

			}

			erogazione_portaApplicativa_Expr.and().equals(model.TIPO_PORTA,
					"applicativa");

			// permessi utente operatore
			if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				erogazione_portaApplicativa_Expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				erogazione_portaApplicativa_Expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.distribSoggettoSearch
					.getNomeAzione()))
				erogazione_portaApplicativa_Expr.and().equals(model.AZIONE,
						this.distribSoggettoSearch.getNomeAzione());

			// nome servizio
			if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
				
				erogazione_portaApplicativa_Expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			}

			// esito
			esitoUtils.setExpression(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
					this.distribSoggettoSearch.getEsitoDettaglio(),
					this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
					this.distribSoggettoSearch.getEsitoContesto(),
					this.distribSoggettoSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());



			// il mittente e' l'utente loggato (sempre presente se non
			// sn admin)
			if (!listaSoggettiGestione.isEmpty()) {
				erogazione_portaApplicativa_Expr.and();

				IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
				                                           .size()];
				int i = 0;
				for (Soggetto soggetto : listaSoggettiGestione) {
					IExpression se = dao.newExpression();
					se.equals(model.TIPO_DESTINATARIO,
							soggetto.getTipoSoggetto());
					se.and().equals(model.DESTINATARIO,
							soggetto.getNomeSoggetto());
					orSoggetti[i] = se;
					i++;
				}
				erogazione_portaApplicativa_Expr.or(orSoggetti);
			}

			if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				
				// il mittente puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.distribSoggettoSearch);
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeMittente()) && !ignoreSetMittente) {
					erogazione_portaApplicativa_Expr.and().equals(	model.TIPO_MITTENTE,	this.distribSoggettoSearch.getTipoMittente());
					erogazione_portaApplicativa_Expr.and().equals(	model.MITTENTE, this.distribSoggettoSearch.getNomeMittente());
				}
			}
			
			// filtro dati m
			this.impostaFiltroDatiMittente(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroGruppo(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroApi(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroIdClusterOrCanale(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			
			// raggruppamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				erogazione_portaApplicativa_Expr.addGroupBy(model.DATA);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaGroupByFiltro3dCustom(erogazione_portaApplicativa_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
			}
			
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				erogazione_portaApplicativa_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_MITTENTE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.MITTENTE);
			}
			else{
				erogazione_portaApplicativa_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_DESTINATARIO);
				erogazione_portaApplicativa_Expr.addGroupBy(model.DESTINATARIO);
			}

			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					erogazione_portaApplicativa_Expr.addForceIndex(index);	
				}
			}
			
			//			erogazione_portaApplicativa_Expr.sortOrder(SortOrder.ASC);
			//			erogazione_portaApplicativa_Expr.addOrder(model.TIPO_MITTENTE);
			//			erogazione_portaApplicativa_Expr.addOrder(model.MITTENTE);

			UnionExpression erogazione_portaApplicativa_UnionExpr = new UnionExpression(erogazione_portaApplicativa_Expr);
			List<String> aliases3dCustom = new ArrayList<>();
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(erogazione_portaApplicativa_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom );
			}
			
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				erogazione_portaApplicativa_UnionExpr.addSelectField(
						model.TIPO_MITTENTE, "tipo_soggetto");
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.MITTENTE,
						"soggetto");
			}
			else{
				erogazione_portaApplicativa_UnionExpr.addSelectField(
						model.TIPO_DESTINATARIO, "tipo_soggetto");
				erogazione_portaApplicativa_UnionExpr.addSelectField(model.DESTINATARIO,
						"soggetto");
			}

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
						model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustomFake(unionExprFake, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_MITTENTE.getFieldType()), "tipo_soggetto");
				unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.MITTENTE.getFieldType()), "soggetto");
			}
			else{
				unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_DESTINATARIO.getFieldType()), "tipo_soggetto");
				unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.DESTINATARIO.getFieldType()), "soggetto");
			}

			Union union = new Union();
			union.setUnionAll(true);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			union.addField("tipo_soggetto");
			union.addField("soggetto");
			
			// group by field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			union.addGroupBy("tipo_soggetto");
			union.addGroupBy("soggetto");

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribSoggettoSearch.getSortOrder() != null ? 	this.distribSoggettoSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribSoggettoSearch.getSortOrder() != null ? 	this.distribSoggettoSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}
			
			TipoVisualizzazione tipoVisualizzazione = this.distribSoggettoSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribSoggettoSearch.getTipoBanda();

				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");

				switch (tipoBanda) {
				case COMPLESSIVA:
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva", 
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, "dato"));
					break;
				case INTERNA:
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna", 
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, "dato"));
					break;
				case ESTERNA:
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna", 
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, "dato"));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");
				erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato"));
				unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni",
						Integer.valueOf(0), model.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, "dato"));

				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.distribSoggettoSearch.getTipoLatenza();

				union.addOrderBy("somma",SortOrder.DESC);
				//union.addField("somma", Function.AVG, "dato");
				union.addField("somma", Function.SUM, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_PORTA);
					fakeExpr.isNotNull(model.LATENZA_PORTA);
//					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, "dato"));
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
//							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta", 
							Integer.valueOf(0), model.LATENZA_PORTA.getFieldType()), Function.SUM, "dato"));
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "dato"));

					break;
				case LATENZA_SERVIZIO:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_SERVIZIO);
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
//					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, "dato"));
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
//							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(0), model.LATENZA_SERVIZIO.getFieldType()), Function.SUM, "dato"));
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "dato"));

					break;

				case LATENZA_TOTALE:
				default:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_TOTALE);
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
//					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, "dato"));
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
//							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
							Integer.valueOf(0), model.LATENZA_TOTALE.getFieldType()), Function.SUM, "dato"));
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "dato"));

					break;
				}
				break;
			}
			}


			if (isPaginated) {
				union.setOffset(start);
				union.setLimit(limit);
			}
			
			this.timeoutEvent = false;
			
			if(this.timeoutRicerche == null) {
				list = dao.union(union, erogazione_portaApplicativa_UnionExpr, unionExprFake);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> dao.union(union, erogazione_portaApplicativa_UnionExpr, unionExprFake)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					String risultato = ((String) row.get("tipo_soggetto")) + "/"
							+ ((String) row.get("soggetto"));
					if(risultato!=null && risultato.contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) {
						continue;
					}
					
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					} 
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribSoggettoSearch.getNumeroDimensioniCustom(), this.distribSoggettoSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("Soggetto '"+risultato+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione Soggetto("+risultato+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					}
					else {
						r = new ResDistribuzione();
					}
					
					r.setRisultato(risultato);

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					res.add(r);
				}

			}

		} else {
			// FRUIZIONE
			// il mittente e' l'utente loggato (sempre presente)

			// FRUIZIONE
			IExpression fruizione_portaDelegata_Expr = dao.newExpression();

			// Data
			fruizione_portaDelegata_Expr.between(model.DATA,
					this.distribSoggettoSearch.getDataInizio(),
					this.distribSoggettoSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(fruizione_portaDelegata_Expr, model);


			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
				//				destExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
				protocollo = this.distribSoggettoSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, fruizione_portaDelegata_Expr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

			}

			fruizione_portaDelegata_Expr.and().equals(model.TIPO_PORTA, "delegata");

			// permessi utente operatore
			if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				fruizione_portaDelegata_Expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				fruizione_portaDelegata_Expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.distribSoggettoSearch
					.getNomeAzione()))
				fruizione_portaDelegata_Expr.and().equals(model.AZIONE,
						this.distribSoggettoSearch.getNomeAzione());

			// nome servizio
			if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
				
				fruizione_portaDelegata_Expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			}

			// esito
			esitoUtils.setExpression(fruizione_portaDelegata_Expr, this.distribSoggettoSearch.getEsitoGruppo(), 
					this.distribSoggettoSearch.getEsitoDettaglio(),
					this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
					this.distribSoggettoSearch.getEsitoContesto(),
					this.distribSoggettoSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// il mittente e' l'utente loggato (sempre presente se non
			// sn admin)
			if (!listaSoggettiGestione.isEmpty()) {
				fruizione_portaDelegata_Expr.and();

				IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
				                                           .size()];
				int i = 0;
				for (Soggetto soggetto : listaSoggettiGestione) {
					IExpression se = dao.newExpression();
					se.equals(model.TIPO_MITTENTE,
							soggetto.getTipoSoggetto());
					se.and().equals(model.MITTENTE,
							soggetto.getNomeSoggetto());
					orSoggetti[i] = se;
					i++;
				}
				fruizione_portaDelegata_Expr.or(orSoggetti);
			}

			if(!this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeDestinatario())) {
					fruizione_portaDelegata_Expr.and().equals(	model.TIPO_DESTINATARIO,	this.distribSoggettoSearch.getTipoDestinatario());
					fruizione_portaDelegata_Expr.and().equals(	model.DESTINATARIO, this.distribSoggettoSearch.getNomeDestinatario());
				}
			}

			// filtro dati m
			this.impostaFiltroDatiMittente(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroGruppo(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroApi(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);

			this.impostaFiltroIdClusterOrCanale(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			// raggruppamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				fruizione_portaDelegata_Expr.addGroupBy(model.DATA);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaGroupByFiltro3dCustom(fruizione_portaDelegata_Expr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom());
			}
			
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				fruizione_portaDelegata_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_DESTINATARIO);
				fruizione_portaDelegata_Expr.addGroupBy(model.DESTINATARIO);
			}
			else{
				fruizione_portaDelegata_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_MITTENTE);
				fruizione_portaDelegata_Expr.addGroupBy(model.MITTENTE);
			}

			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					fruizione_portaDelegata_Expr.addForceIndex(index);	
				}
			}
			
			//			fruizione_portaDelegata_Expr.sortOrder(SortOrder.ASC);
			//			fruizione_portaDelegata_Expr.addOrder(model.TIPO_DESTINATARIO);
			//			fruizione_portaDelegata_Expr.addOrder(model.DESTINATARIO);

			UnionExpression fruizione_portaDelegata_UnionExpr = new UnionExpression(fruizione_portaDelegata_Expr);
			List<String> aliases3dCustom = new ArrayList<>();
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				fruizione_portaDelegata_UnionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(fruizione_portaDelegata_UnionExpr, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom  );
			}
			
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				fruizione_portaDelegata_UnionExpr.addSelectField(
						model.TIPO_DESTINATARIO,
						"tipo_soggetto");
				fruizione_portaDelegata_UnionExpr.addSelectField(
						model.DESTINATARIO, "soggetto");
			}
			else{
				fruizione_portaDelegata_UnionExpr.addSelectField(
						model.TIPO_MITTENTE,
						"tipo_soggetto");
				fruizione_portaDelegata_UnionExpr.addSelectField(
						model.MITTENTE, "soggetto");
			}

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
						model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustomFake(unionExprFake, model, this.distribSoggettoSearch, this.distribSoggettoSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_DESTINATARIO.getFieldType()), "tipo_soggetto");
				unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.DESTINATARIO.getFieldType()), "soggetto");
			}
			else{
				unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_MITTENTE.getFieldType()), "tipo_soggetto");
				unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.MITTENTE.getFieldType()), "soggetto");
			}

			Union union = new Union();
			union.setUnionAll(true);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			union.addField("tipo_soggetto");
			union.addField("soggetto");
			
			// group by field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			union.addGroupBy("tipo_soggetto");
			union.addGroupBy("soggetto");

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribSoggettoSearch.getSortOrder() != null ? 	this.distribSoggettoSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribSoggettoSearch.getSortOrder() != null ? 	this.distribSoggettoSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}
			
			TipoVisualizzazione tipoVisualizzazione = this.distribSoggettoSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribSoggettoSearch.getTipoBanda();

				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");

				switch (tipoBanda) {
				case COMPLESSIVA:
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, "dato"));

					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA, Function.SUM,
							"dato"));
					break;
				case INTERNA:
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, "dato"));

					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA, Function.SUM,
							"dato"));
					break;
				case ESTERNA:
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, "dato"));

					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA, Function.SUM,
							"dato"));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");
				unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni",
						Integer.valueOf(0), model.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, "dato"));

				fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato"));
				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.distribSoggettoSearch.getTipoLatenza();

				union.addOrderBy("somma",SortOrder.DESC);
				//union.addField("somma", Function.AVG, "dato");
				union.addField("somma", Function.SUM, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_PORTA);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
//							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));
//
//					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta", 
							Integer.valueOf(0), model.LATENZA_PORTA.getFieldType()), Function.SUM, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_SERVIZIO);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
//							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));
//
//					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(0), model.LATENZA_SERVIZIO.getFieldType()), Function.SUM, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_TOTALE);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
//							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));
//
//					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
							Integer.valueOf(0), model.LATENZA_TOTALE.getFieldType()), Function.SUM, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;
				}
				break;
			}
			}

			if (isPaginated) {
				union.setOffset(start);
				union.setLimit(limit);
			}

			this.timeoutEvent = false;
			
			if(this.timeoutRicerche == null) {
				list = dao.union(union, fruizione_portaDelegata_UnionExpr, unionExprFake);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> dao.union(union, fruizione_portaDelegata_UnionExpr, unionExprFake)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					String risultato = ((String) row.get("tipo_soggetto")) + "/"
							+ ((String) row.get("soggetto"));
					if(risultato!=null && risultato.contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) {
						continue;
					}
					
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					} 
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSoggettoSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribSoggettoSearch.getNumeroDimensioniCustom(), this.distribSoggettoSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("Soggetto '"+risultato+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione Soggetto("+risultato+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					}
					else {
						r = new ResDistribuzione();
					}
					
					r.setRisultato(risultato);

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					res.add(r);
				}

			}
		}

		return res;

	}

	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER SERVIZIO ******************
	
	@Override
	public int countAllDistribuzioneServizio() throws ServiceException {
		try {
			StatisticType tipologia = checkStatisticType(this.distribServizioSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneServizioCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = createDistribuzioneServizioExpression(dao,model, true);
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber nnn = dao.count(gByExpr);

			return nnn != null ? ((int) nnn.longValue()) : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e.getMessage(),e);
		}

		//		return 0;

	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneServizio() throws ServiceException {
		return this.executeDistribuzioneServizio(null, null);
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneServizio(int start,int limit) throws ServiceException {
		return this.executeDistribuzioneServizio(start, limit);
	}

	private List<ResDistribuzione> executeDistribuzioneServizio(Integer start, Integer limit) throws ServiceException {
		try {
			
			StatisticType tipologia = checkStatisticType(this.distribServizioSearch);
			StatisticaModel model = null;
//			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				this.dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				this.dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				this.dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.dao).getFieldConverter(); 
			
			IExpression gByExpr = this.createDistribuzioneServizioExpression(this.dao, model, false);

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribServizioSearch.getSortOrder() != null ? 	this.distribServizioSearch.getSortOrder() : SortOrder.ASC;
				gByExpr.sortOrder(s).addOrder(model.DATA);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribServizioSearch.getSortOrder() != null ? 	this.distribServizioSearch.getSortOrder() : SortOrder.ASC;
				impostaSortOrder3dCustom(gByExpr, model, this.distribServizioSearch, this.distribServizioSearch.getNumeroDimensioniCustom(), s);
			}
			
			if(this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
				gByExpr.sortOrder(SortOrder.ASC).addOrder(model.TIPO_SERVIZIO);
				gByExpr.sortOrder(SortOrder.ASC).addOrder(model.SERVIZIO);
				gByExpr.sortOrder(SortOrder.ASC).addOrder(model.VERSIONE_SERVIZIO);
				gByExpr.sortOrder(SortOrder.ASC).addOrder(model.TIPO_DESTINATARIO);
				gByExpr.sortOrder(SortOrder.ASC).addOrder(model.DESTINATARIO);
			}
			else {
				gByExpr.sortOrder(SortOrder.ASC).addOrder(model.URI_API);
			}

			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneServizioGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}

			String aliasFieldTipoServizio = "tipo_servizio";
			String aliasFieldServizio = "servizio";
			String aliasFieldVersioneServizio = "versione_servizio";
			String aliasFieldTipoDestinatario = "tipo_destinatario";
			String aliasFieldDestinatario = "destinatario";
			
			String aliasUriApi = "uri_api";
			
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			IExpression fakeExpr = this.dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
			List<String> aliases3dCustom = new ArrayList<>();
			
			// select field by data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				unionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);

				unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
						model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
			}else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(unionExpr, model, this.distribServizioSearch, this.distribServizioSearch.getNumeroDimensioniCustom(), aliases3dCustom);
				impostaSelectField3dCustomFake(unionExprFake, model, this.distribServizioSearch, this.distribServizioSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			
			if(this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
				
				unionExpr.addSelectField(model.TIPO_SERVIZIO, aliasFieldTipoServizio);
				unionExpr.addSelectField(model.SERVIZIO, aliasFieldServizio);
				unionExpr.addSelectField(model.VERSIONE_SERVIZIO, aliasFieldVersioneServizio);
				unionExpr.addSelectField(model.TIPO_DESTINATARIO, aliasFieldTipoDestinatario);
				unionExpr.addSelectField(model.DESTINATARIO, aliasFieldDestinatario);
	
				// Espressione finta per usare l'ordinamento
				unionExprFake.addSelectField(new ConstantField(aliasFieldTipoServizio, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TIPO_SERVIZIO.getFieldType()), aliasFieldTipoServizio);
				unionExprFake.addSelectField(new ConstantField(aliasFieldServizio, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.SERVIZIO.getFieldType()), aliasFieldServizio);
				unionExprFake.addSelectField(new ConstantField(aliasFieldVersioneServizio, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_VERSIONE,
						model.VERSIONE_SERVIZIO.getFieldType()), aliasFieldVersioneServizio);
				unionExprFake.addSelectField(new ConstantField(aliasFieldTipoDestinatario, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TIPO_DESTINATARIO.getFieldType()), aliasFieldTipoDestinatario);
				unionExprFake.addSelectField(new ConstantField(aliasFieldDestinatario, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, 
						model.DESTINATARIO.getFieldType()), aliasFieldDestinatario);
			}
			else {
				
				unionExpr.addSelectField(model.URI_API, aliasUriApi);
				
				// Espressione finta per usare l'ordinamento
				unionExprFake.addSelectField(new ConstantField(aliasUriApi, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.URI_API.getFieldType()), aliasUriApi);
				
			}
			
			Union union = new Union();
			union.setUnionAll(true);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			// group by field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			
			if(this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
				union.addField(aliasFieldTipoServizio);
				union.addField(aliasFieldServizio);
				union.addField(aliasFieldVersioneServizio);
				union.addField(aliasFieldTipoDestinatario);
				union.addField(aliasFieldDestinatario);
				union.addGroupBy(aliasFieldTipoServizio);
				union.addGroupBy(aliasFieldServizio);
				union.addGroupBy(aliasFieldVersioneServizio);
				union.addGroupBy(aliasFieldTipoDestinatario);
				union.addGroupBy(aliasFieldDestinatario);
			}
			else {
				union.addField(aliasUriApi);
				union.addGroupBy(aliasUriApi);
			}
			
			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribServizioSearch.getSortOrder() != null ? 	this.distribServizioSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			}else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribServizioSearch.getSortOrder() != null ? 	this.distribServizioSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}

			TipoVisualizzazione tipoVisualizzazione = this.distribServizioSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribServizioSearch.getTipoBanda();

				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");

				switch (tipoBanda) {
				case COMPLESSIVA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, "dato"));
					break;
				case INTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, "dato"));
					break;
				case ESTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, "dato"));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");
				unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni",
						Integer.valueOf(0), model.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, "dato"));

				unionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						"dato"));
				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.distribServizioSearch.getTipoLatenza();

				union.addOrderBy("somma",SortOrder.DESC);
				//union.addField("somma", Function.AVG, "dato");
				union.addField("somma", Function.SUM, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					gByExpr.isNotNull(model.LATENZA_PORTA);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
//							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta", 
							Integer.valueOf(0), model.LATENZA_PORTA.getFieldType()), Function.SUM, "dato"));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					gByExpr.isNotNull(model.LATENZA_SERVIZIO);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
//							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(0), model.LATENZA_SERVIZIO.getFieldType()), Function.SUM, "dato"));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					gByExpr.isNotNull(model.LATENZA_TOTALE);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
//							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, "dato"));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
							Integer.valueOf(0), model.LATENZA_TOTALE.getFieldType()), Function.SUM, "dato"));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, "dato"));
					
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<>();

			if(start != null)
				union.setOffset(start);
			if(start != null)
				union.setLimit(limit);

			this.timeoutEvent = false;
			
			List<Map<String, Object>> list = null;
			if(this.timeoutRicerche == null) {
				list = this.dao.union(union, unionExpr, unionExprFake);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.union(union, unionExpr, unionExprFake)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					String risultato = null;
					if(this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
						risultato = ((String) row.get(aliasFieldTipoServizio)) + "/"
								+ ((String) row.get(aliasFieldServizio)) + ":"
								+ (row.get(aliasFieldVersioneServizio));
					}
					else {
						risultato = (String) row.get(aliasUriApi);
					}
					
					if(risultato!=null && risultato.contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) {
						continue;
					}
					
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					} 
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribServizioSearch.getNumeroDimensioniCustom(), this.distribServizioSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("Servizio '"+risultato+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione Servizio("+risultato+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					} 
					else {
						r = new ResDistribuzione();
					}
					
					if(this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
						
						r.setRisultato(risultato);
						
						r.getParentMap().put("0",((String) row.get(aliasFieldTipoDestinatario)) + "/"
								+ ((String) row.get(aliasFieldDestinatario)));
					}
					else {
						
						risultato = this.getLabelCredenzialeFieldGroupBy(risultato, this.distribServizioSearch, new StringBuilder());	
						
						r.setRisultato(DistribuzionePerServizioBean.PREFIX_API+risultato);
						
					}
					
					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					res.add(r);
				}

			}

			return res;

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		}
		return new ArrayList<>();
	}

	private IExpression createDistribuzioneServizioExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, boolean isCount) throws ServiceException {
		IExpression expr = null;

		StatisticheGiornaliereService.logDebug("creo Expression per distribuzione Servizio!");

		List<Soggetto> listaSoggettiGestione = this.distribServizioSearch.getSoggettiGestione();

		try {

			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribServizioSearch.getProtocollo());
			
			expr = dao.newExpression();
			// Data
			expr.between(model.DATA,
					this.distribServizioSearch.getDataInizio(),
					this.distribServizioSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
	//			if (StringUtils.isNotEmpty(this.distribServizioSearch.getProtocollo()) && this.distribServizioSearch.isShowListaProtocolli()) {
			if (this.distribServizioSearch.isSetFiltroProtocollo()) {
				//				expr.and().equals(model.PROTOCOLLO,	this.distribServizioSearch.getProtocollo());
				protocollo = this.distribServizioSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo, this.distribServizioSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.distribServizioSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribServizioSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribServizioSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribServizioSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribServizioSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribServizioSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribServizioSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				expr.and().equals(model.ID_PORTA, idPorta);
			}

			// esito
			esitoUtils.setExpression(expr, this.distribServizioSearch.getEsitoGruppo(), 
					this.distribServizioSearch.getEsitoDettaglio(),
					this.distribServizioSearch.getEsitoDettaglioPersonalizzato(),
					this.distribServizioSearch.getEsitoContesto(),
					this.distribServizioSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// ho 3 diversi tipi di query in base alla tipologia di ricerca

			// imposto il soggetto (loggato) come mittente o destinatario in
			// base
			// alla tipologia di ricerca selezionata
			if (this.distribServizioSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribServizioSearch.getTipologiaRicercaEnum())) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome
				// del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils
						.isNotBlank(this.distribServizioSearch
								.getTrafficoPerSoggetto());
				boolean soggetto = !listaSoggettiGestione.isEmpty();
				String tipoTrafficoSoggetto = null;
				String nomeTrafficoSoggetto = null;
				if (trafficoSoggetto) {
					tipoTrafficoSoggetto = this.distribServizioSearch
							.getTipoTrafficoPerSoggetto();
					nomeTrafficoSoggetto = this.distribServizioSearch
							.getTrafficoPerSoggetto();
				}

				IExpression e1 = dao.newExpression();
				IExpression e2 = dao.newExpression();

				// se trafficoSoggetto e soggetto sono impostati allora devo
				// fare la
				// OR
				if (trafficoSoggetto && soggetto) {
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					e1.and().equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.and().equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);

					// OR
					expr.or(e1, e2);
				} else if (trafficoSoggetto && !soggetto) {
					// il mio soggetto non e' stato impostato (soggetto in
					// gestione,
					// puo succedero solo in caso admin)
					expr.and();

					e1.equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);
					// OR
					expr.or(e1, e2);
				} else if (!trafficoSoggetto && soggetto) {
					// e' impostato solo il soggetto in gestione
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					// OR
					expr.or(e1, e2);
				} else {
					// nessun filtro da impostare
				}

			} else if (TipologiaRicerca.ingresso.equals(this.distribServizioSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.distribServizioSearch);
				if (StringUtils.isNotBlank(this.distribServizioSearch.getNomeMittente()) && !ignoreSetMittente) {
					expr.and().equals(model.TIPO_MITTENTE,
							this.distribServizioSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,
							this.distribServizioSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"applicativa");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribServizioSearch
						.getNomeDestinatario())) {
					expr.and().equals(model.TIPO_DESTINATARIO,
							this.distribServizioSearch.getTipoDestinatario());
					expr.and().equals(model.DESTINATARIO,
							this.distribServizioSearch.getNomeDestinatario());
				}
			}
			
			this.impostaFiltroDatiMittente(expr, this.distribServizioSearch, model, isCount);
			
			this.impostaFiltroGruppo(expr, this.distribServizioSearch, model, isCount);
			
			this.impostaFiltroApi(expr, this.distribServizioSearch, model, isCount); // non viene presentato all'utente la possibilita di impostarlo nel searchform

			this.impostaFiltroIdClusterOrCanale(expr, this.distribServizioSearch, model, isCount);

			// raggruppamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				expr.addGroupBy(model.DATA);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribServizioSearch.getNumeroDimensioni())) {
				impostaGroupByFiltro3dCustom(expr, model, this.distribServizioSearch, this.distribServizioSearch.getNumeroDimensioniCustom());
			}
			
			if(this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
			
				expr.notEquals(model.TIPO_SERVIZIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				expr.notEquals(model.SERVIZIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				expr.notEquals(model.VERSIONE_SERVIZIO, Costanti.INFORMAZIONE_VERSIONE_NON_DISPONIBILE);
				expr.addGroupBy(model.TIPO_SERVIZIO);
				expr.addGroupBy(model.SERVIZIO);
				expr.addGroupBy(model.VERSIONE_SERVIZIO);
				
				expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				expr.addGroupBy(model.TIPO_DESTINATARIO);
				expr.addGroupBy(model.DESTINATARIO);
				
			}
			else {
				
				expr.notEquals(model.URI_API, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				expr.addGroupBy(model.URI_API);
				
			}

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return expr;
	}

	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER AZIONE ******************
	
	@Override
	public int countAllDistribuzioneAzione() throws ServiceException {
		try {
			StatisticType tipologia = checkStatisticType(this.distribAzioneSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneAzioneCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = createDistribuzioneAzioneExpression(dao,	model, true);
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber nnn = dao.count(gByExpr);

			return nnn != null ? ((int) nnn.longValue()) : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e.getMessage(),e);
		}
	}
	@Override
	public List<ResDistribuzione> findAllDistribuzioneAzione() throws ServiceException {
		return this.executeDistribuzioneAzione(null, null);
	}
	@Override
	public List<ResDistribuzione> findAllDistribuzioneAzione(int start, int limit) throws ServiceException {
		return this.executeDistribuzioneAzione(start, limit);
	}

	private IExpression createDistribuzioneAzioneExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, boolean isCount) throws ServiceException {
		IExpression expr = null;

		StatisticheGiornaliereService.logDebug("creo Expression per distribuzione Azione!");

		List<Soggetto> listaSoggettiGestione = this.distribAzioneSearch.getSoggettiGestione();

		try {

			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribAzioneSearch.getProtocollo());

			expr = dao.newExpression();
			// Data
			expr.between(model.DATA,
					this.distribAzioneSearch.getDataInizio(),
					this.distribAzioneSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
//				if (StringUtils.isNotEmpty(this.distribAzioneSearch.getProtocollo()) && this.distribAzioneSearch.isShowListaProtocolli()) {
			if (this.distribAzioneSearch.isSetFiltroProtocollo()) {
				//				expr.and().equals(model.PROTOCOLLO,	this.distribAzioneSearch.getProtocollo());
				protocollo = this.distribAzioneSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo, this.distribAzioneSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.distribAzioneSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribAzioneSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribAzioneSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribAzioneSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribAzioneSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribAzioneSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribAzioneSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				expr.and().equals(model.ID_PORTA, idPorta);
			}

			// esito
			esitoUtils.setExpression(expr, this.distribAzioneSearch.getEsitoGruppo(), 
					this.distribAzioneSearch.getEsitoDettaglio(),
					this.distribAzioneSearch.getEsitoDettaglioPersonalizzato(),
					this.distribAzioneSearch.getEsitoContesto(),
					this.distribAzioneSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// ho 3 diversi tipi di query in base alla tipologia di ricerca

			// imposto il soggetto (loggato) come mittente o destinatario in
			// base
			// alla tipologia di ricerca selezionata
			if (this.distribAzioneSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribAzioneSearch.getTipologiaRicercaEnum())) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome
				// del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils
						.isNotBlank(this.distribAzioneSearch
								.getTrafficoPerSoggetto());
				boolean soggetto = !listaSoggettiGestione.isEmpty();
				String tipoTrafficoSoggetto = null;
				String nomeTrafficoSoggetto = null;
				if (trafficoSoggetto) {
					tipoTrafficoSoggetto = this.distribAzioneSearch
							.getTipoTrafficoPerSoggetto();
					nomeTrafficoSoggetto = this.distribAzioneSearch
							.getTrafficoPerSoggetto();
				}

				IExpression e1 = dao.newExpression();
				IExpression e2 = dao.newExpression();

				// se trafficoSoggetto e soggetto sono impostati allora devo
				// fare la
				// OR
				if (trafficoSoggetto && soggetto) {
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					e1.and().equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.and().equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);

					// OR
					expr.or(e1, e2);
				} else if (trafficoSoggetto && !soggetto) {
					// il mio soggetto non e' stato impostato (soggetto in
					// gestione,
					// puo succedero solo in caso admin)
					expr.and();

					e1.equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);
					// OR
					expr.or(e1, e2);
				} else if (!trafficoSoggetto && soggetto) {
					// e' impostato solo il soggetto in gestione
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					// OR
					expr.or(e1, e2);
				} else {
					// nessun filtro da impostare
				}

			} else if (TipologiaRicerca.ingresso.equals(this.distribAzioneSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.distribAzioneSearch);
				if (StringUtils.isNotBlank(this.distribAzioneSearch.getNomeMittente()) && !ignoreSetMittente) {
					expr.and().equals(model.TIPO_MITTENTE,
							this.distribAzioneSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,
							this.distribAzioneSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"applicativa");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribAzioneSearch
						.getNomeDestinatario())) {
					expr.and().equals(model.TIPO_DESTINATARIO,
							this.distribAzioneSearch.getTipoDestinatario());
					expr.and().equals(model.DESTINATARIO,
							this.distribAzioneSearch.getNomeDestinatario());
				}
			}

			// nome servizio  e tipo
			if (StringUtils.isNotBlank(this.distribAzioneSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribAzioneSearch.getNomeServizio());
				
				expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO, idServizio.getVersione()); 

			}
			
			this.impostaFiltroDatiMittente(expr, this.distribAzioneSearch, model, isCount); 
			
			this.impostaFiltroGruppo(expr, this.distribAzioneSearch, model, isCount);
			
			this.impostaFiltroApi(expr, this.distribAzioneSearch, model, isCount);

			this.impostaFiltroIdClusterOrCanale(expr, this.distribAzioneSearch, model, isCount);

			// raggruppamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				expr.addGroupBy(model.DATA);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				impostaGroupByFiltro3dCustom(expr, model, this.distribAzioneSearch, this.distribAzioneSearch.getNumeroDimensioniCustom());
			}
			
			expr.notEquals(model.AZIONE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			expr.addGroupBy(model.AZIONE);

			expr.notEquals(model.TIPO_SERVIZIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			expr.notEquals(model.SERVIZIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			expr.notEquals(model.VERSIONE_SERVIZIO, Costanti.INFORMAZIONE_VERSIONE_NON_DISPONIBILE);
			expr.addGroupBy(model.TIPO_SERVIZIO);
			expr.addGroupBy(model.SERVIZIO);
			expr.addGroupBy(model.VERSIONE_SERVIZIO);
			
			expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			expr.addGroupBy(model.TIPO_DESTINATARIO);
			expr.addGroupBy(model.DESTINATARIO);

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return expr;
	}

	private List<ResDistribuzione> executeDistribuzioneAzione(Integer start, Integer limit) throws ServiceException {
		try {
			StatisticType tipologia = checkStatisticType(this.distribAzioneSearch);
			StatisticaModel model = null;
//			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				this.dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				this.dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				this.dao = this.statSettimanaleSearchDAO;
				break;
			}
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.dao).getFieldConverter(); 
			
			IExpression gByExpr = this.createDistribuzioneAzioneExpression(this.dao,	model, false);

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribAzioneSearch.getSortOrder() != null ? 	this.distribAzioneSearch.getSortOrder() : SortOrder.ASC;
				gByExpr.sortOrder(s).addOrder(model.DATA);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribAzioneSearch.getSortOrder() != null ? 	this.distribAzioneSearch.getSortOrder() : SortOrder.ASC;
				impostaSortOrder3dCustom(gByExpr, model, this.distribAzioneSearch, this.distribAzioneSearch.getNumeroDimensioniCustom(), s);
			}			
			
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.AZIONE);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.TIPO_SERVIZIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.SERVIZIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.VERSIONE_SERVIZIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.TIPO_DESTINATARIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.DESTINATARIO);

			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneAzioneGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			
			List<String> aliases3dCustom = new ArrayList<>();
			String aliasFieldAzione = "azione";
			String aliasFieldTipoServizio = "tipo_servizio";
			String aliasFieldServizio = "servizio";
			String aliasFieldVersioneServizio = "versione_servizio";
			String aliasFieldTipoDestinatario = "tipo_destinatario";
			String aliasFieldDestinatario = "destinatario";
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				unionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustom(unionExpr, model, this.distribAzioneSearch, this.distribAzioneSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			
			unionExpr.addSelectField(model.AZIONE,		aliasFieldAzione);
			unionExpr.addSelectField(model.TIPO_SERVIZIO, aliasFieldTipoServizio);
			unionExpr.addSelectField(model.SERVIZIO, aliasFieldServizio);
			unionExpr.addSelectField(model.VERSIONE_SERVIZIO, aliasFieldVersioneServizio);
			unionExpr.addSelectField(model.TIPO_DESTINATARIO, aliasFieldTipoDestinatario);
			unionExpr.addSelectField(model.DESTINATARIO, aliasFieldDestinatario);

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = this.dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
			
			// select field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
						model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				impostaSelectField3dCustomFake(unionExprFake, model, this.distribAzioneSearch, this.distribAzioneSearch.getNumeroDimensioniCustom(), aliases3dCustom);
			}
			
			unionExprFake.addSelectField(new ConstantField(aliasFieldAzione, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.AZIONE.getFieldType()), aliasFieldAzione);			
			unionExprFake.addSelectField(new ConstantField(aliasFieldTipoServizio, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.TIPO_SERVIZIO.getFieldType()), aliasFieldTipoServizio);
			unionExprFake.addSelectField(new ConstantField(aliasFieldServizio, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.SERVIZIO.getFieldType()), aliasFieldServizio);
			unionExprFake.addSelectField(new ConstantField(aliasFieldVersioneServizio, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_VERSIONE,
					model.VERSIONE_SERVIZIO.getFieldType()), aliasFieldVersioneServizio);
			unionExprFake.addSelectField(new ConstantField(aliasFieldTipoDestinatario, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.TIPO_DESTINATARIO.getFieldType()), aliasFieldTipoDestinatario);
			unionExprFake.addSelectField(new ConstantField(aliasFieldDestinatario, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, 
					model.DESTINATARIO.getFieldType()), aliasFieldDestinatario);
			
			Union union = new Union();
			union.setUnionAll(true);
			
			// select field e group by data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			
			union.addField(aliasFieldAzione);
			union.addField(aliasFieldTipoServizio);
			union.addField(aliasFieldServizio);
			union.addField(aliasFieldVersioneServizio);
			union.addField(aliasFieldTipoDestinatario);
			union.addField(aliasFieldDestinatario);
			
			// group by field data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			}
			else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			
			union.addGroupBy(aliasFieldAzione);
			union.addGroupBy(aliasFieldTipoServizio);
			union.addGroupBy(aliasFieldServizio);
			union.addGroupBy(aliasFieldVersioneServizio);
			union.addGroupBy(aliasFieldTipoDestinatario);
			union.addGroupBy(aliasFieldDestinatario);

			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribAzioneSearch.getSortOrder() != null ? 	this.distribAzioneSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribAzioneSearch.getSortOrder() != null ? 	this.distribAzioneSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}
			
			TipoVisualizzazione tipoVisualizzazione = this.distribAzioneSearch.getTipoVisualizzazione();
			String sommaAliasName = "somma";
			String datoParamAliasName = "dato";
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribAzioneSearch.getTipoBanda();

				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);

				switch (tipoBanda) {
				case COMPLESSIVA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, datoParamAliasName));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, datoParamAliasName));
					break;
				case INTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, datoParamAliasName));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, datoParamAliasName));
					break;
				case ESTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							model.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, datoParamAliasName));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna",
							Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, datoParamAliasName));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);
				unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni",
						Integer.valueOf(0), model.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, datoParamAliasName));

				unionExpr.addSelectFunctionField(new FunctionField(
						model.NUMERO_TRANSAZIONI, Function.SUM,
						datoParamAliasName));
				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.distribAzioneSearch.getTipoLatenza();

				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				//union.addField(sommaAliasName, Function.AVG, datoParamAliasName);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					gByExpr.isNotNull(model.LATENZA_PORTA);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
//							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, datoParamAliasName));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_PORTA,
//							Function.AVG, datoParamAliasName));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta", 
							Integer.valueOf(0), model.LATENZA_PORTA.getFieldType()), Function.SUM, datoParamAliasName));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					gByExpr.isNotNull(model.LATENZA_SERVIZIO);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
//							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, datoParamAliasName));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_SERVIZIO,
//							Function.AVG, datoParamAliasName));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(0), model.LATENZA_SERVIZIO.getFieldType()), Function.SUM, datoParamAliasName));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					gByExpr.isNotNull(model.LATENZA_TOTALE);
//					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
//							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, datoParamAliasName));
//
//					unionExpr.addSelectFunctionField(new FunctionField(
//							model.LATENZA_TOTALE,
//							Function.AVG, datoParamAliasName));
					
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
							Integer.valueOf(0), model.LATENZA_TOTALE.getFieldType()), Function.SUM, datoParamAliasName));
					unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<>();

			if(start != null)
				union.setOffset(start);
			if(start != null)
				union.setLimit(limit);

			this.timeoutEvent = false;
			
			List<Map<String, Object>> list = null;
			if(this.timeoutRicerche == null) {
				list = this.dao.union(union, unionExpr, unionExprFake);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.union(union, unionExpr, unionExprFake)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				for (Map<String, Object> row : list) {

					String risultato = ((String) row.get(aliasFieldAzione));
					if(risultato!=null && risultato.contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) {
						continue;
					}
					
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					}
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribAzioneSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribAzioneSearch.getNumeroDimensioniCustom(), this.distribAzioneSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("Azione '"+risultato+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione Azione("+risultato+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					} 
					else {
						r = new ResDistribuzione();
					}
					
					r.setRisultato(risultato);
					
					r.getParentMap().put("0",((String) row.get(aliasFieldTipoServizio)) + "/"
							+ ((String) row.get(aliasFieldServizio)) + ":"
									+ (row.get(aliasFieldVersioneServizio)));
					
					r.getParentMap().put("1",((String) row.get(aliasFieldTipoDestinatario)) + "/"
							+ ((String) row.get(aliasFieldDestinatario)));

					Number somma = StatsUtils.converToNumber(row.get(sommaAliasName));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					res.add(r);
				}

			}

			return res;

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		}
		return new ArrayList<>();
	}
	
	
	
	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER SERVIZIO APPLICATIVO ******************
	
	@Override
	public int countAllDistribuzioneServizioApplicativo() throws ServiceException{
		try {
			StatisticType tipologia = checkStatisticType(this.distribSaSearch);
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				break;
			}
			
			if(dao==null) {
				throw new ServiceException("Dao sconosciuto");
			}
			
			// Fix introdotto per gestire il soggetto proprietario
			boolean forceErogazione = false;
			boolean forceFruizione = false;
			if (this.distribSaSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribSaSearch.getTipologiaRicercaEnum())) {
				forceErogazione = true;
				forceFruizione = true;
			} else if (TipologiaRicerca.ingresso.equals(this.distribSaSearch.getTipologiaRicercaEnum())) {
				forceErogazione = true;
			} else {
				forceFruizione = true;
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneServizioApplicativoCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
						
			if(forceErogazione || forceFruizione){
				
				int count = 0;
				if(forceErogazione){
					IExpression expr = createDistribuzioneServizioApplicativoExpression(dao, model, true, forceErogazione, false);
					
					if(forceIndexes!=null && !forceIndexes.isEmpty()){
						for (Index index : forceIndexes) {
							expr.addForceIndex(index);	
						}
					}
					
					NonNegativeNumber nnn = dao.count(expr);
					int valoreLetto = nnn != null ? ((int)nnn.longValue()) : 0;
					count = count + valoreLetto;
				}
				if(forceFruizione){
					IExpression expr = createDistribuzioneServizioApplicativoExpression(dao, model, true, false, forceFruizione);
					
					if(forceIndexes!=null && !forceIndexes.isEmpty()){
						for (Index index : forceIndexes) {
							expr.addForceIndex(index);	
						}
					}
					
					NonNegativeNumber nnn = dao.count(expr);
					int valoreLetto = nnn != null ? ((int) nnn.longValue()) : 0;
					count = count + valoreLetto;
				}
				return count;
				
			}
			else{
				// Lascio else solo se si vuole tornare indietro come soluzione
				IExpression expr = createDistribuzioneServizioApplicativoExpression(dao, model, true, false, false);
				
				if(forceIndexes!=null && !forceIndexes.isEmpty()){
					for (Index index : forceIndexes) {
						expr.addForceIndex(index);	
					}
				}
				
				NonNegativeNumber nnn = dao.count(expr);
				return nnn != null ? ((int) nnn.longValue()) : 0;
			}
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e.getMessage(),e);
		} 

	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneServizioApplicativo() throws ServiceException	{
		return this.executeDistribuzioneServizioApplicativo(null, null);
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneServizioApplicativo(int start, int limit) throws ServiceException {
		return this.executeDistribuzioneServizioApplicativo(start, limit);
	}


	private List<ResDistribuzione> executeDistribuzioneServizioApplicativo(Integer start, Integer limit) throws ServiceException {
		

		try {
			StatisticType tipologia = checkStatisticType(this.distribSaSearch);
			StatisticaModel model = null;

			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				this.dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				this.dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				this.dao = this.statSettimanaleSearchDAO;
				break;
			}
			
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.dao).getFieldConverter(); 
			
			// Fix introdotto per gestire il soggetto proprietario
			boolean forceErogazione = false;
			boolean forceFruizione = false;
			if (this.distribSaSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribSaSearch.getTipologiaRicercaEnum())) {
				forceErogazione = true;
				forceFruizione = true;
			} else if (TipologiaRicerca.ingresso.equals(this.distribSaSearch.getTipologiaRicercaEnum())) {
				forceErogazione = true;
			} else {
				forceFruizione = true;
			}
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model,	this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneServizioApplicativoGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = null;
			IExpression gByExprErogazione = null;
			IExpression gByExprFruizione = null;
			IExpression fakeExpr = null;
			IField credenzialeFieldGroupBy = this.getCredenzialeFieldGroupBy(this.distribSaSearch, model);
			if(forceErogazione || forceFruizione){
				if(forceErogazione){
					gByExprErogazione = createDistribuzioneServizioApplicativoExpression(this.dao,model,false,
							forceErogazione,false);	
					
					// ordinamento per data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
						SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
						gByExprErogazione.sortOrder(s).addOrder(model.DATA);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
						SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
						impostaSortOrder3dCustom(gByExprErogazione, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), s);
					}	
					
					gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(credenzialeFieldGroupBy);
					// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
/**					gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.TIPO_DESTINATARIO);
//					gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.DESTINATARIO);*/
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
								gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.TIPO_MITTENTE);
								gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.MITTENTE);
							}
						}
					}
					
					if(forceIndexes!=null && !forceIndexes.isEmpty()){
						for (Index index : forceIndexes) {
							gByExprErogazione.addForceIndex(index);	
						}
					}
				}
				if(forceFruizione){
					gByExprFruizione = createDistribuzioneServizioApplicativoExpression(this.dao,model,false,
							false,forceFruizione);	
					
					// ordinamento per data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
						SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
						gByExprFruizione.sortOrder(s).addOrder(model.DATA);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
						SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
						impostaSortOrder3dCustom(gByExprFruizione, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), s);
					}
					
					gByExprFruizione.sortOrder(SortOrder.ASC).addOrder(credenzialeFieldGroupBy);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
								gByExprFruizione.sortOrder(SortOrder.ASC).addOrder(model.TIPO_MITTENTE);
								gByExprFruizione.sortOrder(SortOrder.ASC).addOrder(model.MITTENTE);
							}
						}
					}
					
					if(forceIndexes!=null && !forceIndexes.isEmpty()){
						for (Index index : forceIndexes) {
							gByExprFruizione.addForceIndex(index);	
						}
					}
				}
			}
			else{
				// Lascio else solo se si vuole tornare indietro come soluzione
				gByExpr = createDistribuzioneServizioApplicativoExpression(this.dao,model,false,
						false, false);	
				
				// ordinamento per data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
					SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
					gByExpr.sortOrder(s).addOrder(model.DATA);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
					SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
					impostaSortOrder3dCustom(gByExpr, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), s);
				}
				
				gByExpr.sortOrder(SortOrder.ASC).addOrder(credenzialeFieldGroupBy);
				
				if(forceIndexes!=null && !forceIndexes.isEmpty()){
					for (Index index : forceIndexes) {
						gByExpr.addForceIndex(index);	
					}
				}
			}
			
			List<String> aliases3dCustom = new ArrayList<>();
			String aliasFieldCredenzialeMittente = "credenziale_mittente";
			String aliasFieldTipoSoggetto = "tipo_soggetto";
			String aliasFieldSoggetto = "soggetto";
//			String aliasFieldRuoloSoggetto = "ruolo_soggetto";
			UnionExpression unionExpr = null;
			UnionExpression unionExprFake = null;
			UnionExpression unionExprErogatore = null;
			UnionExpression unionExprFruitore = null;
			if(forceErogazione || forceFruizione){
				if(forceErogazione){
					unionExprErogatore = new UnionExpression(gByExprErogazione);
					// select field data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
						unionExprErogatore.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
						impostaSelectField3dCustom(unionExprErogatore, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), aliases3dCustom);
					}
					unionExprErogatore.addSelectField(credenzialeFieldGroupBy, aliasFieldCredenzialeMittente);
					// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
//					unionExprErogatore.addSelectField(model.TIPO_DESTINATARIO, aliasFieldTipoSoggetto);
//					unionExprErogatore.addSelectField(model.DESTINATARIO, aliasFieldSoggetto);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
								unionExprErogatore.addSelectField(model.TIPO_MITTENTE, aliasFieldTipoSoggetto);
								unionExprErogatore.addSelectField(model.MITTENTE, aliasFieldSoggetto);
							}
						}
					}
//					unionExprErogatore.addSelectField(new ConstantField(aliasFieldRuoloSoggetto, "Erogatore", String.class),aliasFieldRuoloSoggetto);
				}
				if(forceFruizione){
					unionExprFruitore = new UnionExpression(gByExprFruizione);
					// select field data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
						unionExprFruitore.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
						impostaSelectField3dCustom(unionExprFruitore, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), aliases3dCustom);
					}
					unionExprFruitore.addSelectField(credenzialeFieldGroupBy, aliasFieldCredenzialeMittente);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
								unionExprFruitore.addSelectField(model.TIPO_MITTENTE, aliasFieldTipoSoggetto);
								unionExprFruitore.addSelectField(model.MITTENTE, aliasFieldSoggetto);
							}
						}
					}
//					unionExprFruitore.addSelectField(new ConstantField(aliasFieldRuoloSoggetto, "Fruitore", String.class),	aliasFieldRuoloSoggetto);
				}
				if(unionExprErogatore==null || unionExprFruitore==null){
					// Espressione finta per usare l'ordinamento
					fakeExpr = this.dao.newExpression();
					unionExprFake = new UnionExpression(fakeExpr);
					
					// select field data in caso di visualizzazione a 3 dimensioni
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
						unionExprFake.addSelectField(new ConstantField(ALIAS_FIELD_DATA_3D, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_TIMESTAMP,
								model.DATA.getFieldType()), ALIAS_FIELD_DATA_3D);
					} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
						impostaSelectField3dCustomFake(unionExprFake, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), aliases3dCustom);
					}
					
					unionExprFake.addSelectField(new ConstantField(aliasFieldCredenzialeMittente, 
							StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, credenzialeFieldGroupBy.getFieldType()), 
							aliasFieldCredenzialeMittente);
					
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
								unionExprFake.addSelectField(new ConstantField(aliasFieldTipoSoggetto, 
										StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, String.class), 
										aliasFieldTipoSoggetto);
								unionExprFake.addSelectField(new ConstantField(aliasFieldSoggetto, 
										StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, String.class), 
										aliasFieldSoggetto);
							}
						}
					}
//					unionExprFake.addSelectField(new ConstantField(aliasFieldRuoloSoggetto, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, String.class),	aliasFieldRuoloSoggetto);
				}
			}
			else{
				// Lascio else solo se si vuole tornare indietro come soluzione
				
				unionExpr = new UnionExpression(gByExpr);
				
				// select field data in caso di visualizzazione a 3 dimensioni
				if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
					unionExpr.addSelectField(model.DATA, ALIAS_FIELD_DATA_3D);
				} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
					impostaSelectField3dCustom(unionExpr, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom(), aliases3dCustom);
				}
				
				unionExpr.addSelectField(credenzialeFieldGroupBy, aliasFieldCredenzialeMittente);
				
				// Espressione finta per usare l'ordinamento
				fakeExpr = this.dao.newExpression();
				unionExprFake = new UnionExpression(fakeExpr);
				unionExprFake.addSelectField(new ConstantField(aliasFieldCredenzialeMittente, 
						StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, credenzialeFieldGroupBy.getFieldType()), 
						aliasFieldCredenzialeMittente);

			}
			 			
			Union union = new Union();
			union.setUnionAll(true);
			
			// select field by data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
				union.addField(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addField(a3dCustom);
				}
			}
			
			union.addField(aliasFieldCredenzialeMittente);
			if(forceErogazione || forceFruizione){
				if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
					if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
						if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
							union.addField(aliasFieldTipoSoggetto);
							union.addField(aliasFieldSoggetto);
							//				union.addField(aliasFieldRuoloSoggetto);
						}
					}
				}
			}
			
			// select group by data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
				union.addGroupBy(ALIAS_FIELD_DATA_3D);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					union.addGroupBy(a3dCustom);
				}
			}
			
			union.addGroupBy(aliasFieldCredenzialeMittente);
			if(forceErogazione || forceFruizione){
				if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
					if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
						if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
							union.addGroupBy(aliasFieldTipoSoggetto);
							union.addGroupBy(aliasFieldSoggetto);
							//				union.addGroupBy(aliasFieldRuoloSoggetto);
						}
					}
				}
			}

			UnionExpression [] uExpressions = new UnionExpression[2];
			int indexUE = 0;
			boolean fake = false;
			if(forceErogazione){
				uExpressions[indexUE++] = unionExprErogatore;
			}
			if(forceFruizione){
				uExpressions[indexUE++] = unionExprFruitore;
			}
			if(indexUE == 0){
				uExpressions[indexUE++] = unionExpr;
			}
			if(indexUE == 1){
				uExpressions[indexUE] = unionExprFake;
				fake = true;
			}
			
			
			// ordinamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
				SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
				union.addOrderBy(ALIAS_FIELD_DATA_3D,s);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
				for (String a3dCustom : aliases3dCustom) {
					SortOrder s = 	this.distribSaSearch.getSortOrder() != null ? 	this.distribSaSearch.getSortOrder() : SortOrder.ASC;
					union.addOrderBy(a3dCustom, s);
				}
			}
			
			TipoVisualizzazione tipoVisualizzazione = this.distribSaSearch.getTipoVisualizzazione();
			String sommaAliasName = "somma";
			String datoParamAliasName = "dato";
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.distribSaSearch.getTipoBanda();

				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);

				switch (tipoBanda) {
				case COMPLESSIVA:
					if(unionExprErogatore!=null){
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExpr!=null){
						unionExpr.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExprFake!=null){
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva",
								Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, datoParamAliasName));
					}
					break;
				case INTERNA:
					if(unionExprErogatore!=null){
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_INTERNA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_INTERNA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExpr!=null){
						unionExpr.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_INTERNA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExprFake!=null){
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna",
								Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, datoParamAliasName));
					}
					break;
				case ESTERNA:
					if(unionExprErogatore!=null){
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_ESTERNA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_ESTERNA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExpr!=null){
						unionExpr.addSelectFunctionField(new FunctionField(
								model.DIMENSIONI_BYTES_BANDA_ESTERNA,
								Function.SUM, datoParamAliasName));
					}
					if(unionExprFake!=null){
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna",
								Integer.valueOf(0), model.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, datoParamAliasName));
					}
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.SUM, datoParamAliasName);
				
				if(unionExprErogatore!=null){
					unionExprErogatore.addSelectFunctionField(new FunctionField(
							model.NUMERO_TRANSAZIONI, Function.SUM,
							datoParamAliasName));
				}
				if(unionExprFruitore!=null){
					unionExprFruitore.addSelectFunctionField(new FunctionField(
							model.NUMERO_TRANSAZIONI, Function.SUM,
							datoParamAliasName));
				}
				if(unionExpr!=null){
					unionExpr.addSelectFunctionField(new FunctionField(
							model.NUMERO_TRANSAZIONI, Function.SUM,
							datoParamAliasName));
				}
				if(unionExprFake!=null){
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni", 
							Integer.valueOf(0), model.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, datoParamAliasName));
				}
				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.distribSaSearch.getTipoLatenza();

				String datoParamRichiesteAliasName = "dato_richieste";
				
				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				/**union.addField(sommaAliasName, Function.AVG, datoParamAliasName);*/
				if(fake) {
					union.addField(sommaAliasName, Function.SUM, datoParamAliasName);
				}
				else {
					union.addField("somma_richieste", Function.SUM, datoParamRichiesteAliasName); // dato non utilizzato, ma richiesto per avere lo stesso numero dei dati ritornati nelle expression
					union.addCustomField(sommaAliasName, StatisticheUtils.getSqlCalcolaMedia(datoParamAliasName, datoParamRichiesteAliasName));

					if(unionExprErogatore!=null){
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.NUMERO_TRANSAZIONI, Function.SUM,
								datoParamRichiesteAliasName));
					}
					if(unionExprFruitore!=null){
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.NUMERO_TRANSAZIONI, Function.SUM,
								datoParamRichiesteAliasName));
					}
				}

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					if(unionExprErogatore!=null){
						gByExprErogazione.isNotNull(model.LATENZA_PORTA);
/**						unionExprErogatore.addSelectFunctionField(new FunctionField(
//								model.LATENZA_PORTA,
//								Function.AVG, datoParamAliasName));*/
						unionExprErogatore.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						gByExprFruizione.isNotNull(model.LATENZA_PORTA);
/**						unionExprFruitore.addSelectFunctionField(new FunctionField(
//								model.LATENZA_PORTA,
//								Function.AVG, datoParamAliasName));*/
						unionExprFruitore.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExpr!=null){
						gByExpr.isNotNull(model.LATENZA_PORTA);
/**						unionExpr.addSelectFunctionField(new FunctionField(
//								model.LATENZA_PORTA,
//								Function.AVG, datoParamAliasName));*/
						unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_PORTA, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExprFake!=null){
						fakeExpr.isNotNull(model.LATENZA_PORTA);
/**						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
//								Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, datoParamAliasName));*/
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta", 
								Integer.valueOf(0), model.LATENZA_PORTA.getFieldType()), Function.SUM, datoParamAliasName));
					}
					break;
				case LATENZA_SERVIZIO:
					if(unionExprErogatore!=null){
						gByExprErogazione.isNotNull(model.LATENZA_SERVIZIO);
/**						unionExprErogatore.addSelectFunctionField(new FunctionField(
//								model.LATENZA_SERVIZIO,
//								Function.AVG, datoParamAliasName));*/
						unionExprErogatore.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						gByExprFruizione.isNotNull(model.LATENZA_SERVIZIO);
/**						unionExprFruitore.addSelectFunctionField(new FunctionField(
//								model.LATENZA_SERVIZIO,
//								Function.AVG, datoParamAliasName));*/
						unionExprFruitore.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExpr!=null){
						gByExpr.isNotNull(model.LATENZA_SERVIZIO);
/**						unionExpr.addSelectFunctionField(new FunctionField(
//								model.LATENZA_SERVIZIO,
//								Function.AVG, datoParamAliasName));*/
						unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_SERVIZIO, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExprFake!=null){
						fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
/**						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio",
//								Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, datoParamAliasName));*/
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
								Integer.valueOf(0), model.LATENZA_SERVIZIO.getFieldType()), Function.SUM, datoParamAliasName));
					}
					break;

				case LATENZA_TOTALE:
				default:
					if(unionExprErogatore!=null){
						gByExprErogazione.isNotNull(model.LATENZA_TOTALE);
/**						unionExprErogatore.addSelectFunctionField(new FunctionField(
//								model.LATENZA_TOTALE,
//								Function.AVG, datoParamAliasName));*/
						unionExprErogatore.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						gByExprFruizione.isNotNull(model.LATENZA_TOTALE);
/**						unionExprFruitore.addSelectFunctionField(new FunctionField(
//								model.LATENZA_TOTALE,
//								Function.AVG, datoParamAliasName));*/
						unionExprFruitore.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExpr!=null){
						gByExpr.isNotNull(model.LATENZA_TOTALE);
/**						unionExpr.addSelectFunctionField(new FunctionField(
//								model.LATENZA_TOTALE,
//								Function.AVG, datoParamAliasName));*/
						unionExpr.addSelectFunctionField(StatisticheUtils.calcolaMedia(fieldConverter, model.LATENZA_TOTALE, model.NUMERO_TRANSAZIONI, datoParamAliasName));
					}
					if(unionExprFake!=null){
						fakeExpr.isNotNull(model.LATENZA_TOTALE);
/**						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
//								Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, datoParamAliasName));*/
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
								Integer.valueOf(0), model.LATENZA_TOTALE.getFieldType()), Function.SUM, datoParamAliasName));
					}
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<>();

			boolean countApplicativo = false;
			int elementi = 0;
			if(start != null && limit!=null && !this.distribSaSearch.isUseCount()) {
				countApplicativo = true;
			}
			
			if(!countApplicativo && start != null) {
				union.setOffset(start);
			}
			if(!countApplicativo && start != null && limit!=null) {
				union.setLimit(limit);
			}

			this.timeoutEvent = false;
			
			List<Map<String, Object>> list = null;
			if(this.timeoutRicerche == null) {
				list = this.dao.union(union, uExpressions);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.union(union, uExpressions)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			if (list != null) {

				for (Map<String, Object> row : list) {

					String risultato = (String) row.get(aliasFieldCredenzialeMittente);
					
					if(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE.equals(risultato)) {
						continue;
					} 
					if(risultato!=null && risultato.contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) {
						continue;
					}
					
					ResDistribuzione r = null;
					if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3D();

						// setto la data e la sua versione formattata in funziona dell'unita' temporale scelta
						Date data = ((Date) row.get(ALIAS_FIELD_DATA_3D));
						((ResDistribuzione3D)r).setData(data);
						((ResDistribuzione3D)r).setDataFormattata(StatsUtils.formatDate(tipologia, data));
					}
					else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
						r = new ResDistribuzione3DCustom();

						StringBuilder resFailure = new StringBuilder();
						try {
							BooleanNullable bSkip = BooleanNullable.NULL();
							String customData = getCustomData(row, this.distribSaSearch.getNumeroDimensioniCustom(), this.distribSaSearch, resFailure, bSkip);
							if(bSkip!=null && bSkip.getValue()!=null && bSkip.getValue().booleanValue()) {
								continue;
							}
							if(customData==null) {
								if(resFailure.length()<=0) {
									resFailure.append("-?-");
								}
								throw new CoreException("Informazione personalizzata non presente");
							}
							((ResDistribuzione3DCustom)r).setDatoCustom(customData);
						}catch(Exception t) {
							((ResDistribuzione3DCustom)r).setDatoCustom("SA '"+risultato+"' - "+resFailure.toString());
							StatisticheGiornaliereService.logError("Traduzione SA("+risultato+") '"+resFailure.toString()+"' non riuscita: "+t.getMessage(), t);
						}
					}
					else {
						r = new ResDistribuzione();
					}
										
					String risultatoIdLongCredenzialeMittente = risultato;
										
					StringBuilder sbTipoCredenziale = new StringBuilder();
					risultato = this.getLabelCredenzialeFieldGroupBy(risultato, this.distribSaSearch, sbTipoCredenziale);
					
					boolean addSoggetto = true;
					
					if(	StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(	this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							if (StringUtils.isNotBlank(this.distribSaSearch.getIdentificazione()) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
								try {
									IDServizioApplicativo idApplicativo = CredenzialeTokenClient.convertApplicationDBValueToOriginal(risultato);
									if(idApplicativo!=null) {
										r.getParentMap().put("0",idApplicativo.getIdSoggettoProprietario().getTipo() + "/" + idApplicativo.getIdSoggettoProprietario().getNome());
										
										try {
											if(CredenzialeTokenClient.isClientIdDBValue(risultato)) {
												String clientId = CredenzialeTokenClient.convertClientIdDBValueToOriginal(risultato);
												r.getParentMap().put("1", clientId);
											}
											else {
												r.getParentMap().put("1", Costanti.INFORMAZIONE_NON_DISPONIBILE);
											}
										}catch(Exception tAppId) {
											StatisticheGiornaliereService.logError(tAppId.getMessage(), tAppId);
											r.getParentMap().put("1", Costanti.INFORMAZIONE_NON_DISPONIBILE);
										}
										
										risultato=idApplicativo.getNome();
										addSoggetto = false;
									}
									else {
										// informazione precedente o che non contiene un applicativo
										continue;
									}
								}catch(Exception tApp) {
									StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
									continue;
								}
							}
						}
						else if(	this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
							try {
								org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(this.distribSaSearch.getTokenClaim(),true);
								if(org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_CLIENT_ID.equals(tcm)) {
									boolean add = false;
									try {
										IDServizioApplicativo idApplicativo = CredenzialeTokenClient.convertApplicationDBValueToOriginal(risultato);
										if(idApplicativo!=null) {
											String soggetto = NamingUtils.getLabelSoggetto(idApplicativo.getIdSoggettoProprietario());
											r.getParentMap().put("0", idApplicativo.getNome());
											r.getParentMap().put("1", soggetto );
											add = true;
										}
									}catch(Exception tApp) {
										StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
									}
									if(!add) {
										r.getParentMap().put("0", Costanti.INFORMAZIONE_NON_DISPONIBILE);
										r.getParentMap().put("1", Costanti.INFORMAZIONE_NON_DISPONIBILE );
									}
									
									risultato=CredenzialeTokenClient.convertClientIdDBValueToOriginal(risultato);
								}
								else if(org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.equals(tcm)) {
									
									String organizzazione = null;
									try {
										MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
										CredenzialeMittente credenzialeMittentePDNDOrganization = mBeanUtilsService.getCredenzialeMittenteByReferenceFromCache(TipoCredenzialeMittente.PDND_ORGANIZATION_NAME,Long.parseLong(risultatoIdLongCredenzialeMittente));
										if(credenzialeMittentePDNDOrganization!=null) {
											organizzazione = credenzialeMittentePDNDOrganization.getCredenziale();
										}
									}catch(Exception tApp) {
										StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
									}
									if(organizzazione==null) {
										// informazione che non contiene la risoluzione del clientId
										/**if(limit!=null) {
											// siamo nella visualizzazione a tabella, in questo caso poiche' viene calcoltata anche la count prima, che non considera il filtro applicativo del 'continue',
											// ritorno l'informazione non disponibile
											organizzazione = Costanti.INFORMAZIONE_NON_DISPONIBILE;
										}
										else {
										Non va bene perchè l'export presenta solo i dati con pdnd, ed inoltre non e' in linea con gli altri report.
										La soluzione e' invece eliminare il count dalla tabella visualizzata!
										*
										*/
										continue;
										/**}*/
									}
									
									String categoria = null;
									String extIdOrigin = null;
									String extIdCode = null;
									String extId = null;
									String categoriaIdPdnd = null;
									try {
										MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
										CredenzialeMittente credenzialeMittentePDNDjson = mBeanUtilsService.getCredenzialeMittenteByReferenceFromCache(TipoCredenzialeMittente.PDND_ORGANIZATION_JSON,Long.parseLong(risultatoIdLongCredenzialeMittente));
										if(credenzialeMittentePDNDjson!=null && credenzialeMittentePDNDjson.getCredenziale()!=null) {
											PDNDTokenInfoDetails d = new PDNDTokenInfoDetails();
											d.setDetails(credenzialeMittentePDNDjson.getCredenziale());
											PDNDTokenInfo info = new PDNDTokenInfo();
											info.setOrganization(d);
											
											categoria = info.getOrganizationCategory(StatisticheGiornaliereService.log);
											
											extIdOrigin = info.getOrganizationExternalOrigin(StatisticheGiornaliereService.log);
											extIdCode = info.getOrganizationExternalId(StatisticheGiornaliereService.log);
											if(extIdOrigin!=null && extIdCode!=null) {
												extId = extIdOrigin + " " + extIdCode;
											}
											else if(extIdOrigin!=null) {
												extId = extIdOrigin;
											}
											else {
												extId = extIdCode;
											}
											
											categoriaIdPdnd = info.getOrganizationId(StatisticheGiornaliereService.log);
										}
									}catch(Exception tApp) {
										StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
									}
									if(categoria==null) {
										categoria = Costanti.INFORMAZIONE_NON_DISPONIBILE;
									}
									if(extId==null) {
										extId = Costanti.INFORMAZIONE_NON_DISPONIBILE;
									}
									if(categoriaIdPdnd==null) {
										categoriaIdPdnd = Costanti.INFORMAZIONE_NON_DISPONIBILE;
									}
									
									IDServizioApplicativo idApplicativo = null;
									if(this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato) {
										try {
											idApplicativo = CredenzialeTokenClient.convertApplicationDBValueToOriginal(risultato);
										}catch(Exception tApp) {
											StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
										}
									}
									
									risultato=CredenzialeTokenClient.convertClientIdDBValueToOriginal(risultato);
									String clientId = risultato;
									
									// Devo invertire. Il dato principale deve essere l'organizzazione.
									risultato = organizzazione;
									
									r.getParentMap().put("0", clientId);
									r.getParentMap().put("1", extId);
									r.getParentMap().put("2", categoria);
									
									// non visualizzato nel report grafico ma solo negli export pdf, csv ...
									r.getParentMap().put("3", categoriaIdPdnd);
									
									if(this.isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato) {
										if(idApplicativo!=null) {
											String soggetto = NamingUtils.getLabelSoggetto(idApplicativo.getIdSoggettoProprietario());
											r.getParentMap().put("4", idApplicativo.getNome());
											r.getParentMap().put("5", soggetto );
										}
										else {
											r.getParentMap().put("4", Costanti.INFORMAZIONE_NON_DISPONIBILE);
											r.getParentMap().put("5", Costanti.INFORMAZIONE_NON_DISPONIBILE );
										}
									}
								}
							}catch(Exception t) {
								StatisticheGiornaliereService.logError(t.getMessage(), t);
							}
						}
						else if(	this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
							
							String tipoCredenziale = convertToTipoCredenziale(sbTipoCredenziale.toString());
							r.getParentMap().put("0", tipoCredenziale);
							
						}

					}
					
					r.setRisultato(risultato);
					
					if(forceErogazione || forceFruizione){
						if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
							if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
								if(addSoggetto) {
									r.getParentMap().put("0",((String) row.get(aliasFieldTipoSoggetto)) + "/" + ((String) row.get(aliasFieldSoggetto)));
								}
							}
						}
						
						//r.getParentMap().put("1",((String) row.get(aliasFieldRuoloSoggetto)));
					}
					
					Number somma = StatsUtils.converToNumber(row.get(sommaAliasName));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					/**System.out.println("============");
					System.out.println("actual-size:"+res.size()+" elementi:"+elementi+" start:"+start+" limit:"+limit+"");*/
					if(countApplicativo) {
						elementi++;
						if(elementi<=start) {
							/**System.out.println("CONTINUE");*/
							continue; 
						}
						if(res.size()>=limit) {
							/**System.out.println("BREAK");*/
							break;
						}
					}
					
					res.add(r);
				}

			}

			return res;
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return new ArrayList<>();
	}
	private String convertToTipoCredenziale(String tipo) {
		String tipoCredenziale = "-";
		if(tipo.length()>0 && tipo.contains("_")) {
			String [] tmp = tipo.split("_");
			if(tmp!=null && tmp.length>1 && tmp[1]!=null && StringUtils.isNotEmpty(tmp[1])) {
				tipoCredenziale = tmp[1];
			}
		}
		if(TipoAutenticazione.SSL.getValue().equals(tipoCredenziale)) {
			tipoCredenziale = TipoAutenticazione.SSL.getLabel(); 
		}
		else if(TipoAutenticazione.BASIC.getValue().equals(tipoCredenziale)) {
			tipoCredenziale = TipoAutenticazione.BASIC.getLabel(); 
		}
		else if(TipoAutenticazione.APIKEY.getValue().equals(tipoCredenziale)) {
			tipoCredenziale = TipoAutenticazione.APIKEY.getLabel(); 
		}
		else if(TipoAutenticazione.PRINCIPAL.getValue().equals(tipoCredenziale)) {
			tipoCredenziale = TipoAutenticazione.PRINCIPAL.getLabel(); 
		}
		return tipoCredenziale;
	}

	private IExpression createDistribuzioneServizioApplicativoExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, boolean isCount,
			boolean forceErogazione, boolean forceFruizione) throws ServiceException{
		IExpression expr = null;

		StatisticheGiornaliereService.logDebug("creo Expression per distribuzione sa!");

		List<Soggetto> listaSoggettiGestione = this.distribSaSearch.getSoggettiGestione();

		try {
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribSaSearch.getProtocollo());
			
			expr = dao.newExpression();
			// Data
			expr.between(model.DATA, this.distribSaSearch.getDataInizio(),	this.distribSaSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(expr, model);
			
			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
	//			if (StringUtils.isNotEmpty(this.distribSaSearch.getProtocollo()) && this.distribSaSearch.isShowListaProtocolli()) {
			if (this.distribSaSearch.isSetFiltroProtocollo()) {
				//				expr.and().equals(model.PROTOCOLLO,	this.distribSaSearch.getProtocollo());
				protocollo = this.distribSaSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo, this.distribSaSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.distribSaSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.distribSaSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.distribSaSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSaSearch.getSoggettoLocale()) && 
					!"--".equals(this.distribSaSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.distribSaSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.distribSaSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.distribSaSearch.getNomeAzione()))
				expr.and().equals(model.AZIONE,	this.distribSaSearch.getNomeAzione());

			// nome servizio
			if (StringUtils.isNotBlank(this.distribSaSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSaSearch.getNomeServizio());
				
				expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			}

			// esito
			esitoUtils.setExpression(expr, this.distribSaSearch.getEsitoGruppo(), 
					this.distribSaSearch.getEsitoDettaglio(),
					this.distribSaSearch.getEsitoDettaglioPersonalizzato(),
					this.distribSaSearch.getEsitoContesto(),
					this.distribSaSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// ho 3 diversi tipi di query in base alla tipologia di ricerca

			// imposto il soggetto (loggato) come mittente o destinatario in
			// base
			// alla tipologia di ricerca selezionata
			if (   !forceErogazione 
					&& 
					!forceFruizione 
					&&
					(this.distribSaSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.distribSaSearch.getTipologiaRicercaEnum()))
				) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome
				// del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils
						.isNotBlank(this.distribSaSearch
								.getTrafficoPerSoggetto());
				boolean soggetto = !listaSoggettiGestione.isEmpty();
				String tipoTrafficoSoggetto = null;
				String nomeTrafficoSoggetto = null;
				if (trafficoSoggetto) {
					tipoTrafficoSoggetto = this.distribSaSearch
							.getTipoTrafficoPerSoggetto();
					nomeTrafficoSoggetto = this.distribSaSearch
							.getTrafficoPerSoggetto();
				}

				IExpression e1 = dao.newExpression();
				IExpression e2 = dao.newExpression();

				// se trafficoSoggetto e soggetto sono impostati allora devo
				// fare la
				// OR
				if (trafficoSoggetto && soggetto) {
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					e1.and().equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.and().equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);

					// OR
					expr.or(e1, e2);
				} else if (trafficoSoggetto && !soggetto) {
					// il mio soggetto non e' stato impostato (soggetto in
					// gestione,
					// puo succedero solo in caso admin)
					expr.and();

					e1.equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO,
							nomeTrafficoSoggetto);

					e2.equals(model.TIPO_MITTENTE,
							tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE,
							nomeTrafficoSoggetto);
					// OR
					expr.or(e1, e2);
				} else if (!trafficoSoggetto && soggetto) {
					// e' impostato solo il soggetto in gestione
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(
									model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					// OR
					expr.or(e1, e2);
				} else {
					// nessun filtro da impostare
				}

			} else if ( forceErogazione || TipologiaRicerca.ingresso.equals(this.distribSaSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.distribSaSearch);
				if (StringUtils.isNotBlank(this.distribSaSearch.getNomeMittente()) && !ignoreSetMittente) {
					expr.and().equals(model.TIPO_MITTENTE,
							this.distribSaSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,
							this.distribSaSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().notEquals(model.TIPO_PORTA,
						"applicativa");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,
								soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribSaSearch
						.getNomeDestinatario())) {
					expr.and().equals(model.TIPO_DESTINATARIO,
							this.distribSaSearch.getTipoDestinatario());
					expr.and().equals(model.DESTINATARIO,
							this.distribSaSearch.getNomeDestinatario());
				}
			}

			// raggruppamento per data in caso di visualizzazione a 3 dimensioni
			if(NumeroDimensioni.DIMENSIONI_3.equals(this.distribSaSearch.getNumeroDimensioni())) {
				expr.addGroupBy(model.DATA);
			} else if(NumeroDimensioni.DIMENSIONI_3_CUSTOM.equals(this.distribSaSearch.getNumeroDimensioni())) {
				impostaGroupByFiltro3dCustom(expr, model, this.distribSaSearch, this.distribSaSearch.getNumeroDimensioniCustom());
			}
			
			this.impostaGroupByFiltroDatiMittente(expr, this.distribSaSearch, model, isCount); 
			
			this.impostaFiltroGruppo(expr, this.distribSaSearch, model, isCount);
			
			this.impostaFiltroApi(expr, this.distribSaSearch, model, isCount);

			this.impostaFiltroIdClusterOrCanale(expr, this.distribSaSearch, model, isCount);
			
			// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
			// Poichè gli applicativi sono identificati univocamente insieme anche al soggetto proprietario, si aggiunge il soggetto nella group by
			if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
				if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					if (!org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
						expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
						expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
						expr.addGroupBy(model.TIPO_MITTENTE);
						expr.addGroupBy(model.MITTENTE);
					}
				}
			}
			
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}

		return expr;
	}
	
	
	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PERSONALIZZATA ******************
	
	@Override
	public int countAllDistribuzionePersonalizzata() throws ServiceException{
		try {
			NonNegativeNumber nnn = executeDistribuzionePersonalizzataCount();

			return nnn != null ? ((int) nnn.longValue()) : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		}

		//		return 0;

	}

	@Override
	public List<ResDistribuzione> findAllDistribuzionePersonalizzata() throws ServiceException{
		try {
			List<ResDistribuzione> res = executeDistribuzionePersonalizzataSearch(false, false, -1, -1);
			return res;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		}
		//		return new ArrayList<>();
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzionePersonalizzata(int start,	int limit) throws ServiceException{
		try {
			List<ResDistribuzione> res = executeDistribuzionePersonalizzataSearch(false, true, start, limit);
			return res;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		}
		//		return new ArrayList<>();
	}

	private List<ResDistribuzione> executeDistribuzionePersonalizzataSearch(boolean isCount, boolean isPaginated, int offset, int limit) throws ServiceException{

		StatisticaModel model = null;
		StatisticaContenutiModel modelContenuti = null;
//		IServiceSearchWithoutId<?> dao = null;

		StatisticType tipologia = this.statistichePersonalizzateSearch.getModalitaTemporale(); 
		try {
			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				modelContenuti = StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				modelContenuti = StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI;
				this.dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				modelContenuti = StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI;
				this.dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				modelContenuti = StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI;
				this.dao = this.statSettimanaleSearchDAO;
				break;

			}
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			if(modelContenuti==null) {
				throw new ServiceException("ModelContenuti unknown");
			}
			
			List<Index> forceIndexes = null;
			try{
				if(isCount){
					forceIndexes = 
							this.convertForceIndexList(model, 
									this.govwayMonitorProperties.getStatisticheForceIndexPersonalizzataDistribuzioneCount(tipologia, 
											this.govwayMonitorProperties.getExternalForceIndexRepository()));
				}
				else{
					forceIndexes = 
							this.convertForceIndexList(model, 
									this.govwayMonitorProperties.getStatisticheForceIndexPersonalizzataDistribuzioneGroupBy(tipologia, 
											this.govwayMonitorProperties.getExternalForceIndexRepository()));
				}
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}

			IExpression gByExpr = createDistribuzionePersonalizzataExpression(this.dao,
					model, modelContenuti, isCount);

			gByExpr.sortOrder(SortOrder.ASC).addOrder(modelContenuti.RISORSA_VALORE);

			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			unionExpr.addSelectField(modelContenuti.RISORSA_VALORE, "nome_risorsa");

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = this.dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
			unionExprFake.addSelectField(new ConstantField("nome_risorsa", 
					StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,modelContenuti.RISORSA_VALORE.getFieldType()), "nome_risorsa");

			Union union = new Union();
			union.setUnionAll(true);
			union.addField("nome_risorsa");
			union.addGroupBy("nome_risorsa");

			TipoVisualizzazione tipoVisualizzazione = this.statistichePersonalizzateSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				TipoBanda tipoBanda = this.statistichePersonalizzateSearch.getTipoBanda();

				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");

				switch (tipoBanda) {
				case COMPLESSIVA:
					unionExpr.addSelectFunctionField(new FunctionField(
							modelContenuti.DIMENSIONI_BYTES_BANDA_COMPLESSIVA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_complessiva",
							Integer.valueOf(0), modelContenuti.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()), Function.SUM, "dato"));
					break;
				case INTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							modelContenuti.DIMENSIONI_BYTES_BANDA_INTERNA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_interna",
							Integer.valueOf(0), modelContenuti.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()), Function.SUM, "dato"));
					break;
				case ESTERNA:
					unionExpr.addSelectFunctionField(new FunctionField(
							modelContenuti.DIMENSIONI_BYTES_BANDA_ESTERNA,
							Function.SUM, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("banda_esterna",
							Integer.valueOf(0), modelContenuti.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()), Function.SUM, "dato"));
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.SUM, "dato");
				unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("numero_transazioni", 
						Integer.valueOf(0), modelContenuti.NUMERO_TRANSAZIONI.getFieldType()), Function.SUM, "dato"));

				unionExpr.addSelectFunctionField(new FunctionField(
						modelContenuti.NUMERO_TRANSAZIONI, Function.SUM,
						"dato"));
				break;

			case TEMPO_MEDIO_RISPOSTA:{

				TipoLatenza tipoLatenza = this.statistichePersonalizzateSearch.getTipoLatenza();

				union.addOrderBy("somma",SortOrder.DESC);
				union.addField("somma", Function.AVG, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(modelContenuti.LATENZA_PORTA);
					gByExpr.isNotNull(modelContenuti.LATENZA_PORTA);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
							Integer.valueOf(1),modelContenuti.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));

					unionExpr.addSelectFunctionField(new FunctionField(
							modelContenuti.LATENZA_PORTA,
							Function.AVG, "dato"));
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(modelContenuti.LATENZA_SERVIZIO);
					gByExpr.isNotNull(modelContenuti.LATENZA_SERVIZIO);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(1), modelContenuti.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));

					unionExpr.addSelectFunctionField(new FunctionField(
							modelContenuti.LATENZA_SERVIZIO,
							Function.AVG, "dato"));
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(modelContenuti.LATENZA_TOTALE);
					gByExpr.isNotNull(modelContenuti.LATENZA_TOTALE);
					unionExprFake.addSelectFunctionField(new FunctionField(
							new ConstantField("latenza_totale", Integer.valueOf(1), modelContenuti.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));

					unionExpr.addSelectFunctionField(new FunctionField(
							modelContenuti.LATENZA_TOTALE,
							Function.AVG, "dato"));
					break;
				}
				break;
			}
			}


			if(isPaginated)
				union.setOffset(offset);
			if(isPaginated)
				union.setLimit(limit);

			this.timeoutEvent = false;
			
			List<Map<String, Object>> list = null;
			if(this.timeoutRicerche == null) {
				list = this.dao.union(union, unionExpr, unionExprFake);
			} else {
				try {
					list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.union(union, unionExpr, unionExprFake)).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					StatisticheGiornaliereService.logError(e.getMessage(), e);
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					if(e.getCause() instanceof NotFoundException) {
						throw (NotFoundException) e.getCause();
					}
					if(e.getCause() instanceof ServiceException) {
						throw (ServiceException) e.getCause();
					}
					if(e.getCause() instanceof NotImplementedException) {
						throw (NotImplementedException) e.getCause();
					}
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				} catch (TimeoutException e) {
					this.timeoutEvent = true;
					StatisticheGiornaliereService.logError(e.getMessage(), e);
				}
			}
			List<ResDistribuzione> res = new ArrayList<>();
			//			Map<String, ResDistribuzione> map = new HashMap<String, ResDistribuzione>();
			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					r.setRisultato(((String) row.get("nome_risorsa")));

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					if(!r.getRisultato().contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE))
						res.add(r);
					//						map.put(r.getSomma()+"_"+r.getRisultato(), r);
				}

			}

			// order by somma
			//			Enumeration<String> enKeys = map.keys();
			//			List<String> order = new ArrayList<>();
			//			while (enKeys.hasMoreElements()) {
			//				String key = (String) enKeys.nextElement();
			//				order.add(key);
			//			}
			//			Collections.sort(order,Collections.reverseOrder());
			//			// risultato ordinato
			//			List<ResDistribuzione> res = new ArrayList<>();
			//			for (String key : order) {
			//				res.add(map.get(key));
			//			}

			return res;

		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		}

		return new ArrayList<>();

	}

	private NonNegativeNumber executeDistribuzionePersonalizzataCount()	throws ServiceException, NotImplementedException {

		StatisticaModel model = null;
		IServiceSearchWithoutId<?> dao = null;
		StatisticaContenutiModel modelContenuti = null;
		StatisticType tipologia = this.statistichePersonalizzateSearch.getModalitaTemporale();  

		switch (tipologia) {
		case GIORNALIERA:
			model = StatisticaGiornaliera.model().STATISTICA_BASE;
			modelContenuti = StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI;
			dao = this.statGiornaliereSearchDAO;
			break;
		case MENSILE:
			model = StatisticaMensile.model().STATISTICA_BASE;
			modelContenuti = StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI;
			dao = this.statMensileSearchDAO;
			break;
		case ORARIA:
			model = StatisticaOraria.model().STATISTICA_BASE;
			modelContenuti = StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI;
			dao = this.statOrariaSearchDAO;
			break;
		case SETTIMANALE:
			model = StatisticaSettimanale.model().STATISTICA_BASE;
			modelContenuti = StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI;
			dao = this.statSettimanaleSearchDAO;
			break;
		}
		if(dao==null) {
			throw new ServiceException("DAO unknown");
		}
		if(model==null) {
			throw new ServiceException("Model unknown");
		}
		if(modelContenuti==null) {
			throw new ServiceException("ModelContenuti unknown");
		}
		
		List<Index> forceIndexes = null;
		try{
			forceIndexes = 
					this.convertForceIndexList(model, 
							this.govwayMonitorProperties.getStatisticheForceIndexPersonalizzataDistribuzioneCount(tipologia, 
									this.govwayMonitorProperties.getExternalForceIndexRepository()));
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}

		IExpression expr = createDistribuzionePersonalizzataExpression(dao,	model, modelContenuti, true);

		if(forceIndexes!=null && !forceIndexes.isEmpty()){
			for (Index index : forceIndexes) {
				try{
					expr.addForceIndex(index);
				}catch(Exception e){
					throw new ServiceException(e.getMessage(),e);
				}
			}
		}
		
		return dao.count(expr);
	}

	private IExpression createDistribuzionePersonalizzataExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model,
			StatisticaContenutiModel modelContenuti, boolean isCount) throws ServiceException{
		IExpression expr = null;

		FilterImpl report = (FilterImpl) this.statistichePersonalizzateSearch
				.getFiltroReport();

		try {
			expr = parseStatistichePersonalizzateFilter(dao, model, modelContenuti);
			expr.and();
			this.impostaFiltroDatiMittente(expr, this.statistichePersonalizzateSearch, model, isCount);
			
			this.impostaFiltroGruppo(expr, this.statistichePersonalizzateSearch, model, isCount);
			
			this.impostaFiltroApi(expr, this.statistichePersonalizzateSearch, model, isCount);

			this.impostaFiltroIdClusterOrCanale(expr, this.statistichePersonalizzateSearch, model, isCount);

			// String idRisorsaAggregare = "RISORSA_DA_AGGREGARE";
			//
			// queryString.append(" AND ");
			// queryString.append(FilterStatisticRepositoryImpl.getStatisticJoins(
			// tipologia, idRisorsaAggregare));

			// Risorsa da aggregare indica la statistica per cui aggregare, deve coincidere nel campo risorsa_nome
			String nomeStatisticaPersonalizzata = this.statistichePersonalizzateSearch.getStatisticaSelezionata().getIdConfigurazioneStatistica();
			if(report!=null && report.getIdStatistic()!=null){
				expr.like(modelContenuti.RISORSA_NOME, nomeStatisticaPersonalizzata+"-"+report.getIdStatistic(),LikeMode.EXACT);
			}
			else{
				expr.like(modelContenuti.RISORSA_NOME, nomeStatisticaPersonalizzata,LikeMode.EXACT);
			}

			//
			if (report != null) {
				expr.and(report.getExpression());
				// report.
				// queryString.append(" AND ");
				//
				// FilterImpl filtro = report;
				// queryString.append(filtro.getJPAFilter(tipologia)
				// .getSqlConditions());
				// queryString.append(" ");
			}
			//
			// // raggruppo per data
			// queryString.append(" GROUP BY ");
			// queryString.append(FilterStatisticRepositoryImpl
			// .getStatisticColumnName(tipologia));

			// condizione di groupby
			expr.addGroupBy(modelContenuti.RISORSA_VALORE);

			if (!isCount) {
				expr.sortOrder(SortOrder.ASC).addOrder(modelContenuti.RISORSA_VALORE);
			}

		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (UtilsException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw e;
		}  

		return expr;
	}

	@Override
	public Map<String, List<Res>> findAllAndamentoTemporalePersonalizzato() throws ServiceException{
		try {
			Map<String, List<Res>>  res = executeAndamentoTemporalePersonalizzatoSearch(false, true, -1, -1);
			return res;
		} catch (ServiceException e) {
			throw e;
			//			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
		//		return new HashMap<String, List<Res>>();
	}

	@Override
	public Map<String, List<Res>> findAllAndamentoTemporalePersonalizzato(int start, int limit) throws ServiceException{

		try {
			Map<String, List<Res>>  res = executeAndamentoTemporalePersonalizzatoSearch(false, true, start, limit);
			return res;
		} catch (ServiceException e){
			throw e;
		}
		//		return new HashMap<String, List<Res>>();
	}

	private Map<String, List<Res>> executeAndamentoTemporalePersonalizzatoSearch(boolean isCount, boolean isPaginated, int offset, int limit) throws  ServiceException{

		StatisticaModel model = null;
		StatisticaContenutiModel modelContenuti = null;
//		IServiceSearchWithoutId<?> dao = null;

		StatisticType tipologia = this.statistichePersonalizzateSearch.getModalitaTemporale(); 
		try {
			switch (tipologia) {
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				modelContenuti = StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI;
				this.dao = this.statGiornaliereSearchDAO;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				modelContenuti = StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI;
				this.dao = this.statMensileSearchDAO;
				break;
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				modelContenuti = StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI;
				this.dao = this.statOrariaSearchDAO;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				modelContenuti = StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI;
				this.dao = this.statSettimanaleSearchDAO;
				break;

			}
			if(this.dao==null) {
				throw new ServiceException("DAO unknown");
			}
			if(model==null) {
				throw new ServiceException("Model unknown");
			}
			if(modelContenuti==null) {
				throw new ServiceException("ModelContenuti unknown");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.dao).getFieldConverter(); 
			
			List<Index> forceIndexes = null;
			try{
				if(isCount){
					forceIndexes = 
							this.convertForceIndexList(model, 
									this.govwayMonitorProperties.getStatisticheForceIndexPersonalizzataAndamentoTemporaleCount(tipologia, 
											this.govwayMonitorProperties.getExternalForceIndexRepository()));
				}
				else{
					forceIndexes = 
							this.convertForceIndexList(model, 
									this.govwayMonitorProperties.getStatisticheForceIndexPersonalizzataAndamentoTemporaleGroupBy(tipologia, 
											this.govwayMonitorProperties.getExternalForceIndexRepository()));
				}
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}

			IExpression expr = createAndamentoTemporalePersonalizzatoExpression(this.dao, model, modelContenuti, isCount);

			boolean isLatenza = false;	
			boolean isLatenza_totale = false;	
			boolean isLatenza_servizio = false;	
			boolean isLatenza_porta = false;	
			boolean isBanda = false;
			boolean isBanda_complessiva = false;
			boolean isBanda_interna = false;
			boolean isBanda_esterna = false;
			List<FunctionField> listaFunzioni = new ArrayList<FunctionField>();
			TipoVisualizzazione tipoVisualizzazione = this.statistichePersonalizzateSearch.getTipoVisualizzazione();
			//			TipoVisualizzazione tipoVisualizzazione = this.andamentoTemporaleSearch.getTipoVisualizzazione();
			switch (tipoVisualizzazione) {
			case DIMENSIONE_TRANSAZIONI:

				isBanda = true;

				TipoBanda tipoBanda = this.statistichePersonalizzateSearch.getTipoBanda();

				switch (tipoBanda) {
				case COMPLESSIVA:
					listaFunzioni.add(new FunctionField(modelContenuti.DIMENSIONI_BYTES_BANDA_COMPLESSIVA, Function.SUM, "somma_banda_complessiva"));
					isBanda_complessiva = true;
					break;
				case INTERNA:
					listaFunzioni.add(new FunctionField(modelContenuti.DIMENSIONI_BYTES_BANDA_INTERNA, Function.SUM, "somma_banda_interna"));
					isBanda_interna = true;
					break;
				case ESTERNA:
					listaFunzioni.add(new FunctionField(modelContenuti.DIMENSIONI_BYTES_BANDA_ESTERNA, Function.SUM, "somma_banda_esterna"));
					isBanda_esterna = true;
					break;
				}
				break;

			case NUMERO_TRANSAZIONI:
				listaFunzioni.add(new FunctionField(modelContenuti.NUMERO_TRANSAZIONI,Function.SUM, "somma"));
				break;

			case TEMPO_MEDIO_RISPOSTA:{
				isLatenza = true;
				//				List<TipoLatenza> tipiLatenza = this.andamentoTemporaleSearch.getTipiLatenzaImpostati();
				TipoLatenza tipoLatenza = this.statistichePersonalizzateSearch.getTipoLatenza();

				//				for (TipoLatenza tipoLatenza : tipiLatenza) {
				switch (tipoLatenza) {
				case LATENZA_PORTA:
					expr.isNotNull(modelContenuti.LATENZA_PORTA);
					//listaFunzioni.add(new  FunctionField(modelContenuti.LATENZA_PORTA, Function.AVG, "somma_latenza_porta"));
					listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, modelContenuti.LATENZA_PORTA, modelContenuti.NUMERO_TRANSAZIONI, "somma_latenza_porta"));
					isLatenza_porta = true;
					break;
				case LATENZA_SERVIZIO:
					expr.isNotNull(modelContenuti.LATENZA_SERVIZIO);
					//listaFunzioni.add(new FunctionField(modelContenuti.LATENZA_SERVIZIO, Function.AVG, "somma_latenza_servizio"));
					listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, modelContenuti.LATENZA_SERVIZIO, modelContenuti.NUMERO_TRANSAZIONI, "somma_latenza_servizio"));
					isLatenza_servizio = true;
					break;

				case LATENZA_TOTALE:
				default:
					expr.isNotNull(modelContenuti.LATENZA_TOTALE);
					//listaFunzioni.add(new  FunctionField(modelContenuti.LATENZA_TOTALE, 	Function.AVG, "somma_latenza_totale"));
					listaFunzioni.add(StatisticheUtils.calcolaMedia(fieldConverter, modelContenuti.LATENZA_TOTALE, modelContenuti.NUMERO_TRANSAZIONI, "somma_latenza_totale"));
					isLatenza_totale = true;
					break;
				}
			}
			//			}
			}

			if(forceIndexes!=null && !forceIndexes.isEmpty()){
				for (Index index : forceIndexes) {
					expr.addForceIndex(index);
				}
			}
			
			this.timeoutEvent = false;
			
			List<Map<String, Object>> list = null;
			if (!isPaginated) {
				if(this.timeoutRicerche == null) {
					list = this.dao.groupBy(expr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
				} else {
					try {
						list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.groupBy(expr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						StatisticheGiornaliereService.logError(e.getMessage(), e);
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						if(e.getCause() instanceof NotFoundException) {
							throw (NotFoundException) e.getCause();
						}
						if(e.getCause() instanceof ServiceException) {
							throw (ServiceException) e.getCause();
						}
						if(e.getCause() instanceof NotImplementedException) {
							throw (NotImplementedException) e.getCause();
						}
						StatisticheGiornaliereService.logError(e.getMessage(), e);
					} catch (TimeoutException e) {
						this.timeoutEvent = true;
						StatisticheGiornaliereService.logError(e.getMessage(), e);
						return null;
					}
				}
			} else {
				IPaginatedExpression pagExpr = this.dao.toPaginatedExpression(expr);
				pagExpr.offset(offset).limit(limit);
				if(this.timeoutRicerche == null) {
					list = this.dao.groupBy(pagExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
				} else {
					try {
						list = ThreadExecutorManager.getClientPoolExecutorRicerche().submit(() -> this.dao.groupBy(pagExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]))).get(this.timeoutRicerche.longValue(), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						StatisticheGiornaliereService.logError(e.getMessage(), e);
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						if(e.getCause() instanceof NotFoundException) {
							throw (NotFoundException) e.getCause();
						}
						if(e.getCause() instanceof ServiceException) {
							throw (ServiceException) e.getCause();
						}
						if(e.getCause() instanceof NotImplementedException) {
							throw (NotImplementedException) e.getCause();
						}
						StatisticheGiornaliereService.logError(e.getMessage(), e);
					} catch (TimeoutException e) {
						this.timeoutEvent = true;
						StatisticheGiornaliereService.logError(e.getMessage(), e);
						return null;
					}
				}
			}

			// supporto
			TreeMap<String, List<Res>> mapRisultati = new TreeMap<String, List<Res>>();
			TreeMap<String, List<Date>> mapDateUsate = new TreeMap<String, List<Date>>();
			List<Date> listaDateUtilizzate = new ArrayList<>();

			// Scorro i risultati per generare gli elementi del grafico.
			if(list!=null) {
				for (Map<String, Object> row : list) {
					String valoreRisorsa = (String) row.get(GenericJDBCUtilities.getAlias(modelContenuti.RISORSA_VALORE));
	
					List<Res> res = null;
					List<Date> datePerRes = null;
	
					boolean nuovaEntry =true;
					if(mapRisultati.containsKey(valoreRisorsa)){
						nuovaEntry = false;
						res = mapRisultati.get(valoreRisorsa);
						datePerRes = mapDateUsate.get(valoreRisorsa);
					}
	
					if(nuovaEntry){
						res = new ArrayList<>();
						datePerRes = new ArrayList<>();
					}
	
	
					Date data = (Date) row.get(GenericJDBCUtilities.getAlias(model.DATA));
	
					//salvo la data trovata
					if(!listaDateUtilizzate.contains(data))
						listaDateUtilizzate.add(data);
	
					if(!datePerRes.contains(data))
						datePerRes.add(data);
	
					Res r = new Res();
					r.setId(data != null ? data.getTime() : null);
					r.setRisultato(data);
	
					//collezione dei risultati
					if(isLatenza){
						Number obLT = StatsUtils.converToNumber(row.get("somma_latenza_totale"));
						Number obLS = StatsUtils.converToNumber(row.get("somma_latenza_servizio"));
						Number obLP = StatsUtils.converToNumber(row.get("somma_latenza_porta"));
	
						if(obLT != null){
							r.inserisciSomma(obLT);
						}
						else{
							if(isLatenza_totale){
								r.inserisciSomma(0);
							}
						}
	
						if(obLS != null){
							r.inserisciSomma(obLS);
						}
						else{
							if(isLatenza_servizio){
								r.inserisciSomma(0);
							}
						}
	
						if(obLP != null){
							r.inserisciSomma(obLP);
						}
						else{
							if(isLatenza_porta){
								r.inserisciSomma(0);
							}
						}
					}
					else if(isBanda){
						Number obBandaComplessiva = StatsUtils.converToNumber(row.get("somma_banda_complessiva"));
						Number obBandaInterna = StatsUtils.converToNumber(row.get("somma_banda_interna"));
						Number obBandaEsterna = StatsUtils.converToNumber(row.get("somma_banda_esterna"));
	
						if(obBandaComplessiva != null){
							r.inserisciSomma(obBandaComplessiva);
						}
						else{
							if(isBanda_complessiva){
								r.inserisciSomma(0);
							}
						}
	
						if(obBandaInterna != null){
							r.inserisciSomma(obBandaInterna);
						}
						else{
							if(isBanda_interna){
								r.inserisciSomma(0);
							}
						}
	
						if(obBandaEsterna != null){
							r.inserisciSomma(obBandaEsterna);
						}
						else{
							if(isBanda_esterna){
								r.inserisciSomma(0);
							}
						}
					}
					else{
						Number obSm = StatsUtils.converToNumber(row.get("somma"));
						if(obSm!=null){
							r.setSomma(obSm);
						}else{
							r.setSomma(0);
						}
					} 
	
					res.add(r);
	
					if(nuovaEntry){
						mapRisultati.put(valoreRisorsa, res);
						mapDateUsate.put(valoreRisorsa, datePerRes);
					}
				}
			}

			// generazione entries mancanti
			for (String val : mapRisultati.keySet()) {
				List<Res> entries = mapRisultati.get(val);
				List<Date> dateVal = mapDateUsate.get(val);

				// ordino la lista delle date utilizzate
				Collections.sort(listaDateUtilizzate);
				// scorro tutte le date, trovate dalla query

				List<Res> listaResOrdinata = new ArrayList<>();
				for (Date data : listaDateUtilizzate) {
					// se la data non e' stata utilizzata per la serie corrente simulo uno zero
					if(!dateVal.contains(data)) {
						Res r = new Res();
						r.setId(data != null ? data.getTime() : null);
						r.setRisultato(data);
						r.inserisciSomma(0);

						listaResOrdinata.add(r);
					} else {
						Res tmpRes = null;
						for (Res res2 : entries) {
							if(res2.getId().longValue() == data.getTime()){
								tmpRes = res2;
								break;
							}
						}

						if(tmpRes != null)
							listaResOrdinata.add(tmpRes);
					}
				}

				// inserisco i valori con le date ordinate.
				entries.clear();
				entries.addAll(listaResOrdinata);
			}

			return mapRisultati;
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (UtilsException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		}catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		}

		return null;
	}

	private IExpression createAndamentoTemporalePersonalizzatoExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model,	StatisticaContenutiModel modelContenuti, boolean isCount) throws ServiceException, UtilsException{
		IExpression expr = null;

		FilterImpl report = (FilterImpl) this.statistichePersonalizzateSearch
				.getFiltroReport();

		try {
			expr = parseStatistichePersonalizzateFilter(dao, model, modelContenuti);
			
			this.impostaFiltroDatiMittente(expr, this.statistichePersonalizzateSearch, model, isCount);
			
			this.impostaFiltroGruppo(expr, this.statistichePersonalizzateSearch, model, isCount);

			this.impostaFiltroApi(expr, this.statistichePersonalizzateSearch, model, isCount);

			this.impostaFiltroIdClusterOrCanale(expr, this.statistichePersonalizzateSearch, model, isCount);
			
			// Risorsa da aggregare indica la statistica per cui aggregare, deve coincidere nel campo risorsa_nome
			String nomeStatisticaPersonalizzata = this.statistichePersonalizzateSearch.getStatisticaSelezionata().getIdConfigurazioneStatistica();
			if(report!=null && report.getIdStatistic()!=null){
				expr.like(modelContenuti.RISORSA_NOME, nomeStatisticaPersonalizzata+"-"+report.getIdStatistic(),LikeMode.EXACT);
			}
			else{
				expr.like(modelContenuti.RISORSA_NOME, nomeStatisticaPersonalizzata,LikeMode.EXACT);
			}

			//
			if (report != null) {
				expr.and(report.getExpression());
			}

			// valori selezionati dall'utente nella pagina
			String[] valoriRisorsa = this.statistichePersonalizzateSearch.getValoriRisorsa();
			if(valoriRisorsa != null && valoriRisorsa.length > 0){
				expr.in(modelContenuti.RISORSA_VALORE, Arrays.asList(valoriRisorsa));
			}

			// condizione di groupby
			expr.addGroupBy(model.DATA).addGroupBy(modelContenuti.RISORSA_VALORE); 

			if (!isCount) {
				expr.sortOrder(SortOrder.ASC).addOrder(model.DATA,SortOrder.ASC).addOrder(modelContenuti.RISORSA_VALORE,SortOrder.ASC); 
			}

		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			throw e;
		}  

		return expr;
	}

	private IExpression parseStatistichePersonalizzateFilter(IServiceSearchWithoutId<?> dao, StatisticaModel model, StatisticaContenutiModel modelContenuti) throws ServiceException {
		IExpression expr = null;

		List<Soggetto> listaSoggettiGestione = this.statistichePersonalizzateSearch
				.getSoggettiGestione();

		try {
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.statistichePersonalizzateSearch.getProtocollo());
			
			expr = dao.newExpression();

			// Data
			expr.between(model.DATA,this.statistichePersonalizzateSearch.getDataInizio(),this.statistichePersonalizzateSearch.getDataFine());
			
			// Record validi
			StatisticheUtils.selezionaRecordValidi(expr, model);

			// Protocollo
			String protocollo = null;
			// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
			// protocollo e' impostato anche scegliendo la modalita'
			//			if (StringUtils.isNotEmpty(this.statistichePersonalizzateSearch.getProtocollo()) && this.statistichePersonalizzateSearch.isShowListaProtocolli()) {
			if (this.statistichePersonalizzateSearch.isSetFiltroProtocollo()) {
				//				expr.and().equals(model.PROTOCOLLO,	this.statistichePersonalizzateSearch.getProtocollo());
				protocollo = this.statistichePersonalizzateSearch.getProtocollo();

				impostaTipiCompatibiliConProtocollo(dao, model, expr, protocollo, this.statistichePersonalizzateSearch.getTipologiaRicercaEnum());

			}

			// permessi utente operatore
			if(this.statistichePersonalizzateSearch.getPermessiUtenteOperatore()!=null){
				IExpression permessi = this.statistichePersonalizzateSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
						model.TIPO_DESTINATARIO,model.DESTINATARIO,
						model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
				expr.and(permessi);
			}
			
			// soggetto locale
			if(Utility.isFiltroDominioAbilitato() && this.statistichePersonalizzateSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.statistichePersonalizzateSearch.getSoggettoLocale()) && 
					!"--".equals(this.statistichePersonalizzateSearch.getSoggettoLocale())){
				String tipoSoggettoLocale = this.statistichePersonalizzateSearch.getTipoSoggettoLocale();
				String nomeSoggettoLocale = this.statistichePersonalizzateSearch.getSoggettoLocale();
				String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
				expr.and().equals(model.ID_PORTA, idPorta);
			}

			// azione
			if (StringUtils.isNotBlank(this.statistichePersonalizzateSearch
					.getNomeAzione()))
				expr.and().equals(model.AZIONE,
						this.statistichePersonalizzateSearch.getNomeAzione());

			// nome servizio  e tipo
			if (StringUtils.isNotBlank(this.statistichePersonalizzateSearch.getNomeServizio())){
				
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.statistichePersonalizzateSearch.getNomeServizio());
				
				expr.and().
					equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
					equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
					equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
					equals(model.SERVIZIO,	idServizio.getNome()).
					equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

			}

			// esito
			esitoUtils.setExpression(expr, this.statistichePersonalizzateSearch.getEsitoGruppo(), 
					this.statistichePersonalizzateSearch.getEsitoDettaglio(),
					this.statistichePersonalizzateSearch.getEsitoDettaglioPersonalizzato(),
					this.statistichePersonalizzateSearch.getEsitoContesto(),
					this.statistichePersonalizzateSearch.isEscludiRichiesteScartate(),
					model.ESITO, model.ESITO_CONTESTO,
					dao.newExpression());


			// ho 3 diversi tipi di query in base alla tipologia di ricerca

			// imposto il soggetto (loggato) come mittente o destinatario in base alla tipologia di ricerca selezionata
			if (this.statistichePersonalizzateSearch.getTipologiaRicercaEnum() == null || TipologiaRicerca.all.equals(this.statistichePersonalizzateSearch.getTipologiaRicercaEnum())) {
				// il soggetto loggato puo essere mittente o destinatario
				// se e' selezionato "trafficoPerSoggetto" allora il nome
				// del
				// soggetto selezionato va messo come complementare

				boolean trafficoSoggetto = StringUtils
						.isNotBlank(this.statistichePersonalizzateSearch
								.getTrafficoPerSoggetto());
				boolean soggetto = !listaSoggettiGestione.isEmpty();
				String tipoTrafficoSoggetto = null;
				String nomeTrafficoSoggetto = null;
				if (trafficoSoggetto) {
					tipoTrafficoSoggetto = this.statistichePersonalizzateSearch
							.getTipoTrafficoPerSoggetto();
					nomeTrafficoSoggetto = this.statistichePersonalizzateSearch
							.getTrafficoPerSoggetto();
				}

				IExpression e1 = dao.newExpression();
				IExpression e2 = dao.newExpression();

				// se trafficoSoggetto e soggetto sono impostati allora devo
				// fare la
				// OR
				if (trafficoSoggetto && soggetto) {
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					e1.and().equals(model.TIPO_DESTINATARIO,
							tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO, nomeTrafficoSoggetto);

					e2.and().equals(model.TIPO_MITTENTE, tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE, nomeTrafficoSoggetto);

					// OR
					expr.or(e1, e2);
				} else if (trafficoSoggetto && !soggetto) {
					// il mio soggetto non e' stato impostato (soggetto in
					// gestione,
					// puo succedero solo in caso admin)
					expr.and();

					e1.equals(model.TIPO_DESTINATARIO, tipoTrafficoSoggetto);
					e1.and().equals(model.DESTINATARIO, nomeTrafficoSoggetto);

					e2.equals(model.TIPO_MITTENTE, tipoTrafficoSoggetto);
					e2.and().equals(model.MITTENTE, nomeTrafficoSoggetto);
					// OR
					expr.or(e1, e2);
				} else if (!trafficoSoggetto && soggetto) {
					// e' impostato solo il soggetto in gestione
					expr.and();

					if (!listaSoggettiGestione.isEmpty()) {
						IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
						                                           .size()];
						IExpression[] orSoggetti2 = new IExpression[listaSoggettiGestione
						                                            .size()];

						int i = 0;
						for (Soggetto sog : listaSoggettiGestione) {
							IExpression se = dao.newExpression();
							IExpression se2 = dao.newExpression();
							se.equals(model.TIPO_MITTENTE,
									sog.getTipoSoggetto());
							se.and().equals(model.MITTENTE,
									sog.getNomeSoggetto());
							orSoggetti[i] = se;

							se2.equals(model.TIPO_DESTINATARIO,
									sog.getTipoSoggetto());
							se2.and().equals(model.DESTINATARIO,
									sog.getNomeSoggetto());
							orSoggetti2[i] = se2;

							i++;
						}
						e1.or(orSoggetti);
						e2.or(orSoggetti2);
					}

					// OR
					expr.or(e1, e2);
				} else {
					// nessun filtro da impostare
				}

			} else if (TipologiaRicerca.ingresso.equals(this.statistichePersonalizzateSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				expr.and().equals(model.TIPO_PORTA, "applicativa");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_DESTINATARIO,
								soggetto.getTipoSoggetto());
						se.and().equals(model.DESTINATARIO,
								soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				boolean ignoreSetMittente = isIgnoreSetMittente(this.statistichePersonalizzateSearch);
				if (StringUtils.isNotBlank(this.statistichePersonalizzateSearch.getNomeMittente()) && !ignoreSetMittente) {
					expr.and().equals(model.TIPO_MITTENTE,this.statistichePersonalizzateSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,this.statistichePersonalizzateSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().equals(model.TIPO_PORTA, "delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (!listaSoggettiGestione.isEmpty()) {
					expr.and();

					IExpression[] orSoggetti = new IExpression[listaSoggettiGestione
					                                           .size()];
					int i = 0;
					for (Soggetto soggetto : listaSoggettiGestione) {
						IExpression se = dao.newExpression();
						se.equals(model.TIPO_MITTENTE,	soggetto.getTipoSoggetto());
						se.and().equals(model.MITTENTE, soggetto.getNomeSoggetto());
						orSoggetti[i] = se;
						i++;
					}
					expr.or(orSoggetti);
				}

				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.statistichePersonalizzateSearch	.getNomeDestinatario())) {
					expr.and().equals(	model.TIPO_DESTINATARIO,	this.statistichePersonalizzateSearch.getTipoDestinatario());
					expr.and().equals(	model.DESTINATARIO, this.statistichePersonalizzateSearch.getNomeDestinatario());
				}
			}
		} catch (ServiceException e) {
			throw e;
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}  

		return expr;
	}


	@Override
	public List<String> getValoriRisorse() throws ServiceException{
		StatisticheGiornaliereService.logDebug("Leggo i valori delle risorse per la statistica: " + this.statistichePersonalizzateSearch.getNomeStatisticaPersonalizzata()); 

		List<String> valori = new ArrayList<>();
		StatisticaModel model = null;
		IServiceSearchWithoutId<?> dao = null;
		StatisticaContenutiModel modelContenuti = null;
		StatisticType tipologia = this.statistichePersonalizzateSearch.getModalitaTemporale();  

		switch (tipologia) {
		case GIORNALIERA:
			model = StatisticaGiornaliera.model().STATISTICA_BASE;
			modelContenuti = StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI;
			dao = this.statGiornaliereSearchDAO;
			break;
		case MENSILE:
			model = StatisticaMensile.model().STATISTICA_BASE;
			modelContenuti = StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI;
			dao = this.statMensileSearchDAO;
			break;
		case ORARIA:
			model = StatisticaOraria.model().STATISTICA_BASE;
			modelContenuti = StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI;
			dao = this.statOrariaSearchDAO;
			break;
		case SETTIMANALE:
			model = StatisticaSettimanale.model().STATISTICA_BASE;
			modelContenuti = StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI;
			dao = this.statSettimanaleSearchDAO;
			break;
		}
		if(dao==null) {
			throw new ServiceException("DAO unknown");
		}
		if(model==null) {
			throw new ServiceException("Model unknown");
		}
		if(modelContenuti==null) {
			throw new ServiceException("ModelContenuti unknown");
		}

		try{
			IExpression expr = parseStatistichePersonalizzateFilter(dao, model, modelContenuti);
			expr.and();
			this.impostaFiltroDatiMittente(expr, this.statistichePersonalizzateSearch, model, false);
			
			this.impostaFiltroGruppo(expr, this.statistichePersonalizzateSearch, model, false);
			
			this.impostaFiltroApi(expr, this.statistichePersonalizzateSearch, model, false);

			this.impostaFiltroIdClusterOrCanale(expr, this.statistichePersonalizzateSearch, model, false);

			boolean resourceStats = false;
			if(StatisticByResource.ID.equals(this.statistichePersonalizzateSearch.getStatisticaSelezionata().getIdConfigurazioneStatistica())){
				if(this.statistichePersonalizzateSearch.getStatisticaSelezionataParameters()!=null && 
						this.statistichePersonalizzateSearch.getStatisticaSelezionataParameters().size()>0){
					Parameter<?> p = this.statistichePersonalizzateSearch.getStatisticaSelezionataParameters().get(0);
					if(p!=null && StatisticByResource.PARAM_RESOURCE.equals(p.getId())){
						try{
							if(p.getValue()!=null){
								String resouceName = p.getValueAsString();
								expr.like(modelContenuti.RISORSA_NOME, 
										this.statistichePersonalizzateSearch.getStatisticaSelezionata().getIdConfigurazioneStatistica()+
										"-"+
										resouceName,
										LikeMode.EXACT);
								resourceStats = true;
							}
						}catch(Exception e){
							StatisticheGiornaliereService.logError(e.getMessage(), e);
						}
					}
				}
			}
			if(!resourceStats){
				IExpression orName = dao.newExpression();
				orName.or();
				orName.like(modelContenuti.RISORSA_NOME, this.statistichePersonalizzateSearch.getStatisticaSelezionata().getIdConfigurazioneStatistica(),LikeMode.EXACT);
				orName.like(modelContenuti.RISORSA_NOME, this.statistichePersonalizzateSearch.getStatisticaSelezionata().getIdConfigurazioneStatistica()+"-",LikeMode.START);
				expr.and(orName);
			}

			expr.sortOrder(SortOrder.ASC).addOrder(modelContenuti.RISORSA_VALORE);

			IPaginatedExpression pagExpr = dao.toPaginatedExpression(expr);

			List<Object> select = dao.select(pagExpr, true, modelContenuti.RISORSA_VALORE);

			if(select != null && select.size() > 0){
				for (Object object : select) {
					valori.	add((String) object);
				}
			}

		} catch (ServiceException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw e;
		} catch (UtilsException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.logDebug(e.getMessage(), e);
		}   
		return valori;
	}

	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithoutId<?> dao, StatisticaModel model,	IExpression expr, String protocollo, TipologiaRicerca tipologiaRicercaParam) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsServiceEngine.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
			}
		} catch (Exception e) {
			StatisticheGiornaliereService.logError("Si e' verificato un errore durante il calcolo dei tipi servizio compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoServiziCompatibili != null)
			expr.and(expressionTipoServiziCompatibili);

		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiMittenteCompatibili = null;
		try {
			if(protocollo != null) {
				if(tipologiaRicercaParam==null || TipologiaRicerca.all.equals(tipologiaRicercaParam) || TipologiaRicerca.ingresso.equals(tipologiaRicercaParam)) {
					// devo prendere anche le transazioni in cui il mittente non e' definito poiche' e' possibile nelle porte applicative avere una autenticazione anonima.
					expressionTipoSoggettiMittenteCompatibili = dao.newExpression();
					expressionTipoSoggettiMittenteCompatibili.or();
					expressionTipoSoggettiMittenteCompatibili.isNull(model.TIPO_MITTENTE);
					expressionTipoSoggettiMittenteCompatibili.equals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					expressionTipoSoggettiMittenteCompatibili.and(DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_MITTENTE, protocollo));
				}
				else {
					expressionTipoSoggettiMittenteCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_MITTENTE, protocollo);
				}
			}
		} catch (Exception e) {
			StatisticheGiornaliereService.logError("Si e' verificato un errore durante il calcolo dei tipi soggetto mittente compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiMittenteCompatibili != null)
			expr.and(expressionTipoSoggettiMittenteCompatibili);


		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiDestinatarioCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_DESTINATARIO, protocollo);
			}
		} catch (Exception e) {
			StatisticheGiornaliereService.logError("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}


	private IField getCredenzialeField(org.openspcoop2.core.transazioni.CredenzialeMittente credenzialeMittente, StatisticaModel model) throws ServiceException {
		IField fieldCredenziale = null;
		String credenzialeTipo = credenzialeMittente.getTipo();
		if(credenzialeTipo.startsWith(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CREDENZIALE_TRASPORTO_PREFIX)) {
			fieldCredenziale = model.TRASPORTO_MITTENTE;
		} else {
			org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = null;
			try {
				tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(credenzialeTipo, true);
			}catch(NotFoundException e) {
				throw new ServiceException(e.getMessage(),e);
			}
			
			switch (tcm) {
			case TOKEN_CLIENT_ID:
				fieldCredenziale = model.TOKEN_CLIENT_ID;
				break;
			case TOKEN_EMAIL:
				fieldCredenziale = model.TOKEN_MAIL;
				break;
			case TOKEN_ISSUER:
				fieldCredenziale = model.TOKEN_ISSUER;
				break;
			case TOKEN_SUBJECT:
				fieldCredenziale = model.TOKEN_SUBJECT;
				break;
			case TOKEN_USERNAME:
				fieldCredenziale = model.TOKEN_USERNAME;
				break;
				
			case CLIENT_ADDRESS:
				return model.CLIENT_ADDRESS;
				
			case GRUPPI:
				return model.GRUPPI;
				
			case API:
				return model.URI_API;
				
			case EVENTI:
			case TRASPORTO:
			case PDND_CLIENT_JSON:
			case PDND_ORGANIZATION_JSON:
			case PDND_ORGANIZATION_NAME:
			case PDND_ORGANIZATION_EXTERNAL_ID:
			case PDND_ORGANIZATION_CONSUMER_ID:
				// caso impossibile
				break; 
			}
		}
		
		return fieldCredenziale;
	}
	
	private List<org.openspcoop2.core.transazioni.CredenzialeMittente> getIdCredenzialiFromFilter(BaseSearchForm searchForm, org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialeMittentiService, boolean isCount) {
		List<org.openspcoop2.core.transazioni.CredenzialeMittente> findAll = new ArrayList<>();
		
		try {
			CaseSensitiveMatch caseSensitiveMatch = CaseSensitiveMatch.valueOf(searchForm.getMittenteCaseSensitiveType());
			TipoMatch match = TipoMatch.valueOf(searchForm.getMittenteMatchingType());
			boolean ricercaEsatta = TipoMatch.EQUALS.equals(match);
			boolean caseSensitive = CaseSensitiveMatch.SENSITIVE.equals(caseSensitiveMatch);
			 
			IPaginatedExpression pagExpr = null;
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				CredenzialeSearchTrasporto searchTrasporto = new CredenzialeSearchTrasporto(searchForm.getAutenticazione());
				pagExpr = searchTrasporto.createExpression(credenzialeMittentiService, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			} 
			
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				
				caseSensitive = true;
				boolean socketAddress = false;
				boolean trasportAddress = false;
				boolean and = false;
				if(ricercaEsatta) {
					if(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_SOCKET.equals(searchForm.getClientAddressMode())) {
						socketAddress = true;
					}
					else if(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CLIENT_ADDRESS_TRASPORTO.equals(searchForm.getClientAddressMode())) {
						trasportAddress = true;
					}
					else {
						socketAddress = true;
						trasportAddress = true;
					}
				}
				
				CredenzialeSearchClientAddress searchClientAddress = new CredenzialeSearchClientAddress(socketAddress, trasportAddress, and);
				pagExpr = searchClientAddress.createExpression(credenzialeMittentiService, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			} 
			
			boolean searchByRefCredentials = false;
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(searchForm.getTokenClaim(), true);
				AbstractSearchCredenziale searchToken = null;
				String valoreRiconoscimento = searchForm.getValoreRiconoscimento();
				if(TipoCredenzialeMittente.TOKEN_CLIENT_ID.equals(tcm)) {
					searchToken = new CredenzialeSearchTokenClient(true, false, true);
				}
				else if(TipoCredenzialeMittente.PDND_ORGANIZATION_EXTERNAL_ID.equals(tcm) ||
						TipoCredenzialeMittente.PDND_ORGANIZATION_CONSUMER_ID.equals(tcm)) {
					searchToken = new CredenzialeSearchToken(TipoCredenzialeMittente.PDND_ORGANIZATION_JSON);
					searchByRefCredentials = true;
					if(ricercaEsatta) {
						valoreRiconoscimento = "\""+valoreRiconoscimento+"\"";
					}
					ricercaEsatta = false; // sono entrambi identificativi, non devo fare una ricerca esatta
				}
				else {
					searchToken = new CredenzialeSearchToken(tcm);
					searchByRefCredentials = TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.equals(tcm);
				}
				pagExpr = searchToken.createExpression(credenzialeMittentiService, valoreRiconoscimento, ricercaEsatta, caseSensitive);
			}
			
			findAll = credenzialeMittentiService.findAll(pagExpr);
			
			if(searchByRefCredentials) {
				findAll = CredenzialiMittenteUtils.translateByRef(findAll, credenzialeMittentiService);
			}
			
		}catch(ServiceException | ExpressionException | NotImplementedException | ExpressionNotImplementedException | UtilsException | NotFoundException e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
		}
		return findAll;
	}
	
	private void impostaFiltroDatiMittente(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount)
			throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(searchForm.getIdentificazione())) {
					if (StringUtils.isNotBlank(searchForm.getServizioApplicativo())) {
						if(org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TRASPORTO_KEY.equals(searchForm.getIdentificazione())) {
							// sb.append("AND t.servizioApplicativo = :servizio_applicativo ");
							filter.and().equals(model.SERVIZIO_APPLICATIVO,	searchForm.getServizioApplicativo());
						}
						else if(org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(searchForm.getIdentificazione())) {
							
							boolean soggettoDefined = StringUtils.isNotBlank(searchForm.getNomeMittente()) && StringUtils.isNotBlank(searchForm.getTipoMittente());
							if(soggettoDefined) {
								IDSoggetto idSoggetto = new IDSoggetto(searchForm.getTipoMittente(), searchForm.getNomeMittente());
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setIdSoggettoProprietario(idSoggetto);
								idSA.setNome(searchForm.getServizioApplicativo());
								String idSAasString = CredenzialeTokenClient.getApplicationAsString(idSA);
								String dbValue = CredenzialeTokenClient.getApplicationDBValue(idSAasString);
								CredenzialeSearchTokenClient searchToken = new CredenzialeSearchTokenClient(false, true, false);
								searchToken.disableConvertToDBValue();
								boolean ricercaEsatta = false;
								boolean caseSensitive = true;
								try {
									IPaginatedExpression pagExpr = searchToken.createExpression(this.credenzialiMittenteDAO, dbValue, ricercaEsatta, caseSensitive);
									List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
									addListaCredenzialiMittente(filter, listaCredenzialiMittente, model);
								}catch(Exception e) {
									throw new ServiceException(e.getMessage(),e);
								}
							}
							
						}
					}
				}
			}
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
				// gia aggiunto in precedenza
				// nop;
			}
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO) ||
					searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP) ||
					searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				List<org.openspcoop2.core.transazioni.CredenzialeMittente> listaCredenzialiMittente = getIdCredenzialiFromFilter(searchForm, this.credenzialiMittenteDAO, isCount);
				addListaCredenzialiMittente(filter, listaCredenzialiMittente, model);
			}
		}
	}
	
	private void impostaFiltroGruppo(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount) throws UtilsException, ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		if (StringUtils.isNotEmpty(searchForm.getGruppo())) {
			
			CredenzialeSearchGruppo searchGruppi = new CredenzialeSearchGruppo();
			IPaginatedExpression pagExpr = searchGruppi.createExpression(this.credenzialiMittenteDAO, searchForm.getGruppo(), true, true);
			List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
			addListaCredenzialiMittente(filter, listaCredenzialiMittente, model);

		}
	}
	
	private void impostaFiltroApi(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount) throws UtilsException, ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		if (StringUtils.isNotEmpty(searchForm.getApi())) {
			
			CredenzialeSearchApi searchApi = new CredenzialeSearchApi();
			IPaginatedExpression pagExpr = searchApi.createExpression(this.credenzialiMittenteDAO, searchForm.getApi(), true, true);
			List<CredenzialeMittente> listaCredenzialiMittente = this.credenzialiMittenteDAO.findAll(pagExpr);
			addListaCredenzialiMittente(filter, listaCredenzialiMittente, model);

		}
	}
	
	private void impostaFiltroIdClusterOrCanale(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount) throws UtilsException, ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		if (StringUtils.isNotEmpty(searchForm.getClusterId())) {
			
			if(this.clusterDinamico) {
				List<String> listId = this.getClusterIdDinamici(searchForm.getClusterId().trim(), this.clusterDinamicoRefresh);
				if(listId!=null && !listId.isEmpty()) {
					filter.and().in(model.CLUSTER_ID, listId);
				}
				else {
					filter.and().equals(model.CLUSTER_ID,	"--"); // non esistente volutamente
				}
			}
			else {
				filter.and().equals(model.CLUSTER_ID,	searchForm.getClusterId().trim());
			}
		}
		else if (StringUtils.isNotEmpty(searchForm.getCanale())) {
			
			List<String> listId = searchForm.getIdClusterByCanale(searchForm.getCanale());
			if(listId!=null && !listId.isEmpty()) {
				filter.and().in(model.CLUSTER_ID, listId);
			}
			else {
				filter.and().equals(model.CLUSTER_ID,	"--"); // non esistente volutamente
			}
		}
	}
	
	private void impostaGroupByFiltroDatiMittente(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount)
			throws ExpressionNotImplementedException, ExpressionException, NotFoundException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(this.distribSaSearch.getIdentificazione()) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
					filter.notEquals(model.TOKEN_CLIENT_ID, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_CLIENT_ID);
				}
				else {
					filter.notEquals(model.SERVIZIO_APPLICATIVO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.SERVIZIO_APPLICATIVO);
				}
			} else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
				// Caso non previsto nella distribuzione del servizio applicativo dove viene usato questo metodo
				return;
			} else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				filter.notEquals(model.TRASPORTO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TRASPORTO_MITTENTE);
			} else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				filter.notEquals(model.CLIENT_ADDRESS, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.CLIENT_ADDRESS);
			} else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(searchForm.getTokenClaim(), true);
				
				switch (tcm) {
				case TOKEN_CLIENT_ID:
				case PDND_ORGANIZATION_NAME: // viene risolto per riferimento
					filter.notEquals(model.TOKEN_CLIENT_ID, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_CLIENT_ID);
					break;
				case TOKEN_EMAIL:
					filter.notEquals(model.TOKEN_MAIL, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_MAIL);
					break;
				case TOKEN_ISSUER:
					filter.notEquals(model.TOKEN_ISSUER, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_ISSUER);
					break;
				case TOKEN_SUBJECT:
					filter.notEquals(model.TOKEN_SUBJECT, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_SUBJECT);
					break;
				case TOKEN_USERNAME:
					filter.notEquals(model.TOKEN_USERNAME, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_USERNAME);
					break;
					
				case CLIENT_ADDRESS:
					filter.notEquals(model.CLIENT_ADDRESS, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.CLIENT_ADDRESS);
					break;
					
				case GRUPPI:
					filter.notEquals(model.GRUPPI, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.GRUPPI);
					break;
					
				case API:
					filter.notEquals(model.URI_API, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.URI_API);
					break;
					
				case EVENTI:
				case TRASPORTO:
				case PDND_CLIENT_JSON:
				case PDND_ORGANIZATION_JSON:
				case PDND_ORGANIZATION_EXTERNAL_ID:
				case PDND_ORGANIZATION_CONSUMER_ID:
					// caso impossibile
					break; 
				}
			}
		}
	}
	
	
	private IField getCredenzialeFieldGroupBy(BaseSearchForm searchForm, StatisticaModel model) throws NotFoundException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(this.distribSaSearch.getIdentificazione()) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
					return model.TOKEN_CLIENT_ID;
				}
				else {
					return model.SERVIZIO_APPLICATIVO;
				}
			}
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
				// Caso non previsto nella distribuzione del servizio applicativo dove viene usato questo metodo
				return null;
			}  
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
				return model.TRASPORTO_MITTENTE;
			} 
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
				return model.CLIENT_ADDRESS;
			} 
			else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(searchForm.getTokenClaim(), true);
				
				switch (tcm) {
				case TOKEN_CLIENT_ID:
					return model.TOKEN_CLIENT_ID;
				case TOKEN_EMAIL:
					return model.TOKEN_MAIL;
				case TOKEN_ISSUER:
					return model.TOKEN_ISSUER;
				case TOKEN_SUBJECT:
					return model.TOKEN_SUBJECT;
				case TOKEN_USERNAME:
					return model.TOKEN_USERNAME;
					
				case CLIENT_ADDRESS:
					return model.CLIENT_ADDRESS;
					
				case GRUPPI:
					return model.GRUPPI;
					
				case API:
					return model.URI_API;
					
				case PDND_ORGANIZATION_NAME: 
				case PDND_ORGANIZATION_EXTERNAL_ID:
				case PDND_ORGANIZATION_CONSUMER_ID: 
					// Verro poi risolto per riferimento
					return model.TOKEN_CLIENT_ID;
					
				case EVENTI:
				case TRASPORTO:
				case PDND_CLIENT_JSON:
				case PDND_ORGANIZATION_JSON:
					// caso impossibile
					break; 
				}
			}
		}
		
		return null;
		 
	}
	
	
	private String getLabelCredenzialeFieldGroupBy(String risultato, BaseSearchForm searchForm, StringBuilder sbTipoCredenziale) {
		try {
			// credenziali mittente
			if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
				if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					if (StringUtils.isNotBlank(this.distribSaSearch.getIdentificazione()) && org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(this.distribSaSearch.getIdentificazione())) {
						if(StringUtils.isNotEmpty(risultato)) {
							/**CredenzialeMittente credenzialeMittente =  ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));*/
							MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
							CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
							if(credenzialeMittente!=null && credenzialeMittente.getTipo()!=null) {
								sbTipoCredenziale.append(credenzialeMittente.getTipo());
							}
							return credenzialeMittente != null ? credenzialeMittente.getCredenziale() : risultato;
						}
						else {
							return risultato;
						}
					}
					else {
						return risultato;
					}
				} 
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
					// Caso non previsto nella distribuzione del servizio applicativo dove viene usato questo metodo
					return risultato;
				}  
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
					if(StringUtils.isNotEmpty(risultato)) {
						/**CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));*/
						MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
						CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
						if(credenzialeMittente!=null && credenzialeMittente.getTipo()!=null) {
							sbTipoCredenziale.append(credenzialeMittente.getTipo());
						}
						return credenzialeMittente != null ? credenzialeMittente.getCredenziale() : risultato;
					}
				} 
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
					if(StringUtils.isNotEmpty(risultato)) {
						/**CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));*/
						MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
						CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
						
						if(credenzialeMittente!=null && credenzialeMittente.getTipo()!=null) {
							sbTipoCredenziale.append(credenzialeMittente.getTipo());
						}
						
						String credenziale = credenzialeMittente!=null ? credenzialeMittente.getCredenziale() : null;
						return getLabelCredenzialeFieldGroupByIndirizzoIp(credenziale, risultato);
					}
				} 
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
					org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.toEnumConstant(searchForm.getTokenClaim(), true);
					CredenzialeMittente credenzialeMittente  = null;
					if(StringUtils.isNotEmpty(risultato)) {
					switch (tcm) {
					case TOKEN_CLIENT_ID:
					case TOKEN_EMAIL:
					case TOKEN_ISSUER:
					case TOKEN_SUBJECT:
					case TOKEN_USERNAME:
					case PDND_ORGANIZATION_NAME:
							/**credenzialeMittente  = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));*/
							MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
							credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
							if(credenzialeMittente!=null && credenzialeMittente.getTipo()!=null) {
								sbTipoCredenziale.append(credenzialeMittente.getTipo());
							}
							return credenzialeMittente != null ? credenzialeMittente.getCredenziale() : risultato;

					case TRASPORTO:
					default:
						// caso impossibile
						break; 
					}
				}
				}
			}
			else if(!this.distribServizioSearch.isDistribuzionePerImplementazioneApi()) {
				if(StringUtils.isNotEmpty(risultato)) {
					/**CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));*/
					MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
					CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
					return credenzialeMittente != null ? credenzialeMittente.getCredenziale() : risultato;
				}
			}
		} catch (NotFoundException e) {
			StatisticheGiornaliereService	.logDebug(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService	.logError(e.getMessage(), e);
		}
		
		return risultato;
		 
	}
	
	private String getLabelCredenzialeFieldGroupByIndirizzoIp(String credenziale, String risultato) {
		if(credenziale!=null) {
			StringBuilder bf = new StringBuilder();
			if(CredenzialeClientAddress.isSocketAddressDBValue(credenziale)) {
				bf.append(CredenzialeClientAddress.convertSocketDBValueToOriginal(credenziale));
				if(CredenzialeClientAddress.isTransportAddressDBValue(credenziale)) {
					bf.append(" (X-Forwarded-For: ");
					bf.append(CredenzialeClientAddress.convertTransportDBValueToOriginal(credenziale));
					bf.append(")");
				}
				
			}
			else if(CredenzialeClientAddress.isTransportAddressDBValue(credenziale)) {
				bf.append(CredenzialeClientAddress.convertTransportDBValueToOriginal(credenziale));
			}
			return bf.length()>0 ? bf.toString() : risultato; 
		}
		else {
			return risultato;
		}
	}
	
	private void addListaCredenzialiMittente(IExpression filter, List<CredenzialeMittente> listaCredenzialiMittente, StatisticaModel model) throws ExpressionNotImplementedException, ExpressionException {
		// se non ho trovato credenziali che corrispondono a quelle inserite allora restituisco un elenco di transazioni vuoto forzando l'id transazione
		if(listaCredenzialiMittente.isEmpty()) {
			Calendar c = Calendar.getInstance();
			Date d = new Date();
			c.setTime(d);
			c.set(Calendar.YEAR, 2100);
			filter.and().equals(model.DATA, c.getTime());
		} else {
			org.openspcoop2.core.transazioni.CredenzialeMittente credenzialeMittente = listaCredenzialiMittente.get(0);
			IField fieldCredenziale = null;
			try {
				fieldCredenziale = getCredenzialeField(credenzialeMittente, model);
			}catch(ServiceException e) {
				throw new ExpressionException(e.getMessage(),e);
			}
			
			if(listaCredenzialiMittente.size() ==1) {
				filter.and().equals(fieldCredenziale, credenzialeMittente.getId().toString());
				
			} else {
				List<String> ids = new ArrayList<>();
				for (org.openspcoop2.core.transazioni.CredenzialeMittente cMittente : listaCredenzialiMittente) {
					ids.add(cMittente.getId().toString());
				}
				
				filter.and().in(fieldCredenziale, ids); 
			}
		}
	}
	
	
	@Override
	public List<String> getHostnames(String gruppo, int refreshSecondsInterval){
		Connection con = null;
		List<String> lNull = null;
		try {
			con = this.driverConfigurazioneDB.getConnection("StatisticheGiornaliereService.getHostnames");
			return DynamicClusterManager.getHostnames(con, this.driverConfigurazioneDB.getTipoDB(), gruppo, refreshSecondsInterval);
		}catch (Exception e) {
			StatisticheGiornaliereService.logError(e.getMessage(), e);
			return lNull;
		}finally {
			try {
				this.driverConfigurazioneDB.releaseConnection(con);
			}catch(Exception eClose) {
				// ignore
			}
		}
	}
	@Override
	public List<String> getClusterIdDinamici(String gruppo, int refreshSecondsInterval){
		List<String> l = this.getHostnames(gruppo, refreshSecondsInterval);
		List<String> list = null;
		if(l!=null && !l.isEmpty()) {
			list = new ArrayList<>();
			for (String id : l) {
				list.add( DynamicClusterManager.hashClusterId(id) );
			}
		}
		return list;
	}
	
	@Override
	public boolean isTimeoutEvent() {
		return this.timeoutEvent;
	}
	
	private boolean isIgnoreSetMittente(BaseSearchForm searchForm) {
		boolean ignoreSetMittente = false;
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(searchForm.getIdentificazione())) {
					if (StringUtils.isNotBlank(searchForm.getServizioApplicativo())) {
						if(org.openspcoop2.web.monitor.core.constants.Costanti.IDENTIFICAZIONE_TOKEN_KEY.equals(searchForm.getIdentificazione())) {
							ignoreSetMittente=true; // la ricerca avviene dentro la credenziale salvata per l'applicativo token
						}
					}
				}
			}
		}
		return ignoreSetMittente;
	}
	
	private static final String MESSAGE_3D_CUSTOM_INFORMAZIONE_NON_INDICATA = "Informazione personalizzata non indicata";
	
	private void impostaGroupByFiltro3dCustom(IExpression filter, StatisticaModel model, StatsSearchForm form, DimensioneCustom dc)
			throws ExpressionNotImplementedException, ExpressionException {
		
		if(dc!=null) {
			switch (dc) {
			case TAG:{
				filter.notEquals(model.GRUPPI, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.GRUPPI);
				break;
			}
			case API:{
				filter.notEquals(model.URI_API, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.URI_API);
				break;
			}
			case IMPLEMENTAZIONE_API:{
				impostaGroupByFiltro3dCustomImplementazioneApi(filter, model);
				break;
			}
			case OPERAZIONE:{
				filter.notEquals(model.AZIONE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.AZIONE);
				break;
			}
			case SOGGETTO_LOCALE:{
				impostaGroupByFiltro3dCustomSoggettoLocale(filter, model, form);
				break;
			}
			case SOGGETTO_REMOTO:{
				impostaGroupByFiltro3dCustomSoggettoRemoto(filter, model, form);
				break;
			}
			case SOGGETTO_FRUITORE:{
				impostaGroupByFiltro3dCustomSoggettoFruitore(filter, model);
				break;
			}
			case SOGGETTO_EROGATORE:{
				impostaGroupByFiltro3dCustomSoggettoErogatore(filter, model);
				break;
			}
			case TOKEN_CLIENT_ID:
			case TOKEN_PDND_ORGANIZATION:
			case TOKEN_PDND_ORGANIZATION_EXTERNAL_ID:
			case TOKEN_PDND_ORGANIZATION_CONSUMER_ID:
			case APPLICATIVO_TOKEN:{
				filter.notEquals(model.TOKEN_CLIENT_ID, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TOKEN_CLIENT_ID);
				break;
			}
			case TOKEN_ISSUER:{
				filter.notEquals(model.TOKEN_ISSUER, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TOKEN_ISSUER);
				break;
			}
			case TOKEN_SUBJECT:{
				filter.notEquals(model.TOKEN_SUBJECT, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TOKEN_SUBJECT);
				break;
			}
			case TOKEN_USERNAME:{
				filter.notEquals(model.TOKEN_USERNAME, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TOKEN_USERNAME);
				break;
			}
			case TOKEN_EMAIL:{
				filter.notEquals(model.TOKEN_MAIL, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TOKEN_MAIL);
				break;
			}
			case APPLICATIVO_TRASPORTO:{
				filter.notEquals(model.SERVIZIO_APPLICATIVO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.SERVIZIO_APPLICATIVO);
				filter.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TIPO_MITTENTE);
				filter.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.MITTENTE);
				break;
			}
			case INDIRIZZO_IP:{
				filter.notEquals(model.CLIENT_ADDRESS, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.CLIENT_ADDRESS);
				break;
			}
			case PRINCIPAL:{
				filter.notEquals(model.TRASPORTO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.TRASPORTO_MITTENTE);
				break;
			}
			case ESITO:{
				filter.notEquals(model.ESITO, Costanti.INFORMAZIONE_ESITO_NON_DISPONIBILE);
				filter.addGroupBy(model.ESITO);
				break;
			}
			}
		}
		else {
			throw new ExpressionException(MESSAGE_3D_CUSTOM_INFORMAZIONE_NON_INDICATA);
		}
	}
	private void impostaGroupByFiltro3dCustomImplementazioneApi(IExpression filter, StatisticaModel model)
			throws ExpressionNotImplementedException, ExpressionException {
		filter.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.notEquals(model.TIPO_SERVIZIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.notEquals(model.SERVIZIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.notEquals(model.VERSIONE_SERVIZIO, Costanti.INFORMAZIONE_VERSIONE_NON_DISPONIBILE);
		filter.addGroupBy(model.TIPO_DESTINATARIO);
		filter.addGroupBy(model.DESTINATARIO);
		filter.addGroupBy(model.TIPO_SERVIZIO);
		filter.addGroupBy(model.SERVIZIO);
		filter.addGroupBy(model.VERSIONE_SERVIZIO);
	}
	private void impostaGroupByFiltro3dCustomSoggettoLocale(IExpression filter, StatisticaModel model, StatsSearchForm form)
			throws ExpressionNotImplementedException, ExpressionException {
		if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
			filter.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.addGroupBy(model.TIPO_DESTINATARIO);
			filter.addGroupBy(model.DESTINATARIO);
		}
		else if(TipologiaRicerca.uscita.equals(form.getTipologiaRicercaEnum())){
			filter.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.addGroupBy(model.TIPO_MITTENTE);
			filter.addGroupBy(model.MITTENTE);
		}
	}
	private void impostaGroupByFiltro3dCustomSoggettoRemoto(IExpression filter, StatisticaModel model, StatsSearchForm form)
			throws ExpressionNotImplementedException, ExpressionException {
		if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
			filter.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.addGroupBy(model.TIPO_MITTENTE);
			filter.addGroupBy(model.MITTENTE);
		}
		else if(TipologiaRicerca.uscita.equals(form.getTipologiaRicercaEnum())){
			filter.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
			filter.addGroupBy(model.TIPO_DESTINATARIO);
			filter.addGroupBy(model.DESTINATARIO);
		}
	}
	private void impostaGroupByFiltro3dCustomSoggettoFruitore(IExpression filter, StatisticaModel model)
			throws ExpressionNotImplementedException, ExpressionException {
		filter.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.addGroupBy(model.TIPO_MITTENTE);
		filter.addGroupBy(model.MITTENTE);
	}
	private void impostaGroupByFiltro3dCustomSoggettoErogatore(IExpression filter, StatisticaModel model)
			throws ExpressionNotImplementedException, ExpressionException {
		filter.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
		filter.addGroupBy(model.TIPO_DESTINATARIO);
		filter.addGroupBy(model.DESTINATARIO);
	}
	
	private void impostaSortOrder3dCustom(IExpression filter, StatisticaModel model, StatsSearchForm form, DimensioneCustom dc, SortOrder s)
			throws ExpressionNotImplementedException, ExpressionException {
		
		if(dc!=null) {
			filter.sortOrder(s);
			switch (dc) {
			case TAG:
				filter.addOrder(model.GRUPPI);
				break;
			case API:
				filter.addOrder(model.URI_API);
				break;
			case IMPLEMENTAZIONE_API:
				filter.addOrder(model.TIPO_DESTINATARIO);
				filter.addOrder(model.DESTINATARIO);
				filter.addOrder(model.TIPO_SERVIZIO);
				filter.addOrder(model.SERVIZIO);
				filter.addOrder(model.VERSIONE_SERVIZIO);
				break;
			case OPERAZIONE:
				filter.addOrder(model.AZIONE);
				break;
			case SOGGETTO_LOCALE:
				if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
					filter.addOrder(model.TIPO_DESTINATARIO);
					filter.addOrder(model.DESTINATARIO);
				}
				else {
					filter.addOrder(model.TIPO_MITTENTE);
					filter.addOrder(model.MITTENTE);
				}
				break;
			case SOGGETTO_REMOTO:
				if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
					filter.addOrder(model.TIPO_MITTENTE);
					filter.addOrder(model.MITTENTE);
				}
				else {
					filter.addOrder(model.TIPO_DESTINATARIO);
					filter.addOrder(model.DESTINATARIO);
				}
				break;
			case SOGGETTO_FRUITORE:
				filter.addOrder(model.TIPO_MITTENTE);
				filter.addOrder(model.MITTENTE);
				break;
			case SOGGETTO_EROGATORE:
				filter.addOrder(model.TIPO_DESTINATARIO);
				filter.addOrder(model.DESTINATARIO);
				break;
			case TOKEN_CLIENT_ID:
			case TOKEN_PDND_ORGANIZATION:
			case TOKEN_PDND_ORGANIZATION_EXTERNAL_ID:
			case TOKEN_PDND_ORGANIZATION_CONSUMER_ID:
			case APPLICATIVO_TOKEN:{
				filter.addOrder(model.TOKEN_CLIENT_ID);
				break;
			}
			case TOKEN_ISSUER:{
				filter.addOrder(model.TOKEN_ISSUER);
				break;
			}
			case TOKEN_SUBJECT:{
				filter.addOrder(model.TOKEN_SUBJECT);
				break;
			}
			case TOKEN_USERNAME:{
				filter.addOrder(model.TOKEN_USERNAME);
				break;
			}
			case TOKEN_EMAIL:{
				filter.addOrder(model.TOKEN_MAIL);
				break;
			}
			case APPLICATIVO_TRASPORTO:{
				filter.addOrder(model.SERVIZIO_APPLICATIVO);
				filter.addOrder(model.TIPO_MITTENTE);
				filter.addOrder(model.MITTENTE);
				break;
			}
			case INDIRIZZO_IP:{
				filter.addOrder(model.CLIENT_ADDRESS);
				break;
			}
			case PRINCIPAL:{
				filter.addOrder(model.TRASPORTO_MITTENTE);
				break;
			}
			case ESITO:{
				filter.addOrder(model.ESITO);
				break;
			}
			}
		}
		else {
			throw new ExpressionException(MESSAGE_3D_CUSTOM_INFORMAZIONE_NON_INDICATA);
		}
		
	}
	
	private void impostaSelectField3dCustom(UnionExpression expr, StatisticaModel model, StatsSearchForm form, DimensioneCustom dc, List<String> aliases)
			throws ExpressionException {

		if(dc!=null) {
			switch (dc) {
			case TAG:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"tag";
				expr.addSelectField(model.GRUPPI, a);
				addAlias(aliases, a);
				break;
			}
			case API:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"api";
				expr.addSelectField(model.URI_API, a);
				addAlias(aliases, a);
				break;
			}
			case IMPLEMENTAZIONE_API:{
				impostaSelectField3dCustomApiImplementation(expr, model, aliases);
				break;
			}
			case OPERAZIONE:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"op";
				expr.addSelectField(model.AZIONE, a);
				addAlias(aliases, a);
				break;
			}
			case SOGGETTO_LOCALE:{
				impostaSelectField3dCustomSoggettoLocale(expr, model, form, aliases);
				break;
			}
			case SOGGETTO_REMOTO:{
				impostaSelectField3dCustomSoggettoRemoto(expr, model, form, aliases);
				break;
			}
			case SOGGETTO_FRUITORE:{
				impostaSelectField3dCustomSoggettoFruitore(expr, model, aliases);
				break;
			}
			case SOGGETTO_EROGATORE:{
				impostaSelectField3dCustomSoggettoErogatore(expr, model, aliases);
				break;
			}
			case TOKEN_CLIENT_ID:
			case TOKEN_PDND_ORGANIZATION:
			case TOKEN_PDND_ORGANIZATION_EXTERNAL_ID:
			case TOKEN_PDND_ORGANIZATION_CONSUMER_ID:
			case APPLICATIVO_TOKEN:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"tc";
				expr.addSelectField(model.TOKEN_CLIENT_ID, a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_ISSUER:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"ti";
				expr.addSelectField(model.TOKEN_ISSUER, a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_SUBJECT:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"ts";
				expr.addSelectField(model.TOKEN_SUBJECT, a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_USERNAME:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"tu";
				expr.addSelectField(model.TOKEN_USERNAME, a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_EMAIL:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"te";
				expr.addSelectField(model.TOKEN_MAIL, a);
				addAlias(aliases, a);
				break;
			}
			case APPLICATIVO_TRASPORTO:{
				impostaSelectField3dCustomServizioApplicativoTrasporto(expr, model, aliases);
				break;
			}
			case PRINCIPAL:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"p";
				expr.addSelectField(model.TRASPORTO_MITTENTE, a);
				addAlias(aliases, a);
				break;
			}
			case INDIRIZZO_IP:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"ip";
				expr.addSelectField(model.CLIENT_ADDRESS, a);
				addAlias(aliases, a);
				break;
			}
			case ESITO:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"e";
				expr.addSelectField(model.ESITO, a);
				addAlias(aliases, a);
				break;
			}
			}
		}
		else {
			throw new ExpressionException(MESSAGE_3D_CUSTOM_INFORMAZIONE_NON_INDICATA);
		}
		
	}
	private void impostaSelectField3dCustomApiImplementation(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aTD = ALIAS_FIELD_DATA_3D_CUSTOM+"td";
		expr.addSelectField(model.TIPO_DESTINATARIO, aTD);
		addAlias(aliases, aTD);
		
		String aD = ALIAS_FIELD_DATA_3D_CUSTOM+"d";
		expr.addSelectField(model.DESTINATARIO, aD);
		addAlias(aliases, aD);
		
		String aTS = ALIAS_FIELD_DATA_3D_CUSTOM+"ts";
		expr.addSelectField(model.TIPO_SERVIZIO, aTS);
		addAlias(aliases, aTS);
		
		String aS = ALIAS_FIELD_DATA_3D_CUSTOM+"s";
		expr.addSelectField(model.SERVIZIO, aS);
		addAlias(aliases, aS);
		
		String aVS = ALIAS_FIELD_DATA_3D_CUSTOM+"vs";
		expr.addSelectField(model.VERSIONE_SERVIZIO, aVS);
		addAlias(aliases, aVS);
	}
	private void impostaSelectField3dCustomSoggettoLocale(UnionExpression expr, StatisticaModel model, StatsSearchForm form, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
			expr.addSelectField(model.TIPO_DESTINATARIO, aT);
			expr.addSelectField(model.DESTINATARIO, aN);
		}
		else {
			expr.addSelectField(model.TIPO_MITTENTE, aT);
			expr.addSelectField(model.MITTENTE, aN);
		}
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomSoggettoRemoto(UnionExpression expr, StatisticaModel model, StatsSearchForm form, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
			expr.addSelectField(model.TIPO_MITTENTE, aT);
			expr.addSelectField(model.MITTENTE, aN);
		}
		else {
			expr.addSelectField(model.TIPO_DESTINATARIO, aT);
			expr.addSelectField(model.DESTINATARIO, aN);
		}
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomSoggettoFruitore(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		expr.addSelectField(model.TIPO_MITTENTE, aT);
		expr.addSelectField(model.MITTENTE, aN);
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomSoggettoErogatore(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		expr.addSelectField(model.TIPO_DESTINATARIO, aT);
		expr.addSelectField(model.DESTINATARIO, aN);
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomServizioApplicativoTrasporto(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aSA = ALIAS_FIELD_DATA_3D_CUSTOM+"sa";
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		expr.addSelectField(model.SERVIZIO_APPLICATIVO, aSA);
		expr.addSelectField(model.TIPO_MITTENTE, aT);
		expr.addSelectField(model.MITTENTE, aN);
		addAlias(aliases, aSA);
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	
	private void impostaSelectField3dCustomFake(UnionExpression expr, StatisticaModel model, StatsSearchForm form, DimensioneCustom dc, List<String> aliases)
			throws ExpressionException {

		if(dc!=null) {
			switch (dc) {
			case TAG:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"tag";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.GRUPPI.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case API:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"api";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.URI_API.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case IMPLEMENTAZIONE_API:{
				impostaSelectField3dCustomFakeApiImplementation(expr, model, aliases);				
				break;
			}
			case OPERAZIONE:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"op";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.AZIONE.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case SOGGETTO_LOCALE:{
				impostaSelectField3dCustomFakeSoggettoLocale(expr, model, form, aliases);		
				break;
			}
			case SOGGETTO_REMOTO:{
				impostaSelectField3dCustomFakeSoggettoRemoto(expr, model, form, aliases);		
				break;
			}
			case SOGGETTO_FRUITORE:{
				impostaSelectField3dCustomFakeSoggettoFruitore(expr, model, aliases);		
				break;
			}
			case SOGGETTO_EROGATORE:{
				impostaSelectField3dCustomFakeSoggettoErogatore(expr, model, aliases);		
				break;
			}
			case TOKEN_CLIENT_ID:
			case TOKEN_PDND_ORGANIZATION:
			case TOKEN_PDND_ORGANIZATION_EXTERNAL_ID:
			case TOKEN_PDND_ORGANIZATION_CONSUMER_ID:
			case APPLICATIVO_TOKEN:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"tc";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TOKEN_CLIENT_ID.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_ISSUER:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"ti";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TOKEN_ISSUER.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_SUBJECT:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"ts";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TOKEN_SUBJECT.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_USERNAME:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"tu";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TOKEN_USERNAME.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case TOKEN_EMAIL:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"te";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TOKEN_MAIL.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case APPLICATIVO_TRASPORTO:{
				impostaSelectField3dCustomFakeServizioApplicativoTrasporto(expr, model, aliases);
				break;
			}
			case PRINCIPAL:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"p";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.TRASPORTO_MITTENTE.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case INDIRIZZO_IP:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"ip";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
						model.CLIENT_ADDRESS.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			case ESITO:{
				String a = ALIAS_FIELD_DATA_3D_CUSTOM+"e";
				expr.addSelectField(new ConstantField(a, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_INT,
						model.ESITO.getFieldType()), a);
				addAlias(aliases, a);
				break;
			}
			}
		}
		else {
			throw new ExpressionException(MESSAGE_3D_CUSTOM_INFORMAZIONE_NON_INDICATA);
		}
		
	}
	private void impostaSelectField3dCustomFakeApiImplementation(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aTD = ALIAS_FIELD_DATA_3D_CUSTOM+"td";
		expr.addSelectField(new ConstantField(aTD, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.TIPO_DESTINATARIO.getFieldType()), aTD);
		addAlias(aliases, aTD);
		
		String aD = ALIAS_FIELD_DATA_3D_CUSTOM+"d";
		expr.addSelectField(new ConstantField(aD, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.DESTINATARIO.getFieldType()), aD);
		addAlias(aliases, aD);
		
		String aTS = ALIAS_FIELD_DATA_3D_CUSTOM+"ts";
		expr.addSelectField(new ConstantField(aTS, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.TIPO_SERVIZIO.getFieldType()), aTS);
		addAlias(aliases, aTS);
		
		String aS = ALIAS_FIELD_DATA_3D_CUSTOM+"s";
		expr.addSelectField(new ConstantField(aS, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.SERVIZIO.getFieldType()), aS);
		addAlias(aliases, aS);
		
		String aVS = ALIAS_FIELD_DATA_3D_CUSTOM+"vs";
		expr.addSelectField(new ConstantField(aVS, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE_VERSIONE,
				model.VERSIONE_SERVIZIO.getFieldType()), aVS);
		addAlias(aliases, aVS);
	}
	private void impostaSelectField3dCustomFakeSoggettoLocale(UnionExpression expr, StatisticaModel model, StatsSearchForm form, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
			expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.TIPO_DESTINATARIO.getFieldType()), aT);
			expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.DESTINATARIO.getFieldType()), aN);
		}
		else {
			expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.TIPO_MITTENTE.getFieldType()), aT);
			expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.MITTENTE.getFieldType()), aN);
		}
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomFakeSoggettoRemoto(UnionExpression expr, StatisticaModel model, StatsSearchForm form, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		if(TipologiaRicerca.ingresso.equals(form.getTipologiaRicercaEnum())){
			expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.TIPO_MITTENTE.getFieldType()), aT);
			expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.MITTENTE.getFieldType()), aN);
		}
		else {
			expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.TIPO_DESTINATARIO.getFieldType()), aT);
			expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
					model.DESTINATARIO.getFieldType()), aN);
		}
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomFakeSoggettoFruitore(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.TIPO_MITTENTE.getFieldType()), aT);
		expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.MITTENTE.getFieldType()), aN);
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomFakeSoggettoErogatore(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.TIPO_DESTINATARIO.getFieldType()), aT);
		expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.DESTINATARIO.getFieldType()), aN);
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	private void impostaSelectField3dCustomFakeServizioApplicativoTrasporto(UnionExpression expr, StatisticaModel model, List<String> aliases)
			throws ExpressionException {
		String aSA = ALIAS_FIELD_DATA_3D_CUSTOM+"sa";
		String aT = ALIAS_FIELD_DATA_3D_CUSTOM+"tipo";
		String aN = ALIAS_FIELD_DATA_3D_CUSTOM+"nome";
		expr.addSelectField(new ConstantField(aSA, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.SERVIZIO_APPLICATIVO.getFieldType()), aSA);
		expr.addSelectField(new ConstantField(aT, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.TIPO_MITTENTE.getFieldType()), aT);
		expr.addSelectField(new ConstantField(aN, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,
				model.MITTENTE.getFieldType()), aN);
		addAlias(aliases, aSA);
		addAlias(aliases, aT);
		addAlias(aliases, aN);
	}
	
	private void addAlias(List<String> aliases, String a) {
		if(!aliases.contains(a)) {
			aliases.add(a);
		}
	}
	
	private String getCustomData(Map<String, Object> row, DimensioneCustom dc, StatsSearchForm form, StringBuilder res, BooleanNullable bSkip) throws NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException, DriverRegistroServiziException, ProtocolException, ExpressionException, CoreException {
		if(dc!=null) {
			switch (dc) {
			case TAG:{
				return getCustomDataCredenzialeNormalize(row, res, "tag");
			}
			case API:{
				return getCustomDataApi(row, form, res);
			}
			case IMPLEMENTAZIONE_API:{
				return getCustomDataApiImplementation(row, res);
			}
			case OPERAZIONE:{
				return getCustomData(row, res, "op");
			}
			case SOGGETTO_LOCALE:
			case SOGGETTO_REMOTO:
			case SOGGETTO_FRUITORE:
			case SOGGETTO_EROGATORE:
			{
				return getCustomDataSoggetto(row, res);
			}
			case TOKEN_CLIENT_ID:{
				return getCustomDataCredenzialeTokenClient(row, res, "tc", false, bSkip);
			}
			case APPLICATIVO_TOKEN:{
				return getCustomDataCredenzialeTokenClient(row, res, "tc", true, bSkip);
			}
			case TOKEN_PDND_ORGANIZATION:{
				return getCustomDataCredenzialeTokenPDNDOrganization(row, res, "tc", bSkip);
			}
			case TOKEN_PDND_ORGANIZATION_EXTERNAL_ID:{
				return getCustomDataCredenzialeTokenPDNDOrganizationExternalId(row, res, "tc", bSkip);
			}
			case TOKEN_PDND_ORGANIZATION_CONSUMER_ID:{
				return getCustomDataCredenzialeTokenPDNDOrganizationConsumerId(row, res, "tc", bSkip);
			}
			case TOKEN_ISSUER:{
				return getCustomDataCredenziale(row, res, "ti");
			}
			case TOKEN_SUBJECT:{
				return getCustomDataCredenziale(row, res, "ts");
			}
			case TOKEN_USERNAME:{
				return getCustomDataCredenziale(row, res, "tu");
			}
			case TOKEN_EMAIL:{
				return getCustomDataCredenziale(row, res, "te");
			}
			case APPLICATIVO_TRASPORTO:{
				return getCustomDataApplicativoTrasporto(row, res);
			}
			case PRINCIPAL:{
				return getCustomDataCredenzialePrincipal(row, res, "p");
			}
			case INDIRIZZO_IP:{
				return getCustomDataCredenzialeIndirizzoIp(row, res, "ip");
			}
			case ESITO:{
				return getCustomDataEsito(row, res, form, "e");
			}
			}
		}
		else {
			throw new ExpressionException(MESSAGE_3D_CUSTOM_INFORMAZIONE_NON_INDICATA);
		}
		return null;
	}
	private String getCustomData(Map<String, Object> row, StringBuilder res, String alias) throws NumberFormatException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		if(oRisultato instanceof String) {
			return (String) oRisultato;
		}
		return null;
	}
	private String getCustomDataCredenziale(Map<String, Object> row, StringBuilder res, String alias) throws NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		String v = getCustomDataCredenzialeMittente(oRisultato);
		if(v!=null && StringUtils.isNotEmpty(v)) {
			return v;
		}
		if(oRisultato instanceof String) {
			return (String) oRisultato;
		}
		return null;
	}
	private String getCustomDataCredenzialeNormalize(Map<String, Object> row, StringBuilder res, String alias) throws NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		String tagValue = getCustomDataCredenzialeMittente(oRisultato);
		if(tagValue!=null && StringUtils.isNotEmpty(tagValue)) {
			String s = AbstractCredenzialeList.normalize(tagValue);
			if(s!=null) {
				return s;
			}
			
		}
		if(oRisultato instanceof String) {
			return (String) oRisultato;
		}
		return null;
	}
	private String getCustomDataCredenzialeTokenClient(Map<String, Object> row, StringBuilder res, String alias, boolean applicativo, BooleanNullable bSkip) throws CoreException, NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException, ProtocolException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		String rValue = getCustomDataCredenzialeMittente(oRisultato);
		if(rValue!=null && StringUtils.isNotEmpty(rValue)) {
			if(applicativo) {
				IDServizioApplicativo idApplicativo = CredenzialeTokenClient.convertApplicationDBValueToOriginal(rValue);
				if(idApplicativo!=null) {
					return NamingUtils.getLabelServizioApplicativo(idApplicativo);
				}
				else {
					bSkip.setValue(true);
				}
			}
			else {
				return CredenzialeTokenClient.convertClientIdDBValueToOriginal(rValue);
			}
			
		}
		if(applicativo) {
			bSkip.setValue(true);
		}
		if(oRisultato instanceof String) {
			return (String) oRisultato;
		}
		return null;
	}
	private String getCustomDataCredenzialeTokenPDNDOrganization(Map<String, Object> row, StringBuilder res, String alias, BooleanNullable bSkip) throws NumberFormatException, ServiceException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		if(oRisultato instanceof String) {
			String risultato = (String) oRisultato;
			String organizzazione = null;
			try {
				MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
				CredenzialeMittente credenzialeMittentePDNDOrganization = mBeanUtilsService.getCredenzialeMittenteByReferenceFromCache(TipoCredenzialeMittente.PDND_ORGANIZATION_NAME,Long.parseLong(risultato));
				if(credenzialeMittentePDNDOrganization!=null) {
					organizzazione = credenzialeMittentePDNDOrganization.getCredenziale();
				}
			}catch(Exception tApp) {
				StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
			}
			if(organizzazione==null) {
				bSkip.setValue(true);
			}
			else {
				return organizzazione;
			}
		}
		bSkip.setValue(true);
		return null;
	}
	private String getCustomDataCredenzialeTokenPDNDOrganizationExternalId(Map<String, Object> row, StringBuilder res, String alias, BooleanNullable bSkip) throws NumberFormatException, ServiceException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		if(oRisultato instanceof String) {
			String risultato = (String) oRisultato;
			String pdndOrganizationExternalId = null;
			try {
				String json = getCustomDataCredenzialeTokenPDNDOrganizationJson(row, res, alias, bSkip, risultato);
				if(json!=null) {
					pdndOrganizationExternalId = getCustomDataCredenzialeTokenPDNDOrganizationExternalId(json);
				}
			}catch(Exception tApp) {
				StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
			}
			if(pdndOrganizationExternalId==null) {
				bSkip.setValue(true);
			}
			else {
				return pdndOrganizationExternalId;
			}
		}
		bSkip.setValue(true);
		return null;
	}
	private String getCustomDataCredenzialeTokenPDNDOrganizationExternalId(String json) throws ProtocolException {
		String pdndOrganizationExternalId = null;
		String origin = PDNDTokenInfo.readOrganizationExternalOriginFromJson(log, json);
		String id = PDNDTokenInfo.readOrganizationExternalIdFromJson(log, json);
		if(origin!=null && StringUtils.isNotEmpty(origin) &&
				id!=null && StringUtils.isNotEmpty(id)) {
			pdndOrganizationExternalId = origin + " "+id;
		}
		else if(origin!=null && StringUtils.isNotEmpty(origin)) {
			pdndOrganizationExternalId = origin;
		}
		else if(id!=null && StringUtils.isNotEmpty(id)) {
			pdndOrganizationExternalId = id;
		}
		return pdndOrganizationExternalId;
	}
	private String getCustomDataCredenzialeTokenPDNDOrganizationConsumerId(Map<String, Object> row, StringBuilder res, String alias, BooleanNullable bSkip) throws NumberFormatException, ServiceException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		if(oRisultato instanceof String) {
			String risultato = (String) oRisultato;
			String pdndConsumerId = null;
			try {
				String json = getCustomDataCredenzialeTokenPDNDOrganizationJson(row, res, alias, bSkip, risultato);
				if(json!=null) {
					String id = PDNDTokenInfo.readOrganizationIdFromJson(log, json);
					if(id!=null && StringUtils.isNotEmpty(id)) {
						pdndConsumerId = id;
					}
				}
			}catch(Exception tApp) {
				StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
			}
			if(pdndConsumerId==null) {
				bSkip.setValue(true);
			}
			else {
				return pdndConsumerId;
			}
		}
		bSkip.setValue(true);
		return null;
	}
	private String getCustomDataCredenzialeTokenPDNDOrganizationJson(Map<String, Object> row, StringBuilder res, String alias, BooleanNullable bSkip, String risultato) throws NumberFormatException, ServiceException, NotFoundException, NotImplementedException {
		try {
			MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
			CredenzialeMittente credenzialeMittentePDNDOrganization = mBeanUtilsService.getCredenzialeMittenteByReferenceFromCache(TipoCredenzialeMittente.PDND_ORGANIZATION_JSON,Long.parseLong(risultato));
			if(credenzialeMittentePDNDOrganization!=null) {
				return credenzialeMittentePDNDOrganization.getCredenziale();
			}
		}catch(Exception tApp) {
			StatisticheGiornaliereService.logError(tApp.getMessage(), tApp);
		}
		return null;
	}
	private String getCustomDataApplicativoTrasporto(Map<String, Object> row, StringBuilder res) throws ProtocolException {
		IDServizioApplicativo idSA = new IDServizioApplicativo();
		idSA.setNome((String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"sa"));
		idSA.setIdSoggettoProprietario(new IDSoggetto((String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"tipo"), (String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"nome")));
		res.append(idSA.toFormatString());
		return NamingUtils.getLabelServizioApplicativo(idSA);
	}
	private String getCustomDataCredenzialePrincipal(Map<String, Object> row, StringBuilder res, String alias) throws NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		if(oRisultato instanceof String) {
			MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
			String risultato = (String)oRisultato;
			CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
			
			if(credenzialeMittente!=null) {
				String tipoCredenziale = convertToTipoCredenziale(credenzialeMittente.getTipo());
				return "["+tipoCredenziale+"] "+credenzialeMittente.getCredenziale();
			}
		
			return risultato;
		}
		return null;
	}
	private String getCustomDataCredenzialeIndirizzoIp(Map<String, Object> row, StringBuilder res, String alias) throws NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		String v = getCustomDataCredenzialeMittente(oRisultato);
		if(oRisultato instanceof String && v!=null && StringUtils.isNotEmpty(v)) {
			return getLabelCredenzialeFieldGroupByIndirizzoIp(v, (String) oRisultato);
		}
		if(oRisultato instanceof String) {
			return (String) oRisultato;
		}
		return null;
	}
	private String getCustomDataApi(Map<String, Object> row, StatsSearchForm form, StringBuilder res) throws ProtocolException, NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException {
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"api");
		res.append(oRisultato);
		
		String s = getCustomDataCredenzialeMittente(oRisultato);
		if(s!=null && StringUtils.isNotEmpty(s)) {
			IDAccordo idAccordo = getIDAccordoFromUriSafe(s);
			if(idAccordo!=null) {
				return NamingUtils.getLabelAccordoServizioParteComune(form.getProtocollo(), idAccordo);
			}
		}
		return s;
	}
	private String getCustomDataApiImplementation(Map<String, Object> row, StringBuilder res) throws ProtocolException, DriverRegistroServiziException {
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues((String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"ts"), (String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"s"),
				(String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"td"), (String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"d"),
				(Integer)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"vs")); 	
		res.append(idServizio.toString());
		return NamingUtils.getLabelAccordoServizioParteSpecifica(idServizio);
	}
	private String getCustomDataSoggetto(Map<String, Object> row, StringBuilder res) throws ProtocolException {
		IDSoggetto idSoggetto = new IDSoggetto((String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"tipo"), (String)row.get(ALIAS_FIELD_DATA_3D_CUSTOM+"nome"));
		res.append(idSoggetto.toString());
		return NamingUtils.getLabelSoggetto(idSoggetto);
	}
	private String getCustomDataCredenzialeMittente(Object oRisultato) throws NumberFormatException, ServiceException, MultipleResultException, NotFoundException, NotImplementedException {
		if(oRisultato instanceof String) {
			MBeanUtilsService mBeanUtilsService = new MBeanUtilsService(this.credenzialiMittenteDAO, StatisticheGiornaliereService.log);
			String risultato = (String)oRisultato;
			CredenzialeMittente credenzialeMittente = mBeanUtilsService.getCredenzialeMittenteFromCache(Long.parseLong(risultato));
			return credenzialeMittente != null ? credenzialeMittente.getCredenziale() : risultato;
		}
		return null;
	}
	private IDAccordo getIDAccordoFromUriSafe(String s) {
		if(s!=null) {
			try {
				return IDAccordoFactory.getInstance().getIDAccordoFromUri(s);
			}catch(Exception e) {
				// ignore
			}
		}
		return null;
	}
	private String getCustomDataEsito(Map<String, Object> row, StringBuilder res, StatsSearchForm form, String alias) throws NumberFormatException, ServiceException, ProtocolException {
		
		EsitiProperties esitiProperties = null;
		try {
			esitiProperties = EsitiProperties.getInstanceFromProtocolName(StatisticheGiornaliereService.log, form.getProtocollo());
		}catch(Exception t) {
			StatisticheGiornaliereService.logError("EsitiProperties reader non disponibile: "+t.getMessage(), t);
		}
		if(esitiProperties==null) {
			throw new ServiceException("EsitiProperties unavailable");
		}
		
		Object oRisultato = row.get(ALIAS_FIELD_DATA_3D_CUSTOM+alias);
		res.append(oRisultato);
		if(oRisultato instanceof String) {
			int esito = Integer.parseInt((String)oRisultato);
			return esitiProperties.getEsitoLabel(esito);
		}
		else if(oRisultato instanceof Integer) {
			int esito = (Integer)oRisultato;
			return esitiProperties.getEsitoLabel(esito);
		}
		else if(oRisultato instanceof Long) {
			int esito = ((Long)oRisultato).intValue();
			return esitiProperties.getEsitoLabel(esito);
		}
		return null;
	}
}
