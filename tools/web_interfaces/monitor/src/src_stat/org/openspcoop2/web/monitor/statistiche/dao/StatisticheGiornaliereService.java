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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDServizio;
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
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchClientAddress;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchGruppo;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchToken;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeSearchTrasporto;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
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
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.condition.FilterImpl;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.monitor.engine.statistic.StatisticByResource;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.datamodel.Res;
import org.openspcoop2.web.monitor.core.datamodel.ResBase;
import org.openspcoop2.web.monitor.core.datamodel.ResDistribuzione;
import org.openspcoop2.web.monitor.core.datamodel.ResLive;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.report.CostantiReport;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
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

	private static final String FALSA_UNION_DEFAULT_VALUE = "gbyfake";
	private static final Integer FALSA_UNION_DEFAULT_VALUE_VERSIONE = 1;

	private StatsSearchForm andamentoTemporaleSearch;
	private StatsSearchForm distribSoggettoSearch;
	private StatsSearchForm distribServizioSearch;
	private StatsSearchForm distribAzioneSearch;
	private StatsSearchForm distribSaSearch;
	private StatistichePersonalizzateSearchForm statistichePersonalizzateSearch;

	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;
	
	private org.openspcoop2.core.statistiche.dao.IServiceManager transazioniStatisticheServiceManager;

	private IStatisticaGiornalieraServiceSearch statGiornaliereSearchDAO;

	private IStatisticaMensileServiceSearch statMensileSearchDAO;

	private IStatisticaOrariaServiceSearch statOrariaSearchDAO;

	private IStatisticaSettimanaleServiceSearch statSettimanaleSearchDAO;
	
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniServiceManager;
	private org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialiMittenteDAO;

	private PddMonitorProperties govwayMonitorProperties;
	
	public StatisticheGiornaliereService() {

		try {
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance(StatisticheGiornaliereService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), StatisticheGiornaliereService.log);
			
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
			
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}
	}
	
	public org.openspcoop2.core.commons.search.dao.IServiceManager getUtilsServiceManager() {
		return this.utilsServiceManager;
	}
	
	public void setAndamentoTemporaleSearch(
			StatsSearchForm andamentoTemporaleSearch) {
		this.andamentoTemporaleSearch = andamentoTemporaleSearch;
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
	
	
	
	
	
	
	
	// ********** ANDAMENTO TEMPORALE (e DISTRUBUZIONE PER ESITI) ******************
	
	
	
	@Override
	public int countAllAndamentoTemporale() throws ServiceException {
		try {
			return Long.valueOf(countAndamentoTemporale().longValue()).intValue();
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			StatisticType tipologia = this.andamentoTemporaleSearch.getModalitaTemporale();
			IExpression gByExpr = null;

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
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexAndamentoTemporaleCount(tipologia, 
										this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}

			gByExpr = createGenericAndamentoTemporaleExpression(dao, model,	true);
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

			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber nnn = dao.count(gByExpr);
			return nnn;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}
		return new NonNegativeNumber(0);
	}

	private List<Res> executeAndamentoTemporaleSearch(boolean isCount,	boolean isPaginated, int offset, int limit) {
		try {
			StatisticType tipologia = this.andamentoTemporaleSearch.getModalitaTemporale();
			IExpression gByExpr = null;

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
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexAndamentoTemporaleGroupBy(tipologia, 
										this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			

			gByExpr = createGenericAndamentoTemporaleExpression(dao, model,	isCount);
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
						case LATENZA_PORTA:
							gByExpr.isNotNull(model.LATENZA_PORTA);
							listaFunzioni.add(new  FunctionField(model.LATENZA_PORTA, Function.AVG, "somma_latenza_porta"));
							isLatenza_porta = true;
							break;
						case LATENZA_SERVIZIO:
							gByExpr.isNotNull(model.LATENZA_SERVIZIO);
							listaFunzioni.add(new FunctionField(model.LATENZA_SERVIZIO, Function.AVG, "somma_latenza_servizio"));
							isLatenza_servizio = true;
							break;
						case LATENZA_TOTALE:
						default:
							gByExpr.isNotNull(model.LATENZA_TOTALE);
							listaFunzioni.add(new  FunctionField(model.LATENZA_TOTALE, 	Function.AVG, "somma_latenza_totale"));
							isLatenza_totale = true;
							break;
						}
					}
					else{
						List<TipoLatenza> tipiLatenza = this.andamentoTemporaleSearch.getTipiLatenzaImpostati();
	
						for (TipoLatenza tipoLatenza : tipiLatenza) {
							switch (tipoLatenza) {
							case LATENZA_PORTA:
								gByExpr.isNotNull(model.LATENZA_PORTA);
								listaFunzioni.add(new  FunctionField(model.LATENZA_PORTA, Function.AVG, "somma_latenza_porta"));
								isLatenza_porta = true;
								break;
							case LATENZA_SERVIZIO:
								gByExpr.isNotNull(model.LATENZA_SERVIZIO);
								listaFunzioni.add(new FunctionField(model.LATENZA_SERVIZIO, Function.AVG, "somma_latenza_servizio"));
								isLatenza_servizio = true;
								break;	
							case LATENZA_TOTALE:
							default:
								gByExpr.isNotNull(model.LATENZA_TOTALE);
								listaFunzioni.add(new  FunctionField(model.LATENZA_TOTALE, 	Function.AVG, "somma_latenza_totale"));
								isLatenza_totale = true;
								break;
							}
						}
					}
				}
			}

			List<Map<String, Object>> list = null;

			try{
				if(forceIndexes!=null && forceIndexes.size()>0){
					for (Index index : forceIndexes) {
						gByExpr.addForceIndex(index);	
					}
				}
				
				if (!isPaginated) {
					list = dao.groupBy(gByExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
				} else {
					IPaginatedExpression pagExpr = dao
							.toPaginatedExpression(gByExpr);

					pagExpr.offset(offset).limit(limit);
					list = dao.groupBy(pagExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
				}
			} catch (NotFoundException e) {
				StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente: "+e.getMessage(),e);
				list = new ArrayList<Map<String,Object>>(); // per evitare il nullPointer
			} 

			List<Res> res = new ArrayList<Res>();
			for (Map<String, Object> row : list) {

				Res r = new Res();
				Date data = (Date) row.get(JDBCUtilities.getAlias(model.DATA));
				r.setId(data != null ? data.getTime() : null);
				r.setRisultato(data);

				//collezione dei risultati
				if(isLatenza){
					Number obLT = StatsUtils.converToNumber(row.get("somma_latenza_totale"));
					Number obLS = StatsUtils.converToNumber(row.get("somma_latenza_servizio"));
					Number obLP = StatsUtils.converToNumber(row.get("somma_latenza_porta"));

					if(obLT!=null){
						r.inserisciSomma(obLT);
					}
					else{
						if(isLatenza_totale){
							r.inserisciSomma(0);
						}
					}

					if(obLS!=null){
						r.inserisciSomma(obLS);
					}
					else{
						if(isLatenza_servizio){
							r.inserisciSomma(0);
						}
					}

					if(obLP!=null){
						r.inserisciSomma(obLP);
					}
					else{
						if(isLatenza_porta){
							r.inserisciSomma(0);
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

					IExpression expOk = createGenericAndamentoTemporaleExpression(dao, model,	isCount,data,false);
					IExpression expKo = createGenericAndamentoTemporaleExpression(dao, model,	isCount,data,false);
					IExpression expFault = createGenericAndamentoTemporaleExpression(dao, model,	isCount,data,false);
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
					
					if(forceIndexes!=null && forceIndexes.size()>0){
						for (Index index : forceIndexes) {
							expOk.addForceIndex(index);	
							expKo.addForceIndex(index);	
							expFault.addForceIndex(index);	
						}
					}
					
					EsitiProperties esitiProperties = EsitiProperties.getInstance(StatisticheGiornaliereService.log, this.andamentoTemporaleSearch.getProtocollo());
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
					try{
						listOk = dao.groupBy(expOk, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
					} catch (NotFoundException e) {
						StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente con esiti Ok: "+esitiOk);
						//collezione dei risultati
						rEsito.inserisciSomma(0);
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

							if(obLT != null){
								rEsito.inserisciSomma(obLT);
							}
							else{
								if(isLatenza_totale){
									rEsito.inserisciSomma(0);
								}
							}

							if(obLS != null){
								rEsito.inserisciSomma(obLS);
							}
							else{
								if(isLatenza_servizio){
									rEsito.inserisciSomma(0);
								}
							}

							if(obLP != null){
								rEsito.inserisciSomma(obLP);
							}
							else{
								if(isLatenza_porta){
									rEsito.inserisciSomma(0);
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
					//System.out.println("MISURAZIONE OK: \n"+rEsito.toString());

					List<Map<String, Object>> listFaultApplicativo = null;
					try{
						listFaultApplicativo = dao.groupBy(expFault, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
					} catch (NotFoundException e) {
						StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente con esiti Fault: "+esitiFault);
						//collezione dei risultati
						rEsito.inserisciSomma(0);
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

							if(obLT != null){
								rEsito.inserisciSomma(obLT);
							}
							else{
								if(isLatenza_totale){
									rEsito.inserisciSomma(0);
								}
							}

							if(obLS != null){
								rEsito.inserisciSomma(obLS);
							}
							else{
								if(isLatenza_servizio){
									rEsito.inserisciSomma(0);
								}
							}

							if(obLP != null){
								rEsito.inserisciSomma(obLP);
							}
							else{
								if(isLatenza_porta){
									rEsito.inserisciSomma(0);
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
					//System.out.println("MISURAZIONE FAULT: \n"+rEsito.toString());

					List<Map<String, Object>> listKo = null;
					try{
						listKo = dao.groupBy(expKo, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
					} catch (NotFoundException e) {
						StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente con esiti Ko: "+esitiKo);
						//collezione dei risultati
						rEsito.inserisciSomma(0);
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

							if(obLT != null){
								rEsito.inserisciSomma(obLT);
							}
							else{
								if(isLatenza_totale){
									rEsito.inserisciSomma(0);
								}
							}

							if(obLS != null){
								rEsito.inserisciSomma(obLS);
							}
							else{
								if(isLatenza_servizio){
									rEsito.inserisciSomma(0);
								}
							}

							if(obLP != null){
								rEsito.inserisciSomma(obLP);
							}
							else{
								if(isLatenza_porta){
									rEsito.inserisciSomma(0);
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
					//System.out.println("MISURAZIONE KO: \n"+rEsito.toString());


					// ************ FINE ESITI ***********************

					//System.out.println("ESITI");
					res.add(rEsito);

				}
				else{

					//System.out.println("NORMALE");
					res.add(r);
				}

			}

			return res;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return new ArrayList<Res>();
	}

	private IExpression createGenericAndamentoTemporaleExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			boolean isCount) {
		return this.createGenericAndamentoTemporaleExpression(dao, model, isCount, null, true);
	}
	private IExpression createGenericAndamentoTemporaleExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, 
			boolean isCount, Date date, boolean setEsito) {

		IExpression expr = null;

		StatisticheGiornaliereService.log
		.debug("creo  Expression per Andamento Temporale!");

		try {

			List<Soggetto> listaSoggettiGestione = this.andamentoTemporaleSearch
					.getSoggettiGestione();

			expr = dao.newExpression();

			// Data
			if(date==null){
				expr.between(model.DATA, this.andamentoTemporaleSearch.getDataInizio(),	this.andamentoTemporaleSearch.getDataFine());
			}
			else{
				expr.equals(model.DATA,date);
			}

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
				boolean soggetto = listaSoggettiGestione.size() > 0;
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

					if (listaSoggettiGestione.size() > 0) {
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

					if (listaSoggettiGestione.size() > 0) {
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
				if (listaSoggettiGestione.size() > 0) {
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
				if (StringUtils.isNotBlank(this.andamentoTemporaleSearch
						.getNomeMittente())) {
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
				if (listaSoggettiGestione.size() > 0) {
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
			

			if(date==null){
				// ORDER BY
				SortOrder s = 	this.andamentoTemporaleSearch.getSortOrder() != null ? 	this.andamentoTemporaleSearch.getSortOrder() : SortOrder.ASC;

				if (!isCount) {
					expr.sortOrder(s).addOrder(model.DATA);

				}
			}

			expr.addGroupBy(model.DATA);

		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return expr;
	}

	
	
	
	
	
	// ********** ESITI LIVE ******************
	
	@Override
	public ResLive getEsiti(PermessiUtenteOperatore permessiUtente, Date min, Date max,	String periodo, String esitoContesto,String protocollo, TipologiaRicerca tipologiaRicerca) {

		// StringBuilder pezzoIdPorta = new StringBuilder();
		StatisticheGiornaliereService.log.debug("Get Esiti [id porta: " + permessiUtente + "],[ Date Min: " + min + "], [Date Max: " + max + "]");
		try {
			StatisticaModel model = null;
			IServiceSearchWithoutId<?> dao = null;
			StatisticType tipologia = null;
			
			if(periodo.equals(CostantiReport.ULTIMO_ANNO)){
				model = StatisticaMensile.model().STATISTICA_BASE;
				dao = this.statMensileSearchDAO;
				tipologia = StatisticType.MENSILE;
			} else if(periodo.equals(CostantiReport.ULTIMI_30_GIORNI)){
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				dao = this.statSettimanaleSearchDAO;
				tipologia = StatisticType.SETTIMANALE;
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

			EsitiProperties esitiProperties = EsitiProperties.getInstance(StatisticheGiornaliereService.log, protocollo);
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, protocollo);
			List<Integer> esitiOk = esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
			List<Integer> esitiKo = esitiProperties.getEsitiCodeKo_senzaFaultApplicativo();
			List<Integer> esitiFault = esitiProperties.getEsitiCodeFaultApplicativo();

			// ok 
			IExpression exprOk =  dao.newExpression();
			exprOk.between(model.DATA, min,max);
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

			impostaTipiCompatibiliConProtocollo(dao, model, exprOk, protocollo, null);
			impostaTipiCompatibiliConProtocollo(dao, model, exprFault, protocollo, null);
			impostaTipiCompatibiliConProtocollo(dao, model, exprKo, protocollo, null);
			
			if(forceIndexes!=null && forceIndexes.size()>0){
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
				StatisticheGiornaliereService.log.debug("Non sono presenti statistiche con esito OK");
			}
			long s = 0l;
			if(list != null && list.size() > 0){
				for (Map<String, Object> row : list) {

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						s = numOk.longValue() + somma.longValue();
					}

				}
			}
			numOk = Long.valueOf(s);

			// fault
			list = new ArrayList<Map<String,Object>>();
			try {
				list = dao.groupBy(exprFault,fSum);
			} catch (NotFoundException e) {
				StatisticheGiornaliereService.log.debug("Non sono presenti statistiche con esito Fault");
			}
			s = 0l;
			if(list != null && list.size() > 0){
				for (Map<String, Object> row : list) {

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						s = numFault.longValue() + somma.longValue();
					}

				}
			}
			numFault = Long.valueOf(s);

			// ko
			list = new ArrayList<Map<String,Object>>();
			try {
				list = dao.groupBy(exprKo,fSum);
			} catch (NotFoundException e) {
				StatisticheGiornaliereService.log.debug("Non sono presenti statistiche con esito KO");
			}
			s = 0l;
			if(list != null && list.size() > 0){
				for (Map<String, Object> row : list) {

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						s = numKo.longValue() + somma.longValue();
					}

				}
			}
			numKo = Long.valueOf(s);

			return new ResLive(numOk.longValue(), numFault.longValue(), numKo.longValue(), new Date());

		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return new ResLive(Long.valueOf("0"), Long.valueOf("0"), Long.valueOf("0"));
	}

	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER SOGGETTO ******************

	@Override
	public int countAllDistribuzioneSoggetto() throws ServiceException{

		try {

			StatisticType tipologia = this.distribSoggettoSearch.getModalitaTemporale();
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}

		//		return 0;
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneSoggetto() throws ServiceException{
		try {

			StatisticType tipologia = this.distribSoggettoSearch.getModalitaTemporale();
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
			return this.executeDistribuzioneSoggetto( dao,model, false, -1, -1);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ResDistribuzione>();
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzioneSoggetto(int start,int limit)  throws ServiceException{
		try {

			StatisticType tipologia = this.distribSoggettoSearch.getModalitaTemporale();
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
			return this.executeDistribuzioneSoggetto(dao,model, true, start, limit);

		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ResDistribuzione>();
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

				// MITTENTE
				IExpression mitExpr = dao.newExpression();


				//tipo porta
				mitExpr.equals(model.TIPO_PORTA,
						"applicativa");


				// Data
				mitExpr.and().between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());

				// Protocollo
				String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				// protocollo e' impostato anche scegliendo la modalita'
//				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
				if (this.distribSoggettoSearch.isSetFiltroProtocollo()) {
					//					mitExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, mitExpr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					mitExpr.and(permessi);
				}

				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					mitExpr.and().equals(model.ID_PORTA, idPorta);
				}
				
				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					mitExpr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					mitExpr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(mitExpr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (listaSoggettiGestione.size() > 0) {
					mitExpr.and();

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
					mitExpr.or(orSoggetti);
				}
				
				this.impostaFiltroDatiMittente(mitExpr, this.distribSoggettoSearch, model, true);
				
				this.impostaFiltroGruppo(mitExpr, this.distribSoggettoSearch, model, true);

				// DESTINATARIO
				IExpression destExpr = dao.newExpression();

				destExpr.equals(model.TIPO_PORTA,
						"delegata");

				// Data
				destExpr.and().between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());

				// Protocollo
				//String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
					//					destExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, destExpr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					destExpr.and(permessi);
				}
				
				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					destExpr.and().equals(model.ID_PORTA, idPorta);
				}

				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					destExpr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					destExpr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(destExpr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (listaSoggettiGestione.size() > 0) {
					destExpr.and();

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
					destExpr.or(orSoggetti);
				}
				
				this.impostaFiltroDatiMittente(destExpr, this.distribSoggettoSearch, model, true);
				
				this.impostaFiltroGruppo(destExpr, this.distribSoggettoSearch, model, true);

				mitExpr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				mitExpr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				mitExpr.addGroupBy(model.TIPO_MITTENTE);
				mitExpr.addGroupBy(model.MITTENTE);

				destExpr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				destExpr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				destExpr.addGroupBy(model.TIPO_DESTINATARIO);
				destExpr.addGroupBy(model.DESTINATARIO);

				if(forceIndexes!=null && forceIndexes.size()>0){
					for (Index index : forceIndexes) {
						mitExpr.addForceIndex(index);	
						destExpr.addForceIndex(index);	
					}
				}
				
				UnionExpression mitUnionExpr = new UnionExpression(mitExpr);
				mitUnionExpr.addSelectField(
						model.TIPO_MITTENTE, "tipo_soggetto");
				mitUnionExpr.addSelectField(model.MITTENTE,
						"soggetto");

				UnionExpression destUnionExpr = new UnionExpression(destExpr);
				destUnionExpr.addSelectField(
						model.TIPO_DESTINATARIO,
						"tipo_soggetto");
				destUnionExpr.addSelectField(
						model.DESTINATARIO, "soggetto");

				Union union = new Union();
				union.setUnionAll(true);
				union.addField("tipo_soggetto");
				union.addField("soggetto");
				union.addGroupBy("tipo_soggetto");
				union.addGroupBy("soggetto");


				NonNegativeNumber nnn = dao.unionCount(union, mitUnionExpr, destUnionExpr); 
				return nnn != null ? nnn.longValue() : 0L;

			} else if (TipologiaRicerca.ingresso.equals(this.distribSoggettoSearch.getTipologiaRicercaEnum())) {
				// EROGAZIONE
				// il destinatario e' l'utente loggato (sempre presente se non
				// sono
				// admin)

				// MITTENTE
				IExpression mitExpr = dao.newExpression();

				// Data
				mitExpr.between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());

				// Protocollo
				String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
					//					mitExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, mitExpr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				mitExpr.and().equals(model.TIPO_PORTA,
						"applicativa");

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					mitExpr.and(permessi);
				}
				
				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					mitExpr.and().equals(model.ID_PORTA, idPorta);
				}

				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					mitExpr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					mitExpr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(mitExpr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (listaSoggettiGestione.size() > 0) {
					mitExpr.and();

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
					mitExpr.or(orSoggetti);
				}
				
				this.impostaFiltroDatiMittente(mitExpr, this.distribSoggettoSearch, model, true);
				
				this.impostaFiltroGruppo(mitExpr, this.distribSoggettoSearch, model, true);

				mitExpr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				mitExpr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				mitExpr.addGroupBy(model.TIPO_MITTENTE);
				mitExpr.addGroupBy(model.MITTENTE);

				if(forceIndexes!=null && forceIndexes.size()>0){
					for (Index index : forceIndexes) {
						mitExpr.addForceIndex(index);		
					}
				}
				
				UnionExpression mitUnionExpr = new UnionExpression(mitExpr);
				mitUnionExpr.addSelectField(
						model.TIPO_MITTENTE, "tipo_soggetto");
				mitUnionExpr.addSelectField(model.MITTENTE,
						"soggetto");

				// Espressione finta per usare l'ordinamento
				IExpression fakeExpr = dao.newExpression();
				UnionExpression unionExprFake = new UnionExpression(fakeExpr);
				unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_MITTENTE.getFieldType()), "tipo_soggetto");
				unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.MITTENTE.getFieldType()), "soggetto");

				Union union = new Union();
				union.setUnionAll(true);
				union.addField("tipo_soggetto");
				union.addField("soggetto");
				union.addGroupBy("tipo_soggetto");
				union.addGroupBy("soggetto");

				NonNegativeNumber nnn = dao.unionCount(union, mitUnionExpr, unionExprFake); 
				return nnn != null ? nnn.longValue() - 1 : 0L;
			} else {
				// FRUIZIONE
				// il mittente e' l'utente loggato (sempre presente)

				// DESTINATARIO
				IExpression destExpr = dao.newExpression();

				// Data
				destExpr.between(model.DATA,
						this.distribSoggettoSearch.getDataInizio(),
						this.distribSoggettoSearch.getDataFine());

				// Protocollo
				String protocollo = null;
				// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
				if (StringUtils.isNotEmpty(this.distribSoggettoSearch.getProtocollo()) && this.distribSoggettoSearch.isShowListaProtocolli()) {
					//					destExpr.and().equals(model.PROTOCOLLO,	this.distribSoggettoSearch.getProtocollo());
					protocollo = this.distribSoggettoSearch.getProtocollo();

					impostaTipiCompatibiliConProtocollo(dao, model, destExpr, protocollo, this.distribSoggettoSearch.getTipologiaRicercaEnum());

				}

				destExpr.and().equals(model.TIPO_PORTA,
						"delegata");

				// permessi utente operatore
				if(this.distribSoggettoSearch.getPermessiUtenteOperatore()!=null){
					IExpression permessi = this.distribSoggettoSearch.getPermessiUtenteOperatore().toExpression(dao, model.ID_PORTA, 
							model.TIPO_DESTINATARIO,model.DESTINATARIO,
							model.TIPO_SERVIZIO,model.SERVIZIO, model.VERSIONE_SERVIZIO);
					destExpr.and(permessi);
				}
				
				// soggetto locale
				if(Utility.isFiltroDominioAbilitato() && this.distribSoggettoSearch.getSoggettoLocale()!=null && !StringUtils.isEmpty(this.distribSoggettoSearch.getSoggettoLocale()) && 
						!"--".equals(this.distribSoggettoSearch.getSoggettoLocale())){
					String tipoSoggettoLocale = this.distribSoggettoSearch.getTipoSoggettoLocale();
					String nomeSoggettoLocale = this.distribSoggettoSearch.getSoggettoLocale();
					String idPorta = Utility.getIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale);
					destExpr.and().equals(model.ID_PORTA, idPorta);
				}

				// azione
				if (StringUtils.isNotBlank(this.distribSoggettoSearch
						.getNomeAzione()))
					destExpr.and().equals(model.AZIONE,
							this.distribSoggettoSearch.getNomeAzione());

				// nome servizio
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeServizio())){
					
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(this.distribSoggettoSearch.getNomeServizio());
					
					destExpr.and().
						equals(model.TIPO_DESTINATARIO,	idServizio.getSoggettoErogatore().getTipo()).
						equals(model.DESTINATARIO,	idServizio.getSoggettoErogatore().getNome()).
						equals(model.TIPO_SERVIZIO,	idServizio.getTipo()).
						equals(model.SERVIZIO,	idServizio.getNome()).
						equals(model.VERSIONE_SERVIZIO,	idServizio.getVersione());

				}

				// esito
				esitoUtils.setExpression(destExpr, this.distribSoggettoSearch.getEsitoGruppo(), 
						this.distribSoggettoSearch.getEsitoDettaglio(),
						this.distribSoggettoSearch.getEsitoDettaglioPersonalizzato(),
						this.distribSoggettoSearch.getEsitoContesto(),
						this.distribSoggettoSearch.isEscludiRichiesteScartate(),
						model.ESITO, model.ESITO_CONTESTO,
						dao.newExpression());


				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (listaSoggettiGestione.size() > 0) {
					destExpr.and();

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
					destExpr.or(orSoggetti);
				}
				
				this.impostaFiltroDatiMittente(destExpr, this.distribSoggettoSearch, model, true);
				
				this.impostaFiltroGruppo(destExpr, this.distribSoggettoSearch, model, true);

				destExpr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				destExpr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				destExpr.addGroupBy(model.TIPO_DESTINATARIO);
				destExpr.addGroupBy(model.DESTINATARIO);

				if(forceIndexes!=null && forceIndexes.size()>0){
					for (Index index : forceIndexes) {
						destExpr.addForceIndex(index);	
					}
				}
				
				UnionExpression destUnionExpr = new UnionExpression(destExpr);
				destUnionExpr.addSelectField(
						model.TIPO_DESTINATARIO,
						"tipo_soggetto");
				destUnionExpr.addSelectField(
						model.DESTINATARIO, "soggetto");

				// Espressione finta per usare l'ordinamento
				IExpression fakeExpr = dao.newExpression();
				UnionExpression unionExprFake = new UnionExpression(fakeExpr);
				unionExprFake.addSelectField(new ConstantField("tipo_soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE,	model.TIPO_DESTINATARIO.getFieldType()), "tipo_soggetto");
				unionExprFake.addSelectField(new ConstantField("soggetto", StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, model.DESTINATARIO.getFieldType()), "soggetto");

				Union union = new Union();
				union.setUnionAll(true);
				union.addField("tipo_soggetto");
				union.addField("soggetto");
				union.addGroupBy("tipo_soggetto");
				union.addGroupBy("soggetto");

				NonNegativeNumber nnn =  dao.unionCount(union, destUnionExpr, unionExprFake); 
				return nnn != null ? nnn.longValue() - 1 : 0L;

				//				return dao.count(destExpr);
			}
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}
		return 0L;
	}

	private List<ResDistribuzione> executeDistribuzioneSoggetto(
			IServiceSearchWithoutId<?> dao,
			StatisticaModel model, boolean isPaginated, int start,
			int limit) throws ExpressionNotImplementedException,
			ExpressionException, ServiceException, NotImplementedException,
			CoreException, NotFoundException, ProtocolException, UserInvalidException, UtilsException {

		List<Index> forceIndexes = null;
		try{
			forceIndexes = 
					this.convertForceIndexList(model, 
							this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneSoggettoGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
		
		List<Map<String, Object>> list = null;
		ArrayList<ResDistribuzione> res = new ArrayList<ResDistribuzione>();
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
			if (listaSoggettiGestione.size() > 0) {
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

			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()==false){
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
			if (listaSoggettiGestione.size() > 0) {
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

			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()==false){
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

			// UNION

			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()){
				erogazione_portaApplicativa_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_MITTENTE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.MITTENTE);

				fruizione_portaDelegata_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_DESTINATARIO);
				fruizione_portaDelegata_Expr.addGroupBy(model.DESTINATARIO);
			}
			else{
				erogazione_portaApplicativa_Expr.notEquals(model.TIPO_DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.notEquals(model.DESTINATARIO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				erogazione_portaApplicativa_Expr.addGroupBy(model.TIPO_DESTINATARIO);
				erogazione_portaApplicativa_Expr.addGroupBy(model.DESTINATARIO);

				fruizione_portaDelegata_Expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				fruizione_portaDelegata_Expr.addGroupBy(model.TIPO_MITTENTE);
				fruizione_portaDelegata_Expr.addGroupBy(model.MITTENTE);
			}

			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					erogazione_portaApplicativa_Expr.addForceIndex(index);	
					fruizione_portaDelegata_Expr.addForceIndex(index);	
				}
			}
			
			UnionExpression erogazione_portaApplicativa_UnionExpr = new UnionExpression(erogazione_portaApplicativa_Expr);
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
			union.addField("tipo_soggetto");
			union.addField("soggetto");
			union.addGroupBy("tipo_soggetto");
			union.addGroupBy("soggetto");

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
				union.addField("somma", Function.AVG, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_PORTA);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_PORTA);
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_PORTA,
							Function.AVG, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_PORTA,
							Function.AVG, "dato"));
					break;
				case LATENZA_SERVIZIO:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_SERVIZIO);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_SERVIZIO);
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_SERVIZIO,
							Function.AVG, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_SERVIZIO,
							Function.AVG, "dato"));
					break;

				case LATENZA_TOTALE:
				default:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_TOTALE);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_TOTALE);
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_TOTALE,
							Function.AVG, "dato"));
					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_TOTALE,
							Function.AVG, "dato"));
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

			list = dao.union(union, erogazione_portaApplicativa_UnionExpr, fruizione_portaDelegata_UnionExpr);

			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					r.setRisultato(((String) row.get("tipo_soggetto")) + "/"
							+ ((String) row.get("soggetto")));

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
			if (listaSoggettiGestione.size() > 0) {
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

			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()==false){
				// il mittente puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeMittente())) {
					erogazione_portaApplicativa_Expr.and().equals(	model.TIPO_MITTENTE,	this.distribSoggettoSearch.getTipoMittente());
					erogazione_portaApplicativa_Expr.and().equals(	model.MITTENTE, this.distribSoggettoSearch.getNomeMittente());
				}
			}
			
			// filtro dati m
			this.impostaFiltroDatiMittente(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroGruppo(erogazione_portaApplicativa_Expr, this.distribSoggettoSearch, model, false);
			
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

			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					erogazione_portaApplicativa_Expr.addForceIndex(index);	
				}
			}
			
			//			erogazione_portaApplicativa_Expr.sortOrder(SortOrder.ASC);
			//			erogazione_portaApplicativa_Expr.addOrder(model.TIPO_MITTENTE);
			//			erogazione_portaApplicativa_Expr.addOrder(model.MITTENTE);

			UnionExpression erogazione_portaApplicativa_UnionExpr = new UnionExpression(erogazione_portaApplicativa_Expr);
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
			union.addField("tipo_soggetto");
			union.addField("soggetto");
			union.addGroupBy("tipo_soggetto");
			union.addGroupBy("soggetto");

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
				union.addField("somma", Function.AVG, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_PORTA);
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_PORTA,
							Function.AVG, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));

					break;
				case LATENZA_SERVIZIO:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_SERVIZIO);
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_SERVIZIO,
							Function.AVG, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));

					break;

				case LATENZA_TOTALE:
				default:
					erogazione_portaApplicativa_Expr.isNotNull(model.LATENZA_TOTALE);
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					erogazione_portaApplicativa_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_TOTALE,
							Function.AVG, "dato"));
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));

					break;
				}
				break;
			}
			}


			if (isPaginated) {
				union.setOffset(start);
				union.setLimit(limit);
			}

			list = dao.union(union, erogazione_portaApplicativa_UnionExpr, unionExprFake);

			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					r.setRisultato(((String) row.get("tipo_soggetto")) + "/"
							+ ((String) row.get("soggetto")));

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					if(!r.getRisultato().contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE))
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
			if (listaSoggettiGestione.size() > 0) {
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

			if(this.distribSoggettoSearch.isDistribuzionePerSoggettoRemota()==false){
				// il destinatario puo nn essere specificato
				if (StringUtils.isNotBlank(this.distribSoggettoSearch.getNomeDestinatario())) {
					fruizione_portaDelegata_Expr.and().equals(	model.TIPO_DESTINATARIO,	this.distribSoggettoSearch.getTipoDestinatario());
					fruizione_portaDelegata_Expr.and().equals(	model.DESTINATARIO, this.distribSoggettoSearch.getNomeDestinatario());
				}
			}

			// filtro dati m
			this.impostaFiltroDatiMittente(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
			this.impostaFiltroGruppo(fruizione_portaDelegata_Expr, this.distribSoggettoSearch, model, false);
			
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

			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					fruizione_portaDelegata_Expr.addForceIndex(index);	
				}
			}
			
			//			fruizione_portaDelegata_Expr.sortOrder(SortOrder.ASC);
			//			fruizione_portaDelegata_Expr.addOrder(model.TIPO_DESTINATARIO);
			//			fruizione_portaDelegata_Expr.addOrder(model.DESTINATARIO);

			UnionExpression fruizione_portaDelegata_UnionExpr = new UnionExpression(fruizione_portaDelegata_Expr);
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
			union.addField("tipo_soggetto");
			union.addField("soggetto");
			union.addGroupBy("tipo_soggetto");
			union.addGroupBy("soggetto");

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
				union.addField("somma", Function.AVG, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_PORTA);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));

					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_PORTA,
							Function.AVG, "dato"));
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_SERVIZIO);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));

					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_SERVIZIO,
							Function.AVG, "dato"));
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					fruizione_portaDelegata_Expr.isNotNull(model.LATENZA_TOTALE);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale", 
							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));

					fruizione_portaDelegata_UnionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_TOTALE,
							Function.AVG, "dato"));
					break;
				}
				break;
			}
			}

			if (isPaginated) {
				union.setOffset(start);
				union.setLimit(limit);
			}


			list = dao.union(union, fruizione_portaDelegata_UnionExpr, unionExprFake);

			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					r.setRisultato(((String) row.get("tipo_soggetto")) + "/"
							+ ((String) row.get("soggetto")));

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					if(!r.getRisultato().contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) 	
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
			StatisticType tipologia = this.distribSoggettoSearch.getModalitaTemporale();
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
			
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneServizioCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = createDistribuzioneServizioExpression(dao,model, true);
			
			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber nnn = dao.count(gByExpr);

			return nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			
			StatisticType tipologia = this.distribServizioSearch.getModalitaTemporale();
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
			
			IExpression gByExpr = this.createDistribuzioneServizioExpression(dao, model, false);

			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.TIPO_SERVIZIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.SERVIZIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.VERSIONE_SERVIZIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.TIPO_DESTINATARIO);
			gByExpr.sortOrder(SortOrder.ASC).addOrder(model.DESTINATARIO);

			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneServizioGroupBy(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
				
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			String aliasFieldTipoServizio = "tipo_servizio";
			String aliasFieldServizio = "servizio";
			String aliasFieldVersioneServizio = "versione_servizio";
			String aliasFieldTipoDestinatario = "tipo_destinatario";
			String aliasFieldDestinatario = "destinatario";
			
			unionExpr.addSelectField(model.TIPO_SERVIZIO, aliasFieldTipoServizio);
			unionExpr.addSelectField(model.SERVIZIO, aliasFieldServizio);
			unionExpr.addSelectField(model.VERSIONE_SERVIZIO, aliasFieldVersioneServizio);
			unionExpr.addSelectField(model.TIPO_DESTINATARIO, aliasFieldTipoDestinatario);
			unionExpr.addSelectField(model.DESTINATARIO, aliasFieldDestinatario);

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
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
				union.addField("somma", Function.AVG, "dato");

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					gByExpr.isNotNull(model.LATENZA_PORTA);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, "dato"));

					unionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_PORTA,
							Function.AVG, "dato"));
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					gByExpr.isNotNull(model.LATENZA_SERVIZIO);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, "dato"));

					unionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_SERVIZIO,
							Function.AVG, "dato"));
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					gByExpr.isNotNull(model.LATENZA_TOTALE);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, "dato"));

					unionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_TOTALE,
							Function.AVG, "dato"));
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<ResDistribuzione>();

			if(start != null)
				union.setOffset(start);
			if(start != null)
				union.setLimit(limit);

			List<Map<String, Object>> list = dao.union(union, unionExpr, unionExprFake);

			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					r.setRisultato(((String) row.get(aliasFieldTipoServizio)) + "/"
							+ ((String) row.get(aliasFieldServizio)) + ":"
							+ (row.get(aliasFieldVersioneServizio)));
					
					r.getParentMap().put("0",((String) row.get(aliasFieldTipoDestinatario)) + "/"
							+ ((String) row.get(aliasFieldDestinatario)));

					Number somma = StatsUtils.converToNumber(row.get("somma"));
					if(somma!=null){
						r.setSomma(somma);
					}else{
						r.setSomma(0);
					}

					if(!r.getRisultato().contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) 	
						res.add(r);
				}

			}

			return res;

		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		}
		return new ArrayList<ResDistribuzione>();
	}

	private IExpression createDistribuzioneServizioExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, boolean isCount) throws ServiceException {
		IExpression expr = null;

		StatisticheGiornaliereService.log.debug("creo Expression per distribuzione Servizio!");

		List<Soggetto> listaSoggettiGestione = this.distribServizioSearch.getSoggettiGestione();

		try {

			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribServizioSearch.getProtocollo());
			
			expr = dao.newExpression();
			// Data
			expr.between(model.DATA,
					this.distribServizioSearch.getDataInizio(),
					this.distribServizioSearch.getDataFine());

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
				boolean soggetto = listaSoggettiGestione.size() > 0;
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

					if (listaSoggettiGestione.size() > 0) {
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

					if (listaSoggettiGestione.size() > 0) {
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
				if (listaSoggettiGestione.size() > 0) {
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
				if (StringUtils.isNotBlank(this.distribServizioSearch
						.getNomeMittente())) {
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
				if (listaSoggettiGestione.size() > 0) {
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return expr;
	}

	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER AZIONE ******************
	
	@Override
	public int countAllDistribuzioneAzione() throws ServiceException {
		try {
			StatisticType tipologia = this.distribAzioneSearch.getModalitaTemporale();
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
			List<Index> forceIndexes = null;
			try{
				forceIndexes = 
						this.convertForceIndexList(model, 
								this.govwayMonitorProperties.getStatisticheForceIndexDistribuzioneAzioneCount(this.govwayMonitorProperties.getExternalForceIndexRepository()));
			}catch(Exception e){
				throw new ServiceException(e.getMessage(),e);
			}
			
			IExpression gByExpr = createDistribuzioneAzioneExpression(dao,	model, true);
			
			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			NonNegativeNumber nnn = dao.count(gByExpr);

			return nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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

		StatisticheGiornaliereService.log.debug("creo Expression per distribuzione Azione!");

		List<Soggetto> listaSoggettiGestione = this.distribAzioneSearch.getSoggettiGestione();

		try {

			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribAzioneSearch.getProtocollo());

			expr = dao.newExpression();
			// Data
			expr.between(model.DATA,
					this.distribAzioneSearch.getDataInizio(),
					this.distribAzioneSearch.getDataFine());

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
				boolean soggetto = listaSoggettiGestione.size() > 0;
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

					if (listaSoggettiGestione.size() > 0) {
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

					if (listaSoggettiGestione.size() > 0) {
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
				if (listaSoggettiGestione.size() > 0) {
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
				if (StringUtils.isNotBlank(this.distribAzioneSearch
						.getNomeMittente())) {
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
				if (listaSoggettiGestione.size() > 0) {
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return expr;
	}

	private List<ResDistribuzione> executeDistribuzioneAzione(Integer start, Integer limit) throws ServiceException {
		try {
			StatisticType tipologia = this.distribAzioneSearch.getModalitaTemporale();
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
			
			IExpression gByExpr = this.createDistribuzioneAzioneExpression(dao,	model, false);

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
			
			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			String aliasFieldAzione = "azione";
			String aliasFieldTipoServizio = "tipo_servizio";
			String aliasFieldServizio = "servizio";
			String aliasFieldVersioneServizio = "versione_servizio";
			String aliasFieldTipoDestinatario = "tipo_destinatario";
			String aliasFieldDestinatario = "destinatario";
			
			unionExpr.addSelectField(model.AZIONE,		aliasFieldAzione);
			unionExpr.addSelectField(model.TIPO_SERVIZIO, aliasFieldTipoServizio);
			unionExpr.addSelectField(model.SERVIZIO, aliasFieldServizio);
			unionExpr.addSelectField(model.VERSIONE_SERVIZIO, aliasFieldVersioneServizio);
			unionExpr.addSelectField(model.TIPO_DESTINATARIO, aliasFieldTipoDestinatario);
			unionExpr.addSelectField(model.DESTINATARIO, aliasFieldDestinatario);

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = dao.newExpression();
			UnionExpression unionExprFake = new UnionExpression(fakeExpr);
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
			union.addField(aliasFieldAzione);
			union.addField(aliasFieldTipoServizio);
			union.addField(aliasFieldServizio);
			union.addField(aliasFieldVersioneServizio);
			union.addField(aliasFieldTipoDestinatario);
			union.addField(aliasFieldDestinatario);
			union.addGroupBy(aliasFieldAzione);
			union.addGroupBy(aliasFieldTipoServizio);
			union.addGroupBy(aliasFieldServizio);
			union.addGroupBy(aliasFieldVersioneServizio);
			union.addGroupBy(aliasFieldTipoDestinatario);
			union.addGroupBy(aliasFieldDestinatario);

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
				union.addField(sommaAliasName, Function.AVG, datoParamAliasName);

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					fakeExpr.isNotNull(model.LATENZA_PORTA);
					gByExpr.isNotNull(model.LATENZA_PORTA);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
							Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, datoParamAliasName));

					unionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_PORTA,
							Function.AVG, datoParamAliasName));
					break;
				case LATENZA_SERVIZIO:
					fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
					gByExpr.isNotNull(model.LATENZA_SERVIZIO);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio", 
							Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, datoParamAliasName));

					unionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_SERVIZIO,
							Function.AVG, datoParamAliasName));
					break;

				case LATENZA_TOTALE:
				default:
					fakeExpr.isNotNull(model.LATENZA_TOTALE);
					gByExpr.isNotNull(model.LATENZA_TOTALE);
					unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
							Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, datoParamAliasName));

					unionExpr.addSelectFunctionField(new FunctionField(
							model.LATENZA_TOTALE,
							Function.AVG, datoParamAliasName));
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<ResDistribuzione>();

			if(start != null)
				union.setOffset(start);
			if(start != null)
				union.setLimit(limit);

			List<Map<String, Object>> list = dao.union(union, unionExpr, unionExprFake);

			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					r.setRisultato(((String) row.get(aliasFieldAzione)));
					
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

					if(!r.getRisultato().contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) 	
						res.add(r);
				}

			}

			return res;

		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		}
		return new ArrayList<ResDistribuzione>();
	}
	
	
	
	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PER SERVIZIO APPLICATIVO ******************
	
	@Override
	public int countAllDistribuzioneServizioApplicativo() throws ServiceException{
		try {
			StatisticType tipologia = this.distribSaSearch.getModalitaTemporale();
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
					
					if(forceIndexes!=null && forceIndexes.size()>0){
						for (Index index : forceIndexes) {
							expr.addForceIndex(index);	
						}
					}
					
					NonNegativeNumber nnn = dao.count(expr);
					int valoreLetto = nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
					count = count + valoreLetto;
				}
				if(forceFruizione){
					IExpression expr = createDistribuzioneServizioApplicativoExpression(dao, model, true, false, forceFruizione);
					
					if(forceIndexes!=null && forceIndexes.size()>0){
						for (Index index : forceIndexes) {
							expr.addForceIndex(index);	
						}
					}
					
					NonNegativeNumber nnn = dao.count(expr);
					int valoreLetto = nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
					count = count + valoreLetto;
				}
				return count;
				
			}
			else{
				// Lascio else solo se si vuole tornare indietro come soluzione
				IExpression expr = createDistribuzioneServizioApplicativoExpression(dao, model, true, false, false);
				
				if(forceIndexes!=null && forceIndexes.size()>0){
					for (Index index : forceIndexes) {
						expr.addForceIndex(index);	
					}
				}
				
				NonNegativeNumber nnn = dao.count(expr);
				return nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
			}
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(),e);
		} 

		//		return 0;
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
			StatisticType tipologia = this.distribSaSearch.getModalitaTemporale();
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
					gByExprErogazione = createDistribuzioneServizioApplicativoExpression(dao,model,false,
							forceErogazione,false);	
					gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(credenzialeFieldGroupBy);
					// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
