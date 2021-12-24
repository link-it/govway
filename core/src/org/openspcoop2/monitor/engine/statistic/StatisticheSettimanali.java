/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IDBStatisticaSettimanaleService;
import org.openspcoop2.core.statistiche.dao.IStatisticaSettimanaleService;
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
 * StatisticheSettimanali
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheSettimanali extends AbstractStatistiche {

	public static StatisticheSettimanali getInstanceForUtils(){
		return new StatisticheSettimanali(); 
	}
	StatisticheSettimanali(){
			super();
	}
	public StatisticheSettimanali( 
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
			
			this.model = StatisticaSettimanale.model().STATISTICA_BASE;
			this.statisticaServiceDAO = this.statisticheSM.getStatisticaSettimanaleService();
			this.statisticaServiceSearchDAO = this.statisticheSM.getStatisticaSettimanaleServiceSearch();
		
		} catch (ServiceException e) {
			this.logger.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.logger.error(e.getMessage(), e);
		}
	}
	
	private Calendar truncDate(Date date){
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		while(Calendar.MONDAY!=cTmp.get(Calendar.DAY_OF_WEEK)){
			cTmp.add(Calendar.DAY_OF_WEEK, -1);
		}
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
		
		//System.out.println("INTERVALLO SETTIMANALE PER ["+date.toString()+"]");
		
		Calendar cTmp = truncDate(date);
		StringBuilder bf = new StringBuilder();
		bf.append("Settimana N.");
		bf.append(cTmp.get(Calendar.WEEK_OF_YEAR));
		
		bf.append(" (");
		
		switch (cTmp.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			bf.append("Lunedì");
			break;
		case Calendar.TUESDAY:
			bf.append("Martedì");
			break;
		case Calendar.WEDNESDAY:
			bf.append("Mercoledì");
			break;
		case Calendar.THURSDAY:
			bf.append("Giovedì");
			break;
		case Calendar.FRIDAY:
			bf.append("Venerdì");
			break;
		case Calendar.SATURDAY:
			bf.append("Sabato");
			break;
		case Calendar.SUNDAY:
			bf.append("Domenica");
			break;	
		}
		bf.append(",");
		bf.append(cTmp.get(Calendar.DAY_OF_MONTH));
		bf.append("/");
		bf.append((cTmp.get(Calendar.MONTH)+1));
		bf.append("/");
		bf.append(cTmp.get(Calendar.YEAR));
		
		bf.append(" - ");
		cTmp.add(Calendar.DAY_OF_YEAR, 7);
		cTmp.add(Calendar.MILLISECOND, -1);
		switch (cTmp.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			bf.append("Lunedì");
			break;
		case Calendar.TUESDAY:
			bf.append("Martedì");
			break;
		case Calendar.WEDNESDAY:
			bf.append("Mercoledì");
			break;
		case Calendar.THURSDAY:
			bf.append("Giovedì");
			break;
		case Calendar.FRIDAY:
			bf.append("Venerdì");
			break;
		case Calendar.SATURDAY:
			bf.append("Sabato");
			break;
		case Calendar.SUNDAY:
			bf.append("Domenica");
			break;
		}
		bf.append(",");
		bf.append(cTmp.get(Calendar.DAY_OF_MONTH));
		bf.append("/");
		bf.append((cTmp.get(Calendar.MONTH)+1));
		bf.append("/");
		bf.append(cTmp.get(Calendar.YEAR));
		
		bf.append(")");
		
		//System.out.println("INTERVALLO SETTIMANALE PER ["+date.toString()+"]: "+bf.toString());
		
		return bf.toString();
	}
	
	@Override
	public Date incrementDate(Date date,boolean print){
		//return operation(date, print, +1);
		return operation(date, print, +7);
	}
	
	@Override
	public Date decrementDate(Date date,boolean print){
		//return operation(date, print, -1);
		return operation(date, print, -7);
	}
	
	private Date operation(Date date,boolean print, int value){
	
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
		if(print){
			super.logger.debug(">>> incrementDate >>>>");
			super.logger.debug("Before: ");
			printCalendar(cTmp);
		}
		//cTmp.add(Calendar.WEEK_OF_YEAR, 1);
		cTmp.add(Calendar.DAY_OF_YEAR, value);
		if(print){
			super.logger.debug("After: ");
			printCalendar(cTmp);
			super.logger.debug(">>> incrementDate end >>>>");
		}
		return cTmp.getTime();
	}
	
	@Override
	public TipoIntervalloStatistico getTipoStatistiche(){
		return TipoIntervalloStatistico.STATISTICHE_SETTIMANALI;
	}
 

	@Override
	public void callStatisticPluginMethod(IStatisticProcessing statProcessing,
			StatisticBean stat) throws StatisticException {
		IStatistic statContext = new CustomStatisticsSdkGenerator(stat, StatisticType.SETTIMANALE, this);
		statProcessing.createWeeklyStatisticData(statContext);
	}

	@Override
	public String getStatisticPluginMethodName() throws StatisticException {
		return "createWeeklyStatisticData";
	}

	
	@Override
	protected Long insertStatistica(Statistica statistica) throws StatisticException {
		
		try{
		
			StatisticaSettimanale stat = new StatisticaSettimanale();
			stat.setStatisticaBase(statistica);
			((IStatisticaSettimanaleService)this.statisticaServiceDAO).create(stat);
			return stat.getId();
			
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}

	}
	
	@Override
	protected void updateStatistica(long idStatistica, StatisticaContenuti ... statisticaContenuti) throws StatisticException {
		
		try{
		
			StatisticaSettimanale stat = ((IDBStatisticaSettimanaleService)this.statisticaServiceDAO).get(idStatistica);
			for (int i = 0; i < statisticaContenuti.length; i++) {
				statisticaContenuti[i].setData(stat.getStatisticaBase().getData());
				stat.addStatisticaSettimanaleContenuti(statisticaContenuti[i]);
			}
			((IStatisticaSettimanaleService)this.statisticaServiceDAO).update(stat);
			
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}

	}
	
}
