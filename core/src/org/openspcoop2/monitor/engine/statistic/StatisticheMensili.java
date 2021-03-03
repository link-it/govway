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

import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IDBStatisticaMensileService;
import org.openspcoop2.core.statistiche.dao.IStatisticaMensileService;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.plugins.IStatisticProcessing;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * StatisticheMensili
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheMensili extends AbstractStatistiche {

	public static StatisticheMensili getInstanceForUtils(){
		return new StatisticheMensili(); 
	}
	StatisticheMensili(){
			super();
	}
	public StatisticheMensili( 
			Logger logger, boolean logQuery, boolean useUnionForLatency,  
			boolean generazioneStatisticheCustom, boolean analisiTransazioniCustom,
			StatisticsForceIndexConfig forceIndexConfig,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM) {
		super( logger, logQuery, useUnionForLatency, 
				generazioneStatisticheCustom,analisiTransazioniCustom,
				forceIndexConfig,
				statisticheSM,transazioniSM,
				pluginsStatisticheSM,pluginsBaseSM,utilsSM,pluginsTransazioniSM);
		
		try {
			
			if(statisticheSM==null){
				throw new ServiceException("ServiceManager ["+org.openspcoop2.core.statistiche.dao.IServiceManager.class.getName()+"] non inizializzato");
			}
			
			this.model = StatisticaMensile.model().STATISTICA_BASE;
			this.statisticaServiceDAO = this.statisticheSM.getStatisticaMensileService( );
			this.statisticaServiceSearchDAO = this.statisticheSM.getStatisticaMensileServiceSearch();
		
		} catch (ServiceException e) {
			this.logger.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.logger.error(e.getMessage(), e);
		}
	}
	
	private Calendar truncDate(Date date){
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		cTmp.set(Calendar.DAY_OF_MONTH, 1);
		cTmp.set(Calendar.HOUR_OF_DAY, 0);
		cTmp.set(Calendar.MINUTE, 0);
		cTmp.set(Calendar.SECOND, 0);
		cTmp.set(Calendar.MILLISECOND, 0);
		return cTmp;
	}
	
	@Override
	public Date truncDate(Date date,boolean print){
		Calendar cTmp = truncDate(date);
		if(print){
			printCalendar(cTmp);
		}
		return cTmp.getTime();
	}
	
	@Override
	public String getIntervalloStatistica(Date date,boolean print){
		
		//System.out.println("INTERVALLO MENSILE PER ["+date.toString()+"]");
		
		Calendar cTmp = truncDate(date);
		StringBuilder bf = new StringBuilder();
		switch (cTmp.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
			bf.append("Gennaio");	
			break;
		case Calendar.FEBRUARY:
			bf.append("Febbraio");	
			break;
		case Calendar.MARCH:
			bf.append("Marzo");	
			break;
		case Calendar.APRIL:
			bf.append("Aprile");	
			break;
		case Calendar.MAY:
			bf.append("Maggio");	
			break;
		case Calendar.JUNE:
			bf.append("Giugno");	
			break;
		case Calendar.JULY:
			bf.append("Luglio");	
			break;
		case Calendar.AUGUST:
			bf.append("Agosto");	
			break;
		case Calendar.SEPTEMBER:
			bf.append("Settembre");	
			break;
		case Calendar.OCTOBER:
			bf.append("Ottobre");	
			break;
		case Calendar.NOVEMBER:
			bf.append("Novembre");	
			break;
		case Calendar.DECEMBER:
			bf.append("Dicembre");	
			break;
		default:
			bf.append("Non definito?");	
			break;
		}
		
		//System.out.println("INTERVALLO MENSILE PER ["+date.toString()+"]: "+bf.toString());
		
		return bf.toString();
	}
	
	@Override
	public Date incrementDate(Date date,boolean print){
		return operation(date, print, +1);
	}
	
	@Override
	public Date decrementDate(Date date,boolean print){
		return operation(date, print, -1);
	}
	
	private Date operation(Date date,boolean print, int value){
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		if(print){
			super.logger.debug(">>> incrementDate >>>>");
			super.logger.debug("Before: ");
			printCalendar(cTmp);
		}
		/*else{
			super.log.debug("***** ("+value+") data PRIMA OPERAZIONE:");
			printCalendar(cTmp);
		}*/
		
		// prelevo giorno impostato nella data
		int giorno = cTmp.get(Calendar.DAY_OF_MONTH);
		//super.log.debug("***** ("+value+") prelevo giorno impostato nella data: "+giorno);
		
		// prelevo ultimo giorno del mese impostato nell'attuale data
		int lastDayActualMonth = cTmp.getActualMaximum(Calendar.DAY_OF_MONTH);
		//super.log.debug("***** ("+value+") prelevo ultimo giorno del mese impostato nell'attuale data: "+lastDayActualMonth);
		
		// incremento
		//super.log.debug("***** ("+value+") effettuo operazione");
		cTmp.add(Calendar.MONTH, value);
		
		// prelevo giorno impostato nella data modificata
		//int giornoDataModificata = cTmp.get(Calendar.DAY_OF_MONTH);
		//super.log.debug("***** ("+value+") prelevo giorno impostato nella data modificata: "+giornoDataModificata);
		
		// Se il giorno della data originale era uguale all'ultimo giorno del mese della data originale, 
		// ripristino questa condizione anche nella data modificata
		if(giorno == lastDayActualMonth){
			
			//super.log.debug("***** ("+value+") ripristino ultimo giorno del mese");
			
			// prelevo ultimo giorno del mese impostato nella data modificata
			int lastDayMonth = cTmp.getActualMaximum(Calendar.DAY_OF_MONTH);
			//super.log.debug("***** ("+value+") prelevo ultimo giorno del mese impostato nella data modificata: "+lastDayMonth);
			
			// ripristino condizione
			cTmp.set(Calendar.DAY_OF_MONTH, lastDayMonth);
			
			//int giornoDataRipristinata = cTmp.get(Calendar.DAY_OF_MONTH);
			//super.log.debug("***** ("+value+") prelevo giorno impostato nella data ripristinata: "+giornoDataRipristinata);
			
		}
		
		if(print){
			super.logger.debug("After: ");
			printCalendar(cTmp);
			super.logger.debug(">>> incrementDate end >>>>");
		}
		/*else{
			super.log.debug("***** ("+value+") data FINE OPERAZIONE:");
			printCalendar(cTmp);
		}*/
		return cTmp.getTime();
	}
	
	@Override
	public TipoIntervalloStatistico getTipoStatistiche(){
		return TipoIntervalloStatistico.STATISTICHE_MENSILI;
	}
	
 	@Override
	public void callStatisticPluginMethod(IStatisticProcessing statProcessing,
			StatisticBean stat) throws StatisticException {
		IStatistic statContext = new CustomStatisticsSdkGenerator(stat, StatisticType.MENSILE, this);
		statProcessing.createMonthlyStatisticData(statContext);
	}
	
	@Override
	public String getStatisticPluginMethodName() throws StatisticException {
		return "createMonthlyStatisticData";
	}
	

	@Override
	protected Long insertStatistica(Statistica statistica) throws StatisticException {
		
		try{
		
			StatisticaMensile stat = new StatisticaMensile();
			stat.setStatisticaBase(statistica);
			((IStatisticaMensileService)this.statisticaServiceDAO).create(stat);
			return stat.getId();
			
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}

	}
	
	@Override
	protected void updateStatistica(long idStatistica, StatisticaContenuti ... statisticaContenuti) throws StatisticException {
		
		try{
		
			StatisticaMensile stat = ((IDBStatisticaMensileService)this.statisticaServiceDAO).get(idStatistica);
			for (int i = 0; i < statisticaContenuti.length; i++) {
				statisticaContenuti[i].setData(stat.getStatisticaBase().getData());
				stat.addStatisticaMensileContenuti(statisticaContenuti[i]);
			}
			((IStatisticaMensileService)this.statisticaServiceDAO).update(stat);
			
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}

	}
	
}