//					gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.TIPO_DESTINATARIO);
//					gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.DESTINATARIO);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.TIPO_MITTENTE);
							gByExprErogazione.sortOrder(SortOrder.ASC).addOrder(model.MITTENTE);
						}
					}
					
					if(forceIndexes!=null && forceIndexes.size()>0){
						for (Index index : forceIndexes) {
							gByExprErogazione.addForceIndex(index);	
						}
					}
				}
				if(forceFruizione){
					gByExprFruizione = createDistribuzioneServizioApplicativoExpression(dao,model,false,
							false,forceFruizione);	
					gByExprFruizione.sortOrder(SortOrder.ASC).addOrder(credenzialeFieldGroupBy);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							gByExprFruizione.sortOrder(SortOrder.ASC).addOrder(model.TIPO_MITTENTE);
							gByExprFruizione.sortOrder(SortOrder.ASC).addOrder(model.MITTENTE);
						}
					}
					
					if(forceIndexes!=null && forceIndexes.size()>0){
						for (Index index : forceIndexes) {
							gByExprFruizione.addForceIndex(index);	
						}
					}
				}
			}
			else{
				// Lascio else solo se si vuole tornare indietro come soluzione
				gByExpr = createDistribuzioneServizioApplicativoExpression(dao,model,false,
						false, false);	
				gByExpr.sortOrder(SortOrder.ASC).addOrder(credenzialeFieldGroupBy);
				
				if(forceIndexes!=null && forceIndexes.size()>0){
					for (Index index : forceIndexes) {
						gByExpr.addForceIndex(index);	
					}
				}
			}
			
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
					unionExprErogatore.addSelectField(credenzialeFieldGroupBy, aliasFieldCredenzialeMittente);
					// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
