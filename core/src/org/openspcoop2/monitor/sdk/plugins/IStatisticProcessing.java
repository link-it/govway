/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.condition.StatisticsContext;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;

import java.util.List;

/**
 * IStatisticProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IStatisticProcessing extends ISearchArguments {
	
	public List<StatisticType> getEnabledStatisticType();
	
	// popolamento
	
	public void createHourlyStatisticData(IStatistic context) throws StatisticException;
	public void createDailyStatisticData(IStatistic context) throws StatisticException;
	public void createWeeklyStatisticData(IStatistic context) throws StatisticException;
	public void createMonthlyStatisticData(IStatistic context) throws StatisticException;
	
	
	// estrazione
	public IStatisticFilter createSearchFilter(StatisticsContext context) throws StatisticException;
	
}
