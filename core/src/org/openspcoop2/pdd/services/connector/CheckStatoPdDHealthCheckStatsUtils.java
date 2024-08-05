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

package org.openspcoop2.pdd.services.connector;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.statistiche.StatisticaInfo;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoServiceSearch;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.statistic.StatisticheGiornaliere;
import org.openspcoop2.monitor.engine.statistic.StatisticheMensili;
import org.openspcoop2.monitor.engine.statistic.StatisticheOrarie;
import org.openspcoop2.monitor.engine.statistic.StatisticheSettimanali;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**
 * CheckStatoPdDHealthCheckStatsUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CheckStatoPdDHealthCheckStatsUtils {
	
	private CheckStatoPdDHealthCheckStatsUtils() {}

	public static void verificaInformazioniStatistiche(Logger log, OpenSPCoop2Properties properties) throws CoreException {
		CheckStatoPdDHealthCheckStats check = CheckStatoPdDHealthCheckStats.readProprietaVerificaInformazioniStatistiche(properties);
		verificaInformazioniStatistiche(log, properties, check);
	}
	
	public static void verificaInformazioniStatistiche(Logger log, OpenSPCoop2Properties properties, CheckStatoPdDHealthCheckStats check) throws CoreException {
		
		boolean verificaStatisticaOraria = check.isVerificaStatisticaOraria();
		boolean verificaStatisticaGiornaliera = check.isVerificaStatisticaGiornaliera();
		boolean verificaStatisticaSettimanale = check.isVerificaStatisticaSettimanale();
		boolean verificaStatisticaMensile = check.isVerificaStatisticaMensile();
		
		if(!verificaStatisticaOraria && !verificaStatisticaGiornaliera && !verificaStatisticaSettimanale && !verificaStatisticaMensile) {
			return;
		}
		
		DAOFactoryProperties daoFactoryProperties = null;
		Logger daoFactoryLogger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		DAOFactory daoFactory = DAOFactory.getInstance(daoFactoryLogger);
		try {
			daoFactoryProperties = DAOFactoryProperties.getInstance(daoFactoryLogger);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		String tipoDatabase = properties.getDatabaseType();
		if(tipoDatabase==null){
			try {
				tipoDatabase = daoFactoryProperties.getTipoDatabase(new org.openspcoop2.core.statistiche.utils.ProjectInfo());
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		if(tipoDatabase==null){
			throw new CoreException("Database type undefined");
		}
		
		ServiceManagerProperties daoFactoryServiceManagerPropertiesStatistiche = null;
		try {
			daoFactoryServiceManagerPropertiesStatistiche = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		daoFactoryServiceManagerPropertiesStatistiche.setShowSql(false);	
		daoFactoryServiceManagerPropertiesStatistiche.setDatabaseType(tipoDatabase);
		
		DBStatisticheManager dbStatisticheManager = DBStatisticheManager.getInstance();
				
		Resource r = null;
		IDSoggetto dominio = properties.getIdentitaPortaDefaultWithoutProtocol();
		String idModulo = "GovWayHealthCheck";
		try{	
			Connection con = null;
			r = dbStatisticheManager.getResource(dominio, idModulo, null);
			if(r==null){
				throw new CoreException("Database resource not available");
			}
			con = (Connection) r.getResource();
			
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM = 
					(org.openspcoop2.core.statistiche.dao.IServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance(),
							con, daoFactoryServiceManagerPropertiesStatistiche, daoFactoryLogger);
			IStatisticaInfoServiceSearch search = statisticheSM.getStatisticaInfoServiceSearch();
			verificaInformazioniStatistiche(log, search, check);
		}
		catch(CoreException e) {
			throw e;
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
		finally {
			try{
				if(r!=null) {
					dbStatisticheManager.releaseResource(dominio, idModulo, r);
				}
			}catch(Exception eClose){
				// ignore
			}
		}
			
	}
	
	public static void verificaInformazioniStatistiche(Logger log, IStatisticaInfoServiceSearch search, CheckStatoPdDHealthCheckStats check) throws ServiceException, NotImplementedException, CoreException {
		
		boolean verificaStatisticaOraria = check.isVerificaStatisticaOraria();
		boolean verificaStatisticaGiornaliera = check.isVerificaStatisticaGiornaliera();
		boolean verificaStatisticaSettimanale = check.isVerificaStatisticaSettimanale();
		boolean verificaStatisticaMensile = check.isVerificaStatisticaMensile();
		int verificaStatisticaOrariaSoglia = check.getVerificaStatisticaOrariaSoglia();
		int verificaStatisticaGiornalieraSoglia = check.getVerificaStatisticaGiornalieraSoglia();
		int verificaStatisticaSettimanaleSoglia = check.getVerificaStatisticaSettimanaleSoglia();
		int verificaStatisticaMensileSoglia = check.getVerificaStatisticaMensileSoglia();
		
		IPaginatedExpression pagExpr = search.newPaginatedExpression();
		List<StatisticaInfo> list = search.findAll(pagExpr);
		if(list==null || list.isEmpty()) {
			throw new CoreException("No statistical report available");
		}
		Date dataUltimaGenerazioneOraria = null;
		Date dataUltimaGenerazioneGiornaliera = null;
		Date dataUltimaGenerazioneSettimanale = null;
		Date dataUltimaGenerazioneMensile = null;
		for (StatisticaInfo statisticaInfo : list) {
			if(statisticaInfo.getTipoStatistica()==null) {
				throw new CoreException("Found row without associated report type");
			}
			switch (statisticaInfo.getTipoStatistica()) {
			case STATISTICHE_ORARIE:
				dataUltimaGenerazioneOraria = statisticaInfo.getDataUltimaGenerazione();
				break;
			case STATISTICHE_GIORNALIERE:
				dataUltimaGenerazioneGiornaliera = statisticaInfo.getDataUltimaGenerazione();
				break;
			case STATISTICHE_SETTIMANALI:
				dataUltimaGenerazioneSettimanale = statisticaInfo.getDataUltimaGenerazione();
				break;
			case STATISTICHE_MENSILI:
				dataUltimaGenerazioneMensile = statisticaInfo.getDataUltimaGenerazione();
				break;
			}
		}
		
		// Se viene rilevata come ultimo intervallo temporale delle statistiche una data più vecchia dell'unità indicata nella soglia (relativamente al tipo di statistica) viene ritornato un errore
		// Se si indica un valore '1' si richiede ad esempio che le statistiche siano aggiornate all'intervallo precedente. 
		// Quindi ad es. all'ora precedente per l'orario, al giorno precedente per il giornaliero e cosi via... 
		// Se si indica 0 si vuole imporre che le statistiche siano aggiornate (anche parzialmente) all'ultimo intervallo 
		// NOTA: (potrebbe creare dei falsi negativi se la generazione delle statistiche avviene dopo il check)
		StringBuilder bf = new StringBuilder();
		Date now = DateManager.getDate();
		
		if(verificaStatisticaOraria) {
			if(dataUltimaGenerazioneOraria==null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("No hourly report detected");
			}
			else {
				Date nowTruncate = StatisticheOrarie.getInstanceForUtils().truncDate(now, false);
				Date dataUltimaGenerazioneTruncate = StatisticheOrarie.getInstanceForUtils().truncDate(dataUltimaGenerazioneOraria, false);
				Date dataUltimaGenerazioneTruncateConAggiuntaSoglia = new Date(dataUltimaGenerazioneTruncate.getTime());
				for (int i = 0; i < verificaStatisticaOrariaSoglia; i++) {
					dataUltimaGenerazioneTruncateConAggiuntaSoglia = StatisticheOrarie.getInstanceForUtils().incrementDate(dataUltimaGenerazioneTruncateConAggiuntaSoglia, false);	
				}
				boolean sogliaNonRispettata = dataUltimaGenerazioneTruncateConAggiuntaSoglia.before(nowTruncate);
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				String msg = getLogMessageStats("oraria",dataUltimaGenerazioneOraria, verificaStatisticaOrariaSoglia,
						dataUltimaGenerazioneTruncate, dataUltimaGenerazioneTruncateConAggiuntaSoglia,
						nowTruncate, sogliaNonRispettata);
				log.debug(msg);

				if(sogliaNonRispettata) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append("Hourly statistical information found whose last update is older than the allowed threshold ("+verificaStatisticaOrariaSoglia+"); last generation date: "+dateformat.format(dataUltimaGenerazioneOraria));
				}
			}
		}
		
		if(verificaStatisticaGiornaliera) {
			if(dataUltimaGenerazioneGiornaliera==null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("No daily report detected");
			}
			else {
				Date nowTruncate = StatisticheGiornaliere.getInstanceForUtils().truncDate(now, false);
				Date dataUltimaGenerazioneTruncate = StatisticheGiornaliere.getInstanceForUtils().truncDate(dataUltimaGenerazioneGiornaliera, false);
				Date dataUltimaGenerazioneTruncateConAggiuntaSoglia = new Date(dataUltimaGenerazioneTruncate.getTime());
				for (int i = 0; i < verificaStatisticaGiornalieraSoglia; i++) {
					dataUltimaGenerazioneTruncateConAggiuntaSoglia = StatisticheGiornaliere.getInstanceForUtils().incrementDate(dataUltimaGenerazioneTruncateConAggiuntaSoglia, false);	
				}
				boolean sogliaNonRispettata = dataUltimaGenerazioneTruncateConAggiuntaSoglia.before(nowTruncate);
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				String msg = getLogMessageStats("giornaliera",dataUltimaGenerazioneGiornaliera, verificaStatisticaGiornalieraSoglia,
						dataUltimaGenerazioneTruncate, dataUltimaGenerazioneTruncateConAggiuntaSoglia,
						nowTruncate, sogliaNonRispettata);
				log.debug(msg);

				if(sogliaNonRispettata) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append("Daily statistical information found whose last update is older than the allowed threshold ("+verificaStatisticaGiornalieraSoglia+"); last generation date: "+dateformat.format(dataUltimaGenerazioneGiornaliera));
				}
			}
		}
		
		if(verificaStatisticaSettimanale) {
			if(dataUltimaGenerazioneSettimanale==null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("No weekly report detected");
			}
			else {
				Date nowTruncate = StatisticheSettimanali.getInstanceForUtils().truncDate(now, false);
				Date dataUltimaGenerazioneTruncate = StatisticheSettimanali.getInstanceForUtils().truncDate(dataUltimaGenerazioneSettimanale, false);
				Date dataUltimaGenerazioneTruncateConAggiuntaSoglia = new Date(dataUltimaGenerazioneTruncate.getTime());
				for (int i = 0; i < verificaStatisticaSettimanaleSoglia; i++) {
					dataUltimaGenerazioneTruncateConAggiuntaSoglia = StatisticheSettimanali.getInstanceForUtils().incrementDate(dataUltimaGenerazioneTruncateConAggiuntaSoglia, false);	
				}
				boolean sogliaNonRispettata = dataUltimaGenerazioneTruncateConAggiuntaSoglia.before(nowTruncate);
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				String msg = getLogMessageStats("settimanale",dataUltimaGenerazioneSettimanale, verificaStatisticaSettimanaleSoglia,
						dataUltimaGenerazioneTruncate, dataUltimaGenerazioneTruncateConAggiuntaSoglia,
						nowTruncate, sogliaNonRispettata);
				log.debug(msg);

				if(sogliaNonRispettata) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append("Weekly statistical information found whose last update is older than the allowed threshold ("+verificaStatisticaSettimanaleSoglia+"); last-generation: "+dateformat.format(dataUltimaGenerazioneSettimanale));
				}
			}
		}
		
		if(verificaStatisticaMensile) {
			if(dataUltimaGenerazioneMensile==null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append("No monthly report detected");
			}
			else {
				Date nowTruncate = StatisticheMensili.getInstanceForUtils().truncDate(now, false);
				Date dataUltimaGenerazioneTruncate = StatisticheMensili.getInstanceForUtils().truncDate(dataUltimaGenerazioneMensile, false);
				Date dataUltimaGenerazioneTruncateConAggiuntaSoglia = new Date(dataUltimaGenerazioneTruncate.getTime());
				for (int i = 0; i < verificaStatisticaMensileSoglia; i++) {
					dataUltimaGenerazioneTruncateConAggiuntaSoglia = StatisticheMensili.getInstanceForUtils().incrementDate(dataUltimaGenerazioneTruncateConAggiuntaSoglia, false);	
				}
				boolean sogliaNonRispettata = dataUltimaGenerazioneTruncateConAggiuntaSoglia.before(nowTruncate);
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				String msg = getLogMessageStats("mensile",dataUltimaGenerazioneMensile, verificaStatisticaMensileSoglia,
						dataUltimaGenerazioneTruncate, dataUltimaGenerazioneTruncateConAggiuntaSoglia,
						nowTruncate, sogliaNonRispettata);
				log.debug(msg);

				if(sogliaNonRispettata) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append("Monthly statistical information found whose last update is older than the allowed threshold ("+verificaStatisticaMensileSoglia+"); last-generation: "+dateformat.format(dataUltimaGenerazioneMensile));
				}
			}
		}
		
		if(bf.length()>0) {
			throw new CoreException(bf.toString());
		}
	}
	
	private static String getLogMessageStats(String tipo, Date dataUltimaGenerazione, int verificaStatisticaSoglia,
			Date dataUltimaGenerazioneTruncate, Date dataUltimaGenerazioneTruncateConAggiuntaSoglia,
			Date nowTruncate, boolean sogliaNonRispettata) {
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		return "Check [Statistica "+tipo+"], ultima data di generazione ["+dateformat.format(dataUltimaGenerazione)+
				"] dataUltimaGenerazioneTruncate["+dateformat.format(dataUltimaGenerazioneTruncate)+"] soglia["+verificaStatisticaSoglia+
				"] dataUltimaGenerazioneTruncateConAggiuntaSoglia["+dateformat.format(dataUltimaGenerazioneTruncateConAggiuntaSoglia)+
				"] nowTruncate["+dateformat.format(nowTruncate)+"] sogliaNonRispettata["+sogliaNonRispettata+"]";
	}
	
}