//					unionExprErogatore.addSelectField(model.TIPO_DESTINATARIO, aliasFieldTipoSoggetto);
//					unionExprErogatore.addSelectField(model.DESTINATARIO, aliasFieldSoggetto);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							unionExprErogatore.addSelectField(model.TIPO_MITTENTE, aliasFieldTipoSoggetto);
							unionExprErogatore.addSelectField(model.MITTENTE, aliasFieldSoggetto);
						}
					}
//					unionExprErogatore.addSelectField(new ConstantField(aliasFieldRuoloSoggetto, "Erogatore", String.class),aliasFieldRuoloSoggetto);
				}
				if(forceFruizione){
					unionExprFruitore = new UnionExpression(gByExprFruizione);
					unionExprFruitore.addSelectField(credenzialeFieldGroupBy, aliasFieldCredenzialeMittente);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							unionExprFruitore.addSelectField(model.TIPO_MITTENTE, aliasFieldTipoSoggetto);
							unionExprFruitore.addSelectField(model.MITTENTE, aliasFieldSoggetto);
						}
					}
//					unionExprFruitore.addSelectField(new ConstantField(aliasFieldRuoloSoggetto, "Fruitore", String.class),	aliasFieldRuoloSoggetto);
				}
				if(unionExprErogatore==null || unionExprFruitore==null){
					// Espressione finta per usare l'ordinamento
					fakeExpr = dao.newExpression();
					unionExprFake = new UnionExpression(fakeExpr);
					unionExprFake.addSelectField(new ConstantField(aliasFieldCredenzialeMittente, 
							StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, credenzialeFieldGroupBy.getFieldType()), 
							aliasFieldCredenzialeMittente);
					if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
						if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
							unionExprFake.addSelectField(new ConstantField(aliasFieldTipoSoggetto, 
									StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, String.class), 
									aliasFieldTipoSoggetto);
							unionExprFake.addSelectField(new ConstantField(aliasFieldSoggetto, 
									StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, String.class), 
									aliasFieldSoggetto);
						}
					}
