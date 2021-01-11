/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.statistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IServiceManager;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoService;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoServiceSearch;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.IServiceSearchWithoutId;
import org.openspcoop2.generic_project.dao.IServiceWithoutId;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.Index;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.monitor.engine.condition.FilterImpl;
import org.openspcoop2.monitor.engine.config.BasicServiceLibrary;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.StatisticsServiceLibrary;
import org.openspcoop2.monitor.engine.config.StatisticsServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;
import org.openspcoop2.monitor.engine.config.statistiche.dao.IConfigurazioneStatisticaService;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.plugins.IStatisticProcessing;
import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.slf4j.Logger;



/**
 * AbstractStatistiche
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractStatistiche {

	protected Logger logger = LoggerWrapperFactory.getLogger(AbstractStatistiche.class);
	protected boolean debug = false;
	protected boolean generazioneStatisticheCustom = false;
	protected boolean analisiTransazioniCustom = false;
	protected IServiceManager statisticheSM = null;
	protected IServiceWithoutId<?> statisticaServiceDAO = null;
	protected IServiceSearchWithoutId<?> statisticaServiceSearchDAO = null;
	protected StatisticaModel model = null;
	private IStatisticaInfoServiceSearch statisticaInfoSearchDAO = null;
	private IStatisticaInfoService statisticaInfoDAO = null;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM = null;
	private ITransazioneServiceSearch transazioneSearchDAO = null;
	private org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM = null;
	private org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM = null;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM = null;
	private org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM;
	@SuppressWarnings("unused")
	private IConfigurazioneStatisticaService confStatisticaDAO =null;
	private TipiDatabase databaseType;
	private StatisticsForceIndexConfig forceIndexConfig;
	

	private boolean initialized = false;
	
	AbstractStatistiche(){
		
	}
	public AbstractStatistiche(Logger logger,boolean debug, boolean generazioneStatisticheCustom, boolean analisiTransazioniCustom,
			StatisticsForceIndexConfig forceIndexConfig,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM){
		if(logger!=null){
			this.logger = logger;
		}
		this.debug = debug;
		this.generazioneStatisticheCustom = generazioneStatisticheCustom;
		this.analisiTransazioniCustom = analisiTransazioniCustom;
		
		try {
			if(statisticheSM==null){
				throw new ServiceException("ServiceManager ["+org.openspcoop2.core.statistiche.dao.IServiceManager.class.getName()+"] non inizializzato");
			}
			if(transazioniSM==null){
				throw new ServiceException("ServiceManager ["+org.openspcoop2.core.transazioni.dao.IServiceManager.class.getName()+"] non inizializzato");
			}
			
			this.statisticheSM = statisticheSM;
			this.statisticaInfoSearchDAO = this.statisticheSM.getStatisticaInfoServiceSearch();
			this.statisticaInfoDAO = this.statisticheSM.getStatisticaInfoService();

			this.databaseType = DAOFactoryProperties.getInstance(this.logger).getTipoDatabaseEnum(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
			
			this.transazioniSM = transazioniSM;
			this.transazioneSearchDAO = this.transazioniSM.getTransazioneServiceSearch();

			if(this.generazioneStatisticheCustom){
				
				if(pluginsStatisticheSM==null){
					throw new ServiceException("ServiceManager ["+org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager.class.getName()+"] non inizializzato");
				}
				this.pluginsStatisticheSM = pluginsStatisticheSM;
				this.confStatisticaDAO  = this. pluginsStatisticheSM.getConfigurazioneStatisticaService();
				
				if(utilsSM==null){
					throw new ServiceException("ServiceManager ["+org.openspcoop2.core.commons.search.dao.IServiceManager.class.getName()+"] non inizializzato");
				}
				this.utilsSM = utilsSM;
				
				if(pluginsBaseSM==null){
					throw new ServiceException("ServiceManager ["+org.openspcoop2.monitor.engine.config.base.dao.IServiceManager.class.getName()+"] non inizializzato");
				}
				this.pluginsBaseSM = pluginsBaseSM;
				
				if(this.analisiTransazioniCustom){
					if(pluginsTransazioniSM==null){
						throw new ServiceException("ServiceManager ["+org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager.class.getName()+"] non inizializzato");
					}
					this.pluginsTransazioniSM = pluginsTransazioniSM;
				}
			}
			
			this.forceIndexConfig = forceIndexConfig;
			
			this.initialized = true;
			
		} catch (ServiceException e) {
			this.logger.error(e.getMessage(),e);
		} catch (Exception e) {
			this.logger.error(e.getMessage(),e);
		}
	}

	public boolean isDebug() {
		return this.debug;
	}
	
	protected TipiDatabase getDatabaseType() {
		return this.databaseType;
	}
	
	public void printDate(Date d){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		printCalendar(c);
	}

	public void printCalendar(Calendar c){
		StringBuilder bf = new StringBuilder();
		bf.append("DATA ["+c.getTime().toString()+"]");
		bf.append(" DATA in millisecondi: "+c.getTime().getTime());
		bf.append(" YEAR:"+c.get(Calendar.YEAR));
		bf.append(" MONTH:"+(c.get(Calendar.MONTH)+1));
		bf.append(" DAY_OF_MONTH:"+c.get(Calendar.DAY_OF_MONTH));
		bf.append(" HOUR_OF_DAY:"+c.get(Calendar.HOUR_OF_DAY));
		bf.append(" MINUTE:"+c.get(Calendar.MINUTE));
		bf.append(" SECOND:"+c.get(Calendar.SECOND));
		bf.append(" MILLISECOND:"+c.get(Calendar.MILLISECOND));
		this.logger.debug(bf.toString());
	}

	// METODI ASTRATTI
	public abstract Date truncDate(Date date,boolean print);

	public abstract Date incrementDate(Date date,boolean print);

	public abstract Date decrementDate(Date date,boolean print);

	public Date decrementDate1Millisecond(Date date){
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		cTmp.add(Calendar.MILLISECOND, -1);
		return cTmp.getTime();
	}

	public abstract String getIntervalloStatistica(Date date,boolean print);

	private String getIntervalloStatistica(Date date){
		Date d = this.incrementDate(date, false);
		return this.getIntervalloStatistica(d, false);
	}

	public abstract TipoIntervalloStatistico getTipoStatistiche();

	public abstract void callStatisticPluginMethod(IStatisticProcessing statProcessing, StatisticBean stat) throws StatisticException;

	public abstract String getStatisticPluginMethodName() throws StatisticException;


	public void generaStatistiche( boolean gestioneUltimoIntervallo) throws Exception{

		if(this.initialized==false){
			throw new ServiceException("Inizializzazione fallita (verificare errori precedenti)");
		}

		try{
			if(this.debug)
				this.logger.debug("********************************************* "+this.getTipoStatistiche()+" *******************************************************");

			// Ottengo data in cui e' stato fatto girare l'ultima volta la procedura di popolamento 
			// Tale data viene troncata in funzione della statistica che sto eseguendo 
			// (La prima insert potrebbe contenere informazioni maggiori di quelle che servono: es. in statistiche giornaliere ci potrebbero essere ora,minuti e secondi)
			Date dataUltimaGenerazioneStatistiche = StatisticsInfoUtils.readDataUltimaGenerazioneStatistiche(this.statisticaInfoSearchDAO, this.getTipoStatistiche(), this.logger);
			dataUltimaGenerazioneStatistiche = truncDate(dataUltimaGenerazioneStatistiche, false);
			// Decremento la data di 1 millisecondo in modo da generare poi il SQL nella forma 
			// statistiche per transazioni emessi in data > dataUltimaGenerazioneStatistiche
			dataUltimaGenerazioneStatistiche = this.decrementDate1Millisecond(dataUltimaGenerazioneStatistiche); 


			// Now
			// Tale data viene troncata in funzione della statistica che sto eseguendo 
			Date now = new Date();
			now = truncDate(now,false);
			now = this.decrementDate1Millisecond(now);
			Date nowMenoUno = decrementDate(now, false);


			// Genero statistiche per transazioni emessi in data > dataUltimaGenerazioneStatistiche AND data <= now
			while(dataUltimaGenerazioneStatistiche.compareTo(nowMenoUno) <= 0){

				//if(dataUltimaGenerazioneStatistiche.compareTo(nowMenoUno) == 0){
				// L'IF sopra non lo devo fare, se spengo la macchina, e la riaccendo dopo due ore, l'eliminazione delle statistiche gia' generate riguarda nowMenoDue
				// Se ho la stessa data devo eliminare l'intervallo, posso gia' averlo generato
				// Eliminazione
				if(this.debug)
					this.logger.debug("----------- eliminazione (DataUguale) ------------");
				this.deleteStatistiche( TipoPdD.DELEGATA, dataUltimaGenerazioneStatistiche);
				if(this.debug)
					this.logger.debug("------------ eliminazione (DataUguale) -----------");
				this.deleteStatistiche( TipoPdD.APPLICATIVA, dataUltimaGenerazioneStatistiche );
				//}

				//delegata
				if(this.debug)
					this.logger.debug("-----------------------");
				this.generaStatistiche( TipoPdD.DELEGATA, dataUltimaGenerazioneStatistiche );

				// applicativa
				if(this.debug)
					this.logger.debug("-----------------------");
				this.generaStatistiche( TipoPdD.APPLICATIVA, dataUltimaGenerazioneStatistiche );

				// increment
				dataUltimaGenerazioneStatistiche = incrementDate(dataUltimaGenerazioneStatistiche, false);
				if(this.debug)
					this.logger.debug("-----------------------");

				// Salvo nuova data di ultima generazione statistiche (entro nel prossimo intervallo)
				Date next = this.truncDate(this.incrementDate(dataUltimaGenerazioneStatistiche, false),false);
				if(this.debug)
					this.logger.debug("Save data ultima generazione statistiche: "+next.toString());
				StatisticsInfoUtils.updateDataUltimaGenerazioneStatistiche(this.statisticaInfoSearchDAO, this.statisticaInfoDAO, 
						this.getTipoStatistiche(), this.logger, next);
				/*Calendar cTmp = Calendar.getInstance();
				cTmp.setTime(dataUltimaGenerazioneStatistiche);
				cTmp.add(Calendar.MILLISECOND, 1);
				if(this.debug)
					this.log.debug("Save data ultima generazione statistiche: "+cTmp.getTime().toString());
				this.log.debug("SAVE: ");
				this.printDate(cTmp.getTime());
				this.updateDataUltimaGenerazioneStatistiche(con, cTmp.getTime());*/
			}


			// Genero statistiche parziali della data di oggi
			if(gestioneUltimoIntervallo){

				Date dataUltimoIntervallo = dataUltimaGenerazioneStatistiche;

				// Eliminazione
				if(this.debug)
					this.logger.debug("----------- eliminazione ------------");
				this.deleteStatistiche(TipoPdD.DELEGATA, dataUltimoIntervallo);
				if(this.debug)
					this.logger.debug("------------ eliminazione -----------");
				this.deleteStatistiche(TipoPdD.APPLICATIVA, dataUltimoIntervallo );

				// Rigenerazione statistiche
				if(this.debug)
					this.logger.debug("---------- Rigenerazione statistiche ultimo intervallo -------------");
				this.generaStatistiche(TipoPdD.DELEGATA, dataUltimoIntervallo );
				if(this.debug)
					this.logger.debug("---------- Rigenerazione statistiche ultimo intervallo -------------");
				this.generaStatistiche(TipoPdD.APPLICATIVA, dataUltimoIntervallo );
				if(this.debug)
					this.logger.debug("---------- Rigenerazione statistiche ultimo intervallo -------------");

			}

		}catch(Exception e){
			this.logger.error(e.getMessage(), e);
			throw e;
		}
	}


	
	
	
	// ---- GENERAZIONE STATISTICHE BASE ----
	
	private void generaStatistiche(  TipoPdD tipoPdD, Date data) {

		if(this.debug){
			this.logger.debug("Generazione statistiche ["+this.getTipoStatistiche()+"] ["+tipoPdD+"]("+this.getIntervalloStatistica(data)+") ...");
		}
		try{
			IExpression expr = this.transazioneSearchDAO.newExpression();

			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.transazioneSearchDAO).getFieldConverter(); 
			
			// ** Select field **
			List<FunctionField> selectList = new ArrayList<FunctionField>();
			StatisticsUtils.addSelectFieldCountTransaction(selectList);
			StatisticsUtils.addSelectFieldSizeTransaction(tipoPdD, selectList);
			
			// ** Where **
			// Creo intervallo
			Date dateNext = incrementDate(data, false);
			StatisticsUtils.setExpression(expr, data, dateNext, tipoPdD, false, null, fieldConverter);

			if(this.debug){
				this.logger.debug("Genero statistiche ["+this.getTipoStatistiche()+"] Intervallo date: ["+data.toString()+" - "+dateNext.toString()+"]");
				this.logger.debug("Valori query (ms) tr.data_ingresso_richiesta>["+data.getTime()+"] AND tr.data_ingresso_richiesta<=["+dateNext.getTime()+"]");
			}

			if(this.forceIndexConfig!=null){
				if(this.forceIndexConfig.getTransazioniForceIndexGroupBy_numero_dimesione()!=null){
					List<Index> listForceIndexes = this.forceIndexConfig.getTransazioniForceIndexGroupBy_numero_dimesione();
					if(listForceIndexes.size()>0){
						for (Index index : listForceIndexes) {
							expr.addForceIndex(index);
						}
					}
				}
			}
			
			List<Map<String, Object>> list = this.transazioneSearchDAO.groupBy(expr, selectList.toArray(new FunctionField[1]));

			for (Map<String, Object> row : list) {
				
				// Leggo informazioni di base dalla statistica
				StatisticBean stat = this.readStatisticBean(data, row);
				
				// Aggiungo informazioni sul numero di transazioni e size delle transazioni
				StatisticsUtils.updateStatisticBeanCountTransactionInfo(stat, row);
				StatisticsUtils.updateStatisticBeanSizeTransactionInfo(stat, row);

				// Aggiungo informazioni che richiedono ulteriori condizioni di where (es. date not null) 
				this.addLatenze(data, dateNext, tipoPdD, stat, fieldConverter);
				
				// Inserisco statistica
				insertStatistica(stat);

				if(this.generazioneStatisticheCustom){
					createCustomStatistic(stat);
				}
			}


		} catch (ServiceException e){
			this.logger.error(e.getMessage(),e);
		} catch (NotImplementedException e) {
			this.logger.error(e.getMessage(),e);
		} catch (ExpressionNotImplementedException e) {
			this.logger.error(e.getMessage(),e);
		} catch (ExpressionException e) {
			this.logger.error(e.getMessage(),e);
		} catch (NotFoundException e) {
			if(this.debug){
				this.logger.debug(e.getMessage(),e);
			}
		}catch (Exception e) {
			this.logger.error(e.getMessage(),e);
		}  

		if(this.debug){
			this.logger.debug("Generazione statistiche ["+this.getTipoStatistiche()+"] ["+tipoPdD+"]("+this.getIntervalloStatistica(data)+") terminata");
		}
	}
	
	private StatisticBean readStatisticBean(Date data,Map<String, Object> row){
		StatisticBean stat = new StatisticBean();

		stat.setDateIntervalLeft(data);
		stat.setDateIntervalRight(this.incrementDate(data, false));
		Date next = this.truncDate(this.incrementDate(data, false),false);
		stat.setData(next);
		if(this.debug)
			this.logger.debug("Salvo statistica con data ["+next+"]");
	
		return StatisticsUtils.readStatisticBean(stat, row);
	}
	
	private void addLatenze(Date data, Date dateNext, TipoPdD tipoPdD, StatisticBean stat, ISQLFieldConverter fieldConverter) throws Exception{
		
		IExpression exprDateNotNull = this.transazioneSearchDAO.newExpression();
		StatisticsUtils.setExpression(exprDateNotNull, data, dateNext, tipoPdD, true, stat, fieldConverter);
		
		List<FunctionField> selectListDateNotNull = new ArrayList<FunctionField>();
		
		StatisticsUtils.addSelectFieldLatencyTransaction(tipoPdD, fieldConverter, selectListDateNotNull);
		
		if(this.debug){
			this.logger.debug("Leggo ulteriormente statistiche con campi data not null ["+this.getTipoStatistiche()+"] Intervallo date: ["+data.toString()+" - "+dateNext.toString()+"]");
			this.logger.debug("Valori query (ms) tr.data_ingresso_richiesta>["+data.getTime()+"] AND tr.data_ingresso_richiesta<=["+dateNext.getTime()+"]");
		}

		try{
			if(this.forceIndexConfig!=null){
				if(this.forceIndexConfig.getTransazioniForceIndexGroupBy_latenze()!=null){
					List<Index> listForceIndexes = this.forceIndexConfig.getTransazioniForceIndexGroupBy_latenze();
					if(listForceIndexes.size()>0){
						for (Index index : listForceIndexes) {
							exprDateNotNull.addForceIndex(index);
						}
					}
				}
			}
			
			List<Map<String, Object>> listDateNotNull = this.transazioneSearchDAO.groupBy(exprDateNotNull, selectListDateNotNull.toArray(new FunctionField[1]));
			
			if(listDateNotNull.size()>1){
				throw new MultipleResultException("Attesa un solo gruppo, ritornati "+listDateNotNull.size()+" gruppi");
			}
			Map<String, Object> row = listDateNotNull.get(0);
			
			StatisticsUtils.updateStatisticsBeanLatencyTransactionInfo(stat, row);
			
		}catch(NotFoundException notFound){
			// possono non esistere
			if(this.debug){
				this.logger.debug(notFound.getMessage(),notFound);
			}
			stat.setLatenzaPorta(Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE);
			stat.setLatenzaServizio(Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE);
			stat.setLatenzaTotale(Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE);
		}
	}
	
	
	
	
	
	
	// ---- RICERCA STATISTICHE PERSONALIZZATE ----
	
	private void createCustomStatistic(StatisticBean stat)   {

		//IExpression expr;
		try {
//			expr = this.confStatisticaDAO.newExpression();
//			expr.and();
//
//			expr.equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO, stat.getServizio());
//
//			if ("*".equals(stat.getAzione())) {
//				expr.and().equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE, "*");
//			} else {
//				IExpression orExpr = this.confStatisticaDAO.newExpression();
//				orExpr.or();
//				orExpr.equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE, "*");
//				orExpr.equals(ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE, stat.getAzione());
//
//				expr.and(orExpr);
//			}
//			
//			expr.equals(ConfigurazioneStatistica.model().ENABLED, true);
//
//			IPaginatedExpression pagExpr = this.confStatisticaDAO.toPaginatedExpression(expr);
//			List<ConfigurazioneStatistica> list = this.confStatisticaDAO.findAll(pagExpr);

			if(stat.getDestinatario()==null || stat.getDestinatario().getTipo()==null || stat.getDestinatario().getNome()==null){
				if(this.debug){
					this.logger.debug("Statistiche personalizzate non ricercate: destinatario non presente");
				}
				return;
			}
			if(stat.getTipoServizio()==null || stat.getServizio()==null){
				if(this.debug){
					this.logger.debug("Statistiche personalizzate non ricercate: servizio non presente");
				}
				return;
			}
			
			IDServizio idServizio = IDServizioFactory.getInstance().
					getIDServizioFromValues(stat.getTipoServizio(), stat.getServizio(), 
							stat.getDestinatario(), 
							stat.getVersioneServizio());
			if (!("*".equals(stat.getAzione()))) {
				idServizio.setAzione(stat.getAzione());
			}
			
			BasicServiceLibraryReader reader = new BasicServiceLibraryReader(this.pluginsBaseSM, this.utilsSM, this.debug);
			BasicServiceLibrary basicLibrary = null;
			try{
				basicLibrary = reader.read(idServizio, this.logger);
				if(basicLibrary==null){
					throw new NotFoundException("Null instance return");
				}
			}catch(NotFoundException notFound){
				if(this.debug){
					this.logger.debug("Statistiche personalizzate non ricercate: nessuna configurazione base presente per l'IDServizio: "+idServizio,notFound);
				}
				return;
			}

			TransactionServiceLibrary transactionLibrary = null;
			if(this.analisiTransazioniCustom){
				TransactionServiceLibraryReader transactionReader = new TransactionServiceLibraryReader(this.pluginsTransazioniSM, this.debug);
				transactionLibrary = transactionReader.readConfigurazioneTransazione(basicLibrary, this.logger);
			}
			
			StatisticsServiceLibraryReader statReader = new StatisticsServiceLibraryReader(this.pluginsStatisticheSM, this.debug);
			StatisticsServiceLibrary statLibrary = null;
			try{
				statLibrary = statReader.readConfigurazioneStatistiche(basicLibrary, transactionLibrary, this.logger);
				if(statLibrary==null){
					throw new NotFoundException("Null instance return");
				}
			}catch(NotFoundException notFound){
				if(this.debug){
					this.logger.debug("Statistiche personalizzate non ricercate: nessuna configurazione specifica per le statistiche presente per l'IDServizio: "+idServizio,notFound);
				}
				return;
			}
						
			List<ConfigurazioneStatistica> list = statLibrary.mergeServiceActionSearchLibrary(false,true); // si vuole registrare nei log che un plugin è disabilitato
			
			for (ConfigurazioneStatistica confStat : list) {

				String classNameStatisticaPersonalizzata = confStat.getPlugin().getClassName();
				String labelStatisticaPersonalizzata = confStat.getLabel();
				if(this.debug){
					this.logger.debug("*** Inizio Gestione Statistica personalizzata ("+classNameStatisticaPersonalizzata+" ["+labelStatisticaPersonalizzata+"]) ****");
				}
				stat.setIdStatistica(confStat.getIdConfigurazioneStatistica());
				stat.setPluginClassname(classNameStatisticaPersonalizzata);
				IDynamicLoader cStatPersonalizzata = DynamicFactory.getInstance().newDynamicLoader(confStat.getPlugin().getTipoPlugin(), confStat.getPlugin().getTipo(),
						classNameStatisticaPersonalizzata, this.logger);
				IStatisticProcessing statProcessing = (IStatisticProcessing) cStatPersonalizzata.newInstance();
				if(this.isEnabledStatisticTypeCustom(statProcessing)){
					if(this.debug){
						this.logger.debug("---- Invocazione ["+cStatPersonalizzata.getClassName()+"."+this.getStatisticPluginMethodName()+"()] ...");
					}
					try {
						this.callStatisticPluginMethod(statProcessing, stat);
						if(this.debug){
							this.logger.debug("---- Invocazione ["+cStatPersonalizzata.getClassName()+"."+this.getStatisticPluginMethodName()+"()] terminata");
						}
					} catch (Exception e) {
						this.logger.error("---- Invocazione ["+cStatPersonalizzata.getClassName()+"."+this.getStatisticPluginMethodName()+"()] terminata con errori: "+e.getMessage(), e);
					} 
				}
				else{
					if(this.debug){
						this.logger.debug("Tipo di statistica ["+this.getTipoStatistiche()+"] non abilitata nel plugin");
					}
				}
				if(this.debug){
					this.logger.debug("*** Fine Gestione Statistica personalizzata ("+classNameStatisticaPersonalizzata+" ["+labelStatisticaPersonalizzata+"]) ****");
				}
				
			}

		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		} 

	}
	
	private boolean isEnabledStatisticTypeCustom(IStatisticProcessing statProcessing){
		List<StatisticType> listEnabled = statProcessing.getEnabledStatisticType();
		switch (this.getTipoStatistiche()) {
			case STATISTICHE_ORARIE:
				for (StatisticType statisticType : listEnabled) {
					if(StatisticType.ORARIA.equals(statisticType)){
						return true;
					}
				}
				break;
			case STATISTICHE_GIORNALIERE:
				for (StatisticType statisticType : listEnabled) {
					if(StatisticType.GIORNALIERA.equals(statisticType)){
						return true;
					}
				}
				break;
			case STATISTICHE_SETTIMANALI:
				for (StatisticType statisticType : listEnabled) {
					if(StatisticType.SETTIMANALE.equals(statisticType)){
						return true;
					}
				}
				break;
			case STATISTICHE_MENSILI:
				for (StatisticType statisticType : listEnabled) {
					if(StatisticType.MENSILE.equals(statisticType)){
						return true;
					}
				}
				break;
		}
		return false;
	}
	
	
	
	
	// ---- GENERAZIONE STATISTICHE PERSONALIZZATE ----
	protected void generaStatisticaPersonalizzataByStato(StatisticBean stat) {
		this.generaStatisticaPersonalizzata(stat, true, null, null);
	}
	protected void generaStatisticaPersonalizzata(StatisticBean stat,
			RisorsaSemplice risorsaSemplice) {
		this.generaStatisticaPersonalizzata(stat, false, risorsaSemplice, null);
	}
	protected void generaStatisticaPersonalizzata(StatisticBean stat,
			RisorsaAggregata risorsaAggregata) {
		this.generaStatisticaPersonalizzata(stat, false, null, risorsaAggregata);
	}
	private void generaStatisticaPersonalizzata(StatisticBean stat,
			boolean gropuByStato,
			RisorsaSemplice risorsaSemplice, RisorsaAggregata risorsaAggregata) {

		TipoPdD tipoPdD = null;
		String idStatisticaPersonalizzata = null;
		Date dateStatistics = null;
		try{
			tipoPdD = stat.getTipoPorta();
			Date dateLeft = stat.getDateIntervalLeft();
			Date dateRight = stat.getDateIntervalRight();
			dateStatistics = stat.getData();
			idStatisticaPersonalizzata = stat.getIdStatistica();
			
			if(this.debug){
				this.logger.debug("Generazione statistica personalizzata ["+idStatisticaPersonalizzata+"] ["+this.getTipoStatistiche()+"] ["+tipoPdD+
						"]("+this.getIntervalloStatistica(dateStatistics,false)+") ...");
			}
			
			ISQLFieldConverter fieldConverter = ((IDBServiceUtilities<?>)this.transazioneSearchDAO).getFieldConverter(); 
			
			Hashtable<String, StatisticaContenuti> statisticheContenuti = new Hashtable<String, StatisticaContenuti>();
			
			
			String idRisorsa = null;
			StatisticResourceFilter [] risorseFiltri = null;
			if(risorsaSemplice!=null){
				idRisorsa = risorsaSemplice.getIdRisorsa();
				if(risorsaSemplice.getFiltri()!=null && risorsaSemplice.getFiltri().size()>0){
					risorseFiltri = risorsaSemplice.getFiltri().toArray(new StatisticResourceFilter[1]);
				}
			}
			
			
			
			
			
			// ** Informazioni di Base (numero transazioni e dimensione) **

			IExpression expr = this.transazioneSearchDAO.newExpression();
			
			List<FunctionField> selectList = new ArrayList<FunctionField>();
			StatisticsUtils.addSelectFieldCountTransaction(selectList);
			StatisticsUtils.addSelectFieldSizeTransaction(tipoPdD, selectList);
			
			// Creo espressione
			List<AliasFilter> aliases = new ArrayList<AliasFilter>();
			if(gropuByStato){
				StatisticsUtils.setExpressionByStato(expr, dateLeft, dateRight, tipoPdD, false, stat, fieldConverter);
			}else{
				StatisticsUtils.setExpressionStatsPersonalizzate(expr, dateLeft, dateRight, tipoPdD, false, stat, fieldConverter, 
						aliases, idRisorsa, risorseFiltri);
			}

			// aggiungo eventuale filtro personalizzato
			if(risorsaAggregata!=null && (risorsaAggregata.getFiltro() instanceof FilterImpl)){
				FilterImpl f = (FilterImpl) risorsaAggregata.getFiltro();
				expr.and(f.getExpression());
			}
			
			if(this.debug){
				this.logger.debug("Analizzo dati di base statistica personalizzata ["+idStatisticaPersonalizzata+"]  ["+this.getTipoStatistiche()+
						"] Intervallo date: ["+dateLeft.toString()+" - "+dateRight.toString()+"]");
				this.logger.debug("Valori query (ms) tr.data_ingresso_richiesta>["+dateLeft.getTime()+"] AND tr.data_ingresso_richiesta<=["+dateRight.getTime()+"]");
				this.logger.debug("Expr: "+expr.toString());
			}

			if(this.forceIndexConfig!=null){
				if(this.forceIndexConfig.getTransazioniForceIndexGroupBy_custom_numero_dimesione()!=null){
					List<Index> listForceIndexes = this.forceIndexConfig.getTransazioniForceIndexGroupBy_custom_numero_dimesione();
					if(listForceIndexes.size()>0){
						for (Index index : listForceIndexes) {
							expr.addForceIndex(index);
						}
					}
				}
			}
			
			List<Map<String, Object>> list = this.transazioneSearchDAO.groupBy(expr, selectList.toArray(new FunctionField[1]));

			for (Map<String, Object> row : list) {
				
				if(list.size()>1){
					if(risorsaAggregata!=null){
						throw new Exception("Localizzata più di una entry?? Comportamento non atteso per una risorsa aggregata");
					}
				}
				
				// Update della statistica per quanto concerne i valori
				StatisticaContenuti statisticaContenuti = new  StatisticaContenuti();
				if(gropuByStato){
					StatisticsUtils.fillStatisticsContenutiByStato(idStatisticaPersonalizzata, statisticaContenuti, row);
				}
				else if(risorsaSemplice!=null){
					StatisticsUtils.fillStatisticsContenuti(idStatisticaPersonalizzata, statisticaContenuti, row, aliases, risorsaSemplice);
				}
				else{
					StatisticsUtils.fillStatisticsContenuti(idStatisticaPersonalizzata, statisticaContenuti, risorsaAggregata);
				}
				
				// Aggiungo informazioni sul numero di transazioni e size delle transazioni
				StatisticBean tmp = new StatisticBean();
				tmp.setTipoPorta(stat.getTipoPorta());
				StatisticsUtils.updateStatisticBeanCountTransactionInfo(tmp, row);
				StatisticsUtils.updateStatisticBeanSizeTransactionInfo(tmp, row);
				statisticaContenuti.setNumeroTransazioni((int)tmp.getRichieste());
				statisticaContenuti.setDimensioniBytesBandaComplessiva(tmp.getBytesBandaTotale());
				statisticaContenuti.setDimensioniBytesBandaInterna(tmp.getBytesBandaInterna());
				statisticaContenuti.setDimensioniBytesBandaEsterna(tmp.getBytesBandaEsterna());
				
				// Aggiungo in cache
				String key = StatisticsUtils.buildKey(statisticaContenuti);
				statisticheContenuti.put(key, statisticaContenuti);
				
			}
			
			
			
			
			// ** Latenze **
			try{
				expr = this.transazioneSearchDAO.newExpression();
				
				selectList = new ArrayList<FunctionField>();
				StatisticsUtils.addSelectFieldLatencyTransaction(tipoPdD, fieldConverter, selectList);
				
				// Creo espressione
				aliases = new ArrayList<AliasFilter>();
				if(gropuByStato){
					StatisticsUtils.setExpressionByStato(expr, dateLeft, dateRight, tipoPdD, false, stat, fieldConverter);
				}else{
					StatisticsUtils.setExpressionStatsPersonalizzate(expr, dateLeft, dateRight, tipoPdD, true, stat, fieldConverter, 
							aliases, idRisorsa, risorseFiltri);
				}

				// aggiungo eventuale filtro personalizzato
				if(risorsaAggregata!=null && (risorsaAggregata.getFiltro() instanceof FilterImpl)){
					FilterImpl f = (FilterImpl) risorsaAggregata.getFiltro();
					expr.and(f.getExpression());
				}
	
				if(this.debug){
					this.logger.debug("Analizzo dati sulla latenza per la statistica personalizzata ["+idStatisticaPersonalizzata+"]  ["+
							this.getTipoStatistiche()+"] Intervallo date: ["+dateLeft.toString()+" - "+dateRight.toString()+"]");
					this.logger.debug("Valori query (ms) tr.data_ingresso_richiesta>["+dateLeft.getTime()+"] AND tr.data_ingresso_richiesta<=["+dateRight.getTime()+"]");
				}
	
				if(this.forceIndexConfig!=null){
					if(this.forceIndexConfig.getTransazioniForceIndexGroupBy_custom_latenze()!=null){
						List<Index> listForceIndexes = this.forceIndexConfig.getTransazioniForceIndexGroupBy_custom_latenze();
						if(listForceIndexes.size()>0){
							for (Index index : listForceIndexes) {
								expr.addForceIndex(index);
							}
						}
					}
				}
				
				list = this.transazioneSearchDAO.groupBy(expr, selectList.toArray(new FunctionField[1]));
	
				for (Map<String, Object> row : list) {
					
					if(list.size()>1){
						if(risorsaAggregata!=null){
							throw new Exception("Localizzata più di una entry?? Comportamento non atteso per una risorsa aggregata");
						}
					}
					
					// Update della statistica per quanto concerne i valori
					StatisticaContenuti statisticaContenuti = new  StatisticaContenuti();
					if(gropuByStato){
						StatisticsUtils.fillStatisticsContenutiByStato(idStatisticaPersonalizzata, statisticaContenuti, row);
					}
					else if(risorsaSemplice!=null){
						StatisticsUtils.fillStatisticsContenuti(idStatisticaPersonalizzata, statisticaContenuti, row, aliases, risorsaSemplice);
					}
					else{
						StatisticsUtils.fillStatisticsContenuti(idStatisticaPersonalizzata, statisticaContenuti, risorsaAggregata);
					}
					
					// Prelevo dalla cache (Deve esistere per forza)
					String key = StatisticsUtils.buildKey(statisticaContenuti);
					statisticaContenuti = statisticheContenuti.get(key);
					if(statisticaContenuti==null){
						throw new Exception("Statistica ["+key+"] non presente in cache??");
					}
					
					// Aggiorno informazioni sulla latenza
					StatisticBean tmp = new StatisticBean();
					StatisticsUtils.updateStatisticsBeanLatencyTransactionInfo(tmp, row);
					statisticaContenuti.setLatenzaPorta(tmp.getLatenzaPorta());
					statisticaContenuti.setLatenzaServizio(tmp.getLatenzaServizio());
					statisticaContenuti.setLatenzaTotale(tmp.getLatenzaTotale());
					
				}
			} catch (NotFoundException e) {
				if(this.debug){
					this.logger.debug(e.getMessage(),e);
				}
			}
			
			
			
			
			// ** Aggiorno statistiche **
			if(this.debug){
				this.logger.debug("Aggiorno ["+statisticheContenuti.size()+"] risultati statistiche personalizzate");
			}
			if(statisticheContenuti.size()>0){
				this.updateStatistica(stat.getId(), statisticheContenuti.values().toArray(new StatisticaContenuti[1]));
			}


		} catch (ServiceException e){
			this.logger.error(e.getMessage(),e);
		} catch (NotImplementedException e) {
			this.logger.error(e.getMessage(),e);
		} catch (ExpressionNotImplementedException e) {
			this.logger.error(e.getMessage(),e);
		} catch (ExpressionException e) {
			this.logger.error(e.getMessage(),e);
		} catch (NotFoundException e) {
			if(this.debug){
				this.logger.debug(e.getMessage(),e);
			}
		}catch (Exception e) {
			this.logger.error(e.getMessage(),e);
		}  

		if(this.debug){
			this.logger.debug("Generazione statistica personalizzata ["+idStatisticaPersonalizzata+"] ["+this.getTipoStatistiche()+"] ["+tipoPdD+"]("+
					this.getIntervalloStatistica(dateStatistics,false)+") terminata");
		}
	}


	
	
	
	
	
	
	// ---- CRUD ----
	

	private void deleteStatistiche( TipoPdD tipoPdD, Date data) {

		if(this.debug){
			this.logger.debug("Eliminazione statistiche ["+this.getTipoStatistiche()+"] ["+tipoPdD+"]("+this.getIntervalloStatistica(data)+") ...");
		}

		try{

			IExpression expr = this.statisticaServiceDAO.newExpression();
			Date next = this.truncDate(this.incrementDate(data, false),false);
			expr.equals(this.model.DATA,  next);
			expr.and();
			if(TipoPdD.DELEGATA.equals(tipoPdD)){
				expr.equals(this.model.TIPO_PORTA, PddRuolo.DELEGATA);
			}else{
				expr.equals(this.model.TIPO_PORTA, PddRuolo.APPLICATIVA);
			}

			if(this.debug){
				this.logger.debug("Elimino statistiche ["+this.getTipoStatistiche()+"]");
				this.logger.debug("Valori eliminazione (ms) tr.data_ingresso_richiesta=["+next.getTime()+"]");
			}
			NonNegativeNumber nnn = this.statisticaServiceDAO.deleteAll(expr);

			int righeEliminate = nnn!= null ? (int) nnn.longValue() : 0;

			if(this.debug){
				this.logger.debug("Eliminate ["+righeEliminate+"] entry");
			}


		}catch(ServiceException e){
			this.logger.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.logger.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			this.logger.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			this.logger.error(e.getMessage(), e);
		}

		if(this.debug){
			this.logger.debug("Eliminazione statistiche ["+this.getTipoStatistiche()+"] ["+tipoPdD+"]("+this.getIntervalloStatistica(data)+") terminata");
		}

	}

	protected abstract Long insertStatistica(Statistica statistica) throws StatisticException;
	protected abstract void updateStatistica(long idStatistica, StatisticaContenuti ... statisticaContenuti) throws StatisticException;


	private void insertStatistica(StatisticBean stat) throws StatisticException  {

		if(this.debug){
			this.logger.debug("Inserimento statistica ["+stat.toString()+"] in corso ...");
		}

		Statistica statisticaBase = new Statistica();

		statisticaBase.setData(stat.getData());
		
		statisticaBase.setTipoPorta(org.openspcoop2.core.statistiche.constants.TipoPorta.toEnumConstant(stat.getTipoPorta().getTipo()));
		statisticaBase.setIdPorta(stat.getIdPorta());
		
		statisticaBase.setTipoMittente(stat.getMittente().getTipo());
		statisticaBase.setMittente(stat.getMittente().getNome());
		
		statisticaBase.setTipoDestinatario(stat.getDestinatario().getTipo());
		statisticaBase.setDestinatario(stat.getDestinatario().getNome());
		
		statisticaBase.setTipoServizio(stat.getTipoServizio());
		statisticaBase.setServizio(stat.getServizio());
		statisticaBase.setVersioneServizio(stat.getVersioneServizio());
		
		statisticaBase.setAzione(stat.getAzione());
		
		statisticaBase.setServizioApplicativo(stat.getServizioApplicativo());
		
		statisticaBase.setTrasportoMittente(stat.getTrasportoMittente());
		
		statisticaBase.setTokenIssuer(stat.getTokenIssuer());
		statisticaBase.setTokenClientId(stat.getTokenClientId());
		statisticaBase.setTokenSubject(stat.getTokenSubject());
		statisticaBase.setTokenUsername(stat.getTokenUsername());
		statisticaBase.setTokenMail(stat.getTokenMail());
		
		statisticaBase.setClientAddress(stat.getClientAddress());
		
		statisticaBase.setGruppi(stat.getGruppo());
		
		statisticaBase.setUriApi(stat.getApi());
		
		if(stat.getDestinatario()!=null && stat.getDestinatario().getTipo()!=null) {
			EsitiProperties esitiProperties = null;
			int esitoConsegnaMultipla = -1;
			int esitoConsegnaMultiplaFallita = -1;
			int esitoConsegnaMultiplaCompletata = -1;
			try {
				String protocollo = null;
				if(stat.getDestinatario()!=null && 
						stat.getDestinatario().getTipo()!=null && 
						!"".equals(stat.getDestinatario().getTipo()) && 
						!Costanti.INFORMAZIONE_NON_DISPONIBILE.equals(stat.getDestinatario().getTipo())) {
					protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(stat.getDestinatario().getTipo());
				}
				else if(stat.getMittente()!=null && 
						stat.getMittente().getTipo()!=null && 
						!"".equals(stat.getMittente().getTipo()) && 
						!Costanti.INFORMAZIONE_NON_DISPONIBILE.equals(stat.getMittente().getTipo())) {
					protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(stat.getMittente().getTipo());
				}
				else {
					protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
				}
				esitiProperties = EsitiProperties.getInstance(this.logger, protocollo);
				esitoConsegnaMultipla = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
				esitoConsegnaMultiplaFallita = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA);
				esitoConsegnaMultiplaCompletata = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA);
				if(stat.getEsito().intValue() == esitoConsegnaMultiplaFallita || stat.getEsito().intValue() == esitoConsegnaMultiplaCompletata) {
					statisticaBase.setEsito(esitoConsegnaMultipla);
				}
				else {
					statisticaBase.setEsito(stat.getEsito());
				}
			}catch(Exception er) {
				throw new StatisticException(er.getMessage(),er);
			}
		}
		else {
			statisticaBase.setEsito(stat.getEsito());
		}
		statisticaBase.setEsitoContesto(stat.getEsitoContesto());
		
		statisticaBase.setNumeroTransazioni((int)stat.getRichieste());
		
		statisticaBase.setDimensioniBytesBandaComplessiva(stat.getBytesBandaTotale());
		statisticaBase.setDimensioniBytesBandaInterna(stat.getBytesBandaInterna());
		statisticaBase.setDimensioniBytesBandaEsterna(stat.getBytesBandaEsterna());
		
		if(stat.getLatenzaServizio()!=Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE) {
			statisticaBase.setLatenzaServizio(stat.getLatenzaServizio());
		}
		if(stat.getLatenzaPorta()!=Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE) {
			statisticaBase.setLatenzaPorta(stat.getLatenzaPorta());
		}
		if(stat.getLatenzaTotale()!=Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE) {
			statisticaBase.setLatenzaTotale(stat.getLatenzaTotale());
		}
		
		Long id = insertStatistica(statisticaBase);
		if(id!=null && id >0){
			stat.setId(id);
		}	
		if(this.debug){
			this.logger.debug("Inserimento statistica effettuato con successo");
		}

	}
	
}
