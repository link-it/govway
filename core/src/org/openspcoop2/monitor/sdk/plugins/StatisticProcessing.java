/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.sdk.plugins;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.condition.StatisticsContext;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.exceptions.ValidationException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;

/**
 * StatisticProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class StatisticProcessing implements IStatisticProcessing {

	@Override
	public List<Parameter<?>> getParameters(Context context) throws SearchException, ParameterException{
		return new ArrayList<Parameter<?>>();
	}
	
	@Override
	public void updateRendering(Parameter<?> parameter,Context context) throws SearchException, ParameterException{
		
	}
	
	@Override
	public void onChangeValue(Parameter<?> parameter, Context context) throws SearchException, ParameterException{
		
	}

	@Override
	public void validate(Context context) throws ValidationException,
			SearchException, ParameterException {
		
	}

	/** Implementazioni vuote dei metodi di popolamento delle statistiche personalizzate.
	 * Lo sviluppatore del plugin dovr&agrave; effettuare l'override dei metodi che
	 * ritiene di voler utilizzare. */
	@Override
	public void createHourlyStatisticData(IStatistic context) throws StatisticException {
		return;
	}
	/** Implementazioni vuote dei metodi di popolamento delle statistiche personalizzate.
	 * Lo sviluppatore del plugin dovr&agrave; effettuare l'override dei metodi che
	 * ritiene di voler utilizzare. */
	@Override
	public void createDailyStatisticData(IStatistic context) throws StatisticException {
		return;
	}
	/** Implementazioni vuote dei metodi di popolamento delle statistiche personalizzate.
	 * Lo sviluppatore del plugin dovr&agrave; effettuare l'override dei metodi che
	 * ritiene di voler utilizzare. */
	@Override
	public void createWeeklyStatisticData(IStatistic context) throws StatisticException {
		return;
	}
	/** Implementazioni vuote dei metodi di popolamento delle statistiche personalizzate.
	 * Lo sviluppatore del plugin dovr&agrave; effettuare l'override dei metodi che
	 * ritiene di voler utilizzare. */
	@Override
	public void createMonthlyStatisticData(IStatistic context) throws StatisticException {
		return;
	}

	
	@Override
	public List<StatisticType> getEnabledStatisticType(){
		List<StatisticType> list = new ArrayList<StatisticType>();
		StatisticType [] s = StatisticType.values();
		for (int i = 0; i < s.length; i++) {
			list.add(s[i]);		
		}
		return list;
	}
	@Override
	public IStatisticFilter createSearchFilter(StatisticsContext context)
			throws StatisticException {
		return null;
	}

}