//					unionExprFake.addSelectField(new ConstantField(aliasFieldRuoloSoggetto, StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, String.class),	aliasFieldRuoloSoggetto);
				}
			}
			else{
				// Lascio else solo se si vuole tornare indietro come soluzione
				
				unionExpr = new UnionExpression(gByExpr);
				unionExpr.addSelectField(credenzialeFieldGroupBy, aliasFieldCredenzialeMittente);
				
				// Espressione finta per usare l'ordinamento
				fakeExpr = dao.newExpression();
				unionExprFake = new UnionExpression(fakeExpr);
				unionExprFake.addSelectField(new ConstantField(aliasFieldCredenzialeMittente, 
						StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE, credenzialeFieldGroupBy.getFieldType()), 
						aliasFieldCredenzialeMittente);

			}
			 			
			Union union = new Union();
			union.setUnionAll(true);
			union.addField(aliasFieldCredenzialeMittente);
			if(forceErogazione || forceFruizione){
				if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
					if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
						union.addField(aliasFieldTipoSoggetto);
						union.addField(aliasFieldSoggetto);
		//				union.addField(aliasFieldRuoloSoggetto);
					}
				}
			}
			union.addGroupBy(aliasFieldCredenzialeMittente);
			if(forceErogazione || forceFruizione){
				if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
					if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
						union.addGroupBy(aliasFieldTipoSoggetto);
						union.addGroupBy(aliasFieldSoggetto);
		//				union.addGroupBy(aliasFieldRuoloSoggetto);
					}
				}
			}

			UnionExpression [] uExpressions = new UnionExpression[2];
			int indexUE = 0;
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
				uExpressions[indexUE++] = unionExprFake;
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

				union.addOrderBy(sommaAliasName,SortOrder.DESC);
				union.addField(sommaAliasName, Function.AVG, datoParamAliasName);

				switch (tipoLatenza) {
				case LATENZA_PORTA:
					if(unionExprErogatore!=null){
						gByExprErogazione.isNotNull(model.LATENZA_PORTA);
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.LATENZA_PORTA,
								Function.AVG, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						gByExprFruizione.isNotNull(model.LATENZA_PORTA);
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.LATENZA_PORTA,
								Function.AVG, datoParamAliasName));
					}
					if(unionExpr!=null){
						gByExpr.isNotNull(model.LATENZA_PORTA);
						unionExpr.addSelectFunctionField(new FunctionField(
								model.LATENZA_PORTA,
								Function.AVG, datoParamAliasName));
					}
					if(unionExprFake!=null){
						fakeExpr.isNotNull(model.LATENZA_PORTA);
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_porta",
								Integer.valueOf(1), model.LATENZA_PORTA.getFieldType()), Function.AVG, datoParamAliasName));
					}
					break;
				case LATENZA_SERVIZIO:
					if(unionExprErogatore!=null){
						gByExprErogazione.isNotNull(model.LATENZA_SERVIZIO);
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.LATENZA_SERVIZIO,
								Function.AVG, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						gByExprFruizione.isNotNull(model.LATENZA_SERVIZIO);
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.LATENZA_SERVIZIO,
								Function.AVG, datoParamAliasName));
					}
					if(unionExpr!=null){
						gByExpr.isNotNull(model.LATENZA_SERVIZIO);
						unionExpr.addSelectFunctionField(new FunctionField(
								model.LATENZA_SERVIZIO,
								Function.AVG, datoParamAliasName));
					}
					if(unionExprFake!=null){
						fakeExpr.isNotNull(model.LATENZA_SERVIZIO);
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_servizio",
								Integer.valueOf(1), model.LATENZA_SERVIZIO.getFieldType()), Function.AVG, datoParamAliasName));
					}
					break;

				case LATENZA_TOTALE:
				default:
					if(unionExprErogatore!=null){
						gByExprErogazione.isNotNull(model.LATENZA_TOTALE);
						unionExprErogatore.addSelectFunctionField(new FunctionField(
								model.LATENZA_TOTALE,
								Function.AVG, datoParamAliasName));
					}
					if(unionExprFruitore!=null){
						gByExprFruizione.isNotNull(model.LATENZA_TOTALE);
						unionExprFruitore.addSelectFunctionField(new FunctionField(
								model.LATENZA_TOTALE,
								Function.AVG, datoParamAliasName));
					}
					if(unionExpr!=null){
						gByExpr.isNotNull(model.LATENZA_TOTALE);
						unionExpr.addSelectFunctionField(new FunctionField(
								model.LATENZA_TOTALE,
								Function.AVG, datoParamAliasName));
					}
					if(unionExprFake!=null){
						fakeExpr.isNotNull(model.LATENZA_TOTALE);
						unionExprFake.addSelectFunctionField(new FunctionField(new ConstantField("latenza_totale",
								Integer.valueOf(1), model.LATENZA_TOTALE.getFieldType()), Function.AVG, datoParamAliasName));
					}
					break;
				}
				break;
			}
			}

			ArrayList<ResDistribuzione> res = new ArrayList<ResDistribuzione>();

			if(start != null)
				union.setOffset(start);
			if(start != null)
				union.setLimit(limit);

			List<Map<String, Object>> list = dao.union(union, uExpressions);

			if (list != null) {

				// List<Object[]> list = q.getResultList();
				for (Map<String, Object> row : list) {

					ResDistribuzione r = new ResDistribuzione();
					String risultato = (String) row.get(aliasFieldCredenzialeMittente); 
					
					if(!risultato.contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE))
						risultato = this.getLabelCredenzialeFieldGroupBy(risultato, this.distribSaSearch);
					
					r.setRisultato(risultato);
					
					if(forceErogazione || forceFruizione){
						if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
							if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
								r.getParentMap().put("0",((String) row.get(aliasFieldTipoSoggetto)) + "/" + ((String) row.get(aliasFieldSoggetto)));
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

					if(!r.getRisultato().contains(StatisticheGiornaliereService.FALSA_UNION_DEFAULT_VALUE)) 	
						res.add(r);
				}

			}

			return res;
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return new ArrayList<ResDistribuzione>();
	}

	private IExpression createDistribuzioneServizioApplicativoExpression(IServiceSearchWithoutId<?> dao, StatisticaModel model, boolean isCount,
			boolean forceErogazione, boolean forceFruizione) throws ServiceException{
		IExpression expr = null;

		StatisticheGiornaliereService.log.debug("creo Expression per distribuzione sa!");

		List<Soggetto> listaSoggettiGestione = this.distribSaSearch.getSoggettiGestione();

		try {
			EsitoUtils esitoUtils = new EsitoUtils(StatisticheGiornaliereService.log, this.distribSaSearch.getProtocollo());
			
			expr = dao.newExpression();
			// Data
			expr.between(model.DATA, this.distribSaSearch.getDataInizio(),	this.distribSaSearch.getDataFine());

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
				boolean soggetto = listaSoggettiGestione.size() > 0;
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

					if (listaSoggettiGestione.size() > 0) {
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

					if (listaSoggettiGestione.size() > 0) {
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
				if (listaSoggettiGestione.size() > 0) {
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
				if (StringUtils.isNotBlank(this.distribSaSearch
						.getNomeMittente())) {
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
				if (listaSoggettiGestione.size() > 0) {
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

			
			this.impostaGroupByFiltroDatiMittente(expr, this.distribSaSearch, model, isCount); 
			
			this.impostaFiltroGruppo(expr, this.distribSaSearch, model, isCount);

			// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
			// Poichè gli applicativi sono identificati univocamente insieme anche al soggetto proprietario, si aggiunge il soggetto nella group by
			if(StringUtils.isNotEmpty(this.distribSaSearch.getRiconoscimento())) {
				if(this.distribSaSearch.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					expr.notEquals(model.TIPO_MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					expr.notEquals(model.MITTENTE, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					expr.addGroupBy(model.TIPO_MITTENTE);
					expr.addGroupBy(model.MITTENTE);
				}
			}
			
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}

		return expr;
	}
	
	
	
	
	
	
	
	
	
	
	// ********** DISTRIBUZIONE PERSONALIZZATA ******************
	
	@Override
	public int countAllDistribuzionePersonalizzata() throws ServiceException{
		try {
			NonNegativeNumber nnn = executeDistribuzionePersonalizzataCount();

			return nnn != null ? Long.valueOf(nnn.longValue()).intValue() : 0;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		}
		//		return new ArrayList<ResDistribuzione>();
	}

	@Override
	public List<ResDistribuzione> findAllDistribuzionePersonalizzata(int start,	int limit) throws ServiceException{
		try {
			List<ResDistribuzione> res = executeDistribuzionePersonalizzataSearch(false, true, start, limit);
			return res;
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		}
		//		return new ArrayList<ResDistribuzione>();
	}

	private List<ResDistribuzione> executeDistribuzionePersonalizzataSearch(boolean isCount, boolean isPaginated, int offset, int limit) throws ServiceException{

		StatisticaModel model = null;
		StatisticaContenutiModel modelContenuti = null;
		IServiceSearchWithoutId<?> dao = null;

		StatisticType tipologia = this.statistichePersonalizzateSearch.getModalitaTemporale(); 
		try {
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

			IExpression gByExpr = createDistribuzionePersonalizzataExpression(dao,
					model, modelContenuti, isCount);

			gByExpr.sortOrder(SortOrder.ASC).addOrder(modelContenuti.RISORSA_VALORE);

			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					gByExpr.addForceIndex(index);	
				}
			}
			
			UnionExpression unionExpr = new UnionExpression(gByExpr);
			unionExpr.addSelectField(modelContenuti.RISORSA_VALORE, "nome_risorsa");

			// Espressione finta per usare l'ordinamento
			IExpression fakeExpr = dao.newExpression();
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

			List<Map<String, Object>> list = dao.union(union, unionExpr, unionExprFake);
			List<ResDistribuzione> res = new ArrayList<ResDistribuzione>();
			//			Hashtable<String, ResDistribuzione> map = new Hashtable<String, ResDistribuzione>();
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
			//			List<String> order = new ArrayList<String>();
			//			while (enKeys.hasMoreElements()) {
			//				String key = (String) enKeys.nextElement();
			//				order.add(key);
			//			}
			//			Collections.sort(order,Collections.reverseOrder());
			//			// risultato ordinato
			//			List<ResDistribuzione> res = new ArrayList<ResDistribuzione>();
			//			for (String key : order) {
			//				res.add(map.get(key));
			//			}

			return res;

		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}

		return new ArrayList<ResDistribuzione>();

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

		if(forceIndexes!=null && forceIndexes.size()>0){
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

		}  catch (ExpressionNotImplementedException | NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (UtilsException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			//			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
		IServiceSearchWithoutId<?> dao = null;

		StatisticType tipologia = this.statistichePersonalizzateSearch.getModalitaTemporale(); 
		try {
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

			IExpression expr = createAndamentoTemporalePersonalizzatoExpression(dao, model, modelContenuti, isCount);

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
					listaFunzioni.add(new  FunctionField(modelContenuti.LATENZA_PORTA, Function.AVG, "somma_latenza_porta"));
					isLatenza_porta = true;
					break;
				case LATENZA_SERVIZIO:
					expr.isNotNull(modelContenuti.LATENZA_SERVIZIO);
					listaFunzioni.add(new FunctionField(modelContenuti.LATENZA_SERVIZIO, Function.AVG, "somma_latenza_servizio"));
					isLatenza_servizio = true;
					break;

				case LATENZA_TOTALE:
				default:
					expr.isNotNull(modelContenuti.LATENZA_TOTALE);
					listaFunzioni.add(new  FunctionField(modelContenuti.LATENZA_TOTALE, 	Function.AVG, "somma_latenza_totale"));
					isLatenza_totale = true;
					break;
				}
			}
			//			}
			}

			if(forceIndexes!=null && forceIndexes.size()>0){
				for (Index index : forceIndexes) {
					expr.addForceIndex(index);
				}
			}
			
			List<Map<String, Object>> list = null;

			if (!isPaginated) {
				list = dao.groupBy(expr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
			} else {
				IPaginatedExpression pagExpr = dao.toPaginatedExpression(expr);
				pagExpr.offset(offset).limit(limit);
				list = dao.groupBy(pagExpr, listaFunzioni.toArray(new FunctionField[listaFunzioni.size()]));
			}

			// supporto
			TreeMap<String, List<Res>> mapRisultati = new TreeMap<String, List<Res>>();
			TreeMap<String, List<Date>> mapDateUsate = new TreeMap<String, List<Date>>();
			List<Date> listaDateUtilizzate = new ArrayList<Date>();

			// Scorro i risultati per generare gli elementi del grafico.
			for (Map<String, Object> row : list) {
				String valoreRisorsa = (String) row.get(JDBCUtilities.getAlias(modelContenuti.RISORSA_VALORE));

				List<Res> res = null;
				List<Date> datePerRes = null;

				boolean nuovaEntry =true;
				if(mapRisultati.containsKey(valoreRisorsa)){
					nuovaEntry = false;
					res = mapRisultati.get(valoreRisorsa);
					datePerRes = mapDateUsate.get(valoreRisorsa);
				}

				if(nuovaEntry){
					res = new ArrayList<Res>();
					datePerRes = new ArrayList<Date>();
				}


				Date data = (Date) row.get(JDBCUtilities.getAlias(model.DATA));

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

			// generazione entries mancanti
			for (String val : mapRisultati.keySet()) {
				List<Res> entries = mapRisultati.get(val);
				List<Date> dateVal = mapDateUsate.get(val);

				// ordino la lista delle date utilizzate
				Collections.sort(listaDateUtilizzate);
				// scorro tutte le date, trovate dalla query

				List<Res> listaResOrdinata = new ArrayList<Res>();
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (UtilsException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug("Nessuna statistica trovata per la ricerca corrente.");
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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

		}  catch (ExpressionNotImplementedException | NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
				boolean soggetto = listaSoggettiGestione.size() > 0;
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

					if (listaSoggettiGestione.size() > 0) {
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

					if (listaSoggettiGestione.size() > 0) {
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
				if (listaSoggettiGestione.size() > 0) {
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
				if (StringUtils.isNotBlank(this.statistichePersonalizzateSearch.getNomeMittente())) {
					expr.and().equals(model.TIPO_MITTENTE,this.statistichePersonalizzateSearch.getTipoMittente());
					expr.and().equals(model.MITTENTE,this.statistichePersonalizzateSearch.getNomeMittente());
				}

			} else {
				// FRUIZIONE
				expr.and().equals(model.TIPO_PORTA, "delegata");

				// il mittente e' l'utente loggato (sempre presente se non
				// sn admin)
				if (listaSoggettiGestione.size() > 0) {
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (CoreException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		}  

		return expr;
	}


	@Override
	public List<String> getValoriRisorse() throws ServiceException{
		StatisticheGiornaliereService.log.debug("Leggo i valori delle risorse per la statistica: " + this.statistichePersonalizzateSearch.getNomeStatisticaPersonalizzata()); 

		List<String> valori = new ArrayList<String>();
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

		try{
			IExpression expr = parseStatistichePersonalizzateFilter(dao, model, modelContenuti);
			expr.and();
			this.impostaFiltroDatiMittente(expr, this.statistichePersonalizzateSearch, model, false);
			
			this.impostaFiltroGruppo(expr, this.statistichePersonalizzateSearch, model, false);

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
							StatisticheGiornaliereService.log.error(e.getMessage(), e);
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
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw e;
		} catch (UtilsException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
			throw new ServiceException(e);
		} catch (NotFoundException e) {
			StatisticheGiornaliereService.log.debug(e.getMessage(), e);
		}   
		return valori;
	}

	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithoutId<?> dao, StatisticaModel model,	IExpression expr, String protocollo, TipologiaRicerca tipologiaRicercaParam) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsService.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
			}
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error("Si e' verificato un errore durante il calcolo dei tipi servizio compatibili con il protocollo scelto: "+ e.getMessage(), e);
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
					expressionTipoSoggettiMittenteCompatibili.and(DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_MITTENTE, protocollo));
				}
				else {
					expressionTipoSoggettiMittenteCompatibili = DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_MITTENTE, protocollo);
				}
			}
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto mittente compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiMittenteCompatibili != null)
			expr.and(expressionTipoSoggettiMittenteCompatibili);


		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiDestinatarioCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_DESTINATARIO, protocollo);
			}
		} catch (Exception e) {
			StatisticheGiornaliereService.log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}


	private IField getCredenzialeField(org.openspcoop2.core.transazioni.CredenzialeMittente credenzialeMittente, StatisticaModel model) {
		IField fieldCredenziale = null;
		String credenzialeTipo = credenzialeMittente.getTipo();
		if(credenzialeTipo.startsWith(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_CREDENZIALE_TRASPORTO_PREFIX)) {
			fieldCredenziale = model.TRASPORTO_MITTENTE;
		} else {
			org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(credenzialeTipo);
			
			switch (tcm) {
			case token_clientId:
				fieldCredenziale = model.TOKEN_CLIENT_ID;
				break;
			case token_eMail:
				fieldCredenziale = model.TOKEN_MAIL;
				break;
			case token_issuer:
				fieldCredenziale = model.TOKEN_ISSUER;
				break;
			case token_subject:
				fieldCredenziale = model.TOKEN_SUBJECT;
				break;
			case token_username:
				fieldCredenziale = model.TOKEN_USERNAME;
				break;
				
			case client_address:
				return model.CLIENT_ADDRESS;
				
			case gruppi:
				return model.GRUPPI;
				
			case eventi:
			case trasporto:
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
			
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(searchForm.getTokenClaim());
				CredenzialeSearchToken searchToken = new CredenzialeSearchToken(tcm);
				pagExpr = searchToken.createExpression(credenzialeMittentiService, searchForm.getValoreRiconoscimento(), ricercaEsatta, caseSensitive);
			}
			
			findAll = credenzialeMittentiService.findAll(pagExpr);
		}catch(ServiceException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			StatisticheGiornaliereService.log.error(e.getMessage(), e);
		} catch (UtilsException e) {
			StatisticheGiornaliereService	.log.error(e.getMessage(), e);
		}
		return findAll;
	}
	
	private void impostaFiltroDatiMittente(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount)
			throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				if (StringUtils.isNotBlank(searchForm.getServizioApplicativo())) {
					// sb.append("AND t.servizioApplicativo = :servizio_applicativo ");
					filter.and().equals(model.SERVIZIO_APPLICATIVO,	searchForm.getServizioApplicativo());
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
	
	private void impostaGroupByFiltroDatiMittente(IExpression filter, BaseSearchForm searchForm, StatisticaModel model, boolean isCount)
			throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				filter.notEquals(model.SERVIZIO_APPLICATIVO, Costanti.INFORMAZIONE_NON_DISPONIBILE);
				filter.addGroupBy(model.SERVIZIO_APPLICATIVO);
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
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(searchForm.getTokenClaim());
				
				switch (tcm) {
				case token_clientId:
					filter.notEquals(model.TOKEN_CLIENT_ID, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_CLIENT_ID);
					break;
				case token_eMail:
					filter.notEquals(model.TOKEN_MAIL, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_MAIL);
					break;
				case token_issuer:
					filter.notEquals(model.TOKEN_ISSUER, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_ISSUER);
					break;
				case token_subject:
					filter.notEquals(model.TOKEN_SUBJECT, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_SUBJECT);
					break;
				case token_username:
					filter.notEquals(model.TOKEN_USERNAME, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.TOKEN_USERNAME);
					break;
					
				case client_address:
					filter.notEquals(model.CLIENT_ADDRESS, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.CLIENT_ADDRESS);
					break;
					
				case gruppi:
					filter.notEquals(model.GRUPPI, Costanti.INFORMAZIONE_NON_DISPONIBILE);
					filter.addGroupBy(model.GRUPPI);
					break;
										
				case eventi:
				case trasporto:
					// caso impossibile
					break; 
				}
			}
		}
	}
	
	
	private IField getCredenzialeFieldGroupBy(BaseSearchForm searchForm, StatisticaModel model) {
		// credenziali mittente
		if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
			if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
				return model.SERVIZIO_APPLICATIVO;
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
				org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(searchForm.getTokenClaim());
				
				switch (tcm) {
				case token_clientId:
					return model.TOKEN_CLIENT_ID;
				case token_eMail:
					return model.TOKEN_MAIL;
				case token_issuer:
					return model.TOKEN_ISSUER;
				case token_subject:
					return model.TOKEN_SUBJECT;
				case token_username:
					return model.TOKEN_USERNAME;
					
				case client_address:
					return model.CLIENT_ADDRESS;
					
				case gruppi:
					return model.GRUPPI;
					
				case eventi:
				case trasporto:
					// caso impossibile
					break; 
				}
			}
		}
		
		return null;
		 
	}
	
	
	private String getLabelCredenzialeFieldGroupBy(String risultato, BaseSearchForm searchForm) {
		try {
			// credenziali mittente
			if(StringUtils.isNotEmpty(searchForm.getRiconoscimento())) {
				if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					return risultato;
				} 
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_SOGGETTO)) {
					// Caso non previsto nella distribuzione del servizio applicativo dove viene usato questo metodo
					return risultato;
				}  
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
					if(StringUtils.isNotEmpty(risultato)) {
						CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
						return credenzialeMittente.getCredenziale();
					}
				} 
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
					if(StringUtils.isNotEmpty(risultato)) {
						CredenzialeMittente credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
						
						String credenziale = credenzialeMittente.getCredenziale();
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
						return bf.toString(); 
					}
				} 
				else if(searchForm.getRiconoscimento().equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO)) {
					org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tcm = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.valueOf(searchForm.getTokenClaim());
					CredenzialeMittente credenzialeMittente  = null;
					if(StringUtils.isNotEmpty(risultato)) {
					switch (tcm) {
					case token_clientId:
							credenzialeMittente  = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
							return credenzialeMittente.getCredenziale();
					case token_eMail:
							credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
							return credenzialeMittente.getCredenziale();
					case token_issuer:
							credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
							return credenzialeMittente.getCredenziale();
					case token_subject:
							credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
							return credenzialeMittente.getCredenziale();
					case token_username:
							credenzialeMittente = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(Long.parseLong(risultato));
							return credenzialeMittente.getCredenziale();
					case trasporto:
					default:
						// caso impossibile
						break; 
					}
				}
				}
			}
		} catch (NumberFormatException | ServiceException | NotFoundException | MultipleResultException	| NotImplementedException e) {
			StatisticheGiornaliereService	.log.error(e.getMessage(), e);
		}
		
		return risultato;
		 
	}
	
	private void addListaCredenzialiMittente(IExpression filter, List<CredenzialeMittente> listaCredenzialiMittente, StatisticaModel model) throws ExpressionNotImplementedException, ExpressionException {
		// se non ho trovato credenziali che corrispondono a quelle inserite allora restituisco un elenco di transazioni vuoto forzando l'id transazione
		if(listaCredenzialiMittente.size() ==0) {
			Calendar c = Calendar.getInstance();
			Date d = new Date();
			c.setTime(d);
			c.set(Calendar.YEAR, 2100);
			filter.and().equals(model.DATA, c.getTime());
		} else {
			org.openspcoop2.core.transazioni.CredenzialeMittente credenzialeMittente = listaCredenzialiMittente.get(0);
			IField fieldCredenziale = getCredenzialeField(credenzialeMittente, model);
			
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
}
