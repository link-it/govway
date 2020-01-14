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
package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.core.statistiche.Statistica;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IDBStatisticaOrariaService;
import org.openspcoop2.core.statistiche.dao.IStatisticaOrariaService;
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
 * StatisticheOrarie
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheOrarie extends AbstractStatistiche {

	public static StatisticheOrarie getInstanceForUtils(){
		return new StatisticheOrarie(); 
	}
	StatisticheOrarie(){
			super();
	}
	public StatisticheOrarie( 
			Logger logger, boolean logQuery, boolean generazioneStatisticheCustom, boolean analisiTransazioniCustom,
			StatisticsForceIndexConfig forceIndexConfig,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM) {
		super( logger, logQuery, generazioneStatisticheCustom,analisiTransazioniCustom,
				forceIndexConfig,
				statisticheSM,transazioniSM,
				pluginsStatisticheSM,pluginsBaseSM,utilsSM,pluginsTransazioniSM);
		
		try {
			
			if(statisticheSM==null){
				throw new ServiceException("ServiceManager ["+org.openspcoop2.core.statistiche.dao.IServiceManager.class.getName()+"] non inizializzato");
			}
			
			this.model = StatisticaOraria.model().STATISTICA_BASE;
			this.statisticaServiceDAO = this.statisticheSM.getStatisticaOrariaService();
			this.statisticaServiceSearchDAO = this.statisticheSM.getStatisticaOrariaServiceSearch();
		
		} catch (ServiceException e) {
			this.logger.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			this.logger.error(e.getMessage(), e);
		}
		
		
	}
	
	private Calendar truncDate(Date date){
		Calendar cTmp = Calendar.getInstance();
		cTmp.setTime(date);
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
		
		//System.out.println("INTERVALLO ORARIOE PER ["+date.toString()+"]");
		
		Calendar cTmp = truncDate(date);
		StringBuffer bf = new StringBuffer();
		bf.append(cTmp.get(Calendar.DAY_OF_MONTH));
		bf.append("/");
		bf.append((cTmp.get(Calendar.MONTH)+1));
		bf.append("/");
		bf.append(cTmp.get(Calendar.YEAR));
		bf.append(" ");
		bf.append(cTmp.get(Calendar.HOUR_OF_DAY));
		bf.append(":00.000-");
		bf.append(cTmp.get(Calendar.HOUR_OF_DAY));
		bf.append(":59.999-");
		
		//System.out.println("INTERVALLO ORARIOE PER ["+date.toString()+"]: "+bf.toString());
		
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
		cTmp.add(Calendar.HOUR_OF_DAY, value);
		if(print){
			super.logger.debug("After: ");
			printCalendar(cTmp);
			super.logger.debug(">>> incrementDate end >>>>");
		}
		return cTmp.getTime();
	}
	
	@Override
	public TipoIntervalloStatistico getTipoStatistiche(){
		return TipoIntervalloStatistico.STATISTICHE_ORARIE;
	}
 

	@Override
	public void callStatisticPluginMethod(IStatisticProcessing statProcessing,
			StatisticBean stat) throws StatisticException {
		IStatistic statContext = new CustomStatisticsSdkGenerator(stat, StatisticType.ORARIA , this);
		statProcessing.createHourlyStatisticData(statContext);
	}

	@Override
	public String getStatisticPluginMethodName() throws StatisticException {
		return "createHourlyStatisticData";
	}
	
	
	@Override
	protected Long insertStatistica(Statistica statistica) throws StatisticException {
		
		try{
		
			StatisticaOraria stat = new StatisticaOraria();
			stat.setStatisticaBase(statistica);
			((IStatisticaOrariaService)this.statisticaServiceDAO).create(stat);
			return stat.getId();
			
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}

	}
	
	@Override
	protected void updateStatistica(long idStatistica, StatisticaContenuti ... statisticaContenuti) throws StatisticException {
		
		try{
		
			StatisticaOraria stat = ((IDBStatisticaOrariaService)this.statisticaServiceDAO).get(idStatistica);
			for (int i = 0; i < statisticaContenuti.length; i++) {
				statisticaContenuti[i].setData(stat.getStatisticaBase().getData());
				stat.addStatisticaOrariaContenuti(statisticaContenuti[i]);
			}
			((IStatisticaOrariaService)this.statisticaServiceDAO).update(stat);
			
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}

	}
}
